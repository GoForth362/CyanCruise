## Context

CyanCruise 已完成 CareerLoop 用户侧主循环和多个 P2 能力，但管理后台仍停留在迁移地图的“待迁移”状态。IPD 管理后台由 `AdminController` 提供组织、学生、技能图谱、题库、用户、内容、广播、统计和审计接口，由 `AdminAuthService` 校验 `ADMIN` 角色，由 `AdminAuditAspect` 在成功返回后写审计记录，并由 `admin-frontend` 的 Vue/Element Plus 页面消费。

本次迁移目标是先建立 Cosmic/JDK 8 兼容的管理治理契约。IPD 中 Spring Controller、JPA repository、Flyway SQL、AOP 注解拦截、Vue/Element Plus/Vite/Pinia、旧 JWT 管线和前端页面实现不直接迁移。CyanCruise 后续实现 SHALL 复用已有用户画像、测评、面试、职业计划、就业资源、通知订阅和 datamodel adapter 边界。

## Goals / Non-Goals

**Goals:**

- 定义 JDK 8 兼容的管理后台 DTO：管理员身份、组织、学生行、学生详情、组织看板、用户治理、题库审核、内容项、广播请求、统计摘要、审计日志和分页结果。
- 提供纯 Java helper 规则：管理员角色判断结果、分页边界、用户封禁/解封校验、广播目标解析、题库审核状态转换、内容上下架/置顶状态转换、内容安全本地拦截、面试雷达聚合、弱项排序和审计动作构造。
- 提供 app01 管理后台应用服务、存储边界和 Cosmic WebAPI，确保所有写操作可审计。
- 支持管理入口消费：whoami、组织列表/保存、组织看板、学生列表/详情、用户列表/详情/封禁/解封、题库列表/更新/审核、内容列表/创建/更新/删除/置顶/隐藏、广播、统计摘要、审计日志。
- 更新迁移地图和验证流程，确保实现阶段可通过 OpenSpec、聚焦测试、静态 route 检查和 JDK 8 Gradle 构建。

**Non-Goals:**

- 不在 propose 阶段实现代码。
- 不直接迁移 IPD Spring Boot Controller、JPA/Flyway、repository、AOP、Lombok builder 语义、旧 JWT 安全管线或 Java 17 API。
- 不迁移 IPD `admin-frontend` 的 Vue、Element Plus、Vite、Pinia、axios 拦截器或页面布局代码。
- 不建立生产级 RBAC、菜单权限、苍穹组织权限模型或单点登录；首轮只定义 `ADMIN` 等价角色和当前用户身份输入边界。
- 不新增外部依赖，除非 apply 阶段证明 Cosmic/JDK 8 现有能力无法满足且明确说明必要性。

## Decisions

### 1. 管理后台以治理契约迁移，不复制旧前端和 Spring 管线

`base-common` SHALL 定义管理 DTO 和常量；`base-helper` SHALL 定义可测试规则；`app01` SHALL 提供应用服务、存储边界和 Cosmic WebAPI。webapp 只维护 route/API 映射或 Cosmic 管理入口挂载说明，不直接迁移 Vue 组件。

原因：IPD admin-frontend 和 Spring Boot 管线不符合 CyanCruise Cosmic/JDK 8/KDDT 约束。业务价值在治理流程、字段语义和审计契约，而不是旧技术实现。

替代方案是复制 admin-frontend 和 `/api/admin` Controller；这会引入 Vue/Element Plus/Spring/JPA 依赖，不采用。

### 2. 所有管理操作先鉴权，写操作必须可审计

管理 WebAPI SHALL 要求可解析用户身份，并通过角色存储或平台 adapter 校验 `ADMIN` 等价权限。用户封禁/解封、题库审核/删除、内容创建/更新/删除/置顶/隐藏、广播、技能图谱维护等写操作 SHALL 生成审计日志，记录 adminId、action、targetType、targetId、beforeJson、afterJson、ip、ua、createdAt。

原因：管理后台能影响用户账号、内容和通知投递，必须有最小权限门禁和操作追踪。

替代方案是在前端隐藏菜单或只依赖 token 存在；这无法防止越权调用，不采用。

### 3. 看板聚合采用容错解析和可解释降级

组织看板 SHALL 汇总学生数、面试次数、报告数、雷达维度均值和最低 3 个弱项。报告 JSON 缺失、字段非数字或解析失败时 SHALL 跳过该报告并保留可观测 warning/fallback，不使整个看板失败。

原因：IPD 面试报告来自 AI JSON，历史数据可能不稳定。管理看板应尽量可用，并暴露数据覆盖情况。

替代方案是遇到坏报告直接失败；这会让单条脏数据拖垮组织看板，不采用。

### 4. 题库治理区分公开市场和管理员审核

公开题库可继续服务用户侧列表、点赞和贡献；管理后台 SHALL 能看到隐藏、AI 生成和待审核问题，并支持更新内容、岗位、难度、答案、状态、reviewStatus、批准、拒绝和删除。用户贡献内容 SHALL 经过本地内容安全规则；管理员审核状态 SHALL 通过明确状态机转换。

原因：IPD 题库同时服务模拟面试和运营审核，迁移后需要保护用户侧体验，同时给运营留出治理入口。

替代方案是只迁移用户侧题库或只迁移后台审核；都会丢失题库闭环，不采用。

### 5. 广播复用通知能力，发送失败按结果汇总

管理广播 SHALL 复用已迁移通知/订阅能力的站内通知写入边界。广播到单用户或全体活跃用户时 SHALL 校验标题和内容，按用户归属写入 `ADMIN_BROADCAST` 通知，并返回目标数、成功数、失败数或跳过原因。

原因：通知/订阅已提供 best-effort 写入语义，管理后台不应重复实现消息基础设施。

替代方案是广播绕过通知服务直接写存储；这会产生重复字段和不可审计投递语义，不采用。

## Risks / Trade-offs

- [Risk] Cosmic 生产权限模型尚未最终确定 -> Mitigation：先定义 `ADMIN` 等价角色 adapter 和 identity-required/forbidden 状态，后续接平台权限。
- [Risk] 管理后台范围较大 -> Mitigation：首轮聚焦治理契约和核心 WebAPI，页面只做 route/API 映射或平台挂载说明。
- [Risk] 审计 before/after JSON 可能包含敏感字段 -> Mitigation：helper SHALL 提供快照脱敏/字段白名单，用户密码、token、openid 等敏感字段不得进入审计快照。
- [Risk] 统计口径会随业务演进变化 -> Mitigation：统计摘要 DTO 带口径说明、时间窗口和未知事件容错。
- [Risk] 题库/内容管理与既有能力交叉 -> Mitigation：复用现有 assessment/interview/employment/notification 边界，只在管理服务聚合治理操作。

## Migration Plan

1. 新增管理后台 DTO、状态常量、审计动作常量和分页/筛选契约。
2. 新增 helper 规则：鉴权结果、治理校验、看板聚合、题库状态转换、内容安全、审计快照和广播投递汇总。
3. 新增 app01 存储边界和管理应用服务，复用已有用户、面试、职业路径、题库、内容、通知和审计 adapter。
4. 新增 Cosmic WebAPI：whoami、组织、学生、用户治理、题库审核、内容管理、广播、统计、审计查询。
5. 更新 webapp route/API 映射或 Cosmic 管理入口说明，不引入 Vue/uni-app/admin-frontend 运行时。
6. 更新迁移地图，运行 OpenSpec、聚焦测试、webapp route 静态检查、JDK 8 Gradle 测试和完整构建。

Rollback：管理后台治理是新增平台侧能力。若实现异常，可隐藏管理入口或禁用 `/cc001/admin/*` WebAPI，不影响用户侧 CareerLoop 主流程；广播应保留 best-effort 语义，失败不得破坏通知以外的业务。

## Open Questions

- 生产态管理员身份最终来自 Cosmic 登录上下文、CyanCruise 用户角色表还是平台菜单权限，需要 apply 或后续 change 明确。
- 管理入口使用 Cosmic 标准管理页面、`webapp/isv/v620/careerloop` 静态入口还是 KDDT 菜单挂载，需要与平台部署方式对齐。
- 组织、学生和用户数据的最终 datamodel 字段是否需要补充正式 Cosmic 对象，需要与 `cosmic-datamodel-adapters` 的后续适配一起确认。
