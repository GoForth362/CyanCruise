## ADDED Requirements

### Requirement: 简历页面可交互工作流
CareerLoop webapp 的 `resume` route SHALL 提供可交互的简历工作流，而不只是展示接口契约。页面 SHALL 展示当前用户的简历记录列表、简历创建表单、提交状态和恢复性错误信息，并继续遵守 `careerloop-routes.json` 中声明的身份、API 和 fallback 约束。

#### Scenario: 打开简历页加载记录
- **WHEN** 已解析 Cosmic userId 的用户打开 `index.html#resume`
- **THEN** 页面 SHALL 调用 `/cc001/resume/list` 并展示该用户的简历记录、空列表状态或可恢复错误状态

#### Scenario: 身份缺失时阻止调用
- **WHEN** 用户打开 `resume` route 但页面无法解析生产身份或显式开发身份
- **THEN** 页面 SHALL 显示 identity-required 状态，并 SHALL NOT 使用硬编码、猜测或上一次缓存的 userId 调用 `/cc001/resume/*`

#### Scenario: 简历页保留导航
- **WHEN** 简历列表、创建或文件相关 API 调用失败
- **THEN** 页面 SHALL 保留工作台返回入口、简历表单和已加载数据，并显示局部可恢复错误而不是整页不可用

### Requirement: 简历创建表单
CareerLoop webapp 的简历页 SHALL 提供创建简历记录的表单。表单 SHALL 支持标题、目标岗位、文件 key 和解析内容字段；目标岗位 SHALL 默认使用画像或工作台当前目标岗位，但用户 MUST 能为本次简历单独覆盖。

#### Scenario: 默认目标岗位
- **WHEN** 用户已在 onboarding 或画像偏好中保存目标岗位，并打开简历创建表单
- **THEN** 表单 SHALL 默认填入该目标岗位

#### Scenario: 覆盖单份简历目标岗位
- **WHEN** 用户在简历创建表单中修改目标岗位并提交
- **THEN** 页面 SHALL 将修改后的目标岗位作为 `/cc001/resume/create` 请求的 `targetJob` 字段提交

#### Scenario: 创建简历记录
- **WHEN** 用户填写简历标题和可选文件 key、解析内容后提交表单
- **THEN** 页面 SHALL 调用 `/cc001/resume/create`，并在成功后刷新 `/cc001/resume/list`

#### Scenario: 创建成功后不自动跳转
- **WHEN** 简历记录创建成功
- **THEN** 页面 SHALL 停留在简历页，展示成功提示和“去简历诊断”入口，而 SHALL NOT 自动跳转到诊断页

### Requirement: 简历创建后的工作台刷新
CareerLoop webapp SHALL 在简历创建成功后尽量刷新职业画像快照和工作台摘要，使“简历状态”体现系统真实保存的简历记录。画像刷新失败 SHALL NOT 回滚已创建的简历记录。

#### Scenario: 创建后刷新画像
- **WHEN** `/cc001/resume/create` 成功并且 `/cc001/resume/list` 刷新完成
- **THEN** 页面 SHALL 尝试调用 `/cc001/career-profile/snapshot/get` 并更新工作台卡片中的简历状态

#### Scenario: 画像刷新失败
- **WHEN** 简历创建成功但画像快照刷新失败
- **THEN** 页面 SHALL 保留简历列表刷新结果并提示画像稍后刷新，且 SHALL NOT 把创建成功显示为失败
