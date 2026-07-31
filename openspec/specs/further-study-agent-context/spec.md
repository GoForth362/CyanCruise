# further-study-agent-context Specification

## Purpose
TBD - created by archiving change persist-further-study-results-and-agent-context. Update Purpose after archive.
## Requirements
### Requirement: 向升学智能体提供受限历史上下文
系统 SHALL 在调用任一升学分析任务前，将本次输入保存到该任务专属表，并按当前用户从该表回读输入后发送给智能体。

#### Scenario: 同方向历史可用于分析
- **WHEN** 用户再次提交某一方向的升学分析
- **THEN** 智能体输入 SHALL 使用该用户该任务专属表中刚保存的输入
- **AND** 不得包含其他用户数据

#### Scenario: 历史为空
- **WHEN** 当前用户没有该方向的历史记录
- **THEN** 系统 SHALL 仅发送本次请求和空上下文
- **AND** 仍正常执行分析

### Requirement: 任务流响应使用唯一信封
系统 SHALL 仅接受任务流最终节点直接返回的业务 JSON；根对象必须包含 `taskType`、`status` 和 `result`，不得使用 `answer`、`content`、`data`、`output`、`outputs`、`body` 或额外的 `result` 包装层。

#### Scenario: 任务流直接返回最终结果
- **WHEN** 已发布任务流完成分析
- **THEN** 最终节点 SHALL 直接返回包含 `taskType`、`status` 和 `result` 的 JSON 对象
- **AND** 后端 SHALL 按该唯一信封校验并映射结果

#### Scenario: SDK 将最终结果拆分为输出变量
- **WHEN** Agent SDK 的 `END_OUTPUT` 将 `taskType`、`status` 和 `result` 作为三个独立输出变量返回
- **THEN** 后端 SHALL 按这三个变量组成同一唯一信封
- **AND** 不得把其中单个变量误判为完整分析结果

### Requirement: 升学分析直接调用已发布任务流
系统 SHALL 使用配置项 `cc001.agent.platform.study.companion.taskFlowCode` 直接调用升学陪伴任务流，不得先调用智能体再回退到任务流。

#### Scenario: 页面提交升学分析
- **WHEN** 用户提交任一升学陪伴分析表单
- **THEN** 后端 SHALL 将当前用户专属表回读的 `payload` 直接发送给已配置任务流
- **AND** 仅解析该任务流的最终 JSON 结果

