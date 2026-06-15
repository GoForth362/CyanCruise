## 1. OpenSpec 与 SQL

- [x] 1.1 审查并确认本 OpenSpec change 后再实现。
- [x] 1.2 为职业计划、简历、诊断、面试和 AI 助手聊天表新增手工 PostgreSQL DDL。
- [x] 1.3 确认 DDL 不包含真实密码、本地路径、破坏性语句或自动迁移假设。
- [x] 1.4 在 `gradle.properties.example` 中补充共享 `cc001.storage.*` 占位配置。

## 2. 共享 PostgreSQL 配置

- [x] 2.1 新增 `PostgresqlStorageConfig`，支持 system properties 和环境变量。
- [x] 2.2 新增工厂/辅助逻辑，校验必需 PostgreSQL 配置，不完整时快速失败。
- [x] 2.3 仅在必要范围保留画像专用配置兼容，但运行期画像存储不再回退文件。
- [x] 2.4 将共享 PostgreSQL 测试属性从 Gradle 转发到测试 JVM。

## 3. PostgreSQL 适配器

- [x] 3.1 实现 `PostgresqlCareerPlanStorage`。
- [x] 3.2 实现 `PostgresqlResumeStorage`。
- [x] 3.3 实现 `PostgresqlResumeDiagnosisStorage`。
- [x] 3.4 实现 `PostgresqlInterviewStorage`。
- [x] 3.5 实现 `PostgresqlAssistantChatStorage`。
- [x] 3.6 复用 JDK 8 兼容的 JDBC 和 Jackson 模式；不引入 Spring Boot、JPA、Flyway、Lombok 或 Java 9+ API。

## 4. 运行期接线

- [x] 4.1 将计划、简历、诊断、面试、AI 助手聊天和画像服务的默认构造器改为使用 PostgreSQL 工厂。
- [x] 4.2 移除已迁移业务状态模块对 `File*Storage` 的静默运行期回退。
- [x] 4.3 对需要单元隔离的测试保留内存或显式注入 storage。
- [x] 4.4 确认运行期默认路径不会创建 `filestorage/career-profile`、`filestorage/career-plan`、`filestorage/resume-core`、`filestorage/resume-diagnosis`、`filestorage/interview-core` 或 `filestorage/assistant-chat`。

## 5. 测试

- [x] 5.1 新增共享配置完整性和失败消息的单元测试。
- [ ] 5.2 为计划、简历、诊断、面试和 AI 助手聊天行为新增 PostgreSQL 适配器测试。
- [x] 5.3 新增或更新 WebAPI/service 测试，避免默认路径被本地 `gradle.properties` 污染。
- [ ] 5.4 新增由显式 live-test 属性控制的可选 PostgreSQL live tests。
- [ ] 5.5 验证用户隔离、排序、删除、跨适配器实例重载和 ID 分配。

## 6. 验证

- [x] 6.1 执行 `openspec validate replace-filestorage-with-postgresql --strict`。
- [x] 6.2 执行 `openspec validate --all --strict`。
- [x] 6.3 在 JDK 8 下执行 `.\gradlew.bat clean build`。
- [ ] 6.4 如果已配置 live PostgreSQL，针对 `10.0.0.8:5432/cyancruise` 执行聚焦 live tests。
- [ ] 6.5 记录 Kingdee Cosmic 服务启动所需的部署/运行期配置。
