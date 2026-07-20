## Why

当前简历制作页把创建表单、文件上传、正文粘贴和简历记录以等权面板并排展示，缺少资产概况和明确步骤；记录中的文件、岗位、诊断与更新时间也以连续文本呈现，用户难以快速判断下一步操作。需要在不改变现有接口与业务闭环的前提下，将页面重构为更清晰、专业且适配窄屏的简历工作台。

## What Changes

- 为 `resume` 路由增加独立页面状态和真实简历资产总览。
- 将简历创建区整理为“填写基础信息、上传 PDF、确认解析内容、创建记录”的清晰流程。
- 将已有简历重构为信息分组明确的记录卡，突出目标岗位、文件状态、诊断状态、更新时间及下一步操作。
- 使用与现有产品一致的青色视觉体系，并补充窄屏、长文本、键盘焦点和减少动态效果适配。
- 保留简历创建、PDF 上传与正文提取、预览、诊断、删除、局部错误和调试契约。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `webapp-cyancruise-pages`: 明确简历工作台的总览层级、创建流程、记录卡状态表达、响应式布局和交互保护要求。

## Impact

- 主要修改 `webapp/isv/v620/cyancruise/assets/app-runtime.js`、`assets/styles.css`、`assets/app.js`、`index.html` 和 `validate-routes.js`。
- 不新增依赖，不改变 `/cc001/resume/*` 与文件服务接口，不修改 PostgreSQL、Cosmic 数据模型或 JDK 代码。
- 需要更新静态资源版本并完成桌面、窄屏和现有功能契约验证。
