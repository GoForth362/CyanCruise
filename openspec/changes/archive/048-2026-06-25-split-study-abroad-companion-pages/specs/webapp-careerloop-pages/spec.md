## ADDED Requirements

### Requirement: 留学陪伴功能拆分为独立页面
CareerLoop webapp SHALL 将留学陪伴的五个核心能力拆分为独立路由页面。`study-abroad` route SHALL 作为留学陪伴总入口展示五个能力入口；`study-abroad-profile`、`study-abroad-language`、`study-abroad-school`、`study-abroad-statement` 和 `study-abroad-visa` SHALL 分别承载国家地区与申请画像、语言考试、选校定位、文书主线和签证网申。

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

#### Scenario: 子页面保持可返回
- **WHEN** 用户在任一留学子页面查看结果或遇到错误
- **THEN** 页面 SHALL 保留返回“留学陪伴”总入口的操作
- **AND** 局部错误 SHALL NOT 影响其它留学子页面的入口可见性
