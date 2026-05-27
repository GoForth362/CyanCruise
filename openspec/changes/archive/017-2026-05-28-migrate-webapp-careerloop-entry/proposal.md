## Why

CareerLoop 后端主循环已经完成画像、测评、简历、今日行动、职业计划、模拟面试、简历诊断、助手聊天、Cosmic datamodel 适配和 AI 基础设施的基础迁移，但 `webapp/` 仍只有占位目录。现在需要把 IPD 小程序中的主入口流程抽取为 CyanCruise 可承载的 webapp 页面契约，让用户能从一个可用入口进入今日行动、onboarding、测评、简历、面试和助手。

## What Changes

- 新增 CareerLoop webapp 入口能力规格：定义首屏工作台、onboarding gate、主循环快捷入口、页面状态和 WebAPI 契约映射。
- 抽取 IPD `pages.json`、home、onboarding、agent、assessment、resume、interview、assistant 页面中的用户流程和跳转语义，但不直接迁移 Vue/uni-app 实现、生命周期、组件写法或运行时依赖。
- 在后续 apply 阶段于 `webapp/isv/v620/` 下重建 CyanCruise 页面资源入口，优先覆盖 CareerLoop 主循环可试用路径，而不是一次性重做所有 IPD 页面。
- 定义 webapp 对已迁移 Cosmic WebAPI 的消费边界：显式 userId、加载/空态/错误/后端不可用状态、未完成能力的降级展示和页面跳转目标。
- 记录暂不迁移项：IPD uni-app/Vue 源码、管理后台、微信订阅、消息中心、CDUT 就业内容详情、文件上传预览、语音/数字人面试、生产登录态和真实前端构建链路。
- 本 change 先生成 proposal、spec、design、tasks 文档，等待审阅通过后再 apply 实现代码。

## Capabilities

### New Capabilities

- `webapp-careerloop-entry`: 定义 CyanCruise webapp 中 CareerLoop 首个可用入口、主循环页面导航、onboarding gate、页面到 WebAPI 的契约映射、状态降级和迁移边界要求。

### Modified Capabilities

- 无。本次新增 webapp 入口规格，不修改已归档后端能力的 SHALL；后续实现只消费既有 WebAPI/DTO 契约。

## Impact

- IPD 来源路径：
  - `F:\Project\IPD\frontend\src\pages.json`
  - `F:\Project\IPD\frontend\src\pages\home\index.vue`
  - `F:\Project\IPD\frontend\src\pages\onboarding\index.vue`
  - `F:\Project\IPD\frontend\src\pages\agent\index.vue`
  - `F:\Project\IPD\frontend\src\pages\agent\profile.vue`
  - `F:\Project\IPD\frontend\src\pages\assessment\index.vue`
  - `F:\Project\IPD\frontend\src\pages\assessment\quiz.vue`
  - `F:\Project\IPD\frontend\src\pages\assessment\result.vue`
  - `F:\Project\IPD\frontend\src\pages\resume\index.vue`
  - `F:\Project\IPD\frontend\src\pages\resume-ai\index.vue`
  - `F:\Project\IPD\frontend\src\pages\interview\index.vue`
  - `F:\Project\IPD\frontend\src\pages\interview\start.vue`
  - `F:\Project\IPD\frontend\src\pages\interview\chat.vue`
  - `F:\Project\IPD\frontend\src\pages\interview\history.vue`
  - `F:\Project\IPD\frontend\src\pages\interview\report.vue`
  - `F:\Project\IPD\frontend\src\pages\assistant\index.vue`
  - `F:\Project\IPD\frontend\src\pages\assistant\history.vue`
  - `F:\Project\IPD\frontend\src\utils\onboardingGate.ts`
  - `F:\Project\IPD\frontend\src\utils\onboardingSync.ts`
  - `F:\Project\IPD\frontend\src\api\agent.ts`
  - `F:\Project\IPD\frontend\src\api\assessment.ts`
  - `F:\Project\IPD\frontend\src\api\career.ts`
  - `F:\Project\IPD\frontend\src\api\resume.ts`
  - `F:\Project\IPD\frontend\src\api\interview.ts`
  - `F:\Project\IPD\frontend\src\api\ai.ts`
- CyanCruise 目标模块：
  - `webapp/isv/v620/`：CareerLoop webapp 入口页面、静态资源、路由/接口契约说明。
  - `openspec/specs/`：新增 webapp 入口主规格。
  - `docs/ipd-to-cyancruise-migration-map.md`：实现阶段更新迁移地图，记录 webapp 入口状态、来源、目标和后续项。
- API 影响：默认不新增后端 WebAPI；页面消费已迁移的画像/onboarding、今日行动、测评、简历、职业计划、模拟面试、简历诊断和助手聊天 WebAPI。若 apply 阶段发现入口聚合确有必要，应先以 webapp 本地聚合或薄契约说明处理，避免扩大后端范围。
- 依赖影响：默认不新增外部依赖，不引入 Node/Vue/uni-app 构建链；若目标平台需要页面打包配置，必须保持 Cosmic/KDDT 模板约束并说明必要性。
