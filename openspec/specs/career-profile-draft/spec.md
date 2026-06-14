# career-profile-draft Specification

## Purpose
TBD - created by archiving change implement-career-profile-draft. Update Purpose after archive.
## Requirements
### Requirement: Persist partial user profile drafts
The system SHALL persist a per-user profile draft that can contain route-entry fields including identity type, education stage, school/major, resume status, target role, preference, experience, route intent, and update time.

#### Scenario: Save a partial draft
- **WHEN** a user saves a draft containing only identity type and target role
- **THEN** the system stores those fields for that user without requiring completed onboarding data

#### Scenario: Read an empty draft
- **WHEN** a user has never saved a profile draft
- **THEN** the system returns an empty draft object instead of failing or returning null

### Requirement: Merge draft updates without clearing omitted values
The system SHALL treat draft saves as partial updates and SHALL NOT clear previously saved non-empty fields when a new request omits or sends blank values for those fields.

#### Scenario: Preserve existing draft fields
- **WHEN** an existing draft has a target role and the user later saves only resume status
- **THEN** the saved draft keeps the target role and updates resume status

#### Scenario: Ignore blank draft values
- **WHEN** an existing draft has a preference and the user saves a blank preference value
- **THEN** the saved draft keeps the previous preference value

### Requirement: Clear drafts explicitly
The system SHALL provide an explicit operation to clear the current user's profile draft without clearing onboarding snapshots, preferences, facts, or derived profile data.

#### Scenario: Clear only draft data
- **WHEN** a user clears a saved profile draft
- **THEN** subsequent draft reads return an empty draft while existing profile snapshot data remains readable

### Requirement: Expose profile draft operations through Career Profile API
The system SHALL expose application and WebAPI operations for reading, saving, and clearing profile drafts through the existing Career Profile boundary and custom WebAPI plugin routing.

#### Scenario: Web API saves and reads draft
- **WHEN** the web entry calls the draft save operation and then the draft get operation for the same user
- **THEN** the returned draft contains the saved values

### Requirement: PostgreSQL 支撑画像草稿持久化
当 PostgreSQL 画像存储被显式启用时，画像草稿操作 SHALL 使用 PostgreSQL 持久化，同时保持现有草稿 WebAPI 和应用服务契约不变。

#### Scenario: 草稿 API 使用 PostgreSQL 适配器
- **WHEN** PostgreSQL 画像存储已启用，并且 web 入口为同一用户保存后再读取草稿
- **THEN** 返回的草稿 SHALL 来自 PostgreSQL，并包含已保存的字段值

#### Scenario: 草稿合并规则保持不变
- **WHEN** PostgreSQL 画像存储已启用，并且用户保存部分字段或空白草稿更新
- **THEN** 现有合并行为 SHALL 继续保留未提交字段，并忽略空白覆盖值

#### Scenario: 草稿 fallback 保持可用
- **WHEN** PostgreSQL 画像存储未启用或配置不完整
- **THEN** 草稿操作 SHALL 继续通过已配置 fallback 存储工作，用于开发和回滚
