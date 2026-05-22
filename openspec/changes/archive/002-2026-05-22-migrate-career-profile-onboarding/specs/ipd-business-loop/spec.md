## MODIFIED Requirements

### Requirement: Prioritize P0 loop entry capabilities
The first implementation changes SHALL prioritize career profile, onboarding, assessment core, resume core, and today's action recommendation before lower-priority resource, notification, and management features. Career profile and onboarding SHALL be the first implementation slice because they provide the target role, user context, and early signals required by later P0 capabilities.

#### Scenario: Choose first implementation scope
- **WHEN** the team selects the next implementation change after the baseline
- **THEN** the selected change comes from the P0 migration scope unless explicitly approved otherwise

#### Scenario: Start profile and onboarding migration
- **WHEN** `migrate-career-profile-onboarding` is active
- **THEN** it is treated as the first P0 implementation slice of the CareerLoop migration

