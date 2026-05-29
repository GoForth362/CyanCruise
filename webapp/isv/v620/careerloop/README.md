# CareerLoop 苍穹 Webapp

本目录承载 CareerLoop 在 CyanCruise/Kingdee Cosmic webapp 资源侧的静态入口。当前入口不是 IPD Vue/uni-app 的直接搬迁，而是把 IPD 页面语义重建为可审阅、可降级、可发布的 hash route 页面状态。

## 入口文件

- `index.html`：CareerLoop webapp 壳，包含身份区、页面导航、状态卡片和页面容器。
- `assets/app.js`：轻量 page registry、hash navigation、身份解析、WebAPI 调用和页面状态渲染。
- `assets/styles.css`：桌面和移动 webview 的响应式布局。
- `careerloop-routes.json`：IPD 来源、CyanCruise route key、Cosmic WebAPI、平台挂载、身份要求和 fallback 契约。
- `validate-routes.js`：route/API/platform/page-shell metadata 静态校验。

## 页面路由

用户侧页面包括：

- `#workbench`
- `#onboarding`
- `#today-action`
- `#assessment`
- `#resume`
- `#file-upload-preview`
- `#resume-diagnosis`
- `#career-plan`
- `#interview`
- `#assistant`
- `#messages`
- `#employment-insight`
- `#career-resources`

管理侧入口为 `#admin-console`，只在生产管理员身份或开发角色显式包含 `ADMIN`、`COSMIC_ADMIN`、`PLATFORM_ADMIN` 时展示可访问状态。

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
```
