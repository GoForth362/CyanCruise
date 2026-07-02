## 1. 公共契约与数据模型
- [x] 1.1 在 `base-common` 新增深造方向、记录类型和状态常量，覆盖考研、保研、留学和归档/进行中/已完成等状态。
- [x] 1.2 在 `v620.cc001.base.common.dto.furtherstudy` 下集中管理深造 DTO，新增目标、记录摘要、记录详情、材料、历史事件、列表查询和状态更新等 DTO，并保持 JDK 1.8 兼容。
- [x] 1.3 为现有生成结果补充可选记录标识或包装响应方案，确保旧调用方仍能读取原有结果字段。
- [x] 1.4 检查新增面向用户的错误提示和状态说明，使用普通中文，不直接暴露内部枚举或接口名。
- [x] 1.5 将深造规则 helper 迁入 `v620.base.helper.furtherstudy`，与 `helper.career` 下的求职主循环规则分离。

## 2. 存储边界与 PostgreSQL 实现
- [x] 2.1 新增 `openspec/changes/persist-further-study-companion-records/sql/postgresql-further-study-storage.sql`，包含目标、记录、材料、历史事件表和必要索引，不包含破坏性 SQL、密码或本地路径。
- [x] 2.2 在 `cloud01` 新增 `FurtherStudyCompanionStorage` 接口，覆盖目标保存、记录保存、列表、详情、状态更新、材料保存和历史事件查询。
- [x] 2.3 新增 `InMemoryFurtherStudyCompanionStorage`，用于单元测试和显式本地夹具。
- [x] 2.4 新增 `PostgresqlFurtherStudyCompanionStorage`，复用 `PostgresqlStorageConfig`、`PostgresqlStorageSupport`、JDBC 与 Jackson。
- [x] 2.5 在 `CyanCruiseStorageFactory` 增加 `furtherStudyCompanionStorage()`，默认使用共享 `cc001.storage.*` 配置并在配置缺失时 fail fast。

## 3. 应用服务与 WebAPI 接入
- [x] 3.1 调整 `PostgraduateApplicationService`：生成择校、复习计划、错题解析和复试准备结果后保存考研记录，并新增历史查询和状态更新方法。
- [x] 3.2 调整 `RecommendationApplicationService`：保存竞争力诊断、行动计划、文书润色和导师意向信记录，并支持材料和导师联系状态更新。
- [x] 3.3 调整 `StudyAbroadApplicationService`：保存画像诊断、语言计划、选校定位、个人陈述主线和签证网申清单记录，并支持文书和清单状态更新。
- [x] 3.4 新增或扩展统一深造记录 WebAPI，提供当前用户的记录列表、详情、状态更新、材料保存和历史事件查询。
- [x] 3.5 更新 CyanCruise WebAPI 插件注册与 route metadata 中的后端契约说明，保持旧生成端点兼容。
- [x] 3.6 确保所有新增查询和更新路径都通过当前登录身份约束，不接受硬编码、猜测或缓存 userId。
- [x] 3.7 将 cloud01 深造 application service、storage 与 WebAPI 迁入 `mservice.furtherstudy` / `webapi.furtherstudy` 子包，保留统一自定义 WebAPI 插件作为跨域路由入口。

## 4. 测试与验证
- [x] 4.1 补充 helper、storage、application service 和 WebAPI 聚焦测试。
- [x] 4.2 运行 `openspec validate persist-further-study-companion-records --strict`。
- [x] 4.3 在 JDK 8 环境下运行 Gradle 构建或相关聚焦测试。
