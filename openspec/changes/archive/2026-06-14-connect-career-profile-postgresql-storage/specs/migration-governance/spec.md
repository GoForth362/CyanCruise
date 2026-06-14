## ADDED Requirements

### Requirement: 使用 CyanCruise 作为当前项目正式名称
所有新增 OpenSpec 文档、设计说明、任务清单、页面文案、提交说明和实现注释 SHALL 将当前项目称为 CyanCruise。CareerLoop 只能作为 IPD 历史来源、旧业务闭环名称、遗留类名或历史路径的一部分出现，不得把当前项目称为 CareerLoop。

#### Scenario: 新增规格文档
- **WHEN** 创建或更新新的 OpenSpec change、主规格或迁移说明
- **THEN** 文档 SHALL 使用 CyanCruise 指代当前项目
- **AND** 文档 MAY 在说明 IPD 历史来源、旧领域命名或遗留代码标识时提到 CareerLoop

#### Scenario: 新增用户可见文案
- **WHEN** 新增或修改 webapp 页面标题、按钮、提示、空状态或错误信息
- **THEN** 用户可见文案 SHALL 使用 CyanCruise 或具体业务能力名称，不得把当前系统称为 CareerLoop

#### Scenario: 解释遗留类名
- **WHEN** 文档需要引用 `CareerProfileStorage`、`CareerPlanApplicationService` 等已有类名、接口名或 route metadata
- **THEN** 文档 SHALL 将这些名称视为历史领域命名或代码标识，而不是当前项目名称
