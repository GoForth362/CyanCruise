## Why

CyanCruise 已具备模拟面试会话、消息、报告摘要和画像同步的基础契约，但现有页面只能查看历史和创建空会话，`InterviewAiService` 也尚未进入应用流程，用户无法完成“开始练习—回答—追问—结束—查看复盘”的闭环。简历诊断已完成并归档，现在应复用目标岗位、简历、用户画像、文件与 AI 网关，把 CareerLoop 的模拟面试主流程真正接通。

## What Changes

- 在既有 `interview-core` 上增加文本模拟面试编排：生成开场问题、保存用户回答、生成下一道问题、结束会话并按完整记录生成一次结构化复盘报告。
- 生成问题时复用目标岗位、所选简历内容和用户画像；缺少部分上下文时仍可围绕已知岗位继续练习，并给出普通用户能理解的提示。
- 复用现有 provider-neutral `AiGateway` 和未配置降级机制，不引入新的 AI SDK 或运行期依赖。
- 复用既有报告 DTO、存储边界和画像同步；已生成的报告直接读取，不重复调用 AI。
- 将 webapp 的“模拟面试”从契约列表页升级为可操作的中文流程，包含准备、对话、结束、复盘和历史查看。
- 将面试中心、AI 模拟面试和全景仿真面试拆分为独立路由；入口和历史记录按面试方式分类，不再让两种面试共用同一个页面。
- 为全景仿真面试增加沉浸式面试环境贴图、浏览器摄像头授权与实时预览、AI 问题、答题计时和语音转文字回答流程；摄像头画面仅在浏览器本地预览。
- 将普通 AI 模拟面试调整为最多 7 题的逐题问答页面，只展示当前问题与回答区；最后一题提交后自动生成总分、评价和改进方向。
- 将首页“推荐工具”中的单个“模拟面试”入口拆成“全景仿真面试”和“AI 模拟面试”两张卡片，并分别跳转到对应页面。
- 增强苍穹嵌入环境的摄像头兼容：依次尝试当前页面、可访问父页面和旧版媒体接口；仍受浏览器安全策略限制时允许用户选择无摄像头模式继续全景 AI 面试。
- 明确不迁移 IPD 的 Spring Boot、JPA、Flyway、Vue、uni-app、语音、数字人、身体语言和题库管理实现。

## Capabilities

### New Capabilities

- `ai-mock-interview`: 定义基于现有 AI 网关的文本面试开场、追问、上下文组装、结构化复盘、降级行为和页面闭环。

### Modified Capabilities

- `interview-core`: 增加面向用户的一体化面试轮次与结束复盘入口，并约束报告缓存、会话状态和消息顺序。

## Impact

- `code/base/v620-cc001-base-common/`：补充面试轮次请求/响应等公共契约，保持 JDK 1.8 兼容。
- `code/base/v620-cc001-base-helper/`：补充提示词上下文、AI JSON 解析与无 AI 时的安全降级规则。
- `code/cloud01/v620-cc001-cloud01-app01/`：接通 `InterviewApplicationService`、`InterviewAiService`、`AiGateway`、简历与画像服务，并扩展 Cosmic WebAPI 和聚焦测试。
- `webapp/isv/v620/cyancruise/`：实现模拟面试准备、对话、复盘和历史页面状态。
- IPD 业务来源：`F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\InterviewServiceImpl.java`、`controller\InterviewController.java`、`controller\InterviewReportController.java`、`model\entity\Interview*.java`，以及 `F:\Project\IPD\frontend\src\pages\interview\` 和 `src\api\interview.ts`。
- 不新增依赖，不硬编码本地路径；构建继续使用仓库内 `gradlew.bat` 和 JDK 1.8。
