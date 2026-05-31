## ADDED Requirements

### Requirement: 简历工作流可选文件上传
文件上传预览能力 SHALL 能被简历页作为可选增强使用。简历页 MAY 调用 `/cc001/files/upload` 获得稳定 object key，但文件上传失败、adapter disabled 或 provider unavailable SHALL NOT 阻断 `/cc001/resume/create` 的元数据创建流程。

#### Scenario: 文件上传成功填充 fileKey
- **WHEN** 用户在简历页选择可读取的小文件并触发上传
- **THEN** 页面 SHALL 调用 `/cc001/files/upload`，并在成功后把返回的 object key 填入简历创建表单的 fileKey 字段

#### Scenario: 文件上传不可用
- **WHEN** `/cc001/files/upload` 返回 unavailable、skipped、failed 或平台错误
- **THEN** 页面 SHALL 显示文件上传不可用提示，并保留用户继续手工填写 fileKey 或只创建简历元数据的能力

#### Scenario: 不要求先上传文件
- **WHEN** 用户未选择文件但填写了简历标题、目标岗位或解析内容
- **THEN** 页面 SHALL 允许继续调用 `/cc001/resume/create` 创建简历记录

### Requirement: 简历文件引用即时预览
文件上传预览能力 SHALL 支持简历页针对已有 fileKey 请求短期预览 URL。预览 URL 获取失败 SHALL 被视为局部文件面板失败，不影响简历列表展示或创建流程。

#### Scenario: 请求简历预览 URL
- **WHEN** 简历记录包含非空 fileKey 且用户请求预览
- **THEN** 页面 SHALL 调用 `/cc001/files/preview-url` 并展示返回的短期预览 URL 或平台临时引用

#### Scenario: 预览不可用
- **WHEN** 文件 adapter 无法生成预览 URL
- **THEN** 页面 SHALL 显示预览不可用状态，并继续展示简历记录、fileKey 和其他操作入口

### Requirement: 文件字段不保存敏感凭据
简历工作流使用文件能力时 SHALL 只保存稳定 object key 或平台文件 ID，不得把 access token、Authorization header、预签名 URL 签名参数或客户私有凭据写入简历记录、route metadata 或 OpenSpec 文档。

#### Scenario: 上传返回临时 URL
- **WHEN** 文件 provider 返回包含签名参数的临时 URL 或绝对引用
- **THEN** 系统 SHALL 规范化并保存稳定 object key 或平台文件 ID，而 SHALL NOT 把签名 URL 作为长期简历 fileKey 保存

#### Scenario: 文档和元数据审查
- **WHEN** 审查 `careerloop-routes.json`、OpenSpec artifacts 或迁移文档
- **THEN** 其中 SHALL 只包含属性名、API 路径、fallback 和验证方式，不包含真实 token、Authorization header、endpoint secret 或预签名 URL
