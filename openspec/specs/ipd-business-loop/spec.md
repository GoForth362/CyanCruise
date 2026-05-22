# ipd-business-loop Specification

## Purpose
Define the CareerLoop product flow from IPD that CyanCruise migration work must preserve and prioritize.
## Requirements
### Requirement: Preserve the CareerLoop business loop
The system migration SHALL preserve the IPD CareerLoop core loop as the guiding product flow: target role, user profile or assessment, resume diagnosis, JD matching, today's task, mock interview, feedback, next action, and career plan.

#### Scenario: Define a migration change
- **WHEN** a new IPD migration change is proposed
- **THEN** the change identifies which part of the CareerLoop business loop it supports

#### Scenario: Defer non-core work
- **WHEN** a source IPD capability does not directly support the core loop
- **THEN** the capability is classified as P2 unless the proposal explains why it is needed earlier

### Requirement: Prioritize P0 loop entry capabilities
The first implementation changes SHALL prioritize career profile, onboarding, assessment core, resume core, and today's action recommendation before lower-priority resource, notification, and management features. Career profile and onboarding SHALL be the first implementation slice because they provide the target role, user context, and early signals required by later P0 capabilities.

#### Scenario: Choose first implementation scope
- **WHEN** the team selects the next implementation change after the baseline
- **THEN** the selected change comes from the P0 migration scope unless explicitly approved otherwise

#### Scenario: Start profile and onboarding migration
- **WHEN** `migrate-career-profile-onboarding` is active
- **THEN** it is treated as the first P0 implementation slice of the CareerLoop migration

### Requirement: Keep product language user-centered
The migrated product experience SHALL emphasize the user's next career-preparation step, readiness, target role, and plan rather than requiring users to understand internal agent architecture.

#### Scenario: Write user-facing copy
- **WHEN** a migration change introduces user-facing text
- **THEN** the text describes career-preparation outcomes and next actions instead of internal agent mechanics

