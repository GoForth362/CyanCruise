## MODIFIED Requirements

### Requirement: CyanCruise webapp workbench entry

CyanCruise SHALL provide a CyanCruise webapp entry that makes the migrated job-preparation and further-study routes usable from `webapp/isv/v620/`. The entry SHALL prioritize the current route and target, today's recommended action, compact readiness signals, the saved self-profile summary, and direct action entries for the current direction center, onboarding or assessment, today action, and career plan. All displayed status and summary values SHALL come from the current user's loaded profile, planning, resume, interview, or route data and SHALL NOT fabricate progress or trends.

#### Scenario: Existing user opens the workbench

- **WHEN** a user with a resolvable `userId` opens the CyanCruise webapp entry
- **THEN** the workbench SHALL request the migrated profile and today-action contracts and display the current route target, today's next action, readiness signals, and available CyanCruise action entries in a prioritized layout

#### Scenario: Today action contains long text

- **WHEN** the current route returns a long today-action description
- **THEN** the workbench SHALL keep neighboring status cards stable, preserve the complete action text for assistive or title access, and provide a clear entry to the today-action page

#### Scenario: Saved self-profile is shown

- **WHEN** the current user has a saved self-profile
- **THEN** the workbench SHALL show a compact summary with the existing controls to expand, edit, and enter the current employment or further-study center

#### Scenario: User uses keyboard navigation

- **WHEN** the user focuses a workbench action card or profile control with the keyboard
- **THEN** the page SHALL show a clear focus state and trigger the same existing route or action as pointer input

#### Scenario: User opens a narrow viewport

- **WHEN** the workbench is rendered on a tablet or narrow viewport
- **THEN** the summary, today action, status cards, self-profile, and route entries SHALL adapt to a readable order without horizontal page scrolling

#### Scenario: Missing user id opens the workbench

- **WHEN** the webapp cannot resolve a `userId`
- **THEN** the entry SHALL show an explicit identity-required state and SHALL NOT call user-owned CyanCruise WebAPI with a hardcoded production user
