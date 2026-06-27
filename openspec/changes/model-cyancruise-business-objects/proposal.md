# 变更提案：建立 CyanCruise 苍穹业务对象建模规划

## 背景

CyanCruise 当前已经可以通过外部前端、WebAPI 和 PostgreSQL 运行核心功能，但金蝶苍穹平台中的业务对象仍处于人工逐步建模阶段。已实际建立的对象包括：

- 用户职业画像：基础资料，`v620_cc_user_profile`，表名 `tk_v620_cc_user_profile`
- Agent执行记录：单据，`v620_cc_agent_run`，表名 `tk_v620_cc_agent_run`

已有建模文档中混入了未实际采用的字段和结构，例如 `v620_billno`、Agent 执行步骤分录、部分驼峰字段命名说明等，容易误导后续手工建模。需要按当前实际建模口径修正文档，并补齐全部待建业务对象规划。

## 目标

- 修正 `datamodel/cyancruise-business-modeling.md`，以已建对象和当前金蝶设计器实际字段标识为准。
- 明确对象编码统一使用不超过 25 个字符的 `v620_cc_xxx` 短编码，例如 `v620_cc_assess_record`、`v620_cc_study_target`。
- 明确字段标识统一使用平台保存后的 `v620_xxx` 形式，例如 `v620_userid`、`v620_recordid`、`v620_runid`。
- 去除当前阶段不需要建立的 `v620_billno` 和 Agent“执行步骤”分录。
- 补齐职业发展、深造陪伴、Agent 运行管理所需全部业务对象清单。
- 记录每个对象的对象类型、对象编码、表名、字段、逻辑字段映射和建立顺序。

## 非目标

- 不在本变更中实现 Java 代码、WebAPI 或 Cosmic datamodel adapter。
- 不迁移 IPD 的 Spring Boot、JPA、Flyway、Vue 或 uni-app 实现。
- 不要求立即替换当前 PostgreSQL 存储。
- 不要求一次性在金蝶设计器中完成所有布局美化。

## 影响范围

- `datamodel/cyancruise-business-modeling.md`
- `openspec/changes/model-cyancruise-business-objects/`

## 验证方式

- 人工核对已建对象是否与设计器一致。
- 人工核对待建对象字段是否符合当前命名规范。
- 运行 `openspec validate model-cyancruise-business-objects --strict`。
