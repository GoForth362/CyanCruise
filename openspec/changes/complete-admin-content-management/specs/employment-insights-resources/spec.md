## MODIFIED Requirements

### Requirement: Career resource feed
CyanCruise SHALL provide a resource feed contract for webapp consumption that can expose public service cards, article cards, video cards, consultation/tip cards, and career path cards without requiring IPD frontend or crawler runtime. The feed SHALL include visible administrator-managed content when such content exists.

#### Scenario: Resource feed is requested
- **WHEN** the webapp requests employment resources
- **THEN** the system SHALL return resource cards with stable identifiers, title, summary or body, category or keyword, source URL, image URL when available, and type-specific fields such as video duration or career path id

#### Scenario: Admin content is visible
- **WHEN** administrator-managed content is saved and not hidden
- **THEN** the resource feed SHALL include that content as a resource card using its title, summary, category, image URL, source URL, and content type

#### Scenario: Admin content is hidden
- **WHEN** administrator-managed content is marked hidden
- **THEN** the resource feed SHALL NOT expose that content to user-facing pages

#### Scenario: Resource feed has no configured content
- **WHEN** no administrator-managed resource records are available
- **THEN** the system SHALL return seeded or empty resource state that keeps the webapp navigable and SHALL NOT fail the workbench
