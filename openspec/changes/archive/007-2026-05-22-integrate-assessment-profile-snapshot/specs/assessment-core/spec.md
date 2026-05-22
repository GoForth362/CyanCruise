## MODIFIED Requirements

### Requirement: 返回可写入用户画像的测评摘要
评分结果 SHALL 包含量表 ID、量表标题、状态、画像摘要、维度计数和答案快照，以便后续写入 `UserProfileSnapshot.AssessmentBlock`。应用服务 SHALL 能够把一次评分结果合并到指定用户的职业画像快照，并刷新统一画像。

#### Scenario: 测评完成
- **WHEN** 评分服务完成一次提交
- **THEN** 返回状态为 `COMPLETED` 的结果，并包含画像摘要和维度计数

#### Scenario: 测评结果写入画像快照
- **WHEN** 用户提交测评并完成评分
- **THEN** 应用服务把量表 ID、量表标题、画像摘要和完成时间写入该用户的 `AssessmentBlock`

