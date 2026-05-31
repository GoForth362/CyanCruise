## 为什么

当前简历页已经能创建简历记录，但文件上传仍停留在 `UnavailableCosmicCareerFileServiceProvider`。用户选择 PDF 后，系统只能保存手工填写的 `fileKey` 字符串，不能把文件真正写入苍穹文件服务，也无法生成可用预览入口。

苍穹运行包已经提供 `kd.bos.fileservice.FileServiceFactory` 和 `FileService`，可以复用平台附件文件服务完成上传、下载、删除和预览 URL 生成。本 change 目标是在不引入 IPD OSS/Spring/PDFBox/Vue 运行时依赖的前提下，把 CareerLoop 文件能力接到真实 BOS 文件服务。

## 变更内容

- 新增 BOS 文件服务 provider，实现 `CosmicCareerFileServiceProvider`。
- 生产工厂在文件 adapter 显式启用时使用 BOS `AttachmentFileService`，未启用时继续返回安全 unavailable。
- 上传成功后返回平台稳定 object key，并保留原文件名、目录、扩展名、大小和 provider 信息。
- 预览优先调用平台 preview 能力，无法取得专用预览 URL 时生成文件服务下载 URL 作为可点击引用。
- 下载、删除复用 BOS 文件服务能力，异常转换为现有 DTO 的可恢复状态。
- 更新文档和验证步骤，说明本地 8080 需要启用 `cc001.file.adapter.enabled=true` 后才能真实落文件。

## 能力范围

### 修改能力

- `cosmic-file-service-adapter`：从“抽象边界 + unavailable provider”推进到“BOS 附件文件服务真实 provider”。
- `file-upload-preview`：上传、预览、下载、删除在 adapter 启用时落到苍穹文件服务，未启用时仍保持可恢复降级。

## 影响

- 目标模块：`code/cloud01/v620-cc001-cloud01-app01` 文件 adapter 与测试。
- 前端 API：不新增 endpoint，继续使用 `/cc001/files/upload`、`/cc001/files/preview-url`、`/cc001/files/download`、`/cc001/files/delete`。
- 依赖：不新增外部依赖；使用 Cosmic/BOS 运行包已有 `bos-fileservice-sdk-8.0.jar`。
- 部署：需要在 8080 星瀚服务启动参数中启用 `-Dcc001.file.adapter.enabled=true`，并确认 `attachmentServer.url` 已由苍穹环境设置。
