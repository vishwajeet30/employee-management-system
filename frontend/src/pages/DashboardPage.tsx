import { useNavigate } from "react-router";
import { clearAuth, getAuth } from "../authStorage";

/**
 * Main page displayed after successful authentication.
 */
export default function DashboardPage() {
  const navigate = useNavigate();
  const auth = getAuth();

  /**
   * Removes the stored JWT and returns to login.
   */
  function handleLogout(): void {
    clearAuth();

    navigate("/login", {
      replace: true,
    });
  }

  return (
    <main className="dashboard-page">
      <header className="dashboard-header">
        <div>
          <p className="eyebrow">EEMS Dashboard</p>

          <h1>Welcome, {auth?.username}</h1>

          <p>
            Signed in as <strong>{auth?.role}</strong>
          </p>
        </div>

        <button
          type="button"
          className="secondary-button"
          onClick={handleLogout}
        >
          Logout
        </button>
      </header>

      <section className="dashboard-grid">
        <article className="dashboard-card">
          <h2>Employees</h2>

          <p>
            View and search employee records using secured
            Spring Boot APIs.
          </p>

          <button
            type="button"
            onClick={() => navigate("/employees")}
          >
            Open employees
          </button>
        </article>

        <article className="dashboard-card">
          <h2>Leave Management</h2>

          <p>
            Apply for leave, view your requests, and review
            pending applications.
          </p>

          <button
            type="button"
            onClick={() => navigate("/leaves")}
          >
            Open leave management
          </button>
        </article>
      </section>
    </main>
  );
}