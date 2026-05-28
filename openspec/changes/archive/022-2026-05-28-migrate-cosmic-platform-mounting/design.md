## Context

CyanCruise 已完成 CareerLoop 多个后端能力和 `webapp/isv/v620/careerloop` 静态入口迁移，当前 route/API map 明确生产身份仍是 `pending-cosmic-login-context`。IPD 侧入口来自 uni-app `pages.json`、`App.vue` 启动守卫、`utils/auth.ts` 登录状态、`utils/request.ts` 请求拦截、`api/user.ts` 登录接口，以及管理端 `admin-frontend` 的 router 和 token 拦截。目标平台是 Kingdee Cosmic，不能直接搬迁 IPD 的 Vue/uni-app、axios、JWT、Spring Boot/JPA/Flyway 实现。

本 change 先定义平台挂载契约，审阅通过后再实现。实现应保持 JDK 1.8、Cosmic 二开工程结构、仓库内 `gradlew.bat` 和现有 OpenSpec 迁移治理约束。

## Goals / Non-Goals

**Goals:**

- 定义 CareerLoop webapp 在苍穹/Cosmic 中的可发布入口、菜单/KDDT 挂载和 route key 对应关系。
- 定义生产态身份解析：用户身份、管理员身份、缺失身份降级和开发态 fallback 的边界。
- 定义 webapp 调用 `/cc001/*` Cosmic WebAPI 的统一约束，包括显式身份、角色、错误状态和不可硬编码生产用户。
- 定义部署验证说明，使 webapp 资源、菜单、权限、路由图、OpenSpec、Node 静态检查和 JDK 8 Gradle 构建可被审阅。
- 更新迁移地图，记录 IPD 来源、CyanCruise 目标、数据/接口映射、暂不迁移项和验证方式。

**Non-Goals:**

- 不直接实现真实 KDDT 菜单发布脚本或替代客户环境中的苍穹配置台操作。
- 不迁移 IPD 登录注册、JWT、微信登录、uni-app tabBar、Vue router、axios 拦截器或 admin-frontend 页面实现。
- 不新增生产 RBAC、SSO、组织权限模型或最终 Cosmic 文件服务/datamodel adapter。
- 不改变既有业务 WebAPI 的 DTO 语义；只收敛入口和调用边界。

## Decisions

### Decision: 以平台挂载清单连接 webapp 路由和 KDDT/菜单

在 `webapp/isv/v620/careerloop` 下维护可机器检查的挂载清单或扩展现有 `careerloop-routes.json`，记录入口 key、标题、hash target、用户/管理员可见性、所需角色、源 IPD page、对应 `/cc001/*` WebAPI 和发布备注。这样后续实现可以同时服务静态页面、审阅文档和部署核查，避免只在散文文档中描述菜单。

替代方案是只在 `docs` 中写部署说明；该方案简单但难以被 `validate-routes.js` 或后续自动检查覆盖。

### Decision: 生产身份只来自 Cosmic 平台上下文

生产态 SHALL 通过 Cosmic 登录上下文或平台提供的用户/管理员身份 adapter 解析 `userId`、`adminId`、角色和组织范围。query/localStorage/manual input 只保留为开发态 fallback，并且 route map 必须标记 `environment: development` 或同等语义。用户私有 WebAPI 在无显式身份时 SHALL 不发起请求，管理员 API 在缺失 `ADMIN` 或平台管理员角色时 SHALL 显示 forbidden/identity-required 状态。

替代方案是继续用 query userId 作为所有环境入口；这会让生产数据隔离和审计不可验证。

### Decision: webapp 调用边界在前端清单和后端 adapter 双侧约束

前端 route/API map 负责声明调用路径、method、body、身份要求、fallback；后端或平台 adapter 负责把平台身份注入应用服务，并拒绝不匹配的 user/admin 请求。实现阶段可以先加入轻量 helper/adapter 和静态验证，不强行改造所有已迁移 WebAPI。

替代方案是只在后端增加身份 adapter；这无法让 KDDT/菜单审阅者判断哪些入口需要哪些权限。

### Decision: 部署验证先以文档和静态检查落地

本模块优先产出 `docs` 说明、route/API/mount map、Node 校验和 OpenSpec/Gradle 验证。真实客户环境中的菜单发布、租户权限、SSO/RBAC 联调记录为手工核查项或后续 change，而不是在本仓库伪造环境配置。

替代方案是尝试生成完整 KDDT 配置；当前仓库没有足够真实平台元数据，容易产生不可执行配置。

## Risks / Trade-offs

- [Risk] Cosmic 登录上下文 API 在目标环境中的具体取值方式仍未确认 → Mitigation: 设计 adapter 接口和部署核查清单，将未知点集中在单一边界，开发态 fallback 不进入生产约束。
- [Risk] route/API map 扩展后与现有静态入口不一致 → Mitigation: 更新 `validate-routes.js`，校验每个挂载入口引用的 route key、identity 和 WebAPI 契约。
- [Risk] 管理后台入口被错误暴露给普通用户 → Mitigation: 挂载清单必须声明 admin role，前端显示 forbidden，后端 admin WebAPI 继续执行身份校验。
- [Risk] 平台菜单配置无法在本地自动验证 → Mitigation: 提供部署核查文档和可审阅清单，本地验证覆盖文件结构、route map、OpenSpec 和 JDK 8 构建。

## Migration Plan

1. 增加或扩展 CareerLoop 平台挂载清单，覆盖 workbench、onboarding、today-action、resume、assessment、interview、assistant、messages、admin-console 等入口。
2. 实现身份解析边界：生产态从 Cosmic 平台上下文读取，开发态继续支持 query/localStorage/manual，但被明确标记且不会作为生产默认。
3. 更新 webapp 静态入口和验证脚本，使缺失身份、权限不足、后端不可用时有一致 fallback。
4. 增加平台挂载/部署验证说明，记录 KDDT/菜单挂载字段、权限、WebAPI 调用检查和回滚方式。
5. 更新迁移地图，运行 `openspec validate migrate-cosmic-platform-mounting --strict`、`openspec validate --all --strict`、webapp Node 检查和 JDK 8 Gradle 构建。

Rollback 策略：保留现有 `careerloop-routes.json` 的业务 route key，不删除已有静态入口；若生产挂载适配失败，可回退到仅开发态入口，同时禁用平台菜单发布项。

## Open Questions

- 目标客户环境中 Cosmic 登录上下文暴露的字段名称、获取 API 和管理员角色编码是什么？
- KDDT/菜单挂载最终由仓库文件、苍穹配置台还是交付脚本承载？
- 管理后台是否需要独立菜单入口，还是先作为 CareerLoop 工作台中的受限入口发布？
