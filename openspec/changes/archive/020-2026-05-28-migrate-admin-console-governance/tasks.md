## 1. Migration Context

- [x] 1.1 Review IPD admin sources listed in proposal.md and confirm migrated semantics: admin role gate, organization/student dashboard, user governance, skill map maintenance, question moderation, content management, broadcast, analytics, audit logging, and question contribution safety.
- [x] 1.2 Confirm CyanCruise DTO/helper/app01/WebAPI package patterns before editing code.
- [x] 1.3 Keep Spring Boot, JPA, Flyway, AOP runtime, Vue, Element Plus, Vite, Pinia, old JWT implementation, Java 17 APIs, and IPD admin-frontend runtime code out of scope.

## 2. Common DTO And Helper Rules

- [x] 2.1 Add JDK 8 compatible admin DTOs and constants in `code/base/v620-cc001-base-common`, including admin identity, organization, student row/detail, dashboard summary, user governance, skill map, question moderation, content item, broadcast request/result, analytics summary, audit log, page request/result, and audit action constants.
- [x] 2.2 Add helper rules in `code/base/v620-cc001-base-helper` for admin authorization decisions, bounded pagination, ban/unban validation, broadcast target resolution, question review status transitions, content pin/hide toggles, local content safety matching, radar aggregation, weak dimension ranking, audit action construction, and audit snapshot masking.
- [x] 2.3 Add focused helper tests for identity missing/forbidden/admin, page size bounds, malformed radar JSON skip, weak top 3 ordering, ban reason fallback, broadcast validation, question approve/reject transitions, unsafe contribution rejection, content toggle behavior, and sensitive audit field masking.

## 3. Application Service And WebAPI

- [x] 3.1 Add app01 storage boundaries for admin roles, organizations, users, interviews/report summaries, career paths/nodes, questions, content items, usage events, and audit logs, using existing adapters where available.
- [x] 3.2 Add admin governance application service for whoami, organization list/save, organization dashboard, organization students, student detail, user list/detail, ban, unban, skill map list/save/delete, question list/update/approve/reject/delete, content list/create/update/delete/pin/hide, broadcast, analytics summary, and audit log list.
- [x] 3.3 Reuse notification/subscription capability for admin broadcast and ban/unban notifications with best-effort delivery results.
- [x] 3.4 Add question contribution service boundary or adapter updates as needed to preserve content safety, contributor hash, difficulty normalization, and public market semantics without IPD runtime dependencies.
- [x] 3.5 Add Cosmic WebAPI contracts under `/cc001/admin/*` or equivalent paths for all admin governance operations.
- [x] 3.6 Add focused service/WebAPI tests for successful admin flow, missing identity, forbidden identity, user ban/unban, broadcast partial failure, ownership/scoping of organization students, question moderation, content toggles, analytics unknown event handling, audit log creation, and audit write failure observability.

## 4. Webapp Or Platform Contract Mapping

- [x] 4.1 Update `webapp/isv/v620/careerloop/careerloop-routes.json` or equivalent route/API map with admin governance route keys, WebAPI paths, identity/authorization requirements, and unavailable fallback states.
- [x] 4.2 Add or update static webapp/admin entry assets only as needed to expose the management entry or Cosmic platform mounting contract without introducing Vue/Element Plus/Vite/Pinia dependencies.
- [x] 4.3 Add static checks or lightweight validation that route/API map references the new admin contracts and keeps existing user-facing CareerLoop routes unchanged.

## 5. Documentation And Verification

- [x] 5.1 Update `docs/ipd-to-cyancruise-migration-map.md` with IPD source paths, CyanCruise target modules, data mapping, temporarily excluded items, and validation approach for `migrate-admin-console-governance`.
- [x] 5.2 Run `openspec validate migrate-admin-console-governance --strict`.
- [x] 5.3 Run `openspec validate --all --strict`.
- [x] 5.4 Set JDK 8 environment and run `.\gradlew.bat clean build`.
- [x] 5.5 After implementation review passes, sync specs, archive with the next numbered archive folder, commit, and push the migration branch.
