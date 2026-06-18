## Why

当前 CyanCruise 已具备简历记录、文件上传预览、简历诊断、用户画像和 AI 基础设施，但“简历诊断”仍停留在结果展示层，用户难以基于已有简历和目标岗位形成“诊断、建议、选择优化方向、再次验证”的闭环。

本 change 推进 CareerLoop 主循环中的简历诊断模块：复用已迁移能力，将 IPD 中简历诊断、关键词、画像联动和简历优化建议的业务语义落到 CyanCruise 的 JDK 1.8/Cosmic 二开边界内。

## What Changes

- 增强 `resume-diagnosis` 能力，新增面向简历诊断闭环的结构化建议：问题类型、优先级、建议动作、示例改写方向、关联简历证据、目标岗位/岗位要求 关键词和画像信号来源。
- 增强简历诊断应用服务，使其可基于已有 `resumeId`、简历文本、目标岗位、目标岗位要求 和用户画像上下文生成更稳定的诊断建议，并把诊断分数和关键建议同步到用户画像 resume block。
- 增强 `webapp/isv/v620/cyancruise` 的“简历诊断”页面，展示已有简历选择、目标岗位/岗位要求 输入、诊断结果、诊断建议清单、优化方向确认和再次诊断入口。
- 复用现有 `/cc001/resume/list`、`/cc001/resume/create`、`/cc001/resume-diagnosis/analyze`、`/cc001/resume-diagnosis/keywords/status`、`/cc001/files/*`、`/cc001/career-profile/snapshot/get` 和 AI gateway 边界，避免重复造接口。
- 如需持久化建议结果，优先复用现有 PostgreSQL 简历诊断存储的 payload_json，不新增第三方依赖，不引入 Spring Boot、JPA、Flyway、Vue 或 uni-app 运行时。
- 更新静态资源版本号与 route/API 元数据，确保页面变更部署后不会继续命中旧缓存。

## Capabilities

### New Capabilities

- `resume-revision`: 定义 CyanCruise 简历诊断闭环的数据语义、建议结构、画像/目标岗位联动、页面交互和验证要求。

### Modified Capabilities

- `resume-diagnosis`: 将诊断结果从分数/strengths/weaknesses/suggestions 扩展为可落地的诊断建议，并要求按 resumeId 执行所有权校验、结果回写和画像 resume block 同步。
- `webapp-careerloop-pages`: 将 `resume-diagnosis` route 从诊断结果展示增强为用户可执行的简历诊断工作台。
- `file-upload-preview`: 明确简历诊断页面只能复用稳定 object key、预览 URL 和文本提取边界，不保存预签名 URL、token 或客户私有凭据。
- `career-profile-onboarding`: 明确简历诊断应消费用户目标岗位、测评和画像摘要作为上下文，但不能用 onboarding 自报状态替代真实简历记录。

## Impact

- IPD 来源路径：
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\ResumeDiagnosisController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\ResumeGenController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\ResumeService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\ResumeKeywordService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\UserProfileSnapshotService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\AgentProfileService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\ResumeServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\ResumeKeywordServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\utils\PdfTextExtractor.java`
  - `F:\Project\IPD\frontend\src\api\resume.ts`
  - `F:\Project\IPD\frontend\src\api\file.ts`
  - `F:\Project\IPD\frontend\src\api\user.ts`
  - `F:\Project\IPD\frontend\src\pages\resume-ai\index.vue`
  - `F:\Project\IPD\frontend\src\pages\resume\index.vue`
- CyanCruise 目标模块：
  - `webapp/isv/v620/cyancruise`
  - `code/base/v620-cc001-base-common/`
  - `code/base/v620-cc001-base-helper/`
  - `code/cloud01/v620-cc001-cloud01-app01/`
- 数据映射：
  - IPD `Resume.resumeId/userId/title/targetJob/fileUrl/parsedContent/diagnosisScore` -> CyanCruise `ResumeRecordDto.resumeId/userId/title/targetJob/fileKey/parsedContent/diagnosisScore`。
  - IPD `ResumeProfileKeyword.category/label/weight/evidence/status` -> CyanCruise `ResumeKeywordDto` 与 `ResumeKeywordStatusDto`。
  - IPD `UserProfileSnapshot.resume/preferences/assessment` -> CyanCruise `UserProfileSnapshot.ResumeBlock`、`PreferencesBlock`、`AssessmentBlock`。
  - IPD 简历优化建议文本/HTML 语义 -> CyanCruise JDK 8 DTO 中的结构化建议项和页面清单，不迁移 IPD 富文本渲染实现。
- 暂不迁移项：
  - Spring Boot Controller、JPA Repository、Flyway/MySQL SQL、PDFBox 运行时、OSS SDK、Vue/uni-app/Pinia/Vite/uView、小程序文件选择与富文本编辑器。
  - 完整在线简历编辑器、模板库、PDF 导出、版本 diff、自动改写并覆盖原简历。
- 依赖影响：不新增依赖；继续使用 JDK 1.8、仓库内 `gradlew.bat` 和现有 Cosmic/KDDT 模板约束。
- 验证方式：
  - `openspec validate enhance-resume-revision --strict`
  - focused helper/service/WebAPI tests
  - `node --check webapp\isv\v620\cyancruise\assets\app.js`
  - `node webapp\isv\v620\cyancruise\validate-routes.js`
  - 必要时使用 JDK 8 执行 `.\gradlew.bat clean build`
