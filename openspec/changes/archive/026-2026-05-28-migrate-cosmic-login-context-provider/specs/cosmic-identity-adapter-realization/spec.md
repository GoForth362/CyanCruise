## ADDED Requirements

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
