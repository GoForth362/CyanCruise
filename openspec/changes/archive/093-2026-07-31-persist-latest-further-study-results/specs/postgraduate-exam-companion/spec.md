## MODIFIED Requirements

### Requirement: Persist postgraduate exam companion records
Postgraduate exam WebAPI SHALL save the request and result as the current user's further-study companion record after generating school recommendations, review plans, or re-exam preparation. The saved record SHALL mark direction as postgraduate exam, include the specific record type, and retain only the latest generated result for each user and record type. Mistake analysis SHALL save the question payload and analysis result only to the user's independent mistake book, which SHALL retain all mistake records.

#### Scenario: Save school recommendation result
- **WHEN** a user generates postgraduate school recommendations
- **THEN** the WebAPI SHALL persist the request, result, record type, and Chinese summary as that user's latest school recommendation record

#### Scenario: Save review plan result
- **WHEN** a user generates a postgraduate review plan
- **THEN** the WebAPI SHALL replace that user's prior review-plan record with the current request and generated plan

#### Scenario: Save mistake analysis result
- **WHEN** a user generates mistake analysis
- **THEN** the WebAPI SHALL persist the question payload and analysis result in the independent mistake book
- **AND** the system SHALL retain all of that user's mistake-book records

#### Scenario: Save re-exam preparation result
- **WHEN** a user generates re-exam preparation content
- **THEN** the WebAPI SHALL replace that user's prior re-exam preparation record with the current request and checklist result

