"use client";

import { useEffect, useState } from "react";
import { mockAuthService } from "./mock-auth-service";
import { clearSession, readSession, saveSession } from "./session-storage";
import type { AuthSession, LoginCredentials } from "./types";

export function useAuth() {
  const [session, setSession] = useState<AuthSession | null>(null);
  const [isRestoringSession, setIsRestoringSession] = useState(true);

  useEffect(() => {
    const restoreTimer = window.setTimeout(() => {
      setSession(readSession());
      setIsRestoringSession(false);
    }, 0);
    return () => window.clearTimeout(restoreTimer);
  }, []);

  async function login(credentials: LoginCredentials) {
    const result = await mockAuthService.login(credentials);
    if (result.ok) {
      saveSession(result.session);
      setSession(result.session);
    }
    return result;
  }

  async function logout() {
    await mockAuthService.logout();
    clearSession();
    setSession(null);
  }

  return { session, isRestoringSession, login, logout };
}
