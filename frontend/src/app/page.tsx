"use client";

/* eslint-disable @next/next/no-img-element -- Figma SVG assets retain their authored dimensions. */

import { FormEvent, useState } from "react";

type Role = "Administrador" | "Docente" | "Estudiante" | "Apoderado";
const roleDetails: Record<Role, { name: string; initials: string; title: string; nav: string[] }> = {
  Administrador: { name: "Ana Ríos", initials: "AR", title: "Panel de administración", nav: ["Inicio", "Matrícula", "Asistencia", "Académico", "Horarios", "Finanzas", "Administración"] },
  Docente: { name: "Patricia Morales", initials: "PM", title: "Bienvenida, Prof. Patricia Morales", nav: ["Inicio", "Mis cursos", "Asistencia", "Evaluaciones", "Tareas", "Horario"] },
  Estudiante: { name: "Luis García", initials: "LG", title: "Bienvenido, Luis García", nav: ["Inicio", "Tareas", "Progreso", "Asistencia", "Horario"] },
  Apoderado: { name: "María Rivas", initials: "MR", title: "Panel del Apoderado", nav: ["Inicio", "Matrícula", "Pagos", "Progreso", "Asistencia"] },
};

function Brand({ compact = false }: { compact?: boolean }) { return <div className="brand"><span className="brand-icon"><img src="/school-logo.svg" alt="" /></span><span><b>Colegio PUCP</b><small>{compact ? "Sistema de Gestión" : "Sistema de Gestión Académica"}</small></span></div>; }
function roleFromUsername(username: string): Role { const value = username.trim().toLowerCase(); if (value.includes("admin")) return "Administrador"; if (value.includes("estudiante") || value.includes("alumno")) return "Estudiante"; if (value.includes("apoderado") || value.includes("padre")) return "Apoderado"; return "Docente"; }

export default function Home() {
  const [role, setRole] = useState<Role>("Docente");
  const [loggedIn, setLoggedIn] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [activeNav, setActiveNav] = useState("Inicio");
  const [isAccountMenuOpen, setIsAccountMenuOpen] = useState(false);
  const details = roleDetails[role];
  function login(event: FormEvent<HTMLFormElement>) { event.preventDefault(); const form = new FormData(event.currentTarget); setRole(roleFromUsername(String(form.get("username") ?? ""))); setActiveNav("Inicio"); setLoggedIn(true); }

  if (!loggedIn) return <main className="login-screen"><section className="login-identity"><div className="orb orb-one" /><div className="orb orb-two" /><div className="orb orb-three" /><Brand /><div className="identity-content"><h1>Plataforma<br />Académica<br />Integrada</h1><p>Accede a la información académica, administrativa y de comunicación del Colegio PUCP.</p><ul><li>Administrador</li><li>Docente</li><li>Estudiante</li><li>Apoderado</li></ul></div><small className="version">Año académico 2025 · Versión 2.1.0</small></section><section className="login-form-panel"><form className="login-form" onSubmit={login}><h2>Iniciar sesión</h2><p>Ingresa tus credenciales institucionales</p><label>Usuario <i>*</i><input name="username" required placeholder="Ej. admin, docente, estudiante, apoderado" /></label><label>Contraseña <i>*</i><span className="password-field"><input name="password" required type={showPassword ? "text" : "password"} placeholder="Ingresa tu contraseña" /><button type="button" aria-label="Mostrar contraseña" onClick={() => setShowPassword(!showPassword)}><img src="/eye.svg" alt="" /></button></span></label><a className="forgot" href="#recuperar">¿Olvidaste tu contraseña?</a><button className="login-button" type="submit">Iniciar sesión</button><div className="login-help">¿Problemas para acceder? Contacta a la administración del colegio.</div></form></section></main>;

  return <main className="role-home"><header className="top-nav"><Brand compact /><nav>{details.nav.map((item) => <button className={activeNav === item ? "selected" : ""} key={item} onClick={() => setActiveNav(item)}>{item}</button>)}</nav><div className="account"><button className="bell" aria-label="Notificaciones">♧<b>3</b></button><button className="account-trigger" aria-expanded={isAccountMenuOpen} aria-label="Abrir menú de usuario" onClick={() => setIsAccountMenuOpen(!isAccountMenuOpen)}><span className="account-name"><strong>{details.name}</strong><small>{role}</small></span><span className={`avatar avatar-${role.toLowerCase()}`}>{details.initials}</span></button>{isAccountMenuOpen && <div className="account-menu"><div className="account-menu-info"><strong>{details.name}</strong><small>{role}</small></div><button className="logout-option" onClick={() => { setIsAccountMenuOpen(false); setLoggedIn(false); }}><img src="/sign-out.svg" alt="" />Cerrar sesión</button></div>}</div></header><section className="home-content"><div className="page-title"><div><h1>{activeNav === "Inicio" ? details.title : activeNav}</h1><p>Lunes, 8 de septiembre de 2025 · Año académico 2025</p></div></div><article className="role-message"><span className="message-mark">✓</span><div><p className="eyebrow">INICIO</p><h2>Has ingresado como {role.toLowerCase()}.</h2><p>Esta es la página de inicio para el perfil de {role.toLowerCase()}.</p></div></article></section></main>;
}
