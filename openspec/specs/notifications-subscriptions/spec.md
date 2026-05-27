## Purpose

定义 CyanCruise CareerLoop 的站内通知、订阅授权/配额、周报通知、消息中心 WebAPI、webapp 消费契约、降级状态和迁移边界。

## Requirements

### Requirement: In-app notification records

CyanCruise SHALL provide in-app CareerLoop notification records with user ownership, type, title, content, optional deep link, read state, and created time.

#### Scenario: Domain service pushes notification

- **WHEN** a migrated business service emits a notification for a user
- **THEN** the system SHALL create a user-owned notification record with type, title, content, link, unread state, and created time

#### Scenario: Notification push fails

- **WHEN** notification storage or delivery fails during a business flow
- **THEN** the notification push SHALL be best-effort and SHALL NOT break the primary business operation

### Requirement: Notification list and unread count

The notification capability SHALL expose user-owned notification list and unread count contracts for webapp consumption.

#### Scenario: User lists notifications

- **WHEN** a user requests notifications with a resolvable user identity
- **THEN** the system SHALL return only that user's notifications ordered newest first

#### Scenario: User requests unread count

- **WHEN** a user requests unread count
- **THEN** the system SHALL count only unread notifications owned by that user

#### Scenario: User identity is missing

- **WHEN** the system cannot resolve a user identity for a user-owned notification operation
- **THEN** the operation SHALL return an identity-required state and SHALL NOT use a hardcoded production user

### Requirement: Notification read and delete operations

The notification capability SHALL support marking one notification as read, marking all user notifications as read, and deleting a notification, while enforcing ownership.

#### Scenario: User marks own notification read

- **WHEN** a user marks a notification they own as read
- **THEN** the system SHALL set read state to true and leave other users' notifications unchanged

#### Scenario: User marks all notifications read

- **WHEN** a user marks all notifications as read
- **THEN** the system SHALL mark only that user's unread notifications as read and return the number updated

#### Scenario: User deletes another user's notification

- **WHEN** a user attempts to delete or mutate a notification owned by another user
- **THEN** the system SHALL reject the operation with an ownership error

### Requirement: Notification type taxonomy and message center grouping

CyanCruise SHALL define canonical notification type constants and grouping rules for message center tabs such as career, system, and AI.

#### Scenario: Known notification type is rendered

- **WHEN** the webapp receives a known notification type such as `INTERVIEW_REPORT`, `ASSESSMENT_RESULT`, `RESUME_DIAGNOSIS`, `WEEKLY_REPORT`, `STREAK_WARNING`, `AI_PROACTIVE`, or `ADMIN_BROADCAST`
- **THEN** the message center contract SHALL identify its group, label, icon key or equivalent display hint, and default deep-link behavior

#### Scenario: Unknown notification type is rendered

- **WHEN** the webapp receives an unknown notification type
- **THEN** the system SHALL treat it as a generic system notification instead of failing the message list

### Requirement: Subscription grant and quota semantics

The subscription capability SHALL record user grant results per template and maintain remaining send quota for accepted templates.

#### Scenario: User accepts subscription template

- **WHEN** the client reports a template result of `accept`
- **THEN** the system SHALL increment remaining quota for that user and template

#### Scenario: User rejects or bans subscription template

- **WHEN** the client reports a template result of `reject` or `ban`
- **THEN** the system SHALL NOT increment remaining quota for that template

#### Scenario: User queries subscription quota

- **WHEN** a user requests subscription quota
- **THEN** the system SHALL return only that user's template quota records

### Requirement: Subscription sending boundary

The subscription sending boundary SHALL consume quota before external dispatch and SHALL skip safely when template, identity binding, quota, or provider configuration is unavailable.

#### Scenario: Subscription can be sent

- **WHEN** a user has remaining quota, a configured template, and a bound external recipient identity
- **THEN** the system SHALL consume one quota and ask the configured subscription adapter to send the message

#### Scenario: Subscription cannot be sent

- **WHEN** quota is missing, template id is blank, recipient identity is unavailable, or provider adapter is unavailable
- **THEN** the system SHALL skip external dispatch and return a skipped/unavailable result without breaking the originating business flow

### Requirement: Weekly report notification

CyanCruise SHALL provide a weekly report notification contract that can summarize recent CareerLoop activity and push an in-app notification with optional subscription dispatch.

#### Scenario: Weekly report has enough activity

- **WHEN** a user has sufficient recent CareerLoop activity for a weekly recap
- **THEN** the weekly report service SHALL generate a concise summary, create a `WEEKLY_REPORT` notification, and attempt optional subscription dispatch best-effort

#### Scenario: Weekly report lacks comparison data

- **WHEN** a user has insufficient recent activity or comparison data
- **THEN** the weekly report service SHALL skip delivery for that user and return a skipped count or reason

### Requirement: WebAPI and webapp route contract mapping

The migration SHALL define Cosmic WebAPI and webapp route/API mapping for notifications, unread counts, read/delete operations, subscription grants, subscription quota, and weekly report trigger or preview.

#### Scenario: Route map is reviewed

- **WHEN** reviewers inspect webapp migration artifacts
- **THEN** they SHALL find message center and notification/subscription route keys, consumed WebAPI paths, DTO fields, identity requirements, and fallback states

#### Scenario: Webapp cannot reach notification backend

- **WHEN** notification WebAPI is unavailable
- **THEN** the webapp SHALL keep the CareerLoop workbench navigable and display a recoverable message-unavailable or empty state

### Requirement: Migration boundary for notifications and subscriptions

The notification/subscription migration SHALL rebuild business semantics for CyanCruise and SHALL NOT directly migrate IPD Spring Boot, JPA, Flyway, Redis, Java 17 HTTP, WeChat network API implementation, Vue, uni-app, Pinia/store, or mini-program runtime code.

#### Scenario: Implementation is inspected

- **WHEN** implementation files are reviewed
- **THEN** they SHALL reside in CyanCruise target modules and SHALL NOT require `F:\Project\IPD` source files or IPD runtime dependencies

#### Scenario: Dependencies are checked

- **WHEN** dependency changes are reviewed
- **THEN** the change SHALL NOT introduce new external dependencies unless their necessity and Cosmic/KDDT/JDK 8 compatibility are documented

### Requirement: Verification and migration documentation

The notification/subscription migration SHALL include verification and documentation that prove the proposed contracts are OpenSpec-valid and aligned with the migration map.

#### Scenario: Change is verified before archive

- **WHEN** implementation is complete
- **THEN** verification SHALL include strict OpenSpec validation, focused helper/service/WebAPI tests or equivalent static checks, JDK 8 Gradle build validation, and migration map updates

#### Scenario: Migration map is updated

- **WHEN** the change is finalized
- **THEN** `docs/ipd-to-cyancruise-migration-map.md` SHALL record IPD source paths, CyanCruise target modules, data mapping, temporarily excluded items, and validation results
