## ADDED Requirements

### Requirement: 按用户推荐读取职业计划周重点
按用户 ID 生成今日行动推荐时，系统 SHALL 通过可替换的职业计划边界读取该用户已保存的职业计划，并将计划本周重点填入 `CareerAgentRuleInput.weeklyFocusItems`。如果用户没有计划或计划本周重点为空，系统 SHALL 继续返回基于画像、测评、简历和执行状态的今日推荐，而不是失败。

#### Scenario: 用户有职业计划周重点
- **WHEN** 用户已有职业计划且计划包含本周重点
- **THEN** 按用户 ID 生成的今日推荐包含来源为 `PLAN_WEEKLY` 的行动项

#### Scenario: 用户没有职业计划
- **WHEN** 用户没有职业计划且按用户 ID 生成今日推荐
- **THEN** 系统不伪造计划周重点，并继续返回今日行动推荐

#### Scenario: 计划输入源可替换
- **WHEN** 后续 Cosmic datamodel 职业计划适配器替换默认计划存储
- **THEN** 今日行动输入源通过同一计划边界读取本周重点，无需修改今日行动规则 helper
