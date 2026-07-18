## MODIFIED Requirements

### Requirement: Question bank moderation

CyanCruise SHALL expose administrator question bank contracts that include a persistent career assessment catalog, including questions, options, dimensions, pool question count, and the configured answer question count for each scale.

#### Scenario: 管理员维护职业测评题库
- **GIVEN** 当前用户具备管理后台权限
- **WHEN** 管理员新增、编辑或删除职业测评题目
- **THEN** 修改 SHALL 持久化并在服务重启后继续生效
- **AND** 管理页面 SHALL 展示题库总数与实际作答题数

#### Scenario: 管理员配置作答题数
- **WHEN** 管理员将量表作答题数设置为 1 到当前题库总数之间的整数
- **THEN** 系统 SHALL 保存配置并用于后续新建的测评批次

#### Scenario: 非法作答题数
- **WHEN** 管理员设置的作答题数小于 1 或大于当前题库总数
- **THEN** 系统 SHALL 拒绝保存并显示普通中文提示
