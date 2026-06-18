## ADDED Requirements

### Requirement: 文件边界支持 PDF 正文提取
文件文本提取边界 SHALL 在对象 key 以 `.pdf` 结尾时使用 PDF 提取器，在 `.txt` 或 `.md` 时继续使用 UTF-8 纯文本提取器。文件下载失败、解析器不可用和正文为空 SHALL 返回不同的可恢复状态。

#### Scenario: 从 BOS 文件提取 PDF 正文
- **WHEN** 已授权用户请求提取有效 PDF object key 且文件下载成功
- **THEN** 文件服务将下载字节交给 PDF 提取器并返回正文结果

#### Scenario: 文件下载失败
- **WHEN** BOS provider 无法下载指定 object key
- **THEN** 系统返回下载失败或不可用状态，并且不调用 PDF 解析器

