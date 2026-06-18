# CyanCruise 开发约定

本仓库使用 OpenSpec 进行规格驱动开发。涉及 IPD 迁移或业务能力重构时，先创建或更新 `openspec/changes/<change-id>/` 下的 proposal、spec、design、tasks，再进行代码实现。

## OpenSpec 文档与归档要求

- 提供给用户审查的 OpenSpec 文档必须默认使用中文；`SHALL`、`WHEN`、`THEN` 等 OpenSpec 关键词、代码标识、配置项和路径可保留英文。
- OpenSpec change 归档目录必须带三位顺序号前缀，格式为 `NNN-YYYY-MM-DD-<change-id>`，例如 `034-2026-06-14-connect-career-profile-postgresql-storage`。
- 归档前必须扫描 `openspec/changes/archive/` 中已有三位数字前缀目录，取最大序号加 1；不得使用无序号的 `YYYY-MM-DD-<change-id>` 归档目录名。

## 项目约束

- 本项目是 Kingdee Cosmic 二开工程，必须兼容 JDK 1.8。
- 构建应使用仓库内的 `gradlew.bat`，不要依赖全局 Gradle。
- `systemProp.cosmic_home`、`systemProp.project_dir` 等本地路径来自 `gradle.properties`，不得在业务代码中硬编码替代路径。
- 新增依赖必须说明必要性，并确认不破坏 Cosmic/KDDT 模板约束。
- 面向用户的页面、按钮、提示、空状态和文档说明必须使用普通用户能理解的中文表达，避免直接使用 `JD`、`STAR` 等专业缩写或行业黑话；确需保留代码标识时，应在用户可见文案中写成“岗位要求”“经历讲述框架”等清晰说法。

## 模块边界

- `datamodel/`：数据模型与业务对象定义。
- `code/base/v620-cc001-base-common/`：通用模型、常量、公共契约。
- `code/base/v620-cc001-base-helper/`：通用工具和辅助能力。
- `code/cloud01/v620-cc001-cloud01-app01/`：业务应用实现。
- `code/v620-cosmic-debug/`：调试工程，不承载正式业务能力。
- `webapp/`：页面、前端资源和平台相关展示资产。
- `script_modules/`、`local_dataentity/`、`filestorage/`、`logs/`：本地生成或运行期目录，默认不作为迁移实现入口。

## IPD 迁移规则

- IPD 项目位置：`F:\Project\IPD`。
- 迁移目标是业务规则、数据语义、流程和接口契约，不直接搬迁 Spring Boot、JPA、Flyway、Vue 或 uni-app 实现。
- 每个迁移 change 必须记录 IPD 来源路径、CyanCruise 目标模块、数据映射、暂不迁移项和验证方式。
- 优先迁移 CareerLoop 主循环：目标岗位、用户画像/测评、简历、今日任务、模拟面试、反馈、职业计划。

## 验证命令

在 Windows PowerShell 中构建前确认 JDK 8：

```powershell
$env:JAVA_HOME = 'F:\kingdee\ENV\jdk'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat clean build
```

OpenSpec change 校验：

```powershell
openspec validate <change-id> --strict
```

