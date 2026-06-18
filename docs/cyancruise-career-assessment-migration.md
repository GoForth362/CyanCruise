# CyanCruise 职业测评迁移记录

## 来源与目标

- change：`migrate-career-assessment`
- IPD 来源：`F:\Project\IPD\backend\src\main\java\com\group1\career\controller\AssessmentController.java`、`service\impl\AssessmentServiceImpl.java`、`model\entity\Assessment*`、`repository\Assessment*`、`backend\scripts\seed_assessments.sql`、`frontend\src\api\assessment.ts`、`frontend\src\pages\assessment\*`
- CyanCruise 目标：`code/base/v620-cc001-base-common`、`code/base/v620-cc001-base-helper`、`code/cloud01/v620-cc001-cloud01-app01`、`webapp/isv/v620/cyancruise`

## 数据映射

- IPD `assessment_scales/questions/options` 映射为 CyanCruise `AssessmentCatalog` 与内置 MBTI 题库。
- IPD `assessment_records/answers` 映射为 PostgreSQL `cc_assessment_record`，保留结构化索引列、维度 JSON、答案 JSON、推荐岗位 JSON 和完整 payload JSON。
- IPD `UserProfileSnapshot.assessment` 映射为 CyanCruise `UserProfileSnapshot.AssessmentBlock`，包含 `lastRecordId`、`scaleId`、`scaleTitle`、`summary`、`suggestedRoles` 和 `completedAt`。

## 本次迁移

- 量表列表、题目读取、答案提交、MBTI 评分、答案快照、推荐岗位、结果记录、画像回写。
- `/cc001/assessment/scales`、`/questions`、`/submit`、`/records`、`/record/get` WebAPI 路由。
- 静态 webapp 职业测评答题、提交和结果展示闭环。

## 暂不迁移

- Spring Boot、JPA、Flyway、Vue、uni-app、小程序运行时。
- 微信订阅通知、签到积分、真实 AI 个性化解读。
- IPD 数据库密码、部署脚本、本地 ENV 路径。

## 验证方式

- 手动建表脚本：`openspec/changes/migrate-career-assessment/sql/postgresql-assessment-storage.sql`
- `openspec validate migrate-career-assessment --strict`
- `node webapp\isv\v620\cyancruise\validate-routes.js`
- `node --check webapp\isv\v620\cyancruise\assets\app.js`
- JDK 1.8：`.\gradlew.bat` focused 测试或 `.\gradlew.bat clean build`
