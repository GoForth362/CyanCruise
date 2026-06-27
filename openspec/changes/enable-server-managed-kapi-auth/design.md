## Context

CyanCruise 静态 webapp 同时支持同域 `/cc001/*` WebAPI 和 KAPI/OpenAPI 代理模式。历史联调中，页面会读取 `apiMode`、`access_token` 等参数并缓存到 localStorage。自建应用从苍穹菜单直接进入时，若沿用 KAPI 模式但没有 token，会触发授权错误。

用户希望把 AccessToken 认证密钥放到后端，由后端自动获取 token。该能力有两个边界：密钥不能进入前端或业务源码；固定第三方应用 token 只是调用凭据，不代表当前浏览器登录用户身份。

## Goals / Non-Goals

**Goals:**
- 提供 JDK 1.8 兼容的 KAPI token 配置、获取和缓存服务。
- 让密钥来自 system properties 或环境配置，不进入业务源码、前端资源、URL 或 localStorage。
- 调整 webapp 默认 API 模式：自建应用入口默认同域直连，显式 `apiMode=kapi` 才走 OpenAPI。
- 保留 KAPI 联调能力，并允许清理历史 KAPI 缓存。

**Non-Goals:**
- 不把固定代理 token 当作当前登录用户。
- 不在前端实现 getToken，不暴露 `client_secret`。
- 不新增 Spring、OAuth 客户端库或破坏 Cosmic/KDDT 模板约束的新依赖。
- 不改变现有 `/cc001/*` DTO 契约。

## Decisions

- 后端通过 `KapiAccessTokenService` 读取 `cc001.kapi.token.*` 配置，调用 `/ierp/kapi/oauth2/getToken` 获取 OpenAPI 调用凭据。
- token 结果在服务端缓存，缓存日志和错误消息必须脱敏，不输出密钥、token 或敏感用户字段。
- webapp 默认 `apiMode` 使用 `direct`，仅当 URL 显式设置 `apiMode=kapi` 且存在可用 token 时走 KAPI。
- `apiMode=reset` 用于清理旧 localStorage 中的 KAPI 参数，便于从苍穹菜单恢复自建应用默认行为。

## Verification

- `KapiAccessTokenServiceTest`
- `node --check webapp\isv\v620\cyancruise\assets\app.js`
- `node webapp\isv\v620\cyancruise\validate-routes.js`
- `openspec validate enable-server-managed-kapi-auth --strict`
