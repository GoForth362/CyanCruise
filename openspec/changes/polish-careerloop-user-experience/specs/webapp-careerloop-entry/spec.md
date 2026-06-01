## ADDED Requirements

### Requirement: 默认用户导航聚焦主循环
CareerLoop webapp 入口 SHALL 在默认用户模式下只展示普通用户可理解、可继续操作的主循环导航入口。调试页、entry-only 验证页、接口清单页和未形成用户闭环的能力 SHALL NOT 出现在默认主导航中。

#### Scenario: 默认打开工作台
- **WHEN** 用户不带 `debug` 参数打开 CareerLoop webapp
- **THEN** 顶部导航 SHALL 只展示工作台、新用户引导、今日行动、职业测评、简历、简历诊断、职业计划、模拟面试、求职助手、消息中心、就业洞察等主循环入口中当前允许默认展示的项

#### Scenario: 调试页不进入默认导航
- **WHEN** `file-upload-preview`、接口契约页或其他调试用途 route 存在于 route map
- **THEN** 默认主导航 SHALL NOT 展示这些 route 的入口

#### Scenario: Hash 直达隐藏 route
- **WHEN** 开发者直接访问隐藏 route 的 hash
- **THEN** 页面 SHALL 显示该 route 或给出可恢复提示，但 SHALL NOT 把该 route 加入默认主导航

### Requirement: 调试模式恢复工程信息
CareerLoop webapp 入口 SHALL 支持显式调试模式，用于展示 route metadata、接口契约、entry-only 状态和隐藏页面入口。调试模式 MUST 由 URL 参数或等价显式开关启用，默认不得开启。

#### Scenario: 使用 debug 参数打开
- **WHEN** 用户或开发者使用 `?debug=1` 打开 CareerLoop webapp
- **THEN** 页面 SHALL 显示调试导航项和工程状态信息，包括隐藏 route、接口契约和 route/status metadata

#### Scenario: 不带 debug 参数
- **WHEN** 普通用户不带 `debug=1` 打开页面
- **THEN** 页面 SHALL 隐藏调试导航项和工程状态信息
