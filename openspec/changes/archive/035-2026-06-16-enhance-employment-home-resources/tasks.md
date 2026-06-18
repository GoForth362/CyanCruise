## 1. OpenSpec

- [x] 1.1 创建 `enhance-employment-home-resources` change。
- [x] 1.2 编写中文 proposal、design、delta spec 和 tasks。
- [x] 1.3 执行 `openspec validate enhance-employment-home-resources --strict`。

## 2. 后端资源内容

- [x] 2.1 扩充 `InMemoryCareerResourceStorage` 默认资源卡，加入公开就业服务、招聘入口、职业指导文章和求职提示。
- [x] 2.2 确认默认资源卡不包含数据库密码、本地路径或运行时爬虫假设。
- [x] 2.3 更新或补充资源 feed 测试，覆盖文章/视频/提示/职业路径输出。

## 3. 就业首页前端

- [x] 3.1 为 `employment-home` 增加资源和就业洞察状态加载逻辑。
- [x] 3.2 在就业首页渲染工具区、资讯文章区、服务入口区和洞察摘要区。
- [x] 3.3 实现资源 API 失败或空内容时的页面内降级状态。
- [x] 3.4 更新样式和静态资源版本号，保持 CyanCruise 风格和移动端可读性。
- [x] 3.5 将规则版就业路线图调整为就业首页首个内容区，并将就业工具区移动到页面后部。
- [x] 3.6 将就业洞察区调整为用户画像信息 + 简历摘要展示，并处理历史英文状态/说明的中文化兜底。
- [x] 3.7 将就业资源区调整为公共服务、精选文章、相关视频顺序，并让“查看资源”直达外部平台来源。
- [x] 3.8 将用户画像中的学校和专业拆成独立输入、独立草稿字段，并兼容旧 `schoolMajor` 数据。

## 4. 验证

- [x] 4.1 执行 `node --check webapp/isv/v620/cyancruise/assets/app.js`。
- [x] 4.2 执行 `node webapp/isv/v620/cyancruise/validate-routes.js`。
- [x] 4.3 在 JDK 8 下执行仓库内 `.\gradlew.bat test` 或更高强度构建。
