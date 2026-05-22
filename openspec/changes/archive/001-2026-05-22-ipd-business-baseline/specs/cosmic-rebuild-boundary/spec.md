## ADDED Requirements

### Requirement: Rebuild behavior for Cosmic
The migration SHALL rebuild IPD business behavior for the Kingdee Cosmic/JDK 8 target rather than copying Spring Boot, JPA, Flyway, Vue, or uni-app implementation details.

#### Scenario: Encounter source framework code
- **WHEN** IPD source behavior is implemented with Spring Boot, JPA, Flyway, Vue, or uni-app specifics
- **THEN** the CyanCruise design extracts the business rule and selects a Cosmic-compatible implementation approach

### Requirement: Respect CyanCruise module boundaries
The migrated implementation SHALL place data definitions, shared contracts, helpers, business logic, debug-only code, and web resources in their appropriate CyanCruise modules.

#### Scenario: Add a new business capability
- **WHEN** a change introduces a data model, service, helper, or page resource
- **THEN** the design names the owning CyanCruise module before code is changed

### Requirement: Preserve JDK 8 compatibility
The migrated implementation SHALL remain compatible with JDK 1.8 and the checked-in Gradle wrapper.

#### Scenario: Add or port Java code
- **WHEN** Java code is added during migration
- **THEN** it avoids Java 9+ APIs and language features

### Requirement: Avoid uncontrolled dependency migration
The migration SHALL NOT add IPD dependencies to CyanCruise solely because they existed in the source project.

#### Scenario: Need an external library
- **WHEN** a migration design proposes a new dependency
- **THEN** the design explains the business need, Cosmic compatibility, and why existing project capabilities are insufficient

