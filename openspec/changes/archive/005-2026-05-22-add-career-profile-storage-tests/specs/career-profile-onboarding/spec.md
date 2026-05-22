## MODIFIED Requirements

### Requirement: Expose profile operations through an application boundary
The system SHALL expose application-level operations to read a snapshot, merge onboarding, merge preferences, save profile inputs, refresh unified profile, and get unified profile for the current user or a specified user id. The default application service SHALL use a durable storage adapter when no platform data-model adapter is configured, and the storage boundary SHALL remain replaceable by a future Cosmic datamodel implementation. The durable adapter SHALL have focused tests proving data can be read through a fresh storage or service instance.

#### Scenario: Web API calls merge onboarding
- **WHEN** a web or form entry calls the onboarding operation
- **THEN** the operation merges the snapshot, refreshes the unified profile, and returns the updated snapshot or profile result according to the API contract

#### Scenario: Restart with durable adapter
- **WHEN** the default file-backed adapter saves a profile snapshot and the service is recreated
- **THEN** the snapshot can be read back from the configured storage directory

#### Scenario: Replace with Cosmic persistence
- **WHEN** a Cosmic datamodel adapter is implemented
- **THEN** it can replace the default adapter through the `CareerProfileStorage` boundary without changing helper logic or WebAPI contracts

#### Scenario: Run storage persistence tests
- **WHEN** the cloud application module test suite runs
- **THEN** tests verify snapshot, facts, and derived profile data survive across fresh file-storage or application-service instances

