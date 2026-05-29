## Why

CareerLoop 已经具备工作台入口、平台挂载、生产身份边界、文件服务边界和多项 `/cc001/*` 后端契约，但当前 webapp 仍主要停留在单页入口和动作卡片层面，用户无法在苍穹 webapp 内按 IPD 主循环完成连续页面流程。现在需要在不迁移 Vue/uni-app 实现的前提下，把 IPD 页面语义重建为 CyanCruise/Cosmic 可发布的页面状态和 route/API 契约。

## What Changes

- 新增 CareerLoop webapp 页面壳能力，覆盖工作台、onboarding、今日行动、测评、简历、文件上传预览、简历诊断、职业计划、模拟面试、求职助手、消息中心、就业洞察和职业资源等页面状态。
- 扩展现有 `webapp/isv/v620/careerloop` 静态入口，使 hash route 不再只是“入口已就绪”提示，而是展示可审阅、可降级、可调用既有 WebAPI 的页面面板。
- 更新 `careerloop-routes.json` 中页面 publishability、IPD 来源、WebAPI 消费、fallback 和暂不迁移项，使页面与苍穹菜单/KDDT 挂载审阅保持一致。
- 记录并保留暂不迁移项：IPD Vue/uni-app 页面源码、Pinia/store、Vite/uView、小程序生命周期、微信运行时、真实 AI provider、真实外部内容抓取、语音/数字人面试、管理后台完整页面和生产 datamodel 适配。
- 不引入新的前端构建链或数据库结构变更；页面继续使用仓库内静态 webapp 资源和已有 Cosmic WebAPI 契约。

## Capabilities

### New Capabilities
- `webapp-careerloop-pages`: 定义 CareerLoop 在 CyanCruise/Cosmic webapp 中的多页面壳、route-state、页面数据绑定、降级状态、迁移边界和验证要求。

### Modified Capabilities
- `webapp-careerloop-entry`: 在既有入口契约上补充从单一工作台入口扩展到多页面 route-state 的导航、页面状态和 route/API map 一致性要求。

## Impact

- IPD 来源：`F:\Project\IPD\frontend\src\pages.json`、`pages\home`、`pages\onboarding`、`pages\agent`、`pages\assessment`、`pages\resume`、`pages\resume-ai`、`pages\interview`、`pages\assistant`、`pages\messages`、`pages\cdut-employment`、`pages\map`，以及 `api\agent.ts`、`api\assessment.ts`、`api\career.ts`、`api\resume.ts`、`api\interview.ts`、`api\ai.ts`、`api\notification.ts`、`api\home.ts`、`api\cdutEmployment.ts`、`api\file.ts`、`api\user.ts`。
- CyanCruise 目标：`webapp/isv/v620/careerloop/index.html`、`assets/app.js`、`assets/styles.css`、`careerloop-routes.json`、`validate-routes.js`、`README.md`（如需补充）、`docs/ipd-to-cyancruise-migration-map.md`。
- 验证影响：`node webapp\isv\v620\careerloop\validate-routes.js`、`node --check webapp\isv\v620\careerloop\assets\app.js`、OpenSpec 严格校验、JDK 8 `.\gradlew.bat clean build`。
- 运行边界：页面仅消费既有 `/cc001/*` WebAPI 和生产身份上下文；不直接依赖 IPD 源项目、Spring Boot、JPA、Flyway、Vue、uni-app 或 Java 17。
