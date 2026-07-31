## ADDED Requirements

### Requirement: 服务端统一调用升学陪伴智能体
系统 SHALL 通过服务端运行时配置的已发布升学陪伴智能体执行 13 项考研、保研和留学分析，SHALL NOT 将智能体编号、平台凭据或 SDK 调用暴露给浏览器。

#### Scenario: 调用已配置智能体
- **WHEN** `cc001.agent.platform.study.companion.enabled=true` 且配置了非空 `agentNumber`
- **THEN** 系统 SHALL 使用金蝶 Agent SDK 调用该智能体
- **AND** SHALL 将页面请求作为 `question` 对应的完整 JSON 原文发送

#### Scenario: 智能体未配置
- **WHEN** 统一升学陪伴智能体未启用或没有配置编号
- **THEN** 系统 SHALL 返回普通中文不可用提示
- **AND** SHALL NOT 生成规则、模板、示例或默认分析结果

### Requirement: 使用固定升学分析请求信封
系统 SHALL 为每次调用生成包含 `mode`、`taskType`、`currentDate`、`payload`、`profileContext` 和 `userMaterials` 的结构化请求，其中 `mode` SHALL 为 `FURTHER_STUDY_ANALYSIS`，`taskType` SHALL 来自服务端固定映射。

#### Scenario: 封装页面请求
- **WHEN** 当前用户提交任一升学页面表单
- **THEN** `payload` SHALL 保存该请求 DTO 的真实字段
- **AND** `profileContext` SHALL 使用服务端确认的当前用户身份
- **AND** 模型 SHALL NOT 通过请求字段决定用户归属

### Requirement: 严格校验结构化智能体结果
系统 SHALL 仅接受任务类型一致、外层状态为 `OK`、内层 `result.status` 为 `OK` 且可映射到目标 DTO 的结果。

#### Scenario: 接受有效结果
- **WHEN** 智能体返回与请求 `taskType` 一致的有效 JSON 结果
- **THEN** 系统 SHALL 将 `result` 映射为现有业务结果 DTO 并返回

#### Scenario: 拒绝串台或无效结果
- **WHEN** 返回任务类型不一致、状态要求补充资料、JSON 无效或结果无法映射
- **THEN** 系统 SHALL 返回普通中文失败或补充资料提示
- **AND** SHALL NOT 保存或返回半成品及虚假结果

### Requirement: 诊断信息保护用户内容和平台凭据
系统 SHALL 记录最小调用状态、目标尾号、耗时和错误分类，SHALL NOT 记录完整用户请求、平台凭据或完整智能体编号。

#### Scenario: Agent SDK 调用失败
- **WHEN** SDK 抛出异常或没有返回可用答案
- **THEN** 服务端日志 SHALL 仅包含脱敏诊断信息
- **AND** 页面 SHALL 收到普通中文可重试提示
