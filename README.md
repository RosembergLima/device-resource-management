# Device Resources API

A robust Spring Boot REST API for managing device resources. This application demonstrates a clean architecture, comprehensive testing, and automated code quality analysis.

## 🚀 Features

- **Full CRUD Operations**: Manage devices with specialized endpoints.
- **State Management**:
  - Valid states: `AVAILABLE`, `IN_USE`, `INACTIVE`.
  - **Soft-delete**: `DELETE` operations transition devices to the `INACTIVE` state rather than physical removal.
  - **Business Rules**:
    - `creation_time` is immutable and server-generated.
    - Protected `name` and `brand` when a device is `IN_USE`.
    - Default state is `AVAILABLE` if not provided during creation.
- **Advanced Filtering**: Case-insensitive search by `brand` and filtering by `state`.
- **Pagination & Sorting**: Built-in support using Spring Data Pageable.
- **Partial Updates**: `PATCH` endpoint allowing updates to only specific fields.
- **API Documentation**: Interactive Swagger UI and OpenAPI 3.0 specs.
- **Database Migrations**: Versioned schema changes using Flyway.
- **Containerization**: Fully dockerized environment including PostgreSQL and SonarQube.

## 🛠 Tech Stack

- **Java 21**: Leveraging the latest LTS features.
- **Spring Boot 4.0.2**: Core framework for web, JPA, and validation.
- **PostgreSQL 15.4**: Production-grade relational database.
- **H2 Database**: Fast, in-memory database for unit and integration testing.
- **MapStruct 1.6.3**: Type-safe bean mapping between Entities and DTOs.
- **Springdoc OpenAPI 2.8.13**: Automatic generation of Swagger UI.
- **Flyway**: Database schema migration tool.
- **Lombok**: Reducing boilerplate code.
- **Gradle**: Build automation tool.

## 🏗 Architecture & Design

The project follows a **Layered Architecture** to ensure separation of concerns:
- **Controller Layer**: Handles HTTP requests and maps them to DTOs.
- **Service Layer**: Contains business logic and manages transaction boundaries (`@Transactional`).
- **Repository Layer**: Interfaces with the database using Spring Data JPA and Specifications for dynamic queries.

**Key Patterns:**
- **DTO Pattern**: Decoupling the API contract from the internal database model.
- **Specification Pattern**: Used for flexible, type-safe filtering of devices.
- **Global Exception Handling**: Centralized error management returning consistent RFC-compliant JSON responses.

## 🚦 Getting Started

### Prerequisites
- **Docker Desktop** (or Docker Engine + Compose)
- **Java 21** (if running locally without Docker)

### 🐳 Run with Docker Compose (Recommended)

The easiest way to start the entire stack (API, PostgreSQL, SonarQube) is using Docker Compose:

1.  **Navigate to the local folder:**
    ```bash
    cd local
    ```
2.  **Start the services:**
    ```bash
    docker-compose up -d
    ```
3.  **Access the Application:**
    - API: `http://localhost:8080/devices`
    - Swagger UI: `http://localhost:8080/swagger-ui.html`
    - SonarQube: `http://localhost:9000`

---

## 📊 Code Quality & SonarQube

Code quality is monitored using **SonarQube** integrated with **JaCoCo** for test coverage reports.

### Step-by-Step: Running SonarQube Analysis

Follow these steps to analyze the code quality and coverage:

1.  **Start the SonarQube Infrastructure:**
    Ensure the SonarQube container is running (it is included in the `docker-compose.yml` mentioned above).
    ```bash
    cd local
    docker-compose up -d sonarqube
    ```
    *Wait a minute for SonarQube to fully initialize (check logs with `docker logs -f sonarqube`).*

2.  **Login to SonarQube (Initial Setup):**
    - Go to `http://localhost:9000`
    - Login with **Username:** `admin` / **Password:** `admin`
    - You will be prompted to change the password.

3.  **Generate Test Coverage Report (JaCoCo):**
    Before sending data to Sonar, we need to generate the JaCoCo XML report by running tests:
    ```bash
    ./gradlew test jacocoTestReport
    ```
    *The report will be generated at `build/reports/jacoco/test/jacocoTestReport.xml`.*

4.  **Run Sonar Analysis:**
    Execute the Sonar scan. You can use the local configuration or pass the token explicitly.

    **Option A: Using the default command (requires local setup):**
    ```bash
    ./gradlew sonar
    ```

    **Option B: Full command with Token (Recommended for CI/CD or first run):**
    ```bash
    ./gradlew clean test jacocoTestReport sonar \
      -Dsonar.token=YOUR_TOKEN_HERE \
      -Dsonar.host.url=http://localhost:9000
    ```

5.  **How to get a Token in SonarQube:**
    If you haven't generated a token yet:
    1.  Log in to `http://localhost:9000`.
    2.  Click on your **User Icon** (top right) > **My Account**.
    3.  Go to the **Security** tab.
    4.  In the **Tokens** section, enter a name (e.g., `local-dev`) and click **Generate**.
    5.  **Copy the token immediately**, as you won't be able to see it again.

6.  **View Results:**
    Refresh `http://localhost:9000`. You will see the **"Devices API Challenge"** project with metrics for:
    - **Bugs, Vulnerabilities, and Hotspots**.
    - **Code Smells and Technical Debt**.
    - **Code Coverage %** (calculated by JaCoCo).
    - **Duplications**.

---

## 📖 API Documentation

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

### Endpoints Summary

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/devices` | Create a new device |
| `GET` | `/devices` | List all devices (paginated, filters: `brand`, `state`) |
| `GET` | `/devices/{id}` | Get device by ID |
| `PATCH` | `/devices/{id}` | Partially update a device |
| `DELETE` | `/devices/{id}` | Soft-delete a device (sets state to `INACTIVE`) |

---

## 🧪 Testing

The project includes a comprehensive test suite:
- **Unit Tests**: Testing service logic in isolation (`DeviceServiceTest`).
- **Integration Tests**: End-to-end API testing using H2/Testcontainers (`DeviceControllerIT`).

Run all tests:
```bash
./gradlew test
```

## 🗄 Database Migrations

Database schema is managed by **Flyway**.
- Migration scripts are located in: `src/main/resources/db/migration`
- They run automatically on application startup.

## 🛠 Development Commands

- **Build project:** `./gradlew build`
- **Run locally (using local DB):** `./gradlew bootRun`
- **Clean build:** `./gradlew clean build`