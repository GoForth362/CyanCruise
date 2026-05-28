# cosmic-platform-mounting Specification

## Purpose
TBD - created by archiving change migrate-cosmic-platform-mounting. Update Purpose after archive.
## Requirements
### Requirement: Cosmic webapp platform mount map

CyanCruise SHALL define a platform mount map for CareerLoop webapp entries deployed under `webapp/isv/v620/careerloop`. The map SHALL identify each published entry's route key, title, target hash or URL, audience, required role, IPD source page semantics, and related `/cc001/*` Cosmic WebAPI contracts.

#### Scenario: Reviewer inspects platform mount map

- **WHEN** reviewers inspect the CareerLoop platform mounting artifacts
- **THEN** they SHALL find mount entries for the user workbench, onboarding, today action, assessment, resume, resume diagnosis, interview, career plan, assistant, messages, subscriptions, file upload preview, employment resources, and admin console where applicable

#### Scenario: Mount entry references unknown route

- **WHEN** a platform mount entry references a route key that is not present in `careerloop-routes.json`
- **THEN** validation SHALL fail or report the mount entry as invalid before archive

### Requirement: Cosmic login context identity resolution

CareerLoop SHALL resolve production user and administrator identity from Kingdee Cosmic platform login context or an approved platform adapter. Development identity sources such as query parameters, localStorage, or manual input SHALL be marked as development-only fallback and SHALL NOT be treated as production identity.

#### Scenario: Production user opens CareerLoop

- **WHEN** a logged-in Cosmic user opens the CareerLoop webapp entry in production
- **THEN** the platform identity adapter SHALL provide the user-owned identifier required by migrated CareerLoop WebAPI calls without requiring a hardcoded `userId`

#### Scenario: No platform identity is available

- **WHEN** the webapp cannot resolve a production user identity
- **THEN** CareerLoop SHALL show an identity-required state and SHALL NOT call user-owned `/cc001/*` WebAPI with a hardcoded, guessed, or stale user identifier

#### Scenario: Administrator opens admin console

- **WHEN** a Cosmic user opens the CareerLoop admin console entry
- **THEN** the platform identity adapter SHALL resolve administrator identity and role claims before any `/cc001/admin/*` WebAPI call is attempted

### Requirement: Menu and KDDT publishing contract

CareerLoop SHALL document the menu/KDDT publishing contract needed to expose the migrated webapp in Cosmic. The contract SHALL include menu title, route target, visible audience, required role, resource path, deployment owner, validation steps, and rollback notes.

#### Scenario: User-facing menu is published

- **WHEN** the CareerLoop user menu is configured in Cosmic
- **THEN** it SHALL point to the CareerLoop webapp entry and SHALL make workbench navigation available only under the intended user audience

#### Scenario: Admin menu is published

- **WHEN** the CareerLoop admin menu is configured in Cosmic
- **THEN** it SHALL require platform administrator role or equivalent `ADMIN` authority and SHALL NOT be visible as an unrestricted user-facing menu

### Requirement: WebAPI platform call boundary

CareerLoop webapp calls to migrated `/cc001/*` WebAPI SHALL use the platform-aware API map. Each call SHALL declare method, path, request body identity fields, user/admin ownership requirement, expected DTO semantics, and fallback behavior when identity or backend service is unavailable.

#### Scenario: User-owned WebAPI call is made

- **WHEN** a user-owned CareerLoop route needs server data
- **THEN** the webapp SHALL call the mapped `/cc001/*` WebAPI only after identity is resolved and SHALL include or rely on the approved platform identity required by the backend contract

#### Scenario: Admin WebAPI call is made

- **WHEN** an admin route needs `/cc001/admin/*` data
- **THEN** the webapp SHALL require administrator identity and SHALL display forbidden or identity-required state when platform role claims are missing

#### Scenario: Backend service is unavailable

- **WHEN** a mapped WebAPI fails, times out, or returns unavailable status
- **THEN** the webapp SHALL keep the mounted entry navigable and display the route-specific fallback state defined in the route/API map

### Requirement: IPD implementation boundary

The platform mounting migration SHALL transfer IPD page semantics, route intent, identity rules, and request contracts, but SHALL NOT directly migrate IPD uni-app, Vue, axios, JWT, Spring Boot, JPA, Flyway, or Java 17 implementation details.

#### Scenario: Source evidence is reviewed

- **WHEN** reviewers inspect the change artifacts
- **THEN** they SHALL find IPD source references for `pages.json`, app startup/auth flow, request/auth utilities, user API, and admin router/API files

#### Scenario: Runtime dependency is checked

- **WHEN** CareerLoop is built or opened from CyanCruise
- **THEN** it SHALL NOT require `F:\Project\IPD` frontend, admin-frontend, backend, or package dependencies at runtime

### Requirement: Deployment verification checklist

The migration SHALL include a deployment verification checklist for Cosmic platform mounting. The checklist SHALL cover static webapp resources, platform mount map, route/API validation, identity fallback behavior, admin visibility, OpenSpec validation, and JDK 8 Gradle build.

#### Scenario: Implementation is ready for archive

- **WHEN** the implementation is complete
- **THEN** verification SHALL include strict OpenSpec validation, webapp route/mount validation, JavaScript syntax validation where applicable, JDK 8 `.\gradlew.bat clean build`, and migration map updates

#### Scenario: Platform configuration cannot be fully exercised locally

- **WHEN** local verification cannot access a real Cosmic tenant menu or login context
- **THEN** the verification record SHALL distinguish automated local checks from manual tenant deployment checks and SHALL document the remaining platform items

### Requirement: Platform mount backend identity adapter

CareerLoop platform mounting SHALL be backed by a server-side identity adapter contract. The webapp mount identity metadata SHALL align with the backend identity context so that production menu calls rely on Cosmic platform identity instead of development fallback or hardcoded identifiers.

#### Scenario: Mounted user route calls backend

- **WHEN** a user-facing mounted route calls a user-owned `/cc001/*` WebAPI in production
- **THEN** the backend identity adapter SHALL resolve the platform user and SHALL reject missing or mismatched identity according to the identity context contract

#### Scenario: Mounted admin route calls backend

- **WHEN** the admin console mount calls `/cc001/admin/*` in production
- **THEN** the backend identity adapter SHALL resolve administrator identity and role before management service execution

