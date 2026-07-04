## ADDED Requirements

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
- **THEN** the page SHALL clearly mark it as 管理员公告 and visually distinguish it from ordinary system messages

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
