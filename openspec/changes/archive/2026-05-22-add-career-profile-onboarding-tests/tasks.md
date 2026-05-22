## 1. Test Setup

- [x] 1.1 Add JUnit Jupiter test dependency to `v620-cc001-base-helper`.
- [x] 1.2 Keep dependency scoped to tests only.

## 2. Helper Tests

- [x] 2.1 Add tests for onboarding merge preserving existing snapshot blocks.
- [x] 2.2 Add tests for blank onboarding target role not clearing existing preferences.
- [x] 2.3 Add tests for target-role priority using preferences before assessment suggestions.
- [x] 2.4 Add tests for assessment fallback when no stronger target source exists.
- [x] 2.5 Add tests for career-switch stage inference.
- [x] 2.6 Add tests proving self-reported resume readiness does not set `hasResume` to true.

## 3. Documentation

- [x] 3.1 Update migration map to note helper rules now have unit test coverage.

## 4. Validation

- [x] 4.1 Run `openspec validate add-career-profile-onboarding-tests --strict`.
- [x] 4.2 Run `openspec validate --all --strict`.
- [x] 4.3 Run JDK 8 Gradle build.
