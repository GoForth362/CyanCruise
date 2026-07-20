## MODIFIED Requirements

### Requirement: Persist study abroad companion records
Study abroad companion WebAPI SHALL save request and successful result for all five analysis tasks as a current-user study-abroad record before returning the result.

#### Scenario: 保存留学分析结果
- **WHEN** 用户成功完成任一留学分析
- **THEN** 系统 SHALL 保存本次请求和结构化结果，且记录只属于该用户
