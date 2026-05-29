## ADDED Requirements

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
