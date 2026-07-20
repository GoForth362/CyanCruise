## MODIFIED Requirements

### Requirement: Persist postgraduate exam companion records
Postgraduate exam WebAPI SHALL save request and successful result for all four analysis tasks as a current-user postgraduate record before returning the result.

#### Scenario: 分析服务失败
- **WHEN** 智能体调用失败或返回不可用结果
- **THEN** 系统 SHALL NOT save a successful analysis record
