## 1. 配置与依赖审核

- [x] 1.1 确认 `cyancruise_app` 密码注入方式、DDL 权限和建表初始化策略；数据库固定为 `cyancruise`，schema 固定为 `public`。
- [x] 1.2 在说明 JDK 8 与 Cosmic/KDDT 兼容性后，再新增 PostgreSQL JDBC 依赖。
- [x] 1.3 在示例配置中新增画像存储相关配置项，只使用占位值，不写真实密码。

## 2. 表结构与初始化

- [x] 2.1 起草画像草稿、画像快照、画像 fact、派生统一画像四张表的 PostgreSQL 手工 DDL。
- [x] 2.2 如保留程序初始化能力，实现由 `cc001.profile.postgresql.initialize` 控制且默认关闭的幂等初始化或表结构检查 helper。
- [x] 2.3 确保初始化逻辑不会删除已有表或已有数据。

## 3. 存储适配器

- [x] 3.1 实现基于配置的画像存储工厂选择逻辑。
- [x] 3.2 为画像草稿实现 `PostgresqlCareerProfileStorage`。
- [x] 3.3 实现 PostgreSQL 画像快照持久化。
- [x] 3.4 实现 PostgreSQL 画像 facts 持久化。
- [x] 3.5 实现 PostgreSQL 派生统一画像持久化。
- [x] 3.6 当 PostgreSQL 未启用或配置不完整时，保留文件存储 fallback。

## 4. 测试与验证

- [x] 4.1 增加不连接真实数据库的配置 fallback 和适配器选择测试。
- [x] 4.2 增加由显式测试属性控制的真实 PostgreSQL 联调测试。
- [x] 4.3 验证草稿合并、清空草稿、快照重载、facts 重载、派生画像重载和用户隔离。
- [x] 4.4 运行 `openspec validate connect-career-profile-postgresql-storage --strict`。
- [x] 4.5 使用 JDK 8 运行相关 Gradle 目标测试。
- [x] 4.6 使用 JDK 8 运行 `.\gradlew.bat clean build`。

## 5. 文档

- [x] 5.1 更新迁移说明，记录 PostgreSQL 表映射、配置项和回滚步骤。
- [x] 5.2 记录手工 DBA SQL；如果允许程序初始化，则同时说明该能力默认关闭且不得执行破坏性 DDL。
- [x] 5.3 记录验证结果，但不得记录数据库密码或私有凭据。
