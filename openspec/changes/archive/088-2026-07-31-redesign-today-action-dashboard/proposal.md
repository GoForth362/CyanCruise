## Why

当前“今日行动”把每日任务和本周任务作为两张等权重的长清单并排展示，任务文本密集、进度不明显，在部分宽度下还会产生横向滚动。用户难以快速判断今天还剩几件事、当前路线阶段以及本周任务与今日优先级的关系。

## What Changes

- 将页面重构为“今日进度概览—今日优先任务—本周节奏—路线来源”的行动面板。
- 使用真实任务完成状态展示今日完成数、剩余数、顺延任务和当前阶段。
- 强化今日任务的视觉优先级，将本周动作与交付物作为辅助节奏展示。
- 统一使用青色色阶、状态标记和更易扫读的任务卡片。
- 消除页面横向滚动，适配窄屏、长文本、键盘焦点和减少动态效果偏好。
- 保留就业/升学路线隔离、任务勾选、持久化与未完成任务顺延逻辑。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `webapp-cyancruise-pages`: 优化今日行动页的真实进度、任务层级和响应式体验。

## Impact

- 前端运行时：`webapp/isv/v620/cyancruise/assets/app-runtime.js`
- 页面样式：`webapp/isv/v620/cyancruise/assets/styles.css`
- 静态契约：`webapp/isv/v620/cyancruise/validate-routes.js`
- 前端资源版本：`webapp/isv/v620/cyancruise/assets/app.js`、`webapp/isv/v620/cyancruise/index.html`
- 不修改每日计划接口、任务 ID、完成状态存储或路线规划数据结构。
