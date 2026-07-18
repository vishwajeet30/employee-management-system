/**
 * Authentication data returned by the Spring Boot backend.
 */
export interface AuthenticationResponse {
  accessToken: string;
  tokenType: string;
  username: string;
  role: string;
}

/**
 * Standard successful response returned by the backend.
 */
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

/**
 * Standard error response returned by the backend.
 */
export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  validationErrors?: Record<string, string> | null;
}

/**
 * Data submitted from the login form.
 */
export interface LoginRequest {
  username: string;
  password: string;
}

/**
 * Authentication information stored in the browser.
 */
export interface StoredAuth {
  accessToken: string;
  username: string;
  role: string;
}