<!-- markdownlint-disable MD041 -->
![CI/CD](https://github.com/jerosanchez/pms-patient-service/actions/workflows/ci-cd.yml/badge.svg)
![Coverage](https://img.shields.io/badge/coverage-80%25-brightgreen)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)

# README

A sample production-grade Spring Boot RESTful API.

 This project leverages modern AI-powered tools, such as GitHub Copilot, to accelerate development and improve productivity and the learning process. All AI-generated code and suggestions are carefully reviewed, validated, and adapted to ensure correctness, maintainability, and compliance with project standards. The use of such tools is guided by a commitment to responsible, ethical, and professional software engineering practices, in line with industry expectations and best practices.

---

## Purpose

The Patient Service is one of several microservices that together form the Patient Management System (PMS).

This particular microservice is responsible for managing patient records, providing a dedicated REST API for creating, reading, updating, and deleting patient data. It maintains its own persistent storage and enforces data integrity and validation (such as unique emails), serving as the core patient CRUD backend for the broader system.

---

## User Features

- **Create Patient:** Add new patients with validated data
- **List Patients:** Retrieve all patient records
- **Update Patient:** Edit existing patient details
- **Delete Patient:** Remove patient records

---

## Technical Features

- **Spring Boot 3, Java 21**
- **RESTful API** with DTOs and validation
- **PostgreSQL** (via Docker Compose)
- **Profiles:** Separate Spring property profiles
  - H2 in-memory database for testing
  - PostgreSQL for deployment
- **Makefile:**
  - Common tasks (lint, test, build, coverage, Docker stack)
- **Unit & Integration Tests:**
  - Unit tests for services, controllers, and mappers
  - Integration tests for API endpoints and database
  - Integration tests for relevant end-to-end workflows (API and database)
- **Architecture:**
  - Layered design with clear separation of concerns
  - Use of policy objects to encapsulate business rules
- **Code Coverage:**
  - Enforced via JaCoCo (≥80% by default)
  - Coverage target is configurable via environment variable (`COVERAGE_TARGET`)
- **Linting:**
  - Java: Checkstyle (Google Java Style)
  - Markdown: markdownlint-cli
- **CI/CD Pipeline:**
  - Automated build, lint, test, and coverage checks
  - Builds a Docker image as part of the pipeline
  - Docker image push stage is included but commented out for documentation purposes
- **Logging:** Key operations (create, update, delete) are logged for debugging and basic traceability
- **OpenAPI/Swagger:** API documentation

---

## File Organization

- `src/main/java/com/jerosanchez/pms_patient_service/`
  - `controller/` — REST controllers (API endpoints)
  - `service/` — Business logic
  - `repository/` — Spring Data JPA repositories
  - `model/` — Entity classes
  - `dto/` — Data Transfer Objects (DTOs)
  - `mapper/` — DTO/entity mappers
  - `policy/` — Business rules (e.g., email uniqueness)
  - `exception/` — Custom exceptions and global handler
- `src/test/java/com/jerosanchez/pms_patient_service/`
  - `controller/`, `service/`, `integration/`, etc. — Unit and integration tests
- `api-requests/` — Example HTTP requests for API testing
- `docs/` —
  - `CONTRIBUTING.md` — Contribution guidelines (setup, linting, workflow)
  - `AGENTS.md` — Test, Makefile, and .env authoring guidelines
- `Makefile` — Common development tasks
- `docker-compose.yml` — Local development stack

---

## Basic Architecture

- **Spring Boot** application with layered architecture:
  - **Controller Layer:** Handles HTTP requests and responses
  - **Service Layer:** Business logic and validation
  - **Repository Layer:** Data persistence (PostgreSQL)
  - **DTO/Mapper Layer:** Data transfer and transformation
  - **Policy Layer:** Business rules enforcement
  - **Exception Handling:** Centralized error responses
- **Testing:**
  - Unit tests for logic and validation
  - Integration tests for main end-to-end workflows (API/database)
  - Test guidelines and helpers in `docs/AGENTS.md`
- **CI/CD:**
  - Linting, testing, and coverage checks automated in pipeline

---

## Quick Start

For setup and getting started instructions, please refer to the [Prerequisites & Setup](docs/CONTRIBUTING.md#prerequisites--setup) and [Getting Started](docs/CONTRIBUTING.md#getting-started) sections in CONTRIBUTING.md.

---

## License

[MIT](LICENSE)
