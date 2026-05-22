## 1. Source Review

- [x] 1.1 Review IPD `UserProfileSnapshot` DTO.
- [x] 1.2 Review IPD `ProfileInputsRequest` DTO.
- [x] 1.3 Review IPD snapshot merge service.
- [x] 1.4 Review IPD unified agent profile service.
- [x] 1.5 Review IPD onboarding controller and frontend flow.
- [x] 1.6 Review CyanCruise existing career DTOs, helper, application service, and WebAPI.

## 2. Shared DTOs

- [x] 2.1 Extend CyanCruise `UserProfileSnapshot.OnboardingBlock` with stage, pain point, resume status, timeline, education, weekly availability, priority help, and recommended entry.
- [x] 2.2 Add JDK 8-compatible `EducationBlock` to `UserProfileSnapshot`.
- [x] 2.3 Add `CareerProfileInputsRequest` for optional user-supplied career facts.
- [x] 2.4 Add request DTOs for preferences and onboarding merge.
- [x] 2.5 Add unified profile DTO with target, readiness, behavior, missing signals, evidence, completeness, and current stage.

## 3. Helper Logic

- [x] 3.1 Add field-by-field snapshot merge helper for preferences and onboarding.
- [x] 3.2 Add target-role resolution helper with source priority and confidence.
- [x] 3.3 Add readiness/completeness/stage helper using currently available snapshot inputs.
- [x] 3.4 Add unit tests or focused Java test coverage for merge, target resolution, and stage inference where the local test setup allows. Current template has no active JUnit dependency, so this slice is verified by focused helper separation and Gradle compile/build.

## 4. Application And Storage Boundary

- [x] 4.1 Add career profile storage interface for loading and saving user snapshots/profile facts.
- [x] 4.2 Add default unavailable or in-memory-safe adapter if Cosmic persistence is not ready yet.
- [x] 4.3 Add `CareerProfileApplicationService` for snapshot read, preferences merge, onboarding merge, profile input save, profile refresh, and profile get.
- [x] 4.4 Ensure onboarding target role merges to preferences and does not clear existing non-empty target role with blank input.

## 5. WebAPI Boundary

- [x] 5.1 Add profile snapshot get endpoint following existing `CareerAgentWebApi` annotation style.
- [x] 5.2 Add preferences save endpoint.
- [x] 5.3 Add onboarding save endpoint.
- [x] 5.4 Add profile inputs save endpoint.
- [x] 5.5 Add unified profile get/refresh endpoints.

## 6. Migration Documents

- [x] 6.1 Update `docs/ipd-to-cyancruise-migration-map.md` status for user profile/onboarding to active while implementing.
- [x] 6.2 Update the migration map with implemented target modules after completion.

## 7. Validation

- [x] 7.1 Run `openspec validate migrate-career-profile-onboarding --strict`.
- [x] 7.2 Run `openspec validate --all --strict`.
- [x] 7.3 Run JDK 8 Gradle build after implementation or document blocker.
