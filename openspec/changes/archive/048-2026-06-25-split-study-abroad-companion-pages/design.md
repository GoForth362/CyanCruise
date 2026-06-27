# 设计说明

## 页面结构

`study-abroad` 保留为留学陪伴总入口，只展示流程说明和五个功能按钮。五个功能拆为独立路由：

- `study-abroad-profile` 消费 `/cc001/study-abroad/profile/diagnose`
- `study-abroad-language` 消费 `/cc001/study-abroad/language/plan`
- `study-abroad-school` 消费 `/cc001/study-abroad/school/position`
- `study-abroad-statement` 消费 `/cc001/study-abroad/statement/outline`
- `study-abroad-visa` 消费 `/cc001/study-abroad/visa/checklist`

## 前端状态

继续复用现有 `studyAbroadProfileResult`、`studyAbroadLanguageResult`、`studyAbroadSchoolResult`、`studyAbroadStatementResult`、`studyAbroadVisaResult`、`studyAbroadLoading` 和 `studyAbroadMessage`。提交完成后重新渲染当前子页面，而不是固定回到总入口。

## 路由元数据

`cyancruise-routes.json` 需要声明五个新增路由，使平台菜单、debug 面板和 route validation 能识别这些页面与 API 契约。默认用户导航可以只展示 `study-abroad` 总入口，子页面由总入口跳转。

## 部署

本次仅修改静态页面资源和 route metadata。后端 Java 不新增能力，但仍需跑 JDK 8 构建，确保当前工作树中的后端能力仍可编译。
