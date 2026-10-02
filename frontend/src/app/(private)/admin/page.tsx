"use client";

import { Dashboard } from "@/app/page";
import { RoleGuard } from "@/features/auth/role-guard";

export default function AdminPage() {
  return <RoleGuard allowedRole="ADMINISTRADOR">{({ user, logout }) => <Dashboard user={user} onLogout={logout} />}</RoleGuard>;
}
