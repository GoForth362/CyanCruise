# study-center Specification

## Purpose
TBD - created by archiving change add-study-center-isolation. Update Purpose after archive.
## Requirements
### Requirement: 保存具体升学方向
系统 SHALL 允许用户在升学中心选择考研、保研或留学，并持久保存该选择。

#### Scenario: 用户保存方向
- **当** 用户选择“保研”并保存
- **那么** 系统 SHALL 仅更新该用户的升学中心选择
- **并且** 升学中心路线图 SHALL 显示保研准备步骤

### Requirement: 升学数据与就业数据隔离
系统 SHALL 从独立的升学中心数据存储读取升学资讯，且不得读取就业中心内容作为升学资讯。

#### Scenario: 展示升学资讯
- **当** 用户进入升学中心
- **那么** 系统 SHALL 展示升学公共服务、精选文章和相关视频
- **并且** 内容 SHALL 来自升学中心资源表

### Requirement: 升学资讯独立发布管理
系统 SHALL 为升学中心维护独立的资讯资源，不得复用就业中心的内容记录。

#### Scenario: 管理员发布升学资讯
- **WHEN** 管理员在内容管理中选择“升学资讯”并保存文章、视频或官方服务
- **THEN** 系统 SHALL 将内容保存到升学资讯专用表，并支持置顶、发布、隐藏和删除
- **AND** 未发布或隐藏的内容不得出现在用户端升学中心

