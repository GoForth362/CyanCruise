## MODIFIED Requirements

### Requirement: 职业画像快照合并
系统 SHALL 维护一个用户画像快照，并为测评、简历、面试、偏好和 onboarding 保持彼此独立的 block，使每个迁移能力可以只更新自己的 block，而不会覆盖无关数据。快照合并 helper SHALL 有聚焦测试，验证无关 block 被保留，并验证空白目标岗位输入不会清除已有偏好。测评结果 SHALL 能够合并进 assessment block，记录最近测评记录、量表、摘要、推荐岗位和完成时间，且不覆盖 onboarding 或 preference 数据。

#### Scenario: 保留已有测评和简历数据
- **WHEN** 已有测评或简历数据的用户提交 onboarding 数据
- **THEN** 系统保留已有 assessment 和 resume block，只更新本次提供的 onboarding 字段

#### Scenario: 将测评合并到已有快照
- **WHEN** 已有 onboarding 和 preferences 的用户提交测评结果数据
- **THEN** 系统更新 assessment block，并保留 onboarding 和 preferences

#### Scenario: 测评结果记录推荐岗位
- **WHEN** 用户完成职业测评并产生推荐岗位
- **THEN** 系统在 assessment block 中保存 `suggestedRoles`，供统一画像目标岗位兜底规则使用
