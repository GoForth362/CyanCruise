## MODIFIED Requirements

### Requirement: 生成保研背景提升计划
系统 SHALL 根据当前年级、学校、专业、成绩排名和已提供背景生成紧凑、结构化的背景提升行动计划。

#### Scenario: 首次任务流结果被截断
- **WHEN** `RECOMMENDATION_PLAN_GENERATE` 首次返回的内容不是完整 JSON
- **THEN** 后端 SHALL 自动重试同一已发布任务流一次
- **AND** 第二次返回完整结果时 SHALL 正常保存并展示背景提升计划

### Requirement: Persist recommendation companion records
Recommendation companion WebAPI SHALL save request and successful result for all four analysis tasks as a current-user recommendation record before returning the result.

#### Scenario: 保存保研分析结果
- **WHEN** 用户成功完成任一保研分析
- **THEN** 系统 SHALL 保存本次请求和结构化结果，且记录只属于该用户

#### Scenario: 再次进入保研功能页面
- **WHEN** 当前用户此前已成功生成排名监控、背景提升、材料精修或导师联系结果并再次进入对应页面
- **THEN** 页面 SHALL 按当前用户、`RECOMMENDATION` 方向及对应任务类型读取最新一条成功记录
- **AND** 显示该记录中的结构化结果；不得显示其他用户、其他功能或过期的本地结果

### Requirement: 导师联系区分本科院校与目标院校
系统 SHALL 在导师联系表单和 `RECOMMENDATION_TUTOR_LETTER` 请求中分别使用 `currentSchool` 表示用户本科就读院校、使用 `targetSchool` 表示拟申请导师所在院校。

#### Scenario: 生成导师意向信
- **WHEN** 用户填写本科就读院校和目标院校并生成导师意向信
- **THEN** 邮件正文 SHALL 仅把 `currentSchool` 描述为用户本科就读院校
- **AND** SHALL 仅把 `targetSchool` 描述为拟申请或咨询的院校
- **AND** 不得用其中一个字段替代、推断或覆盖另一个字段
