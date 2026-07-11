## 当前实施状态

2026-07-11 决定：CyanCruise 在功能完善阶段统一以 PostgreSQL 作为唯一主存储。该 change 已完成的映射、gateway 与 adapter 代码作为后续迁移准备保留；真实苍穹业务对象试水、模块切换和手工验收延后，不作为当前应用功能完成的前置条件。当前禁止 PostgreSQL 与 Cosmic storage 双写。

## Context

CyanCruise 当前已经具备三层基础：

- 前端通过金蝶菜单外部链接进入 `webapp/isv/v620/cyancruise/index.html?ccRoute=...&apiMode=server`，再通过统一 WebAPI 调用后端。
- 后端应用服务已经通过 storage interface 读写业务数据，部分模块已有 PostgreSQL 实现，部分测试和局部流程仍使用内存实现。
- 代码中已经存在 `CosmicDatamodelGateway`、`MappedCosmicDatamodelGateway`、`CyanCruiseBusinessModelMapping` 和多组 `Cosmic*Storage`，但真实金蝶平台业务对象读写 gateway 尚未落地。

这次 change 的目标不是重做前端页面，也不是重新建模，而是把“已经建好的金蝶业务对象”接入为后端运行时存储。前端和 WebAPI 契约应尽量保持不变，让存储切换发生在后端 adapter 层。

## Goals / Non-Goals

**Goals:**

- 实现一个真实金蝶平台业务对象 gateway，用于保存、加载、查询和删除 `v620_cc_*` 记录。
- 让 storage factory 支持 `cosmic` 后端，并可按模块逐步切换。
- 复核逻辑字段到平台字段的映射，确保 DTO/storage 字段能稳定落到 `v620_xxx` 字段。
- 对用户归属、记录 ID、状态、时间、分页和排序建立统一约束。
- 保留 PostgreSQL/内存实现作为未切换模块、测试和回退路径。
- 通过 fake gateway 单元测试覆盖字段映射和 storage 行为，通过真实苍穹环境手工验证关键读写链路。

**Non-Goals:**

- 不改造金蝶平台菜单和前端路由结构。
- 不让前端直接读写金蝶业务对象。
- 不把真实数据库密码、access token、clientSecret 或租户私密配置写入仓库。
- 不迁移 IPD 的 Spring Boot、JPA、Flyway、Vue、uni-app 或旧登录实现。
- 不在本次实现中强制一次性切换所有模块；未验证模块可以继续使用当前存储。

## Decisions

### Decision 1: 通过 gateway 接入金蝶业务对象，而不是在每个 storage 中直接调用平台 API

`CosmicDatamodelGateway` 继续作为统一边界，新增真实实现，例如 `CosmicBusinessObjectDatamodelGateway`。各 `Cosmic*Storage` 只处理领域 DTO 和逻辑字段，不直接依赖平台 API 细节。

备选方案是在每个 storage adapter 中直接调用 `BusinessDataServiceHelper`、`SaveServiceHelper` 等平台 API。该方案短期写得快，但字段映射、错误处理、查询条件和测试替身会分散到多个类里，后续维护成本高。

### Decision 2: 字段映射集中在 `CyanCruiseBusinessModelMapping`

逻辑字段继续使用 `user_id`、`resume_id`、`result_json` 等后端语义字段，平台字段统一映射为 `v620_userid`、`v620_resumeid`、`v620_resultjson` 等。这样 DTO 和业务服务不需要跟随金蝶字段命名变化。

如果发现已建平台对象字段与映射不一致，先更新建模文档和 mapping，再实现存储接入；不得在业务逻辑里写零散字段别名。

### Decision 3: 存储切换由配置控制，并允许模块级灰度

全局配置建议支持：

- `cc001.storage.backend=cosmic`
- `cc001.storage.cosmic.enabled=true`
- `cc001.storage.cosmic.modules=profile,resume,assessment,...`

当全局 backend 为 `cosmic` 但某模块未列入或未通过验证时，该模块可以继续使用 PostgreSQL 或内存后备。这样首页和登录功能不会因为某个业务对象接入失败而整体不可用。

### Decision 4: 删除语义优先采用状态字段归档

对通知、简历、任务、内容等用户可见记录，删除应优先写状态字段，例如 `archived`、`deleted`、`hidden`，避免物理删除导致审计、恢复和问题追踪困难。仅对明确的临时记录或测试替身允许物理删除。

### Decision 5: 真实平台 API 通过反射或窄边界封装，保持 JDK 8 和 Cosmic 模板兼容

如果金蝶 runtime 类在本地测试或构建环境不可直接稳定引用，gateway 可以采用窄边界反射或 adapter 封装，避免 base-common/base-helper 引入平台 API。实现必须继续使用仓库内 `gradlew.bat` 并兼容 JDK 1.8。

## Risks / Trade-offs

- [Risk] 平台对象字段与文档或 mapping 不一致 -> Mitigation: 先增加映射校验测试和人工核对清单，缺口进入任务，不在业务逻辑中临时绕过。
- [Risk] 金蝶数据服务 API 在本地单元测试不可用 -> Mitigation: 使用 fake gateway 做自动化测试，真实环境读写作为手工验收项记录。
- [Risk] 一次性切换全部模块影响首页和登录 -> Mitigation: 使用模块级开关，先切用户画像和少量可验证模块，再逐步扩大。
- [Risk] PostgreSQL 与金蝶业务对象出现双写不一致 -> Mitigation: 本 change 默认不做长期双写；切换模块只有一个主数据源，回退时明确从哪一端读取。
- [Risk] 平台查询能力限制导致分页或排序语义不一致 -> Mitigation: gateway 层统一处理可下推查询，必要时在受控数量内内存排序，并记录限制。

## Migration Plan

1. 审核本 change 文档，确认目标对象、切换范围和验收路径。
2. 实现真实金蝶业务对象 gateway，并保留 fake/in-memory gateway 测试。
3. 复核 `CyanCruiseBusinessModelMapping` 中对象和字段映射，补齐通知、管理端和深造相关字段。
4. 先接入用户画像、简历、测评、面试、通知这些能通过页面快速验证的模块。
5. 在真实苍穹环境使用当前用户“冯如”完成首页读取、画像保存、简历/测评/面试/消息写入读取验证。
6. 验证通过后，将对应模块配置为 `cosmic` 主存储；未通过模块保留当前存储。
7. 如发生阻塞，关闭 `cc001.storage.cosmic.enabled` 或移除模块开关，回退到 PostgreSQL/内存后备。

## Open Questions

- 当前苍穹环境中所有业务对象是否已经发布并可被后端数据服务读写？
- `v620_cc_notice` 是否同时承担管理端通知公告和用户端消息中心记录，还是需要区分公告模板与用户消息投递记录？
- 管理端 5 个对象是否都已经创建完成，还是本次先只接入用户端 14 个对象？
- 金蝶平台数据服务在当前版本中推荐使用的查询、保存、删除 API 类名和调用方式是否已在本地工程依赖中可用？
