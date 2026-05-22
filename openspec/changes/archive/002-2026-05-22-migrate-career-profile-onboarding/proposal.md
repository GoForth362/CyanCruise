## Why

CyanCruise needs the first IPD migration slice that feeds the CareerLoop core loop. Career profile and onboarding are the right starting point because today's recommendation, assessment, resume, interview, and career plan all depend on a reliable user portrait and target role.

## What Changes

- Expand CyanCruise's migrated `UserProfileSnapshot` concept to cover the IPD onboarding fields and user-supplied career preferences.
- Define a Cosmic-compatible service boundary for reading, merging, and refreshing a user's career profile without Spring/JPA/Lombok coupling.
- Define onboarding persistence semantics: partial updates merge by field, target role is stored as a preference, and self-reported resume state is not treated as proof of an uploaded resume.
- Define a profile summary that exposes target role, current stage, completeness, readiness, missing signals, and evidence for downstream recommendation logic.
- Update the migration map status for career profile/onboarding from pending to active.

## Capabilities

### New Capabilities
- `career-profile-onboarding`: Captures onboarding intake, profile snapshot merge behavior, target-role resolution, and unified profile outputs for the CyanCruise rebuild.

### Modified Capabilities
- `ipd-business-loop`: Adds career profile and onboarding as the first P0 implementation slice of the preserved loop.
- `migration-governance`: Applies source-evidence and target-ownership rules to this implementation change.
- `cosmic-rebuild-boundary`: Applies Cosmic/JDK 8 rebuild constraints to the profile/onboarding migration.

## Impact

- Affected CyanCruise code areas:
  - `code/base/v620-cc001-base-common/src/main/java/v620/cc001/base/common/dto/career`
  - `code/base/v620-cc001-base-helper/src/main/java/v620/base/helper/career`
  - `code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/mservice`
  - `code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/webapi`
  - `datamodel/` when persistent Cosmic entities are introduced
  - `webapp/` when the onboarding page is implemented
- IPD source evidence:
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\dto\UserProfileSnapshot.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\dto\ProfileInputsRequest.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\UserProfileSnapshotServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\AgentProfileServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\UserController.java`
  - `F:\Project\IPD\frontend\src\pages\onboarding\index.vue`
  - `F:\Project\IPD\frontend\src\utils\onboardingGate.ts`
  - `F:\Project\IPD\frontend\src\utils\onboardingSync.ts`
- No breaking change is intended for the existing `CareerAgentTodayApplicationService` API; it should continue to accept rule input while becoming able to consume richer profile data.

