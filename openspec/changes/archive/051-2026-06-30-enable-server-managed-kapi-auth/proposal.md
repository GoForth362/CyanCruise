## Why

当前 CyanCruise 自建应用页面曾依赖 `apiMode=kapi&access_token=...` 方式联调 KAPI。用户从苍穹菜单进入时，如果没有手工带入 token，容易出现“该接口需要第三方应用授权”或无法识别登录身份的问题。密钥也不应暴露在前端 URL、localStorage 或页面脚本中。

本 change 需要让 KAPI 认证能力由服务端配置化托管，并让自建应用默认优先使用同域 Cosmic 登录上下文，减少手工 token 操作，避免把固定代理 token 误当作当前登录人。

## What Changes

- 新增后端 KAPI AccessToken 配置、请求和缓存能力，支持从 system properties 读取 `client_id`、密钥、代理用户、账套和 token 接口地址。
- 前端 API 模式调整为自建应用默认同域直连，只有显式 `apiMode=kapi` 时才走 OpenAPI/KAPI。
- 前端提供 `apiMode=reset`/`direct` 清理历史 KAPI localStorage 状态，避免旧联调参数污染自建应用入口。
- 文档补充后端托管 token 的配置边界：密钥不得硬编码在业务代码或前端资源中，固定代理 token 不得作为当前登录用户身份。
- 不新增第三方依赖，保持 JDK 1.8 兼容。

## Impact

- 前端默认访问路径更适合苍穹自建应用菜单。
- 服务端统一管理 KAPI token 获取和缓存，降低人工配置外链的风险。
- 当前登录用户身份仍来自 Cosmic 登录上下文或批准的平台上下文桥，不由 KAPI token 推断。
