## Context

This is the first implementation-oriented IPD migration slice after the archived `ipd-business-baseline`. IPD stores cross-tool career context in `users.profile_snapshot` and derives an `AgentUserProfile` summary from snapshot blocks, user facts, check-in, tasks, and career plan. CyanCruise already contains a minimal `UserProfileSnapshot`, `CareerAgentRuleInput`, and today's recommendation rule, so this change should extend that foundation rather than create a parallel profile model.

## Source Evidence

| Source | Path | Extracted behavior |
| --- | --- | --- |
| Snapshot DTO | `F:\Project\IPD\backend\src\main\java\com\group1\career\model\dto\UserProfileSnapshot.java` | Assessment/resume/interview/preferences/onboarding blocks |
| Profile input DTO | `F:\Project\IPD\backend\src\main\java\com\group1\career\model\dto\ProfileInputsRequest.java` | Optional user-supplied career facts |
| Snapshot service | `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\UserProfileSnapshotServiceImpl.java` | Read empty snapshot, field-by-field block merge, target role preference merge |
| Unified profile service | `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\AgentProfileServiceImpl.java` | Target-role priority, readiness, completeness, missing signals, stage inference |
| User controller | `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\UserController.java` | Onboarding endpoint, target role persisted to preferences, education updates user basics |
| Onboarding page | `F:\Project\IPD\frontend\src\pages\onboarding\index.vue` | Five-step intake fields and recommended entry |
| Onboarding gate | `F:\Project\IPD\frontend\src\utils\onboardingGate.ts` | Determine whether onboarding should be shown |
| Pending sync | `F:\Project\IPD\frontend\src\utils\onboardingSync.ts` | Local/pending onboarding sync after login |

## Target Ownership

| Behavior | CyanCruise owner |
| --- | --- |
| Shared profile snapshot DTO and nested blocks | `code/base/v620-cc001-base-common/src/main/java/v620/cc001/base/common/dto/career` |
| Shared unified profile DTO and profile input DTO | `code/base/v620-cc001-base-common/src/main/java/v620/cc001/base/common/dto/career` |
| Merge helpers, target-role resolution, readiness/completeness calculation | `code/base/v620-cc001-base-helper/src/main/java/v620/base/helper/career` |
| Snapshot/profile application service interfaces and default orchestration | `code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/mservice` |
| WebAPI entry points for profile/onboarding | `code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/webapi` |
| Persistent Cosmic data models | `datamodel/` |
| Onboarding and profile page resources | `webapp/` |

## Proposed Design

### Snapshot Contract

Extend CyanCruise's existing `UserProfileSnapshot` to include the full onboarding shape from IPD:

- `identityType`
- `stage`
- `painPoint`
- `hasResume`
- `resumeStatus`
- `timeline`
- `education.school`
- `education.major`
- `education.degree`
- `education.graduationYear`
- `weeklyAvailability`
- `priorityHelp`
- `recommendedEntry`
- `onboardingCompletedAt`

Target role remains in `PreferencesBlock.targetRole`. This avoids the IPD problem of multiple target-role copies.

### Merge Semantics

Create a helper that can field-merge:

- preferences
- onboarding
- assessment
- resume
- interview

Only non-null incoming fields update the target block. Empty strings should not erase an existing non-empty value unless a future requirement explicitly introduces clearing semantics.

### Unified Profile

Create a DTO for unified profile output with these groups:

- personalization level
- completeness score
- current stage
- target role, source, confidence
- readiness indicators
- behavior indicators where available
- missing signals
- evidence map

The first implementation can compute only the fields supported by available CyanCruise inputs, but the DTO should leave room for assessment/resume/interview/plan integration.

### Application Boundary

Introduce a `CareerProfileApplicationService` with operations shaped around business behavior:

- `getSnapshot(userId)`
- `mergePreferences(userId, request)`
- `mergeOnboarding(userId, request)`
- `saveProfileInputs(userId, request)`
- `refreshProfile(userId)`
- `getProfile(userId)`

Persistence can begin with an adapter interface so the service can be tested without Cosmic storage. The actual Cosmic persistence binding can be implemented once the data model is defined.

### WebAPI Boundary

Expose endpoints under the existing career namespace rather than copying IPD URLs literally. Suggested routes:

- `/cc001/career-profile/snapshot/get`
- `/cc001/career-profile/preferences/save`
- `/cc001/career-profile/onboarding/save`
- `/cc001/career-profile/inputs/save`
- `/cc001/career-profile/profile/get`
- `/cc001/career-profile/profile/refresh`

Exact annotation names should follow current `CareerAgentWebApi` conventions.

### Integration With Today Recommendation

`CareerAgentRuleInput` should be able to carry the richer snapshot. `CareerAgentTodayRuleService` should continue to work with partial input and should not require a persisted profile. This keeps today's recommendation independently testable.

## Migration Decisions

- Do not copy Spring annotations, Lombok, JPA repositories, Flyway migrations, or uni-app storage code.
- Do not introduce Java 9+ APIs.
- Do not make self-reported `hasResume` equivalent to a real resume record.
- Do not duplicate target role in onboarding.
- Do not block snapshot update on optional profile refresh failures unless the persisted data itself fails.

## Risks And Mitigations

- **Risk**: CyanCruise persistent data model is not yet defined.  
  **Mitigation**: Introduce application/storage boundaries and keep pure merge/calculation helpers testable.
- **Risk**: Chinese strings from IPD show terminal mojibake.  
  **Mitigation**: Treat IPD copy as behavior input; rewrite user-facing text in CyanCruise when UI is implemented.
- **Risk**: Over-migrating the full IPD agent profile could delay P0 delivery.  
  **Mitigation**: Implement only onboarding/profile fields required by the CareerLoop entry and today's recommendation.

## Verification

- `openspec validate migrate-career-profile-onboarding --strict`
- JDK 8 Gradle build after implementation:

```powershell
$env:JAVA_HOME = 'F:\kingdee\ENV\jdk'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat clean build
```

