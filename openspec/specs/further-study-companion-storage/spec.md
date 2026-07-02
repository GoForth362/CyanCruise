# further-study-companion-storage Specification

## Purpose
定义考研、保研和留学陪伴能力的统一持久化边界，包括深造目标、生成记录、材料记录、历史事件和 PostgreSQL 存储实现，确保不同用户的数据隔离且可复用。

## Requirements

### Requirement: 管理深造目标
CyanCruise SHALL support saving target information for postgraduate exam, recommendation, and study abroad directions. The saved target SHALL include direction, target school or program, target major or field, target region, current phase, status, and full JSON payload.

#### Scenario: Save a further-study target
- **WHEN** a user submits target information from a further-study companion WebAPI
- **THEN** the system SHALL persist the target for the current user and direction
- **AND** the persisted target SHALL keep the original JSON payload for later review

#### Scenario: Isolate targets by user
- **WHEN** user A and user B save targets for the same direction
- **THEN** user A SHALL NOT read or update user B's target data

### Requirement: Query and update further-study records
CyanCruise SHALL provide list, detail, and status update capabilities for further-study companion records. The list SHALL support filtering by direction, record type, and status, and SHALL return summaries ordered by update time descending.

#### Scenario: List companion records
- **WHEN** a user requests further-study records with optional filters
- **THEN** the system SHALL return only that user's matching record summaries

#### Scenario: Update companion record status
- **WHEN** a user updates a record status
- **THEN** the system SHALL save the new status and update time

### Requirement: Manage further-study material records
CyanCruise SHALL support saving reusable material records for further-study work. Material records SHALL support recommendation self-statement, recommendation letter, tutor contact material, study abroad personal statement, visa material, and postgraduate re-exam material types.

#### Scenario: Save material record
- **WHEN** a user saves a material record
- **THEN** the system SHALL persist its material type, title, status, file reference, text or JSON content, and update time

#### Scenario: List material records
- **WHEN** a user requests material records for a direction or source record
- **THEN** the system SHALL return only matching material records owned by that user

### Requirement: Save further-study history events
CyanCruise SHALL save history events for important changes to further-study targets, records, and materials. Each event SHALL include user, direction, source record, event type, Chinese summary, event JSON, and creation time.

#### Scenario: Create history event
- **WHEN** a target, record, or material is created or updated
- **THEN** the system SHALL append a history event describing the change

#### Scenario: Read history events
- **WHEN** a user views record history
- **THEN** the system SHALL return events for that user's record ordered by creation time

### Requirement: Provide PostgreSQL further-study storage implementation
CyanCruise SHALL provide a PostgreSQL implementation for further-study companion storage and reuse shared `cc001.storage.*` configuration. The implementation SHALL remain compatible with JDK 1.8 and SHALL NOT introduce Spring Boot, JPA, Flyway, Lombok, or Java 9+ APIs.

#### Scenario: PostgreSQL storage is configured
- **WHEN** `cc001.storage.backend=postgresql` and PostgreSQL connection properties are configured
- **THEN** the further-study storage factory SHALL create a PostgreSQL-backed implementation

#### Scenario: PostgreSQL storage is not configured
- **WHEN** PostgreSQL storage is selected but required connection properties are missing
- **THEN** the system SHALL fail fast with a clear configuration error instead of silently using local generated storage

