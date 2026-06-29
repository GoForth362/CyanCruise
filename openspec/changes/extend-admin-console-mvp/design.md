## Context

CyanCruise 当前已经存在 `admin-console-governance` 规格、`AdminConsoleGovernanceWebApi`、`AdminConsoleGovernanceApplicationService`、`AdminConsoleGovernanceService` 和静态前端入口 `admin-console`。这些代码提供了管理端数据契约和部分治理规则，但前端入口仍偏占位，管理端接口的管理员身份校验也没有完全统一。

本项目是 Kingdee Cosmic 二开工程，管理端应复用现有 Cosmic 登录上下文、WebAPI、静态页面和 Gradle 构建，不引入新的前端框架、后端依赖或独立部署应用。

## Goals / Non-Goals

**Goals:**

- 在现有 CyanCruise 应用内实现可用的管理端 MVP。
- 管理端页面提供总览、用户管理、内容管理、题库审核、通知公告和操作记录视图。
- 管理端前端调用统一封装到 `admin-service.js`，页面不散落拼接接口细节。
- 所有 `/cc001/admin/*` 管理接口统一经过 `IdentityAwareCyanCruiseWebApiBoundary.requireAdmin()` 校验。
- 管理端无权限、缺少身份、后端不可用时展示清晰中文状态，不影响用户端页面。

**Non-Goals:**

- 不新建独立 Cosmic 应用。
- 不实现复杂多租户权限、字段级权限、审批流或机构管理员独立数据域。
- 不迁移 IPD 的 Spring Boot、JPA、Vue、Pinia 等运行时实现。
- 不新增外部依赖。

## Decisions

1. **同应用内扩展管理端**

   管理端继续挂在 `webapp/isv/v620/cyancruise` 和 `code/cloud01/v620-cc001-cloud01-app01` 下。这样能复用已有路由、身份解析、WebAPI 注册和构建链路。备选方案是新建独立应用，但当前 MVP 不需要独立部署和生命周期，拆分会增加配置和权限同步成本。

2. **后端身份校验作为最终边界**

   前端只负责隐藏入口和展示状态，所有管理端 WebAPI 都必须在服务调用前解析当前 Cosmic 身份并校验 `ADMIN` 等价角色。请求体中的 `adminId` 只作为显式身份一致性校验输入，不能作为权限来源。

3. **前端服务封装管理接口**

   新增 `assets/services/admin-service.js`，封装 `whoami`、dashboard、users、questions、content、broadcast 和 audit log 请求。页面渲染只消费服务返回结果，减少接口路径和 payload 结构在页面中的重复。

4. **优先使用现有内存/适配存储边界**

   本次 MVP 只完善管理端调用与交互，不引入新的数据模型或数据库迁移。后续如需接入正式 Cosmic 数据对象或 PostgreSQL 存储，应另开 change 记录映射和验证方式。

## Risks / Trade-offs

- 管理端部分数据仍来自现有存储适配或演示数据边界 → 在页面文案中避免承诺完整生产数据，后续按数据对象迁移逐步替换。
- 一次性收口所有管理接口权限可能暴露既有测试中依赖 `adminId` 的调用 → 同步调整测试为带管理员身份的边界注入。
- 静态前端不使用现代状态管理框架，复杂交互会更朴素 → MVP 采用简单标签页和表格，控制范围，后续再按需要拆组件。
