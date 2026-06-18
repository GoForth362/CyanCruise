# 迁移治理规格

## Purpose
定义每个 IPD 到 CyanCruise 迁移 change 必须满足的来源证据、目标归属、状态跟踪和验证规则。
## Requirements
### Requirement: 记录来源证据
每个 IPD 迁移 change 都 SHALL 记录支撑 CyanCruise 实现的 IPD 源文件、源数据结构和源用户流程。对于职业画像和 onboarding，来源证据 SHALL 包含 IPD 快照 DTO、画像输入 DTO、快照合并服务、agent 画像服务、用户 controller onboarding 端点、onboarding 页面、onboarding gate 和待同步工具。

#### Scenario: 审阅迁移 proposal
- **WHEN** 审阅迁移 proposal
- **THEN** proposal 列出相关的 `F:\Project\IPD` 源路径，或明确说明不存在对应源文件

#### Scenario: 审阅职业画像 onboarding 迁移
- **WHEN** 审阅 `migrate-career-profile-onboarding`
- **THEN** 审阅者可以将每个画像和 onboarding requirement 追溯到 IPD 源文件或已记录的迁移决策

### Requirement: 映射目标归属
每个 IPD 迁移 change 都 SHALL 定义产出的数据模型、业务逻辑和用户界面由哪些 CyanCruise 目标模块负责。对于职业画像和 onboarding，共享 DTO SHALL 放在 `base-common`，纯计算或合并 helper SHALL 放在 `base-helper`，应用编排和 WebAPI SHALL 放在 `cloud01-app01`，持久化实体 SHALL 放在 `datamodel`，页面资源 SHALL 放在 `webapp`。

#### Scenario: 规划迁移实现
- **WHEN** 创建设计文档
- **THEN** 文档将每个行为映射到 `datamodel`、`code/base`、`code/cloud01`、`webapp` 或其他明确目标位置

#### Scenario: 新增画像 DTO 和服务
- **WHEN** 实现 profile/onboarding 代码
- **THEN** 共享数据契约、helper、应用服务、持久化模型和页面资源必须放在已定义的归属模块中

### Requirement: 维护迁移状态
当实现 change 启动、完成或优先级变化时，迁移映射表 SHALL 被更新。

#### Scenario: 完成迁移 change
- **WHEN** 迁移 change 被归档
- **THEN** `docs/ipd-to-cyancruise-migration-map.md` 反映最终状态和任何后续 change

### Requirement: 归档前验证
实现型迁移 change SHALL 通过 OpenSpec 校验和项目构建，才能被视为完成。

#### Scenario: 归档实现工作
- **WHEN** 迁移实现准备归档
- **THEN** 必须已运行 `openspec validate <change-id> --strict` 和约定的 Gradle 验证命令，或记录任何阻塞项

### Requirement: 生产就绪证据
实现型迁移 change 在归档或发布前 SHALL 记录生产就绪证据。证据 SHALL 区分本地自动化已通过、租户手工待验证、明确延后和发布阻塞项，并 SHALL 引用迁移地图、route metadata、OpenSpec 规格或生产就绪清单中的至少一种记录。

#### Scenario: 归档实现型迁移
- **WHEN** 实现型迁移 change 准备归档
- **THEN** 迁移记录 SHALL 包含 OpenSpec/Gradle/route 或 focused test 结果，并 SHALL 标明是否需要租户手工验证

#### Scenario: 发布候选仍有手工项
- **WHEN** 某能力依赖真实 Cosmic 租户、KDDT 菜单、身份、文件、AI 或客户平台 adapter
- **THEN** 生产就绪证据 SHALL 将该项标记为 manual required，而不是声明自动化已完全覆盖

#### Scenario: 能力明确延后
- **WHEN** 某 IPD 能力暂不迁移或等待后续平台适配
- **THEN** 迁移治理记录 SHALL 标注 deferred/pending 状态、当前 fallback 和后续验证入口

### Requirement: 使用 CyanCruise 作为当前项目正式名称
所有新增 OpenSpec 文档、设计说明、任务清单、页面文案、提交说明和实现注释 SHALL 将当前项目称为 CyanCruise。CareerLoop 只能作为 IPD 历史来源、旧业务闭环名称、遗留类名或历史路径的一部分出现，不得把当前项目称为 CareerLoop。

#### Scenario: 新增规格文档
- **WHEN** 创建或更新新的 OpenSpec change、主规格或迁移说明
- **THEN** 文档 SHALL 使用 CyanCruise 指代当前项目
- **AND** 文档 MAY 在说明 IPD 历史来源、旧领域命名或遗留代码标识时提到 CareerLoop

#### Scenario: 新增用户可见文案
- **WHEN** 新增或修改 webapp 页面标题、按钮、提示、空状态或错误信息
- **THEN** 用户可见文案 SHALL 使用 CyanCruise 或具体业务能力名称，不得把当前系统称为 CareerLoop

#### Scenario: 解释遗留类名
- **WHEN** 文档需要引用 `CareerProfileStorage`、`CareerPlanApplicationService` 等已有类名、接口名或 route metadata
- **THEN** 文档 SHALL 将这些名称视为历史领域命名或代码标识，而不是当前项目名称

### Requirement: OpenSpec 审查文档使用中文并按序号归档
提供给用户审查的 OpenSpec proposal、design、spec、tasks、verification、migration 说明 SHALL 默认使用中文表达范围、设计、任务、风险和验证要求。OpenSpec 关键词、代码标识、配置项、路径和数据库对象名 MAY 保留英文。OpenSpec change 归档目录 SHALL 使用三位顺序号前缀、日期和 change id，格式为 `NNN-YYYY-MM-DD-<change-id>`。

#### Scenario: 生成审查文档
- **WHEN** 创建或更新给用户审查的 OpenSpec 文档
- **THEN** 文档 SHALL 默认使用中文
- **AND** 文档 MAY 保留 `SHALL`、`WHEN`、`THEN`、代码标识、配置项、路径和数据库对象名等英文内容

#### Scenario: 归档 change
- **WHEN** change 准备归档
- **THEN** 归档工具或操作者 SHALL 扫描 `openspec/changes/archive/` 中已有三位数字前缀目录
- **AND** 新归档目录 SHALL 使用最大序号加 1 作为三位数字前缀
- **AND** 归档目标 SHALL 为 `NNN-YYYY-MM-DD-<change-id>`
- **AND** SHALL NOT 使用无序号的 `YYYY-MM-DD-<change-id>` 目录

### Requirement: 记录 filestorage 替换范围和凭据约束
本迁移 SHALL 明确记录被替换的业务状态范围、暂不替换的文件服务范围、PostgreSQL 配置、手工 DDL、验证命令和凭据处理规则。

#### Scenario: 记录替换范围
- **WHEN** 审查本变更文档
- **THEN** 文档 SHALL 明确列出被替换的 `filestorage` 子目录以及不在本次范围内的文件服务能力

#### Scenario: 不提交真实密码
- **WHEN** 审查源码、OpenSpec artifacts、示例配置、SQL 或提交内容
- **THEN** 其中 SHALL NOT 包含真实 PostgreSQL 密码、生产密钥或本地私有环境凭据

#### Scenario: 验证实际运行环境
- **WHEN** 部署到 Kingdee Cosmic 运行环境
- **THEN** 验证记录 SHALL 区分 Gradle/test 配置和实际星瀚服务 JVM 启动配置，避免只配置 `gradle.properties` 却未影响运行服务

