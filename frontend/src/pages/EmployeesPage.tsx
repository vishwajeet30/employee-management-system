import axios from "axios";
import {
  type FormEvent,
  useEffect,
  useState,
} from "react";
import { useNavigate } from "react-router";
import { clearAuth, getAuth } from "../authStorage";
import {
  getEmployees,
  searchEmployees,
} from "../employeeService";
import type {
  EmployeeResponse,
  ErrorResponse,
  PageResponse,
} from "../types";
import "./EmployeesPage.css";

/**
 * Formats salary values using the Indian Rupee format.
 */
const salaryFormatter = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
  maximumFractionDigits: 0,
});

/**
 * Displays employees with search and pagination.
 */
export default function EmployeesPage() {
  const navigate = useNavigate();
  const auth = getAuth();

  const [employees, setEmployees] = useState<EmployeeResponse[]>(
    [],
  );

  const [pageNumber, setPageNumber] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  /*
   * searchText contains the current input value.
   * activeKeyword contains the value currently sent to the API.
   */
  const [searchText, setSearchText] = useState("");
  const [activeKeyword, setActiveKeyword] = useState("");

  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  /**
   * Loads employees whenever the page number
   * or active search keyword changes.
   */
  useEffect(() => {
    let requestIsActive = true;

    async function loadEmployees(): Promise<void> {
      setIsLoading(true);
      setErrorMessage("");

      try {
        let pageResponse: PageResponse<EmployeeResponse>;

        if (activeKeyword) {
          pageResponse = await searchEmployees(
            activeKeyword,
            pageNumber,
          );
        } else {
          pageResponse = await getEmployees(pageNumber);
        }

        /*
         * Ignore the result if the component was unmounted
         * before the request completed.
         */
        if (!requestIsActive) {
          return;
        }

        setEmployees(pageResponse.content);
        setTotalPages(pageResponse.totalPages);
        setTotalElements(pageResponse.totalElements);
      } catch (error: unknown) {
        if (!requestIsActive) {
          return;
        }

        if (axios.isAxiosError<ErrorResponse>(error)) {
          setErrorMessage(
            error.response?.data?.message ??
              "Unable to retrieve employees.",
          );
        } else {
          setErrorMessage("Unable to retrieve employees.");
        }
      } finally {
        if (requestIsActive) {
          setIsLoading(false);
        }
      }
    }

    void loadEmployees();

    return () => {
      requestIsActive = false;
    };
  }, [pageNumber, activeKeyword]);

  /**
   * Starts a new employee search.
   */
  function handleSearch(
    event: FormEvent<HTMLFormElement>,
  ): void {
    event.preventDefault();

    const trimmedKeyword = searchText.trim();

    // A new search should always begin from the first page.
    setPageNumber(0);
    setActiveKeyword(trimmedKeyword);
  }

  /**
   * Clears search filters and reloads all employees.
   */
  function handleClearSearch(): void {
    setSearchText("");
    setActiveKeyword("");
    setPageNumber(0);
  }

  /**
   * Removes the JWT and returns to the login page.
   */
  function handleLogout(): void {
    clearAuth();

    navigate("/login", {
      replace: true,
    });
  }

  /**
   * Moves to the previous result page.
   */
  function goToPreviousPage(): void {
    setPageNumber((currentPage) =>
      Math.max(currentPage - 1, 0),
    );
  }

  /**
   * Moves to the next result page.
   */
  function goToNextPage(): void {
    setPageNumber((currentPage) =>
      Math.min(currentPage + 1, totalPages - 1),
    );
  }

  return (
    <main className="employees-page">
      <header className="employees-header">
        <div>
          <p className="eyebrow">Employee Management</p>

          <h1>Employees</h1>

          <p>
            Logged in as{" "}
            <strong>{auth?.username}</strong> ({auth?.role})
          </p>
        </div>

        <div className="employees-header-actions">
          <button
            type="button"
            className="outline-button"
            onClick={() => navigate("/dashboard")}
          >
            Dashboard
          </button>

          <button
            type="button"
            className="outline-button"
            onClick={handleLogout}
          >
            Logout
          </button>
        </div>
      </header>

      <section className="employees-content">
        <div className="employees-toolbar">
          <div>
            <h2>Employee records</h2>

            <p>
              {totalElements} employee
              {totalElements === 1 ? "" : "s"} found
            </p>
          </div>

          <form
            className="employee-search-form"
            onSubmit={handleSearch}
          >
            <input
              type="search"
              value={searchText}
              onChange={(event) =>
                setSearchText(event.target.value)
              }
              placeholder="Search name, email or code"
              aria-label="Search employees"
            />

            <button type="submit">
              Search
            </button>

            {activeKeyword && (
              <button
                type="button"
                className="clear-button"
                onClick={handleClearSearch}
              >
                Clear
              </button>
            )}
          </form>
        </div>

        {activeKeyword && (
          <div className="search-summary">
            Search results for:{" "}
            <strong>{activeKeyword}</strong>
          </div>
        )}

        {errorMessage && (
          <div className="page-error" role="alert">
            {errorMessage}
          </div>
        )}

        {isLoading ? (
          <div className="employees-state">
            Loading employees...
          </div>
        ) : employees.length === 0 ? (
          <div className="employees-state">
            No employee records found.
          </div>
        ) : (
          <div className="employee-table-wrapper">
            <table className="employee-table">
              <thead>
                <tr>
                  <th>Code</th>
                  <th>Employee</th>
                  <th>Department</th>
                  <th>Designation</th>
                  <th>Joining date</th>
                  <th>Salary</th>
                  <th>Status</th>
                </tr>
              </thead>

              <tbody>
                {employees.map((employee) => (
                  <tr key={employee.id}>
                    <td>
                      <strong>{employee.employeeCode}</strong>
                    </td>

                    <td>
                      <div className="employee-name">
                        {employee.firstName}{" "}
                        {employee.lastName}
                      </div>

                      <div className="employee-email">
                        {employee.email}
                      </div>
                    </td>

                    <td>
                      {employee.department ?? "—"}
                    </td>

                    <td>
                      {employee.designation ?? "—"}
                    </td>

                    <td>{employee.joiningDate}</td>

                    <td>
                      {salaryFormatter.format(
                        employee.salary,
                      )}
                    </td>

                    <td>
                      <span
                        className={
                          employee.status
                            ? "status-badge active"
                            : "status-badge inactive"
                        }
                      >
                        {employee.status
                          ? "Active"
                          : "Inactive"}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {!isLoading && totalPages > 0 && (
          <footer className="pagination">
            <button
              type="button"
              className="outline-button"
              onClick={goToPreviousPage}
              disabled={pageNumber === 0}
            >
              Previous
            </button>

            <span>
              Page {pageNumber + 1} of {totalPages}
            </span>

            <button
              type="button"
              className="outline-button"
              onClick={goToNextPage}
              disabled={pageNumber >= totalPages - 1}
            >
              Next
            </button>
          </footer>
        )}
      </section>
    </main>
  );
}