## MODIFIED Requirements

### Requirement: Question Bank Management

管理后台 SHALL 提供“题库管理”入口，并将题库分为“面试题库”和“职业测评题库”。

#### Scenario: Manage interview questions

- **GIVEN** 当前用户具备管理后台权限
- **WHEN** 管理员进入“题库管理”的“面试题库”
- **THEN** 页面 SHALL 展示面试题列表
- **AND** 管理员 SHALL be able to 新增、编辑、发布、隐藏和删除面试题

#### Scenario: Manage assessment question catalog

- **GIVEN** 当前用户具备管理后台权限
- **WHEN** 管理员进入“题库管理”的“职业测评题库”
- **THEN** 页面 SHALL 展示现有职业测评量表、题目数量、题目文本、维度和选项
- **AND** 管理员 SHALL be able to 新增、编辑和删除职业测评题目
- **AND** 页面 SHALL 说明职业测评题库当前保存在应用运行期题库中

#### Scenario: Hide implementation details from users

- **GIVEN** 管理端或客户端操作失败
- **WHEN** 页面展示错误提示
- **THEN** 提示文案 SHALL 使用普通中文说明问题
- **AND** 提示文案 SHALL NOT 暴露接口路径、TraceId 或 Java 类名
