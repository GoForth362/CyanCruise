## MODIFIED Requirements

### Requirement: 默认入口采用平台菜单外部链接落地页
CyanCruise webapp 默认入口 SHALL 采用面向金蝶平台菜单的外部链接落地页，而不是工程验收型 route 工作台。页面 SHALL 由 hash 决定当前内容页，并在右侧内容区承载路线图和主要功能入口。

#### Scenario: 默认打开工作台
- **WHEN** 普通用户打开 `/ierp/isv/v620/cyancruise/index.htm?ccRoute=workbench`
- **THEN** 页面 SHALL 在首屏展示基础信息表单、就业/深造路线选择和路线入口
- **AND** 页面 SHALL NOT 以大面积 hero、接口路线、route chip、横向滚动 route 清单或工程状态面板作为首屏主要内容

#### Scenario: 平台侧边栏打开就业页
- **WHEN** 用户从金蝶平台侧边栏点击“就业”
- **THEN** 平台菜单 SHALL 打开 `/ierp/isv/v620/cyancruise/index.htm?ccRoute=employment-home`
- **AND** 页面 SHALL 先展示就业路线图，并在后续区域提供简历制作和简历诊断入口
- **AND** AI 模拟面试与全景仿真面试 SHALL 作为独立平台菜单能力，不从就业页提供跳转入口

#### Scenario: 首页进入升学路线图
- **WHEN** 用户在首页选择“深造”并点击路线入口
- **THEN** 页面 SHALL 打开 `/ierp/isv/v620/cyancruise/index.htm?ccRoute=further-study-home`
- **AND** 页面 SHALL 展示升学路线图、方向选择、升学洞察和升学资讯
- **AND** 页面 SHALL NOT 在底部重复展示三个陪伴入口卡片

#### Scenario: 平台侧边栏打开升学路线图
- **WHEN** 用户从金蝶平台侧边栏点击“升学路线图”
- **THEN** 平台菜单 SHALL 打开 `/ierp/isv/v620/cyancruise/index.htm?ccRoute=further-study-home`
- **AND** 页面 SHALL NOT 再展示旧的“深造护航”工具总览首屏

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
