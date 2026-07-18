# 职业计划摘要规格

## Purpose
定义 CyanCruise 如何维护长期求职计划记录、输出计划摘要、评估计划健康度，并将本周重点供给 CareerLoop 今日行动。
## Requirements
### Requirement: 输出职业计划摘要
系统 SHALL 将职业计划记录转换为职业计划摘要。摘要 SHALL 包含是否有计划、目标岗位、计划健康度、调整原因、下一阶段里程碑、本周重点、生成时间、更新时间和版本。

#### Scenario: 有计划摘要
- **WHEN** 用户存在包含里程碑和本周重点的职业计划
- **THEN** 摘要标记 `hasPlan` 为 true，并返回第一条里程碑作为下一阶段信息

#### Scenario: 无计划摘要
- **WHEN** 用户没有职业计划
- **THEN** 摘要标记 `hasPlan` 为 false，计划健康度为 `MISSING`，并返回空本周重点

#### Scenario: 解析本周重点
- **WHEN** 职业计划包含多个本周重点
- **THEN** 摘要返回非空、去除空白项后的本周重点列表

### Requirement: 评估计划健康度
系统 SHALL 按更新时间、本周重点和版本评估计划健康度。缺少更新时间、超过 14 天未更新、缺少本周重点或版本低于当前基线时，计划健康度 SHALL 为 `NEEDS_REFRESH`；否则 SHALL 为 `ON_TRACK`。

#### Scenario: 计划超过刷新窗口
- **WHEN** 职业计划最后更新时间早于当前时间 14 天以上
- **THEN** 摘要计划健康度为 `NEEDS_REFRESH`，并说明计划需要重新对齐最新状态

#### Scenario: 本周重点缺失
- **WHEN** 职业计划存在但本周重点为空
- **THEN** 摘要计划健康度为 `NEEDS_REFRESH`，并说明今日任务无法与长期路径对齐

#### Scenario: 计划版本过旧
- **WHEN** 职业计划版本低于当前计划摘要基线
- **THEN** 摘要计划健康度为 `NEEDS_REFRESH`，并说明计划版本过旧

#### Scenario: 计划可用
- **WHEN** 职业计划更新时间新、本周重点非空且版本满足基线
- **THEN** 摘要计划健康度为 `ON_TRACK`

### Requirement: 确保用户有默认职业计划
系统 SHALL 提供确保计划的应用边界：当用户已有计划时返回现有摘要；当用户没有计划时，系统 SHALL 基于用户画像中的目标岗位或兜底目标岗位生成确定性的默认计划并保存。

#### Scenario: 已有计划时确保计划
- **WHEN** 用户已有职业计划且调用确保计划
- **THEN** 系统返回该计划摘要，不覆盖已有里程碑或本周重点

#### Scenario: 缺少计划时生成默认计划
- **WHEN** 用户没有职业计划且调用确保计划
- **THEN** 系统保存一份包含阶段里程碑和本周重点的默认计划，并返回摘要

#### Scenario: 使用画像目标岗位
- **WHEN** 用户画像包含目标岗位且需要生成默认计划
- **THEN** 默认计划的目标岗位使用画像解析出的目标岗位

#### Scenario: 目标岗位缺失
- **WHEN** 用户画像没有目标岗位且需要生成默认计划
- **THEN** 默认计划使用明确的兜底目标岗位，而不是返回空目标岗位

### Requirement: 暴露职业计划 Cosmic WebAPI
系统 SHALL 通过 Cosmic WebAPI 暴露职业计划摘要能力。WebAPI SHALL 支持按用户 ID 获取当前计划摘要、确保计划，以及保存或更新职业计划记录。

#### Scenario: WebAPI 获取计划摘要
- **WHEN** 调用方按用户 ID 请求当前职业计划摘要
- **THEN** WebAPI 返回该用户的计划摘要

#### Scenario: WebAPI 确保计划
- **WHEN** 调用方按用户 ID 请求确保计划
- **THEN** WebAPI 返回已有计划摘要或新生成的默认计划摘要

#### Scenario: WebAPI 保存计划
- **WHEN** 调用方提交用户 ID 和计划内容
- **THEN** WebAPI 保存或更新该用户计划，并返回更新后的计划摘要

### Requirement: 保持可替换的计划存储边界
系统 SHALL 通过职业计划存储边界读取和保存计划。默认存储实现 SHALL 可在没有 Cosmic datamodel 的情况下通过测试；未来 Cosmic datamodel 适配 SHALL 能替换该边界，而无需修改摘要 helper 或 WebAPI 契约。

#### Scenario: 默认存储可读回
- **WHEN** 默认存储保存职业计划后创建新的应用服务实例
- **THEN** 新实例可以按用户 ID 读回该计划

#### Scenario: 替换为 Cosmic 存储
- **WHEN** Cosmic datamodel 职业计划适配器实现完成
- **THEN** 它可以通过同一存储边界替换默认存储

### Requirement: PostgreSQL 持久化职业计划记录
CyanCruise 职业计划记录 SHALL 在运行时通过 PostgreSQL 持久化，而不是默认写入 `filestorage/career-plan`。

#### Scenario: 保存后跨实例读取
- **WHEN** 用户保存或确保一份职业计划，并创建新的应用服务实例
- **THEN** 新实例 SHALL 从 PostgreSQL 读取该用户当前职业计划和摘要

#### Scenario: 无计划时生成默认计划
- **WHEN** 用户没有职业计划且调用确保计划
- **THEN** 系统 SHALL 将生成的默认计划保存到 PostgreSQL，并返回摘要

#### Scenario: 按用户隔离计划
- **WHEN** 用户 A 和用户 B 都存在职业计划
- **THEN** 读取用户 A 的计划 SHALL NOT 返回用户 B 的计划

### Requirement: 维护一人每路线一份当前计划记录
系统 SHALL 为每个用户分别维护最多一份当前就业计划和一份当前升学计划。就业计划 SHALL 包含目标岗位，升学计划 SHALL 包含具体升学方向和目标院校；两类计划均 SHALL 包含用户 ID、起点状态、阶段里程碑、本周重点、生成来源、生成时间、最后更新时间和版本号。

#### Scenario: 保存新的就业计划
- **WHEN** 用户还没有就业计划且系统保存就业计划记录
- **THEN** 系统创建该用户的就业计划，并将版本初始化为有效版本

#### Scenario: 保存新的升学计划
- **WHEN** 用户已有就业计划但还没有升学计划且系统保存升学计划
- **THEN** 系统创建独立升学计划
- **AND** 已有就业计划 SHALL 保持不变

#### Scenario: 更新指定路线计划
- **WHEN** 用户已有指定路线计划且系统保存该路线的新计划内容
- **THEN** 系统更新同一路线现有计划并递增版本号
- **AND** 另一条路线计划 SHALL 保持不变

#### Scenario: 按路线读取当前计划
- **WHEN** 调用方按用户 ID 和路线查询计划
- **THEN** 系统返回该路线当前计划，若不存在则返回明确空结果

### Requirement: 计划摘要声明路线上下文
就业与升学计划摘要 SHALL 返回路线类型；升学摘要还 SHALL 返回具体升学方向和目标院校，以便调用方明确当前数据语义。

#### Scenario: 读取升学摘要
- **WHEN** 用户读取已有升学规划摘要
- **THEN** 摘要 SHALL 标记路线类型为升学
- **AND** 返回已保存的考研、保研或留学方向

