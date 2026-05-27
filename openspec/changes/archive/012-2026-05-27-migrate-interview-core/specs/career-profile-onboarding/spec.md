## ADDED Requirements

### Requirement: 使用模拟面试结果更新画像
模拟面试核心能力 SHALL 能够将最近一次面试结果写入用户画像 interview block。写入内容 SHALL 包含面试 ID、岗位名称、难度、最近分数、强项、弱项和完成时间，并保留画像中其他 block。

#### Scenario: 面试结束写入基础结果
- **WHEN** 用户结束模拟面试且报告尚未生成
- **THEN** 系统将面试 ID、岗位名称、难度、最近分数和完成时间写入 interview block

#### Scenario: 保存报告写入强弱项
- **WHEN** 用户保存面试报告且报告包含可解析雷达分
- **THEN** 系统更新 interview block 的 strongDimensions 和 weakDimensions，并保留其他画像 block

#### Scenario: 今日行动消费面试信号
- **WHEN** 用户画像已有 interview block
- **THEN** 今日行动和统一画像可以使用该面试信号判断面试是否缺失或是否低分
