import type { AuthenticationResponse, StoredAuth } from "./types";

/**
 * Small utility module that manages login state in the browser

 * Local-storage key used for authentication data.
 */
const AUTH_STORAGE_KEY = "ems_auth";

/**
 * Saves authentication information after successful login.
 */
export function saveAuth(
  authenticationResponse: AuthenticationResponse,
): void {
  const storedAuth: StoredAuth = {
    accessToken: authenticationResponse.accessToken,
    username: authenticationResponse.username,
    role: authenticationResponse.role,
  };

  localStorage.setItem(
    AUTH_STORAGE_KEY,
    JSON.stringify(storedAuth),
  );
}

/**
 * Reads authentication information from local storage.
 */
export function getAuth(): StoredAuth | null {
  const storedValue = localStorage.getItem(AUTH_STORAGE_KEY);

  if (!storedValue) {
    return null;
  }

  try {
    return JSON.parse(storedValue) as StoredAuth;
  } catch {
    /*
     * Remove invalid browser data rather than allowing
     * the application to continue with corrupted state.
     */
    localStorage.removeItem(AUTH_STORAGE_KEY);
    return null;
  }
}

/**
 * Returns the saved JWT access token.
 */
export function getAccessToken(): string | null {
  return getAuth()?.accessToken ?? null;
}

/**
 * Checks whether authentication information exists.
 */
export function isAuthenticated(): boolean {
  return Boolean(getAccessToken());
}

/**
 * Removes authentication information during logout.
 */
export function clearAuth(): void {
  localStorage.removeItem(AUTH_STORAGE_KEY);
}