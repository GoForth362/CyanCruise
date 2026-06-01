## 1. 规格与边界

- [x] 1.1 确认本 change 中文 proposal、design、specs、tasks 已记录 BOS 文件服务接入边界。
- [x] 1.2 运行 `openspec validate enable-bos-file-service-provider --strict`。
- [x] 1.3 确认不新增 Spring Multipart、Aliyun OSS、PDFBox、Java 17、Vue/uni-app 或生产密钥依赖。

## 2. Provider 实现

- [x] 2.1 新增 `BosAttachmentFileServiceProvider`，通过 `FileServiceFactory.getAttachmentFileService()` 获取 BOS 附件文件服务。
- [x] 2.2 实现上传委托，使用 `FileItem` 写入 BOS 文件服务，并返回稳定 object key。
- [x] 2.3 实现预览 URL 委托，优先解析 BOS preview map，必要时生成文件服务下载 URL。
- [x] 2.4 实现下载和删除委托，并把平台异常转换为可恢复 DTO 状态。
- [x] 2.5 修改生产工厂：adapter 显式启用时使用 BOS provider，未启用时保持 unavailable。

## 3. 测试与文档

- [x] 3.1 增加聚焦测试，覆盖启用配置选择 BOS provider、上传/预览/下载/删除委托和错误降级。
- [x] 3.2 更新迁移地图或运行文档，记录启用参数、验证步骤和回滚方式。

## 4. 验证与部署

- [x] 4.1 运行文件 adapter 聚焦 Gradle 测试。
- [x] 4.2 运行 `.\gradlew.bat clean build`。
- [x] 4.3 同步 JAR/zip 到本地 8080 苍穹运行目录。
- [x] 4.4 本地启用 `cc001.file.adapter.enabled=true` 后，通过 `#resume` 页面验证 PDF 上传、fileKey 填入、创建简历和预览入口。
- [x] 4.5 修复 BOS preview map 无 URL 或 preview API 异常时的下载 URL 回退，并增加聚焦测试。
- [x] 4.6 前端预览不再暴露服务端本机 URL，改为通过 `/cc001/files/download` 读取文件字节并生成浏览器临时预览链接。
