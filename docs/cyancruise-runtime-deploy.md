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
