## Context

`FileCareerProfileStorage` uses Java serialization and a configurable directory as a temporary durable adapter. This change tests that adapter directly and through `CareerProfileApplicationService`.

## Approach

- Add JUnit Jupiter to `v620-cc001-cloud01-app01` as a test-only dependency.
- Use JUnit `@TempDir` to isolate storage files.
- Verify direct storage read/write across separate `FileCareerProfileStorage` instances.
- Verify onboarding saved through one `CareerProfileApplicationService` can be loaded by a second service using the same storage directory.

## Non-Goals

- No Cosmic datamodel adapter.
- No WebAPI runtime/container test.

## Verification

- `openspec validate add-career-profile-storage-tests --strict`
- `openspec validate --all --strict`
- `.\gradlew.bat :v620-cc001-cloud01-app01:test`
- `.\gradlew.bat clean build`

