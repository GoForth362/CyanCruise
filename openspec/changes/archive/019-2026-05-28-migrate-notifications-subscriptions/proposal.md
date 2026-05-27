## Why

CareerLoop 主循环已经具备画像、测评、简历、今日行动、职业计划、模拟面试、助手、AI 基础设施、webapp 入口和就业资源，但用户还缺少统一的消息中心、未读状态和关键求职事件提醒。IPD 已沉淀站内通知、微信订阅授权/配额和周报通知的业务语义，现在应先迁移通知/订阅契约，为后续工作台、消息页和周期性复盘打基础。

## What Changes

- 新增通知/订阅能力规格：定义站内通知类型、用户归属、未读计数、列表、标记已读、全部已读、删除、深链和最佳努力写入语义。
- 抽取 IPD `NotificationService`、`NotificationController`、`NotificationTypes`、`Notification`、`WechatSubscribeService`、`WxSubscribeQuota`、`WeeklyReportService/Job`、前端 `notification.ts`、`wxSubscribe.ts` 和消息页中的业务规则、数据语义、流程和接口契约。
- 在后续 apply 阶段为 CyanCruise 增加 JDK 8 DTO、helper、应用服务/WebAPI、可替换存储边界和 webapp route/API 映射，优先实现站内通知和订阅配额的本地契约。
- 定义通知写入边界：业务服务推送通知 SHALL 为 best-effort，失败不得中断测评提交、面试结束、简历诊断、周报生成等主流程。
- 定义订阅边界：迁移微信订阅的“授权结果记录、模板配额、发送前消耗额度、无额度/无 openid/无模板时跳过”语义，但不直接迁移微信网络调用、Redis token 缓存、生产 appid/secret、uni-app `wx.requestSubscribeMessage` 运行时。
- 定义周报边界：迁移周报通知的输入、摘要、投递和跳过语义，但不直接迁移 Spring `@Scheduled`、AI 生成实现或面试 radar JSON 解析细节；后续可接 Cosmic 调度或手动触发入口。
- 本 change 先生成 proposal、spec、design、tasks 文档，等待审阅通过后再 apply 实现代码。

## Capabilities

### New Capabilities

- `notifications-subscriptions`: 定义 CyanCruise CareerLoop 的站内通知、订阅授权/配额、周报通知、消息中心 WebAPI、webapp 消费契约、降级状态和迁移边界。

### Modified Capabilities

- 无。本次新增通知/订阅规格，不修改已归档主循环能力的 SHALL；后续实现可被测评、面试、简历诊断、今日行动、职业计划、就业洞察等能力以 best-effort 方式调用。

## Impact

- IPD 来源路径：
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\NotificationController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\WechatSubscribeController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\NotificationTypes.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\Notification.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\WxSubscribeQuota.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\repository\NotificationRepository.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\repository\WxSubscribeQuotaRepository.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\NotificationService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\NotificationServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\WechatSubscribeService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\WechatSubscribeServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\WeeklyReportService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\WeeklyReportJob.java`
  - `F:\Project\IPD\frontend\src\api\notification.ts`
  - `F:\Project\IPD\frontend\src\utils\wxSubscribe.ts`
  - `F:\Project\IPD\frontend\src\pages\messages\index.vue`
- CyanCruise 目标模块：
  - `code/base/v620-cc001-base-common/`：通知、未读计数、订阅配额、授权结果、周报摘要 DTO 与通知类型常量。
  - `code/base/v620-cc001-base-helper/`：通知分类、未读聚合、订阅配额变更、周报摘要 fallback 和 deep link 规则。
  - `code/cloud01/v620-cc001-cloud01-app01/`：通知/订阅应用服务、存储边界、订阅发送 adapter 占位和 Cosmic WebAPI。
  - `webapp/isv/v620/careerloop/`：消息中心 route/API 映射与通知入口状态。
  - `openspec/specs/`：新增通知/订阅主规格。
  - `docs/ipd-to-cyancruise-migration-map.md`：实现阶段更新迁移地图，记录来源、目标、数据映射、暂不迁移项和验证方式。
- API 影响：预计新增 `/cc001/notifications/*` 与 `/cc001/subscriptions/*` 只读/写入 WebAPI 或等价 Cosmic WebAPI 契约；不改变既有测评、面试、简历、计划、助手和就业洞察 API。
- 依赖影响：默认不新增外部依赖，不引入 IPD 的 Spring/JPA/Flyway/Redis/Java 17 HTTP/WeChat SDK/uni-app/Vue 依赖。若 apply 阶段确需平台消息或调度能力，必须单独说明必要性并确认不破坏 Cosmic/KDDT/JDK 8 约束。
