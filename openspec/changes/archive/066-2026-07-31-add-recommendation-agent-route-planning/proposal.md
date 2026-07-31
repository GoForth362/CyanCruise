## Why

当前升学中心虽可选择保研，但完整路线、今日行动和规划资料仍主要围绕考研链路实现，升学规划表与每日任务也只按用户保存，切换考研和保研会产生覆盖风险。需要复用已验证的考研智能规划链路，为保研提供无规则兜底、可直接接入智能体且按用户与方向隔离持久化的正式能力。

## What Changes

- 为保研方向提供独立的升学中心状态、规划依据资料、完整路径规划、本周计划和今日行动。
- 将升学规划、每日任务和资料统一按 `userId + direction` 隔离，确保考研、保研及后续留学互不覆盖。
- 为保研规划预留独立智能体配置与调用接口；未配置、调用失败或结构不合格时返回可恢复错误，不生成任何规则兜底或示例路线。
- 复用考研规划的结构化 JSON 解析、进度保护、每日任务拆解和 PostgreSQL 持久化能力，并使用保研语义校验输入输出。
- 升级现有 PostgreSQL 表和兼容迁移逻辑，使历史考研数据保持可读，新保研数据可跨实例恢复。

## Capabilities

### New Capabilities

- `recommendation-agent-route-planning`: 定义保研智能体输入、真实路线校验、资料引用、阶段保护和失败行为。

### Modified Capabilities

- `study-center`: 升学中心按当前方向展示并操作保研规划资料与真实规划状态。
- `independent-route-plan-storage`: 升学规划、每日任务和规划资料从仅按用户隔离扩展为按用户与具体升学方向隔离。
- `today-action-recommendation`: 今日行动按当前保研方向读取、更新和恢复持久化任务，且无规划时不构造任务。
- `webapp-cyancruise-pages`: 保研方向提供与考研一致的升学中心、完整规划和今日行动交互，并只展示真实智能体数据。

## Impact

- 后端：`StudyPlanApplicationService`、`AgentPlatformStudyPlanGenerator`、升学中心 WebAPI、每日任务服务及存储适配器。
- 存储：`cc_study_center_plan`、`cc_study_center_daily_task`、`cc_study_center_material` 的复合隔离键和迁移逻辑。
- 前端：升学中心、路径规划、今日行动、规划资料管理及路线/API 元数据。
- 配置：继续使用 `cc001.agent.platform.study.recommendation.*` 作为保研智能体预留配置，不硬编码智能体或任务流编码。
- 兼容 JDK 1.8，不引入新依赖，不写入公共知识库。
