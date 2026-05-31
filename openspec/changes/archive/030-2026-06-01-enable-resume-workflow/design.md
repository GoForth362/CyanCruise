## Context

CyanCruise 当前已完成 CareerLoop 在苍穹门户中的基础挂载，并通过 KAPI v2 custom Java-plugin route 转发 `/cc001/*` WebAPI。工作台可以读取画像、今日行动、简历列表、职业计划和面试摘要；onboarding 已可保存目标岗位并刷新后保持。

简历后端基础能力已经存在：`resume-core` 提供 `/cc001/resume/list` 与 `/cc001/resume/create`，并在创建/更新后同步职业画像 resume block；`file-upload-preview` 提供 `/cc001/files/upload`、`/preview-url` 等文件能力；`CareerLoopCustomWebApiPlugin` 已覆盖这些路径。当前缺口主要在 webapp：`resume` route 仍由通用契约页渲染，只展示接口契约和列表空态，用户无法在页面内创建简历记录或看到创建后的闭环反馈。

本 change 的实现目标是把已有后端契约串成用户侧最小可用流程，同时保持 Kingdee Cosmic 二开约束：JDK 1.8、仓库内 `gradlew.bat`、不硬编码本地 runtime 路径、不引入 IPD 前端/后端运行时依赖。

## Goals / Non-Goals

**Goals:**

- 在现有静态 webapp 壳中为 `resume` route 增加专用页面渲染逻辑，展示简历列表、创建表单和可恢复状态。
- 支持用户填写简历标题、目标岗位、文件 key 和解析内容，通过 KAPI route 调用 `/cc001/resume/create`。
- 创建成功后刷新 `/cc001/resume/list`，并尽量刷新 `/cc001/career-profile/snapshot/get`，使工作台简历状态体现真实系统简历信号。
- 可选接入 `/cc001/files/upload`：当文件能力可用时把上传结果 object key 带入简历表单；当文件 adapter 不可用或浏览器限制文件读取时，不阻断纯元数据创建。
- 保留现有 `apiMode=kapi`、direct contract、file preview/development fallback 语义。
- 增加静态校验或聚焦测试，防止 route map 与简历页消费的 endpoint 不一致。

**Non-Goals:**

- 不迁移 IPD Vue、uni-app、Pinia、Vite、uView、axios 拦截器或小程序文件选择实现。
- 不新增 Spring Multipart、OSS SDK、PDFBox、OCR、Flyway、JPA 或 Java 17 依赖。
- 不实现完整简历编辑器、模板库、在线预览器、版本比较或富文本排版。
- 不在本 change 中实现简历诊断 AI 质量提升、PDF 文本抽取生产 adapter 或 Cosmic 文件服务最终客户配置。
- 不解决苍穹首页“快速发起”快捷方式 `menuId` 为空问题；该问题属于门户菜单发布配置。

## Decisions

### 1. 使用现有静态壳新增 `renderResumePage`

实现时在 `assets/app.js` 中让 `renderPage` 对 `item.key === "resume"` 走专用 `renderResumePage`，而不是继续走通用 `renderContractPage`。

理由：当前页面已经是无构建静态资源，直接扩展现有渲染函数能保持部署简单，也避免引入新的前端框架或构建链。简历页需要表单、列表、提交状态和刷新行为，继续塞进通用契约页会让通用逻辑承担过多页面特例。

备选方案：引入独立组件化框架或拆出多文件模块。放弃原因是会增加 KDDT/静态资源发布复杂度，也不符合当前无构建 webapp 的既有模式。

### 2. 以“元数据创建”为主流程，文件上传作为增强能力

简历创建主流程使用 `/cc001/resume/create`，请求体包含 `title`、`targetJob`、`fileKey`、`parsedContent`。页面允许用户手工填写文件 key，也可以在文件能力可用时通过 `/cc001/files/upload` 自动获得 object key。

理由：本地 8080 验收中 Cosmic 文件 adapter 可能仍处于 disabled/unavailable 状态。简历基础记录不应被文件 provider 阻断；用户先保存元数据，后续再补齐真实文件能力，能更快形成 CareerLoop 主循环信号。

备选方案：强制先上传文件再创建简历。放弃原因是这会把简历核心流程绑定到尚未最终客户化的文件 adapter，增加验收阻塞。

### 3. 创建成功后以服务端列表和画像快照为准

提交 `/cc001/resume/create` 成功后，页面不只把返回值追加到本地数组，而是重新调用 `/cc001/resume/list`；随后尽量刷新 `/cc001/career-profile/snapshot/get` 并更新工作台卡片。刷新失败时保留已创建结果和可恢复提示。

理由：列表排序、ID、更新时间、画像同步都由后端应用服务确定。以服务端返回为准可以避免前端伪造状态，也能验证“刷新后仍在”的真实持久化语义。

备选方案：只在前端本地追加一条记录。放弃原因是会掩盖后端持久化或身份归属问题。

### 4. 继续通过统一 `post` 与 KAPI route 调用后端

简历页所有后端调用继续使用现有 `post` helper。生产/验收模式下由 `apiMode=kapi` 转发到 `cc001/careerloop/route`；静态 file preview 模式不调用后端，仅显示预览或本地提示。

理由：这保持当前苍穹 OpenAPI 配置、第三方应用 token、权限项和 RequestContext 身份桥的验收路径一致，不需要再为简历页开新的 servlet route 或 OpenAPI API code。

备选方案：简历页直接调用 `/ierp/cc001/resume/*`。放弃原因是本地 8.0.4 runtime 已验证直接 `@ApiController` servlet route 不可靠，KAPI custom plugin 是当前可用路径。

### 5. 页面状态按面板级恢复，不整页失败

简历页至少区分以下状态：加载中、身份缺失、列表为空、创建中、创建成功、创建失败、文件上传不可用。文件上传失败不影响创建元数据；列表刷新失败不清空已有页面结构。

理由：CareerLoop 主循环要求用户能持续导航。局部 API 失败时保持页面可用，比整页报错更适合苍穹门户中的验收和生产排查。

备选方案：任何 API 失败都回到通用 backend-error 页。放弃原因是会放大单点失败，并破坏已建立的工作台导航体验。

## Risks / Trade-offs

- 文件上传 provider 在当前环境不可用 -> 页面允许手工 fileKey 或只保存元数据，并显示“文件能力暂不可用”状态。
- 浏览器读取文件字节可能受文件大小和内存限制影响 -> MVP 仅支持小文件/文本级读取，保留文件 key 手工输入作为 fallback，验证中不把大文件上传作为阻断项。
- 创建成功后画像刷新失败 -> 保留简历列表刷新结果，工作台卡片下次 overview 加载时再恢复一致。
- 简历表单字段过少，不能覆盖完整 IPD 简历编辑体验 -> 本 change 明确只做最小闭环；模板库、富文本编辑和完整解析后续 change 处理。
- KAPI token 过期导致页面创建失败 -> 页面显示可恢复错误，验收时使用新 token，不在前端持久保存敏感 token 以外的新凭据。
- 静态页面逻辑继续集中在 `app.js`，文件会继续变大 -> 本阶段优先遵循既有无构建模式；若后续多个页面都进入复杂交互，再单独提出前端模块化 change。

## Migration Plan

1. 完成并校验 OpenSpec artifacts：proposal、design、delta specs、tasks。
2. 实现 webapp 简历专用页面状态、表单、列表刷新和可选文件上传入口。
3. 若 route/API metadata 发生变化，更新 `careerloop-routes.json` 与 `validate-routes.js`。
4. 运行验证：
   - `openspec validate enable-resume-workflow --strict`
   - `node webapp\isv\v620\careerloop\validate-routes.js`
   - `node --check webapp\isv\v620\careerloop\assets\app.js`
   - 相关 WebAPI/KAPI 聚焦测试
   - JDK 8 `.\gradlew.bat clean build`
5. 部署时同步静态资源到 `F:\kingdee\ENV\static-file-service\isv\v620\careerloop\`。若 Java 后端未变，可不重启 8080；若补充后端测试之外的 Java 实现，则重新构建 JAR 并重启苍穹服务。
6. 验收入口继续使用：

```text
http://10.0.0.8:8080/ierp/isv/v620/careerloop/index.htm?apiMode=kapi&access_token=<new-token>#resume
```

回滚方式：回退本 change 的静态资源到上一版本，或从门户继续使用工作台/其他 route；后端 `/cc001/resume/*` 与 `/cc001/files/*` 既有契约不改变。

## Open Questions

- 文件上传 MVP 是否限制为小文本/小 PDF 文件，还是本 change 只提供 fileKey 输入并把真实文件选择留到后续？
- 简历创建表单中的目标岗位默认值是否优先取 `preferences.targetRole`，还是允许用户每份简历单独覆盖？
- 创建成功后是否立即跳转到简历诊断页，还是仅提供“去诊断”按钮让用户自主进入下一步？
