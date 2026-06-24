## ADDED Requirements

### Requirement: CareerLoop 提供考研陪伴页面
CareerLoop webapp SHALL 新增 `postgraduate` 路由作为考研陪伴入口。该页面 SHALL 与现有静态页面风格保持一致，消费 route metadata 中声明的 `/cc001/postgraduate/*` WebAPI，并提供择校建议、复习计划、错题解析和复试准备的局部加载、成功、空状态和失败提示。

#### Scenario: 从工作台进入考研陪伴
- **WHEN** 用户在 CareerLoop 工作台点击“考研陪伴”
- **THEN** 页面进入 `postgraduate` 路由，并显示可填写的考研规划表单和操作入口

#### Scenario: 查看考研 API 契约
- **WHEN** 开发者以 `?ccDebug=1` 打开考研页面
- **THEN** 页面 MAY 显示 `/cc001/postgraduate/*` WebAPI 契约、身份要求和 fallback 信息

#### Scenario: 普通用户不看调试字段
- **WHEN** 普通用户打开考研页面
- **THEN** 页面 SHALL NOT 默认展示 route key、接口路径、内部枚举或调试面板作为主要内容
