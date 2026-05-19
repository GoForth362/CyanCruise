# Career Agent 最小数据模型设计

## 目标

第一阶段只支撑“今日职业规划建议”闭环，不迁移旧 IPD 的全部 Entity。

规则服务需要的标准输入是 `CareerAgentRuleInput`，后续苍穹数据模型或表单插件只负责把业务数据组装成这个 DTO。

## 最小对象

### 1. 用户职业画像

建议实体：`cc_agent_profile`

用途：保存一人一份的职业画像聚合结果，优先承接旧系统 `AgentUserProfile` 和 onboarding 信息。

关键字段：

- `user_id`：用户标识。
- `target_role`：目标岗位。
- `target_role_source`：目标岗位来源，如 `PREFERENCES`、`RESUME`、`INTERVIEW`、`USER_INPUT`。
- `current_stage`：当前职业准备阶段。
- `personalization_level`：个性化等级，`LOW`、`MEDIUM`、`HIGH`。
- `completeness_score`：画像完整度。
- `identity_type`：身份类型，如 `new_graduate`、`internship_seeker`、`career_switcher`。
- `has_resume`：onboarding 自报是否已有简历，建议保留 `yes`、`no`。
- `primary_risk_code`：主要风险编码。
- `updated_at`：最后更新时间。

### 2. 测评摘要

建议实体：`cc_assessment_record`

只保留今日规则需要的最近一次已完成测评摘要。

关键字段：

- `user_id`
- `scale_id`
- `scale_title`
- `status`
- `summary`
- `suggested_roles_json`
- `completed_at`

### 3. 简历摘要

建议实体：`cc_resume_summary`

只保留最近一份可用于求职准备度判断的简历摘要。

关键字段：

- `user_id`
- `resume_id`
- `resume_key`
- `title`
- `target_job`
- `diagnosis_score`
- `updated_at`

### 4. 面试摘要

建议实体：`cc_interview_summary`

只保留最近一次已完成模拟面试摘要。

关键字段：

- `user_id`
- `interview_id`
- `position_name`
- `difficulty`
- `last_score`
- `weak_dimensions_json`
- `strong_dimensions_json`
- `completed_at`

### 5. 打卡状态

建议实体：`cc_career_checkin`

用于汇总本周执行节奏和今日任务完成度。

关键字段：

- `user_id`
- `check_day`
- `action`
- `created_at`

规则输入映射：

- `weeklyDays`：本周有有效行动的去重天数。
- `todayCompleted`：今日已完成核心任务数。
- `todayTotal`：今日核心任务总数。

### 6. 职业计划

建议实体：`cc_career_plan`

用于提供本周重点行动。

关键字段：

- `user_id`
- `target_role`
- `weekly_focus_json`
- `version`
- `last_updated_at`

## 适配层边界

当前代码已预留：

- `CareerAgentRuleInputSource`：从苍穹数据模型读取并组装 `CareerAgentRuleInput`。
- `CareerAgentTodayApplicationService`：应用服务入口，负责连接数据源和纯规则服务。
- `CareerAgentTodayRuleService`：纯 Java 规则，不依赖苍穹、Spring、JPA。

后续实现顺序：

1. 在苍穹设计器创建上述最小业务对象。
2. 用苍穹 `DynamicObject` 查询实现 `CareerAgentRuleInputSource`。
3. 给 WebAPI 增加按当前用户或 `userId` 查询的入口。
4. 再考虑表单插件或操作插件调用同一个 `CareerAgentTodayApplicationService`。
