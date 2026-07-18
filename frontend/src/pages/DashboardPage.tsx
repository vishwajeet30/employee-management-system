import { useNavigate } from "react-router";
import { clearAuth, getAuth } from "../authStorage";

/**
 * Small landing page displayed after successful login.
 *
 * Employee and Leave navigation will be connected next.
 */
export default function DashboardPage() {
  const navigate = useNavigate();
  const auth = getAuth();

  /**
   * Clears authentication and returns to login.
   */
  function handleLogout() {
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
            Signed in as{" "}
            <strong>{auth?.role}</strong>
          </p>
        </div>

        <button
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
            View, search and manage employee records.
          </p>

          <span>Coming in the next step</span>
        </article>

        <article className="dashboard-card">
          <h2>Leave Management</h2>

          <p>
            Apply for leave and review leave requests.
          </p>

          <span>Coming in the next step</span>
        </article>
      </section>
    </main>
  );
}