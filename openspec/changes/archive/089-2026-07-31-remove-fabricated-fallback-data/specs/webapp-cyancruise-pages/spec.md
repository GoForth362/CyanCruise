## ADDED Requirements

### Requirement: 页面只呈现真实业务数据
CyanCruise 页面 SHALL 只呈现 route 对应接口返回的数据、用户已经保存的数据或用户明确输入的草稿。接口失败或空数据时 SHALL 使用可区分的不可用状态或空状态，不得调用本地预览、示例或默认业务数据生成器。

#### Scenario: 测评目录或题目读取失败
- **WHEN** 测评量表目录或题目接口不可用
- **THEN** 页面 SHALL 提示测评内容暂时无法读取并提供重试
- **AND** 页面 SHALL NOT 展示内置预览题目或生成预览测评结果

#### Scenario: 规划或今日行动为空
- **WHEN** 职业规划或今日行动接口返回无记录
- **THEN** 页面 SHALL 展示生成规划或完善信息的真实入口
- **AND** 页面 SHALL NOT 构造默认阶段、默认任务、进度或完成状态

#### Scenario: 资源接口失败
- **WHEN** 就业或升学资源接口失败
- **THEN** 页面 SHALL 展示局部不可用状态
- **AND** 页面 SHALL NOT 展示内置示例文章、岗位或院校资源

#### Scenario: 创建业务记录失败
- **WHEN** 简历、画像或其他业务记录创建失败
- **THEN** 页面 SHALL 保留用户表单输入并提示重试
- **AND** 页面 SHALL NOT 创建 `preview` 记录加入正式列表
