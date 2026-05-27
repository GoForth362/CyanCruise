## 1. Rule Coverage

- [x] 1.1 新增 `CareerAgentTodayRuleService` 聚焦测试。
- [x] 1.2 覆盖缺少目标岗位时推荐选择目标岗位和测评。
- [x] 1.3 覆盖目标岗位存在但缺少测评时推荐测评基线。
- [x] 1.4 覆盖目标岗位和测评存在但缺少系统简历时推荐简历创建/上传。
- [x] 1.5 覆盖简历诊断分数低于阈值时推荐简历优化。
- [x] 1.6 覆盖面试缺失、面试低分和执行节奏不足分支。
- [x] 1.7 覆盖 onboarding 转行、找实习无简历、应届自报有简历路径。
- [x] 1.8 覆盖周重点最多追加两个 `PLAN_WEEKLY` 行动。

## 2. User Input Source

- [x] 2.1 新增基于 `CareerProfileApplicationService` 的 `CareerAgentRuleInputSource` 实现。
- [x] 2.2 输入源 SHALL 读取用户画像快照并组装 `CareerAgentRuleInput.snapshot`。
- [x] 2.3 输入源 SHALL 保留默认空打卡状态和空周重点，后续可由 datamodel/计划适配增强。
- [x] 2.4 保持未配置输入源时明确抛出未配置错误。

## 3. Application And WebAPI Boundary

- [x] 3.1 确认 `CareerAgentTodayApplicationService.recommend` 继续支持直接规则输入。
- [x] 3.2 确认 `CareerAgentTodayApplicationService.recommendByUserId` 使用输入源生成推荐。
- [x] 3.3 扩展 `CareerAgentWebApi`，新增按用户 ID 获取今日推荐入口。
- [x] 3.4 WebAPI 保持 Cosmic 注解风格和 `/cc001/career-agent` 路径，不引入 Spring、JPA、LLM 或 AgentTask 依赖。

## 4. Application Tests

- [x] 4.1 测试按用户 ID 推荐能读取职业画像中的目标岗位。
- [x] 4.2 测试按用户 ID 推荐能消费测评和简历信号，进入面试或后续阶段。
- [x] 4.3 测试 WebAPI 按用户 ID 入口返回今日行动推荐。

## 5. Migration Documents

- [x] 5.1 更新 `docs/ipd-to-cyancruise-migration-map.md` 中 AI 今日任务状态。
- [x] 5.2 在文档中保留 AgentTask 持久化、风险看板、长期计划联动、当前用户身份解析和 webapp 页面仍待后续迁移的说明。

## 6. Validation

- [x] 6.1 运行 `openspec validate migrate-today-action-recommendation --strict`。
- [x] 6.2 运行 `openspec validate --all --strict`。
- [x] 6.3 设置 JDK 8 后运行相关 Gradle 测试和 `clean build`。
