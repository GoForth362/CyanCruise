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
