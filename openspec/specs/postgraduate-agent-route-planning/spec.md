# postgraduate-agent-route-planning Specification

## Purpose
TBD - created by archiving change integrate-postgraduate-agent-document-planning. Update Purpose after archive.
## Requirements
### Requirement: 考研路线 SHALL 通过已配置的金蝶智能服务生成

CyanCruise SHALL 在用户明确生成考研路线时，通过服务器端金蝶智能体 SDK 调用已发布的考研规划任务流。任务流编码存在时 SHALL 优先直连任务流；智能体编码仅作为兼容回退。配置 SHALL 来自运行时且 SHALL NOT 暴露给浏览器。

#### Scenario: 对话智能体只产生工具调用

- **WHEN** 对话智能体返回 `Thought`、`Action` 和 `Action_input`，但没有任务流最终 `answer`
- **THEN** 后端 SHALL 忽略工具调用参数中的用户输入回显
- **AND** SHALL NOT 将该响应保存为考研路线
- **AND** SHALL 提示管理员配置已发布任务流编码或修复智能体工具输出

#### Scenario: 已配置考研任务流

- **WHEN** 运行时配置包含有效考研任务流编码
- **THEN** 后端 SHALL 直接调用该任务流
- **AND** SHALL 使用任务流最终输出进行结构校验

### Requirement: 考研智能体 SHALL 使用服务器聚合的定制资料
CyanCruise SHALL 由后端聚合当前日期、当前用户画像、考研方向、目标院校、当前路线进度和该用户已成功解析的考研资料作为任务流输入。浏览器提交的用户标识或画像快照 SHALL NOT 覆盖服务器身份和已持久化画像。

#### Scenario: 用户有可用考研资料
- **WHEN** 当前用户已上传并成功解析目标院校招生材料、专业目录或个人学习证明
- **THEN** 后端 SHALL 将资料标题、类型、正文摘要和来源标识加入该用户的考研规划输入
- **AND** 生成路线 SHALL 仍以用户画像和目标院校为主要约束

#### Scenario: 用户没有可用资料
- **WHEN** 当前用户没有资料或资料正文无法提取
- **THEN** 后端 SHALL 仍可使用用户画像和目标院校生成路线
- **AND** 输入 SHALL 明确资料缺失，智能体 SHALL NOT 编造具体招生条件或日期
- **AND** 可选信息缺失 SHALL 被写入待确认信息和首阶段核验行动，而 SHALL NOT 导致任务流拒绝生成暂行路线

#### Scenario: 用户画像包含运行期时间字段
- **WHEN** 用户画像快照包含更新时间、测评完成时间或画像生成时间等 Java 时间字段
- **THEN** 系统 SHALL 只提取升学规划需要的稳定画像结论和扁平字段
- **AND** 系统 SHALL NOT 因直接序列化完整画像快照而阻断任务流调用

### Requirement: 智能路线与每日行动 SHALL 持久化并保护已有进度
成功生成的考研路线 SHALL 保存到独立升学路线存储，每日行动 SHALL 按当前有效阶段逐日生成并持久化完成状态。重新生成 SHALL 只替换未开始阶段。

#### Scenario: 首次生成后再次打开应用
- **WHEN** 用户生成路线并完成部分每日行动后在另一天重新打开应用
- **THEN** 系统 SHALL 读取持久化路线和完成状态
- **AND** 当日行动 SHALL 从当前有效阶段的剩余事项继续推进

#### Scenario: 刷新包含进行中阶段的路线
- **WHEN** 用户确认重新生成且已有阶段处于进行中或已完成状态
- **THEN** 系统 SHALL 保留这些阶段及其执行进度
- **AND** 系统 SHALL 仅使用智能体结果更新尚未开始的阶段

### Requirement: 考研智能路线 SHALL 覆盖未来一年
CyanCruise SHALL 将考研智能路线校验为覆盖未来十二个月的连续多阶段规划，而不是把一个月的短期安排当作完整主线图保存。

#### Scenario: 任务流首次只返回一个月
- **WHEN** 考研任务流首次返回少于三个阶段，或所有阶段均未覆盖第十二个月
- **THEN** 后端 SHALL 携带“一年、至少三个连续阶段”的结构约束自动请求任务流修正一次
- **AND** 只有修正结果通过结构校验后才 SHALL 持久化
- **AND** 修正仍失败时 SHALL 保留原路线并返回普通用户可理解的错误

#### Scenario: 历史单阶段路线已经开始
- **WHEN** 用户已有一个进行中或已完成的短期阶段，但该路线尚未覆盖未来一年
- **THEN** 后端 SHALL 将该不完整路线及其执行进度作为无效历史数据清理
- **AND** 页面 SHALL 回到尚未生成真实路线的状态，或在重新生成成功后整体展示新的完整路线

#### Scenario: 历史多阶段路线只包含十二月但未覆盖十二个月
- **WHEN** 历史考研路线包含至少三个阶段且某个实际日期落在十二月，但阶段首尾未连续覆盖未来十二个月
- **THEN** 后端 SHALL NOT 将日期中的“十二月”当作“第十二个月”
- **AND** 后端 SHALL 删除该不完整路线及其派生的每日任务
- **AND** 页面生成按钮 SHALL 显示“生成考研规划”

### Requirement: 考研路线 SHALL NOT 使用规则兜底内容
CyanCruise SHALL 只展示和持久化通过结构校验的真实考研智能体路线。系统 SHALL NOT 在没有真实路线时自动生成阶段、周计划、每日建议或完成状态。

#### Scenario: 用户尚未生成真实考研路线
- **WHEN** 当前用户没有已通过校验的考研智能体路线
- **THEN** 路线摘要 SHALL 返回未生成状态
- **AND** 页面 SHALL 展示生成入口和可恢复提示，不得展示规则拼装的阶段路线图

#### Scenario: 读取历史规则兜底路线
- **WHEN** 存储中存在 `RULE_FALLBACK`、`FALLBACK_READY` 或 `study-rule-fallback` 标记的升学路线
- **THEN** 后端 SHALL 删除该虚假路线及其派生的每日任务
- **AND** 页面 SHALL 回到尚未生成真实路线的状态

#### Scenario: 读取缺少来源标记的历史单阶段考研路线
- **WHEN** 存储中的考研路线缺少规则兜底标记，但未标记为 `AGENT` 和 `AGENT_GENERATED`，或少于三个阶段
- **THEN** 后端 SHALL 将该路线视为无效历史数据并删除路线及其派生的每日任务
- **AND** 该路线中即使存在进行中或已完成状态也 SHALL NOT 被带入下一次真实智能体生成结果
- **AND** 页面 SHALL 以服务端返回的新路线或空状态整体替换本地旧路线

#### Scenario: 智能体返回缺失业务字段
- **WHEN** 智能体结果缺少路线目标、情况摘要、完整阶段、周计划、每周重点或每日行动
- **THEN** 后端 SHALL 拒绝保存并返回普通中文错误
- **AND** 后端 SHALL NOT 使用规则内容补齐缺失字段

