## ADDED Requirements

### Requirement: 使用金蝶业务对象作为运行时业务状态存储

CyanCruise SHALL support Kingdee Cosmic business objects as a runtime storage backend for migrated business state. The storage backend SHALL write to the configured `v620_cc_*` business objects instead of local PostgreSQL or process memory for modules explicitly switched to Cosmic storage.

#### Scenario: Cosmic storage backend is enabled

- **WHEN** `cc001.storage.backend=cosmic` and Cosmic business object storage is enabled for a module
- **THEN** that module SHALL use the Cosmic business object storage adapter as its primary runtime storage
- **AND** the WebAPI request and response contract SHALL remain compatible with the existing frontend

#### Scenario: Module is not switched

- **WHEN** Cosmic storage is enabled globally but a module is not included in the enabled module list
- **THEN** that module SHALL continue using its current configured storage backend
- **AND** the system SHALL NOT fail the whole application because an unrelated module is not yet connected

### Requirement: 真实平台 gateway SHALL 封装金蝶数据服务

CyanCruise SHALL provide a real `CosmicDatamodelGateway` implementation that encapsulates Kingdee business object read, save, list, find, and delete operations. Domain storage adapters SHALL depend on `CosmicDatamodelGateway`, not on scattered platform API calls.

#### Scenario: Save business object record

- **WHEN** a storage adapter saves a logical `CosmicDatamodelRecord`
- **THEN** the gateway SHALL convert the logical object and fields to platform object and `v620_xxx` fields before calling Kingdee data services
- **AND** the saved record SHALL be converted back to logical fields before returning to domain storage

#### Scenario: Query user-owned records

- **WHEN** a storage adapter lists records by user, status, time, or business identifier
- **THEN** the gateway SHALL apply equivalent platform filters using mapped field names
- **AND** records belonging to other users SHALL NOT be returned

### Requirement: Storage factory SHALL support safe Cosmic switching

CyanCruise storage factories SHALL support an explicit Cosmic backend mode and SHALL keep PostgreSQL or in-memory storage as a controlled fallback for tests, local development, and modules not yet switched.

#### Scenario: Cosmic backend selected

- **WHEN** `cc001.storage.backend=cosmic` is configured
- **THEN** `CyanCruiseStorageFactory` SHALL construct Cosmic-backed storage implementations for enabled modules
- **AND** it SHALL NOT silently use PostgreSQL for a module that has been explicitly marked as Cosmic-only

#### Scenario: Cosmic backend unavailable

- **WHEN** Cosmic backend is selected but the platform gateway is unavailable for an enabled module
- **THEN** the system SHALL return a clear storage-unavailable state or fail fast during construction according to the module contract
- **AND** it SHALL NOT write partial records to an unintended local storage backend

### Requirement: Identity and ownership SHALL be preserved

Cosmic business object storage SHALL use the resolved Cosmic identity context for user-owned operations and SHALL preserve user ownership fields on every persisted user record.

#### Scenario: Current user saves profile data

- **WHEN** the current platform user saves profile, resume, assessment, interview, task, plan, further-study, or notification data
- **THEN** the storage adapter SHALL persist the resolved user identifier into `v620_userid` or the object-specific ownership field
- **AND** subsequent list and detail operations SHALL filter by that ownership field

#### Scenario: User identity missing

- **WHEN** a user-owned storage operation has no valid platform identity
- **THEN** the operation SHALL return identity-required or equivalent safe failure
- **AND** it SHALL NOT use a hardcoded production user identifier

### Requirement: Delete operations SHALL preserve auditability where needed

Cosmic business object storage SHALL prefer status-based archive/delete semantics for user-visible and admin-visible records, unless the domain contract explicitly requires physical deletion.

#### Scenario: Archive user message

- **WHEN** a user deletes a notification from the message center
- **THEN** Cosmic storage SHALL mark the corresponding notice status as archived or equivalent
- **AND** normal message center lists SHALL exclude the archived record

#### Scenario: Hide admin content

- **WHEN** an administrator hides content, question, or announcement data
- **THEN** Cosmic storage SHALL update visibility or status fields instead of physically deleting the record

### Requirement: Verification SHALL cover mapping, storage behavior, and real platform acceptance

The change SHALL include automated tests for object/field mapping and storage adapter behavior using a fake gateway, and SHALL define manual acceptance steps for a real Kingdee Cosmic environment.

#### Scenario: Mapping tests pass

- **WHEN** unit tests exercise logical records for mapped objects
- **THEN** object names SHALL map to `v620_cc_*` business objects
- **AND** logical fields SHALL map to expected `v620_xxx` platform fields

#### Scenario: Real environment is accepted

- **WHEN** the implementation is deployed to the target Cosmic environment
- **THEN** reviewers SHALL verify at least profile save/read, resume save/list, assessment submit/read, interview save/read, and notification save/read through existing WebAPI and frontend flows
