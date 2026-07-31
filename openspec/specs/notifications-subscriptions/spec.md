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

### Requirement: PostgreSQL notice storage aligned with business object

CyanCruise SHALL support a local PostgreSQL notification storage implementation whose structured fields align with the `v620_cc_notice` business object semantics. The storage SHALL preserve the existing `NotificationStorage` contract and SHALL keep WebAPI and DTO callers independent from the storage backend.

#### Scenario: Persist notification in PostgreSQL

- **WHEN** a business flow pushes an in-app notification while PostgreSQL storage is enabled
- **THEN** the system SHALL save a record with notice ID, receiving user, notice type, title, content, optional route, status, created time, optional read time, and payload JSON aligned to `v620_cc_notice` semantics

#### Scenario: Restore notification after service restart

- **WHEN** a user lists notifications after the application service is recreated
- **THEN** the system SHALL return the user's PostgreSQL-backed notification records ordered newest first

#### Scenario: Archive instead of physical delete

- **WHEN** a user deletes a notification they own
- **THEN** PostgreSQL storage SHALL mark the notice status as `archived` and SHALL exclude it from normal message center lists

#### Scenario: Read state maps to notice status

- **WHEN** a user marks a notification as read
- **THEN** PostgreSQL storage SHALL set the notice status to `read`, record read time, and return the DTO with `readFlag=true`

### Requirement: Notification type taxonomy and message center grouping

CyanCruise SHALL define canonical notification type constants and grouping rules for message center tabs such as career, system, and AI.

#### Scenario: Known notification type is rendered

- **WHEN** the webapp receives a known notification type such as `INTERVIEW_REPORT`, `ASSESSMENT_RESULT`, `RESUME_DIAGNOSIS`, `WEEKLY_REPORT`, `STREAK_WARNING`, `AI_PROACTIVE`, or `ADMIN_BROADCAST`
- **THEN** the message center contract SHALL identify its group, label, icon key or equivalent display hint, and default deep-link behavior

#### Scenario: Unknown notification type is rendered

- **WHEN** the webapp receives an unknown notification type
- **THEN** the system SHALL treat it as a generic system notification instead of failing the message list

### Requirement: Complete webapp message center experience

CyanCruise webapp SHALL provide a usable Chinese message center page for ordinary users. The page SHALL consume existing notification APIs and SHALL support list, unread count, mark read, mark all read, archive/delete, subscription quota display, weekly report trigger, loading, empty, and unavailable states.

#### Scenario: User opens message center

- **WHEN** a user opens the message center route
- **THEN** the page SHALL load notifications, unread count, and subscription quota, then render messages grouped with readable Chinese labels

#### Scenario: Message list is paginated

- **WHEN** the user has more than 10 visible notifications
- **THEN** the page SHALL show 10 notifications per page and provide previous/next paging controls

#### Scenario: Message labels are visible

- **WHEN** a notification is rendered
- **THEN** the page SHALL display a readable Chinese label for its notification type and read state

#### Scenario: Administrator announcement is highlighted

- **WHEN** a notification type is `ADMIN_BROADCAST`
- **THEN** the page SHALL clearly mark it as “管理员公告” and visually distinguish it from ordinary system messages

#### Scenario: User marks one message read

- **WHEN** a user clicks an unread message or its read action
- **THEN** the page SHALL call the read API, refresh unread state, and keep the user on the message center

#### Scenario: Message actions update asynchronously

- **WHEN** the user marks a message read, marks all read, archives a message, refreshes, changes page, or generates a weekly report
- **THEN** the page SHALL keep the existing message center visible, show operation progress at the action level, and update the affected list or counters without returning the whole page to a loading state

#### Scenario: User marks all messages read

- **WHEN** a user clicks the mark-all-read action
- **THEN** the page SHALL call the read-all API and update the message list and unread count

#### Scenario: User archives a message

- **WHEN** a user deletes a message from the message center
- **THEN** the page SHALL call the delete API, remove the archived message from the list, and show a recoverable state if the operation fails

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

### Requirement: 消息中心工作台真实状态与清晰交互
`messages` 与 `message-detail` 页面 SHALL 以消息中心工作台形式展示真实通知、未读数、订阅配额、操作状态和完整详情。页面 SHALL 保留既有通知和订阅接口，不得使用虚构消息、待办、趋势或配额填充界面。

#### Scenario: 查看消息中心概览
- **WHEN** 用户打开消息中心且通知数据加载成功
- **THEN** 页面 SHALL 使用真实未读数、通知总数和订阅剩余额度生成概览
- **AND** 页面 SHALL 清晰区分概览、常用操作和通知列表

#### Scenario: 查看空消息状态
- **WHEN** 用户的真实通知列表为空
- **THEN** 页面 SHALL 展示明确的无消息状态和消息用途说明
- **AND** 页面 SHALL NOT 构造示例通知、虚假未读数或虚假活动数据

#### Scenario: 查看不同消息状态
- **WHEN** 页面渲染管理员公告、未读消息、已读消息或未知类型通知
- **THEN** 页面 SHALL 提供可辨识的类型、来源、时间与已读状态
- **AND** 未知类型 SHALL 继续降级为普通系统消息

#### Scenario: 异步处理消息操作
- **WHEN** 用户刷新、标记单条已读、全部已读、归档消息、翻页或生成周报通知
- **THEN** 页面 SHALL 保留既有动作、请求参数和异步局部更新逻辑
- **AND** 操作进度 SHALL 在相关按钮或消息项中表达，不得无故清空现有消息工作台

#### Scenario: 查看消息详情
- **WHEN** 用户打开一条消息
- **THEN** 页面 SHALL 展示真实来源、类型、时间、标题和完整正文
- **AND** 返回消息中心操作 SHALL 保留既有路由逻辑

#### Scenario: 消息服务不可用
- **WHEN** 通知、未读数或订阅配额接口不可用
- **THEN** 页面 SHALL 展示可恢复的中文不可用状态与重试操作
- **AND** 其他页面导航 SHALL 保持可用

#### Scenario: 在窄屏查看消息中心
- **WHEN** 用户在窄屏设备查看概览、操作区、长消息或消息详情
- **THEN** 页面 SHALL 使用单列或可换行布局
- **AND** 页面 SHALL NOT 产生横向滚动

#### Scenario: 使用键盘访问消息中心
- **WHEN** 用户使用键盘访问刷新、已读、归档、详情、分页和返回操作
- **THEN** 可交互元素 SHALL 提供清晰焦点状态
- **AND** 页面 SHALL 在用户偏好减少动态效果时停止非必要动画

