## MODIFIED Requirements

### Requirement: 生成考研复习计划
系统 SHALL 根据目标院校、目标专业、初试日期、考试科目和可投入时间生成紧凑且可执行的三轮复习计划。

#### Scenario: 首次任务流结果被截断
- **WHEN** `POSTGRADUATE_PLAN_GENERATE` 首次返回的内容不是完整 JSON
- **THEN** 后端 SHALL 自动重试同一已发布任务流一次
- **AND** 第二次返回完整结果时 SHALL 正常保存并展示复习计划

### Requirement: Persist postgraduate exam companion records
Postgraduate exam WebAPI SHALL save request and successful result for all four analysis tasks as a current-user postgraduate record before returning the result.

#### Scenario: 分析服务失败
- **WHEN** 智能体调用失败或返回不可用结果
- **THEN** 系统 SHALL NOT save a successful analysis record

#### Scenario: 再次进入择校择专业页面
- **WHEN** 当前用户此前已成功生成择校择专业建议并再次进入该页面
- **THEN** 页面 SHALL 按当前用户、`POSTGRADUATE` 方向和 `POSTGRADUATE_SCHOOL_RECOMMEND` 任务类型读取最新一条成功记录
- **AND** 显示该记录中的结构化建议结果

#### Scenario: 再次进入其他考研功能页面
- **WHEN** 当前用户此前已成功生成复习计划或复试准备结果并再次进入对应页面
- **THEN** 页面 SHALL 按当前用户、`POSTGRADUATE` 方向及对应任务类型读取最新一条成功记录
- **AND** 显示该记录中的结构化结果；没有历史记录时保持结果区域为空
