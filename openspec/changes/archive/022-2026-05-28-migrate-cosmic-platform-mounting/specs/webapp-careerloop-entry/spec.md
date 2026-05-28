## ADDED Requirements

### Requirement: Platform-mounted entry identity mode

The CareerLoop webapp entry SHALL distinguish production Cosmic identity mode from development fallback identity mode. Production mode SHALL require platform login context for user-owned or admin-owned calls, while development fallback mode MAY use query, localStorage, or manual input only when explicitly marked as non-production.

#### Scenario: Entry runs in production identity mode

- **WHEN** the CareerLoop webapp entry is opened from a Cosmic menu or KDDT-mounted entry
- **THEN** identity resolution SHALL prefer the Cosmic platform context and SHALL NOT silently fall back to query/localStorage as production identity

#### Scenario: Entry runs in development fallback mode

- **WHEN** developers open the static CareerLoop entry outside a real Cosmic login context
- **THEN** query, localStorage, or manual `userId` fallback SHALL remain available for validation and SHALL be visibly treated as a development fallback before user-owned WebAPI calls

### Requirement: Platform mount metadata for existing routes

The CareerLoop route/API map SHALL expose enough platform mount metadata for each existing route to be reviewed before menu publication. Metadata SHALL include publishability, audience, role requirement, target route, and fallback behavior.

#### Scenario: Existing route is prepared for menu review

- **WHEN** reviewers inspect any existing route in `careerloop-routes.json`
- **THEN** they SHALL be able to determine whether the route is user-facing, admin-only, entry-only, hidden, or pending platform configuration

#### Scenario: Admin route is reviewed

- **WHEN** reviewers inspect the admin console route
- **THEN** the route metadata SHALL show administrator identity and role requirements before any admin menu publication
