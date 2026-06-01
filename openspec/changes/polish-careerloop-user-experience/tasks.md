## 1. 规格与范围确认

- [x] 1.1 运行 `openspec validate polish-careerloop-user-experience --strict`，确认 proposal/design/specs/tasks 合法。
- [x] 1.2 审阅 `careerloop-routes.json` 当前 route 列表，标记默认用户可见、debug 可见和 hash 直达可见的页面。
- [x] 1.3 确认本 change 仅做静态 webapp 体验收口，不新增后端 API、不新增依赖、不改变 `/cc001/*` 契约。

## 2. 导航与调试模式

- [x] 2.1 在 `assets/app.js` 中新增 `isDebugMode()` 或等价判断，支持 `?ccDebug=1`。
- [x] 2.2 调整导航渲染：默认隐藏 `file-upload-preview` 等调试或 entry-only 页面，debug 模式展示。
- [x] 2.3 保留 hash 直达隐藏 route 的能力，默认模式下给出可恢复提示或直接渲染但不加入导航。
- [x] 2.4 更新 `careerloop-routes.json` 的可见性 metadata，使 route map 能表达默认可见与 debug 可见。

## 3. 页面展示收口

- [x] 3.1 默认模式隐藏页面头部的 `Route:`、status chip、audience chip 和 entry-only chip。
- [x] 3.2 默认模式隐藏各页面“接口契约”面板，debug 模式继续展示。
- [x] 3.3 优化主循环页面默认文案，减少工程词汇，突出业务动作和下一步入口。
- [x] 3.4 保持 identity-required、forbidden、backend-error 等错误状态可理解且可恢复。

## 4. 简历页体验打磨

- [x] 4.1 调整简历页布局顺序，使简历记录、创建表单、上传入口和去诊断入口更符合用户流程。
- [x] 4.2 默认模式弱化或隐藏 fileKey 的工程暴露，debug 模式显示完整 fileKey 和接口信息。
- [x] 4.3 预览失败时展示业务化错误文案，不暴露 `127.0.0.1`、签名 URL 或平台内部异常。
- [x] 4.4 删除简历和刷新列表后保持工作台摘要一致，避免已删除记录继续显示为当前简历。

## 5. 文档、同步与验证

- [x] 5.1 更新 README 或迁移文档，记录 `?ccDebug=1` 调试模式和隐藏文件上传预览入口的原因。
- [x] 5.2 运行 `node webapp\isv\v620\careerloop\validate-routes.js`。
- [x] 5.3 运行 `node --check webapp\isv\v620\careerloop\assets\app.js`。
- [x] 5.4 如仅改静态资源，记录无需 Gradle；如触及 Java，使用 JDK 8 运行相关 Gradle 测试或 `.\gradlew.bat clean build`。
- [x] 5.5 同步静态文件到 `F:\kingdee\ENV\static-file-service\isv\v620\careerloop\` 并本地验证默认模式与 `?ccDebug=1` 模式。

## 6. 功能中心首页重规划

- [ ] 6.1 定义默认工作台的业务分组和功能卡片清单，至少包含“简历”“面试”，并为每张卡片标注目标 route、图标、标题、说明和是否已接入。
- [ ] 6.2 将默认 `#workbench` 从大 hero + 横向 route 导航 + 状态卡布局，调整为左侧分组导航 + 右侧功能卡片矩阵。
- [ ] 6.3 默认模式隐藏顶部大块身份卡、横向滚动 route 清单、Route/status chip 和接口契约；`?ccDebug=1` 模式保留调试入口。
- [ ] 6.4 为功能卡片增加稳定响应式样式：桌面 3 至 4 列，窄屏折叠为单列或顶部分组条，避免文字溢出和横向滚动。
- [ ] 6.5 已接入卡片跳转到现有 route；未接入卡片展示禁用态或“即将接入”提示，不暴露接口路径。
- [ ] 6.6 更新静态资源版本号并同步到 `F:\kingdee\ENV\static-file-service\isv\v620\careerloop\`。
- [ ] 6.7 运行 `node webapp\isv\v620\careerloop\validate-routes.js`、`node --check webapp\isv\v620\careerloop\assets\app.js` 和 `openspec validate polish-careerloop-user-experience --strict`。
