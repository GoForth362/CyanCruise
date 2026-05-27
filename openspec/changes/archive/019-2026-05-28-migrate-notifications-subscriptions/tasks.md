## 1. Migration Context

- [x] 1.1 Review IPD notification/subscription sources listed in proposal.md and confirm migrated semantics: best-effort notification push, user-owned list/unread/read/delete, type grouping, subscription quota, external send skip rules, and weekly report delivery.
- [x] 1.2 Confirm CyanCruise DTO/helper/app01/WebAPI package patterns before editing code.
- [x] 1.3 Keep Spring Boot, JPA, Flyway, Redis, Java 17 HTTP, WeChat network API implementation, Spring `@Scheduled`, Vue, uni-app, Pinia/store, and mini-program runtime code out of scope.

## 2. Common DTO And Helper Rules

- [x] 2.1 Add JDK 8 compatible notification DTOs and constants in `code/base/v620-cc001-base-common`, including notification record, unread count, operation result, type constants, subscription quota, grant request/result, send result, and weekly report summary.
- [x] 2.2 Add helper rules in `code/base/v620-cc001-base-helper` for notification type grouping, unread aggregation, ownership mutation decisions, deep-link fallback, subscription grant quota changes, send skip decisions, and weekly report fallback summaries.
- [x] 2.3 Add focused helper tests for known/unknown type grouping, unread counts, ownership rejection, grant accept/reject behavior, send skip cases, and weekly report fallback summary.

## 3. Application Service And WebAPI

- [x] 3.1 Add app01 storage boundaries for notifications and subscription quota with in-memory or existing platform-compatible adapters suitable for tests.
- [x] 3.2 Add notification application service for best-effort push, list, unread count, mark read, mark all read, and delete with user ownership validation.
- [x] 3.3 Add subscription application service for grant recording, quota query, safe send decision, and unavailable external provider adapter.
- [x] 3.4 Add weekly report service boundary that can build a fallback summary, create a `WEEKLY_REPORT` notification, and attempt optional subscription dispatch best-effort.
- [x] 3.5 Add Cosmic WebAPI contracts for notification list/unread/read-all/read/delete, subscription grant/quota, and weekly report trigger or preview under `/cc001/*` style paths.
- [x] 3.6 Add focused service/WebAPI tests for successful notification flow, missing identity, ownership rejection, push best-effort failure, subscription quota changes, provider unavailable skip, and weekly report delivery/skip.

## 4. Webapp Contract Mapping

- [x] 4.1 Update `webapp/isv/v620/careerloop/careerloop-routes.json` or equivalent route/API map with messages, notifications, subscription grant/quota, and weekly report entry contracts.
- [x] 4.2 Add or update static webapp entry assets only as needed to expose the message center entry without introducing Vue/uni-app runtime dependencies.
- [x] 4.3 Add static checks or lightweight validation that route/API map references the new notification/subscription contracts and keeps existing CareerLoop workbench contracts unchanged.

## 5. Documentation And Verification

- [x] 5.1 Update `docs/ipd-to-cyancruise-migration-map.md` with IPD source paths, CyanCruise target modules, data mapping, temporarily excluded items, and validation approach for `migrate-notifications-subscriptions`.
- [x] 5.2 Run `openspec validate migrate-notifications-subscriptions --strict`.
- [x] 5.3 Run `openspec validate --all --strict`.
- [x] 5.4 Set JDK 8 environment and run `.\gradlew.bat clean build`.
- [x] 5.5 After implementation review passes, sync specs, archive with the next numbered archive folder, commit, and push the migration branch.
