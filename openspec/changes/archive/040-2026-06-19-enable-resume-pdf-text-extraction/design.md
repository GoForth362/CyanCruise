## Context

简历上传链路已经通过 BOS/文件适配器保存稳定 `fileKey`，`/cc001/files/extract-text` 也已有契约，但生产 BOS provider 的 `textExtractionAvailable()` 为 `false`，默认 `PlainTextFileTextExtractor` 只处理 `.txt` 和 `.md`。简历诊断按 `resumeId` 读取 `parsedContent`，正文为空时只能拒绝诊断。

IPD 使用 PDFBox `2.0.31` 的 `PDDocument.load(byte[])` 和 `PDFTextStripper` 从授权下载后的 PDF 字节提取文本，并限制 20,000 字。本 change 迁移这项业务语义和边界，不搬迁 Spring/OSS/AI 代码。实现必须兼容 JDK 1.8、Cosmic/KDDT 模板并使用仓库 `gradlew.bat` 构建。

## Goals / Non-Goals

**Goals:**

- 让文本型 PDF 上传后可以自动提取正文并保存到简历记录。
- 让已有简历在 `parsedContent` 为空但 `fileKey` 可用时重新提取、回写并继续诊断。
- 保持既有文件、简历、诊断 API 兼容并返回可解释失败状态。
- 通过 focused tests 和 JDK 8 构建证明 PDFBox `2.0.31` 不破坏 Cosmic/KDDT 工程。

**Non-Goals:**

- 不实现扫描件 OCR、图片识别、复杂版面/表格结构化。
- 不引入 IPD Spring Boot、OSS、Lombok、Flyway、Vue 或 uni-app 实现。
- 不在本 change 接入 agent 或真实 AI provider。

## Decisions

### 1. PDFBox 放在现有 `FileTextExtractor` 边界内

新增组合提取器或扩展现有默认提取器：`.pdf` 使用 PDFBox，`.txt/.md` 继续按 UTF-8 读取，其它类型返回空文本/不支持状态。`FileUploadPreviewApplicationService` 继续负责下载、限长和结果 DTO，不改变文件存储职责。

选择 PDFBox `2.0.31` 是因为 IPD 已固定并验证该 2.x API，且兼容 JDK 8。JDK 标准库和当前 BOS provider 都不提供 PDF 解析；手写 PDF parser 无法可靠处理压缩流、字体编码和加密文档。

### 2. 提取结果区分“成功但无正文”和“技术失败”

文本型 PDF 提取到非空文本返回 `OK`。扫描版或纯图片 PDF 返回明确 `EMPTY`/等价可解释状态；加密、损坏或解析异常返回 `FAILED`，错误信息必须脱敏。所有结果最多保留 20,000 字。

### 3. 正文回写复用简历更新边界

前端上传时继续先调用文件上传和提取接口，并把提取文本放入创建请求。对于已经保存但缺正文的简历，诊断应用服务在 owner-only 校验后通过文件文本边界提取，并调用现有简历更新能力保存 `parsedContent`；提取失败则返回明确提示，不进入规则/AI 诊断。

### 4. Custom WebAPI 和 route metadata 保持显式

若现有 Custom WebAPI 路由没有暴露简历更新或重新提取所需契约，补齐 `/cc001/resume/update` 映射并更新 `cyancruise-routes.json`。用户可见文案只使用“PDF 正文”“简历正文”等普通中文。

## Risks / Trade-offs

- [Risk] PDFBox 及其传递依赖可能与 Cosmic 运行时类冲突 → Mitigation：固定 `2.0.31`，仅在 cloud app 模块使用，运行 JDK 8 focused tests 和完整 Gradle build，并记录依赖树。
- [Risk] 扫描版 PDF 提取为空 → Mitigation：返回“当前 PDF 只有图片，暂不支持自动识别”，保留手工粘贴正文入口。
- [Risk] 大文件导致内存压力 → Mitigation：保留上传大小边界，提取文本限长 20,000 字，使用 try-with-resources 及时关闭 `PDDocument`。
- [Risk] 回写正文覆盖用户已有内容 → Mitigation：仅在 `parsedContent` 为空时自动回写；非空内容继续优先使用，不自动覆盖。
- [Risk] PDF 内含敏感信息 → Mitigation：正文只存入已有用户归属简历记录，不写日志、route metadata 或 OpenSpec，所有读取继续执行 owner-only 校验。

## Migration Plan

1. 新增并验证 PDFBox 依赖与 PDF 提取器 focused tests。
2. 接入文件提取服务和简历诊断缺正文回补流程。
3. 更新前端上传/诊断状态与静态资源版本。
4. 运行 strict OpenSpec、focused tests、前端校验和 JDK 8 Gradle build。
5. 回滚时移除 PDFBox 提取器并恢复默认纯文本提取器；既有 `parsedContent` 和 API 契约保持可读。

## Open Questions

- 扫描件 OCR 由 Cosmic 平台能力还是独立受控服务承担，留待后续 change。
