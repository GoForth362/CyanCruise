## 1. Platform Mount Map

- [x] 1.1 Review existing `webapp/isv/v620/careerloop/careerloop-routes.json` route keys, identity blocks, WebAPI entries, and pending capabilities.
- [x] 1.2 Add or extend a CareerLoop platform mount map for user-facing, entry-only, pending, and admin-only routes.
- [x] 1.3 Record IPD source paths for page/menu/auth/request semantics without depending on IPD files at runtime.
- [x] 1.4 Ensure mount entries include title, route target, audience, required role, publishability, fallback, and deployment notes.

## 2. Identity And API Boundary

- [x] 2.1 Define production Cosmic identity mode and development fallback identity mode in the webapp contract.
- [x] 2.2 Update static webapp identity handling so production mode does not silently use query/localStorage/manual `userId` as production identity.
- [x] 2.3 Define administrator identity and role requirements before admin console WebAPI calls.
- [x] 2.4 Keep user-owned and admin-owned WebAPI calls aligned with the route/API map fallback behavior.

## 3. Menu And Deployment Documentation

- [x] 3.1 Add platform mounting documentation for Cosmic webapp resource path, menu/KDDT target, audience, role requirement, owner, and rollback.
- [x] 3.2 Document manual tenant checks for Cosmic login context, user menu visibility, admin menu visibility, and `/cc001/*` WebAPI reachability.
- [x] 3.3 Document temporarily excluded items: IPD uni-app/Vue router/tabBar, axios/JWT login, Spring Boot auth, production SSO/RBAC, and customer-environment KDDT scripts.

## 4. Validation

- [x] 4.1 Extend or add webapp validation so platform mount entries reference known route keys and declare required identity metadata.
- [x] 4.2 Run `node webapp\isv\v620\careerloop\validate-routes.js`.
- [x] 4.3 Run `node --check webapp\isv\v620\careerloop\assets\app.js` if the static app script changes.
- [x] 4.4 Run `openspec validate migrate-cosmic-platform-mounting --strict`.
- [x] 4.5 Run `openspec validate --all --strict`.
- [x] 4.6 Set JDK 8 and run `.\gradlew.bat clean build`.

## 5. Migration Map And Archive Readiness

- [x] 5.1 Update `docs/ipd-to-cyancruise-migration-map.md` with IPD sources, CyanCruise targets, platform identity/menu/API mapping, exclusions, and verification results.
- [x] 5.2 Verify the working tree only contains this change's scoped artifacts before archive.
- [x] 5.3 Archive the change after implementation review, commit locally, and push `codex/migrate-cosmic-platform-mounting`.
