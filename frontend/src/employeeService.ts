import api from "./api";
import type {
  ApiResponse,
  EmployeeResponse,
  PageResponse,
} from "./types";

/**
 * Number of employees displayed on one page.
 */
export const EMPLOYEE_PAGE_SIZE = 10;

/**
 * Retrieves employees using backend pagination and sorting.
 *
 * The shared Axios client automatically includes the JWT token.
 */
export async function getEmployees(
  page: number,
): Promise<PageResponse<EmployeeResponse>> {
  const response = await api.get<
    ApiResponse<PageResponse<EmployeeResponse>>
  >("/employees", {
    params: {
      page,
      size: EMPLOYEE_PAGE_SIZE,
      sortBy: "id",

      /*
       * This parameter name must match the Spring Boot
       * controller parameter: sortDir.
       */
      sortDir: "desc",
    },
  });

  return response.data.data;
}

/**
 * Searches employees by keyword.
 */
export async function searchEmployees(
  keyword: string,
  page: number,
): Promise<PageResponse<EmployeeResponse>> {
  const response = await api.get<
    ApiResponse<PageResponse<EmployeeResponse>>
  >("/employees/search", {
    params: {
      keyword,
      page,

      size: EMPLOYEE_PAGE_SIZE,
    },
  });

  return response.data.data;
}