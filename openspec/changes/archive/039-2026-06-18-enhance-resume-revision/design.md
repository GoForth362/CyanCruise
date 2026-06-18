## Context

CyanCruise 当前已完成 CareerLoop 的多个基础能力：`resume-core` 保存真实简历记录并同步画像 resume block，`file-upload-preview` 提供稳定 object key、预览 URL 和文本提取边界，`resume-diagnosis` 已能基于 resumeId 或文本产生分数、优缺点和 suggestions，`career-profile-onboarding` 与 `assessment-core` 提供目标岗位、测评和画像上下文，`ai-infrastructure` 提供生产 AI provider 的可替换边界。

本 change 面向“简历诊断”而不是重新实现简历创建。实现必须兼容 JDK 1.8，使用仓库内 `gradlew.bat` 构建，不在业务代码中硬编码 `cosmic_home`、`project_dir` 等本地路径，不新增依赖，且 OpenSpec 文档保持中文。IPD 来源仅作为业务语义参考，不迁移 Spring Boot、JPA、Flyway、Vue、uni-app、PDFBox 或 OSS SDK 运行时。

## Goals / Non-Goals

**Goals:**

- 在 DTO/helper/service/WebAPI/page 层形成“选择已有简历 -> 结合目标岗位/岗位要求/画像诊断 -> 查看结构化建议 -> 确认优化方向 -> 再次诊断”的闭环。
- 让建议项可落地：包含问题类型、优先级、建议动作、示例改写方向、证据来源、目标关键词和完成状态。
- 复用现有简历、文件、画像、AI gateway 和 PostgreSQL 存储边界；如保存建议结果，优先落在既有简历诊断 payload 中。
- 页面风格沿用 `webapp/isv/v620/cyancruise` 的静态 hash route、卡片、表单、状态提示和 KAPI 调用模式。
- 增加 focused tests 和静态校验，确保 JDK 1.8、route metadata 和 OpenSpec 均可验证。

**Non-Goals:**

- 不实现完整富文本简历编辑器、模板库、PDF 导出、版本 diff、在线排版或自动覆盖原简历。
- 不新增外部依赖，不引入 IPD 的 Spring Multipart、JPA Repository、Flyway SQL、PDFBox、OSS SDK、Vue/uni-app/Pinia/Vite/uView。
- 不改变 `/cc001/resume/*` 和 `/cc001/files/*` 的既有稳定契约；只在必要时扩展简历诊断/修改 DTO 与页面消费。
- 不把预签名 URL、access token、Authorization header、endpoint secret 或客户私有凭据写入业务记录、route metadata 或 OpenSpec 文档。

## Decisions

### 1. 以 `resume-revision` 表达闭环语义，复用 `resume-diagnosis` 执行入口

简历诊断闭环新增 DTO 表达建议和用户选择，但诊断触发继续走现有 `/cc001/resume-diagnosis/analyze`。请求中补充目标岗位、目标岗位要求 和画像上下文；响应中在现有 strengths/weaknesses/suggestions 之外增加结构化 revision suggestions。

理由：现有诊断 WebAPI 已具备 resumeId 所有权校验、AI/fallback analyzer、结果持久化和画像回写。复用入口可避免新增重复接口，也能让“诊断”和“诊断建议”共享相同审计与存储语义。

替代方案是新增 `/cc001/resume-revision/*` 全套接口。该方案边界更清晰，但会造成与 `resume-diagnosis` 的分数、文本来源、持久化、AI 调用重复，当前阶段收益不足。

### 2. 结构化建议使用 JDK 1.8 DTO，不依赖 JSON 动态字段

在 `base-common` 中新增或扩展 JDK 1.8 兼容 DTO，例如 `ResumeRevisionSuggestionDto`、`ResumeRevisionPlanDto` 或等价字段；helper 负责把 AI JSON、IPD 文本建议和 deterministic fallback 归一成列表。

理由：页面、WebAPI 和测试需要稳定字段；JDK 1.8 DTO 避免依赖 Lombok、Jackson 注解或 Java 17 record。PostgreSQL 存储可继续序列化 payload_json，但业务层不直接依赖 Map 结构。

替代方案是只把建议塞入 `List<String> suggestions`。该方案改动小，但无法表达优先级、证据、关键词和完成状态，不能支持后续闭环。

### 3. 画像和目标岗位作为上下文，不替代真实简历

应用服务可读取 `UserProfileSnapshot` 的目标岗位、测评摘要、resume block 和偏好，作为诊断提示词与 fallback 规则输入；但只有系统真实保存的 `ResumeRecordDto` 才能作为 resumeId 诊断来源。

理由：IPD 主循环强调工具之间共享画像，但当前仓库已明确 onboarding 自报简历不能替代真实简历记录。保持这个边界可避免页面误判“已有简历”。

替代方案是在没有 resumeId 时自动使用画像 resume block。该方案可能引用过期或自报数据，不利于所有权和文本来源校验。

### 4. 页面以现有静态资源模式增强，不引入前端构建链

`assets/app.js` 中增强 `resume-diagnosis` route：读取简历列表和画像快照，允许选择简历、填写目标岗位要求，提交诊断后渲染分数、强弱项、结构化建议和下一步行动。`index.html`、`assets/app.js` 或 `assets/styles.css` 改动后同步更新静态资源版本号。

理由：当前 CyanCruise webapp 是无构建静态资源，部署路径和 KDDT 静态发布已经围绕该模式建立。继续沿用能降低 Cosmic 门户部署复杂度。

替代方案是引入模块化前端或复用 IPD Vue 页面。该方案会破坏当前静态资源约束，并引入不必要依赖。

### 5. 持久化优先复用简历诊断 payload

诊断结果和建议项优先存入现有 `ResumeDiagnosisResultDto` 对应的 PostgreSQL payload_json；不新增表结构，除非实现中发现现有 payload 无法表达必要字段。若不新增表，`postgresql-business-storage` 无需变更。

理由：已有 `PostgresqlResumeDiagnosisStorage` 按 resumeId 保存诊断结果，适合作为闭环的最新状态来源。避免表结构变更可降低 Cosmic/KDDT 模板风险。

替代方案是新增 revision plan 存储表。该方案适合后续多版本优化计划，但当前目标是最新诊断闭环，暂不需要版本化。

## Risks / Trade-offs

- [AI 返回结构不稳定] -> helper 对 JSON 与普通文本均做容错解析；无 AI 或 AI 失败时返回 deterministic fallback 建议。
- [页面和后端字段不一致] -> 增加 DTO/helper/WebAPI focused tests，并运行 `node --check` 与 `validate-routes.js`。
- [静态资源缓存命中旧版本] -> 修改 `index.html`、`assets/app.js` 或 `assets/styles.css` 时同步更新资源版本号，并在交付说明中标注需要重新部署静态资源。
- [画像上下文缺失] -> 页面和服务都允许无画像运行，显示“建议补齐目标岗位/测评”而不是中断诊断。
- [文件 adapter 不可用] -> 简历诊断页面只依赖已保存 resumeId/parsedContent；文件预览和文本提取失败作为局部状态，不阻断建议查看。
- [PostgreSQL payload 字段扩展兼容] -> 新字段以 DTO 可选字段表达，旧诊断记录没有 revision suggestions 时页面退回普通 suggestions 展示。

## Migration Plan

1. 完成并校验 OpenSpec artifacts：proposal、design、delta specs、tasks。
2. 增加或扩展 `base-common` DTO 与 `base-helper` 解析/规则 helper，保持 JDK 1.8。
3. 增强 `ResumeDiagnosisApplicationService`、AI prompt/fallback analyzer 和 WebAPI 测试，复用现有存储和画像服务。
4. 增强 `webapp/isv/v620/cyancruise` 的 `resume-diagnosis` 页面、样式和 route metadata；更新静态资源版本号。
5. 运行 `openspec validate enhance-resume-revision --strict`、focused Java tests、`node --check webapp\isv\v620\cyancruise\assets\app.js`、`node webapp\isv\v620\cyancruise\validate-routes.js`；必要时使用 JDK 8 执行 `.\gradlew.bat clean build`。
6. 回滚方式：恢复本 change 修改的静态资源和 DTO/service 代码；既有 `/cc001/resume/*`、`/cc001/files/*`、`/cc001/resume-diagnosis/analyze` 基础契约保持兼容，不需要数据迁移回滚。

## Open Questions

- 简历诊断建议是否需要长期保存“已采纳/已忽略”状态，还是本阶段只保存最新建议和页面本地勾选状态？
- 如果用户没有填写目标岗位要求，是否只使用目标岗位关键词，还是要求先补充 岗位要求 后再生成高置信建议？
- 页面是否需要在建议项上提供“复制示例改写”按钮，还是仅展示建议和下一步行动？
