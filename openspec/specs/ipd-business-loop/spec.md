# IPD 业务闭环规格

## Purpose
定义 CyanCruise 迁移工作必须保留并优先实现的 IPD CareerLoop 产品流程。该规格用于约束后续迁移顺序，确保每个能力都服务于目标岗位、画像、简历、任务、面试、反馈和计划组成的求职准备闭环。
## Requirements
### Requirement: 保留 CareerLoop 业务闭环
系统迁移 SHALL 保留 IPD CareerLoop 核心闭环，并将其作为产品流程指引：目标岗位、用户画像或测评、简历诊断、JD 匹配、今日任务、模拟面试、反馈、下一步行动和职业计划。

#### Scenario: 定义迁移 change
- **WHEN** 新的 IPD 迁移 change 被提出
- **THEN** 该 change 必须标明它支持 CareerLoop 业务闭环中的哪一部分

#### Scenario: 延后非核心工作
- **WHEN** 某个 IPD 源能力不直接支持核心闭环
- **THEN** 该能力被归类为 P2，除非 proposal 说明它为什么需要更早实现

### Requirement: 优先实现 P0 闭环入口能力
首批实现 change SHALL 优先处理职业画像、onboarding、测评核心、简历核心和今日行动推荐，再处理优先级较低的资源、通知和管理功能。职业画像和 onboarding SHALL 作为第一个实现切片，因为它们提供后续 P0 能力所需的目标岗位、用户上下文和早期信号。测评核心 SHALL 作为下一个 P0 切片，因为它为用户画像和推荐提供第一份结构化方向基线。

#### Scenario: 选择首个实现范围
- **WHEN** 团队在 baseline 之后选择下一个实现 change
- **THEN** 所选 change 必须来自 P0 迁移范围，除非另有明确批准

#### Scenario: 启动画像和 onboarding 迁移
- **WHEN** `migrate-career-profile-onboarding` 处于 active 状态
- **THEN** 它被视为 CareerLoop 迁移的第一个 P0 实现切片

#### Scenario: 启动测评核心迁移
- **WHEN** `migrate-assessment-core` 处于 active 状态
- **THEN** 它被视为画像和 onboarding 之后的下一个 P0 实现切片

### Requirement: 保持以用户为中心的产品语言
迁移后的产品体验 SHALL 强调用户的下一步求职准备行动、准备度、目标岗位和计划，而不是要求用户理解内部 agent 架构。

#### Scenario: 编写用户可见文案
- **WHEN** 迁移 change 引入用户可见文案
- **THEN** 文案描述求职准备结果和下一步行动，而不是内部 agent 机制
