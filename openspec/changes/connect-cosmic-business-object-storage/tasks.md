## 1. 映射和建模核对

- [x] 1.1 核对 `datamodel/cyancruise-business-modeling.md` 中已建业务对象与金蝶平台实际对象编码、表名、字段标识一致。
  - 2026-07-12 人工核对确认：已建业务对象的对象编码、表名和字段标识与金蝶平台实际配置一致。
- [x] 1.2 补齐 `CyanCruiseBusinessModelMapping` 中通知、管理端、深造、Agent 相关对象和字段映射缺口。
- [x] 1.3 增加对象/字段映射单元测试，覆盖逻辑对象到 `v620_cc_*`、逻辑字段到 `v620_xxx` 的转换。
- [x] 1.4 记录暂不切换到 Cosmic 存储的模块和原因，避免误认为所有对象已接入运行时。

## 2. 真实 Cosmic gateway

- [x] 2.1 新增真实 `CosmicDatamodelGateway` 实现，封装金蝶业务对象保存、加载、列表、单条查询和删除能力。
- [x] 2.2 在 gateway 中统一处理平台 API 异常、对象不存在、字段缺失和身份缺失，返回可诊断但不泄露敏感信息的错误。
- [x] 2.3 保留 `InMemoryCosmicDatamodelGateway` 作为测试替身，禁止生产 Cosmic 模式默认使用该替身。
- [x] 2.4 确认平台 API 依赖只出现在 `code/cloud01/v620-cc001-cloud01-app01`，不进入 base-common/base-helper。

## 3. 存储工厂和配置切换

- [x] 3.1 扩展存储配置，支持 `cc001.storage.backend=cosmic` 和模块级启用清单。
- [x] 3.2 调整 `CyanCruiseStorageFactory`，为启用模块构造 Cosmic-backed storage，为未启用模块保留当前存储。
- [x] 3.3 对显式 Cosmic-only 的模块增加 fail-fast 或 storage-unavailable 行为，避免静默写入错误后端。
- [x] 3.4 编写配置选择测试，覆盖 PostgreSQL、Cosmic、未启用模块和 gateway 不可用场景。

## 4. 分模块接入

- [ ] 4.1 接入用户画像 `v620_cc_user_profile`，验证首页读取和画像保存不破坏当前登录身份链路。
- [ ] 4.2 接入简历档案和简历诊断 `v620_cc_resume_record`、`v620_cc_resume_diag`，验证保存、列表、诊断记录读取。
- [ ] 4.3 接入职业测评记录 `v620_cc_assess_record`，验证测评提交、结果读取和画像同步。
- [ ] 4.4 接入模拟面试 `v620_cc_interview`，验证面试会话、报告、历史读取。
- [ ] 4.5 接入今日任务和职业计划 `v620_cc_career_task`、`v620_cc_career_plan`，验证首页行动和路径规划读取。
- [ ] 4.6 接入深造陪伴对象 `v620_cc_study_target`、`v620_cc_study_record`、`v620_cc_study_material`、`v620_cc_study_event`。
- [ ] 4.7 接入通知公告 `v620_cc_notice`，验证管理端公告和用户端消息中心复用同一对象。
- [ ] 4.8 评估管理端对象 `v620_cc_user_account`、`v620_cc_question`、`v620_cc_content`、`v620_cc_admin_audit` 是否已建并可纳入本轮或后续 change。

## 5. 验证和文档

- [ ] 5.1 使用 fake gateway 完成 storage adapter 自动化测试，覆盖用户隔离、状态过滤、排序、归档删除和字段映射。
- [ ] 5.2 在真实苍穹环境中使用当前登录用户完成画像、简历、测评、面试、通知的手工读写验收。
- [x] 5.3 更新 `docs/ipd-to-cyancruise-migration-map.md`，记录每个模块的数据源从 PostgreSQL/内存到 Cosmic 业务对象的状态。
- [x] 5.4 更新运行部署文档，说明 `cc001.storage.backend=cosmic`、模块开关、回退到 PostgreSQL 的步骤和注意事项。
- [x] 5.5 运行 `openspec validate connect-cosmic-business-object-storage --strict`。
- [x] 5.6 在 JDK 8 环境运行仓库内 `.\gradlew.bat clean build`，确认构建通过。
