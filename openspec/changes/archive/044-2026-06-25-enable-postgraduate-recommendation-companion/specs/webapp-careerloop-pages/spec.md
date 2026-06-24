## ADDED Requirements

### Requirement: CareerLoop 提供保研陪伴页面
CareerLoop webapp SHALL 将 `postgraduate-recommendation` 路由升级为正式保研陪伴页面。该页面 SHALL 消费 route metadata 中声明的 `/cc001/recommendation/*` WebAPI，并提供竞争力诊断、行动计划、文书润色和导师意向信的局部加载、成功、空状态和失败提示。

#### Scenario: 从深造页进入保研陪伴
- **WHEN** 用户在深造页点击“保研陪伴”
- **THEN** 页面进入 `postgraduate-recommendation` 路由，并显示可填写的保研规划表单和操作入口

#### Scenario: 普通用户不看调试字段
- **WHEN** 普通用户打开保研页面
- **THEN** 页面 SHALL NOT 默认展示 route key、接口路径、内部枚举或调试面板作为主要内容

#### Scenario: 查看保研 API 契约
- **WHEN** 开发者以 `?ccDebug=1` 打开保研页面
- **THEN** 页面 MAY 显示 `/cc001/recommendation/*` WebAPI 契约、身份要求和 fallback 信息
