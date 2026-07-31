## Context

升学规划生成器按 `POSTGRADUATE`、`RECOMMENDATION`、`STUDY_ABROAD` 维护独立配置，并共享 Agent SDK 客户端、结构解析、路线校验和按 `(userId, direction)` 持久化能力。当前 SDK 客户端在 `taskFlowCode` 与 `agentNumber` 同时存在时优先运行任务流，留学调试配置又未设置 `agentNumber`，因此无法保证调用用户刚发布的留学规划智能体。

## Goals / Non-Goals

**Goals:**

- 留学规划只通过独立 `agentNumber` 运行已发布智能体。
- 保持完整原始结构化输入、单一 JSON 解析、十二个月覆盖校验、无兜底和原规划保护。
- 保持考研、保研、留学的规划、阶段、今日行动和资料按用户与方向隔离。
- 本地调试和部署配置清晰暴露留学智能体编码。

**Non-Goals:**

- 不在代码中硬编码具体智能体编码。
- 不修改就业、考研或保研当前调用策略。
- 不新增留学专用数据库表，不改变共享规划 JSON 契约。
- 不在后端补造院校、项目、截止日期或默认规划。

## Decisions

### 1. 在升学规划生成器装配阶段为三个方向创建 agent-only 配置

分别加载 `cc001.agent.platform.study.postgraduate.*`、`recommendation.*` 和 `abroad.*` 后，显式忽略三个升学方向的 `taskFlowCode`，并且只有 `enabled=true` 且存在 `agentNumber` 时才创建 Agent SDK 客户端。就业、简历诊断等其他能力的调用策略保持不变。

备选方案是在共享 SDK 客户端增加全局“智能体优先”开关，但会扩大影响范围并增加现有考研、保研回归风险，因此不采用。

### 2. 生成请求不携带留学任务流编码

`AgentPlatformStudyPlanGenerator` 对所有升学规划请求都将 `taskFlowCode` 保持为空，SDK 客户端因此必然调用 `runAgent(agentNumber, query)`。三个升学智能体接收与平台预览测试一致的原始 JSON 文本，不再把整段 JSON 二次编码成带外层引号的 JSON 字符串；就业、简历等其他智能体继续保持现有编码方式。各升学智能体内部可按角色设定调用自己的规划任务流。

### 3. 复用现有解析与持久化边界

智能体最终回答仍须为一个共享规划 JSON。只有通过必要字段、阶段数量、连续十二个月覆盖和留学方向校验的结果才以 `STUDY_ABROAD` 保存；失败时保留原规划且不生成默认阶段或今日行动。

### 4. 具体智能体编码只通过运行环境注入

本地使用 `debug-local.properties`，部署环境使用 `CC001_AGENT_PLATFORM_STUDY_ABROAD_AGENTNUMBER`。仓库示例和文档只保留占位说明，不在业务代码中写入平台编码。

## Risks / Trade-offs

- [运行环境未配置留学智能体编码] → 返回可恢复中文错误并保持真实空状态，日志只记录安全化目标信息。
- [智能体返回两个 JSON 或带解释文字] → 共享解析与结构校验拒绝保存，避免污染持久化数据。
- [智能体内部任务流未启用或未绑定] → 后端视为调用失败，不自行改用旧任务流或兜底规划。
- [已有留学任务流配置残留] → 装配阶段主动忽略该字段，确保不会误路由。

## Migration Plan

1. 部署 agent-only 留学选择逻辑与回归测试。
2. 在实际运行环境设置 `enabled=true` 和已发布的留学智能体 `agentNumber`，不设置留学 `taskFlowCode`。
3. 重启星瀚服务，从升学中心选择留学并生成规划，验证路线、今日行动和方向隔离。
4. 若需回滚，仅撤回本次选择逻辑；已保存的合法留学规划仍按 `STUDY_ABROAD` 保留。

## Open Questions

- 实际留学规划智能体编码由平台发布后提供，并通过运行环境注入。
