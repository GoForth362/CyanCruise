## Why

考研、保研和留学的 13 个智能分析入口虽然会提交本次表单给后端，但除考研页面外没有统一的服务端草稿保存与回显机制。页面重绘、刷新或智能体返回异常时，用户输入会丢失，也无法保证后续分析可以读取同一份真实信息。

## What Changes

- 在全部 13 个升学分析操作调用智能体前，按当前用户和任务类型保存原始请求载荷。
- 为考研、保研、留学全部表单提供失败、重绘和刷新后的服务端草稿恢复；本地缓存仅作为网络异常时的补充保护。
- 统一前端提交过程，确保保存的草稿与发送给智能体的请求完全一致，并避免分析期间清空表单。
- 保持已有结果接口不变，不保存智能体凭据或跨用户数据。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `further-study-companion-storage`: 升学分析草稿按用户和任务类型持久化，并支持重载。
- `postgraduate-exam-companion`: 四个考研分析表单均保存并恢复真实输入。
- `postgraduate-recommendation-companion`: 四个保研分析表单均保存并恢复真实输入。
- `study-abroad-companion`: 五个留学分析表单均保存并恢复真实输入。

## Impact

- 后端：三个升学 ApplicationService、学习中心草稿存储和 WebAPI。
- 前端：`webapp/isv/v620/cyancruise/assets/app-runtime.js` 的 13 个表单提交与渲染。
- 数据库：复用 `cc_study_analysis_draft`，以 `(user_id, task_type)` 唯一定位草稿。
