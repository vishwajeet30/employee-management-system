import axios from "axios";
import { clearAuth, getAccessToken } from "./authStorage";

/**
 * Shared Axios client for all backend API requests.
 *
 * Vite forwards /api requests to the Spring Boot backend.
 */
const api = axios.create({
  baseURL: "/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Add the JWT token before every request.
 *
 * Result:
 * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
 */
api.interceptors.request.use((config) => {
  const accessToken = getAccessToken();

  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }

  return config;
});

/**
 * Remove invalid authentication when the backend returns 401.
 *
 * A 401 normally means the token is missing, invalid or expired.
 */
api.interceptors.response.use(
  (response) => response,

  (error: unknown) => {
    if (
      axios.isAxiosError(error) &&
      error.response?.status === 401
    ) {
      clearAuth();
    }

    return Promise.reject(error);
  },
);

export default api;