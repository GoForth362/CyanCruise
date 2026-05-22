## Why

The career profile/onboarding migration now has executable Java behavior, but the most important helper rules are only protected by the project build. Adding focused unit tests reduces the risk of regressions before the next slices add persistent data models and UI entry points.

## What Changes

- Add JUnit-based test coverage for snapshot merge behavior, target-role resolution, onboarding stage inference, and self-reported resume readiness.
- Add test dependencies only to the helper module that owns the pure Java rules.
- Document that the current storage adapter remains an in-memory migration placeholder until the data model persistence slice is implemented.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `career-profile-onboarding`: Adds an explicit expectation that core merge and profile derivation behavior is covered by focused tests.

## Impact

- Affects `code/base/v620-cc001-base-helper/build.gradle`.
- Adds tests under `code/base/v620-cc001-base-helper/src/test/java`.
- Does not change runtime WebAPI behavior or data model storage.

