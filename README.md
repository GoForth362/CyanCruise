# CyanCruise

> 基于金蝶云苍穹（Kingdee Cosmic）构建的 AI 职业发展与成长规划应用。

CyanCruise 面向求职与升学场景，围绕“认识自己、明确方向、持续行动、获得反馈”构建未来规划。项目将用户画像、职业测评、简历、今日任务、模拟面试、反馈与职业计划组织为连贯的业务流程，并探索 AI 智能体在职业规划和个性化建议中的实际应用。

本仓库同时也是一个金蝶云苍穹二次开发工程。

## 核心能力

- **职业画像**：沉淀目标岗位、个人背景、测评、简历和面试等成长信息。
- **职业测评**：通过结构化量表和评分规则辅助用户认识自身特征。
- **简历服务**：覆盖简历管理、文件上传预览、内容诊断与修改建议。
- **今日行动**：根据画像与当前准备状态，生成可执行的下一步任务。
- **模拟面试**：提供面试会话、过程记录和反馈报告的业务基础。
- **职业计划**：围绕目标岗位生成阶段计划，并与每日行动形成联动。
- **AI 助手**：通过可替换的 AI Provider、工具调用和规则兜底提供对话与分析能力。
- **平台治理**：适配 Cosmic 身份上下文、权限边界、WebAPI 和平台菜单挂载。

## 技术架构

| 层级 | 职责 | 主要位置 |
| --- | --- | --- |
| 数据模型 | Cosmic 业务对象与数据语义 | `datamodel/` |
| 公共契约 | DTO、常量和跨模块接口 | `code/base/v620-cc001-base-common/` |
| 领域能力 | 纯 Java 规则、工具和辅助服务 | `code/base/v620-cc001-base-helper/` |
| 应用实现 | 应用服务、平台适配器与 WebAPI | `code/cloud01/v620-cc001-cloud01-app01/` |
| 用户界面 | Cosmic 页面和前端资源 | `webapp/` |
| 调试工程 | 本地 Cosmic 调试入口 | `code/v620-cosmic-debug/` |
| 规格与决策 | OpenSpec proposal、spec、design 和 tasks | `openspec/` |

项目坚持 JDK 8 兼容，并通过接口隔离 Cosmic 平台能力、业务规则和外部 AI 服务。正式业务能力不放入调试工程或本地运行期目录。

## 开发环境

- JDK 1.8
- 仓库内置 Gradle Wrapper
- 金蝶云苍穹 / Cosmic 开发环境
- Node.js（仅用于部分 webapp 静态检查）
- OpenSpec CLI（用于规格校验）

本地 Cosmic 路径由 `gradle.properties` 中的 `systemProp.cosmic_home`、`systemProp.project_dir` 等配置提供。首次配置可参考：

```powershell
Copy-Item gradle.properties.example gradle.properties
Copy-Item debug-local.properties.example debug-local.properties
```

请勿提交本机路径、租户信息、API Key 或其他密钥。

## 构建与验证

在 Windows PowerShell 中确认使用 JDK 8，然后执行仓库自带的构建脚本：

```powershell
$env:JAVA_HOME = 'F:\kingdee\ENV\jdk'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat clean build
```

校验 Webapp 路由和脚本：

```powershell
node webapp\isv\v620\cyancruise\validate-routes.js
node --check webapp\isv\v620\cyancruise\assets\app.js
```

校验 OpenSpec 规格：

```powershell
openspec validate --all --strict
```

## 项目状态

CyanCruise 目前处于持续迁移与平台化验证阶段。核心领域规则、应用服务、WebAPI、AI Provider 边界和 Webapp 工作台已形成基础能力；真实租户中的最终数据模型、平台身份、文件服务、AI 配置及发布流程仍需结合目标 Cosmic 环境完成联调。

## License

本项目使用 [MIT License](LICENSE)。
