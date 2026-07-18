## ADDED Requirements

### Requirement: 留学路线 SHALL 由独立配置的智能服务生成
系统 SHALL 在用户选择留学并显式生成规划时调用 `cc001.agent.platform.study.abroad.*` 独立配置，且 SHALL NOT 使用考研、保研智能服务、规则模板或示例数据替代。

#### Scenario: 留学任务流已配置
- **WHEN** 用户保存留学方向并点击生成留学规划
- **THEN** 系统 SHALL 使用留学 `taskFlowCode` 直接调用任务流
- **AND** `taskFlowCode` 与 `agentNumber` 同时存在时 SHALL 优先使用 `taskFlowCode`
- **AND** 输入 SHALL 包含当前用户画像、留学目标、既有留学进度和该用户留学规划资料

#### Scenario: 留学智能服务未配置
- **WHEN** 用户生成留学规划但没有可用的留学任务流或智能体配置
- **THEN** 系统 SHALL 返回可恢复的中文错误
- **AND** 系统 SHALL NOT 保存默认阶段、示例规划或虚假今日行动

### Requirement: 留学规划 SHALL 通过真实结构与方向校验
系统 SHALL 只保存能够解析为共享规划契约、使用留学语义且覆盖连续十二个月的智能结果。

#### Scenario: 返回有效留学规划
- **WHEN** 留学智能服务返回包含目标、起点摘要、至少三个阶段、本周计划、每日建议和每周重点的完整 JSON
- **THEN** 系统 SHALL 标记规划来源为 `AGENT`、状态为 `AGENT_GENERATED`
- **AND** 系统 SHALL 按 `STUDY_ABROAD` 方向持久化规划

#### Scenario: 返回无效留学结果
- **WHEN** 智能服务超时、返回非规划 JSON、缺少必要结构或仅返回单月默认路线
- **THEN** 系统 SHALL 拒绝保存结果并保留原有有效留学规划
- **AND** 用户的考研和保研规划 SHALL 保持不变

### Requirement: 留学规划 SHALL 使用方向专属资料
系统 SHALL 允许用户上传留学规划资料，并且只把当前用户 `STUDY_ABROAD` 方向下正文读取成功的资料加入留学智能服务输入。

#### Scenario: 用户同时拥有多个方向资料
- **WHEN** 用户生成留学规划
- **THEN** 智能服务输入 SHALL 只包含该用户留学方向的可用资料
- **AND** 系统 SHALL NOT 读取其他用户、考研或保研资料
- **AND** 用户资料 SHALL NOT 写入公共知识库

### Requirement: 留学规划和今日行动 SHALL 独立持久化
系统 SHALL 按用户与 `STUDY_ABROAD` 方向保存留学路线、阶段进度和今日任务，并 SHALL 与同一用户的考研、保研数据隔离。

#### Scenario: 用户切换升学方向
- **WHEN** 用户从留学切换到考研或保研后再切回留学
- **THEN** 系统 SHALL 恢复该用户此前保存的留学规划、阶段进度和今日任务
- **AND** 方向切换 SHALL NOT 覆盖其他方向的数据

### Requirement: 留学页面 SHALL 只展示真实规划状态
升学中心、完整路径规划和今日行动 SHALL 根据当前留学方向展示真实持久化数据和留学专属文案。

#### Scenario: 尚未生成留学规划
- **WHEN** 当前用户没有通过智能服务校验的留学规划
- **THEN** 页面 SHALL 展示“尚未生成真实留学规划”的空状态和“生成留学规划”入口
- **AND** 页面 SHALL NOT 展示默认阶段、示例路线或虚假今日任务

#### Scenario: 已生成留学规划
- **WHEN** 当前用户存在有效留学规划
- **THEN** 完整路径页面 SHALL 展示留学阶段、本周计划和每日建议
- **AND** 今日行动 SHALL 从该留学规划的持久化任务中读取和更新
