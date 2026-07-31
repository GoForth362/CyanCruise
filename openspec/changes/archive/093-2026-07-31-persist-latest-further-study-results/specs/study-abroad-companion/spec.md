## MODIFIED Requirements

### Requirement: Persist study abroad companion records
Study abroad companion WebAPI SHALL save the request and result as the current user's further-study companion record after generating profile diagnosis, language plans, school positioning, personal statement outlines, or visa and application checklists. The saved record SHALL mark direction as study abroad, include the specific record type, and retain only the latest generated result for each user and record type.

#### Scenario: Save study abroad profile diagnosis
- **WHEN** a user generates study abroad profile diagnosis
- **THEN** the WebAPI SHALL persist the request and diagnosis result as that user's latest study abroad profile record

#### Scenario: Save school positioning result
- **WHEN** a user generates school positioning
- **THEN** the WebAPI SHALL replace that user's prior school-positioning record with the current request and school option result

#### Scenario: Regenerate another study abroad function
- **WHEN** a user regenerates a language plan, personal statement outline, or visa checklist
- **THEN** the WebAPI SHALL replace only the previous record for that user and function type
