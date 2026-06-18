## 1. OpenSpec 与迁移记录

- [x] 1.1 创建中文 proposal、design、spec 和 tasks，记录 IPD 来源、目标模块、数据映射、暂不迁移项和验证方式
- [x] 1.2 更新迁移映射文档和 route metadata 中的职业测评状态

## 2. 后端题库与评分闭环

- [x] 2.1 新增服务端职业测评题库边界和 IPD MBTI 内置题库
- [x] 2.2 扩展测评 DTO，支持 recordId、questionCount、createdAt、suggestedRoles 等页面和存储字段
- [x] 2.3 更新应用服务，支持按 scaleId 读取题库、提交评分、保存结果、回写画像

## 3. PostgreSQL 数据库

- [x] 3.1 新增 PostgreSQL assessment result storage，保存结果 JSON、维度 JSON、答案 JSON 与索引字段
- [x] 3.2 在初始化逻辑或 SQL 文档中补齐 `cc_assessment_record` 表结构

## 4. WebAPI 与前端

- [x] 4.1 扩展 `/cc001/assessment/*` WebAPI 和 `CareerLoopCustomWebApiPlugin` 路由
- [x] 4.2 更新 `webapp/isv/v620/cyancruise` 职业测评页面为可答题、可提交、可展示结果
- [x] 4.3 更新 route validator 所需路径和资源版本

## 5. 验证

- [x] 5.1 增加或更新 helper、应用服务、WebAPI、PostgreSQL focused 测试
- [x] 5.2 执行 `openspec validate migrate-career-assessment --strict`
- [x] 5.3 执行 `node webapp\isv\v620\cyancruise\validate-routes.js` 与 `node --check webapp\isv\v620\cyancruise\assets\app.js`
- [x] 5.4 使用 JDK 1.8 和仓库 `gradlew.bat` 执行 focused Gradle 测试或完整构建
