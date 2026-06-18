## ADDED Requirements

### Requirement: 表达简历诊断建议
系统 SHALL 使用 JDK 1.8 兼容 DTO 表达简历诊断建议。每条建议 SHALL 至少包含建议 ID、问题类型、优先级、涉及简历区域、问题说明、建议动作、示例改写方向、证据来源、目标关键词、状态和更新时间，并 SHALL NOT 依赖 Spring、JPA、Lombok、Jackson 注解或 Java 17 record。

#### Scenario: 返回结构化建议
- **WHEN** 简历诊断发现项目经历缺少量化结果
- **THEN** 系统返回一条包含问题类型、优先级、简历区域、建议动作和示例改写方向的结构化建议

#### Scenario: 建议字段可为空但结构稳定
- **WHEN** AI 或 fallback 规则无法识别证据来源或目标关键词
- **THEN** 系统仍返回稳定 DTO，并把缺失字段表示为空字符串或空列表，而不是省略顶层建议结构

### Requirement: 基于已有简历生成修改闭环
系统 SHALL 支持用户基于已有 `resumeId` 发起简历诊断，并把已有简历文本、目标岗位、目标岗位要求、用户画像和关键词状态作为上下文生成建议。按 `resumeId` 发起的简历诊断 MUST 执行用户所有权校验。

#### Scenario: 使用已有简历和目标岗位
- **WHEN** 用户选择自己拥有的简历并填写目标岗位或目标岗位要求
- **THEN** 系统基于该简历内容和岗位上下文返回诊断分数、优缺点和结构化诊断建议

#### Scenario: 拒绝跨用户简历诊断
- **WHEN** 用户尝试对不属于自己的 `resumeId` 发起简历诊断
- **THEN** 系统拒绝请求，并且不生成、不保存、不回写该简历的诊断建议

#### Scenario: 缺少可诊断文本
- **WHEN** 用户选择的简历没有可用 `parsedContent`，且请求中没有提供 `resumeText`
- **THEN** 系统返回明确的缺少简历文本状态，并提示用户上传、粘贴或提取文本

### Requirement: 联动用户画像和目标岗位
简历诊断能力 SHALL 消费用户画像中的目标岗位、测评摘要、偏好和最新 resume block 作为上下文，用于补齐目标岗位、提示词和 fallback 规则；系统 MUST NOT 用 onboarding 自报“已有简历”替代真实简历记录。

#### Scenario: 使用画像目标岗位作为默认值
- **WHEN** 用户未在简历诊断页面填写目标岗位但画像中存在目标岗位
- **THEN** 系统默认使用画像目标岗位作为诊断上下文，并在结果中标记该上下文来源

#### Scenario: 自报简历不替代真实记录
- **WHEN** 用户只在 onboarding 自报已有简历但 `/cc001/resume/list` 没有真实简历记录
- **THEN** 简历诊断页面 SHALL 提示用户先创建或上传简历，而不是伪造可修改的简历

### Requirement: 形成可执行优化计划
系统 SHALL 将诊断建议聚合为可执行优化计划。计划 SHALL 包含总体优先级、建议总数、高优先级建议数、建议项列表、下一步动作和再次诊断入口；页面 SHALL 允许用户查看建议并标记建议项的本次处理状态。

#### Scenario: 生成优化计划摘要
- **WHEN** 诊断结果包含多条结构化建议
- **THEN** 系统返回包含总数、高优先级数量和下一步动作的优化计划摘要

#### Scenario: 建议状态可在页面确认
- **WHEN** 用户在页面上将建议项标记为已处理或暂不处理
- **THEN** 页面更新本次会话中的建议状态，并保留再次诊断入口

#### Scenario: 再次诊断验证优化
- **WHEN** 用户根据建议修改并重新提交简历文本或选择更新后的简历
- **THEN** 系统 SHALL 支持再次调用诊断入口，并展示新的分数和建议差异提示

### Requirement: 不保存敏感文件凭据
简历诊断能力 SHALL 只保存稳定 `fileKey`、简历 ID、诊断 payload 和建议 DTO。系统 SHALL NOT 将 access token、Authorization header、预签名 URL 签名参数、endpoint secret 或客户私有凭据写入简历记录、诊断记录、route metadata 或 OpenSpec 文档。

#### Scenario: 使用文件预览 URL
- **WHEN** 页面为简历文件请求短期预览 URL
- **THEN** 页面只即时打开或展示该 URL，不把预签名 URL 写回简历或诊断 payload

#### Scenario: 文档审查
- **WHEN** 审查 OpenSpec artifacts、route metadata 或迁移文档
- **THEN** 其中只包含字段名、API 路径、fallback 和验证方式，不包含真实 token、签名 URL 或 endpoint secret
