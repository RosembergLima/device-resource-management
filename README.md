# Device Resources API

A Spring Boot REST API to manage device resources in an organization. It supports creation, retrieval, partial update (PATCH), and soft-delete operations, with filtering and pagination. The project follows layered architecture (Controller → Service → Repository) and leverages MapStruct for mapping and the Specification pattern for dynamic queries.

## Table of Contents
- Features
- Tech Stack
- Architecture & Design
- API Documentation
- Getting Started
  - Prerequisites
  - Run with PostgreSQL
  - Run with Docker
  - Profiles & Configuration
- Database Migrations (Flyway)
- Testing
- Error Handling
- Project Structure
- Coding Conventions

## Features
- CRUD for devices with soft delete (state transition to INACTIVE)
- Filtering by brand and state via query parameters
- Pagination and sorting with Spring Data Pageable
- OpenAPI/Swagger documentation via springdoc
- Database migrations managed by Flyway

## Tech Stack
- Java 21
- Spring Boot
  - spring-boot-starter-web
  - spring-boot-starter-data-jpa
  - spring-boot-starter-validation
  - spring-boot-starter-flyway
- PostgreSQL (production/runtime)
- H2 (tests)
- MapStruct (DTO/entity mapping)
- Springdoc OpenAPI (Swagger UI)
- Gradle

## Architecture & Design
- Controller-Service-Repository layering with DTOs
- MapStruct for mapping Device ↔ DTOs
- Specification pattern for dynamic filtering (brand, state)
- Transaction boundaries at the service layer
  - @Transactional(readOnly = true) at class level
  - Write operations (create, update, delete) marked @Transactional
- Soft delete implemented as a state transition to INACTIVE (no physical deletion)

## API Documentation
Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/api-docs

### Resource
Base path: /devices

### Data Model
DeviceResponse
- id: Long
- name: String
- brand: String
- state: DeviceStateEnum (AVAILABLE, IN_USE, INACTIVE)
- creationTime: Instant (server-generated)

DeviceRequest (POST)
- name: String (required)
- brand: String (required)

DevicePatchRequest (PATCH)
- name: String (optional)
- brand: String (optional)
- state: DeviceStateEnum (optional)

### Endpoints
1) POST /devices
- Description: Create a new device
- Status: 201 Created
- Body: DeviceRequest
- Response: DeviceResponse

curl -X POST http://localhost:8080/devices \
  -H 'Content-Type: application/json' \
  -d '{"name":"iPhone 15","brand":"Apple"}'

2) GET /devices
- Description: List devices with optional filtering and pagination
- Status: 200 OK
- Query params:
  - brand (optional, case-insensitive)
  - state (optional: AVAILABLE|IN_USE|INACTIVE)
  - pageable params (page, size, sort)
- Response: Page<DeviceResponse>

curl 'http://localhost:8080/devices?brand=Apple&state=AVAILABLE&page=0&size=20&sort=name,asc'

3) GET /devices/{id}
- Description: Get a device by id
- Status: 200 OK (404 if not found)
- Response: DeviceResponse

curl http://localhost:8080/devices/1

4) PATCH /devices/{id}
- Description: Partially update a device (only non-null fields are applied)
- Status: 200 OK (400/404/409)
- Body: DevicePatchRequest
- Response: DeviceResponse

curl -X PATCH http://localhost:8080/devices/1 \
  -H 'Content-Type: application/json' \
  -d '{"name":"iPhone 15 Pro"}'

5) DELETE /devices/{id}
- Description: Soft delete (sets state to INACTIVE)
- Status: 204 No Content (404/409)

curl -X DELETE http://localhost:8080/devices/1

## Getting Started

### Prerequisites
- Java 21
- Gradle (wrapper included)
- PostgreSQL running locally

### Run with PostgreSQL
1. Ensure a database exists (matches application.yml):
   - URL: jdbc:postgresql://localhost:5432/device_management
   - User: postgres
   - Password: postgres
2. Start the app:

./gradlew bootRun

The application will apply Flyway migrations at startup and listen on port 8080.

### Run with Docker
- A Dockerfile and local/docker-compose.yml are provided. Example flow:

cd local
docker compose up -d

Then build and run the app container or run the app locally pointing to the compose PostgreSQL service.

### Profiles & Configuration
- Main configuration: src/main/resources/application.yml
- Test profile uses H2: src/test/resources/application-test.yml
- Springdoc paths configured:
  - Swagger UI: /swagger-ui.html
  - OpenAPI: /api-docs

## Database Migrations (Flyway)
- SQL migrations live under src/main/resources/db/migration
- At startup Flyway runs pending migrations before starting the application

## Testing
- Run unit/integration tests:

./gradlew test

- The test profile uses an in-memory H2 database and JPA/Hibernate

## Error Handling
- StandardError (for generic/business errors):
  - timestamp, status, message, path
- ValidationError (for @Valid failures):
  - timestamp, status, error, path, fieldErrors[] { field, message }
- Typical status codes:
  - 201 Created (POST), 200 OK (GET/PATCH), 204 No Content (DELETE)
  - 400 Bad Request (validation or business rule violations)
  - 404 Not Found (resource not found)
  - 409 Conflict (operation blocked by domain rules)

## Project Structure
- src/main/java/com/device
  - controller: REST endpoints (DeviceController)
  - service: business logic (DeviceService)
  - repository: Spring Data JPA (DeviceRepository)
  - specification: JPA Specifications (DeviceSpecifications)
  - mapper: MapStruct mapper (DeviceMapper)
  - dto: request/response records
  - model: JPA entity (Device)
  - config: OpenAPI configuration
  - service/exception: error payloads and handlers
- src/main/resources
  - application.yml, Flyway migrations
- src/test: tests and test profile config

## Coding Conventions
- RESTful resource naming and status codes
- Query parameters for filtering collection resources
- Pageable for lists to prevent unbounded queries
- Constructor injection via Lombok @RequiredArgsConstructor
- Entity with @Getter/@Setter instead of @Data, and controlled equals/hashCode
- Service methods wrapped in @Transactional where appropriate
- MapStruct for mapping; ignore nulls on PATCH

---
If you have any questions or need additional examples (Postman collection, OpenAPI export), feel free to ask!