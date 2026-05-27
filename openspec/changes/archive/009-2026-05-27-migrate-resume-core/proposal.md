## Why

CareerLoop 已有 onboarding、职业画像和测评提交链路，但真实简历记录仍缺失。简历是今日任务、后续诊断、JD 匹配和模拟面试的重要输入，因此需要先迁移 IPD 的简历基础记录能力，打通“上传或保存简历元数据 -> 更新画像 resume block”的主循环信号。

## What Changes

- 新增简历基础能力，表达 JDK 8 兼容的简历记录 DTO 和创建/更新请求。
- 新增简历应用服务边界，支持保存简历记录、查询单条、查询用户列表、更新元数据和删除记录。
- 新增可替换的简历存储边界，先使用本地文件型或内存型适配器验证业务语义，后续可替换为 Cosmic datamodel。
- 保存或更新简历时 SHALL 合并到 `UserProfileSnapshot.ResumeBlock`，并刷新统一画像中的真实简历信号。
- 删除简历时 SHALL 只删除简历记录；若删除的是当前画像引用的最新简历，系统 SHALL 清理或重算 resume block，避免画像继续引用已删除记录。
- 暂不迁移 IPD 的 Spring Controller、JPA Repository、OSS SDK、PDF 代理下载、简历诊断、简历生成、关键词抽取和前端页面。
- 更新迁移映射表，标记简历基础记录能力进入已实现状态，并保留 datamodel、文件上传/预览和诊断生成作为后续工作。

## Capabilities

### New Capabilities

- `resume-core`: 定义简历基础记录、用户列表、详情、更新、删除和画像 resume block 同步规则。

### Modified Capabilities

- 无。

## Impact

- 影响模块：
  - `code/base/v620-cc001-base-common`：新增简历记录和请求 DTO。
  - `code/base/v620-cc001-base-helper`：如需要，新增简历记录校验或画像 resume block 组装 helper。
  - `code/cloud01/v620-cc001-cloud01-app01`：新增简历应用服务、存储边界、默认适配器、WebAPI 和测试。
  - `openspec/specs/resume-core`：新增简历基础规格。
  - `docs/ipd-to-cyancruise-migration-map.md`：同步简历基础迁移状态。
- IPD 来源：
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\ResumeController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\ResumeService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\ResumeServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\Resume.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\FileService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\FileServiceImpl.java`
- 不新增运行期依赖，不改变现有画像、测评或今日推荐 API 契约。
