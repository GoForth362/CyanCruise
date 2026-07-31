## MODIFIED Requirements

### Requirement: Admin console MVP page experience

CyanCruise SHALL provide a management console page in the existing CyanCruise webapp that allows an authorized administrator to view dashboard, users, content, question review, broadcasts, and audit records from one entry. The overview SHALL prioritize a truthful management summary, core metrics, operational health, attention items, and recent actions using only data already returned by the management service.

#### Scenario: Admin opens management console

- **WHEN** an authorized administrator opens the `admin-console` route
- **THEN** the page SHALL show management sections for 总览, 用户管理, 内容管理, 题库管理, 通知公告, and 审计日志

#### Scenario: Admin uses management workspace layout

- **WHEN** an authorized administrator opens the `admin-console` route
- **THEN** the page SHALL present a management workspace with a persistent navigation area, top account area, prioritized overview summary, clearly distinguishable metrics and attention states, spacious content region, and table-oriented panels that follow the CyanCruise user-facing visual style

#### Scenario: Admin uses a narrow viewport

- **WHEN** an authorized administrator opens the management console on a tablet or narrow viewport
- **THEN** navigation and overview panels SHALL adapt without hiding management sections or requiring horizontal page scrolling

#### Scenario: Admin uses keyboard navigation

- **WHEN** an administrator focuses a management navigation or quick-entry control with the keyboard
- **THEN** the page SHALL show a clear focus state and activate the same existing management section as pointer input

#### Scenario: Non-admin opens management console

- **WHEN** a signed-in caller without an `ADMIN` equivalent role opens the `admin-console` route
- **THEN** the page SHALL show a Chinese no-permission state and SHALL NOT call state-changing `/cc001/admin/*` operations

#### Scenario: Admin backend is unavailable

- **WHEN** the page cannot load management data from `/cc001/admin/*`
- **THEN** the page SHALL show a recoverable Chinese unavailable state and SHALL NOT affect user-facing routes

#### Scenario: Admin confirms a destructive action

- **WHEN** an administrator deletes content, an interview question, or an assessment question
- **THEN** the page SHALL use the CyanCruise in-app danger confirmation dialog instead of a browser-native confirmation
- **AND** cancelling SHALL NOT call the delete operation
- **AND** confirming SHALL call the existing delete operation exactly once
