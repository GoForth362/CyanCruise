## Context

CyanCruise 已经迁移 CareerLoop 主循环的业务规则和 WebAPI 边界，当前 `datamodel/` 仍为空，业务应用模块内存在 `File*Storage`、`InMemory*Storage` 等过渡实现。这些实现帮助前期验证业务语义，但无法支撑 Cosmic 页面、后台治理、跨能力查询、真实 AI 上下文检索和后续运营报表。

IPD 源项目使用 Spring Boot、JPA entity、repository 和 Flyway SQL 管理数据结构。CyanCruise 是 Kingdee Cosmic 二开工程，必须兼容 JDK 1.8，不能直接搬迁 JPA/Flyway，也不能让 base-common/base-helper 依赖 Cosmic 平台 API。本设计只抽取 IPD 的数据语义、流程状态和接口契约，形成正式 Cosmic datamodel 适配路线。

## Goals / Non-Goals

**Goals:**

- 定义 CareerLoop 主循环的 Cosmic datamodel 对象分组、字段语义、主子关系和查询键。
- 约束正式适配 SHALL 只替换应用层 storage 实现，保持 DTO、helper 和现有 WebAPI 契约稳定。
- 规划从文件型持久化到 Cosmic datamodel 的分阶段替换顺序，降低一次性切换风险。
- 明确 IPD 来源路径、CyanCruise 目标模块、数据映射、暂不迁移项和验证方式。
- 为后续 apply 阶段提供可执行任务拆分，但当前仅生成提案文档。

**Non-Goals:**

- 不在本 propose 阶段创建 datamodel 元数据、Java adapter、测试或构建脚本变更。
- 不直接迁移 Spring Boot、JPA、Flyway、Lombok、repository、Vue 或 uni-app 实现。
- 不新增外部依赖，不引入真实 AI SDK，不处理生产数据补偿或历史文件导入。
- 不改变画像、测评、简历、今日行动、职业计划、模拟面试、简历诊断和助手聊天的既有业务 SHALL。
- 不承诺最终 KDDT 表单布局；表单展示可在 webapp 或管理后台 change 中继续细化。

## Decisions

### 1. 建立横向 `cosmic-datamodel-adapters` 能力，不拆散到每个既有业务规格

正式 datamodel 适配是跨模块基础设施，涉及画像、测评、简历、任务、计划、面试和助手聊天。规格层先新增一个横向能力，描述共享对象命名、字段类别、边界隔离和替换顺序。

原因：现有业务规格已经锁定用户可见语义。本 change 主要改变持久化底座，如果逐一修改八个业务规格，会放大审阅成本，也容易把“业务语义变化”和“底层适配变化”混在一起。

替代方案是为每个业务能力增加 MODIFIED requirement；该方案只有在需要改变业务行为时才合适，本次不采用。

### 2. datamodel 对象按主循环聚合分组，保留必要 JSON 文本字段

apply 阶段 SHALL 优先规划以下对象分组：

- 画像：用户画像快照、用户事实、聚合画像。
- 测评：量表、题目、选项、答题记录、答案明细。
- 简历：简历记录、简历关键词/诊断摘要。
- 今日行动：行动任务与父子任务关系。
- 职业计划：计划主记录、里程碑、周重点；若 Cosmic 子表成本过高，可先保留结构化 JSON 字段。
- 模拟面试：面试会话、面试消息、报告摘要。
- 助手聊天：助手会话、助手消息。

原因：这些分组对应 IPD 的 JPA entity 语义，也对应 CyanCruise 已迁移 storage 边界。JSON 文本字段用于保留 AI 输出、画像证据、评分维度等半结构化内容，避免为了 AI 结果形态过早固化大量细碎字段。

替代方案是为每个 JSON 字段都拆成完整子对象；这会提升查询能力，但会显著扩大 KDDT 和适配成本，应在真实运营查询需求明确后再拆。

### 3. Cosmic 平台 API 只允许出现在 app01 的 adapter 层

`DynamicObject`、`QFilter`、`BusinessDataServiceHelper`、`SaveServiceHelper` 等 Cosmic API SHALL 只出现在 `code/cloud01/v620-cc001-cloud01-app01/` 的 datamodel adapter 实现中。`base-common` 继续只承载 DTO/常量；`base-helper` 继续只承载纯 Java 业务规则。

原因：前期迁移已经形成“DTO + helper + application service + storage boundary”的稳定结构。保持边界干净可以避免 Cosmic 平台依赖污染可测试业务规则，也方便继续在 JDK 8 单元测试里验证 helper。

替代方案是在 DTO 或 helper 中直接使用 Cosmic 对象；该方案会让测试和后续重构成本升高，不采用。

### 4. 替换顺序按数据依赖推进

apply 阶段建议先落画像和简历，再落测评记录、任务/计划、面试和助手聊天：

1. 画像是今日行动、职业计划、助手上下文的输入中心。
2. 简历是诊断、面试、今日行动的重要引用。
3. 测评记录会回写画像，但初期也可通过画像 block 消费。
4. 任务/计划依赖画像、简历和测评摘要。
5. 面试和助手聊天消息量更高，适合在对象命名、分页和排序策略稳定后接入。

原因：这样能先稳定跨能力引用和用户归属，再处理高频历史数据。

替代方案是从消息类对象开始，因为其结构简单；但聊天/面试消息量和分页排序风险更高，先做基础画像对象更稳。

### 5. 文件型存储在验证通过前保留为回退适配

正式 Cosmic adapter SHALL 与现有 storage interface 并存，直到相关模块测试和构建通过。切换接线应集中在应用服务默认构造或配置入口，避免调用方感知存储变化。

原因：文件型适配是当前可运行基线。保留回退路径能帮助定位 datamodel 元数据、字段映射或平台 API 问题。

替代方案是一次性删除 `File*Storage`；这会降低回退能力，不适合主循环底座迁移。

## Risks / Trade-offs

- [Risk] Cosmic datamodel 元数据格式或 KDDT 约束不明确 -> Mitigation：apply 前先在 `datamodel/` 建立最小对象和命名约定，优先验证单对象保存/查询。
- [Risk] JSON 文本字段降低平台侧细粒度查询能力 -> Mitigation：只对 AI 输出、证据、评分维度等半结构化内容使用 JSON；用户归属、状态、目标岗位、时间、排序等查询字段 SHALL 结构化。
- [Risk] 跨模块一次性适配范围较大 -> Mitigation：任务按对象分组和 storage adapter 分阶段完成，每个阶段保留聚焦测试。
- [Risk] Cosmic 主键类型与现有 Long/String DTO 字段存在差异 -> Mitigation：adapter 层 SHALL 负责主键转换，公共 DTO 不暴露平台对象。
- [Risk] 文件型数据与正式 datamodel 并存期间可能产生双写歧义 -> Mitigation：本 change 不做双写；每个模块切换时 SHALL 明确唯一默认 storage，并保留文件适配作为测试/回退实现。

## Migration Plan

1. 在 `datamodel/` 新增 CareerLoop 对象命名、字段映射和对象关系定义。
2. 在 app01 新增 Cosmic datamodel adapter 基础工具，集中处理字段读写、主键转换、时间转换、用户归属过滤和分页排序。
3. 按画像、简历、测评、任务/计划、面试、助手聊天顺序替换 storage 实现。
4. 为每个 adapter 增加聚焦测试；不能连接真实 Cosmic 运行时的部分 SHALL 通过 mapper/contract test 验证字段映射。
5. 更新迁移地图和主规格，运行 `openspec validate <change-id> --strict`、`openspec validate --all --strict`、相关 Gradle 测试和 `.\gradlew.bat clean build`。
6. 归档时按顺序编号，提交并推送当前迁移分支。

Rollback：若某个模块 datamodel adapter 验证失败，默认接线 SHALL 回退到原文件型 storage；已创建的 datamodel 元数据和 adapter 代码保留但不作为默认路径。

## Open Questions

- Cosmic datamodel 元数据应使用当前仓库已有约定还是 KDDT 导出的标准文件格式，需要在 apply 前确认实际模板。
- 高容量消息对象是否需要独立归档/分页策略，还是先使用普通业务对象分页查询即可。
- 职业计划里程碑和周重点是否第一阶段拆子对象，还是先保留 JSON 文本并等待 webapp 展示需求稳定。
