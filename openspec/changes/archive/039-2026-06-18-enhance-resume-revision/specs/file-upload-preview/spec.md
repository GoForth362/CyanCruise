## ADDED Requirements

### Requirement: 简历诊断复用文件边界
简历诊断页面 SHALL 复用现有文件上传预览和文本提取边界。页面 MAY 为已有 `fileKey` 请求预览 URL 或文本提取，但文件 adapter unavailable、skipped 或 failed SHALL 只影响文件局部面板，不得阻断已有简历诊断和诊断建议查看。

#### Scenario: 文件预览可用
- **WHEN** 用户选择的简历包含 `fileKey` 且文件 adapter 返回预览 URL
- **THEN** 页面提供打开或查看预览的入口，并继续允许诊断

#### Scenario: 文件预览不可用
- **WHEN** 文件 adapter 无法生成预览 URL
- **THEN** 页面显示预览不可用状态，但仍允许用户基于已保存简历文本或手工粘贴文本发起诊断

### Requirement: 不迁移 IPD 文件运行时
简历诊断能力 SHALL NOT 直接迁移 IPD 的 Spring Multipart、Aliyun OSS SDK、PDFBox、Flyway SQL、Vue 或 uni-app 文件选择实现。CyanCruise SHALL 只迁移稳定 object key、文本提取结果、预览失败降级和业务流程语义。

#### Scenario: 依赖审查
- **WHEN** 审查本 change 的依赖和实现
- **THEN** 不应出现新增 PDFBox、OSS SDK、Spring Multipart、Vue 或 uni-app 运行时依赖
