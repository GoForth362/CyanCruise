## ADDED Requirements

### Requirement: 提取文本型 PDF 正文
系统 SHALL 使用 JDK 1.8 兼容的 PDF 解析器从已授权下载的 PDF 字节提取纯文本，并 SHALL 将返回正文限制在 20,000 字以内。解析器 SHALL 在处理完成后关闭 PDF 文档资源。

#### Scenario: 提取文本型 PDF
- **WHEN** 文件字节是包含可搜索文字的有效 PDF
- **THEN** 系统返回非空正文、实际字符数和成功状态

#### Scenario: 正文超过上限
- **WHEN** PDF 提取正文超过 20,000 字
- **THEN** 系统只返回前 20,000 字并标记结果已截断

### Requirement: 区分无法提取的 PDF
系统 SHALL 区分扫描版或纯图片 PDF、加密或损坏 PDF以及不支持文件类型，不得将空正文伪装为可诊断内容。

#### Scenario: 扫描版 PDF
- **WHEN** PDF 有页面但没有可提取文字
- **THEN** 系统返回明确的无正文状态并提示当前不支持图片文字识别

#### Scenario: 损坏或加密 PDF
- **WHEN** PDFBox 无法打开或读取 PDF
- **THEN** 系统返回失败状态和脱敏原因，并且不保存空正文

### Requirement: PDF 解析依赖保持受控
系统 SHALL 固定使用 `org.apache.pdfbox:pdfbox:2.0.31`，不得迁移 IPD 的 Spring、OSS、Lombok 或 AI 运行时依赖。构建 SHALL 使用仓库 `gradlew.bat` 并兼容 JDK 1.8。

#### Scenario: 审查运行依赖
- **WHEN** 审查 Gradle 依赖和实现源码
- **THEN** PDF 提取能力只新增 PDFBox 2.0.31 及其必要传递依赖，不引入 IPD 应用框架

