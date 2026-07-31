# 就业规划任务流搭建蓝图

## 一、平台对象

- 任务流名称：`CyanCruise 就业规划任务流`
- 调用智能体：就业规划智能体
- 授权知识库：`CyanCruise 就业岗位知识库`
- 应用侧运行时配置前缀：`cc001.agent.platform.employment`
- 官方 SDK 入口：`SDKClient.create().getAgentService().runAgent(RunAgentRequest)`

真实的智能体编码、任务流编码和凭据只配置在运行环境，不写入本文、源码或浏览器。

## 二、输入变量

任务流接收一个名为 `question` 的 JSON 字符串，字段如下：

| 字段 | 必填 | 含义 |
| --- | --- | --- |
| `mode` | 是 | 固定为 `EMPLOYMENT_PLANNING` |
| `currentDate` | 是 | CyanCruise 服务端生成请求时的当前日期，格式为 `yyyy-MM-dd` |
| `targetRole` | 否 | 用户明确填写或画像识别出的目标岗位 |
| `profileSummary` | 是 | 当前阶段、准备度、测评分析参考和资料不足项 |
| `resumeEvidence` | 否 | 用户选定简历中的真实标题、方向与正文 |
| `userQuestion` | 是 | “请根据现有资料生成可执行的就业路线图”或用户补充问题 |

不得把用户标识、平台凭据、数据库连接信息放进 `question`。

## 三、节点与变量

1. **开始**：接收 `question`。
2. **解析输入**：提取 `targetRole`、`profileSummary`、`resumeEvidence`、`userQuestion`；缺少目标岗位时标记到 `missingInfo`。
3. **就业知识检索**：使用目标岗位、技能和用户问题检索就业岗位知识库，输出 `retrievalContent`；无可靠依据时输出“当前资料不足”。
4. **就业规划分析**：只基于输入事实和 `retrievalContent` 分析岗位方向、能力差距、风险、阶段动作和衡量标准。
5. **结构化结果**：生成下述 camelCase JSON，禁止 Markdown 代码围栏和额外解释。
6. **结束**：将完整 JSON 作为最终回答返回。

## 四、就业规划分析提示词

```text
你是 CyanCruise 就业规划助手。你只能使用输入中的用户事实和知识库检索结果。
不要编造薪资、招聘数量、企业要求、政策或用户经历；资料不足时必须明确指出。
请给出普通学生能理解的中文路线图，覆盖当前情况、岗位方向、能力差距、风险、
未来 1 年阶段目标、本周行动和每日建议。行动必须具体、可执行、可检查。
测评和深度画像只作为参考，不得表述为唯一职业结论。
最终只输出符合约定的 camelCase JSON，不要输出 Markdown。
```

## 五、结构化输出契约

```json
{
  "targetRole": "Java 开发工程师",
  "startStateSummary": "当前优势、差距、事实依据和资料不足说明",
  "horizonYears": 1,
  "phases": [
    {
      "phaseId": "year-1",
      "horizon": "1年",
      "title": "形成可投递能力",
      "goal": "完成岗位基础能力、项目证据和求职准备",
      "description": "只写有输入或检索依据的判断",
      "status": "TODO",
      "skills": ["岗位所需技能"],
      "actions": ["可执行动作"],
      "kpis": ["可检查的达成标准"],
      "subStages": []
    }
  ],
  "weeklyPlan": {
    "weekTitle": "本周推进重点",
    "weekGoal": "本周可验收目标",
    "actions": ["本周动作"],
    "deliverables": ["本周交付物"],
    "dailySuggestions": ["每天可完成的小事"]
  },
  "dailySuggestions": ["今天可以开始的小事"],
  "weeklyFocus": ["本周最重要的三件事"]
}
```

## 六、试运行用例

1. 有明确目标岗位和简历：确认输出引用真实经历，包含未来 1 年阶段、本周动作和每日建议。
2. 只有模糊方向：确认输出资料不足和不超过三个补充问题，不武断指定唯一岗位。
3. 知识库无依据：确认明确出现“当前资料不足”，不生成薪资或招聘事实。
4. 纯就业问题：确认不输出考研、保研或留学建议。

平台试运行通过后，再在运行环境配置：

```properties
cc001.agent.platform.employment.enabled=true
cc001.agent.platform.employment.agentNumber=<已发布智能体编码>
cc001.agent.platform.employment.taskFlowCode=<就业规划任务流编码>
```
