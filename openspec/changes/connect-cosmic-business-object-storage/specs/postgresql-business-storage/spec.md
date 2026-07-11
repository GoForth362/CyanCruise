## ADDED Requirements

### Requirement: PostgreSQL SHALL become fallback for Cosmic-switched modules

For modules explicitly switched to Kingdee Cosmic business object storage, PostgreSQL SHALL no longer be the primary runtime storage. PostgreSQL MAY remain available as a local development backend, test backend, migration fallback, or storage for modules not yet switched.

#### Scenario: Module switched to Cosmic

- **WHEN** a module is configured as Cosmic-backed
- **THEN** runtime reads and writes for that module SHALL use Kingdee business object storage
- **AND** PostgreSQL SHALL NOT receive primary writes for that module unless an explicit migration or rollback task is being executed

#### Scenario: Rollback to PostgreSQL

- **WHEN** a Cosmic-backed module must be rolled back
- **THEN** the operator SHALL change the storage configuration to PostgreSQL or remove the module from the Cosmic-enabled list
- **AND** the rollback plan SHALL state which data source is authoritative after rollback

### Requirement: PostgreSQL and Cosmic SHALL NOT silently double-write

CyanCruise SHALL NOT silently double-write the same business state to both PostgreSQL and Cosmic business objects as normal runtime behavior.

#### Scenario: Dual write requested

- **WHEN** a migration step needs temporary comparison or backfill between PostgreSQL and Cosmic
- **THEN** the task SHALL be implemented as an explicit migration, audit, or verification job
- **AND** normal WebAPI requests SHALL still have one authoritative write target per module
