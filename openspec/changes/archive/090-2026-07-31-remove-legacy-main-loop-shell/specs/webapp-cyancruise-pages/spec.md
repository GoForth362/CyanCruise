## ADDED Requirements

### Requirement: 功能页首屏不展示历史入口壳
CyanCruise webapp SHALL 在浏览器首次可绘制时隐藏仅供调试使用的入口头部、页面内导航和状态区。普通用户打开任一受支持功能页时，页面 SHALL NOT 在目标功能页渲染前短暂展示“求职主循环”、旧主循环状态卡片或其它历史 CareerLoop 入口内容。

#### Scenario: 普通用户直接打开功能页
- **WHEN** 普通用户不带 `ccDebug=1` 直接打开任一受支持的 CyanCruise 功能路由
- **THEN** 浏览器首次可见内容 SHALL NOT 包含历史入口头部、页面内调试导航或旧状态卡片
- **AND** 页面 SHALL 在身份与运行脚本准备完成后渲染 URL 指定的功能页

#### Scenario: 运行脚本加载较慢
- **WHEN** `app-runtime.js` 尚未下载完成或尚未执行
- **THEN** 静态 HTML 与首屏 CSS SHALL 保持调试入口壳不可见
- **AND** 页面 SHALL 只展示通用加载状态，不得展示历史业务页面

#### Scenario: 开发者显式打开调试模式
- **WHEN** 开发者使用 `?ccDebug=1` 打开 CyanCruise
- **THEN** 运行脚本 MAY 恢复身份调试、页面导航和状态信息
- **AND** 调试区域 SHALL 使用 CyanCruise 当前文案，不得把当前应用称为 CareerLoop 或“求职主循环”

### Requirement: 清理历史壳不得破坏现有功能
历史入口壳清理 SHALL 保留现有路由解析、页面挂载、身份解析、显式开发身份输入、WebAPI 调用和功能页返回路径。该清理 SHALL NOT 修改 `cyancruise-routes.json` 中的业务路由或 `/cc001/*` 接口契约。

#### Scenario: 通过 URL 打开现有功能页
- **WHEN** 用户通过 `ccRoute`、hash 或平台外部链接打开现有功能页
- **THEN** CyanCruise SHALL 继续解析并渲染同一目标路由
- **AND** 页面 SHALL 继续使用现有身份和 route metadata 调用对应 WebAPI

#### Scenario: 调试模式输入开发身份
- **WHEN** 开发者以显式调试和开发身份模式打开页面
- **THEN** 现有身份输入控件和加载操作 SHALL 保持可用

#### Scenario: 执行静态验证
- **WHEN** 执行 `node webapp\isv\v620\cyancruise\validate-routes.js`
- **THEN** 验证 SHALL 检查默认首屏隐藏规则、历史文案不存在、既有路由映射和页面挂载点仍然完整
