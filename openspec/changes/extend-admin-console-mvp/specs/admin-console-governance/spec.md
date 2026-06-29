## ADDED Requirements

### Requirement: Admin console MVP page experience

CyanCruise SHALL provide a management console page in the existing CyanCruise webapp that allows an authorized administrator to view dashboard, users, content, question review, broadcasts, and audit records from one entry.

#### Scenario: Admin opens management console

- **WHEN** an authorized administrator opens the `admin-console` route
- **THEN** the page SHALL show management sections for 总览, 用户管理, 内容管理, 题库审核, 通知公告, and 操作记录

#### Scenario: Non-admin opens management console

- **WHEN** a signed-in caller without an `ADMIN` equivalent role opens the `admin-console` route
- **THEN** the page SHALL show a Chinese no-permission state and SHALL NOT call state-changing `/cc001/admin/*` operations

#### Scenario: Admin backend is unavailable

- **WHEN** the page cannot load management data from `/cc001/admin/*`
- **THEN** the page SHALL show a recoverable Chinese unavailable state and SHALL NOT affect user-facing routes

### Requirement: Admin frontend service boundary

CyanCruise SHALL centralize admin frontend API calls behind a webapp service module so page rendering does not duplicate endpoint paths or payload assembly.

#### Scenario: Page loads admin dashboard

- **WHEN** the admin page requests dashboard data
- **THEN** it SHALL call the admin service module, which SHALL invoke the configured `/cc001/admin/*` endpoint with the current admin identity

#### Scenario: Admin action fails

- **WHEN** an admin service call returns forbidden, identity-required, failed, or unavailable status
- **THEN** the page SHALL present a clear Chinese failure message and SHALL keep the current page usable

### Requirement: Unified admin WebAPI authorization

Every CyanCruise management WebAPI under `/cc001/admin/*`, except public question contribution, SHALL resolve the current Cosmic identity and verify an `ADMIN` equivalent role before executing the application service operation.

#### Scenario: Admin lists management data

- **WHEN** an administrator lists organizations, users, questions, content, analytics, or audit logs
- **THEN** the WebAPI SHALL validate the current admin identity before reading management data

#### Scenario: Admin writes management data

- **WHEN** an administrator creates, updates, deletes, bans, unbans, approves, rejects, broadcasts, pins, or hides management data
- **THEN** the WebAPI SHALL validate the current admin identity before executing the write and recording audit output

#### Scenario: Explicit admin id conflicts with platform identity

- **WHEN** a request body supplies an `adminId` that does not match the resolved Cosmic administrator identity
- **THEN** the WebAPI SHALL reject the operation and SHALL NOT execute the application service write

### Requirement: Admin user-visible Chinese copy

CyanCruise SHALL show management console labels, buttons, empty states, errors, and identity hints in understandable Chinese.

#### Scenario: Management text is rendered

- **WHEN** the admin console renders labels, buttons, or status messages
- **THEN** the visible text SHALL use understandable Chinese such as “管理后台”, “用户管理”, “题库审核”, and “无管理员权限”, and SHALL NOT show garbled text or unexplained abbreviations
