## ADDED Requirements

### Requirement: 面试问题临时保底
当真实 AI provider 未配置、调用失败、超时或未返回有效问题时，系统 SHALL 使用目标岗位、难度和当前题号生成可回答的中文基础问题。问题 SHALL 标记为临时业务保底且 SHALL NOT 被记录为真实 AI provider 输出。

#### Scenario: 开场问题生成失败
- **WHEN** 用户开始 AI 模拟面试或全景仿真面试且真实 AI 未返回有效开场问题
- **THEN** 系统 SHALL 保存并返回围绕目标岗位的基础开场问题
- **AND** 会话 SHALL 保持可回答状态

#### Scenario: 追问生成失败
- **WHEN** 用户保存有效回答但真实 AI 未返回下一题
- **THEN** 系统 SHALL 根据当前题号返回下一道基础问题
- **AND** 已保存回答 SHALL NOT 因 AI 失败被回滚或重复保存

#### Scenario: 真实 AI 返回有效问题
- **WHEN** 真实 AI 成功返回非空且有效的面试问题
- **THEN** 系统 SHALL 使用真实 AI 问题
- **AND** SHALL NOT 调用临时问题保底

### Requirement: 面试复盘临时保底
当至少保存一条有效回答但真实 AI 报告不可用时，系统 SHALL 生成并保存结构化基础规则复盘，使面试可以完成。基础复盘 SHALL 使用确定性规则，包含流程所需字段并明确标记来源，SHALL NOT 宣称执行了真实 AI 深度分析。

#### Scenario: 结束时 AI 报告不可用
- **WHEN** 用户结束至少包含一条有效回答的面试且真实 AI 报告生成失败或无效
- **THEN** 系统 SHALL 完成会话并保存基础规则复盘
- **AND** 结果 SHALL 明确显示“基础规则复盘”来源

#### Scenario: 零回答面试退出
- **WHEN** 用户未保存任何有效回答便结束或离开面试
- **THEN** 系统 SHALL 删除该会话
- **AND** SHALL NOT 生成基础复盘或保存进行中记录

#### Scenario: 真实 AI 报告有效
- **WHEN** 真实 AI 返回通过结构校验的面试报告
- **THEN** 系统 SHALL 保存真实 AI 报告
- **AND** SHALL NOT 使用基础规则复盘覆盖报告

### Requirement: 保底实现可撤除且范围受限
临时保底 SHALL 集中在面试业务边界，并 SHALL NOT 改变其他 AI 场景或 provider gateway 的失败语义。

#### Scenario: 其他 AI 场景失败
- **WHEN** 简历诊断、职业规划、测评或助手聊天的 AI 调用失败
- **THEN** 本面试保底 SHALL NOT 为这些场景生成替代结果

#### Scenario: Provider 不可用
- **WHEN** provider gateway 判定模型不可用
- **THEN** gateway SHALL 保留真实不可用状态供日志和监控使用
- **AND** 仅面试业务适配层 MAY 将该失败转换为明确标记的临时保底结果
