# CyanCruise Runtime Deploy Notes

These notes capture the local Cosmic runtime used for the current portal validation.

## Runtime Paths

The Gradle template reads `systemProp.cosmic_home` from `gradle.properties` and its `deployJar` task copies JARs to:

```text
F:\kingdee\cosmic\home\mservice-cosmic\lib\cus
```

The currently validated `8080` portal is started from:

```text
F:\kingdee\ENV\start-cosmic.bat
F:\kingdee\ENV\mservice-cosmic\lib\cus
F:\kingdee\ENV\static-file-service\isv\v620\cyancruise
```

When validating `http://10.0.0.8:8080/ierp/isv/v620/cyancruise/index.htm#workbench`, deploy the three CyanCruise JARs and the static webapp resources into the `F:\kingdee\ENV` runtime directories above. The static files validated in the local portal are:

```text
F:\kingdee\ENV\static-file-service\isv\v620\cyancruise\index.htm
F:\kingdee\ENV\static-file-service\isv\v620\cyancruise\index.html
F:\kingdee\ENV\static-file-service\isv\v620\cyancruise\assets\app.js
```

The ENV startup script must include the CyanCruise cus packages in `CUSLIBS`, otherwise `kd-appstore-download-1.0.jar` will reset `lib\cus` to the packages listed by the script:

```bat
set CUSLIBS=kd-studio-login,v620-cc001-base-common,v620-cc001-base-helper,v620-cc001-cloud01-app01
```

In the current 8.0.4 developer-tool runtime, the observed JVM classpath expands `lib\biz` but not `lib\cus`. For this local validation mode, also place the same package ZIPs under:

```text
F:\kingdee\ENV\apppackage-cosmic\biz
```

and prepend the package names to `BIZLIBS`:

```bat
set BIZLIBS=v620-cc001-base-common,v620-cc001-base-helper,v620-cc001-cloud01-app01,<existing biz packages>
```

## Identity Flags

Start the 8080 Cosmic process with these JVM system properties:

```text
-Dcc001.identity.adapter.enabled=true
-Dcc001.identity.login.provider.enabled=true
```

`RequestContextCosmicLoginContextBridge` is the default bridge when the login provider is enabled and `cc001.identity.login.provider.bridgeClass` is not set. A custom bridge class may still be supplied with:

```text
-Dcc001.identity.login.provider.bridgeClass=<fully.qualified.ClassName>
```

## Custom Web API Entry

Cosmic does not expose `@ApiController` classes directly as `POST /ierp/cc001/*` servlet routes in the local 8.0.4 runtime. The runtime-supported custom Web API model is `kd.bos.bill.IBillWebApiPlugin#doCustomService(Map)`.

CyanCruise provides:

```text
v620.cc001.cloud01.app01.webapi.CyanCruiseCustomWebApiPlugin
```

Register it in the Cosmic OpenAPI/custom service configuration as a custom Java-plugin API:

```text
Third-party app: cc001 / CyanCruise
API code: cc001/cyancruise/route
Class: v620.cc001.cloud01.app01.webapi.CyanCruiseCustomWebApiPlugin
Method: route
Method: POST
URL: /v2/v620/v620_cc001/cc001/cyancruise/route
```

Authorize the `cc001` third-party application for this API, then call:

```text
POST {cosmicUrl}/kapi/v2/v620/v620_cc001/cc001/cyancruise/route?access_token={access_token}
Content-Type: application/json

{
  "path": "/cc001/identity/current",
  "body": {}
}
```

The static webapp can call this mode by adding these query parameters once:

```text
apiMode=kapi&access_token=<token>
```

This KAPI URL shape is for explicit debugging only:

```text
http://10.0.0.8:8080/ierp/isv/v620/cyancruise/index.htm?apiMode=kapi&access_token=<new-token>#workbench
```

正式自建应用菜单不要把 `access_token` 放在 URL 中。菜单应使用固定的 server-managed 入口：

```text
/ierp/isv/v620/cyancruise/index.htm?apiMode=server#workbench
```

如果现有菜单已经是下面这种形式，也可以继续使用；未显式指定 `apiMode` 时，前端默认会走 server-managed 模式：

```text
/ierp/isv/v620/cyancruise/index.html?ccRoute=workbench
```

`apiMode=server` 时，前端默认调用已注册的 `/ierp/kapi/v2/v620/v620_cc001/cc001/cyancruise/route`，请求体是 `{path, body}`。
不要把前端请求配置成 `/ierp/cc001/cyancruise/server/route`；该地址不是浏览器可直接访问的 KAPI v2 路径，会返回 404。
如果后续在苍穹 OpenAPI 中单独注册并启用了 `cc001/cyancruise/server/route`，可以通过 URL 参数 `serverApiCode=cc001/cyancruise/server/route` 切换到独立 server-managed route。
后端会使用 `systemProp.cc001.kapi.token.*` 配置换取并缓存 KAPI token，然后由后端带 token 调用
`cc001/cyancruise/route`。浏览器、应用菜单、localStorage 都不应保存 KAPI token 或 client secret。

如果本地运行态没有直接暴露 `POST /ierp/cc001/*`，自建应用菜单应使用下面这种外部链接。
这是本地验证时推荐的“自定义 WebAPI 路由”入口：

```text
/ierp/isv/v620/cyancruise/index.htm?apiMode=server#workbench
```

`access_token` 只是 KAPI 调用凭据，不能当作 CyanCruise 用户标识使用。当前用户仍然必须由后端
Cosmic `RequestContext` 桥接解析；如果页面提示 `IDENTITY_REQUIRED`，先检查
`cc001.identity.adapter.enabled`、`cc001.identity.login.provider.enabled`、自定义 API 注册和第三方应用授权，
不要为了绕过问题去改用户数据。

The webapp defaults to server-managed mode. In this mode it posts to the registered KAPI v2 custom WebAPI `cc001/cyancruise/route` and does not require `access_token` in the menu URL. It also keeps `cloudId=v620`, `appNumber=v620_cc001`, `apiCode=cc001/cyancruise/route`, and `kapiRouteVersion=v2` for explicit `apiMode=kapi` debugging. These can be overridden in the URL if a tenant uses different OpenAPI metadata. Use `kapiRouteVersion=legacy` only for the older `/kapi/app/{appId}/{serviceName}/` route. The values are cached in browser localStorage only for explicit `apiMode=kapi` debugging. Direct `/ierp/cc001/*` mode remains for contract display and non-Cosmic harnesses.

For the local validation API details, the successful configuration disabled "third-party application authorization" on the API itself and instead relied on the configured third-party application `CyanCruise` access token plus business object and permission item authorization. If the API keeps rejecting a valid token, recheck the OpenAPI API detail page, the bound business object, and the permission item before changing CyanCruise code.

## Accepted Local Identity And Storage

The validated Cosmic RequestContext bridge resolves the current platform user as:

```text
userId/adminId: 2477190919195983874
source: cosmic-platform-context
```

When the datamodel/database storage is unavailable in the local 8080 runtime, CyanCruise falls back to local file storage under the running Cosmic process. The current accepted user data is stored at:

```text
F:\kingdee\ENV\mservice-cosmic\bin\filestorage\career-profile\2477190919195983874\
```

Expected files after saving onboarding/profile data include `snapshot.ser` and `profile.ser`. This path is an observed runtime artifact for local validation only; business code must continue to read runtime paths from configuration and must not hard-code this local directory.

## Admin Governance Storage

管理端的用户治理、题库审核、内容管理、通知广播和审计日志可以使用共享 PostgreSQL 业务存储。生产或联调环境需要显式配置：

```properties
cc001.storage.backend=postgresql
cc001.storage.postgresql.url=jdbc:postgresql://10.0.0.8:5432/cyancruise
cc001.storage.postgresql.username=cyancruise_app
cc001.storage.postgresql.password=<password>
cc001.storage.postgresql.schema=public
cc001.storage.postgresql.initialize=false
```

首次使用前执行：

```powershell
psql -h 10.0.0.8 -U postgres -d cyancruise -f datamodel/postgresql-admin-governance-storage.sql
```

`cc001.storage.postgresql.initialize=true` 仅建议本地开发或一次性初始化时使用；生产环境建议由数据库脚本明确建表和授权。启用 PostgreSQL 后，管理端不再使用进程内存保存用户、题库、内容和审计状态；管理员禁用用户后，用户端 `/cc001/*` 业务接口会在统一路由入口被拒绝。

## 当前存储决策

当前 CyanCruise 处于功能完善阶段，PostgreSQL 是全部用户端和管理端业务数据的唯一主存储。首页、用户画像、简历、测评、面试、路径规划、消息和管理治理功能都应优先完成 PostgreSQL 的保存、读取、权限和页面闭环。

每日路线任务复用“今日任务”业务对象。PostgreSQL 验收环境首次启用前执行：

```powershell
psql -h 10.0.0.8 -U postgres -d cyancruise -f datamodel/postgresql-career-daily-task.sql
```

该表保存任务日期、来源路线动作和完成状态，使同一天重复打开时保持稳定，并在次日优先顺延未完成事项。

就业与升学规划使用独立数据表。生产或验收环境还需执行：

```powershell
psql -h 10.0.0.8 -U postgres -d cyancruise -f datamodel/postgresql-study-center-planning.sql
```

升学规划按方向配置三套独立规划服务，属性前缀分别为 `cc001.agent.platform.study.postgraduate`、`cc001.agent.platform.study.recommendation` 和 `cc001.agent.platform.study.abroad`。保研规划与就业规划、考研规划使用相同的智能体直连方式，只需配置已发布智能体的 `agentNumber`；切换当前路线只改变首页读取哪套规划，不会删除另一套规划和每日任务。

考研规划示例：

```properties
cc001.agent.platform.study.postgraduate.enabled=true
cc001.agent.platform.study.postgraduate.agentNumber=<已发布的考研规划智能体编码>
```

保研规划预留接口与考研使用相同的请求、解析、持久化和今日行动链路，只需配置独立智能体编码：

```properties
cc001.agent.platform.study.recommendation.enabled=true
cc001.agent.platform.study.recommendation.agentNumber=<已发布的保研规划智能体编码>
```

保研方向固定调用 `agentNumber` 对应的智能体，不读取 `cc001.agent.platform.study.recommendation.taskFlowCode`。智能体最终回复仍须满足后端共享规划 JSON 契约，校验通过后才会持久化。

升学中心上传的规划资料使用现有 Cosmic 文件服务保存文件，并在 `cc_study_center_material` 中保存当前用户的文件引用、解析状态和受限正文。执行更新后的 `datamodel/postgresql-study-center-planning.sql` 后，服务重启仍可读取资料；考研与保研资料按“用户 + 升学方向”隔离，只有正文解析状态为 `OK` 的当前方向资料会进入对应智能体请求。规划和今日行动同样按“用户 + 升学方向”持久化，切换方向不会覆盖另一方向的数据。

本地 8080 运行时保持 `cc001.storage.backend=postgresql`。不要启用 `cc001.storage.backend=cosmic`、`cc001.storage.cosmic.modules` 或 `cc001.storage.cosmic.clientClass`，也不进行 PostgreSQL 与金蝶业务对象双写。已有的 Cosmic storage adapter 仅作为后续迁移准备保留，不参与当前功能验收。

## 延后：Cosmic Business Object Storage

金蝶业务对象存储接入的本质是：CyanCruise 后端仍通过现有 WebAPI 接收前端请求，然后在 storage adapter 层调用当前苍穹运行时的数据服务，把业务状态写入 `v620_cc_*` 业务对象。前端不会直接连接数据库，也不会直接操作业务对象。

当前环境写到哪里，取决于这套 CyanCruise Java 包运行在哪个苍穹环境里：

- 本地苍穹运行时：写入本地苍穹环境配置的数据源。
- 客户或云端苍穹运行时：写入该租户/环境配置的数据源。
- 代码仓库不保存数据库地址、密码、access token 或 client secret。

可使用下面的配置启用 Cosmic storage：

```properties
cc001.storage.backend=cosmic
cc001.storage.cosmic.modules=profile,resume,resume-diagnosis,assessment,interview,career-plan,assistant
cc001.storage.cosmic.clientClass=<tenant-specific CosmicBusinessObjectClient implementation>
```

当前代码侧已具备 `profile`、`resume`、`resume-diagnosis`、`assessment`、`interview`、`career-plan`、`assistant` 的 Cosmic storage 切换入口。`notification`、`admin-governance`、`further-study` 尚未默认切换到 Cosmic storage；这些模块在完成对应 adapter 和真实环境验收前继续使用当前 PostgreSQL 或内存后备。

当前本地金蝶数据表详情页显示自定义字段标识为 `fk_v620_*`，例如 `fk_v620_userid`、`fk_v620_targetrole`。运行时字段映射已按该实际表字段前缀写入；如果后续租户环境保存为无 `fk_` 前缀的 `v620_*`，只需调整 `CyanCruiseBusinessModelMapping` 的平台字段前缀转换。

如果需要回退，移除 `cc001.storage.backend=cosmic` 或从 `cc001.storage.cosmic.modules` 中移除对应模块，重启当前 8080 进程即可。回退后必须确认哪一端数据是权威数据源，避免同一模块长期双写。

## Restart Requirement

Copying JARs into `lib\cus` is not enough for an already running 8080 process. Restart the process that owns port `8080`, then verify:

```powershell
Invoke-WebRequest -Uri 'http://10.0.0.8:8080/ierp/cc001/identity/current' -Method Post -ContentType 'application/json' -Body '{}'
```

Before restart, this endpoint may still return `404` because the old JVM has not loaded the new WebAPI class.

For the Cosmic custom Web API path, verify with the registered service name and a valid access token:

```powershell
Invoke-WebRequest -Uri 'http://10.0.0.8:8080/ierp/kapi/v2/v620/v620_cc001/cc001/cyancruise/route?access_token=<token>' -Method Post -ContentType 'application/json' -Body '{"path":"/cc001/identity/current","body":{}}'
```
