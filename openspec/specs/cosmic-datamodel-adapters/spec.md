# Cosmic Datamodel Adapters 规格

## Purpose

定义 CareerLoop 主循环在 CyanCruise 中的正式 Cosmic datamodel 对象、字段映射、存储适配边界、替换文件型持久化的分阶段规则和验证要求。
## Requirements
### Requirement: 金蝶业务建模标识风格
金蝶苍穹业务建模 SHALL 使用与平台限制兼容的对象标识和字段标识风格。对象标识 SHALL 使用 `v620_cc_` 前缀；字段标识 SHALL 使用 `v620_` 前缀，且 SHALL NOT 以 `_id` 结尾。涉及 ID 语义的字段 SHALL 使用 `userid`、`runid`、`scaleid`、`resumeid` 等连续写法，并在建模文档中说明对应的后端逻辑字段。

#### Scenario: 建立包含用户 ID 的业务对象字段
- **WHEN** 在金蝶苍穹设计器中为业务对象新增“用户 ID”字段
- **THEN** 字段标识 SHALL 使用 `v620_userid`
- **AND** 建模文档 SHALL 标注其对应后端逻辑字段为 `user_id`

#### Scenario: 区分金蝶标识和后端逻辑字段
- **WHEN** 后端 DTO、SQL、PostgreSQL 或 storage adapter 使用 `user_id`、`resume_id` 等 snake_case 字段
- **THEN** 金蝶业务建模文档 SHALL NOT 直接把这些字段作为苍穹字段标识
- **AND** 金蝶字段标识 SHALL 使用 `v620_userid`、`v620_resumeid` 等平台兼容写法

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

### Requirement: 支持 PostgreSQL 作为当前租户选定存储
对于当前租户，正式 CyanCruise 画像存储替换 SHALL 支持 PostgreSQL 作为选定数据库后端，而不是要求画像草稿、画像快照、画像 facts 和派生画像必须依赖 Cosmic datamodel 持久化。

#### Scenario: PostgreSQL 替换文件画像存储
- **WHEN** PostgreSQL 画像存储通过验证并被显式启用
- **THEN** CyanCruise 画像应用服务 SHALL 通过存储边界使用 PostgreSQL，而不是文件存储

#### Scenario: Cosmic datamodel 不阻塞画像存储
- **WHEN** 画像数据的 Cosmic datamodel 对象不可用，但 PostgreSQL 画像存储已配置
- **THEN** 画像草稿、画像快照、画像 facts 和派生画像持久化 SHALL NOT 因缺少 Cosmic datamodel 对象而被阻塞

#### Scenario: 其他模块保持不变
- **WHEN** PostgreSQL 画像存储被实现
- **THEN** 简历、测评、今日行动、职业计划、模拟面试、AI 助手、通知和管理后台存储 SHALL 继续使用当前已有存储适配器，直到后续单独批准的 change 替换它们

### Requirement: 记录 PostgreSQL 存储替换
存储替换文档 SHALL 记录 PostgreSQL 配置、表映射、回滚行为和验证结果，但不得保存数据库凭据。

#### Scenario: 更新迁移地图
- **WHEN** PostgreSQL 画像存储实现完成
- **THEN** 迁移地图或实现说明 SHALL 记录来源语义、目标存储适配器、表名、配置项名称、验证命令和回滚步骤

#### Scenario: 排除密钥
- **WHEN** 审查 PostgreSQL 存储文档
- **THEN** 文档 SHALL 只包含配置项名称和占位值，不包含真实数据库密码或私有连接凭据

### Requirement: PostgreSQL 替换当前业务状态文件存储
对于当前 CyanCruise 租户，PostgreSQL SHALL 作为已批准的业务状态数据库后端，替换本地 `filestorage` 业务状态适配器；未来 Cosmic datamodel adapter 仍可通过相同业务边界替换 PostgreSQL。

#### Scenario: PostgreSQL 不等待 Cosmic datamodel
- **WHEN** Cosmic datamodel 对象尚未建立，但 PostgreSQL 表结构和配置已就绪
- **THEN** 用户画像、职业计划、简历、简历诊断、模拟面试和助手聊天 SHALL 可通过 PostgreSQL 正常持久化

#### Scenario: 文件服务边界保持独立
- **WHEN** 页面需要上传、预览、下载或删除二进制文件
- **THEN** 该能力 SHALL 继续通过文件服务适配边界处理，不被误认为业务状态 `filestorage` 替换的一部分
