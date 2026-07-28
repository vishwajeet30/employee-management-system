# Employee Management System

A full-stack application for managing employee records and leave applications. The backend exposes a secured REST API, while the React frontend provides a browser interface for authentication, employee search, and leave workflows.

## Features

- JWT-based authentication with BCrypt password hashing
- Role-based access for `ADMIN`, `HR`, and `EMPLOYEE` users
- Employee creation, retrieval, update, deletion, pagination, sorting, and search
- Leave application, personal leave history, and manager approval or rejection
- Swagger/OpenAPI documentation
- Unit and application-context tests using JUnit, Mockito, and H2
- Docker Compose setup for the API and MySQL database

## Technology

| Area | Tools |
| --- | --- |
| Backend | Java 21, Spring Boot, Spring Security, Spring Data JPA, Hibernate |
| Database | MySQL 8, H2 for tests |
| Frontend | React, TypeScript, Vite, Axios, React Router |
| API documentation | springdoc-openapi |
| Build and containers | Maven, Docker, Docker Compose |

## Roles

| Role | Capabilities |
| --- | --- |
| `ADMIN` | Full employee management and leave review |
| `HR` | Create and update employees; review leave applications |
| `EMPLOYEE` | View employees; submit and view personal leave applications |

The application initializes these roles on startup. Create user accounts through the registration endpoint or Swagger UI.

## Project Structure

```text
employee-management-system/
|-- src/main/java/com/project/ems/   # Spring Boot API
|-- src/test/                        # Backend tests
|-- frontend/                        # React/Vite application
|-- Dockerfile                       # Backend image definition
|-- docker-compose.yml               # Backend and MySQL services
|-- employee-management-system.postman_collection.json
`-- pom.xml
```

## Prerequisites

- Java 21
- Node.js 22 or later
- MySQL 8 for a local backend run, or Docker Desktop

## Run Locally

### 1. Configure MySQL

Create a local database:

```sql
CREATE DATABASE employee_management_db;
```

The default local connection uses MySQL on `localhost:3306` with username `root`. Set your password and, if needed, connection settings in `src/main/resources/application.yml`, or provide these environment variables:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/employee_management_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:SPRING_DATASOURCE_USERNAME = "root"
$env:SPRING_DATASOURCE_PASSWORD = "your-password"
```

### 2. Start the backend

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux or macOS:

```bash
./mvnw spring-boot:run
```

The API runs at `http://localhost:8080`. Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

### 3. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. During development, Vite proxies `/api` requests to the backend on port `8080`.

## Run with Docker

Docker Compose starts the backend and MySQL database:

```bash
docker compose up --build
```

| Service | Address |
| --- | --- |
| Backend API | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| MySQL (host) | `localhost:3307` |

The Compose database uses `employee_management_db`, username `ems_user`, and password `ems_password`. Stop the stack with:

```bash
docker compose down
```

Use `docker compose down -v` only when you intend to remove the persisted database volume.

## API Overview

All protected endpoints require an `Authorization: Bearer <token>` header.

| Method | Endpoint | Access |
| --- | --- | --- |
| `POST` | `/api/v1/auth/register` | Public |
| `POST` | `/api/v1/auth/login` | Public |
| `GET` | `/api/v1/employees` | Authenticated |
| `POST` | `/api/v1/employees` | ADMIN, HR |
| `PUT` | `/api/v1/employees/{id}` | ADMIN, HR |
| `DELETE` | `/api/v1/employees/{id}` | ADMIN |
| `GET` | `/api/v1/leaves/my` | Authenticated |
| `POST` | `/api/v1/leaves` | Authenticated |
| `GET` | `/api/v1/leaves` | ADMIN, HR |
| `PUT` | `/api/v1/leaves/{id}/approve` | ADMIN, HR |
| `PUT` | `/api/v1/leaves/{id}/reject` | ADMIN, HR |

For request schemas and the full API surface, use Swagger UI or import `employee-management-system.postman_collection.json` into Postman.

## Tests

Backend tests use an H2 in-memory database and do not require MySQL:

```powershell
.\mvnw.cmd clean test
```

Run the frontend checks from the `frontend` directory:

```bash
npm run lint
npm run build
```

## Security Configuration

The default JWT secret in `application.yml` is for development only. Supply a strong Base64-encoded secret through `APPLICATION_SECURITY_JWT_SECRET_KEY` before deploying the application, and do not use development database credentials in production.
