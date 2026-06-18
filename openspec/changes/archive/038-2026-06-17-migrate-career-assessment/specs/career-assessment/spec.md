## ADDED Requirements

### Requirement: 职业测评题库
CyanCruise SHALL provide a server-side career assessment catalog migrated from IPD assessment semantics, including active scales, ordered questions, ordered options, dimension codes, score values, and scale version.

#### Scenario: 列出可用量表
- **WHEN** a user opens the assessment page
- **THEN** the system SHALL return active assessment scales with `scaleId`, title, description, version, and question count

#### Scenario: 用户选择测评类型
- **WHEN** a user views the assessment page
- **THEN** the page SHALL show multiple assessment cards such as MBTI, RIASEC, BIG5, career values, and stress coping, allowing the user to choose which scale to start

#### Scenario: 读取题目
- **WHEN** a user selects an active scale
- **THEN** the system SHALL return ordered questions and ordered options for that scale

### Requirement: 职业测评提交评分
CyanCruise SHALL score submitted assessment answers on the backend using the server-side scale definition, producing status, result summary, dimension counts, answer snapshots, and suggested roles.

#### Scenario: 提交 MBTI 答案
- **WHEN** a user submits one selected option for each MBTI question
- **THEN** the system SHALL calculate the four-letter MBTI result from dimension counts and mark the result `COMPLETED`

#### Scenario: 非法选项
- **WHEN** a submitted answer references an option that does not belong to the selected question
- **THEN** the system SHALL keep an invalid answer snapshot with zero score and SHALL NOT count that option dimension

### Requirement: 职业测评结果持久化
CyanCruise SHALL persist assessment results by user, scale, status, result summary, dimension counts, answer snapshots, suggested roles, and creation time through a replaceable storage boundary.

#### Scenario: 保存结果
- **WHEN** a user submits a valid assessment
- **THEN** the system SHALL save the result and return a stable record id

#### Scenario: 查看历史结果
- **WHEN** a user requests assessment records
- **THEN** the system SHALL return only that user's results ordered by newest first

### Requirement: 职业测评画像回写
CyanCruise SHALL merge completed assessment results into `UserProfileSnapshot.AssessmentBlock` without clearing onboarding, preferences, resume, interview, or plan data.

#### Scenario: 测评完成后刷新画像
- **WHEN** a user completes an assessment
- **THEN** the system SHALL update the assessment block with record id, scale id, scale title, summary, suggested roles, and completed time

### Requirement: 职业测评页面闭环
CyanCruise SHALL provide a webapp assessment page that can load scales, render questions, collect answers, submit them, and show the returned result.

#### Scenario: 页面完成测评
- **WHEN** a user with identity answers all questions and submits
- **THEN** the page SHALL call `/cc001/assessment/submit`, display the result summary and suggested roles, and refresh the local snapshot overview

#### Scenario: 无身份或文件预览
- **WHEN** the page runs without a user identity or in `file://` preview
- **THEN** the page SHALL show a recoverable preview or identity-required state and SHALL NOT call protected user-owned WebAPI
