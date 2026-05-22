## ADDED Requirements

### Requirement: Capture source evidence
Every IPD migration change SHALL document the IPD source files, source data structures, and source user flow that informed the CyanCruise implementation.

#### Scenario: Review a migration proposal
- **WHEN** a migration proposal is reviewed
- **THEN** it lists the relevant `F:\Project\IPD` source paths or explicitly states that no source file exists

### Requirement: Map target ownership
Every IPD migration change SHALL define the CyanCruise target modules that own the resulting data model, business logic, and user interface.

#### Scenario: Plan a migration implementation
- **WHEN** a design document is created
- **THEN** it maps each behavior to `datamodel`, `code/base`, `code/cloud01`, `webapp`, or another explicit target location

### Requirement: Maintain migration status
The migration map SHALL be updated when an implementation change starts, completes, or changes priority.

#### Scenario: Complete a migration change
- **WHEN** a migration change is archived
- **THEN** `docs/ipd-to-cyancruise-migration-map.md` reflects the final status and any follow-up changes

### Requirement: Validate before archive
An implementation migration change SHALL pass OpenSpec validation and the project build before it is considered complete.

#### Scenario: Archive implementation work
- **WHEN** a migration implementation is ready to archive
- **THEN** `openspec validate <change-id> --strict` and the agreed Gradle verification command have been run or any blocker is documented

