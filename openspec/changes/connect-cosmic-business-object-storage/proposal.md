## Why

CyanCruise 已经在金蝶苍穹中完成核心业务对象建模，前端也已经通过平台菜单和 WebAPI 进入业务页面；但后端运行时仍主要使用 PostgreSQL 或内存存储，尚未把业务数据真正写入金蝶平台业务对象。现在需要建立一个可审核、可分阶段落地的接入方案，让平台业务模型成为正式数据来源，同时保留现有首页、登录和前后端接口稳定性。

## What Changes

- 新增金蝶业务对象存储接入能力：实现运行时 `CosmicDatamodelGateway`，通过金蝶平台数据服务读写 `v620_cc_*` 业务对象。
- 调整存储工厂策略：支持 `cc001.storage.backend=cosmic` 或等价配置，将已验证模块切换到金蝶业务对象存储。
- 补齐业务对象映射：复核 `CyanCruiseBusinessModelMapping` 与 `datamodel/cyancruise-business-modeling.md`，覆盖用户画像、测评、简历、任务、计划、面试、深造、通知和管理端对象。
- 保持前后端契约不变：前端仍调用现有 WebAPI，WebAPI 入参、出参、路由和身份识别链路不因存储切换而改变。
- 建立分阶段迁移和回退：优先接入用户画像、简历、测评、面试、通知等已建且用户能验证的对象；未验证模块继续保留 PostgreSQL 或内存后备。
- 不迁移本地数据库表结构到平台表，不直接搬迁 Spring Boot/JPA/Flyway/Vue/uni-app 实现。

## Capabilities

### New Capabilities

- `cosmic-business-object-storage`: 定义 CyanCruise 使用金蝶苍穹业务对象作为运行时业务状态存储的读写、映射、配置、验证和回退要求。

### Modified Capabilities

- `cosmic-datamodel-adapters`: 明确已有 datamodel adapter 从“逻辑映射/内存模拟”升级为“可接入真实金蝶业务对象”的运行时边界。
- `postgresql-business-storage`: 明确 PostgreSQL 从当前运行时存储调整为可回退/可过渡存储，生产切换到金蝶业务对象后不得继续作为已切换模块的主数据源。
- `cosmic-business-modeling`: 明确建模文档中的已建对象是本次存储接入的目标对象清单，字段缺口必须先记录并处理。

## Impact

- 后端模块：`code/cloud01/v620-cc001-cloud01-app01` 中 datamodel gateway、storage adapter、storage factory、WebAPI 组合入口。
- 通用模块：`code/base/*` 只保留 DTO、常量、helper，不引入金蝶平台运行时 API。
- 数据模型文档：`datamodel/cyancruise-business-modeling.md` 和迁移地图需要记录对象/字段映射、已接入模块和暂未接入模块。
- 配置项：新增或确认 `cc001.storage.backend=cosmic`、业务对象接入开关、只读校验或回退配置；不得硬编码本地路径、租户凭据或数据库凭据。
- 验证：OpenSpec 严格校验、JDK 8 Gradle 构建、storage adapter 单元测试、使用 in-memory fake gateway 的字段映射测试，以及在真实苍穹环境中的手工读写验收。
