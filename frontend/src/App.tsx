import {
  Navigate,
  Route,
  Routes,
} from "react-router";
import ProtectedRoute from "./components/ProtectedRoute";
import DashboardPage from "./pages/DashboardPage";
import EmployeesPage from "./pages/EmployeesPage";
import LeavePage from "./pages/LeavePage";
import LoginPage from "./pages/LoginPage";

/**
 * Defines all routes in the compact React frontend.
 */
export default function App() {
  return (
    <Routes>
      {/* Public login page */}
      <Route
        path="/login"
        element={<LoginPage />}
      />

      {/* Protected dashboard */}
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <DashboardPage />
          </ProtectedRoute>
        }
      />

      {/* Protected employee list */}
      <Route
        path="/employees"
        element={
          <ProtectedRoute>
            <EmployeesPage />
          </ProtectedRoute>
        }
      />

      {/* Protected leave-management page */}
      <Route
        path="/leaves"
        element={
          <ProtectedRoute>
            <LeavePage />
          </ProtectedRoute>
        }
      />

      {/* Unknown URLs return to the dashboard */}
      <Route
        path="*"
        element={
          <Navigate
            to="/dashboard"
            replace
          />
        }
      />
    </Routes>
  );
}