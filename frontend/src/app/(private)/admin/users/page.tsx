"use client";

import { Dashboard } from "@/app/page";
import { RoleGuard } from "@/features/auth/role-guard";

export default function AdminUsersPage() {
  return <RoleGuard allowedRole="ADMINISTRADOR">{({ user, logout }) => <Dashboard user={user} onLogout={logout} initialActiveNav="Administración" />}</RoleGuard>;
}
