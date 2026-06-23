## ADDED Requirements

### Requirement: 提供一体化模拟面试轮次入口
模拟面试核心能力 SHALL 提供开始并生成开场问题、提交回答并生成下一题、结束并读取或生成报告的一体化应用与 Cosmic WebAPI 入口。这些入口 SHALL 复用既有会话所有权、消息排序、报告保存和画像同步规则。

#### Scenario: 一体化开始入口
- **WHEN** 调用方提交用户 ID 和开始面试请求
- **THEN** WebAPI 返回新会话及已保存的第一条面试官消息

#### Scenario: 一体化回答入口
- **WHEN** 调用方提交用户 ID、面试 ID 和回答内容
- **THEN** WebAPI 返回已保存的用户回答和下一条面试官消息

#### Scenario: 一体化结束复盘入口
- **WHEN** 调用方提交用户 ID 和面试 ID结束有回答的面试
- **THEN** WebAPI 返回已保存或新生成的结构化复盘报告

### Requirement: 保持细粒度面试契约兼容
新增一体化入口 SHALL NOT 移除或改变既有开始会话、追加消息、读取消息、结束会话、保存报告、读取报告、历史、详情和删除入口的请求与响应语义。

#### Scenario: 既有调用方继续使用核心接口
- **WHEN** 既有调用方继续调用原有模拟面试 WebAPI
- **THEN** 系统按照归档 `interview-core` 规格处理请求，无需迁移到一体化入口
