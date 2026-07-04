## MODIFIED Requirements

### Requirement: Content management governance
CyanCruise SHALL support administrator management of CareerLoop home articles, videos, and resource content with create, update, delete, pin, hide, and list contracts. Managed content SHALL be usable by the user-facing resource feed when it is not hidden.

#### Scenario: Admin creates article
- **WHEN** an administrator creates a home article
- **THEN** the system SHALL assign platform ownership fields as needed, default missing published time to current time, persist the article, and record an audit event

#### Scenario: Admin updates existing content
- **WHEN** an administrator saves content with an existing content identifier
- **THEN** the system SHALL update the editable content fields, preserve the content identity, persist the new state, and record an audit event

#### Scenario: Admin toggles article pin or hidden state
- **WHEN** an administrator toggles pinned or hidden state for an existing article
- **THEN** the system SHALL invert only the requested state, preserve other fields, and record an audit event

#### Scenario: Admin deletes content
- **WHEN** an administrator deletes an article or video
- **THEN** the system SHALL remove or mark the content unavailable according to the storage adapter and record an audit event

#### Scenario: Admin manages content in webapp
- **WHEN** an administrator opens the management console content section
- **THEN** the page SHALL provide controls to create, edit, save, hide, restore, pin, unpin, and delete content using understandable Chinese labels

### Requirement: Admin WebAPI and route mapping
The migration SHALL define Cosmic WebAPI and webapp or platform route/API mapping for admin whoami, organizations, dashboards, students, users, skill map, questions, content, broadcast, analytics, and audit logs.

#### Scenario: Content write route map is reviewed
- **WHEN** reviewers inspect webapp or platform migration artifacts
- **THEN** they SHALL find route keys for admin content list, save, pin, hide, and delete operations with administrator authorization requirements
