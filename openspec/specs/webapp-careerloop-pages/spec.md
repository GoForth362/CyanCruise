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

