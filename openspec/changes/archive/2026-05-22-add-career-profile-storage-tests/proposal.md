## Why

The career profile storage adapter was made durable with file-backed storage, but that persistence behavior should be verified by tests. This protects the migration from accidentally regressing back to volatile behavior while the final Cosmic datamodel adapter is still pending.

## What Changes

- Add JUnit coverage for `FileCareerProfileStorage`.
- Add application-service coverage proving onboarding data can be read by a fresh service instance using the same storage directory.
- Keep runtime behavior unchanged.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `career-profile-onboarding`: Adds focused test coverage for durable adapter behavior.

## Impact

- Adds test dependency to `v620-cc001-cloud01-app01`.
- Adds tests under `code/cloud01/v620-cc001-cloud01-app01/src/test/java`.
- Does not change production WebAPI or helper logic.

