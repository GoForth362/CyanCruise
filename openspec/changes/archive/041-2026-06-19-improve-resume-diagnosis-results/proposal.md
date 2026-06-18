## Why

当前简历诊断结果直接展示内部上下文标识、英文状态和行业缩写，诊断分数也没有评分标准，用户无法判断分数从何而来或下一步具体改什么。需要把规则诊断结果改造成可解释、可执行、普通用户能理解的反馈。

## What Changes

- 将基础规则评分调整为四项合计 100 分：内容完整度、目标岗位匹配、经历证据、表达清晰度。
- 诊断响应增加各项得分、满分和评分依据，页面展示评分标准及本次得分原因。
- 基于真实简历信号生成多条具体建议，包括缺少量化成果、岗位关键词不足、经历证据不足和内容过短等。
- 页面把 `HIGH`、`projects`、`TODO`、`resume.targetJob` 等内部值转换为用户可理解的中文，不展示 `JD` 等行业缩写。
- 保持旧 `overallScore`、`strengths`、`weaknesses` 和 `suggestions` 字段兼容，不新增依赖。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `resume-diagnosis`: 增加可解释评分明细和针对简历内容的规则建议。
- `webapp-careerloop-pages`: 优化诊断结果展示，隐藏内部标识并使用普通中文。

## Impact

- 目标模块：`code/base/v620-cc001-base-common/` 评分明细 DTO，`base-helper` 解析兼容，`cloud01` 默认诊断规则，`webapp/isv/v620/cyancruise` 结果页面。
- 数据语义：总分仍为 0-100；新增四项评分明细只扩展响应，不破坏旧调用方。
- 暂不迁移：agent 深度语义判断、逐句智能改写、招聘方偏好预测。
- 依赖：不新增依赖，保持 JDK 1.8 和 Cosmic/KDDT 模板约束。
- 验证：OpenSpec strict、helper/analyzer/application focused tests、前端语法和 route 校验、JDK 8 Gradle 构建。
