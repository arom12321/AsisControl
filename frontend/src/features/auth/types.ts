export type UserRole = "ADMINISTRADOR" | "DOCENTE" | "ESTUDIANTE" | "APODERADO";

export type AuthenticatedUser = {
  id: string;
  username: string;
  fullName: string;
  role: UserRole;
};

export type AuthSession = {
  accessToken: string;
  user: AuthenticatedUser;
};

export type LoginCredentials = {
  username: string;
  password: string;
};

export type LoginResult =
  | { ok: true; session: AuthSession }
  | { ok: false; message: string };

/**
 * Contrato que consumirá la interfaz. Cuando el backend esté disponible,
 * reemplazar MockAuthService por una implementación basada en HTTP.
 */
export interface AuthService {
  login(credentials: LoginCredentials): Promise<LoginResult>;
  logout(): Promise<void>;
}
