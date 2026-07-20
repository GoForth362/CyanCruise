## Why

当前路径规划页把规划依据、每日/本周任务、总进度、阶段导航和全部阶段卡片连续铺开，信息层级接近且页面过宽，用户难以快速理解当前目标、所处阶段与下一步行动。升学和就业路线虽然语义不同，也缺少统一且清晰的规划工作台体验。

## What Changes

- 将路径规划页重构为“路线总览—总进度与里程碑—执行计划—阶段工作区”的清晰层级。
- 使用真实目标、当前阶段、任务进度和阶段数量生成路线总览。
- 将规划依据从超长列表改为紧凑、可扫读的依据卡片。
- 优化阶段导航和阶段卡片，突出当前阶段、完成数量与时间范围。
- 保留每日、本周、阶段任务勾选、周期选择、刷新规划和已开始阶段保护逻辑。
- 消除横向滚动，适配窄屏、长文本、键盘焦点和减少动态效果偏好。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `webapp-cyancruise-pages`: 优化路线感知的路径规划页面层级、真实进度和响应式体验。

## Impact

- 前端运行时：`webapp/isv/v620/cyancruise/assets/app-runtime.js`
- 页面样式：`webapp/isv/v620/cyancruise/assets/styles.css`
- 静态契约：`webapp/isv/v620/cyancruise/validate-routes.js`
- 前端资源版本：`webapp/isv/v620/cyancruise/assets/app.js`、`webapp/isv/v620/cyancruise/index.html`
- 不修改规划接口、阶段状态、任务 ID、规划刷新策略或数据结构。
