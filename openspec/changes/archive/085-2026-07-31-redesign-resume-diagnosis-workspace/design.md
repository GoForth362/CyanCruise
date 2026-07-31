## Context

`resume-diagnosis` 页面由 `renderResumeDiagnosisPage` 组合诊断状态、`diagnosisFormPanel`、`diagnosisResultPanel` 和 `diagnosisHistoryPanel`。简历切换、下拉选择、PDF 预览、诊断提交、再次诊断和历史操作由 `bindResumeDiagnosisEvents` 绑定。现有功能完整，但输入与输出组件均以通用面板连续排列，缺少当前诊断对象、材料准备度和结果优先级的视觉表达。

## Goals / Non-Goals

**Goals:**

- 用真实选择状态展示当前简历、目标岗位、PDF/正文和历史记录准备度。
- 将输入区整理为诊断对象、岗位要求、简历正文和提交操作的清晰步骤。
- 让总分、分项评分、优势、问题和修改建议形成易扫描的结果层级。
- 保留全部 DOM 标识、数据属性、加载状态、错误恢复和历史操作。
- 消除窄屏横向滚动，支持长岗位要求、长正文、长建议和键盘访问。

**Non-Goals:**

- 不修改智能体提示词、评分算法、重试、缓存或保存逻辑。
- 不新增简历在线编辑、建议自动回写或虚假的“已处理”状态。
- 不修改简历诊断服务、DTO、数据库或文件服务。
- 不改变诊断历史只展示真实智能体结果的规则。

## Decisions

### 使用独立诊断页面模式

仅在 `resume-diagnosis` 路由增加 `resume-diagnosis-workspace-mode`，新样式限定在该模式下，避免影响简历制作页及其他使用评分、状态卡和表单的页面。

### 总览只读取真实选择与记录状态

从已选简历、目标岗位、`fileKey`、`parsedContent`、草稿正文和当前简历历史记录计算准备状态。总览不生成诊断分数，也不把存在 PDF 描述成正文已读取。

### 保留单表单与选择器契约

继续使用 `resumeDiagnosisForm`、`diagnosisResumeId`、`diagnosisResumeTrigger`、`diagnosisJobDescription`、`diagnosisResumeText` 和现有提交函数。通过增加步骤包装、状态标签和说明实现重构，避免改变简历切换后草稿清理、PDF 提取与禁用条件。

### 结果区域增加专属包装而不改变数据

`diagnosisResultPanel`、`scoreBreakdownPanel`、`diagnosisTextLists`、`revisionSuggestionList` 和历史记录继续读取原结果字段，仅增加专属类和标题层级。总分、建议数和参考信息仍来自真实智能体返回值。

### 窄屏使用单列

桌面端可使用上下文摘要与输入内容的分区布局，窄屏统一单列；长文本允许换行，按钮和状态标签可折行，诊断选择菜单不超出容器。

### 简历功能页不生成页头返回操作

`resume` 与 `resume-diagnosis` 均由顶部功能导航直接进入，不设置通用页头父级返回目标。`backRouteFor` 对这两个路由固定返回空值，导航配置中的父级值同步设为空，避免通用 `pageShell` 再次生成“返回”按钮。页面内部用于岗位缺失恢复的“返回简历页”等业务操作不受影响。

## Risks / Trade-offs

- [页面状态重绘导致输入丢失] → 保留原 `diagnosisDraft` 读取和提交链路，不改变重绘时机。
- [结果样式影响其他诊断组件] → 所有增强规则限定在 `resume-diagnosis-workspace-mode`。
- [准备度被误认为诊断结果] → 总览只显示材料状态，不生成或展示推测分数。
- [长建议撑开页面] → 网格子项设置 `min-width: 0` 并允许任意位置换行。

## Migration Plan

1. 增加页面状态类和真实诊断准备总览。
2. 重构输入、空状态和局部消息结构。
3. 增加结果、评分、建议和历史记录专属包装及样式。
4. 更新静态契约与缓存版本，完成视觉和构建验证。
5. 清除简历制作与简历诊断页头的父级返回目标并验证两页不再渲染返回按钮。

回滚仅涉及前端结构、样式和资源版本，不涉及数据迁移。

## Open Questions

无。
