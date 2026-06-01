# CareerLoop 苍穹 Webapp

本目录承载 CareerLoop 在 CyanCruise/Kingdee Cosmic webapp 资源侧的静态入口。当前入口不是 IPD Vue/uni-app 的直接搬迁，而是把 IPD 页面语义重建为可审阅、可降级、可发布的 hash route 页面状态。

## 入口文件

- `index.html`：CareerLoop webapp 壳，默认由金蝶平台侧边栏外部链接驱动，`?ccDebug=1` 时显示调试身份区、页面导航和状态卡片。
- `assets/app.js`：轻量 page registry、hash navigation、身份解析、WebAPI 调用和页面状态渲染。
- `assets/styles.css`：桌面和移动 webview 的响应式布局。
- `careerloop-routes.json`：IPD 来源、CyanCruise route key、Cosmic WebAPI、平台挂载、身份要求和 fallback 契约。
- `validate-routes.js`：route/API/platform/page-shell metadata 静态校验。

## 页面路由

用户侧页面包括：

- `#workbench`
- `#resume-home`
- `#onboarding`
- `#today-action`
- `#assessment`
- `#resume`
- `#file-upload-preview`
- `#resume-diagnosis`
- `#resume-templates`
- `#resume-course`
- `#career-plan`
- `#interview-home`
- `#interview`
- `#digital-interview`
- `#exam-civil-service`
- `#exam-selected-graduate`
- `#exam-public-institution`
- `#exam-big-tech`
- `#interview-course`
- `#assistant`
- `#messages`
- `#employment-insight`
- `#career-resources`（默认导航隐藏，调试或后续内容接入时使用）

管理侧入口为 `#admin-console`，只在生产管理员身份或开发角色显式包含 `ADMIN`、`COSMIC_ADMIN`、`PLATFORM_ADMIN` 时展示可访问状态。

## 金蝶平台侧边栏外部链接

正式菜单建议由金蝶“应用菜单”配置，CyanCruise 页面不再重复绘制页面内左侧栏。每个菜单项使用外部链接打开同一个静态入口的不同 hash：

| 菜单 | 外部链接 | 状态 |
| --- | --- | --- |
| CyanCruise 工作台 | `/ierp/isv/v620/careerloop/index.htm#workbench` | 已接入 |
| 简历 / AI简历制作 | `/ierp/isv/v620/careerloop/index.htm#resume-home` | 已接入 |
| 简历 / AI简历修改 | `/ierp/isv/v620/careerloop/index.htm#resume-diagnosis` | 已接入 |
| 简历 / 乔布简历 | `/ierp/isv/v620/careerloop/index.htm#resume-templates` | 占位 |
| 简历 / 简历微课 | `/ierp/isv/v620/careerloop/index.htm#resume-course` | 占位 |
| 面试 / 全景仿真面试 | `/ierp/isv/v620/careerloop/index.htm#interview-home` | 已接入 |
| 面试 / AI模拟面试 | `/ierp/isv/v620/careerloop/index.htm#interview` | 已接入 |
| 面试 / 数字人面试 | `/ierp/isv/v620/careerloop/index.htm#digital-interview` | 占位 |
| 面试 / 公务员真题 | `/ierp/isv/v620/careerloop/index.htm#exam-civil-service` | 占位 |
| 面试 / 选调生真题 | `/ierp/isv/v620/careerloop/index.htm#exam-selected-graduate` | 占位 |
| 面试 / 事业编 | `/ierp/isv/v620/careerloop/index.htm#exam-public-institution` | 占位 |
| 面试 / 大厂真题 | `/ierp/isv/v620/careerloop/index.htm#exam-big-tech` | 占位 |
| 面试 / 面试微课 | `/ierp/isv/v620/careerloop/index.htm#interview-course` | 占位 |

## 调试模式

默认用户模式会隐藏迁移验收和工程调试信息，包括接口契约面板、`Route:` 标识、`available/entry-only/user` 状态 chip，以及独立的 `#file-upload-preview` 文件服务调试页。普通用户应从“简历”页完成 PDF 上传、预览、删除和去诊断流程。

开发排查时可以显式使用：

```text
index.html?ccDebug=1#file-upload-preview
```

调试模式会恢复隐藏 route、接口契约、route/status metadata 和文件上传预览入口，用于检查 KAPI 路由、BOS 文件服务和 route map。该模式只用于开发与验收，不作为生产菜单发布入口。

## 身份模式

生产模式 SHALL 只使用 Cosmic 登录上下文或平台 adapter 注入的身份，例如 `window.__CAREERLOOP_COSMIC_CONTEXT__`、`window.__COSMIC_CONTEXT__` 或 `window.cosmicContext`。页面没有解析到生产身份时，SHALL 显示 identity-required，并且不会用硬编码 userId/adminId 调用用户或管理员 WebAPI。

开发验证模式需要显式启用：

```text
index.html?identityMode=development&userId=<id>
```

也可以通过页面输入框写入 `localStorage.careerloop.userId`。该 fallback 仅用于本地或联调验证，不能作为生产菜单发布依据。

## WebAPI 契约

页面只消费已迁移的 `/cc001/*` WebAPI。典型契约包括画像、onboarding、今日行动、测评、简历、文件、简历诊断、职业计划、模拟面试、助手聊天、消息、订阅配额、就业洞察、职业资源和管理后台治理接口。具体路径以 `careerloop-routes.json` 为准。

本地直接用 `file://` 打开时，页面进入契约预览状态，不主动请求后端。部署到苍穹 webapp 或通过 Web 服务打开后，可通过相对路径或 `?apiBase=` 调用平台 WebAPI。

## 暂不迁移

本目录不迁移 IPD Vue/uni-app 源码、Pinia/store、Vite/uView、小程序生命周期、微信运行时、axios/JWT 登录、Spring Boot/JPA/Flyway、真实 AI provider、真实外部内容抓取、语音/数字人面试、完整管理后台页面、生产 datamodel adapter 或客户环境 KDDT 发布脚本。

## 验证

```powershell
node webapp\isv\v620\careerloop\validate-routes.js
node --check webapp\isv\v620\careerloop\assets\app.js
openspec validate migrate-webapp-careerloop-pages --strict
openspec validate polish-careerloop-user-experience --strict
```
