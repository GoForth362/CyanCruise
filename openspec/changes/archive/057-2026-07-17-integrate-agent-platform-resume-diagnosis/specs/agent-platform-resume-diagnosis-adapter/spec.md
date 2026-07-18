## ADDED Requirements

### Requirement: 服务端调用简历诊断任务流
CyanCruise 服务端 SHALL 通过可替换的 Agent 平台适配器调用配置的简历诊断智能体。适配器 SHALL 使用官方 `bos-ai-sdk`，接收简历正文、目标岗位、岗位要求和画像上下文，并 SHALL 将其作为单一 JSON 请求传给智能体；智能体负责调用关联的简历诊断任务流。

#### Scenario: 平台任务流调用成功
- **WHEN** 简历诊断服务具备有效的智能体编码且智能体返回有效诊断 JSON
- **THEN** 适配器 SHALL 汇总 SDK 聊天消息中的诊断 JSON 供现有简历诊断服务解析

#### Scenario: 平台配置缺失
- **WHEN** 智能体编码缺失
- **THEN** 适配器 SHALL 返回可识别的不可用状态，且 SHALL NOT 向浏览器暴露配置细节或凭据

### Requirement: 平台凭据和用户数据保持服务端边界
Agent 平台地址、鉴权信息和任务流编码 SHALL 只由 CyanCruise 服务端读取。适配器 SHALL NOT 在日志中记录完整简历正文、平台凭据或完整平台响应。

#### Scenario: 用户发起简历诊断
- **WHEN** 已登录用户从 CyanCruise 页面发起简历诊断
- **THEN** 浏览器 SHALL 只调用 CyanCruise 自有接口，且 SHALL NOT 直接调用 Agent 平台

#### Scenario: 记录平台调用失败
- **WHEN** 平台调用失败或超时
- **THEN** 服务端日志 SHALL 仅记录失败类型、任务流标识摘要和调用追踪信息，不记录简历正文或鉴权信息

### Requirement: 校验平台诊断响应
适配器返回的任务流 `answer` SHALL 是合法 JSON，并 SHALL 包含总分、四项评分和结构化修改建议所需字段。服务端 SHALL 对非法 JSON、缺失字段和无效分数执行失败处理。

#### Scenario: 分数不一致
- **WHEN** 任务流返回的四项评分之和与总分不一致，或分项满分不符合既有诊断标准
- **THEN** 服务端 SHALL 将该响应视为无效并执行有限重试
- **AND THEN** 重试仍失败时 SHALL 返回可供用户重试的失败信息，且 SHALL NOT 保存该响应
