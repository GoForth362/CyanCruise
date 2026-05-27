## Context

CyanCruise 已迁移 CareerLoop 主循环的核心后端能力与首个 webapp 入口，但外部就业参照仍停留在迁移地图的 P2 待迁移项。IPD 的就业洞察由 `CdutEmploymentInsightServiceImpl` 读取当前用户学校、专业和画像目标岗位，匹配公开就业来源，输出摘要、趋势、覆盖审计、来源列表；首页资源由 `HomepageController` 聚合视频、文章、咨询和职业路径卡片。

本次迁移的目标平台是 Kingdee Cosmic，必须兼容 JDK 1.8。IPD 中 Spring Controller、JPA repository、Flyway SQL、Java 17 `HttpClient`、PDFBox、Redis、Bilibili 刷新、Vue/uni-app 页面和生产抓取任务不直接迁移；只迁移业务规则、数据语义、流程和接口契约。

## Goals / Non-Goals

**Goals:**

- 定义 JDK 8 兼容的就业洞察 DTO，覆盖 school、major、targetRole、matchLabel、summary、latest rates、trend、coverage、sources 和 updatedAt。
- 定义资源卡片 DTO，覆盖文章、视频、咨询、职业路径入口和可在 webapp 使用的链接/分类/来源摘要。
- 提供纯 Java helper 规则：学校归一、用户画像匹配、来源排序、趋势聚合、覆盖审计、摘要生成和不可用降级。
- 提供 app01 应用服务和 Cosmic WebAPI 边界，供 webapp 工作台、今日行动和职业计划后续消费。
- 保留来源可追溯和“待核验”状态，避免伪造就业率、深造率、去向结论或把其他学校数据替代为用户学校数据。
- 更新迁移地图和验证流程，确保实现阶段可通过 OpenSpec、聚焦测试和 JDK 8 Gradle 构建。

**Non-Goals:**

- 不在 propose 阶段实现代码。
- 不直接迁移 IPD Spring Boot Controller、JPA/Flyway、repository、Java 17 `HttpClient`、PDFBox、Redis、`@Transactional` 或定时刷新实现。
- 不迁移 IPD Vue/uni-app 页面、Pinia/store、Bilibili 小程序 web-view、图片代理、刷新限流或 tabbar。
- 不建立生产内容运营后台、真实外部抓取调度、全量就业报告解析器或 PDF/HTML 通用抽取器。
- 不新增外部依赖，除非 apply 阶段证明 Cosmic/JDK 8 现有能力无法满足且单独说明必要性。

## Decisions

### 1. 就业洞察先做只读契约和可替换存储边界

`base-common` SHALL 定义就业洞察 DTO；`base-helper` SHALL 承载学校归一、匹配、排序、覆盖审计和摘要规则；`app01` SHALL 提供应用服务、存储边界和 Cosmic WebAPI。首轮实现优先使用内存/文件型或 Cosmic datamodel adapter 可替换存储，不直接引入外部抓取。

原因：就业洞察要被 webapp、今日行动、职业计划复用，业务规则应可测试且不依赖平台网络环境。外部数据刷新属于运营和平台集成问题，应该与主循环消费契约解耦。

替代方案是直接迁移 IPD 的抓取服务和 repository；该方案依赖 Spring/JPA/Java 17/PDFBox，并会把网络失败变成主循环不可用风险，不采用。

### 2. 学校覆盖范围和来源状态显式表达

就业洞察 SHALL 对已支持学校、未填写学校、暂未支持学校和来源不足分别返回明确状态。支持学校列表首轮沿用 IPD 的四川“双一流”高校语义，但 SHALL 通过常量/helper 管理，不能在 WebAPI 或页面里散落硬编码。

原因：IPD 的关键安全语义是“不用其他学校数据替代当前用户学校”。迁移后需要让页面和服务都能识别缺失、部分覆盖、完整验证、待人工核验等状态。

替代方案是所有用户都展示成都理工大学默认数据；这会误导用户，不采用。

### 3. 就业率和深造率只来自可追溯来源

系统 SHALL 只在来源记录中存在可识别指标时返回 latestEmploymentRate、latestPostgraduateRate 和 trend。缺失时 SHALL 返回空指标和文字说明，而不是估算或使用 mock 数字。source item SHALL 暴露 title、url、sourceType、year、keyword、excerpt 和 fetchedAt，支持审阅核验。

原因：就业数据具有高信任要求，错误数字会直接影响用户职业决策。可追溯比“看起来完整”更重要。

替代方案是用规则根据行业热度推断指标；该方案不可审计，不采用。

### 4. 资源入口迁移为轻量摘要卡片

资源能力 SHALL 提供文章、视频、咨询和职业路径卡片的统一摘要契约。视频可保留外部 URL、bvid、时长、播放量等语义；文章和咨询可保留分类、摘要、作者、来源链接。首轮不实现抓取、封面代理或刷新接口，只允许展示已配置/已存储资源。

原因：CareerLoop webapp 需要资源入口增强工作台，但生产内容采集和运营后台是独立能力。轻量卡片可先接入主循环，不阻塞后续管理后台。

替代方案是迁移 IPD 首页的全部内容刷新链路；该方案范围过大，且涉及 Redis、Bilibili 接口和运营策略，不适合本 change。

### 5. WebAPI 使用显式用户归属和可恢复降级

就业洞察 WebAPI SHALL 以当前用户或显式 userId 解析用户画像输入；未解析用户时 SHALL 返回身份必需状态，不能使用生产硬编码用户。资源 feed 可以匿名读取，但个性化排序 SHALL 在有 userId 时才启用。

原因：这与已迁移 webapp 入口的开发/生产身份边界保持一致，避免跨用户数据和伪个性化。

替代方案是在前端本地拼装默认用户画像；会破坏后端契约和审计，不采用。

## Risks / Trade-offs

- [Risk] 首轮不接真实抓取后，数据可能偏静态或不足 -> Mitigation：通过 coverage、sourceCount、updatedAt 和 unavailable state 明确展示数据状态，后续单独迁移刷新/运营能力。
- [Risk] 四川“双一流”高校列表可能不覆盖所有用户 -> Mitigation：未支持学校返回明确提示，不用其他学校替代；支持列表集中管理，便于后续扩展。
- [Risk] 资源卡片和就业洞察会扩大 webapp 信息密度 -> Mitigation：route/API map 明确入口与降级状态，首轮只接主循环必要入口。
- [Risk] 外部链接可能受 Cosmic webview 或安全白名单限制 -> Mitigation：WebAPI 只提供链接语义，webapp 展示打开方式和不可打开降级，不在后端代理所有外部资源。
- [Risk] 后续真实内容解析需要新依赖或平台服务 -> Mitigation：本 change 不新增依赖；若后续需要，单独 propose 并说明 Cosmic/KDDT 兼容性。

## Migration Plan

1. 新增 `employment-insights-resources` 规格并通过 OpenSpec 严格校验。
2. apply 阶段新增就业洞察和资源 DTO、常量、helper 规则与聚焦测试。
3. 新增 app01 应用服务、存储边界和 Cosmic WebAPI，保留可替换数据源。
4. 更新 webapp CareerLoop route/API 映射，提供就业洞察/资源入口接线点和降级展示契约。
5. 更新迁移地图，记录 IPD 来源、CyanCruise 目标、数据映射、暂不迁移项和验证方式。
6. 使用 `openspec validate migrate-employment-insights-resources --strict`、`openspec validate --all --strict` 和 JDK 8 `.\gradlew.bat clean build` 验证。

Rollback：就业洞察/资源作为新增只读能力，若实现出现问题，webapp SHALL 隐藏或标记该入口为 pending；既有画像、今日行动、职业计划和 webapp 工作台契约不变。

## Open Questions

- 生产态就业数据初始来源由 Cosmic datamodel 配置、静态种子文件还是后续管理后台维护，需要 apply 或后续 change 明确。
- 真实外部内容抓取和 PDF/HTML 解析是否由 CyanCruise 承担，还是只接入已审核内容源，需要与平台部署约束对齐。
- webapp 打开外部链接的最终方式需要结合 Cosmic WebAPI/webview 白名单确认。
