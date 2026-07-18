# 职业测评规格

## Purpose
定义 CyanCruise 职业测评题库、答题评分、结果持久化、画像回写和 WebApp 页面闭环。
## Requirements
### Requirement: 职业测评题库
CyanCruise SHALL provide a persistent server-side career assessment catalog including active scales, questions, options, dimension codes, score values, scale version, pool question count, and administrator-configured answer question count.

#### Scenario: 列出可用量表
- **WHEN** 用户打开画像补全页面
- **THEN** 系统 SHALL 返回可用量表的题库总数和实际作答题数
- **AND** 页面 SHALL 将实际作答题数展示给用户

#### Scenario: 开始测评
- **WHEN** 用户选择一个可用量表开始测评
- **THEN** 系统 SHALL 创建用户专属作答批次并返回 `attemptId` 和固定题目快照

#### Scenario: 用户选择测评类型
- **WHEN** a user views the assessment page
- **THEN** the page SHALL show multiple assessment cards such as MBTI, RIASEC, BIG5, career values, and stress coping, allowing the user to choose which scale to start

#### Scenario: 读取题目
- **WHEN** a user selects an active scale
- **THEN** the system SHALL return the ordered questions and options from the user's fixed assessment attempt snapshot

### Requirement: 职业测评提交评分
CyanCruise SHALL score submitted answers using the immutable server-side question snapshot associated with the authenticated user's assessment attempt.

#### Scenario: 完成抽题测评
- **WHEN** 用户回答作答批次中的全部题目并提交
- **THEN** 系统 SHALL 校验 `attemptId` 所属用户和题目范围
- **AND** 计算结果、保存记录、回写画像并将批次标记为已完成

#### Scenario: 提交内容与批次不一致
- **WHEN** 答案缺少批次题目或包含批次外题目
- **THEN** 系统 SHALL 拒绝提交并提示用户重新检查答案

#### Scenario: 提交 MBTI 答案
- **WHEN** a user submits one selected option for each MBTI question
- **THEN** the system SHALL calculate the four-letter MBTI result from dimension counts and mark the result `COMPLETED`

#### Scenario: 非法选项
- **WHEN** a submitted answer references an option that does not belong to the selected question
- **THEN** the system SHALL reject the submission and SHALL NOT score or complete the assessment attempt

### Requirement: 职业测评结果持久化
CyanCruise SHALL persist assessment results by user, scale, status, result summary, dimension counts, answer snapshots, suggested roles, and creation time through a replaceable storage boundary.

#### Scenario: 保存结果
- **WHEN** a user submits a valid assessment
- **THEN** the system SHALL save the result and return a stable record id

#### Scenario: 查看历史结果
- **WHEN** a user requests assessment records
- **THEN** the system SHALL return only that user's results ordered by newest first

### Requirement: 职业测评画像回写
CyanCruise SHALL merge completed assessment results into `UserProfileSnapshot.AssessmentBlock` without clearing onboarding, preferences, resume, interview, or plan data.

#### Scenario: 测评完成后刷新画像
- **WHEN** a user completes an assessment
- **THEN** the system SHALL update the assessment block with record id, scale id, scale title, summary, suggested roles, and completed time

### Requirement: 职业测评页面闭环
CyanCruise SHALL provide a webapp assessment page that can load scales, render questions, collect answers, submit them, and show the returned result.

#### Scenario: 页面完成测评
- **WHEN** a user with identity answers all questions and submits
- **THEN** the page SHALL call `/cc001/assessment/submit`, display the result summary and suggested roles, and refresh the local snapshot overview

#### Scenario: 无身份或文件预览
- **WHEN** the page runs without a user identity or in `file://` preview
- **THEN** the page SHALL show a recoverable preview or identity-required state and SHALL NOT call protected user-owned WebAPI

### Requirement: 测评页面提供深度画像入口
系统 SHALL 在画像补全页面向已完成至少一项测评的用户提供生成和查看 AI 深度画像的入口。

#### Scenario: 已完成测评的用户打开页面
- **WHEN** 页面加载到该用户至少一条已完成测评记录
- **THEN** 页面 SHALL 显示“生成深度画像”或“查看深度画像”操作，并在生成期间显示明确的加载状态

#### Scenario: 已存在深度画像的用户打开页面

- **WHEN** 页面加载到该用户的最新深度画像
- **THEN** 页面 SHALL 仅展示摘要、标签、生成时间和“查看详情与历史”按钮，不在题组列表页展开全部画像内容

### Requirement: 已完成测评支持独立 AI 解读
系统 SHALL 允许用户对一份已完成测评主动生成并查看 AI 解读；解读必须基于该次测评保存的答题快照、计分结果和维度信息，不得伪造成确定的人格能力或职业结论。

#### Scenario: 用户生成单项测评 AI 解读
- **WHEN** 用户在已完成测评结果页点击“生成 AI 解读”
- **THEN** 系统 SHALL 调用画像补全任务流的单项解读模式、保存返回的解读，并展示摘要、观察、建议和资料不足

#### Scenario: 单项 AI 解读生成失败
- **WHEN** 平台调用失败或返回不符合 JSON 契约
- **THEN** 系统 SHALL 向用户展示可重试错误，且不得展示规则兜底解读

#### Scenario: 平台返回内部工具调用指令
- **WHEN** 平台返回包含 `Thought`、`Action` 或 `Action_input` 的内部处理内容，而非约定的测评解读 JSON
- **THEN** 服务端 MUST NOT 将其保存为有效解读，页面 SHALL 隐藏已经存储的同类旧内容并提供重新生成操作

### Requirement: 已完成测评默认展示最近结果
系统 SHALL 在用户再次进入已完成的测评题组时默认展示该题组最近一次测评结果和已保存的答题记录，并 SHALL 仅在用户明确选择重新测评后创建新的空白答题轮次。

#### Scenario: 再次进入已完成题组
- **WHEN** 用户进入已有完成记录的测评题组
- **THEN** 页面 SHALL 展示最近一次测评结论、答题记录和生成时间
- **AND** 页面 SHALL 提供独立的“重新测评”操作，不得自动创建新的答题轮次

