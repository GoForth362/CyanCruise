## ADDED Requirements

### Requirement: 具体升学方向数据 SHALL 独立持久化
系统 SHALL 使用用户 ID 与具体升学方向共同隔离规划、每日任务和规划资料，考研、保研及留学记录不得相互覆盖。

#### Scenario: 同一用户先后生成考研和保研规划
- **WHEN** 用户已有考研规划后生成保研规划
- **THEN** PostgreSQL SHALL 保存两条不同方向的规划记录
- **AND** 两份规划及版本 SHALL 分别可读

#### Scenario: 跨实例恢复保研规划
- **WHEN** 用户生成保研规划和今日行动后服务重新启动
- **THEN** 新服务实例 SHALL 按用户与 `RECOMMENDATION` 方向恢复规划、任务及完成状态

#### Scenario: 不同用户使用同一方向
- **WHEN** 用户 A 和用户 B 都生成保研规划
- **THEN** 任一用户 SHALL NOT 读取或更新另一用户的规划、资料和任务

### Requirement: 历史考研数据 SHALL 在复合键迁移后保持可读
系统 SHALL 将旧的仅按用户保存的升学规划和任务迁移到其原有方向；无法识别方向的历史数据 SHALL 按既有考研语义归入 `POSTGRADUATE`，不得复制成保研数据。

#### Scenario: 升级已有考研环境
- **WHEN** 数据库存在旧主键结构和考研规划
- **THEN** 初始化迁移 SHALL 保留考研规划与任务状态
- **AND** 新增保研规划 SHALL NOT 覆盖迁移后的考研数据
