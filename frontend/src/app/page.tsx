"use client";

/* eslint-disable @next/next/no-img-element -- SVG assets are served from public/. */

import { FormEvent, useEffect, useMemo, useState } from "react";
import { demoCredentials } from "@/features/auth/mock-auth-service";
import { useAuth } from "@/features/auth/use-auth";
import type { AuthenticatedUser, UserRole } from "@/features/auth/types";
import { mockUserManagementService } from "@/features/users/mock-user-management-service";
import type {
  AuditRecord,
  ManagedUser,
  UserInput,
} from "@/features/users/types";

type RolePresentation = { initials: string; title: string; nav: string[] };

const roleLabels: Record<UserRole, string> = {
  ADMINISTRADOR: "Administrador",
  DOCENTE: "Docente",
  ESTUDIANTE: "Estudiante",
  APODERADO: "Apoderado",
};

const roleDetails: Record<UserRole, RolePresentation> = {
  ADMINISTRADOR: {
    initials: "AR",
    title: "Panel de administración",
    nav: [
      "Inicio",
      "Matrícula",
      "Asistencia",
      "Académico",
      "Horarios",
      "Finanzas",
      "Administración",
    ],
  },
  DOCENTE: {
    initials: "PM",
    title: "Bienvenida, Prof. Patricia Morales",
    nav: [
      "Inicio",
      "Mis cursos",
      "Asistencia",
      "Evaluaciones",
      "Tareas",
      "Horario",
    ],
  },
  ESTUDIANTE: {
    initials: "LG",
    title: "Bienvenido, Luis García",
    nav: ["Inicio", "Tareas", "Progreso", "Asistencia", "Horario"],
  },
  APODERADO: {
    initials: "MR",
    title: "Panel del Apoderado",
    nav: ["Inicio", "Matrícula", "Pagos", "Progreso", "Asistencia"],
  },
};

function Brand({ compact = false }: { compact?: boolean }) {
  return (
    <div className="brand">
      <span className="brand-icon">
        <img src="/school-logo.svg" alt="" />
      </span>
      <span>
        <b>Colegio PUCP</b>
        <small>
          {compact ? "Sistema de Gestión" : "Sistema de Gestión Académica"}
        </small>
      </span>
    </div>
  );
}

function LoginScreen({
  onLogin,
}: {
  onLogin: ReturnType<typeof useAuth>["login"];
}) {
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function login(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setIsSubmitting(true);
    const form = new FormData(event.currentTarget);
    const result = await onLogin({
      username: String(form.get("username") ?? ""),
      password: String(form.get("password") ?? ""),
    });
    setIsSubmitting(false);
    if (!result.ok) setError(result.message);
  }

  return (
    <main className="login-screen">
      <section className="login-identity">
        <div className="orb orb-one" />
        <div className="orb orb-two" />
        <div className="orb orb-three" />
        <Brand />
        <div className="identity-content">
          <h1>
            Plataforma
            <br />
            Académica
            <br />
            Integrada
          </h1>
          <p>
            Accede a la información académica, administrativa y de comunicación
            del Colegio PUCP.
          </p>
          <ul>
            <li>Administrador</li>
            <li>Docente</li>
            <li>Estudiante</li>
            <li>Apoderado</li>
          </ul>
        </div>
        <small className="version">Año académico 2025 · Versión 2.1.0</small>
      </section>
      <section className="login-form-panel">
        <form className="login-form" onSubmit={login} noValidate>
          <h2>Iniciar sesión</h2>
          <p>Ingresa tus credenciales institucionales</p>
          <label>
            Usuario <i>*</i>
            <input
              name="username"
              required
              autoComplete="username"
              placeholder="Ingresa tu usuario"
              disabled={isSubmitting}
            />
          </label>
          <label>
            Contraseña <i>*</i>
            <span className="password-field">
              <input
                name="password"
                required
                autoComplete="current-password"
                type={showPassword ? "text" : "password"}
                placeholder="Ingresa tu contraseña"
                disabled={isSubmitting}
              />
              <button
                type="button"
                aria-label={
                  showPassword ? "Ocultar contraseña" : "Mostrar contraseña"
                }
                onClick={() => setShowPassword(!showPassword)}
                disabled={isSubmitting}
              >
                <img src="/eye.svg" alt="" />
              </button>
            </span>
          </label>
          {error && (
            <p className="form-error" role="alert">
              {error}
            </p>
          )}
          <a className="forgot" href="#recuperar">
            ¿Olvidaste tu contraseña?
          </a>
          <button
            className="login-button"
            type="submit"
            disabled={isSubmitting}
          >
            {isSubmitting ? "Validando acceso…" : "Iniciar sesión"}
          </button>
          <details className="demo-accounts">
            <summary>Credenciales de prueba</summary>
            {demoCredentials.map(({ username, password, role }) => (
              <p key={username}>
                <b>{roleLabels[role]}:</b> {username} / {password}
              </p>
            ))}
          </details>
          <div className="login-help">
            ¿Problemas para acceder? Contacta a la administración del colegio.
          </div>
        </form>
      </section>
    </main>
  );
}

function UserManagement() {
  const [users, setUsers] = useState<ManagedUser[]>([]);
  const [audit, setAudit] = useState<AuditRecord[]>([]);
  const [query, setQuery] = useState("");
  const [section, setSection] = useState<"users" | "audit">("users");
  const [editingUser, setEditingUser] = useState<ManagedUser | null>(null);
  const [isCreating, setIsCreating] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [feedback, setFeedback] = useState("");

  async function loadData() {
    setIsLoading(true);
    const [loadedUsers, loadedAudit] = await Promise.all([
      mockUserManagementService.list(),
      mockUserManagementService.listAudit(),
    ]);
    setUsers(loadedUsers);
    setAudit(loadedAudit);
    setIsLoading(false);
  }

  useEffect(() => {
    const loadTimer = window.setTimeout(() => {
      void loadData();
    }, 0);
    return () => window.clearTimeout(loadTimer);
  }, []);

  const filteredUsers = useMemo(
    () =>
      users.filter((managedUser) =>
        `${managedUser.fullName} ${managedUser.email} ${roleLabels[managedUser.role]}`
          .toLowerCase()
          .includes(query.toLowerCase()),
      ),
    [query, users],
  );

  async function submitUser(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const input: UserInput = {
      fullName: String(form.get("fullName") ?? "").trim(),
      email: String(form.get("email") ?? "").trim(),
      role: String(form.get("role")) as UserRole,
    };
    if (!input.fullName || !input.email) return;
    if (editingUser)
      await mockUserManagementService.update(editingUser.id, input);
    else await mockUserManagementService.create(input);
    setFeedback(
      editingUser
        ? "Usuario actualizado correctamente."
        : "Usuario registrado correctamente.",
    );
    setEditingUser(null);
    setIsCreating(false);
    await loadData();
  }

  async function toggleStatus(managedUser: ManagedUser) {
    await mockUserManagementService.updateStatus(
      managedUser.id,
      managedUser.status === "ACTIVE" ? "INACTIVE" : "ACTIVE",
    );
    setFeedback(
      `Usuario ${managedUser.status === "ACTIVE" ? "desactivado" : "activado"} correctamente.`,
    );
    await loadData();
  }

  const formUser = editingUser;
  return (
    <section className="management-page">
      <div className="management-heading">
        <div>
          <p className="eyebrow">ADMINISTRACIÓN</p>
          <h2>Gestión de usuarios</h2>
          <p>
            Administra las cuentas del sistema y consulta los cambios
            realizados.
          </p>
        </div>
        <button
          className="primary-action"
          onClick={() => {
            setEditingUser(null);
            setIsCreating(true);
          }}
        >
          + Registrar usuario
        </button>
      </div>
      <div className="management-tabs" role="tablist">
        <button
          className={section === "users" ? "active" : ""}
          onClick={() => setSection("users")}
          role="tab"
        >
          Usuarios
        </button>
        <button
          className={section === "audit" ? "active" : ""}
          onClick={() => setSection("audit")}
          role="tab"
        >
          Auditoría
        </button>
      </div>
      {feedback && (
        <p className="action-feedback" role="status">
          {feedback}
        </p>
      )}
      {section === "users" ? (
        <>
          <div className="users-toolbar">
            <label>
              Buscar usuario
              <input
                value={query}
                onChange={(event) => setQuery(event.target.value)}
                placeholder="Nombre, correo o rol"
              />
            </label>
            <span>{filteredUsers.length} usuarios</span>
          </div>
          <div className="user-table-wrap">
            <table className="user-table">
              <thead>
                <tr>
                  <th>Usuario</th>
                  <th>Rol</th>
                  <th>Estado</th>
                  <th>Registro</th>
                  <th aria-label="Acciones" />
                </tr>
              </thead>
              <tbody>
                {isLoading ? (
                  <tr>
                    <td colSpan={5}>Cargando usuarios…</td>
                  </tr>
                ) : (
                  filteredUsers.map((managedUser) => (
                    <tr key={managedUser.id}>
                      <td>
                        <strong>{managedUser.fullName}</strong>
                        <small>{managedUser.email}</small>
                      </td>
                      <td>{roleLabels[managedUser.role]}</td>
                      <td>
                        <span
                          className={`status status-${managedUser.status.toLowerCase()}`}
                        >
                          {managedUser.status === "ACTIVE"
                            ? "Activo"
                            : "Inactivo"}
                        </span>
                      </td>
                      <td>{managedUser.createdAt}</td>
                      <td className="row-actions">
                        <button
                          onClick={() => {
                            setIsCreating(false);
                            setEditingUser(managedUser);
                          }}
                        >
                          Editar
                        </button>
                        <button
                          className={
                            managedUser.status === "ACTIVE"
                              ? "danger-action"
                              : "success-action"
                          }
                          onClick={() => void toggleStatus(managedUser)}
                        >
                          {managedUser.status === "ACTIVE"
                            ? "Desactivar"
                            : "Activar"}
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </>
      ) : (
        <div className="audit-list">
          {audit.map((record) => (
            <article key={record.id}>
              <div>
                <strong>{record.action}</strong>
                <p>{record.detail}</p>
              </div>
              <div>
                <span>{record.performedBy}</span>
                <small>{record.occurredAt}</small>
              </div>
            </article>
          ))}
        </div>
      )}
      {(isCreating || formUser) && (
        <div className="modal-backdrop" role="presentation">
          <form className="user-form-card" onSubmit={submitUser}>
            <div className="form-card-heading">
              <div>
                <h3>{formUser ? "Editar usuario" : "Registrar usuario"}</h3>
                <p>
                  {formUser
                    ? "Actualiza los datos de la cuenta seleccionada."
                    : "Crea una cuenta para el acceso al sistema."}
                </p>
              </div>
              <button
                type="button"
                aria-label="Cerrar formulario"
                onClick={() => {
                  setEditingUser(null);
                  setIsCreating(false);
                }}
              >
                ×
              </button>
            </div>
            <label>
              Nombre completo
              <input
                name="fullName"
                defaultValue={formUser?.fullName}
                required
              />
            </label>
            <label>
              Correo institucional
              <input
                name="email"
                type="email"
                defaultValue={formUser?.email}
                required
              />
            </label>
            <label>
              Rol
              <select name="role" defaultValue={formUser?.role ?? "DOCENTE"}>
                {Object.entries(roleLabels).map(([value, label]) => (
                  <option key={value} value={value}>
                    {label}
                  </option>
                ))}
              </select>
            </label>
            <div className="form-actions">
              <button
                type="button"
                onClick={() => {
                  setEditingUser(null);
                  setIsCreating(false);
                }}
              >
                Cancelar
              </button>
              <button className="primary-action" type="submit">
                {formUser ? "Guardar cambios" : "Registrar usuario"}
              </button>
            </div>
          </form>
        </div>
      )}
    </section>
  );
}

function Dashboard({
  user,
  onLogout,
}: {
  user: AuthenticatedUser;
  onLogout: () => Promise<void>;
}) {
  const [activeNav, setActiveNav] = useState("Inicio");
  const [isAccountMenuOpen, setIsAccountMenuOpen] = useState(false);
  const details = roleDetails[user.role];
  const roleLabel = roleLabels[user.role];

  return (
    <main className="role-home">
      <header className="top-nav">
        <Brand compact />
        <nav aria-label="Navegación principal">
          {details.nav.map((item) => (
            <button
              className={activeNav === item ? "selected" : ""}
              key={item}
              onClick={() => setActiveNav(item)}
            >
              {item}
            </button>
          ))}
        </nav>
        <div className="account">
          <button className="bell" aria-label="Notificaciones">
            ♧<b>3</b>
          </button>
          <button
            className="account-trigger"
            aria-expanded={isAccountMenuOpen}
            aria-label="Abrir menú de usuario"
            onClick={() => setIsAccountMenuOpen(!isAccountMenuOpen)}
          >
            <span className="account-name">
              <strong>{user.fullName}</strong>
              <small>{roleLabel}</small>
            </span>
            <span className={`avatar avatar-${user.role.toLowerCase()}`}>
              {details.initials}
            </span>
          </button>
          {isAccountMenuOpen && (
            <div className="account-menu">
              <div className="account-menu-info">
                <strong>{user.fullName}</strong>
                <small>{roleLabel}</small>
              </div>
              <button className="logout-option" onClick={onLogout}>
                <img src="/sign-out.svg" alt="" />
                Cerrar sesión
              </button>
            </div>
          )}
        </div>
      </header>
      <section className="home-content">
        {user.role === "ADMINISTRADOR" && activeNav === "Administración" ? (
          <UserManagement />
        ) : (
          <>
            <div className="page-title">
              <div>
                <h1>{activeNav === "Inicio" ? details.title : activeNav}</h1>
                <p>Lunes, 8 de septiembre de 2025 · Año académico 2025</p>
              </div>
            </div>
            <article className="role-message">
              <span className="message-mark">✓</span>
              <div>
                <p className="eyebrow">INICIO</p>
                <h2>Has ingresado como {roleLabel.toLowerCase()}.</h2>
                <p>
                  La interfaz ya consume una sesión y un perfil; los módulos de
                  esta sección podrán conectarse a sus respectivos endpoints.
                </p>
              </div>
            </article>
          </>
        )}
      </section>
    </main>
  );
}

export default function Home() {
  const { session, isRestoringSession, login, logout } = useAuth();
  if (isRestoringSession)
    return (
      <main className="app-loading" aria-live="polite">
        Cargando sesión…
      </main>
    );
  if (!session) return <LoginScreen onLogin={login} />;
  return <Dashboard user={session.user} onLogout={logout} />;
}
