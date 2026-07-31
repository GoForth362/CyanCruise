# postgraduate-exam-companion Specification

## Purpose
定义 CyanCruise 考研陪伴能力，覆盖择校择专业、复习计划、错题解析、复试准备和对应 Cosmic WebAPI 契约。
## Requirements
### Requirement: 提供考研陪伴入口
CyanCruise SHALL 提供考研陪伴页面，支持用户围绕择校择专业、复习计划、错题解析和复试准备完成一个连续的考研规划流程。首页 SHALL 使用不重复的阶段行动卡片展示阶段编号、当前任务、功能用途和进入入口，并形成从目标定位到复试准备的清晰路径。页面 SHALL 使用深造陪伴统一青色体系中的明亮青绿色调。页面和 WebAPI 可见文案 SHALL 使用普通中文表达，不得把内部接口名、代码枚举或行业黑话作为主要用户信息。

#### Scenario: 打开考研页面
- **WHEN** 用户打开 `postgraduate` 路由
- **THEN** 页面以连续阶段行动卡片显示择校择专业、复习计划、错题解析和复试准备入口，且不得再用另一排同名按钮重复展示入口

#### Scenario: 使用键盘进入考研功能
- **WHEN** 用户使用键盘聚焦任一考研阶段行动卡片并触发
- **THEN** 页面进入该卡片对应的既有考研子路由，并显示清晰的焦点反馈

#### Scenario: 身份缺失时保留可理解提示
- **WHEN** 页面无法解析当前用户身份
- **THEN** 页面显示身份缺失提示，并不得使用硬编码、猜测或上一次缓存的 userId 调用受保护 WebAPI

### Requirement: 生成稳冲保择校建议
系统 SHALL 支持用户输入本科学校、本科专业、学校层次、绩点、英语水平、期望地区、目标专业和备考偏好后生成“保、稳、冲”三档择校建议。建议 SHALL 说明推荐理由、准备风险、下一步核对事项，并提醒用户核对当年招生简章、国家线和报录比。

#### Scenario: 输入完整画像生成择校建议
- **WHEN** 用户提交本科学校、本科专业、学校层次、绩点、英语水平、期望地区和目标专业
- **THEN** WebAPI SHALL 返回保底、稳妥和冲刺三档院校建议，每档包含院校名称、专业方向、匹配理由、风险提示和行动建议

#### Scenario: 缺少非核心画像仍可推荐
- **WHEN** 用户未填写英语水平、绩点或备考偏好等非核心信息
- **THEN** 系统 SHALL 使用已填写信息生成保守建议，并标记需要补充的信息

### Requirement: 生成考研复习计划
系统 SHALL 根据目标院校、目标专业、初试日期、考试科目和可投入时间生成紧凑且可执行的三轮复习计划。

#### Scenario: 首次任务流结果被截断
- **WHEN** `POSTGRADUATE_PLAN_GENERATE` 首次返回的内容不是完整 JSON
- **THEN** 后端 SHALL 自动重试同一已发布任务流一次
- **AND** 第二次返回完整结果时 SHALL 正常保存并展示复习计划

### Requirement: 解析错题并推荐同类练习
系统 SHALL 支持用户粘贴或输入错题文本并获取解析。解析 SHALL 包含答案思路、关键考点、知识树、易错原因、订正建议和同类衍生题，不得在 AI 不可用或解析失败时向用户展示原始技术错误。解析成功后，系统 SHALL 将题目文本、科目、错误答案和解析结果写入当前用户独立错题本。

#### Scenario: 输入错题文本获取解析
- **WHEN** 用户提交科目、题目文本和自己的错误答案
- **THEN** WebAPI 返回结构化错题解析、知识树节点、易错原因和同类衍生题
- **AND** 系统保存该用户的独立错题本记录

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
Postgraduate exam WebAPI SHALL save request and successful result for all four analysis tasks as a current-user postgraduate record before returning the result.

#### Scenario: 分析服务失败
- **WHEN** 智能体调用失败或返回不可用结果
- **THEN** 系统 SHALL NOT save a successful analysis record

#### Scenario: 再次进入择校择专业页面
- **WHEN** 当前用户此前已成功生成择校择专业建议并再次进入该页面
- **THEN** 页面 SHALL 按当前用户、`POSTGRADUATE` 方向和 `POSTGRADUATE_SCHOOL_RECOMMEND` 任务类型读取最新一条成功记录
- **AND** 显示该记录中的结构化建议结果

#### Scenario: 再次进入其他考研功能页面
- **WHEN** 当前用户此前已成功生成复习计划或复试准备结果并再次进入对应页面
- **THEN** 页面 SHALL 按当前用户、`POSTGRADUATE` 方向及对应任务类型读取最新一条成功记录
- **AND** 显示该记录中的结构化结果；没有历史记录时保持结果区域为空

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

### Requirement: 考研页面分析由真实升学陪伴智能体生成
系统 SHALL 将考研择校、复习计划、错题解析和复试准备分别映射为 `POSTGRADUATE_SCHOOL_RECOMMEND`、`POSTGRADUATE_PLAN_GENERATE`、`POSTGRADUATE_MISTAKE_ANALYZE` 和 `POSTGRADUATE_REEXAM_PREPARE`，并由统一升学陪伴智能体生成结果。

#### Scenario: 生成考研页面结果
- **WHEN** 当前用户提交任一考研页面分析请求
- **THEN** 系统 SHALL 调用升学陪伴智能体并返回对应的现有结果 DTO
- **AND** SHALL NOT 调用本地 Helper 生成规则结果

#### Scenario: 考研智能分析失败
- **WHEN** 智能体未配置、调用失败或返回无效结构
- **THEN** 系统 SHALL 返回普通中文可重试提示
- **AND** SHALL NOT 返回默认院校、默认计划、默认答案或默认清单

### Requirement: 考研分析表单保留用户真实输入
系统 SHALL 在调用智能体前按当前用户和 `taskType` 保存本次表单草稿，并在页面重绘、分析失败或重新进入页面时恢复该用户最后保存的内容。

#### Scenario: 智能分析失败后保留输入
- **WHEN** 当前用户提交考研择校表单且智能体返回失败
- **THEN** 页面 SHALL 继续显示本次提交的本科学校、学校层次、成绩、英语水平、期望地区、目标专业和备考偏好
- **AND** SHALL NOT 因加载状态或错误状态重绘而清空输入

#### Scenario: 重新进入页面恢复草稿
- **WHEN** 当前用户重新打开考研择校页面
- **THEN** 系统 SHALL 从服务端读取该用户 `POSTGRADUATE_SCHOOL_RECOMMEND` 的最后一份草稿
- **AND** SHALL NOT 读取其他用户或其他任务类型的草稿

### Requirement: 错题解析页使用独立错题本入口
错题解析页 SHALL 在提交操作区提供“错题记录”入口，并 SHALL NOT 在当前解析页底部嵌入过往错题列表。过往错题的浏览和详情查看 SHALL 由独立错题本页面完成。

#### Scenario: 打开错题解析页
- **WHEN** 用户进入错题解析页
- **THEN** 页面 SHALL 显示解析操作和“错题记录”入口
- **AND** 页面 SHALL NOT 在底部渲染过往错题列表

### Requirement: 考研陪伴入口显示当前路线阶段
考研陪伴入口 SHALL 在“你的行动路线”区域显示当前用户的实时阶段位置，并使阶段工具卡同步呈现已完成、当前进行中和待完成状态。

#### Scenario: 打开考研陪伴入口
- **WHEN** 用户打开 `postgraduate` 路由
- **THEN** 页面 SHALL 同步该用户的路线进度，并以清晰中文说明当前所处阶段

### Requirement: 错题本提供移除操作
考研陪伴的错题本 SHALL 允许用户移除自己不再需要的错题记录；操作入口 SHALL 使用“移除错题”这一普通中文文案。

#### Scenario: 错题本存在历史记录
- **WHEN** 用户查看错题本列表或某一道错题详情
- **THEN** 页面 SHALL 提供该记录的移除入口

### Requirement: 考研陪伴 SHALL 在调用前提示核心缺项
考研四项表单 SHALL 在调用智能体前校验各自核心字段，并以普通中文指出缺少项；核心字段齐全时 SHALL 调用智能体，不得因未填写页面不存在的字段而阻止调用。

#### Scenario: 复习计划缺少目标院校
- **WHEN** 用户提交复习计划且目标院校为空
- **THEN** 页面 SHALL 提示填写目标院校且不得调用智能体

### Requirement: 考研全部分析表单保留真实输入

系统 SHALL 对择校择专业、复习计划、错题解析和复试准备四项表单在调用智能体前保存真实请求，并在页面重绘、分析失败、刷新和重新进入页面时恢复该用户该任务的最后一次输入。

#### Scenario: 分析失败后保留复习计划输入

- **WHEN** 用户提交复习计划且智能体调用或结果校验失败
- **THEN** 页面 SHALL 继续显示目标院校、专业、日期、每周时间和考试科目
- **AND** 用户无需重新填写后即可修改并再次提交

