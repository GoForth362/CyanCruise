## MODIFIED Requirements

### Requirement: Persist recommendation companion records
Recommendation companion WebAPI SHALL save request and successful result for all four analysis tasks as a current-user recommendation record before returning the result.

#### Scenario: 保存保研分析结果
- **WHEN** 用户成功完成任一保研分析
- **THEN** 系统 SHALL 保存本次请求和结构化结果，且记录只属于该用户
