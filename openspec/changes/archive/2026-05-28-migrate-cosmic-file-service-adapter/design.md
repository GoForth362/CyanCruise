## Context

CareerLoop 现有 `file-upload-preview` 已提供 JDK 8 DTO、key/URL/TTL/text helper、`CareerFileStorage`、`FileTextExtractor`、`FileUploadPreviewApplicationService`、`/cc001/files/*` WebAPI 与 webapp route/API map。当前默认实现以 `InMemoryCareerFileStorage` 和 `PlainTextFileTextExtractor` 保持本地可测，尚未接入真实苍穹文件服务。

IPD 来源提供了清晰业务语义：`FileService` 上传后返回 bare object key，业务记录按需请求短期签名 URL，私有桶通过服务端下载，删除为幂等清理，PDF 文本抽取最多 20000 字。但 IPD 实现依赖 Spring Multipart、Aliyun OSS SDK、PDFBox、Java 17 `readAllBytes` 和 uni-app 上传运行时，不能直接迁入 CyanCruise/Cosmic。

## Goals / Non-Goals

**Goals:**
- 定义真实 Cosmic 文件服务 adapter 的生产接入边界，覆盖上传、预览 URL、下载、删除和文本抽取。
- 让 adapter 默认安全不可用，只有显式配置或平台服务可用时才启用生产路径。
- 保持现有 `/cc001/files/*` WebAPI、DTO 和 object key 语义稳定，避免业务记录持久化长期绝对 URL。
- 为无真实 Cosmic 租户的本地验证提供可测试 fallback，同时把租户手工验证项写入迁移地图和任务。

**Non-Goals:**
- 不直接迁移 IPD Aliyun OSS SDK、OSS 配置属性、Spring Multipart、PDFBox、OCR、Flyway、Vue/uni-app 上传代码或生产密钥。
- 不在本 change 中实现病毒扫描、Office 在线预览、CDN、外部 OSS、对象生命周期管理或跨租户存储治理。
- 不改变简历、诊断、头像、内容资源等上层业务记录的数据模型，只维护它们对 object key 的调用契约。

## Decisions

1. **以 Cosmic adapter 包装既有 `CareerFileStorage`，而不是改写上层 WebAPI。**
   - 理由：现有文件能力已经把业务语义和 WebAPI 契约收敛在 `FileUploadPreviewApplicationService`，adapter 只需替换底层存储/预览/下载/删除实现。
   - 备选：新增一套 `/cc001/cosmic-files/*` API。放弃，因为会造成 webapp route map 与业务调用双轨。

2. **生产 adapter 显式启用，默认返回 unavailable。**
   - 理由：真实苍穹文件服务 API、租户字段与部署方式可能因环境不同而变化，默认启用会把本地内存实现误认为生产能力。
   - 备选：默认回退到内存存储。放弃，因为生产上传成功但重启丢失比显式 unavailable 更危险。

3. **保留 object key 为唯一稳定引用，预览 URL 每次按需生成。**
   - 理由：沿用 IPD 已完成的“URL 到 key”治理，降低桶、域名、签名策略、租户路径变更对业务记录的影响。
   - 备选：在业务记录中保存平台返回的绝对 URL。放弃，因为长期 URL 可能过期、泄露或绑定租户环境。

4. **文本抽取作为独立 adapter，不与文件存储强耦合。**
   - 理由：苍穹文件服务可能只提供对象读写，不提供 PDF/OCR/Office 解析；简历诊断需要在抽取不可用时返回空文本和原因，而不是阻断文件元数据。
   - 备选：上传时同步抽取并强制成功。放弃，因为会放大上传延迟和平台依赖风险。

5. **配置与诊断只记录非敏感信息。**
   - 理由：文件服务配置可能包含服务标识、bucket/space、tenant、endpoint 或 provider 名称，但不得输出密钥、token、签名 URL 全量查询串。
   - 备选：直接打印平台上下文和完整 URL 便于排错。放弃，因为会增加文件访问凭证泄露风险。

## Risks / Trade-offs

- [真实 Cosmic 文件 API 字段未知] → 通过 `CosmicCareerFileServiceProvider` 边界和配置字段隔离平台差异，首版以 adapter contract 和可替换 provider 实现。
- [本地无法完整验证租户文件服务] → 保留内存 provider 聚焦测试，并把真实上传/预览/下载/删除/文本抽取列入手工租户验证清单。
- [预览 URL 或诊断日志泄露签名] → helper/adapter 在结果和诊断中只保留 object key、TTL、provider、状态和脱敏 message。
- [文本抽取不可用影响简历诊断] → `extract-text` 返回 `UNAVAILABLE` 或空文本，诊断流程保持可恢复，并提示需要上传可解析文件或等待平台 adapter。
- [新增平台 SDK 破坏 JDK 8/Cosmic 构建] → 如需依赖，必须先说明必要性并用仓库 `gradlew.bat` 在 JDK 8 下验证；优先使用 Cosmic 工程已有服务类或反射/接口边界。

## Migration Plan

1. 在 OpenSpec 中定义 `cosmic-file-service-adapter` 新能力和 `file-upload-preview` 增量要求。
2. 审阅通过后实现配置、provider 边界、默认 unavailable provider、Cosmic adapter、文本抽取 adapter 与工厂启用策略。
3. 将 `FileUploadPreviewApplicationService` 默认生产构造改为 adapter 工厂，同时保持测试可注入内存实现。
4. 补充 focused adapter/service/WebAPI 测试、route map 校验、OpenSpec strict 校验和 JDK 8 Gradle 构建。
5. 更新 `docs/ipd-to-cyancruise-migration-map.md` 和 `careerloop-routes.json` 中的文件服务生产接入状态、暂不迁移项和租户验证说明。
6. 若租户启用失败，回滚配置为 adapter disabled；WebAPI 保持 `UNAVAILABLE`/`IDENTITY_REQUIRED` 类可恢复状态，不回退到生产内存文件。

## Open Questions

- 真实苍穹文件服务在当前工程中暴露的是 SDK、服务接口、BOS 附件能力还是租户配置服务，需要实现阶段从 Cosmic/KDDT 可用 API 中确认。
- 文件服务 object key 是否需要包含租户/组织前缀，以及该前缀由平台服务生成还是由 CareerLoop adapter 生成，需要租户部署时确认。
- PDF/OCR/Office 文本抽取应优先复用苍穹平台能力还是后续接入独立 provider，本 change 仅保留 adapter 边界。
