import api from "./api";
import { saveAuth } from "./authStorage";
import type {
  ApiResponse,
  AuthenticationResponse,
  LoginRequest,
} from "./types";

/**
 * Authenticates a user using the Spring Boot login API.
 */
export async function login(
  request: LoginRequest,
): Promise<AuthenticationResponse> {
  const response = await api.post<
    ApiResponse<AuthenticationResponse>
  >("/auth/login", request);

  const authenticationResponse = response.data.data;

  // Store JWT, username and role after successful login.
  saveAuth(authenticationResponse);

  return authenticationResponse;
}