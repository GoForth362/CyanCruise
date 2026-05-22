## 背景

`migrate-assessment-core` 完成了评分 DTO 和 `AssessmentScoringService`，但它只返回评分结果。IPD 的完整流程会在测评完成后写入 `UserProfileSnapshot.AssessmentBlock`，让简历、面试、今日任务和助手都能读取这次测评结果。本次迁移把这条连接补上。

## 设计

- 在 `CareerProfileSnapshotMergeService` 中增加 `mergeAssessment`。
- 在 `CareerProfileApplicationService` 中增加 `saveAssessment`，保存 snapshot 后刷新统一画像。
- 新增 `AssessmentApplicationService`：
  - 输入：`userId`、`AssessmentScaleDto`、`AssessmentSubmitRequest`。
  - 调用：`AssessmentScoringService.score(...)`。
  - 合并：把结果摘要写入用户画像。
  - 输出：`AssessmentScoreResult`。
- `suggestedRoles` 暂不生成，因为 IPD 中该字段来自 AI insight，本次不迁移 AI 洞察。

## 非目标

- 不持久化完整测评记录表。
- 不生成 AI 洞察。
- 不发送通知、打卡或订阅消息。
- 不提供 WebAPI 页面入口。

## 验证

- `openspec validate integrate-assessment-profile-snapshot --strict`
- `openspec validate --all --strict`
- `.\gradlew.bat :v620-cc001-cloud01-app01:test`
- `.\gradlew.bat clean build`

