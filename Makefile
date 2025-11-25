.PHONY: test coverage

test:
	./mvnw test

coverage:
	./mvnw clean test jacoco:report jacoco:check