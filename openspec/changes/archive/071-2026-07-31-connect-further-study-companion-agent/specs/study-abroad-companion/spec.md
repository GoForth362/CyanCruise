## ADDED Requirements

### Requirement: 留学页面分析由真实升学陪伴智能体生成
系统 SHALL 将留学画像诊断、语言计划、选校定位、个人陈述主线和签证网申清单分别映射为 `STUDY_ABROAD_PROFILE_DIAGNOSE`、`STUDY_ABROAD_LANGUAGE_PLAN`、`STUDY_ABROAD_SCHOOL_POSITION`、`STUDY_ABROAD_STATEMENT_OUTLINE` 和 `STUDY_ABROAD_VISA_CHECKLIST`，并由统一升学陪伴智能体生成结果。

#### Scenario: 生成留学页面结果
- **WHEN** 当前用户提交任一留学页面分析请求
- **THEN** 系统 SHALL 调用升学陪伴智能体并返回对应的现有结果 DTO
- **AND** SHALL NOT 调用本地 Helper 生成规则结果

#### Scenario: 留学智能分析失败
- **WHEN** 智能体要求补充资料、调用失败或返回无效结构
- **THEN** 系统 SHALL 返回普通中文提示
- **AND** SHALL NOT 返回默认准备度、默认计划、默认院校、默认文书或默认清单
