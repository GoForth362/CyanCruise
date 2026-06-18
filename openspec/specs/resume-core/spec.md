# 简历基础规格

## Purpose
定义 CyanCruise 如何管理简历基础记录、用户简历列表、详情、更新、删除和职业画像 resume block 同步，为后续简历诊断、JD 匹配、今日任务和模拟面试提供真实简历信号。
## Requirements
### Requirement: 表达简历基础记录
系统 SHALL 使用 JDK 8 兼容 DTO 表达简历基础记录，包括简历 ID、用户 ID、标题、目标岗位、文件 key、版本、状态、解析内容、诊断分数、创建时间和更新时间，且不得依赖 Spring、JPA、Lombok 或 Jackson 注解。

#### Scenario: 创建简历记录 DTO
- **WHEN** 调用方构造一条简历记录
- **THEN** 该记录能够表达用户归属、简历标题、目标岗位、文件 key 和解析内容等基础字段

### Requirement: 管理用户简历记录
系统 SHALL 提供应用级操作，用于创建简历、查询单条简历、查询用户简历列表、更新简历元数据和删除简历记录。查询、更新和删除操作 SHALL 以用户 ID 约束简历归属，避免跨用户访问。

#### Scenario: 创建简历记录
- **WHEN** 用户保存包含标题、目标岗位、文件 key 和解析内容的简历记录
- **THEN** 系统生成简历 ID，保存记录，并按更新时间维护该用户的简历列表

#### Scenario: 查询用户简历列表
- **WHEN** 调用方请求某个用户的简历列表
- **THEN** 系统只返回该用户拥有的简历记录，并按最近更新时间优先排序

#### Scenario: 拒绝跨用户读取
- **WHEN** 用户请求不属于自己的简历记录
- **THEN** 系统拒绝返回该简历详情

#### Scenario: 更新简历元数据
- **WHEN** 简历拥有者更新标题、目标岗位、文件 key 或解析内容
- **THEN** 系统保存更新后的记录并刷新更新时间

#### Scenario: 删除简历记录
- **WHEN** 简历拥有者删除一条简历记录
- **THEN** 系统从该用户简历列表中移除该记录，后续详情查询不再返回该记录

### Requirement: 同步真实简历信号到职业画像
系统 SHALL 在创建或更新简历后，将最新简历摘要合并到 `UserProfileSnapshot.ResumeBlock`，并刷新统一职业画像。该信号 SHALL 表示系统真实保存过简历记录，不能由 onboarding 自报简历状态替代。

#### Scenario: 简历保存后更新画像
- **WHEN** 用户创建或更新一条简历记录
- **THEN** 系统将简历 ID、文件 key、标题、目标岗位、诊断分数和更新时间写入该用户画像快照的 resume block

#### Scenario: 真实简历移除缺失信号
- **WHEN** 用户已有系统保存的简历记录
- **THEN** 统一画像将 `hasResume` 标记为 true，并不再报告 resume 缺失信号

#### Scenario: 删除当前画像引用的简历
- **WHEN** 用户删除画像 resume block 当前引用的简历记录
- **THEN** 系统清理该引用或切换到该用户剩余的最新简历，避免画像继续引用已删除记录

### Requirement: 使用诊断结果更新简历摘要
简历诊断能力 SHALL 能够将诊断总分写回简历记录的 diagnosisScore，并复用简历基础能力将最新诊断分数同步到职业画像 resume block。该更新 SHALL 保留简历记录的标题、目标岗位、文件 key、版本、状态和解析内容。

#### Scenario: 诊断分数写入简历
- **WHEN** 简历诊断对用户自己的简历产生 overallScore
- **THEN** 系统将该分数保存到该简历记录 diagnosisScore

#### Scenario: 诊断分数同步画像
- **WHEN** 简历记录的 diagnosisScore 被诊断结果更新
- **THEN** 系统将最新诊断分数同步到用户画像 resume block

#### Scenario: 保留简历元数据
- **WHEN** 系统只更新诊断分数
- **THEN** 简历标题、目标岗位、文件 key、版本、状态和解析内容保持不变

### Requirement: 保持文件能力可替换
系统 SHALL 将简历记录中的文件引用保存为文件 key 或等价稳定引用，并将预览 URL、字节下载、OSS 删除等文件服务能力保留在可替换边界之外。本次基础迁移 SHALL NOT 依赖 OSS SDK、Multipart 上传、PDF 解析或前端文件预览。

#### Scenario: 保存文件 key
- **WHEN** 简历记录包含 `resumes/xxx.pdf` 形式的文件 key
- **THEN** 系统保存该稳定引用，而不是要求保存外部可访问的完整 URL

#### Scenario: 延后文件预览能力
- **WHEN** 调用方需要预览或下载简历文件
- **THEN** 本次简历基础能力只暴露已保存的文件 key，并将短链预览或代理下载留给后续文件服务适配

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

### Requirement: PostgreSQL 持久化简历记录
CyanCruise 简历记录 SHALL 在运行时通过 PostgreSQL 持久化，而不是默认写入 `filestorage/resume-core`。简历记录中的 `fileKey` SHALL 继续作为稳定文件引用保存，二进制文件服务不属于本要求。

#### Scenario: 创建后跨实例列表可见
- **WHEN** 用户创建简历记录后创建新的应用服务实例
- **THEN** 新实例通过 `/cc001/resume/list` 或应用服务列表操作 SHALL 从 PostgreSQL 返回该记录

#### Scenario: 更新和删除写入 PostgreSQL
- **WHEN** 简历拥有者更新或删除自己的简历记录
- **THEN** PostgreSQL 中对应记录 SHALL 被更新或删除，并保持后续详情和列表查询一致

#### Scenario: 保持用户隔离
- **WHEN** 另一个用户查询简历列表或详情
- **THEN** 系统 SHALL NOT 返回不属于该用户的 PostgreSQL 简历记录


### Requirement: 保存 PDF 提取的简历正文
简历创建和更新能力 SHALL 支持把 PDF 提取的正文保存到 `parsedContent`。自动提取 SHALL NOT 用空文本覆盖已有非空正文，更新操作 SHALL 继续执行用户所有权校验。

#### Scenario: 创建简历时保存正文
- **WHEN** 用户上传 PDF 并成功提取正文后创建简历
- **THEN** 简历记录同时保存稳定 `fileKey` 和提取后的 `parsedContent`

#### Scenario: 已有简历回写正文
- **WHEN** 用户拥有的简历有 `fileKey` 但 `parsedContent` 为空且重新提取成功
- **THEN** 系统把提取正文更新到该简历记录

#### Scenario: 拒绝空正文覆盖
- **WHEN** PDF 提取结果为空或失败而简历已有非空 `parsedContent`
- **THEN** 系统保留原正文不变
