.PHONY: test integration-test coverage

test:
	./mvnw test

integration-test:
	./mvnw failsafe:integration-test failsafe:verify

coverage:
	@echo "Calculating code coverage..."
	@./mvnw test jacoco:report > /dev/null 2>&1
	@bash utils/jacoco-coverage.sh target/site/jacoco/jacoco.csv 80
