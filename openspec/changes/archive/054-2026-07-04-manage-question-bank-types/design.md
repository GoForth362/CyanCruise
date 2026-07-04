# 设计说明：题库管理分型

## 总体设计

管理后台题库页面改为“题库管理”，页面内使用分段切换：

- 面试题库：面向模拟面试、用户贡献题和 AI 生成题，使用 `AdminQuestionDto` 与 `AdminGovernanceStorage` 存储。
- 职业测评题库：面向 MBTI、RIASEC 等测评量表，复用现有 `AssessmentApplicationService` 读取量表和题目。

## 面试题库

面试题使用现有 `AdminQuestionDto`：

- `content`：题目正文。
- `summary`：题目摘要或展示标题。
- `position`：适用岗位。
- `difficulty`：难度。
- `source`：来源，后台新建默认为 `ADMIN`。
- `reviewStatus`：发布状态，发布为 `PUBLISHED`，隐藏为 `REJECTED`。
- `status`：题目状态，发布为 `APPROVED`，隐藏为 `HIDDEN`。

后端已有 `updateQuestion` 和 `deleteQuestion` 应用服务，本次补齐：

- 自定义 WebAPI 路由 `/cc001/admin/questions/update`。
- 自定义 WebAPI 路由 `/cc001/admin/questions/delete`。
- 后台新建时走 `/cc001/admin/questions/contribute`，由服务端创建新题并写入管理题库。

## 职业测评题库

职业测评题库当前由 `AssessmentCatalog` 提供，默认实现为 `InMemoryAssessmentCatalog`。本次后台读取并维护应用运行期题库：

- `/cc001/assessment/scales` 获取量表列表。
- `/cc001/assessment/questions` 按量表获取题目和选项。
- `/cc001/admin/assessment/questions/save` 保存测评题。
- `/cc001/admin/assessment/questions/delete` 删除测评题。

页面展示每个量表的标题、说明、版本、题目数量，并列出题目、维度和选项。管理员可以选择量表新增题目，也可以编辑或删除已有题目。由于测评题会影响计分和结果解释，本次先复用现有运行期题库，后续迁移到平台业务对象后再实现跨重启持久化。

## 用户体验

- 页面标题使用“题库管理”，避免继续使用单一“审核”表述。
- 面试题库的按钮使用“保存题目”“发布”“隐藏”“删除”等明确文案。
- 职业测评题库显示“当前保存在应用运行期题库”，提醒用户后续需要接业务对象持久化。
- 错误提示不展示接口路径、TraceId 或 Java 类名。
