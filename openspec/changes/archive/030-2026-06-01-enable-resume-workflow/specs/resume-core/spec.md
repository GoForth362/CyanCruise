## ADDED Requirements

### Requirement: 支持用户侧简历创建闭环
简历基础能力 SHALL 支持 webapp 用户侧创建闭环：调用方通过 `/cc001/resume/create` 创建记录后，系统 SHALL 按服务端持久化结果返回简历 DTO，并确保后续 `/cc001/resume/list` 能在同一用户身份下读取到该记录。

#### Scenario: 创建后列表可见
- **WHEN** 用户通过 `/cc001/resume/create` 保存一条包含标题、目标岗位、文件 key 或解析内容的简历记录
- **THEN** 后续同一用户调用 `/cc001/resume/list` SHALL 返回该记录，并按最近更新时间排序

#### Scenario: 创建后刷新保持
- **WHEN** 用户创建简历记录后刷新苍穹 webapp 页面
- **THEN** 页面重新调用 `/cc001/resume/list` SHALL 仍能读取该用户已保存的简历记录

#### Scenario: 跨用户不可见
- **WHEN** 另一个用户调用 `/cc001/resume/list`
- **THEN** 系统 SHALL NOT 返回不属于该用户的简历记录

### Requirement: 简历创建同步真实画像信号
简历基础能力 SHALL 在用户创建简历记录后同步职业画像 resume block，并使工作台摘要能够区分“系统真实保存过简历记录”和“onboarding 自报已有简历”。

#### Scenario: 创建后同步 resume block
- **WHEN** 用户创建简历记录成功
- **THEN** 系统 SHALL 将最新简历 ID、标题、目标岗位、文件 key、诊断分数和更新时间同步到该用户画像快照的 resume block

#### Scenario: 自报简历不替代真实记录
- **WHEN** 用户仅在 onboarding 中自报已有简历但未创建系统简历记录
- **THEN** `/cc001/resume/list` SHALL 仍返回空列表，画像 SHALL NOT 把自报状态等同于真实简历记录

#### Scenario: 真实记录优先用于工作台
- **WHEN** 用户同时存在 onboarding 自报状态和系统保存的简历记录
- **THEN** 工作台简历状态 SHALL 优先体现系统保存的简历记录

### Requirement: 简历创建请求容忍可选文件引用
简历创建能力 SHALL 允许调用方在文件 adapter 不可用时只保存简历元数据；`fileKey` 和 `parsedContent` 可以为空，但标题或目标岗位等用户输入 SHALL 仍可形成简历记录。

#### Scenario: 无文件 key 创建元数据
- **WHEN** 用户提交标题和目标岗位但没有文件 key
- **THEN** 系统 SHALL 创建简历元数据记录，并在列表中返回该记录

#### Scenario: 带文件 key 创建记录
- **WHEN** 用户提交由文件服务返回或手工输入的稳定 fileKey
- **THEN** 系统 SHALL 将该 fileKey 保存为简历记录的稳定文件引用
