# Migration Governance Delta

## MODIFIED Requirements
### Requirement: 记录 filestorage 替换范围和凭据约束
本迁移 SHALL 明确记录被替换的业务状态范围、暂不替换的文件服务范围、PostgreSQL 配置、手工 DDL、验证命令和凭据处理规则。

#### Scenario: 记录替换范围
- **WHEN** 审查本变更文档
- **THEN** 文档 SHALL 明确列出被替换的 `filestorage` 子目录以及不在本次范围内的文件服务能力

#### Scenario: 不提交真实密码
- **WHEN** 审查源码、OpenSpec artifacts、示例配置、SQL 或提交内容
- **THEN** 其中 SHALL NOT 包含真实 PostgreSQL 密码、生产密钥或本地私有环境凭据

#### Scenario: 验证实际运行环境
- **WHEN** 部署到 Kingdee Cosmic 运行环境
- **THEN** 验证记录 SHALL 区分 Gradle/test 配置和实际星瀚服务 JVM 启动配置，避免只配置 `gradle.properties` 却未影响运行服务
