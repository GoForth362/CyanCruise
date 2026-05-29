## 1. Route And Page Contract

- [x] 1.1 Review `careerloop-routes.json` against IPD `pages.json` and page/API source paths, then confirm the route keys included in this change.
- [x] 1.2 Add page visibility/status metadata needed by the multi-page webapp shell without changing existing `/cc001/*` WebAPI contracts.
- [x] 1.3 Extend route validation so page route keys, platform mount route keys, WebAPI paths, identity requirements, and page visibility metadata stay consistent.

## 2. Static Webapp Page Shell

- [x] 2.1 Update `index.html` to include the multi-page navigation, route status area, page container, and reusable panel regions.
- [x] 2.2 Update `assets/styles.css` with responsive layout constraints for navigation, headers, forms, lists, status chips, action buttons, and fallback states.
- [x] 2.3 Update `assets/app.js` with a page registry and hash navigation for workbench, onboarding, today action, assessment, resume, file upload/preview, resume diagnosis, career plan, interview, assistant, messages, employment insight, and resources.
- [x] 2.4 Ensure unknown, hidden, pending, entry-only, identity-required, forbidden, empty, unavailable, and backend-error states render without blocking navigation.

## 3. Page Data Binding

- [x] 3.1 Bind workbench, onboarding, and today action pages to existing profile/onboarding/today WebAPI helpers.
- [x] 3.2 Bind assessment, resume, file upload/preview, resume diagnosis, career plan, and interview pages to existing route-mapped WebAPI helpers or safe entry-only fallbacks.
- [x] 3.3 Bind assistant, messages, employment insight, and resources pages to existing route-mapped WebAPI helpers or safe unavailable/empty states.
- [x] 3.4 Preserve production Cosmic identity mode and development fallback separation before any user-owned or admin-owned call.

## 4. Documentation And Migration Map

- [x] 4.1 Update `README.md` or webapp local documentation if page routes or validation steps need reviewer-facing notes.
- [x] 4.2 Update `docs/ipd-to-cyancruise-migration-map.md` with IPD sources, CyanCruise target files, route/API mapping, temporarily excluded items, validation result, branch, commit, and archive.
- [x] 4.3 Keep excluded items explicit: IPD Vue/uni-app runtime, Pinia/store, Vite/uView, WeChat runtime, true AI provider, external content crawling, voice/digital-human interview, full admin pages, and production datamodel adapters.

## 5. Verification

- [x] 5.1 Run `node webapp\isv\v620\careerloop\validate-routes.js`.
- [x] 5.2 Run `node --check webapp\isv\v620\careerloop\assets\app.js`.
- [x] 5.3 Run `openspec validate migrate-webapp-careerloop-pages --strict`.
- [x] 5.4 Run `openspec validate --all --strict`.
- [x] 5.5 Run JDK 8 `.\gradlew.bat clean build`.
- [x] 5.6 Archive the completed change with a numeric archive prefix, then commit and push `codex/migrate-webapp-careerloop-pages`.
