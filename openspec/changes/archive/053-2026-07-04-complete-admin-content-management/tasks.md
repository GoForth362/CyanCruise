## 1. OpenSpec

- [x] 1.1 创建 `complete-admin-content-management` change。
- [x] 1.2 编写 proposal、design、delta specs 和 tasks。
- [x] 1.3 执行 strict 校验。

## 2. 后端内容管理

- [x] 2.1 暴露 `/cc001/admin/content/save` 和 `/cc001/admin/content/delete` 自定义路由。
- [x] 2.2 为 Admin WebAPI 补齐删除入口。
- [x] 2.3 新增后台内容到用户资源 feed 的存储适配器。
- [x] 2.4 增加内容保存、删除、隐藏过滤和资源 feed 打通测试。

## 3. 前端内容管理

- [x] 3.1 补齐 `adminContentSave`、`adminContentDelete` endpoint 和 admin service 方法。
- [x] 3.2 将“内容审核”调整为“内容管理”。
- [x] 3.3 增加新增、编辑、保存、重置、置顶、隐藏、删除的页面交互。
- [x] 3.4 更新 route 映射、README 和静态资源版本号。

## 4. 验证

- [x] 4.1 执行 `openspec validate complete-admin-content-management --strict`。
- [x] 4.2 执行 `node webapp/isv/v620/cyancruise/validate-routes.js`。
- [x] 4.3 执行相关 Java 单元测试。
- [x] 4.4 执行 JDK 8 `.\gradlew.bat build`。
