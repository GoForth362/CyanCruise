## Context

CyanCruise 已有 `resume-core`：可以保存简历记录、解析内容、诊断分数，并把最新简历摘要同步到职业画像 resume block。IPD 的简历诊断能力基于 Spring Boot Controller、JPA 实体、OSS 文件下载、PDFBox 文本提取、DashScope/Qwen 调用和通知推送实现；这些技术实现不能直接迁移到 Cosmic 二开工程。

本次迁移只提取可稳定复用的业务语义：诊断输入、诊断输出、AI 返回解析、关键词抽取、关键词状态、resumeId 所有权校验、诊断分数回写和 WebAPI 契约。真实 AI 适配、PDF/OSS 读取、通知和前端页面保留为后续平台适配。

## Goals / Non-Goals

**Goals:**

- 定义并实现 JDK 8 兼容的简历诊断 DTO，包括请求、结果、关键词、关键词状态和诊断文本来源。
- 提供纯 Java helper：解析结构化或非结构化诊断响应、提取首个 0-100 分数、清洗关键词、合并重复关键词、过滤联系方式和停用词。
- 提供应用服务边界：支持按 resumeId 或直接文本发起诊断；按用户 ID 校验简历归属；保存诊断结果后更新简历记录和职业画像。
- 提供关键词抽取边界：支持读取状态、触发抽取、force 重算和错误状态表达。
- 暴露 Cosmic WebAPI 风格入口，保持与已迁移 resume WebAPI 的用户 ID 参数风格一致。
- 增加聚焦测试，覆盖解析、关键词、所有权、回写画像和 WebAPI 契约。

**Non-Goals:**

- 不迁移 Spring Boot、JPA、Flyway、Lombok、PDFBox、OSS SDK、DashScope/Qwen SDK 或 Spring 异步执行器。
- 不实现真实 PDF 文本解析；只定义可替换的文本来源边界，并优先复用 `ResumeRecordDto.parsedContent`。
- 不实现真实 AI 生成；默认诊断分析器可以是确定性规则或可替换边界，后续再接入平台 AI。
- 不迁移通知推送、Vue/uni-app 页面、管理端或最终 Cosmic datamodel。

## Decisions

### 1. 将“诊断生成”拆成可替换边界

应用服务 SHALL 依赖 `ResumeDiagnosisAnalyzer` 风格的边界来生成原始分析文本或诊断结果。默认实现使用确定性规则和输入文本生成可测试结果；后续 AI 适配器可以替换该边界，而无需修改 WebAPI、DTO 或结果解析 helper。

原因：IPD 的核心价值是“简历 + JD + 画像上下文 → 诊断结果”，不是具体 Qwen 调用方式。可替换边界能保持 Cosmic 工程可构建、可测试，也避免在本 change 中引入外部 SDK。

### 2. 优先复用已迁移的简历记录与画像边界

当请求提供 resumeId 且未提供 resumeText 时，应用服务 SHALL 从 `ResumeApplicationService` 读取用户自己的简历记录，并优先使用 `parsedContent` 作为诊断文本。诊断保存后通过已有简历更新能力写入 `diagnosisScore`，从而复用 `resume-core` 的画像同步路径。

原因：这样不会重复建立简历所有权逻辑，也让今日行动已经定义的“简历低分”规则自然消费诊断结果。

### 3. 将 PDF/OSS 解析作为文本来源适配，不在本次实现

IPD 的 `PdfTextExtractor` 只迁移“最大文本长度、失败返回空文本、避免让解析失败中断主流程”的契约语义。具体 PDFBox 依赖和 OSS 下载不进入本次实现。

原因：CyanCruise 文件服务和 Cosmic datamodel 适配尚未定型，提前引入 PDF 依赖会扩大构建和运行边界。

### 4. 关键词抽取保持确定性、有限词表和状态化

关键词 helper SHALL 按 IPD 语义从目标岗位、标题、解析 JSON 字段、原始文本和更新时间抽取关键词，过滤联系方式、停用词、过长 token 和非技能时间 token，按类别+标签去重并保留最高权重。关键词状态 SHALL 支持 `PENDING`、`PROCESSING`、`READY`、`EMPTY`、`FAILED`。

原因：关键词是画像和后续推荐的中间信号，必须可重复、可测试；异步调度和持久化实现可在后续 Cosmic 适配中替换。

### 5. WebAPI 保持当前迁移风格

新增 WebAPI 使用显式 `userId` 参数，提供诊断触发、关键词状态、关键词抽取、force 重算等入口。所有按 resumeId 的操作 SHALL 先校验用户所有权。

原因：当前已迁移 WebAPI 都先使用显式用户 ID，以避开平台当前用户解析未完成的问题；后续可统一替换为 Cosmic 身份上下文。

## Risks / Trade-offs

- [Risk] 默认诊断不是真实 AI 质量 → Mitigation：本次只承诺契约、解析和回写；AI 适配器作为后续 change 接入。
- [Risk] PDF 简历无 parsedContent 时无法诊断 → Mitigation：明确返回文本为空错误，并在设计中保留文本来源边界。
- [Risk] 关键词抽取不如 AI 或 NLP 精准 → Mitigation：迁移 IPD 的有限词表和过滤规则，聚焦 CareerLoop 可消费信号。
- [Risk] 诊断结果可能覆盖已有分数 → Mitigation：只有诊断成功且分数明确时更新 `diagnosisScore`，并通过测试验证画像同步。

## Migration Plan

1. 新增 DTO 和常量，不引入 Spring/JPA/Lombok/Jackson 注解。
2. 新增 helper 并覆盖诊断解析、分数兜底、关键词清洗、去重和状态规则。
3. 新增诊断存储边界与默认实现，保留后续 Cosmic datamodel 替换点。
4. 新增应用服务，复用 `ResumeApplicationService` 校验归属和回写分数。
5. 新增 Cosmic WebAPI 与聚焦测试。
6. 更新主规格、迁移地图和归档 change；运行 OpenSpec 与 JDK 8 Gradle 验证。

## Open Questions

- 后续 AI 适配使用哪个 Cosmic 可用服务或外部网关，需要在 AI 接入 change 中确认。
- PDF 文本解析应落在文件服务适配还是简历诊断适配，需要等 `filestorage` 边界稳定后确认。
- 关键词是否进入统一画像 fact/tag 存储，需要在最终 Cosmic datamodel 或画像标签 change 中细化。
