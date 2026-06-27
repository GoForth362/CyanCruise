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

The current accepted local portal URL shape is:

```text
http://10.0.0.8:8080/ierp/isv/v620/cyancruise/index.htm?apiMode=kapi&access_token=<new-token>#workbench
```

The webapp defaults to `cloudId=v620`, `appNumber=v620_cc001`, `apiCode=cc001/cyancruise/route`, and `kapiRouteVersion=v2`. These can be overridden in the URL if a tenant uses different OpenAPI metadata. Use `kapiRouteVersion=legacy` only for the older `/kapi/app/{appId}/{serviceName}/` route. The values are cached in browser localStorage for later page loads. Without `apiMode=kapi`, the page keeps the older direct `/ierp/cc001/*` contract mode for contract display and non-Cosmic harnesses.

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
