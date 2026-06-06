## Why

The polished CareerLoop entry now lets users fill profile information incrementally on the home page, but the backend only exposes committed onboarding/profile operations. The next backend slice needs a durable draft boundary so the UI can save and reload partial user-profile intent without treating it as completed onboarding.

## What Changes

- Add a user-profile draft contract for saving and reading partial home/onboarding form data.
- Keep draft data separate from completed onboarding snapshot blocks until the user commits onboarding.
- Preserve current `ccRoute` web flow compatibility by exposing draft operations through the existing Career Profile API/plugin boundary.
- Persist drafts through the replaceable profile storage adapter so the current file storage and future Cosmic datamodel storage can share the same service contract.
- No breaking changes to existing snapshot, onboarding, preferences, or profile APIs.

## Capabilities

### New Capabilities
- `career-profile-draft`: Saving, reading, and clearing partial user-profile draft data before completed onboarding.

### Modified Capabilities
- `career-profile-onboarding`: The application boundary distinguishes draft profile input from completed onboarding submission.

## Impact

- `code/base/v620-cc001-base-common/`: draft request/DTO contract.
- `code/cloud01/v620-cc001-cloud01-app01/`: application service, storage boundary, file/Cosmic adapters, WebAPI/plugin routing, tests.
- `webapp/isv/v620/cyancruise/`: optional endpoint wiring can use backend drafts instead of local-only fallback.
- No new third-party dependencies.
