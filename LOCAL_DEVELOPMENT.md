# Local Development Guide

This guide explains how to build and run the Tofimotia application locally.

## Prerequisites

- Java 17 or higher
- Maven (or use the included Maven wrapper `./mvnw`)

## Quick Start

### Option 1: Using the convenience script
```bash
./run-local.sh
```

### Option 2: Manual commands
```bash
# Build the application
./mvnw clean package -DskipTests

# Run with local profile
java -jar target/Tofimotia-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

### Option 3: Using Maven Spring Boot plugin
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## Local Configuration

The application uses the `application-local.properties` profile for local development:

- **Database**: H2 in-memory database (no external database required)
- **Port**: 8080
- **Flyway**: Disabled (Hibernate handles schema creation)
- **JPA**: `create-drop` mode (schema recreated on each startup)

## Available Endpoints

Once the application is running, you can access:

- **Application**: http://localhost:8080
- **H2 Database Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (leave empty)
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Documentation**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/actuator/health

## Database Schema

The application automatically creates the following tables:
- `users` - User accounts (customers and vendors)
- `venues` - Venue information
- `bookings` - Booking records
- `payments` - Payment transactions
- `reviews` - User reviews

## Development Notes

- The H2 database is in-memory, so data is lost when the application stops
- Spring Boot DevTools is enabled for hot reloading during development
- All SQL queries are logged to the console for debugging
- The application uses JWT for authentication

## Troubleshooting

### Build Issues
- Ensure Java 17+ is installed: `java -version`
- Clean and rebuild: `./mvnw clean compile`

### Runtime Issues
- Check if port 8080 is available
- Verify the local profile is active in the logs
- Check application logs for any startup errors

### Database Issues
- H2 console should be accessible at http://localhost:8080/h2-console
- Use JDBC URL: `jdbc:h2:mem:testdb` with username `sa` and empty password