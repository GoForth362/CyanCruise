## Why

IPD 的 CareerLoop 在简历上传之后会继续完成 JD 匹配、简历诊断、关键词提取和画像补强；CyanCruise 当前只有简历基础记录，还缺少“简历是否需要优化”的可消费信号。迁移简历诊断后，今日行动、职业计划和后续面试准备可以基于真实简历内容给出更精确的下一步建议。

## What Changes

- 新增简历诊断业务契约：支持用户提交简历文本或 resumeId，并可选提交目标 JD，返回总分、优势、短板、修改建议和原始分析摘要。
- 新增诊断结果解析规则：支持从结构化 JSON 或非结构化文本中提取 0-100 总分，解析失败时使用明确兜底结果。
- 新增简历关键词抽取契约：从简历目标岗位、标题、解析内容和更新时间中提取职业相关关键词，输出类别、标签、权重和证据。
- 新增诊断应用边界与 Cosmic WebAPI 规划：按用户 ID 校验 resumeId 归属，保存诊断分数，并暴露触发诊断、读取关键词状态和强制重算入口。
- 扩展简历基础能力：诊断完成后 SHALL 更新简历记录的 diagnosisScore，并同步到用户画像 resume block。
- 暂不迁移 Spring Boot Controller/JPA/Flyway、OSS 下载、PDFBox PDF 解析、DashScope/Qwen 调用、Spring 异步任务、通知推送、Vue/uni-app 页面实现。

## Capabilities

### New Capabilities

- `resume-diagnosis`: 定义简历诊断、诊断结果解析、关键词抽取、关键词状态和 WebAPI 入口契约。

### Modified Capabilities

- `resume-core`: 增加“诊断结果更新简历记录与画像 resume block”的规格要求。

## Impact

- IPD 来源路径：
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\ResumeDiagnosisController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\ResumeKeywordService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\ResumeKeywordServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\dto\ResumeKeywordDto.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\ResumeProfileKeyword.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\utils\PdfTextExtractor.java`
- CyanCruise 目标模块：
  - `code/base/v620-cc001-base-common/`：JDK 8 DTO、请求和诊断结果契约。
  - `code/base/v620-cc001-base-helper/`：纯 Java 诊断结果解析、关键词抽取和状态规则。
  - `code/cloud01/v620-cc001-cloud01-app01/`：应用服务、可替换存储边界、Cosmic WebAPI 和聚焦测试。
  - `openspec/specs/`、`docs/ipd-to-cyancruise-migration-map.md`：规格和迁移地图同步。
- 不新增外部依赖；PDF 解析、AI 调用、通知和最终 Cosmic datamodel 适配保留为后续 change。
