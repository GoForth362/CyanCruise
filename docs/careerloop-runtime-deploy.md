# CareerLoop Runtime Deploy Notes

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
F:\kingdee\ENV\static-file-service\isv\v620\careerloop
```

When validating `http://10.0.0.8:8080/ierp/isv/v620/careerloop/index.htm#workbench`, deploy the three CyanCruise JARs and the static webapp resources into the `F:\kingdee\ENV` runtime directories above.

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
v620.cc001.cloud01.app01.webapi.CareerLoopCustomWebApiPlugin
```

Register it in the Cosmic OpenAPI/custom service configuration with a service name such as `careerloop`, under app id `cc001`. The HTTP caller should then use:

```text
POST {cosmicUrl}/kapi/app/cc001/careerloop/?access_token={access_token}
Content-Type: application/json

{
  "path": "/cc001/identity/current",
  "body": {}
}
```

The static webapp can call this mode by adding these query parameters once:

```text
apiMode=kapi&appId=cc001&serviceName=careerloop&access_token=<token>
```

The values are cached in browser localStorage for later page loads. Without `apiMode=kapi`, the page keeps the older direct `/ierp/cc001/*` contract mode for contract display and non-Cosmic harnesses.

## Restart Requirement

Copying JARs into `lib\cus` is not enough for an already running 8080 process. Restart the process that owns port `8080`, then verify:

```powershell
Invoke-WebRequest -Uri 'http://10.0.0.8:8080/ierp/cc001/identity/current' -Method Post -ContentType 'application/json' -Body '{}'
```

Before restart, this endpoint may still return `404` because the old JVM has not loaded the new WebAPI class.

For the Cosmic custom Web API path, verify with the registered service name and a valid access token:

```powershell
Invoke-WebRequest -Uri 'http://10.0.0.8:8080/ierp/kapi/app/cc001/careerloop/?access_token=<token>' -Method Post -ContentType 'application/json' -Body '{"path":"/cc001/identity/current","body":{}}'
```
