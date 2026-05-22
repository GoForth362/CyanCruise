## 上下文

IPD 的职业测评由 `AssessmentServiceImpl` 负责，完整流程包含量表读取、题目读取、提交答案、保存记录、保存答案、生成 AI 洞察、合并用户画像、推送通知、打卡和微信订阅。本次只迁移评分内核，让 CyanCruise 先拥有可测试、可复用的测评规则。

## 来源证据

| 来源 | 路径 | 迁移内容 |
| --- | --- | --- |
| 测评服务接口 | `F:\Project\IPD\backend\src\main\java\com\group1\career\service\AssessmentService.java` | 量表、题目、提交评分、记录查询的边界 |
| 测评服务实现 | `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\AssessmentServiceImpl.java` | 维度计数、MBTI/非 MBTI 画像生成 |
| 测评 Controller | `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\AssessmentController.java` | `questionId -> optionId` 提交语义 |
| 测评实体 | `AssessmentScale/Question/Option/Record/Answer.java` | DTO 字段来源 |

## 目标设计

### DTO

新增以下 JDK 8 兼容 DTO：

- `AssessmentScaleDto`
- `AssessmentQuestionDto`
- `AssessmentOptionDto`
- `AssessmentSubmitRequest`
- `AssessmentAnswerSnapshot`
- `AssessmentScoreResult`

DTO 位于 `base-common`，不使用 Lombok、JPA、Spring、Jackson。

### 评分服务

新增 `AssessmentScoringService` 到 `base-helper`：

- 输入：量表 DTO + 提交请求。
- 输出：评分结果 DTO。
- 规则：
  - 只对存在且属于该题的选项做维度计数。
  - 答案快照保留用户提交的题目 ID 和选项 ID。
  - MBTI 量表使用四组比较生成画像。
  - 非 MBTI 量表取前三个维度。

### 本次不做

- 不写 Cosmic datamodel。
- 不接 WebAPI。
- 不保存历史记录。
- 不接 AI 洞察。
- 不合并 `UserProfileSnapshot`。

这些属于后续 change。

## 验证

- `openspec validate migrate-assessment-core --strict`
- `openspec validate --all --strict`
- `.\gradlew.bat :v620-cc001-base-helper:test`
- `.\gradlew.bat clean build`

