# 变更提案：继续 CyanCruise 金蝶业务建模

## 背景

CyanCruise 已在金蝶云苍穹中开始建立核心业务对象，目前已有“用户职业画像”基础资料（`tk_v620_cc_user_profile`）和“Agent执行记录”单据（`tk_v620_cc_agent_run`）。下一步需要从 Agent 执行记录布局开始，把表单、列表、分录和校验口径固化下来，并继续建立下一个主循环业务对象。

## 目标

- 完成“Agent执行记录”的表单布局、列表布局、执行步骤分录、操作和校验说明。
- 新增“职业测评记录”业务对象定义，覆盖字段、答案明细分录、表单布局、列表布局和验证要点。
- 新增“简历记录”业务对象定义，覆盖文件引用、解析内容、关键词明细、诊断建议、表单布局、列表布局和验证要点。
- 建立持续建模进度文档，记录已建对象和后续对象顺序。

## 非目标

- 不直接迁移 IPD 的 Spring Boot、JPA、Flyway、Vue 或 uni-app 实现。
- 不在本变更中实现新的 Java 存储适配器或 WebAPI。
- 不替代金蝶设计器中的人工建模操作；本文档作为字段、布局和验证口径的实施依据。

## 影响范围

- `datamodel/`：新增 CyanCruise 金蝶业务建模进度文档。
- `openspec/changes/model-cyancruise-business-objects/`：记录本次建模变更的设计、规格和任务。

## 验证方式

- 使用 `openspec validate model-cyancruise-business-objects --strict` 校验 OpenSpec 变更。
- 人工核对文档中的对象名、表名、字段标识和用户可见中文名称。
