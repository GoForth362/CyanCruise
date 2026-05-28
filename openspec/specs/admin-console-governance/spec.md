# admin-console-governance Specification

## Purpose
TBD - created by archiving change migrate-admin-console-governance. Update Purpose after archive.
## Requirements
### Requirement: Admin identity and authorization

CyanCruise SHALL provide an admin governance boundary that resolves the caller identity and verifies an `ADMIN` equivalent role before any management operation is executed.

#### Scenario: Admin caller is authorized

- **WHEN** a caller has a resolvable user identity and an `ADMIN` equivalent role
- **THEN** the management operation SHALL proceed and include the admin identity in service context

#### Scenario: Caller identity is missing

- **WHEN** a management operation is requested without a resolvable caller identity
- **THEN** the system SHALL return an identity-required state and SHALL NOT use a hardcoded administrator

#### Scenario: Caller lacks admin role

- **WHEN** a caller has a user identity but lacks the `ADMIN` equivalent role
- **THEN** the system SHALL reject the management operation with a forbidden state

### Requirement: Organization and student dashboard

CyanCruise SHALL expose organization and student dashboard contracts for administrators, including organization records, student rows, interview counts, latest scores, radar averages, report coverage, and weakest dimensions.

#### Scenario: Admin views organization dashboard

- **WHEN** an administrator requests an organization dashboard
- **THEN** the system SHALL return student count, interview count, report count, radar dimension averages, weakest dimension top 3, and data coverage for that organization

#### Scenario: Interview report JSON is incomplete

- **WHEN** an interview report lacks radar data, contains non-numeric radar values, or cannot be parsed
- **THEN** the dashboard aggregation SHALL skip that report, keep the remaining aggregation available, and expose the skipped coverage count or reason

#### Scenario: Admin lists organization students

- **WHEN** an administrator lists students for an organization
- **THEN** the system SHALL return only students in that organization with nickname, school, major, interview count, and latest interview score if available

### Requirement: User governance

CyanCruise SHALL support administrator user search, detail, ban, and unban contracts with reason tracking, notification broadcast hooks, and audit coverage.

#### Scenario: Admin lists users

- **WHEN** an administrator lists users with page, size, and optional nickname keyword
- **THEN** the system SHALL return non-deleted users ordered newest first with bounded page size

#### Scenario: Admin bans user

- **WHEN** an administrator bans an existing user with a reason
- **THEN** the system SHALL set the user status to banned, persist the ban reason, attempt an `ADMIN_BROADCAST` or system notification best-effort, and record an audit event

#### Scenario: Admin unbans user

- **WHEN** an administrator unbans an existing user
- **THEN** the system SHALL restore the active status, clear the ban reason, attempt a restoration notification best-effort, and record an audit event

### Requirement: Skill map governance

CyanCruise SHALL provide administrator contracts for career path and career node maintenance without changing user progress semantics.

#### Scenario: Admin saves career path

- **WHEN** an administrator creates or updates a career path
- **THEN** the system SHALL validate required code/name fields, persist the path through a platform-compatible boundary, and record an audit event for state changes

#### Scenario: Admin maintains path nodes

- **WHEN** an administrator lists, creates, updates, or deletes nodes under a career path
- **THEN** the system SHALL scope nodes to the requested path, preserve sort order semantics, and record audit events for writes

### Requirement: Question bank moderation

CyanCruise SHALL expose administrator question bank moderation contracts that include hidden, AI-generated, user-contributed, and pending-review questions.

#### Scenario: Admin filters questions

- **WHEN** an administrator lists questions with optional source or reviewStatus filters
- **THEN** the system SHALL return matching questions including records that are hidden from the public market

#### Scenario: Admin updates question

- **WHEN** an administrator updates question content, summary, position, difficulty, status, reviewStatus, or answer
- **THEN** the system SHALL apply only provided fields, preserve unspecified fields, and record an audit event

#### Scenario: Admin approves AI-generated question

- **WHEN** an administrator approves a pending AI-generated question
- **THEN** the system SHALL transition reviewStatus to `PUBLISHED` and record an audit event

#### Scenario: Admin rejects question

- **WHEN** an administrator rejects a question
- **THEN** the system SHALL transition reviewStatus to `REJECTED` and record an audit event

### Requirement: Public question contribution safety

CyanCruise SHALL retain the IPD question bank contribution semantics for content length, difficulty normalization, contributor anonymization, and local content safety.

#### Scenario: User contributes safe question

- **WHEN** a signed-in user contributes a question whose content passes safety rules and minimum length
- **THEN** the system SHALL normalize blank position to `General`, normalize difficulty to `Easy`, `Normal`, or `Hard`, store an anonymized contributor hash, initialize likes and draw count, and make the question available according to its review policy

#### Scenario: User contributes unsafe question

- **WHEN** contributed question content matches local blocked patterns or fails safety validation
- **THEN** the system SHALL reject the contribution with a user-facing reason and SHALL NOT persist the question

### Requirement: Content management governance

CyanCruise SHALL support administrator management of CareerLoop home articles, videos, and resource content with create, update, delete, pin, hide, and list contracts.

#### Scenario: Admin creates article

- **WHEN** an administrator creates a home article
- **THEN** the system SHALL assign platform ownership fields as needed, default missing published time to current time, persist the article, and record an audit event

#### Scenario: Admin toggles article pin or hidden state

- **WHEN** an administrator toggles pinned or hidden state for an existing article
- **THEN** the system SHALL invert only the requested state, preserve other fields, and record an audit event

#### Scenario: Admin deletes content

- **WHEN** an administrator deletes an article or video
- **THEN** the system SHALL remove or mark the content unavailable according to the storage adapter and record an audit event

### Requirement: Admin broadcast

CyanCruise SHALL provide an administrator broadcast contract that reuses the notification capability for single-user and all-active-user announcements.

#### Scenario: Admin broadcasts to one user

- **WHEN** an administrator sends a broadcast with a target userId, title, content, and optional link
- **THEN** the system SHALL validate title/content, create an `ADMIN_BROADCAST` notification for that user best-effort, return delivery counts, and record an audit event

#### Scenario: Admin broadcasts to all active users

- **WHEN** an administrator sends a broadcast without target userId
- **THEN** the system SHALL target active non-deleted users only, attempt notification creation for each user best-effort, return success/failure/skipped counts, and record an audit event

### Requirement: Analytics summary

CyanCruise SHALL expose administrator analytics summary contracts with platform totals and recent usage event breakdowns.

#### Scenario: Admin views analytics

- **WHEN** an administrator requests analytics summary
- **THEN** the system SHALL return total users, total interviews, total assessments, total check-ins, a 30-day event breakdown, and the metric time window

#### Scenario: Unknown usage event exists

- **WHEN** analytics includes an unknown or new usage event type
- **THEN** the system SHALL include it in the event breakdown without failing the summary

### Requirement: Admin audit log

CyanCruise SHALL record and expose administrator audit logs for state-changing management operations.

#### Scenario: Audited management operation succeeds

- **WHEN** a state-changing management operation completes successfully
- **THEN** the system SHALL record adminId, action, targetType, targetId when available, beforeJson, afterJson, ip, ua, and createdAt

#### Scenario: Audit snapshot contains sensitive fields

- **WHEN** beforeJson or afterJson is built for user, auth, subscription, or external identity records
- **THEN** the snapshot SHALL omit or mask sensitive fields such as password, token, secret, openid, phone, and raw credential values

#### Scenario: Admin lists audit logs

- **WHEN** an administrator requests audit logs with pagination
- **THEN** the system SHALL return newest-first audit entries with bounded page size

### Requirement: Admin WebAPI and route mapping

The migration SHALL define Cosmic WebAPI and webapp or platform route/API mapping for admin whoami, organizations, dashboards, students, users, skill map, questions, content, broadcast, analytics, and audit logs.

#### Scenario: Route map is reviewed

- **WHEN** reviewers inspect webapp or platform migration artifacts
- **THEN** they SHALL find admin route keys, consumed WebAPI paths, DTO fields, identity requirements, authorization requirements, and fallback states

#### Scenario: Admin backend is unavailable

- **WHEN** the management WebAPI is unavailable
- **THEN** the admin entry SHALL show a recoverable unavailable state and SHALL NOT affect user-facing CareerLoop workbench routes

### Requirement: Migration boundary for admin console governance

The admin console governance migration SHALL rebuild business semantics for CyanCruise and SHALL NOT directly migrate IPD Spring Boot, JPA, Flyway, AOP runtime, Vue, Element Plus, Vite, Pinia, or old JWT implementation.

#### Scenario: Implementation is inspected

- **WHEN** implementation files are reviewed
- **THEN** they SHALL reside in CyanCruise target modules and SHALL NOT require `F:\Project\IPD` source files or IPD runtime dependencies

#### Scenario: Dependencies are checked

- **WHEN** dependency changes are reviewed
- **THEN** the change SHALL NOT introduce new external dependencies unless their necessity and Cosmic/KDDT/JDK 8 compatibility are documented

### Requirement: Verification and migration documentation

The admin console governance migration SHALL include verification and documentation that prove the proposed contracts are OpenSpec-valid and aligned with the migration map.

#### Scenario: Change is verified before archive

- **WHEN** implementation is complete
- **THEN** verification SHALL include strict OpenSpec validation, focused helper/service/WebAPI tests or equivalent static checks, route/API map checks when webapp artifacts change, JDK 8 Gradle build validation, and migration map updates

#### Scenario: Migration map is updated

- **WHEN** the change is finalized
- **THEN** `docs/ipd-to-cyancruise-migration-map.md` SHALL record IPD source paths, CyanCruise target modules, data mapping, temporarily excluded items, and validation results

### Requirement: Admin governance uses Cosmic identity context

Admin governance SHALL use the shared Cosmic identity context boundary for administrator identity and authorization. Admin operations SHALL NOT rely solely on request body `adminId`, legacy admin token, or hardcoded administrator identifiers.

#### Scenario: Admin operation receives platform admin identity

- **WHEN** a management operation is requested by a Cosmic platform user with administrator equivalent role
- **THEN** admin governance SHALL use the resolved identity context as the admin identity and proceed through the existing management service contract

#### Scenario: Admin operation has mismatched admin id

- **WHEN** a management operation supplies an explicit adminId that conflicts with the resolved platform administrator identity
- **THEN** admin governance SHALL reject the operation with identity-mismatch or forbidden semantics and SHALL NOT write audit or business changes as if it succeeded
