# Cosmic Datamodel Adapters Delta

## MODIFIED Requirements
### Requirement: PostgreSQL 替换当前业务状态文件存储
对于当前 CyanCruise 租户，PostgreSQL SHALL 作为已批准的业务状态数据库后端，替换本地 `filestorage` 业务状态适配器；未来 Cosmic datamodel adapter 仍可通过相同业务边界替换 PostgreSQL。

#### Scenario: PostgreSQL 不等待 Cosmic datamodel
- **WHEN** Cosmic datamodel 对象尚未建立，但 PostgreSQL 表结构和配置已就绪
- **THEN** 用户画像、职业计划、简历、简历诊断、模拟面试和助手聊天 SHALL 可通过 PostgreSQL 正常持久化

#### Scenario: 文件服务边界保持独立
- **WHEN** 页面需要上传、预览、下载或删除二进制文件
- **THEN** 该能力 SHALL 继续通过文件服务适配边界处理，不被误认为业务状态 `filestorage` 替换的一部分
