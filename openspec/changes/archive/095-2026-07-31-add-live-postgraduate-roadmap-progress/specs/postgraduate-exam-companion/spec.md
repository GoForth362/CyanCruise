## ADDED Requirements

### Requirement: 考研陪伴入口显示当前路线阶段
考研陪伴入口 SHALL 在“你的行动路线”区域显示当前用户的实时阶段位置，并使阶段工具卡同步呈现已完成、当前进行中和待完成状态。

#### Scenario: 打开考研陪伴入口
- **WHEN** 用户打开 `postgraduate` 路由
- **THEN** 页面 SHALL 同步该用户的路线进度，并以清晰中文说明当前所处阶段
