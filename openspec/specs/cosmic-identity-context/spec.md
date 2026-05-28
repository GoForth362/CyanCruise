# cosmic-identity-context Specification

## Purpose
TBD - created by archiving change migrate-cosmic-identity-context. Update Purpose after archive.
## Requirements
### Requirement: Cosmic identity context contract

CyanCruise SHALL define a JDK 8 compatible identity context contract for CareerLoop platform calls. The context SHALL represent userId, adminId, roles, orgId, source, environment, status, ip, userAgent, and optional diagnostic message without depending on IPD JWT, uni-app storage, axios, Spring Security, or Java 17 features.

#### Scenario: Production identity is resolved

- **WHEN** a Cosmic platform adapter resolves the current logged-in user
- **THEN** the identity context SHALL contain the CareerLoop user identifier, source `cosmic-platform-context`, environment `production`, and status `OK`

#### Scenario: Identity is unavailable

- **WHEN** no platform user can be resolved in production mode
- **THEN** the identity context SHALL return status `IDENTITY_REQUIRED` and SHALL NOT invent or reuse a hardcoded user identifier

### Requirement: Development fallback isolation

Development fallback identity SHALL be explicit and separated from production identity. Query parameters, test fixtures, localStorage values, or manual input MAY be used for local validation only when the context is marked as development fallback.

#### Scenario: Development fallback is used

- **WHEN** a local validation call supplies an explicit development userId
- **THEN** the identity context SHALL mark environment `development`, source `development-fallback`, and SHALL make the fallback visible to callers

#### Scenario: Production call omits platform identity

- **WHEN** a production WebAPI call lacks Cosmic platform identity but includes a request body userId
- **THEN** the identity boundary SHALL reject the call with identity-required semantics instead of trusting the body userId as production identity

### Requirement: Explicit identity consistency

The identity helper SHALL validate explicit request body ownership fields against the resolved platform identity. User-owned requests SHALL match the resolved userId; admin-owned requests SHALL match the resolved adminId or resolved administrator user identity according to the admin adapter contract.

#### Scenario: Explicit user id matches platform user

- **WHEN** a user-owned WebAPI receives request userId equal to the resolved platform userId
- **THEN** the boundary SHALL allow the service call to proceed with that userId

#### Scenario: Explicit user id conflicts with platform user

- **WHEN** a user-owned WebAPI receives request userId different from the resolved platform userId
- **THEN** the boundary SHALL reject the call with identity-mismatch semantics and SHALL NOT call the user-owned application service

#### Scenario: Explicit user id is omitted for current-user operation

- **WHEN** a user-owned WebAPI can safely use the current platform user without a body userId
- **THEN** the boundary SHALL supply the resolved platform userId to the application service

### Requirement: Administrator authorization context

Administrator operations SHALL require a resolvable identity and an `ADMIN` equivalent role. The helper SHALL normalize recognized administrator roles while preserving raw roles for audit and diagnostics.

#### Scenario: Admin caller has equivalent role

- **WHEN** the identity context contains a user/admin identifier and role `ADMIN`, `COSMIC_ADMIN`, or another configured admin equivalent
- **THEN** admin WebAPI boundaries SHALL authorize the management operation and include the admin identity in audit context

#### Scenario: Admin caller lacks role

- **WHEN** a caller has a user identity but no administrator equivalent role
- **THEN** admin WebAPI boundaries SHALL return forbidden semantics and SHALL NOT call management application services

#### Scenario: Admin caller identity is missing

- **WHEN** no caller identity can be resolved for an admin operation
- **THEN** admin WebAPI boundaries SHALL return identity-required semantics and SHALL NOT use a hardcoded administrator

### Requirement: WebAPI identity boundary

CareerLoop WebAPI entry points SHALL use the identity context boundary before invoking user-owned or admin-owned application services. The boundary SHALL convert identity-required, forbidden, and identity-mismatch results into stable DTO/status semantics compatible with existing route/API fallback behavior.

#### Scenario: User-owned route is protected

- **WHEN** a protected CareerLoop user route such as profile snapshot, onboarding, today action, resume, notification, or assistant chat is invoked
- **THEN** the WebAPI boundary SHALL resolve identity and validate ownership before calling the application service

#### Scenario: Admin route is protected

- **WHEN** a `/cc001/admin/*` route is invoked
- **THEN** the WebAPI boundary SHALL resolve administrator identity and role before calling admin governance services

#### Scenario: Boundary rejects request

- **WHEN** the identity boundary returns identity-required, forbidden, or identity-mismatch
- **THEN** the WebAPI response SHALL expose a stable status or result that the webapp route/API fallback can render without treating the operation as successful

### Requirement: Source evidence and migration boundary

The identity context migration SHALL record IPD source evidence for auth, request, user, app startup, and admin request semantics, while excluding direct migration of IPD implementation technologies.

#### Scenario: Reviewer inspects identity migration

- **WHEN** reviewers inspect proposal, design, tasks, or migration map entries
- **THEN** they SHALL find IPD references for frontend auth/request/user API, app startup/login handling, admin request/whoami semantics, and backend auth/admin source where applicable

#### Scenario: Runtime dependencies are checked

- **WHEN** CyanCruise is built or deployed
- **THEN** it SHALL NOT require IPD JWT, Vue, uni-app, axios, Spring Security, JPA, Flyway, Java 17, or IPD runtime files for identity context behavior

### Requirement: Identity verification coverage

The migration SHALL include focused verification for identity resolution, development fallback isolation, explicit ID consistency, administrator role authorization, WebAPI rejection behavior, and platform documentation.

#### Scenario: Implementation is verified

- **WHEN** implementation is complete
- **THEN** verification SHALL include helper tests, WebAPI or application-boundary tests, OpenSpec strict validation, webapp route validation where changed, JDK 8 Gradle build, and migration map updates

### Requirement: Resolver factory production adapter selection

Cosmic identity context SHALL include a resolver factory or equivalent selection boundary that chooses between safe unavailable production resolver, configurable Cosmic adapter, and explicit development resolver according to environment and configuration.

#### Scenario: Production adapter is configured

- **WHEN** production adapter configuration is explicitly enabled
- **THEN** the factory SHALL return a resolver that reads Cosmic platform context candidates

#### Scenario: Production adapter is not configured

- **WHEN** production adapter configuration is absent or disabled
- **THEN** the factory SHALL return the safe unavailable resolver and preserve identity-required behavior

### Requirement: Adapter diagnostics in identity context

Cosmic identity context SHALL expose safe adapter diagnostics through status and message fields so deployment checks can distinguish disabled adapter, missing platform context, missing identity fields, and successful resolution.

#### Scenario: Deployment checks identity adapter

- **WHEN** tenant deployment validation invokes a representative protected WebAPI
- **THEN** the identity context result or fallback status SHALL make adapter disabled, missing context, missing role, or identity mismatch distinguishable without exposing secrets

