## ADDED Requirements

### Requirement: 两个中心提供一致的规划核心操作
就业中心和升学中心 SHALL 均展示路线摘要、完整规划入口和生成或更新未开始阶段操作。升学中心 SHALL 在已选择具体方向后启用规划操作。

#### Scenario: 对比两个中心规划操作
- **WHEN** 用户分别打开就业中心和已选择方向的升学中心
- **THEN** 两个页面 SHALL 均显示“完整规划”和规划生成或更新操作

#### Scenario: 升学方向未选择
- **WHEN** 用户打开升学中心但未选择具体方向
- **THEN** 升学规划操作 SHALL 禁用或提示先选择方向

### Requirement: 路径规划页面跟随当前路线
`career-plan` 页面 SHALL 根据当前路线展示就业或升学完整规划，并 SHALL 在标题和目标信息中明确当前上下文。

#### Scenario: 当前路线为升学
- **WHEN** 当前路线为升学且用户打开路径规划
- **THEN** 页面 SHALL 展示当前升学方向的阶段、每日建议和本周计划
- **AND** 页面 SHALL 使用目标院校而不是目标岗位作为目标字段

### Requirement: 今日行动页面跟随当前路线
`today-action` 页面 SHALL 根据当前路线读取、更新并展示对应每日任务，任务勾选 SHALL 仅影响当前路线。

#### Scenario: 完成升学今日任务
- **WHEN** 用户在升学上下文勾选今日任务
- **THEN** 页面 SHALL 更新升学每日任务接口
- **AND** 就业任务状态 SHALL 不变
