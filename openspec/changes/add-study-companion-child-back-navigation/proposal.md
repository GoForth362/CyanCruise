## Why

考研陪伴、保研陪伴和留学陪伴的子功能页当前被配置为无父级路由，进入择校、复习、材料、导师联系、语言考试等页面后没有可见的返回操作，用户只能依赖浏览器或平台顶部菜单离开。

## What Changes

- 为考研陪伴的全部子功能页建立返回 `postgraduate` 的父级关系。
- 为保研陪伴的全部子功能页建立返回 `postgraduate-recommendation` 的父级关系。
- 为留学陪伴的全部子功能页建立返回 `study-abroad` 的父级关系。
- 在子功能页标题区右侧展示带具体目的地名称的中文返回按钮。
- 更新路由元数据和静态验证，防止子页面再次成为无法退出的孤立页面。

## Capabilities

### New Capabilities

- `study-companion-child-navigation`: 定义考研、保研、留学子功能页的父级路由和用户可见返回行为。

### Modified Capabilities

无。

## Impact

- 前端：`webapp/isv/v620/cyancruise/assets/navigation.js`、运行时路由映射和页面壳返回文案。
- 路由契约：`cyancruise-routes.json` 中 13 个升学子功能页的 `parentRoute`。
- 验证：`validate-routes.js` 增加三组父子路由和返回按钮校验。
- API、数据模型与数据库不变。
