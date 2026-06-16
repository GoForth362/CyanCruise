## Design

### 数据契约

继续使用现有 `CareerPlanRecordDto` 作为持久化根对象，新增结构化字段：

- `planningMode`：`RULE_FALLBACK` 或后续 `AGENT`
- `horizonYears`：路线目标年限，默认 3
- `phases`：大阶段列表，每个阶段包含阶段目标、时间范围、行动、指标、小阶段
- `weeklyPlan`：本周推进计划
- `dailySuggestions`：每日建议动作
- `agentStatus`：当前生成来源状态，例如 `FALLBACK_READY`、`AGENT_READY`

新增 DTO：

- `CareerPlanPhaseDto`
- `CareerPlanSubStageDto`
- `CareerPlanWeeklyPlanDto`

保留 `milestones` 和 `weeklyFocus`，作为旧接口兼容字段。

### 生成策略

`CareerPlanApplicationService.ensurePlan` 的生成顺序：

1. 如已存在结构化路线图并健康，则直接返回。
2. 如配置了 `CareerPlanAiGenerator`，后续可调用智能体生成 `CareerPlanRecordDto`。
3. 当前无智能体时，调用规则版生成器，根据目标岗位、画像、简历与面试状态生成默认路线。

### 前端展示

`career-plan` 页面改为真实功能页：

- 顶部展示目标岗位、路线来源、刷新按钮。
- 大阶段以时间线或阶段卡展示。
- 小阶段展示每周目标和行动。
- 每日建议展示为紧凑任务清单。
- 接口失败时保留中文降级状态。

### 验证

- OpenSpec strict 校验。
- JDK 8 下运行职业规划相关测试。
- `node --check` 和路由校验。
