import type { UserRole } from "@/features/auth/types";

export const roleHomePath: Record<UserRole, string> = {
  ADMINISTRADOR: "/admin",
  DOCENTE: "/docente",
  ESTUDIANTE: "/estudiante",
  APODERADO: "/apoderado",
};
