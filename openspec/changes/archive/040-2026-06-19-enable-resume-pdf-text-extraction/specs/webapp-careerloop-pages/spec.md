## ADDED Requirements

### Requirement: 页面展示 PDF 正文提取状态
简历上传和诊断页面 SHALL 分别展示文件上传、PDF 正文提取、简历记录保存和诊断状态。页面 SHALL NOT 把“PDF 已上传”描述成“正文已读取”。

#### Scenario: 上传并提取成功
- **WHEN** PDF 上传和正文提取均成功
- **THEN** 页面提示正文已读取，并在创建简历时提交 `parsedContent`

#### Scenario: 扫描版 PDF
- **WHEN** PDF 上传成功但没有可提取正文
- **THEN** 页面说明当前 PDF 可能只有图片，并提供粘贴简历正文的入口

#### Scenario: 已有 PDF 重新读取正文
- **WHEN** 用户选择正文为空但有关联 PDF 的已有简历
- **THEN** 页面允许触发重新读取正文，并在成功后继续诊断而不要求重新上传文件

