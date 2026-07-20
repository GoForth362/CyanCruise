## Why

当前“画像补全”页面把深度画像状态、生成操作和测评题组平铺在一个过于松散的长页面中：上半部分留白过多，题组则采用近似表格的横向排列，标题、说明、状态与操作挤在同一行。用户难以快速判断画像完成度、下一步该做什么，也无法在不同屏幕宽度下稳定浏览。

## What Changes

- 将画像补全首页重构为“画像概览—下一步建议—测评题组”的清晰信息层级。
- 使用统一但有层次的青色色阶，强化深度画像状态、测评进度和主要操作。
- 将横向题组行改为响应式卡片网格，分别展示题组说明、预计时长、题量、完成状态与操作。
- 保留现有深度画像生成、详情与历史、开始测评、查看上次结果等业务入口和数据契约。
- 补充窄屏、键盘焦点和减少动态效果偏好下的展示约束。

## Capabilities

### Modified Capabilities

- `career-assessment`: 优化画像补全首页的深度画像概览、真实完成进度和测评题组选择体验。

### New Capabilities

无。

## Impact

- 前端运行时：`webapp/isv/v620/cyancruise/assets/app-runtime.js`
- 页面样式：`webapp/isv/v620/cyancruise/assets/app.css`
- 静态契约校验：`webapp/isv/v620/cyancruise/validate-routes.js`
- 前端资源版本：`webapp/isv/v620/cyancruise/assets/app.js`、`webapp/isv/v620/cyancruise/index.html`
- 不修改后端接口、测评答案、测评结果或深度画像的数据存储结构。
