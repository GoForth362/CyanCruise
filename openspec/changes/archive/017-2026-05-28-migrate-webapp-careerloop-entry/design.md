## Context

CyanCruise 当前已经具备 CareerLoop 主循环的后端契约：`/cc001/career-profile`、`/cc001/career-agent`、`/cc001/assessment`、`/cc001/resume`、`/cc001/resume-diagnosis`、`/cc001/career-plan`、`/cc001/interview` 和 `/cc001/assistant-chat`。但是 `webapp/isv/v620/` 仍只有 `.gitkeep`，多个后端迁移项都把页面入口标记为后续工作。

IPD 前端是 uni-app/Vue 3 小程序，入口由 `frontend/src/pages.json` 管理，主路径包括 `pages/home/index`、`pages/onboarding/index`、`pages/agent/index`、`pages/assessment/*`、`pages/resume/*`、`pages/interview/*` 和 `pages/assistant/*`。本次迁移只抽取页面流程、信息架构、接口契约和状态语义，在 CyanCruise `webapp` 中重建入口，不复制 IPD 技术实现。

## Goals / Non-Goals

**Goals:**

- 建立 CyanCruise CareerLoop 第一个可用 webapp 入口，展示今日行动、画像/目标岗位状态、简历/测评/面试/助手快捷入口和 onboarding 引导。
- 定义页面路由和 WebAPI 映射，保证页面消费已迁移后端契约时使用显式 `userId`，并能处理加载、空态、错误和后端不可用。
- 保留 IPD 主循环的用户流程：新用户先补 onboarding，老用户进入工作台，根据状态继续测评、简历、面试、诊断、计划或助手。
- 使用 `webapp/isv/v620/` 承载页面资源，不新增 Java 后端能力，不引入 Node/Vue/uni-app 构建链。
- 在实现阶段补充静态验证，确认页面资源、路由地图和接口契约文档存在且不依赖 IPD 源项目运行时。

**Non-Goals:**

- 不在 propose 阶段实现代码。
- 不迁移 IPD Vue/uni-app 源码、组件库、Pinia/store、uni storage、页面生命周期或小程序 tabBar 配置。
- 不重建所有页面细节；首轮只覆盖 CareerLoop 主循环入口和必要的二级占位/跳转契约。
- 不实现生产登录态、权限中心、微信订阅、消息中心、CDUT 就业详情、管理后台、文件上传预览、语音/数字人面试或前端流式聊天。
- 不新增后端 WebAPI，除非 apply 阶段发现已有契约无法表达入口最低可用状态，并经文档说明后单独处理。

## Decisions

### 1. 页面入口放在 `webapp/isv/v620/careerloop/`

实现阶段 SHALL 在 `webapp/isv/v620/careerloop/` 下新增 CareerLoop 入口资源，例如 `index.html`、`assets/`、`careerloop-routes.json` 和 `README.md`。该目录作为首轮页面重建的独立边界，不混入 `code/` 业务模块。

原因：`webapp/` 是 AGENTS.md 指定的页面资源归属，`isv/v620/` 是当前仓库已有占位路径。独立 `careerloop/` 目录可以让后续 Cosmic 页面适配、KDDT 表单挂载或静态资源发布逐步接入。

替代方案是直接在根 `webapp/isv/v620/` 放散文件；这会让后续页面扩展和资产归属变得松散，不采用。

### 2. 首屏是工作台，不做营销落地页

入口首屏 SHALL 是 CareerLoop 工作台：今日行动、目标岗位/画像状态、主循环能力卡片和 onboarding 提醒。它 SHALL 直接服务用户下一步行动，而不是介绍产品卖点。

原因：IPD 的核心价值是“打开后知道下一步做什么”。后端已经有今日行动规则，webapp 应优先把闭环串起来。

替代方案是先做品牌首页或纯导航页；这无法验证已迁移后端契约，也不能恢复 CareerLoop 主循环体验，不采用。

### 3. 路由保留 IPD 语义，但使用 CyanCruise 命名空间

实现阶段 SHALL 建立路由地图，将 IPD 路径映射到 CyanCruise 页面入口，例如：

| IPD 路径 | CyanCruise 入口语义 |
| --- | --- |
| `pages/home/index` | CareerLoop 工作台 |
| `pages/onboarding/index` | onboarding gate/表单 |
| `pages/agent/index` | 今日行动详情 |
| `pages/assessment/index` | 测评入口与结果摘要 |
| `pages/resume/index` | 简历列表/最近简历摘要 |
| `pages/resume-ai/index` | 简历诊断入口 |
| `pages/interview/index` | 模拟面试入口 |
| `pages/assistant/index` | 助手聊天入口 |

原因：保留 IPD 路径语义便于迁移追踪，但 CyanCruise 不应复用 uni-app 路由运行时。

替代方案是完全重新命名页面，不保留来源映射；这会削弱迁移可审计性，不采用。

### 4. 页面只消费已迁移 WebAPI，状态聚合放在前端契约层

首轮 webapp SHALL 消费既有 WebAPI：画像快照、今日行动、测评提交、简历列表、简历诊断、职业计划、面试历史和助手聊天。页面可以在前端以轻量方式聚合这些状态，但不新增后端聚合 API 作为默认方案。

原因：现有后端契约已经按能力拆分并经过测试。首轮 webapp 先验证契约可消费性，避免为了页面便利过早扩大后端表面。

替代方案是新增 `/cc001/careerloop/dashboard` 聚合接口；如果后续性能或平台调用限制证明必要，可单独提 change，不在本轮默认采用。

### 5. 使用显式 `userId` 和开发态降级

webapp 契约 SHALL 记录用户身份来源：生产态等待 Cosmic 登录态接入，开发/验证态可通过查询参数、本地配置或页面输入提供 `userId`。页面 SHALL 明确区分未配置 userId、后端返回空数据、后端不可用和能力未实现。

原因：现有迁移 WebAPI 采用显式 `userId` 风格，当前尚未完成生产登录态解析。前端入口需要可验证，同时不能假装已经完成真实鉴权。

替代方案是在前端硬编码单一 userId；这会污染业务语义，也不利于后续测试，不采用。

### 6. 不引入 Vue/uni-app 构建链

实现阶段默认使用可由 Cosmic webapp 资源承载的静态 HTML/CSS/JS 或平台兼容资源形态。不得引入 IPD 的 Vue、uni-app、Vite、Pinia、uView 等运行时依赖。

原因：迁移目标是 Cosmic/JDK 8 工程，用户已明确要求不直接迁移 Vue/uni-app 实现。无构建链入口也更容易在仓库内用静态检查验证。

替代方案是复制 IPD 前端并改接口；这违反迁移规则，不采用。

## Risks / Trade-offs

- [Risk] 静态 webapp 首轮体验不如完整 Vue 应用丰富。-> Mitigation：先覆盖主循环入口和契约验证，后续再按 Cosmic 平台能力增强交互。
- [Risk] 多个 WebAPI 分散调用会增加页面复杂度。-> Mitigation：在 `careerloop-routes.json` 或 README 中记录接口映射；若调用成本成为问题，再提后端聚合 change。
- [Risk] 当前生产登录态未接入，页面验证可能依赖开发态 userId。-> Mitigation：显式标注开发态身份来源和生产态待接入项，不硬编码用户。
- [Risk] IPD 页面中部分能力尚未迁移，例如消息、订阅、内容资源、语音面试。-> Mitigation：首屏展示可用入口，对后续项使用不可用/后续状态，不制造假入口。
- [Risk] Cosmic webapp 最终承载方式可能需要平台配置。-> Mitigation：首轮保持普通资源目录和契约文档，避免绑定尚未验证的平台专有配置。

## Migration Plan

1. 盘点 IPD `pages.json` 与主循环页面，形成 route/API 映射。
2. 在 `webapp/isv/v620/careerloop/` 新增入口资源和契约说明。
3. 实现工作台首屏、onboarding gate、主能力入口、状态降级和开发态 userId 输入/读取策略。
4. 将入口动作映射到已迁移 WebAPI 或后续占位状态，不新增外部依赖。
5. 更新 `docs/ipd-to-cyancruise-migration-map.md`，记录 webapp 入口迁移状态、来源路径、目标模块和暂不迁移项。
6. 运行 OpenSpec 校验、静态资源检查和 JDK 8 Gradle 构建验证。

Rollback：如页面资源无法被目标平台承载，可删除 `webapp/isv/v620/careerloop/` 新增资源并保留 OpenSpec 规格；后端 WebAPI 不受影响。

## Open Questions

- Cosmic 最终页面发布是否需要额外 KDDT/表单配置，还是首轮 webapp 静态资源即可挂载。
- 生产态当前用户身份应从 Cosmic 上下文、统一登录参数还是后端代理中解析，需要后续平台适配确认。
- 首轮是否需要把 `CareerAgentTodayDto` 中的跳转 `target` 统一改为 CyanCruise route key，还是在 webapp route map 中兼容 IPD path。
