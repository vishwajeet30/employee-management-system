import axios from "axios";
import {
  type FormEvent,
  useCallback,
  useEffect,
  useState,
} from "react";
import { useNavigate } from "react-router";
import { clearAuth, getAuth } from "../authStorage";
import {
  applyForLeave,
  getAllLeaves,
  getMyLeaves,
  reviewLeave,
} from "../leaveService";
import type {
  ErrorResponse,
  LeaveResponse,
  LeaveStatus,
  LeaveType,
} from "../types";
import "./LeavePage.css";

/**
 * Returns today's date in YYYY-MM-DD format using the
 * browser's local timezone.
 */
function getToday(): string {
  const today = new Date();
  const timezoneOffset = today.getTimezoneOffset();

  return new Date(
    today.getTime() - timezoneOffset * 60_000,
  )
    .toISOString()
    .slice(0, 10);
}

/**
 * Converts a backend date into a readable format.
 */
function formatDate(date: string): string {
  return new Date(`${date}T00:00:00`).toLocaleDateString(
    "en-IN",
    {
      day: "2-digit",
      month: "short",
      year: "numeric",
    },
  );
}

/**
 * Extracts a readable message from a backend error.
 */
function getErrorMessage(
  error: unknown,
  fallbackMessage: string,
): string {
  if (axios.isAxiosError<ErrorResponse>(error)) {
    return (
      error.response?.data?.message ?? fallbackMessage
    );
  }

  return fallbackMessage;
}

interface LeaveTableProps {
  leaves: LeaveResponse[];
  showApplicant: boolean;
  allowReview: boolean;
  reviewingLeaveId: number | null;
  onReview?: (
    leaveId: number,
    status: Extract<
      LeaveStatus,
      "APPROVED" | "REJECTED"
    >,
  ) => void;
}

/**
 * Reusable table for personal and administrative leave lists.
 */
function LeaveTable({
  leaves,
  showApplicant,
  allowReview,
  reviewingLeaveId,
  onReview,
}: LeaveTableProps) {
  if (leaves.length === 0) {
    return (
      <div className="leave-empty-state">
        No leave applications found.
      </div>
    );
  }

  return (
    <div className="leave-table-wrapper">
      <table className="leave-table">
        <thead>
          <tr>
            {showApplicant && <th>Applicant</th>}
            <th>Type</th>
            <th>Dates</th>
            <th>Days</th>
            <th>Reason</th>
            <th>Status</th>
            <th>Reviewed by</th>
            {allowReview && <th>Actions</th>}
          </tr>
        </thead>

        <tbody>
          {leaves.map((leave) => {
            const isReviewing =
              reviewingLeaveId === leave.id;

            return (
              <tr key={leave.id}>
                {showApplicant && (
                  <td>
                    <strong>
                      {leave.applicantUsername}
                    </strong>
                  </td>
                )}

                <td>
                  {leave.leaveType.replace("_", " ")}
                </td>

                <td>
                  <div>{formatDate(leave.startDate)}</div>

                  <div className="leave-secondary-text">
                    to {formatDate(leave.endDate)}
                  </div>
                </td>

                <td>{leave.totalDays}</td>

                <td className="leave-reason">
                  {leave.reason}

                  {leave.reviewComment && (
                    <div className="review-comment">
                      Review: {leave.reviewComment}
                    </div>
                  )}
                </td>

                <td>
                  <span
                    className={`leave-status ${leave.status.toLowerCase()}`}
                  >
                    {leave.status}
                  </span>
                </td>

                <td>{leave.reviewedBy ?? "—"}</td>

                {allowReview && (
                  <td>
                    {leave.status === "PENDING" ? (
                      <div className="review-actions">
                        <button
                          type="button"
                          className="approve-button"
                          disabled={isReviewing}
                          onClick={() =>
                            onReview?.(
                              leave.id,
                              "APPROVED",
                            )
                          }
                        >
                          Approve
                        </button>

                        <button
                          type="button"
                          className="reject-button"
                          disabled={isReviewing}
                          onClick={() =>
                            onReview?.(
                              leave.id,
                              "REJECTED",
                            )
                          }
                        >
                          Reject
                        </button>
                      </div>
                    ) : (
                      <span className="leave-secondary-text">
                        Reviewed
                      </span>
                    )}
                  </td>
                )}
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}

/**
 * Leave-management page.
 *
 * Every authenticated user can:
 * - Apply for leave
 * - View personal leave applications
 *
 * ADMIN and HR can additionally:
 * - View all leave applications
 * - Approve or reject pending applications
 */
export default function LeavePage() {
  const navigate = useNavigate();
  const auth = getAuth();

  /*
   * Remove a possible ROLE_ prefix so both formats work:
   * ADMIN and ROLE_ADMIN.
   */
  const normalizedRole =
    auth?.role?.replace("ROLE_", "") ?? "";

  const canReview =
    normalizedRole === "ADMIN" ||
    normalizedRole === "HR";

  const today = getToday();

  const [leaveType, setLeaveType] =
    useState<LeaveType>("CASUAL");

  const [startDate, setStartDate] =
    useState(today);

  const [endDate, setEndDate] =
    useState(today);

  const [reason, setReason] = useState("");

  const [myLeaves, setMyLeaves] = useState<
    LeaveResponse[]
  >([]);

  const [allLeaves, setAllLeaves] = useState<
    LeaveResponse[]
  >([]);

  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] =
    useState(false);

  const [reviewingLeaveId, setReviewingLeaveId] =
    useState<number | null>(null);

  const [errorMessage, setErrorMessage] =
    useState("");

  const [successMessage, setSuccessMessage] =
    useState("");

  /**
   * Loads the personal leave list and, for ADMIN/HR,
   * the complete leave list.
   */
  const loadLeaveData = useCallback(async () => {
    setIsLoading(true);
    setErrorMessage("");

    try {
      const myLeavesRequest = getMyLeaves();

      const allLeavesRequest = canReview
        ? getAllLeaves()
        : Promise.resolve([]);

      const [personalLeaves, administrativeLeaves] =
        await Promise.all([
          myLeavesRequest,
          allLeavesRequest,
        ]);

      setMyLeaves(personalLeaves);
      setAllLeaves(administrativeLeaves);
    } catch (error: unknown) {
      setErrorMessage(
        getErrorMessage(
          error,
          "Unable to retrieve leave applications.",
        ),
      );
    } finally {
      setIsLoading(false);
    }
  }, [canReview]);

  /**
   * Load leave information when the page opens.
   */
  useEffect(() => {
    void loadLeaveData();
  }, [loadLeaveData]);

  /**
   * Submits a new leave application.
   */
  async function handleApplyLeave(
    event: FormEvent<HTMLFormElement>,
  ): Promise<void> {
    event.preventDefault();

    setErrorMessage("");
    setSuccessMessage("");

    if (endDate < startDate) {
      setErrorMessage(
        "End date cannot be before the start date.",
      );
      return;
    }

    setIsSubmitting(true);

    try {
      await applyForLeave({
        leaveType,
        startDate,
        endDate,
        reason: reason.trim(),
      });

      setSuccessMessage(
        "Leave application submitted successfully.",
      );

      // Reset the form after successful submission.
      setLeaveType("CASUAL");
      setStartDate(getToday());
      setEndDate(getToday());
      setReason("");

      await loadLeaveData();
    } catch (error: unknown) {
      setErrorMessage(
        getErrorMessage(
          error,
          "Unable to submit the leave application.",
        ),
      );
    } finally {
      setIsSubmitting(false);
    }
  }

  /**
   * Approves or rejects a pending leave application.
   */
  async function handleReview(
    leaveId: number,
    status: Extract<
      LeaveStatus,
      "APPROVED" | "REJECTED"
    >,
  ): Promise<void> {
    const actionName =
      status === "APPROVED" ? "approval" : "rejection";

    const comment = window.prompt(
      `Enter an optional ${actionName} comment:`,
      "",
    );

    /*
     * Pressing Cancel means the user does not want to
     * continue with the review action.
     */
    if (comment === null) {
      return;
    }

    setErrorMessage("");
    setSuccessMessage("");
    setReviewingLeaveId(leaveId);

    try {
      await reviewLeave(
        leaveId,
        status,
        comment.trim() || null,
      );

      setSuccessMessage(
        `Leave application ${
          status === "APPROVED"
            ? "approved"
            : "rejected"
        } successfully.`,
      );

      await loadLeaveData();
    } catch (error: unknown) {
      setErrorMessage(
        getErrorMessage(
          error,
          "Unable to review the leave application.",
        ),
      );
    } finally {
      setReviewingLeaveId(null);
    }
  }

  /**
   * Logs out and removes the stored JWT.
   */
  function handleLogout(): void {
    clearAuth();

    navigate("/login", {
      replace: true,
    });
  }

  return (
    <main className="leave-page">
      <header className="leave-header">
        <div>
          <p className="eyebrow">Leave Management</p>

          <h1>Leave applications</h1>

          <p>
            Logged in as{" "}
            <strong>{auth?.username}</strong> (
            {normalizedRole})
          </p>
        </div>

        <div className="leave-header-actions">
          <button
            type="button"
            className="leave-outline-button"
            onClick={() => navigate("/dashboard")}
          >
            Dashboard
          </button>

          <button
            type="button"
            className="leave-outline-button"
            onClick={handleLogout}
          >
            Logout
          </button>
        </div>
      </header>

      <div className="leave-page-content">
        {errorMessage && (
          <div className="leave-message error" role="alert">
            {errorMessage}
          </div>
        )}

        {successMessage && (
          <div
            className="leave-message success"
            role="status"
          >
            {successMessage}
          </div>
        )}

        <section className="leave-section">
          <div className="leave-section-heading">
            <div>
              <h2>Apply for leave</h2>

              <p>
                Submit a new request using future or
                current dates.
              </p>
            </div>
          </div>

          <form
            className="leave-apply-form"
            onSubmit={handleApplyLeave}
          >
            <div className="leave-form-field">
              <label htmlFor="leaveType">
                Leave type
              </label>

              <select
                id="leaveType"
                value={leaveType}
                onChange={(event) =>
                  setLeaveType(
                    event.target.value as LeaveType,
                  )
                }
              >
                <option value="CASUAL">
                  Casual leave
                </option>

                <option value="SICK">
                  Sick leave
                </option>

                <option value="EARNED">
                  Earned leave
                </option>
              </select>
            </div>

            <div className="leave-form-field">
              <label htmlFor="startDate">
                Start date
              </label>

              <input
                id="startDate"
                type="date"
                min={today}
                value={startDate}
                onChange={(event) =>
                  setStartDate(event.target.value)
                }
                required
              />
            </div>

            <div className="leave-form-field">
              <label htmlFor="endDate">
                End date
              </label>

              <input
                id="endDate"
                type="date"
                min={startDate || today}
                value={endDate}
                onChange={(event) =>
                  setEndDate(event.target.value)
                }
                required
              />
            </div>

            <div className="leave-form-field reason-field">
              <label htmlFor="reason">Reason</label>

              <textarea
                id="reason"
                value={reason}
                onChange={(event) =>
                  setReason(event.target.value)
                }
                placeholder="Enter the reason for leave"
                maxLength={500}
                required
              />
            </div>

            <div className="leave-form-action">
              <button
                type="submit"
                disabled={isSubmitting}
              >
                {isSubmitting
                  ? "Submitting..."
                  : "Submit application"}
              </button>
            </div>
          </form>
        </section>

        <section className="leave-section">
          <div className="leave-section-heading">
            <div>
              <h2>My leave applications</h2>

              <p>
                Track the status of your submitted
                requests.
              </p>
            </div>

            <span className="leave-count">
              {myLeaves.length} request
              {myLeaves.length === 1 ? "" : "s"}
            </span>
          </div>

          {isLoading ? (
            <div className="leave-empty-state">
              Loading leave applications...
            </div>
          ) : (
            <LeaveTable
              leaves={myLeaves}
              showApplicant={false}
              allowReview={false}
              reviewingLeaveId={reviewingLeaveId}
            />
          )}
        </section>

        {canReview && (
          <section className="leave-section">
            <div className="leave-section-heading">
              <div>
                <h2>All leave applications</h2>

                <p>
                  Approve or reject pending employee
                  requests.
                </p>
              </div>

              <span className="leave-count">
                {allLeaves.length} request
                {allLeaves.length === 1 ? "" : "s"}
              </span>
            </div>

            {isLoading ? (
              <div className="leave-empty-state">
                Loading leave applications...
              </div>
            ) : (
              <LeaveTable
                leaves={allLeaves}
                showApplicant
                allowReview
                reviewingLeaveId={reviewingLeaveId}
                onReview={(leaveId, status) => {
                  void handleReview(leaveId, status);
                }}
              />
            )}
          </section>
        )}
      </div>
    </main>
  );
}