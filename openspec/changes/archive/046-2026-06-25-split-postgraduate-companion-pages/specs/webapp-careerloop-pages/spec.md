## ADDED Requirements

### Requirement: 考研陪伴功能拆分为独立页面
CareerLoop webapp SHALL 将考研陪伴的四个核心能力拆分为独立路由页面。`postgraduate` route SHALL 作为考研陪伴总入口展示四个能力入口；`postgraduate-school`、`postgraduate-plan`、`postgraduate-mistake` 和 `postgraduate-reexam` SHALL 分别承载择校择专业、复习计划、错题解析和复试准备。

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

#### Scenario: 子页面保持可返回
- **WHEN** 用户在任一考研子页面查看结果或遇到错误
- **THEN** 页面 SHALL 保留返回“考研陪伴”总入口的操作
- **AND** 局部错误 SHALL NOT 影响其它考研子页面的入口可见性
