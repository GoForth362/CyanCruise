## 1. Datamodel Mapping

- [x] 1.1 梳理 IPD 主循环 entity/Flyway 字段来源，形成画像、测评、简历、今日行动、职业计划、模拟面试、简历诊断摘要和助手聊天的数据映射表。
- [x] 1.2 在 `datamodel/` 新增 CareerLoop Cosmic 对象命名和字段定义，覆盖用户归属、业务主键、状态、目标岗位、时间、排序、父子关系和 JSON 文本字段。
- [x] 1.3 明确每个对象的 CyanCruise 目标 storage boundary，并标注暂不迁移的 JPA/Flyway/repository/Vue/uni-app/真实 AI SDK 内容。
- [x] 1.4 为高频列表对象定义排序和分页字段，包括简历列表、今日任务、面试历史、助手会话和消息记录。

## 2. Adapter Infrastructure

- [x] 2.1 在 app01 模块新增 Cosmic datamodel adapter 基础工具，集中处理字段读写、主键转换、时间转换和空值默认值。
- [x] 2.2 新增用户归属过滤与校验工具，供简历、面试、助手会话、任务和计划 adapter 复用。
- [x] 2.3 确保 `base-common` 和 `base-helper` 不引入 Cosmic 平台 API 或 datamodel 运行时对象。
- [x] 2.4 保留文件型/内存型 storage 作为测试和回退实现，新增默认接线时保持可替换。

## 3. Core Storage Adapters

- [x] 3.1 实现画像 Cosmic storage adapter，覆盖 snapshot、facts 和聚合 profile 的保存与读取。
- [x] 3.2 实现简历 Cosmic storage adapter，覆盖创建、读取、按用户列表、更新诊断分数和删除。
- [x] 3.3 为测评结果增加正式存储边界或 adapter，覆盖量表引用、提交记录、答案快照和画像回写所需字段。
- [x] 3.4 实现今日行动/职业计划 datamodel adapter，覆盖任务唯一键、日期、状态、计划摘要、里程碑和周重点字段。
- [x] 3.5 实现模拟面试 datamodel adapter，覆盖会话、消息、报告摘要、状态和所有权校验。
- [x] 3.6 实现助手聊天 datamodel adapter，覆盖会话、消息、persona、token/cost 字段、排序和级联删除语义。

## 4. Tests

- [x] 4.1 增加 datamodel 字段映射测试或 contract test，验证 DTO 与 Cosmic 字段之间的读写映射。
- [x] 4.2 增加 storage adapter 聚焦测试，覆盖保存、读取、列表排序、更新、删除和跨用户拒绝。
- [x] 4.3 增加回退接线测试，确认未启用 Cosmic adapter 时现有文件型/内存型 storage 仍可用于验证。
- [x] 4.4 在 JDK 8 环境下运行相关模块测试：`.\gradlew.bat :v620-cc001-base-helper:test :v620-cc001-cloud01-app01:test`。

## 5. Documentation

- [x] 5.1 更新 `docs/ipd-to-cyancruise-migration-map.md`，记录 Cosmic datamodel 正式适配状态、IPD 来源路径、目标模块和后续项。
- [x] 5.2 将 delta spec 同步到 `openspec/specs/cosmic-datamodel-adapters/`。
- [x] 5.3 在 change 文档中保留暂不迁移项：Spring Boot、JPA、Flyway、Vue、uni-app、真实 AI SDK、生产数据补偿脚本和页面实现。

## 6. Validation And Delivery

- [x] 6.1 运行 `openspec validate migrate-cosmic-datamodel-adapters --strict`。
- [x] 6.2 运行 `openspec validate --all --strict`。
- [x] 6.3 设置 JDK 8 后运行 `.\gradlew.bat clean build`。
- [x] 6.4 verify 通过后归档 change，按下一个顺序编号命名归档目录。
- [x] 6.5 本地 commit 并推送当前分支 `codex/migrate-cosmic-datamodel-adapters`。
