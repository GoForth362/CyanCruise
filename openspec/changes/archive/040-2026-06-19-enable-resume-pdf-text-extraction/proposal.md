## Why

当前简历记录可以关联和预览 PDF，但 BOS 文件适配器不支持正文提取，默认提取器也只读取 `.txt`/`.md`。因此“基于用户已上传简历进行诊断”的主链路在 PDF 到正文之间中断，用户只能手工粘贴文本，不能满足简历诊断的基本产品定义。

## What Changes

- 新增 JDK 1.8 兼容的 PDF 正文提取适配器，使用 IPD 已验证的 Apache PDFBox `2.0.31` 从已下载 PDF 字节中提取文本，并限制最多 20,000 字。
- 扩展现有文件文本提取边界：文本型 PDF 返回正文；扫描版、加密、损坏或无正文 PDF 返回可解释状态，不伪造内容。
- 打通“上传 PDF -> 提取正文 -> 创建/更新简历记录 -> 基于正文诊断”的闭环；已有简历缺少 `parsedContent` 时允许重新提取并回写。
- 保持 `/cc001/files/extract-text`、简历和诊断主契约兼容，不搬迁 IPD Spring Boot、OSS、Lombok、Vue 或 AI 调用实现。
- 修正页面状态，使用户可以区分上传成功、正文提取成功、扫描版需 OCR 和提取失败。

## Capabilities

### New Capabilities

- `resume-pdf-text-extraction`: 定义 JDK 8 PDF 正文提取、限长、失败分类、安全边界和依赖约束。

### Modified Capabilities

- `file-upload-preview`: 文件文本提取边界增加可搜索 PDF 正文提取能力和明确降级状态。
- `resume-core`: 简历创建或更新可以保存从 PDF 提取的 `parsedContent`，已有记录可安全回写正文。
- `resume-diagnosis`: 按 `resumeId` 诊断时可在正文缺失且文件存在时提取并回写，再进入既有诊断流程。
- `webapp-careerloop-pages`: 简历上传与诊断页面展示真实提取状态，并提供已有 PDF 重新读取正文的入口。

## Impact

- IPD 来源：`F:\Project\IPD\backend\src\main\java\com\group1\career\utils\PdfTextExtractor.java`、`ResumeDiagnosisController.java` 和 `backend\pom.xml` 中 PDFBox `2.0.31` 约束。
- CyanCruise 目标模块：`code/base/v620-cc001-base-common/` 的状态契约，`code/cloud01/v620-cc001-cloud01-app01/` 的提取器、应用服务和 WebAPI，`webapp/isv/v620/cyancruise` 的上传/诊断交互。
- 数据映射：IPD PDF bytes -> CyanCruise 文件下载结果 bytes；IPD 提取文本 -> `FileTextExtractionResult.text` 与 `ResumeRecordDto.parsedContent`；20,000 字上限保持一致。
- 新增依赖：`org.apache.pdfbox:pdfbox:2.0.31`。必要性是 JDK 标准库和当前 Cosmic/BOS provider 均不提供 PDF 文本解析；版本沿用 IPD 已验证的 2.x API并兼容 JDK 1.8。实现须通过仓库 `gradlew.bat` 验证，不引入 Spring、JPA、Flyway、Vue、uni-app 或 OCR 依赖。
- 暂不迁移：扫描件 OCR、复杂版面还原、表格结构化、图片识别、IPD OSS 下载和 AI 调用实现。
- 验证：OpenSpec strict 校验、PDF 提取 focused tests、文件/简历/诊断应用服务测试、`node --check`、route 校验以及 JDK 8 Gradle 构建。
