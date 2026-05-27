## ADDED Requirements

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

### Requirement: 保持文件能力可替换
系统 SHALL 将简历记录中的文件引用保存为文件 key 或等价稳定引用，并将预览 URL、字节下载、OSS 删除等文件服务能力保留在可替换边界之外。本次基础迁移 SHALL NOT 依赖 OSS SDK、Multipart 上传、PDF 解析或前端文件预览。

#### Scenario: 保存文件 key
- **WHEN** 简历记录包含 `resumes/xxx.pdf` 形式的文件 key
- **THEN** 系统保存该稳定引用，而不是要求保存外部可访问的完整 URL

#### Scenario: 延后文件预览能力
- **WHEN** 调用方需要预览或下载简历文件
- **THEN** 本次简历基础能力只暴露已保存的文件 key，并将短链预览或代理下载留给后续文件服务适配
