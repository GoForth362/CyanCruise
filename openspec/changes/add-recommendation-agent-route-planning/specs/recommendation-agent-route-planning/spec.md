## ADDED Requirements

### Requirement: 保研路线 SHALL 通过独立配置的智能服务生成
系统 SHALL 在用户选择保研并显式生成规划时调用保研独立配置的智能服务，并 SHALL NOT 使用考研智能体、规则模板或示例数据替代。

#### Scenario: 保研任务流已配置
- **WHEN** 用户保存保研方向并点击生成保研规划
- **THEN** 系统 SHALL 使用 `cc001.agent.platform.study.recommendation.taskFlowCode` 直接调用保研任务流
- **AND** `taskFlowCode` 与 `agentNumber` 同时存在时 SHALL 优先使用 `taskFlowCode`
- **AND** 调用及结果解析方式 SHALL 与考研规划任务流保持一致
- **AND** 输入 SHALL 包含当前用户画像、目标院校、已有保研进度和保研规划资料

#### Scenario: 仅配置保研智能体编号
- **WHEN** 未配置保研 `taskFlowCode` 但配置了 `agentNumber`
- **THEN** 系统 MAY 直接调用对应保研智能体
- **AND** 返回结果仍 SHALL 通过共享规划结构校验

#### Scenario: 保研智能服务未配置
- **WHEN** 用户生成保研规划但保研智能体尚未配置
- **THEN** 系统 SHALL 返回可恢复的中文错误
- **AND** 系统 SHALL NOT 保存兜底阶段、示例规划或虚假今日行动

### Requirement: 保研智能体结果 SHALL 通过真实结构校验
系统 SHALL 只保存能够解析为共享规划契约且包含目标、起点摘要、阶段、本周计划、每日建议和每周重点的保研智能体结果。

#### Scenario: 返回有效保研规划
- **WHEN** 保研智能体返回结构完整的 JSON 规划
- **THEN** 系统 SHALL 标记规划来源为 `AGENT`、状态为 `AGENT_GENERATED` 并按保研方向持久化

#### Scenario: 返回无效结果
- **WHEN** 保研智能体超时、返回非规划 JSON 或缺少必要结构
- **THEN** 系统 SHALL 拒绝保存结果并保留原有保研规划
- **AND** 考研规划 SHALL 保持不变

### Requirement: 保研规划 SHALL 使用方向专属资料
系统 SHALL 只把当前用户上传且成功读取正文的保研资料加入保研智能体输入，并 SHALL NOT 读取考研或其他用户资料。

#### Scenario: 用户同时上传考研和保研资料
- **WHEN** 用户生成保研规划
- **THEN** 智能体输入 SHALL 只包含该用户 `RECOMMENDATION` 方向的可用资料
- **AND** 资料 SHALL NOT 写入公共知识库

### Requirement: 保研规划更新 SHALL 保护已有进度
用户更新保研规划时，系统 SHALL 保留已开始和已完成阶段及其任务状态，只更新未开始阶段。

#### Scenario: 更新部分进行中的保研规划
- **WHEN** 用户已有进行中阶段并重新生成保研规划
- **THEN** 已开始和已完成阶段 SHALL 保留
- **AND** 未开始阶段 MAY 使用新的智能体结果替换
