# study-abroad-companion Specification

## Purpose
定义 CyanCruise 留学陪伴能力，覆盖画像诊断、语言规划、选校定位、个人陈述主线、签证网申清单和对应 Cosmic WebAPI 契约。
## Requirements
### Requirement: 提供留学陪伴入口
CyanCruise SHALL 提供留学陪伴页面，支持用户围绕国家/地区选择、语言考试、软实力提升、选校定位、文书撰写、签证与网申完成留学规划。首页 SHALL 使用不重复的阶段行动卡片说明目的地判断、语言准备、选校梯度、文书主线和签证网申之间的顺序，并为每一阶段提供明确入口。页面 SHALL 使用深造陪伴统一青色体系中的偏冷蓝青色调。用户可见文案 SHALL 使用普通中文，并在出现 PS、CV、RL 等文书类型时解释其含义。

#### Scenario: 打开留学页面
- **WHEN** 用户打开 `study-abroad` 路由
- **THEN** 页面以连续阶段行动卡片显示国家地区、语言考试、选校定位、个人陈述主线和签证网申入口，且不得再用另一排同名按钮重复展示入口

#### Scenario: 使用键盘进入留学功能
- **WHEN** 用户使用键盘聚焦任一留学阶段行动卡片并触发
- **THEN** 页面进入该卡片对应的既有留学子路由，并显示清晰的焦点反馈

#### Scenario: 身份缺失时不调用受保护接口
- **WHEN** 页面无法解析当前用户身份
- **THEN** 页面显示身份缺失提示，并不得使用硬编码、猜测或上一次缓存的 userId 调用留学 WebAPI

### Requirement: 诊断留学画像并生成任务总览
系统 SHALL 支持用户输入目标国家/地区、申请阶段、GPA、语言成绩、预算、专业方向、科研/实习/竞赛经历后生成留学画像诊断。诊断 SHALL 包含准备度、优势、短板、软实力提升建议和风险提醒。

#### Scenario: 生成留学画像诊断
- **WHEN** 用户提交目标国家/地区、GPA、语言成绩、预算和背景经历
- **THEN** WebAPI 返回准备度评分、优势短板、软实力提升建议和后续任务总览

### Requirement: 生成语言考试规划
系统 SHALL 根据目标语言考试、当前分数、目标分数、考试日期和每周可投入时间生成备考计划。计划 SHALL 覆盖雅思/托福/GRE 的听说读写或对应分项，并提供写作自查和口语对练题。

#### Scenario: 生成雅思备考计划
- **WHEN** 用户提交当前雅思分数、目标分数和考试日期
- **THEN** WebAPI 返回分阶段备考计划、每周任务、写作自查维度和口语对练题

### Requirement: 生成选校定位
系统 SHALL 根据目标国家/地区、专业方向、GPA、语言成绩、预算和背景经历生成冲刺、匹配、稳妥三档选校定位。定位 SHALL 提醒用户核对院校官网、项目要求、截止时间和奖学金信息。

#### Scenario: 生成选校定位
- **WHEN** 用户提交目标国家/地区、专业方向和申请背景
- **THEN** WebAPI 返回三档项目建议、推荐理由、风险和官网核对事项

### Requirement: 生成个人陈述黄金线
系统 SHALL 支持用户输入目标专业、个人经历、教授或项目方向、未来目标后生成个人陈述黄金线。黄金线 SHALL 包含开头故事、学术兴趣、关键经历、项目匹配、未来目标和继续补充问题。

#### Scenario: 生成个人陈述黄金线
- **WHEN** 用户提交个人经历、目标专业和项目方向
- **THEN** WebAPI 返回可编辑的个人陈述结构、核心故事线和补充问题

### Requirement: 生成签证与网申清单
系统 SHALL 根据目标国家/地区、申请季、材料状态和录取阶段生成签证与网申清单。清单 SHALL 覆盖网申账号、成绩单、语言成绩、文书、推荐信、资金证明、签证材料和截止时间。

#### Scenario: 生成签证网申清单
- **WHEN** 用户提交目标国家/地区和申请阶段
- **THEN** WebAPI 返回分阶段材料清单、优先级和官方核对提醒

### Requirement: 暴露留学陪伴 WebAPI
系统 SHALL 通过 Cosmic WebAPI 暴露留学陪伴能力。WebAPI SHALL 支持画像诊断、语言规划、选校定位、个人陈述黄金线和签证网申清单，并对当前用户执行身份约束。

#### Scenario: 调用留学规划 WebAPI
- **WHEN** 调用方提交 userId 和留学规划请求
- **THEN** WebAPI 返回该用户本次请求对应的结构化结果，且错误提示使用普通中文

### Requirement: Persist study abroad companion records
Study abroad companion WebAPI SHALL save request and successful result for all five analysis tasks as a current-user study-abroad record before returning the result.

#### Scenario: 保存留学分析结果
- **WHEN** 用户成功完成任一留学分析
- **THEN** 系统 SHALL 保存本次请求和结构化结果，且记录只属于该用户

#### Scenario: 再次进入留学功能页面
- **WHEN** 当前用户此前已成功生成国家地区、语言考试、选校定位、文书主线或签证网申结果并再次进入对应页面
- **THEN** 页面 SHALL 按当前用户、`STUDY_ABROAD` 方向及对应任务类型读取最新一条成功记录
- **AND** 显示该记录中的结构化结果；没有历史记录时保持结果区域为空

### Requirement: Manage study abroad documents and visa checklist status
Study abroad companion SHALL support maintaining personal statements, recommendation letters, resumes, visa materials, and online application checklist status for the current user. The system SHALL save material records, file references, and history events.

#### Scenario: Save personal statement material
- **WHEN** a user saves or generates personal statement content
- **THEN** the system SHALL persist the material record with status and source record reference

#### Scenario: Save visa checklist status
- **WHEN** a user updates visa or online application checklist status
- **THEN** the system SHALL persist the status change and append a history event

### Requirement: 留学页面分析由真实升学陪伴智能体生成
系统 SHALL 将留学画像诊断、语言计划、选校定位、个人陈述主线和签证网申清单分别映射为 `STUDY_ABROAD_PROFILE_DIAGNOSE`、`STUDY_ABROAD_LANGUAGE_PLAN`、`STUDY_ABROAD_SCHOOL_POSITION`、`STUDY_ABROAD_STATEMENT_OUTLINE` 和 `STUDY_ABROAD_VISA_CHECKLIST`，并由统一升学陪伴智能体生成结果。

#### Scenario: 生成留学页面结果
- **WHEN** 当前用户提交任一留学页面分析请求
- **THEN** 系统 SHALL 调用升学陪伴智能体并返回对应的现有结果 DTO
- **AND** SHALL NOT 调用本地 Helper 生成规则结果

#### Scenario: 留学智能分析失败
- **WHEN** 智能体要求补充资料、调用失败或返回无效结构
- **THEN** 系统 SHALL 返回普通中文提示
- **AND** SHALL NOT 返回默认准备度、默认计划、默认院校、默认文书或默认清单

### Requirement: 留学全部分析表单保留真实输入

系统 SHALL 对申请画像、语言考试、选校定位、文书主线和签证网申五项表单在调用智能体前保存真实请求，并在页面重绘、分析失败、刷新和重新进入页面时恢复该用户该任务的最后一次输入。

#### Scenario: 语言计划失败后保留备考信息

- **WHEN** 用户提交语言考试计划且智能体未返回有效结果
- **THEN** 页面 SHALL 保留考试类型、当前分数、目标分数、考试日期、每周时间和薄弱项
- **AND** 用户刷新页面后仍可恢复该任务的最近输入

### Requirement: 留学陪伴 SHALL 校验各任务核心资料
留学画像、语言计划、选校定位、文书主线和签证网申 SHALL 在调用智能体前仅校验完成该任务的核心页面字段；软实力、薄弱单项和偏好等补充资料为空 SHALL 由结果中的补充建议说明，不得阻止生成。

#### Scenario: 语言计划缺少目标成绩
- **WHEN** 用户提交语言计划且目标成绩或考试日期为空
- **THEN** 页面 SHALL 提示对应中文字段且不得调用智能体

#### Scenario: 文书主线资料齐全
- **WHEN** 用户提交目标专业、个人故事和学术或项目经历
- **THEN** 页面 SHALL 调用智能体并展示其返回的文书主线和追问项

