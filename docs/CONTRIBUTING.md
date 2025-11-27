
# Contributing to Patient Service

Thank you for your interest in contributing to this project!

## Prerequisites & Setup

Before you start developing, ensure you have the following tools installed:

### 1. Docker

Used for running the database and application stack locally.

Install Docker (Debian/Ubuntu):

```sh
sudo apt-get update
sudo apt-get install docker.io
sudo systemctl enable --now docker
```

### 2. OpenJDK 21

Required to build and run the Spring Boot application.

Install OpenJDK (Debian/Ubuntu):

```sh
sudo apt-get install openjdk-21-jdk
```

### 3. Node.js, npm, and markdownlint-cli

Used for linting Markdown documentation.

Install Node.js, npm, and markdownlint-cli (Debian/Ubuntu):

```sh
sudo apt-get install nodejs npm
sudo npm install -g markdownlint-cli
```

### 4. Maven Wrapper

The project uses the Maven Wrapper (`./mvnw`). No extra installation is needed, but you can install Maven globally if you prefer.

---

## Getting Started

1. Clone the repo and set up your `.env` file (see `.env.example`).

2. Run sanity checks to verify code quality and test coverage:

    ```sh
    make lint test integration-test coverage
    ```

3. Start the stack:

    ```sh
    make compose-up
    ```

4. Access API docs at: [http://localhost:4000/swagger-ui.html](http://localhost:4000/swagger-ui.html)

---

## Source Code Linting

We use automated tools to ensure code quality and consistency:

- **Java code** is linted using [Checkstyle](https://checkstyle.org/) with the official Google Java Style configuration. The configuration file is located at `.github/google_checks.xml`.
  - Exceptions to specific Java linting rules (such as `JavadocVariable`) can be configured in the `.github/checkstyle-suppressions.xml` file. Add or adjust suppressions in this file to customize which rules are ignored during linting, while keeping the main style configuration intact.
  - The Java linter is configured to only fail the build on main issues (errors). It is acceptable to have warnings, such as indentation or import order, as these do not fail the build or block contributions.

- **Markdown files** are linted using [`markdownlint-cli`](https://github.com/DavidAnson/markdownlint-cli).
  - Exceptions to specific linting rules (such as line length, MD013) can be configured in the `.github/.markdownlint.json` file. Add or adjust rules in this file to customize linting behavior as needed.

To lint the entire codebase, simply run:

```sh
make lint
```

This will check both Markdown and Java source files for style issues.

---

## Development Workflow

The project provides a `Makefile` to simplify common development tasks:

- `make lint` — Lint Markdown files and Java source code
- `make test` — Run all unit tests
- `make integration-test` — Run all integration tests
- `make coverage` — Generate code coverage report
- `make build` — Build the project JAR without running tests
- `make compose-up` — Start the full Docker stack (database and app)
- `make compose-down` — Stop the Docker stack

You can also run Maven commands directly using `./mvnw`.
