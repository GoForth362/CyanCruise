## ADDED Requirements

### Requirement: 统一升学陪伴智能体 SHALL 遵守可生成输入契约

统一升学陪伴智能体 SHALL 对 13 个 `taskType` 使用页面请求中的真实字段。核心字段齐全时 SHALL 返回 `OK` 和对应结果 DTO；非核心资料缺少时 SHALL 在 `result.missingInfo` 中说明，不得返回 `NEED_MORE_INFO`。

#### Scenario: 完整核心输入生成结果
- **WHEN** 任一陪伴页面提交该任务的核心字段
- **THEN** 智能体 SHALL 返回同一 `taskType`、外层和 `result` 均为 `OK` 的单个 JSON 对象

#### Scenario: 核心字段缺失
- **WHEN** 任务无法完成核心目标且缺少页面标注的核心字段
- **THEN** 智能体 SHALL 返回 `NEED_MORE_INFO` 并在 `message` 中列出缺失字段中文名称

### Requirement: 平台提示词 SHALL 覆盖全部 13 个任务

系统 SHALL 提供可直接用于平台系统提示词的中文文档，覆盖考研 4 项、保研 4 项和留学 5 项任务的最小输入、结果字段、状态和禁止行为。

#### Scenario: 配置统一智能体
- **WHEN** 配置人员复制仓库提供的系统提示词并发布智能体
- **THEN** 智能体 SHALL 可处理全部 13 个已定义任务类型而不混用结果结构
