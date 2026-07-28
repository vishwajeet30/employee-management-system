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

/**
 * Employee information returned by EmployeeResponse.java.
 */
export interface EmployeeResponse {
  id: number;
  employeeCode: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string | null;
  department: string | null;
  designation: string | null;
  salary: number;
  joiningDate: string;
  status: boolean;
}

/**
 * Generic Spring Data pagination response.
 */
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

/**
 * Leave types supported by LeaveType.java.
 */
export type LeaveType =
  | "CASUAL"
  | "SICK"
  | "EARNED";

/**
 * Leave statuses supported by LeaveStatus.java.
 */
export type LeaveStatus =
  | "PENDING"
  | "APPROVED"
  | "REJECTED";

/**
 * Request body used when applying for leave.
 */
export interface LeaveApplyRequest {
  leaveType: LeaveType;
  startDate: string;
  endDate: string;
  reason: string;
}

/**
 * Optional comment sent while reviewing leave.
 */
export interface LeaveReviewRequest {
  comment: string | null;
}

/**
 * Leave information returned by LeaveResponse.java.
 */
export interface LeaveResponse {
  id: number;
  applicantUsername: string;
  leaveType: LeaveType;
  startDate: string;
  endDate: string;
  totalDays: number;
  reason: string;
  status: LeaveStatus;
  reviewedBy: string | null;
  reviewComment: string | null;
  createdAt: string;
}