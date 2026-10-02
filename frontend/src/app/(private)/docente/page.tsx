"use client";

import { Dashboard } from "@/app/page";
import { RoleGuard } from "@/features/auth/role-guard";

export default function DocentePage() { return <RoleGuard allowedRole="DOCENTE">{({ user, logout }) => <Dashboard user={user} onLogout={logout} />}</RoleGuard>; }
