## ADDED Requirements

### Requirement: 保研页面分析由真实升学陪伴智能体生成
系统 SHALL 将保研竞争力诊断、行动计划、文书润色和导师意向信分别映射为 `RECOMMENDATION_DIAGNOSE`、`RECOMMENDATION_PLAN_GENERATE`、`RECOMMENDATION_DOCUMENT_POLISH` 和 `RECOMMENDATION_TUTOR_LETTER`，并由统一升学陪伴智能体生成结果。

#### Scenario: 生成保研页面结果
- **WHEN** 当前用户提交任一保研页面分析请求
- **THEN** 系统 SHALL 调用升学陪伴智能体并返回对应的现有结果 DTO
- **AND** SHALL NOT 调用本地 Helper 生成规则结果

#### Scenario: 保研智能分析失败
- **WHEN** 智能体要求补充资料、调用失败或返回无效结构
- **THEN** 系统 SHALL 返回普通中文提示
- **AND** SHALL NOT 返回默认评分、默认行动、默认文书或默认邮件
