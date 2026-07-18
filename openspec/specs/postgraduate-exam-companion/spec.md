# postgraduate-exam-companion Specification

## Purpose
定义 CyanCruise 考研陪伴能力，覆盖择校择专业、复习计划、错题解析、复试准备和对应 Cosmic WebAPI 契约。
## Requirements
### Requirement: 提供考研陪伴入口
CyanCruise SHALL 提供考研陪伴页面，支持用户围绕择校择专业、复习计划、错题解析和复试准备完成一个连续的考研规划流程。页面和 WebAPI 可见文案 SHALL 使用普通中文表达，不得把内部接口名、代码枚举或行业黑话作为主要用户信息。

#### Scenario: 打开考研页面
- **WHEN** 用户打开 `postgraduate` 路由
- **THEN** 页面显示考研目标信息、择校建议、复习计划、错题解析和复试准备入口

#### Scenario: 身份缺失时保留可理解提示
- **WHEN** 页面无法解析当前用户身份
- **THEN** 页面显示身份缺失提示，并不得使用硬编码、猜测或上一次缓存的 userId 调用受保护 WebAPI

### Requirement: 生成稳冲保择校建议
系统 SHALL 支持用户输入本科学校层次、绩点、英语水平、期望地区、目标专业和备考偏好后生成“保、稳、冲”三档择校建议。建议 SHALL 说明推荐理由、准备风险、下一步核对事项，并提醒用户核对当年招生简章、国家线和报录比。

#### Scenario: 输入完整画像生成择校建议
- **WHEN** 用户提交本科学校层次、绩点、英语水平、期望地区和目标专业
- **THEN** WebAPI 返回保底、稳妥和冲刺三档院校建议，每档包含院校名称、专业方向、匹配理由、风险提示和行动建议

#### Scenario: 缺少部分画像仍可推荐
- **WHEN** 用户未填写英语水平或绩点等部分信息
- **THEN** 系统使用已填写信息生成保守建议，并标记需要补充的信息

### Requirement: 生成分轮复习计划
系统 SHALL 根据目标院校、目标专业、考试科目、当前日期和初试日期生成复习计划。计划 SHALL 至少包含基础、提高、冲刺三轮，每轮包含时间范围、科目重点、每周任务、阶段目标和状态调整建议。

#### Scenario: 基于考试科目生成三轮计划
- **WHEN** 用户提交数学、英语、政治和专业课等考试科目以及初试日期
- **THEN** WebAPI 返回基础、提高、冲刺三轮计划，并按科目拆分每周任务和阶段目标

#### Scenario: 备考时间较短时压缩计划
- **WHEN** 当前日期距离初试日期不足三个月
- **THEN** 系统生成压缩版计划，优先安排高频考点、真题复盘和冲刺模拟

### Requirement: 解析错题并推荐同类练习
系统 SHALL 支持用户粘贴或输入错题文本并获取解析。解析 SHALL 包含答案思路、关键考点、知识树、易错原因、订正建议和同类衍生题，不得在 AI 不可用或解析失败时向用户展示原始技术错误。

#### Scenario: 输入错题文本获取解析
- **WHEN** 用户提交科目、题目文本和自己的错误答案
- **THEN** WebAPI 返回结构化错题解析、知识树节点、易错原因和同类衍生题

#### Scenario: 题目文本为空
- **WHEN** 用户提交空白题目文本
- **THEN** 系统拒绝解析并返回清晰中文错误提示

### Requirement: 提供复试准备清单
系统 SHALL 在初试后或用户主动请求时生成复试准备清单。清单 SHALL 覆盖简历打磨、联系导师、专业课复盘、英语口语、自我介绍、科研经历梳理和材料准备。

#### Scenario: 生成复试准备建议
- **WHEN** 用户提交目标院校、目标专业、初试状态和已有材料
- **THEN** WebAPI 返回分阶段复试准备清单、导师联系建议、简历准备要点和模拟面试建议

#### Scenario: 初试尚未结束
- **WHEN** 用户在初试前请求复试准备
- **THEN** 系统返回轻量预备清单，并提示当前重点仍是初试复习

### Requirement: 暴露考研陪伴 WebAPI
系统 SHALL 通过 Cosmic WebAPI 暴露考研陪伴能力。WebAPI SHALL 支持择校建议、复习计划生成、错题解析和复试准备清单，并对当前用户执行身份约束。

#### Scenario: 调用择校建议 WebAPI
- **WHEN** 调用方提交 userId 和择校画像请求
- **THEN** WebAPI 返回该用户本次请求对应的择校建议结果

#### Scenario: 调用计划与错题 WebAPI
- **WHEN** 调用方提交 userId、计划请求或错题请求
- **THEN** WebAPI 返回结构化计划或错题解析结果，且错误提示使用普通中文

### Requirement: Persist postgraduate exam companion records
Postgraduate exam WebAPI SHALL save the request and result as the current user's further-study companion record after generating school recommendations, review plans, mistake analysis, or re-exam preparation. The saved record SHALL mark direction as postgraduate exam and SHALL include the specific record type.

#### Scenario: Save school recommendation result
- **WHEN** a user generates postgraduate school recommendations
- **THEN** the WebAPI SHALL persist the request, result, record type, and Chinese summary for that user

#### Scenario: Save review plan result
- **WHEN** a user generates a postgraduate review plan
- **THEN** the WebAPI SHALL persist the request and generated plan as a further-study record for that user

#### Scenario: Save mistake analysis result
- **WHEN** a user generates mistake analysis
- **THEN** the WebAPI SHALL persist the question payload and analysis result as a further-study record

#### Scenario: Save re-exam preparation result
- **WHEN** a user generates re-exam preparation content
- **THEN** the WebAPI SHALL persist the preparation request and checklist result as a further-study record

### Requirement: 考研陪伴 SHALL 提供基于用户资料的完整路线入口
考研陪伴 SHALL 将当前用户画像、目标院校和用户上传资料接入升学中心的完整路线生成，而不是只生成孤立的复习建议。

#### Scenario: 从升学中心生成考研完整路线
- **WHEN** 用户选择考研方向、保存目标院校并生成升学规划
- **THEN** 系统 SHALL 调用考研规划智能体生成阶段路线、每周计划和每日行动
- **AND** 生成结果 SHALL 保存为当前用户的考研路线

#### Scenario: 用户资料发生变化
- **WHEN** 用户新增或删除考研资料后再次确认生成路线
- **THEN** 任务流输入 SHALL 使用最新的该用户资料集合
- **AND** 已开始或已完成阶段 SHALL 按路线进度保护规则保留

