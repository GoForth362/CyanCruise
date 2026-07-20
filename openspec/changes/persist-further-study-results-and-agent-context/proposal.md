## Why

当前 13 项升学分析只保存用户提交的草稿，成功结果没有形成可追溯记录；深造记录的 PostgreSQL 适配器仍退化为进程内存，智能体也无法获得用户已保存的同方向资料和历史结论。

## What Changes

- 将考研、保研、留学的 13 项成功分析保存为当前用户、当前方向的独立记录。
- 将深造目标、记录、材料和事件实现为 PostgreSQL 持久化，替换占位内存适配器。
- 在调用智能体前聚合当前用户、当前方向的目标、最近记录和材料摘要，作为受限历史上下文传入。
- 分析失败时不保存伪造结果；上下文加载失败时仍保留本次分析能力并提示可理解错误。

## Capabilities

### New Capabilities
- `further-study-agent-context`: 为升学智能分析按用户与方向提供受限的历史上下文。

### Modified Capabilities
- `further-study-companion-storage`: 深造目标、记录、材料、事件必须由 PostgreSQL 持久化。
- `postgraduate-exam-companion`: 四项考研分析必须保存成功结果并使用同方向上下文。
- `postgraduate-recommendation-companion`: 四项保研分析必须保存成功结果并使用同方向上下文。
- `study-abroad-companion`: 五项留学分析必须保存成功结果并使用同方向上下文。

## Impact

影响升学 ApplicationService、存储工厂、PostgreSQL 表、智能体调用入参及相关测试；保持 JDK 8 和现有 Cosmic WebAPI 契约兼容。
