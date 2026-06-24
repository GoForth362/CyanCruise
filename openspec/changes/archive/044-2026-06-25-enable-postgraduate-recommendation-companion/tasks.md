## 1. OpenSpec 与契约

- [x] 1.1 校验 `enable-postgraduate-recommendation-companion` OpenSpec 文档满足中文、场景和能力命名要求
- [x] 1.2 更新 CareerLoop route metadata，声明 `postgraduate-recommendation` 正式路由和 `/cc001/recommendation/*` API 契约

## 2. 后端能力

- [x] 2.1 在 `base-common` 新增保研请求、结果、条目和常量 DTO
- [x] 2.2 在 `base-helper` 新增保研陪伴服务，生成竞争力诊断、行动计划、文书润色和导师意向信
- [x] 2.3 在 `cloud01` 新增保研 WebAPI 并注册到 CareerLoop WebAPI 插件

## 3. 前端页面

- [x] 3.1 将深造页“保研”入口升级为“保研陪伴”
- [x] 3.2 实现 `postgraduate-recommendation` 页面表单、局部状态、API 调用和结果展示
- [x] 3.3 更新静态资源版本和样式，确保普通中文文案、移动端可读和 debug 面板隔离

## 4. 验证与部署

- [x] 4.1 运行 `openspec validate enable-postgraduate-recommendation-companion --strict`
- [x] 4.2 运行 `node webapp\isv\v620\cyancruise\validate-routes.js` 与 `node --check webapp\isv\v620\cyancruise\assets\app.js`
- [x] 4.3 使用 JDK 1.8 和仓库内 `gradlew.bat` 运行构建验证
- [x] 4.4 执行本地 `deployJar` 并同步静态资源到本地 Cosmic 运行目录
