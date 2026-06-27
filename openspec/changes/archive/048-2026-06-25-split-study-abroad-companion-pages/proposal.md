# 拆分留学陪伴功能页面

## 背景

当前 `study-abroad` 留学陪伴页面把“国家地区、语言考试、选校定位、文书主线、签证网申”五个功能放在同一个长页面内。用户从顶部入口按钮进入后仍需要滚动定位对应表单，信息密度较高，单项任务边界不够清楚。

## 目标

- 保留 `study-abroad` 作为留学陪伴总入口。
- 新增五个独立页面：
  - `study-abroad-profile`：国家地区与申请画像
  - `study-abroad-language`：语言考试
  - `study-abroad-school`：选校定位
  - `study-abroad-statement`：文书主线
  - `study-abroad-visa`：签证网申
- 每个子页面只展示本功能所需表单、局部状态、结果和返回入口。
- 继续复用现有 `/cc001/study-abroad/*` WebAPI，不新增后端依赖。

## 非目标

- 不调整留学画像诊断、语言规划、选校定位、个人陈述或签证清单的后端规则。
- 不接入真实 AI provider、雅思口语对练、写作批改或院校官网检索。
- 不迁移 IPD Vue/uni-app 前端实现。

## 验证

- `openspec validate split-study-abroad-companion-pages --strict`
- `node --check webapp\isv\v620\cyancruise\assets\app.js`
- `node webapp\isv\v620\cyancruise\validate-routes.js`
- JDK 1.8 + 仓库内 `.\gradlew.bat clean build`
