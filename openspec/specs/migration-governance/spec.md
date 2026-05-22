# migration-governance Specification

## Purpose
Define the evidence, ownership, status tracking, and validation rules required for each IPD-to-CyanCruise migration change.
## Requirements
### Requirement: Capture source evidence
Every IPD migration change SHALL document the IPD source files, source data structures, and source user flow that informed the CyanCruise implementation. For career profile and onboarding, the source evidence SHALL include the IPD snapshot DTO, profile input DTO, snapshot merge service, agent profile service, user controller onboarding endpoint, onboarding page, onboarding gate, and pending-sync utility.

#### Scenario: Review a migration proposal
- **WHEN** a migration proposal is reviewed
- **THEN** it lists the relevant `F:\Project\IPD` source paths or explicitly states that no source file exists

#### Scenario: Review career profile onboarding migration
- **WHEN** `migrate-career-profile-onboarding` is reviewed
- **THEN** the review can trace each profile and onboarding requirement back to an IPD source file or documented migration decision

### Requirement: Map target ownership
Every IPD migration change SHALL define the CyanCruise target modules that own the resulting data model, business logic, and user interface. For career profile and onboarding, shared DTOs SHALL live in `base-common`, pure calculation or merge helpers SHALL live in `base-helper`, application orchestration and WebAPI SHALL live in `cloud01-app01`, persistent entities SHALL live in `datamodel`, and page resources SHALL live in `webapp`.

#### Scenario: Plan a migration implementation
- **WHEN** a design document is created
- **THEN** it maps each behavior to `datamodel`, `code/base`, `code/cloud01`, `webapp`, or another explicit target location

#### Scenario: Add profile DTOs and services
- **WHEN** profile/onboarding code is implemented
- **THEN** shared data contracts, helpers, application services, persistent models, and page resources are placed in their defined owning modules

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

