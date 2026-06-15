## 背景

上一个 change 已经将 CyanCruise 用户画像存储接入 PostgreSQL，但为了过渡仍保留了文件回退。当前运行期仍有多个默认构造器会创建文件适配器：

- `FileCareerPlanStorage` -> `filestorage/career-plan`
- `FileResumeStorage` -> `filestorage/resume-core`
- `FileResumeDiagnosisStorage` -> `filestorage/resume-diagnosis`
- `FileInterviewStorage` -> `filestorage/interview-core`
- `FileAssistantChatStorage` -> `filestorage/assistant-chat`
- `FileCareerProfileStorage` 仍可作为画像文件回退

本 change 将这些运行期默认路径替换为 PostgreSQL 适配器。二进制文件存储、BOS 附件、预览/下载适配器和简历 `fileKey` 引用不属于本次替换范围。

## 目标与非目标

### 目标

- 对已迁移的 CyanCruise 业务状态，运行期只使用 PostgreSQL 作为持久化后端。
- PostgreSQL 配置缺失或不完整时快速失败。
- 保持现有 DTO、WebAPI 路由、归属校验、排序和服务契约。
- 为新增表和索引提供手工 PostgreSQL DDL。
- 兼容 JDK 1.8，不引入 Spring Boot、JPA、Flyway、Lombok，也不硬编码本地环境。

### 非目标

- 不迁移二进制文件、BOS/OSS 对象、文件预览、上传、下载或删除语义。
- 不要求本次替换必须落到 Cosmic datamodel 对象。
- 不在应用启动时自动迁移历史 `.ser` 文件。
- 不硬编码数据库密码或本地运行路径。

## 存储配置

已迁移业务存储使用一组共享配置：

- `cc001.storage.backend=postgresql`
- `cc001.storage.postgresql.url=jdbc:postgresql://10.0.0.8:5432/cyancruise`
- `cc001.storage.postgresql.username=cyancruise_app`
- `cc001.storage.postgresql.password=<password>`
- `cc001.storage.postgresql.schema=public`
- `cc001.storage.postgresql.initialize=false`

等价环境变量：

- `CC001_STORAGE_BACKEND`
- `CC001_STORAGE_POSTGRESQL_URL`
- `CC001_STORAGE_POSTGRESQL_USERNAME`
- `CC001_STORAGE_POSTGRESQL_PASSWORD`
- `CC001_STORAGE_POSTGRESQL_SCHEMA`
- `CC001_STORAGE_POSTGRESQL_INITIALIZE`

兼容规则：

- 既有画像专用属性 MAY 作为用户画像存储的临时别名继续支持。
- 新适配器 SHOULD 使用共享 `cc001.storage.*` 配置。
- 运行期构造器在共享 PostgreSQL 配置不完整时 SHALL 快速失败。

## 表设计

默认所有表位于 `public`。每张用户归属表 SHALL 在 JSON payload 之外暴露结构化归属列。JSON payload 列使用 `TEXT`，保持 JDK 8/JDBC 简洁性；适配器代码使用 Jackson 序列化 DTO。

已有画像表保持不变：

- `cc_profile_draft`
- `cc_profile_snapshot`
- `cc_profile_fact`
- `cc_user_profile`

新增表：

- `cc_career_plan`：`user_id`、`target_role`、`version`、`generated_from`、`payload_json`、`created_at`、`updated_at`
- `cc_resume_record`：`resume_id`、`user_id`、`title`、`target_job`、`file_key`、`version`、`status`、`diagnosis_score`、`payload_json`、`created_at`、`updated_at`
- `cc_resume_diagnosis`：`resume_id`、`overall_score`、`payload_json`、`updated_at`
- `cc_resume_keyword_status`：`resume_id`、`status`、`payload_json`、`updated_at`
- `cc_interview_session`：`interview_id`、`user_id`、`resume_id`、`target_role`、`status`、`mode`、`started_at`、`ended_at`、`final_score`、`payload_json`、`created_at`、`updated_at`
- `cc_interview_message`：`message_id`、`interview_id`、`role`、`created_at`、`payload_json`
- `cc_assistant_chat_session`：`session_id`、`user_id`、`persona`、`title`、`model_name`、`created_at`、`updated_at`、`payload_json`
- `cc_assistant_chat_message`：`msg_id`、`session_id`、`role`、`created_at`、`payload_json`

消息到会话表 MAY 使用外键；默认 DDL 在安全场景下 SHOULD 使用清晰索引和 `ON DELETE CASCADE`。

## 适配器策略

新增与现有 storage interface 对齐的 PostgreSQL 适配器：

- `PostgresqlCareerPlanStorage implements CareerPlanStorage`
- `PostgresqlResumeStorage implements ResumeStorage`
- `PostgresqlResumeDiagnosisStorage implements ResumeDiagnosisStorage`
- `PostgresqlInterviewStorage implements InterviewStorage`
- `PostgresqlAssistantChatStorage implements AssistantChatStorage`

新增共享辅助类：

- `PostgresqlStorageConfig`
- `CyanCruiseStorageFactory`
- 必要时新增小型 JDBC/Jackson 工具

以下默认构造器改为使用工厂：

- `CareerProfileApplicationService`
- `CareerPlanApplicationService`
- `ResumeApplicationService`
- `ResumeDiagnosisApplicationService`
- `InterviewApplicationService`
- `AssistantChatApplicationService`

历史文件适配器行为测试 MAY 继续直接实例化文件适配器，但运行期构造器和 WebAPI 默认路径 SHALL 使用 PostgreSQL 工厂。

## ID 处理

当 DTO ID 为空时，PostgreSQL `bigserial`/identity 作为简历、面试、消息和聊天 ID 的权威来源。测试或调用方传入已有 ID 时，适配器 MAY 按该 ID upsert，以保持更新语义。

适配器 SHALL 在插入后返回包含已分配 ID 的 DTO 副本。

## 失败行为

运行期 PostgreSQL 配置不完整时：

- 工厂 SHALL 抛出带清晰缺失配置说明的 `IllegalStateException`。
- 运行期构造器 SHALL NOT 创建 `filestorage/...` 目录。
- 需要隔离非 PostgreSQL 行为的单元测试 SHALL 注入内存或显式测试 storage。

JDBC 操作失败时：

- 适配器 SHALL 包装为 `IllegalStateException`。
- 错误消息 SHALL 标识适配器/表操作，但 SHALL NOT 包含密码。

## 迁移与发布

本 change 不自动导入既有 `.ser` 文件。如需历史数据迁移，应在表契约稳定后通过后续工具或一次性脚本处理。

建议发布步骤：

1. 在 `cyancruise.public` 执行手工 DDL。
2. 授予 `cyancruise_app` 必要的表和 sequence 权限。
3. 在真实 Kingdee Cosmic 运行期 JVM 或服务环境中配置共享 PostgreSQL 属性。
4. 部署并重启。
5. 使用页面操作验证 PostgreSQL 行被更新，并确认运行期默认构造器不再创建业务 `filestorage/...` 目录。

## 风险

- 影响面较大：多个模块同时替换持久化。
- 共享 PostgreSQL 配置缺失时运行期启动或首次访问会失败。
- 既有本地 `.ser` 数据不会自动出现在新后端，除非手工迁移。
- PostgreSQL/Jackson 相关依赖 jar 必须在 Cosmic 运行 classpath 中可用。

## 验证

- `openspec validate replace-filestorage-with-postgresql --strict`
- `openspec validate --all --strict`
- JDK 8 下执行 `.\gradlew.bat clean build`
- 通过显式 live-test 属性和数据库凭据启用聚焦 PostgreSQL live tests。
- 使用 Kingdee UI 后，在 `public` 表中执行手工 SQL 检查。
