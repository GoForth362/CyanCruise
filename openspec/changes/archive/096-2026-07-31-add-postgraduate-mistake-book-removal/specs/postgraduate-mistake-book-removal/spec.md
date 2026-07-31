## ADDED Requirements

### Requirement: 用户可以移除自己的错题记录
系统 SHALL 在错题本列表和错题详情页提供“移除错题”操作。系统 SHALL 在用户确认后，从持久化错题本中移除该用户自己的对应记录。

#### Scenario: 从错题本列表移除记录
- **WHEN** 用户确认移除错题本中的一条记录
- **THEN** 系统 SHALL 删除该记录并立即刷新错题本列表

#### Scenario: 从错题详情移除记录
- **WHEN** 用户在错题详情页确认移除当前错题
- **THEN** 系统 SHALL 删除该记录并返回错题本列表

### Requirement: 错题删除必须受用户身份约束
系统 SHALL 仅删除 `userId` 与 `mistakeId` 同时匹配的记录；不得删除或泄露其他用户的错题记录。

#### Scenario: 尝试移除不属于当前用户的记录
- **WHEN** 用户提交不属于自己的错题记录标识
- **THEN** 系统 SHALL 拒绝该操作并返回普通中文提示
