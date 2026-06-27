# 拆分考研陪伴四个功能页面

## 背景

当前 `postgraduate` 考研陪伴页面在同一屏内同时承载“择校择专业、复习计划、错题解析、复试准备”四个功能。用户从页面顶部看到四个入口按钮后，仍然需要在同一长页面中滚动查找对应表单，功能边界不够清晰。

## 目标

- 将考研陪伴总入口保留为 `postgraduate`，作为四个能力的导航页。
- 新增四个独立页面：
  - `postgraduate-school`：择校择专业
  - `postgraduate-plan`：复习计划
  - `postgraduate-mistake`：错题解析
  - `postgraduate-reexam`：复试准备
- 每个子页面只展示本功能所需表单、加载状态、结果和返回入口。
- 继续复用现有 `/cc001/postgraduate/*` WebAPI，不新增后端依赖。

## 非目标

- 不调整考研后端推荐、计划、错题和复试规则算法。
- 不引入真实 AI provider 或图片 OCR。
- 不迁移 IPD Vue/uni-app 前端实现。

## 验证

- `openspec validate split-postgraduate-companion-pages --strict`
- `node --check webapp\isv\v620\cyancruise\assets\app.js`
- `node webapp\isv\v620\cyancruise\validate-routes.js`
- JDK 1.8 + 仓库内 `.\gradlew.bat clean build`
