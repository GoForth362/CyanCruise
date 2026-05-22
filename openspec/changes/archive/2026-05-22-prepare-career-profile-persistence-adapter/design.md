## Context

`CareerProfileApplicationService` currently defaults to `InMemoryCareerProfileStorage`. That was useful for the first skeleton but is too volatile for continued migration work. The repository has no committed `datamodel` examples beyond `.gitkeep`, and no existing DynamicObject usage to mirror safely.

## Approach

- Add `FileCareerProfileStorage` under `cloud01` as a non-platform durable adapter.
- Use Java object serialization to avoid new JSON dependencies in the Cosmic project.
- Store per-user files under `filestorage/career-profile` by default.
- Allow override with the system property `cc001.career.profile.storage.dir`.
- Keep this adapter behind `CareerProfileStorage` so a future Cosmic/BOS adapter can replace it cleanly.

## Non-Goals

- Do not invent unverified Cosmic datamodel files.
- Do not claim production persistence is complete.
- Do not change WebAPI routes or helper behavior.

## Verification

- `openspec validate prepare-career-profile-persistence-adapter --strict`
- `openspec validate --all --strict`
- `.\gradlew.bat clean build`

