## 1. Shared Contracts

- [x] 1.1 新增面试会话、消息、报告、雷达分、建议项和请求 DTO，保持 JDK 8 兼容。
- [x] 1.2 明确面试状态、模式、消息角色和难度取值常量。
- [x] 1.3 保持 DTO 不依赖 Spring、JPA、Cosmic datamodel、语音 SDK 或外部 AI SDK。

## 2. Helper Rules

- [x] 2.1 新增面试 helper，标准化模式、难度、状态和消息角色。
- [x] 2.2 实现结束面试时长计算规则。
- [x] 2.3 实现报告雷达分强弱维度提取规则。
- [x] 2.4 实现面试会话和报告到 `UserProfileSnapshot.InterviewBlock` 的转换规则。
- [x] 2.5 增加 helper 聚焦测试，覆盖标准化、时长、报告强弱项和画像转换。

## 3. Storage And Application Boundary

- [x] 3.1 新增 `InterviewStorage` 边界，支持会话、消息、报告读取保存和删除。
- [x] 3.2 新增默认存储实现，并测试跨新 service/storage 实例可读回面试和消息。
- [x] 3.3 新增 `InterviewApplicationService`，支持开始、追加消息、读取消息、结束、保存报告、读取报告、历史、详情和删除。
- [x] 3.4 应用服务 SHALL 对所有按 interviewId 操作执行用户所有权校验。

## 4. Profile Integration

- [x] 4.1 扩展职业画像应用边界，支持保存或合并 interview block。
- [x] 4.2 面试结束时同步基础 interview block。
- [x] 4.3 保存报告时同步强项、弱项和最终分数。
- [x] 4.4 增加画像集成测试，覆盖面试结束和报告保存不会覆盖其他画像 block。

## 5. WebAPI

- [x] 5.1 新增 Cosmic WebAPI 开始面试入口。
- [x] 5.2 新增 Cosmic WebAPI 追加消息和读取消息入口。
- [x] 5.3 新增 Cosmic WebAPI 结束面试、保存报告和读取报告入口。
- [x] 5.4 新增 Cosmic WebAPI 历史、详情和删除入口。
- [x] 5.5 增加 WebAPI 聚焦测试，覆盖开始、消息、结束、报告和跨用户拒绝。

## 6. Migration Documents

- [x] 6.1 更新 `docs/ipd-to-cyancruise-migration-map.md` 中模拟面试状态。
- [x] 6.2 记录题库管理、AI 追问/报告生成、语音/身体语言、通知、webapp 页面和最终 Cosmic datamodel 仍为后续迁移项。

## 7. Validation

- [x] 7.1 运行 `openspec validate migrate-interview-core --strict`。
- [x] 7.2 运行 `openspec validate --all --strict`。
- [x] 7.3 设置 JDK 8 后运行相关 Gradle 测试。
- [x] 7.4 设置 JDK 8 后运行 `.\gradlew.bat clean build`。
