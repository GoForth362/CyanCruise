# assessment-core Specification

## Purpose
定义 CyanCruise 职业测评核心的数据结构、提交评分、维度统计和画像生成规则，为后续用户画像、今日行动建议和测评页面接入提供稳定内核。

## Requirements
### Requirement: 表达测评量表结构
系统 SHALL 使用 JDK 8 兼容 DTO 表达测评量表、题目和选项，且不得依赖 Spring、JPA、Lombok 或 Jackson 注解。

#### Scenario: 读取量表结构
- **WHEN** 调用方组装一个测评量表
- **THEN** 量表能够包含量表 ID、标题、版本、题目列表、题目排序和每题选项列表

### Requirement: 提交单选答案并生成答案快照
系统 SHALL 接收 `questionId -> optionId` 的单选答案映射，并为每条答案生成包含题目 ID、选项 ID、维度代码和分值快照的结果项。

#### Scenario: 提交有效选项
- **WHEN** 用户提交的选项存在于对应题目的选项列表中
- **THEN** 评分结果包含该题的答案快照，并记录选项的维度代码和分值

#### Scenario: 提交不存在的选项
- **WHEN** 用户提交的选项 ID 不存在或不属于该题
- **THEN** 系统忽略该选项的维度计数，并在答案快照中保留题目 ID 和选项 ID

### Requirement: 按选项维度统计结果
系统 SHALL 基于被选中选项的 `dimensionCode` 统计各维度出现次数。

#### Scenario: 多个选项命中同一维度
- **WHEN** 多道题选择的选项具有相同维度代码
- **THEN** 该维度计数按命中次数累加

### Requirement: 生成 MBTI 画像代码
当量表标题包含 `MBTI` 时，系统 SHALL 根据 E/I、S/N、T/F、J/P 四组维度计数生成四字母画像代码；同分时选择前一维度。

#### Scenario: MBTI 画像计算
- **WHEN** E 大于 I、N 大于 S、T 大于 F、P 大于 J
- **THEN** 画像代码为 `ENTP`

#### Scenario: MBTI 同分规则
- **WHEN** E 与 I 同分
- **THEN** 第一位选择 `E`

### Requirement: 生成非 MBTI 画像代码
当量表标题不包含 `MBTI` 时，系统 SHALL 按维度计数从高到低取前三个维度代码并拼接为画像。

#### Scenario: Holland 画像计算
- **WHEN** R、I、A、S 等维度有不同计数
- **THEN** 结果摘要由计数最高的前三个维度按降序拼接

### Requirement: 返回可写入用户画像的测评摘要
评分结果 SHALL 包含量表 ID、量表标题、状态、画像摘要、维度计数和答案快照，以便后续写入 `UserProfileSnapshot.AssessmentBlock`。

#### Scenario: 测评完成
- **WHEN** 评分服务完成一次提交
- **THEN** 返回状态为 `COMPLETED` 的结果，并包含画像摘要和维度计数
