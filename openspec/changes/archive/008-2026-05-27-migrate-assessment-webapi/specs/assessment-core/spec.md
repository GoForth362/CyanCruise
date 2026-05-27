## ADDED Requirements

### Requirement: 通过 WebAPI 提交测评并写入画像
系统 SHALL 暴露 Cosmic WebAPI 测评提交入口，用于接收用户 ID、测评量表和单选答案提交请求。该入口 SHALL 复用现有测评应用服务完成评分，并 SHALL 将完成后的量表 ID、量表标题、画像摘要和完成时间写入该用户的 `UserProfileSnapshot.AssessmentBlock`。

#### Scenario: 提交测评答案
- **WHEN** 调用方通过测评 WebAPI 提交用户 ID、量表结构和 `questionId -> optionId` 答案
- **THEN** 系统返回 `AssessmentScoreResult`，其中包含完成状态、画像摘要、维度计数和答案快照

#### Scenario: 测评提交后刷新画像
- **WHEN** 测评 WebAPI 完成一次有效提交
- **THEN** 系统将评分结果合并到该用户画像快照的 assessment block，并刷新统一画像中的测评完成信号

#### Scenario: 保持测评入口轻量
- **WHEN** 本次迁移实现测评提交 WebAPI
- **THEN** 系统 SHALL NOT 引入 Spring Controller、JPA Repository、Flyway SQL、AI 解读或题库管理页面作为该入口的运行依赖
