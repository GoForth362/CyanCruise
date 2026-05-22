## Why

职业测评评分内核已经完成，但评分结果还没有进入 CyanCruise 的职业画像快照。为了让测评成为今日推荐和统一画像的真实输入，需要把测评结果合并到 `UserProfileSnapshot.AssessmentBlock` 并刷新统一画像。

## What Changes

- 为职业画像快照新增测评 block 的字段级合并能力。
- 为职业画像应用服务新增 `saveAssessment` 操作。
- 新增测评应用服务：调用评分内核，生成评分结果，并把结果写入职业画像快照。
- 新增测试，验证测评结果可持久化进 snapshot，并影响统一画像阶段与完整度。

## Capabilities

### New Capabilities
- None.

### Modified Capabilities
- `assessment-core`：评分结果可被应用服务写入职业画像。
- `career-profile-onboarding`：职业画像快照支持测评 block 合并与画像刷新。

## Impact

- 修改 `CareerProfileSnapshotMergeService` 和 `CareerProfileApplicationService`。
- 新增 `AssessmentApplicationService`。
- 新增 cloud01 应用服务测试。
- 不新增 Cosmic datamodel，不接前端页面。

