## Context

CyanCruise 已完成 CareerLoop 主循环的多项迁移：profile/onboarding、测评、简历、今日行动、职业计划、面试、简历诊断、助手聊天、datamodel adapter、webapp、就业洞察、通知、管理治理、Cosmic 平台挂载、身份 adapter、文件 adapter 和 AI provider 生产 adapter。现有文档能说明单个 adapter 如何启用，但缺少一份发布前统一检查清单。

IPD 的生产语义分散在 `application-prod.yml`、`docker-compose.yml`、Dockerfile、部署脚本、回滚脚本、备份恢复脚本、health controller、告警配置和 `AI_PRODUCT_HANDOFF.md` 的发布记录中。CyanCruise 是 Kingdee Cosmic 二开工程，不能直接迁移 Spring Boot/Flyway/Docker/Vue/Java 17 运维实现，只迁移生产发布规则、数据语义、验证流程和接口契约。

## Goals / Non-Goals

**Goals:**

- 输出一份 CyanCruise/Cosmic 可执行的 CareerLoop 生产就绪检查清单。
- 覆盖自动化验证、本地构建、OpenSpec、route map、身份、文件、AI、datamodel、webapp/KDDT、管理权限、监控、日志诊断、备份恢复、回滚和未完成项登记。
- 为每项检查标注来源、目标能力、执行方式、通过标准、证据记录和回滚/降级方式。
- 明确密钥不入库、诊断脱敏、开发 fallback 不可作为生产身份、不可用 adapter 不得伪装成功。
- 修改迁移治理规格，使后续实现型迁移在 archive/发布前保留生产就绪证据。

**Non-Goals:**

- 不引入新的部署工具、CI 平台、Docker Compose、nginx、Uptime Kuma、ServerChan 或 OSS SDK。
- 不直接迁移 IPD Spring Boot actuator、JPA/Flyway 配置、bash 脚本、服务器 IP、生产域名或生产密钥。
- 不替代客户 Cosmic 租户的正式发布流程、权限审批、KDDT 菜单配置和主数据治理。
- 不声明所有 pending 能力已经生产可用；检查清单必须能标记 `BLOCKED`、`MANUAL_REQUIRED` 或 `DEFERRED`。

## Decisions

1. **以文档化检查清单为主要产物**

   生产就绪是跨模块治理能力，现阶段不应做成运行时业务代码。`docs/careerloop-production-readiness-checklist.md` 将作为发布门禁索引，关联现有 OpenSpec、迁移地图、route map 和验证命令。

   备选方案是新增 Java readiness service 或 WebAPI；暂不采用，因为客户租户验证依赖平台环境，文档清单更适合当前迁移阶段。

2. **检查项分为 automated、local、tenant-manual、deferred**

   自动化项包括 OpenSpec、Gradle、route validator、JS syntax、focused tests；本地项包括 JDK 8 和配置文件不硬编码；租户手工项包括 Cosmic 登录上下文、文件服务、AI provider、KDDT 菜单和管理员权限；延后项包括真实微信发送、外部抓取、语音/数字人和最终客户平台能力。

   备选方案是把所有项都视为发布阻塞；不采用，因为部分能力已明确为后续平台适配，必须能区分“核心发布阻塞”和“已登记延后”。

3. **route metadata 只记录状态和属性名，不记录秘密**

   `careerloop-routes.json` 可增加 readiness metadata，但只能包含检查项 key、执行命令、手工验证说明、fallback 和 pending 能力。endpoint secret、apiKey、Authorization、租户私有字段值不得写入。

   备选方案是把租户配置样例完整写入 route map；不采用，因为 route map 属于仓库资产，不能承载真实密钥或客户私有信息。

4. **沿用 adapter enabled/disabled 回滚语义**

   身份、文件、AI 等生产 adapter 的回滚方式应优先是禁用对应 `cc001.*.enabled` 配置或撤销平台挂载，而不是删除代码或回退数据库。数据层回滚需要单独走客户环境备份/恢复审批。

   备选方案是迁移 IPD 的 Docker image rollback 脚本；不采用，因为 CyanCruise 的部署形态由 Cosmic/KDDT/客户环境决定。

5. **迁移治理增加生产证据要求**

   后续实现型 change 完成时，除了 `openspec validate` 和 Gradle build，还应更新迁移地图或生产清单，记录哪些检查已自动通过、哪些需要租户手工验证、哪些明确延后。

## Risks / Trade-offs

- [Risk] 文档清单可能过期 → Mitigation: 检查项引用 route map、迁移地图和 OpenSpec spec 名称，后续 change 更新对应能力时必须同步清单。
- [Risk] 租户手工项无法在本地验证 → Mitigation: 明确标记 `MANUAL_REQUIRED`，并写出输入、期望状态和失败回滚方式。
- [Risk] 误把 pending 能力当作可发布 → Mitigation: 每项包含 `releaseGate` 与 `fallback`，pending 能力只能登记为 `DEFERRED`。
- [Risk] 密钥或客户字段泄漏 → Mitigation: 清单和 route metadata 只记录属性名、状态和脱敏规则，不记录实际值。
- [Risk] 文档不能阻止违规发布 → Mitigation: 将生产就绪证据纳入 `migration-governance` 规格，作为 review/archive 检查项。

## Migration Plan

1. 根据 IPD 生产配置、部署脚本、回滚脚本、备份恢复脚本、health/alert 代码和 handoff 记录整理来源证据。
2. 新增 `production-readiness-checklist` 规格，定义检查项分类、证据、门禁、密钥、回滚和 pending 能力规则。
3. 更新 `migration-governance` 规格，加入生产就绪证据要求。
4. 实现阶段新增 `docs/careerloop-production-readiness-checklist.md`，并在 route map 中增加 secret-free readiness metadata。
5. 回填迁移地图，运行 route validator、OpenSpec strict 校验和 JDK 8 Gradle build。

## Open Questions

- 客户 Cosmic 租户最终是否有统一的发布审批模板、审计记录系统或 KDDT 导出格式，需要在真实租户接入时确认。
- 生产监控是接入苍穹平台能力、客户自有监控，还是继续使用外部 uptime 服务，需要后续平台运维决策。
