# CyanCruise 苍穹 Webapp

本目录承载 CyanCruise 在/Kingdee Cosmic webapp 资源侧的静态入口。当前入口不是 IPD Vue/uni-app 的直接搬迁，而是把 IPD 页面语义重建为可审阅、可降级、可发布的 `ccRoute` 页面状态。

## 入口文件

- `index.html`：CyanCruise webapp 壳，默认由金蝶平台侧边栏外部链接驱动，`?ccDebug=1` 时显示调试身份区、页面导航和状态卡片。
- `assets/app.js`：轻量 page registry、`ccRoute` navigation、身份解析、WebAPI 调用和页面状态渲染。
- `assets/styles.css`：桌面和移动 webview 的响应式布局。
- `cyancruise-routes.json`：IPD 来源、CyanCruise route key、Cosmic WebAPI、平台挂载、身份要求和 fallback 契约。
- `validate-routes.js`：route/API/platform/page-shell metadata 静态校验。

## 页面路由

用户侧页面包括：

- `?ccRoute=workbench`
- `?ccRoute=employment-home`
- `?ccRoute=further-study-home`
- `?ccRoute=postgraduate-exam`
- `?ccRoute=postgraduate-recommendation`
- `?ccRoute=study-abroad`
- `?ccRoute=resume-home`
- `?ccRoute=onboarding`（个人情况）
- `?ccRoute=today-action`
- `?ccRoute=assessment`
- `?ccRoute=resume`
- `?ccRoute=file-upload-preview`
- `?ccRoute=resume-diagnosis`
- `?ccRoute=career-plan`（AI路径规划）
- `?ccRoute=interview-home`
- `?ccRoute=interview`
- `?ccRoute=assistant`
- `?ccRoute=messages`
- `?ccRoute=employment-insight`
- `?ccRoute=career-resources`（默认导航隐藏，调试或后续内容接入时使用）

管理侧入口为 `?ccRoute=admin-console`，只在生产管理员身份或开发角色显式包含 `ADMIN`、`COSMIC_ADMIN`、`PLATFORM_ADMIN` 时展示可访问状态。

## 金蝶平台侧边栏外部链接

正式菜单建议由金蝶“应用菜单”配置，CyanCruise 页面不再重复绘制页面内左侧栏。首页负责填写用户画像草稿和选择路线；就业、深造分别使用独立 `ccRoute` 落地页，不能和首页共用同一个地址。

| 菜单 | 外部链接 | 状态 |
| --- | --- | --- |
| CyanCruise 首页 | `/ierp/isv/v620/cyancruise/index.htm?ccRoute=workbench` | 已接入 |
| 就业 | `/ierp/isv/v620/cyancruise/index.htm?ccRoute=employment-home` | 已接入 |
| 就业 / 简历 | `/ierp/isv/v620/cyancruise/index.htm?ccRoute=resume-home` | 父级/分组 |
| 就业 / 简历 / AI简历制作 | `/ierp/isv/v620/cyancruise/index.htm?ccRoute=resume` | 已接入 |
| 就业 / 简历 / AI简历诊断 | `/ierp/isv/v620/cyancruise/index.htm?ccRoute=resume-diagnosis` | 已接入 |
| 就业 / 面试 | `/ierp/isv/v620/cyancruise/index.htm?ccRoute=interview-home` | 两种面试入口与分类记录 |
| 就业 / 面试 / 全景仿真面试 | `/ierp/isv/v620/cyancruise/index.htm?ccRoute=interview-panorama` | 沉浸式环境、摄像头预览、按难度自动计时与 AI 问答已接入 |
| 就业 / 面试 / AI 模拟面试 | `/ierp/isv/v620/cyancruise/index.htm?ccRoute=interview` | 七题逐题问答、语音辅助与复盘已接入 |
| 深造 | `/ierp/isv/v620/cyancruise/index.htm?ccRoute=further-study-home` | 规划入口 |
| 深造 / 考研 | `/ierp/isv/v620/cyancruise/index.htm?ccRoute=postgraduate-exam` | 规划入口 |
| 深造 / 保研 | `/ierp/isv/v620/cyancruise/index.htm?ccRoute=postgraduate-recommendation` | 规划入口 |
| 深造 / 留学 | `/ierp/isv/v620/cyancruise/index.htm?ccRoute=study-abroad` | 规划入口 |
| AI路径规划 | `/ierp/isv/v620/cyancruise/index.htm?ccRoute=career-plan` | 规划入口 |
| 职业测评 | `/ierp/isv/v620/cyancruise/index.htm?ccRoute=assessment` | 规划入口 |

金蝶菜单配置不依赖 `#hash`，统一使用查询参数形式，例如：

```text
/ierp/isv/v620/cyancruise/index.htm?ccRoute=resume
/ierp/isv/v620/cyancruise/index.htm?ccRoute=resume-diagnosis
/ierp/isv/v620/cyancruise/index.htm?ccRoute=interview
```

当前阶段就业发布四个核心入口：简历制作、简历诊断、全景仿真面试、AI 模拟面试。全景仿真面试支持沉浸式环境、摄像头与麦克风本地预览、答题自动倒计时和 AI 逐轮提问；题目出现后立即计时，入门、常规、进阶每题分别为 3、5、8 分钟，超时后自动进入后续流程。视频不会上传或保存。摄像头受非安全连接或苍穹嵌入策略限制时，可选择无摄像头模式继续。普通 AI 模拟面试采用最多七题的逐题问答，最后自动给出总分、评价和改进方向。

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

也可以通过页面输入框写入 `localStorage.cyancruise.userId`。该 fallback 仅用于本地或联调验证，不能作为生产菜单发布依据。

## WebAPI 契约

页面只消费已迁移的 `/cc001/*` WebAPI。典型契约包括画像、onboarding、今日行动、测评、简历、文件、简历诊断、AI路径规划、模拟面试、助手聊天、消息、订阅配额、就业洞察、职业资源和管理后台治理接口。具体路径以 `cyancruise-routes.json` 为准。

本地直接用 `file://` 打开时，页面进入契约预览状态，不主动请求后端。部署到苍穹 webapp 或通过 Web 服务打开后，可通过相对路径或 `?apiBase=` 调用平台 WebAPI。

## 暂不迁移

本目录不迁移 IPD Vue/uni-app 源码、Pinia/store、Vite/uView、小程序生命周期、微信运行时、axios/JWT 登录、Spring Boot/JPA/Flyway、真实 AI provider、真实外部内容抓取、语音/数字人面试、完整管理后台页面、生产 datamodel adapter 或客户环境 KDDT 发布脚本。

## 验证

```powershell
node webapp\isv\v620\cyancruise\validate-routes.js
node --check webapp\isv\v620\cyancruise\assets\app.js
openspec validate migrate-webapp-careerloop-pages --strict
openspec validate polish-careerloop-user-experience --strict
```
