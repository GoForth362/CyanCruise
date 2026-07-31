## Why

CyanCruise 功能页加载时，浏览器会在运行脚本接管页面前短暂绘制旧 CareerLoop 入口壳，导致“求职主循环”和旧状态卡片闪现。该历史展示会干扰当前平台功能页体验，也不符合当前项目命名与用户文案约定。

## What Changes

- 清理静态入口中的“求职主循环”“主循环状态”等历史用户可见文案。
- 让仅供调试使用的入口头部、导航和状态区在首屏默认隐藏，只在显式 `ccDebug=1` 模式下恢复。
- 保留既有 DOM 标识、身份调试入口、hash/查询参数路由、页面挂载点和 WebAPI 加载流程，避免影响当前功能。
- 将仍需展示的状态标题改为普通用户可理解的 CyanCruise 业务文案。
- 增加静态检查，防止历史文案和首屏调试壳再次进入默认用户页面。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `webapp-cyancruise-pages`: 补充功能页首屏不得闪现历史入口壳、默认模式隐藏调试区域且现有路由功能保持可用的要求。

## Impact

- 影响 `webapp/isv/v620/cyancruise/index.html`、`assets/app-runtime.js`、静态验证脚本及相关 OpenSpec 文档。
- 不改变 `cyancruise-routes.json`、`/cc001/*` WebAPI、身份解析契约、后端代码和依赖。
- 需要重新部署 CyanCruise 静态资源并更新缓存版本号。
