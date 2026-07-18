## ADDED Requirements

### Requirement: 首页按当前路线展示规划摘要
首页 SHALL 根据持久化当前路线优先展示对应的目标、今日行动和路径规划摘要。另一条路线的数据 SHALL 保留但不作为当前摘要展示。

#### Scenario: 首页当前路线为升学
- **WHEN** 用户保存升学为当前路线并返回首页
- **THEN** 首页目标区域 SHALL 展示目标院校或具体升学方向
- **AND** 今日行动与路径规划入口 SHALL 使用升学规划数据

#### Scenario: 首页切回就业
- **WHEN** 用户把当前路线切回就业
- **THEN** 首页 SHALL 恢复就业目标、就业今日行动和就业路径规划摘要
- **AND** 升学规划 SHALL 保持可在再次切换后恢复

### Requirement: 路线切换不触发隐式覆盖
用户在首页切换当前路线时，webapp SHALL 只保存当前路线选择并重新加载对应摘要，不得自动覆盖另一条路线规划。

#### Scenario: 保存路线切换
- **WHEN** 用户从就业切换为升学并保存自画像
- **THEN** webapp SHALL 保存 `study` 当前路线
- **AND** SHALL NOT 调用就业规划保存或清空接口
