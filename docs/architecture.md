# Employee Management System — Architecture

​```mermaid
flowchart TD

subgraph group_frontend["React SPA"]
  node_spa_entry["SPA entry &amp; routes<br/>React/Vite<br/>[main.tsx]"]
  node_protected_route["Protected navigation<br/>route guard<br/>[ProtectedRoute.tsx]"]
  node_frontend_api["API client<br/>HTTP services<br/>[api.ts]"]
  node_token_storage["Token storage<br/>client auth state<br/>[authStorage.ts]"]
end

subgraph group_backend["Spring API"]
  node_boot_app["Spring Boot application<br/>Java 21 runtime"]
  node_spa_fallback["SPA fallback<br/>Spring controller<br/>[SpaController.java]"]
  node_employee_api["Employee API<br/>REST controller"]
  node_employee_service["Employee rules"]
  node_leave_api["Leave API<br/>REST controller"]
  node_leave_service["Leave workflow"]
  node_api_errors["API errors<br/>exception handler"]
end

subgraph group_security["Security"]
  node_auth_api["Authentication API<br/>REST controller"]
  node_auth_service["JWT authentication<br/>auth service"]
  node_jwt_filter["JWT request filter<br/>security filter"]
  node_security_policy["Authorization policy<br/>Spring Security config"]
end

subgraph group_data["Persistence"]
  node_employee_store[("Employee persistence<br/>JPA repository + model")]
  node_leave_store[("Leave persistence<br/>JPA repository + model")]
  node_mysql[("MySQL<br/>database<br/>[application.yml]")]
end

subgraph group_delivery["Runtime &amp; delivery"]
  node_container_stack["Container stack<br/>Docker Compose<br/>[docker-compose.yml]"]
  node_ci_cd["CI/CD pipeline<br/>GitHub Actions<br/>[ci-cd.yml]"]
end

node_spa_entry -->|"guards routes"| node_protected_route
node_spa_entry -->|"uses"| node_frontend_api
node_protected_route
