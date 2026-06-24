## Why

CareerLoop 当前主要覆盖就业与面试主循环，尚未为准备考研的学生提供从择校择专业、复习计划、刷题答疑到复试准备的连续支持。考研用户同样需要目标拆解、知识陪伴和阶段性反馈，因此需要在现有 CyanCruise 能力边界内补齐“考研全周期陪伴”入口。

## What Changes

- 新增考研陪伴能力，覆盖智能择校、动态复习计划、错题解析与复试准备提示。
- 新增 `/cc001/postgraduate/*` WebAPI，提供择校建议、复习计划生成、错题解析和复试准备清单。
- 新增后端 DTO 与纯 Java helper service，优先提供可解释的规则兜底结果；AI 可用时可用于增强文案，但结果不依赖外部服务才能使用。
- 在 `webapp/isv/v620/cyancruise` 新增“考研”页面入口和交互，用户可在同一页面完成画像输入、计划生成和错题解析。
- 更新静态路由元数据与资源版本，确保页面可从 CareerLoop 导航进入。

## Capabilities

### New Capabilities

- `postgraduate-exam-companion`: 定义考研全周期陪伴的用户输入、择校推荐、复习计划、错题解析、复试准备和 WebAPI/页面契约。

### Modified Capabilities

- `webapp-careerloop-pages`: CareerLoop 页面集合新增 `postgraduate` 路由和用户可理解的“考研”入口。

## Impact

- 影响后端模块：
  - `code/base/v620-cc001-base-common/`：新增考研 DTO 与常量。
  - `code/base/v620-cc001-base-helper/`：新增考研规则与 AI 辅助服务。
  - `code/cloud01/v620-cc001-cloud01-app01/`：新增考研 WebAPI，并注册到 WebAPI 插件。
- 影响前端模块：
  - `webapp/isv/v620/cyancruise/cyancruise-routes.json`
  - `webapp/isv/v620/cyancruise/index.html`
  - `webapp/isv/v620/cyancruise/assets/app.js`
  - `webapp/isv/v620/cyancruise/assets/styles.css`
- 不新增第三方依赖，继续兼容 JDK 1.8，并使用仓库内 `gradlew.bat` 验证。
