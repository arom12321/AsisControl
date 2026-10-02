"use client";

import { useEffect, type ReactNode } from "react";
import { useRouter } from "next/navigation";
import { roleHomePath } from "@/features/navigation/role-routes";
import type { UserRole } from "./types";
import { useAuth } from "./use-auth";

type RoleGuardProps = { allowedRole: UserRole; children: (props: { logout: () => Promise<void>; user: NonNullable<ReturnType<typeof useAuth>["session"]>["user"] }) => ReactNode };

export function RoleGuard({ allowedRole, children }: RoleGuardProps) {
  const router = useRouter();
  const { session, isRestoringSession, logout } = useAuth();

  useEffect(() => {
    if (isRestoringSession) return;
    if (!session) router.replace("/login");
    else if (session.user.role !== allowedRole) router.replace(roleHomePath[session.user.role]);
  }, [allowedRole, isRestoringSession, router, session]);

  if (isRestoringSession || !session || session.user.role !== allowedRole) return <main className="app-loading">Verificando acceso…</main>;
  return <>{children({ user: session.user, logout })}</>;
}
