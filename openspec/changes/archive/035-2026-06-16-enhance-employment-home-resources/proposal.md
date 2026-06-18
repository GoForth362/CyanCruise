## Why

当前 CyanCruise 就业首页只展示简历和面试工具入口，用户进入后缺少可直接阅读和使用的就业资讯、文章、职业指导和公共就业服务链接。这会让页面看起来像功能目录，而不是就业场景的工作台。

本次变更先把既有就业资源能力暴露到就业首页：用户在同一页面即可进入工具、查看就业洞察摘要，并阅读来自公开就业服务平台或人工配置的文章/资源卡，为后续接入定时采集或人工运营后台留出契约。

同时，就业首页不应首先呈现工具列表，而应先回答用户“接下来怎么走”。在 AI 路径规划智能体尚未接入前，页面先展示基于既有画像、简历、面试和规划摘要的规则版就业路线图；后续接入智能体时复用该区域和交互入口。

## What Changes

- 就业首页 SHALL 在工具入口之外展示就业资讯与文章资源区，优先使用 `/cc001/career-employment/resources/list` 返回的资源卡。
- 就业首页 SHALL 将就业路线图作为首个主要内容区，并将就业工具区放在洞察和资源内容之后。
- 在路径规划智能体尚未接入时，路线图 SHALL 使用既有用户画像、简历、面试记录和 `/cc001/career-plan/ensure` 规则版规划接口提供可操作占位。
- 就业首页 SHALL 在有用户身份时读取就业洞察摘要，并以轻量方式提示学校/专业/目标岗位匹配状态。
- 就业洞察区 SHALL 以用户画像作为左侧信息来源；摘要区 SHALL 优先展示用户简历摘要或 AI 简历分析摘要，中文端 SHALL NOT 直出英文状态、英文错误或英文说明文案。
- 资源卡 SHALL 支持公共服务、精选文章和相关视频类型，页面 SHALL 按“公共服务、精选文章、相关视频”顺序展示，并 SHALL 优先使用国内可直接阅读或观看的互联网资源链接，相关视频 SHALL NOT 绑定单一视频平台。
- 资源卡的“查看资源” SHALL 跳转到对应外部平台来源，例如公共就业服务平台、可阅读文章页面或 B 站视频播放页，而不是仅展示站内文字提示。
- 后端默认资源种子 SHALL 使用 CyanCruise 命名，补充公开就业服务平台、求职指导文章、招聘信息入口等可追溯外部链接。
- 本次不引入运行时网页爬虫、外部 HTTP 抓取依赖、JPA/Flyway/Spring Boot、数据库密码或本地环境硬编码。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `employment-insights-resources`: 就业资源能力从独立资源页扩展为就业首页的一等内容区，并要求就业首页优先展示规则版就业路线图，资源卡可承载公开就业资讯、相关文章和外部就业服务入口。

## Impact

- 影响前端：
  - `webapp/isv/v620/cyancruise/assets/app.js`
  - `webapp/isv/v620/cyancruise/assets/styles.css`
  - `webapp/isv/v620/cyancruise/index.html`
- 影响后端资源种子与测试：
  - `code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/mservice/InMemoryCareerResourceStorage.java`
  - 相关就业资源 service/WebAPI 测试
- API 仍复用既有 `/cc001/career-employment/insight/get` 与 `/cc001/career-employment/resources/list`，不新增外部依赖。
