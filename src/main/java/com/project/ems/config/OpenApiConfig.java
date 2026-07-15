package com.project.ems.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class responsible for customizing
 * the OpenAPI (Swagger) documentation.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates and customizes the OpenAPI metadata
     * displayed on the Swagger UI page.
     */
    @Bean
    public OpenAPI employeeManagementOpenAPI() {

        return new OpenAPI()

                .info(new Info()
                        // API title
                        .title("Enterprise Employee Management System API")

                        // Current API version
                        .version("v1.0")

                        // Description shown on Swagger home page
                        .description("""
                                REST APIs for Enterprise Employee Management System (EEMS).
                                
                                This project demonstrates enterprise-level Spring Boot
                                development practices including:
                                
                                • Layered Architecture
                                • DTO Pattern
                                • Global Exception Handling
                                • Validation
                                • Pagination & Sorting
                                • JWT Authentication (Upcoming)
                                • Docker (Upcoming)
                                • Redis (Upcoming)
                                """)

                        // Project owner information
                        .contact(new Contact()
                                .name("Vishwajeet Singh")
                                .email("your-email@example.com")
                                .url("https://github.com/vishwajeet30"))

                        // License information
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))

                // Optional external documentation link
                .externalDocs(new ExternalDocumentation()
                        .description("Project Documentation")
                        .url("https://github.com/vishwajeet30"));
    }
}