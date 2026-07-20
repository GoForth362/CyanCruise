## ADDED Requirements

### Requirement: 测评页面必须使用真实发布量表
测评目录、题目、答案提交和结果 SHALL 只使用服务端真实发布量表及其真实评分结果。前端 SHALL NOT 在目录为空、读取失败或提交失败时生成预览量表、预览答案结果或默认推荐岗位。

#### Scenario: 没有已发布量表
- **WHEN** 服务端返回空测评目录
- **THEN** 页面 SHALL 展示暂无可用测评
- **AND** SHALL NOT 显示内置量表卡片

#### Scenario: 题目读取失败
- **WHEN** 用户进入某量表但题目读取失败
- **THEN** 页面 SHALL 保留返回与重试操作并显示失败原因
- **AND** SHALL NOT 使用本地题目继续答题

#### Scenario: 提交失败
- **WHEN** 测评答案提交或评分失败
- **THEN** 页面 SHALL 保留用户当前选择以便重试
- **AND** SHALL NOT 生成完成记录、画像代码或推荐岗位

#### Scenario: 真实评分无推荐岗位
- **WHEN** 服务端评分结果没有推荐岗位
- **THEN** 页面 SHALL 展示真实画像摘要和空推荐状态
- **AND** SHALL NOT 根据前端规则填入默认岗位
