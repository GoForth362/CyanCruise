## Context

CyanCruise 的 CareerLoop 页面已经完成苍穹门户挂载、平台身份识别、KAPI route、简历上传/预览/删除和工作台摘要刷新。当前页面仍保留大量迁移验收阶段的信息，例如接口契约面板、Route 标识、available/entry-only/user chip、文件上传预览独立入口。这些信息对开发调试有价值，但会让普通用户误以为系统是接口清单或半成品页面。

本 change 的目标是把现有静态 webapp 体验收口为用户可自然操作的求职主循环，同时保留调试能力，避免影响后续排查。

## Goals / Non-Goals

**Goals:**

- 默认用户模式下只展示真实可用或可继续操作的主循环入口。
- 将调试信息统一收敛到 `?debug=1` 或等价开关，不在默认页面展示。
- 简历页保留上传、创建、预览、删除、去诊断闭环，并减少 fileKey、接口路径等工程字段的视觉噪音。
- 保持 `careerloop-routes.json`、`validate-routes.js`、OpenSpec 和现有 `/cc001/*` API 契约可审计。
- 仅修改静态 webapp 和 route metadata，除非验证发现必须补后端映射。

**Non-Goals:**

- 不新增 AI 诊断真实大模型能力。
- 不引入新的前端构建链、Vue/uni-app、PDF 渲染库或 Java 17 依赖。
- 不删除 `/cc001/files/*` 底层文件服务契约。
- 不在本 change 中重做整体视觉品牌或移动端全量设计。

## Decisions

### 1. 默认隐藏调试信息，而不是删除

默认页面隐藏接口契约、route/status chip、entry-only 标签、调试入口和文件上传预览独立导航。通过 `?debug=1` 打开时仍可显示这些面板，便于继续排查苍穹 KAPI、BOS 文件服务和 route map。

替代方案是直接删除调试信息。该方案会让用户界面更干净，但会削弱当前本地部署和苍穹集成排错能力，因此不采用。

### 2. route metadata 继续作为单一审计来源

导航是否默认展示、是否调试可见、是否 entry-only，继续以 `careerloop-routes.json` 为基础。`app.js` 只消费 metadata，不在多处硬编码同一套可见性规则。

替代方案是在 `app.js` 中直接写 route 白名单。该方案改动小，但后续菜单发布和 route 校验容易分叉，因此不采用。

### 3. 简历页按业务流程布局

简历页默认优先展示简历记录和创建表单；文件上传入口保留在简历页内作为辅助能力；接口契约和原始 fileKey 细节仅在必要位置或 debug 模式显示。预览失败时展示业务化错误文案，避免暴露本机文件服务 URL、签名 URL 或平台内部异常。

### 4. 文件上传预览作为调试页保留

`file-upload-preview` route 不作为普通用户主导航入口，但 hash 直达或 debug 模式仍可打开。这样保留底层文件能力验证，同时避免把用户导向非业务流程页面。

## Risks / Trade-offs

- **调试入口隐藏后排查路径不明显** -> 在 README 或迁移文档记录 `?debug=1`，并保留 route hash 直达。
- **route metadata 调整可能影响校验脚本** -> 同步更新 `careerloop-routes.json` 与 `validate-routes.js`，运行 route 校验。
- **用户模式与 debug 模式分支造成遗漏** -> 重点验证 `#workbench`、`#resume`、`#resume-diagnosis` 以及 `?debug=1#file-upload-preview`。
- **简历页隐藏 fileKey 后排错信息减少** -> debug 模式下显示完整 fileKey 和接口契约；默认模式保留必要的“已关联文件”状态。

## Migration Plan

1. 更新 route metadata，区分默认导航可见和 debug 可见。
2. 更新 `app.js`：新增 debug mode 判断，默认隐藏开发信息和调试页。
3. 调整简历页渲染顺序、文案和 fileKey 展示策略。
4. 更新样式，确保隐藏调试信息后布局不塌陷、不重叠。
5. 运行静态校验、OpenSpec 校验，并同步静态文件到苍穹本地运行目录。

回滚方式：回退本 change 的静态资源提交，或在 URL 添加 `?debug=1` 临时恢复调试信息用于排查。

## Open Questions

- 是否需要在默认用户模式完全隐藏“职业资源”，还是保留为后续内容入口？当前建议默认隐藏，等真实内容页接入后再恢复。
- 是否需要把 debug 模式入口放入帮助中心？当前建议先只用 URL 参数，避免普通用户误触。
