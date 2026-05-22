## 1. Snapshot 合并

- [x] 1.1 在 `CareerProfileSnapshotMergeService` 中新增 assessment block 合并。
- [x] 1.2 保证合并 assessment 时不覆盖 onboarding 和 preferences。

## 2. 应用服务

- [x] 2.1 在 `CareerProfileApplicationService` 中新增 `saveAssessment`。
- [x] 2.2 新增 `AssessmentApplicationService`。
- [x] 2.3 测评评分完成后写入 `UserProfileSnapshot.AssessmentBlock`。
- [x] 2.4 保存 assessment 后刷新统一画像。

## 3. 测试

- [x] 3.1 测试测评结果写入 snapshot。
- [x] 3.2 测试测评合并不覆盖 onboarding/preference。
- [x] 3.3 测试保存测评后统一画像 `hasAssessment=true`。
- [x] 3.4 测试保存测评后 missing signals 不再包含 assessment。

## 4. 文档

- [x] 4.1 更新迁移映射表中职业测评状态。

## 5. 验证

- [x] 5.1 运行 `openspec validate integrate-assessment-profile-snapshot --strict`。
- [x] 5.2 运行 `openspec validate --all --strict`。
- [x] 5.3 运行 cloud app 测试。
- [x] 5.4 运行 JDK 8 Gradle 构建。
