# postgraduate-recommendation-companion Specification

## Purpose
定义 CyanCruise 保研陪伴能力，覆盖竞争力诊断、行动计划、文书润色、导师意向信和对应 Cosmic WebAPI 契约。

## Requirements

### Requirement: 提供保研陪伴入口
CyanCruise SHALL 提供保研陪伴页面，支持用户围绕绩点排名、背景提升、营校目标、投递材料、导师联系和面试准备完成保研规划。用户可见文案 SHALL 使用普通中文，不得默认展示内部接口名或不解释的专业缩写。

#### Scenario: 打开保研页面
- **WHEN** 用户打开 `postgraduate-recommendation` 路由
- **THEN** 页面显示背景竞争力诊断、行动计划、文书润色和导师意向信入口

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

### Requirement: 生成保研行动计划
系统 SHALL 根据当前年级、排名稳定性、目标层次和背景短板生成保研行动计划。计划 SHALL 覆盖绩点排名监控、背景提升、夏令营/预推免信息、材料准备和面试模拟。

#### Scenario: 生成阶段行动计划
- **WHEN** 用户提交当前年级、目标院校层次和背景诊断信息
- **THEN** WebAPI 返回按阶段排列的行动清单、截止提醒和每周推进建议

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
Recommendation companion WebAPI SHALL save the request and result as the current user's further-study companion record after generating competitiveness diagnosis, action plans, document polishing, or tutor intention letters. The saved record SHALL mark direction as recommendation and SHALL include the specific record type.

#### Scenario: Save recommendation diagnosis result
- **WHEN** a user generates recommendation competitiveness diagnosis
- **THEN** the WebAPI SHALL persist the request and result as a recommendation companion record

#### Scenario: Save recommendation plan result
- **WHEN** a user generates a recommendation action plan
- **THEN** the WebAPI SHALL persist the plan result and related request for that user

### Requirement: Manage recommendation materials and tutor contact status
Recommendation companion SHALL support maintaining recommendation materials and tutor contact status for the current user. Material and contact records SHALL be bound to user, direction, and source record, and SHALL support status updates and history events.

#### Scenario: Save polished document material
- **WHEN** a user saves or generates polished recommendation material
- **THEN** the system SHALL persist the material record with status and source record reference

#### Scenario: Save tutor contact letter
- **WHEN** a user generates or updates a tutor intention letter
- **THEN** the system SHALL persist the tutor contact material and append a history event

