## MODIFIED Requirements

### Requirement: 职业测评题库
CyanCruise SHALL provide a persistent server-side career assessment catalog including active scales, questions, options, dimension codes, score values, scale version, pool question count, and administrator-configured answer question count. The assessment home page SHALL present each available scale as a clearly differentiated, responsive card using the actual answer question count and the user's completion status.

#### Scenario: 列出可用量表
- **WHEN** 用户打开画像补全页面
- **THEN** 系统 SHALL 返回可用量表的题库总数和实际作答题数
- **AND** 页面 SHALL 将实际作答题数、预计用时和完成状态展示给用户

#### Scenario: 开始测评
- **WHEN** 用户选择一个可用量表开始测评
- **THEN** 系统 SHALL 创建用户专属作答批次并返回 `attemptId` 和固定题目快照

#### Scenario: 用户选择测评类型
- **WHEN** a user views the assessment page
- **THEN** the page SHALL show multiple responsive assessment cards such as MBTI, RIASEC, BIG5, career values, and stress coping, allowing the user to choose which scale to start
- **AND** each card SHALL separate its title, description, metadata, completion status, and primary action into a clear visual hierarchy

#### Scenario: 读取题目
- **WHEN** a user selects an active scale
- **THEN** the system SHALL return the ordered questions and options from the user's fixed assessment attempt snapshot

### Requirement: 测评页面提供深度画像入口
系统 SHALL 在画像补全页面使用真实测评记录展示画像完成进度，并向已完成至少一项测评的用户提供生成和查看 AI 深度画像的入口。

#### Scenario: 用户打开画像补全首页
- **WHEN** 页面加载可用题组和该用户的已完成测评记录
- **THEN** 页面 SHALL 在画像概览中展示已完成题组数、题组总数和清晰的下一步提示
- **AND** 页面 SHALL NOT 使用演示数字替代真实进度

#### Scenario: 已完成测评的用户打开页面
- **WHEN** 页面加载到该用户至少一条已完成测评记录
- **THEN** 页面 SHALL 显示“生成深度画像”或“重新生成深度画像”操作，并在生成期间显示明确的加载状态

#### Scenario: 已存在深度画像的用户打开页面
- **WHEN** 页面加载到该用户的最新深度画像
- **THEN** 页面 SHALL 仅展示摘要状态、生成时间和“查看详情与历史”按钮，不在题组列表页展开全部画像内容

#### Scenario: 用户在窄屏或使用键盘浏览
- **WHEN** 用户在窄屏设备或使用键盘打开画像补全首页
- **THEN** 题组卡片 SHALL 自动排列为单列，并为可操作元素提供清晰的焦点状态
