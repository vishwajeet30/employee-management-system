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
node_protected_route -->|"checks token"| node_token_storage
node_frontend_api -->|"reads Bearer token"| node_token_storage
node_frontend_api -->|"auth requests"| node_auth_api
node_frontend_api -->|"employee requests"| node_employee_api
node_frontend_api -->|"leave requests"| node_leave_api
node_token_storage -->|"stores auth token"| node_frontend_api
node_boot_app -->|"serves SPA fallback"| node_spa_fallback
node_auth_api -->|"delegates"| node_auth_service
node_auth_service -->|"loads users and roles"| node_mysql
node_jwt_filter -->|"applies policy"| node_security_policy
node_employee_api -->|"protected request"| node_jwt_filter
node_leave_api -->|"protected request"| node_jwt_filter
node_employee_api -->|"delegates"| node_employee_service
node_employee_service -->|"persists via"| node_employee_store
node_employee_store -->|"JPA/Hibernate"| node_mysql
node_leave_api -->|"delegates"| node_leave_service
node_leave_service -->|"persists via"| node_leave_store
node_leave_store -->|"JPA/Hibernate"| node_mysql
node_employee_api -.->|"translated errors"| node_api_errors
node_leave_api -.->|"translated errors"| node_api_errors
node_container_stack -->|"runs application"| node_boot_app
node_container_stack -->|"provisions"| node_mysql
node_ci_cd -->|"builds image stack"| node_container_stack

click node_spa_entry "https://github.com/vishwajeet30/employee-management-system/blob/main/frontend/src/main.tsx"
click node_protected_route "https://github.com/vishwajeet30/employee-management-system/blob/main/frontend/src/components/ProtectedRoute.tsx"
click node_frontend_api "https://github.com/vishwajeet30/employee-management-system/blob/main/frontend/src/api.ts"
click node_token_storage "https://github.com/vishwajeet30/employee-management-system/blob/main/frontend/src/authStorage.ts"
click node_boot_app "https://github.com/vishwajeet30/employee-management-system/blob/main/src/main/java/com/project/ems/EmployeeManagementSystemApplication.java"
click node_spa_fallback "https://github.com/vishwajeet30/employee-management-system/blob/main/src/main/java/com/project/ems/controller/SpaController.java"
click node_employee_api "https://github.com/vishwajeet30/employee-management-system/blob/main/src/main/java/com/project/ems/controller/EmployeeController.java"
click node_employee_service "https://github.com/vishwajeet30/employee-management-system/blob/main/src/main/java/com/project/ems/service/impl/EmployeeServiceImpl.java"
click node_leave_api "https://github.com/vishwajeet30/employee-management-system/blob/main/src/main/java/com/project/ems/controller/LeaveController.java"
click node_leave_service "https://github.com/vishwajeet30/employee-management-system/blob/main/src/main/java/com/project/ems/service/impl/LeaveServiceImpl.java"
click node_api_errors "https://github.com/vishwajeet30/employee-management-system/blob/main/src/main/java/com/project/ems/exception/GlobalExceptionHandler.java"
click node_auth_api "https://github.com/vishwajeet30/employee-management-system/blob/main/src/main/java/com/project/ems/security/controller/AuthenticationController.java"
click node_auth_service "https://github.com/vishwajeet30/employee-management-system/blob/main/src/main/java/com/project/ems/security/service/impl/AuthenticationServiceImpl.java"
click node_jwt_filter "https://github.com/vishwajeet30/employee-management-system/blob/main/src/main/java/com/project/ems/security/jwt/JwtAuthenticationFilter.java"
click node_security_policy "https://github.com/vishwajeet30/employee-management-system/blob/main/src/main/java/com/project/ems/security/config/SecurityConfig.java"
click node_employee_store "https://github.com/vishwajeet30/employee-management-system/blob/main/src/main/java/com/project/ems/repository/EmployeeRepository.java"
click node_leave_store "https://github.com/vishwajeet30/employee-management-system/blob/main/src/main/java/com/project/ems/repository/LeaveRepository.java"
click node_mysql "https://github.com/vishwajeet30/employee-management-system/blob/main/src/main/resources/application.yml"
click node_container_stack "https://github.com/vishwajeet30/employee-management-system/blob/main/docker-compose.yml"
click node_ci_cd "https://github.com/vishwajeet30/employee-management-system/blob/main/.github/workflows/ci-cd.yml"

classDef toneNeutral fill:#f8fafc,stroke:#334155,stroke-width:1.5px,color:#0f172a
classDef toneBlue fill:#dbeafe,stroke:#2563eb,stroke-width:1.5px,color:#172554
classDef toneAmber fill:#fef3c7,stroke:#d97706,stroke-width:1.5px,color:#78350f
classDef toneMint fill:#dcfce7,stroke:#16a34a,stroke-width:1.5px,color:#14532d
classDef toneRose fill:#ffe4e6,stroke:#e11d48,stroke-width:1.5px,color:#881337
classDef toneIndigo fill:#e0e7ff,stroke:#4f46e5,stroke-width:1.5px,color:#312e81
classDef toneTeal fill:#ccfbf1,stroke:#0f766e,stroke-width:1.5px,color:#134e4a
class node_spa_entry,node_protected_route,node_frontend_api,node_token_storage toneBlue
class node_boot_app,node_spa_fallback,node_employee_api,node_employee_service,node_leave_api,node_leave_service,node_api_errors toneAmber
class node_auth_api,node_auth_service,node_jwt_filter,node_security_policy toneMint
class node_employee_store,node_leave_store,node_mysql toneRose
class node_container_stack,node_ci_cd toneIndigo
