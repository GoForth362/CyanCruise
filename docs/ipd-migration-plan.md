# IPD 到 CyanCruise 迁移计划

## 现状判断

旧项目路径：`F:\Project\IPD`

旧项目不是单一 Maven 工程，而是一个全栈项目：

- `backend`：Spring Boot 3.5.3 / Java 17 / Maven 后端。
- `frontend`：uni-app / Vue 3 小程序与 H5 前端。
- `admin-frontend`：Vue 3 / Element Plus 管理端。
- `body-lang-sidecar`：独立侧车模块，待后续确认语言和运行方式。

当前项目 `CyanCruise` 是金蝶云苍穹 Cosmic Gradle 多模块二开工程，当前仍按 Java 8 编译，业务模块基本还是模板骨架。

## 旧后端能力

`F:\Project\IPD\backend` 当前是职业规划产品 `CareerLoop` 后端，主要能力包括：

- 用户、认证、角色、权限。
- 职业测评、职业路径、职业规划。
- 简历上传、简历诊断、简历生成、PDF 处理。
- 模拟面试、面试报告、题库。
- AI 职业规划 Agent、AI 对话、函数调用工具。
- 通知、反馈、打卡、用户画像。
- 管理端审计、数据初始化、定时任务。
- Redis、Flyway、JPA、MySQL/H2、Aliyun OSS、DashScope、邮件、RSS。

源码规模初步统计：

- Java 文件约 206 个。
- Controller 约 26 个。
- Entity 约 38 个。
- Repository 约 38 个。
- Service/Impl 约 60+ 个。
- Flyway SQL 迁移 16 个。

## 不能直接整包搬迁的原因

1. 运行容器不同

旧项目是独立 Spring Boot 应用，通过 `CareerApplication` 启动。
苍穹项目通过 Cosmic 容器和 `DebugApplication` 启动，业务代码应以苍穹插件、服务、WebAPI、操作插件等方式接入。

2. 运行模型和依赖生态仍然不同

旧项目要求 Java 17，当前苍穹工程用 Java 8 编译。
需要解决语言版本问题，消除 Spring Boot 3 / Jakarta / JPA 与苍穹运行模型之间的差异。
迁移时仍然要避免把 Spring Boot 应用入口、Controller、Repository、JPA Entity 原样搬进苍穹工程。

3. 数据访问方式不同

旧项目大量使用 Spring Data JPA Repository 和 JPA Entity。
苍穹项目通常应优先使用苍穹 BOS 的数据模型、动态对象、服务工具和平台事务能力。

4. Web API 方式不同

旧项目的 `@RestController` 不能原样作为苍穹接口层。
需要改造成苍穹 WebAPI、表单插件、操作插件或服务插件入口。

5. 依赖冲突风险较高

Spring Boot、JPA、Redis、JWT、Flyway、PDF、OSS、DashScope 等依赖需要逐个确认是否允许进入苍穹运行时。

## 推荐迁移顺序

### 第 1 阶段：抽取低耦合公共代码

目标：先把不依赖 Spring Boot、JPA、Controller 的代码迁进来，建立可编译基础。

优先候选：

- `common/ErrorCode.java`
- `common/Result.java`
- 部分 DTO
- 部分纯工具类
- AI/职业规划中的纯规则计算逻辑

落点：

- 公共返回、错误码、DTO：`code/base/v620-cc001-base-common`
- 工具类、适配器：`code/base/v620-cc001-base-helper`

注意：

- Lombok 先不要直接引入，优先改成普通 Java Bean，降低苍穹运行时风险。
- 包名建议改成 `v620.cc001...`，和当前项目命名保持一致。

### 第 2 阶段：迁移核心业务规则

建议先迁 `CareerAgentServiceImpl` 里的“今日任务/职业准备度/下一步建议”规则。

原因：

- 这是旧项目产品闭环的核心。
- 相比用户、认证、JPA、文件上传，它更容易从 Spring Boot 中剥离。
- 可以先做成一个纯 Java 规则服务，不急着接数据库。

目标形态：

- 输入：用户画像、测评摘要、简历摘要、面试摘要、打卡状态。
- 输出：今日建议、风险原因、下一步行动。
- 先用普通 POJO 和接口，后续再接苍穹数据模型。

### 第 3 阶段：设计苍穹数据模型

把旧项目的 JPA Entity/Flyway SQL 转换为苍穹数据模型。

优先模型：

- 用户职业画像
- 测评记录
- 简历摘要/诊断结果
- 面试记录/报告
- Agent 任务/状态/事件

不建议把 38 个 Entity 一次性全搬。
应该围绕第一个可运行业务闭环逐个建模。

### 第 4 阶段：改造接口入口

旧项目 Controller 需要按苍穹方式重写入口：

- 面向页面：表单插件、操作插件。
- 面向外部调用：苍穹 WebAPI。
- 面向后台逻辑：服务插件或普通服务类。

旧 Controller 可以作为接口语义参考，不应直接复制。

### 第 5 阶段：处理外部能力

逐个确认以下能力在苍穹运行环境中的接入方式：

- DashScope / 通义千问
- Aliyun OSS
- PDFBox / OpenHTMLtoPDF
- Redis
- 邮件
- 定时任务
- JWT / 权限

## 第一个建议迁移目标

建议先迁移“AI 职业规划 Agent 的今日建议规则”。

第一步只做纯 Java 版本：

- 不接数据库。
- 不接 Spring。
- 不接 Controller。
- 不接前端。
- 用测试或简单调用构造输入数据，验证输出是否正确。

对应旧代码入口：

- `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\CareerAgentServiceImpl.java`
- `F:\Project\IPD\backend\src\main\java\com\group1\career\model\dto\CareerAgentTodayDto.java`
- `F:\Project\IPD\backend\src\main\java\com\group1\career\model\dto\UserProfileSnapshot.java`

建议迁移到：

- `code/base/v620-cc001-base-common/src/main/java/v620/cc001/base/common/dto`
- `code/base/v620-cc001-base-helper/src/main/java/v620/base/helper/career`

## 下一步执行清单

- [x] 读取 `CareerAgentServiceImpl` 完整逻辑和相关 DTO。
- [x] 分离纯规则输入/输出模型。
- [x] 在 `base-common` 新增 DTO。
- [x] 在 `base-helper` 新增规则服务。
- [x] 移除 Lombok、Spring、JPA 依赖。
- [x] 编译当前 Gradle 工程。
- [x] 增加苍穹 WebAPI 薄入口，先暴露纯规则服务。
- [x] 增加应用服务和数据源接口，隔离规则层与未来 BOS 数据读取。
- [x] 梳理 Career Agent 最小数据模型。
- [ ] 再决定是否接入表单插件、操作插件或真实数据模型。

## 已完成迁移落点

- `code/base/v620-cc001-base-common/src/main/java/v620/cc001/base/common/dto/career/CareerAgentTodayDto.java`
- `code/base/v620-cc001-base-common/src/main/java/v620/cc001/base/common/dto/career/CareerAgentRuleInput.java`
- `code/base/v620-cc001-base-common/src/main/java/v620/cc001/base/common/dto/career/UserProfileSnapshot.java`
- `code/base/v620-cc001-base-helper/src/main/java/v620/base/helper/career/CareerAgentTodayRuleService.java`
- `code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/mservice/CareerAgentRuleInputSource.java`
- `code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/mservice/CareerAgentRuleInputSourceUnavailable.java`
- `code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/mservice/CareerAgentTodayApplicationService.java`
- `code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/webapi/CareerAgentWebApi.java`
- `docs/career-agent-data-model.md`

本轮只迁移 `CareerAgentServiceImpl#getToday` 的纯规则决策链。未迁移旧系统中的 Spring 事务、Repository、任务同步、Agent 状态刷新、LLM reason 生成、旧 Controller 语义改造。

WebAPI 入口目前只做 `CareerAgentRuleInput` 到 `CareerAgentTodayRuleService` 的透传，用于验证苍穹侧调用链。它不读取苍穹数据模型，也不写入 Agent 状态。

数据适配边界已预留在 `CareerAgentRuleInputSource`。当前默认实现会明确提示 BOS 数据模型尚未配置，避免在模型未创建前伪造数据读取逻辑。

验证结果：

- `.\gradlew.bat build` 通过。
- `.\gradlew.bat clean build` 未完成，原因是现有 `build/libs/*.jar` 被本机进程占用，Gradle 无法删除旧构建目录；这不是新增代码编译错误。
- `.\gradlew.bat :v620-cc001-cloud01-app01:compileJava` 通过，WebAPI 薄入口可编译。
