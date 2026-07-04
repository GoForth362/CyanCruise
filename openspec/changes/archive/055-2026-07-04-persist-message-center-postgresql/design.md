## Context

CyanCruise 已完成通知/订阅 DTO、helper、应用服务、WebAPI、管理端公告入口和消息中心路由，但 `NotificationsSubscriptionsApplicationService` 默认使用 `InMemoryNotificationStorage`。这导致站内通知、管理公告和周报通知只能在进程生命周期内存在。

金蝶平台侧已规划 `v620_cc_notice` 业务对象，字段包括 `v620_noticeid`、`v620_userid`、`v620_noticetype`、`v620_title`、`v620_content`、`v620_linkroute`、`v620_status`、`v620_adminid`、`v620_createdat`、`v620_readat`。本次先用本地 PostgreSQL 保存同等语义，避免直接依赖平台业务对象 API，同时为后续替换 adapter 保持字段映射清晰。

## Goals / Non-Goals

**Goals:**

- 通知记录在本地 PostgreSQL 中持久化，并在服务重启后可继续读取。
- PostgreSQL 表字段和状态语义对齐 `v620_cc_notice`。
- 消息中心页面提供普通用户能使用的消息列表、未读数、已读和归档删除体验。
- 管理端公告继续复用现有通知推送边界，不改变 `/cc001/admin/broadcast` 契约。
- 保持 JDK 1.8 兼容，不新增依赖。

**Non-Goals:**

- 不实现最终金蝶业务对象存储 adapter。
- 不接入真实微信订阅/模板消息发送。
- 不新增管理员、权限组或平台安全模型。
- 不迁移 IPD Spring Boot、JPA、Flyway 或 Vue/uni-app 代码。

## Decisions

1. 使用现有 `NotificationStorage` 边界承载 PostgreSQL 实现

   新增 `PostgresqlNotificationStorage`，由 `CyanCruiseStorageFactory.notificationStorage()` 按 `cc001.storage.backend=postgresql` 选择。应用服务默认构造器只依赖工厂，不把 PostgreSQL 细节泄露给 WebAPI。

2. 本地表使用 `cc_notice`，字段语义贴近 `v620_cc_notice`

   表名仍按本地 PostgreSQL 约定使用 `cc_notice`，字段使用逻辑名：`notice_id`、`user_id`、`notice_type`、`title`、`content`、`link_route`、`status`、`admin_id`、`created_at`、`read_at`、`payload_json`。后续接平台对象时只需映射到 `v620_*` 字段。

3. 删除操作采用归档状态

   `NotificationStorage.delete` 在 PostgreSQL 实现中更新 `status=archived`，并从用户消息列表排除。这样贴合 `v620_status` 的 `sent/read/archived` 模型，也避免用户误删后数据不可追溯。

4. 已读状态双写到 DTO 和结构化字段

   DTO 仍保留 `readFlag`；PostgreSQL 结构化字段使用 `status=sent/read/archived` 和 `read_at`。读取时按状态还原 `readFlag`，保存时按 `readFlag` 生成状态。

5. 消息中心使用现有 API 服务封装

   前端只消费已有 `notifications`、`notificationUnread`、`notificationRead`、`notificationReadAll`、`notificationDelete`、`subscriptionQuota` 和 `weeklyReport` key。页面文案使用中文，不暴露内部缩写。

## Risks / Trade-offs

- [Risk] PostgreSQL 表与后续金蝶业务对象存在字段差异。→ Mitigation: 本次表字段按 `v620_cc_notice` 的逻辑字段逐项映射，并在 SQL 注释和 OpenSpec 中记录状态转换。
- [Risk] 当前通知推送没有管理员 ID 字段。→ Mitigation: `admin_id` 暂留空，管理端广播仍通过 `type=ADMIN_BROADCAST` 和内容区分；后续可扩展 `NotificationPushRequest` 或广播服务上下文。
- [Risk] 前端运行时已有未提交改动。→ Mitigation: 只新增消息中心相关服务方法和渲染分支，避免重写管理端和公共运行时结构。
- [Risk] 自动建表不适合生产。→ Mitigation: 保留 `cc001.storage.postgresql.initialize` 开关，并提供 `datamodel/postgresql-notice-storage.sql` 手动建表脚本。

## Migration Plan

1. 执行 `datamodel/postgresql-notice-storage.sql`，或在本地开发环境开启 `cc001.storage.postgresql.initialize=true`。
2. 配置 `cc001.storage.backend=postgresql` 及 PostgreSQL 连接信息。
3. 部署后通过管理端发送公告，普通用户进入消息中心验证列表、未读数、已读和归档。
4. 回滚时可切回非 PostgreSQL backend，系统会回到内存通知存储；既有 `cc_notice` 数据保留不删除。

## Open Questions

- 后续接金蝶业务对象时，群发公告是否保留“每个用户一条记录”，还是允许 `v620_userid` 为空表示全员公告并在读取时展开？
