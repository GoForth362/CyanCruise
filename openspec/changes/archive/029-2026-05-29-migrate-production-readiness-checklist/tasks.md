## 1. Source Evidence

- [x] 1.1 Review IPD production sources: `AI_PRODUCT_HANDOFF.md`, `application-prod.yml`, Dockerfile, `docker-compose.yml`, deploy/rollback/backup/restore scripts, health controller, alert config, and WebConfig probe exemptions.
- [x] 1.2 Map IPD production semantics to CyanCruise/Cosmic targets without migrating Spring Boot, Docker, Flyway, JPA, Java 17, nginx, Uptime Kuma, ServerChan, bash scripts, production server addresses, or secrets.

## 2. Checklist Document

- [x] 2.1 Create `docs/careerloop-production-readiness-checklist.md` with scope, source evidence, status legend, automated checks, local checks, tenant manual checks, deferred capabilities, release blockers, and rollback notes.
- [x] 2.2 Include concrete checks for OpenSpec, JDK 8 Gradle build, route validation, webapp JS syntax, identity adapter, login context provider, file adapter, AI provider, datamodel storage, notifications, admin route, health probe, monitoring, backup/restore, and rollback.
- [x] 2.3 Ensure the checklist records property names and validation intent only, with no apiKey, Authorization header, endpoint secret, customer-private value, server address, or production credential.

## 3. Metadata and Migration Map

- [x] 3.1 Update `webapp/isv/v620/careerloop/careerloop-routes.json` with secret-free readiness metadata, automated commands, manual tenant items, pending capabilities, and rollback notes.
- [x] 3.2 Update `docs/ipd-to-cyancruise-migration-map.md` with production readiness source paths, targets, migrated semantics, excluded runtime items, tenant verification, and validation commands.
- [x] 3.3 Confirm OpenSpec markdown remains Chinese-first while preserving SHALL/WHEN/THEN keywords.

## 4. Verification

- [x] 4.1 Run `node webapp\isv\v620\careerloop\validate-routes.js`.
- [x] 4.2 Run `node --check webapp\isv\v620\careerloop\assets\app.js`.
- [x] 4.3 Run `openspec validate migrate-production-readiness-checklist --strict`.
- [x] 4.4 Run `openspec validate --all --strict`.
- [x] 4.5 Run JDK 8 `.\gradlew.bat clean build`.

## 5. Finalization

- [x] 5.1 Archive the OpenSpec change after implementation is complete.
- [x] 5.2 Commit the implementation, specs, archive, checklist, route metadata, and migration-map changes.
- [x] 5.3 Push `codex/migrate-production-readiness-checklist`.
