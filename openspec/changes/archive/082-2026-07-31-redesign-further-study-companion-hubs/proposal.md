## Why

考研陪伴、保研陪伴和留学陪伴首页目前重复展示同一组阶段名称与大按钮，缺少重点、下一步引导和三条深造路径各自的识别度。需要在不改变功能和路由的前提下，重构为更清晰、更有陪伴感的深造行动首页。

## What Changes

- 删除首页重复的“流程标签 + 功能按钮”两层入口，合并为一组可解释、可点击的阶段行动卡片。
- 为考研、保研、留学设计统一结构和青色视觉体系的陪伴首页，通过不同深浅与冷暖倾向突出各自目标、阶段节奏和关键产出。
- 每张入口卡片展示阶段编号、用户任务、功能名称、用途说明和明确的进入反馈。
- 增加桌面端层次、悬停反馈、键盘焦点和窄屏单列布局。
- 保留现有子页面、表单、后端接口和业务数据，不调整功能语义。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `postgraduate-exam-companion`: 考研首页改为以择校、计划、错题和复试为主线的阶段行动入口。
- `postgraduate-recommendation-companion`: 保研首页改为以资格、背景、材料和导师联系为主线的阶段行动入口。
- `study-abroad-companion`: 留学首页改为以目的地、语言、选校、文书和签证网申为主线的阶段行动入口。

## Impact

- 修改 `webapp/isv/v620/cyancruise/assets/app-runtime.js` 中三个陪伴首页的渲染。
- 修改 `webapp/isv/v620/cyancruise/assets/styles.css` 的深造首页视觉和响应式样式。
- 更新路由静态校验与前端资源版本。
- 不新增图片、第三方依赖、后端接口或数据模型。
