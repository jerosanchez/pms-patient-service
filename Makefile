ifneq (,$(wildcard .env))
	include .env
	export
endif

.PHONY: test integration-test coverage lint compose-up compose-down

# Run all unit tests
test:
	@./mvnw test

# Run all integration tests
integration-test:
	@./mvnw failsafe:integration-test failsafe:verify

# Run code coverage analysis
coverage:
	@echo "Calculating code coverage..."
	@./mvnw jacoco:report > /dev/null 2>&1
	@bash utils/jacoco-coverage.sh target/site/jacoco/jacoco.csv "${COVERAGE_TARGET}"

# Run all linters
lint:
	@echo "Running docs linter..."
	@markdownlint "**/*.md"
	@echo "Running source code linter..."
	@./mvnw checkstyle:check

# Start the docker compose stack
compose-up:
	@docker compose up -d

# Stop the docker compose stack
compose-down:
	@docker compose down
