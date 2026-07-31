## MODIFIED Requirements

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
