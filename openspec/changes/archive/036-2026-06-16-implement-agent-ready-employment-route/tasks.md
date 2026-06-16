## 1. OpenSpec

- [x] 1.1 创建 `implement-agent-ready-employment-route` change。
- [x] 1.2 编写中文 proposal、design、delta spec 和 tasks。

## 2. 后端契约与生成

- [x] 2.1 扩展职业规划 DTO，支持大阶段、小阶段、本周计划和每日建议。
- [x] 2.2 实现规则版就业路线生成器，基于画像、目标岗位、简历和面试状态生成路线。
- [x] 2.3 在应用服务中预留规划智能体接口，当前无智能体时使用规则版 fallback。
- [x] 2.4 更新职业规划 WebAPI 测试和 helper 测试。

## 3. 前端页面

- [x] 3.1 将 `AI路径规划` 页面从占位改为完整路线图页面。
- [x] 3.2 增加刷新路线图、阶段展示、本周计划和每日建议展示。
- [x] 3.3 就业首页路线摘要优先复用后端计划数据。
- [x] 3.4 更新静态资源版本号并同步本地 ENV。

## 4. 验证

- [x] 4.1 执行 `openspec validate implement-agent-ready-employment-route --strict`。
- [x] 4.2 执行 `node --check webapp/isv/v620/cyancruise/assets/app.js`。
- [x] 4.3 执行 `node webapp/isv/v620/cyancruise/validate-routes.js`。
- [x] 4.4 在 JDK 8 下执行职业规划相关测试。
