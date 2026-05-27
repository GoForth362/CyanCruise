## 1. Shared Contracts

- [x] 1.1 新增简历诊断请求、结果、关键词、关键词状态和常量 DTO，保持 JDK 8 兼容。
- [x] 1.2 DTO SHALL 不依赖 Spring、JPA、Lombok、Jackson、PDFBox、OSS SDK 或外部 AI SDK。
- [x] 1.3 明确诊断状态、关键词状态、关键词类别和兜底分数常量。

## 2. Helper Rules

- [x] 2.1 新增简历诊断 helper，解析 JSON 格式 AI 响应为结构化诊断结果。
- [x] 2.2 实现非结构化文本分数提取和 0-100 分数钳制规则。
- [x] 2.3 实现诊断解析兜底规则，覆盖空响应、非法 JSON 和无合法分数。
- [x] 2.4 新增关键词抽取 helper，迁移 IPD 的有限词表、停用词、联系方式过滤、时间 token 和类别权重语义。
- [x] 2.5 实现关键词按 category+label 去重并保留最高权重。
- [x] 2.6 增加 helper 聚焦测试，覆盖 JSON 解析、文本兜底、关键词过滤、去重和 EMPTY 状态。

## 3. Storage And Application Boundary

- [x] 3.1 新增 `ResumeDiagnosisAnalyzer` 或等价诊断分析边界，默认实现不调用真实 AI。
- [x] 3.2 新增关键词/诊断状态存储边界，支持状态、错误信息、关键词列表和 force 重算。
- [x] 3.3 新增默认存储实现，允许测试跨新 service/storage 实例读回诊断与关键词状态。
- [x] 3.4 新增 `ResumeDiagnosisApplicationService`，支持按 resumeId 或 resumeText 发起诊断。
- [x] 3.5 应用服务 SHALL 对按 resumeId 的诊断、关键词读取、触发和回写执行用户所有权校验。
- [x] 3.6 诊断成功后通过简历基础应用服务更新 diagnosisScore，并同步画像 resume block。

## 4. WebAPI

- [x] 4.1 新增 Cosmic WebAPI 触发简历诊断入口。
- [x] 4.2 新增 Cosmic WebAPI 读取关键词状态入口。
- [x] 4.3 新增 Cosmic WebAPI 触发关键词抽取和 force 重算入口。
- [x] 4.4 增加 WebAPI 聚焦测试，覆盖诊断成功、关键词状态和跨用户拒绝。

## 5. Migration Documents

- [x] 5.1 更新 `docs/ipd-to-cyancruise-migration-map.md` 中简历诊断状态。
- [x] 5.2 记录 AI 真实调用、PDF/OSS 文本解析、通知推送、webapp 页面和最终 Cosmic datamodel 仍为后续迁移项。
- [x] 5.3 将 delta spec 同步到 `openspec/specs/resume-diagnosis/` 和 `openspec/specs/resume-core/`。

## 6. Validation

- [x] 6.1 运行 `openspec validate migrate-resume-diagnosis --strict`。
- [x] 6.2 运行 `openspec validate --all --strict`。
- [x] 6.3 设置 JDK 8 后运行相关 Gradle 测试。
- [x] 6.4 设置 JDK 8 后运行 `.\gradlew.bat clean build`。
