import api from "./api";
import type {
  ApiResponse,
  LeaveApplyRequest,
  LeaveResponse,
  LeaveReviewRequest,
  LeaveStatus,
} from "./types";

/**
 * Submits a leave application for the authenticated user.
 *
 * Backend endpoint:
 * POST /api/v1/leaves
 */
export async function applyForLeave(
  request: LeaveApplyRequest,
): Promise<LeaveResponse> {
  const response = await api.post<
    ApiResponse<LeaveResponse>
  >("/leaves", request);

  return response.data.data;
}

/**
 * Retrieves leave applications belonging to the
 * currently authenticated user.
 *
 * Backend endpoint:
 * GET /api/v1/leaves/my
 */
export async function getMyLeaves(): Promise<
  LeaveResponse[]
> {
  const response = await api.get<
    ApiResponse<LeaveResponse[]>
  >("/leaves/my");

  return response.data.data;
}

/**
 * Retrieves every leave application.
 *
 * The backend permits only ADMIN and HR users.
 *
 * Backend endpoint:
 * GET /api/v1/leaves
 */
export async function getAllLeaves(): Promise<
  LeaveResponse[]
> {
  const response = await api.get<
    ApiResponse<LeaveResponse[]>
  >("/leaves");

  return response.data.data;
}

/**
 * Approves or rejects a pending leave application.
 */
export async function reviewLeave(
  leaveId: number,
  status: Extract<LeaveStatus, "APPROVED" | "REJECTED">,
  comment: string | null,
): Promise<LeaveResponse> {
  const action =
    status === "APPROVED" ? "approve" : "reject";

  const request: LeaveReviewRequest = {
    comment,
  };

  const response = await api.put<
    ApiResponse<LeaveResponse>
  >(`/leaves/${leaveId}/${action}`, request);

  return response.data.data;
}