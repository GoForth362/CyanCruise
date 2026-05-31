## 上下文

已有 `FileUploadPreviewApplicationService`、`CareerFileStorage`、`CosmicCareerFileStorage` 和 `CosmicCareerFileServiceProvider`，但 `CareerLoopFileServiceAdapterFactory.production()` 当前固定使用 `UnavailableCosmicCareerFileServiceProvider`。这让生产路径保持安全，但无法满足“PDF 真正进平台文件服务”的验收。

本地苍穹 8.0.4 运行包提供：

- `kd.bos.fileservice.FileServiceFactory#getAttachmentFileService()`
- `kd.bos.fileservice.FileService#upload(FileItem)`
- `FileService#getInputStream(String)`
- `FileService#delete(String)`
- `FileService#preview(String, String, String)`
- `FileService#getHttpUrlPrefix()`

这些类来自 Cosmic/BOS 自带 JAR，符合 JDK 8 和 KDDT 模板约束。

## 设计决策

### 1. 新增 provider，不改上层 service 契约

新增 `BosAttachmentFileServiceProvider implements CosmicCareerFileServiceProvider`，只替换 provider 层。`/cc001/files/*` WebAPI、DTO、webapp 表单和简历创建逻辑保持不变。

理由：上层已经稳定通过 `FileUploadPreviewApplicationService` 编排状态、DTO 和异常降级；真实平台差异应被封装在 provider 内。

### 2. 仍需显式启用 adapter

`CareerLoopFileServiceAdapterFactory.production()` 读取 `CosmicFileAdapterConfig.fromSystemProperties()`。只有 `cc001.file.adapter.enabled=true` 时才使用 BOS provider；否则继续 unavailable。

理由：避免开发调试环境误以为文件已持久化。启用动作属于部署配置，不硬编码到业务代码。

### 3. object key 使用 BOS 上传返回值

上传时使用 helper 已生成的 `reference.objectKey` 作为目标路径，并把 BOS `upload(FileItem)` 返回值作为最终稳定 key；如果平台返回空值，则保留请求 key。

理由：平台可能重写路径、补充租户或生成冲突规避 key，最终业务记录应保存平台认可的稳定引用。

### 4. 预览 URL 分两级

先调用 `FileService.preview(filename, objectKey, null)`，从返回 map 中提取 `url`、`previewUrl`、`preview.url`、`result.url` 等常见字段；如果没有可用 URL，则使用 `getHttpUrlPrefix()` 拼接只读下载 URL。

理由：不同文件类型和预览服务配置可能返回不同结构。简历页需要一个可点击入口，无法在线转预览时也应能下载查看。

### 5. 文本抽取不在本 change 强行接 PDF/OCR

本 change 让文件真实落入 BOS 文件服务。`extract-text` 继续通过现有边界返回 unsupported/unavailable 或后续由独立抽取 provider 补齐。

理由：平台文件存储与文本解析是两类能力；强行接 PDF/OCR 会扩大依赖和验证面。

## 风险与处理

- BOS 文件服务未配置 `attachmentServer.url`：provider `available()` 返回不可用，上传返回现有 unavailable 状态。
- 预览服务未配置：上传和下载仍可用，预览返回下载 URL 或 unavailable。
- 签名参数泄露：只把临时 URL 返回给当前请求，不写入简历记录；持久化只保存 object key。
- 文件过大：沿用前端小文件限制和 BOS 文件服务自身限制；错误转换为 failed/unavailable message。

## 验收方式

1. 运行 OpenSpec strict 校验。
2. 运行文件 adapter 聚焦测试。
3. JDK 8 运行 `.\gradlew.bat clean build`。
4. 部署 JAR 到 8080，并启用 `cc001.file.adapter.enabled=true`。
5. 在 `#resume` 页面选择 PDF，点击上传并填入 fileKey，确认返回的 key 不是手工字符串。
6. 创建简历后点击预览，能打开平台文件服务下载/预览入口。
