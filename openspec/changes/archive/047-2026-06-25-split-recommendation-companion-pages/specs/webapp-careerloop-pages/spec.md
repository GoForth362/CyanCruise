## ADDED Requirements

### Requirement: 保研陪伴功能拆分为独立页面
CareerLoop webapp SHALL 将保研陪伴的四个核心能力拆分为独立路由页面。`postgraduate-recommendation` route SHALL 作为保研陪伴总入口展示四个能力入口；`recommendation-ranking`、`recommendation-background`、`recommendation-material` 和 `recommendation-tutor` SHALL 分别承载排名监控、背景提升、材料精修和导师联系。

#### Scenario: 打开保研陪伴总入口
- **WHEN** 用户打开 `index.html#postgraduate-recommendation`
- **THEN** 页面 SHALL 展示“排名监控”“背景提升”“材料精修”“导师联系”四个入口
- **AND** 每个入口 SHALL 跳转到对应独立页面，而不是在同一长页面内继续堆叠所有表单

#### Scenario: 打开排名监控页面
- **WHEN** 用户打开 `index.html#recommendation-ranking`
- **THEN** 页面 SHALL 只展示保研竞争力诊断表单、排名与背景诊断结果、加载提示和返回保研陪伴入口
- **AND** 页面 SHALL 调用 `/cc001/recommendation/diagnose`

#### Scenario: 打开背景提升页面
- **WHEN** 用户打开 `index.html#recommendation-background`
- **THEN** 页面 SHALL 只展示背景信息表单、行动计划结果、加载提示和返回保研陪伴入口
- **AND** 页面 SHALL 调用 `/cc001/recommendation/plan/generate`

#### Scenario: 打开材料精修页面
- **WHEN** 用户打开 `index.html#recommendation-material`
- **THEN** 页面 SHALL 只展示文书润色表单、润色结果、加载提示和返回保研陪伴入口
- **AND** 页面 SHALL 调用 `/cc001/recommendation/document/polish`

#### Scenario: 打开导师联系页面
- **WHEN** 用户打开 `index.html#recommendation-tutor`
- **THEN** 页面 SHALL 只展示导师意向信表单、邮件结果、加载提示和返回保研陪伴入口
- **AND** 页面 SHALL 调用 `/cc001/recommendation/tutor-letter/generate`

#### Scenario: 子页面保持可返回
- **WHEN** 用户在任一保研子页面查看结果或遇到错误
- **THEN** 页面 SHALL 保留返回“保研陪伴”总入口的操作
- **AND** 局部错误 SHALL NOT 影响其它保研子页面的入口可见性
