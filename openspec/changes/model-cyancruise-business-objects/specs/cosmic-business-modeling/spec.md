## ADDED Requirements

### Requirement: Agent执行记录 SHALL 提供可排障的单据布局

系统 SHALL 为 `tk_v620_cc_agent_run` 提供面向管理员和排障人员的单据布局，覆盖基本信息、执行时间与消耗、执行步骤、输入输出和错误信息。

#### Scenario: 查看 Agent 执行记录

- **WHEN** 管理员打开一条 Agent执行记录
- **THEN** 表单 SHALL 展示单据编号、用户 ID、智能体名称、场景编码、触发方式、执行状态、开始时间、结束时间、耗时和总消耗
- **AND** 表单 SHALL 通过分录展示执行步骤

#### Scenario: 排查失败记录

- **WHEN** 执行状态为失败
- **THEN** 记录 SHALL 至少保存 `error_code` 或 `error_message` 之一
- **AND** 输入内容 SHALL NOT 保存 access token、Authorization header、签名 URL 或客户私有凭据

### Requirement: 职业测评记录 SHALL 作为用户测评事件单据

系统 SHALL 为职业测评提交结果建立 `tk_v620_cc_assessment_record` 单据对象，记录用户、量表、状态、摘要、主要结果、完成时间、维度分数、推荐岗位、答案快照和完整结果。

#### Scenario: 保存已完成测评

- **WHEN** 用户完成一次职业测评
- **THEN** 职业测评记录 SHALL 保存 `user_id`、`scale_id`、`scale_title`、`status`、`summary`、`primary_result` 和 `completed_at`
- **AND** 答案明细 SHALL 保存题目、选项、维度编码、分值和是否有效

#### Scenario: 保留非法答案快照

- **WHEN** 用户提交的答案包含不属于当前题目的选项
- **THEN** 职业测评记录 SHALL 保留该答案快照
- **AND** 该答案明细 SHALL 标记为无效，且 SHALL NOT 计入维度分数

### Requirement: 建模文档 SHALL 记录连续建模顺序

系统 SHALL 在 `datamodel/` 下维护 CyanCruise 金蝶业务建模进度文档，记录已建对象、当前布局、下一个对象和后续建模顺序。

#### Scenario: 继续下一个业务对象

- **WHEN** 已完成用户职业画像和 Agent执行记录的对象定义
- **THEN** 建模文档 SHALL 把职业测评记录作为下一个业务对象
- **AND** 文档 SHALL 给出简历记录、今日任务、职业计划、模拟面试记录和求职助手会话的后续顺序建议

### Requirement: 简历记录 SHALL 承接简历文件和诊断结果

系统 SHALL 为简历创建、文件引用、解析内容和诊断结果建立 `tk_v620_cc_resume` 单据对象，记录用户、简历标题、目标岗位、稳定文件标识、版本、状态、诊断分数、解析内容、关键词和诊断建议。

#### Scenario: 创建简历记录

- **WHEN** 用户创建一条简历记录
- **THEN** 简历记录 SHALL 保存 `user_id`、`title`、`target_job`、`status`、`created_at` 和 `updated_at`
- **AND** 系统 MAY 保存 `file_key` 或 `parsed_content` 作为后续诊断输入

#### Scenario: 保存文件引用

- **WHEN** 文件上传能力返回平台文件标识
- **THEN** 简历记录 SHALL 只保存稳定 `file_key`
- **AND** 简历记录 SHALL NOT 保存 access token、Authorization header、签名 URL 或客户私有凭据

#### Scenario: 诊断后更新简历

- **WHEN** 简历诊断完成
- **THEN** 简历记录 SHALL 更新 `diagnosis_score`、关键词明细和诊断建议
- **AND** `diagnosis_score` SHALL 保持在 0 到 100 之间
