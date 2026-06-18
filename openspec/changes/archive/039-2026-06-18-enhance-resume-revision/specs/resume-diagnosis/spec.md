## ADDED Requirements

### Requirement: 输出简历诊断建议
简历诊断结果 SHALL 在保留 `overallScore`、`strengths`、`weaknesses`、`suggestions` 和 `rawAnalysis` 的基础上，支持返回结构化简历诊断建议和优化计划摘要。旧调用方未消费新字段时，既有字段语义 SHALL 保持兼容。

#### Scenario: AI 返回结构化诊断建议
- **WHEN** AI 诊断响应包含 revision suggestions JSON
- **THEN** 系统解析为结构化建议列表，并同步保留普通 suggestions 以兼容旧页面

#### Scenario: AI 只返回普通文本
- **WHEN** AI 或 fallback analyzer 只返回普通建议文本
- **THEN** 系统从普通文本生成至少一条可展示的诊断建议，或返回空建议列表和明确的 fallback 状态

#### Scenario: 旧诊断记录可读取
- **WHEN** PostgreSQL 中已有旧版诊断 payload 没有结构化建议字段
- **THEN** 系统仍能读取诊断结果，并让页面退回展示 `suggestions`

### Requirement: 保存最新诊断建议
按 `resumeId` 完成的简历诊断 SHALL 将最新结构化诊断建议随诊断结果保存到现有简历诊断存储边界，并 SHALL 将 `overallScore` 和关键建议摘要同步到简历记录及用户画像 resume block。

#### Scenario: 保存诊断建议
- **WHEN** 用户对自己拥有的简历完成诊断
- **THEN** 系统保存诊断分数、优缺点、普通建议和结构化诊断建议

#### Scenario: 同步画像摘要
- **WHEN** 诊断结果包含高优先级诊断建议
- **THEN** 系统将最新诊断分数和关键建议摘要用于刷新用户画像中的简历状态

### Requirement: 强化诊断上下文组装
简历诊断 SHALL 在可用时组装目标岗位、目标岗位要求、用户画像摘要、测评结果、简历关键词和文件文本提取结果作为上下文。上下文缺失 SHALL 降级为明确提示或 fallback 规则，不得伪造不存在的画像、文件或 AI 能力。

#### Scenario: 画像和岗位要求同时可用
- **WHEN** 用户画像存在目标岗位且请求包含目标岗位要求
- **THEN** 诊断上下文同时包含岗位、岗位要求和画像摘要，并在结果中保留上下文来源

#### Scenario: AI provider 不可用
- **WHEN** 真实 AI provider 未启用或返回 unavailable
- **THEN** 系统使用确定性 fallback analyzer 生成基础诊断和诊断建议，并返回明确 fallback 状态
