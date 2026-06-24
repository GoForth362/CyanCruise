## 1. OpenSpec 与契约

- [x] 1.1 校验 `split-study-abroad-companion-pages` OpenSpec 文档
- [x] 1.2 更新 CareerLoop route metadata，声明五个留学子路由与对应 API 契约

## 2. 前端页面

- [x] 2.1 将 `study-abroad` 页面改为五个功能入口页
- [x] 2.2 新增 `study-abroad-profile` 页面，承载国家地区与申请画像表单和结果
- [x] 2.3 新增 `study-abroad-language` 页面，承载语言考试规划表单和结果
- [x] 2.4 新增 `study-abroad-school` 页面，承载选校定位表单和结果
- [x] 2.5 新增 `study-abroad-statement` 页面，承载文书主线表单和结果
- [x] 2.6 新增 `study-abroad-visa` 页面，承载签证网申表单和结果
- [x] 2.7 更新提交后的重渲染逻辑、入口文案、静态资源版本和必要样式

## 3. 验证与部署

- [x] 3.1 运行 `openspec validate split-study-abroad-companion-pages --strict`
- [x] 3.2 运行 `node --check webapp\isv\v620\cyancruise\assets\app.js`
- [x] 3.3 运行 `node webapp\isv\v620\cyancruise\validate-routes.js`
- [x] 3.4 使用 JDK 1.8 和仓库内 `gradlew.bat` 运行构建验证
- [x] 3.5 执行本地 `deployJar` 并同步静态资源到本地 Cosmic 运行目录
