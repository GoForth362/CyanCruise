## Why

`migrate-file-upload-preview` 已经完成 CareerLoop 文件上传、预览、下载、删除与文本抽取的业务契约，但当前生产接入仍停留在内存/纯文本适配边界，无法代表真实金蝶苍穹文件服务。现在需要在不迁移 IPD Spring Multipart、Aliyun OSS SDK、PDFBox、Flyway、Vue/uni-app 实现的前提下，补齐 Cosmic 文件服务 adapter 的生产接入规格，支撑简历、诊断、头像、内容资源等后续能力在苍穹环境中安全使用文件。

## What Changes

- 新增 `cosmic-file-service-adapter` 能力，定义 Cosmic 文件服务 adapter 的上传、预览 URL、下载、删除、文本抽取接入边界。
- 规定 adapter 配置、启用策略、默认安全不可用行为、失败降级状态、诊断信息与验证方式。
- 约束生产文件引用继续使用稳定 object key，业务记录不得持久化长期绝对 URL。
- 明确 IPD 来源语义与 CyanCruise 目标模块映射，保留 Aliyun OSS、PDFBox/OCR、Spring Multipart、Java 17 与前端运行时为暂不迁移项。
- 修改既有 `file-upload-preview` 能力，补充其生产文件 adapter SHALL 对接规则与本地不可用降级要求。
- 不引入破坏性 API 变更；现有 `/cc001/files/*` WebAPI 契约继续作为上层调用入口。

## Capabilities

### New Capabilities
- `cosmic-file-service-adapter`: 定义 CareerLoop 文件能力接入真实 Cosmic 文件服务的 adapter 配置、上传/预览/下载/删除/文本抽取边界、失败降级、诊断与验证契约。

### Modified Capabilities
- `file-upload-preview`: 增补生产文件服务 adapter 接入要求，要求既有文件上传预览契约在 adapter 未启用或平台不可用时保持可恢复状态。

## Impact

- IPD 来源：`F:\Project\IPD\backend\src\main\java\com\group1\career\controller\FileController.java`、`service\FileService.java`、`service\impl\FileServiceImpl.java`、`config\OssConfigProperties.java`、`utils\PdfTextExtractor.java`、`frontend\src\api\file.ts`。
- CyanCruise 目标：`code/base/v620-cc001-base-common` 的文件 DTO/常量、`code/base/v620-cc001-base-helper` 的 key/TTL/text helper、`code/cloud01/v620-cc001-cloud01-app01` 的 `CareerFileStorage`/`FileTextExtractor`/`FileUploadPreviewApplicationService` 与后续 Cosmic adapter、`webapp/isv/v620/careerloop/careerloop-routes.json`、`docs/ipd-to-cyancruise-migration-map.md`。
- API：继续使用 `/cc001/files/upload`、`/cc001/files/preview-url`、`/cc001/files/download`、`/cc001/files/delete`、`/cc001/files/extract-text`。
- 依赖：本 propose 不新增依赖；后续实现如需接入苍穹文件 SDK 或平台服务类，必须说明 JDK 8/Cosmic/KDDT 兼容性，并提供无真实平台时的安全降级。
