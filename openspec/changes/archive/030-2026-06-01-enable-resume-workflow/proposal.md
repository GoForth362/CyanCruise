## Why

当前 CareerLoop 已能在苍穹门户中打开工作台，并通过 KAPI route 读取简历列表等契约，但“简历”页面仍主要停留在契约展示与空状态。下一阶段需要把已迁移的简历基础、文件上传预览和画像 resume block 串成用户可操作的最小闭环，让目标岗位建立后可以继续进入“创建简历记录、保存文件引用、刷新后可见”的真实流程。

## What Changes

- 将 `webapp/isv/v620/careerloop` 的简历页从静态契约展示升级为可交互工作流，支持读取简历列表、填写简历标题/目标岗位/文件 key/解析内容并创建简历记录。
- 在简历页展示已保存的简历记录、更新时间、目标岗位、文件引用和诊断分数等核心信息，并在空列表、后端不可用、身份缺失时保持可恢复页面状态。
- 将文件上传/预览能力作为可选增强接入简历流程：当文件 adapter 可用时支持通过 `/cc001/files/upload` 获得 object key；不可用时允许用户保留手工文件 key 或只创建元数据。
- 创建或刷新简历后，工作台和画像摘要应能继续体现真实系统简历信号，而不是只依赖 onboarding 自报简历状态。
- 更新 route/API 校验与验证说明，确保简历页使用的 `/cc001/resume/*`、`/cc001/files/*` 契约继续通过 KAPI route 可达。
- 不迁移 IPD Vue、uni-app、Pinia、Vite、uView、Spring Boot Multipart、JPA、Flyway、OSS SDK 或 Java 17 实现。

## Capabilities

### New Capabilities

- 无。

### Modified Capabilities

- `webapp-careerloop-pages`：简历 route 从契约展示升级为真实可交互页面状态，要求页面能够读取、创建和刷新用户简历记录，并保持身份/错误/空列表 fallback。
- `resume-core`：补充用户侧简历创建闭环的前端消费要求，明确创建后的列表可见性、画像 resume block 同步和刷新后保持。
- `file-upload-preview`：补充简历工作流对文件上传/预览的可选接入要求，明确 adapter 不可用时不得阻断简历元数据创建。

## Impact

- 受影响代码：
  - `webapp/isv/v620/careerloop/assets/app.js`
  - `webapp/isv/v620/careerloop/index.html`
  - `webapp/isv/v620/careerloop/assets/styles.css`
  - `webapp/isv/v620/careerloop/careerloop-routes.json`
  - `webapp/isv/v620/careerloop/validate-routes.js`
  - 如有必要，补充 `code/cloud01/v620-cc001-cloud01-app01` 中已有 KAPI route 或 WebAPI 聚焦测试。
- 受影响 API：
  - `/cc001/resume/list`
  - `/cc001/resume/create`
  - `/cc001/files/upload`
  - `/cc001/files/preview-url`
  - 可选读取 `/cc001/career-profile/snapshot/get` 用于刷新工作台摘要。
- 受影响系统：
  - 苍穹 8080 本地验收入口继续使用 `apiMode=kapi` 的 custom Java-plugin route。
  - 本 change 不新增第三方依赖，不改变 JDK 1.8 兼容要求，不硬编码本地 runtime 路径。
- 验证影响：
  - `openspec validate enable-resume-workflow --strict`
  - `node webapp\isv\v620\careerloop\validate-routes.js`
  - `node --check webapp\isv\v620\careerloop\assets\app.js`
  - 相关 Gradle 聚焦测试或 `.\gradlew.bat :v620-cc001-cloud01-app01:test --tests ...`
  - 最终 JDK 8 `.\gradlew.bat clean build`
