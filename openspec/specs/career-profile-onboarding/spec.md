# 职业画像与 Onboarding 规格

## Purpose
定义 CyanCruise 如何采集 onboarding 信息、维护跨工具职业画像快照，并派生统一职业画像，为后续 CareerLoop 能力提供输入。
## Requirements
### Requirement: 维护跨工具用户画像快照
系统 SHALL 维护一个用户画像快照，并为测评、简历、面试、偏好和 onboarding 保持彼此独立的 block，使每个迁移能力可以只更新自己的 block，而不会覆盖无关数据。快照合并 helper SHALL 有聚焦测试，验证无关 block 被保留，并验证空白目标岗位输入不会清除已有偏好。测评结果 SHALL 能够合并进 assessment block，且不覆盖 onboarding 或 preference 数据。

#### Scenario: 将 onboarding 合并到已有快照
- **WHEN** 已有测评或简历数据的用户提交 onboarding 数据
- **THEN** 系统保留已有 assessment 和 resume block，只更新本次提供的 onboarding 字段

#### Scenario: 读取空快照
- **WHEN** 用户还没有画像快照
- **THEN** 系统返回一个带版本号的空快照，而不是失败或返回 null

#### Scenario: 运行快照合并测试
- **WHEN** helper 模块测试套件运行
- **THEN** 测试验证 onboarding 合并会保留已有快照 block，并且空白输入不会清除已有目标岗位

#### Scenario: 将测评合并到已有快照
- **WHEN** 已有 onboarding 和 preferences 的用户提交测评结果数据
- **THEN** 系统更新 assessment block，并保留 onboarding 和 preferences

### Requirement: 采集 onboarding 输入字段
onboarding block SHALL 采集身份类型、阶段、痛点、自报简历状态、简历准备状态、时间线、教育经历、每周可投入时间、优先帮助项、推荐入口和完成时间戳。

#### Scenario: 完成 onboarding 输入
- **WHEN** 用户完成包含职业阶段、目标岗位、痛点、简历状态、时间线、教育经历、每周可投入时间和优先帮助项的 onboarding
- **THEN** 系统将这些值存入 onboarding block，并将目标岗位存入 preferences

#### Scenario: 自报有简历不等于系统简历
- **WHEN** onboarding 表示用户已有简历
- **THEN** 系统仅将其视为用户自报信息，不标记用户已有上传的简历记录

### Requirement: 将目标岗位作为偏好保存
系统 SHALL 将目标岗位保存到画像 preferences block 中，且 SHALL NOT 将其重复保存为 onboarding 字段。

#### Scenario: 提交 onboarding 目标岗位
- **WHEN** onboarding payload 包含非空目标岗位
- **THEN** 系统将该目标岗位合并进 preferences，并让 onboarding 保持聚焦于输入上下文

#### Scenario: 提交空白目标岗位
- **WHEN** onboarding payload 包含空白目标岗位
- **THEN** 系统不会用空白值替换已有的非空目标岗位

### Requirement: 按证据优先级解析目标岗位
统一画像 SHALL 按 preferences、简历目标岗位、面试岗位、显式用户输入 fact、测评推荐岗位的优先级解析目标岗位。目标岗位优先级 SHALL 有聚焦测试覆盖。

#### Scenario: 偏好覆盖推断岗位
- **WHEN** preferences 中已有目标岗位，同时测评也推荐岗位
- **THEN** 统一画像的目标岗位来自 preferences，且置信度高于测评推断数据

#### Scenario: 使用测评兜底
- **WHEN** 没有 preference、简历目标岗位、面试岗位或显式用户输入目标岗位，但存在测评推荐岗位
- **THEN** 统一画像使用第一个测评推荐岗位作为推断目标

#### Scenario: 运行目标岗位解析测试
- **WHEN** helper 模块测试套件运行
- **THEN** 测试验证 preference 优先级和测评兜底行为

### Requirement: 计算统一画像输出
系统 SHALL 生成统一画像摘要，包含个性化等级、完整度分数、当前阶段、目标岗位来源、准备度指标、缺失信号和证据。当前阶段和准备度规则 SHALL 对仅完成 onboarding 的用户有聚焦测试覆盖。当存在测评数据时，测评 SHALL 参与准备度计算，并移除对应的缺失信号。

#### Scenario: 缺少核心信号
- **WHEN** 用户没有目标岗位、测评、简历、面试或职业计划
- **THEN** 统一画像包含这些缺失核心闭环输入的 missing signals

#### Scenario: Onboarding 影响当前阶段
- **WHEN** onboarding 身份类型为转行求职者
- **THEN** 统一画像当前阶段先体现转行定位，再进入后续简历或面试优化阶段

#### Scenario: 运行 onboarding 画像派生测试
- **WHEN** helper 模块测试套件运行
- **THEN** 测试验证转行阶段推断，并验证自报简历准备状态不会标记真实简历已存在

#### Scenario: 测评移除缺失信号
- **WHEN** 用户已有保存的 assessment block
- **THEN** 统一画像将 `hasAssessment` 标记为 true，并不再报告 assessment 缺失信号

### Requirement: 支持用户补充画像输入
系统 SHALL 接受可选的用户职业输入，包括目标城市、目标行业、时间线、每周投入小时数、偏好的任务难度、是否考虑考研、是否考虑留学，以及自由文本职业目标说明。

#### Scenario: 保存部分画像输入
- **WHEN** 用户只提交目标城市和每周投入小时数
- **THEN** 系统保存这些 fact，且不清除之前保存的目标行业或职业目标说明

### Requirement: 使用画像数据供给今日推荐
onboarding/profile 迁移 SHALL 保持现有今日推荐规则兼容，同时允许它使用更丰富的 onboarding 和 preference 数据。

#### Scenario: onboarding 后推荐
- **WHEN** 用户已完成 onboarding，但没有测评、简历或面试
- **THEN** 今日推荐可以使用目标岗位、身份类型和自报简历状态来选择下一步行动

### Requirement: 通过应用边界暴露画像操作
系统 SHALL 暴露应用级操作，用于读取快照、合并 onboarding、合并 preferences、保存画像输入、刷新统一画像，并按当前用户或指定用户 ID 获取统一画像。当未配置平台数据模型适配器时，默认应用服务 SHALL 使用可持久化的存储适配器；该存储边界 SHALL 可被未来的 Cosmic datamodel 实现替换。持久化适配器 SHALL 有聚焦测试，证明数据可以通过新的 storage 或 service 实例读回。

#### Scenario: Web API 调用合并 onboarding
- **WHEN** web 或表单入口调用 onboarding 操作
- **THEN** 该操作合并快照、刷新统一画像，并按 API 契约返回更新后的快照或画像结果

#### Scenario: 使用持久化适配器重启
- **WHEN** 默认文件型适配器保存画像快照后，服务被重新创建
- **THEN** 可以从配置的存储目录读回该快照

#### Scenario: 替换为 Cosmic 持久化
- **WHEN** Cosmic datamodel 适配器实现完成
- **THEN** 它可以通过 `CareerProfileStorage` 边界替换默认适配器，而无需修改 helper 逻辑或 WebAPI 契约

#### Scenario: 运行存储持久化测试
- **WHEN** cloud 应用模块测试套件运行
- **THEN** 测试验证 snapshot、facts 和派生画像数据能跨新的文件存储或应用服务实例保留
