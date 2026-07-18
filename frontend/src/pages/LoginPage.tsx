import axios from "axios";
import { type FormEvent, useState } from "react";
import { Navigate, useNavigate } from "react-router";
import { login } from "../authService";
import { isAuthenticated } from "../authStorage";
import type { ErrorResponse } from "../types";

/**
 * Login page for ADMIN, HR and EMPLOYEE users.
 */
export default function LoginPage() {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [errorMessage, setErrorMessage] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  /*
   * A logged-in user should not return to the login screen.
   */
  if (isAuthenticated()) {
    return <Navigate to="/dashboard" replace />;
  }

  /**
   * Submits username and password to the backend.
   */
  async function handleSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    setErrorMessage("");
    setIsSubmitting(true);

    try {
      await login({
        username: username.trim(),
        password,
      });

      navigate("/dashboard", {
        replace: true,
      });
    } catch (error: unknown) {
      if (axios.isAxiosError<ErrorResponse>(error)) {
        setErrorMessage(
          error.response?.data?.message ??
            "Unable to log in. Please try again.",
        );
      } else {
        setErrorMessage(
          "Unable to log in. Please try again.",
        );
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="login-page">
      <section className="login-card">
        <div className="login-heading">
          <p className="eyebrow">EEMS</p>

          <h1>Employee Management System</h1>

          <p>
            Sign in with your registered username and password.
          </p>
        </div>

        <form onSubmit={handleSubmit}>
          <label htmlFor="username">
            Username
          </label>

          <input
            id="username"
            type="text"
            value={username}
            onChange={(event) =>
              setUsername(event.target.value)
            }
            placeholder="Enter username"
            autoComplete="username"
            required
          />

          <label htmlFor="password">
            Password
          </label>

          <input
            id="password"
            type="password"
            value={password}
            onChange={(event) =>
              setPassword(event.target.value)
            }
            placeholder="Enter password"
            autoComplete="current-password"
            required
          />

          {errorMessage && (
            <div className="error-message" role="alert">
              {errorMessage}
            </div>
          )}

          <button
            type="submit"
            disabled={isSubmitting}
          >
            {isSubmitting ? "Signing in..." : "Sign in"}
          </button>
        </form>
      </section>
    </main>
  );
}