## Context

CyanCruise 已完成 CareerLoop 主循环的主要后端能力、AI 基础设施、webapp 入口、就业洞察/资源入口。当前 webapp route map 仍将消息提醒、通知/订阅标记为 pending。IPD 的通知能力由 `NotificationServiceImpl` 提供站内消息写入、列表、未读计数、已读和删除；`WechatSubscribeServiceImpl` 处理订阅授权配额、模板发送和微信 access token；`WeeklyReportService/Job` 根据近 14 天面试记录生成周报通知。

本次迁移目标是先建立 Cosmic/JDK 8 兼容的通知/订阅契约和本地边界。IPD 中 Spring Controller、JPA repository、Flyway SQL、Redis、Java 17 `HttpClient`、微信网络 API、Spring `@Scheduled`、uni-app `wx.requestSubscribeMessage` 和 Vue 页面不直接迁移。

## Goals / Non-Goals

**Goals:**

- 定义 JDK 8 兼容的通知 DTO、通知类型常量、未读计数、操作结果、订阅配额、授权结果和周报摘要 DTO。
- 提供纯 Java helper 规则：通知类型分类、消息中心 tab 归类、未读聚合、所有权校验结果、订阅授权配额增量、发送前额度消耗、周报 fallback 摘要和 deep link 校验。
- 提供 app01 通知/订阅应用服务、存储边界、发送 adapter 占位和 Cosmic WebAPI。
- 保持通知写入 best-effort：推送失败 SHALL 不影响面试、测评、简历诊断、周报等主流程。
- 支持 webapp 消费：列表、未读数、单条已读、全部已读、删除、订阅授权记录、订阅配额查询和消息中心 route/API 映射。
- 更新迁移地图和验证流程，确保实现阶段可通过 OpenSpec、聚焦测试、静态 route 检查和 JDK 8 Gradle 构建。

**Non-Goals:**

- 不在 propose 阶段实现代码。
- 不直接迁移 IPD Spring Boot Controller、JPA/Flyway、repository、Redis、Java 17 `HttpClient`、微信 access token 网络调用、生产 appid/secret 或 Spring `@Scheduled`。
- 不迁移 IPD Vue/uni-app 消息页、Pinia/store、小程序 tabbar、`wx.requestSubscribeMessage` 运行时或真实微信模板消息发送。
- 不把通知能力强接入所有业务模块；首轮建立通知/订阅基础设施和可验证触发点，后续按场景增量接线。
- 不新增外部依赖，除非 apply 阶段证明 Cosmic/JDK 8 现有能力无法满足且明确说明必要性。

## Decisions

### 1. 站内通知先于外部订阅发送

`base-common` SHALL 定义通知 DTO 和类型常量；`base-helper` SHALL 定义分类、聚合和状态变更规则；`app01` SHALL 提供站内通知服务、存储边界和 WebAPI。外部订阅发送通过 adapter 表达，默认实现可返回 skipped/unavailable，不进行真实微信网络调用。

原因：站内通知是 Cosmic webapp 可以立即消费的主契约，微信订阅依赖生产 appid、openid、模板、额度和平台安全策略，不能在没有部署上下文时伪造。

替代方案是直接复制 IPD `WechatSubscribeServiceImpl`；该实现依赖 Redis、Java 17 HTTP、微信网络 API 和 Spring 配置，不符合当前 JDK 8/Cosmic 约束。

### 2. 通知写入必须 best-effort

业务模块调用通知推送时，写入失败 SHALL 返回明确 skipped/failed 结果或空结果，但 MUST NOT 抛出导致主业务失败的异常。用户主动读写通知时，所有权错误和参数错误 SHALL 返回可审计错误。

原因：IPD 明确将通知作为非关键路径。面试结束、测评提交、简历诊断完成时，通知失败不能破坏用户已完成的核心操作。

替代方案是所有推送失败都抛异常；这会把辅助消息系统变成主流程风险，不采用。

### 3. 用户归属和所有权校验集中处理

通知记录 SHALL 持有 userId；列表、未读计数、全部已读只作用于当前用户；单条已读和删除 SHALL 验证通知归属。WebAPI SHALL 要求显式 userId 或未来 Cosmic 登录上下文，不使用生产硬编码用户。

原因：消息中心直接承载用户隐私和业务进展，不能跨用户读写。

替代方案是在前端只按 notificationId 操作；这会产生越权风险，不采用。

### 4. 订阅配额只迁语义，不迁真实微信发送

订阅授权记录 SHALL 将 templateId -> accept/reject/ban 转为配额增量；发送前 SHALL 检查模板、openid 绑定和剩余额度；额度不足或配置缺失 SHALL skip。真实微信 access token、HTTP 调用和模板字段映射 SHALL 通过后续平台 adapter 接入。

原因：IPD 的重要业务规则是“一次授权对应一次可发送额度”，不是具体 Redis token 或 HTTP 代码。先迁移语义可以让后续平台适配有明确契约。

替代方案是忽略配额直接发送；这违反微信订阅消息授权模型，不采用。

### 5. 周报作为可触发服务，不绑定调度协议

周报服务 SHALL 能基于最近一段时间的面试/行动摘要生成通知内容和投递结果；默认 fallback 摘要应可测试。是否由 Cosmic 调度、手动 WebAPI、后台任务或自动化触发，由后续平台部署决定。

原因：Spring `@Scheduled` 不适合直接迁移，且 Cosmic 调度方案需要单独验证。

替代方案是本 change 直接复制 cron 注解；这会把平台调度和通知领域规则混在一起，不采用。

## Risks / Trade-offs

- [Risk] 首轮不接真实微信发送，用户只能看到站内通知 -> Mitigation：外部发送 adapter 返回明确 skipped/unavailable，订阅配额和授权语义先可测试。
- [Risk] 通知类型会随业务增加而扩散 -> Mitigation：统一类型常量和分类 helper，未知类型归为 system 或 generic。
- [Risk] 周报输入来源尚未完全稳定 -> Mitigation：先定义摘要输入 DTO 和 fallback 规则，后续按面试/今日行动/计划数据接线。
- [Risk] 站内通知存储首轮可能仍是文件/内存或 adapter 边界 -> Mitigation：保持 storage interface，后续可替换 Cosmic datamodel。
- [Risk] webapp 消息页完整 UI 迁移可能扩大范围 -> Mitigation：本 change 先更新 route/API 契约和入口状态，不直接迁移 uni-app 页面。

## Migration Plan

1. 新增通知/订阅 DTO、类型常量和 helper 规则。
2. 新增通知 storage、订阅配额 storage、发送 adapter 和应用服务。
3. 新增 Cosmic WebAPI：列表、未读数、已读、全部已读、删除、订阅授权记录、配额查询、周报触发/预览。
4. 在关键已迁移模块中只接入少量 best-effort 触发点，或先通过聚焦测试验证推送边界。
5. 更新 webapp `careerloop-routes.json`，将 messages/notifications 从 pending 转为可用或 entry-only 契约。
6. 更新迁移地图，运行 OpenSpec、route 静态检查、JDK 8 Gradle 测试和完整构建。

Rollback：通知/订阅是新增辅助能力。若实现异常，业务模块 SHALL 保持主流程成功，并可隐藏消息入口或显示消息服务不可用状态；既有 CareerLoop 核心 API 不变。

## Open Questions

- 生产态用户身份和 openid 绑定最终来自 Cosmic 登录上下文、用户认证表还是外部集成，需要 apply 或后续 change 明确。
- 微信模板 ID、字段映射和发送渠道是否由 Cosmic 平台消息服务承接，需要与部署环境对齐。
- 周报调度最终使用 Cosmic 调度、自动化任务还是后台管理触发，需要平台侧确认。
