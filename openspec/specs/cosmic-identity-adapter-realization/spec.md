# cosmic-identity-adapter-realization Specification

## Purpose
TBD - created by archiving change migrate-cosmic-identity-adapter-realization. Update Purpose after archive.
## Requirements
### Requirement: Configurable Cosmic identity adapter

CyanCruise SHALL provide a configurable Cosmic identity adapter that can resolve CareerLoop identity from platform context candidates without depending on IPD JWT, Spring Security, uni-app storage, axios, Java 17, or unknown tenant-specific runtime classes.

#### Scenario: Adapter is explicitly enabled

- **WHEN** identity adapter configuration enables the Cosmic adapter
- **THEN** the resolver factory SHALL use the configurable Cosmic adapter for production identity resolution

#### Scenario: Adapter is not enabled

- **WHEN** no explicit adapter enablement is configured
- **THEN** production identity resolution SHALL remain safely unavailable and SHALL return identity-required semantics

### Requirement: Platform context field candidates

The configurable adapter SHALL resolve userId, adminId, orgId, roles, ip, and userAgent from configured field candidates. Defaults SHALL include common candidates such as userId, personId, operatorId, uid, adminId, orgId, roles, roleCodes, ip, and userAgent.

#### Scenario: User candidate is found

- **WHEN** platform context contains one configured user candidate
- **THEN** the adapter SHALL populate `CosmicIdentityContextDto.userId`, source `cosmic-platform-context`, environment `production`, and status `OK`

#### Scenario: No user candidate is found

- **WHEN** platform context has no configured user/admin candidate
- **THEN** the adapter SHALL return identity-required status and SHALL NOT use request body identity as a substitute

### Requirement: Role and admin alias resolution

The adapter SHALL accept roles from collections, arrays, or delimited strings and SHALL preserve normalized roles for the existing identity helper. Admin aliases SHALL be configurable while preserving default aliases `ADMIN`, `COSMIC_ADMIN`, and `PLATFORM_ADMIN`.

#### Scenario: Roles are delimited text

- **WHEN** platform context contains roles as comma or semicolon separated text
- **THEN** the adapter SHALL split and trim roles before writing them to identity context

#### Scenario: Admin alias is configured

- **WHEN** adapter configuration includes a tenant-specific administrator role alias
- **THEN** the identity helper SHALL treat that alias as an admin-equivalent role after normalization

### Requirement: Production and development isolation

Configurable production adapter SHALL remain separate from development fallback. Development resolver SHALL continue to mark environment `development` and source `development-fallback`, while Cosmic adapter SHALL mark environment `production` and source `cosmic-platform-context`.

#### Scenario: Development resolver is used

- **WHEN** tests or local validation use the development resolver
- **THEN** the resulting identity SHALL remain marked as development fallback regardless of production adapter configuration

#### Scenario: Production adapter is used

- **WHEN** the configurable Cosmic adapter resolves identity
- **THEN** the resulting identity SHALL NOT be marked as development fallback

### Requirement: Diagnostics and safe failure

The configurable adapter SHALL provide safe diagnostics for missing context, disabled adapter, missing fields, and parsed source without exposing sensitive token or credential values.

#### Scenario: Adapter is disabled

- **WHEN** production adapter is disabled
- **THEN** identity context SHALL include an identity-required status and a diagnostic message indicating adapter disabled or unavailable

#### Scenario: Context is malformed

- **WHEN** platform context is null, empty, or contains unsupported role value types
- **THEN** adapter SHALL return stable identity-required or partial role results without throwing unchecked parsing errors

### Requirement: Verification coverage

The adapter realization SHALL include focused tests for enablement, disabled fallback, candidate fields, roles as collection/array/text, admin aliases, missing identity, and WebAPI boundary integration.

#### Scenario: Implementation is verified

- **WHEN** implementation is complete
- **THEN** verification SHALL include focused adapter/helper/WebAPI tests, route map validation if metadata changes, OpenSpec strict validation, JDK 8 Gradle build, and migration map updates

### Requirement: Production login context provider integration
The configurable Cosmic identity adapter SHALL integrate with a production login context provider that supplies platform context candidates to `ConfigurableCosmicIdentityResolver`. The adapter SHALL keep existing field candidate, role alias, diagnostics, and safe disabled behavior.

#### Scenario: Provider supplies login context
- **WHEN** the identity adapter is enabled and the login context provider returns current Cosmic user context
- **THEN** the configurable resolver SHALL map provider fields to `CosmicIdentityContextDto` without requiring request body userId/adminId as production identity

#### Scenario: Provider returns empty context
- **WHEN** the identity adapter is enabled but the provider returns empty context
- **THEN** the resolver SHALL return identity-required diagnostics and SHALL NOT use development fallback

#### Scenario: Provider bridge is tenant-specific
- **WHEN** a tenant uses different Cosmic field names or role aliases
- **THEN** the existing `cc001.identity.adapter.*` configuration SHALL adapt field candidates and admin aliases without changing WebAPI contracts

#### Scenario: Provider integration is verified
- **WHEN** implementation is complete
- **THEN** focused tests SHALL cover production factory wiring, provider success, provider empty/exception states, role object extraction, disabled adapter fallback, and WebAPI boundary identity rejection

