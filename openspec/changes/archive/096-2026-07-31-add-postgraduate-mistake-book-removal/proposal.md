## Why

错题本会长期保存用户的错题，但用户目前无法移除已不需要的记录，导致错题本无法自行整理。

## What Changes

- 在错题本列表和错题详情页提供“移除错题”操作。
- 新增仅允许删除当前用户本人错题记录的服务端删除接口。
- 删除后立即刷新错题本列表和考研路线图的错题阶段状态。

## Capabilities

### New Capabilities

- `postgraduate-mistake-book-removal`: 用户安全移除自己错题本中的记录。

### Modified Capabilities

- `postgraduate-exam-companion`: 错题本增加用户可控的删除记录行为。

## Impact

- 考研错题本存储接口、内存与 PostgreSQL 实现、WebAPI 路由及前端错题本页面。
