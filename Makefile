.PHONY: test coverage

test:
	./mvnw test

integration-test:
	./mvnw failsafe:integration-test failsafe:verify

coverage:
	./mvnw clean test jacoco:report jacoco:check