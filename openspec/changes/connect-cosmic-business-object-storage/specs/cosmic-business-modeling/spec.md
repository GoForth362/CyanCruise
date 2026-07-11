## ADDED Requirements

### Requirement: 建模文档 SHALL 作为存储接入对象清单

`datamodel/cyancruise-business-modeling.md` SHALL be treated as the reviewed object and field checklist for connecting CyanCruise runtime storage to Kingdee Cosmic business objects.

#### Scenario: Storage adapter references modeled object

- **WHEN** a storage adapter is connected to Cosmic business object storage
- **THEN** its target platform object SHALL appear in the business modeling document
- **AND** the document SHALL include the object code, table name, key fields, ownership field, status field, and JSON payload fields needed by that adapter

#### Scenario: Modeling gap is found

- **WHEN** implementation discovers that a required field is missing from the platform object or modeling document
- **THEN** the gap SHALL be recorded before switching the module to Cosmic storage
- **AND** the implementation SHALL NOT silently store that value only in a local backend for a Cosmic-switched module

### Requirement: 管理端与用户端可复用同一通知对象

The modeled `v620_cc_notice` business object SHALL be allowed to serve both administrator announcements and user-facing message center records, provided that recipient, type, status, read time, and route fields distinguish the concrete use case.

#### Scenario: Administrator sends announcement

- **WHEN** an administrator sends an announcement to one user or all active users
- **THEN** the storage adapter SHALL create notice records in `v620_cc_notice` with administrator, recipient, type, title, content, status, and created time fields

#### Scenario: User reads message center

- **WHEN** a user opens message center
- **THEN** the notification storage SHALL read `v620_cc_notice` records filtered by recipient or allowed broadcast scope
- **AND** it SHALL map read and archived states to the existing message center DTO contract
