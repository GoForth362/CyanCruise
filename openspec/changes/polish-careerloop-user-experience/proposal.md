## Why

当前 CareerLoop 页面已经跑通苍穹身份、简历上传、预览、删除和主要页面入口，但普通用户界面仍混入接口契约、route/status chip、entry-only 等工程验收信息。现在需要把“可验证的工程页面”收口成“可自然使用的求职主循环体验”，避免用户被调试信息和非核心入口干扰。

## What Changes

- 默认导航只展示真实用户可理解、可继续操作的主循环入口，隐藏文件上传预览、职业资源等调试或非当前闭环重点页面。
- 将“接口契约”“Route: xxx”“available/entry-only/user”等开发信息统一收敛到调试模式，正常页面不展示。
- 优化简历页体验：保留上传、创建、预览、删除、去诊断闭环，但弱化 fileKey 等工程字段的视觉暴露。
- 优化工作台和页面标题文案，让用户看到的是目标岗位、准备度、简历、诊断、计划、面试等业务语义。
- 不改 `/cc001/*` 后端契约，不新增依赖，不引入 Vue/uni-app 或新的前端构建链。

## Capabilities

### New Capabilities

- 无。

### Modified Capabilities

- `webapp-careerloop-entry`：调整入口导航可见性和调试信息展示规则。
- `webapp-careerloop-pages`：调整多页面默认渲染、调试面板、简历页业务化展示和错误状态呈现。
- `file-upload-preview`：保留文件服务底层能力，但将独立文件上传预览页面从普通用户主导航中移出。

## Impact

- 主要影响 `webapp/isv/v620/careerloop/assets/app.js`、`assets/styles.css` 和 `careerloop-routes.json`。
- 可能更新 `webapp/isv/v620/careerloop/README.md` 或迁移地图，记录 `?ccDebug=1` 调试模式和隐藏入口策略。
- 需要运行 `node webapp\isv\v620\careerloop\validate-routes.js`、`node --check webapp\isv\v620\careerloop\assets\app.js`、OpenSpec 校验；若仅改静态资源，可不跑完整 Gradle，若触及 Java 则使用 JDK 8 `.\gradlew.bat clean build`。
