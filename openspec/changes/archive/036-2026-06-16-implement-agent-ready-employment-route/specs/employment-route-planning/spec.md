## ADDED Requirements

### Requirement: Agent-ready employment route plan

CyanCruise SHALL provide an agent-ready employment route plan that can be generated from user profile data and displayed as structured long-term, weekly, and daily guidance.

#### Scenario: Rule fallback generates route plan

- **WHEN** 用户进入 `AI路径规划` 页面并点击生成或刷新路线图
- **THEN** 后端 SHALL 在没有真实规划智能体时生成规则版路线图
- **AND** 路线图 SHALL 包含目标岗位、1 年或 3 年大阶段、小阶段、本周计划和每日建议

#### Scenario: Agent integration can reuse contract

- **WHEN** 后续接入规划智能体
- **THEN** 智能体 SHALL 返回与规则版路线图相同的 `CareerPlanRecordDto` 结构
- **AND** 前端 SHALL NOT 需要为了智能体来源重写页面结构

#### Scenario: Career plan page displays full route

- **WHEN** 用户打开 `career-plan`
- **THEN** 页面 SHALL 展示路线状态、目标岗位、大阶段、小阶段、本周计划和每日建议
- **AND** 页面 SHALL 提供刷新路线图入口

#### Scenario: Employment home uses route summary

- **WHEN** 用户进入就业首页
- **THEN** 就业首页 SHALL 优先使用后端路线图摘要展示下一步行动
- **AND** 当后端不可用时 SHALL 保留规则版中文降级提示
