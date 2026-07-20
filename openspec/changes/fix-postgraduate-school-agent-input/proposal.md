## Why

考研、保研和留学陪伴的 13 个表单会把用户已填写的资料发送给统一智能体，但智能体仍可能以笼统的 `NEED_MORE_INFO` 拒绝请求。页面没有明确说明缺少什么，也没有将每个表单可填写的最小输入与智能体输出契约固定下来，导致用户完整填写后仍无法获得结果。

## What Changes

- 为考研 4 项、保研 4 项和留学 5 项陪伴能力定义“可生成的最小输入”和“仅在无法完成核心任务时才补充信息”的统一语义。
- 补齐与请求 DTO 已有字段不一致的表单输入，并在前端提交前对真正必要的字段给出中文提示。
- 让后端保留并向页面传递智能体明确的补充项；当输入已达到该任务最低要求却返回 `NEED_MORE_INFO` 时，拒绝笼统结果并提供可诊断的契约错误信息。
- 提供可直接发布到金蝶平台的统一升学陪伴任务流系统提示词，覆盖 13 个 `taskType`、输入规则和 JSON 返回结构。

## Capabilities

### New Capabilities

- `further-study-companion-agent-prompt-contract`: 定义统一升学陪伴智能体的 13 项任务输入门槛、补充信息语义与结构化返回契约。

### Modified Capabilities

- `postgraduate-exam-companion`: 调整考研择校、复习计划、错题解析和复试准备的最小输入与失败提示行为。
- `postgraduate-recommendation-companion`: 调整保研排名监控、背景提升、材料精修和导师联系的最小输入与失败提示行为。
- `study-abroad-companion`: 调整留学画像、语言计划、选校定位、文书主线和签证网申的最小输入与失败提示行为。

## Impact

- `webapp/isv/v620/cyancruise/assets/app-runtime.js` 的 13 个表单、草稿恢复与提示文案。
- 升学陪伴请求 DTO、`AgentPlatformFurtherStudyCompanionAnalyzer`、对应 ApplicationService 和测试。
- 新增面向平台配置人员的系统提示词文档；不新增第三方依赖，不改变 Cosmic WebAPI 路径。
