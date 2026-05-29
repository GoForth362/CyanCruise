# CareerLoop 生产就绪清单

本文档承接 OpenSpec change `migrate-production-readiness-checklist`，用于在 CareerLoop 从 IPD 迁移到 CyanCruise/Cosmic 后进入客户租户或生产发布前统一确认发布门禁、配置治理、监控、备份恢复和回滚证据。本文档只迁移生产语义和检查契约，不迁移 IPD 的 Spring Boot、Docker、Flyway、JPA、Java 17、nginx、Uptime Kuma、ServerChan 或 bash 脚本实现。

## 状态标记

| 标记 | 含义 |
| --- | --- |
| `PASS` | 已完成并保留证据。 |
| `BLOCKED` | 发布阻断项，必须修复或明确豁免。 |
| `MANUAL_REQUIRED` | 需要在真实 Cosmic 租户或客户环境手工确认。 |
| `DEFERRED` | 明确延期，不作为本次发布阻断，但必须在迁移地图保留。 |
| `N/A` | 不适用于当前发布范围。 |

## IPD 来源证据

| IPD 来源 | 迁移到 CyanCruise 的生产语义 |
| --- | --- |
| `F:\Project\IPD\AI_PRODUCT_HANDOFF.md` | 生产交付项、未完成项、验收边界和演示风险转为发布清单和迁移地图证据。 |
| `F:\Project\IPD\backend\src\main\resources\application-prod.yml` | 密钥必填、数据源/Redis/OSS/AI/微信/JWT/告警配置、健康暴露策略转为属性名检查和租户配置检查；不记录真实值。 |
| `F:\Project\IPD\backend\docker-compose.yml`、`Dockerfile` | 服务依赖、健康检查、日志滚动、非 root 运行、探针语义转为 Cosmic 发布检查；不迁移容器编排。 |
| `backend\scripts\deploy-backend.sh`、`rollback.sh` | 发布后健康等待、失败回滚、镜像回退语义转为 Cosmic 包/菜单/adapter 回滚步骤；不迁移服务器地址和脚本。 |
| `backend\scripts\backup-mysql.sh`、`restore-from-backup.sh` | 备份、恢复演练、二次确认、恢复后抽样校验转为租户手工检查。 |
| `HealthController.java`、`WebConfig.java` | 健康探针无需业务身份、只暴露最小状态；在 Cosmic 中以平台健康页或代表性 WebAPI 探针实现。 |
| `AlertProperties.java`、`ServerChanAppender.java` | 告警开关、错误限流和通知渠道语义转为监控接入检查；不迁移 ServerChan 实现。 |

## 自动发布门禁

以下检查任一失败均为 `BLOCKED`。

| 检查 | 命令/证据 | 期望 |
| --- | --- | --- |
| 当前 change 严格校验 | `openspec validate migrate-production-readiness-checklist --strict` | change artifacts 与 delta specs 合法。 |
| 全量 OpenSpec 严格校验 | `openspec validate --all --strict` | 所有已归档和活跃规格一致。 |
| JDK 8 构建 | `$env:JAVA_HOME='F:\kingdee\ENV\jdk'; $env:Path="$env:JAVA_HOME\bin;$env:Path"; .\gradlew.bat clean build` | 使用仓库内 wrapper 和 JDK 8 完整构建通过。 |
| webapp route map | `node webapp\isv\v620\careerloop\validate-routes.js` | 路由、API、挂载、待办能力和元数据结构合法。 |
| webapp JS 语法 | `node --check webapp\isv\v620\careerloop\assets\app.js` | 静态页面脚本无语法错误。 |
| 生产 adapter 聚焦测试 | identity/file/AI provider 已迁移 change 中记录的 Gradle 聚焦测试 | 关键 adapter 的 disabled fallback、诊断字段和租户边界不回退为开发假数据。 |

## 本地检查

| 检查 | 状态 |
| --- | --- |
| 构建前确认 `JAVA_HOME` 指向 `F:\kingdee\ENV\jdk`，业务代码不硬编码本地路径。 | `MANUAL_REQUIRED` |
| 新增或修改的实现不引入 Spring Boot、JPA、Flyway、Vue、uni-app、Java 17、Docker runtime 依赖。 | `MANUAL_REQUIRED` |
| `careerloop-routes.json` 只记录属性名、命令和 fallback，不记录 apiKey、Authorization、endpoint secret、客户私有值、服务器地址或生产凭据。 | `MANUAL_REQUIRED` |
| README/KDDT 菜单说明、迁移地图和 OpenSpec archive 指向同一发布范围。 | `MANUAL_REQUIRED` |
| 所有 `DEFERRED` 能力在路由元数据和迁移地图中可追溯。 | `MANUAL_REQUIRED` |

## 租户手工检查

| 领域 | 需要确认 | 失败 fallback/回滚 |
| --- | --- | --- |
| Cosmic 登录上下文 | 启用 `cc001.identity.adapter.enabled=true` 与 `cc001.identity.login.provider.enabled=true`；按租户桥接字段确认 userId/adminId/orgId/roles；普通用户、管理员、身份冲突、禁用 provider 四类场景均有证据。 | 禁用 adapter 后页面保持 `identity-required` 或 `forbidden`，不使用开发身份替代。 |
| 文件服务 adapter | 启用 `cc001.file.adapter.enabled=true` 并绑定平台文件 provider；验证上传、预览 URL、下载、删除、文本抽取和日志脱敏；业务记录只保存 object key。 | 禁用 adapter 后返回 unavailable/skipped，不把内存或本地文件实现当生产替代。 |
| AI provider adapter | 启用 `cc001.ai.provider.enabled=true`；确认 `endpoint`、`apiKey`、`model`、`timeoutSeconds`、`retryOn5xx`、`diagnostics.enabled` 等属性名配置完整；验证 chat、场景 fallback、tool userId 注入、401、超时、5xx 重试和诊断脱敏。 | 禁用、缺配置、失败或超时均返回 AI unavailable 或业务规则 fallback，不伪造成功 AI 内容。 |
| KDDT 菜单与 webapp 挂载 | 用户菜单、入口页、管理员菜单和角色可见性符合 `careerloop-routes.json`。 | 下架菜单或取消 route publish，不影响后端数据。 |
| Cosmic datamodel/storage | 画像、测评、简历、今日任务、面试、通知、管理等记录具备用户归属、组织隔离、排序和状态字段；客户确认备份范围覆盖业务对象。 | 禁止以开发文件/内存 adapter 承担生产数据。 |
| 通知和订阅 | 站内通知是 canonical；微信订阅发送保持 `DEFERRED` 或租户平台化接入后再启用。 | 微信发送失败不得阻塞主流程。 |
| 管理治理 | `whoami`、管理员角色别名、组织范围、审计日志和敏感字段脱敏可验证。 | 管理入口隐藏或返回 forbidden，不使用硬编码管理员。 |
| 健康与监控 | 平台健康页或代表性 `/cc001/*` WebAPI 可用于探针；错误日志、AI/file/identity adapter 诊断进入客户认可的监控渠道。 | 健康失败阻断发布；监控未接入时标记 `MANUAL_REQUIRED` 并需发布负责人确认。 |
| 备份/恢复 | 客户确认数据库、附件对象、配置和审计日志备份策略；至少完成一次恢复演练或取得正式豁免。 | 恢复演练失败为 `BLOCKED`。 |
| 回滚 | 保留上一个 Cosmic 包、菜单发布记录和 adapter enablement 配置；可通过禁用 AI/file/identity adapter、下架菜单或回退包撤销本次发布。 | 回滚路径不依赖 IPD Docker 镜像或 bash 脚本。 |

## 延期能力

| 能力 | 状态 | 发布说明 |
| --- | --- | --- |
| 真实微信订阅/模板消息发送 | `DEFERRED` | 本次以站内通知为准，微信发送需平台 token、模板和租户小程序运行时。 |
| 外部就业内容抓取和全文详情 | `DEFERRED` | 当前只迁移只读洞察和资源卡片契约。 |
| 语音、ASR/TTS、数字人面试 | `DEFERRED` | 不作为主循环生产发布门禁。 |
| 完整 Vue/Element 管理后台页面 | `DEFERRED` | 当前迁移管理治理 WebAPI 契约和 webapp 入口语义。 |
| 客户最终 datamodel、权限树和人员主数据 adapter | `MANUAL_REQUIRED` | 需要按租户真实模型配置和验收。 |
| 生产监控/告警渠道自动集成 | `MANUAL_REQUIRED` | 由客户 Cosmic 运维体系或项目发布方案确认。 |

## 发布阻断项

发布前若出现以下任一情况，必须标记 `BLOCKED`：

- 自动发布门禁任一命令失败。
- route metadata、日志、文档或测试数据中出现 apiKey、Authorization header、endpoint secret、客户私有值、服务器地址或生产凭据。
- 生产身份、文件或 AI provider 启用失败后回退为开发假数据或硬编码用户。
- 业务数据没有明确租户/用户归属、备份范围或回滚路径。
- 关键 `DEFERRED` 能力没有在迁移地图和发布说明中披露。

## 证据记录模板

| 项目 | 结果 |
| --- | --- |
| 发布版本/commit |  |
| Cosmic 租户/环境 |  |
| OpenSpec 校验结果 |  |
| JDK 8 构建结果 |  |
| route/js 校验结果 |  |
| identity/file/AI adapter 验收人 |  |
| KDDT 菜单验收人 |  |
| 备份恢复验收人 |  |
| 监控告警验收人 |  |
| 延期能力确认人 |  |
| 回滚负责人和回滚窗口 |  |

## 回滚速查

| 范围 | 回滚动作 |
| --- | --- |
| webapp 菜单/页面 | 取消 KDDT 菜单发布或回退到上一个静态 webapp 包。 |
| identity provider | 关闭 `cc001.identity.adapter.enabled` 或租户登录 provider enablement，确认页面进入 identity-required/forbidden。 |
| file provider | 关闭 `cc001.file.adapter.enabled`，确认文件动作返回 unavailable/skipped，保留已有 object key。 |
| AI provider | 关闭 `cc001.ai.provider.enabled`，确认业务返回 AI unavailable 或规则 fallback。 |
| datamodel/storage | 按客户备份/恢复方案回退数据；恢复前需要二次确认和抽样校验计划。 |
