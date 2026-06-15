## 为什么

CyanCruise 已经把用户画像存储接入 PostgreSQL，但职业计划、简历记录、简历诊断、面试会话和 AI 助手会话等核心业务状态仍然会在运行期写入本地 `filestorage/`。这会导致 Kingdee Cosmic 的不同启动方式、不同开发机或调试环境看到的数据不一致，也不利于团队共同验证同一个 PostgreSQL 后端状态。

## 改动内容

- **破坏性变更**：将 CyanCruise 用户画像、职业计划、简历记录、简历诊断、面试会话/消息、AI 助手会话/消息的运行期业务状态适配器替换为 PostgreSQL 适配器。
- **破坏性变更**：上述业务模块的默认应用构造器 SHALL 不再静默回退到本地 `filestorage`。缺少 PostgreSQL 配置时 SHALL 以清晰配置错误快速失败。
- 新增共享 PostgreSQL 存储配置和工厂模式，统一使用 URL、schema、username、password 和手工 DDL 策略。
- 新增可手工执行的 PostgreSQL DDL，覆盖新增表和索引。脚本 SHALL NOT 包含密码、本地路径或破坏性 DDL。
- 文件附件、预览、下载、BOS/OSS 平台文件能力不属于本次替换范围。简历记录仍 MAY 保存稳定的 `fileKey`，但该二进制文件服务不在本 change 中替换。
- 单元测试仍可保留内存测试替身，但运行期默认路径 SHALL 移除对 `filestorage/...` 的依赖。

## 能力范围

### 新增能力

- `postgresql-business-storage`：定义 CyanCruise 跨模块业务状态的 PostgreSQL 存储策略、配置、表归属、DDL 和验证要求。

### 修改能力

- `postgresql-profile-storage`：移除运行期用户画像文件回退，并对齐共享 PostgreSQL 配置策略。
- `career-plan-summary`：职业计划记录改为通过 PostgreSQL 持久化，不再使用 `filestorage/career-plan`。
- `resume-core`：简历记录改为通过 PostgreSQL 持久化，不再使用 `filestorage/resume-core`。
- `resume-diagnosis`：诊断结果和关键词状态改为通过 PostgreSQL 持久化，不再使用 `filestorage/resume-diagnosis`。
- `interview-core`：面试会话和消息改为通过 PostgreSQL 持久化，不再使用 `filestorage/interview-core`。
- `assistant-chat`：AI 助手会话和消息改为通过 PostgreSQL 持久化，不再使用 `filestorage/assistant-chat`。
- `cosmic-datamodel-adapters`：记录 PostgreSQL 是当前 CyanCruise 业务状态适配器的批准后端，同时保留未来替换为 Cosmic 数据模型适配器的空间。
- `migration-governance`：要求本次迁移记录排除的文件服务范围、凭据处理和验证命令。

## 影响范围

- 受影响模块：
  - `code/cloud01/v620-cc001-cloud01-app01/`：应用服务、存储适配器、存储工厂、测试、PostgreSQL DDL。
  - `openspec/specs/`：存储、画像、计划、简历、诊断、面试、助手、数据模型和治理规格。
  - `gradle.properties.example` 和根 Gradle 测试属性转发。
- 数据库：
  - 只使用 PostgreSQL，目标为 `jdbc:postgresql://10.0.0.8:5432/cyancruise`，schema 为 `public`，应用用户为 `cyancruise_app`。
  - 密码 SHALL NOT 提交或硬编码。
  - 默认保持手工 DDL。除非另有明确、非破坏性设计批准，程序 SHALL NOT 自动建表。
- 验证：
  - OpenSpec strict 校验。
  - JDK 8 下执行 `.\gradlew.bat clean build`。
  - 聚焦验证跨实例重载、用户隔离、删除、排序、ID 分配和运行期不创建业务 `filestorage`。
