# CareerLoop 苍穹 Webapp 入口

本目录承载 `migrate-webapp-careerloop-entry` 的首轮页面资源，目标是把 IPD 小程序里的 CareerLoop 主循环入口重建到 CyanCruise 苍穹 webapp 资源侧。

## 入口文件

- `index.html`：CareerLoop 工作台入口，可直接打开做静态验证，也可在苍穹 webapp 资源中挂载。
- `careerloop-routes.json`：IPD 页面语义、CyanCruise route key、苍穹 WebAPI、userId 要求和降级状态映射。
- `assets/app.js`：轻量页面状态和 WebAPI 调用边界，不依赖 Vue/uni-app。
- `assets/styles.css`：响应式布局样式。
- `assets/careerloop-flow.svg`：与主循环相关的可检查视觉资产。

## 用户身份

生产态用户身份等待苍穹/Cosmic 登录上下文接入。本轮只提供开发和验证态来源：

- URL 参数：`index.html?userId=<id>`
- 本地存储：`localStorage.careerloop.userId`
- 页面输入框

没有显式 `userId` 时，页面不会调用用户归属 WebAPI，也不会硬编码生产用户。

## WebAPI 边界

页面默认消费已迁移的苍穹 WebAPI：

- `/cc001/career-profile/snapshot/get`
- `/cc001/career-profile/onboarding/save`
- `/cc001/career-agent/today/get`
- `/cc001/assessment/submit`
- `/cc001/resume/list`
- `/cc001/resume/create`
- `/cc001/resume-diagnosis/analyze`
- `/cc001/resume-diagnosis/keywords/status`
- `/cc001/career-plan/summary`
- `/cc001/career-plan/ensure`
- `/cc001/interview/list`
- `/cc001/interview/start`
- `/cc001/assistant-chat/send`
- `/cc001/assistant-chat/session/list`

如果本地直接以 `file://` 打开页面，页面进入契约预览状态，不主动请求后端。部署到苍穹或通过 Web 服务打开时，可通过 `?apiBase=` 或相对路径调用平台 WebAPI。

## 暂不迁移

本目录不迁移 IPD Vue/uni-app 源码、Pinia/store、Vite、uView、小程序 tabBar、微信订阅、消息中心、CDUT 就业详情、管理后台、语音/数字人面试、文件上传预览、生产登录态或前端流式聊天。上述能力在 `careerloop-routes.json` 中记录为 pending，等待后续独立 change。

