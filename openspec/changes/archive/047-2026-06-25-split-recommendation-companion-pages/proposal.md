# 拆分保研陪伴四个功能页面

## 背景

当前 `postgraduate-recommendation` 保研陪伴页面把“排名监控、背景提升、材料精修、导师联系”四个功能堆在同一个长页面内。用户在顶部看到四个入口后，还需要继续滚动定位对应表单，容易混淆当前正在处理哪一项。

## 目标

- 保留 `postgraduate-recommendation` 作为保研陪伴总入口。
- 新增四个独立页面：
  - `recommendation-ranking`：排名监控
  - `recommendation-background`：背景提升
  - `recommendation-material`：材料精修
  - `recommendation-tutor`：导师联系
- 每个子页面只展示本功能的表单、局部状态、结果和返回入口。
- 继续复用现有 `/cc001/recommendation/*` WebAPI，不新增后端依赖。

## 非目标

- 不调整保研竞争力诊断、行动计划、文书润色或导师意向信的后端规则。
- 不接入真实 AI provider 或导师论文检索。
- 不迁移 IPD Vue/uni-app 前端实现。

## 验证

- `openspec validate split-recommendation-companion-pages --strict`
- `node --check webapp\isv\v620\cyancruise\assets\app.js`
- `node webapp\isv\v620\cyancruise\validate-routes.js`
- JDK 1.8 + 仓库内 `.\gradlew.bat clean build`
