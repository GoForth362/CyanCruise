# CyanCruise Apifox 接口测试文档

本文档用于在 Apifox 中调试 CyanCruise 后端接口。当前项目在本地 Cosmic 8.0.4 运行态中不直接暴露 `POST /ierp/cc001/*`，统一通过苍穹自定义 WebAPI 路由转发。

## 1. Apifox 环境变量

建议在 Apifox 环境中配置：

| 变量 | 示例值 | 说明 |
| --- | --- | --- |
| `baseUrl` | `http://10.0.0.8:8080` | 苍穹服务地址，不带 `/ierp` |
| `accessToken` | `<KAPI access_token>` | 第三方应用访问令牌 |
| `userId` | `2477190919195983874` | 普通用户 ID，本地联调可用当前平台用户 |
| `adminId` | `2477190919195983874` | 管理员 ID，需具备管理权限 |

## 2. 统一请求入口

所有业务接口测试都使用：

```http
POST {{baseUrl}}/ierp/kapi/v2/v620/v620_cc001/cc001/cyancruise/route?access_token={{accessToken}}
Content-Type: application/json
```

请求体统一为：

```json
{
  "path": "/cc001/identity/current",
  "body": {}
}
```

响应通常为：

```json
{
  "success": true,
  "data": {}
}
```

失败时通常为：

```json
{
  "success": false,
  "errorCode": "IDENTITY_REQUIRED",
  "message": "..."
}
```

## 3. Apifox 导入集合

可直接导入同目录下的 Postman Collection：

```text
docs/cyancruise-apifox.postman_collection.json
```

导入后在 Apifox 中切换环境，填入 `baseUrl`、`accessToken`、`userId`、`adminId` 即可逐个发送。

## 4. 接口清单

### 身份

| 名称 | `path` | `body` 示例 |
| --- | --- | --- |
| 获取当前平台身份 | `/cc001/identity/current` | `{}` |

### 职业画像与今日任务

| 名称 | `path` | `body` 示例 |
| --- | --- | --- |
| 获取画像快照 | `/cc001/career-profile/snapshot/get` | `{"userId":"{{userId}}"}` |
| 获取画像草稿 | `/cc001/career-profile/draft/get` | `{"userId":"{{userId}}"}` |
| 保存画像草稿 | `/cc001/career-profile/draft/save` | `{"userId":"{{userId}}","draft":{"targetRole":"Java开发工程师","educationStage":"本科"}}` |
| 清空画像草稿 | `/cc001/career-profile/draft/clear` | `{"userId":"{{userId}}"}` |
| 保存入门引导信息 | `/cc001/career-profile/onboarding/save` | `{"userId":"{{userId}}","request":{"identityType":"student","stage":"本科","targetRole":"Java开发工程师","hasResume":"yes","weeklyAvailability":"6小时"}}` |
| 获取今日任务 | `/cc001/career-agent/today/get` | `{"userId":"{{userId}}"}` |

### 职业测评

| 名称 | `path` | `body` 示例 |
| --- | --- | --- |
| 获取量表列表 | `/cc001/assessment/scales` | `{}` |
| 获取量表题目 | `/cc001/assessment/questions` | `{"scaleId":1}` |
| 提交测评答案 | `/cc001/assessment/submit` | `{"userId":"{{userId}}","scale":{"scaleId":1,"title":"职业兴趣测评"},"request":{"scaleId":1,"answers":{"1":2,"2":3}}}` |
| 获取测评记录 | `/cc001/assessment/records` | `{"userId":"{{userId}}"}` |
| 获取单条测评记录 | `/cc001/assessment/record/get` | `{"userId":"{{userId}}","recordId":1}` |

### 简历与简历诊断

| 名称 | `path` | `body` 示例 |
| --- | --- | --- |
| 获取简历列表 | `/cc001/resume/list` | `{"userId":"{{userId}}"}` |
| 创建简历 | `/cc001/resume/create` | `{"userId":"{{userId}}","request":{"title":"Java开发工程师简历","targetRole":"Java开发工程师","content":"教育经历、项目经历、技能栈..."}}` |
| 删除简历 | `/cc001/resume/delete` | `{"userId":"{{userId}}","resumeId":1}` |
| 分析简历 | `/cc001/resume-diagnosis/analyze` | `{"userId":"{{userId}}","request":{"resumeId":1,"force":true}}` |
| 获取关键词状态 | `/cc001/resume-diagnosis/keywords/status` | `{"userId":"{{userId}}","resumeId":1}` |

### 职业计划、模拟面试与助手

| 名称 | `path` | `body` 示例 |
| --- | --- | --- |
| 获取职业计划摘要 | `/cc001/career-plan/summary` | `{"userId":"{{userId}}"}` |
| 确保生成职业计划 | `/cc001/career-plan/ensure` | `{"userId":"{{userId}}"}` |
| 获取今天的路线任务 | `/cc001/career-plan/daily/get` | `{"userId":"{{userId}}"}` |
| 更新每日任务状态 | `/cc001/career-plan/daily/task/update` | `{"userId":"{{userId}}","request":{"taskId":"daily-v1-2026-07-16-0-xxxx","completed":true}}` |
| 获取面试列表 | `/cc001/interview/list` | `{"userId":"{{userId}}"}` |
| 分页获取面试列表 | `/cc001/interview/page` | `{"userId":"{{userId}}","page":1,"mode":"text"}` |
| 开始模拟面试 | `/cc001/interview/start` | `{"userId":"{{userId}}","request":{"positionName":"Java开发工程师","difficulty":"normal","mode":"text"}}` |
| 开始引导式面试 | `/cc001/interview/guided/start` | `{"userId":"{{userId}}","request":{"positionName":"Java开发工程师","difficulty":"normal","mode":"text"}}` |
| 提交面试回答 | `/cc001/interview/guided/answer` | `{"userId":"{{userId}}","interviewId":1,"answer":"我负责了用户画像模块..."}` |
| 结束引导式面试 | `/cc001/interview/guided/finish` | `{"userId":"{{userId}}","interviewId":1}` |
| 获取面试消息 | `/cc001/interview/messages` | `{"userId":"{{userId}}","interviewId":1}` |
| 删除面试 | `/cc001/interview/delete` | `{"userId":"{{userId}}","interviewId":1}` |
| 发送助手消息 | `/cc001/assistant-chat/send` | `{"userId":"{{userId}}","request":{"message":"帮我优化简历项目经历","persona":"career-coach"}}` |
| 获取助手会话列表 | `/cc001/assistant-chat/session/list` | `{"userId":"{{userId}}"}` |

### 就业洞察、通知与文件

| 名称 | `path` | `body` 示例 |
| --- | --- | --- |
| 获取就业洞察 | `/cc001/career-employment/insight/get` | `{"userId":"{{userId}}"}` |
| 获取就业资源 | `/cc001/career-employment/resources/list` | `{"userId":"{{userId}}"}` |
| 获取通知列表 | `/cc001/notifications/list` | `{"userId":"{{userId}}"}` |
| 获取未读数 | `/cc001/notifications/unread-count` | `{"userId":"{{userId}}"}` |
| 标记通知已读 | `/cc001/notifications/read` | `{"userId":"{{userId}}","notificationId":"notice-1"}` |
| 全部标记已读 | `/cc001/notifications/read-all` | `{"userId":"{{userId}}"}` |
| 删除通知 | `/cc001/notifications/delete` | `{"userId":"{{userId}}","notificationId":"notice-1"}` |
| 查询订阅配额 | `/cc001/notifications/subscription/quota` | `{"userId":"{{userId}}"}` |
| 生成周报 | `/cc001/notifications/weekly-report/run` | `{"userId":"{{userId}}","highlights":["完成简历初稿","练习一次面试"]}` |
| 上传文件 | `/cc001/files/upload` | `{"folder":"resume","originalFilename":"resume.txt","base64":"5rWL6K+V5paH5pys"}` |
| 获取预览链接 | `/cc001/files/preview-url` | `{"fileUrlOrKey":"resume/resume.txt","ttlSeconds":3600}` |
| 下载文件 | `/cc001/files/download` | `{"fileUrlOrKey":"resume/resume.txt"}` |
| 删除文件 | `/cc001/files/delete` | `{"fileUrlOrKey":"resume/resume.txt"}` |
| 提取文件文本 | `/cc001/files/extract-text` | `{"fileUrlOrKey":"resume/resume.txt"}` |

### 升学规划

| 名称 | `path` | `body` 示例 |
| --- | --- | --- |
| 考研院校推荐 | `/cc001/postgraduate/school-recommend` | `{"userId":"{{userId}}","request":{"major":"计算机科学与技术","targetRegion":"上海","scoreRange":"360-390"}}` |
| 生成考研计划 | `/cc001/postgraduate/plan/generate` | `{"userId":"{{userId}}","request":{"targetSchool":"复旦大学","targetMajor":"计算机技术","monthsLeft":8}}` |
| 分析错题 | `/cc001/postgraduate/mistake/analyze` | `{"userId":"{{userId}}","request":{"subject":"数学","mistakeText":"极限计算错误"}}` |
| 复试准备 | `/cc001/postgraduate/reexam/prepare` | `{"userId":"{{userId}}","request":{"targetSchool":"复旦大学","targetMajor":"计算机技术"}}` |
| 保研画像诊断 | `/cc001/recommendation/diagnose` | `{"userId":"{{userId}}","request":{"gpa":"3.7/4.0","research":"一段科研经历"}}` |
| 生成保研计划 | `/cc001/recommendation/plan/generate` | `{"userId":"{{userId}}","request":{"gpa":"3.7/4.0","targetSchools":["浙江大学"]}}` |
| 润色申请文书 | `/cc001/recommendation/document/polish` | `{"userId":"{{userId}}","request":{"documentType":"个人陈述","content":"我希望申请..."}}` |
| 生成导师联系信 | `/cc001/recommendation/tutor-letter/generate` | `{"userId":"{{userId}}","request":{"teacherName":"张老师","researchInterest":"自然语言处理"}}` |
| 留学画像诊断 | `/cc001/study-abroad/profile/diagnose` | `{"userId":"{{userId}}","request":{"gpa":"3.6/4.0","languageScore":"雅思 7.0","targetCountry":"英国"}}` |
| 生成语言备考计划 | `/cc001/study-abroad/language/plan` | `{"userId":"{{userId}}","request":{"testType":"IELTS","currentScore":"6.0","targetScore":"7.0","weeks":12}}` |
| 留学院校定位 | `/cc001/study-abroad/school/position` | `{"userId":"{{userId}}","request":{"targetCountry":"英国","major":"Data Science","gpa":"3.6/4.0"}}` |
| 生成文书大纲 | `/cc001/study-abroad/statement/outline` | `{"userId":"{{userId}}","request":{"program":"Data Science","experiences":["推荐系统项目"]}}` |
| 生成签证清单 | `/cc001/study-abroad/visa/checklist` | `{"userId":"{{userId}}","request":{"country":"英国","school":"University of Manchester"}}` |
| 保存升学目标 | `/cc001/further-study/target/save` | `{"userId":"{{userId}}","request":{"track":"postgraduate","targetSchool":"复旦大学","targetMajor":"计算机技术","status":"active"}}` |
| 查询升学记录 | `/cc001/further-study/records/list` | `{"userId":"{{userId}}","request":{"track":"postgraduate","limit":20,"offset":0}}` |
| 查看升学记录详情 | `/cc001/further-study/records/detail` | `{"userId":"{{userId}}","recordId":"record-1"}` |
| 更新升学记录状态 | `/cc001/further-study/records/status/update` | `{"userId":"{{userId}}","request":{"recordId":"record-1","status":"done","eventSummary":"已完成材料初稿"}}` |
| 保存升学材料 | `/cc001/further-study/materials/save` | `{"userId":"{{userId}}","request":{"track":"postgraduate","recordId":"record-1","materialType":"statement","title":"个人陈述","contentJson":"{}"}}` |
| 查询升学材料 | `/cc001/further-study/materials/list` | `{"userId":"{{userId}}","track":"postgraduate","recordId":"record-1"}` |
| 查询升学记录事件 | `/cc001/further-study/records/events` | `{"userId":"{{userId}}","recordId":"record-1"}` |

### 管理端

| 名称 | `path` | `body` 示例 |
| --- | --- | --- |
| 查看管理员身份 | `/cc001/admin/whoami` | `{"adminId":"{{adminId}}"}` |
| 组织看板 | `/cc001/admin/organizations/dashboard` | `{"adminId":"{{adminId}}","orgId":"default"}` |
| 组织学生列表 | `/cc001/admin/organizations/students` | `{"adminId":"{{adminId}}","orgId":"default"}` |
| 用户列表 | `/cc001/admin/users/list` | `{"adminId":"{{adminId}}","page":1,"size":20,"keyword":""}` |
| 禁用用户 | `/cc001/admin/users/ban` | `{"adminId":"{{adminId}}","userId":"{{userId}}","reason":"测试禁用"}` |
| 解除禁用用户 | `/cc001/admin/users/unban` | `{"adminId":"{{adminId}}","userId":"{{userId}}"}` |
| 面试题列表 | `/cc001/admin/questions/list` | `{"adminId":"{{adminId}}","source":"","reviewStatus":""}` |
| 保存面试题 | `/cc001/admin/questions/save` | `{"adminId":"{{adminId}}","question":{"position":"Java开发工程师","difficulty":"normal","content":"请介绍一个项目经历","answer":"可按经历讲述框架回答"}}` |
| 更新面试题 | `/cc001/admin/questions/update` | `{"adminId":"{{adminId}}","questionId":"question-1","patch":{"summary":"项目经历题"}}` |
| 通过面试题 | `/cc001/admin/questions/approve` | `{"adminId":"{{adminId}}","questionId":"question-1"}` |
| 拒绝面试题 | `/cc001/admin/questions/reject` | `{"adminId":"{{adminId}}","questionId":"question-1"}` |
| 删除面试题 | `/cc001/admin/questions/delete` | `{"adminId":"{{adminId}}","questionId":"question-1"}` |
| 保存测评题 | `/cc001/admin/assessment/questions/save` | `{"adminId":"{{adminId}}","scaleId":1,"question":{"questionText":"我喜欢分析复杂问题","questionType":"single","sortOrder":1,"options":[]}}` |
| 删除测评题 | `/cc001/admin/assessment/questions/delete` | `{"adminId":"{{adminId}}","scaleId":1,"questionId":1}` |
| 内容列表 | `/cc001/admin/content/list` | `{"adminId":"{{adminId}}","type":"resource"}` |
| 保存内容 | `/cc001/admin/content/save` | `{"adminId":"{{adminId}}","content":{"type":"resource","title":"简历写作指南","summary":"适合应届生的简历建议","category":"resume"}}` |
| 置顶内容 | `/cc001/admin/content/pin` | `{"adminId":"{{adminId}}","contentId":"content-1"}` |
| 隐藏内容 | `/cc001/admin/content/hide` | `{"adminId":"{{adminId}}","contentId":"content-1"}` |
| 删除内容 | `/cc001/admin/content/delete` | `{"adminId":"{{adminId}}","contentId":"content-1"}` |
| 发送广播 | `/cc001/admin/broadcast` | `{"adminId":"{{adminId}}","request":{"userIds":["{{userId}}"],"title":"测试通知","content":"这是一条测试广播"}}` |
| 运营统计 | `/cc001/admin/analytics/summary` | `{"adminId":"{{adminId}}"}` |
| 审计日志 | `/cc001/admin/audit-log/list` | `{"adminId":"{{adminId}}","page":1,"size":20}` |

## 5. 常见问题

- 直接请求 `/ierp/cc001/...` 返回 404：本地运行态不直接暴露这些路径，请使用 KAPI 统一入口。
- 返回 `IDENTITY_REQUIRED`：检查 JVM 是否开启 `cc001.identity.adapter.enabled=true`、`cc001.identity.login.provider.enabled=true`，并确认自定义 API 已注册授权。
- 返回 `USER_BANNED`：当前用户被管理端禁用，需使用管理端解除禁用。
- 管理端接口失败：确认 `adminId` 对应用户具备管理权限。

# 升学路线规划与今日行动

升学中心使用独立于就业规划的接口和数据库记录。调用 `/cc001/study-center/selection/save` 保存 `POSTGRADUATE`、`RECOMMENDATION` 或 `STUDY_ABROAD` 后，可使用：

- `POST /cc001/study-center/plan/summary`
- `POST /cc001/study-center/plan/ensure`
- `POST /cc001/study-center/plan/generate`
- `POST /cc001/study-center/daily/get`
- `POST /cc001/study-center/daily/task/update`

除任务更新接口使用 `{ "userId": "...", "request": { "taskId": "...", "completed": true } }` 外，其余规划和每日行动接口请求体均为 `{ "userId": "..." }`。生成失败时原升学规划保持不变；切换当前路线不会删除就业规划。
