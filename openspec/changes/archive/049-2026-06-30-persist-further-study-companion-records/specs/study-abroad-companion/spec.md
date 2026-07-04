## MODIFIED Requirements

### Requirement: Persist study abroad companion records
Study abroad companion WebAPI SHALL save the request and result as the current user's further-study companion record after generating profile diagnosis, language plans, school positioning, personal statement outlines, or visa and application checklists. The saved record SHALL mark direction as study abroad and SHALL include the specific record type.

#### Scenario: Save study abroad profile diagnosis
- **WHEN** a user generates study abroad profile diagnosis
- **THEN** the WebAPI SHALL persist the request and diagnosis result as a study abroad companion record

#### Scenario: Save school positioning result
- **WHEN** a user generates school positioning
- **THEN** the WebAPI SHALL persist the positioning request and school option result for that user

### Requirement: Manage study abroad documents and visa checklist status
Study abroad companion SHALL support maintaining personal statements, recommendation letters, resumes, visa materials, and online application checklist status for the current user. The system SHALL save material records, file references, and history events.

#### Scenario: Save personal statement material
- **WHEN** a user saves or generates personal statement content
- **THEN** the system SHALL persist the material record with status and source record reference

#### Scenario: Save visa checklist status
- **WHEN** a user updates visa or online application checklist status
- **THEN** the system SHALL persist the status change and append a history event
