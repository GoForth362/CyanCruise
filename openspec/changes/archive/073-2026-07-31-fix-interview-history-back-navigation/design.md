## Context

页面跳转函数 `navigateToRoute` 会通过 `rememberReturnRoute(target, source)` 保存来源页面。此前为两个面试记录页增加了读取来源路由的优先逻辑，导致页面头部“返回”按钮可能回到任意来源页，与面试记录页固定返回各自开始主页的产品语义不符。

## Goals / Non-Goals

**Goals:**

- AI 模拟面试记录页始终返回 AI 模拟面试开始主页。
- 全景仿真面试记录页始终返回全景仿真面试开始主页。
- 从应用内入口、直接链接、刷新或其他页面进入时，返回结果保持一致。

**Non-Goals:**

- 不改写浏览器原生历史栈。
- 不调整其他页面现有的父子返回关系。
- 不移除其他业务页面使用的来源记录与滚动恢复机制。

## Decisions

1. 在运行时 `backRouteFor` 的最高优先级明确处理两个记录页：`interview-history` 固定返回 `interview`，`interview-panorama-history` 固定返回 `interview-panorama`。
2. 删除面试记录页读取 `state.returnRoutes` 的专用逻辑，确保保存的来源页不会覆盖固定映射。
3. `navigation.js` 保留相同固定映射，作为组件级导航契约和运行时之外的统一配置。
4. 继续使用现有 `navigateBackToRoute` 完成跳转，但目标路由由固定映射决定，不使用 `window.history.back()`。

## Risks / Trade-offs

- [通用来源返回逻辑再次覆盖固定规则] → 在 `backRouteFor` 最前面处理两个记录页，并用自动校验禁止恢复专用动态来源函数。
- [配置与运行时映射不一致] → 同时校验 `app-runtime.js` 和 `navigation.js` 中的固定映射。

## 补充决定

5. 两个面试记录页的页头“返回”不再使用通用 `data-back-route` 和 `navigateBackToRoute` 事件链，而是在 `pageHeaderActions` 最高优先级生成带固定 `data-link` 的专用入口。AI 模拟面试记录固定链接到 `interview`，全景仿真面试记录固定链接到 `interview-panorama`。通用父子返回映射仅保留为导航契约，不参与这两个按钮的实际点击处理。
