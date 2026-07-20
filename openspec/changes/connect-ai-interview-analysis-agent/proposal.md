## Why

AI 模拟面试和全景仿真面试虽然已经保存问答并调用 AI 网关，但当 AI 未配置、调用失败或返回无效报告时，系统会按回答次数生成固定分数和固定优缺点，导致用户把规则占位结果误认为真实 AI 分析。现在需要像简历诊断一样接入独立的面试智能体任务流，只保存经过结构校验、确实来自问答内容的分析结论。

## What Changes

- 为面试场景增加独立的智能体任务流配置与调用适配器，支持苍穹 Agent SDK 和现有服务端任务流调用方式，配置项与简历诊断隔离。
- 普通 AI 模拟面试和全景仿真面试共用已持久化的岗位、难度、问题和回答，生成总分、五项能力评分、评分依据、做得好的地方、改进方向与总结。
- 加强结构化结果校验：总分与分项分数必须合法，优点和改进项必须包含来自实际问答的证据，不接受空洞或缺字段的报告。
- **BREAKING**：删除按回答次数计算分数的 `fallbackReport` 和固定“完成了有效练习”“补充具体证据”等虚假分析；AI 未配置、调用失败或报告无效时不保存报告和分数，页面明确提示稍后重试。
- 全景仿真面试结果页与普通 AI 模拟面试结果页展示同一套真实分析内容，不再只显示总分和泛化总结。

## Capabilities

### New Capabilities

- `agent-platform-interview-analysis-adapter`: 定义面试智能体任务流配置、真实问答上下文调用、结构化报告校验以及不可用状态语义。

### Modified Capabilities

- `ai-mock-interview`: 将“AI 无效时生成基础复盘”改为明确失败且不保存虚假评分，并要求普通面试与全景面试展示有问答证据的真实分析。
- `ai-infrastructure`: 收紧面试报告降级边界，禁止用规则分数伪装为模型分析结果。

## Impact

- IPD 语义来源：`F:\Project\IPD\backend` 中面试问答、AI 调用与报告契约，以及 `F:\Project\IPD\frontend\src\pages\interview` 中复盘展示流程；不迁移 Spring Boot、Vue 或 uni-app 实现。
- CyanCruise 目标模块：`code/base/v620-cc001-base-common/` 的面试报告契约、`code/base/v620-cc001-base-helper/` 的提示词与校验辅助、`code/cloud01/v620-cc001-cloud01-app01/` 的 Agent 调用和应用服务、`webapp/isv/v620/cyancruise/` 的两类面试结果页。
- 数据映射：沿用 `v620_cc_interview`/`InterviewStorage` 的会话、消息、报告和最终分数字段，不新增数据库或外部依赖；仅当真实 AI 报告通过校验后写入 `report`、`finalScore` 并同步画像。
- 暂不迁移：视频上传、表情/视线/肢体动作识别、声纹或语速算法；全景模式本次只分析语音识别后的文字问答。
- 验证方式：增加智能体适配器、报告解析、失败不落库、两种模式复用报告的测试，执行前端路由校验、OpenSpec 严格校验和 JDK 8 Gradle 构建。
