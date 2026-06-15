## Why

CyanCruise 的用户画像数据目前通过文件、内存或占位 datamodel 适配器持久化。后端接口和应用服务边界已经存在，但还没有真正连接到你的 PostgreSQL 数据库。现在需要把用户画像草稿、画像快照、画像 facts 和派生统一画像接入 PostgreSQL，让这些数据能够跨会话、跨服务实例稳定保存和读取。

## What Changes

- 新增仅面向 PostgreSQL 的 CyanCruise 用户画像生产存储能力。
- 将画像草稿、画像快照、画像 facts、派生统一画像保存到 PostgreSQL，同时保持现有 WebAPI 契约不变。
- 数据库连接通过配置项提供；已知 PostgreSQL 地址为 `10.0.0.8:5432`，数据库为 `cyancruise`，schema 为 `public`，建议应用专用用户为 `cyancruise_app`。这些值只能出现在配置示例和部署说明中，不得硬编码到业务代码；密码不得提交。
- 在 PostgreSQL 验证通过前，继续保留文件存储作为开发和回滚 fallback。
- 在实现前明确表结构、用户归属约束、初始化策略、验证方式和回滚方式。
- 本 change 不修改前端页面交互，也不实现 AI 生成逻辑。

## Capabilities

### New Capabilities
- `postgresql-profile-storage`：为 CyanCruise 用户画像草稿、画像快照、画像 facts 和派生统一画像提供 PostgreSQL 持久化能力。

### Modified Capabilities
- `career-profile-draft`：当画像存储适配器启用 PostgreSQL 时，草稿保存、读取和清空必须走 PostgreSQL。
- `career-profile-onboarding`：当画像存储适配器启用 PostgreSQL 时，onboarding 快照、画像 facts 和派生统一画像必须走 PostgreSQL。
- `cosmic-datamodel-adapters`：当前租户选择 PostgreSQL 作为用户画像正式数据库，存储替换策略必须支持 PostgreSQL，而不是强制依赖 Cosmic datamodel。
- `migration-governance`：新增项目命名治理规则，当前项目正式名称为 CyanCruise，CareerLoop 仅作为历史来源或遗留命名出现。

## Impact

- `code/cloud01/v620-cc001-cloud01-app01/`：新增 PostgreSQL 画像存储适配器、配置工厂、验证测试和可选初始化能力。
- `code/base/v620-cc001-base-common/`：预计不需要修改 DTO；只有审核发现缺少可序列化字段时才调整。
- `gradle.properties.example`：新增 PostgreSQL URL、用户名、密码、schema、启用开关、初始化行为等配置项示例，示例只能使用占位值。
- `openspec/specs/`：新增 PostgreSQL 画像存储规格，并补充画像草稿、onboarding/画像、存储替换边界和命名治理的 delta spec。
- 外部系统：PostgreSQL `10.0.0.8:5432`，数据库 `cyancruise`，schema `public`，建议应用用户 `cyancruise_app`；密码通过本地/部署配置提供，不提交到仓库。
- 不新增前端运行时依赖，不迁移 IPD 的 Spring/JPA/Flyway 实现，不在代码中硬编码本地路径或数据库凭据。
