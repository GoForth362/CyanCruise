## Context

考研、保研和留学陪伴已经有独立 DTO、helper service、application service、WebAPI 和页面路由。当前 application service 主要校验 `userId` 后即时调用规则 helper，未把输入、输出、目标状态或后续更新写入后端存储。用户刷新页面、切换设备或隔天继续准备时，无法查看历史择校建议、复习计划、错题解析、保研材料、导师联系、语言计划、选校定位、文书主线和签证清单。

CyanCruise 已经将多类业务状态迁移到 PostgreSQL，并具备 `PostgresqlStorageConfig`、`PostgresqlStorageSupport`、`CyanCruiseStorageFactory`、PostgreSQL JDBC 与 Jackson。深造陪伴应复用该边界，避免新增 ORM、迁移框架或硬编码本地路径。OpenSpec 文档、用户可见文案和错误提示使用普通中文；实现必须兼容 JDK 1.8 与 Kingdee Cosmic 二开工程。

深造相关公共契约集中放在 `v620.cc001.base.common.dto.furtherstudy` 包下，覆盖考研、保研、留学和统一深造记录 DTO。深造规则 helper 集中放在 `v620.base.helper.furtherstudy` 包下。cloud01 应用层深造服务和存储集中放在 `v620.cc001.cloud01.app01.mservice.furtherstudy`，深造 WebAPI 集中放在 `v620.cc001.cloud01.app01.webapi.furtherstudy`。`dto.career`、`helper.career` 以及 cloud01 的职业主循环包继续保留求职、简历、面试、职业计划等契约。

## Goals / Non-Goals

**Goals:**
- 为考研、保研和留学提供统一的后端持久化模型，支持创建、查询、详情、状态更新、历史回看和用户隔离。
- 保存关键结构化索引字段，同时用 JSON 保存完整请求与结果，减少第一期表结构爆炸并保留 DTO 演进弹性。
- 复用共享 `cc001.storage.*` PostgreSQL 配置、JDBC/Jackson 模式和 `CyanCruiseStorageFactory`。
- 保持现有即时生成 WebAPI 兼容；生成类接口成功返回时同步保存一条当前用户记录。
- 提供可人工审查的 PostgreSQL DDL，默认不自动执行破坏性或不可逆变更。

**Non-Goals:**
- 不在本 change 改造 AI provider、模型选择或提示词策略。
- 不迁移 Spring Boot、JPA、MyBatis、Flyway、Lombok 或 Java 9+ API。
- 不接入真实外部院校库、导师库、签证系统或支付/运营系统。
- 不改变 Cosmic 当前登录身份边界；所有记录仍按当前用户上下文归属。

## Data Model

- `cc_further_study_target`：保存用户深造方向、目标国家/地区、目标学校、目标专业、考试年份和当前阶段。
- `cc_further_study_record`：保存一次陪伴生成或人工更新的核心记录，包含类型、状态、标题、摘要、payload JSON、创建时间和更新时间。
- `cc_further_study_material`：保存文书、材料、导师联系、签证清单等可更新材料状态。
- `cc_further_study_event`：保存状态变化、人工更新和生成结果回写等历史事件。

结构化列负责查询、过滤、排序和归属校验；JSON 列保存完整输入输出，便于后续扩展。

## Storage Boundary

- 新增 `FurtherStudyCompanionStorage` 接口，覆盖目标保存、记录保存、列表、详情、状态更新、材料保存和历史事件查询。
- 新增 `InMemoryFurtherStudyCompanionStorage` 支持单元测试。
- 新增 `PostgresqlFurtherStudyCompanionStorage` 复用现有 JDBC/Jackson 支撑类。
- `CyanCruiseStorageFactory` 提供 `furtherStudyCompanionStorage()`，默认使用共享 `cc001.storage.*` 配置，配置缺失时 fail fast。

## Verification

- 深造 helper 聚焦测试。
- 应用服务与 WebAPI 聚焦测试。
- `openspec validate persist-further-study-companion-records --strict`
- JDK 8 `gradlew.bat` 构建。
