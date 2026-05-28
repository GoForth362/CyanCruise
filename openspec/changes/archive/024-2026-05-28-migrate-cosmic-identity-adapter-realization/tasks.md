## 1. Adapter Configuration And Provider

- [x] 1.1 Add JDK 8 compatible adapter configuration for enablement, field candidates, admin aliases, and diagnostics.
- [x] 1.2 Add a platform context provider boundary that returns a safe map/object representation without depending on unknown Cosmic runtime classes.
- [x] 1.3 Add parser utilities for field candidate lookup, string/list/array role extraction, and safe diagnostic messages.

## 2. Resolver Factory And Adapter

- [x] 2.1 Implement configurable Cosmic identity resolver using provider + adapter config.
- [x] 2.2 Implement resolver factory/selector so default production remains unavailable unless explicitly enabled.
- [x] 2.3 Preserve explicit development resolver isolation and ensure factory does not return development fallback for production.

## 3. Tests

- [x] 3.1 Add focused tests for disabled adapter, missing context, userId/personId/operatorId candidate resolution, orgId, ip, userAgent, and diagnostics.
- [x] 3.2 Add role tests for collection, array, comma/semicolon text, default admin aliases, and configured tenant admin aliases.
- [x] 3.3 Add WebAPI boundary integration tests showing factory-enabled adapter authorizes matching user/admin calls and rejects disabled/missing/mismatched identity.

## 4. Documentation And Maps

- [x] 4.1 Update `careerloop-routes.json` identity metadata with adapter enablement property, default candidate fields, and disabled behavior.
- [x] 4.2 Update `docs/careerloop-cosmic-platform-mounting.md` with adapter configuration, tenant validation, diagnostics, and rollback.
- [x] 4.3 Update `docs/ipd-to-cyancruise-migration-map.md` with IPD sources, CyanCruise targets, adapter mapping, exclusions, and verification results.

## 5. Validation

- [x] 5.1 Run focused adapter/helper/WebAPI tests.
- [x] 5.2 Run `node webapp\isv\v620\careerloop\validate-routes.js` if route metadata changes.
- [x] 5.3 Run `openspec validate migrate-cosmic-identity-adapter-realization --strict`.
- [x] 5.4 Run `openspec validate --all --strict`.
- [x] 5.5 Set JDK 8 and run `.\gradlew.bat clean build`.

## 6. Archive Readiness

- [x] 6.1 Verify working tree scope is limited to identity adapter realization artifacts and implementation.
- [x] 6.2 Archive the change after implementation review, commit locally, and push `codex/migrate-cosmic-identity-adapter-realization`.
