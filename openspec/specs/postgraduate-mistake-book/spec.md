# postgraduate-mistake-book Specification

## Purpose
TBD - created by archiving change add-mistake-book-storage. Update Purpose after archive.
## Requirements
### Requirement: 保存全部用户错题
系统 SHALL 在错题解析成功后，为当前用户创建独立错题本记录。记录 SHALL 保存科目、题目文本、错误答案、结构化解析结果、创建时间和更新时间，且不得以固定数量覆盖或截断该用户既有错题。

#### Scenario: 解析成功后写入错题本
- **WHEN** 用户提交有效错题并获得解析结果
- **THEN** 系统为该用户保存一条独立错题本记录
- **AND** 记录包含本次题目、错误答案和解析结果

### Requirement: 分页查看全部错题
系统 SHALL 提供仅查询当前用户错题本记录的分页接口，并按更新时间倒序返回。调用方 SHALL 使用分页参数逐页获取全部历史记录，不得受通用深造记录展示数量限制。

#### Scenario: 加载下一页错题
- **WHEN** 用户请求错题本的指定 offset 和 limit
- **THEN** 系统返回该页错题摘要及是否仍有下一页

### Requirement: 查看历史错题解析
系统 SHALL 允许当前用户读取自己某条错题的完整题目、错误答案和解析结果，并拒绝读取其他用户记录。

#### Scenario: 打开本人历史错题
- **WHEN** 用户请求自己的错题记录详情
- **THEN** 系统返回完整错题与解析内容

#### Scenario: 请求其他用户错题
- **WHEN** 用户请求不属于自己的错题记录
- **THEN** 系统不返回该记录并提供清晰中文提示

