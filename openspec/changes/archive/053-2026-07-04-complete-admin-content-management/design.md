## Context

现有管理后台已经具备 `AdminContentItemDto`、`AdminGovernanceStorage`、PostgreSQL/内存存储，以及 `listContent`、`saveContent`、`toggleContentPinned`、`toggleContentHidden`、`deleteContent` 应用服务方法。前端仅展示内容列表和置顶/隐藏操作，自定义 OpenAPI 路由也只暴露了 list/pin/hide。

就业资源接口目前通过 `CareerResourceStorage` 返回种子资源，用户端首页和资源页消费 `/cc001/career-employment/resources/list`。

## Decisions

1. 复用现有 `AdminContentItemDto` 和 `AdminGovernanceStorage`
   - 不新建业务对象。
   - 新增/编辑统一走 `/cc001/admin/content/save`。
   - 删除走 `/cc001/admin/content/delete`。

2. 用适配器打通用户端资源
   - 新增 `AdminContentCareerResourceStorage`，把后台内容转为 `CareerResourceCardDto`。
   - 过滤 `hidden=true` 的内容。
   - 置顶内容在后台内容源中优先排序。
   - 内置 `InMemoryCareerResourceStorage` 作为兜底资源源，避免后台未配置时资源页为空。

3. 内容类型做前后端兼容
   - 管理端允许 `ARTICLE`、`VIDEO`、`RESOURCE`。
   - 用户端资源 feed 映射为 `article`、`video`、`consultation`，保证现有资源页分组可直接展示。

4. 前端保持 CyanCruise 现有风格
   - 使用已有管理后台卡片、表格、按钮样式。
   - 表单置于内容列表上方，支持新建、编辑、保存、重置。
   - 保存、删除、置顶、隐藏后只刷新管理后台数据，不跳出当前页面。

## Risks

- 后台内容和种子资源并存时可能出现语义重复。先通过内容 ID 去重，后续若统一到金蝶业务对象存储，可以增加来源优先级配置。
- 当前内容模型字段较轻，不承载富文本正文。先用摘要和外链满足首页/资源页展示，后续可扩展正文或文件字段。

## Verification

- `openspec validate complete-admin-content-management --strict`
- `node webapp/isv/v620/cyancruise/validate-routes.js`
- 管理内容保存/删除和就业资源可见性单元测试
- `.\gradlew.bat build`
