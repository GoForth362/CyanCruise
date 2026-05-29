## ADDED Requirements

### Requirement: Entry navigation to multi-page route states

The CareerLoop webapp entry SHALL navigate from the existing workbench into multi-page route states for the migrated main-loop capabilities. Navigation SHALL use stable route keys from `careerloop-routes.json` and SHALL keep the workbench, onboarding, today action, assessment, resume, file upload/preview, resume diagnosis, career plan, interview, assistant, messages, employment insight, and career resources reachable when their route metadata marks them visible.

#### Scenario: Workbench action opens page state
- **WHEN** a user activates a visible workbench action or navigation item
- **THEN** the entry SHALL update the hash to the mapped route key and render that page state without requiring a full page reload

#### Scenario: Route metadata hides a capability
- **WHEN** a capability is marked hidden, pending, admin-only, or otherwise not user-facing in `careerloop-routes.json`
- **THEN** the entry SHALL not expose it as a normal user action, and SHALL show an explicit unavailable or forbidden state if the hash is opened directly

### Requirement: Route/API map consistency for page states

The CareerLoop entry SHALL keep page navigation, platform mount metadata, and WebAPI consumption consistent with `careerloop-routes.json`. Each rendered page state SHALL have a route-map entry that declares IPD source semantics, target hash, status, publishability or platform mount metadata, WebAPI contracts, identity requirements, and fallback behavior.

#### Scenario: Page state is reviewed
- **WHEN** reviewers inspect any rendered CareerLoop page state
- **THEN** they SHALL be able to trace it to a `careerloop-routes.json` route entry and to the relevant IPD source paths in OpenSpec or migration documentation

#### Scenario: Route validation runs
- **WHEN** `node webapp\isv\v620\careerloop\validate-routes.js` is executed
- **THEN** validation SHALL confirm that rendered route keys, platform mount route keys, WebAPI paths, identity metadata, and page visibility metadata are internally consistent
