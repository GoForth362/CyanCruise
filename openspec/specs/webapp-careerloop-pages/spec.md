# webapp-careerloop-pages Specification

## Purpose
TBD - created by archiving change migrate-webapp-careerloop-pages. Update Purpose after archive.
## Requirements
### Requirement: CareerLoop multi-page webapp shell

CyanCruise SHALL provide a CareerLoop webapp page shell under `webapp/isv/v620/careerloop` that exposes migrated IPD page semantics as Cosmic-compatible hash route states. The shell SHALL cover workbench, onboarding, today action, assessment, resume, file upload/preview, resume diagnosis, career plan, interview, assistant, messages, employment insight, and career resources.

#### Scenario: User opens a supported page route
- **WHEN** a user opens `index.html#<route-key>` for a supported CareerLoop route
- **THEN** the webapp SHALL render the matching page state with title, route status, primary data panels, available actions, and route-specific fallback text

#### Scenario: User opens an unknown route
- **WHEN** a user opens an unknown CareerLoop hash route
- **THEN** the webapp SHALL return to the workbench or show a recoverable not-found state without calling unrelated `/cc001/*` WebAPI

### Requirement: Page data binding to migrated WebAPI contracts

Each CareerLoop page SHALL consume only existing migrated `/cc001/*` WebAPI contracts declared in `careerloop-routes.json`. Page requests SHALL respect user/admin identity requirements and SHALL render loading, empty, success, forbidden, identity-required, unavailable, and backend-error states independently per panel.

#### Scenario: Page has resolvable production identity
- **WHEN** a production Cosmic user opens a user-owned page with resolved platform identity
- **THEN** the page SHALL call only the route-mapped user-owned WebAPI contracts and SHALL display returned DTO semantics without requiring IPD runtime code

#### Scenario: Page lacks required identity
- **WHEN** a user-owned or admin-owned page cannot resolve the required identity
- **THEN** the page SHALL display an identity-required or forbidden state and SHALL NOT call protected WebAPI with a hardcoded, guessed, or stale identifier

#### Scenario: One panel call fails
- **WHEN** one route-mapped WebAPI call fails while other page data remains available
- **THEN** the page SHALL keep navigation and successful panels usable while showing a recoverable fallback state for the failed panel

### Requirement: IPD page semantics without IPD frontend runtime

The page migration SHALL preserve IPD business page semantics and data meaning while rebuilding the implementation as CyanCruise static webapp resources. The implementation SHALL NOT directly migrate or require IPD Vue, uni-app, Pinia/store, Vite, uView, mini-program lifecycle, axios interceptors, Spring Boot, JPA, Flyway, or Java 17 runtime code.

#### Scenario: Implementation dependencies are reviewed
- **WHEN** reviewers inspect the webapp files and build configuration
- **THEN** the change SHALL reside in CyanCruise target files and SHALL NOT introduce IPD frontend runtime dependencies or a new frontend build chain

#### Scenario: Source mapping is reviewed
- **WHEN** reviewers inspect OpenSpec and migration map artifacts
- **THEN** they SHALL find IPD source paths, target CyanCruise files, data/API mappings, temporarily excluded items, and validation commands for the page migration

### Requirement: Main-loop page workflow continuity

The webapp pages SHALL keep the CareerLoop main loop navigable across target role/profile, assessment, resume, resume diagnosis, today action, interview, feedback-oriented messages, assistant guidance, employment insight, and career plan. Pages MAY show entry-only or pending states for capabilities whose production adapter is outside this change, but SHALL preserve the user's next available action.

#### Scenario: User moves from workbench to a page
- **WHEN** a user selects a main-loop action from the workbench or navigation
- **THEN** the webapp SHALL update the hash route, render the target page, and keep a visible path back to the workbench

#### Scenario: Capability is entry-only or pending
- **WHEN** a route is marked entry-only or depends on a later platform adapter
- **THEN** the page SHALL show what is available now, what is pending, and a safe fallback action without implying the full IPD capability is production-ready

### Requirement: Responsive and inspectable page layout

CareerLoop webapp pages SHALL use responsive, inspectable layouts suitable for Cosmic desktop and webview usage. Navigation, page headers, status chips, forms, lists, tool buttons, and cards SHALL have stable dimensions or responsive constraints so labels and controls do not overlap on common desktop and mobile widths.

#### Scenario: Layout is checked on desktop and mobile
- **WHEN** reviewers open representative CareerLoop pages on common desktop and mobile viewports
- **THEN** navigation, page content, actions, status text, and fallback messages SHALL remain readable and non-overlapping

#### Scenario: Static assets are inspected
- **WHEN** reviewers inspect static page assets
- **THEN** visible assets and controls SHALL represent actual CareerLoop workflow states rather than unrelated decoration

### Requirement: Webapp page verification

The page migration SHALL include verification that the page shell, route/API metadata, JavaScript syntax, OpenSpec artifacts, migration map, and JDK 8 build remain valid before archive.

#### Scenario: Change is verified
- **WHEN** implementation is complete
- **THEN** verification SHALL include `node webapp\isv\v620\careerloop\validate-routes.js`, `node --check webapp\isv\v620\careerloop\assets\app.js`, `openspec validate migrate-webapp-careerloop-pages --strict`, `openspec validate --all --strict`, JDK 8 `.\gradlew.bat clean build`, and migration map updates

#### Scenario: Change is archived
- **WHEN** the change is archived after implementation
- **THEN** the archive folder SHALL keep the numeric archive prefix and the migration map SHALL record the final branch, commit, validation result, and temporarily excluded platform items

### Requirement: 简历页面可交互工作流
CareerLoop webapp 的 `resume` route SHALL 提供可交互的简历工作流，而不只是展示接口契约。页面 SHALL 展示当前用户的简历记录列表、简历创建表单、提交状态和恢复性错误信息，并继续遵守 `careerloop-routes.json` 中声明的身份、API 和 fallback 约束。

#### Scenario: 打开简历页加载记录
- **WHEN** 已解析 Cosmic userId 的用户打开 `index.html#resume`
- **THEN** 页面 SHALL 调用 `/cc001/resume/list` 并展示该用户的简历记录、空列表状态或可恢复错误状态

#### Scenario: 身份缺失时阻止调用
- **WHEN** 用户打开 `resume` route 但页面无法解析生产身份或显式开发身份
- **THEN** 页面 SHALL 显示 identity-required 状态，并 SHALL NOT 使用硬编码、猜测或上一次缓存的 userId 调用 `/cc001/resume/*`

#### Scenario: 简历页保留导航
- **WHEN** 简历列表、创建或文件相关 API 调用失败
- **THEN** 页面 SHALL 保留工作台返回入口、简历表单和已加载数据，并显示局部可恢复错误而不是整页不可用

### Requirement: 简历创建表单
CareerLoop webapp 的简历页 SHALL 提供创建简历记录的表单。表单 SHALL 支持标题、目标岗位、文件 key 和解析内容字段；目标岗位 SHALL 默认使用画像或工作台当前目标岗位，但用户 MUST 能为本次简历单独覆盖。

#### Scenario: 默认目标岗位
- **WHEN** 用户已在 onboarding 或画像偏好中保存目标岗位，并打开简历创建表单
- **THEN** 表单 SHALL 默认填入该目标岗位

#### Scenario: 覆盖单份简历目标岗位
- **WHEN** 用户在简历创建表单中修改目标岗位并提交
- **THEN** 页面 SHALL 将修改后的目标岗位作为 `/cc001/resume/create` 请求的 `targetJob` 字段提交

#### Scenario: 创建简历记录
- **WHEN** 用户填写简历标题和可选文件 key、解析内容后提交表单
- **THEN** 页面 SHALL 调用 `/cc001/resume/create`，并在成功后刷新 `/cc001/resume/list`

#### Scenario: 创建成功后不自动跳转
- **WHEN** 简历记录创建成功
- **THEN** 页面 SHALL 停留在简历页，展示成功提示和“去简历诊断”入口，而 SHALL NOT 自动跳转到诊断页

### Requirement: 简历创建后的工作台刷新
CareerLoop webapp SHALL 在简历创建成功后尽量刷新职业画像快照和工作台摘要，使“简历状态”体现系统真实保存的简历记录。画像刷新失败 SHALL NOT 回滚已创建的简历记录。

#### Scenario: 创建后刷新画像
- **WHEN** `/cc001/resume/create` 成功并且 `/cc001/resume/list` 刷新完成
- **THEN** 页面 SHALL 尝试调用 `/cc001/career-profile/snapshot/get` 并更新工作台卡片中的简历状态

#### Scenario: 画像刷新失败
- **WHEN** 简历创建成功但画像快照刷新失败
- **THEN** 页面 SHALL 保留简历列表刷新结果并提示画像稍后刷新，且 SHALL NOT 把创建成功显示为失败

### Requirement: 页面默认展示业务语义
CyanCruise 多页面 SHALL 在默认用户模式下优先展示业务语义、用户数据和下一步动作，而不是 route key、接口路径、audience、available、entry-only 等开发迁移标识。

#### Scenario: 打开简历页
- **WHEN** 普通用户打开 `#resume`
- **THEN** 页面 SHALL 优先展示简历创建、上传、记录、预览、删除和去诊断操作，且 SHALL NOT 默认展示接口契约面板

#### Scenario: 打开任一主循环页面
- **WHEN** 普通用户打开工作台、今日行动、职业测评、简历诊断、职业计划、模拟面试、求职助手、消息中心或就业洞察页面
- **THEN** 页面标题、说明和状态 SHALL 使用用户可理解的业务文案，而不是以 route/status chip 作为主要信息

#### Scenario: 调试模式打开页面
- **WHEN** 用户以 `?ccDebug=1` 打开任一页面
- **THEN** 页面 MAY 显示 route key、status chip、接口契约、fallback 策略和 identity metadata 以支持开发排查

### Requirement: 简历页形成正式用户闭环
简历页 SHALL 将上传 PDF、创建简历、预览文件、删除记录和进入诊断组织为正式用户流程。工程字段 MAY 保留在表单或 debug 模式中，但默认展示 SHALL 避免让 fileKey 和接口路径成为主要视觉内容。

#### Scenario: 上传后创建简历
- **WHEN** 用户上传 PDF 并点击创建简历
- **THEN** 页面 SHALL 将上传返回的 object key 关联到简历记录，并用业务化文案提示“文件已关联”或等价状态

#### Scenario: 预览简历 PDF
- **WHEN** 用户点击简历记录的预览操作
- **THEN** 页面 SHALL 直接打开或嵌入展示 PDF 预览，且 SHALL NOT 要求用户再点击裸 URL 或平台内部 URL

#### Scenario: 删除简历记录
- **WHEN** 用户点击删除并确认
- **THEN** 页面 SHALL 调用简历删除能力，成功后刷新简历列表和工作台摘要

#### Scenario: 文件服务局部失败
- **WHEN** 上传、下载或预览失败
- **THEN** 页面 SHALL 展示局部可恢复错误，保留简历列表、创建表单和返回工作台入口

### Requirement: 调试面板不影响用户流程
接口契约和 route metadata 面板 SHALL 在 debug 模式中可用，但 SHALL NOT 成为默认用户流程的一部分。

#### Scenario: 默认模式隐藏接口契约
- **WHEN** 普通用户打开任一主循环页面
- **THEN** 页面 SHALL NOT 默认渲染“接口契约”面板

#### Scenario: debug 模式显示接口契约
- **WHEN** 开发者使用 `?ccDebug=1` 打开页面
- **THEN** 页面 SHALL 显示该页面消费的 `/cc001/*` WebAPI 路径、身份要求和 fallback 信息

### Requirement: 功能卡片承载业务入口
CyanCruise 默认工作台 SHALL 使用功能卡片表达用户可执行能力。卡片 SHALL 包含清晰图标、功能名称、短说明和可点击目标，且默认文案 SHALL 避免接口路径、fileKey、route key 等工程字段。

#### Scenario: 简历外部链接页展示
- **WHEN** 用户通过金蝶平台菜单打开 `#resume-home`
- **THEN** 页面 SHALL 展示 AI简历制作、AI简历诊断两个 IPD 核心入口
- **AND** AI简历制作 SHALL 跳转到现有简历 route，AI简历诊断 SHALL 跳转到现有简历诊断 route

#### Scenario: 面试外部链接页展示
- **WHEN** 用户通过金蝶平台菜单打开 `#interview-home`
- **THEN** 页面 SHALL 展示全景仿真面试、AI模拟面试两个 IPD 核心入口
- **AND** 两个入口 SHALL 跳转到现有模拟面试 route

#### Scenario: 非 IPD 核心能力不展示
- **WHEN** 当前阶段未接入乔布简历、简历微课、数字人面试、公务员真题、事业编、大厂真题、面试微课等能力
- **THEN** 页面 SHALL NOT 展示这些功能卡片
- **AND** 平台菜单建议 SHALL NOT 包含这些外部链接

#### Scenario: 视觉密度
- **WHEN** 页面在苍穹门户内以桌面宽度打开
- **THEN** 功能卡片 SHALL 使用 3 至 4 列响应式网格展示
- **AND** 卡片文字 SHALL 不溢出、不遮挡、不依赖横向滚动或页面内二级侧边栏才能阅读主要内容



### Requirement: 简历诊断页面闭环
`resume-diagnosis` route SHALL 作为简历诊断工作台展示。页面 SHALL 支持读取已有简历列表、选择简历、读取画像默认目标岗位、填写目标岗位要求、触发诊断、展示诊断结果和结构化诊断建议，并提供再次诊断入口。

#### Scenario: 选择已有简历诊断
- **WHEN** 用户打开 `resume-diagnosis` 页面且存在真实简历记录
- **THEN** 页面展示简历选择、目标岗位/岗位要求 输入和触发诊断按钮

#### Scenario: 无真实简历记录
- **WHEN** 用户没有真实简历记录
- **THEN** 页面提示先创建或上传简历，并提供跳转到 `resume` route 的入口

#### Scenario: 展示建议清单
- **WHEN** 诊断返回结构化诊断建议
- **THEN** 页面按优先级展示建议项、证据、目标关键词和示例改写方向

#### Scenario: 再次诊断
- **WHEN** 用户查看建议后选择再次诊断
- **THEN** 页面复用当前 resumeId、目标岗位和岗位要求 重新调用诊断入口

### Requirement: 页面保持 CyanCruise 风格和可恢复状态
简历诊断页面 SHALL 沿用现有 CyanCruise 静态页面风格、状态提示、卡片密度、按钮样式和 KAPI 调用 helper。身份缺失、后端错误、AI unavailable、文件预览 unavailable 或列表为空 SHALL 显示局部可恢复状态，而不是整页崩溃。

#### Scenario: 身份缺失
- **WHEN** 页面无法解析当前用户身份
- **THEN** 页面显示身份缺失提示，并不使用硬编码 userId 发起诊断

#### Scenario: 后端错误
- **WHEN** `/cc001/resume-diagnosis/analyze` 返回错误
- **THEN** 页面保留用户已填写的目标岗位和岗位要求，并显示可重试提示

### Requirement: 静态资源版本更新
当 `index.html`、`assets/app.js` 或 `assets/styles.css` 因简历诊断页面发生变更时，系统 SHALL 更新静态资源版本号，并在交付说明中声明是否需要重新部署静态资源。

#### Scenario: 修改 app.js
- **WHEN** 本 change 修改 `webapp/isv/v620/cyancruise/assets/app.js`
- **THEN** `index.html` 中引用的静态资源版本号 SHALL 更新，避免部署后继续命中旧缓存

### Requirement: 页面展示 PDF 正文提取状态
简历上传和诊断页面 SHALL 分别展示文件上传、PDF 正文提取、简历记录保存和诊断状态。页面 SHALL NOT 把“PDF 已上传”描述成“正文已读取”。

#### Scenario: 上传并提取成功
- **WHEN** PDF 上传和正文提取均成功
- **THEN** 页面提示正文已读取，并在创建简历时提交 `parsedContent`

#### Scenario: 扫描版 PDF
- **WHEN** PDF 上传成功但没有可提取正文
- **THEN** 页面说明当前 PDF 可能只有图片，并提供粘贴简历正文的入口

#### Scenario: 已有 PDF 重新读取正文
- **WHEN** 用户选择正文为空但有关联 PDF 的已有简历
- **THEN** 页面允许触发重新读取正文，并在成功后继续诊断而不要求重新上传文件

### Requirement: 诊断结果使用用户可理解的中文
简历诊断结果页面 SHALL 展示评分标准、分项得分、评分依据和具体建议。页面 SHALL NOT 直接展示 `resume.targetJob`、`HIGH`、`projects`、`TODO`、`JD` 等内部标识、英文状态或行业缩写。

#### Scenario: 展示诊断结果
- **WHEN** 诊断响应包含总分、评分明细、上下文来源和结构化建议
- **THEN** 页面以中文展示评分标准、得分原因、建议优先级、简历区域、修改动作和示例

#### Scenario: 展示上下文来源
- **WHEN** 结果包含内部上下文来源代码
- **THEN** 页面将其转换为“当前简历”“目标岗位”“职业测评”“岗位要求”等用户可理解名称

### Requirement: 诊断目标岗位来自所选简历
简历诊断页面 SHALL 自动展示当前所选简历记录中的目标岗位，并 SHALL NOT 要求用户在诊断页面重复填写或覆盖目标岗位。切换简历时，目标岗位 SHALL 随简历记录同步变化。

#### Scenario: 切换诊断简历
- **WHEN** 用户从前端岗位简历切换到后端岗位简历
- **THEN** 页面自动将目标岗位从前端方向更新为后端方向，并以该岗位发起诊断

#### Scenario: 简历没有目标岗位
- **WHEN** 所选简历记录没有目标岗位
- **THEN** 页面提示用户回到简历页补充目标岗位，而不是在诊断页临时填写

### Requirement: 诊断分数在页面间保持一致
简历诊断成功后，页面 SHALL 重新读取简历列表和用户画像，使诊断结果中的最新分数与简历记录、工作台摘要保持一致，不得继续展示浏览器缓存中的旧分数。

#### Scenario: 完成一次重新诊断
- **WHEN** 某份简历的新诊断分数已经保存
- **THEN** 页面刷新该简历记录，并在返回简历页时展示与诊断结果相同的最新分数

#### Scenario: 列表刷新失败
- **WHEN** 诊断成功但简历列表刷新暂时失败
- **THEN** 页面保留本次诊断结果并提示列表稍后刷新，不得把诊断显示为失败

### Requirement: 诊断建议只展示可执行指导
简历诊断建议 SHALL 直接展示问题、修改动作、参考写法和建议补充内容。页面 SHALL NOT 要求用户选择“已处理”或“暂不处理”，也 SHALL NOT 展示没有实际更新能力的“更新简历记录”操作。

#### Scenario: 用户查看诊断建议
- **WHEN** 页面展示一条结构化诊断建议
- **THEN** 用户可以直接阅读如何修改，不需要维护建议处理状态

#### Scenario: 用户完成外部修改后再次诊断
- **WHEN** 用户希望验证修改效果
- **THEN** 页面保留“再次诊断”操作，不展示虚假的简历更新操作
