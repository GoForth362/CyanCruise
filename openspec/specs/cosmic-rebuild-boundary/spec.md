# Cosmic 重构边界规格

## Purpose
定义在 Kingdee Cosmic/JDK 8 的 CyanCruise 项目中重构 IPD 行为时必须遵守的技术边界和架构边界。
## Requirements
### Requirement: 面向 Cosmic 重构业务行为
迁移 SHALL 面向 Kingdee Cosmic/JDK 8 目标重构 IPD 业务行为，而不是复制 Spring Boot、JPA、Flyway、Vue 或 uni-app 的实现细节。对于画像/onboarding，Lombok 注解、Spring controller/service、JPA repository、Jackson 特定注解、Java 17 API 和 uni-app 存储调用，SHALL 转换为 CyanCruise 兼容的 DTO、服务、WebAPI 方法、数据模型定义和 web 资源。

#### Scenario: 遇到源框架代码
- **WHEN** IPD 源行为使用 Spring Boot、JPA、Flyway、Vue 或 uni-app 细节实现
- **THEN** CyanCruise 设计提取业务规则，并选择 Cosmic 兼容的实现方式

#### Scenario: 迁移画像快照 DTO
- **WHEN** 迁移 IPD 的 `UserProfileSnapshot` DTO
- **THEN** CyanCruise DTO 使用普通的 JDK 8 兼容 Java 类和访问器，而不是 Lombok、Jackson 注解或仅 Java 17 支持的 API

### Requirement: 遵守 CyanCruise 模块边界
迁移后的实现 SHALL 将数据定义、共享契约、helper、业务逻辑、仅调试代码和 web 资源放在对应的 CyanCruise 模块中。

#### Scenario: 新增业务能力
- **WHEN** 某个 change 引入数据模型、服务、helper 或页面资源
- **THEN** 设计文档必须在代码变更前明确所属 CyanCruise 模块

### Requirement: 保持 JDK 8 兼容
迁移后的实现 SHALL 保持兼容 JDK 1.8 和仓库内 Gradle wrapper。画像/onboarding 代码 SHALL 避免使用 `List.of`、`Map.of`、`Optional.isEmpty`、`String.isBlank`、`var`、record、依赖更高 JDK 行为的 stream 写法，以及当前项目设置不支持的 Java time API。

#### Scenario: 新增或迁移 Java 代码
- **WHEN** 迁移过程中新增 Java 代码
- **THEN** 代码避免使用 Java 9+ API 和语言特性

#### Scenario: 转换 IPD helper 逻辑
- **WHEN** IPD 画像或快照 helper 逻辑使用 Java 11+ 方法
- **THEN** CyanCruise 实现将其替换为 JDK 8 兼容写法

### Requirement: 避免无控制地迁移依赖
迁移 SHALL NOT 仅因为 IPD 源项目存在某个依赖，就将该依赖加入 CyanCruise。

#### Scenario: 需要外部库
- **WHEN** 迁移设计提出新增依赖
- **THEN** 设计必须说明业务必要性、Cosmic 兼容性，以及为什么现有项目能力不足
