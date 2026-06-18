# file-upload-preview Specification

## Purpose
TBD - created by archiving change migrate-file-upload-preview. Update Purpose after archive.
## Requirements
### Requirement: File object reference

CyanCruise SHALL represent uploaded files with a stable object key and SHALL NOT require business records to persist long-lived absolute object storage URLs.

#### Scenario: File upload succeeds

- **WHEN** a file is uploaded to a valid folder
- **THEN** the system SHALL return a bare object key such as `resumes/<generated-id>.pdf`, original filename, size, folder, extension, and status

#### Scenario: Business record stores file reference

- **WHEN** resume, avatar, content, or diagnosis records reference an uploaded file
- **THEN** they SHALL store the object key or file reference DTO and SHALL request preview/download URLs just-in-time

### Requirement: Upload validation and key generation

CyanCruise SHALL validate uploads and generate normalized keys without depending on IPD Spring Multipart or Aliyun OSS runtime classes.

#### Scenario: Empty file is uploaded

- **WHEN** upload content is missing or byte length is zero
- **THEN** the system SHALL reject the upload with a file-empty state and SHALL NOT create an object key

#### Scenario: Folder is blank

- **WHEN** an upload request omits folder or provides a blank folder
- **THEN** the system SHALL default the folder to `others`

#### Scenario: Original filename has extension

- **WHEN** the original filename contains an extension
- **THEN** the generated object key SHALL preserve the extension after a generated unique id

### Requirement: Object key normalization

CyanCruise SHALL normalize file references from bare keys, leading-slash keys, and legacy absolute URLs.

#### Scenario: Bare key is normalized

- **WHEN** the input is `resumes/a.pdf`
- **THEN** normalization SHALL return `resumes/a.pdf`

#### Scenario: Leading slash key is normalized

- **WHEN** the input is `/resumes/a.pdf`
- **THEN** normalization SHALL return `resumes/a.pdf`

#### Scenario: Presigned URL is normalized

- **WHEN** the input is `https://bucket.endpoint/resumes/a.pdf?signature=x`
- **THEN** normalization SHALL return `resumes/a.pdf` and SHALL strip the query string

#### Scenario: Malformed URL is normalized

- **WHEN** the input contains scheme and host but no object path
- **THEN** the system SHALL return a malformed-reference state instead of guessing a key

### Requirement: Preview URL generation

CyanCruise SHALL provide short-lived preview URL contracts for browser or Cosmic webapp rendering.

#### Scenario: Preview URL is requested

- **WHEN** a valid object key or legacy URL is provided with ttlSeconds
- **THEN** the system SHALL clamp ttlSeconds to the allowed preview window and return a preview URL or platform temporary URL

#### Scenario: Preview reference is blank

- **WHEN** preview is requested for a blank file reference
- **THEN** the system SHALL return an empty or skipped preview result and SHALL NOT throw into the caller flow

#### Scenario: Preview provider is unavailable

- **WHEN** the storage adapter cannot generate a preview URL
- **THEN** the system SHALL return an unavailable state with reason and keep the caller page navigable

### Requirement: Download and delete boundary

CyanCruise SHALL expose download and delete operations through a file storage adapter.

#### Scenario: File bytes are downloaded

- **WHEN** a valid object key is requested by an authorized service
- **THEN** the system SHALL return bytes, key, content length, and status

#### Scenario: File delete is requested

- **WHEN** delete is requested for a valid, missing, or blank object reference
- **THEN** the operation SHALL be idempotent and SHALL return ok/skipped status without rolling back the originating business operation

### Requirement: Text extraction boundary

CyanCruise SHALL provide a text extraction boundary for resume diagnosis and similar AI workflows.

#### Scenario: PDF text extraction succeeds

- **WHEN** a text extractor receives readable PDF or document bytes
- **THEN** the system SHALL return extracted text capped to the configured maximum character count

#### Scenario: Text extraction fails

- **WHEN** the file is unreadable, unsupported, or extraction adapter fails
- **THEN** the system SHALL return empty text with failed/unavailable reason and SHALL NOT break the primary file metadata flow

### Requirement: File WebAPI and webapp mapping

The migration SHALL define Cosmic WebAPI and webapp route/API mapping for file upload, preview URL, download, delete, and text extraction.

#### Scenario: Route map is reviewed

- **WHEN** reviewers inspect webapp migration artifacts
- **THEN** they SHALL find file upload/preview route keys, consumed WebAPI paths, DTO fields, identity requirements, and fallback states

#### Scenario: File backend is unavailable

- **WHEN** file WebAPI is unavailable
- **THEN** the webapp SHALL keep CareerLoop workbench and resume metadata routes navigable and display upload/preview unavailable state

### Requirement: Migration boundary for file upload preview

The file upload preview migration SHALL rebuild business semantics for CyanCruise and SHALL NOT directly migrate IPD Spring Multipart, Aliyun OSS SDK, PDFBox, Flyway SQL, Java 17 APIs, Vue, uni-app, or production secrets.

#### Scenario: Implementation is inspected

- **WHEN** implementation files are reviewed
- **THEN** they SHALL reside in CyanCruise target modules and SHALL NOT require `F:\Project\IPD` source files or IPD runtime dependencies

#### Scenario: Dependencies are checked

- **WHEN** dependency changes are reviewed
- **THEN** the change SHALL NOT introduce new external dependencies unless their necessity and Cosmic/KDDT/JDK 8 compatibility are documented

### Requirement: Verification and migration documentation

The file upload preview migration SHALL include verification and documentation that prove the proposed contracts are OpenSpec-valid and aligned with the migration map.

#### Scenario: Change is verified before archive

- **WHEN** implementation is complete
- **THEN** verification SHALL include strict OpenSpec validation, focused helper/service/WebAPI tests or equivalent static checks, route/API map checks when webapp artifacts change, JDK 8 Gradle build validation, and migration map updates

#### Scenario: Migration map is updated

- **WHEN** the change is finalized
- **THEN** `docs/ipd-to-cyancruise-migration-map.md` SHALL record IPD source paths, CyanCruise target modules, data mapping, temporarily excluded items, and validation results

### Requirement: Production Cosmic file adapter integration
The file upload preview capability SHALL integrate with a production Cosmic file service adapter when explicitly enabled. Existing `/cc001/files/*` WebAPI contracts SHALL remain stable, and adapter unavailable states SHALL be returned as recoverable results rather than uncaught platform errors.

#### Scenario: Production adapter is enabled
- **WHEN** `/cc001/files/upload`, `/cc001/files/preview-url`, `/cc001/files/download`, or `/cc001/files/delete` is invoked with the BOS file adapter enabled
- **THEN** the operation SHALL delegate to BOS attachment file service through the configured provider and SHALL preserve existing DTO status semantics

#### Scenario: Production adapter is disabled
- **WHEN** file WebAPI is invoked in production without an enabled Cosmic file adapter
- **THEN** upload, preview, download, delete, and extract-text SHALL return skipped or unavailable states and SHALL NOT persist files in an in-memory production fallback

#### Scenario: Existing business record references a file
- **WHEN** resume, diagnosis, avatar, or content flows request preview/download/text extraction for an existing object key
- **THEN** the file upload preview service SHALL keep the business route navigable even if the Cosmic file adapter returns unavailable or unsupported

### Requirement: 简历工作流可选文件上传
文件上传预览能力 SHALL 能被简历页作为可选增强使用。简历页 MAY 调用 `/cc001/files/upload` 获得稳定 object key，但文件上传失败、adapter disabled 或 provider unavailable SHALL NOT 阻断 `/cc001/resume/create` 的元数据创建流程。

#### Scenario: 文件上传成功填充 fileKey
- **WHEN** 用户在简历页选择可读取的小文件并触发上传
- **THEN** 页面 SHALL 调用 `/cc001/files/upload`，并在成功后把返回的 object key 填入简历创建表单的 fileKey 字段

#### Scenario: 文件上传不可用
- **WHEN** `/cc001/files/upload` 返回 unavailable、skipped、failed 或平台错误
- **THEN** 页面 SHALL 显示文件上传不可用提示，并保留用户继续手工填写 fileKey 或只创建简历元数据的能力

#### Scenario: 不要求先上传文件
- **WHEN** 用户未选择文件但填写了简历标题、目标岗位或解析内容
- **THEN** 页面 SHALL 允许继续调用 `/cc001/resume/create` 创建简历记录

### Requirement: 简历文件引用即时预览
文件上传预览能力 SHALL 支持简历页针对已有 fileKey 请求短期预览 URL。预览 URL 获取失败 SHALL 被视为局部文件面板失败，不影响简历列表展示或创建流程。

#### Scenario: 请求简历预览 URL
- **WHEN** 简历记录包含非空 fileKey 且用户请求预览
- **THEN** 页面 SHALL 调用 `/cc001/files/preview-url` 并展示返回的短期预览 URL 或平台临时引用

#### Scenario: 预览不可用
- **WHEN** 文件 adapter 无法生成预览 URL
- **THEN** 页面 SHALL 显示预览不可用状态，并继续展示简历记录、fileKey 和其他操作入口

### Requirement: 文件字段不保存敏感凭据
简历工作流使用文件能力时 SHALL 只保存稳定 object key 或平台文件 ID，不得把 access token、Authorization header、预签名 URL 签名参数或客户私有凭据写入简历记录、route metadata 或 OpenSpec 文档。

#### Scenario: 上传返回临时 URL
- **WHEN** 文件 provider 返回包含签名参数的临时 URL 或绝对引用
- **THEN** 系统 SHALL 规范化并保存稳定 object key 或平台文件 ID，而 SHALL NOT 把签名 URL 作为长期简历 fileKey 保存

#### Scenario: 文档和元数据审查
- **WHEN** 审查 `careerloop-routes.json`、OpenSpec artifacts 或迁移文档
- **THEN** 其中 SHALL 只包含属性名、API 路径、fallback 和验证方式，不包含真实 token、Authorization header、endpoint secret 或预签名 URL

### Requirement: 文件上传预览作为调试能力保留
文件上传预览能力 SHALL 继续保留 `/cc001/files/*` 底层契约、route map 和验证入口，但独立文件上传预览页面 SHALL NOT 作为普通用户默认主导航入口。真实用户 SHALL 通过简历页等业务页面间接使用文件能力。

#### Scenario: 普通用户导航
- **WHEN** 普通用户打开 CyanCruise 默认导航
- **THEN** `file-upload-preview` SHALL NOT 作为普通业务入口展示

#### Scenario: 简历页使用文件能力
- **WHEN** 用户在简历页上传、预览或删除简历关联文件
- **THEN** 页面 SHALL 通过 `/cc001/files/upload`、`/cc001/files/download`、`/cc001/files/delete` 或等价文件服务契约完成操作

#### Scenario: 调试模式访问文件页
- **WHEN** 开发者使用 `?ccDebug=1#file-upload-preview` 或 hash 直达方式打开文件上传预览页面
- **THEN** 页面 SHALL 保留文件上传、预览、下载、删除和文本抽取契约信息，用于排查 BOS 文件服务接入



### Requirement: 简历诊断复用文件边界
简历诊断页面 SHALL 复用现有文件上传预览和文本提取边界。页面 MAY 为已有 `fileKey` 请求预览 URL 或文本提取，但文件 adapter unavailable、skipped 或 failed SHALL 只影响文件局部面板，不得阻断已有简历诊断和诊断建议查看。

#### Scenario: 文件预览可用
- **WHEN** 用户选择的简历包含 `fileKey` 且文件 adapter 返回预览 URL
- **THEN** 页面提供打开或查看预览的入口，并继续允许诊断

#### Scenario: 文件预览不可用
- **WHEN** 文件 adapter 无法生成预览 URL
- **THEN** 页面显示预览不可用状态，但仍允许用户基于已保存简历文本或手工粘贴文本发起诊断

### Requirement: 不迁移 IPD 文件运行时
简历诊断能力 SHALL NOT 直接迁移 IPD 的 Spring Multipart、Aliyun OSS SDK、PDFBox、Flyway SQL、Vue 或 uni-app 文件选择实现。CyanCruise SHALL 只迁移稳定 object key、文本提取结果、预览失败降级和业务流程语义。

#### Scenario: 依赖审查
- **WHEN** 审查本 change 的依赖和实现
- **THEN** 不应出现新增 PDFBox、OSS SDK、Spring Multipart、Vue 或 uni-app 运行时依赖
