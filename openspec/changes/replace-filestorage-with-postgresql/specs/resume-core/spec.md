# Resume Core Delta

## MODIFIED Requirements
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
