"use client";

/* eslint-disable @next/next/no-img-element -- SVG assets are served from public/. */

import { FormEvent, useState } from "react";
import { Administration } from "@/features/management/admin";
import Link from "next/link";
import { GuardianEnrollment } from "@/features/management/guardian-enrollment";
import { Enrollments } from "@/features/management/enrollments";
import { Eye } from "@/features/management/shared";
import { ChangePassword } from "@/features/auth/change-password";
import { useAuth } from "@/features/auth/use-auth";
import type { AuthenticatedUser, UserRole } from "@/features/auth/types";

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
        <small className="version">AsisControl · Prototipo académico</small>
      </section>
      <section className="login-form-panel">
        <form className="login-form" onSubmit={login}>
          <h2>Iniciar sesión</h2>
          <p>Ingresa tus credenciales institucionales</p>
          <label>
            Usuario <i>*</i>
            <input
              name="username"
              maxLength={150}
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
                maxLength={72}
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
                <Eye crossed={showPassword} />
              </button>
            </span>
          </label>
          {error && (
            <p className="form-error" role="alert">
              {error}
            </p>
          )}
          <Link className="forgot" href="/recuperar-contrasena">
            ¿Olvidaste tu contraseña?
          </Link>
          <button
            className="login-button"
            type="submit"
            disabled={isSubmitting}
          >
            {isSubmitting ? "Validando acceso…" : "Iniciar sesión"}
          </button>
          <div className="login-help">
            ¿Problemas para acceder? Contacta a la administración del colegio.
          </div>
        </form>
      </section>
    </main>
  );
}

function Dashboard({
  user,
  onLogout,
  initialNav = "Inicio",
  startCreate = false,
}: {
  user: AuthenticatedUser;
  onLogout: () => Promise<void>;
  initialNav?: string;
  startCreate?: boolean;
}) {
  const [activeNav, setActiveNav] = useState(initialNav);
  const [isAccountMenuOpen, setIsAccountMenuOpen] = useState(false);
  const [changingPassword, setChangingPassword] = useState(false);
  const details = roleDetails[user.role];
  const roleLabel = roleLabels[user.role];

  if (changingPassword)
    return <ChangePassword onLogout={onLogout} mandatory={false} />;
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
              {user.fullName
                .split(" ")
                .filter(Boolean)
                .slice(0, 2)
                .map((n) => n[0])
                .join("")}
            </span>
          </button>
          {isAccountMenuOpen && (
            <div className="account-menu">
              <div className="account-menu-info">
                <strong>{user.fullName}</strong>
                <small>{roleLabel}</small>
              </div>
              <button
                className="logout-option"
                onClick={() => setChangingPassword(true)}
              >
                Cambiar contraseña
              </button>
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
          <Administration currentUserId={user.id} startCreate={startCreate} />
        ) : user.role === "ADMINISTRADOR" && activeNav === "Matrícula" ? (
          <Enrollments />
        ) : user.role === "APODERADO" && activeNav === "Matrícula" ? (
          <GuardianEnrollment />
        ) : (
          <>
            <div className="page-title">
              <div>
                <h1>
                  {activeNav === "Inicio"
                    ? ["DOCENTE", "ESTUDIANTE"].includes(user.role)
                      ? `Bienvenido/a, ${user.fullName}`
                      : details.title
                    : activeNav}
                </h1>
                <p>
                  {new Date().toLocaleDateString("es-PE", {
                    timeZone: "America/Lima",
                    dateStyle: "long",
                  })}
                </p>
              </div>
            </div>
            <article className="role-message">
              <span className="message-mark">✓</span>
              <div>
                <p className="eyebrow">{activeNav.toUpperCase()}</p>
                <h2>Has ingresado como {roleLabel.toLowerCase()}.</h2>
                <p>
                  {activeNav === "Inicio"
                    ? "Selecciona una opción del menú para continuar."
                    : "Este módulo todavía está pendiente de implementación."}
                </p>
              </div>
            </article>
          </>
        )}
      </section>
    </main>
  );
}

export default function AsisApp({
  initialNav,
  startCreate,
}: {
  initialNav?: string;
  startCreate?: boolean;
}) {
  const { session, isRestoringSession, login, logout } = useAuth();
  if (isRestoringSession)
    return (
      <main className="app-loading" aria-live="polite">
        Cargando sesión…
      </main>
    );
  if (!session) return <LoginScreen onLogin={login} />;
  if (session.user.requiresPasswordChange)
    return <ChangePassword onLogout={logout} />;
  return (
    <Dashboard
      user={session.user}
      onLogout={logout}
      initialNav={initialNav}
      startCreate={startCreate}
    />
  );
}
