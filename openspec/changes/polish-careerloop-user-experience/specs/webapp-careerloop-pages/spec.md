## ADDED Requirements

### Requirement: 页面默认展示业务语义
CareerLoop 多页面 SHALL 在默认用户模式下优先展示业务语义、用户数据和下一步动作，而不是 route key、接口路径、audience、available、entry-only 等开发迁移标识。

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
CareerLoop 默认工作台 SHALL 使用功能卡片表达用户可执行能力。卡片 SHALL 包含清晰图标、功能名称、短说明和可点击目标，且默认文案 SHALL 避免接口路径、fileKey、route key 等工程字段。

#### Scenario: 简历外部链接页展示
- **WHEN** 用户通过金蝶平台菜单打开 `#resume-home`
- **THEN** 页面 SHALL 展示 AI简历制作、AI简历修改两个 IPD 核心入口
- **AND** AI简历制作 SHALL 跳转到现有简历 route，AI简历修改 SHALL 跳转到现有简历诊断 route

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
