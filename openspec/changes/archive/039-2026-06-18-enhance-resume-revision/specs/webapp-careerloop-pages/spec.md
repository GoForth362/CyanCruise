## ADDED Requirements

### Requirement: 简历诊断页面闭环
`resume-diagnosis` route SHALL 作为简历诊断工作台展示。页面 SHALL 支持读取已有简历列表、选择简历、读取画像默认目标岗位、填写目标岗位要求、触发诊断、展示诊断结果和结构化诊断建议，并提供再次诊断入口。

#### Scenario: 选择已有简历诊断
- **WHEN** 用户打开 `resume-diagnosis` 页面且存在真实简历记录
- **THEN** 页面展示简历选择、目标岗位/岗位要求 输入和触发诊断按钮

#### Scenario: 无真实简历记录
- **WHEN** 用户没有真实简历记录
- **THEN** 页面提示先创建或上传简历，并提供跳转到 `resume` route 的入口

#### Scenario: 展示建议清单
- **WHEN** 诊断返回结构化诊断建议
- **THEN** 页面按优先级展示建议项、证据、目标关键词和示例改写方向

#### Scenario: 再次诊断
- **WHEN** 用户查看建议后选择再次诊断
- **THEN** 页面复用当前 resumeId、目标岗位和岗位要求 重新调用诊断入口

### Requirement: 页面保持 CyanCruise 风格和可恢复状态
简历诊断页面 SHALL 沿用现有 CyanCruise 静态页面风格、状态提示、卡片密度、按钮样式和 KAPI 调用 helper。身份缺失、后端错误、AI unavailable、文件预览 unavailable 或列表为空 SHALL 显示局部可恢复状态，而不是整页崩溃。

#### Scenario: 身份缺失
- **WHEN** 页面无法解析当前用户身份
- **THEN** 页面显示身份缺失提示，并不使用硬编码 userId 发起诊断

#### Scenario: 后端错误
- **WHEN** `/cc001/resume-diagnosis/analyze` 返回错误
- **THEN** 页面保留用户已填写的目标岗位和岗位要求，并显示可重试提示

### Requirement: 静态资源版本更新
当 `index.html`、`assets/app.js` 或 `assets/styles.css` 因简历诊断页面发生变更时，系统 SHALL 更新静态资源版本号，并在交付说明中声明是否需要重新部署静态资源。

#### Scenario: 修改 app.js
- **WHEN** 本 change 修改 `webapp/isv/v620/cyancruise/assets/app.js`
- **THEN** `index.html` 中引用的静态资源版本号 SHALL 更新，避免部署后继续命中旧缓存
