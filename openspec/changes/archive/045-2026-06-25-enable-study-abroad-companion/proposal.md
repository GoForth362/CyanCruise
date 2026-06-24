## Why

CyanCruise 已开始把深造路径从占位入口扩展为考研和保研陪伴，但留学用户还缺少国家地区选择、语言考试、软实力提升、选校定位、文书和签证网申的一体化规划。新增留学陪伴可以补齐深造三分支的完整体验。

## What Changes

- 新增留学陪伴能力，覆盖国家/地区选择、语言考试规划、软实力提升、选校定位、多语种文书和签证网申清单。
- 新增 `/cc001/study-abroad/*` WebAPI，提供留学画像诊断、语言备考计划、选校定位、文书黄金线和签证网申清单。
- 新增后端 DTO 与纯 Java helper service，当前提供规则兜底版建议；未来 AI 接入后增强雅思写作批改、口语对练和 PS 多轮挖掘。
- 将 `study-abroad` 从规划中入口升级为正式留学页面。
- 更新静态路由元数据与资源版本，确保页面可从“深造”入口进入。

## Capabilities

### New Capabilities

- `study-abroad-companion`: 定义留学全周期陪伴的画像输入、语言规划、选校定位、文书黄金线、签证网申和 WebAPI/页面契约。

### Modified Capabilities

- `webapp-careerloop-pages`: CareerLoop 页面集合将 `study-abroad` 升级为正式留学陪伴页面。

## Impact

- 影响后端模块：
  - `code/base/v620-cc001-base-common/`：新增留学 DTO 与常量。
  - `code/base/v620-cc001-base-helper/`：新增留学规则与文书模板服务。
  - `code/cloud01/v620-cc001-cloud01-app01/`：新增留学 WebAPI，并注册到统一 WebAPI 插件。
- 影响前端模块：
  - `webapp/isv/v620/cyancruise/cyancruise-routes.json`
  - `webapp/isv/v620/cyancruise/index.html`
  - `webapp/isv/v620/cyancruise/assets/app.js`
  - `webapp/isv/v620/cyancruise/assets/styles.css`
- 不新增第三方依赖，继续兼容 JDK 1.8，并使用仓库内 `gradlew.bat` 验证。
