## Why

CareerLoop 已开始覆盖深造路径中的考研陪伴，但保研用户的核心问题不同：他们更需要竞争力诊断、信息战节奏、材料精修和导师联系准备。补齐保研能力可以让深造入口从占位页升级为面向本科生前三年持续准备的正式闭环。

## What Changes

- 新增保研陪伴能力，覆盖绩点排名监控、背景竞争力诊断、边缘背景提升、夏令营/预推免目标、文书润色和导师意向信。
- 新增 `/cc001/recommendation/*` WebAPI，提供竞争力诊断、材料润色、导师意向信生成和保研行动计划。
- 新增后端 DTO 与纯 Java helper service，当前提供规则兜底版个性化建议；未来 AI 接入后可替换润色和信件生成文本。
- 将 `postgraduate-recommendation` 从规划中入口升级为正式保研页面，保留用户可理解的中文表单、局部错误和结果展示。
- 更新静态路由元数据与资源版本，确保页面可从“深造”入口进入。

## Capabilities

### New Capabilities

- `postgraduate-recommendation-companion`: 定义保研全周期陪伴的竞争力诊断、背景提升、营校目标、材料精修、导师意向信和 WebAPI/页面契约。

### Modified Capabilities

- `webapp-careerloop-pages`: CareerLoop 页面集合将 `postgraduate-recommendation` 升级为正式保研陪伴页面。

## Impact

- 影响后端模块：
  - `code/base/v620-cc001-base-common/`：新增保研 DTO 与常量。
  - `code/base/v620-cc001-base-helper/`：新增保研规则与文书模板服务。
  - `code/cloud01/v620-cc001-cloud01-app01/`：新增保研 WebAPI，并注册到统一 WebAPI 插件。
- 影响前端模块：
  - `webapp/isv/v620/cyancruise/cyancruise-routes.json`
  - `webapp/isv/v620/cyancruise/index.html`
  - `webapp/isv/v620/cyancruise/assets/app.js`
  - `webapp/isv/v620/cyancruise/assets/styles.css`
- 不新增第三方依赖，继续兼容 JDK 1.8，并使用仓库内 `gradlew.bat` 验证。
