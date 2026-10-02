"use client";

import { Dashboard } from "@/app/page";
import { RoleGuard } from "@/features/auth/role-guard";

export default function EstudiantePage() { return <RoleGuard allowedRole="ESTUDIANTE">{({ user, logout }) => <Dashboard user={user} onLogout={logout} />}</RoleGuard>; }
