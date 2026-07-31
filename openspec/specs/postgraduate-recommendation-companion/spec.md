# postgraduate-recommendation-companion Specification

## Purpose
定义 CyanCruise 保研陪伴能力，覆盖竞争力诊断、行动计划、文书润色、导师意向信和对应 Cosmic WebAPI 契约。
## Requirements
### Requirement: 提供保研陪伴入口
CyanCruise SHALL 提供保研陪伴页面，支持用户围绕绩点排名、背景提升、营校目标、投递材料、导师联系和面试准备完成保研规划。首页 SHALL 使用不重复的阶段行动卡片说明资格判断、背景提升、材料表达和导师联系之间的关系，并为每一阶段提供明确入口。页面 SHALL 使用深造陪伴统一青色体系中的沉稳青色调。用户可见文案 SHALL 使用普通中文，不得默认展示内部接口名或不解释的专业缩写。

#### Scenario: 打开保研页面
- **WHEN** 用户打开 `postgraduate-recommendation` 路由
- **THEN** 页面以连续阶段行动卡片显示排名监控、背景提升、材料精修和导师联系入口，且不得再用另一排同名按钮重复展示入口

#### Scenario: 使用键盘进入保研功能
- **WHEN** 用户使用键盘聚焦任一保研阶段行动卡片并触发
- **THEN** 页面进入该卡片对应的既有保研子路由，并显示清晰的焦点反馈

#### Scenario: 身份缺失时不调用受保护接口
- **WHEN** 页面无法解析当前用户身份
- **THEN** 页面显示身份缺失提示，并不得使用硬编码、猜测或上一次缓存的 userId 调用保研 WebAPI

### Requirement: 诊断保研背景竞争力
系统 SHALL 支持用户输入绩点、专业排名、英语水平、竞赛、科研、论文、软著、目标院校层次和目标专业后生成竞争力诊断。诊断 SHALL 包含总分、分项得分、优势、弱项、补强建议和风险提醒。

#### Scenario: 生成竞争力诊断
- **WHEN** 用户提交完整保研背景信息
- **THEN** WebAPI 返回总分、绩点排名、竞赛科研、材料表达、目标匹配等分项评价，并指出最需要补强的方向

#### Scenario: 科研论文为空但竞赛较强
- **WHEN** 用户填写竞赛奖项但未填写科研、论文或软著
- **THEN** 系统提示竞赛基础较好，但科研产出偏弱，并建议联系导师、加入课题组或整理课程项目为研究经历

### Requirement: 生成保研背景提升计划
系统 SHALL 根据当前年级、学校、专业、成绩排名和已提供背景生成紧凑、结构化的背景提升行动计划。

#### Scenario: 首次任务流结果被截断
- **WHEN** `RECOMMENDATION_PLAN_GENERATE` 首次返回的内容不是完整 JSON
- **THEN** 后端 SHALL 自动重试同一已发布任务流一次
- **AND** 第二次返回完整结果时 SHALL 正常保存并展示背景提升计划

### Requirement: 润色保研文书
系统 SHALL 支持用户提交自述信、邮件或推荐信初稿，并按“背景、行动、结果、学术潜力”的经历讲述框架进行结构化润色。润色结果 SHALL 包含改写稿、改写理由、保留亮点和继续补充的信息。

#### Scenario: 润色自述信初稿
- **WHEN** 用户提交文书类型、目标专业和初稿
- **THEN** WebAPI 返回突出学术潜力的中文改写稿，并列出具体修改理由

#### Scenario: 初稿为空
- **WHEN** 用户提交空白文书
- **THEN** 系统拒绝润色并返回清晰中文提示

### Requirement: 生成导师意向信
系统 SHALL 支持用户输入目标导师姓名、目标院校、导师研究方向或论文关键词、个人背景和联系目的后生成导师意向信。系统 SHALL NOT 编造导师最新论文事实；未提供论文方向时 SHALL 要求用户补充或使用泛化表达。

#### Scenario: 根据导师方向生成意向信
- **WHEN** 用户提交导师姓名、研究方向、目标专业和个人背景
- **THEN** WebAPI 返回包含邮件标题、正文、附件建议和发送提醒的导师意向信

#### Scenario: 缺少导师方向
- **WHEN** 用户未填写导师研究方向或论文关键词
- **THEN** 系统返回较保守的通用意向信，并提示发送前补充导师真实研究信息

### Requirement: 暴露保研陪伴 WebAPI
系统 SHALL 通过 Cosmic WebAPI 暴露保研陪伴能力。WebAPI SHALL 支持竞争力诊断、行动计划生成、文书润色和导师意向信生成，并对当前用户执行身份约束。

#### Scenario: 调用保研诊断 WebAPI
- **WHEN** 调用方提交 userId 和保研背景请求
- **THEN** WebAPI 返回该用户本次请求对应的结构化诊断结果

#### Scenario: 调用文书和意向信 WebAPI
- **WHEN** 调用方提交 userId、文书请求或导师意向信请求
- **THEN** WebAPI 返回结构化润色或意向信结果，且错误提示使用普通中文

### Requirement: Persist recommendation companion records
Recommendation companion WebAPI SHALL save request and successful result for all four analysis tasks as a current-user recommendation record before returning the result.

#### Scenario: 保存保研分析结果
- **WHEN** 用户成功完成任一保研分析
- **THEN** 系统 SHALL 保存本次请求和结构化结果，且记录只属于该用户

#### Scenario: 再次进入保研功能页面
- **WHEN** 当前用户此前已成功生成排名监控、背景提升、材料精修或导师联系结果并再次进入对应页面
- **THEN** 页面 SHALL 按当前用户、`RECOMMENDATION` 方向及对应任务类型读取最新一条成功记录
- **AND** 显示该记录中的结构化结果；不得显示其他用户、其他功能或过期的本地结果

### Requirement: Manage recommendation materials and tutor contact status
Recommendation companion SHALL support maintaining recommendation materials and tutor contact status for the current user. Material and contact records SHALL be bound to user, direction, and source record, and SHALL support status updates and history events.

#### Scenario: Save polished document material
- **WHEN** a user saves or generates polished recommendation material
- **THEN** the system SHALL persist the material record with status and source record reference

#### Scenario: Save tutor contact letter
- **WHEN** a user generates or updates a tutor intention letter
- **THEN** the system SHALL persist the tutor contact material and append a history event

### Requirement: 保研页面分析由真实升学陪伴智能体生成
系统 SHALL 将保研竞争力诊断、行动计划、文书润色和导师意向信分别映射为 `RECOMMENDATION_DIAGNOSE`、`RECOMMENDATION_PLAN_GENERATE`、`RECOMMENDATION_DOCUMENT_POLISH` 和 `RECOMMENDATION_TUTOR_LETTER`，并由统一升学陪伴智能体生成结果。

#### Scenario: 生成保研页面结果
- **WHEN** 当前用户提交任一保研页面分析请求
- **THEN** 系统 SHALL 调用升学陪伴智能体并返回对应的现有结果 DTO
- **AND** SHALL NOT 调用本地 Helper 生成规则结果

#### Scenario: 保研智能分析失败
- **WHEN** 智能体要求补充资料、调用失败或返回无效结构
- **THEN** 系统 SHALL 返回普通中文提示
- **AND** SHALL NOT 返回默认评分、默认行动、默认文书或默认邮件

### Requirement: 保研全部分析表单保留真实输入

系统 SHALL 对竞争力诊断、行动计划、材料精修和导师联系四项表单在调用智能体前保存真实请求，并在页面重绘、分析失败、刷新和重新进入页面时恢复该用户该任务的最后一次输入。

#### Scenario: 文书润色失败后保留初稿

- **WHEN** 用户提交个人陈述、邮件或推荐信要点润色且智能体未返回有效结果
- **THEN** 页面 SHALL 保留文书类型、目标专业、亮点素材和原始初稿
- **AND** SHALL NOT 用智能体返回内容覆盖用户初稿

### Requirement: 保研陪伴 SHALL 校验各任务核心资料
保研排名监控、背景提升、材料精修和导师联系 SHALL 在调用智能体前仅校验完成该任务所必需的页面字段，并在核心字段齐全时调用智能体；竞赛、论文、软著等补充信息为空 SHALL 不得阻止生成。

#### Scenario: 缺少文书初稿
- **WHEN** 用户请求润色材料但未填写文书初稿
- **THEN** 页面 SHALL 提示填写文书初稿且不得调用智能体

#### Scenario: 背景资料不完整
- **WHEN** 用户提交年级、学校、专业、绩点和排名以生成保研行动计划
- **THEN** 页面 SHALL 调用智能体，并允许智能体在结果中提示补充竞赛或科研资料

### Requirement: 导师联系区分本科院校与目标院校
系统 SHALL 在导师联系表单和 `RECOMMENDATION_TUTOR_LETTER` 请求中分别使用 `currentSchool` 表示用户本科就读院校、使用 `targetSchool` 表示拟申请导师所在院校。

#### Scenario: 生成导师意向信
- **WHEN** 用户填写本科就读院校和目标院校并生成导师意向信
- **THEN** 邮件正文 SHALL 仅把 `currentSchool` 描述为用户本科就读院校
- **AND** SHALL 仅把 `targetSchool` 描述为拟申请或咨询的院校
- **AND** 不得用其中一个字段替代、推断或覆盖另一个字段

