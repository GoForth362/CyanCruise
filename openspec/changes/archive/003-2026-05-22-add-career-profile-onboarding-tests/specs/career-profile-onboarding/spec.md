## MODIFIED Requirements

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

