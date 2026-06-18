## Why

CyanCruise 已有职业测评评分骨架，但前端仍停留在入口状态，后端也缺少可复用题库读取、结果持久化和 PostgreSQL 表结构，无法形成“答题-评分-入库-回写画像”的闭环。

本次以 IPD 项目中的职业能力测评为参考，迁移核心业务语义到 Cosmic/JDK 1.8 工程，使测评结果能服务后续画像、今日行动和路线规划。

## What Changes

- 新增 CyanCruise 职业测评量表、题目、选项、记录的后端边界，支持列出量表、读取题目、提交答案、查看最近结果。
- 迁移 IPD `MBTI Personality Test` 题库语义为 CyanCruise 内置题库，并保留题目维度、选项维度和排序规则。
- 新增 PostgreSQL 测评存储适配器和初始化 SQL，保存用户答题结果、维度计数、答案快照和创建时间。
- 更新职业测评 WebAPI 与自定义路由，使前端只需提交 `scaleId` 和 `answers`，不再依赖前端传入完整评分密钥。
- 更新静态 webapp 的职业测评页面，从入口态升级为可答题、可提交、可展示结果的页面。
- 更新迁移文档与 route metadata，记录 IPD 来源、CyanCruise 目标模块、数据映射、暂不迁移项和验证方式。

## Capabilities

### New Capabilities
- `career-assessment`: 定义 CyanCruise 职业测评题库、答题、评分、结果持久化、画像回写和页面交互闭环。

### Modified Capabilities
- `career-profile-onboarding`: 测评提交完成后写入画像 `assessment` block，并继续遵守已有画像合并与目标岗位兜底规则。

## Impact

- IPD 来源路径：`F:\Project\IPD\backend\src\main\java\com\group1\career\controller\AssessmentController.java`、`service\impl\AssessmentServiceImpl.java`、`model\entity\Assessment*`、`repository\Assessment*`、`backend\scripts\seed_assessments.sql`、`frontend\src\api\assessment.ts`、`frontend\src\pages\assessment\*`。
- CyanCruise 目标模块：`code/base/v620-cc001-base-common`、`code/base/v620-cc001-base-helper`、`code/cloud01/v620-cc001-cloud01-app01`、`webapp/isv/v620/cyancruise`、`docs`、`datamodel`。
- 数据映射：IPD `assessment_scales/questions/options/records/answers` 迁移为 CyanCruise DTO、内置题库与 PostgreSQL `cc_assessment_*` 表；IPD `UserProfileSnapshot.assessment` 语义映射到 CyanCruise `UserProfileSnapshot.AssessmentBlock`。
- 暂不迁移：Spring Boot/JPA/Flyway、Vue/uni-app runtime、微信订阅通知、签到积分、AI 个性化解读真实调用和 IPD 数据库密码或部署脚本。
- 依赖影响：不新增第三方依赖；继续使用仓库已有 Jackson/PostgreSQL JDBC 与 JDK 1.8。
