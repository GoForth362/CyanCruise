## ADDED Requirements

### Requirement: 使用诊断结果更新简历摘要
简历诊断能力 SHALL 能够将诊断总分写回简历记录的 diagnosisScore，并复用简历基础能力将最新诊断分数同步到职业画像 resume block。该更新 SHALL 保留简历记录的标题、目标岗位、文件 key、版本、状态和解析内容。

#### Scenario: 诊断分数写入简历
- **WHEN** 简历诊断对用户自己的简历产生 overallScore
- **THEN** 系统将该分数保存到该简历记录 diagnosisScore

#### Scenario: 诊断分数同步画像
- **WHEN** 简历记录的 diagnosisScore 被诊断结果更新
- **THEN** 系统将最新诊断分数同步到用户画像 resume block

#### Scenario: 保留简历元数据
- **WHEN** 系统只更新诊断分数
- **THEN** 简历标题、目标岗位、文件 key、版本、状态和解析内容保持不变
