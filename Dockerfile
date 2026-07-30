# ============================================================
# STAGE 1: Build the React frontend
# ============================================================
FROM node:22-alpine AS frontend-build

# Directory used for the React build.
WORKDIR /frontend

# Copy dependency files first so Docker can cache npm packages.
COPY frontend/package.json frontend/package-lock.json ./

# Install exactly the versions recorded in package-lock.json.
RUN npm ci

# Copy the remaining frontend source code.
COPY frontend/ ./

# Create the optimized React production files.
RUN npm run build


# ============================================================
# STAGE 2: Build the Spring Boot backend
# ============================================================
FROM maven:3.9-eclipse-temurin-21-alpine AS backend-build

# Directory used for the Maven build.
WORKDIR /app

# Copy pom.xml first so Maven dependencies can be cached.
COPY pom.xml .

# Download dependencies before copying the Java source.
RUN mvn dependency:go-offline -B

# Copy the Spring Boot source code.
COPY src ./src

# Copy React's compiled production files into Spring Boot.
#
# Spring Boot will serve these files from:
# src/main/resources/static
COPY --from=frontend-build \
    /frontend/dist \
    ./src/main/resources/static

# Build the executable Spring Boot JAR.
#
# Tests are already run by GitHub Actions, so they are not
# repeated inside the Docker image build.
RUN mvn clean package -DskipTests -B


# ============================================================
# STAGE 3: Create the runtime image
# ============================================================
FROM eclipse-temurin:21-jre-alpine

# Application directory inside the runtime container.
WORKDIR /app

# Create a non-root user for improved security.
RUN addgroup -S spring && \
    adduser -S spring -G spring

# Copy only the final executable JAR.
COPY --from=backend-build \
    --chown=spring:spring \
    /app/target/*.jar \
    app.jar

# Run the application without root privileges.
USER spring:spring

# Spring Boot listens on this port inside the container.
EXPOSE 8080

# Limit Java to a reasonable percentage of container memory.
ENTRYPOINT [
    "java",
    "-XX:MaxRAMPercentage=75.0",
    "-jar",
    "app.jar"
]