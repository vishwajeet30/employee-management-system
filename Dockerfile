# ============================================================
# STAGE 1: Build the Spring Boot application using Maven
# ============================================================
FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Directory used inside the build container
WORKDIR /app

# Copy pom.xml first so Docker can cache downloaded dependencies
COPY pom.xml .

# Download Maven dependencies before copying source code
RUN mvn dependency:go-offline -B

# Copy the complete application source code
COPY src ./src

# Build the executable Spring Boot JAR
#
# Unit tests should already be run locally using:
# mvn test
#
# They are skipped here to prevent Docker image creation
# from depending on an external database or test environment.
RUN mvn clean package -DskipTests -B


# ============================================================
# STAGE 2: Run the application using a smaller Java runtime
# ============================================================
FROM eclipse-temurin:21-jre-alpine

# Directory used by the running application
WORKDIR /app

# Create a non-root Linux user for running the application
RUN addgroup -S spring && adduser -S spring -G spring

# Copy only the generated JAR from the build stage
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

# Run the application as a non-root user
USER spring:spring

# Document the port used by Spring Boot
EXPOSE 8080

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]