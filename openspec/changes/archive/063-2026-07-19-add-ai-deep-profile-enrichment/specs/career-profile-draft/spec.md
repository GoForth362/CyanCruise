## ADDED Requirements

### Requirement: 快照区分事实画像和 AI 深度画像
统一职业画像快照 SHALL 使用独立区块保存 AI 深度画像，并保留原有用户事实、测评结果、偏好和引导信息的语义不变。

#### Scenario: 保存 AI 深度画像
- **WHEN** 系统写入最新 AI 深度画像
- **THEN** 快照 SHALL 将其标识为测评 AI 分析，并保留用户自行填写的字段值不变

### Requirement: 保存自画像补充事实
统一职业画像快照 SHALL 在事实画像区块保存用户填写的“自画像补充”，并允许后续 AI 能力读取该字段。该字段 MUST NOT 被标记为 AI 推断。

#### Scenario: 用户保存自画像补充
- **WHEN** 用户在自画像中填写补充事实并保存
- **THEN** 系统 SHALL 将内容持久化到该用户的职业画像快照，重新打开页面时 SHALL 展示已保存内容
