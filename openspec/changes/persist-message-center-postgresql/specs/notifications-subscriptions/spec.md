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

#### Scenario: User marks one message read
- **WHEN** a user clicks an unread message or its read action
- **THEN** the page SHALL call the read API, refresh unread state, and keep the user on the message center

#### Scenario: User marks all messages read
- **WHEN** a user clicks the mark-all-read action
- **THEN** the page SHALL call the read-all API and update the message list and unread count

#### Scenario: User archives a message
- **WHEN** a user deletes a message from the message center
- **THEN** the page SHALL call the delete API, remove the archived message from the list, and show a recoverable state if the operation fails
