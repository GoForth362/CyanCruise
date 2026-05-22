## 背景与原因

职业测评是 CareerLoop 主循环里的 P0 能力，负责为用户画像和今日行动建议提供方向基线。IPD 中的测评能力依赖 Spring Boot、JPA、Repository 和 AI 洞察生成；本次迁移先抽取可独立验证的核心评分规则，落成 CyanCruise 中 JDK 8 兼容的 DTO 和纯 Java helper。

## 变更内容

- 新增测评量表、题目、选项、提交请求、答案快照、评分结果等 DTO。
- 新增纯 Java 测评评分服务，支持按选项维度计数。
- 迁移 IPD 的 MBTI 画像生成规则：E/I、S/N、T/F、J/P 四组维度比较。
- 迁移 IPD 的非 MBTI 画像生成规则：按维度计数降序取前三个维度拼接。
- 新增单元测试覆盖 MBTI、Holland/通用量表、无效选项忽略、答案快照等规则。
- 更新迁移映射表，标记职业测评核心规则开始迁移。

## 能力

### 新增能力
- `assessment-core`：定义职业测评核心 DTO、提交评分、画像生成和答案快照规则。

### 修改能力
- `ipd-business-loop`：补充职业测评核心作为 P0 主循环能力的第二个实现切片。

## 影响范围

- `code/base/v620-cc001-base-common/src/main/java/v620/cc001/base/common/dto/career`
- `code/base/v620-cc001-base-helper/src/main/java/v620/base/helper/career`
- `code/base/v620-cc001-base-helper/src/test/java/v620/base/helper/career`
- `docs/ipd-to-cyancruise-migration-map.md`

本次不迁移：

- Cosmic 数据模型持久化。
- 测评页面。
- AI 洞察生成。
- 通知、打卡、微信订阅等提交后的副作用。

