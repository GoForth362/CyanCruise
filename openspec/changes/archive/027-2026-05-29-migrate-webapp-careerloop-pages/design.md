## Context

当前 `webapp/isv/v620/careerloop` 已经提供 CareerLoop 静态入口、route/API map、生产/开发身份模式、平台挂载 metadata 和多项后端 WebAPI 调用包装。后端侧已迁移画像、onboarding、今日行动、测评、简历、文件、简历诊断、职业计划、模拟面试、助手聊天、通知、就业洞察、资源和管理治理等契约，但前端仍以工作台和动作入口为主，hash route 还没有承载对应页面流程。

IPD 页面来源主要来自 `F:\Project\IPD\frontend\src\pages.json`、`pages\home`、`pages\onboarding`、`pages\agent`、`pages\assessment`、`pages\resume`、`pages\resume-ai`、`pages\interview`、`pages\assistant`、`pages\messages`、`pages\cdut-employment`、`pages\map` 以及对应 `api\*.ts`。迁移目标不是搬运 Vue/uni-app 实现，而是把页面语义、数据语义、状态机和接口契约重建为苍穹 webapp 可运行的静态资源。

## Goals / Non-Goals

**Goals:**
- 在现有静态 webapp 内建立多页面 route-state，使主循环页面可被打开、刷新、降级和审阅。
- 让每个页面明确绑定既有 `/cc001/*` WebAPI、身份要求、空态/错误态/未实现态和下一步动作。
- 保持 Cosmic 生产身份优先，开发 fallback 仅用于本地验证。
- 更新 route map、验证脚本和迁移地图，使页面发布范围与平台挂载 metadata 一致。
- 保持 JDK 8 构建兼容，不引入新的前端构建链。

**Non-Goals:**
- 不迁移 IPD Vue、uni-app、Pinia/store、Vite、uView、小程序生命周期或微信运行时。
- 不新增数据库表、Flyway、JPA repository 或生产 datamodel adapter。
- 不在本 change 接入真实 AI provider、真实外部内容抓取、微信订阅发送、语音/数字人面试、完整管理后台页面或新的文件 SDK。
- 不改变后端 WebAPI 路径和 DTO 契约；页面只消费已经迁移的接口。

## Decisions

1. 使用静态 route-state 页面壳，而不是引入 SPA 框架。
   - 理由：仓库当前 webapp 已是静态资源入口，苍穹 webapp 发布也以该路径为目标；继续使用原生 HTML/CSS/JS 可以避免新增依赖和构建链风险。
   - 备选：引入 Vue/React 重新构建页面。该方案会扩大 Cosmic/KDDT 兼容验证范围，也容易误把 IPD 实现迁入，因此暂不采用。

2. 页面按“数据面板 + 操作入口 + 降级状态”组织。
   - 理由：多数后端契约已经可用，但部分生产 adapter 仍可能 disabled 或 unavailable；页面需要在接口不可用时保持可导航，而不是表现为完整生产闭环。
   - 备选：为每个 IPD 页面一次性复刻完整交互。该方案会跨越 AI、文件、微信、内容抓取等未完成平台能力，范围过大。

3. route/API map 作为页面契约源。
   - 理由：`careerloop-routes.json` 已经承载 IPD 来源、目标 hash、WebAPI、平台挂载和 fallback。页面实现、验证脚本和迁移地图都应围绕它收敛。
   - 备选：在 JS 中硬编码页面清单。该方案会让发布审阅和页面行为分叉。

4. 保留生产身份边界，不用页面层伪造身份。
   - 理由：身份 adapter 已完成，生产模式下用户私有接口必须来自 Cosmic 登录上下文；页面仅在 development fallback 明确开启时使用 query/localStorage/manual userId。
   - 备选：继续允许生产 query userId。该方案会破坏前序身份迁移成果。

## Risks / Trade-offs

- [Risk] 多页面壳可能被误解为所有能力已生产完成。  
  Mitigation：每个页面展示 route status、fallback 和 pending 能力；route map 继续区分 `available`、`entry-only`、`pending`、`admin-only`。
- [Risk] 页面调用多个 WebAPI 后局部失败导致整体不可用。  
  Mitigation：页面按面板独立渲染加载态、空态和错误态，失败不阻断导航。
- [Risk] 静态 JS 体积和状态管理复杂度上升。  
  Mitigation：抽取轻量 page registry、render helper 和 API helper，避免引入框架。
- [Risk] 移动端或苍穹 webview 宽度下控件重叠。  
  Mitigation：实现后用响应式约束、固定工具栏尺寸和手工截图检查，配合 `node --check` 和 route 校验。

## Migration Plan

1. 建立页面 registry，覆盖 route map 中的用户主循环页面，并保持 hash route 可直接访问。
2. 扩展 `index.html` 和 `assets/styles.css`，提供主导航、页面容器、状态条、面板、表单和列表的稳定布局。
3. 扩展 `assets/app.js`，将工作台、onboarding、今日行动、测评、简历/文件、诊断、职业计划、面试、助手、消息、就业洞察、资源等页面接入既有 API helper。
4. 更新 `careerloop-routes.json`、`validate-routes.js` 和 README/迁移地图中对应页面状态、暂不迁移项和验证结果。
5. 运行静态 JS 检查、route 校验、OpenSpec 严格校验和 JDK 8 Gradle 构建。若页面变更有问题，可回滚本 change 的 webapp 静态资源和 route map。

## Open Questions

- 真实苍穹租户中是否需要把全部用户页面发布为独立菜单，还是只发布工作台并通过 hash deep link 导航？本 change 先保持 route map 可审阅，实际菜单发布由平台挂载配置决定。
