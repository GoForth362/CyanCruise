## 1. Shared Identity Contract

- [x] 1.1 Add JDK 8 compatible identity DTO/status/source constants in `base-common`.
- [x] 1.2 Add identity helper rules in `base-helper` for role normalization, admin detection, development fallback marking, and explicit ID consistency.
- [x] 1.3 Add focused helper tests for missing identity, development fallback, matching userId, mismatched userId, admin role, and forbidden role cases.

## 2. Cloud01 Identity Resolver

- [x] 2.1 Add a `CareerLoopIdentityResolver` boundary and default unavailable production adapter in `cloud01-app01`.
- [x] 2.2 Add a development/test adapter that explicitly marks `environment=development` and never reports itself as production Cosmic identity.
- [x] 2.3 Add WebAPI boundary helper methods for requiring current user, validating explicit userId, requiring admin, and detecting identity mismatch.

## 3. Representative WebAPI Integration

- [x] 3.1 Apply identity boundary to representative user-owned endpoints: profile snapshot, onboarding save, today action, and resume list/create or equivalent current-user flow.
- [x] 3.2 Apply identity boundary to representative admin endpoints: admin whoami and at least one mutating admin governance operation.
- [x] 3.3 Preserve existing explicit `userId/adminId` application service methods and tests unless a focused WebAPI boundary test proves a safer wrapper is needed.
- [x] 3.4 Add tests proving identity-required, forbidden, and identity-mismatch responses do not invoke protected services.

## 4. Webapp And Documentation

- [x] 4.1 Update `careerloop-routes.json` identity metadata to reference the backend identity context adapter and mismatch semantics.
- [x] 4.2 Update `docs/careerloop-cosmic-platform-mounting.md` with backend identity adapter behavior and tenant manual checks.
- [x] 4.3 Update `docs/ipd-to-cyancruise-migration-map.md` with IPD sources, CyanCruise targets, identity mapping, exclusions, and verification results.

## 5. Validation

- [x] 5.1 Run focused identity helper and WebAPI/application-boundary tests.
- [x] 5.2 Run `node webapp\isv\v620\careerloop\validate-routes.js` if route metadata changes.
- [x] 5.3 Run `openspec validate migrate-cosmic-identity-context --strict`.
- [x] 5.4 Run `openspec validate --all --strict`.
- [x] 5.5 Set JDK 8 and run `.\gradlew.bat clean build`.

## 6. Archive Readiness

- [x] 6.1 Verify working tree scope is limited to identity context migration artifacts and implementation.
- [x] 6.2 Archive the change after implementation review, commit locally, and push `codex/migrate-cosmic-identity-context`.
