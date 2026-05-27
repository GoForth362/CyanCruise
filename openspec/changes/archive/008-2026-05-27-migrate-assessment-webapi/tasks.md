## 1. WebAPI Boundary

- [x] 1.1 新增 `AssessmentWebApi`，使用 Cosmic WebAPI 注解暴露 `/cc001/assessment/submit`。
- [x] 1.2 WebAPI 提交方法接收 `userId`、`AssessmentScaleDto` 和 `AssessmentSubmitRequest`。
- [x] 1.3 WebAPI 调用 `AssessmentApplicationService.submitAndSaveProfile` 并返回 `AssessmentScoreResult`。
- [x] 1.4 保持 WebAPI 不依赖 Spring、JPA、Flyway、AI 解读或题库管理页面。

## 2. Test Coverage

- [x] 2.1 为 `AssessmentWebApi` 或同等应用边界新增云应用模块测试。
- [x] 2.2 测试有效提交返回 `COMPLETED` 状态、画像摘要、维度计数和答案快照。
- [x] 2.3 测试提交后用户画像快照包含 assessment block，且统一画像测评信号被刷新。

## 3. Migration Documents

- [x] 3.1 更新 `docs/ipd-to-cyancruise-migration-map.md` 中职业测评状态，标记 WebAPI 已接入。
- [x] 3.2 在文档中保留 datamodel、页面和 AI 解读仍待后续迁移的说明。

## 4. Validation

- [x] 4.1 运行 `openspec validate migrate-assessment-webapi --strict`。
- [x] 4.2 运行 `openspec validate --all --strict`。
- [x] 4.3 设置 JDK 8 后运行相关 Gradle 测试或构建。
