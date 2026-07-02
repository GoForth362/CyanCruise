(function (window) {
  "use strict";

  window.CYANCRUISE_ROUTE_CONFIG = {
    pages: [
      page("workbench", "CyanCruise 首页", "available", "user", "填写用户画像草稿，选择就业或深造路线。", ["snapshot", "onboarding"]),
      page("employment-home", "就业指导", "available", "user", "进入简历和面试核心功能。", ["resumes", "resumeCreate", "resumeDiagnosis", "interviews", "startInterview"]),
      page("further-study-home", "深造护航", "available", "user", "考研、保研和留学方向规划入口。", ["snapshot", "plan"]),
      page("postgraduate", "考研陪伴", "available", "user", "围绕择校、复习计划、错题解析和复试准备完成考研全周期规划。", ["postgraduateSchoolRecommend", "postgraduatePlanGenerate", "postgraduateMistakeAnalyze", "postgraduateReexamPrepare"]),
      page("postgraduate-school", "择校择专业", "available", "user", "输入本科学校、成绩、英语水平和期望地区，生成稳、冲、保三档择校建议。", ["postgraduateSchoolRecommend"], { defaultNav: false }),
      page("postgraduate-plan", "复习计划", "available", "user", "按目标院校、考试日期、科目和每周时间生成基础、提高、冲刺三轮计划。", ["postgraduatePlanGenerate"], { defaultNav: false }),
      page("postgraduate-mistake", "错题解析", "available", "user", "粘贴错题文本，整理答案思路、考点知识树、错因和同类题。", ["postgraduateMistakeAnalyze"], { defaultNav: false }),
      page("postgraduate-reexam", "复试准备", "available", "user", "围绕目标院校、初试状态、材料和项目经历生成复试准备清单。", ["postgraduateReexamPrepare"], { defaultNav: false }),
      page("postgraduate-recommendation", "保研陪伴", "available", "user", "围绕绩点排名、背景提升、营校投递、材料精修和导师联系完成保研规划。", ["recommendationDiagnose", "recommendationPlanGenerate", "recommendationDocumentPolish", "recommendationTutorLetterGenerate"]),
      page("recommendation-ranking", "排名监控", "available", "user", "录入绩点、排名、英语和成果，诊断当前保研竞争力与资格风险。", ["recommendationDiagnose"], { defaultNav: false }),
      page("recommendation-background", "背景提升", "available", "user", "根据当前年级、排名和背景短板生成保研行动计划。", ["recommendationPlanGenerate"], { defaultNav: false }),
      page("recommendation-material", "材料精修", "available", "user", "围绕自述信、邮件或推荐信要点进行结构化润色。", ["recommendationDocumentPolish"], { defaultNav: false }),
      page("recommendation-tutor", "导师联系", "available", "user", "根据目标导师方向和个人背景生成导师意向信。", ["recommendationTutorLetterGenerate"], { defaultNav: false }),
      page("study-abroad", "留学陪伴", "available", "user", "围绕国家地区、语言考试、软实力、选校定位、文书主线和签证网申完成留学申请规划。", ["studyAbroadProfileDiagnose", "studyAbroadLanguagePlan", "studyAbroadSchoolPosition", "studyAbroadStatementOutline", "studyAbroadVisaChecklist"]),
      page("study-abroad-profile", "国家地区", "available", "user", "确认国家地区、目标学位、成绩、预算和软实力经历，生成留学准备度诊断。", ["studyAbroadProfileDiagnose"], { defaultNav: false }),
      page("study-abroad-language", "语言考试", "available", "user", "围绕雅思、托福、GRE 等考试生成分阶段备考计划。", ["studyAbroadLanguagePlan"], { defaultNav: false }),
      page("study-abroad-school", "选校定位", "available", "user", "根据目标地区、专业、成绩、语言和预算生成冲刺、匹配、稳妥三档定位。", ["studyAbroadSchoolPosition"], { defaultNav: false }),
      page("study-abroad-statement", "文书主线", "available", "user", "围绕个人故事、学术经历和目标项目方向生成个人陈述主线。", ["studyAbroadStatementOutline"], { defaultNav: false }),
      page("study-abroad-visa", "签证网申", "available", "user", "根据目标国家、申请季、录取状态和材料状态生成签证与网申清单。", ["studyAbroadVisaChecklist"], { defaultNav: false }),
      page("onboarding", "个人情况", "available", "user", "收集身份、目标岗位、简历状态和偏好信号。", ["onboarding"]),
      page("today-action", "今日行动", "entry-only", "user", "根据路径规划拆解每天应该推进的事项。", ["today"]),
      page("assessment", "职业测评", "entry-only", "user", "通过答题分析人格、性格和偏好，进一步明确用户画像。", ["assessmentSubmit"]),
      page("resume-home", "简历", "available", "user", "简历制作和简历诊断入口。", ["resumes", "resumeCreate", "resumeDiagnosis"]),
      page("resume", "简历制作", "available", "user", "查看简历记录，创建元数据，并关联文件能力。", ["resumes", "resumeCreate", "resumeDelete"]),
      page("file-upload-preview", "文件上传预览", "entry-only", "user", "展示上传、预览、下载、删除和文本抽取契约。", ["fileUpload", "filePreview", "fileDownload", "fileDelete", "fileExtractText"], { defaultNav: false, debugNav: true }),
      page("resume-diagnosis", "简历诊断", "available", "user", "围绕目标岗位分析匹配度、关键词和建议。", ["resumes", "snapshot", "filePreview", "resumeDiagnosis", "keywordStatus"]),
      page("career-plan", "路径规划", "entry-only", "user", "根据用户方向和画像生成实现路径规划，后续接入规划智能体。", ["plan", "ensurePlan"]),
      page("interview-home", "面试中心", "available", "user", "选择 AI 模拟面试或全景仿真面试，并进入独立记录页。", ["interviews"]),
      page("interview", "AI 模拟面试", "available", "user", "围绕目标岗位完成文字问答练习和复盘。", ["guidedInterviewStart", "guidedInterviewAnswer", "guidedInterviewFinish"]),
      page("interview-history", "AI 模拟面试记录", "available", "user", "分页查看已保存的 AI 模拟面试记录。", ["interviewPage", "guidedInterviewFinish", "interviewMessages", "interviewDelete"]),
      page("interview-panorama", "全景仿真面试", "available", "user", "在沉浸式面试环境中使用摄像头与 AI 面试官面对面练习。", ["interviews", "guidedInterviewStart", "guidedInterviewAnswer", "guidedInterviewFinish"]),
      page("interview-panorama-history", "全景仿真面试记录", "available", "user", "分页查看已保存的全景仿真面试记录。", ["interviewPage", "guidedInterviewFinish", "interviewMessages", "interviewDelete"]),
      page("assistant", "求职助手", "available", "user", "发送助手问题并查看会话历史入口。", ["assistantSend", "assistantSessions"]),
      page("messages", "消息中心", "available", "user", "查看站内通知、未读数、订阅配额和周报入口。", ["notifications", "notificationUnread", "notificationRead", "notificationReadAll", "notificationDelete", "subscriptionQuota", "weeklyReport"]),
      page("employment-insight", "就业洞察", "available", "user", "按学校、专业和目标岗位查看就业洞察。", ["employmentInsight"]),
      page("career-resources", "职业资源", "available", "public", "查看文章、视频、咨询和职业路径资源。", ["careerResources"], { defaultNav: false, debugNav: true }),
      page("admin-console", "管理后台", "entry-only", "admin", "管理员治理入口，仅对 ADMIN 或平台管理员开放。", ["adminWhoami", "adminDashboard", "adminUsersBan", "adminQuestions", "adminContent", "adminBroadcast", "adminAuditLog"], { defaultNav: false, debugNav: true })
    ],

    featureGroups: {
      "employment-home": [
        feature("简历制作", "简", "上传或创建简历，关联 PDF 并维护简历记录", "resume", "已接入"),
        feature("简历诊断", "诊", "围绕目标岗位诊断简历匹配度和优化建议", "resume-diagnosis", "已接入"),
        feature("面试中心", "面", "选择全景仿真面试或 AI 模拟面试", "interview-home", "已接入")
      ],
      "resume-home": [
        feature("简历制作", "简", "上传或创建简历，关联 PDF 并维护简历记录", "resume", "已接入"),
        feature("简历诊断", "诊", "围绕目标岗位给出简历优化建议", "resume-diagnosis", "已接入")
      ],
      "interview-home": [
        feature("全景仿真面试", "仿", "开启摄像头进入沉浸式面试房间", "interview-panorama", "已接入"),
        feature("AI 模拟面试", "面", "进入 AI 文字问答练习并查看该类型记录", "interview", "已接入")
      ],
      "further-study-home": [
        feature("考研陪伴", "考", "完成择校建议、复习计划、错题解析和复试准备", "postgraduate", "已接入"),
        feature("保研陪伴", "保", "诊断保研竞争力，精修文书并生成导师意向信", "postgraduate-recommendation", "已接入"),
        feature("留学陪伴", "留", "拆解语言考试、选校定位、文书主线和签证网申清单", "study-abroad", "已接入")
      ]
    }
  };

  function page(key, title, status, audience, summary, endpointKeys, options) {
    var meta = options || {};
    return {
      key: key,
      title: title,
      status: status,
      audience: audience,
      summary: summary,
      endpoints: endpointKeys || [],
      defaultNav: meta.defaultNav !== false,
      debugNav: meta.debugNav !== false
    };
  }

  function feature(title, icon, summary, route, status) {
    return {
      title: title,
      icon: icon,
      summary: summary,
      route: route,
      status: status || "已接入"
    };
  }
}(window));
