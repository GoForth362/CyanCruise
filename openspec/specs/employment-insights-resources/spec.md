## Purpose

定义 CyanCruise 就业洞察与就业资源入口能力，包括用户画像匹配、学校/专业独立存储、简历摘要展示、公共就业服务、精选文章、相关视频、资源跳转、首页消费契约、降级状态和迁移边界。
## Requirements
### Requirement: Employment insight profile matching

CyanCruise SHALL provide an employment insight contract that derives school, major, and target role from the current user profile context and uses them to select relevant employment records and public resource signals.

#### Scenario: User has supported school and target role

- **WHEN** a user with a supported school, major, and target role requests employment insight
- **THEN** the system SHALL return school, major, targetRole, matchLabel, summary, destinationHighlights, sourceCount, updatedAt, trend, coverage, and sources based on matching employment records

#### Scenario: Target role is missing

- **WHEN** a user has school and major data but no usable target role in the profile context
- **THEN** the system SHALL return an insight response that asks the user to complete the target role and SHALL NOT fabricate role-specific destination conclusions

### Requirement: School and major are stored separately

CyanCruise SHALL treat school and major as separate user profile fields in the Chinese endpoint and database-backed profile storage.

#### Scenario: User edits profile

- **WHEN** 用户填写或修改用户画像
- **THEN** 页面 SHALL 将学校和专业作为两个独立字段填写
- **AND** 后端存储 SHALL 将学校和专业分开保存
- **AND** legacy `schoolMajor` data SHALL only be used as a compatible read source, not as the primary write field

### Requirement: Supported school boundary

The employment insight capability SHALL explicitly distinguish supported schools, missing school data, and unsupported schools. The system MUST NOT substitute another school's employment data for the user's school.

#### Scenario: School is missing

- **WHEN** the user profile has no usable school
- **THEN** the response SHALL identify that school data is required and SHALL return no employment rate, postgraduate rate, trend, or source-derived destination conclusion

#### Scenario: School is unsupported

- **WHEN** the user profile school is not in the supported school list
- **THEN** the response SHALL mark the school as unsupported and SHALL NOT return employment metrics from a different school

#### Scenario: School alias is recognized

- **WHEN** the user profile contains a known alias or variant for a supported school
- **THEN** the system SHALL normalize it to the canonical school name before matching records

### Requirement: Resume summary is preferred for employment insight

CyanCruise 就业首页 SHALL 在用户身份可用时展示就业洞察信息，用户画像信息 SHALL 来自当前用户画像，摘要信息 SHALL 优先来自用户简历摘要或智能简历分析摘要。

#### Scenario: User identity can request insight

- **WHEN** 用户进入 `employment-home` 且存在可用 `userId`
- **THEN** 页面 SHALL 请求 `/cc001/career-employment/insight/get`
- **AND** 页面 SHALL 展示用户画像中的学校、专业、目标岗位和洞察来源状态

#### Scenario: Resume summary is available

- **WHEN** 用户存在简历摘要、智能简历分析摘要或简历诊断分
- **THEN** 就业洞察摘要区 SHALL 优先展示简历摘要或基于简历诊断的中文摘要
- **AND** 页面 SHALL NOT 使用就业来源缺失说明替代简历摘要

#### Scenario: Insight is not ready

- **WHEN** 用户画像缺少学校、专业、目标岗位或就业洞察 WebAPI 暂不可用
- **THEN** 页面 SHALL 展示中文的可操作提示
- **AND** 页面 SHALL NOT 阻止用户继续使用简历、简历诊断或面试工具

#### Scenario: Chinese endpoint does not leak English descriptions

- **WHEN** 就业洞察 WebAPI 返回英文状态、英文错误或历史英文学校/岗位名称
- **THEN** 中文端 SHALL 转换为中文展示文案或中文兜底提示

### Requirement: Source-backed metrics and trends

The employment insight capability SHALL expose employment rate, postgraduate rate, latest year, trend points, and source excerpts only when those values are present in traceable source records.

#### Scenario: Traceable metrics exist

- **WHEN** selected employment records include employment or postgraduate rates with source year and source URL
- **THEN** the response SHALL include the latest metrics, trend points by year, and source items that identify title, URL, source type, year, keywords, excerpt, and fetchedAt

#### Scenario: Metrics are absent

- **WHEN** selected records do not contain recognizable employment or postgraduate rates
- **THEN** the response SHALL omit numeric metric values and SHALL include a summary or highlight that explains the data is unavailable or pending verification

### Requirement: Career resource feed
CyanCruise SHALL provide a resource feed contract for webapp consumption that can expose public service cards, article cards, video cards, consultation/tip cards, and career path cards without requiring IPD frontend or crawler runtime. The feed SHALL include visible administrator-managed content when such content exists.

#### Scenario: Resource feed is requested
- **WHEN** the webapp requests employment resources
- **THEN** the system SHALL return resource cards with stable identifiers, title, summary or body, category or keyword, source URL, image URL when available, and type-specific fields such as video duration or career path id

#### Scenario: Admin content is visible
- **WHEN** administrator-managed content is saved and not hidden
- **THEN** the resource feed SHALL include that content as a resource card using its title, summary, category, image URL, source URL, and content type

#### Scenario: Admin content is hidden
- **WHEN** administrator-managed content is marked hidden
- **THEN** the resource feed SHALL NOT expose that content to user-facing pages

#### Scenario: Resource feed has no configured content
- **WHEN** no administrator-managed resource records are available
- **THEN** the system SHALL return seeded or empty resource state that keeps the webapp navigable and SHALL NOT fail the workbench

### Requirement: Employment home resource content

CyanCruise 就业首页 SHALL 在就业工具入口之外展示就业资讯、相关文章、公共就业服务入口和职业指导资源，并 SHALL 优先使用既有资源 feed WebAPI 返回的资源卡。

#### Scenario: Employment home loads resources

- **WHEN** 用户进入 `employment-home` 页面
- **THEN** 页面 SHALL 请求 `/cc001/career-employment/resources/list`
- **AND** 页面 SHALL 在就业首页按“公共服务、精选文章、相关视频”顺序展示资源卡
- **AND** 就业首页每个资源类型 SHALL 最多展示 2 条资源
- **AND** 用户 SHALL 能通过“全部资源”查看更多资源

#### Scenario: All resources page shows full resource feed

- **WHEN** 用户点击“全部资源”
- **THEN** 页面 SHALL 展示公共服务、精选文章、相关视频等完整资源列表
- **AND** 页面 SHALL NOT 只展示占位说明

#### Scenario: Resource card opens external platform

- **WHEN** 资源卡存在可访问的 `sourceUrl`
- **THEN** “查看资源” SHALL 直接打开对应外部平台或页面
- **AND** 公共服务 SHALL 跳转国内公共就业服务入口
- **AND** 精选文章 SHALL 跳转可直接阅读的互联网文章页面
- **AND** 相关视频 SHALL 跳转可直接播放的国内视频页面

#### Scenario: Resource feed is unavailable

- **WHEN** 资源 feed WebAPI 不可用或返回空内容
- **THEN** 就业首页 SHALL 保持简历和面试工具入口可用
- **AND** 页面 SHALL 展示资源暂不可用或暂无配置内容的页面内状态

### Requirement: Employment home roadmap first

CyanCruise 就业首页 SHALL 将就业路线图作为第一个主要内容区，帮助用户先理解目标岗位、简历证据、资讯投递和面试复盘的下一步顺序，并 SHALL 将就业工具区放在洞察和资源内容之后。

#### Scenario: Employment home shows roadmap before tools

- **WHEN** 用户进入 `employment-home` 页面
- **THEN** 页面 SHALL 先展示就业路线图区块
- **AND** 页面 SHALL 在路线图、就业洞察和资源内容之后展示就业工具入口

#### Scenario: Planning agent is unavailable

- **WHEN** 路径规划智能体尚未接入
- **THEN** 就业路线图 SHALL 使用已有用户画像、简历记录、面试记录和规划摘要生成规则版路线图提示
- **AND** 页面 SHALL 提供调用 `/cc001/career-plan/ensure` 的生成或刷新入口

### Requirement: WebAPI and route contract mapping

The migration SHALL define Cosmic WebAPI and webapp route/API mapping for employment insights and resource entries.

#### Scenario: WebAPI contract is reviewed

- **WHEN** reviewers inspect the migration artifacts
- **THEN** they SHALL find the employment insight WebAPI contract, resource feed WebAPI contract, required user identity semantics, DTO fields, fallback states, and related webapp route keys

#### Scenario: Existing workbench integrates the entry

- **WHEN** the webapp workbench adds employment insight or resource entry points
- **THEN** the route/API map SHALL identify how those entries consume the new contracts without changing existing profile, today-action, career-plan, interview, resume, assessment, or assistant contracts

### Requirement: Migration boundary for employment insights and resources

The migration SHALL rebuild employment insight and resource semantics for CyanCruise and SHALL NOT directly migrate IPD Spring Boot, JPA, Flyway, Java 17 HTTP, PDFBox, Redis, Bilibili crawler, Vue, uni-app, Pinia/store, or mini-program runtime implementations.

#### Scenario: Implementation is inspected

- **WHEN** implementation files are reviewed
- **THEN** they SHALL reside in CyanCruise target modules and SHALL NOT require `F:\Project\IPD` source files or IPD runtime dependencies

#### Scenario: Dependency changes are checked

- **WHEN** dependency changes are reviewed
- **THEN** the change SHALL NOT introduce new external dependencies unless their necessity and Cosmic/KDDT/JDK 8 compatibility are documented

### Requirement: Verification and migration documentation

The employment insight and resource migration SHALL include verification and documentation proving the OpenSpec contract, implementation, webapp mapping, and migration map are aligned.

#### Scenario: Change is verified before archive

- **WHEN** implementation is complete
- **THEN** verification SHALL include strict OpenSpec validation, focused helper/service/WebAPI tests or equivalent static checks, JDK 8 Gradle build validation, and migration map updates

#### Scenario: Migration map is updated

- **WHEN** the change is finalized
- **THEN** `docs/ipd-to-cyancruise-migration-map.md` SHALL record IPD source paths, CyanCruise target modules, data mapping, temporarily excluded items, and validation results

