## Context

现有 `CareerPlanStorage`、`CareerDailyTaskStorage` 均以 `userId` 作为唯一业务键，服务和 `/cc001/career-plan/*` 接口默认只处理就业规划。升学中心已经有独立方向选择与 PostgreSQL 存储，但没有长期规划、每日任务和 AI 生成能力。首页提交的 `routeGoal` 目前也没有完整进入后端快照，刷新或跨设备后无法可靠判断当前路线。

项目必须兼容 JDK 1.8、既有 Cosmic/KAPI 路由和现有就业用户数据；不能以破坏既有就业表主键的方式完成改造。

## Goals / Non-Goals

**Goals:**

- 就业与升学分别持久化一份当前规划和独立每日任务完成状态。
- 当前路线和升学具体方向均可持久化、读取和切换。
- 升学中心补齐与就业中心一致的规划摘要、完整规划、生成/刷新和每日任务体验。
- 考研、保研、留学使用不同的智能体配置和提示语义。
- 首页、路径规划和今日行动始终优先展示当前路线数据，另一条路线数据保持不变。

**Non-Goals:**

- 不合并考研陪伴、保研陪伴、留学陪伴既有业务表单与规划主循环。
- 不把就业规划迁移到新表，不修改已有就业规划记录的主键。
- 不允许用户同时选择多个升学主方向。
- 不新增第三方依赖，不引入 Spring/JPA/Flyway。

## Decisions

1. 保留 `CareerPlanStorage` 和 `CareerDailyTaskStorage` 作为就业专用存储；在 `StudyCenterStorage` 增加升学规划与升学每日任务读写能力。PostgreSQL 新增 `cc_study_center_plan`、`cc_study_center_daily_task` 两张独立表，避免对已有就业表做高风险主键迁移。
2. `CareerPlanRecordDto`、`CareerPlanSummaryDto` 和每日任务响应增加 `routeType`、`studyDirection`、`targetSchool` 等可选上下文字段。旧就业调用不传这些字段时按 `EMPLOYMENT` 处理，保持兼容。
3. 在 onboarding 请求与快照中持久化 `routeGoal`，其规范值为 `employment` 或 `study`。切换路线只更新这个字段，不删除、覆盖或重新生成任何计划。
4. 新建 `StudyPlanApplicationService` 管理升学规划。它要求升学方向已保存，并通过存储适配器复用现有路线摘要与每日任务执行结构；规则兜底计划由升学专用 helper 生成，不复用就业岗位文案。
5. 新建升学规划智能体生成器，根据 `POSTGRADUATE`、`RECOMMENDATION`、`STUDY_ABROAD` 分别读取 `cc001.agent.platform.study.postgraduate`、`cc001.agent.platform.study.recommendation`、`cc001.agent.platform.study.abroad` 配置。未选方向时不调用智能体；调用失败时保留原规划。
6. 现有 `/cc001/career-plan/*` 继续代表就业规划；升学新增 `/cc001/study-center/plan/*` 和 `/cc001/study-center/daily/*`。前端根据当前路线选择接口，避免隐式接口语义变化。
7. 首页同时保留 `employmentPlan/studyPlan` 与 `employmentDailyPlan/studyDailyPlan` 状态，但只渲染当前路线对应数据。`career-plan` 与 `today-action` route 复用现有页面结构，按当前路线切换标题、目标字段、按钮和接口。
8. 升学方向变更后保留旧方向规划数据直到用户明确生成新规划；摘要将标记方向不一致并提示更新。生成新方向规划后覆盖“当前升学规划”，就业规划完全不受影响。

## Risks / Trade-offs

- [升学方向切换后旧规划暂时与新方向不一致] → 摘要明确提示“方向已变更，请生成新规划”，不把旧规划伪装为新方向结果。
- [多个智能体配置增加部署复杂度] → 使用统一配置结构和三个明确前缀，缺少配置时提供规则版兜底并显示可恢复提示。
- [前端路由继续复用 `career-plan`/`today-action`] → 所有请求都从持久化 `routeGoal` 解析，页面明显展示“就业”或具体升学方向，避免上下文混淆。
- [PostgreSQL 初始化与 Cosmic 模式差异] → 当前升学中心生产存储沿用其既有 PostgreSQL/内存选择；不影响已有 Cosmic 就业数据对象。

## Migration Plan

1. 先发布 DTO、服务、WebAPI 和新升学表初始化逻辑，保留旧就业接口不变。
2. 发布前端并更新静态资源版本，使首页与两个中心使用路线感知接口。
3. 已有用户默认保留当前 `routeGoal`；缺失时根据自画像目标院校/岗位兼容推断，首次保存后持久化规范值。
4. 验证同一用户依次生成就业和升学规划、切换路线、完成每日任务并跨实例读回。
5. 回滚前端时新升学表保留；回滚后端时既有就业规划仍可工作，不需要数据回退。

## Open Questions

无。智能体编号和任务流编码由部署环境分别配置，不写入业务代码。
