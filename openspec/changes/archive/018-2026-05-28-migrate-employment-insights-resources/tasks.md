## 1. Migration Context

- [x] 1.1 Review IPD sources listed in proposal.md and confirm the migrated business semantics: school/major/target-role matching, source-backed metrics, coverage audit, resource cards, and fallback states.
- [x] 1.2 Confirm CyanCruise target packages and existing CareerLoop DTO/WebAPI naming patterns before editing code.
- [x] 1.3 Keep Spring Boot, JPA, Flyway, Java 17 HTTP, PDFBox, Redis, Bilibili crawler, Vue, uni-app, Pinia/store, and mini-program runtime implementation out of scope.

## 2. Common DTO And Helper Rules

- [x] 2.1 Add JDK 8 compatible employment insight DTOs in `code/base/v620-cc001-base-common`, including school, major, targetRole, matchLabel, summary, metrics, trend, coverage, and source items.
- [x] 2.2 Add JDK 8 compatible CareerLoop resource DTOs for article, video, consultation/tip, career path, feed response, and unavailable/empty state.
- [x] 2.3 Add helper rules in `code/base/v620-cc001-base-helper` for supported school constants, alias normalization, source scoring, trend aggregation, coverage audit, highlight extraction, and safe summary fallback.
- [x] 2.4 Add focused helper tests for supported school matching, unsupported/missing school fallback, metric absence, coverage statuses, and resource feed empty state.

## 3. Application Service And WebAPI

- [x] 3.1 Add app01 storage boundaries for employment records and resource cards with in-memory or existing platform-compatible adapters suitable for tests.
- [x] 3.2 Add employment insight application service that resolves user profile context, reads records through the storage boundary, applies helper rules, and returns source-traceable insight DTOs.
- [x] 3.3 Add resource feed application service that returns configured resource cards and optional stable per-user ordering without requiring external crawlers.
- [x] 3.4 Add Cosmic WebAPI contracts for employment insight and resource feed under `/cc001/*` style naming, including explicit user identity semantics and recoverable unavailable states.
- [x] 3.5 Add focused service/WebAPI tests for successful insight, missing identity, unsupported school, no metrics, no resources, and source traceability.

## 4. Webapp Contract Mapping

- [x] 4.1 Update `webapp/isv/v620/careerloop/careerloop-routes.json` or equivalent route/API map with employment insight and resource entry routes, consumed WebAPI paths, DTO fields, and fallback states.
- [x] 4.2 Add or update static webapp entry assets only as needed to expose the new employment insight/resource entry points without introducing Vue/uni-app runtime dependencies.
- [x] 4.3 Add static checks or lightweight validation that the route/API map references the new contracts and keeps existing CareerLoop workbench contracts unchanged.

## 5. Documentation And Verification

- [x] 5.1 Update `docs/ipd-to-cyancruise-migration-map.md` with IPD source paths, CyanCruise target modules, data mapping, temporarily excluded items, and validation approach for `migrate-employment-insights-resources`.
- [x] 5.2 Run `openspec validate migrate-employment-insights-resources --strict`.
- [x] 5.3 Run `openspec validate --all --strict`.
- [x] 5.4 Set JDK 8 environment and run `.\gradlew.bat clean build`.
- [x] 5.5 Record verification results, then wait for archive/commit/push instructions after implementation review is complete.
