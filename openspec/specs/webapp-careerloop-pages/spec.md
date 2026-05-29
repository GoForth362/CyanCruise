# webapp-careerloop-pages Specification

## Purpose
TBD - created by archiving change migrate-webapp-careerloop-pages. Update Purpose after archive.
## Requirements
### Requirement: CareerLoop multi-page webapp shell

CyanCruise SHALL provide a CareerLoop webapp page shell under `webapp/isv/v620/careerloop` that exposes migrated IPD page semantics as Cosmic-compatible hash route states. The shell SHALL cover workbench, onboarding, today action, assessment, resume, file upload/preview, resume diagnosis, career plan, interview, assistant, messages, employment insight, and career resources.

#### Scenario: User opens a supported page route
- **WHEN** a user opens `index.html#<route-key>` for a supported CareerLoop route
- **THEN** the webapp SHALL render the matching page state with title, route status, primary data panels, available actions, and route-specific fallback text

#### Scenario: User opens an unknown route
- **WHEN** a user opens an unknown CareerLoop hash route
- **THEN** the webapp SHALL return to the workbench or show a recoverable not-found state without calling unrelated `/cc001/*` WebAPI

### Requirement: Page data binding to migrated WebAPI contracts

Each CareerLoop page SHALL consume only existing migrated `/cc001/*` WebAPI contracts declared in `careerloop-routes.json`. Page requests SHALL respect user/admin identity requirements and SHALL render loading, empty, success, forbidden, identity-required, unavailable, and backend-error states independently per panel.

#### Scenario: Page has resolvable production identity
- **WHEN** a production Cosmic user opens a user-owned page with resolved platform identity
- **THEN** the page SHALL call only the route-mapped user-owned WebAPI contracts and SHALL display returned DTO semantics without requiring IPD runtime code

#### Scenario: Page lacks required identity
- **WHEN** a user-owned or admin-owned page cannot resolve the required identity
- **THEN** the page SHALL display an identity-required or forbidden state and SHALL NOT call protected WebAPI with a hardcoded, guessed, or stale identifier

#### Scenario: One panel call fails
- **WHEN** one route-mapped WebAPI call fails while other page data remains available
- **THEN** the page SHALL keep navigation and successful panels usable while showing a recoverable fallback state for the failed panel

### Requirement: IPD page semantics without IPD frontend runtime

The page migration SHALL preserve IPD business page semantics and data meaning while rebuilding the implementation as CyanCruise static webapp resources. The implementation SHALL NOT directly migrate or require IPD Vue, uni-app, Pinia/store, Vite, uView, mini-program lifecycle, axios interceptors, Spring Boot, JPA, Flyway, or Java 17 runtime code.

#### Scenario: Implementation dependencies are reviewed
- **WHEN** reviewers inspect the webapp files and build configuration
- **THEN** the change SHALL reside in CyanCruise target files and SHALL NOT introduce IPD frontend runtime dependencies or a new frontend build chain

#### Scenario: Source mapping is reviewed
- **WHEN** reviewers inspect OpenSpec and migration map artifacts
- **THEN** they SHALL find IPD source paths, target CyanCruise files, data/API mappings, temporarily excluded items, and validation commands for the page migration

### Requirement: Main-loop page workflow continuity

The webapp pages SHALL keep the CareerLoop main loop navigable across target role/profile, assessment, resume, resume diagnosis, today action, interview, feedback-oriented messages, assistant guidance, employment insight, and career plan. Pages MAY show entry-only or pending states for capabilities whose production adapter is outside this change, but SHALL preserve the user's next available action.

#### Scenario: User moves from workbench to a page
- **WHEN** a user selects a main-loop action from the workbench or navigation
- **THEN** the webapp SHALL update the hash route, render the target page, and keep a visible path back to the workbench

#### Scenario: Capability is entry-only or pending
- **WHEN** a route is marked entry-only or depends on a later platform adapter
- **THEN** the page SHALL show what is available now, what is pending, and a safe fallback action without implying the full IPD capability is production-ready

### Requirement: Responsive and inspectable page layout

CareerLoop webapp pages SHALL use responsive, inspectable layouts suitable for Cosmic desktop and webview usage. Navigation, page headers, status chips, forms, lists, tool buttons, and cards SHALL have stable dimensions or responsive constraints so labels and controls do not overlap on common desktop and mobile widths.

#### Scenario: Layout is checked on desktop and mobile
- **WHEN** reviewers open representative CareerLoop pages on common desktop and mobile viewports
- **THEN** navigation, page content, actions, status text, and fallback messages SHALL remain readable and non-overlapping

#### Scenario: Static assets are inspected
- **WHEN** reviewers inspect static page assets
- **THEN** visible assets and controls SHALL represent actual CareerLoop workflow states rather than unrelated decoration

### Requirement: Webapp page verification

The page migration SHALL include verification that the page shell, route/API metadata, JavaScript syntax, OpenSpec artifacts, migration map, and JDK 8 build remain valid before archive.

#### Scenario: Change is verified
- **WHEN** implementation is complete
- **THEN** verification SHALL include `node webapp\isv\v620\careerloop\validate-routes.js`, `node --check webapp\isv\v620\careerloop\assets\app.js`, `openspec validate migrate-webapp-careerloop-pages --strict`, `openspec validate --all --strict`, JDK 8 `.\gradlew.bat clean build`, and migration map updates

#### Scenario: Change is archived
- **WHEN** the change is archived after implementation
- **THEN** the archive folder SHALL keep the numeric archive prefix and the migration map SHALL record the final branch, commit, validation result, and temporarily excluded platform items

