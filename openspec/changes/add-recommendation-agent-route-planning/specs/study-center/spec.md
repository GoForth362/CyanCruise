## ADDED Requirements

### Requirement: 升学中心 SHALL 展示当前方向的独立规划状态
升学中心 SHALL 根据已保存的考研或保研方向读取对应规划、资料和今日行动，不得复用另一方向的数据填充页面。

#### Scenario: 从考研切换到保研
- **WHEN** 用户保存保研方向并重新打开升学中心
- **THEN** 页面 SHALL 展示保研资料数量和保研规划状态
- **AND** 之前的考研规划 SHALL 保持可在切回考研后恢复

### Requirement: 升学中心 SHALL 管理保研规划依据资料
系统 SHALL 允许当前用户在保研方向上传、查看和删除成绩排名、科研竞赛、夏令营通知、材料要求及其他保研规划资料。

#### Scenario: 上传可读取的保研资料
- **WHEN** 用户在保研方向上传资料且正文读取成功
- **THEN** 资料 SHALL 标记为可用于下一次保研智能规划
- **AND** 资料 SHALL 按当前用户和 `RECOMMENDATION` 方向持久化

#### Scenario: 删除保研资料
- **WHEN** 用户删除一份自己的保研资料
- **THEN** 系统 SHALL 只删除匹配当前用户与保研方向的资料
- **AND** 同一用户的考研资料 SHALL 保持不变
