## Purpose

定义 CyanCruise 在苍穹 webapp 资源侧如何承载 CareerLoop 首个可用入口，包括工作台、onboarding gate、主循环导航、页面到 Cosmic WebAPI 的契约映射、状态降级、迁移边界和验证要求。
## Requirements
### Requirement: CareerLoop webapp workbench entry

CyanCruise SHALL provide a CareerLoop webapp entry that makes the migrated job-preparation loop usable from `webapp/isv/v620/`. The entry SHALL present the user's target role/profile status, today's recommended action, and direct action entries for onboarding, assessment, resume, resume diagnosis, interview, career plan, and assistant chat.

#### Scenario: Existing user opens the workbench

- **WHEN** a user with a resolvable `userId` opens the CareerLoop webapp entry
- **THEN** the workbench SHALL request the migrated profile and today-action contracts and display the target role, readiness signals, today's next action, and available CareerLoop action entries

#### Scenario: Missing user id opens the workbench

- **WHEN** the webapp cannot resolve a `userId`
- **THEN** the entry SHALL show an explicit identity-required state and SHALL NOT call user-owned CareerLoop WebAPI with a hardcoded production user

### Requirement: Onboarding gate

The CareerLoop webapp entry SHALL gate new or incomplete users through onboarding semantics migrated from IPD. The gate SHALL collect or request the minimum CareerLoop inputs needed by existing backend contracts: identity type, target role or intended direction, resume ownership state, and optional preference signals.

#### Scenario: Profile is incomplete

- **WHEN** the profile snapshot has no onboarding block or has no usable target role/preference signal
- **THEN** the webapp SHALL guide the user to onboarding before presenting the workbench as complete

#### Scenario: User submits onboarding

- **WHEN** a user submits onboarding information from the webapp
- **THEN** the webapp SHALL call the migrated career-profile onboarding contract and refresh the workbench state from the returned profile snapshot

### Requirement: Route and API contract map

The migration SHALL define a route and API contract map for the CareerLoop webapp entry. The map SHALL connect IPD page semantics to CyanCruise webapp route keys and SHALL list the existing Cosmic WebAPI contracts consumed by each route.

#### Scenario: Route map is reviewed

- **WHEN** reviewers inspect the webapp migration artifacts
- **THEN** they SHALL find mappings for IPD home, onboarding, agent/today action, assessment, resume, resume diagnosis, interview, career plan, and assistant routes

#### Scenario: Page action calls backend contract

- **WHEN** a page action needs server data
- **THEN** the route/API map SHALL identify the existing `/cc001/*` WebAPI contract, required `userId` usage, expected DTO semantics, and fallback state when the contract is unavailable

### Requirement: Progressive page states

The CareerLoop webapp SHALL define consistent page states for loading, empty data, backend error, feature unavailable, and follow-up migration pending. These states SHALL be visible in the page contract and SHALL avoid implying that unimplemented IPD capabilities are production-ready.

#### Scenario: Backend call fails

- **WHEN** a migrated WebAPI call fails or returns an unavailable response
- **THEN** the webapp SHALL keep the workbench navigable and display a recoverable error or unavailable state for the affected capability

#### Scenario: Optional capability is not migrated

- **WHEN** an IPD page capability such as notifications, CDUT content details, voice interview, file preview, or admin management is not in this change scope
- **THEN** the webapp SHALL mark it as pending or omit it from the first usable entry instead of exposing a broken action

### Requirement: Migration boundary for webapp implementation

The CareerLoop webapp migration SHALL rebuild page resources for CyanCruise and SHALL NOT directly migrate IPD Vue, uni-app, Pinia/store, Vite, uView, page lifecycle, mini-program storage, Spring Boot, JPA, Flyway, or Java 17 implementation details.

#### Scenario: Implementation is inspected

- **WHEN** implementation files are reviewed
- **THEN** they SHALL reside under CyanCruise target modules, primarily `webapp/isv/v620/`, and SHALL NOT require `F:\Project\IPD\frontend` at runtime

#### Scenario: Dependencies are checked

- **WHEN** dependency changes are reviewed
- **THEN** the change SHALL NOT introduce Vue/uni-app or other IPD frontend runtime dependencies unless a later approved change explicitly justifies a Cosmic-compatible frontend build path

### Requirement: Responsive and inspectable entry assets

The CareerLoop webapp entry SHALL use inspectable visual assets and responsive layout constraints suitable for desktop and mobile webview usage. Fixed-format elements such as navigation, action entries, status cards, and quick action controls SHALL have stable dimensions or responsive constraints so labels and controls do not overlap.

#### Scenario: Desktop and mobile layouts are checked

- **WHEN** the entry is opened in common desktop and mobile viewport sizes
- **THEN** primary actions, navigation, user state, and today's action SHALL remain readable, non-overlapping, and visually associated with the CareerLoop product

#### Scenario: Static assets are checked

- **WHEN** reviewers inspect webapp assets
- **THEN** assets SHALL represent actual CareerLoop page states or controls and SHALL NOT be only decorative placeholders unrelated to the migrated workflow

### Requirement: Verification and documentation

The webapp entry migration SHALL include verification and documentation that prove the proposed page contract is present, OpenSpec-valid, and aligned with the IPD-to-CyanCruise migration map.

#### Scenario: Change is verified before archive

- **WHEN** implementation is complete
- **THEN** verification SHALL include strict OpenSpec validation, static resource/route-map checks, JDK 8 Gradle build validation, and migration map updates before archive

#### Scenario: Migration map is updated

- **WHEN** the change is finalized
- **THEN** `docs/ipd-to-cyancruise-migration-map.md` SHALL record IPD source paths, CyanCruise webapp target paths, route/API mapping, temporarily excluded items, and validation results

### Requirement: Platform-mounted entry identity mode

The CareerLoop webapp entry SHALL distinguish production Cosmic identity mode from development fallback identity mode. Production mode SHALL require platform login context for user-owned or admin-owned calls, while development fallback mode MAY use query, localStorage, or manual input only when explicitly marked as non-production.

#### Scenario: Entry runs in production identity mode

- **WHEN** the CareerLoop webapp entry is opened from a Cosmic menu or KDDT-mounted entry
- **THEN** identity resolution SHALL prefer the Cosmic platform context and SHALL NOT silently fall back to query/localStorage as production identity

#### Scenario: Entry runs in development fallback mode

- **WHEN** developers open the static CareerLoop entry outside a real Cosmic login context
- **THEN** query, localStorage, or manual `userId` fallback SHALL remain available for validation and SHALL be visibly treated as a development fallback before user-owned WebAPI calls

### Requirement: Platform mount metadata for existing routes

The CareerLoop route/API map SHALL expose enough platform mount metadata for each existing route to be reviewed before menu publication. Metadata SHALL include publishability, audience, role requirement, target route, and fallback behavior.

#### Scenario: Existing route is prepared for menu review

- **WHEN** reviewers inspect any existing route in `careerloop-routes.json`
- **THEN** they SHALL be able to determine whether the route is user-facing, admin-only, entry-only, hidden, or pending platform configuration

#### Scenario: Admin route is reviewed

- **WHEN** reviewers inspect the admin console route
- **THEN** the route metadata SHALL show administrator identity and role requirements before any admin menu publication

### Requirement: Entry navigation to multi-page route states

The CareerLoop webapp entry SHALL navigate from the existing workbench into multi-page route states for the migrated main-loop capabilities. Navigation SHALL use stable route keys from `careerloop-routes.json` and SHALL keep the workbench, onboarding, today action, assessment, resume, file upload/preview, resume diagnosis, career plan, interview, assistant, messages, employment insight, and career resources reachable when their route metadata marks them visible.

#### Scenario: Workbench action opens page state
- **WHEN** a user activates a visible workbench action or navigation item
- **THEN** the entry SHALL update the hash to the mapped route key and render that page state without requiring a full page reload

#### Scenario: Route metadata hides a capability
- **WHEN** a capability is marked hidden, pending, admin-only, or otherwise not user-facing in `careerloop-routes.json`
- **THEN** the entry SHALL not expose it as a normal user action, and SHALL show an explicit unavailable or forbidden state if the hash is opened directly

### Requirement: Route/API map consistency for page states

The CareerLoop entry SHALL keep page navigation, platform mount metadata, and WebAPI consumption consistent with `careerloop-routes.json`. Each rendered page state SHALL have a route-map entry that declares IPD source semantics, target hash, status, publishability or platform mount metadata, WebAPI contracts, identity requirements, and fallback behavior.

#### Scenario: Page state is reviewed
- **WHEN** reviewers inspect any rendered CareerLoop page state
- **THEN** they SHALL be able to trace it to a `careerloop-routes.json` route entry and to the relevant IPD source paths in OpenSpec or migration documentation

#### Scenario: Route validation runs
- **WHEN** `node webapp\isv\v620\careerloop\validate-routes.js` is executed
- **THEN** validation SHALL confirm that rendered route keys, platform mount route keys, WebAPI paths, identity metadata, and page visibility metadata are internally consistent

### Requirement: 默认用户导航聚焦主循环
CyanCruise webapp 入口 SHALL 在默认用户模式下只展示普通用户可理解、可继续操作的主循环导航入口。调试页、entry-only 验证页、接口清单页和未形成用户闭环的能力 SHALL NOT 出现在默认主导航中。

#### Scenario: 默认打开工作台
- **WHEN** 用户不带 `debug` 参数打开 CyanCruise webapp
- **THEN** 顶部导航 SHALL 只展示工作台、新用户引导、今日行动、职业测评、简历、简历诊断、职业计划、模拟面试、求职助手、消息中心、就业洞察等主循环入口中当前允许默认展示的项

#### Scenario: 调试页不进入默认导航
- **WHEN** `file-upload-preview`、接口契约页或其他调试用途 route 存在于 route map
- **THEN** 默认主导航 SHALL NOT 展示这些 route 的入口

#### Scenario: Hash 直达隐藏 route
- **WHEN** 开发者直接访问隐藏 route 的 hash
- **THEN** 页面 SHALL 显示该 route 或给出可恢复提示，但 SHALL NOT 把该 route 加入默认主导航

### Requirement: 调试模式恢复工程信息
CyanCruise webapp 入口 SHALL 支持显式调试模式，用于展示 route metadata、接口契约、entry-only 状态和隐藏页面入口。调试模式 MUST 由 URL 参数或等价显式开关启用，默认不得开启。

#### Scenario: 使用 debug 参数打开
- **WHEN** 用户或开发者使用 `?ccDebug=1` 打开 CyanCruise webapp
- **THEN** 页面 SHALL 显示调试导航项和工程状态信息，包括隐藏 route、接口契约和 route/status metadata

#### Scenario: 不带 debug 参数
- **WHEN** 普通用户不带 `ccDebug=1` 打开页面
- **THEN** 页面 SHALL 隐藏调试导航项和工程状态信息

### Requirement: 默认入口采用平台菜单外部链接落地页
CyanCruise webapp 默认入口 SHALL 采用面向金蝶平台菜单的外部链接落地页，而不是工程验收型 route 工作台。页面 SHALL 由 hash 决定当前内容页，并在右侧内容区使用功能卡片矩阵承载主要入口。

#### Scenario: 默认打开工作台
- **WHEN** 普通用户打开 `/ierp/isv/v620/cyancruise/index.htm?ccRoute=workbench`
- **THEN** 页面 SHALL 在首屏展示基础信息表单、就业/深造路线选择和推荐功能入口
- **AND** 页面 SHALL NOT 以大面积 hero、接口路线、route chip、横向滚动 route 清单或工程状态面板作为首屏主要内容

#### Scenario: 平台侧边栏打开就业页
- **WHEN** 用户从金蝶平台侧边栏点击“就业”
- **THEN** 平台菜单 SHALL 打开 `/ierp/isv/v620/cyancruise/index.htm?ccRoute=employment-home`
- **AND** 页面 SHALL 展示 AI简历制作、AI简历修改、全景仿真面试、AI模拟面试四个核心入口
- **AND** 就业页 SHALL NOT 与首页共用 `ccRoute=workbench` 地址

#### Scenario: 平台侧边栏打开深造页
- **WHEN** 用户从金蝶平台侧边栏点击“深造”
- **THEN** 平台菜单 SHALL 打开 `/ierp/isv/v620/cyancruise/index.htm?ccRoute=further-study-home`
- **AND** 页面 SHALL 展示考研、保研、留学三个规划入口
- **AND** 当前阶段 SHALL 只展示规划入口，不宣称真实 Agent 能力已完成

#### Scenario: 平台侧边栏打开功能页
- **WHEN** 用户从金蝶平台侧边栏点击“简历”“面试”等外部链接菜单
- **THEN** CyanCruise SHALL 按 URL hash 渲染对应右侧内容页
- **AND** CyanCruise 页面 SHALL NOT 再绘制与金蝶平台侧边栏重复的页面内左侧导航

#### Scenario: 点击功能卡片
- **WHEN** 用户点击一个已接入功能卡片
- **THEN** 页面 SHALL 跳转到对应业务 route 或执行对应操作
- **AND** 未接入功能 SHALL 显示禁用态或“即将接入”提示，不应暴露接口契约

#### Scenario: 未识别外部链接 hash
- **WHEN** 平台菜单配置了 CyanCruise 暂不识别的 hash
- **THEN** 页面 SHALL 显示可恢复提示并提供返回工作台入口

