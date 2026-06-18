## ADDED Requirements

### Requirement: 诊断前补齐 PDF 正文
按 `resumeId` 发起诊断时，系统 SHALL 在所有权校验后优先读取已有 `parsedContent`；仅当正文为空且存在 `fileKey` 时，系统 SHALL 尝试通过文件文本提取边界读取 PDF 正文，成功后回写简历记录并继续既有诊断流程。

#### Scenario: 缺少正文但 PDF 可提取
- **WHEN** 用户自己的简历正文为空、存在 PDF `fileKey` 且提取成功
- **THEN** 系统保存提取正文并基于该正文生成诊断结果

#### Scenario: 扫描版 PDF 无正文
- **WHEN** 简历 PDF 只有图片且未提供手工简历正文
- **THEN** 系统不生成伪造诊断，并提示用户当前需要粘贴正文或等待图片文字识别能力

#### Scenario: 已有正文不重复提取
- **WHEN** 简历记录已有非空 `parsedContent`
- **THEN** 系统直接使用已有正文诊断，不重新下载或解析 PDF

