## MODIFIED Requirements

### Requirement: Prioritize P0 loop entry capabilities
The first implementation changes SHALL prioritize career profile, onboarding, assessment core, resume core, and today's action recommendation before lower-priority resource, notification, and management features. Career profile and onboarding SHALL be the first implementation slice because they provide the target role, user context, and early signals required by later P0 capabilities. Assessment core SHALL be the next P0 slice because it provides the first structured direction baseline for the user's profile and recommendations.

#### Scenario: Choose first implementation scope
- **WHEN** the team selects the next implementation change after the baseline
- **THEN** the selected change comes from the P0 migration scope unless explicitly approved otherwise

#### Scenario: Start profile and onboarding migration
- **WHEN** `migrate-career-profile-onboarding` is active
- **THEN** it is treated as the first P0 implementation slice of the CareerLoop migration

#### Scenario: Start assessment core migration
- **WHEN** `migrate-assessment-core` is active
- **THEN** it is treated as the next P0 implementation slice after profile and onboarding

