"use client";

import { Dashboard } from "@/app/page";
import { RoleGuard } from "@/features/auth/role-guard";

export default function ApoderadoPage() { return <RoleGuard allowedRole="APODERADO">{({ user, logout }) => <Dashboard user={user} onLogout={logout} />}</RoleGuard>; }
