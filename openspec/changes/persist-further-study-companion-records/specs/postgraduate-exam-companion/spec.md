## MODIFIED Requirements

### Requirement: Persist postgraduate exam companion records
Postgraduate exam WebAPI SHALL save the request and result as the current user's further-study companion record after generating school recommendations, review plans, mistake analysis, or re-exam preparation. The saved record SHALL mark direction as postgraduate exam and SHALL include the specific record type.

#### Scenario: Save school recommendation result
- **WHEN** a user generates postgraduate school recommendations
- **THEN** the WebAPI SHALL persist the request, result, record type, and Chinese summary for that user

#### Scenario: Save review plan result
- **WHEN** a user generates a postgraduate review plan
- **THEN** the WebAPI SHALL persist the request and generated plan as a further-study record for that user

#### Scenario: Save mistake analysis result
- **WHEN** a user generates mistake analysis
- **THEN** the WebAPI SHALL persist the question payload and analysis result as a further-study record

#### Scenario: Save re-exam preparation result
- **WHEN** a user generates re-exam preparation content
- **THEN** the WebAPI SHALL persist the preparation request and checklist result as a further-study record
