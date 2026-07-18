## MODIFIED Requirements

### Requirement: 职业测评题库

CyanCruise SHALL provide a persistent server-side career assessment catalog including active scales, questions, options, dimension codes, score values, scale version, pool question count, and administrator-configured answer question count.

#### Scenario: 列出可用量表
- **WHEN** 用户打开画像补全页面
- **THEN** 系统 SHALL 返回可用量表的题库总数和实际作答题数
- **AND** 页面 SHALL 将实际作答题数展示给用户

#### Scenario: 开始测评
- **WHEN** 用户选择一个可用量表开始测评
- **THEN** 系统 SHALL 创建用户专属作答批次并返回 `attemptId` 和固定题目快照

### Requirement: 职业测评提交评分

CyanCruise SHALL score submitted answers using the immutable server-side question snapshot associated with the authenticated user's assessment attempt.

#### Scenario: 完成抽题测评
- **WHEN** 用户回答作答批次中的全部题目并提交
- **THEN** 系统 SHALL 校验 `attemptId` 所属用户和题目范围
- **AND** 计算结果、保存记录、回写画像并将批次标记为已完成

#### Scenario: 提交内容与批次不一致
- **WHEN** 答案缺少批次题目或包含批次外题目
- **THEN** 系统 SHALL 拒绝提交并提示用户重新检查答案
