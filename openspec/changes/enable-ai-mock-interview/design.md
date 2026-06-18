## Context

`migrate-interview-core` 已实现会话、消息、报告 DTO、画像同步、文件/内存/PostgreSQL 存储边界和 Cosmic WebAPI；`migrate-ai-provider-production-adapter` 已实现 provider-neutral `AiGateway`。当前 `InterviewAiService` 只有两个返回原始字符串的方法，未组装简历、岗位和画像上下文，未解析报告，也没有被 WebAPI 调用。webapp 的 `interview` 路由仍落入通用契约页，只能展示历史数据。

本 change 参考 IPD 的开场问题、逐轮追问、完整记录复盘和报告缓存语义，但以 CyanCruise 现有纯 Java/Cosmic 边界重新实现，不复制 Spring Controller、JPA、Flyway、Vue 或 uni-app 代码。所有新增 Java 代码必须兼容 JDK 1.8，用户可见文案使用清晰中文。

## Goals / Non-Goals

**Goals:**

- 让用户从目标岗位和已有简历开始一场文本模拟面试。
- 以单个应用入口完成“保存回答并返回下一题”，保持消息顺序和所有权校验。
- 基于完整对话生成结构化中文报告，保存后重复读取不再调用 AI。
- AI 不可用或返回格式错误时提供可继续练习、可解释的本地降级结果。
- 在 webapp 提供准备、对话、结束、报告和历史查看的完整状态流。

**Non-Goals:**

- 不迁移语音识别/合成、数字人、摄像头、身体语言评分或音视频文件。
- 不迁移题库管理、AI 批量出题、题目审核、贡献与点赞。
- 不引入 Spring、JPA、Flyway、Vue、uni-app 或外部 AI SDK。
- 不改变现有存储表结构，不实现新的文件上传能力。

## Decisions

1. 在 `InterviewApplicationService` 上增加编排方法，不另建第二套会话服务

   开场、轮次与结束复盘都需要现有所有权、消息、存储和画像同步规则。编排方法内部调用既有保存能力，并通过可注入的 AI 生成器获取问题或报告，避免 WebAPI 手工拼接多个低层接口。原有细粒度接口继续保留以兼容现有调用方。

2. 新增结构化轮次响应，报告继续复用现有 DTO

   “用户消息 + 面试官消息”是一个原子用户动作，需要返回会话和新消息，避免页面在多次请求之间丢失状态。报告字段已经由 `InterviewReportDto`、`InterviewRadarScoreDto` 和 `InterviewAdviceItemDto` 表达，不再建立平行模型。

3. 上下文在应用层按所有权读取，提示词构造和 JSON 校验放在 helper

   应用层通过 `ResumeApplicationService.get(userId, resumeId)` 和 `CareerProfileApplicationService.getSnapshot(userId)` 获取可用上下文。helper 只消费普通 DTO/字符串，负责裁剪简历文本、生成中文指令、提取 JSON、限制 0—100 分值和过滤空建议，从而保持可聚焦测试且不依赖 Cosmic。

4. AI 网关失败时使用确定性本地降级

   开场和追问回退到围绕岗位、经历、问题处理和复盘的中文问题；报告根据有效回答轮数生成保守分值、明确优势与改进建议。降级内容不伪装成真实 AI 深度评价，并保证用户仍能结束流程。相比直接报错，此方案更符合现有 AI 基础设施的“未配置可降级”约定。

5. 报告采用先查缓存、后生成并保存的语义

   若会话已有报告，结束复盘入口直接返回；否则至少存在一条用户回答才可生成。生成成功或降级后调用现有 `saveReport`，同步最终分数与画像。并发重复请求不新增分布式锁，本阶段依靠保存后的读取和页面防重复提交降低概率。

6. webapp 使用现有单页原生 JavaScript 状态机

   新页面继续沿用 `assets/app.js`、`post()`、身份解析和现有样式体系，不引入前端框架。页面选择已有简历、默认带入画像目标岗位；对话仅展示文本消息；报告使用分数、五项能力、做得好的地方和改进建议等普通中文表达。

## Risks / Trade-offs

- [Risk] AI 返回的 JSON 结构不稳定。→ 使用 `AiJsonHelper` 提取对象、逐字段容错与范围限制，失败时生成本地复盘。
- [Risk] 简历正文过长导致上下文膨胀。→ 只取所选简历的必要字段并限制正文字符数，对话记录也限制总长度。
- [Risk] 一次回答请求中用户消息已保存但 AI 调用失败。→ 保存降级面试官问题，保证一次轮次最终仍有成对消息。
- [Risk] 页面刷新丢失内存状态。→ 通过 interviewId 重新读取会话、消息和已缓存报告；前端只保存当前选中 ID，不作为业务真相。
- [Risk] 本地降级评分精度有限。→ 文案明确这是基于本次练习记录的基础复盘，后续可由已配置 AI 生成更细致结果。

## Migration Plan

1. 增加公共轮次契约和 helper 规则，先完成聚焦测试。
2. 扩展 AI 适配与面试应用服务，接通简历、画像和报告缓存。
3. 扩展 Cosmic WebAPI，同时保留全部既有路径。
4. 实现 webapp 模拟面试状态流并更新路由契约说明。
5. 更新迁移映射，运行 OpenSpec strict 校验、前端路由校验、聚焦 Gradle 测试和 JDK 8 `gradlew.bat clean build`。

回滚时删除新增编排入口、契约、helper 和页面状态，恢复 `interview` 路由到通用契约页；既有会话、消息、报告与数据无需迁移或回滚。

## Open Questions

- 题库能力后续应先接官方题目筛选，还是先增加用户自选练习主题？
- 生产环境是否需要为报告生成增加跨实例幂等锁和独立调用审计？

