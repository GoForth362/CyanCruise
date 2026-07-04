## Why

当前管理后台的“内容审核”只具备列表、置顶和隐藏的局部能力，管理员无法在后台新增或编辑首页/资源页内容；用户端就业资源也仍主要来自内置种子数据，没有真正消费管理员维护的内容。

本次变更把该能力收束为“内容管理”：管理员可以新增、编辑、隐藏、置顶和删除内容，并让未隐藏内容进入用户端首页/资源页资源流。

## What Changes

- 管理后台将“内容审核”调整为“内容管理”，提供内容表单和内容列表。
- 管理员可以维护文章、视频、资源入口等内容字段：标题、摘要、分类、外部链接、封面链接、展示状态和置顶状态。
- 管理后台新增内容保存和删除的前端服务、WebAPI 路由和自定义 OpenAPI 路由映射。
- 用户端就业资源接口读取后台内容，隐藏内容不出现在用户端，置顶内容在后台内容源内优先排序。
- 后台内容继续保留默认种子资源兜底，避免尚未配置内容时用户端资源页变空。
- 本次不新增外部依赖，不引入 IPD Spring Boot/JPA/Vue 运行时。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `admin-console-governance`: 内容治理从只读/状态切换扩展为可新增、编辑、隐藏、置顶、删除的完整管理闭环。
- `employment-insights-resources`: 用户端资源 feed 需要消费管理员维护的可见内容，并保留内置资源兜底。

## Impact

- 后端：
  - `code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/webapi/admin/AdminConsoleGovernanceWebApi.java`
  - `code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/webapi/CyanCruiseCustomWebApiPlugin.java`
  - `code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/mservice/application/EmploymentInsightsResourcesApplicationService.java`
  - `code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/mservice/storage/impl/*`
- 前端：
  - `webapp/isv/v620/cyancruise/assets/pages/admin-console.js`
  - `webapp/isv/v620/cyancruise/assets/services/admin-service.js`
  - `webapp/isv/v620/cyancruise/assets/api.js`
  - `webapp/isv/v620/cyancruise/assets/app-runtime.js`
  - `webapp/isv/v620/cyancruise/cyancruise-routes.json`
  - `webapp/isv/v620/cyancruise/README.md`
- 验证：
  - OpenSpec strict 校验
  - 管理后台和就业资源相关单元测试
  - webapp route 校验
  - JDK 8 Gradle 构建
