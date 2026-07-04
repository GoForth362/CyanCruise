## Why

CyanCruise 已有管理端治理边界和入口雏形，但页面仍缺少可用的管理工作台交互，部分后台接口也尚未统一经过管理员身份校验。现在需要把现有管理端补齐为一个可验证的 MVP，支撑平台管理员完成用户、内容、题库、公告和审计等基础运营工作。

## What Changes

- 扩展现有管理后台页面，提供总览、用户管理、内容管理、题库审核、通知公告、操作记录等首批管理视图。
- 新增前端管理端服务封装，统一调用 `/cc001/admin/*` WebAPI，并在无权限或后端不可用时展示可恢复状态。
- 收口管理端 WebAPI 权限边界，所有管理端读写接口 SHALL 使用当前 Cosmic 身份校验管理员角色，不再仅信任请求体中的 `adminId`。
- 保留现有同应用部署方式，不新建独立应用，不引入新的前后端依赖。
- 修正管理端相关用户可见中文文案，避免乱码和专业缩写直接暴露给普通用户。

## Capabilities

### New Capabilities

- 无。

### Modified Capabilities

- `admin-console-governance`: 明确管理端 MVP 的前端交互、统一管理员身份校验、接口调用封装和无权限状态要求。

## Impact

- `webapp/isv/v620/cyancruise/assets/pages/admin-console.js`
- `webapp/isv/v620/cyancruise/assets/services/admin-service.js`
- `webapp/isv/v620/cyancruise/assets/app-runtime.js`
- `webapp/isv/v620/cyancruise/assets/api.js`
- `code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/webapi/admin/`
- `code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/mservice/application/`
- `code/base/v620-cc001-base-helper/src/main/java/v620/base/helper/career/`
- 不新增外部依赖；继续兼容 JDK 1.8 和 Cosmic/KDDT 模板约束。
