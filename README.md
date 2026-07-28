# Enterprise Employee Management System

A full-stack Employee Management System built with Spring Boot and React.

The application demonstrates secure authentication, role-based authorization, employee management, leave management, automated testing, Docker containerization, and GitHub Actions CI/CD.

## Project Overview

The Enterprise Employee Management System allows organizations to manage employees and leave requests through a secure web application.

Users authenticate using JWT tokens and receive permissions based on their assigned role.

### Supported Roles

| Role     | Permissions                                                                     |
| -------- | ------------------------------------------------------------------------------- |
| ADMIN    | Manage employees, view all employees, delete employees, approve or reject leave |
| HR       | Create and update employees, view employees, approve or reject leave            |
| EMPLOYEE | View employee information, apply for leave, view personal leave requests        |

## Main Features

### Authentication and Security

* User registration
* User login
* BCrypt password encryption
* JWT-based authentication
* Role-based authorization
* Stateless Spring Security configuration
* Automatic JWT validation for protected APIs
* Expired and invalid token handling

### Employee Management

* Create employee
* Update employee
* View employee by ID
* View employee by employee code
* View paginated employee records
* Search employees
* Sort employee records
* Delete employee
* Duplicate employee-code validation
* Duplicate email validation

### Leave Management

* Apply for leave
* View personal leave requests
* View all leave requests
* Approve leave
* Reject leave
* Prevent repeated review
* Validate leave date ranges
* Track reviewer and review comments

### React Frontend

* Login page
* Protected routes
* JWT storage
* Automatic Bearer-token attachment
* Dashboard
* Employee list
* Employee search
* Employee pagination
* Leave application form
* Personal leave history
* ADMIN and HR leave review
* Logout

### Testing

* JUnit 5
* Mockito
* Service-layer unit tests
* Spring application-context test
* H2 in-memory test database
* Tests run without requiring MySQL

### DevOps

* Multi-stage Docker build
* Docker Compose
* MySQL container
* Persistent MySQL volume
* MySQL health check
* GitHub Actions CI/CD
* Backend test automation
* Frontend production build
* Docker image verification
* GitHub Container Registry publishing

---

## Technology Stack

### Backend

* Java 21
* Spring Boot 3
* Spring Security
* Spring Data JPA
* Hibernate
* MySQL
* H2
* JWT
* Maven
* Lombok
* Bean Validation
* Swagger/OpenAPI
* JUnit 5
* Mockito

### Frontend

* React
* TypeScript
* Vite
* Axios
* React Router
* CSS

### DevOps

* Docker
* Docker Compose
* GitHub Actions
* GitHub Container Registry

---

## Architecture

```text
React Frontend
      |
      | HTTP + JWT
      v
Spring Security Filter Chain
      |
      v
REST Controllers
      |
      v
Service Layer
      |
      v
Mapper Layer
      |
      v
Spring Data JPA Repositories
      |
      v
MySQL Database
```

### Backend Request Flow

```text
Request
   |
   v
JwtAuthenticationFilter
   |
   v
SecurityContext
   |
   v
Controller
   |
   v
Service
   |
   v
Repository
   |
   v
MySQL
```

---

## Project Structure

```text
employee-management-system/
|
├── .github/
│   └── workflows/
│       └── ci-cd.yml
|
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── api.ts
│   │   ├── authService.ts
│   │   ├── authStorage.ts
│   │   ├── employeeService.ts
│   │   ├── leaveService.ts
│   │   ├── App.tsx
│   │   ├── main.tsx
│   │   └── types.ts
│   ├── package.json
│   └── vite.config.ts
|
├── src/
│   ├── main/
│   │   ├── java/com/project/ems/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── mapper/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   ├── service/
│   │   │   └── EmployeeManagementSystemApplication.java
│   │   └── resources/
│   │       └── application.yml
│   |
│   └── test/
│       ├── java/com/project/ems/
│       └── resources/
│           └── application.yml
|
├── .dockerignore
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## API Endpoints

### Authentication

| Method | Endpoint                | Access                      |
| ------ | ----------------------- | --------------------------- |
| POST   | `/api/v1/auth/register` | Public development endpoint |
| POST   | `/api/v1/auth/login`    | Public                      |

### Employees

| Method | Endpoint                   | Access        |
| ------ | -------------------------- | ------------- |
| POST   | `/api/v1/employees`        | ADMIN, HR     |
| GET    | `/api/v1/employees`        | Authenticated |
| GET    | `/api/v1/employees/{id}`   | Authenticated |
| PUT    | `/api/v1/employees/{id}`   | ADMIN, HR     |
| DELETE | `/api/v1/employees/{id}`   | ADMIN         |
| GET    | `/api/v1/employees/search` | Authenticated |

### Leave Management

| Method | Endpoint                      | Access        |
| ------ | ----------------------------- | ------------- |
| POST   | `/api/v1/leaves`              | Authenticated |
| GET    | `/api/v1/leaves/my`           | Authenticated |
| GET    | `/api/v1/leaves`              | ADMIN, HR     |
| PUT    | `/api/v1/leaves/{id}/approve` | ADMIN, HR     |
| PUT    | `/api/v1/leaves/{id}/reject`  | ADMIN, HR     |

---

## Example Login Request

```http
POST /api/v1/auth/login
Content-Type: application/json
```

```json
{
  "username": "admin",
  "password": "Admin@12345"
}
```

Example response:

```json
{
  "success": true,
  "message": "Login successful.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "username": "admin",
    "role": "ADMIN"
  },
  "timestamp": "2026-07-28T12:00:00"
}
```

Use the token in protected requests:

```http
Authorization: Bearer <access-token>
```

---

## Example Employee Request

```http
POST /api/v1/employees
Authorization: Bearer <admin-or-hr-token>
Content-Type: application/json
```

```json
{
  "employeeCode": "EMP001",
  "firstName": "Rahul",
  "lastName": "Sharma",
  "email": "rahul.sharma@example.com",
  "phone": "9876543210",
  "department": "IT",
  "designation": "Software Engineer",
  "salary": 50000,
  "joiningDate": "2026-07-28"
}
```

---

## Example Leave Request

```http
POST /api/v1/leaves
Authorization: Bearer <access-token>
Content-Type: application/json
```

```json
{
  "leaveType": "CASUAL",
  "startDate": "2026-08-03",
  "endDate": "2026-08-05",
  "reason": "Personal work"
}
```

---

## Running the Project Locally

### Prerequisites

Install:

* Java 21
* Node.js 22
* MySQL 8
* Maven or Maven Wrapper
* Git

### Clone the Repository

```bash
git clone https://github.com/vishwajeet30/employee-management-system.git
cd employee-management-system
```

### Configure MySQL

Create the database:

```sql
CREATE DATABASE employee_management_db;
```

Update the local MySQL password in:

```text
src/main/resources/application.yml
```

### Start the Backend

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On Linux or macOS:

```bash
./mvnw spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

### Start the Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend URL:

```text
http://localhost:5173
```

Vite forwards `/api` requests to the Spring Boot backend running on port `8080`.

---

## Running with Docker

Make sure Docker Desktop is running.

From the project root:

```bash
docker compose up --build
```

Services:

| Service                     | URL or Port             |
| --------------------------- | ----------------------- |
| Spring Boot backend         | `http://localhost:8080` |
| React development frontend  | `http://localhost:5173` |
| Docker MySQL host port      | `3307`                  |
| Docker MySQL container port | `3306`                  |

Docker database credentials:

```text
Database: employee_management_db
Username: ems_user
Password: ems_password
Port: 3307
```

Stop the containers:

```bash
docker compose down
```

Delete containers and database volume:

```bash
docker compose down -v
```

The `-v` option permanently removes the Docker MySQL data.

---

## Running Tests

The test configuration uses H2, so MySQL does not need to be running.

On Windows:

```powershell
.\mvnw.cmd clean test
```

Run the complete Maven verification lifecycle:

```powershell
.\mvnw.cmd --batch-mode clean verify
```

On Linux or macOS:

```bash
./mvnw clean verify
```

Main test classes:

```text
EmployeeServiceImplTest
AuthenticationServiceImplTest
LeaveServiceImplTest
EmployeeManagementSystemApplicationTests
```

---

## CI/CD Pipeline

The GitHub Actions workflow is located at:

```text
.github/workflows/ci-cd.yml
```

The pipeline runs when code is pushed to `main` or `develop`, and when a pull request targets `main`.

### Pipeline Jobs

```text
Backend Tests
      |
      v
Frontend Build
      |
      v
Docker Build
      |
      v
GitHub Container Registry
```

The backend test job uses the H2 in-memory database, so the GitHub Actions runner does not require MySQL.

When code is pushed to `main`, the Docker image is published to:

```text
ghcr.io/<github-username>/<repository-name>:latest
```

---

## Screenshots

Add screenshots inside:

```text
docs/screenshots/
```

Recommended screenshots:

1. Login page
2. Dashboard
3. Employee list
4. Employee search
5. Leave application form
6. Leave history
7. ADMIN leave review
8. Swagger UI
9. Passing unit tests
10. Successful GitHub Actions pipeline
11. Running Docker containers

Example Markdown:

```markdown
![Login Page](docs/screenshots/login-page.png)

![Employee List](docs/screenshots/employee-list.png)

![Leave Management](docs/screenshots/leave-management.png)

![CI/CD Pipeline](docs/screenshots/github-actions.png)
```

---

## Security Notes

* Passwords are stored using BCrypt.
* Protected APIs require a valid JWT.
* The backend uses stateless authentication.
* Access tokens expire after the configured period.
* ADMIN, HR, and EMPLOYEE permissions are enforced by Spring Security.
* Production credentials and JWT secrets should be supplied through environment variables.
* Development credentials must not be used in production.

---

## Future Improvements

The current application is intentionally maintained as a focused, resume-ready MVP.

Possible future improvements include:

* Refresh tokens
* Employee create and edit forms in React
* Cloud deployment
* Password-reset flow
* Email notifications
* File upload for employee profiles
* Integration tests using Testcontainers

---

## Resume Description

**Enterprise Employee Management System**

Developed a full-stack Employee Management System using Java 21, Spring Boot, Spring Security, React, TypeScript, and MySQL. Implemented JWT authentication, BCrypt password encryption, role-based authorization for ADMIN, HR, and EMPLOYEE users, employee CRUD operations, pagination, search, leave application and approval workflows, centralized exception handling, Swagger documentation, JUnit and Mockito tests, H2-based CI testing, Docker containerization, and a GitHub Actions CI/CD pipeline.

---

## Interview Summary

> The application follows a layered architecture consisting of controllers, services, repositories, entities, DTOs, and mappers. Authentication is stateless and uses JWT tokens validated by a custom Spring Security filter. Role-based access controls employee and leave-management operations. The React frontend uses Axios interceptors to attach tokens to secured requests. Unit tests use JUnit and Mockito, while the Spring context test uses an H2 in-memory database. Docker Compose runs the backend and MySQL, and GitHub Actions tests the backend, builds the frontend, verifies the Docker image, and publishes it to GitHub Container Registry.

---

## Author

**Vishwajeet Singh**

GitHub: `https://github.com/vishwajeet30`
