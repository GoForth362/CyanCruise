## Context

CyanCruise 已完成 CareerLoop 的画像、测评、简历、今日行动和职业计划摘要。今日行动规则已经会在方向、测评和简历齐全后推荐模拟面试，也会在面试低分时进入专项练习分支，但目前 CyanCruise 还没有能保存模拟面试会话、消息、结束状态或报告摘要的后端契约。

IPD 模拟面试能力覆盖范围很大：文本面试、语音面试、题库抽题、AI 追问、报告生成、身体语言、通知、历史列表、删除和画像同步。本次只迁移“基础会话与报告契约”，即不依赖外部 AI 的可测试后端基础。真实 AI 问答、语音、题库管理和 webapp 页面后续拆分。

IPD 来源证据：

| 来源 | 迁移参考 |
| --- | --- |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\service\InterviewService.java` | 开始面试、发消息、取消息、结束、历史、所有权、保存报告、删除 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\InterviewServiceImpl.java` | mode/difficulty/status 语义、结束时长、报告后画像同步、强弱维度提取 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\InterviewController.java` | `/start`、`/message`、`/messages`、`/end`、历史、详情、删除流程 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\InterviewReportController.java` | 报告 DTO、雷达分、总分、优势/改进建议、缓存语义 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\Interview.java` | 会话字段、状态、模式、报告、时长 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\InterviewMessage.java` | 消息字段、角色、顺序 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\InterviewQuestion.java` | 题库字段作为后续题库 change 的来源边界 |
| `F:\Project\IPD\backend\src\main\resources\db\migration\V1__baseline_schema.sql` | `interviews`、`interview_messages`、`interview_questions` 数据字段 |
| `F:\Project\IPD\backend\src\test\java\com\group1\career\service\InterviewServiceTest.java` | 核心服务测试依据 |
| `F:\Project\IPD\backend\src\test\java\com\group1\career\controller\InterviewControllerTest.java` | API 行为测试依据 |

## Goals / Non-Goals

**Goals:**

- 新增 `interview-core` 主规格。
- 新增 JDK 8 DTO 表达面试会话、消息、报告、雷达分、建议项和请求对象。
- 新增纯 Java helper，负责 mode/difficulty/status 标准化、结束时长计算、报告强弱维度提取、画像 interview block 转换。
- 新增 `InterviewStorage` 边界和默认存储实现，支持会话与消息按用户隔离保存。
- 新增 `InterviewApplicationService`，支持开始、追加消息、读取消息、结束、保存报告、历史列表、详情、删除。
- 新增 Cosmic WebAPI，使用显式 userId 调用应用服务，保持当前迁移模块风格。
- 面试结束和保存报告时更新 `UserProfileSnapshot.InterviewBlock`，使画像与今日行动能消费面试信号。
- 更新迁移映射表和 OpenSpec 主规格。

**Non-Goals:**

- 不迁移 Spring Boot Controller、JPA Repository、Flyway SQL 或数据库外键。
- 不迁移真实 AI 问答、AI 报告生成、LLM prompt、DashScope 调用或 function calling。
- 不迁移语音/ASR/TTS、数字人、身体语言评分和音频文件能力。
- 不迁移题库管理、官方题库 seed、AI 出题任务、点赞/贡献/审核后台。
- 不迁移通知、订阅消息、打卡联动和周报。
- 不创建 webapp 或 uni-app 页面。
- 不实现当前登录用户解析，仍使用显式 `userId` 和所有权校验。

## Decisions

1. 将面试核心拆为“会话/消息/报告摘要”，题库和 AI 后续独立迁移

   IPD `InterviewController` 同时承担文本、语音、题库和 AI 编排。CyanCruise 先落一个无外部依赖的会话模型，保证用户面试历史、消息顺序、结束状态和报告摘要能够被保存与测试。题库和 AI 生成后续通过同一会话边界接入。

2. DTO 不保留 JPA/Mongo/JSON 字段名耦合

   IPD `Interview` 有 `report_json`、`report_mongo_id` 等存储字段。本次 DTO 直接表达业务含义：会话、消息、报告摘要、雷达分、建议项。报告不以原始 JSON 为唯一来源，但可保留 `reportSummary` 和结构化字段，方便后续 AI 报告适配。

3. 应用服务负责所有权校验

   IPD 每个 controller endpoint 都先解析当前用户并 `assertOwnership`。CyanCruise 当前迁移 WebAPI 使用显式 `userId`，因此应用服务方法 SHALL 校验 interviewId 是否属于该用户；跨用户读取、写入、结束、报告和删除都返回明确错误。

4. 面试结束与报告保存都同步画像

   IPD `endInterview` 会先写入岗位和分数，`saveReport` 会再补强弱维度。本次沿用该语义：结束面试时可写入基础面试 block；保存报告时根据雷达分或报告维度刷新 strong/weak dimensions。

5. 存储边界延续文件型迁移模式

   和画像、简历、职业计划一致，默认实现用文件型或内存型存储验证跨实例读回。未来 Cosmic datamodel 实现 `InterviewStorage` 后，helper、应用服务和 WebAPI 契约不变。

## Risks / Trade-offs

- [Risk] 暂不接 AI，无法自动生成下一题或真实报告。→ Mitigation: 提供保存消息和保存报告入口，后续 AI 适配只需调用应用服务。
- [Risk] 文件型存储不是最终 Cosmic datamodel。→ Mitigation: 所有读取写入通过 `InterviewStorage` 边界，后续替换适配器。
- [Risk] 显式 userId 与最终安全模型不同。→ Mitigation: 所有权校验先在应用层实现，后续当前用户解析可以替换 WebAPI 入参来源。
- [Risk] 报告强弱项规则可能与 AI 文案不完全一致。→ Mitigation: helper 只基于结构化雷达分生成强弱维度；AI 解释文本作为后续增强。
- [Risk] 题库字段现在只作为来源证据。→ Mitigation: 题库拆成 `migrate-interview-question-bank` 或等价后续 change。

## Migration Plan

1. 新增 `interview-core` delta spec，并补充职业画像面试同步要求。
2. 新增 base-common 面试 DTO。
3. 新增 base-helper 面试 helper 和聚焦测试。
4. 新增 cloud app 面试存储边界、默认存储、应用服务和测试。
5. 新增 Cosmic WebAPI 和边界测试。
6. 接入 `CareerProfileApplicationService.saveInterview` 或等价画像同步路径。
7. 更新迁移映射表。
8. 运行 `openspec validate migrate-interview-core --strict`、`openspec validate --all --strict`、JDK 8 Gradle 测试和 `.\gradlew.bat clean build`。

回滚策略：删除新增面试 DTO/helper/storage/application/WebAPI 和测试，撤回画像 interview block 同步要求，删除 active change；已有画像、测评、简历、今日行动和职业计划能力不受影响。

## Open Questions

- 题库迁移是否优先做 `migrate-interview-question-bank`，还是先做 AI 追问/报告生成？
- 保存报告时是否需要保留原始 AI JSON，还是只保留结构化 DTO？
- 后续打卡和通知是否由面试完成事件触发，还是等 AgentTask 持久化后统一处理？
