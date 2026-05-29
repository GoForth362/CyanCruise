## Why

CareerLoop 主循环、webapp、身份、文件和 AI provider 的关键迁移已经具备生产 adapter 或平台挂载边界，但目前生产发布证据分散在迁移地图、route metadata 和各 change 文档中，缺少一份可执行的 CyanCruise/Cosmic 上线前检查清单。

本 change 迁移 IPD 生产配置、健康探针、部署/回滚、备份恢复、监控告警和发布验证语义，形成适配 Kingdee Cosmic/JDK 8 工程的生产就绪门禁，避免把 Docker/Spring/Vue 运行时直接搬入 CyanCruise。

## What Changes

- 新增生产就绪检查清单规格，覆盖环境配置、密钥不入库、Cosmic 身份、文件 adapter、AI provider、datamodel/存储、webapp/KDDT 挂载、健康检查、日志诊断、监控告警、备份恢复、回滚和发布证据。
- 新增 `docs/careerloop-production-readiness-checklist.md`，按自动化检查、本地构建、租户手工验证、发布前阻塞项和回滚步骤组织。
- 在 `webapp/isv/v620/careerloop/careerloop-routes.json` 增加 secret-free readiness metadata，明确哪些能力可自动验证，哪些必须在客户 Cosmic 租户手工核查。
- 在 `docs/ipd-to-cyancruise-migration-map.md` 回填 IPD 来源、CyanCruise 目标、暂不迁移项和验证方式。
- 修改 `migration-governance`，要求实现型迁移归档/发布前保留生产就绪证据，且明确区分本地自动化通过与租户手工待验证。
- 不直接迁移 IPD Docker Compose、Spring Boot actuator、Flyway、JPA、nginx、Uptime Kuma、ServerChan、bash 部署脚本或生产服务器地址。

## Capabilities

### New Capabilities

- `production-readiness-checklist`: CareerLoop 生产就绪检查清单、发布门禁、租户验证、密钥治理、健康/监控/备份/回滚和证据记录契约。

### Modified Capabilities

- `migration-governance`: 归档与发布治理新增生产就绪证据要求，明确自动化验证和租户手工验证的边界。

## Impact

- IPD 来源：
  - `F:\Project\IPD\AI_PRODUCT_HANDOFF.md`
  - `F:\Project\IPD\backend\src\main\resources\application-prod.yml`
  - `F:\Project\IPD\backend\docker-compose.yml`
  - `F:\Project\IPD\backend\Dockerfile`
  - `F:\Project\IPD\backend\scripts\deploy-backend.sh`
  - `F:\Project\IPD\backend\scripts\rollback.sh`
  - `F:\Project\IPD\backend\scripts\backup-mysql.sh`
  - `F:\Project\IPD\backend\scripts\restore-from-backup.sh`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\HealthController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\config\AlertProperties.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\config\ServerChanAppender.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\config\WebConfig.java`
- CyanCruise 目标：
  - `docs/careerloop-production-readiness-checklist.md`
  - `docs/ipd-to-cyancruise-migration-map.md`
  - `webapp/isv/v620/careerloop/careerloop-routes.json`
  - `openspec/specs/production-readiness-checklist/spec.md`
  - `openspec/specs/migration-governance/spec.md`
- 验证：
  - `node webapp\isv\v620\careerloop\validate-routes.js`
  - `openspec validate migrate-production-readiness-checklist --strict`
  - `openspec validate --all --strict`
  - JDK 8 `.\gradlew.bat clean build`
