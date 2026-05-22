## Why

The current career profile application service defaults to in-memory storage, so profile data disappears when the JVM restarts. CyanCruise does not yet contain a committed Cosmic datamodel definition, so the next safe migration step is to add a durable, replaceable adapter while keeping the Cosmic persistence boundary explicit.

## What Changes

- Add a file-backed `CareerProfileStorage` implementation for development and integration validation.
- Make profile DTOs serializable so storage adapters can persist snapshots, facts, and derived profiles without external JSON dependencies.
- Switch the default application service storage from in-memory to file-backed storage.
- Keep `InMemoryCareerProfileStorage` available for tests and temporary callers.
- Document that this is a durable adapter, not the final Cosmic datamodel binding.

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `career-profile-onboarding`: Adds a durable storage adapter requirement and clarifies that final production persistence still requires Cosmic datamodel binding.

## Impact

- Affects career DTOs in `base-common`.
- Adds a storage adapter in `code/cloud01/v620-cc001-cloud01-app01/.../mservice`.
- Uses local `filestorage/career-profile` by default, or `cc001.career.profile.storage.dir` when configured.
- Does not add third-party dependencies or create Cosmic datamodel files.

