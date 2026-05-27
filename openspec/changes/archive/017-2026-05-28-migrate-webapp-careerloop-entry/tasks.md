## 1. IPD Flow And Contract Inventory

- [x] 1.1 盘点 `F:\Project\IPD\frontend\src\pages.json` 中 CareerLoop 主循环路由，标注首页、onboarding、今日行动、测评、简历、诊断、面试、计划和助手入口。
- [x] 1.2 盘点 IPD `pages\home`、`pages\onboarding`、`pages\agent`、`pages\assessment`、`pages\resume`、`pages\resume-ai`、`pages\interview` 和 `pages\assistant` 的用户流程，不复制 Vue/uni-app 实现。
- [x] 1.3 盘点 IPD `api\agent.ts`、`api\assessment.ts`、`api\career.ts`、`api\resume.ts`、`api\interview.ts`、`api\ai.ts`、`utils\onboardingGate.ts` 和 `utils\onboardingSync.ts` 的接口/流程语义。
- [x] 1.4 对齐 CyanCruise 已迁移 WebAPI：`/cc001/career-profile`、`/cc001/career-agent`、`/cc001/assessment`、`/cc001/resume`、`/cc001/resume-diagnosis`、`/cc001/career-plan`、`/cc001/interview` 和 `/cc001/assistant-chat`。

## 2. Route And API Map

- [x] 2.1 在 `webapp/isv/v620/careerloop/` 下新增 route/API 映射文件，记录 IPD path、CyanCruise route key、页面语义、所需 WebAPI、userId 要求和降级状态。
- [x] 2.2 为未纳入首轮的 IPD 能力记录 pending 状态，包括消息中心、通知/订阅、CDUT 就业详情、管理后台、文件预览、语音/数字人面试和生产登录态。
- [x] 2.3 保证今日行动 DTO 中的目标跳转可映射到 CyanCruise route key 或兼容 IPD path。

## 3. Webapp Entry Resources

- [x] 3.1 在 `webapp/isv/v620/careerloop/` 新增 CareerLoop 入口页面资源和 README，保持不依赖 `F:\Project\IPD\frontend` 运行时。
- [x] 3.2 实现工作台首屏：目标岗位/画像状态、今日行动、主循环快捷入口和可恢复错误/空态。
- [x] 3.3 实现 onboarding gate 的页面契约：身份类型、目标岗位/方向、是否已有简历和偏好信号，并调用已迁移 onboarding WebAPI。
- [x] 3.4 实现主入口动作：测评、简历、简历诊断、模拟面试、职业计划和助手聊天入口均能进入可用页面状态或明确 pending 状态。
- [x] 3.5 增加开发/验证态 `userId` 输入或解析策略，生产态登录态保持待平台适配，不硬编码单一生产用户。

## 4. Responsive UX And Asset Checks

- [x] 4.1 为桌面和移动 webview 约束布局尺寸，确保导航、按钮、状态卡片和今日行动文本不重叠。
- [x] 4.2 使用与 CareerLoop 工作流相关的可检查视觉资产或图标，不使用仅装饰的占位图。
- [x] 4.3 确认页面文案聚焦用户动作，不在应用内展示实现说明、迁移说明或技术说明。

## 5. Documentation

- [x] 5.1 更新 `docs/ipd-to-cyancruise-migration-map.md`，记录 webapp 入口迁移的 IPD 来源路径、目标模块、数据/接口映射、暂不迁移项和验证方式。
- [x] 5.2 将 delta spec 同步到 `openspec/specs/webapp-careerloop-entry/`。
- [x] 5.3 在 change 文档或目标 README 中记录不迁移 Vue/uni-app、消息订阅、CDUT 详情、管理后台、语音面试、文件预览和生产登录态。

## 6. Validation And Delivery

- [x] 6.1 运行 `openspec validate migrate-webapp-careerloop-entry --strict`。
- [x] 6.2 运行 `openspec validate --all --strict`。
- [x] 6.3 运行静态资源/route-map 检查，确认 webapp 入口文件、路由映射和引用资产存在。
- [x] 6.4 设置 JDK 8 后运行 `.\gradlew.bat clean build`，确认 webapp 迁移不破坏现有工程构建。
- [x] 6.5 verify 通过后归档 change，按下一个顺序编号命名归档目录。
- [x] 6.6 本地 commit 并推送当前迁移分支 `codex/migrate-webapp-careerloop-entry`。
