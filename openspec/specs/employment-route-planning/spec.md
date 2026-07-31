## Purpose

定义 CyanCruise 就业路线规划能力，包括可接入智能体的路线图契约、规则版兜底生成、阶段路线图、规划周期、每日建议、本周计划、进度勾选、首页今日行动联动和刷新边界。
## Requirements
### Requirement: Agent-ready employment route plan

CyanCruise SHALL 提供可接入智能服务的就业路线规划。运行时配置了任务流编码时 SHALL 优先直连已发布任务流；否则 MAY 使用智能体编码兼容调用。智能结果 SHALL 映射为现有 `CareerPlanRecordDto` 结构。

#### Scenario: 任务流返回包装后的路线

- **WHEN** 就业任务流通过 `data`、`result`、`answer` 或 `output` 返回路线，或将路线编码为 JSON 字符串
- **THEN** 后端 SHALL 提取满足路线契约的最终对象
- **AND** 前端 SHALL NOT 因智能服务来源变化而修改接口结构

#### Scenario: 智能体只返回调用过程

- **WHEN** 就业智能体只返回思考、工具调用参数或用户输入回显
- **THEN** 后端 SHALL 拒绝将其保存为就业路线
- **AND** SHALL 保留已有路线和完成状态

### Requirement: Career plan page displays full route

CyanCruise SHALL display the route plan as a navigable and trackable plan rather than a static placeholder.

#### Scenario: Career plan page displays route structure

- **WHEN** 用户打开 `career-plan`
- **THEN** 页面 SHALL 展示路线状态、目标岗位、规划周期、大阶段、小阶段、本周计划和每日建议
- **AND** 页面 SHALL 提供刷新路线图入口

#### Scenario: Daily plan appears before weekly plan

- **WHEN** 用户查看 `career-plan`
- **THEN** 页面 SHALL 在本周计划之前展示每日建议
- **AND** 每日建议 SHALL 是可在当天完成的小事项，而不是对本周计划或阶段目标的简单搬运

#### Scenario: Planning period can be selected

- **WHEN** 用户调整规划周期
- **THEN** 页面 SHALL 支持用户选择规划周期
- **AND** 刷新路线图 SHALL 使用用户选择的规划周期生成后续未开始内容

### Requirement: Route progress is based on stage tasks

CyanCruise SHALL track overall route progress from phase route tasks only, while daily and weekly plans act as supporting execution aids.

#### Scenario: User checks phase tasks

- **WHEN** 用户勾选阶段路线图中的阶段动作、关键结果或小阶段动作
- **THEN** 总目标进度 SHALL 根据阶段路线图任务完成数量更新
- **AND** 总目标进度 SHALL NOT count daily suggestions or weekly plan items directly

#### Scenario: All phase tasks are completed

- **WHEN** 阶段路线图中的所有可勾选任务都已完成
- **THEN** 总目标进度 SHALL 显示 100%

#### Scenario: Daily and weekly tasks support phase completion

- **WHEN** 用户完成每日建议或本周计划
- **THEN** 页面 SHALL preserve those completion states
- **AND** those states MAY help the user identify which phase tasks to complete, but SHALL NOT directly change the overall route percentage unless corresponding phase tasks are checked

### Requirement: Route flow and milestones

CyanCruise SHALL present the route plan with visual sequence and milestone progress so users can understand where they are in the plan.

#### Scenario: Route flow is displayed

- **WHEN** 用户查看阶段路线图
- **THEN** 页面 SHALL display phases in order with a visual arrow or navigation flow between phases
- **AND** each phase card SHALL show its time range, title, status, and completed task count

#### Scenario: Total progress bar shows milestone points

- **WHEN** 用户查看总目标进度
- **THEN** 页面 SHALL show a progress bar with key time points such as `0-1个月`, `1-3个月`, `3-12个月`, and `1-3年`
- **AND** milestone labels SHALL stay within the card boundary on desktop and mobile widths

### Requirement: Refresh preserves completed progress

CyanCruise SHALL preserve completed route tasks when refreshing a route plan.

#### Scenario: Refresh route plan

- **WHEN** 用户点击刷新路线图
- **THEN** 已完成的阶段任务 SHALL remain completed
- **AND** 刷新 SHALL prioritize updating tasks or suggestions that have not started

### Requirement: Employment home uses route summary

CyanCruise SHALL use route plan summary data to drive home page route and today-action entry points.

#### Scenario: Employment home uses route summary

- **WHEN** 用户进入就业首页
- **THEN** 就业首页 SHALL 优先使用后端路线图摘要展示下一步行动
- **AND** 当后端不可用时 SHALL 保留规则版中文降级提示

#### Scenario: Today action is connected

- **WHEN** 路线规划已经生成每日建议
- **THEN** 首页今日行动卡 SHALL 显示为已接入状态
- **AND** 今日行动 SHALL describe that it is based on route planning daily suggestions

### Requirement: Navigation preserves source context

CyanCruise SHALL keep users oriented when they enter and leave route-related pages.

#### Scenario: User returns from child page

- **WHEN** 用户从就业首页某个位置进入路线规划、资源、简历或面试等子页面后点击返回
- **THEN** 页面 SHALL return to the source page where the user came from
- **AND** 页面 SHOULD restore the previous scroll position when possible

