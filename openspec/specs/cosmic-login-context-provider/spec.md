# cosmic-login-context-provider Specification

## Purpose
TBD - created by archiving change migrate-cosmic-login-context-provider. Update Purpose after archive.
## Requirements
### Requirement: Cosmic login context provider
CyanCruise SHALL provide a JDK 8 compatible provider that reads current login context from the Cosmic platform or an approved bridge and exposes it as a `Map<String,Object>` for the existing configurable identity resolver.

#### Scenario: Platform context is available
- **WHEN** a logged-in Cosmic user invokes a protected CareerLoop WebAPI and the login context bridge is available
- **THEN** the provider SHALL expose configured user/admin/org/role/client candidate fields to `ConfigurableCosmicIdentityResolver`

#### Scenario: Platform context is unavailable
- **WHEN** the login context bridge is unavailable, disabled, or returns no context
- **THEN** the provider SHALL return an empty context or identity-required diagnostics and SHALL NOT invent userId/adminId values

### Requirement: Production and development isolation
The login context provider SHALL be production-only and SHALL NOT read query parameters, localStorage, request body userId/adminId, system properties, or manual development values as production identity.

#### Scenario: Development fallback exists
- **WHEN** a local test uses `DevelopmentCareerLoopIdentityResolver`
- **THEN** the login context provider SHALL NOT be involved and the identity SHALL remain marked as development fallback

#### Scenario: Production body id is present
- **WHEN** production provider cannot resolve platform identity but the request body contains userId or adminId
- **THEN** the identity boundary SHALL preserve identity-required behavior instead of trusting the body id

### Requirement: Context field mapping and role extraction
The provider SHALL preserve or extract platform context fields needed by existing identity adapter configuration, including userId, adminId, orgId, roles, ip, and userAgent candidates. Role values MAY be collections, arrays, delimited text, or platform role objects with code/name-like fields.

#### Scenario: Role objects are returned
- **WHEN** the Cosmic platform bridge returns role objects or maps with code/name values
- **THEN** the provider SHALL expose role codes or names in a form the configurable resolver can normalize

#### Scenario: Tenant uses custom field names
- **WHEN** tenant configuration changes `cc001.identity.adapter.*.fields`
- **THEN** the provider output SHALL remain map-based so the resolver can match configured field candidates without code changes

### Requirement: Safe diagnostics
The login context provider SHALL expose safe diagnostics for disabled provider, missing bridge, bridge exception, empty context, and resolved field sources. Diagnostics SHALL NOT include tokens, cookies, passwords, raw session objects, mobile numbers, email addresses, or full raw context dumps.

#### Scenario: Bridge throws exception
- **WHEN** the platform bridge fails while reading login context
- **THEN** provider diagnostics SHALL report a sanitized failure message and return no trusted identity

#### Scenario: Diagnostics are enabled
- **WHEN** diagnostics are enabled for tenant validation
- **THEN** messages SHALL distinguish disabled provider, empty context, and missing identity fields without exposing sensitive values

### Requirement: Resolver factory integration
CareerLoop production identity resolver factory SHALL use the login context provider when the identity adapter is enabled. Adapter disabled behavior SHALL remain unchanged and SHALL return safe unavailable identity-required semantics.

#### Scenario: Adapter enabled with provider
- **WHEN** `cc001.identity.adapter.enabled=true` and login context provider is available
- **THEN** production resolver SHALL resolve identity from provider output and SHALL mark source `cosmic-platform-context` and environment `production`

#### Scenario: Adapter disabled
- **WHEN** `cc001.identity.adapter.enabled` is absent or false
- **THEN** production resolver SHALL remain safely unavailable and SHALL NOT call development fallback

### Requirement: Tenant verification and rollback
The migration SHALL document tenant validation and rollback for Cosmic login context provider. Rollback SHALL disable the provider/adapter and return identity-required states without changing `/cc001/*` WebAPI contracts.

#### Scenario: Tenant validates provider
- **WHEN** tenant operators validate CareerLoop in Cosmic
- **THEN** they SHALL verify representative user and admin WebAPI calls resolve current platform identity, reject mismatched request body ids, and enforce admin role aliases

#### Scenario: Provider is rolled back
- **WHEN** provider configuration is disabled after deployment
- **THEN** protected production WebAPI SHALL return identity-required or forbidden states instead of falling back to development identity

### Requirement: Verification coverage
The login context provider migration SHALL include focused tests for bridge success, bridge unavailable, bridge exception, role object extraction, adapter disabled behavior, request body mismatch rejection, development fallback isolation, diagnostics sanitization, OpenSpec validation, route validation if metadata changes, and JDK 8 Gradle build.

#### Scenario: Implementation is verified
- **WHEN** implementation is complete
- **THEN** verification SHALL include focused provider/resolver/WebAPI tests, strict OpenSpec validation, route map validation when changed, migration map updates, and JDK 8 `.\gradlew.bat clean build`

