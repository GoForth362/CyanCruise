# 今日行动推荐规格

## Purpose
定义 CyanCruise 如何基于 CareerLoop 中的目标岗位、职业画像、测评、简历、面试、职业计划周重点和执行状态生成今日下一步行动，为后续 AgentTask、风险看板和页面入口提供稳定推荐契约。
## Requirements
### Requirement: 基于 CareerLoop 信号生成今日行动
系统 SHALL 基于用户画像快照、测评摘要、简历摘要、面试摘要、职业计划周重点和执行状态生成今日行动推荐。推荐结果 SHALL 包含阶段、风险等级、标题、原因、今日焦点、进度百分比、风险原因和行动项。

#### Scenario: 缺少目标岗位
- **WHEN** 用户没有 preferences、简历或面试提供的目标岗位
- **THEN** 系统推荐先选择目标岗位，并把测评作为中优先级补充行动

#### Scenario: 目标岗位已确定但缺少测评
- **WHEN** 用户已有目标岗位但没有测评摘要
- **THEN** 系统推荐完成职业测评以建立方向基线

#### Scenario: 目标岗位和测评已存在但缺少系统简历
- **WHEN** 用户已有目标岗位和测评摘要，但没有系统保存的简历记录
- **THEN** 系统推荐创建或上传简历，并将该行动标记为高优先级

#### Scenario: 简历分数偏低
- **WHEN** 用户已有简历且诊断分数低于推荐阈值
- **THEN** 系统推荐优化简历，并将简历低分加入风险原因

#### Scenario: 面试信号缺失
- **WHEN** 用户已有方向、测评和简历，但没有模拟面试摘要
- **THEN** 系统推荐开始目标岗位的模拟面试练习

#### Scenario: 执行节奏不足
- **WHEN** 用户核心画像信号齐全，但本周有效行动天数低于规则阈值
- **THEN** 系统推荐先完成一项核心职业行动以重建节奏

### Requirement: 支持 onboarding 特殊路径
系统 SHALL 保留 IPD onboarding 对今日行动的特殊路径：转行用户优先整理转岗证据，找实习且无简历用户优先整理实习简历素材，应届用户自报已有简历但系统无简历时优先上传简历并做 JD 匹配。

#### Scenario: 转行用户
- **WHEN** onboarding 身份类型为 `career_switcher`
- **THEN** 今日行动阶段为转岗定位，并推荐整理转岗理由和能力证据

#### Scenario: 找实习且无简历
- **WHEN** onboarding 身份类型为 `internship_seeker` 且用户没有系统简历
- **THEN** 今日行动推荐整理实习简历素材

#### Scenario: 应届用户自报已有简历
- **WHEN** onboarding 身份类型为 `new_graduate` 且自报已有简历但系统没有简历记录
- **THEN** 今日行动推荐上传已有简历并匹配目标 JD

### Requirement: 追加职业计划周重点
系统 SHALL 在输入包含职业计划周重点时，将最多两个周重点追加为今日行动项，并在推荐原因中说明今日任务已与长期规划对齐。

#### Scenario: 附加周重点行动
- **WHEN** 规则输入包含非空周重点列表
- **THEN** 推荐结果包含来源为 `PLAN_WEEKLY` 的行动项，且不超过两个

### Requirement: 提供可替换的用户输入源
系统 SHALL 保留直接传入 `CareerAgentRuleInput` 的规则入口，同时提供按用户 ID 组装规则输入的应用服务边界。默认用户输入源 SHALL 能够读取当前职业画像快照；未来 Cosmic datamodel、打卡和职业计划适配 SHALL 通过同一边界替换或增强。

#### Scenario: 直接规则输入
- **WHEN** 调用方传入完整 `CareerAgentRuleInput`
- **THEN** 系统直接使用该输入生成今日行动，不读取外部存储

#### Scenario: 按用户 ID 推荐
- **WHEN** 调用方只提供用户 ID
- **THEN** 系统通过输入源读取该用户画像快照并生成今日行动

#### Scenario: 输入源未配置
- **WHEN** 没有配置可用输入源
- **THEN** 系统返回明确的未配置错误，而不是伪造用户画像

### Requirement: 暴露 Cosmic WebAPI 今日推荐入口
系统 SHALL 通过 Cosmic WebAPI 暴露今日推荐能力，并保持与现有 `/cc001/career-agent` 路径风格一致。WebAPI SHALL 支持直接传入规则输入，也 SHALL 支持按用户 ID 获取今日推荐。

#### Scenario: WebAPI 直接推荐
- **WHEN** 调用方请求 `/today` 并提交规则输入
- **THEN** WebAPI 返回今日行动推荐结果

#### Scenario: WebAPI 按用户推荐
- **WHEN** 调用方请求按用户 ID 生成今日推荐
- **THEN** WebAPI 通过应用服务输入源生成并返回今日行动推荐结果
