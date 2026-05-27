# Cosmic Datamodel Adapters 规格

## Purpose

定义 CareerLoop 主循环在 CyanCruise 中的正式 Cosmic datamodel 对象、字段映射、存储适配边界、替换文件型持久化的分阶段规则和验证要求。

## Requirements

### Requirement: CareerLoop datamodel object map
系统 SHALL 为 CareerLoop 主循环定义正式 Cosmic datamodel 对象映射，覆盖用户画像、测评、简历、今日行动、职业计划、模拟面试、简历诊断摘要和助手聊天的核心数据语义。

#### Scenario: Map core loop entities
- **WHEN** 开发者查看 datamodel 适配定义
- **THEN** 定义 SHALL 至少包含画像快照/用户事实/聚合画像、测评量表/题目/选项/记录/答案、简历记录/关键词、行动任务、职业计划、面试会话/消息、助手会话/消息对象

#### Scenario: Preserve IPD semantics without copying implementation
- **WHEN** datamodel 字段来自 IPD entity 或 Flyway 语义
- **THEN** 字段说明 SHALL 记录来源语义，并 SHALL NOT 直接迁移 Spring Boot、JPA、Flyway、Lombok、repository、Vue 或 uni-app 实现

### Requirement: Structured ownership and query fields
每个用户归属的数据对象 SHALL 明确保存用户标识，并对状态、目标岗位、时间、排序、父子关系和外部引用等查询字段进行结构化建模。

#### Scenario: Query by user
- **WHEN** storage adapter 按用户读取画像、简历、任务、计划、面试或助手会话
- **THEN** adapter SHALL 使用结构化用户标识过滤结果，不得依赖 JSON 文本解析用户归属

#### Scenario: Query by status and time
- **WHEN** storage adapter 读取今日任务、会话列表、面试历史或简历列表
- **THEN** adapter SHALL 使用结构化状态字段和时间字段支持排序、筛选和分页语义

### Requirement: Semi-structured content containment
系统 MAY 将 AI 输出、画像证据、评分维度、简历解析内容、报告摘要和计划明细等半结构化内容保存为 JSON 文本字段，但用户归属、状态、目标岗位、时间和排序字段 SHALL 保持结构化。

#### Scenario: Save AI generated report
- **WHEN** 模拟面试报告、简历诊断结果或职业计划摘要包含不稳定结构
- **THEN** datamodel SHALL 允许 adapter 以 JSON 文本保存该内容，同时保留结构化的用户标识、业务主键、状态和更新时间

#### Scenario: Avoid hidden query keys
- **WHEN** 某字段需要用于列表筛选、所有权校验或跨能力引用
- **THEN** 该字段 SHALL NOT 只存在于 JSON 文本中

### Requirement: Adapter boundary isolation
正式 Cosmic datamodel 适配 SHALL 通过 app01 应用模块内的 storage adapter 接入；base-common 和 base-helper SHALL NOT 依赖 Cosmic 平台 API 或 datamodel 运行时对象。

#### Scenario: Keep DTOs platform-neutral
- **WHEN** 新增或调整 DTO、常量、helper 规则
- **THEN** 代码 SHALL NOT 引入 `DynamicObject`、`QFilter`、`BusinessDataServiceHelper`、`SaveServiceHelper` 或其他 Cosmic 平台类型

#### Scenario: Replace storage implementation
- **WHEN** 某能力从文件型 storage 切换到 Cosmic datamodel storage
- **THEN** 应用服务 SHALL 继续通过既有 storage/application boundary 访问数据，WebAPI 入参和出参 SHALL 保持兼容

### Requirement: Ownership validation
所有通过 datamodel adapter 读取、更新或删除用户数据的操作 SHALL 执行用户归属校验，防止跨用户访问会话、消息、简历、计划、任务和面试记录。

#### Scenario: Reject cross-user session access
- **WHEN** 用户 A 请求读取或删除用户 B 的助手会话、面试会话或相关消息
- **THEN** adapter 或应用服务 SHALL 拒绝该操作，并 SHALL NOT 返回用户 B 的业务内容

#### Scenario: Preserve ownership in child records
- **WHEN** 子记录通过父记录关联读取，例如测评答案、面试消息或助手消息
- **THEN** 系统 SHALL 通过父记录用户归属或子记录冗余用户标识完成所有权校验

### Requirement: Phased storage replacement
正式适配 SHALL 按可验证阶段替换现有文件型或内存型存储，优先稳定画像和简历，再推进测评、任务/计划、面试和助手聊天。

#### Scenario: Replace one module at a time
- **WHEN** apply 阶段实现某个模块的 Cosmic storage adapter
- **THEN** 该模块 SHALL 拥有独立任务和聚焦验证，不要求其他模块在同一提交中同时切换

#### Scenario: Keep rollback adapter
- **WHEN** Cosmic datamodel adapter 尚未通过相关验证
- **THEN** 文件型或内存型 storage SHALL 可作为测试或回退实现保留，默认接线 SHALL 只在验证通过后切换

### Requirement: Datamodel validation baseline
每个 datamodel 适配阶段 SHALL 通过 OpenSpec 校验、字段映射验证、相关 storage/application 测试和 JDK 8 Gradle 构建验证。

#### Scenario: Validate proposed change
- **WHEN** propose 文档生成完成
- **THEN** `openspec validate migrate-cosmic-datamodel-adapters --strict` SHALL 通过

#### Scenario: Validate implementation phase
- **WHEN** apply 阶段完成一个或多个 adapter
- **THEN** 相关测试、`openspec validate --all --strict` 和仓库内 `.\gradlew.bat clean build` SHALL 在 JDK 8 下通过

### Requirement: Migration documentation
datamodel 适配 SHALL 更新迁移地图，记录 IPD 来源路径、CyanCruise 目标模块、数据映射、暂不迁移项和验证方式。

#### Scenario: Update migration map
- **WHEN** apply 阶段完成 datamodel 对象或 storage adapter
- **THEN** `docs/ipd-to-cyancruise-migration-map.md` SHALL 同步说明对应能力的正式 datamodel 状态和后续项

#### Scenario: Record excluded implementation
- **WHEN** 某项 IPD 技术实现不迁移
- **THEN** 文档 SHALL 明确标注暂不迁移 Spring Boot、JPA、Flyway、Vue、uni-app、真实 AI SDK 或生产数据补偿脚本
