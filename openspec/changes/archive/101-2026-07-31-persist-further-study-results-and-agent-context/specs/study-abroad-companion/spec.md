## MODIFIED Requirements

### Requirement: Persist study abroad companion records
Study abroad companion WebAPI SHALL save request and successful result for all five analysis tasks as a current-user study-abroad record before returning the result.

#### Scenario: 保存留学分析结果
- **WHEN** 用户成功完成任一留学分析
- **THEN** 系统 SHALL 保存本次请求和结构化结果，且记录只属于该用户

#### Scenario: 再次进入留学功能页面
- **WHEN** 当前用户此前已成功生成国家地区、语言考试、选校定位、文书主线或签证网申结果并再次进入对应页面
- **THEN** 页面 SHALL 按当前用户、`STUDY_ABROAD` 方向及对应任务类型读取最新一条成功记录
- **AND** 显示该记录中的结构化结果；没有历史记录时保持结果区域为空
