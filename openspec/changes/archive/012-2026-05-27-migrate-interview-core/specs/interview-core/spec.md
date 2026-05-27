## ADDED Requirements

### Requirement: 管理模拟面试会话
系统 SHALL 支持用户围绕目标岗位开始模拟面试会话。会话 SHALL 包含用户 ID、可选简历 ID、岗位名称、难度、模式、状态、开始时间、结束时间、时长、最终分数和报告摘要。

#### Scenario: 开始文本面试
- **WHEN** 用户以目标岗位和难度开始面试且未指定模式
- **THEN** 系统创建状态为 `ONGOING`、模式为 `TEXT` 的面试会话

#### Scenario: 标准化模式
- **WHEN** 用户指定模式为 `TEXT` 或 `VOICE`
- **THEN** 系统保存对应模式；其他模式 SHALL 回退为 `TEXT`

#### Scenario: 标准化难度
- **WHEN** 用户未指定难度或指定空白难度
- **THEN** 系统使用默认难度 `Normal`

### Requirement: 维护面试消息历史
系统 SHALL 支持向面试会话追加消息，并按创建顺序返回消息历史。消息 SHALL 包含面试 ID、角色、内容和创建时间。角色 SHALL 至少支持 `USER` 和 `AI`。

#### Scenario: 追加用户消息
- **WHEN** 用户向自己的面试会话发送非空内容
- **THEN** 系统保存角色为 `USER` 的消息并保留发送顺序

#### Scenario: 追加 AI 消息
- **WHEN** 应用服务或后续 AI 适配向面试会话追加面试官内容
- **THEN** 系统保存角色为 `AI` 的消息并保留发送顺序

#### Scenario: 拒绝空消息
- **WHEN** 调用方追加空白消息内容
- **THEN** 系统拒绝保存该消息并返回明确错误

### Requirement: 结束面试并计算会话状态
系统 SHALL 支持结束进行中的面试。结束时 SHALL 将状态设置为 `COMPLETED`，记录结束时间，可保存最终分数，并在已有开始时间时计算时长秒数。

#### Scenario: 结束进行中面试
- **WHEN** 用户结束自己的 `ONGOING` 面试
- **THEN** 系统将面试状态更新为 `COMPLETED` 并记录结束时间

#### Scenario: 计算面试时长
- **WHEN** 面试存在开始时间且结束面试
- **THEN** 系统保存从开始到结束的时长秒数

#### Scenario: 结束后写入基础画像
- **WHEN** 面试结束且包含岗位、难度或分数
- **THEN** 系统将最近一次面试摘要写入用户画像 interview block

### Requirement: 保存并读取面试报告摘要
系统 SHALL 支持保存面试报告摘要。报告摘要 SHALL 包含总分、雷达分、优势、改进建议、文本总结和答题轮数。保存报告后 SHALL 更新会话最终分数，并将强项和弱项写入画像 interview block。

#### Scenario: 保存报告
- **WHEN** 调用方提交面试报告摘要
- **THEN** 系统保存报告摘要并用报告总分更新面试最终分数

#### Scenario: 读取已保存报告
- **WHEN** 面试已经保存报告摘要
- **THEN** 系统按面试 ID 返回该报告摘要，不要求重新生成报告

#### Scenario: 提取强弱维度
- **WHEN** 报告雷达分中某维度分数高于强项阈值或低于弱项阈值
- **THEN** 系统将对应维度分别写入画像 strongDimensions 或 weakDimensions

### Requirement: 提供用户历史、详情和删除能力
系统 SHALL 支持按用户 ID 查询面试历史、读取单个面试详情，并删除用户自己的面试及其消息历史。历史列表 SHALL 按开始时间倒序返回。

#### Scenario: 查询历史
- **WHEN** 用户请求自己的面试历史
- **THEN** 系统返回该用户的面试列表并按最近开始时间优先排序

#### Scenario: 删除面试
- **WHEN** 用户删除自己的面试
- **THEN** 系统删除该面试及其消息历史

#### Scenario: 删除不存在的面试
- **WHEN** 用户删除不存在或已删除的面试
- **THEN** 系统返回明确错误

### Requirement: 强制面试所有权校验
系统 SHALL 对面试详情、消息、结束、报告、删除等按 interviewId 操作执行所有权校验。调用方 SHALL NOT 能读取或修改其他用户的面试。

#### Scenario: 拒绝跨用户读取
- **WHEN** 用户请求不属于自己的面试详情或消息
- **THEN** 系统拒绝该请求

#### Scenario: 拒绝跨用户写入
- **WHEN** 用户尝试向不属于自己的面试追加消息、结束、保存报告或删除
- **THEN** 系统拒绝该请求

### Requirement: 暴露模拟面试 Cosmic WebAPI
系统 SHALL 通过 Cosmic WebAPI 暴露模拟面试核心能力。WebAPI SHALL 支持开始面试、追加消息、读取消息、结束面试、保存报告、读取报告、查询历史、读取详情和删除面试。

#### Scenario: WebAPI 开始面试
- **WHEN** 调用方提交用户 ID 和开始面试请求
- **THEN** WebAPI 返回新建面试会话

#### Scenario: WebAPI 追加与读取消息
- **WHEN** 调用方提交用户 ID、面试 ID 和消息内容
- **THEN** WebAPI 保存消息，并允许按面试 ID 读取有序消息历史

#### Scenario: WebAPI 保存报告
- **WHEN** 调用方提交用户 ID、面试 ID 和报告摘要
- **THEN** WebAPI 保存报告并返回更新后的面试会话或报告摘要

### Requirement: 保持可替换的面试存储边界
系统 SHALL 通过面试存储边界读写会话、消息和报告。默认存储实现 SHALL 可在没有 Cosmic datamodel 的情况下通过测试；未来 Cosmic datamodel 适配 SHALL 能替换该边界，而无需修改 helper 或 WebAPI 契约。

#### Scenario: 默认存储可读回
- **WHEN** 默认存储保存面试和消息后创建新的应用服务实例
- **THEN** 新实例可以按用户 ID 和面试 ID 读回面试及消息

#### Scenario: 替换为 Cosmic 存储
- **WHEN** Cosmic datamodel 面试适配器实现完成
- **THEN** 它可以通过同一存储边界替换默认存储
