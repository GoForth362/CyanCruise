# 设计说明

## 页面结构

`postgraduate-recommendation` 保留为保研陪伴总入口，只展示流程说明和四个功能按钮。四个功能拆为独立路由：

- `recommendation-ranking` 消费 `/cc001/recommendation/diagnose`
- `recommendation-background` 消费 `/cc001/recommendation/plan/generate`
- `recommendation-material` 消费 `/cc001/recommendation/document/polish`
- `recommendation-tutor` 消费 `/cc001/recommendation/tutor-letter/generate`

## 前端状态

继续复用现有 `recommendationDiagnosisResult`、`recommendationPlanResult`、`recommendationPolishResult`、`recommendationTutorLetterResult`、`recommendationLoading` 和 `recommendationMessage`。提交完成后重新渲染当前子页面，而不是固定回到总入口。

## 路由元数据

`cyancruise-routes.json` 需要声明四个新增路由，使平台菜单、debug 面板和 route validation 能识别这些页面与 API 契约。默认用户导航可以只展示 `postgraduate-recommendation` 总入口，子页面由总入口跳转。

## 部署

本次仅修改静态页面资源和 route metadata。后端 Java 不新增能力，但仍需跑 JDK 8 构建，确保当前工作树中的后端能力仍可编译。
