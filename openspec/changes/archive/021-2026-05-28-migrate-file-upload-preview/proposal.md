## Why

CareerLoop 的简历、头像、诊断和未来管理内容都需要稳定的文件上传与预览能力；当前 CyanCruise 只迁移了简历元数据和诊断语义，文件上传/预览仍是 webapp route map 中的 pending 能力。IPD 已沉淀“上传返回对象 key、预览使用短期签名 URL、下载走后端认证、删除幂等、PDF 文本抽取限长”的业务契约，现在应先迁移为 Cosmic/JDK 8 兼容的文件边界。

## What Changes

- 新增文件上传预览能力规格：定义文件引用、对象 key、folder 归一、扩展名保留、上传校验、下载、预览 URL、删除和文本抽取契约。
- 抽取 IPD `FileController`、`FileService`、`FileServiceImpl`、`OssConfigProperties`、`PdfTextExtractor`、前端 `api/file.ts` 和相关测试中的业务规则、数据语义、流程和接口契约。
- 在后续 apply 阶段为 CyanCruise 增加 JDK 8 DTO、helper、应用服务/WebAPI、可替换文件存储边界和 webapp route/API 映射，优先支撑简历文件上传、预览和诊断文本来源。
- 定义对象引用边界：业务记录 SHALL 保存 bare object key，例如 `resumes/uuid.pdf`；浏览器可访问 URL SHALL 通过短期签名或平台临时访问 URL 即时生成，不得把长期绝对 URL 作为主引用落库。
- 定义预览边界：预览 URL TTL SHALL clamp 到安全窗口；空 key 返回空结果；旧绝对 URL SHALL 可归一为 object key，并剥离 query。
- 定义文本抽取边界：PDF 文本抽取 SHALL 限长，失败返回空文本和可观测状态；不直接迁移 IPD PDFBox 依赖，后续 apply 阶段以平台可用能力或可替换 adapter 表达。
- 本 change 先生成 proposal、spec、design、tasks 文档，等待审阅通过后再 apply 实现代码。

## Capabilities

### New Capabilities

- `file-upload-preview`: 定义 CyanCruise CareerLoop 的文件上传、对象 key、预览 URL、下载、删除、PDF/文本抽取、WebAPI、webapp 消费契约和迁移边界。

### Modified Capabilities

- 无。本次新增文件上传预览规格，不修改 `resume-core` 或 `resume-diagnosis` 的既有 SHALL；后续实现可被简历、诊断、头像、内容管理等能力复用。

## Impact

- IPD 来源路径：
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\FileController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\FileService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\FileServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\config\OssConfigProperties.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\utils\PdfTextExtractor.java`
  - `F:\Project\IPD\backend\src\test\java\com\group1\career\controller\FileControllerTest.java`
  - `F:\Project\IPD\backend\src\test\java\com\group1\career\service\impl\FileServiceTest.java`
  - `F:\Project\IPD\frontend\src\api\file.ts`
  - `F:\Project\IPD\backend\sql\2026_04_oss_url_to_key.sql`
- CyanCruise 目标模块：
  - `code/base/v620-cc001-base-common/`：文件引用、上传请求/结果、预览 URL、下载结果、删除结果、文本抽取结果 DTO。
  - `code/base/v620-cc001-base-helper/`：folder/key/url 归一、扩展名提取、TTL clamp、文件校验、文本限长和预览状态 helper。
  - `code/cloud01/v620-cc001-cloud01-app01/`：文件应用服务、存储 adapter 边界、内存/本地测试 adapter、Cosmic WebAPI。
  - `webapp/isv/v620/careerloop/`：文件上传/预览 route/API 映射，简历入口消费契约。
  - `openspec/specs/`：新增文件上传预览主规格。
  - `docs/ipd-to-cyancruise-migration-map.md`：实现阶段更新迁移地图，记录来源、目标、数据映射、暂不迁移项和验证方式。
- API 影响：预计新增 `/cc001/files/*` 或等价 Cosmic WebAPI，覆盖 upload metadata/raw boundary、preview-url、download、delete、extract-text；不改变既有简历元数据 API。
- 依赖影响：默认不新增外部依赖，不引入 IPD 的 Spring Multipart、Aliyun OSS SDK、PDFBox、Java 17 `readAllBytes`、Flyway 或 uni-app 运行时。若 apply 阶段确需 Cosmic 文件服务或 PDF 文本能力，必须说明必要性并确认不破坏 JDK 8/Cosmic/KDDT 约束。
