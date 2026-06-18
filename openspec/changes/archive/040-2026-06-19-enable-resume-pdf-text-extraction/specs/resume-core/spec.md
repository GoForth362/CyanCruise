## ADDED Requirements

### Requirement: 保存 PDF 提取的简历正文
简历创建和更新能力 SHALL 支持把 PDF 提取的正文保存到 `parsedContent`。自动提取 SHALL NOT 用空文本覆盖已有非空正文，更新操作 SHALL 继续执行用户所有权校验。

#### Scenario: 创建简历时保存正文
- **WHEN** 用户上传 PDF 并成功提取正文后创建简历
- **THEN** 简历记录同时保存稳定 `fileKey` 和提取后的 `parsedContent`

#### Scenario: 已有简历回写正文
- **WHEN** 用户拥有的简历有 `fileKey` 但 `parsedContent` 为空且重新提取成功
- **THEN** 系统把提取正文更新到该简历记录

#### Scenario: 拒绝空正文覆盖
- **WHEN** PDF 提取结果为空或失败而简历已有非空 `parsedContent`
- **THEN** 系统保留原正文不变

