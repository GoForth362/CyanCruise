## 1. OpenSpec 审阅与迁移边界

- [x] 1.1 复核 `AGENTS.md`、proposal、design 和 delta specs，确认中文文档、JDK 1.8、`gradlew.bat`、模块边界和三位序号归档规则均已覆盖。
- [x] 1.2 复核 IPD 来源文件，记录只迁移简历诊断业务语义、数据语义、流程和接口契约，不迁移 Spring Boot、JPA、Flyway、Vue、uni-app、PDFBox 或 OSS SDK 运行时。
- [x] 1.3 运行 `openspec validate enhance-resume-revision --strict`，并在通过后再开始代码实现。

## 2. DTO 与 helper

- [x] 2.1 在 `code/base/v620-cc001-base-common/` 增加或扩展 JDK 1.8 兼容 DTO，用于结构化简历诊断建议、优化计划摘要和上下文来源。
- [x] 2.2 扩展 `ResumeDiagnosisResultDto` 或关联 DTO，兼容旧 `suggestions` 字段并新增 revision suggestions/plan 字段。
- [x] 2.3 在 `code/base/v620-cc001-base-helper/` 扩展 `ResumeDiagnosisService`，支持 AI JSON、普通文本和 fallback 规则到结构化建议的容错解析。
- [x] 2.4 增加 helper focused tests，覆盖结构化 JSON、普通文本 fallback、旧诊断结果无新字段、优先级统计和JDK 1.8 DTO 默认值。

## 3. 应用服务、AI 边界与 WebAPI

- [x] 3.1 扩展 `ResumeDiagnosisApplicationService` 的上下文组装，复用已有简历记录、目标岗位/岗位要求、画像快照、关键词状态和文件文本边界。
- [x] 3.2 扩展 `AiGatewayResumeDiagnosisAnalyzer` 和 `DefaultResumeDiagnosisAnalyzer`，要求输出或 fallback 生成结构化诊断建议，并保留旧字段兼容。
- [x] 3.3 确认按 `resumeId` 发起的诊断、建议保存和回写继续执行用户所有权校验。
- [x] 3.4 将最新诊断分数和关键诊断建议摘要同步到简历记录及用户画像 resume block，不能用 onboarding 自报状态替代真实简历。
- [x] 3.5 复用现有 PostgreSQL 简历诊断 payload 保存最新建议；如发现必须新增存储字段，先更新 OpenSpec 设计和规格再实现。
- [x] 3.6 增加应用服务/WebAPI focused tests，覆盖已有简历诊断、跨用户拒绝、AI unavailable fallback、旧 payload 兼容和画像同步。

## 4. Webapp 简历诊断页面

- [x] 4.1 增强 `webapp/isv/v620/cyancruise/assets/app.js` 的 `resume-diagnosis` route，读取简历列表和画像默认目标岗位。
- [x] 4.2 增加简历选择、目标岗位/岗位要求 输入、触发诊断、加载/错误/空简历状态和再次诊断入口。
- [x] 4.3 渲染诊断分数、强项、弱项、普通建议、结构化诊断建议、优先级、证据、目标关键词和示例改写方向。
- [x] 4.4 允许用户在页面本次会话中标记建议项已处理或暂不处理，并保留重新诊断入口。
- [x] 4.5 复用文件预览/文本提取入口作为局部增强；adapter unavailable 时不阻断已有简历诊断和建议查看。
- [x] 4.6 如修改 `index.html`、`assets/app.js` 或 `assets/styles.css`，同步更新静态资源版本号并记录是否需要重新部署。
- [x] 4.7 如 route/API metadata 发生变化，更新 `cyancruise-routes.json` 和 `validate-routes.js`。

## 5. 验证与交付

- [x] 5.1 运行 `openspec validate enhance-resume-revision --strict`。
- [x] 5.2 运行相关 focused Java tests。
- [x] 5.3 运行 `node --check webapp\isv\v620\cyancruise\assets\app.js`。
- [x] 5.4 运行 `node webapp\isv\v620\cyancruise\validate-routes.js`。
- [x] 5.5 必要时在 Windows PowerShell 中设置 JDK 8 并运行 `.\gradlew.bat clean build`。
- [x] 5.6 更新 `docs/ipd-to-cyancruise-migration-map.md` 或相关运行文档，记录 IPD 来源路径、CyanCruise 目标模块、数据映射、暂不迁移项、验证结果和静态资源部署说明。
