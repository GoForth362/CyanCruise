## Why

升学中心和就业中心已经具备规划、洞察、资料与资源能力，但当前页面仍以连续白色区块和密集表单为主，核心状态、当前目标与下一步行动缺少清晰层级。两个中心的视觉表达也不够统一，用户难以快速理解“我现在在哪里、接下来做什么、去哪里继续”。

## What Changes

- 将升学中心和就业中心统一为“路径概览—规划推进—能力/资料入口—资讯资源”的页面层级。
- 使用统一的青色色阶、状态标记和卡片语言，同时保留升学与就业各自的业务文案。
- 将升学方向选择与目标院校整理为紧凑的路径控制区，保留保存、完整规划和生成规划操作。
- 将两个中心的路线摘要和阶段步骤重构为更清晰的响应式卡片布局。
- 优化就业洞察、简历/面试入口、升学资料与资源区块的视觉层级，不混用两类中心的数据。
- 增加窄屏、键盘焦点和减少动态效果偏好适配。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `webapp-cyancruise-pages`: 统一两个中心的页面层级、真实状态呈现和响应式交互体验。

## Impact

- 前端运行时：`webapp/isv/v620/cyancruise/assets/app-runtime.js`
- 页面样式：`webapp/isv/v620/cyancruise/assets/styles.css`
- 静态契约：`webapp/isv/v620/cyancruise/validate-routes.js`
- 前端资源版本：`webapp/isv/v620/cyancruise/assets/app.js`、`webapp/isv/v620/cyancruise/index.html`
- 不修改升学/就业接口、规划生成策略、资料存储、题组或用户数据结构。
