## ADDED Requirements

### Requirement: 考研页面分析由真实升学陪伴智能体生成
系统 SHALL 将考研择校、复习计划、错题解析和复试准备分别映射为 `POSTGRADUATE_SCHOOL_RECOMMEND`、`POSTGRADUATE_PLAN_GENERATE`、`POSTGRADUATE_MISTAKE_ANALYZE` 和 `POSTGRADUATE_REEXAM_PREPARE`，并由统一升学陪伴智能体生成结果。

#### Scenario: 生成考研页面结果
- **WHEN** 当前用户提交任一考研页面分析请求
- **THEN** 系统 SHALL 调用升学陪伴智能体并返回对应的现有结果 DTO
- **AND** SHALL NOT 调用本地 Helper 生成规则结果

#### Scenario: 考研智能分析失败
- **WHEN** 智能体未配置、调用失败或返回无效结构
- **THEN** 系统 SHALL 返回普通中文可重试提示
- **AND** SHALL NOT 返回默认院校、默认计划、默认答案或默认清单

### Requirement: 考研分析表单保留用户真实输入
系统 SHALL 在调用智能体前按当前用户和 `taskType` 保存本次表单草稿，并在页面重绘、分析失败或重新进入页面时恢复该用户最后保存的内容。

#### Scenario: 智能分析失败后保留输入
- **WHEN** 当前用户提交考研择校表单且智能体返回失败
- **THEN** 页面 SHALL 继续显示本次提交的本科学校、学校层次、成绩、英语水平、期望地区、目标专业和备考偏好
- **AND** SHALL NOT 因加载状态或错误状态重绘而清空输入

#### Scenario: 重新进入页面恢复草稿
- **WHEN** 当前用户重新打开考研择校页面
- **THEN** 系统 SHALL 从服务端读取该用户 `POSTGRADUATE_SCHOOL_RECOMMEND` 的最后一份草稿
- **AND** SHALL NOT 读取其他用户或其他任务类型的草稿
