## Why

CyanCruise 已有消息中心 WebAPI、通知 DTO 和管理端公告入口，但通知记录仍默认保存在进程内存中，重启后丢失，无法支撑本地联调和真实用户体验。当前已在金蝶平台完成 `v620_cc_notice` 建模，因此先用本地 PostgreSQL 落库，并按该业务对象字段语义对齐，便于后续平滑替换为金蝶业务对象存储。

## What Changes

- 新增通知记录 PostgreSQL 存储实现，接入现有 `NotificationStorage` 边界。
- 新增 `cc_notice` 本地表和初始化 SQL，字段语义对齐 `v620_cc_notice`：公告 ID、接收用户、公告类型、标题、内容、跳转页面、状态、管理员、创建时间、阅读时间和扩展 JSON。
- 将通知默认运行时存储接入 `CyanCruiseStorageFactory`：配置为 PostgreSQL 时使用本地 PostgreSQL，未配置时继续使用内存降级。
- 完善消息中心页面，展示消息列表、未读数、分组、已读、全部已读和归档删除操作。
- 保持现有 `/cc001/notifications/*` WebAPI 和 DTO 契约兼容，不引入新的外部依赖。

## Capabilities

### New Capabilities

- 无。

### Modified Capabilities

- `notifications-subscriptions`: 通知记录 SHALL 支持按 `v620_cc_notice` 语义对齐的 PostgreSQL 持久化，并支撑用户端消息中心完整读写体验。

## Impact

- 影响后端模块：`code/cloud01/v620-cc001-cloud01-app01`。
- 影响前端资源：`webapp/isv/v620/cyancruise` 消息中心页面和运行时服务。
- 新增本地建表脚本：`datamodel/postgresql-notice-storage.sql`。
- 不新增依赖；继续使用项目已有 PostgreSQL JDBC 与 JDK 1.8 兼容代码。
