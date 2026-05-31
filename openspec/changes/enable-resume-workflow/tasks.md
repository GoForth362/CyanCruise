## 1. OpenSpec 审阅与准备

- [x] 1.1 确认 `proposal.md`、`design.md` 和 delta specs 均使用中文且范围符合“简历最小可用闭环”。
- [x] 1.2 运行 `openspec validate enable-resume-workflow --strict`，确保实现前规格合法。
- [x] 1.3 根据审阅意见更新 OpenSpec artifacts，并在审阅通过后再开始代码实现。

## 2. 简历页交互实现

- [ ] 2.1 在 `webapp/isv/v620/careerloop/assets/app.js` 中为 `resume` route 新增专用 `renderResumePage`，替代通用契约页渲染。
- [ ] 2.2 在简历页展示当前用户简历列表，覆盖有记录、空列表和列表加载失败状态。
- [ ] 2.3 新增简历创建表单，包含标题、目标岗位、文件 key 和解析内容字段。
- [ ] 2.4 让目标岗位默认取画像或工作台当前目标岗位，并允许用户为单份简历覆盖。
- [ ] 2.5 提交表单时通过现有 `post` helper 调用 `/cc001/resume/create`，保持 `apiMode=kapi` 路径兼容。
- [ ] 2.6 创建成功后刷新 `/cc001/resume/list`，并在简历页显示新记录和成功提示。
- [ ] 2.7 创建成功后尝试刷新 `/cc001/career-profile/snapshot/get`，并更新工作台“简历状态”摘要。
- [ ] 2.8 创建成功后停留在简历页，并提供“去简历诊断”入口，不自动跳转。
- [ ] 2.9 在身份缺失、KAPI token 失效或后端错误时显示局部可恢复状态，不使用硬编码 userId。

## 3. 文件能力可选增强

- [ ] 3.1 在简历创建表单中保留手工 fileKey 输入，确保不选择文件也能创建简历元数据。
- [ ] 3.2 增加可选小文件上传入口，成功时调用 `/cc001/files/upload` 并把返回 object key 填入 fileKey。
- [ ] 3.3 文件上传 unavailable、skipped、failed 或平台错误时，只显示文件能力不可用提示，不阻断 `/cc001/resume/create`。
- [ ] 3.4 为已有 fileKey 的简历记录提供预览入口，按需调用 `/cc001/files/preview-url`。
- [ ] 3.5 确认页面和 route metadata 不保存 token、Authorization header、预签名 URL 签名参数或客户私有凭据。

## 4. 元数据、校验与测试

- [ ] 4.1 如简历页消费的 endpoint 集合发生变化，更新 `careerloop-routes.json` 和 `validate-routes.js`。
- [ ] 4.2 运行 `node webapp\isv\v620\careerloop\validate-routes.js`。
- [ ] 4.3 运行 `node --check webapp\isv\v620\careerloop\assets\app.js`。
- [ ] 4.4 如修改 Java 后端或 KAPI router，运行相关 Gradle 聚焦测试；否则记录本 change 仅改静态 webapp。
- [ ] 4.5 使用 JDK 8 运行 `.\gradlew.bat clean build`。
- [ ] 4.6 本地通过 `index.htm?apiMode=kapi&access_token=<new-token>#resume` 验证创建、刷新、返回工作台和去诊断入口。
- [ ] 4.7 更新迁移地图或运行时文档，记录简历工作流验收方式、暂不迁移项和回滚方式。
