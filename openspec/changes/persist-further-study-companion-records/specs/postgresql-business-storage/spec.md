## MODIFIED Requirements

### Requirement: Use PostgreSQL to save further-study companion business state
CyanCruise SHALL include further-study companion records in PostgreSQL business state storage. Migrated further-study state SHALL include targets, postgraduate exam records, recommendation records, study abroad records, material records, and history events.

#### Scenario: Further-study state is saved
- **WHEN** a further-study companion WebAPI completes a user-owned generation or update action
- **THEN** CyanCruise SHALL save the resulting business state through the configured PostgreSQL storage implementation

#### Scenario: PostgreSQL DDL is reviewed
- **WHEN** reviewers inspect the PostgreSQL DDL for further-study companion storage
- **THEN** the SQL SHALL NOT include destructive operations such as `DROP TABLE` or `TRUNCATE`
- **AND** the SQL SHALL define additive tables or indexes for the migrated further-study state
