import type { UserRole } from "@/features/auth/types";

export type UserStatus = "ACTIVE" | "INACTIVE";

export type ManagedUser = {
  id: string;
  fullName: string;
  email: string;
  role: UserRole;
  status: UserStatus;
  createdAt: string;
};

export type UserInput = Pick<ManagedUser, "fullName" | "email" | "role">;

export type AuditRecord = {
  id: string;
  action: string;
  performedBy: string;
  occurredAt: string;
  detail: string;
};

/** Contrato para reemplazar el almacenamiento temporal por endpoints del backend. */
export interface UserManagementService {
  list(): Promise<ManagedUser[]>;
  create(input: UserInput): Promise<ManagedUser>;
  update(id: string, input: UserInput): Promise<ManagedUser>;
  updateStatus(id: string, status: UserStatus): Promise<ManagedUser>;
  listAudit(): Promise<AuditRecord[]>;
}
