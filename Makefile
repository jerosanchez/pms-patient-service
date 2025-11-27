ifneq (,$(wildcard .env))
	include .env
	export
endif

.PHONY: lint test integration-test coverage build compose-up compose-down

# Run linters
lint:
	@echo "Running docs linter..."
	@markdownlint -c .github/.markdownlint.json "**/*.md"
	@echo "Running source code linter..."
	@./mvnw checkstyle:check

# Run unit tests
test:
	@./mvnw test

# Run integration tests
integration-test:
	@./mvnw failsafe:integration-test failsafe:verify

# Run code coverage analysis
coverage:
	@echo "Calculating code coverage..."
	@./mvnw jacoco:report > /dev/null 2>&1
	@bash utils/jacoco-coverage.sh target/site/jacoco/jacoco.csv "${COVERAGE_TARGET}"

# Build the project
build:
	@./mvnw clean package -DskipTests

# Start the docker compose stack
compose-up:
	@docker compose up -d

# Stop the docker compose stack
compose-down:
	@docker compose down
