## Context

`migrate-career-profile-onboarding` introduced pure Java helper behavior in `base-helper`, but the project previously had no active test dependency. This change adds narrowly scoped tests around the pure helper layer without changing runtime behavior.

## Approach

- Add JUnit Jupiter to `code/base/v620-cc001-base-helper/build.gradle`.
- Test `CareerProfileSnapshotMergeService` directly for merge semantics.
- Test `CareerProfileBuildService` directly for target-role priority, assessment fallback, stage inference, readiness, and missing-signal behavior.
- Keep tests independent of Cosmic runtime classes and WebAPI annotations.

## Non-Goals

- No persistent Cosmic data model is introduced.
- No webapp onboarding page is introduced.
- No test coverage is added for WebAPI wiring because the current WebAPI depends on platform runtime annotations.

## Verification

- `openspec validate add-career-profile-onboarding-tests --strict`
- `openspec validate --all --strict`
- `.\gradlew.bat clean build`

