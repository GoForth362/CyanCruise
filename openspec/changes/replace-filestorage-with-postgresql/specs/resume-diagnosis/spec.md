# Resume Diagnosis Delta

## MODIFIED Requirements
### Requirement: PostgreSQL 持久化简历诊断与关键词状态
CyanCruise 简历诊断结果和关键词状态 SHALL 在运行时通过 PostgreSQL 持久化，而不是默认写入 `filestorage/resume-diagnosis`。

#### Scenario: 诊断结果跨实例读取
- **WHEN** 用户完成简历诊断并创建新的应用服务实例
- **THEN** 新实例 SHALL 从 PostgreSQL 读取同一 resumeId 的诊断结果

#### Scenario: 关键词状态跨实例读取
- **WHEN** 用户触发关键词抽取并保存 READY、EMPTY、FAILED 或其他状态
- **THEN** 后续实例 SHALL 从 PostgreSQL 读取该状态和关键词载荷

#### Scenario: 诊断仍遵守简历所有权
- **WHEN** 用户尝试读取、触发或回写不属于自己的 resumeId
- **THEN** 系统 SHALL 拒绝该操作，并且 PostgreSQL 中其他用户数据保持不变
