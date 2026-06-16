## ADDED Requirements

### Requirement: Employment home roadmap first

CyanCruise 就业首页 SHALL 将就业路线图作为首个主要内容区，帮助用户先理解目标岗位、简历证据、资讯投递和面试复盘的下一步顺序，并 SHALL 将就业工具区放在洞察和资源内容之后。

#### Scenario: Employment home shows roadmap before tools

- **WHEN** 用户进入 `employment-home` 页面
- **THEN** 页面 SHALL 先展示就业路线图区块
- **AND** 页面 SHALL 在路线图、就业洞察和资源内容之后展示就业工具入口

#### Scenario: 路径规划智能体未接入

- **WHEN** 路径规划智能体尚未接入
- **THEN** 就业路线图 SHALL 使用既有用户画像、简历记录、面试记录和规划摘要生成规则版路线图提示
- **AND** 页面 SHALL 提供调用 `/cc001/career-plan/ensure` 的生成或刷新入口

### Requirement: Employment home resource content

CyanCruise 就业首页 SHALL 在就业工具入口之外展示就业资讯、相关文章、公共就业服务入口和职业指导资源，并 SHALL 优先使用既有资源 feed WebAPI 返回的资源卡。

#### Scenario: Employment home loads resources

- **WHEN** 用户进入 `employment-home` 页面
- **THEN** 页面 SHALL 请求 `/cc001/career-employment/resources/list` 并在就业首页按“公共服务、精选文章、相关视频”顺序展示资源卡
- **AND** 就业首页每个资源类型 SHALL 最多展示 2 条资源
- **AND** 用户 SHALL 能通过“全部资源”查看更多资源

#### Scenario: Resource card opens external platform

- **WHEN** 资源卡存在可访问的 `sourceUrl`
- **THEN** “查看资源” SHALL 直接打开对应外部平台或页面
- **AND** 公共服务 SHALL 跳转国内公共就业服务入口
- **AND** 精选文章 SHALL 跳转可直接阅读的互联网文章页面
- **AND** 相关视频 SHALL 跳转可直接播放的国内视频页面

#### Scenario: Resource feed is unavailable

- **WHEN** 资源 feed WebAPI 不可用或返回空内容
- **THEN** 就业首页 SHALL 保持简历和面试工具入口可用，并展示资源暂不可用或暂无配置内容的页面内状态

### Requirement: Employment home insight summary

CyanCruise 就业首页 SHALL 在用户身份可用时展示就业洞察信息，用户画像信息 SHALL 来自当前用户画像；摘要信息 SHALL 优先来自用户简历摘要或 AI 简历分析摘要，帮助用户理解当前学校、专业、目标岗位和简历状态。

#### Scenario: School and major are stored separately

- **WHEN** 用户填写或修改用户画像
- **THEN** 页面 SHALL 将学校和专业作为两个独立字段填写
- **AND** 后端草稿存储 SHALL 将学校和专业分开保存
- **AND** 旧 `schoolMajor` 数据 SHALL 仅作为兼容读取来源，不作为新数据的主存储字段

#### Scenario: User identity can request insight

- **WHEN** 用户进入 `employment-home` 且存在可用 `userId`
- **THEN** 页面 SHALL 请求 `/cc001/career-employment/insight/get` 并展示用户画像中的学校、专业、目标岗位和洞察来源状态

#### Scenario: Resume summary is preferred

- **WHEN** 用户存在简历摘要、AI 简历分析摘要或简历诊断分
- **THEN** 就业洞察摘要区 SHALL 优先展示简历摘要或基于简历诊断的中文摘要
- **AND** 页面 SHALL NOT 使用就业来源缺失说明替代简历摘要

#### Scenario: Insight is not ready

- **WHEN** 用户画像缺少学校、专业、目标岗位或就业洞察 WebAPI 暂不可用
- **THEN** 页面 SHALL 展示中文的可操作提示，并 SHALL NOT 阻止用户继续使用简历、简历诊断或面试工具

#### Scenario: Chinese endpoint does not leak English descriptions

- **WHEN** 就业洞察 WebAPI 返回英文状态、英文错误或历史英文学校/岗位名称
- **THEN** 中文端 SHALL 转换为中文展示文案或中文兜底提示

### Requirement: Configured online employment resources

CyanCruise SHALL allow resource cards to represent public online employment services, job search portals, career guidance articles, videos, consultation/tip entries, and career path references without requiring IPD crawler runtime.

#### Scenario: Default resources are inspected

- **WHEN** 默认资源存储被初始化
- **THEN** 它 SHALL include publicly reachable `sourceUrl` values for employment services or guidance content and SHALL use CyanCruise naming in user-visible text

#### Scenario: Runtime crawler is out of scope

- **WHEN** implementation files are reviewed for this change
- **THEN** they SHALL NOT introduce runtime web crawling, HTML parsing dependencies, scheduled external fetch jobs, Spring Boot, JPA, Flyway, Redis, or Java 9+ APIs
