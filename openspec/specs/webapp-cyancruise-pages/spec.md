# webapp-cyancruise-pages Specification

## Purpose
TBD - created by archiving change migrate-webapp-cyancruise-pages. Update Purpose after archive.
## Requirements
### Requirement: CyanCruise multi-page webapp shell

CyanCruise SHALL provide a CyanCruise webapp page shell under `webapp/isv/v620/cyancruise` that exposes migrated IPD page semantics as Cosmic-compatible hash route states. The shell SHALL cover workbench, onboarding, today action, assessment, resume, file upload/preview, resume diagnosis, career plan, interview, assistant, messages, employment insight, and career resources.

#### Scenario: User opens a supported page route
- **WHEN** a user opens `index.html#<route-key>` for a supported CyanCruise route
- **THEN** the webapp SHALL render the matching page state with title, route status, primary data panels, available actions, and route-specific fallback text

#### Scenario: User opens an unknown route
- **WHEN** a user opens an unknown CyanCruise hash route
- **THEN** the webapp SHALL return to the workbench or show a recoverable not-found state without calling unrelated `/cc001/*` WebAPI

### Requirement: Page data binding to migrated WebAPI contracts

Each CyanCruise page SHALL consume only existing migrated `/cc001/*` WebAPI contracts declared in `cyancruise-routes.json`. Page requests SHALL respect user/admin identity requirements and SHALL render loading, empty, success, forbidden, identity-required, unavailable, and backend-error states independently per panel.

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

The webapp pages SHALL keep the CyanCruise main loop navigable across target role/profile, assessment, resume, resume diagnosis, today action, interview, feedback-oriented messages, assistant guidance, employment insight, and career plan. Pages MAY show entry-only or pending states for capabilities whose production adapter is outside this change, but SHALL preserve the user's next available action.

#### Scenario: User moves from workbench to a page
- **WHEN** a user selects a main-loop action from the workbench or navigation
- **THEN** the webapp SHALL update the hash route, render the target page, and keep a visible path back to the workbench

#### Scenario: Capability is entry-only or pending
- **WHEN** a route is marked entry-only or depends on a later platform adapter
- **THEN** the page SHALL show what is available now, what is pending, and a safe fallback action without implying the full IPD capability is production-ready

### Requirement: Responsive and inspectable page layout

CyanCruise webapp pages SHALL use responsive, inspectable layouts suitable for Cosmic desktop and webview usage. Navigation, page headers, status chips, forms, lists, tool buttons, and cards SHALL have stable dimensions or responsive constraints so labels and controls do not overlap on common desktop and mobile widths.

#### Scenario: Layout is checked on desktop and mobile
- **WHEN** reviewers open representative CyanCruise pages on common desktop and mobile viewports
- **THEN** navigation, page content, actions, status text, and fallback messages SHALL remain readable and non-overlapping

#### Scenario: Static assets are inspected
- **WHEN** reviewers inspect static page assets
- **THEN** visible assets and controls SHALL represent actual CyanCruise workflow states rather than unrelated decoration

### Requirement: Webapp page verification

The page migration SHALL include verification that the page shell, route/API metadata, JavaScript syntax, OpenSpec artifacts, migration map, and JDK 8 build remain valid before archive.

#### Scenario: Change is verified
- **WHEN** implementation is complete
- **THEN** verification SHALL include `node webapp\isv\v620\cyancruise\validate-routes.js`, `node --check webapp\isv\v620\cyancruise\assets\app.js`, `openspec validate migrate-webapp-cyancruise-pages --strict`, `openspec validate --all --strict`, JDK 8 `.\gradlew.bat clean build`, and migration map updates

#### Scenario: Change is archived
- **WHEN** the change is archived after implementation
- **THEN** the archive folder SHALL keep the numeric archive prefix and the migration map SHALL record the final branch, commit, validation result, and temporarily excluded platform items

### Requirement: 简历页面可交互工作流
CyanCruise webapp 的 `resume` route SHALL 提供可交互的简历工作流，而不只是展示接口契约。页面 SHALL 展示当前用户的简历记录列表、简历创建表单、提交状态和恢复性错误信息，并继续遵守 `cyancruise-routes.json` 中声明的身份、API 和 fallback 约束。

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
CyanCruise webapp 的简历页 SHALL 提供创建简历记录的表单。表单 SHALL 支持标题、目标岗位、文件 key 和解析内容字段；目标岗位 SHALL 默认使用画像或工作台当前目标岗位，但用户 MUST 能为本次简历单独覆盖。

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
CyanCruise webapp SHALL 在简历创建成功后尽量刷新职业画像快照和工作台摘要，使“简历状态”体现系统真实保存的简历记录。画像刷新失败 SHALL NOT 回滚已创建的简历记录。

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

### Requirement: CyanCruise 提供考研陪伴页面
CyanCruise webapp SHALL 新增 `postgraduate` 路由作为考研陪伴入口。该页面 SHALL 与现有静态页面风格保持一致，消费 route metadata 中声明的 `/cc001/postgraduate/*` WebAPI，并提供择校建议、复习计划、错题解析和复试准备的局部加载、成功、空状态和失败提示。

#### Scenario: 从工作台进入考研陪伴
- **WHEN** 用户在 CyanCruise 工作台点击“考研陪伴”
- **THEN** 页面进入 `postgraduate` 路由，并显示可填写的考研规划表单和操作入口

#### Scenario: 查看考研 API 契约
- **WHEN** 开发者以 `?ccDebug=1` 打开考研页面
- **THEN** 页面 MAY 显示 `/cc001/postgraduate/*` WebAPI 契约、身份要求和 fallback 信息

#### Scenario: 普通用户不看调试字段
- **WHEN** 普通用户打开考研页面
- **THEN** 页面 SHALL NOT 默认展示 route key、接口路径、内部枚举或调试面板作为主要内容

### Requirement: CyanCruise 提供保研陪伴页面
CyanCruise webapp SHALL 将 `postgraduate-recommendation` 路由升级为正式保研陪伴页面。该页面 SHALL 消费 route metadata 中声明的 `/cc001/recommendation/*` WebAPI，并提供竞争力诊断、行动计划、文书润色和导师意向信的局部加载、成功、空状态和失败提示。

#### Scenario: 从深造页进入保研陪伴
- **WHEN** 用户在深造页点击“保研陪伴”
- **THEN** 页面进入 `postgraduate-recommendation` 路由，并显示可填写的保研规划表单和操作入口

#### Scenario: 普通用户不看调试字段
- **WHEN** 普通用户打开保研页面
- **THEN** 页面 SHALL NOT 默认展示 route key、接口路径、内部枚举或调试面板作为主要内容

#### Scenario: 查看保研 API 契约
- **WHEN** 开发者以 `?ccDebug=1` 打开保研页面
- **THEN** 页面 MAY 显示 `/cc001/recommendation/*` WebAPI 契约、身份要求和 fallback 信息

### Requirement: CyanCruise 提供留学陪伴页面
CyanCruise webapp SHALL 将 `study-abroad` 路由升级为正式留学陪伴页面。该页面 SHALL 消费 route metadata 中声明的 `/cc001/study-abroad/*` WebAPI，并提供画像诊断、语言规划、选校定位、个人陈述黄金线和签证网申的局部加载、成功、空状态和失败提示。

#### Scenario: 从深造页进入留学陪伴
- **WHEN** 用户在深造页点击“留学陪伴”
- **THEN** 页面进入 `study-abroad` 路由，并显示可填写的留学规划表单和操作入口

#### Scenario: 普通用户不看调试字段
- **WHEN** 普通用户打开留学页面
- **THEN** 页面 SHALL NOT 默认展示 route key、接口路径、内部枚举或调试面板作为主要内容

#### Scenario: 查看留学 API 契约
- **WHEN** 开发者以 `?ccDebug=1` 打开留学页面
- **THEN** 页面 MAY 显示 `/cc001/study-abroad/*` WebAPI 契约、身份要求和 fallback 信息

### Requirement: 考研陪伴功能拆分为独立页面
CyanCruise webapp SHALL 将考研陪伴的四个核心能力拆分为独立路由页面。`postgraduate` route SHALL 作为考研陪伴总入口展示四个能力入口；`postgraduate-school`、`postgraduate-plan`、`postgraduate-mistake` 和 `postgraduate-reexam` SHALL 分别承载择校择专业、复习计划、错题解析和复试准备。

#### Scenario: 打开考研陪伴总入口
- **WHEN** 用户打开 `index.html#postgraduate`
- **THEN** 页面 SHALL 展示“择校择专业”“复习计划”“错题解析”“复试准备”四个入口
- **AND** 每个入口 SHALL 跳转到对应独立页面，而不是在同一长页面内继续堆叠所有表单

#### Scenario: 打开择校择专业页面
- **WHEN** 用户打开 `index.html#postgraduate-school`
- **THEN** 页面 SHALL 只展示择校择专业表单、择校结果、加载提示和返回考研陪伴入口
- **AND** 页面 SHALL 调用 `/cc001/postgraduate/school-recommend`

#### Scenario: 打开复习计划页面
- **WHEN** 用户打开 `index.html#postgraduate-plan`
- **THEN** 页面 SHALL 只展示复习计划表单、计划结果、加载提示和返回考研陪伴入口
- **AND** 页面 SHALL 调用 `/cc001/postgraduate/plan/generate`

#### Scenario: 打开错题解析页面
- **WHEN** 用户打开 `index.html#postgraduate-mistake`
- **THEN** 页面 SHALL 只展示错题解析表单、解析结果、加载提示和返回考研陪伴入口
- **AND** 页面 SHALL 调用 `/cc001/postgraduate/mistake/analyze`

#### Scenario: 打开复试准备页面
- **WHEN** 用户打开 `index.html#postgraduate-reexam`
- **THEN** 页面 SHALL 只展示复试准备表单、清单结果、加载提示和返回考研陪伴入口
- **AND** 页面 SHALL 调用 `/cc001/postgraduate/reexam/prepare`

#### Scenario: 考研子页面保持可返回
- **WHEN** 用户在任一考研子页面查看结果或遇到错误
- **THEN** 页面 SHALL 保留返回“考研陪伴”总入口的操作
- **AND** 局部错误 SHALL NOT 影响其它考研子页面的入口可见性

### Requirement: 保研陪伴功能拆分为独立页面
CyanCruise webapp SHALL 将保研陪伴的四个核心能力拆分为独立路由页面。`postgraduate-recommendation` route SHALL 作为保研陪伴总入口展示四个能力入口；`recommendation-ranking`、`recommendation-background`、`recommendation-material` 和 `recommendation-tutor` SHALL 分别承载排名监控、背景提升、材料精修和导师联系。

#### Scenario: 打开保研陪伴总入口
- **WHEN** 用户打开 `index.html#postgraduate-recommendation`
- **THEN** 页面 SHALL 展示“排名监控”“背景提升”“材料精修”“导师联系”四个入口
- **AND** 每个入口 SHALL 跳转到对应独立页面，而不是在同一长页面内继续堆叠所有表单

#### Scenario: 打开排名监控页面
- **WHEN** 用户打开 `index.html#recommendation-ranking`
- **THEN** 页面 SHALL 只展示保研竞争力诊断表单、排名与背景诊断结果、加载提示和返回保研陪伴入口
- **AND** 页面 SHALL 调用 `/cc001/recommendation/diagnose`

#### Scenario: 打开背景提升页面
- **WHEN** 用户打开 `index.html#recommendation-background`
- **THEN** 页面 SHALL 只展示背景信息表单、行动计划结果、加载提示和返回保研陪伴入口
- **AND** 页面 SHALL 调用 `/cc001/recommendation/plan/generate`

#### Scenario: 打开材料精修页面
- **WHEN** 用户打开 `index.html#recommendation-material`
- **THEN** 页面 SHALL 只展示文书润色表单、润色结果、加载提示和返回保研陪伴入口
- **AND** 页面 SHALL 调用 `/cc001/recommendation/document/polish`

#### Scenario: 打开导师联系页面
- **WHEN** 用户打开 `index.html#recommendation-tutor`
- **THEN** 页面 SHALL 只展示导师意向信表单、邮件结果、加载提示和返回保研陪伴入口
- **AND** 页面 SHALL 调用 `/cc001/recommendation/tutor-letter/generate`

#### Scenario: 保研子页面保持可返回
- **WHEN** 用户在任一保研子页面查看结果或遇到错误
- **THEN** 页面 SHALL 保留返回“保研陪伴”总入口的操作
- **AND** 局部错误 SHALL NOT 影响其它保研子页面的入口可见性

### Requirement: 留学陪伴功能拆分为独立页面
CyanCruise webapp SHALL 将留学陪伴的五个核心能力拆分为独立路由页面。`study-abroad` route SHALL 作为留学陪伴总入口展示五个能力入口；`study-abroad-profile`、`study-abroad-language`、`study-abroad-school`、`study-abroad-statement` 和 `study-abroad-visa` SHALL 分别承载国家地区与申请画像、语言考试、选校定位、文书主线和签证网申。

#### Scenario: 打开留学陪伴总入口
- **WHEN** 用户打开 `index.html#study-abroad`
- **THEN** 页面 SHALL 展示“国家地区”“语言考试”“选校定位”“文书主线”“签证网申”五个入口
- **AND** 每个入口 SHALL 跳转到对应独立页面，而不是在同一长页面内继续堆叠所有表单

#### Scenario: 打开国家地区页面
- **WHEN** 用户打开 `index.html#study-abroad-profile`
- **THEN** 页面 SHALL 只展示留学申请画像表单、准备度诊断结果、加载提示和返回留学陪伴入口
- **AND** 页面 SHALL 调用 `/cc001/study-abroad/profile/diagnose`

#### Scenario: 打开语言考试页面
- **WHEN** 用户打开 `index.html#study-abroad-language`
- **THEN** 页面 SHALL 只展示语言考试规划表单、语言计划结果、加载提示和返回留学陪伴入口
- **AND** 页面 SHALL 调用 `/cc001/study-abroad/language/plan`

#### Scenario: 打开选校定位页面
- **WHEN** 用户打开 `index.html#study-abroad-school`
- **THEN** 页面 SHALL 只展示选校定位表单、选校梯度结果、加载提示和返回留学陪伴入口
- **AND** 页面 SHALL 调用 `/cc001/study-abroad/school/position`

#### Scenario: 打开文书主线页面
- **WHEN** 用户打开 `index.html#study-abroad-statement`
- **THEN** 页面 SHALL 只展示个人陈述主线表单、提纲结果、加载提示和返回留学陪伴入口
- **AND** 页面 SHALL 调用 `/cc001/study-abroad/statement/outline`

#### Scenario: 打开签证网申页面
- **WHEN** 用户打开 `index.html#study-abroad-visa`
- **THEN** 页面 SHALL 只展示签证与网申表单、清单结果、加载提示和返回留学陪伴入口
- **AND** 页面 SHALL 调用 `/cc001/study-abroad/visa/checklist`

#### Scenario: 留学子页面保持可返回
- **WHEN** 用户在任一留学子页面查看结果或遇到错误
- **THEN** 页面 SHALL 保留返回“留学陪伴”总入口的操作
- **AND** 局部错误 SHALL NOT 影响其它留学子页面的入口可见性

### Requirement: 深造页面使用专用升学路线图渲染
CyanCruise webapp SHALL 为 `further-study-home` 使用专用升学路线图渲染，不再使用仅输出“深造护航工具”卡片矩阵的通用总览渲染。页面 SHALL 复用就业路线图的视觉层级，但 SHALL 使用升学语义和升学阶段文案。

#### Scenario: 打开升学路线图页面
- **WHEN** 用户打开 `index.html?ccRoute=further-study-home`
- **THEN** 页面 SHALL 显示“升学路线图”标题、准备情况摘要和四个阶段
- **AND** 页面 SHALL NOT 显示“深造护航工具”作为首屏主内容

#### Scenario: 浏览升学中心底部
- **WHEN** 用户浏览完升学路线图、洞察和资讯
- **THEN** 页面 SHALL NOT 显示考研陪伴、保研陪伴和留学陪伴卡片区

### Requirement: 两个中心提供一致的规划核心操作
就业中心和升学中心 SHALL 均展示路线摘要、完整规划入口和生成或更新未开始阶段操作。升学中心 SHALL 在已选择具体方向后启用规划操作。

#### Scenario: 对比两个中心规划操作
- **WHEN** 用户分别打开就业中心和已选择方向的升学中心
- **THEN** 两个页面 SHALL 均显示“完整规划”和规划生成或更新操作

#### Scenario: 升学方向未选择
- **WHEN** 用户打开升学中心但未选择具体方向
- **THEN** 升学规划操作 SHALL 禁用或提示先选择方向

### Requirement: 路径规划页面跟随当前路线
`career-plan` 页面 SHALL 根据当前路线展示就业或升学完整规划，并 SHALL 在标题和目标信息中明确当前上下文。

#### Scenario: 当前路线为升学
- **WHEN** 当前路线为升学且用户打开路径规划
- **THEN** 页面 SHALL 展示当前升学方向的阶段、每日建议和本周计划
- **AND** 页面 SHALL 使用目标院校而不是目标岗位作为目标字段

### Requirement: 今日行动页面跟随当前路线
`today-action` 页面 SHALL 根据当前路线读取、更新并展示对应每日任务，任务勾选 SHALL 仅影响当前路线。

#### Scenario: 完成升学今日任务
- **WHEN** 用户在升学上下文勾选今日任务
- **THEN** 页面 SHALL 更新升学每日任务接口
- **AND** 就业任务状态 SHALL 不变

### Requirement: 升学中心 SHALL 提供考研规划资料管理界面
CyanCruise webapp SHALL 在升学中心考研方向下提供“规划依据资料”区域，支持上传、查看解析状态和删除当前用户的资料。

#### Scenario: 上传考研资料
- **WHEN** 用户选择受支持文件并点击上传
- **THEN** 页面 SHALL 展示上传和解析进度
- **AND** 完成后 SHALL 展示文件名、资料类型、更新时间及是否可用于智能规划

#### Scenario: 删除考研资料
- **WHEN** 用户点击删除资料并确认风险
- **THEN** 页面 SHALL 调用资料删除接口
- **AND** 删除成功后 SHALL 从列表移除该资料

#### Scenario: 非考研方向查看升学中心
- **WHEN** 用户当前选择保研或留学方向
- **THEN** 页面 SHALL NOT 将考研资料默认用于该方向的智能规划
- **AND** 页面 MAY 隐藏考研资料上传区域

### Requirement: 升学规划生成入口 SHALL 说明资料用途
升学中心的考研规划生成入口 SHALL 告知用户，成功解析的用户资料会作为下一次路线生成依据，公共知识库只提供长期通用方法。

#### Scenario: 用户准备生成考研路线
- **WHEN** 用户在考研方向查看生成入口
- **THEN** 页面 SHALL 显示当前可用于规划的资料数量
- **AND** 用户 SHALL 能在生成前增删资料

### Requirement: 考研路线 SHALL 使用与就业路线一致的情况概览布局
考研路线页面 SHALL 复用就业路线的“当前情况概览”卡片结构，并使用普通中文按考研语义展示规划依据。

#### Scenario: 查看智能生成的考研路线
- **WHEN** 用户打开已生成的考研完整路线
- **THEN** 页面 SHALL 将摘要分点展示为当前阶段、已有基础、准备差距、考研目标和待确认信息
- **AND** 页面 SHALL NOT 将考研准备差距标记为求职能力缺口

### Requirement: 考研页面 SHALL 只展示真实规划路线
考研页面 SHALL 只渲染服务端返回的真实智能体路线，不得在本地预览、接口失败或空状态下构造示例阶段和每日任务。

#### Scenario: 尚无真实路线
- **WHEN** 路线摘要返回 `hasPlan=false` 或没有阶段
- **THEN** 页面 SHALL 展示“尚未生成真实考研规划”和生成按钮
- **AND** 页面 SHALL NOT 展示示例路线卡、虚假进度或“更新未开始阶段”按钮

#### Scenario: 仅完成升学方向和画像准备
- **WHEN** 用户已选择考研方向并填写目标院校，但尚无通过校验的真实智能体路线
- **THEN** 四步准备引导 MAY 展示方向和画像准备状态，但 SHALL NOT 被计入路线阶段或执行进度
- **AND** 生成按钮 SHALL 显示“生成考研规划”
- **AND** 浏览器缓存的旧阶段或本地进度 SHALL NOT 将按钮改为“更新未开始阶段”

#### Scenario: 服务端已确认真实考研路线
- **WHEN** 路线摘要返回 `hasPlan=true`、`planningMode=AGENT`、`agentStatus=AGENT_GENERATED` 且至少包含三个阶段
- **THEN** 页面 SHALL 展示服务端返回的阶段路线图
- **AND** 页面 SHALL NOT 因为再次解析阶段时间文案而隐藏该路线
- **AND** 路线空状态与生成按钮 SHALL 使用同一个真实规划判据

#### Scenario: 阶段时间包含实际年月
- **WHEN** 真实考研路线的阶段时间包含“2026年7月—2026年8月”等日历年月
- **THEN** 页面 SHALL 将其作为阶段日期展示，不得把 `2026年` 解释为规划跨度
- **AND** 页面 SHALL 完整展示服务端已验证的十二个月阶段

