## Why

考研、保研和留学的 13 个页面分析入口目前只会明确提示“真实智能服务尚未接通”，无法使用已经在金蝶 Agent 平台发布并验证通过的“升学陪伴智能体”。需要由 CyanCruise 服务端统一调用该智能体，把页面真实表单转换为约定的结构化请求，并将经过校验的 AI 结果返回现有 WebAPI。

## What Changes

- 新增统一的升学陪伴智能体适配器，通过运行时配置的 `agentNumber` 调用金蝶 Agent SDK。
- 将考研 4 项、保研 4 项、留学 5 项业务操作映射为 13 个固定 `taskType`，统一传递 `mode`、`currentDate`、`payload`、`profileContext` 和 `userMaterials`。
- 严格解析智能体返回的外层状态与 `result`，仅在任务类型一致、状态为 `OK` 且结果可映射到现有 DTO 时返回页面。
- 智能体未配置、调用失败、要求补充资料、任务类型不一致或结构无效时返回普通中文错误，不恢复规则生成或虚假分析。
- 复用现有 WebAPI、请求/结果 DTO 和进一步升学记录持久化边界，不把智能体编号或平台凭据暴露给浏览器。
- 补充运行时配置、调试环境映射、单元测试与部署说明。

## Capabilities

### New Capabilities
- `further-study-companion-agent-adapter`: 定义 13 项升学分析通过一个已发布智能体执行时的请求封装、结果校验、配置和失败语义。

### Modified Capabilities
- `postgraduate-exam-companion`: 考研择校、计划、错题和复试准备改为由真实升学陪伴智能体生成。
- `postgraduate-recommendation-companion`: 保研诊断、计划、文书和导师意向信改为由真实升学陪伴智能体生成。
- `study-abroad-companion`: 留学画像、语言、选校、文书和签证网申改为由真实升学陪伴智能体生成。

## Impact

- 后端：`code/cloud01/v620-cc001-cloud01-app01/` 的 Agent 适配层和三个升学 ApplicationService。
- 契约：继续使用 `code/base/v620-cc001-base-common/` 中现有请求/结果 DTO，不新增第三方依赖和数据库表。
- 配置：新增 `cc001.agent.platform.study.companion.enabled` 与 `cc001.agent.platform.study.companion.agentNumber`；智能体编号由部署环境提供，不硬编码到业务源码或前端。
- 平台：调用已发布的“升学陪伴智能体”；该智能体内部继续负责调用“升学陪伴任务流”。
