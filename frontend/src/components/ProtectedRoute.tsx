import type { ReactNode } from "react";
import { Navigate } from "react-router";
import { isAuthenticated } from "../authStorage";

interface ProtectedRouteProps {
  children: ReactNode;
}

/**
 * Prevents unauthenticated users from accessing protected pages.
 */
export default function ProtectedRoute({
  children,
}: ProtectedRouteProps) {
  if (!isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }

  return children;
}