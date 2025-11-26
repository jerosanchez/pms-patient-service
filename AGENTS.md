# AGENTS.md

## Test Creation Guidelines

* Use `xxxTestFactory` or similar helpers to generate model objects with random but valid data. Avoid hardcoding values in tests; use factories for maintainability and randomness.
* Prefer explicit type declarations (e.g., `Xxx model = ...;`) over `var` in tests for clarity and maintainability, especially when the type is not immediately obvious.
* When converting between DTOs and models in tests, use the appropriate mapper methods (e.g., `XxxMapper.toDTO`, `XxxMapper.toModel`) instead of duplicating mapping logic in tests.
* Extract repeated setup code (such as converting a model to a DTO) into private helper methods within the test class.
* Use Mockito to mock dependencies (e.g., repositories, policies). Set up mocks to return expected values or throw exceptions as needed for each test scenario.
* For service methods, write tests for both successful execution and expected exceptions (e.g., uniqueness violations).
* Use assertion helpers (e.g., `XxxAssertions.assertEqual`) or compare full objects/DTOs for correctness, not just individual fields.
* Group related tests together (e.g., all `createXxx` tests after all `getXxx` tests).
* Always add tests for null input and edge cases, asserting that the correct exception is thrown.
* Do not use full qualified package names in code, use the corresponding import instead.
* Always run the full test suite after creating new tests or changing/refactoring existing ones to ensure correctness and prevent regressions.
* Add new tests below the existing ones.
* In the assert section of each test, separate `assert` instructions (first) from `verify` instructions (last) with a blank line.
* In each test method, clearly separate the Arrange, Act, and Assert sections using comments; keep verification instructions as part of the Assert section.
* Use `@SuppressWarnings("null")` only to avoid Mockito's null type safety warnings.
