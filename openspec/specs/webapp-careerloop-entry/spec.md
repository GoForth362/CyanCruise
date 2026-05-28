## Purpose

定义 CyanCruise 在苍穹 webapp 资源侧如何承载 CareerLoop 首个可用入口，包括工作台、onboarding gate、主循环导航、页面到 Cosmic WebAPI 的契约映射、状态降级、迁移边界和验证要求。
## Requirements
### Requirement: CareerLoop webapp workbench entry

CyanCruise SHALL provide a CareerLoop webapp entry that makes the migrated job-preparation loop usable from `webapp/isv/v620/`. The entry SHALL present the user's target role/profile status, today's recommended action, and direct action entries for onboarding, assessment, resume, resume diagnosis, interview, career plan, and assistant chat.

#### Scenario: Existing user opens the workbench

- **WHEN** a user with a resolvable `userId` opens the CareerLoop webapp entry
- **THEN** the workbench SHALL request the migrated profile and today-action contracts and display the target role, readiness signals, today's next action, and available CareerLoop action entries

#### Scenario: Missing user id opens the workbench

- **WHEN** the webapp cannot resolve a `userId`
- **THEN** the entry SHALL show an explicit identity-required state and SHALL NOT call user-owned CareerLoop WebAPI with a hardcoded production user

### Requirement: Onboarding gate

The CareerLoop webapp entry SHALL gate new or incomplete users through onboarding semantics migrated from IPD. The gate SHALL collect or request the minimum CareerLoop inputs needed by existing backend contracts: identity type, target role or intended direction, resume ownership state, and optional preference signals.

#### Scenario: Profile is incomplete

- **WHEN** the profile snapshot has no onboarding block or has no usable target role/preference signal
- **THEN** the webapp SHALL guide the user to onboarding before presenting the workbench as complete

#### Scenario: User submits onboarding

- **WHEN** a user submits onboarding information from the webapp
- **THEN** the webapp SHALL call the migrated career-profile onboarding contract and refresh the workbench state from the returned profile snapshot

### Requirement: Route and API contract map

The migration SHALL define a route and API contract map for the CareerLoop webapp entry. The map SHALL connect IPD page semantics to CyanCruise webapp route keys and SHALL list the existing Cosmic WebAPI contracts consumed by each route.

#### Scenario: Route map is reviewed

- **WHEN** reviewers inspect the webapp migration artifacts
- **THEN** they SHALL find mappings for IPD home, onboarding, agent/today action, assessment, resume, resume diagnosis, interview, career plan, and assistant routes

#### Scenario: Page action calls backend contract

- **WHEN** a page action needs server data
- **THEN** the route/API map SHALL identify the existing `/cc001/*` WebAPI contract, required `userId` usage, expected DTO semantics, and fallback state when the contract is unavailable

### Requirement: Progressive page states

The CareerLoop webapp SHALL define consistent page states for loading, empty data, backend error, feature unavailable, and follow-up migration pending. These states SHALL be visible in the page contract and SHALL avoid implying that unimplemented IPD capabilities are production-ready.

#### Scenario: Backend call fails

- **WHEN** a migrated WebAPI call fails or returns an unavailable response
- **THEN** the webapp SHALL keep the workbench navigable and display a recoverable error or unavailable state for the affected capability

#### Scenario: Optional capability is not migrated

- **WHEN** an IPD page capability such as notifications, CDUT content details, voice interview, file preview, or admin management is not in this change scope
- **THEN** the webapp SHALL mark it as pending or omit it from the first usable entry instead of exposing a broken action

### Requirement: Migration boundary for webapp implementation

The CareerLoop webapp migration SHALL rebuild page resources for CyanCruise and SHALL NOT directly migrate IPD Vue, uni-app, Pinia/store, Vite, uView, page lifecycle, mini-program storage, Spring Boot, JPA, Flyway, or Java 17 implementation details.

#### Scenario: Implementation is inspected

- **WHEN** implementation files are reviewed
- **THEN** they SHALL reside under CyanCruise target modules, primarily `webapp/isv/v620/`, and SHALL NOT require `F:\Project\IPD\frontend` at runtime

#### Scenario: Dependencies are checked

- **WHEN** dependency changes are reviewed
- **THEN** the change SHALL NOT introduce Vue/uni-app or other IPD frontend runtime dependencies unless a later approved change explicitly justifies a Cosmic-compatible frontend build path

### Requirement: Responsive and inspectable entry assets

The CareerLoop webapp entry SHALL use inspectable visual assets and responsive layout constraints suitable for desktop and mobile webview usage. Fixed-format elements such as navigation, action entries, status cards, and quick action controls SHALL have stable dimensions or responsive constraints so labels and controls do not overlap.

#### Scenario: Desktop and mobile layouts are checked

- **WHEN** the entry is opened in common desktop and mobile viewport sizes
- **THEN** primary actions, navigation, user state, and today's action SHALL remain readable, non-overlapping, and visually associated with the CareerLoop product

#### Scenario: Static assets are checked

- **WHEN** reviewers inspect webapp assets
- **THEN** assets SHALL represent actual CareerLoop page states or controls and SHALL NOT be only decorative placeholders unrelated to the migrated workflow

### Requirement: Verification and documentation

The webapp entry migration SHALL include verification and documentation that prove the proposed page contract is present, OpenSpec-valid, and aligned with the IPD-to-CyanCruise migration map.

#### Scenario: Change is verified before archive

- **WHEN** implementation is complete
- **THEN** verification SHALL include strict OpenSpec validation, static resource/route-map checks, JDK 8 Gradle build validation, and migration map updates before archive

#### Scenario: Migration map is updated

- **WHEN** the change is finalized
- **THEN** `docs/ipd-to-cyancruise-migration-map.md` SHALL record IPD source paths, CyanCruise webapp target paths, route/API mapping, temporarily excluded items, and validation results

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
