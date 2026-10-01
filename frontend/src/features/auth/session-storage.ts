import type { AuthSession } from "./types";

const SESSION_KEY = "asiscontrol.session";

export function readSession(): AuthSession | null {
  const serializedSession = window.localStorage.getItem(SESSION_KEY);
  if (!serializedSession) return null;

  try {
    return JSON.parse(serializedSession) as AuthSession;
  } catch {
    window.localStorage.removeItem(SESSION_KEY);
    return null;
  }
}

export function saveSession(session: AuthSession) {
  window.localStorage.setItem(SESSION_KEY, JSON.stringify(session));
}

export function clearSession() {
  window.localStorage.removeItem(SESSION_KEY);
}
