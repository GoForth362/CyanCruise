## MODIFIED Requirements

### Requirement: Persist recommendation companion records
Recommendation companion WebAPI SHALL save the request and result as the current user's further-study companion record after generating competitiveness diagnosis, action plans, document polishing, or tutor intention letters. The saved record SHALL mark direction as recommendation, include the specific record type, and retain only the latest generated result for each user and record type.

#### Scenario: Save recommendation diagnosis result
- **WHEN** a user generates recommendation competitiveness diagnosis
- **THEN** the WebAPI SHALL persist the request and result as that user's latest recommendation diagnosis record

#### Scenario: Save recommendation plan result
- **WHEN** a user generates a recommendation action plan
- **THEN** the WebAPI SHALL replace that user's prior recommendation-plan record with the plan result and related request

#### Scenario: Save document or tutor result
- **WHEN** a user regenerates a document polishing result or tutor intention letter
- **THEN** the WebAPI SHALL replace only that user's previous record of the same function type

