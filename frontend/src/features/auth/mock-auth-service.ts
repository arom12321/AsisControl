import type { AuthService, LoginCredentials, LoginResult, UserRole } from "./types";

type DemoAccount = LoginCredentials & {
  id: string;
  fullName: string;
  role: UserRole;
};

// Temporal: estas cuentas se eliminan cuando el backend exponga el endpoint de inicio de sesión.
const DEMO_ACCOUNTS: readonly DemoAccount[] = [
  { id: "usr-admin-001", username: "admin", password: "admin123", fullName: "Ana Ríos", role: "ADMINISTRADOR" },
  { id: "usr-docente-001", username: "docente", password: "docente123", fullName: "Patricia Morales", role: "DOCENTE" },
  { id: "usr-estudiante-001", username: "estudiante", password: "estudiante123", fullName: "Luis García", role: "ESTUDIANTE" },
  { id: "usr-apoderado-001", username: "apoderado", password: "apoderado123", fullName: "María Rivas", role: "APODERADO" },
];

export const mockAuthService: AuthService = {
  async login({ username, password }: LoginCredentials): Promise<LoginResult> {
    // Mantiene la naturaleza asíncrona que tendrá la llamada HTTP real.
    await new Promise((resolve) => window.setTimeout(resolve, 350));

    const account = DEMO_ACCOUNTS.find(
      (candidate) => candidate.username === username.trim().toLowerCase() && candidate.password === password,
    );

    if (!account) {
      return { ok: false, message: "Usuario o contraseña incorrectos. Verifica tus datos e inténtalo nuevamente." };
    }

    return {
      ok: true,
      session: {
        accessToken: `demo-token-${account.id}`,
        user: { id: account.id, username: account.username, fullName: account.fullName, role: account.role },
      },
    };
  },
  async logout() {
    return Promise.resolve();
  },
};

export const demoCredentials = DEMO_ACCOUNTS.map(({ username, password, role }) => ({ username, password, role }));
