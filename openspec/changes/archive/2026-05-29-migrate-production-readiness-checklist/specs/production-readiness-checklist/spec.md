## ADDED Requirements

### Requirement: Readiness checklist scope
系统 SHALL 提供 CareerLoop 生产就绪检查清单，覆盖自动化验证、本地构建、Cosmic 租户手工验证、发布阻塞项、延后项和回滚方式；清单 SHALL 记录 IPD 来源、CyanCruise 目标、执行方式、通过标准和证据位置。

#### Scenario: Review checklist entry
- **WHEN** 审阅任一生产就绪检查项
- **THEN** 检查项 SHALL 包含来源、目标能力、验证类型、执行步骤、通过标准、证据字段和失败处理方式

#### Scenario: Distinguish validation type
- **WHEN** 检查项依赖本地命令、构建或静态文件
- **THEN** 清单 SHALL 标记为 automated 或 local，并 SHALL 给出可执行命令

#### Scenario: Distinguish tenant manual validation
- **WHEN** 检查项依赖真实 Cosmic 登录上下文、KDDT 菜单、文件服务、AI provider 或管理员权限
- **THEN** 清单 SHALL 标记为 tenant-manual，并 SHALL 记录不能在本地自动通过的原因

### Requirement: Automated release gate
生产就绪清单 SHALL 至少包含 OpenSpec 严格校验、JDK 8 Gradle 构建、webapp route map 校验、静态 JS 语法检查和 focused adapter tests；这些自动化项失败时 SHALL 阻塞发布。

#### Scenario: Automated checks pass
- **WHEN** 发布候选完成本地验证
- **THEN** 证据 SHALL 记录 `openspec validate --all --strict`、JDK 8 `.\gradlew.bat clean build`、route validator 和相关 focused tests 的结果

#### Scenario: Automated check fails
- **WHEN** 任一自动化检查失败
- **THEN** release gate SHALL 标记为 blocked，并 SHALL NOT 将该候选标记为 production-ready

### Requirement: Secret and configuration governance
生产就绪清单 SHALL 列出身份、文件、AI、通知、datamodel 和 webapp 挂载所需配置项名称，并 MUST NOT 记录真实密钥、Authorization header、租户私有 endpoint secret 或客户私有字段值。

#### Scenario: Document config names only
- **WHEN** 清单说明 `cc001.identity.*`、`cc001.file.*`、`cc001.ai.*` 或其他生产配置
- **THEN** 文档 SHALL 只记录属性名、用途、默认安全行为和验证方式，不得记录真实值

#### Scenario: Diagnostics are enabled
- **WHEN** 租户验证临时启用 diagnostics
- **THEN** 诊断 SHALL 只输出状态、错误分类和脱敏信息，不得包含密钥或完整用户内容

### Requirement: Adapter and fallback validation
生产就绪清单 SHALL 覆盖 Cosmic 身份、文件服务、AI provider、datamodel storage、通知订阅和 webapp 平台挂载 adapter 的启用、禁用、fallback 和回滚验证。

#### Scenario: Adapter disabled fallback
- **WHEN** 任一生产 adapter 未启用或配置不完整
- **THEN** 清单 SHALL 要求验证系统返回 identity-required、unavailable、skipped 或等价安全状态，而不是使用开发 fallback 或伪造成功

#### Scenario: Adapter enabled validation
- **WHEN** 租户启用生产 adapter
- **THEN** 清单 SHALL 要求验证成功路径、错误路径、诊断脱敏和禁用回滚路径

### Requirement: Health monitoring backup and rollback
生产就绪清单 SHALL 迁移 IPD health probe、监控告警、备份恢复和回滚语义为 Cosmic/CyanCruise 发布核查项，而不是直接迁移 Docker、Spring actuator、ServerChan、Uptime Kuma 或 bash 脚本。

#### Scenario: Health probe documented
- **WHEN** 发布候选进入租户验证
- **THEN** 清单 SHALL 要求记录可用的健康检查或代表性 WebAPI 探测方式，并 SHALL 标明本地替代检查命令

#### Scenario: Backup and restore documented
- **WHEN** 发布涉及持久化数据或 datamodel adapter
- **THEN** 清单 SHALL 要求确认客户环境存在备份、恢复演练或回滚审批路径

#### Scenario: Rollback documented
- **WHEN** 检查项失败或发布后出现关键故障
- **THEN** 清单 SHALL 给出禁用 adapter、撤销菜单/KDDT 挂载、恢复上一版本或启用规则 fallback 的回滚路径

### Requirement: Pending capability disclosure
生产就绪清单 SHALL 明确标注尚未生产化的能力，并 SHALL NOT 将真实微信发送、外部内容抓取、语音/数字人、完整管理后台页面、最终客户 datamodel/权限接入或客户私有平台 adapter 表述为已完成。

#### Scenario: Capability is deferred
- **WHEN** 某能力仍依赖后续平台适配或客户租户配置
- **THEN** 清单 SHALL 标记为 `DEFERRED` 或 `MANUAL_REQUIRED`，并 SHALL 说明当前 fallback

#### Scenario: User-facing route remains published
- **WHEN** 某 route 已发布但后端能力仍可能 unavailable
- **THEN** 清单 SHALL 要求页面展示明确 pending/unavailable 状态，避免暗示完整 IPD 能力已生产就绪

### Requirement: Readiness metadata
CareerLoop route metadata SHALL include a secret-free readiness section that references checklist status, automated commands, tenant manual items, pending capabilities and rollback notes.

#### Scenario: Review route metadata
- **WHEN** 审阅 `careerloop-routes.json`
- **THEN** readiness metadata SHALL include status keys and commands, but SHALL NOT include apiKey、Authorization header、真实 endpoint secret 或客户私有配置值

#### Scenario: Route metadata stays aligned
- **WHEN** 生产就绪检查清单增加、删除或改名检查项
- **THEN** route metadata SHALL 同步更新对应 key、manual item 或 pending capability
