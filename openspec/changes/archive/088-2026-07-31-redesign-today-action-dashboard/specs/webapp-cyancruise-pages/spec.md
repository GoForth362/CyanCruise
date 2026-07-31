## MODIFIED Requirements

### Requirement: 今日行动页面跟随当前路线
`today-action` 页面 SHALL 根据当前路线读取、更新并展示对应每日任务，并 SHALL 使用真实任务状态展示今日完成数、剩余数、顺延任务和当前阶段。任务勾选 SHALL 仅影响当前路线。

#### Scenario: 打开今日行动页面
- **WHEN** 用户打开 `today-action` 且当前路线存在每日任务
- **THEN** 页面 SHALL 在任务列表之前展示今日完成数、任务总数、剩余数和当前阶段
- **AND** 页面 SHALL 将今日任务作为主要内容，将本周动作、交付物和路线来源作为辅助内容

#### Scenario: 完成升学今日任务
- **WHEN** 用户在升学上下文勾选今日任务
- **THEN** 页面 SHALL 更新升学每日任务接口
- **AND** 就业任务状态 SHALL 不变

#### Scenario: 显示顺延任务
- **WHEN** 每日任务包含从前一天顺延的未完成任务
- **THEN** 页面 SHALL 在对应任务和今日概览中显示顺延状态

#### Scenario: 在窄屏查看今日行动
- **WHEN** 用户在窄屏设备查看包含长文本的每日和本周任务
- **THEN** 页面 SHALL 使用单列布局并允许任务文本换行
- **AND** 页面 SHALL NOT 产生横向滚动

#### Scenario: 使用键盘勾选任务
- **WHEN** 用户使用键盘聚焦每日或本周任务
- **THEN** 任务控件 SHALL 提供清晰的焦点状态并保持原任务 ID 与完成逻辑
