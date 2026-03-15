package com.mahedee.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * TaskMagnet Backend Application - Main Application Entry Point
 * 
 * This is the main Spring Boot application class for the TaskMagnet project management system.
 * It serves as the entry point for the entire modular monolith architecture.
 * 
 * Architecture Overview:
 * - Modular Monolith Design: Single deployable unit with well-defined module boundaries
 * - Multi-Module Maven Structure: taskmagnet-core and taskmagnet-web modules
 * - Enterprise Security: JWT authentication with Spring Security
 * - Oracle Database: Production-grade data persistence
 * - API Documentation: Swagger/OpenAPI integration
 * 
 * Key Features:
 * - Project and task management
 * - Role-based access control (RBAC)
 * - RESTful API with comprehensive documentation
 * - Audit logging and security tracking
 * - Multi-profile configuration support
 * 
 * @author TaskMagnet Development Team
 * @version 3.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.mahedee.backend", "com.mahedee.taskmagnet.core"})
public class BackendApplication {

	/**
	 * Main method to start the TaskMagnet application
	 * 
	 * Initializes the Spring Boot application context and starts the embedded Tomcat server.
	 * The application will be available at http://localhost:8080 by default.
	 * Swagger UI documentation will be accessible at http://localhost:8080/swagger-ui/index.html
	 * 
	 * @param args Command line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
