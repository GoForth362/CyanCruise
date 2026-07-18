## ADDED Requirements

### Requirement: 保存用户考研规划资料及解析状态
CyanCruise SHALL 保存当前用户上传的考研规划资料记录。记录 SHALL 包含用户、升学方向、资料类型、标题、原文件名、文件引用、媒体类型、文件大小、解析状态、解析消息、可用正文、创建时间和更新时间。

#### Scenario: 上传可解析资料
- **WHEN** 已登录用户在升学中心上传受支持且含可读取正文的资料
- **THEN** 文件 SHALL 由现有文件服务保存
- **AND** 资料记录与解析正文 SHALL 持久化到 PostgreSQL
- **AND** 该资料 SHALL 标记为可用于下一次考研智能规划

#### Scenario: 上传无法解析的资料
- **WHEN** 文件保存成功但正文为空或文件类型不支持正文提取
- **THEN** 系统 SHALL 持久化资料元数据和解析失败状态
- **AND** 页面 SHALL 告知用户该资料暂不会用于智能规划

### Requirement: 考研规划资料 SHALL 按当前用户隔离
资料的保存、查询和删除 SHALL 使用服务器确认的当前用户身份，并按用户与升学方向隔离。

#### Scenario: 查询自己的考研资料
- **WHEN** 用户请求考研规划资料列表
- **THEN** 系统 SHALL 只返回该用户且方向为考研的资料

#### Scenario: 删除其他用户资料
- **WHEN** 用户提交不属于自己的资料标识
- **THEN** 系统 SHALL 拒绝删除或按不存在处理
- **AND** 其他用户的文件和资料记录 SHALL 保持不变

### Requirement: PostgreSQL SHALL 持久化考研规划资料
CyanCruise SHALL 使用 `cc001.storage.*` 配置的 PostgreSQL 保存考研规划资料，不得以进程内内存作为生产默认持久化。

#### Scenario: 服务重启后读取资料
- **WHEN** 用户上传资料后服务发生重启
- **THEN** 用户 SHALL 仍能在升学中心看到资料元数据和解析状态
- **AND** 下一次考研智能规划 SHALL 能读取已持久化的有效正文
