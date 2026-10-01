import type { AuditRecord, ManagedUser, UserInput, UserManagementService, UserStatus } from "./types";

let users: ManagedUser[] = [
  { id: "usr-100", fullName: "Patricia Morales", email: "patricia.morales@colegiopucp.edu.pe", role: "DOCENTE", status: "ACTIVE", createdAt: "2025-03-02" },
  { id: "usr-101", fullName: "Luis García", email: "luis.garcia@colegiopucp.edu.pe", role: "ESTUDIANTE", status: "ACTIVE", createdAt: "2025-03-04" },
  { id: "usr-102", fullName: "María Rivas", email: "maria.rivas@colegiopucp.edu.pe", role: "APODERADO", status: "ACTIVE", createdAt: "2025-03-04" },
  { id: "usr-103", fullName: "Carlos Mendoza", email: "carlos.mendoza@colegiopucp.edu.pe", role: "DOCENTE", status: "INACTIVE", createdAt: "2025-04-14" },
];

let audit: AuditRecord[] = [
  { id: "aud-001", action: "Usuario desactivado", performedBy: "Ana Ríos", occurredAt: "08/09/2025, 09:14", detail: "Carlos Mendoza (Docente)" },
  { id: "aud-002", action: "Usuario registrado", performedBy: "Ana Ríos", occurredAt: "07/09/2025, 16:30", detail: "María Rivas (Apoderado)" },
];

const delay = () => new Promise((resolve) => window.setTimeout(resolve, 180));
const copy = <T,>(value: T): T => JSON.parse(JSON.stringify(value)) as T;

function appendAudit(action: string, detail: string) {
  audit = [{ id: `aud-${Date.now()}`, action, detail, performedBy: "Ana Ríos", occurredAt: new Date().toLocaleString("es-PE") }, ...audit];
}

export const mockUserManagementService: UserManagementService = {
  async list() { await delay(); return copy(users); },
  async create(input: UserInput) {
    await delay();
    const user: ManagedUser = { id: `usr-${Date.now()}`, ...input, status: "ACTIVE", createdAt: new Date().toISOString().slice(0, 10) };
    users = [user, ...users];
    appendAudit("Usuario registrado", `${user.fullName} (${user.role})`);
    return copy(user);
  },
  async update(id: string, input: UserInput) {
    await delay();
    const index = users.findIndex((user) => user.id === id);
    if (index === -1) throw new Error("No se encontró el usuario solicitado.");
    users[index] = { ...users[index], ...input };
    appendAudit("Usuario actualizado", `${users[index].fullName} (${users[index].role})`);
    return copy(users[index]);
  },
  async updateStatus(id: string, status: UserStatus) {
    await delay();
    const index = users.findIndex((user) => user.id === id);
    if (index === -1) throw new Error("No se encontró el usuario solicitado.");
    users[index] = { ...users[index], status };
    appendAudit(status === "ACTIVE" ? "Usuario activado" : "Usuario desactivado", `${users[index].fullName} (${users[index].role})`);
    return copy(users[index]);
  },
  async listAudit() { await delay(); return copy(audit); },
};
