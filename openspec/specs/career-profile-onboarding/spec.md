# career-profile-onboarding Specification

## Purpose
Define how CyanCruise captures onboarding intake, maintains a cross-tool career profile snapshot, and derives a unified career profile for later CareerLoop capabilities.
## Requirements
### Requirement: Maintain a cross-tool user profile snapshot
The system SHALL maintain a user profile snapshot with independent blocks for assessment, resume, interview, preferences, and onboarding so each migrated capability can update its own block without overwriting unrelated data. The snapshot merge helper SHALL have focused test coverage for preserving unrelated blocks and for blank target-role input not clearing existing preferences.

#### Scenario: Merge onboarding into existing snapshot
- **WHEN** onboarding data is submitted for a user who already has assessment or resume data
- **THEN** the system preserves the existing assessment and resume blocks while updating only the provided onboarding fields

#### Scenario: Read empty snapshot
- **WHEN** no profile snapshot exists for a user
- **THEN** the system returns an empty versioned snapshot rather than failing or returning null

#### Scenario: Run snapshot merge tests
- **WHEN** the helper module test suite runs
- **THEN** tests verify onboarding merge preserves existing snapshot blocks and does not clear an existing target role with blank input

### Requirement: Capture onboarding intake fields
The onboarding block SHALL capture identity type, stage, pain point, self-reported resume state, resume readiness status, timeline, education, weekly availability, priority help, recommended entry, and completion timestamp.

#### Scenario: Complete onboarding intake
- **WHEN** a user completes onboarding with career stage, target role, pain point, resume state, timeline, education, weekly availability, and priority help
- **THEN** the system stores those values in the onboarding block and stores the target role in preferences

#### Scenario: Self-reported resume is not system resume
- **WHEN** onboarding says the user has a resume
- **THEN** the system treats this as self-report only and does not mark the user as having an uploaded resume record

### Requirement: Store target role as a preference
The system SHALL store target role in the profile preferences block and SHALL NOT duplicate it as an onboarding field.

#### Scenario: Submit onboarding target role
- **WHEN** onboarding payload includes a non-empty target role
- **THEN** the system merges that target role into preferences and leaves onboarding focused on intake context

#### Scenario: Submit blank target role
- **WHEN** onboarding payload includes a blank target role
- **THEN** the system does not replace an existing non-empty target role with a blank value

### Requirement: Resolve target role by evidence priority
The unified profile SHALL resolve target role using the priority order preferences, resume target job, interview position, explicit user input fact, then assessment suggested role. The target-role priority SHALL have focused test coverage.

#### Scenario: Preference overrides inferred role
- **WHEN** preferences contain a target role and assessment also suggests roles
- **THEN** the unified profile target role comes from preferences with higher confidence than inferred assessment data

#### Scenario: Use assessment fallback
- **WHEN** no preference, resume target job, interview position, or explicit user-input target role exists and assessment suggested roles are available
- **THEN** the unified profile uses the first assessment suggested role as an inferred target

#### Scenario: Run target resolution tests
- **WHEN** the helper module test suite runs
- **THEN** tests verify preference priority and assessment fallback behavior

### Requirement: Compute unified profile outputs
The system SHALL produce a unified profile summary with personalization level, completeness score, current stage, target role source, readiness indicators, missing signals, and evidence. The current-stage and readiness rules SHALL have focused test coverage for onboarding-only users.

#### Scenario: Missing core signals
- **WHEN** the user has no target role, assessment, resume, interview, or career plan
- **THEN** the unified profile includes missing signals for those absent core loop inputs

#### Scenario: Onboarding affects current stage
- **WHEN** onboarding identity type is career switcher
- **THEN** the unified profile current stage reflects career-switch positioning before later resume or interview optimization stages

#### Scenario: Run onboarding profile derivation tests
- **WHEN** the helper module test suite runs
- **THEN** tests verify career-switch stage inference and self-reported resume readiness does not mark a real resume as present

### Requirement: Support user-supplied profile inputs
The system SHALL accept optional user-supplied career inputs including target city, target industry, timeline, weekly hours, preferred task difficulty, graduate-school consideration, study-abroad consideration, and free-text career goal note.

#### Scenario: Save partial profile inputs
- **WHEN** a user submits only target city and weekly hours
- **THEN** the system stores those facts without clearing previously stored target industry or career goal note

### Requirement: Feed today's recommendation with profile data
The onboarding/profile migration SHALL keep the existing today's recommendation rule compatible while allowing it to use richer onboarding and preference data.

#### Scenario: Recommend after onboarding
- **WHEN** a user has completed onboarding but has no assessment, resume, or interview
- **THEN** today's recommendation can use the target role, identity type, and self-reported resume state to choose the next action

### Requirement: Expose profile operations through an application boundary
The system SHALL expose application-level operations to read a snapshot, merge onboarding, merge preferences, save profile inputs, refresh unified profile, and get unified profile for the current user or a specified user id. The default application service SHALL use a durable storage adapter when no platform data-model adapter is configured, and the storage boundary SHALL remain replaceable by a future Cosmic datamodel implementation. The durable adapter SHALL have focused tests proving data can be read through a fresh storage or service instance.

#### Scenario: Web API calls merge onboarding
- **WHEN** a web or form entry calls the onboarding operation
- **THEN** the operation merges the snapshot, refreshes the unified profile, and returns the updated snapshot or profile result according to the API contract

#### Scenario: Restart with durable adapter
- **WHEN** the default file-backed adapter saves a profile snapshot and the service is recreated
- **THEN** the snapshot can be read back from the configured storage directory

#### Scenario: Replace with Cosmic persistence
- **WHEN** a Cosmic datamodel adapter is implemented
- **THEN** it can replace the default adapter through the `CareerProfileStorage` boundary without changing helper logic or WebAPI contracts

#### Scenario: Run storage persistence tests
- **WHEN** the cloud application module test suite runs
- **THEN** tests verify snapshot, facts, and derived profile data survive across fresh file-storage or application-service instances

