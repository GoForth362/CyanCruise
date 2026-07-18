# 设计：升学中心数据隔离

## 数据边界

新增两张 PostgreSQL 表：

- `cc_study_center_selection`：按用户保存升学方向和目标院校。
- `cc_study_center_resource`：保存升学中心资讯卡片。

就业中心继续使用 `cc_admin_content` 和现有就业洞察链路；升学中心不读取该表。

## 接口

- `POST /cc001/study-center/selection/get`
- `POST /cc001/study-center/selection/save`
- `POST /cc001/study-center/insight/get`
- `POST /cc001/study-center/resources/list`

方向使用 `POSTGRADUATE`、`RECOMMENDATION`、`STUDY_ABROAD` 三个稳定值，前端仅显示考研、保研、留学。

## 前端

升学中心顶部提供方向下拉框与保存按钮。保存成功后刷新路线步骤、升学洞察和升学资讯；资讯卡片使用独立接口。
