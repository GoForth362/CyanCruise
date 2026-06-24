# 设计说明

## 页面结构

`postgraduate` 保留为考研陪伴总入口页面，只展示流程说明和四个功能卡片/按钮。四个功能拆为独立路由：

- `postgraduate-school` 消费 `/cc001/postgraduate/school-recommend`
- `postgraduate-plan` 消费 `/cc001/postgraduate/plan/generate`
- `postgraduate-mistake` 消费 `/cc001/postgraduate/mistake/analyze`
- `postgraduate-reexam` 消费 `/cc001/postgraduate/reexam/prepare`

旧的 `postgraduate-exam` 仍作为兼容入口指向 `postgraduate`。

## 前端状态

继续复用现有 `postgraduateSchoolResult`、`postgraduatePlanResult`、`postgraduateMistakeResult`、`postgraduateReexamResult`、`postgraduateLoading` 和 `postgraduateMessage`。提交完成后重新渲染当前子页面，而不是固定回到总入口。

## 路由元数据

`cyancruise-routes.json` 需要声明四个新增路由，使平台菜单、debug 面板和 route validation 都能识别这些页面与 API 契约。默认用户导航可只展示 `postgraduate` 总入口，子页面由总入口跳转。

## 部署

本次仅修改静态页面资源和 route metadata。后端 Java 不新增能力，但仍需跑 JDK 8 构建，确保当前未提交后端变更仍可编译。
