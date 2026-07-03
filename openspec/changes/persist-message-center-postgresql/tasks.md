## 1. PostgreSQL Notice Storage

- [x] 1.1 新增 `cc_notice` 建表脚本，字段对齐 `v620_cc_notice` 语义。
- [x] 1.2 实现 `PostgresqlNotificationStorage`，支持保存、读取、列表、已读状态和归档删除。
- [x] 1.3 将通知存储接入 `CyanCruiseStorageFactory` 和通知应用服务默认构造器，保留内存降级。

## 2. Message Center Webapp

- [x] 2.1 补齐前端 API key 和消息中心运行时服务方法。
- [x] 2.2 实现消息中心页面列表、未读数、分组、已读、全部已读、归档删除、空状态和错误状态。
- [x] 2.3 实现 10 条一页分页、消息标签展示和管理员公告特殊标识。

## 3. Verification

- [x] 3.1 增加或更新后端聚焦测试，覆盖 PostgreSQL 存储语义可替换性或应用服务通知流程。
- [x] 3.2 运行 OpenSpec 严格校验和可行的 Gradle/前端静态校验。
