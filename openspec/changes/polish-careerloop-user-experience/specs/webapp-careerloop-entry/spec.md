## ADDED Requirements

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
