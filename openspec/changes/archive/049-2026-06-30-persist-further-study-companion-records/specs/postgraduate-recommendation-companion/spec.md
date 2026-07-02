## MODIFIED Requirements

### Requirement: Persist recommendation companion records
Recommendation companion WebAPI SHALL save the request and result as the current user's further-study companion record after generating competitiveness diagnosis, action plans, document polishing, or tutor intention letters. The saved record SHALL mark direction as recommendation and SHALL include the specific record type.

#### Scenario: Save recommendation diagnosis result
- **WHEN** a user generates recommendation competitiveness diagnosis
- **THEN** the WebAPI SHALL persist the request and result as a recommendation companion record

#### Scenario: Save recommendation plan result
- **WHEN** a user generates a recommendation action plan
- **THEN** the WebAPI SHALL persist the plan result and related request for that user

### Requirement: Manage recommendation materials and tutor contact status
Recommendation companion SHALL support maintaining recommendation materials and tutor contact status for the current user. Material and contact records SHALL be bound to user, direction, and source record, and SHALL support status updates and history events.

#### Scenario: Save polished document material
- **WHEN** a user saves or generates polished recommendation material
- **THEN** the system SHALL persist the material record with status and source record reference

#### Scenario: Save tutor contact letter
- **WHEN** a user generates or updates a tutor intention letter
- **THEN** the system SHALL persist the tutor contact material and append a history event
