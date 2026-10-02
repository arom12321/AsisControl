"use client";
import { useEffect, useState, type InputHTMLAttributes } from "react";
import { api, message } from "@/lib/api";
import type { Page, Persona } from "./types";
export function normalizePage<T>(raw: {
  content: T[];
  page?: number | { number: number; totalElements: number; totalPages: number };
  number?: number;
  totalElements?: number;
  totalPages?: number;
}): Page<T> {
  const metadata = typeof raw.page === "object" ? raw.page : raw;
  const page = typeof raw.page === "number" ? raw.page : metadata.number || 0;
  const totalPages = metadata.totalPages || 0;
  return {
    content: raw.content,
    page,
    totalElements: metadata.totalElements || 0,
    totalPages,
    first: page === 0,
    last: page >= totalPages - 1,
  };
}
export function usePaged<T>(path: string, query = "", filters = "") {
  const [page, setPage] = useState(0);
  const [tick, setTick] = useState(0);
  const [data, setData] = useState<Page<T> | null>(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    let active = true;
    const timer = setTimeout(() => {
      setLoading(true);
      setError("");
      api<Page<T>>(
        `${path}${path.includes("?") ? "&" : "?"}page=${page}&size=20${query ? `&search=${encodeURIComponent(query)}` : ""}${filters ? `&${filters}` : ""}`,
      )
        .then((d) => {
          if (active) setData(normalizePage(d));
        })
        .catch((e) => {
          if (active) setError(message(e));
        })
        .finally(() => {
          if (active) setLoading(false);
        });
    }, 0);
    return () => {
      active = false;
      clearTimeout(timer);
    };
  }, [path, query, filters, page, tick]);
  return {
    data,
    error,
    loading,
    page,
    setPage,
    refresh: () => setTick((t) => t + 1),
  };
}
export function Feedback({
  error,
  success,
}: {
  error?: string;
  success?: string;
}) {
  return (
    <>
      {error && (
        <p className="form-error" role="alert">
          {error}
        </p>
      )}
      {success && (
        <p className="action-feedback" role="status">
          {success}
        </p>
      )}
    </>
  );
}
export function Pager({
  data,
  loading,
  setPage,
}: {
  data: Page<unknown> | null;
  loading: boolean;
  setPage: (p: number) => void;
}) {
  if (!data) return null;
  return (
    <div className="pager">
      <span>
        {data.totalElements} registros · Página {data.page + 1} de{" "}
        {Math.max(1, data.totalPages)}
      </span>
      <div>
        <button
          disabled={loading || data.first}
          onClick={() => setPage(data.page - 1)}
        >
          Anterior
        </button>
        <button
          disabled={loading || data.last}
          onClick={() => setPage(data.page + 1)}
        >
          Siguiente
        </button>
      </div>
    </div>
  );
}
export function Field({
  label,
  ...props
}: InputHTMLAttributes<HTMLInputElement> & { label: string }) {
  return (
    <label className="field">
      {label}
      {props.required && " *"}
      <input {...props} />
    </label>
  );
}
export function PasswordField({
  label,
  ...props
}: InputHTMLAttributes<HTMLInputElement> & { label: string }) {
  const [visible, setVisible] = useState(false);
  return (
    <label className="field">
      {label}
      {props.required && " *"}
      <span className="password-field">
        <input {...props} type={visible ? "text" : "password"} />
        <button
          type="button"
          aria-label={visible ? "Ocultar contraseña" : "Mostrar contraseña"}
          aria-pressed={visible}
          onClick={() => setVisible((v) => !v)}
        >
          <Eye crossed={visible} />
        </button>
      </span>
    </label>
  );
}
export function Eye({ crossed = false }: { crossed?: boolean }) {
  return (
    <svg
      viewBox="0 0 24 24"
      width="20"
      height="20"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      aria-hidden="true"
    >
      <path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z" />
      <circle cx="12" cy="12" r="3" />
      {crossed && <path d="m3 3 18 18" />}
    </svg>
  );
}
export function PersonFields({ person }: { person?: Persona }) {
  const [documentType, setDocumentType] = useState(
    person?.tipoDocumento || "DNI",
  );
  const [maxBirth] = useState(() =>
    new Date(Date.now() - 86400000).toISOString().slice(0, 10),
  );
  return (
    <div className="form-grid">
      <Field
        label="Nombres"
        name="nombres"
        required
        maxLength={100}
        defaultValue={person?.nombres}
      />
      <Field
        label="Apellido paterno"
        name="apellidoPaterno"
        required
        maxLength={80}
        defaultValue={person?.apellidoPaterno}
      />
      <Field
        label="Apellido materno"
        name="apellidoMaterno"
        required
        maxLength={80}
        defaultValue={person?.apellidoMaterno}
      />
      <label className="field">
        Tipo de documento *
        <select
          name="tipoDocumento"
          value={documentType}
          onChange={(e) => setDocumentType(e.target.value)}
        >
          <option>DNI</option>
          <option value="CARNET_EXTRANJERIA">Carné de extranjería</option>
          <option>PASAPORTE</option>
        </select>
      </label>
      <Field
        label="Número de documento"
        name="numeroDocumento"
        required
        pattern={documentType === "DNI" ? "[0-9]{8}" : "[A-Za-z0-9-]{6,20}"}
        maxLength={documentType === "DNI" ? 8 : 20}
        defaultValue={person?.numeroDocumento}
      />
      <Field
        label="Fecha de nacimiento"
        name="fechaNacimiento"
        type="date"
        required
        max={maxBirth}
        defaultValue={person?.fechaNacimiento}
      />
      <label className="field">
        Sexo *
        <select
          name="sexo"
          defaultValue={person?.sexo || "PREFIERE_NO_INDICAR"}
        >
          <option value="FEMENINO">Femenino</option>
          <option value="MASCULINO">Masculino</option>
          <option value="OTRO">Otro</option>
          <option value="PREFIERE_NO_INDICAR">Prefiere no indicar</option>
        </select>
      </label>
      <Field
        label="Correo de contacto"
        name="personaCorreo"
        type="email"
        required
        maxLength={150}
        defaultValue={person?.correo}
      />
      <Field
        label="Teléfono"
        name="telefono"
        pattern="[0-9+() -]{7,20}"
        defaultValue={person?.telefono || ""}
      />
      <Field
        label="Dirección"
        name="direccion"
        maxLength={250}
        defaultValue={person?.direccion || ""}
      />
    </div>
  );
}
export function personData(form: FormData, nationality: number | null = null) {
  const str = (key: string) => String(form.get(key) || "").trim();
  return {
    nombres: str("nombres"),
    apellidoPaterno: str("apellidoPaterno"),
    apellidoMaterno: str("apellidoMaterno"),
    tipoDocumento: str("tipoDocumento"),
    numeroDocumento: str("numeroDocumento"),
    fechaNacimiento: str("fechaNacimiento"),
    sexo: str("sexo"),
    correo: str("personaCorreo"),
    telefono: str("telefono") || null,
    direccion: str("direccion") || null,
    nacionalidadId: nationality,
  };
}
