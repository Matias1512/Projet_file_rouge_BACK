# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SchoolDev is a Spring Boot-based learning management system (LMS) that provides programming courses, exercises, and a badge system for learners. The application is containerized and configured for production deployment with Traefik reverse proxy and PostgreSQL database.

## Development Commands

### Maven Commands
- **Build project**: `./mvnw clean compile` (Windows: `mvnw.cmd clean compile`)
- **Run tests**: `./mvnw test`
- **Package application**: `./mvnw clean package`
- **Run application locally**: `./mvnw spring-boot:run`
- **Generate test coverage report**: `./mvnw jacoco:report` (report generated in `target/site/jacoco/`)

### Docker Commands

#### Production Deployment
- **Build image**: `docker build -t schooldev_back .`
- **Run with docker-compose**: `docker-compose up -d`
- **View logs**: `docker-compose logs -f spring-app`

#### Local Development with Docker
- **Run local development environment**: `docker-compose -f docker-compose.local.yml up -d`
- **Build and run with auto-reload**: `docker-compose -f docker-compose.local.yml up --build`
- **View local logs**: `docker-compose -f docker-compose.local.yml logs -f spring-app`
- **Stop local environment**: `docker-compose -f docker-compose.local.yml down`

The local development setup includes:
- PostgreSQL database with simplified credentials
- Hot reload capability for code changes
- Debug port exposed on 5005 for remote debugging
- Maven dependencies cached for faster rebuilds

### Database Setup
The application uses PostgreSQL.

#### For Local Development (Docker):
- Use `docker-compose.local.yml` which includes PostgreSQL with pre-configured credentials
- Database: `schoolDevDatabase`, User: `postgres`, Password: `localpassword`
- Database URL: `jdbc:postgresql://postgres:5432/schoolDevDatabase` (within Docker network)

#### For Local Development (Native):
- Set environment variables: `DB_USER`, `DB_PASSWORD`, `JWT_SECRET_KEY`
- Database URL: `jdbc:postgresql://localhost:5432/schoolDevDatabase`
- The application uses Hibernate with `ddl-auto=update` for schema management

## Architecture Overview

### Package Structure
- **config/**: Security configuration, JWT utilities, rate limiting, CORS, Swagger
- **controller/**: REST API endpoints organized by domain (Auth, Course, Exercise, etc.)
- **model/**: JPA entities representing the domain model
- **repository/**: Spring Data JPA repositories
- **service/**: Business logic layer
- **dto/**: Data transfer objects for API requests/responses
- **filter/**: Custom filters (JWT authentication, rate limiting)
- **exception/**: Global exception handling
- **dataInitializer/**: Application startup data initialization

### Core Domain Models
- **User**: Authentication and user management with role-based access
- **Course**: Programming courses with difficulty levels (BEGINNER, INTERMEDIATE, ADVANCED)
- **Lesson**: Course content organized in lessons
- **Exercise**: Programming exercises with starter code and test cases
- **Badge**: Achievement system with JSON-based badge definitions
- **Progress/Submission**: User progress tracking and exercise submissions

### Security Architecture
- JWT-based authentication with custom `JwtFilter`
- BCrypt password encoding
- Rate limiting using Bucket4j
- CORS configuration for frontend integration
- Security headers (HSTS, Content-Type Options, Frame Options, Referrer Policy)

### Key Features
- RESTful API with OpenAPI/Swagger documentation
- Badge system that automatically assigns all badges to new users
- Progress tracking for courses and exercises
- Comprehensive test coverage with JaCoCo reporting
- Production-ready containerization with Traefik reverse proxy

## Testing

The project has comprehensive test coverage across all layers:
- **Unit tests** for services, controllers, models, and configuration
- **Integration tests** for security configuration
- Run tests with: `./mvnw test`
- Generate coverage report with: `./mvnw jacoco:report`

## Configuration Notes

- **JWT Configuration**: Configured via environment variables `JWT_SECRET_KEY` and `JWT_EXPIRATION`
- **Database**: PostgreSQL with environment-based credentials
- **Swagger UI**: Available at `/swagger-ui/index.html` when running
- **API Documentation**: Available at `/v3/api-docs`

## Production Deployment

The application is configured for production deployment using:
- Docker containerization with Java 23 on Alpine Linux
- Traefik reverse proxy with automatic HTTPS (Let's Encrypt)
- PostgreSQL database with persistent volumes
- Environment-based configuration for secrets

Working directory for the main application is `SchoolDev/` subdirectory.