"use client";
import { useState, type FormEvent } from "react";
import { api, message } from "@/lib/api";
import { Feedback, Field, Pager, usePaged } from "./shared";
import { Dialog } from "./dialog";
import {
  stateLabel,
  type AcademicYear,
  type Grade,
  type Period,
  type Section,
} from "./academic-types";
type Action = {
  kind:
    | "year"
    | "period"
    | "grade"
    | "section"
    | "admission"
    | "year-state"
    | "period-state"
    | "grade-edit"
    | "section-edit"
    | "section-disable";
  target?: Grade | Period | Section;
  next?: string;
};
export function AcademicConfiguration() {
  const years = usePaged<AcademicYear>("/api/configuracion-academica/anios");
  const [year, setYear] = useState<AcademicYear | null>(null);
  const [action, setAction] = useState<Action | null>(null);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [busy, setBusy] = useState(false);
  const [proposedGrade, setProposedGrade] = useState(0);
  const open = (value: Action) => {
    setError("");
    setSuccess("");
    setAction(value);
    setProposedGrade(0);
  };
  async function reload(number: number) {
    const updated = await api<AcademicYear>(
      `/api/configuracion-academica/anios/${number}`,
    );
    setYear(updated);
    years.refresh();
  }
  async function save(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!action) return;
    const form = new FormData(event.currentTarget);
    const str = (key: string) => String(form.get(key) || "").trim();
    const num = (key: string) => Number(form.get(key));
    let path = "/api/configuracion-academica/anios",
      method = "POST",
      body: object;
    const target = action.target;
    setBusy(true);
    setError("");
    setSuccess("");
    try {
      if (action.kind === "year")
        body = {
          anio: num("anio"),
          fechaInicio: str("fechaInicio"),
          fechaFin: str("fechaFin"),
        };
      else if (!year) return;
      else if (action.kind === "period") {
        path += `/${year.anio}/periodos`;
        body = {
          nombre: str("nombre"),
          orden: num("orden"),
          fechaInicio: str("fechaInicio"),
          fechaFin: str("fechaFin"),
        };
      } else if (action.kind === "grade") {
        path += `/${year.anio}/grados`;
        body = { numero: num("numero"), capacidadDefault: num("capacidad") };
      } else if (action.kind === "admission") {
        path += `/${year.anio}/admision`;
        method = "PUT";
        body = {
          abierta: !year.admisionAbierta,
          version: year.version,
          motivo: str("motivo"),
        };
      } else if (action.kind === "year-state") {
        path += `/${year.anio}/estado`;
        method = "PUT";
        body = {
          estado: action.next,
          version: year.version,
          motivo: str("motivo"),
        };
      } else if (action.kind === "period-state" && target) {
        path = `/api/configuracion-academica/periodos/${target.id}/estado`;
        method = "PUT";
        body = {
          estado: action.next,
          version: target.version,
          motivo: str("motivo"),
        };
      } else if (
        action.kind === "grade-edit" &&
        target &&
        "capacidadDefault" in target
      ) {
        path = `/api/configuracion-academica/grados/${target.id}`;
        method = "PUT";
        body = {
          activo: str("activo") === "true",
          capacidadDefault: num("capacidad"),
          version: target.version,
          motivo: str("motivo"),
        };
      } else if (action.kind === "section-disable" && target) {
        await api(`/api/secciones/${target.id}/estado`, {
          method: "PUT",
          body: JSON.stringify({
            activo: action.next === "true",
            version: target.version,
            motivo: str("motivo"),
          }),
        });
        await reload(year.anio);
        setAction(null);
        setSuccess(
          "Estado de sección actualizado. Su información se conserva.",
        );
        return;
      } else {
        path = "/api/secciones";
        method = action.kind === "section-edit" ? "PUT" : "POST";
        const original = target && "grado" in target ? target : null;
        let tutor: number | null = null;
        if (original) {
          const detail = await api<{ docenteTutorId: number | null }>(
            `${path}/${original.id}`,
          );
          tutor = detail.docenteTutorId;
          path += `/${original.id}`;
        }
        body = {
          nombre: str("nombre"),
          grado: original?.grado || num("numero"),
          anioAcademico: year.anio,
          capacidadMaxima: num("capacidad"),
          docenteTutorId: tutor,
          motivo: str("motivo") || null,
          ...(original ? { version: original.version } : {}),
        };
      }
      const result = await api<AcademicYear>(path, {
        method,
        body: JSON.stringify(body),
      });
      if (action.kind === "section" || action.kind === "section-edit")
        await reload(year!.anio);
      else {
        setYear(result);
        years.refresh();
      }
      setAction(null);
      setSuccess("Configuración guardada.");
    } catch (error) {
      setError(message(error));
    } finally {
      setBusy(false);
    }
  }
  const editable = year?.estado !== "CERRADO";
  const titles: Record<Action["kind"], string> = {
    year: "Crear año académico",
    period: "Agregar período",
    grade: "Habilitar grado",
    section: "Crear sección",
    admission: "Recepción de solicitudes del apoderado",
    "year-state": "Cambiar estado del año",
    "period-state": "Cambiar estado del período",
    "grade-edit": "Editar grado",
    "section-edit": "Editar sección",
    "section-disable": "Cambiar estado de sección",
  };
  const target = action?.target;
  return (
    <>
      <Feedback error={!action ? error : ""} success={success} />
      <div className="section-heading">
        <h3>Años académicos</h3>
        <button
          className="primary-action"
          onClick={() => open({ kind: "year" })}
        >
          Crear año
        </button>
      </div>
      <Feedback error={years.error} />
      {years.loading ? (
        <p role="status">Cargando configuración…</p>
      ) : (
        <div className="year-list">
          {years.data?.content.map((value) => (
            <button
              key={value.id}
              className={
                year?.id === value.id ? "year-card selected" : "year-card"
              }
              onClick={() => {
                setError("");
                reload(value.anio).catch((e) => setError(message(e)));
              }}
            >
              <strong>{value.anio}</strong>
              <span>{stateLabel(value.estado)}</span>
              <small>
                {value.fechaInicio} a {value.fechaFin}
              </small>
            </button>
          ))}
        </div>
      )}
      {!years.loading && !years.error && !years.data?.content.length && (
        <p className="empty">
          Crea el año y configura los períodos, grados y secciones.
        </p>
      )}
      <Pager
        data={years.data}
        loading={years.loading}
        setPage={years.setPage}
      />
      {year && (
        <div className="configuration-detail">
          <div className="section-heading">
            <div>
              <h3>Año {year.anio}</h3>
              <p>
                {stateLabel(year.estado)} · {year.fechaInicio} a {year.fechaFin}
                <br />
                Admisión del apoderado:{" "}
                {year.admisionAbierta ? "Abierta" : "Cerrada"}
              </p>
            </div>
            <div className="row-actions">
              <button
                disabled={busy}
                onClick={() =>
                  reload(year.anio).catch((e) => setError(message(e)))
                }
              >
                Actualizar
              </button>
              {year.estado === "ACTIVO" && (
                <button onClick={() => open({ kind: "admission" })}>
                  {year.admisionAbierta ? "Cerrar admisión" : "Abrir admisión"}
                </button>
              )}
              {editable && (
                <button
                  onClick={() =>
                    open({
                      kind: "year-state",
                      next: year.estado === "BORRADOR" ? "ACTIVO" : "CERRADO",
                    })
                  }
                >
                  {year.estado === "BORRADOR" ? "Activar año" : "Cerrar año"}
                </button>
              )}
            </div>
          </div>
          {year.estado === "CERRADO" && (
            <p className="hint">Año cerrado: consulta histórica disponible.</p>
          )}
          <div className="section-heading">
            <h4>Períodos</h4>
            {year.estado === "BORRADOR" && (
              <button onClick={() => open({ kind: "period" })}>
                Agregar período
              </button>
            )}
          </div>
          <div className="user-table-wrap">
            <table className="user-table">
              <thead>
                <tr>
                  <th>Período</th>
                  <th>Fechas</th>
                  <th>Estado</th>
                  <th>Acción</th>
                </tr>
              </thead>
              <tbody>
                {year.periodos.map((period) => (
                  <tr key={period.id}>
                    <td data-label="Período">
                      {period.orden}. {period.nombre}
                    </td>
                    <td data-label="Fechas">
                      {period.fechaInicio} a {period.fechaFin}
                    </td>
                    <td data-label="Estado">{stateLabel(period.estado)}</td>
                    <td data-label="Acción">
                      {year.estado === "ACTIVO" &&
                        period.estado !== "CERRADO" && (
                          <button
                            onClick={() =>
                              open({
                                kind: "period-state",
                                target: period,
                                next:
                                  period.estado === "PLANIFICADO"
                                    ? "ACTIVO"
                                    : "CERRADO",
                              })
                            }
                          >
                            {period.estado === "PLANIFICADO"
                              ? "Activar"
                              : "Cerrar"}
                          </button>
                        )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {!year.periodos.length && (
            <p className="empty">
              Sin períodos configurados. Sus fechas y cantidad se definen aquí.
            </p>
          )}
          <div className="section-heading">
            <h4>Grados de secundaria</h4>
            {editable && (
              <button onClick={() => open({ kind: "grade" })}>
                Habilitar grado
              </button>
            )}
          </div>
          <div className="user-table-wrap">
            <table className="user-table">
              <thead>
                <tr>
                  <th>Grado</th>
                  <th>Capacidad sugerida por sección</th>
                  <th>Capacidad total habilitada</th>
                  <th>Estado</th>
                  <th>Acción</th>
                </tr>
              </thead>
              <tbody>
                {year.grados.map((grade) => (
                  <tr key={grade.id}>
                    <td data-label="Grado">{grade.numero}°</td>
                    <td data-label="Capacidad sugerida">
                      {grade.capacidadDefault}
                    </td>
                    <td data-label="Capacidad total">
                      {year.secciones
                        .filter((s) => s.activo && s.grado === grade.numero)
                        .reduce((total, s) => total + s.capacidadMaxima, 0)}
                    </td>
                    <td data-label="Estado">
                      {grade.activo ? "Habilitado" : "Inhabilitado"}
                    </td>
                    <td data-label="Acción">
                      {editable && (
                        <button
                          onClick={() =>
                            open({ kind: "grade-edit", target: grade })
                          }
                        >
                          Editar
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <div className="section-heading">
            <h4>Secciones y vacantes</h4>
            {editable && (
              <button
                disabled={!year.grados.some((g) => g.activo)}
                onClick={() => open({ kind: "section" })}
              >
                Crear sección
              </button>
            )}
          </div>
          <p className="hint">
            La capacidad de cada sección es independiente. Las solicitudes no
            reservan vacantes; se ocupan al finalizar la matrícula.
          </p>
          <div className="user-table-wrap">
            <table className="user-table">
              <thead>
                <tr>
                  <th>Grado / Sección</th>
                  <th>Capacidad</th>
                  <th>Matriculados</th>
                  <th>Vacantes</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {year.secciones.map((section) => (
                  <tr key={section.id}>
                    <td data-label="Sección">
                      {section.grado}° {section.nombre}
                    </td>
                    <td data-label="Capacidad">{section.capacidadMaxima}</td>
                    <td data-label="Matriculados">{section.ocupacion}</td>
                    <td data-label="Vacantes">{section.vacantes}</td>
                    <td data-label="Estado">
                      {section.activo ? "Habilitada" : "Inhabilitada"}
                    </td>
                    <td data-label="Acciones">
                      {editable && (
                        <div className="row-actions">
                          {section.activo && (
                            <button
                              onClick={() =>
                                open({ kind: "section-edit", target: section })
                              }
                            >
                              Editar
                            </button>
                          )}
                          <button
                            onClick={() =>
                              open({
                                kind: "section-disable",
                                target: section,
                                next: section.activo ? "false" : "true",
                              })
                            }
                          >
                            {section.activo ? "Inhabilitar" : "Habilitar"}
                          </button>
                        </div>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {!year.secciones.length && (
            <p className="empty">Sin secciones configuradas.</p>
          )}
        </div>
      )}
      {action && (
        <Dialog
          title={titles[action.kind]}
          onClose={() => setAction(null)}
          busy={busy}
        >
          <form onSubmit={save}>
            <Feedback error={error} />
            <div className="form-grid">
              {action.kind === "year" && (
                <Field
                  label="Año"
                  name="anio"
                  type="number"
                  min={2000}
                  max={2100}
                  required
                />
              )}
              {(action.kind === "year" || action.kind === "period") && (
                <>
                  <Field
                    label="Fecha inicial"
                    name="fechaInicio"
                    type="date"
                    min={
                      action.kind === "period" ? year?.fechaInicio : undefined
                    }
                    max={action.kind === "period" ? year?.fechaFin : undefined}
                    required
                  />
                  <Field
                    label="Fecha final"
                    name="fechaFin"
                    type="date"
                    min={
                      action.kind === "period" ? year?.fechaInicio : undefined
                    }
                    max={action.kind === "period" ? year?.fechaFin : undefined}
                    required
                  />
                </>
              )}
              {action.kind === "period" && (
                <>
                  <Field
                    label="Nombre del período"
                    name="nombre"
                    required
                    maxLength={100}
                    placeholder="Ej. Primer bimestre"
                  />
                  <Field
                    label="Orden"
                    name="orden"
                    type="number"
                    min={1}
                    required
                  />
                </>
              )}
              {(action.kind === "grade" || action.kind === "section") && (
                <label className="field">
                  Grado *
                  <select
                    name="numero"
                    required
                    defaultValue=""
                    onChange={(event) =>
                      setProposedGrade(Number(event.target.value))
                    }
                  >
                    <option value="" disabled>
                      Seleccionar grado
                    </option>
                    {(action.kind === "grade"
                      ? [1, 2, 3, 4, 5].filter(
                          (n) => !year?.grados.some((g) => g.numero === n),
                        )
                      : year?.grados
                          .filter((g) => g.activo)
                          .map((g) => g.numero) || []
                    ).map((n) => (
                      <option key={n} value={n}>
                        {n}° de secundaria
                      </option>
                    ))}
                  </select>
                </label>
              )}
              {(action.kind === "section" ||
                action.kind === "section-edit") && (
                <Field
                  label="Nombre de sección"
                  name="nombre"
                  required
                  maxLength={20}
                  defaultValue={
                    target && "grado" in target ? target.nombre : ""
                  }
                  placeholder="Ej. A"
                />
              )}
              {["grade", "grade-edit", "section", "section-edit"].includes(
                action.kind,
              ) && (
                <Field
                  label={
                    action.kind.startsWith("grade")
                      ? "Capacidad sugerida por sección"
                      : "Capacidad máxima"
                  }
                  name="capacidad"
                  key={action.kind === "section" ? proposedGrade : "capacity"}
                  type="number"
                  min={1}
                  max={100}
                  required
                  defaultValue={
                    target && "capacidadDefault" in target
                      ? target.capacidadDefault
                      : target && "capacidadMaxima" in target
                        ? target.capacidadMaxima
                        : action.kind === "section"
                          ? year?.grados.find(
                              (grade) => grade.numero === proposedGrade,
                            )?.capacidadDefault || 30
                          : 30
                  }
                />
              )}
              {action.kind === "grade-edit" &&
                target &&
                "capacidadDefault" in target && (
                  <label className="field">
                    Estado *
                    <select name="activo" defaultValue={String(target.activo)}>
                      <option value="true">Habilitado</option>
                      <option value="false">Inhabilitado</option>
                    </select>
                  </label>
                )}
              {([
                "admission",
                "year-state",
                "period-state",
                "grade-edit",
                "section-edit",
                "section-disable",
              ].includes(action.kind) ||
                (action.kind === "section" && year?.estado === "ACTIVO")) && (
                <label className="field full-field">
                  Motivo *
                  <textarea name="motivo" required maxLength={700} rows={3} />
                </label>
              )}
            </div>
            {action.kind === "admission" && (
              <p>
                {year?.admisionAbierta
                  ? "Dejarán de aceptarse solicitudes nuevas y reenvíos del apoderado. La revisión administrativa continúa."
                  : "Los apoderados podrán solicitar los grados habilitados del año activo."}
              </p>
            )}
            {action.next && action.kind !== "section-disable" && (
              <p>
                Nuevo estado: <strong>{stateLabel(action.next)}</strong>. Los
                estados cerrados no se reabren mediante este flujo.
              </p>
            )}
            {action.kind === "section-disable" && (
              <p>
                Solo pueden inhabilitarse secciones sin solicitudes, matrículas
                ni asignaciones. Su registro se conserva.
              </p>
            )}
            <div className="form-actions">
              <button
                type="button"
                disabled={busy}
                onClick={() => setAction(null)}
              >
                Cancelar
              </button>
              <button className="primary-action" disabled={busy}>
                {busy ? "Guardando…" : "Confirmar"}
              </button>
            </div>
          </form>
        </Dialog>
      )}
    </>
  );
}
