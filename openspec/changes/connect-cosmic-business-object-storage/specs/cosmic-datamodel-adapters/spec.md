## ADDED Requirements

### Requirement: Datamodel gateway SHALL connect to real Cosmic business objects

The datamodel adapter capability SHALL include a real runtime gateway that can connect logical CyanCruise storage records to Kingdee Cosmic business objects, not only in-memory or test mappings.

#### Scenario: Real gateway is used by Cosmic storage

- **WHEN** a `Cosmic*Storage` implementation is constructed for production Cosmic backend mode
- **THEN** it SHALL receive a gateway capable of reading and writing real Kingdee business objects
- **AND** it SHALL NOT rely on `InMemoryCosmicDatamodelGateway` outside tests or explicit local fallback

#### Scenario: Platform API dependency stays inside app module

- **WHEN** the real gateway uses Kingdee platform data service APIs
- **THEN** platform runtime dependencies SHALL remain inside `code/cloud01/v620-cc001-cloud01-app01`
- **AND** `code/base/v620-cc001-base-common` and `code/base/v620-cc001-base-helper` SHALL NOT import platform datamodel classes

### Requirement: Field mapping SHALL be complete for switched modules

Before a module is switched to Cosmic storage, its required logical fields SHALL have explicit platform field mappings or documented intentional pass-through behavior.

#### Scenario: Module mapping is reviewed

- **WHEN** a module is added to the Cosmic-enabled storage module list
- **THEN** reviewers SHALL find mappings for its business object, stable business identifier, user ownership field, status field, time fields, and JSON payload fields
- **AND** missing mappings SHALL block switching that module to Cosmic storage

#### Scenario: Unknown logical field is encountered

- **WHEN** a storage adapter attempts to save a logical field that is not mapped
- **THEN** the gateway SHALL either pass it through only if the platform field intentionally has the same identifier or reject it with a clear mapping error
