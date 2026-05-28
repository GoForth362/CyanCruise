## 1. Context and Source Review

- [x] 1.1 Review IPD identity sources `frontend/src/utils/auth.ts`, `utils/request.ts`, `api/user.ts`, `App.vue`, `admin-frontend/src/api/index.ts`, and backend auth/admin semantics; record user/admin/role/401 rules in migration map.
- [x] 1.2 Review existing CyanCruise identity DTO/helper/resolver/provider/factory/WebAPI boundary and representative tests before editing.
- [x] 1.3 Confirm no IPD JWT, Spring Security, uni-app storage, axios, Java 17, JPA/Flyway, or tenant secret dependency is introduced.

## 2. Provider Contracts and Configuration

- [x] 2.1 Add a JDK 8 compatible Cosmic login context provider/bridge contract that returns `Map<String,Object>` for current platform identity candidates.
- [x] 2.2 Add provider configuration or factory selection for enablement, bridge class/provider name, diagnostics, and safe default unavailable behavior.
- [x] 2.3 Add safe diagnostic handling that distinguishes disabled provider, missing bridge, empty context, provider exception, and missing identity fields without dumping raw context or credentials.
- [x] 2.4 Add role object extraction support for common platform role map/object shapes while preserving existing collection/array/string role parsing in `ConfigurableCosmicIdentityResolver`.

## 3. Factory and Boundary Integration

- [x] 3.1 Wire `CareerLoopIdentityResolverFactory.production()` to use the login context provider factory when `cc001.identity.adapter.enabled=true`.
- [x] 3.2 Preserve adapter-disabled behavior as `UnavailableCosmicIdentityResolver` and ensure production never falls back to development identity.
- [x] 3.3 Keep explicit request body userId/adminId consistency checks in `IdentityAwareCareerLoopWebApiBoundary` unchanged and covered by tests.
- [x] 3.4 Update route metadata or platform mounting docs only if provider enablement or tenant validation fields need to be surfaced.

## 4. Tests and Documentation

- [x] 4.1 Add focused provider/resolver tests for bridge success, bridge unavailable, bridge exception, empty context, role object extraction, diagnostics sanitization, and adapter disabled behavior.
- [x] 4.2 Add or update representative WebAPI boundary tests for current-user resolution, request body mismatch rejection, admin role authorization, and development fallback isolation.
- [x] 4.3 Update `docs/ipd-to-cyancruise-migration-map.md` with change id, branch, IPD source paths, target modules, data mapping, temporarily excluded items, tenant validation, rollback, and verification commands.
- [x] 4.4 Keep the previous file-service archive directory correction from `2026-05-28-migrate-cosmic-file-service-adapter` to `025-2026-05-28-migrate-cosmic-file-service-adapter` in this change's final commit.

## 5. Verification and Finalization

- [x] 5.1 Run focused Gradle tests covering identity provider/resolver/WebAPI boundary behavior.
- [x] 5.2 Run `node webapp\isv\v620\careerloop\validate-routes.js` if route metadata changes.
- [x] 5.3 Run `openspec validate migrate-cosmic-login-context-provider --strict`.
- [x] 5.4 Run `openspec validate --all --strict`.
- [x] 5.5 Run JDK 8 `.\gradlew.bat clean build`.
- [x] 5.6 Archive the change, confirm archive numbering continues after `025`, commit all staged migration work, and push `codex/migrate-cosmic-login-context-provider`.
