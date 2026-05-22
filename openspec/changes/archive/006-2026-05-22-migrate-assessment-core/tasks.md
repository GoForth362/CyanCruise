## 1. 来源盘点

- [x] 1.1 阅读 IPD `AssessmentService`。
- [x] 1.2 阅读 IPD `AssessmentServiceImpl`。
- [x] 1.3 阅读 IPD `AssessmentController`。
- [x] 1.4 阅读 IPD `AssessmentScale/Question/Option/Record/Answer` 实体。

## 2. DTO

- [x] 2.1 新增测评量表 DTO。
- [x] 2.2 新增测评题目 DTO。
- [x] 2.3 新增测评选项 DTO。
- [x] 2.4 新增测评提交请求 DTO。
- [x] 2.5 新增答案快照 DTO。
- [x] 2.6 新增评分结果 DTO。

## 3. 评分规则

- [x] 3.1 新增纯 Java `AssessmentScoringService`。
- [x] 3.2 实现选项维度计数。
- [x] 3.3 实现 MBTI 四维画像生成。
- [x] 3.4 实现非 MBTI 前三维画像生成。
- [x] 3.5 保留无效提交的答案快照但不计入维度。

## 4. 测试

- [x] 4.1 覆盖 MBTI 画像计算。
- [x] 4.2 覆盖 MBTI 同分规则。
- [x] 4.3 覆盖非 MBTI/Holland 前三维规则。
- [x] 4.4 覆盖无效选项不计分。
- [x] 4.5 覆盖答案快照。

## 5. 文档

- [x] 5.1 更新迁移映射表中职业测评状态。

## 6. 验证

- [x] 6.1 运行 `openspec validate migrate-assessment-core --strict`。
- [x] 6.2 运行 `openspec validate --all --strict`。
- [x] 6.3 运行 helper 测试。
- [x] 6.4 运行 JDK 8 Gradle 构建。
