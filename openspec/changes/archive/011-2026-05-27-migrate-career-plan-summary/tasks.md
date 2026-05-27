## 1. Shared Contracts

- [x] 1.1 新增职业计划记录、摘要、里程碑和保存请求 DTO，保持 JDK 8 兼容。
- [x] 1.2 明确计划健康度常量或枚举取值：`MISSING`、`NEEDS_REFRESH`、`ON_TRACK`。
- [x] 1.3 保持 DTO 不依赖 Spring、JPA、Cosmic datamodel 或外部 AI SDK。

## 2. Helper Rules

- [x] 2.1 新增职业计划摘要 helper，将计划记录转换为摘要。
- [x] 2.2 实现计划健康度规则：缺更新时间、超过 14 天、本周重点为空、版本过旧、正常可用。
- [x] 2.3 实现默认计划生成规则，基于画像目标岗位或兜底目标岗位生成里程碑和本周重点。
- [x] 2.4 增加 helper 聚焦测试，覆盖无计划、有计划、过期计划、空本周重点、旧版本和默认计划。

## 3. Storage And Application Boundary

- [x] 3.1 新增 `CareerPlanStorage` 边界，支持按用户 ID 读取、保存和判断计划是否存在。
- [x] 3.2 新增默认存储实现，并测试跨新 service/storage 实例可读回计划。
- [x] 3.3 新增 `CareerPlanApplicationService`，支持获取摘要、确保计划、保存或更新计划。
- [x] 3.4 确保保存已有用户计划时更新同一份计划并递增版本。

## 4. CareerLoop Integration

- [x] 4.1 扩展今日行动输入源，按用户 ID 推荐时读取职业计划本周重点。
- [x] 4.2 增加今日行动应用测试，证明有计划时追加 `PLAN_WEEKLY`，无计划时继续正常推荐。
- [x] 4.3 扩展职业画像应用边界，使统一画像 `hasPlan` 能由职业计划存在性提供。
- [x] 4.4 增加画像测试，覆盖有计划时移除职业计划缺失信号、无计划时保留缺失信号。

## 5. WebAPI

- [x] 5.1 新增或扩展 Cosmic WebAPI，支持按用户 ID 获取职业计划摘要。
- [x] 5.2 新增 Cosmic WebAPI 确保计划入口。
- [x] 5.3 新增 Cosmic WebAPI 保存或更新职业计划入口。
- [x] 5.4 增加 WebAPI 聚焦测试，覆盖获取摘要、确保计划和保存计划。

## 6. Migration Documents

- [x] 6.1 更新 `docs/ipd-to-cyancruise-migration-map.md` 中职业计划状态。
- [x] 6.2 记录 AI 生成、最终 Cosmic datamodel、计划页面和周复盘仍为后续迁移项。

## 7. Validation

- [x] 7.1 运行 `openspec validate migrate-career-plan-summary --strict`。
- [x] 7.2 运行 `openspec validate --all --strict`。
- [x] 7.3 设置 JDK 8 后运行相关 Gradle 测试。
- [x] 7.4 设置 JDK 8 后运行 `.\gradlew.bat clean build`。
