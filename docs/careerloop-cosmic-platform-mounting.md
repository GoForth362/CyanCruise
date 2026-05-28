# CareerLoop 苍穹平台挂载说明

本文档记录 `migrate-cosmic-platform-mounting` 的部署契约。目标是把 `webapp/isv/v620/careerloop` 中已迁移的 CareerLoop 入口挂载到 Kingdee Cosmic webapp/KDDT/菜单体系，并明确生产身份、开发 fallback、WebAPI 调用和验证边界。

## 资源路径

| 项目 | 值 |
| --- | --- |
| webapp 根目录 | `webapp/isv/v620/careerloop/` |
| 入口文件 | `index.html` |
| route/API map | `careerloop-routes.json` |
| 静态脚本 | `assets/app.js` |
| 本地验证脚本 | `validate-routes.js` |
| 平台挂载清单 | `careerloop-routes.json.platformMounts` |

## 身份模式

| 模式 | 来源 | 使用范围 | 规则 |
| --- | --- | --- | --- |
| production | Cosmic 登录上下文或平台 adapter | 生产菜单、KDDT、用户/管理员入口 | SHALL 仅从平台上下文解析 `userId`、`adminId`、角色和组织范围。缺失身份时不调用用户归属或管理员 WebAPI。 |
| development | `?identityMode=development&userId=`、`localStorage.careerloop.userId`、页面输入 | 本地开发、静态验证、接口联调 | SHALL 明确标记为非生产 fallback，不得作为生产身份发布。 |

生产态可由平台在页面初始化前注入 `window.__CAREERLOOP_COSMIC_CONTEXT__`、`window.__COSMIC_CONTEXT__` 或 `window.cosmicContext`。当前静态脚本读取 `userId`、`personId`、`operatorId` 或 `uid` 作为用户候选，并读取 `adminId`、`roles` 作为管理员候选；最终字段名需在客户 Cosmic 租户联调时确认。

## 菜单/KDDT 挂载

挂载项以 `careerloop-routes.json.platformMounts` 为准。每个挂载项必须包含：

- `mountKey`：平台挂载唯一键。
- `routeKey`：必须引用 `careerloop-routes.json.routes[*].key`。
- `title`：菜单或入口标题。
- `target`：如 `index.html#workbench`。
- `audience`：`careerloop-user` 或 `careerloop-admin`。
- `requiredRole`：用户入口使用 `COSMIC_AUTHENTICATED_USER`，管理入口使用 `ADMIN`。
- `publishability`：`user-facing`、`entry-only` 或 `admin-only`。
- `identityMode`：生产平台身份模式。
- `fallback`：身份缺失、权限不足或 WebAPI 不可用时的页面状态。
- `deploymentNotes`：发布备注。

建议先发布 `careerloop-workbench` 作为用户主入口；`careerloop-admin-console` 只能挂载到管理员菜单或等效 KDDT 管理入口，不得作为普通用户菜单暴露。

## WebAPI 调用边界

所有 `/cc001/*` 调用继续以 `careerloop-routes.json.routes[*].webApis` 为契约来源。用户归属接口必须在解析到用户身份后调用；管理员接口必须在解析到管理员身份和 `ADMIN` 或平台管理员权限后调用。缺失身份时，页面显示 identity-required 或 forbidden 状态，不使用硬编码用户、旧 JWT、旧 admin token 或上一次 localStorage 作为生产身份。

## 本地自动验证

```powershell
node webapp\isv\v620\careerloop\validate-routes.js
node --check webapp\isv\v620\careerloop\assets\app.js
openspec validate migrate-cosmic-platform-mounting --strict
openspec validate --all --strict
$env:JAVA_HOME = 'F:\kingdee\ENV\jdk'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat clean build
```

`validate-routes.js` 会检查 route key、必需 WebAPI、平台身份元数据、IPD 来源证据和 platform mount 元数据。

## 租户手工核查

| 核查项 | 预期 |
| --- | --- |
| webapp 资源发布 | `index.html`、`assets/*`、`careerloop-routes.json` 可由 Cosmic webapp 资源访问。 |
| 用户菜单 | 指向 `index.html#workbench`，仅面向已登录用户。 |
| 管理菜单 | 指向 `index.html#admin-console`，仅面向 `ADMIN` 或平台管理员角色。 |
| 登录上下文 | 页面可从平台上下文解析当前用户；缺失时显示 identity-required。 |
| 管理权限 | 普通用户打开 admin route 时显示 forbidden/identity-required，不调用 `/cc001/admin/*`。 |
| WebAPI | `/cc001/career-profile/*`、`/cc001/career-agent/*`、`/cc001/admin/*` 等路径在租户网关可达，并由后端执行身份/权限校验。 |
| 回滚 | 禁用对应菜单/KDDT 挂载即可回退；保留 webapp 静态资源和 route map 不影响后端业务接口。 |

## 暂不迁移

本挂载不直接迁移 IPD uni-app/Vue router/tabBar、Pinia/store、Vite/uView、axios/JWT 登录、微信登录、Spring Boot auth、JPA/Flyway、生产 SSO/RBAC 规则、客户环境 KDDT 发布脚本、真实 Cosmic 文件服务 adapter、最终 datamodel/权限接入和小程序原生能力。
