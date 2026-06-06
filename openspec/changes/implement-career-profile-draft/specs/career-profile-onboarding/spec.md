## ADDED Requirements

### Requirement: Separate drafts from completed onboarding
The system SHALL distinguish profile draft saves from completed onboarding saves. Saving or clearing a draft SHALL NOT update the onboarding snapshot block, target-role preferences, profile facts, or derived unified profile.

#### Scenario: Draft save does not complete onboarding
- **WHEN** a user saves profile draft data with identity type and target role
- **THEN** the user's onboarding snapshot remains unchanged and no derived profile refresh is triggered by the draft save

#### Scenario: Onboarding save still updates profile snapshot
- **WHEN** a user submits completed onboarding through the existing onboarding operation
- **THEN** the system continues to merge onboarding data into the snapshot, merge target role into preferences, and refresh the unified profile
