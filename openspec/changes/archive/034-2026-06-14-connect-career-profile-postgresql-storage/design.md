## Context

CyanCruise 已经为用户画像数据建立了存储边界：`CareerProfileStorage` 负责画像快照、画像 facts、派生统一画像和画像草稿。当前实现包括文件存储、内存存储和占位 datamodel 适配器。你已经明确真实数据库只使用 PostgreSQL，已知服务器地址为 `10.0.0.8:5432`，数据库为 `cyancruise`，schema 为 `public`，建议应用专用用户为 `cyancruise_app`。

本 change 只准备“用户画像相关数据”的生产存储连接，不扩展到简历、测评、职业计划、今日行动等其他模块。实现必须兼容 JDK 1.8 和 Kingdee Cosmic 二开约束，不引入 Spring Boot、JPA、Flyway、IPD 运行时代码，也不得硬编码环境路径或数据库密钥。

## Goals / Non-Goals

**Goals:**
- 定义基于 PostgreSQL 的画像草稿、画像快照、画像 facts 和派生统一画像持久化。
- 保持现有 WebAPI 和应用服务契约稳定。
- 通过配置项管理 JDBC URL、用户名、密码、schema、适配器启用开关和建表初始化行为；密码只允许来自本地或部署配置，不进入源码和 OpenSpec artifact。
- 保留文件存储作为明确的开发 fallback 或回滚 fallback。
- 提供可选的 PostgreSQL 联调测试；只有配置了测试数据库属性时才连接真实 PostgreSQL。

**Non-Goals:**
- 本 change 不实现简历、测评、职业计划、今日行动、模拟面试、AI 助手、通知或管理后台的数据库存储。
- 不新增前端功能。
- 不使用独立向量数据库。
- 不使用 Spring Boot、JPA、Flyway、Lombok 或 IPD repository 实现。
- 不把数据库密码、生产密钥或客户私有值写入 OpenSpec、源码或 route metadata。

## Decisions

- 在现有 `CareerProfileStorage` 接口后新增 `PostgresqlCareerProfileStorage`。
  - 原因：现有应用服务和 WebAPI 已经依赖该边界，接入 PostgreSQL 不应影响上层契约。
  - 备选方案：在应用服务里直接写 SQL。拒绝原因：会让持久化逻辑扩散到业务层，后续难以替换和测试。

- 使用原生 JDBC 和 PostgreSQL JDBC 驱动，不使用 ORM 或迁移框架。
  - 原因：项目必须兼容 JDK 8，并避免把 IPD 的 Spring/JPA/Flyway 技术栈带入 Cosmic 二开工程。
  - 备选方案：JPA、MyBatis、Flyway。暂不采用，因为会引入额外框架约束，需要单独审批和验证 Cosmic/KDDT 兼容性。

- 结构化字段与 JSON 载荷分开存储。
  - 草稿、快照、派生画像可以保存完整 DTO JSON 文本，但 `user_id`、版本/状态、目标岗位、更新时间等查询和归属字段必须是结构化列。
  - facts 使用 `(user_id, fact_key)` 一行一个 fact 的结构，便于局部更新和按用户读取。

- PostgreSQL 适配器必须显式启用。
  - 建议配置项：
    - `cc001.profile.storage.adapter=postgresql`
    - `cc001.profile.postgresql.url=jdbc:postgresql://10.0.0.8:5432/cyancruise`
    - `cc001.profile.postgresql.username=cyancruise_app`
    - `cc001.profile.postgresql.password=<password>`
    - `cc001.profile.postgresql.schema=public`
    - `cc001.profile.postgresql.initialize=false`
  - 如果适配器未设置为 `postgresql`，或必要连接属性缺失，应用保持当前文件存储 fallback。

- 表初始化必须显式开启，默认关闭。
  - 原因：租户数据库可能需要 DBA 审核 DDL，程序默认自动建表容易影响共享库或生产库。
  - 备选方案：启动时总是自动建表。拒绝原因：生产环境风险过高。

- 默认交付手工 PostgreSQL DDL 脚本，程序自动建表不作为默认路径。
  - 原因：当前数据库约定优先由人工或 DBA 审核 SQL 后建表，应用只负责连接和读写已审核表结构。
  - 如果后续仍保留 `cc001.profile.postgresql.initialize=true`，其行为 SHALL 限制为幂等创建或结构检查，不得执行删除、截断、迁移或破坏性变更。

## Proposed Tables

审核用 PostgreSQL DDL 位于 `openspec/changes/connect-career-profile-postgresql-storage/sql/postgresql-profile-storage.sql`。该脚本面向数据库 `cyancruise`、schema `public`，仅包含表和索引，不包含密码、本地路径或授权命令。

- `cc_profile_draft`
  - `user_id` 主键
  - `identity_type`、`education_stage`、`school_major`、`resume_status`、`target_role`、`preference`、`route_intent`
  - `experience_text`
  - `draft_json`
  - `created_at`、`updated_at`
- `cc_profile_snapshot`
  - `user_id` 主键
  - `version`、`target_role`
  - `snapshot_json`
  - `created_at`、`updated_at`
- `cc_profile_fact`
  - `user_id`、`fact_key` 复合主键
  - `fact_value`
  - `created_at`、`updated_at`
- `cc_user_profile`
  - `user_id` 主键
  - `personalization_level`、`completeness_score`、`current_stage`、`target_role`
  - `profile_json`
  - `created_at`、`updated_at`

## Risks / Trade-offs

- [Risk] PostgreSQL 密码和 DDL 权限尚未确认。 -> Mitigation: OpenSpec 只记录数据库 `cyancruise`、schema `public` 和建议用户 `cyancruise_app`，密码继续使用占位符并由本地/部署配置提供。
- [Risk] DTO JSON 序列化格式未来可能变化。 -> Mitigation: 用户归属和查询字段使用结构化列，JSON 仅作为完整载荷保存。
- [Risk] 自动 DDL 在租户数据库中可能不安全。 -> Mitigation: 默认关闭初始化，并提供可审核 SQL 或显式开关。
- [Risk] 数据库不可用会影响画像接口。 -> Mitigation: PostgreSQL 适配器显式启用；回滚方式是切回文件存储 fallback。
- [Risk] 新增 JDBC 驱动可能影响 Cosmic/KDDT 打包。 -> Mitigation: 说明依赖必要性，并使用 JDK 8 执行 `gradlew.bat clean build` 验证。

## Migration Plan

1. 确认 `cyancruise_app` 的密码、连接权限、`public` schema 下四张画像表的 DDL 权限或手工建表执行人。
2. 审核通过后新增 PostgreSQL JDBC 依赖，并记录依赖必要性。
3. 实现配置解析和画像存储适配器工厂。
4. 优先提供手工 SQL 脚本；如保留初始化 helper，则默认关闭且只允许幂等创建或结构检查。
5. 实现 `PostgresqlCareerProfileStorage`。
6. 增加测试：CRUD、facts 局部更新、草稿合并持久化、快照/画像重载、配置缺失 fallback、可选真实 PostgreSQL 联调。
7. 运行 OpenSpec 校验和 JDK 8 Gradle 构建。
8. 先在本地/测试环境启用 PostgreSQL adapter，数据库验证通过后再用于生产。

## Open Questions

- `cyancruise_app` 的密码如何通过本地/部署配置注入？
- `cyancruise_app` 是否拥有 `public` schema 下建表权限，还是由 DBA/人工先执行 SQL？
- 是否完全禁用程序自动建表，还是允许 `cc001.profile.postgresql.initialize=true` 仅做幂等建表/结构检查？
- 现有文件存储中的数据是否需要迁移到 PostgreSQL，还是本次 PostgreSQL 可以从空表开始？
