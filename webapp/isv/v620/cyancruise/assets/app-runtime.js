(function () {
  "use strict";

  var APP_VERSION = "20260719-cyancruise-v249";
  var ACTIVE_USER_STORAGE_KEY = "cyancruise.activeUserId";
  var USER_SCOPED_STORAGE_KEYS = [
    "cyancruise.homeIntent",
    "cyancruise.homeIntentSaved",
    "cyancruise.homeIntentEditing",
    "cyancruise.homeProfileExpanded",
    "cyancruise.previewProfile"
  ];

  var endpoints = {
    snapshot: "/cc001/career-profile/snapshot/get",
    draft: "/cc001/career-profile/draft/get",
    draftSave: "/cc001/career-profile/draft/save",
    draftClear: "/cc001/career-profile/draft/clear",
    onboarding: "/cc001/career-profile/onboarding/save",
    today: "/cc001/career-agent/today/get",
    assessmentScales: "/cc001/assessment/scales",
    assessmentQuestions: "/cc001/assessment/questions",
    assessmentSubmit: "/cc001/assessment/submit",
    assessmentAiInterpretation: "/cc001/assessment/ai-interpretation/generate",
    assessmentRecords: "/cc001/assessment/records",
    assessmentRecord: "/cc001/assessment/record/get",
    deepProfileGenerate: "/cc001/career-profile/deep-profile/generate",
    deepProfileLatest: "/cc001/career-profile/deep-profile/latest",
    deepProfileHistory: "/cc001/career-profile/deep-profile/history",
    deepProfileDetail: "/cc001/career-profile/deep-profile/detail",
    resumes: "/cc001/resume/list",
    resumeCreate: "/cc001/resume/create",
    resumeDelete: "/cc001/resume/delete",
    plan: "/cc001/career-plan/summary",
    ensurePlan: "/cc001/career-plan/ensure",
    dailyPlan: "/cc001/career-plan/daily/get",
    dailyTaskUpdate: "/cc001/career-plan/daily/task/update",
    studyPlan: "/cc001/study-center/plan/summary",
    studyEnsurePlan: "/cc001/study-center/plan/ensure",
    studyGeneratePlan: "/cc001/study-center/plan/generate",
    studyDailyPlan: "/cc001/study-center/daily/get",
    studyDailyTaskUpdate: "/cc001/study-center/daily/task/update",
    studyMaterialUpload: "/cc001/study-center/materials/upload",
    studyMaterialList: "/cc001/study-center/materials/list",
    studyMaterialDelete: "/cc001/study-center/materials/delete",
    interviews: "/cc001/interview/list",
    interviewPage: "/cc001/interview/page",
    startInterview: "/cc001/interview/start",
    guidedInterviewStart: "/cc001/interview/guided/start",
    guidedInterviewAnswer: "/cc001/interview/guided/answer",
    guidedInterviewFinish: "/cc001/interview/guided/finish",
    interviewMessages: "/cc001/interview/messages",
    interviewDelete: "/cc001/interview/delete",
    assistantSend: "/cc001/assistant-chat/send",
    assistantSessions: "/cc001/assistant-chat/session/list",
    employmentInsight: "/cc001/career-employment/insight/get",
    careerResources: "/cc001/career-employment/resources/list",
    studyCenterSelection: "/cc001/study-center/selection/get",
    studyCenterSelectionSave: "/cc001/study-center/selection/save",
    studyCenterInsight: "/cc001/study-center/insight/get",
    studyCenterResources: "/cc001/study-center/resources/list",
    notifications: "/cc001/notifications/list",
    notificationUnread: "/cc001/notifications/unread-count",
    notificationRead: "/cc001/notifications/read",
    notificationReadAll: "/cc001/notifications/read-all",
    notificationDelete: "/cc001/notifications/delete",
    subscriptionQuota: "/cc001/notifications/subscription/quota",
    weeklyReport: "/cc001/notifications/weekly-report/run",
    adminWhoami: "/cc001/admin/whoami",
    adminOrganizations: "/cc001/admin/organizations/list",
    adminDashboard: "/cc001/admin/organizations/dashboard",
    adminUsers: "/cc001/admin/users/list",
    adminUsersBan: "/cc001/admin/users/ban",
    adminUsersUnban: "/cc001/admin/users/unban",
    adminQuestions: "/cc001/admin/questions/list",
    adminQuestionSave: "/cc001/admin/questions/save",
    adminQuestionUpdate: "/cc001/admin/questions/update",
    adminQuestionApprove: "/cc001/admin/questions/approve",
    adminQuestionReject: "/cc001/admin/questions/reject",
    adminQuestionDelete: "/cc001/admin/questions/delete",
    adminAssessmentQuestionSave: "/cc001/admin/assessment/questions/save",
    adminAssessmentQuestionDelete: "/cc001/admin/assessment/questions/delete",
    adminAssessmentCatalog: "/cc001/admin/assessment/catalog",
    adminAssessmentScaleSave: "/cc001/admin/assessment/scales/save",
    adminContent: "/cc001/admin/content/list",
    adminContentSave: "/cc001/admin/content/save",
    adminContentPin: "/cc001/admin/content/pin",
    adminContentHide: "/cc001/admin/content/hide",
    adminContentDelete: "/cc001/admin/content/delete",
    studyCenterAdminResources: "/cc001/study-center/admin/resources/list",
    studyCenterAdminResourceSave: "/cc001/study-center/admin/resources/save",
    studyCenterAdminResourcePin: "/cc001/study-center/admin/resources/pin",
    studyCenterAdminResourceHide: "/cc001/study-center/admin/resources/hide",
    studyCenterAdminResourceDelete: "/cc001/study-center/admin/resources/delete",
    adminBroadcast: "/cc001/admin/broadcast",
    adminAnalytics: "/cc001/admin/analytics/summary",
    adminAuditLog: "/cc001/admin/audit-log/list",
    identityCurrent: "/cc001/identity/current",
    fileUpload: "/cc001/files/upload",
    filePreview: "/cc001/files/preview-url",
    fileDownload: "/cc001/files/download",
    fileDelete: "/cc001/files/delete",
    fileExtractText: "/cc001/files/extract-text",
    resumeDiagnosis: "/cc001/resume-diagnosis/analyze",
    resumeDiagnosisHistory: "/cc001/resume-diagnosis/history/list",
    resumeDiagnosisHistoryDelete: "/cc001/resume-diagnosis/history/delete",
    keywordStatus: "/cc001/resume-diagnosis/keywords/status",
    postgraduateSchoolRecommend: "/cc001/postgraduate/school-recommend",
    postgraduatePlanGenerate: "/cc001/postgraduate/plan/generate",
    postgraduateMistakeAnalyze: "/cc001/postgraduate/mistake/analyze",
    postgraduateReexamPrepare: "/cc001/postgraduate/reexam/prepare",
    recommendationDiagnose: "/cc001/recommendation/diagnose",
    recommendationPlanGenerate: "/cc001/recommendation/plan/generate",
    recommendationDocumentPolish: "/cc001/recommendation/document/polish",
    recommendationTutorLetterGenerate: "/cc001/recommendation/tutor-letter/generate",
    studyAbroadProfileDiagnose: "/cc001/study-abroad/profile/diagnose",
    studyAbroadLanguagePlan: "/cc001/study-abroad/language/plan",
    studyAbroadSchoolPosition: "/cc001/study-abroad/school/position",
    studyAbroadStatementOutline: "/cc001/study-abroad/statement/outline",
    studyAbroadVisaChecklist: "/cc001/study-abroad/visa/checklist",
    furtherStudyRecordsList: "/cc001/further-study/records/list",
    furtherStudyRecordDetail: "/cc001/further-study/records/detail",
    furtherStudyRecordStatusUpdate: "/cc001/further-study/records/status/update",
    furtherStudyMaterialSave: "/cc001/further-study/materials/save",
    furtherStudyMaterialList: "/cc001/further-study/materials/list",
    furtherStudyRecordEvents: "/cc001/further-study/records/events",
    serverManagedApiCode: "cc001/cyancruise/route"
  };

  var agentAssistant = {
    h5Url: "http://10.0.0.8:8080/ierp/ai/h5/chat.do?accountId=1565321489509515264&assistant=2521954995352373251",
    statusNote: "金蝶平台助手已发布为 H5 入口；当前平台侧任务流运行仍需修复大模型节点调用异常。"
  };

  var pages = [
    page("workbench", "CyanCruise 首页", "available", "user", "填写自画像草稿，选择就业或深造路线。", ["snapshot", "onboarding"]),
    page("employment-home", "就业中心", "available", "user", "查看就业洞察、简历准备、面试练习与就业资讯。", ["resumes", "resumeCreate", "resumeDiagnosis"]),
    page("further-study-home", "升学中心", "available", "user", "选择升学方向，管理规划资料并生成个性化升学路线。", ["snapshot", "studyPlan", "studyEnsurePlan", "studyGeneratePlan", "studyDailyPlan", "studyMaterialUpload", "studyMaterialList", "studyMaterialDelete", "studyCenterResources"]),
    page("study-resources", "全部升学资源", "available", "public", "查看升学官方服务、精选文章和相关视频。", ["studyCenterResources"], { defaultNav: false, debugNav: false }),
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
    page("assessment", "画像补全", "entry-only", "user", "先补充人格、性格和偏好信息，让后续路径规划和行动建议更准确。", ["assessmentSubmit"]),
    page("deep-profile-detail", "深度画像详情", "entry-only", "user", "查看最新深度画像的完整分析和过往生成记录。", ["deepProfileLatest", "deepProfileHistory", "deepProfileDetail"], { defaultNav: false }),
    page("resume", "简历", "available", "user", "查看简历记录，创建元数据，并关联文件能力。", ["resumes", "resumeCreate", "resumeDelete"]),
    page("file-upload-preview", "文件上传预览", "entry-only", "user", "展示上传、预览、下载、删除和文本抽取契约。", ["fileUpload", "filePreview", "fileDownload", "fileDelete", "fileExtractText"], { defaultNav: false, debugNav: true }),
    page("resume-diagnosis", "简历诊断", "available", "user", "围绕目标岗位分析匹配度、关键词和建议。", ["resumes", "snapshot", "filePreview", "resumeDiagnosis", "keywordStatus"]),
    page("career-plan", "路径规划", "entry-only", "user", "根据当前就业或升学路线展示对应的独立规划。", ["plan", "ensurePlan", "studyPlan", "studyEnsurePlan"]),
    page("interview", "AI 模拟面试", "available", "user", "围绕目标岗位完成文字问答练习和复盘。", ["guidedInterviewStart", "guidedInterviewAnswer", "guidedInterviewFinish"]),
    page("interview-history", "AI 模拟面试记录", "available", "user", "分页查看已保存的 AI 模拟面试记录。", ["interviewPage", "guidedInterviewFinish", "interviewMessages", "interviewDelete"], { defaultNav: false }),
    page("interview-panorama", "全景仿真面试", "available", "user", "在沉浸式面试环境中使用摄像头与 AI 面试官面对面练习。", ["interviews", "guidedInterviewStart", "guidedInterviewAnswer", "guidedInterviewFinish"]),
    page("interview-panorama-history", "全景仿真面试记录", "available", "user", "分页查看已保存的全景仿真面试记录。", ["interviewPage", "guidedInterviewFinish", "interviewMessages", "interviewDelete"], { defaultNav: false }),
    page("assistant", "求职助手", "available", "user", "发送助手问题并查看会话历史入口。", ["assistantSend", "assistantSessions"]),
    page("messages", "消息中心", "available", "user", "查看站内通知、未读数、订阅配额和周报入口。", ["notifications", "notificationUnread", "notificationRead", "notificationReadAll", "notificationDelete", "subscriptionQuota", "weeklyReport"]),
    page("message-detail", "消息详情", "entry-only", "user", "查看站内消息的完整正文。", ["notifications", "notificationRead"], { defaultNav: false }),
    page("employment-insight", "就业洞察", "available", "user", "按学校、专业和目标岗位查看就业洞察。", ["employmentInsight"]),
    page("career-resources", "职业资源", "available", "public", "查看文章、视频、咨询和职业路径资源。", ["careerResources"], { defaultNav: false, debugNav: true }),
    page("admin-console", "管理后台", "entry-only", "admin", "管理员治理入口，仅对 ADMIN 或平台管理员开放。", ["adminWhoami", "adminDashboard", "adminUsersBan", "adminQuestions", "adminContent", "adminBroadcast", "adminAuditLog"], { defaultNav: false, debugNav: true })
  ];

  var pageByKey = {};
  var featureGroups = {
    "employment-home": [
      feature("简历制作", "简", "上传或创建简历，关联 PDF 并维护简历记录", "resume", "已接入"),
      feature("简历诊断", "诊", "围绕目标岗位诊断简历匹配度和优化建议", "resume-diagnosis", "已接入")
    ],
    "further-study-home": [
      feature("考研陪伴", "研", "完成择校建议、复习计划、错题解析和复试准备", "postgraduate", "已接入"),
      feature("保研陪伴", "保", "诊断保研竞争力，精修文书并生成导师意向信", "postgraduate-recommendation", "已接入"),
      feature("留学陪伴", "留", "拆解语言考试、选校定位、文书主线和签证网申清单", "study-abroad", "已接入")
    ]
  };
  var els = {};
  var state = {
    identity: null,
    snapshot: null,
    today: null,
    resumes: null,
    resumeDraft: null,
    resumeSubmitting: false,
    resumeMessage: null,
    resumeListError: null,
    diagnosisResumeListLoading: false,
    diagnosisLoading: false,
    diagnosisResult: null,
    diagnosisHistoryByResume: {},
    diagnosisHistoryLoading: false,
    diagnosisDraft: null,
    diagnosisMessage: null,
    selectedDiagnosisResumeId: null,
    fileMessage: null,
    messageTimer: null,
    previewUrls: {},
    plan: null,
    employmentPlan: null,
    studyPlan: null,
    employmentDailyPlan: null,
    studyDailyPlan: null,
    planEnsuring: false,
    interviews: null,
    activeInterview: null,
    interviewMessages: [],
    interviewReport: null,
    interviewBusy: false,
    interviewError: null,
    interviewCurrentQuestion: null,
    interviewAnswerCount: 0,
    interviewRecognition: null,
    interviewListening: false,
    interviewDraft: "",
    interviewSetupPosition: "",
    interviewSetupResumeId: "",
    interviewSetupDifficulty: "Normal",
    interviewViewMode: "practice",
    interviewHistoryPage: null,
    interviewHistoryPageNumber: 1,
    interviewHistoryLoading: false,
    interviewHistoryError: null,
    panoramaHistoryPage: null,
    panoramaHistoryPageNumber: 1,
    panoramaHistoryLoading: false,
    panoramaHistoryError: null,
    panoramaStream: null,
    panoramaSession: null,
    panoramaMessages: [],
    panoramaViewMode: "practice",
    panoramaQuestion: null,
    panoramaTranscript: "",
    panoramaReport: null,
    panoramaCameraState: "idle",
    panoramaBusy: false,
    panoramaError: null,
    panoramaNotice: null,
    panoramaMediaDiagnostics: [],
    panoramaRecognition: null,
    panoramaRecognitionToken: 0,
    panoramaAnswering: false,
    panoramaSpeaking: false,
    panoramaSpeechSupported: null,
    panoramaSpeechToken: 0,
    panoramaLastSpokenQuestion: null,
    panoramaDifficulty: "Normal",
    panoramaSeconds: 0,
    panoramaDeadlineAt: null,
    panoramaTimer: null,
    panoramaAnswerCount: 0,
    employmentResources: null,
    employmentResourcesLoading: false,
    employmentResourcesError: null,
    employmentInsight: null,
    employmentInsightLoading: false,
    employmentInsightError: null,
    studyCenterSelection: null,
    studyCenterSelectionLoading: false,
    studyCenterSaving: false,
    studyCenterInsight: null,
    studyCenterInsightLoading: false,
    studyCenterInsightError: null,
    studyCenterResources: null,
    studyCenterResourcesLoading: false,
    studyCenterResourcesError: null,
    studyPlanningMaterials: null,
    studyPlanningMaterialsLoading: false,
    studyPlanningMaterialsUploading: false,
    studyPlanningMaterialsError: null,
    planProgress: null,
    assessmentScales: null,
    assessmentScale: null,
    assessmentSelectedScaleId: null,
    assessmentAnswers: {},
    assessmentCurrentIndex: 0,
    assessmentResult: null,
    assessmentRecords: null,
    assessmentLoading: false,
    assessmentSubmitting: false,
    assessmentAiInterpretationLoadingId: null,
    assessmentAiInterpretationError: null,
    assessmentAiInterpretationErrorRecordId: null,
    assessmentError: null,
    deepProfile: null,
    deepProfileHistory: null,
    deepProfileHistoryLoading: false,
    deepProfileHistoryError: null,
    deepProfileSelected: null,
    deepProfileGenerating: false,
    deepProfileError: null,
    postgraduateSchoolResult: null,
    postgraduatePlanResult: null,
    postgraduateMistakeResult: null,
    postgraduateReexamResult: null,
    postgraduateLoading: "",
    postgraduateMessage: null,
    recommendationDiagnosisResult: null,
    recommendationPlanResult: null,
    recommendationPolishResult: null,
    recommendationTutorLetterResult: null,
    recommendationLoading: "",
    recommendationMessage: null,
    studyAbroadProfileResult: null,
    studyAbroadLanguageResult: null,
    studyAbroadSchoolResult: null,
    studyAbroadStatementResult: null,
    studyAbroadVisaResult: null,
    studyAbroadLoading: "",
    studyAbroadMessage: null,
    scrollPositions: {},
    returnRoutes: {},
    route: "workbench",
    previousRoute: "",
    identityDiagnostic: ""
  };
  var appSelectSequence = 0;

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

  function applyExternalConfig() {
    var config = window.CYANCRUISE_APP_CONFIG;
    if (!config) {
      return;
    }
    endpoints = mergeConfig(endpoints, config.endpoints);
    if (Array.isArray(config.pages)) {
      pages = config.pages.map(normalizePageConfig);
    }
    if (config.featureGroups) {
      featureGroups = normalizeFeatureGroups(config.featureGroups);
    }
    if (window.CYANCRUISE_INITIAL_STATE) {
      state = mergeConfig(state, window.CYANCRUISE_INITIAL_STATE);
    }
  }

  function mergeConfig(base, override) {
    var out = {};
    var key;
    for (key in base) {
      if (Object.prototype.hasOwnProperty.call(base, key)) {
        out[key] = base[key];
      }
    }
    if (override) {
      for (key in override) {
        if (Object.prototype.hasOwnProperty.call(override, key)) {
          out[key] = override[key];
        }
      }
    }
    return out;
  }

  function normalizePageConfig(item) {
    return page(item.key, item.title, item.status, item.audience, item.summary, item.endpoints, {
      defaultNav: item.defaultNav,
      debugNav: item.debugNav
    });
  }

  function normalizeFeatureGroups(groups) {
    var out = {};
    Object.keys(groups).forEach(function (key) {
      out[key] = groups[key].map(normalizeFeatureConfig);
    });
    return out;
  }

  function normalizeFeatureConfig(item) {
    return feature(item.title, item.icon, item.summary, item.route, item.status);
  }

  function init() {
    applyExternalConfig();
    pages.forEach(function (item) {
      pageByKey[item.key] = item;
    });
    cacheElements();
    renderNav();
    state.identity = resolveIdentity();
    updateIdentityState();
    bindEvents();
    warmPanoramaVoices();
    loadPlatformIdentity().then(function () {
      handleRouteChange();
      if (state.identityDiagnostic) {
        showMessage("warning", "\u8eab\u4efd\u8bca\u65ad", state.identityDiagnostic);
      }
      loadOverview();
    });
  }

  function cacheElements() {
    els.appHeader = document.querySelector(".app-header");
    els.routeNav = $("routeNav");
    els.pageHost = $("pageHost");
    els.messagePanel = $("messagePanel");
    els.statusGrid = document.querySelector(".status-grid");
    els.userIdInput = $("userIdInput");
    els.saveUserIdButton = $("saveUserIdButton");
    els.identityTitle = $("identityTitle");
    els.identityHint = $("identityHint");
    els.targetRole = $("targetRole");
    els.profileStatus = $("profileStatus");
    els.readinessScore = $("readinessScore");
    els.readinessHint = $("readinessHint");
    els.resumeState = $("resumeState");
    els.resumeHint = $("resumeHint");
    els.interviewState = $("interviewState");
    els.interviewHint = $("interviewHint");
  }

  function bindEvents() {
    window.addEventListener("hashchange", handleRouteChange);
    window.addEventListener("popstate", handleRouteChange);
    window.addEventListener("beforeunload", stopPanoramaMedia);
    els.pageHost.addEventListener("click", handlePageHostClick);
    els.pageHost.addEventListener("keydown", handleAppSelectKeydown);
    els.pageHost.addEventListener("change", handlePageHostChange);
    document.addEventListener("click", function (event) {
      if (!closestAppSelect(event.target)) {
        closeAppSelects();
      }
    });
    document.addEventListener("focusin", function (event) {
      closeAppSelects(closestAppSelect(event.target));
    });
    if (typeof MutationObserver === "function") {
      new MutationObserver(function () {
        enhanceAppSelects(els.pageHost);
      }).observe(els.pageHost, { childList: true, subtree: true });
    }
    els.saveUserIdButton.addEventListener("click", saveDevelopmentUser);
    els.userIdInput.addEventListener("keydown", function (event) {
      if (event.key === "Enter") {
        event.preventDefault();
        saveDevelopmentUser();
      }
    });
  }

  function renderNav() {
    renderChromeMode();
    if (!isDebugMode()) {
      els.routeNav.innerHTML = "";
      return;
    }
    els.routeNav.innerHTML = pages.filter(shouldShowInNav).map(function (item) {
      var hidden = item.audience === "admin" ? ' data-admin="true"' : "";
      return '<button type="button" class="route-tab" data-route="' + item.key + '"' + hidden + ">" + escapeHtml(item.title) + "</button>";
    }).join("");
    els.routeNav.addEventListener("click", function (event) {
      var route = event.target && event.target.getAttribute("data-route");
      if (route) {
        navigateToRoute(route);
      }
    });
  }

  function renderChromeMode() {
    var debug = isDebugMode();
    toggleHidden(els.appHeader, !debug);
    toggleHidden(els.routeNav, !debug);
    toggleHidden(els.statusGrid, !debug);
  }

  function toggleHidden(node, hidden) {
    if (!node) {
      return;
    }
    if (hidden) {
      node.className = node.className.indexOf(" chrome-hidden") >= 0 ? node.className : node.className + " chrome-hidden";
    } else {
      node.className = node.className.replace(/\s*chrome-hidden/g, "");
    }
  }

  function shouldShowInNav(item) {
    if (isDebugMode()) {
      return item.debugNav !== false;
    }
    if (item.audience === "admin") {
      return false;
    }
    return item.defaultNav !== false;
  }

  function handleRouteChange() {
    var key = normalizeRoute(window.location.hash);
    var previous = state.route;
    if (previous === "interview-panorama" && key !== "interview-panorama") {
      stopPanoramaMedia();
    }
    if (previous === "interview" && key !== "interview") stopAiInterviewSpeech();
    if (!pageByKey[key]) {
      state.route = "workbench";
      showMessage("warning", "未知页面", "未找到 route: " + key + "，已回到工作台。");
      replaceRouteInLocation("workbench");
    } else {
      if (previous && previous !== key) {
        rememberRouteScroll(previous);
        clearReturnRoute(key);
      }
      if (key === "deep-profile-detail" && previous !== key) {
        state.deepProfileSelected = state.deepProfile;
      }
      state.route = key;
      if (needsCanonicalRoute(key)) {
        replaceRouteInLocation(key);
      }
    }
    if (state.route === "workbench" || state.route === "career-plan" || state.route === "today-action") {
      syncCurrentRoutePlanningState();
    }
    markActiveNav();
    renderPage(pageByKey[state.route]);
  }

  function markActiveNav() {
    var buttons = els.routeNav.querySelectorAll(".route-tab");
    for (var i = 0; i < buttons.length; i += 1) {
      buttons[i].className = buttons[i].getAttribute("data-route") === state.route ? "route-tab active" : "route-tab";
    }
  }

  function isDebugMode() {
    var params = new URLSearchParams(window.location.search);
    var value = String(params.get("ccDebug") || "").toLowerCase();
    var legacyValue = String(params.get("debug") || "").toLowerCase();
    if (legacyValue === "cyancruise") {
      return true;
    }
    return value === "1" || value === "true" || value === "yes";
  }

  function renderPage(item) {
    if (!item) {
      return;
    }
    if (document.body) {
      document.body.classList.toggle("admin-mode", item.key === "admin-console");
    }
    if (item.audience === "admin" && !hasAdminRole(state.identity)) {
      renderForbidden(item);
      return;
    }
    if (item.audience === "user" && !hasUserIdentity()) {
      renderIdentityRequired(item);
      return;
    }
    if (renderPageModule(item)) {
      enhanceAppSelects(els.pageHost);
      return;
    }
    if (item.key === "onboarding") {
      renderOnboarding(item);
    } else if (item.key === "career-resources") {
      renderCareerResourcesPage(item);
    } else if (item.key === "study-resources") {
      renderStudyResourcesPage(item);
    } else {
      renderContractPage(item);
    }
    enhanceAppSelects(els.pageHost);
  }

  function renderPageModule(item) {
    var registry = window.CYANCRUISE_PAGE_MODULES || {};
    var module = registry[item.key];
    if (!module || typeof module.render !== "function") {
      return false;
    }
    module.render(item, {
      endpoints: endpoints,
      escapeHtml: escapeHtml,
      featureCards: featureCards,
      featureGroups: featureGroups,
      firstText: firstText,
      getValue: getValue,
      hasAdminRole: hasAdminRole,
      hasUserIdentity: hasUserIdentity,
      hideMessage: hideMessage,
      identity: state.identity,
      invalidateEmploymentResources: invalidateEmploymentResources,
      invalidateStudyCenterResources: invalidateStudyCenterResources,
      isFilePreview: isFilePreview,
      normalizeArray: normalizeArray,
      pageByKey: pageByKey,
      pageHost: els.pageHost,
      post: post,
      renderPage: renderPage,
      renderShell: renderShell,
      showMessage: showMessage,
      statePanel: statePanel,
      renderers: {
        renderAdminConsolePage: renderContractPage,
        renderAssessmentPage: renderAssessmentPage,
        renderDeepProfileDetailPage: renderDeepProfileDetailPage,
        renderAssistantPage: renderContractPage,
        renderCareerPlanPage: renderCareerPlanPage,
        renderContractPage: renderContractPage,
        renderEmploymentHome: renderEmploymentHome,
        renderFurtherStudyHome: renderFurtherStudyHome,
        renderInterviewHistoryPage: renderInterviewHistoryPage,
        renderInterviewPage: renderInterviewPage,
        renderMessagesPage: renderContractPage,
        renderPanoramaHistoryPage: renderPanoramaHistoryPage,
        renderPanoramaInterviewPage: renderPanoramaInterviewPage,
        renderPostgraduateMistakePage: renderPostgraduateMistakePage,
        renderPostgraduatePage: renderPostgraduatePage,
        renderPostgraduatePlanPage: renderPostgraduatePlanPage,
        renderPostgraduateReexamPage: renderPostgraduateReexamPage,
        renderPostgraduateSchoolPage: renderPostgraduateSchoolPage,
        renderRecommendationBackgroundPage: renderRecommendationBackgroundPage,
        renderRecommendationMaterialPage: renderRecommendationMaterialPage,
        renderRecommendationPage: renderRecommendationPage,
        renderRecommendationRankingPage: renderRecommendationRankingPage,
        renderRecommendationTutorPage: renderRecommendationTutorPage,
        renderResumeDiagnosisPage: renderResumeDiagnosisPage,
        renderResumePage: renderResumePage,
        renderStudyAbroadLanguagePage: renderStudyAbroadLanguagePage,
        renderStudyAbroadPage: renderStudyAbroadPage,
        renderStudyAbroadProfilePage: renderStudyAbroadProfilePage,
        renderStudyAbroadSchoolPage: renderStudyAbroadSchoolPage,
        renderStudyAbroadStatementPage: renderStudyAbroadStatementPage,
        renderStudyAbroadVisaPage: renderStudyAbroadVisaPage,
        renderTodayPage: renderTodayPage,
        renderWorkbench: renderWorkbench
      },
      renderFeatureShell: renderFeatureShell,
      renderPage: renderPage,
      renderShell: renderShell,
      showMessage: showMessage,
      state: state
    });
    return true;
  }

  function renderWorkbench(item) {
    if (isDebugMode()) {
      var panels = [
        metricsPanel("主循环状态", overviewRows()),
        actionPanel("下一步入口", pages.filter(function (pageItem) {
          return pageItem.key !== "workbench" && shouldShowInNav(pageItem);
        }).map(function (pageItem) {
          return linkButton(pageItem.key, pageItem.title, pageItem.summary);
        }).join(""))
      ];
      renderShell(item, panels.join(""));
      return;
    }
    renderHomeIntentPage(item);
  }

  function renderHomeIntentPage(item) {
    var onboarding = getValue(state.snapshot, "onboarding") || {};
    var intent = readHomeIntent();
    var profile = {
      identityType: firstText(intent.identityType, onboarding.identityType),
      educationStage: firstText(intent.educationStage, onboarding.educationStage, onboarding.stage),
      school: firstText(intent.school, getValue(state.snapshot, "onboarding.education.school")),
      major: firstText(intent.major, getValue(state.snapshot, "onboarding.education.major"), intent.schoolMajor, onboarding.schoolMajor),
      schoolMajor: firstText(intent.schoolMajor, onboarding.schoolMajor),
      resumeStatus: firstText(intent.resumeStatus, onboarding.resumeStatus),
      selectedResumeId: firstText(intent.selectedResumeId, onboarding.selectedResumeId, getValue(state.snapshot, "resume.lastResumeId")),
      experience: firstText(intent.experience, onboarding.experience, onboarding.strengths),
      selfProfileSupplement: firstText(intent.selfProfileSupplement, onboarding.selfProfileSupplement)
    };
    var targetRole = firstText(intent.targetRole, onboarding.targetRole, textFromSnapshot("preferences.targetRole"));
    var targetSchool = firstText(intent.targetSchool, onboarding.targetSchool);
    var preference = firstText(intent.preference, onboarding.preference);
    var selectedGoal = resolveHomeGoal(firstText(onboarding.routeGoal, intent.goal), targetRole, preference, targetSchool);
    var intentPanel = isHomeIntentCollapsed(intent)
      ? homeIntentSummaryPanel(selectedGoal, profile, targetRole, targetSchool, preference)
      : homeIntentFormPanel(selectedGoal, profile, targetRole, targetSchool, preference);
    renderFeatureShell(item, homeWelcomeTitle(), "这里汇总你的路线、今日行动、简历和面试进展。",
      intentPanel +
      overviewStrip(selectedGoal) +
      '<section class="feature-section"><h3>路线入口</h3><div class="feature-grid">' +
      featureCards(homeRouteFeatures(selectedGoal)) + '</div></section>');
    var form = $("homeIntentForm");
    if (form) {
      form.addEventListener("submit", submitHomeIntent);
    }
    var goal = $("homeGoal");
    if (goal) {
      goal.addEventListener("change", changeHomeGoal);
    }
    var edit = $("editHomeIntentButton");
    if (edit) {
      edit.addEventListener("click", editHomeIntent);
    }
    var toggle = $("toggleHomeProfileButton");
    if (toggle) {
      toggle.addEventListener("click", toggleHomeProfile);
    }
    var cancel = $("cancelHomeIntentButton");
    if (cancel) {
      cancel.addEventListener("click", cancelHomeIntentEdit);
    }
  }

  function isHomeIntentCollapsed(intent) {
    return getUserStorageItem("cyancruise.homeIntentSaved") === "true" && getUserStorageItem("cyancruise.homeIntentEditing") !== "true" && !!intent.goal;
  }

  function homeWelcomeTitle() {
    var name = currentUserDisplayName();
    return name ? "欢迎回来，" + name : "欢迎回来";
  }

  function currentUserDisplayName() {
    var onboarding = getValue(state.snapshot, "onboarding") || {};
    var identity = state.identity || {};
    var name = firstText(
      identity.displayName,
      identity.name,
      identity.userName,
      identity.nickName,
      onboarding.displayName,
      onboarding.name,
      onboarding.userName,
      onboarding.nickName,
      getValue(state.snapshot, "user.displayName"),
      getValue(state.snapshot, "user.name"),
      getValue(state.snapshot, "user.userName"),
      getValue(state.snapshot, "profile.displayName"),
      getValue(state.snapshot, "profile.name")
    );
    if (name) {
      return name;
    }
    if (identity.userId) {
      return "用户";
    }
    return "";
  }

  function homeIntentFormPanel(selectedGoal, onboarding, targetRole, targetSchool, preference) {
    var resumeOptions = profileResumeOptions();
    return '<section class="panel full home-intent-panel">' +
      '<h3>自画像</h3>' +
      '<form class="form-grid" id="homeIntentForm">' +
      '<h4 class="form-section-title full">个人情况</h4>' +
      field("profileIdentityType", "身份类型", "select", firstText(onboarding.identityType, "student"), [["student", "在校学生"], ["graduate", "应届毕业生"], ["career_switcher", "转行求职"]]) +
      field("profileEducationStage", "当前阶段", "select", firstText(onboarding.educationStage, onboarding.stage, "undergraduate"), [["undergraduate", "本科"], ["postgraduate", "研究生"], ["vocational", "高职/专科"], ["working", "已工作"], ["other", "其他"]]) +
      field("profileSchool", "学校", "text", firstText(onboarding.school, "")) +
      field("profileMajor", "专业", "text", firstText(onboarding.major, onboarding.schoolMajor, "")) +
      field("resumeStatus", "简历/材料状态", "select", firstText(onboarding.resumeStatus, "none"), [["none", "还没有简历"], ["draft", "已有初稿"], ["ready", "已有可投递简历"], ["materials", "已有升学材料"]]) +
      field("profileSelectedResume", "选择已有简历", "select", String(firstText(onboarding.selectedResumeId, "")), resumeOptions) +
      '<small class="profile-resume-reference-note full">所选简历将用于就业洞察展示，并作为求职或升学规划的分析参考。</small>' +
      '<label class="full">经历与优势<textarea id="profileExperience" placeholder="可以写课程、项目、实习、竞赛、语言、技能、研究方向等。">' + escapeHtml(firstText(onboarding.experience, onboarding.strengths, "")) + '</textarea></label>' +
      '<label class="full">自画像补充<textarea id="selfProfileSupplement" placeholder="可填写深度画像建议补充的真实信息，例如项目职责、技术基础、学习时间、协作经历或目标限制。">' + escapeHtml(firstText(onboarding.selfProfileSupplement, "")) + '</textarea><small>保存后，重新生成深度画像时会优先参考这些信息，后续简历诊断和规划也会读取最新内容。</small></label>' +
      '<h4 class="form-section-title full">路线选择</h4>' +
      field("homeGoal", "当前路线", "select", selectedGoal, [["employment", "就业"], ["study", "深造"]]) +
      homeTargetField(selectedGoal, targetRole, targetSchool) +
      '<div class="full actions-row"><button type="submit">保存自画像</button><button type="button" class="secondary" id="cancelHomeIntentButton">取消修改</button></div>' +
      '</form></section>';
  }

  function homeTargetField(selectedGoal, targetRole, targetSchool) {
    var study = selectedGoal === "study";
    var label = study ? "目标院校" : "目标岗位或方向";
    var value = study ? targetSchool : targetRole;
    var placeholder = study ? "例如：电子科技大学" : "例如：后端开发、产品经理";
    return '<label id="homeTargetField"><span id="homeTargetLabel">' + label + '</span>' +
      '<input id="homeTargetRole" value="' + escapeAttr(value) + '" placeholder="' + escapeAttr(placeholder) + '"' +
      ' data-current-goal="' + (study ? "study" : "employment") + '"' +
      ' data-role-value="' + escapeAttr(targetRole) + '" data-school-value="' + escapeAttr(targetSchool) + '"></label>';
  }

  function homeIntentSummaryPanel(selectedGoal, onboarding, targetRole, targetSchool, preference) {
    var expanded = getUserStorageItem("cyancruise.homeProfileExpanded") === "true";
    var targetLabel = selectedGoal === "study" ? "目标院校" : "目标岗位或方向";
    var targetValue = selectedGoal === "study" ? targetSchool : targetRole;
    var summaryRows = [
      ["当前路线", labelForGoal(selectedGoal)],
      [targetLabel, firstText(targetValue, "待确认")],
      ["身份类型", labelForIdentity(firstText(onboarding.identityType, "student"))],
      ["简历/材料状态", labelForResumeStatus(firstText(onboarding.resumeStatus, "none"))],
      ["规划参考简历", selectedProfileResumeTitle(onboarding.selectedResumeId)]
    ];
    var detailRows = [
      ["身份类型", labelForIdentity(firstText(onboarding.identityType, "student"))],
      ["当前阶段", labelForEducationStage(firstText(onboarding.educationStage, onboarding.stage, "undergraduate"))],
      ["学校", firstText(onboarding.school, "未填写")],
      ["专业", firstText(onboarding.major, onboarding.schoolMajor, "未填写")],
      ["简历/材料状态", labelForResumeStatus(firstText(onboarding.resumeStatus, "none"))],
      ["规划参考简历", selectedProfileResumeTitle(onboarding.selectedResumeId)],
      ["经历与优势", firstText(onboarding.experience, "未填写")],
      ["自画像补充", firstText(onboarding.selfProfileSupplement, "未填写")],
      ["当前路线", labelForGoal(selectedGoal)],
      [targetLabel, firstText(targetValue, "待确认")]
    ];
    var rows = expanded ? detailRows : summaryRows;
    return '<section class="panel full home-intent-panel">' +
      '<h3>自画像已保存</h3>' +
      '<div class="metric-list profile-summary-grid">' + rows.map(function (row) {
        return '<div class="metric"><span class="label">' + escapeHtml(row[0]) + '</span><strong>' + escapeHtml(row[1]) + '</strong></div>';
      }).join("") + '</div>' +
      '<div class="actions-row"><button type="button" class="secondary" id="toggleHomeProfileButton">' + (expanded ? "收起自画像" : "展开自画像") + '</button><button type="button" class="secondary" id="editHomeIntentButton">修改自画像</button>' + homeDirectionButtons(selectedGoal) + '</div>' +
      '</section>';
  }

  function profileResumeOptions() {
    var resumes = normalizeArray(state.resumes);
    if (!resumes.length) {
      return [["", "暂无可选简历"]];
    }
    return resumes.map(function (resume) {
      return [String(firstText(resume.resumeId, resume.id, "")), firstText(resume.title, resume.resumeName, "未命名简历")];
    });
  }

  function selectedProfileResume(selectedResumeId) {
    var resumes = normalizeArray(state.resumes);
    var selectedId = String(firstText(selectedResumeId,
      getValue(state.snapshot, "onboarding.selectedResumeId"),
      getValue(state.snapshot, "resume.lastResumeId"), ""));
    for (var i = 0; i < resumes.length; i += 1) {
      if (String(firstText(resumes[i].resumeId, resumes[i].id, "")) === selectedId) {
        return resumes[i];
      }
    }
    return resumes.length ? resumes[0] : null;
  }

  function selectedProfileResumeTitle(selectedResumeId) {
    var resume = selectedProfileResume(selectedResumeId);
    return resume ? firstText(resume.title, resume.resumeName, "未命名简历") : "暂未选择";
  }

  function editHomeIntent() {
    setUserStorageItem("cyancruise.homeIntentEditing", "true");
    renderPage(pageByKey.workbench);
  }

  function cancelHomeIntentEdit() {
    if (getUserStorageItem("cyancruise.homeIntentSaved") === "true") {
      removeUserStorageItem("cyancruise.homeIntentEditing");
      removeUserStorageItem("cyancruise.previewProfile");
    } else {
      removeUserStorageItem("cyancruise.homeIntent");
      removeUserStorageItem("cyancruise.homeIntentEditing");
      removeUserStorageItem("cyancruise.previewProfile");
    }
    renderPage(pageByKey.workbench);
    showMessage("info", "已取消修改", "页面已恢复到修改前的自画像。");
  }

  function toggleHomeProfile() {
    var expanded = getUserStorageItem("cyancruise.homeProfileExpanded") === "true";
    setUserStorageItem("cyancruise.homeProfileExpanded", expanded ? "false" : "true");
    renderPage(pageByKey.workbench);
  }

  function homeDirectionButtons(goal) {
    var buttons = [];
    if (goal === "employment") {
      buttons.push('<button type="button" class="secondary" data-link="employment-home" data-platform-link="true">前往就业中心</button>');
    }
    if (goal === "study") {
      buttons.push('<button type="button" class="secondary" data-link="further-study-home" data-platform-link="true">前往升学中心</button>');
    }
    return buttons.join("");
  }

  function homeRouteFeatures(goal) {
    var employment = feature("就业中心", "就", "查看就业洞察、简历准备、面试练习与就业资讯", "employment-home", "已接入");
    var study = feature("升学中心", "学", "选择升学方向，查看准备建议与升学资讯", "further-study-home", "已接入");
    var plan = feature("路径规划", "路", "根据当前方向生成实现路径规划，后续接入规划智能体", "career-plan", "规划中");
    var assessment = feature("画像补全", "补", "先补充人格、性格和偏好信息，让后续分析更准确", "assessment", "规划中");
    var today = feature("今日行动", "今", "根据路径规划拆解每天应该推进的事项", "today-action", "已接入");
    employment.platformLink = true;
    study.platformLink = true;
    if (goal === "employment") {
      return [assessment, employment, today, plan];
    }
    if (goal === "study") {
      return [assessment, study, today, plan];
    }
    return [assessment, employment, today, plan, study];
  }

  function changeHomeGoal() {
    setUserStorageItem("cyancruise.homeIntentEditing", "true");
    syncHomeTargetField(valueOf("homeGoal"));
  }

  function syncHomeTargetField(goal) {
    var input = $("homeTargetRole");
    var label = $("homeTargetLabel");
    if (!input || !label) return;
    var previousGoal = input.getAttribute("data-current-goal") || "employment";
    input.setAttribute(previousGoal === "study" ? "data-school-value" : "data-role-value", input.value);
    var study = goal === "study";
    input.value = input.getAttribute(study ? "data-school-value" : "data-role-value") || "";
    input.setAttribute("data-current-goal", study ? "study" : "employment");
    input.setAttribute("placeholder", study ? "例如：电子科技大学" : "例如：后端开发、产品经理");
    label.textContent = study ? "目标院校" : "目标岗位或方向";
  }

  function resolveHomeGoal(goal, target, preference, targetSchool) {
    var normalized = trim(goal);
    if (normalized === "employment" || normalized === "study") {
      return normalized;
    }
    if (trim(targetSchool)) return "study";
    var text = firstText(target, preference);
    if (/考研|保研|留学|升学|深造|研究生|院校|GPA|雅思|托福|申请/.test(text)) {
      return "study";
    }
    return "employment";
  }

  function currentRouteGoal() {
    var onboarding = getValue(state.snapshot, "onboarding") || {};
    return resolveHomeGoal(firstText(onboarding.routeGoal, readHomeIntent().goal),
      firstText(onboarding.targetRole, textFromSnapshot("preferences.targetRole")),
      firstText(onboarding.preference, ""),
      firstText(onboarding.targetSchool, readHomeIntent().targetSchool));
  }

  function isStudyRoute() {
    return currentRouteGoal() === "study";
  }

  function syncCurrentRoutePlanningState() {
    if (isStudyRoute()) {
      state.plan = state.studyPlan;
      state.dailyPlan = state.studyDailyPlan;
    } else {
      state.plan = state.employmentPlan;
      state.dailyPlan = state.employmentDailyPlan;
    }
  }

  function currentPlanEndpoint() { return isStudyRoute() ? endpoints.studyPlan : endpoints.plan; }
  function currentEnsurePlanEndpoint(preferAgent) {
    if (isStudyRoute()) return preferAgent ? endpoints.studyGeneratePlan : endpoints.studyEnsurePlan;
    return preferAgent && endpoints.generatePlan ? endpoints.generatePlan : endpoints.ensurePlan;
  }
  function currentDailyPlanEndpoint() { return isStudyRoute() ? endpoints.studyDailyPlan : endpoints.dailyPlan; }
  function currentDailyTaskUpdateEndpoint() { return isStudyRoute() ? endpoints.studyDailyTaskUpdate : endpoints.dailyTaskUpdate; }
  function shouldEnsureCurrentPlan(plan) {
    return !plan || (!plan.unavailable && !hasExistingCareerPlan(plan));
  }

  function renderFeatureHome(item) {
    if (item.key === "employment-home") {
      renderEmploymentHome(item);
      return;
    }
    if (item.key === "further-study-home") {
      renderFurtherStudyHome(item);
      return;
    }
    var cards = featureGroups[item.key] || [];
    renderFeatureShell(item, item.title, item.summary,
      '<section class="feature-section"><h3>' + escapeHtml(item.title) + '工具</h3><div class="feature-grid">' +
      featureCards(cards) + '</div></section>');
  }

  function renderEmploymentHome(item) {
    ensureEmploymentHomeData();
    renderFeatureShell(item, item.title, "围绕目标岗位完成简历准备、面试练习，查看就业洞察和就业资讯。",
      employmentRoadmapPanel() +
      employmentInsightPanel() +
      employmentResourcePanels());
  }

  function ensureEmploymentHomeData() {
    loadEmploymentResources();
    if (hasUserIdentity()) {
      loadEmploymentInsight();
    }
  }

  function loadEmploymentResources() {
    if (state.employmentResourcesLoading || state.employmentResources || state.employmentResourcesError) {
      return;
    }
    if (isFilePreview()) {
      state.employmentResources = previewEmploymentResources();
      return;
    }
    state.employmentResourcesLoading = true;
    post(endpoints.careerResources, hasUserIdentity() ? state.identity.userId : "").then(function (feed) {
      state.employmentResources = feed || {};
      state.employmentResourcesError = null;
    }).catch(function (error) {
      state.employmentResourcesError = error.message || "就业资源暂不可用。";
    }).then(function () {
      state.employmentResourcesLoading = false;
      if (state.route === "employment-home" || state.route === "career-resources") {
        renderPage(pageByKey[state.route]);
      }
    });
  }

  function loadEmploymentInsight() {
    if (state.employmentInsightLoading || state.employmentInsight || state.employmentInsightError || isFilePreview()) {
      return;
    }
    state.employmentInsightLoading = true;
    post(endpoints.employmentInsight, state.identity.userId).then(function (insight) {
      state.employmentInsight = insight || {};
      state.employmentInsightError = null;
    }).catch(function (error) {
      state.employmentInsightError = error.message || "就业洞察暂不可用。";
    }).then(function () {
      state.employmentInsightLoading = false;
      if (state.route === "employment-home") {
        renderPage(pageByKey[state.route]);
      }
    });
  }

  function employmentRoadmapPanel() {
    var employmentPlan = state.employmentPlan || (!isStudyRoute() ? state.plan : null);
    var plan = employmentPlan && !employmentPlan.unavailable ? employmentPlan : {};
    var hasPlan = !!(plan && Object.keys(plan).length);
    var targetRole = employmentTargetRole(plan);
    var weeklyFocus = normalizeArray(plan.weeklyFocus || getValue(plan, "weeklyPlan.actions") || plan.weekFocus || plan.actions || plan.nextActions).slice(0, 3);
    if (!weeklyFocus.length) {
      weeklyFocus = defaultRoadmapFocus(targetRole);
    }
    var phases = normalizeArray(plan.phases);
    var summary = firstText(plan.startStateSummary, plan.summary, plan.planSummary, "根据你的目标岗位和现有准备情况生成分阶段就业路线图。");
    return '<section class="feature-section roadmap-section">' +
      '<div class="section-heading"><div><h3>就业路线图</h3><p class="section-note">优先展示下一步怎么走，工具入口放在路线之后。</p></div>' +
      '<div class="section-actions">' +
      '<button type="button" class="secondary" data-link="career-plan">完整规划</button>' +
      '<button type="button" data-ensure-plan ' + (state.planEnsuring ? 'disabled aria-disabled="true"' : '') + '>' +
      (state.planEnsuring ? "生成中" : planGenerationButtonLabel(plan, hasPlan ? "重新生成路线图" : "生成路线图")) + '</button></div></div>' +
      '<div class="roadmap-panel">' +
      '<div class="roadmap-summary"><span class="resource-type">就业规划</span>' +
      '<strong>' + escapeHtml(targetRole) + '</strong>' + renderPlanProfileStatus(plan, summary) +
      '<ul class="compact-list">' + weeklyFocus.map(function (item) { return '<li>' + escapeHtml(item) + '</li>'; }).join("") + '</ul></div>' +
      '<div class="roadmap-steps">' + (phases.length ? phases.slice(0, 4).map(planPhaseStepCard).join("") : employmentRoadmapSteps(targetRole).map(roadmapStepCard).join("")) + '</div>' +
      '</div></section>';
  }

  function planPhaseStepCard(phase, index) {
    var title = firstText(phase && phase.title, "阶段目标");
    var desc = firstText(phase && phase.goal, phase && phase.description, "按阶段推进就业目标。");
    var horizon = firstText(phase && phase.horizon, "阶段");
    var cls = "roadmap-step" + (index === 0 ? " active" : "");
    return '<button type="button" class="' + cls + '" data-link="career-plan">' +
      '<span class="step-index">' + (index + 1) + '</span><span class="step-copy">' +
      '<strong>' + escapeHtml(title) + '</strong><small>' + escapeHtml(desc) + '</small></span>' +
      '<span class="step-status">' + escapeHtml(horizon) + '</span></button>';
  }

  function employmentTargetRole(plan) {
    return firstText(
      plan && plan.targetRole,
      plan && plan.role,
      textFromSnapshot("preferences.targetRole", "onboarding.targetRole", "resume.targetJob"),
      "目标岗位待确认"
    );
  }

  function defaultRoadmapFocus(targetRole) {
    var role = targetRole === "目标岗位待确认" ? "目标岗位" : targetRole;
    return [
      "确认 " + role + " 的岗位要求和能力关键词",
      "补齐简历证据，完成一次简历诊断",
      "安排一次模拟面试，并记录复盘反馈"
    ];
  }

  function employmentRoadmapSteps(targetRole) {
    var hasTarget = targetRole !== "目标岗位待确认";
    var resumeCount = normalizeArray(state.resumes).length;
    var interviewCount = normalizeArray(state.interviews).length;
    var resourceReady = !!state.employmentResources && !state.employmentResourcesError;
    return [
      {
        title: "确定目标",
        status: hasTarget ? "已具备" : "待补充",
        active: !hasTarget,
        desc: hasTarget ? "围绕目标岗位拆解能力关键词。" : "先在首页或画像中补充目标岗位。",
        route: "workbench"
      },
      {
        title: "简历证据",
        status: resumeCount ? resumeCount + " 份简历" : "待开始",
        active: hasTarget && !resumeCount,
        desc: "把项目、实习、课程和竞赛证据写进简历。",
        route: "resume"
      },
      {
        title: "资讯与投递",
        status: resourceReady ? "可使用" : state.employmentResourcesLoading ? "加载中" : "待加载",
        active: !!resumeCount,
        desc: "结合公开就业资源和岗位信息安排投递节奏。",
        route: "career-resources"
      },
      {
        title: "面试复盘",
        status: interviewCount ? interviewCount + " 次练习" : "待练习",
        active: !!resumeCount && !interviewCount,
        desc: "用模拟面试验证表达、追问和复盘质量。",
        route: "interview"
      }
    ];
  }

  function roadmapStepCard(step, index) {
    var cls = "roadmap-step" + (step.active ? " active" : "");
    return '<button type="button" class="' + cls + '" data-link="' + escapeAttr(step.route) + '">' +
      '<span class="step-index">' + (index + 1) + '</span><span class="step-copy">' +
      '<strong>' + escapeHtml(step.title) + '</strong><small>' + escapeHtml(step.desc) + '</small></span>' +
      '<span class="step-status">' + escapeHtml(step.status) + '</span></button>';
  }

  function ensureEmploymentPlan(preferAgent) {
    if (state.planEnsuring) {
      return;
    }
    if (isFilePreview()) {
      var previewPlan = {
        startStateSummary: "根据当前目标岗位和准备情况，整理分阶段路线与本周行动。",
        planningMode: "RULE_FALLBACK",
        agentStatus: "FALLBACK_READY",
        horizonYears: 1,
        targetRole: employmentTargetRole({}),
        weeklyFocus: defaultRoadmapFocus(employmentTargetRole({})),
        weeklyPlan: {
          weekTitle: "本周启动计划",
          weekGoal: "完成目标岗位拆解、简历证据整理和一次面试练习。",
          actions: defaultRoadmapFocus(employmentTargetRole({})),
          deliverables: ["岗位关键词清单", "简历优化清单", "模拟面试复盘"],
          dailySuggestions: defaultDailySuggestions(employmentTargetRole({}))
        },
        phases: previewPlanPhases(employmentTargetRole({})),
        dailySuggestions: defaultDailySuggestions(employmentTargetRole({}))
      };
      state.plan = mergeRefreshedPlan(state.plan, previewPlan);
      renderPage(pageByKey[state.route]);
      showMessage("info", "已生成预览路线图", "file:// 模式不会调用后端，已完成阶段会保留。");
      return;
    }
    if (!hasUserIdentity()) {
      showMessage("warning", "需要身份", "生成路线图前需要 Cosmic 身份或显式开发身份。");
      return;
    }
    state.planEnsuring = true;
    var currentEmploymentPlan = state.employmentPlan || (!isStudyRoute() ? state.plan : null);
    var refreshState = careerPlanRefreshState(currentEmploymentPlan && !currentEmploymentPlan.unavailable ? currentEmploymentPlan : {});
    renderPage(pageByKey[state.route]);
    var endpoint = preferAgent && endpoints.generatePlan ? endpoints.generatePlan : endpoints.ensurePlan;
    post(endpoint, state.identity.userId).then(function (plan) {
      state.employmentPlan = mergeRefreshedPlan(state.employmentPlan, plan || {});
      if (!isStudyRoute()) state.plan = state.employmentPlan;
      return post(endpoints.dailyPlan, state.identity.userId).then(function (dailyPlan) {
        state.employmentDailyPlan = dailyPlan || {};
        if (!isStudyRoute()) state.dailyPlan = state.employmentDailyPlan;
        showMessage("info", preferAgent && refreshState.protectedCount > 0 ? "未开始阶段已更新" : (preferAgent ? "智能路线图已生成" : "路线图已刷新"),
          refreshState.protectedCount > 0 ? "进行中和已完成阶段及其进度已保留。" : "每日计划已按新路线从第一步开始安排。");
      });
    }).catch(function (error) {
      showMessage("error", "路线图生成失败", error.message || "智能规划暂不可用，原有路线图已保留，请稍后重试。");
    }).then(function () {
      state.planEnsuring = false;
      if (state.route === "employment-home" || state.route === "career-plan" || state.route === "today-action") {
        renderPage(pageByKey[state.route]);
      }
    });
  }

  function employmentInsightPanel() {
    if (state.employmentInsightLoading) {
      return '<section class="feature-section"><h3>就业洞察</h3>' +
        statePanel("正在读取就业洞察", "系统正在根据学校、专业和目标岗位加载来源覆盖摘要。", "pending") +
        '</section>';
    }
    if (state.employmentInsightError) {
      return '<section class="feature-section"><h3>就业洞察</h3>' +
        statePanel("洞察暂不可用", state.employmentInsightError + " 你仍可继续使用简历和面试工具。", "warning") +
        '</section>';
    }
    if (!state.employmentInsight) {
      return '<section class="feature-section"><h3>就业洞察</h3>' +
        statePanel("等待画像信号", "完善学校、专业和目标岗位后，这里会展示就业去向、来源覆盖和匹配摘要。", "empty") +
        '</section>';
    }
    var insight = state.employmentInsight;
    var rows = employmentInsightProfileRows(insight);
    return '<section class="feature-section"><div class="section-heading">' +
      '<h3>就业洞察</h3></div>' +
      '<div class="insight-summary">' +
      metricsPanel("自画像", rows) +
      employmentResumeSummaryPanel(insight) +
      '</div></section>';
  }

  function ensureStudyPlan(preferAgent) {
    if (state.planEnsuring) return;
    var profile = furtherStudyProfile();
    if (!profile.directionCode) {
      showMessage("warning", "请先选择升学方向", "请选择考研、保研或留学并保存，再生成对应方向的规划。");
      return;
    }
    if (!hasUserIdentity()) {
      showMessage("warning", "需要身份", "生成升学路线图前需要登录身份。");
      return;
    }
    state.planEnsuring = true;
    var previous = state.studyPlan && !state.studyPlan.unavailable ? state.studyPlan : {};
    var refreshState = studyPlanRefreshState(previous);
    renderPage(pageByKey[state.route]);
    post(preferAgent ? endpoints.studyGeneratePlan : endpoints.studyEnsurePlan, state.identity.userId).then(function (plan) {
      // 升学路线的阶段保护已由服务端按真实持久化进度处理。这里必须完全采用
      // 服务端结果，避免浏览器把历史单阶段兜底路线再次合并回来。
      state.studyPlan = plan || {};
      if (isStudyRoute()) state.plan = state.studyPlan;
      return post(endpoints.studyDailyPlan, state.identity.userId).then(function (dailyPlan) {
        state.studyDailyPlan = dailyPlan || {};
        if (isStudyRoute()) state.dailyPlan = state.studyDailyPlan;
      }).catch(function () { return null; });
    }).then(function () {
      showMessage("success", preferAgent ? "升学路线图已生成" : "升学路线图已准备",
        refreshState.protectedCount > 0 ? "进行中和已完成阶段已保留。" : "今日行动已按所选升学方向安排。");
    }).catch(function (error) {
      // 生成失败时也重新读取服务端状态。服务端可能已清理无效历史路线，不能让
      // 浏览器继续显示本地缓存中的虚假阶段；有效旧路线则会由服务端原样返回。
      return post(endpoints.studyPlan, state.identity.userId).then(function (plan) {
        state.studyPlan = plan || {};
        if (isStudyRoute()) state.plan = state.studyPlan;
      }).catch(function () { return null; }).then(function () {
        showMessage("error", "升学路线图生成失败", error.message || "未保存无效路线，请稍后重试真实智能规划。");
      });
    }).then(function () {
      state.planEnsuring = false;
      renderPage(pageByKey[state.route]);
    });
  }

  function ensureCurrentRoutePlan(preferAgent) {
    if (isStudyRoute()) ensureStudyPlan(preferAgent);
    else ensureEmploymentPlan(preferAgent);
  }

  function ensureStudyCenterData() {
    loadStudyCenterResources();
    if (hasUserIdentity()) {
      loadStudyCenterSelection();
      if (state.studyCenterSelection) loadStudyPlanningMaterials(false);
    }
  }

  function loadStudyCenterSelection() {
    if (state.studyCenterSelectionLoading || state.studyCenterSelection || !hasUserIdentity()) return;
    state.studyCenterSelectionLoading = true;
    post(endpoints.studyCenterSelection, state.identity.userId).then(function (selection) {
      state.studyCenterSelection = selection || {};
      state.studyCenterInsight = null;
      loadStudyCenterInsight();
      loadStudyPlanningMaterials(false);
    }).catch(function (error) {
      state.studyCenterInsightError = error.message || "升学中心暂不可用。";
    }).then(function () {
      state.studyCenterSelectionLoading = false;
      if (state.route === "further-study-home") renderPage(pageByKey[state.route]);
    });
  }

  function loadStudyCenterInsight() {
    if (state.studyCenterInsightLoading || state.studyCenterInsight || state.studyCenterInsightError || !hasUserIdentity()) return;
    state.studyCenterInsightLoading = true;
    post(endpoints.studyCenterInsight, state.identity.userId).then(function (insight) {
      state.studyCenterInsight = insight || {};
    }).catch(function (error) {
      state.studyCenterInsightError = error.message || "升学洞察暂不可用。";
    }).then(function () {
      state.studyCenterInsightLoading = false;
      if (state.route === "further-study-home") renderPage(pageByKey[state.route]);
    });
  }

  function loadStudyCenterResources() {
    if (state.studyCenterResourcesLoading || state.studyCenterResources || state.studyCenterResourcesError) return;
    state.studyCenterResourcesLoading = true;
    post(endpoints.studyCenterResources, "").then(function (feed) {
      state.studyCenterResources = feed || {};
    }).catch(function (error) {
      state.studyCenterResourcesError = error.message || "升学资讯暂不可用。";
    }).then(function () {
      state.studyCenterResourcesLoading = false;
      if (state.route === "further-study-home" || state.route === "study-resources") renderPage(pageByKey[state.route]);
    });
  }

  function loadStudyPlanningMaterials(force) {
    if (!hasUserIdentity()) return;
    var selection = state.studyCenterSelection || {};
    var direction = firstText(selection.direction, "");
    if (direction !== "POSTGRADUATE" && direction !== "RECOMMENDATION") {
      state.studyPlanningMaterials = [];
      state.studyPlanningMaterialsError = null;
      return;
    }
    if (state.studyPlanningMaterialsLoading || (!force && state.studyPlanningMaterials)) return;
    state.studyPlanningMaterialsLoading = true;
    state.studyPlanningMaterialsError = null;
    post(endpoints.studyMaterialList, {
      userId: state.identity.userId,
      direction: direction
    }).then(function (materials) {
      state.studyPlanningMaterials = Array.isArray(materials) ? materials : [];
    }).catch(function (error) {
      state.studyPlanningMaterialsError = error.message || "规划资料暂时无法读取。";
    }).then(function () {
      state.studyPlanningMaterialsLoading = false;
      if (state.route === "further-study-home") renderPage(pageByKey[state.route]);
    });
  }

  function uploadStudyPlanningMaterial() {
    if (state.studyPlanningMaterialsUploading || !hasUserIdentity()) return;
    var input = $("studyPlanningMaterialFile");
    var file = input && input.files && input.files[0];
    if (!file) {
      showMessage("warning", "请选择资料", "请选择一份 PDF、Word、TXT 或 Markdown 文件。");
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      showMessage("warning", "文件过大", "单份规划资料不能超过 5MB。");
      return;
    }
    if (isFilePreview()) {
      showMessage("info", "本地预览不上传资料", "请在应用服务中测试资料上传。");
      return;
    }
    state.studyPlanningMaterialsUploading = true;
    var direction = firstText(getValue(state.studyCenterSelection, "direction"), "");
    renderPage(pageByKey[state.route]);
    readFileAsBase64(file).then(function (base64) {
      return post(endpoints.studyMaterialUpload, {
        userId: state.identity.userId,
        request: {
          direction: direction,
          materialType: valueOf("studyPlanningMaterialType") || "OTHER",
          title: file.name,
          mediaType: file.type || "",
          file: {
            originalFilename: file.name,
            base64: base64
          }
        }
      });
    }).then(function (material) {
      state.studyPlanningMaterials = null;
      loadStudyPlanningMaterials(true);
      if (material && material.extractionStatus === "OK") {
        showMessage("success", "资料已保存", "正文读取成功，将用于下一次" + studyCenterDirectionLabel(direction) + "智能路线生成。");
      } else {
        showMessage("warning", "资料已保存", "文件已保存，但没有读取到可用正文；这份资料暂不会用于智能规划。");
      }
    }).catch(function (error) {
      showMessage("error", "资料上传失败", error.message || "请稍后重试。");
    }).then(function () {
      state.studyPlanningMaterialsUploading = false;
      if (state.route === "further-study-home") renderPage(pageByKey[state.route]);
    });
  }

  function deleteStudyPlanningMaterial(materialId) {
    if (!materialId || !hasUserIdentity()) return;
    showConfirmDialog("删除规划资料",
      "删除后，这份资料不会再作为当前升学路线的生成依据，且文件无法恢复。",
      "确认删除", function () {
        return post(endpoints.studyMaterialDelete, {
          userId: state.identity.userId,
          direction: firstText(getValue(state.studyCenterSelection, "direction"), ""),
          materialId: materialId
        }).then(function () {
          state.studyPlanningMaterials = null;
          loadStudyPlanningMaterials(true);
          showMessage("success", "资料已删除", "下一次生成路线时将不再使用这份资料。");
        }).catch(function (error) {
          showMessage("error", "删除失败", error.message || "请稍后重试。");
        });
      }, { danger: true });
  }

  function saveStudyCenterDirection() {
    var select = document.getElementById("studyCenterDirection");
    if (!select || !select.value) { showMessage("warning", "请选择升学方向", "请选择考研、保研或留学后再保存。"); return; }
    if (!hasUserIdentity() || state.studyCenterSaving) return;
    state.studyCenterSaving = true;
    setStudyCenterSaveButtonBusy(true);
    post(endpoints.studyCenterSelectionSave, {
      userId: state.identity.userId,
      direction: select.value
    }).then(function (selection) {
      state.studyCenterSelection = selection || {};
      state.studyCenterInsight = null;
      state.studyCenterInsightError = null;
      state.studyPlanningMaterials = null;
      state.studyPlanningMaterialsError = null;
      return post(endpoints.studyPlan, state.identity.userId).then(function (plan) {
        state.studyPlan = plan || {};
        if (isStudyRoute()) state.plan = state.studyPlan;
      }).catch(function () { return null; });
    }).then(function () {
      showMessage("success", "升学方向已保存", "升学中心已按新方向更新准备建议。");
      loadStudyCenterInsight();
      loadStudyPlanningMaterials(true);
    }).catch(function (error) {
      showMessage("error", "保存失败", error.message || "请稍后重试。");
    }).then(function () {
      state.studyCenterSaving = false;
      setStudyCenterSaveButtonBusy(false);
      if (state.route === "further-study-home") renderPage(pageByKey[state.route]);
    });
  }

  function setStudyCenterSaveButtonBusy(busy) {
    var button = document.querySelector('[data-save-study-direction="true"]');
    if (!button) return;
    button.disabled = !!busy;
    button.setAttribute("aria-disabled", busy ? "true" : "false");
    button.setAttribute("aria-busy", busy ? "true" : "false");
    button.textContent = busy ? "保存中..." : "保存升学方向";
  }

  function renderFurtherStudyHome(item) {
    ensureStudyCenterData();
    renderFeatureShell(item, item.title, "先选择升学方向，再查看准备建议、升学洞察和升学资讯。",
      furtherStudyRoadmapPanel() +
      studyPlanningMaterialsPanel() +
      studyCenterInsightPanel() + studyCenterResourcePanels());
  }

  function furtherStudyRoadmapPanel() {
    var profile = furtherStudyProfile();
    var steps = furtherStudyRoadmapSteps(profile);
    var studyPlan = state.studyPlan && !state.studyPlan.unavailable ? state.studyPlan : {};
    var planButtonLabel = studyPlanGenerationButtonLabel(studyPlan, profile.directionCode);
    return '<section class="feature-section roadmap-section further-study-roadmap">' +
      '<div class="section-heading"><div><h3>升学中心</h3>' +
      '<p class="section-note">根据当前自画像整理准备顺序，先明确方向，再逐步推进关键节点。</p></div>' +
      '<div class="section-actions"><button type="button" class="secondary" data-link="career-plan"' + (!profile.hasDirection ? ' disabled aria-disabled="true"' : '') + '>完整规划</button>' +
      '<button type="button" data-ensure-study-plan="true"' + (!profile.hasDirection || state.planEnsuring ? ' disabled aria-disabled="true"' : '') + '>' +
      (state.planEnsuring ? "生成中" : planButtonLabel) + '</button></div></div>' +
      '<div class="form-grid"><label>选择具体升学方向<select id="studyCenterDirection"><option value="">请选择</option><option value="POSTGRADUATE"' + (profile.directionCode === "POSTGRADUATE" ? " selected" : "") + '>考研</option><option value="RECOMMENDATION"' + (profile.directionCode === "RECOMMENDATION" ? " selected" : "") + '>保研</option><option value="STUDY_ABROAD"' + (profile.directionCode === "STUDY_ABROAD" ? " selected" : "") + '>留学</option></select></label>' +
      '<div class="profile-derived-field"><div class="profile-derived-label"><span>目标院校</span><small>来自升学自画像</small></div>' +
      '<div class="profile-derived-value"' + (profile.targetSchool ? "" : ' data-empty="true"') + '>' +
      escapeHtml(firstText(profile.targetSchool, "请先在首页的升学自画像中填写目标院校")) + '</div></div>' +
      '<div class="actions-row"><button type="button" data-save-study-direction="true"' +
      (state.studyCenterSaving ? ' disabled aria-disabled="true" aria-busy="true"' : ' aria-disabled="false" aria-busy="false"') +
      '>' + (state.studyCenterSaving ? "保存中..." : "保存升学方向") + '</button></div></div>' +
      '<div class="roadmap-panel">' +
      '<div class="roadmap-summary"><span class="resource-type">升学规划</span>' +
      '<strong>' + escapeHtml(profile.direction) + '</strong>' +
      '<p>' + escapeHtml(profile.summary) + '</p>' +
      '<ul class="compact-list">' + profile.focus.map(function (item) {
        return '<li>' + escapeHtml(item) + '</li>';
      }).join("") + '</ul></div>' +
      '<div class="roadmap-steps">' + steps.map(furtherStudyRoadmapStepCard).join("") + '</div>' +
      '</div></section>';
  }

  function studyPlanningMaterialsPanel() {
    var selection = state.studyCenterSelection || {};
    var direction = firstText(selection.direction, "");
    if (direction !== "POSTGRADUATE" && direction !== "RECOMMENDATION") return "";
    var directionLabel = studyCenterDirectionLabel(direction);
    var materials = Array.isArray(state.studyPlanningMaterials) ? state.studyPlanningMaterials : [];
    var availableCount = materials.filter(function (item) {
      return item && item.extractionStatus === "OK" && firstText(item.extractedText, "");
    }).length;
    var status = "";
    if (state.studyPlanningMaterialsLoading) {
      status = statePanel("正在读取规划资料", "请稍候。", "pending");
    } else if (state.studyPlanningMaterialsError) {
      status = statePanel("资料暂时无法读取", state.studyPlanningMaterialsError, "warning");
    } else if (!materials.length) {
      status = statePanel("还没有规划资料", direction === "RECOMMENDATION"
        ? "可以上传目标院校推免说明、夏令营或预推免通知、成绩排名证明及个人经历材料。"
        : "可以上传目标院校招生说明、专业目录、考试科目说明或个人学习证明。", "empty");
    } else {
      status = '<div class="study-material-list">' + materials.map(function (material) {
        var usable = material.extractionStatus === "OK" && firstText(material.extractedText, "");
        return '<article class="study-material-item"><div><strong>' +
          escapeHtml(firstText(material.title, material.originalFilename, "未命名资料")) +
          '</strong><p>' + escapeHtml(studyMaterialTypeLabel(material.materialType)) + " · " +
          escapeHtml(usable ? "可用于智能规划" : "正文暂不可用") +
          (material.extractedCharCount ? " · " + escapeHtml(String(material.extractedCharCount)) + " 字" : "") +
          '</p></div><button type="button" class="secondary danger-text" data-delete-study-material="' +
          escapeHtml(firstText(material.materialId, "")) + '">删除</button></article>';
      }).join("") + '</div>';
    }
    return '<section class="feature-section study-material-section"><div class="section-heading"><div>' +
      '<h3>规划依据资料</h3><p class="section-note">成功读取正文的资料会与用户画像、目标院校一起用于下一次' + escapeHtml(directionLabel) + '智能路线生成，不会写入公共知识库。</p>' +
      '</div><span class="resource-type">可用 ' + availableCount + ' 份</span></div>' +
      '<div class="form-grid study-material-upload"><label>资料类型<select id="studyPlanningMaterialType">' +
      '<option value="ADMISSION_GUIDE">招生或推免说明</option><option value="MAJOR_CATALOG">专业目录与选拔要求</option>' +
      '<option value="SCORE_OR_TRANSCRIPT">成绩或学习证明</option><option value="STUDY_PROGRESS">学习进度记录</option>' +
      '<option value="OTHER">其他相关资料</option></select></label>' +
      '<label>选择文件<input id="studyPlanningMaterialFile" type="file" accept=".pdf,.doc,.docx,.txt,.md,application/pdf,text/plain"></label>' +
      '<div class="actions-row"><button type="button" data-upload-study-material="true"' +
      (state.studyPlanningMaterialsUploading ? ' disabled aria-disabled="true"' : '') + '>' +
      (state.studyPlanningMaterialsUploading ? "上传并读取中" : "上传资料") + '</button></div></div>' + status + '</section>';
  }

  function studyMaterialTypeLabel(value) {
    var labels = {
      ADMISSION_GUIDE: "招生说明",
      MAJOR_CATALOG: "专业目录与考试科目",
      SCORE_OR_TRANSCRIPT: "成绩或学习证明",
      STUDY_PROGRESS: "学习进度记录",
      OTHER: "其他相关资料"
    };
    return labels[value] || "相关资料";
  }

  function furtherStudyProfile() {
    var onboarding = getValue(state.snapshot, "onboarding") || {};
    var preferences = getValue(state.snapshot, "preferences") || {};
    var educationStageValue = firstText(onboarding.educationStage, onboarding.stage, preferences.educationStage, "");
    var educationStage = educationStageValue ? labelForEducationStage(educationStageValue) : "教育阶段待补充";
    var school = firstText(onboarding.school, preferences.school, "");
    var major = firstText(onboarding.major, onboarding.schoolMajor, preferences.major, "");
    var selected = state.studyCenterSelection || {};
    var targetSchool = firstText(onboarding.targetSchool, "");
    var directionCode = firstText(selected.direction, "");
    var direction = directionCode ? studyCenterDirectionLabel(directionCode) : resolveFurtherStudyDirection(onboarding, preferences);
    var missing = [];
    if (!educationStageValue) missing.push("当前教育阶段");
    if (!school) missing.push("当前学校");
    if (!major) missing.push("所学专业");
    var details = [educationStage];
    if (school) details.push(school);
    if (major) details.push(major);
    if (targetSchool) details.push("目标院校：" + targetSchool);
    var focus = [];
    if (direction === "具体升学方向待选择") {
      focus.push("先在下方选择考研、保研或留学方向");
    } else if (targetSchool) {
      focus.push("围绕“" + targetSchool + "”核对招生、申请和时间要求");
    } else {
      focus.push("已选择“" + direction + "”，可继续核对目标与时间安排");
    }
    if (missing.length) {
      focus.push("补充" + missing.join("、") + "，便于判断准备差距");
    } else {
      focus.push("基础教育信息已填写，可开始评估优势与差距");
    }
    focus.push("按阶段完成备考、申请材料和关键节点准备");
    return {
      directionCode: directionCode,
      targetSchool: targetSchool,
      direction: direction,
      hasDirection: direction !== "具体升学方向待选择",
      profileComplete: missing.length === 0,
      summary: "当前情况：" + details.join(" · "),
      focus: focus
    };
  }

  function resolveFurtherStudyDirection(onboarding, preferences) {
    var value = firstText(
      onboarding.studyDirection,
      onboarding.furtherStudyDirection,
      preferences.studyDirection,
      preferences.furtherStudyDirection,
      ""
    );
    if (/保研|推免|recommendation/i.test(value)) return "保研";
    if (/留学|海外|abroad|overseas/i.test(value)) return "留学";
    if (/考研|统考|postgraduate/i.test(value)) return "考研";
    return "具体升学方向待选择";
  }

  function studyCenterDirectionLabel(value) {
    if (value === "POSTGRADUATE") return "考研";
    if (value === "RECOMMENDATION") return "保研";
    if (value === "STUDY_ABROAD") return "留学";
    return "具体升学方向待选择";
  }

  function furtherStudyRoadmapSteps(profile) {
    var activeIndex = !profile.hasDirection ? 0 : !profile.profileComplete ? 1 : 2;
    return [
      {
        title: "明确升学方向",
        desc: "结合个人目标，在考研、保研和留学中确定当前主路线。",
        status: profile.hasDirection ? "已明确" : "当前优先"
      },
      {
        title: "评估基础与差距",
        desc: "梳理教育背景、成绩、语言能力和经历，确认需要补齐的条件。",
        status: profile.profileComplete ? "可继续推进" : "待补信息"
      },
      {
        title: "制定备考或申请计划",
        desc: "围绕目标院校、考试或申请季，安排阶段任务与时间节点。",
        status: profile.hasDirection ? "待制定" : "等待方向"
      },
      {
        title: "推进材料与关键节点",
        desc: "按路线准备复习成果、申请材料、联系沟通和提交事项。",
        status: "后续阶段"
      }
    ].map(function (step, index) {
      step.active = index === activeIndex;
      return step;
    });
  }

  function furtherStudyRoadmapStepCard(step, index) {
    var cls = "roadmap-step" + (step.active ? " active" : "");
    return '<article class="' + cls + '">' +
      '<span class="step-index">' + (index + 1) + '</span><span class="step-copy">' +
      '<strong>' + escapeHtml(step.title) + '</strong><small>' + escapeHtml(step.desc) + '</small></span>' +
      '<span class="step-status">' + escapeHtml(step.status) + '</span></article>';
  }

  function renderPlanProfileStatus(plan, fallbackSummary) {
    var stage = plan && plan.currentStage;
    var score = plan && plan.profileCompletenessScore;
    var missing = normalizeArray(plan && plan.missingSignals);
    var html = '<p>' + escapeHtml(stage ? '你现在正处于：' + plainStageLabel(stage) : fallbackSummary) + '</p>';
    if (score === null || score === undefined || score === '') {
      return html;
    }
    html += '<div class="roadmap-profile-status"><strong>自画像完整度 ' + escapeHtml(score) + '%</strong>';
    if (missing.length) {
      html += '<p>还差这些信息，补齐后规划会更贴合你：</p><ul>' + missing.map(function (signal) {
        var key = signal && signal.key;
        var label = firstText(signal && signal.label, '一项关键信息');
        return '<li><span>' + escapeHtml(label) + '</span><small>' + escapeHtml(missingSignalAction(key)) + '</small></li>';
      }).join('') + '</ul>';
    } else {
      html += '<p>关键信息已补齐，后续会继续根据简历和面试记录更新建议。</p>';
    }
    return html + '</div>';
  }

  function plainStageLabel(stage) {
    var labels = {
      GRADUATE_RESUME_UPLOAD: '先准备好一份可投递的简历',
      ASSESSMENT_BASELINE: '先完成一次画像补全，明确适合的发展方向',
      RESUME_BOOTSTRAP: '先创建或上传简历，整理好基础经历',
      RESUME_IMPROVEMENT: '先把简历优化到可以投递',
      INTERVIEW_BOOTSTRAP: '开始模拟面试练习，准备常见问题',
      INTERVIEW_IMPROVEMENT: '重点提升面试表现，再争取更多机会',
      EXECUTION_RHYTHM: '建立稳定的求职节奏，持续完成每周行动',
      CAREER_MOMENTUM: '持续投递和复盘，积累求职成果'
    };
    return labels[stage] || '按当前路线图继续推进';
  }

  function missingSignalAction(key) {
    var actions = {
      target_role: '在自画像里填写目标岗位或发展方向',
      assessment: '进入画像补全，完成一份测评',
      resume: '进入简历制作，创建或上传简历',
      interview: '完成一次 AI 模拟面试或全景仿真面试',
      target_city: '在自画像补充里填写期望工作城市',
      weekly_hours: '在自画像补充里填写每周可投入的时间',
      career_plan: '点击刷新路线图，生成你的行动计划'
    };
    return actions[key] || '补充这项信息，让建议更贴合你的情况';
  }

  function invalidateEmploymentResources() {
    state.employmentResources = null;
    state.employmentResourcesError = null;
  }

  function invalidateStudyCenterResources() {
    state.studyCenterResources = null;
    state.studyCenterResourcesError = null;
  }

  function employmentInsightProfileRows(insight) {
    var school = firstText(getValue(state.snapshot, "onboarding.education.school"), insight.school, "学校待补充");
    var major = firstText(getValue(state.snapshot, "onboarding.education.major"), insight.major, "专业待补充");
    var legacySchoolMajor = firstText(getValue(state.snapshot, "onboarding.schoolMajor"), "");
    var targetRole = firstText(
      textFromSnapshot("preferences.targetRole", "onboarding.targetRole", "resume.targetJob"),
      insight.targetRole,
      "目标岗位待补充"
    );
    return [
      ["画像状态", chineseInsightStatus(insight)],
      ["学校", chineseInsightText(school)],
      ["专业", chineseInsightText(major === "专业待补充" && legacySchoolMajor ? legacySchoolMajor : major)],
      ["目标岗位", chineseInsightText(targetRole)],
      ["洞察来源", firstText(insight.sourceCount, "0") + " 条"]
    ];
  }

  function employmentResumeSummaryPanel(insight) {
    var resumeBlock = getValue(state.snapshot, "resume") || {};
    var latest = selectedProfileResume(getValue(state.snapshot, "onboarding.selectedResumeId")) || {};
    var rawSummary = firstText(
      latest.aiSummary,
      latest.resumeSummary,
      latest.summary,
      latest.parsedContent,
      resumeBlock.summary,
      resumeBlock.aiSummary
    );
    var title = firstText(latest.title, latest.resumeName, resumeBlock.title, "最新简历");
    var target = firstText(latest.targetJob, latest.targetRole, resumeBlock.targetJob, textFromSnapshot("preferences.targetRole", "onboarding.targetRole"));
    var score = firstText(latest.diagnosisScore, resumeBlock.diagnosisScore, "");
    var sections = resumeSummarySections(rawSummary);
    var fallback = firstText(chineseInsightSummary(insight), "暂无简历摘要。完成简历创建和智能分析后，这里会展示简历摘要。");
    var meta = "";
    if (target) {
      meta += '<span class="resume-summary-chip">目标方向：' + escapeHtml(target) + '</span>';
    }
    if (score) {
      meta += '<span class="resume-summary-chip score">诊断分：' + escapeHtml(score) + '</span>';
    }
    var sectionHtml = sections.map(function (section) {
      return '<div class="resume-summary-section"><span>' + escapeHtml(section.label) + '</span><p>' +
        escapeHtml(section.text) + '</p></div>';
    }).join("");
    if (!sectionHtml) {
      sectionHtml = '<p class="resume-summary-empty">' + escapeHtml(fallback) + '</p>';
    }
    return '<section class="panel employment-resume-summary">' +
      '<div class="resume-summary-head"><div><h3>简历摘要</h3><p>' + escapeHtml(title) + '</p></div></div>' +
      (meta ? '<div class="resume-summary-meta">' + meta + '</div>' : '') +
      '<div class="resume-summary-sections">' + sectionHtml + '</div>' +
      insightHighlights(insight) + '</section>';
  }

  function resumeSummarySections(summary) {
    var parts = String(summary || "")
      .replace(/[\u0000-\u001f]+/g, " ")
      .replace(/[\u25A0-\u25FF\u2700-\u27BF\uE000-\uF8FF\uFFFD]+/g, " ")
      .split(/\s*[|｜]\s*|\s*;\s*|\s*；\s*|\r?\n+/)
      .map(function (part) {
        return part.replace(/[\u0000-\u001f\u25A0-\u25FF\u2700-\u27BF\uE000-\uF8FF\uFFFD]+/g, " ")
          .replace(/\s+/g, " ").trim();
      })
      .filter(function (part) {
        return part.length > 1 && !/^\d{6,}$/.test(part.replace(/\s+/g, "")) &&
          !/^1\d{10}$/.test(part.replace(/\s+/g, "")) && !/@/.test(part);
      });
    var groups = [
      { label: "教育背景", pattern: /大学|学院|本科|硕士|博士|学历|教育经历/ },
      { label: "经历亮点", pattern: /项目|实习|经历|大赛|获奖|作品|著作权|成果/ },
      { label: "专业技能", pattern: /专业技能|技能|HTML|CSS|JavaScript|TypeScript|Java|Python|熟悉|掌握/i }
    ];
    var used = {};
    var sections = [];
    groups.forEach(function (group) {
      var matches = [];
      for (var i = 0; i < parts.length && matches.length < 2; i += 1) {
        if (!used[i] && group.pattern.test(parts[i])) {
          used[i] = true;
          matches.push(shortText(parts[i], 86));
        }
      }
      if (matches.length) sections.push({ label: group.label, text: matches.join("；") });
    });
    if (!sections.length && parts.length) {
      sections.push({ label: "个人概览", text: shortText(parts.slice(0, 3).join("；"), 180) });
    }
    return sections;
  }

  function chineseInsightStatus(insight) {
    var status = firstText(insight.status, "");
    if (status === "AVAILABLE") {
      return Number(insight.sourceCount || 0) > 0 ? "已结合画像匹配" : "画像已读取";
    }
    if (status === "MISSING_SCHOOL") {
      return "待补充学校";
    }
    if (status === "UNSUPPORTED_SCHOOL") {
      return "学校来源待接入";
    }
    if (status === "MISSING_TARGET_ROLE") {
      return "待补充目标岗位";
    }
    if (status === "NO_SOURCES") {
      return "就业来源待接入";
    }
    if (status === "EMPTY") {
      return "暂无就业来源";
    }
    return chineseInsightText(firstText(insight.matchLabel, status, "待完善画像"));
  }

  function chineseInsightSummary(insight) {
    var summary = chineseInsightText(firstText(insight.summary, ""));
    if (isDisplayChinese(summary)) {
      return summary;
    }
    var status = firstText(insight.status, "");
    if (status === "MISSING_SCHOOL") {
      return "自画像中还缺少学校信息，补充学校和专业后可生成更准确的就业洞察。";
    }
    if (status === "UNSUPPORTED_SCHOOL" || status === "NO_SOURCES") {
      return "当前学校的可追溯就业来源尚未接入，页面会先展示自画像与简历摘要。";
    }
    if (status === "MISSING_TARGET_ROLE") {
      return "自画像中还缺少目标岗位，补充后可结合岗位方向生成就业洞察。";
    }
    return "";
  }

  function chineseInsightText(value) {
    var text = firstText(value, "");
    var map = {
      "Employment insight unavailable": "就业洞察暂不可用",
      "Unknown school": "学校待补充",
      "Unknown major": "专业待补充",
      "Unknown target role": "目标岗位待补充",
      "Matched to profile signals": "已结合画像匹配",
      "Using school-level public sources": "使用学校公开来源",
      "School is required before source-backed employment insight can be generated.": "自画像中还缺少学校信息，补充学校后可生成就业洞察。",
      "This school is not connected to verified employment insight sources yet.": "当前学校的可验证就业来源尚未接入。",
      "No traceable employment source is available for this school yet.": "当前学校暂无可追溯就业来源。",
      "Complete the target role to get role-specific employment insight. Current response only uses school-level sources.": "补充目标岗位后，可生成更贴合岗位方向的就业洞察。"
    };
    if (map[text]) {
      return map[text];
    }
    return text
      .replace(/Chengdu University of Technology/g, "成都理工大学")
      .replace(/Chengdu University of Traditional Chinese Medicine/g, "成都中医药大学")
      .replace(/Computer Science/g, "计算机科学")
      .replace(/Software Engineering/g, "软件工程")
      .replace(/Software Engineer/g, "软件工程师")
      .replace(/Frontend Developer/g, "前端开发")
      .replace(/Front-end Developer/g, "前端开发")
      .replace(/Unknown University/g, "学校待补充");
  }

  function insightHighlights(insight) {
    var highlights = normalizeArray(insight.destinationHighlights).slice(0, 3);
    if (!highlights.length) {
      return "";
    }
    var translated = highlights.map(chineseInsightText).filter(function (item) {
      return isDisplayChinese(item);
    });
    if (!translated.length) {
      return "";
    }
    return '<ul class="compact-list">' + translated.map(function (item) {
      return '<li>' + escapeHtml(item) + '</li>';
    }).join("") + '</ul>';
  }

  function employmentResourcePanels() {
    if (state.employmentResourcesLoading) {
      return '<section class="feature-section"><h3>就业资讯与文章</h3>' +
        statePanel("正在加载资源", "正在读取就业资讯、职业指导和公共就业服务入口。", "pending") +
        '</section>';
    }
    if (state.employmentResourcesError) {
      return '<section class="feature-section"><h3>就业资讯与文章</h3>' +
        statePanel("资源暂不可用", state.employmentResourcesError + " 工具入口仍可正常使用。", "warning") +
        '</section>';
    }
    var feed = state.employmentResources || {};
    var articles = pinnedResourcesFirst(feed.articles);
    var services = pinnedResourcesFirst(normalizeArray(feed.consultations).concat(normalizeArray(feed.careerPaths)));
    var videos = pinnedResourcesFirst(feed.videos);
    var total = articles.length + services.length + videos.length;
    if (!total) {
      return '<section class="feature-section"><h3>就业资讯与文章</h3>' +
        statePanel("暂无资源", firstText(feed.message, "当前还没有配置就业资讯资源。"), "empty") +
        '</section>';
    }
    return '<section class="feature-section"><div class="section-heading">' +
      '<h3>就业资讯与文章</h3><button type="button" class="secondary" data-link="career-resources">全部资源</button></div>' +
      '<div class="employment-resource-layout">' +
      resourceColumn("公共服务", services, "service", 2) +
      resourceColumn("精选文章", articles, "article", 2) +
      resourceColumn("相关视频", videos, "video", 2) +
      '</div></section>';
  }

  function renderCareerResourcesPage(item) {
    loadEmploymentResources();
    if (state.employmentResourcesLoading && !state.employmentResources) {
      renderFeatureShell(item, item.title, item.summary,
        '<section class="feature-section">' +
        statePanel("正在加载资源", "正在读取公共服务、精选文章和相关视频。", "pending") +
        '</section>');
      return;
    }
    var feed = state.employmentResources || {};
    var articles = pinnedResourcesFirst(feed.articles);
    var services = pinnedResourcesFirst(normalizeArray(feed.consultations).concat(normalizeArray(feed.careerPaths)));
    var videos = pinnedResourcesFirst(feed.videos);
    var total = articles.length + services.length + videos.length;
    var body = "";
    if (state.employmentResourcesError) {
      body += '<section class="feature-section">' +
        statePanel("资源暂不可用", state.employmentResourcesError + " 请稍后重试。", "warning") +
        '</section>';
    }
    if (!total) {
      body += '<section class="feature-section">' +
        statePanel("暂无资源", firstText(feed.message, "当前还没有配置就业资讯资源。"), "empty") +
        '</section>';
    } else {
      body += '<section class="feature-section">' +
        '<div class="section-heading"><div><h3>全部资源</h3>' +
        '<p class="section-note">按公共服务、精选文章、相关视频分类展示，可直接打开来源平台。</p></div></div>' +
        '<div class="employment-resource-layout">' +
        resourceColumn("公共服务", services, "service", services.length) +
        resourceColumn("精选文章", articles, "article", articles.length) +
        resourceColumn("相关视频", videos, "video", videos.length) +
        '</div></section>';
    }
    renderFeatureShell(item, item.title, item.summary, body);
  }

  function resourceColumn(title, cards, type, limit) {
    var items = pinnedResourcesFirst(cards).slice(0, limit || 4);
    if (!items.length) {
      return '<section class="resource-column"><h4>' + escapeHtml(title) + '</h4><p class="panel-note">暂无内容。</p></section>';
    }
    return '<section class="resource-column"><h4>' + escapeHtml(title) + '</h4><div class="resource-list">' +
      items.map(function (item) { return resourceCard(item, type); }).join("") + '</div></section>';
  }

  function studyCenterInsightPanel() {
    if (state.studyCenterInsightLoading || state.studyCenterSelectionLoading) return '<section class="feature-section"><h3>升学洞察</h3>' + statePanel("正在生成升学洞察", "正在读取你的方向选择和自画像信息。", "pending") + '</section>';
    if (state.studyCenterInsightError) return '<section class="feature-section"><h3>升学洞察</h3>' + statePanel("洞察暂不可用", state.studyCenterInsightError, "warning") + '</section>';
    var insight = state.studyCenterInsight;
    if (!insight) return '<section class="feature-section"><h3>升学洞察</h3>' + statePanel("请选择升学方向", "选择考研、保研或留学后，这里会给出对应的准备建议。", "empty") + '</section>';
    var rows = [["当前方向", firstText(insight.directionLabel, "待选择")], ["学校", firstText(insight.school, "待补充")], ["专业", firstText(insight.major, "待补充")], ["资讯来源", String(insight.sourceCount || 0) + " 条"]];
    return '<section class="feature-section"><div class="section-heading"><h3>升学洞察</h3></div><div class="insight-summary">' + metricsPanel("升学自画像", rows) + '<article class="resume-summary-panel"><h3>准备建议</h3><p>' + escapeHtml(firstText(insight.summary, "选择方向后生成建议。")) + '</p><ul class="compact-list">' + normalizeArray(insight.focusItems).map(function (item) { return '<li>' + escapeHtml(item) + '</li>'; }).join("") + '</ul></article></div></section>';
  }

  function studyCenterResourcePanels() {
    if (state.studyCenterResourcesLoading) return '<section class="feature-section"><h3>升学资讯与文章</h3>' + statePanel("正在加载升学资讯", "升学中心只展示独立维护的升学内容。", "pending") + '</section>';
    if (state.studyCenterResourcesError) return '<section class="feature-section"><h3>升学资讯与文章</h3>' + statePanel("资讯暂不可用", state.studyCenterResourcesError, "warning") + '</section>';
    var feed = state.studyCenterResources || {};
    var services = pinnedResourcesFirst(feed.consultations);
    var articles = pinnedResourcesFirst(feed.articles);
    var videos = pinnedResourcesFirst(feed.videos);
    return '<section class="feature-section"><div class="section-heading"><h3>升学资讯与文章</h3>' +
      '<button type="button" class="secondary" data-link="study-resources">全部资源</button></div><div class="employment-resource-layout">' +
      resourceColumn("官方服务", services, "service", 2) +
      resourceColumn("精选文章", articles, "article", 2) +
      resourceColumn("相关视频", videos, "video", 2) +
      '</div></section>';
  }

  function renderStudyResourcesPage(item) {
    loadStudyCenterResources();
    if (state.studyCenterResourcesLoading && !state.studyCenterResources) {
      renderFeatureShell(item, item.title, item.summary,
        '<section class="feature-section">' +
        statePanel("正在加载升学资源", "正在读取官方服务、精选文章和相关视频。", "pending") +
        '</section>');
      return;
    }
    var feed = state.studyCenterResources || {};
    var services = pinnedResourcesFirst(feed.consultations);
    var articles = pinnedResourcesFirst(feed.articles);
    var videos = pinnedResourcesFirst(feed.videos);
    var total = services.length + articles.length + videos.length;
    var body = "";
    if (state.studyCenterResourcesError) {
      body += '<section class="feature-section">' +
        statePanel("升学资源暂不可用", state.studyCenterResourcesError + " 请稍后重试。", "warning") +
        '</section>';
    }
    if (!total) {
      body += '<section class="feature-section">' +
        statePanel("暂无升学资源", firstText(feed.message, "当前还没有配置升学资讯与文章。"), "empty") +
        '</section>';
    } else {
      body += '<section class="feature-section">' +
        '<div class="section-heading"><div><h3>全部升学资源</h3>' +
        '<p class="section-note">按官方服务、精选文章和相关视频分类展示，可直接打开来源平台。</p></div></div>' +
        '<div class="employment-resource-layout">' +
        resourceColumn("官方服务", services, "service", services.length) +
        resourceColumn("精选文章", articles, "article", articles.length) +
        resourceColumn("相关视频", videos, "video", videos.length) +
        '</div></section>';
    }
    renderFeatureShell(item, item.title, item.summary, body);
  }

  function pinnedResourcesFirst(cards) {
    return normalizeArray(cards).slice().sort(function (left, right) {
      return (right && right.pinned === true ? 1 : 0) - (left && left.pinned === true ? 1 : 0);
    });
  }

  function requestAgentCareerPlanGeneration() {
    if (isStudyRoute()) {
      ensureStudyPlan(true);
      return;
    }
    var plan = state.plan && !state.plan.unavailable ? state.plan : {};
    if (!hasExistingCareerPlan(plan)) {
      ensureEmploymentPlan(true);
      return;
    }
    var refreshState = careerPlanRefreshState(plan);
    if (refreshState.refreshableCount === 0) {
      showConfirmDialog(
        "当前计划不能继续更新",
        "路线图中的所有阶段都已经开始或完成。为避免丢失执行进度，系统不会替换这些阶段，请继续完成现有计划。",
        "我知道了",
        null,
        { danger: true, acknowledgeOnly: true }
      );
      return;
    }
    showConfirmDialog(
      refreshState.protectedCount > 0 ? "更新未开始阶段？" : "重新生成路线图？",
      refreshState.protectedCount > 0
        ? "系统会保留 " + refreshState.protectedCount + " 个进行中或已完成阶段及其进度，只替换 " + refreshState.refreshableCount + " 个未开始阶段。被替换的未开始内容无法恢复。"
        : "重新生成会替换当前路线图中的全部阶段、本周计划和每日建议，原内容无法恢复。",
      refreshState.protectedCount > 0 ? "确认更新未开始阶段" : "确认重新生成",
      function () { ensureEmploymentPlan(true); },
      { danger: true }
    );
  }

  function currentPlanningTarget(plan) {
    if (!isStudyRoute()) return employmentTargetRole(plan);
    var direction = firstText(plan && plan.studyDirection, getValue(state.studyCenterSelection, "direction"));
    return firstText(
      plan && plan.targetRole,
      plan && plan.targetSchool,
      textFromSnapshot("onboarding.targetSchool"),
      direction ? studyCenterDirectionLabel(direction) + "规划" : "",
      "升学目标待确认"
    );
  }

  function hasExistingCareerPlan(plan) {
    return !!(plan && (plan.hasPlan === true || normalizeArray(plan.phases).length));
  }

  function careerPlanRefreshState(plan) {
    var phases = normalizeArray(plan && plan.phases);
    var progressState = readPlanProgress(plan || {}, employmentTargetRole(plan || {}), phases);
    var protectedCount = 0;
    var refreshableCount = 0;
    for (var phaseIndex = 0; phaseIndex < phases.length; phaseIndex += 1) {
      if (isStartedPlanStatus(phases[phaseIndex] && phases[phaseIndex].status)
          || isPhaseStartedByProgress(phases[phaseIndex], phaseIndex, progressState)) protectedCount += 1;
      else refreshableCount += 1;
    }
    return { protectedCount: protectedCount, refreshableCount: refreshableCount };
  }

  function planGenerationButtonLabel(plan, emptyLabel) {
    if (isStudyRoute()) {
      var direction = firstText(plan && plan.studyDirection, getValue(state.studyCenterSelection, "direction"));
      return studyPlanGenerationButtonLabel(plan, direction, emptyLabel);
    }
    if (!hasExistingCareerPlan(plan)) return emptyLabel;
    var refreshState = careerPlanRefreshState(plan);
    if (refreshState.refreshableCount === 0) return "计划已全部开始";
    return refreshState.protectedCount > 0 ? "更新未开始阶段" : emptyLabel;
  }

  function studyPlanGenerationButtonLabel(plan, direction, fallbackLabel) {
    var emptyLabel = direction === "POSTGRADUATE" ? "生成考研规划"
      : direction === "RECOMMENDATION" ? "生成保研规划" : firstText(fallbackLabel, "生成升学规划");
    if (!hasVerifiedStudyPlan(plan, direction)) return emptyLabel;
    var refreshState = studyPlanRefreshState(plan);
    if (refreshState.refreshableCount === 0) return "计划已全部开始";
    return refreshState.protectedCount > 0 ? "更新未开始阶段" : emptyLabel;
  }

  function hasVerifiedStudyPlan(plan, direction) {
    if (!plan || plan.unavailable || plan.hasPlan !== true) return false;
    var phases = normalizeArray(plan.phases);
    var routeDirection = firstText(plan.studyDirection, direction, getValue(state.studyCenterSelection, "direction"));
    if (routeDirection !== "POSTGRADUATE" && routeDirection !== "RECOMMENDATION") return phases.length > 0;
    return phases.length >= 3
      && firstText(plan.planningMode, "").toUpperCase() === "AGENT"
      && firstText(plan.agentStatus, "").toUpperCase() === "AGENT_GENERATED";
  }

  function studyPlanRefreshState(plan) {
    if (!hasVerifiedStudyPlan(plan)) return { protectedCount: 0, refreshableCount: 0 };
    var phases = normalizeArray(plan.phases);
    var protectedCount = 0;
    var refreshableCount = 0;
    for (var phaseIndex = 0; phaseIndex < phases.length; phaseIndex += 1) {
      if (isStartedPlanStatus(phases[phaseIndex] && phases[phaseIndex].status)) protectedCount += 1;
      else refreshableCount += 1;
    }
    return { protectedCount: protectedCount, refreshableCount: refreshableCount };
  }

  function isStartedPlanStatus(status) {
    var raw = trim(status);
    var normalized = raw.toUpperCase().replace(/[-\s]+/g, "_");
    return normalized === "IN_PROGRESS" || normalized === "STARTED" || normalized === "COMPLETED"
      || normalized === "DONE" || normalized === "FINISHED"
      || raw === "执行中" || raw === "进行中" || raw === "已开始" || raw === "已完成";
  }

  function isCompletedPlanStatus(status) {
    var raw = trim(status);
    var normalized = raw.toUpperCase().replace(/[-\s]+/g, "_");
    return normalized === "COMPLETED" || normalized === "DONE" || normalized === "FINISHED" || raw === "已完成";
  }

  function resourceCard(item, type) {
    var rawUrl = firstText(item.sourceUrl, item.url, "");
    var url = externalResourceUrl(item, rawUrl);
    var detailKey = url ? "" : resourceDetailKey(item, rawUrl);
    var meta = [item.pinned === true ? "置顶" : "", resourceTypeLabel(item.type || type), firstText(item.category, item.keyword, ""), shortDate(item.publishedAt)].filter(Boolean).join(" · ");
    return '<article class="resource-card">' +
      '<div><span class="resource-type">' + escapeHtml(meta || "就业资源") + '</span>' +
      '<strong>' + escapeHtml(resourceTitle(item)) + '</strong>' +
      '<p>' + escapeHtml(resourceSummary(item)) + '</p></div>' +
      (detailKey ? '<button type="button" class="resource-link resource-link-button" data-resource-detail="' + escapeAttr(detailKey) + '">查看资源</button>' : '') +
      (url ? '<a class="resource-link" href="' + escapeAttr(url) + '" target="_blank" rel="noopener noreferrer">查看资源</a>' : '') +
      '</article>';
  }

  function externalResourceUrl(item, rawUrl) {
    var value = trim(rawUrl);
    if (/^https?:\/\//i.test(value)) {
      return value;
    }
    var id = trim(item && item.id);
    if (id === "article-resume-001" || id === "tip-resume-evidence-001") {
      return "https://www.ncss.cn/ncss/zd/ws/202208/20220809/2209339200.html";
    }
    if (id === "tip-plan-001" || id === "tip-weekly-focus-001") {
      return "https://www.ncss.cn/ncss/jydt/jy/202404/20240403/2293279892.html";
    }
    if (id === "video-interview-001" || id === "video-interview-practice-001") {
      return "https://www.bilibili.com/video/BV1RN411f7LU/";
    }
    if (id === "path-software" || trim(item && item.careerPathId) === "software-engineer") {
      return "https://search.bilibili.com/all?keyword=%E8%BD%AF%E4%BB%B6%E5%B7%A5%E7%A8%8B%E5%B8%88%20%E6%A0%A1%E6%8B%9B%20%E8%81%8C%E4%B8%9A%E8%B7%AF%E5%BE%84";
    }
    return "";
  }

  function resourceTitle(item) {
    var title = firstText(item.title, "未命名资源");
    if (title === "Resume evidence checklist") {
      return "简历证据清单";
    }
    if (title === "This week career focus") {
      return "本周求职行动重点";
    }
    if (title === "Mock interview practice loop") {
      return "面试练习前的回答结构";
    }
    if (title === "Software Engineer path") {
      return "软件工程师路径";
    }
    return title;
  }

  function resourceSummary(item) {
    var summary = firstText(item.summary, item.body, "暂无摘要。");
    if (summary === "Use target-role evidence to improve resume bullets.") {
      return "围绕目标岗位梳理项目、实习、课程和竞赛证据，再写入简历要点。";
    }
    if (summary === "Pick one target role, one resume improvement and one interview drill.") {
      return "选择一个目标岗位、完成一次简历诊断、安排一次模拟面试，并记录反馈。";
    }
    if (summary === "Practice answer structure before a real interview.") {
      return "用项目复盘和岗位能力关键词整理模拟面试回答。";
    }
    if (summary === "Core route for backend, web, data and AI application roles.") {
      return "面向后端、Web、数据和 AI 应用岗位的基础成长路线。";
    }
    return summary;
  }

  function resourceDetailKey(item, url) {
    var value = trim(url);
    var id = trim(item && item.id);
    if (value.indexOf("/cyancruise/resources/resume-evidence") >= 0 || value.indexOf("/cyancruise/resources/resume-evidence") >= 0 || id === "article-resume-001") {
      return "resume-evidence";
    }
    if (value.indexOf("/cyancruise/resources/weekly-focus") >= 0 || value.indexOf("/cyancruise/resources/weekly-focus") >= 0 || id === "tip-plan-001") {
      return "weekly-focus";
    }
    if (value.indexOf("BV1cyancruise") >= 0 || value.indexOf("BV1cyancruise") >= 0 || id === "video-interview-001") {
      return "interview-practice";
    }
    if (id === "tip-resume-evidence-001") {
      return "resume-evidence";
    }
    if (id === "tip-weekly-focus-001") {
      return "weekly-focus";
    }
    if (id === "video-interview-practice-001") {
      return "interview-practice";
    }
    if (id === "path-software" || trim(item && item.careerPathId) === "software-engineer") {
      return "software-engineer-path";
    }
    return "";
  }

  function resourceTypeLabel(type) {
    var value = trim(type).toLowerCase();
    if (value === "article") {
      return "文章";
    }
    if (value === "video") {
      return "视频";
    }
    if (value === "career_path" || value === "career-path") {
      return "职业路径";
    }
    if (value === "consultation" || value === "tip") {
      return "服务入口";
    }
    return "就业资源";
  }

  function shortDate(value) {
    var text = trim(value);
    if (!text) {
      return "";
    }
    return text.length > 10 ? text.substring(0, 10) : text;
  }

  function previewEmploymentResources() {
    return {
      articles: [
        { type: "article", title: "三“新”看就业共赴好前程", summary: "关注高校毕业生就业新趋势、数智就业服务和模拟面试等公共就业服务实践。", category: "就业观察", sourceUrl: "https://cpc.people.com.cn/n1/2026/0614/c64387-40739823.html" },
        { type: "article", title: "专家支招大学生求职：明确目标、提升自我", summary: "围绕高校毕业生求职困惑，提供目标定位、能力提升和就业指导建议。", category: "求职指导", sourceUrl: "https://www.ncss.cn/ncss/jydt/jy/202404/20240403/2293279892.html" }
      ],
      consultations: [
        { type: "consultation", title: "国家大学生就业服务平台", summary: "查看职位信息、就业指导、专场招聘、重点领域就业和高校毕业生服务。", category: "公共服务", sourceUrl: "https://www.ncss.cn/" },
        { type: "consultation", title: "全国就业公共服务平台", summary: "查询岗位推荐、招聘会信息、职业指导、职业测评和高校毕业生就业服务。", category: "公共服务", sourceUrl: "https://www.12333.gov.cn/job/" },
        { type: "consultation", title: "中国公共招聘网", summary: "查看招聘信息、招聘会信息、事业单位公开招聘和市场资讯。", category: "公共招聘", sourceUrl: "https://job.mohrss.gov.cn/" }
      ],
      careerPaths: [
        { type: "career_path", title: "就业在线", summary: "全国招聘求职服务平台入口，聚合各地公共就业人才服务和招聘求职资源。", category: "公共招聘", sourceUrl: "https://www.jobonline.cn/" }
      ],
      videos: [
        { type: "video", title: "求职简历怎么写？看这 1 个视频就够了", summary: "求职简历讲解视频，可直接跳转播放。", category: "简历", sourceUrl: "https://www.bilibili.com/video/BV1RN411f7LU/" },
        { type: "video", title: "大学生就业季：求职路观察", summary: "央视网就业季视频页面，可直接跳转观看。", category: "就业观察", sourceUrl: "https://news.cctv.com/2013/05/31/VIDE1369989879849147.shtml" }
      ]
    };
  }

  function renderPlannedStudyPage(item) {
    renderFeatureShell(item, item.title, item.summary,
      '<section class="panel full">' +
      '<h3>' + escapeHtml(item.title) + '规划</h3>' +
      '<p class="panel-note">这个方向先作为深造路线入口预留。后续会接入主调度智能体、自画像补全能力和对应规划智能体。</p>' +
      '</section>');
  }

  function renderPostgraduatePage(item) {
    var loading = state.postgraduateLoading;
    var message = state.postgraduateMessage
      ? '<section class="state-card ' + escapeHtml(state.postgraduateMessage.type || "info") + '"><h3>' + escapeHtml(state.postgraduateMessage.title || "提示") + '</h3><p>' + escapeHtml(state.postgraduateMessage.text || "") + '</p></section>'
      : "";
    renderFeatureShell(item, item.title, item.summary,
      message +
      '<section class="panel full postgraduate-hero-panel">' +
      '<h3>考研全周期陪伴</h3>' +
      '<p class="panel-note">先确定目标，再把复习拆成轮次；遇到错题时及时订正，初试后再进入复试材料和导师联系准备。</p>' +
      '<div class="postgraduate-flow"><span>择校择专业</span><span>复习计划</span><span>错题解析</span><span>复试准备</span></div>' +
      '</section>' +
      '<section class="panel full"><h3>智能择校</h3>' +
      '<form class="form-grid" id="postgraduateSchoolForm">' +
      field("pgUndergraduateSchool", "本科学校", "text", "") +
      field("pgUndergraduateLevel", "学校层次", "select", "普通本科", [["985", "985"], ["211", "211 / 双一流"], ["一本", "一本"], ["普通本科", "普通本科"], ["专升本或专科", "专升本或专科"]]) +
      field("pgGpa", "绩点或平均分", "text", "") +
      field("pgEnglishLevel", "英语水平", "text", "") +
      field("pgRegion", "期望地区", "text", "") +
      field("pgTargetMajor", "目标专业", "text", "") +
      '<label class="full">备考偏好<textarea id="pgPreference" placeholder="例如：希望稳一点、希望留在华东、专业课想考 408"></textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "school") + '>生成择校建议</button></div>' +
      '</form>' + renderSchoolRecommendation(state.postgraduateSchoolResult) + '</section>' +
      '<section class="panel full"><h3>动态复习计划</h3>' +
      '<form class="form-grid" id="postgraduatePlanForm">' +
      field("pgTargetSchool", "目标院校", "text", "") +
      field("pgPlanMajor", "目标专业", "text", "") +
      field("pgExamDate", "初试日期", "date", defaultPostgraduateExamDate()) +
      field("pgWeeklyHours", "每周可投入时间", "text", "30 小时") +
      '<label class="full">考试科目<textarea id="pgSubjects" placeholder="例如：数学一、英语一、政治、408 计算机专业课">数学一、英语一、政治、专业课</textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "plan") + '>生成复习计划</button></div>' +
      '</form>' + renderPostgraduatePlan(state.postgraduatePlanResult) + '</section>' +
      '<section class="panel full"><h3>错题本智能解析</h3>' +
      '<form class="form-grid" id="postgraduateMistakeForm">' +
      field("pgMistakeSubject", "科目", "text", "专业课") +
      '<label class="full">题目文本<textarea id="pgQuestionText" placeholder="粘贴题目文字；当前版本暂不识别照片"></textarea></label>' +
      '<label class="full">我的错误答案<textarea id="pgWrongAnswer" placeholder="可选，写下当时的错误思路"></textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "mistake") + '>解析错题</button></div>' +
      '</form>' + renderMistakeAnalysis(state.postgraduateMistakeResult) + '</section>' +
      '<section class="panel full"><h3>复试准备</h3>' +
      '<form class="form-grid" id="postgraduateReexamForm">' +
      field("pgReexamSchool", "目标院校", "text", "") +
      field("pgReexamMajor", "目标专业", "text", "") +
      field("pgPreliminaryStatus", "初试状态", "select", "备考中", [["备考中", "初试前"], ["已初试", "初试已结束"], ["进入复试", "准备复试"]]) +
      '<label class="full">已有材料<textarea id="pgMaterials" placeholder="例如：简历、成绩单、项目材料、论文、竞赛证明"></textarea></label>' +
      '<label class="full">科研或项目经历<textarea id="pgResearchExperience" placeholder="简要写下课程设计、科研、竞赛或项目经历"></textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "reexam") + '>生成复试清单</button></div>' +
      '</form>' + renderReexamPreparation(state.postgraduateReexamResult) + '</section>');
    bindPostgraduateForms();
  }

  function bindPostgraduateForms() {
    var school = $("postgraduateSchoolForm");
    if (school) school.addEventListener("submit", submitPostgraduateSchool);
    var plan = $("postgraduatePlanForm");
    if (plan) plan.addEventListener("submit", submitPostgraduatePlan);
    var mistake = $("postgraduateMistakeForm");
    if (mistake) mistake.addEventListener("submit", submitPostgraduateMistake);
    var reexam = $("postgraduateReexamForm");
    if (reexam) reexam.addEventListener("submit", submitPostgraduateReexam);
  }

  function submitPostgraduateSchool(event) {
    event.preventDefault();
    runPostgraduateAction("school", endpoints.postgraduateSchoolRecommend, {
      userId: state.identity.userId,
      request: {
        undergraduateSchool: valueOf("pgUndergraduateSchool"),
        undergraduateLevel: valueOf("pgUndergraduateLevel"),
        gpa: valueOf("pgGpa"),
        englishLevel: valueOf("pgEnglishLevel"),
        preferredRegion: valueOf("pgRegion"),
        targetMajor: valueOf("pgTargetMajor"),
        preference: valueOf("pgPreference")
      }
    }, function (result) { state.postgraduateSchoolResult = result; });
  }

  function submitPostgraduatePlan(event) {
    event.preventDefault();
    runPostgraduateAction("plan", endpoints.postgraduatePlanGenerate, {
      userId: state.identity.userId,
      request: {
        targetSchool: valueOf("pgTargetSchool"),
        targetMajor: valueOf("pgPlanMajor"),
        examDate: valueOf("pgExamDate"),
        weeklyHours: valueOf("pgWeeklyHours"),
        subjects: splitLines(valueOf("pgSubjects"))
      }
    }, function (result) { state.postgraduatePlanResult = result; });
  }

  function submitPostgraduateMistake(event) {
    event.preventDefault();
    runPostgraduateAction("mistake", endpoints.postgraduateMistakeAnalyze, {
      userId: state.identity.userId,
      request: {
        subject: valueOf("pgMistakeSubject"),
        questionText: valueOf("pgQuestionText"),
        wrongAnswer: valueOf("pgWrongAnswer"),
        targetExam: "考研"
      }
    }, function (result) { state.postgraduateMistakeResult = result; });
  }

  function submitPostgraduateReexam(event) {
    event.preventDefault();
    runPostgraduateAction("reexam", endpoints.postgraduateReexamPrepare, {
      userId: state.identity.userId,
      request: {
        targetSchool: valueOf("pgReexamSchool"),
        targetMajor: valueOf("pgReexamMajor"),
        preliminaryStatus: valueOf("pgPreliminaryStatus"),
        materials: splitLines(valueOf("pgMaterials")),
        researchExperience: valueOf("pgResearchExperience")
      }
    }, function (result) { state.postgraduateReexamResult = result; });
  }

  function runPostgraduateAction(type, endpoint, body, applyResult) {
    state.postgraduateLoading = type;
    state.postgraduateMessage = null;
    renderPage(pageByKey.postgraduate);
    runFurtherStudyService(endpoint, body).then(function (result) {
      applyResult(result || {});
      state.postgraduateMessage = { type: "info", title: "已生成", text: "结果已更新，可继续补充信息后再次生成。" };
    }).catch(function (error) {
      state.postgraduateMessage = { type: "warning", title: "暂时无法生成", text: error && error.message ? error.message : "请稍后重试。" };
    }).then(function () {
      state.postgraduateLoading = "";
      renderPage(pageByKey.postgraduate);
    });
  }

  function renderSchoolRecommendation(result) {
    if (!result) return statePanel("等待择校画像", "填写本科学校、绩点、英语水平、期望地区和目标专业后生成稳、冲、保建议。", "pending");
    var missing = normalizeArray(result.missingInfo);
    return '<div class="postgraduate-result">' +
      '<h4>择校建议</h4><p>' + escapeHtml(result.summary || "已生成择校建议。") + '</p>' +
      (missing.length ? '<p class="panel-note">建议补充：' + escapeHtml(missing.join("、")) + '</p>' : "") +
      '<div class="result-card-grid">' + normalizeArray(result.options).map(function (item) {
        return '<article class="mini-card"><span class="chip">' + escapeHtml(item.tierName || "") + '</span><h4>' + escapeHtml(item.schoolName || "目标院校") + '</h4><p>' + escapeHtml(item.majorName || "") + '</p><p>' + escapeHtml(item.reason || "") + '</p><p class="panel-note">' + escapeHtml(item.risk || "") + '</p>' + simpleListHtml("下一步", item.actions) + '</article>';
      }).join("") + '</div>' + simpleListHtml("提醒", result.reminders) + '</div>';
  }

  function renderPostgraduatePlan(result) {
    if (!result) return statePanel("等待计划信息", "填写目标院校、考试日期和考试科目后生成基础、提高、冲刺三轮计划。", "pending");
    return '<div class="postgraduate-result"><h4>' + escapeHtml(result.target || "复习计划") + '</h4><p>' + escapeHtml(result.summary || "") + '</p>' +
      '<p class="panel-note">初试日期：' + escapeHtml(result.examDate || "未填写") + '，剩余 ' + escapeHtml(result.daysRemaining == null ? "--" : String(result.daysRemaining)) + ' 天</p>' +
      '<div class="result-card-grid">' + normalizeArray(result.rounds).map(function (round) {
        return '<article class="mini-card"><h4>' + escapeHtml(round.roundName || "复习轮次") + '</h4><p class="panel-note">' + escapeHtml(round.dateRange || "") + '</p><p>' + escapeHtml(round.goal || "") + '</p>' +
          simpleListHtml("科目重点", round.subjectFocus) + simpleListHtml("每周任务", round.weeklyTasks) + simpleListHtml("阶段检查", round.checkPoints) +
          '<p class="panel-note">' + escapeHtml(round.stateAdvice || "") + '</p></article>';
      }).join("") + '</div>' + simpleListHtml("每日习惯", result.dailyHabits) + '</div>';
  }

  function renderMistakeAnalysis(result) {
    if (!result) return statePanel("等待错题文本", "粘贴错题文字后，系统会整理答案思路、知识树、易错原因和同类题。", "pending");
    return '<div class="postgraduate-result"><h4>' + escapeHtml(result.subject || "错题解析") + '</h4><p>' + escapeHtml(result.explanation || "") + '</p>' +
      '<p><strong>答案思路：</strong>' + escapeHtml(result.answer || "") + '</p>' +
      '<div class="result-card-grid">' + normalizeArray(result.knowledgeTree).map(function (node) {
        return '<article class="mini-card"><h4>' + escapeHtml(node.name || "知识点") + '</h4>' + simpleListHtml("节点", node.children) + '</article>';
      }).join("") + '</div>' +
      simpleListHtml("易错原因", result.errorReasons) + simpleListHtml("订正步骤", result.correctionSteps) +
      '<div class="result-card-grid">' + normalizeArray(result.derivedQuestions).map(function (item) {
        return '<article class="mini-card"><h4>' + escapeHtml(item.title || "同类题") + '</h4><p>' + escapeHtml(item.hint || "") + '</p><p class="panel-note">' + escapeHtml(item.answerOutline || "") + '</p></article>';
      }).join("") + '</div></div>';
  }

  function renderReexamPreparation(result) {
    if (!result) return statePanel("等待复试信息", "填写目标院校、初试状态和已有材料后生成复试准备清单。", "pending");
    return '<div class="postgraduate-result"><h4>复试准备清单</h4><p>' + escapeHtml(result.summary || "") + '</p>' +
      '<div class="result-card-grid">' + normalizeArray(result.checklist).map(function (item) {
        return '<article class="mini-card"><span class="chip">' + escapeHtml(item.stage || "") + '</span><h4>' + escapeHtml(item.title || "") + '</h4><p>' + escapeHtml(item.detail || "") + '</p><p class="panel-note">优先级：' + escapeHtml(item.priority || "") + '</p></article>';
      }).join("") + '</div>' + simpleListHtml("联系导师", result.tutorContactTips) + simpleListHtml("简历准备", result.resumeTips) + simpleListHtml("模拟面试", result.mockInterviewTips) + '</div>';
  }

  function simpleListHtml(title, items) {
    var list = normalizeArray(items).filter(Boolean);
    if (!list.length) return "";
    return '<div class="simple-list"><strong>' + escapeHtml(title) + '</strong><ul>' + list.map(function (item) {
      return '<li>' + escapeHtml(item) + '</li>';
    }).join("") + '</ul></div>';
  }

  function splitLines(value) {
    return trim(value).split(/[\n,，、;；]+/).map(function (item) { return trim(item); }).filter(Boolean);
  }

  function disabledAttr(disabled) {
    return disabled ? " disabled" : "";
  }

  function defaultPostgraduateExamDate() {
    var now = new Date();
    var year = now.getMonth() >= 11 ? now.getFullYear() + 1 : now.getFullYear();
    return year + "-12-20";
  }

  function renderRecommendationPage(item) {
    var loading = state.recommendationLoading;
    var message = state.recommendationMessage
      ? '<section class="state-card ' + escapeHtml(state.recommendationMessage.type || "info") + '"><h3>' + escapeHtml(state.recommendationMessage.title || "提示") + '</h3><p>' + escapeHtml(state.recommendationMessage.text || "") + '</p></section>'
      : "";
    renderFeatureShell(item, item.title, item.summary,
      message +
      '<section class="panel full postgraduate-hero-panel">' +
      '<h3>保研信息战与材料精修</h3>' +
      '<p class="panel-note">先判断自己是否具备资格和优势，再补背景、锁定夏令营/预推免目标，最后把文书、导师邮件和面试表达打磨成一套材料。</p>' +
      '<div class="postgraduate-flow"><span>排名监控</span><span>背景提升</span><span>材料精修</span><span>导师联系</span></div>' +
      '</section>' +
      '<section class="panel full"><h3>背景竞争力诊断</h3>' +
      '<form class="form-grid" id="recommendationProfileForm">' +
      field("recGrade", "当前年级", "select", "大三", [["大一", "大一"], ["大二", "大二"], ["大三", "大三"], ["大四", "大四"]]) +
      field("recSchool", "本科学校", "text", "") +
      field("recMajor", "本科专业", "text", "") +
      field("recGpa", "绩点或平均分", "text", "") +
      field("recRank", "专业排名", "text", "") +
      field("recEnglish", "英语水平", "text", "") +
      '<label class="full">竞赛与获奖<textarea id="recAwards" placeholder="例如：数学建模国二、蓝桥杯省一、挑战杯校级"></textarea></label>' +
      '<label class="full">科研、论文、软著<textarea id="recResearch" placeholder="例如：参与某课题组、论文在投、软件著作权、课程项目"></textarea></label>' +
      field("recTargetSchools", "目标营校", "text", "") +
      field("recTargetMajor", "目标专业", "text", "") +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "diagnose") + '>诊断竞争力</button><button type="button" class="secondary" id="recommendationPlanButton"' + disabledAttr(loading === "plan") + '>生成行动计划</button></div>' +
      '</form>' + renderRecommendationDiagnosis(state.recommendationDiagnosisResult) + renderRecommendationPlan(state.recommendationPlanResult) + '</section>' +
      '<section class="panel full"><h3>文书润色</h3>' +
      '<form class="form-grid" id="recommendationPolishForm">' +
      field("recDocumentType", "文书类型", "select", "个人自述", [["个人自述", "个人自述"], ["联系导师邮件", "联系导师邮件"], ["推荐信要点", "推荐信要点"]]) +
      field("recPolishMajor", "目标专业", "text", "") +
      '<label class="full">亮点素材<textarea id="recHighlights" placeholder="例如：数学建模国二、课程排名、项目经历、科研兴趣"></textarea></label>' +
      '<label class="full">文书初稿<textarea id="recDraft" placeholder="粘贴初稿，系统会按背景、行动、结果、学术潜力的经历讲述框架改写"></textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "polish") + '>润色文书</button></div>' +
      '</form>' + renderRecommendationPolish(state.recommendationPolishResult) + '</section>' +
      '<section class="panel full"><h3>导师意向信</h3>' +
      '<form class="form-grid" id="recommendationTutorForm">' +
      field("recTutorName", "导师姓名", "text", "") +
      field("recTutorSchool", "目标院校", "text", "") +
      field("recTutorMajor", "目标专业", "text", "") +
      '<label class="full">导师研究方向或论文关键词<textarea id="recResearchDirection" placeholder="请填真实方向；系统不会替你编造导师论文"></textarea></label>' +
      '<label class="full">个人背景<textarea id="recPersonalBackground" placeholder="例如：绩点、排名、竞赛、科研、项目和申请动机"></textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "letter") + '>生成意向信</button></div>' +
      '</form>' + renderRecommendationLetter(state.recommendationTutorLetterResult) + '</section>');
    bindRecommendationForms();
  }

  function bindRecommendationForms() {
    var profile = $("recommendationProfileForm");
    if (profile) profile.addEventListener("submit", submitRecommendationDiagnosis);
    var plan = $("recommendationPlanButton");
    if (plan) plan.addEventListener("click", submitRecommendationPlan);
    var polish = $("recommendationPolishForm");
    if (polish) polish.addEventListener("submit", submitRecommendationPolish);
    var letter = $("recommendationTutorForm");
    if (letter) letter.addEventListener("submit", submitRecommendationLetter);
  }

  function recommendationProfilePayload() {
    return {
      grade: valueOf("recGrade"),
      school: valueOf("recSchool"),
      major: valueOf("recMajor"),
      gpa: valueOf("recGpa"),
      rank: valueOf("recRank"),
      englishLevel: valueOf("recEnglish"),
      awards: valueOf("recAwards"),
      research: valueOf("recResearch"),
      targetSchools: valueOf("recTargetSchools"),
      targetMajor: valueOf("recTargetMajor")
    };
  }

  function submitRecommendationDiagnosis(event) {
    event.preventDefault();
    runRecommendationAction("diagnose", endpoints.recommendationDiagnose, {
      userId: state.identity.userId,
      request: recommendationProfilePayload()
    }, function (result) { state.recommendationDiagnosisResult = result; });
  }

  function submitRecommendationPlan(event) {
    event.preventDefault();
    runRecommendationAction("plan", endpoints.recommendationPlanGenerate, {
      userId: state.identity.userId,
      request: recommendationProfilePayload()
    }, function (result) { state.recommendationPlanResult = result; });
  }

  function submitRecommendationPolish(event) {
    event.preventDefault();
    runRecommendationAction("polish", endpoints.recommendationDocumentPolish, {
      userId: state.identity.userId,
      request: {
        documentType: valueOf("recDocumentType"),
        targetMajor: valueOf("recPolishMajor"),
        highlights: valueOf("recHighlights"),
        draft: valueOf("recDraft")
      }
    }, function (result) { state.recommendationPolishResult = result; });
  }

  function submitRecommendationLetter(event) {
    event.preventDefault();
    runRecommendationAction("letter", endpoints.recommendationTutorLetterGenerate, {
      userId: state.identity.userId,
      request: {
        tutorName: valueOf("recTutorName"),
        targetSchool: valueOf("recTutorSchool"),
        targetMajor: valueOf("recTutorMajor"),
        researchDirection: valueOf("recResearchDirection"),
        personalBackground: valueOf("recPersonalBackground"),
        purpose: "咨询推免机会和课题组方向"
      }
    }, function (result) { state.recommendationTutorLetterResult = result; });
  }

  function runRecommendationAction(type, endpoint, body, applyResult) {
    state.recommendationLoading = type;
    state.recommendationMessage = null;
    renderPage(pageByKey["postgraduate-recommendation"]);
    runFurtherStudyService(endpoint, body).then(function (result) {
      applyResult(result || {});
      state.recommendationMessage = { type: "info", title: "已生成", text: "结果已更新，可继续补充信息后再次生成。" };
    }).catch(function (error) {
      state.recommendationMessage = { type: "warning", title: "暂时无法生成", text: error && error.message ? error.message : "请稍后重试。" };
    }).then(function () {
      state.recommendationLoading = "";
      renderPage(pageByKey["postgraduate-recommendation"]);
    });
  }

  function renderRecommendationDiagnosis(result) {
    if (!result) return statePanel("等待背景信息", "填写绩点、排名、竞赛科研和目标营校后，系统会诊断优势和短板。", "pending");
    return '<div class="postgraduate-result"><h4>竞争力诊断：' + escapeHtml(result.overallScore == null ? "--" : String(result.overallScore)) + ' 分</h4><p>' + escapeHtml(result.summary || "") + '</p>' +
      '<div class="result-card-grid">' + normalizeArray(result.scoreItems).map(function (item) {
        return '<article class="mini-card"><h4>' + escapeHtml(item.name || "维度") + '</h4><p><strong>' + escapeHtml(item.score == null ? "--" : String(item.score)) + '</strong> / ' + escapeHtml(item.maxScore == null ? "--" : String(item.maxScore)) + '</p><p class="panel-note">' + escapeHtml(item.comment || "") + '</p></article>';
      }).join("") + '</div>' + simpleListHtml("优势", result.strengths) + simpleListHtml("短板", result.weaknesses) + renderRecommendationActions(result.actions) + simpleListHtml("提醒", result.reminders) + '</div>';
  }

  function renderRecommendationPlan(result) {
    if (!result) return "";
    return '<div class="postgraduate-result"><h4>行动计划</h4><p>' + escapeHtml(result.summary || "") + '</p>' +
      renderRecommendationActions(result.timeline) + simpleListHtml("每周重点", result.weeklyFocus) + simpleListHtml("营校策略", result.targetCampTips) + '</div>';
  }

  function renderRecommendationPolish(result) {
    if (!result) return statePanel("等待文书初稿", "粘贴自述信、邮件或推荐信要点后，系统会按经历讲述框架改写。", "pending");
    return '<div class="postgraduate-result"><h4>润色稿</h4><pre class="generated-text">' + escapeHtml(result.polishedText || "") + '</pre>' +
      simpleListHtml("改写理由", result.rewriteReasons) + simpleListHtml("保留亮点", result.retainedHighlights) + simpleListHtml("建议补充", result.missingInfo) + '</div>';
  }

  function renderRecommendationLetter(result) {
    if (!result) return statePanel("等待导师信息", "填写导师姓名、真实研究方向和个人背景后生成意向信。", "pending");
    return '<div class="postgraduate-result"><h4>邮件标题</h4><p>' + escapeHtml(result.subject || "") + '</p><h4>邮件正文</h4><pre class="generated-text">' + escapeHtml(result.body || "") + '</pre>' +
      simpleListHtml("附件建议", result.attachments) + simpleListHtml("发送提醒", result.sendTips) + simpleListHtml("需要补充", result.missingInfo) + '</div>';
  }

  function renderRecommendationActions(actions) {
    var list = normalizeArray(actions);
    if (!list.length) return "";
    return '<div class="result-card-grid">' + list.map(function (item) {
      return '<article class="mini-card"><span class="chip">' + escapeHtml(item.stage || "") + '</span><h4>' + escapeHtml(item.title || "") + '</h4><p>' + escapeHtml(item.detail || "") + '</p><p class="panel-note">优先级：' + escapeHtml(item.priority || "") + '</p></article>';
    }).join("") + '</div>';
  }

  function recommendationMessageHtml() {
    return state.recommendationMessage
      ? '<section class="state-card ' + escapeHtml(state.recommendationMessage.type || "info") + '"><h3>' + escapeHtml(state.recommendationMessage.title || "提示") + '</h3><p>' + escapeHtml(state.recommendationMessage.text || "") + '</p></section>'
      : "";
  }

  function recommendationBackActions() {
    return '';
  }

  function recommendationProfileFormHtml(buttonText, loadingType, formId, includePlanButton) {
    return '<form class="form-grid" id="' + formId + '">' +
      field("recGrade", "当前年级", "select", "大三", [["大一", "大一"], ["大二", "大二"], ["大三", "大三"], ["大四", "大四"]]) +
      field("recSchool", "本科学校", "text", "") +
      field("recMajor", "本科专业", "text", "") +
      field("recGpa", "绩点或平均分", "text", "") +
      field("recRank", "专业排名", "text", "") +
      field("recEnglish", "英语水平", "text", "") +
      '<label class="full">竞赛与获奖<textarea id="recAwards" placeholder="例如：数学建模国二、蓝桥杯省一、挑战杯校级"></textarea></label>' +
      '<label class="full">科研、论文、软著<textarea id="recResearch" placeholder="例如：参与课题组、论文在投、软件著作权、课程项目"></textarea></label>' +
      field("recTargetSchools", "目标营校", "text", "") +
      field("recTargetMajor", "目标专业", "text", "") +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(state.recommendationLoading === loadingType) + '>' + buttonText + '</button>' +
      (includePlanButton ? '<button type="button" class="secondary" id="recommendationPlanButton"' + disabledAttr(state.recommendationLoading === "plan") + '>生成行动计划</button>' : "") +
      '</div></form>';
  }

  function renderRecommendationPage(item) {
    renderFeatureShell(item, item.title, item.summary,
      recommendationMessageHtml() +
      '<section class="panel full postgraduate-hero-panel">' +
      '<h3>保研信息战与材料精修</h3>' +
      '<p class="panel-note">先判断自己是否具备资格和优势，再补背景、锁定夏令营/预推免目标，最后把文书、导师邮件和面试表达打磨成一套材料。</p>' +
      '<div class="postgraduate-flow"><span>排名监控</span><span>背景提升</span><span>材料精修</span><span>导师联系</span></div>' +
      '</section>' +
      '<section class="panel full"><h3>选择一个功能</h3>' +
      '<div class="postgraduate-action-grid">' +
      '<button type="button" data-link="recommendation-ranking">排名监控</button>' +
      '<button type="button" data-link="recommendation-background">背景提升</button>' +
      '<button type="button" data-link="recommendation-material">材料精修</button>' +
      '<button type="button" data-link="recommendation-tutor">导师联系</button>' +
      '</div></section>');
  }

  function renderRecommendationRankingPage(item) {
    renderFeatureShell(item, item.title, item.summary,
      recommendationMessageHtml() +
      '<section class="panel full"><h3>排名监控</h3>' +
      '<p class="panel-note">用于判断当前是否接近推免资格边界，以及绩点、排名、英语、竞赛科研中最需要补强的方向。</p>' +
      recommendationProfileFormHtml("诊断竞争力", "diagnose", "recommendationProfileForm", false) +
      renderRecommendationDiagnosis(state.recommendationDiagnosisResult) + recommendationBackActions() + '</section>');
    bindRecommendationForms();
  }

  function renderRecommendationBackgroundPage(item) {
    renderFeatureShell(item, item.title, item.summary,
      recommendationMessageHtml() +
      '<section class="panel full"><h3>背景提升</h3>' +
      '<p class="panel-note">根据当前年级、排名稳定性、竞赛科研和目标营校，生成接下来每周该推进的行动清单。</p>' +
      recommendationProfileFormHtml("生成行动计划", "plan", "recommendationProfileForm", false) +
      renderRecommendationPlan(state.recommendationPlanResult) + recommendationBackActions() + '</section>');
    var profile = $("recommendationProfileForm");
    if (profile) profile.addEventListener("submit", submitRecommendationPlan);
  }

  function renderRecommendationMaterialPage(item) {
    var loading = state.recommendationLoading;
    renderFeatureShell(item, item.title, item.summary,
      recommendationMessageHtml() +
      '<section class="panel full"><h3>材料精修</h3>' +
      '<form class="form-grid" id="recommendationPolishForm">' +
      field("recDocumentType", "文书类型", "select", "个人自述", [["个人自述", "个人自述"], ["联系导师邮件", "联系导师邮件"], ["推荐信要点", "推荐信要点"]]) +
      field("recPolishMajor", "目标专业", "text", "") +
      '<label class="full">亮点素材<textarea id="recHighlights" placeholder="例如：数学建模国二、课程排名、项目经历、科研兴趣"></textarea></label>' +
      '<label class="full">文书初稿<textarea id="recDraft" placeholder="粘贴初稿，系统会按背景、行动、结果、学术潜力的经历讲述框架改写"></textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "polish") + '>润色文书</button></div>' +
      '</form>' + renderRecommendationPolish(state.recommendationPolishResult) + recommendationBackActions() + '</section>');
    bindRecommendationForms();
  }

  function renderRecommendationTutorPage(item) {
    var loading = state.recommendationLoading;
    renderFeatureShell(item, item.title, item.summary,
      recommendationMessageHtml() +
      '<section class="panel full"><h3>导师联系</h3>' +
      '<form class="form-grid" id="recommendationTutorForm">' +
      field("recTutorName", "导师姓名", "text", "") +
      field("recTutorSchool", "目标院校", "text", "") +
      field("recTutorMajor", "目标专业", "text", "") +
      '<label class="full">导师研究方向或论文关键词<textarea id="recResearchDirection" placeholder="请填真实方向；系统不会替你编造导师论文"></textarea></label>' +
      '<label class="full">个人背景<textarea id="recPersonalBackground" placeholder="例如：绩点、排名、竞赛、科研、项目和申请动机"></textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "letter") + '>生成意向信</button></div>' +
      '</form>' + renderRecommendationLetter(state.recommendationTutorLetterResult) + recommendationBackActions() + '</section>');
    bindRecommendationForms();
  }

  function runRecommendationAction(type, endpoint, body, applyResult) {
    state.recommendationLoading = type;
    state.recommendationMessage = null;
    renderPage(pageByKey[state.route] || pageByKey["postgraduate-recommendation"]);
    runFurtherStudyService(endpoint, body).then(function (result) {
      applyResult(result || {});
      state.recommendationMessage = { type: "info", title: "已生成", text: "结果已更新，可继续补充信息后再次生成。" };
    }).catch(function (error) {
      state.recommendationMessage = { type: "warning", title: "暂时无法生成", text: error && error.message ? error.message : "请稍后重试。" };
    }).then(function () {
      state.recommendationLoading = "";
      renderPage(pageByKey[state.route] || pageByKey["postgraduate-recommendation"]);
    });
  }

  function postgraduateMessageHtml() {
    return state.postgraduateMessage
      ? '<section class="state-card ' + escapeHtml(state.postgraduateMessage.type || "info") + '"><h3>' + escapeHtml(state.postgraduateMessage.title || "提示") + '</h3><p>' + escapeHtml(state.postgraduateMessage.text || "") + '</p></section>'
      : "";
  }

  function postgraduateBackActions() {
    return '';
  }

  function renderPostgraduatePage(item) {
    renderFeatureShell(item, item.title, item.summary,
      postgraduateMessageHtml() +
      '<section class="panel full postgraduate-hero-panel">' +
      '<h3>考研全周期陪伴</h3>' +
      '<p class="panel-note">先确定目标，再把复习拆成轮次；遇到错题时及时订正，初试后再进入复试材料和导师联系准备。</p>' +
      '<div class="postgraduate-flow"><span>择校择专业</span><span>复习计划</span><span>错题解析</span><span>复试准备</span></div>' +
      '</section>' +
      '<section class="panel full"><h3>选择一个功能</h3>' +
      '<div class="postgraduate-action-grid">' +
      '<button type="button" data-link="postgraduate-school">择校择专业</button>' +
      '<button type="button" data-link="postgraduate-plan">复习计划</button>' +
      '<button type="button" data-link="postgraduate-mistake">错题解析</button>' +
      '<button type="button" data-link="postgraduate-reexam">复试准备</button>' +
      '</div></section>');
  }

  function renderPostgraduateSchoolPage(item) {
    var loading = state.postgraduateLoading;
    renderFeatureShell(item, item.title, item.summary,
      postgraduateMessageHtml() +
      '<section class="panel full"><h3>择校择专业</h3>' +
      '<form class="form-grid" id="postgraduateSchoolForm">' +
      field("pgUndergraduateSchool", "本科学校", "text", "") +
      field("pgUndergraduateLevel", "学校层次", "select", "普通本科", [["985", "985"], ["211", "211 / 双一流"], ["一本", "一本"], ["普通本科", "普通本科"], ["专升本或专科", "专升本或专科"]]) +
      field("pgGpa", "绩点或平均分", "text", "") +
      field("pgEnglishLevel", "英语水平", "text", "") +
      field("pgRegion", "期望地区", "text", "") +
      field("pgTargetMajor", "目标专业", "text", "") +
      '<label class="full">备考偏好<textarea id="pgPreference" placeholder="例如：希望稳一点、希望留在华东、专业课想考 408"></textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "school") + '>生成择校建议</button></div>' +
      '</form>' + renderSchoolRecommendation(state.postgraduateSchoolResult) + postgraduateBackActions() + '</section>');
    bindPostgraduateForms();
  }

  function renderPostgraduatePlanPage(item) {
    var loading = state.postgraduateLoading;
    renderFeatureShell(item, item.title, item.summary,
      postgraduateMessageHtml() +
      '<section class="panel full"><h3>复习计划</h3>' +
      '<form class="form-grid" id="postgraduatePlanForm">' +
      field("pgTargetSchool", "目标院校", "text", "") +
      field("pgPlanMajor", "目标专业", "text", "") +
      field("pgExamDate", "初试日期", "date", defaultPostgraduateExamDate()) +
      field("pgWeeklyHours", "每周可投入时间", "text", "30 小时") +
      '<label class="full">考试科目<textarea id="pgSubjects" placeholder="例如：数学一、英语一、政治、408 计算机专业课">数学一、英语一、政治、专业课</textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "plan") + '>生成复习计划</button></div>' +
      '</form>' + renderPostgraduatePlan(state.postgraduatePlanResult) + postgraduateBackActions() + '</section>');
    bindPostgraduateForms();
  }

  function renderPostgraduateMistakePage(item) {
    var loading = state.postgraduateLoading;
    renderFeatureShell(item, item.title, item.summary,
      postgraduateMessageHtml() +
      '<section class="panel full"><h3>错题解析</h3>' +
      '<form class="form-grid" id="postgraduateMistakeForm">' +
      field("pgMistakeSubject", "科目", "text", "专业课") +
      '<label class="full">题目文本<textarea id="pgQuestionText" placeholder="粘贴题目文字；当前版本暂不识别照片"></textarea></label>' +
      '<label class="full">我的错误答案<textarea id="pgWrongAnswer" placeholder="可选，写下当时的错误思路"></textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "mistake") + '>解析错题</button></div>' +
      '</form>' + renderMistakeAnalysis(state.postgraduateMistakeResult) + postgraduateBackActions() + '</section>');
    bindPostgraduateForms();
  }

  function renderPostgraduateReexamPage(item) {
    var loading = state.postgraduateLoading;
    renderFeatureShell(item, item.title, item.summary,
      postgraduateMessageHtml() +
      '<section class="panel full"><h3>复试准备</h3>' +
      '<form class="form-grid" id="postgraduateReexamForm">' +
      field("pgReexamSchool", "目标院校", "text", "") +
      field("pgReexamMajor", "目标专业", "text", "") +
      field("pgPreliminaryStatus", "初试状态", "select", "备考中", [["备考中", "初试前"], ["已初试", "初试已结束"], ["进入复试", "准备复试"]]) +
      '<label class="full">已有材料<textarea id="pgMaterials" placeholder="例如：简历、成绩单、项目材料、论文、竞赛证明"></textarea></label>' +
      '<label class="full">科研或项目经历<textarea id="pgResearchExperience" placeholder="简要写下课程设计、科研、竞赛或项目经历"></textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "reexam") + '>生成复试清单</button></div>' +
      '</form>' + renderReexamPreparation(state.postgraduateReexamResult) + postgraduateBackActions() + '</section>');
    bindPostgraduateForms();
  }

  function runPostgraduateAction(type, endpoint, body, applyResult) {
    state.postgraduateLoading = type;
    state.postgraduateMessage = null;
    renderPage(pageByKey[state.route] || pageByKey.postgraduate);
    post(endpoint, body).then(function (result) {
      applyResult(result || {});
      state.postgraduateMessage = { type: "info", title: "已生成", text: "结果已更新，可继续补充信息后再次生成。" };
    }).catch(function (error) {
      state.postgraduateMessage = { type: "warning", title: "暂时无法生成", text: error && error.message ? error.message : "请稍后重试。" };
    }).then(function () {
      state.postgraduateLoading = "";
      renderPage(pageByKey[state.route] || pageByKey.postgraduate);
    });
  }

  function renderStudyAbroadPage(item) {
    var loading = state.studyAbroadLoading;
    var message = state.studyAbroadMessage
      ? '<section class="state-card ' + escapeHtml(state.studyAbroadMessage.type || "info") + '"><h3>' + escapeHtml(state.studyAbroadMessage.title || "提示") + '</h3><p>' + escapeHtml(state.studyAbroadMessage.text || "") + '</p></section>'
      : "";
    renderFeatureShell(item, item.title, item.summary,
      message +
      '<section class="panel full postgraduate-hero-panel">' +
      '<h3>留学全流程陪伴</h3>' +
      '<p class="panel-note">先确认国家地区和语言考试，再把软实力、选校定位、个人陈述和签证网申拆成可执行清单；当前版本使用规则建议，后续可替换为真实 AI 对话和文书批改。</p>' +
      '<div class="postgraduate-flow"><span>国家地区</span><span>语言考试</span><span>选校定位</span><span>文书主线</span><span>签证网申</span></div>' +
      '</section>' +
      '<section class="panel full"><h3>申请准备度诊断</h3>' +
      '<form class="form-grid" id="studyAbroadProfileForm">' +
      field("saCountry", "国家或地区", "text", "") +
      field("saDegree", "目标学位", "select", "硕士", [["本科", "本科"], ["硕士", "硕士"], ["博士", "博士"], ["交换", "交换"]]) +
      field("saTargetMajor", "目标专业", "text", "") +
      field("saSchool", "当前学校", "text", "") +
      field("saMajor", "当前专业", "text", "") +
      field("saGpa", "绩点或平均分", "text", "") +
      field("saLanguageScore", "语言成绩", "text", "") +
      field("saBudget", "预算范围", "text", "") +
      '<label class="full">软实力经历<textarea id="saBackground" placeholder="例如：科研、课程项目、实习、竞赛、交换、志愿经历"></textarea></label>' +
      '<label class="full">申请偏好<textarea id="saPreference" placeholder="例如：偏向一年制、希望毕业后就业、需要奖学金、希望城市安全"></textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "profile") + '>诊断准备度</button></div>' +
      '</form>' + renderStudyAbroadProfile(state.studyAbroadProfileResult) + '</section>' +
      '<section class="panel full"><h3>语言考试计划</h3>' +
      '<form class="form-grid" id="studyAbroadLanguageForm">' +
      field("saExamType", "考试类型", "select", "雅思", [["雅思", "雅思"], ["托福", "托福"], ["GRE", "GRE"], ["其他", "其他"]]) +
      field("saCurrentScore", "当前成绩", "text", "") +
      field("saTargetScore", "目标成绩", "text", "") +
      field("saExamDate", "考试日期", "date", defaultLanguageExamDate()) +
      field("saWeeklyHours", "每周可投入时间", "text", "12 小时") +
      '<label class="full">薄弱单项<textarea id="saWeakParts" placeholder="例如：雅思写作小作文、口语追问、托福听力笔记"></textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "language") + '>生成语言计划</button></div>' +
      '</form>' + renderStudyAbroadLanguage(state.studyAbroadLanguageResult) + '</section>' +
      '<section class="panel full"><h3>选校定位</h3>' +
      '<form class="form-grid" id="studyAbroadSchoolForm">' +
      field("saSchoolCountry", "国家或地区", "text", "") +
      field("saSchoolMajor", "目标专业", "text", "") +
      field("saSchoolGpa", "绩点或平均分", "text", "") +
      field("saSchoolLanguage", "语言成绩", "text", "") +
      field("saSchoolBudget", "预算范围", "text", "") +
      '<label class="full">背景和偏好<textarea id="saSchoolPreference" placeholder="例如：希望冲 QS 前 50、接受跨专业、预算有限、希望带实习项目"></textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "school") + '>生成选校梯度</button></div>' +
      '</form>' + renderStudyAbroadSchools(state.studyAbroadSchoolResult) + '</section>' +
      '<section class="panel full"><h3>个人陈述主线</h3>' +
      '<form class="form-grid" id="studyAbroadStatementForm">' +
      field("saStatementMajor", "目标专业", "text", "") +
      field("saStatementTopic", "教授课题或项目方向", "text", "") +
      '<label class="full">个人故事<textarea id="saPersonalStory" placeholder="写一个真实的转折、问题、观察或经历，不需要华丽"></textarea></label>' +
      '<label class="full">学术或项目经历<textarea id="saAcademicExperience" placeholder="课程项目、科研、论文、实习、竞赛或作品集经历"></textarea></label>' +
      field("saCareerGoal", "未来目标", "text", "") +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "statement") + '>生成文书主线</button></div>' +
      '</form>' + renderStudyAbroadStatement(state.studyAbroadStatementResult) + '</section>' +
      '<section class="panel full"><h3>签证与网申清单</h3>' +
      '<form class="form-grid" id="studyAbroadVisaForm">' +
      field("saVisaCountry", "国家或地区", "text", "") +
      field("saSeason", "申请季", "text", "2026 秋季") +
      field("saAdmissionStatus", "录取状态", "select", "准备申请", [["准备申请", "准备申请"], ["已投递", "已投递"], ["已拿录取", "已拿录取"], ["已确认入读", "已确认入读"]]) +
      '<label class="full">材料状态<textarea id="saMaterialStatus" placeholder="例如：护照已办、成绩单待翻译、推荐信两封确认、资金证明未准备"></textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "visa") + '>生成清单</button></div>' +
      '</form>' + renderStudyAbroadVisa(state.studyAbroadVisaResult) + '</section>');
    bindStudyAbroadForms();
  }

  function bindStudyAbroadForms() {
    var profile = $("studyAbroadProfileForm");
    if (profile) profile.addEventListener("submit", submitStudyAbroadProfile);
    var language = $("studyAbroadLanguageForm");
    if (language) language.addEventListener("submit", submitStudyAbroadLanguage);
    var school = $("studyAbroadSchoolForm");
    if (school) school.addEventListener("submit", submitStudyAbroadSchool);
    var statement = $("studyAbroadStatementForm");
    if (statement) statement.addEventListener("submit", submitStudyAbroadStatement);
    var visa = $("studyAbroadVisaForm");
    if (visa) visa.addEventListener("submit", submitStudyAbroadVisa);
  }

  function submitStudyAbroadProfile(event) {
    event.preventDefault();
    runStudyAbroadAction("profile", endpoints.studyAbroadProfileDiagnose, {
      userId: state.identity.userId,
      request: {
        countryOrRegion: valueOf("saCountry"),
        targetDegree: valueOf("saDegree"),
        targetMajor: valueOf("saTargetMajor"),
        school: valueOf("saSchool"),
        major: valueOf("saMajor"),
        gpa: valueOf("saGpa"),
        languageScore: valueOf("saLanguageScore"),
        budget: valueOf("saBudget"),
        background: valueOf("saBackground"),
        preference: valueOf("saPreference")
      }
    }, function (result) { state.studyAbroadProfileResult = result; });
  }

  function submitStudyAbroadLanguage(event) {
    event.preventDefault();
    runStudyAbroadAction("language", endpoints.studyAbroadLanguagePlan, {
      userId: state.identity.userId,
      request: {
        examType: valueOf("saExamType"),
        currentScore: valueOf("saCurrentScore"),
        targetScore: valueOf("saTargetScore"),
        examDate: valueOf("saExamDate"),
        weeklyHours: valueOf("saWeeklyHours"),
        weakParts: valueOf("saWeakParts")
      }
    }, function (result) { state.studyAbroadLanguageResult = result; });
  }

  function submitStudyAbroadSchool(event) {
    event.preventDefault();
    runStudyAbroadAction("school", endpoints.studyAbroadSchoolPosition, {
      userId: state.identity.userId,
      request: {
        countryOrRegion: valueOf("saSchoolCountry"),
        targetMajor: valueOf("saSchoolMajor"),
        gpa: valueOf("saSchoolGpa"),
        languageScore: valueOf("saSchoolLanguage"),
        budget: valueOf("saSchoolBudget"),
        preference: valueOf("saSchoolPreference")
      }
    }, function (result) { state.studyAbroadSchoolResult = result; });
  }

  function submitStudyAbroadStatement(event) {
    event.preventDefault();
    runStudyAbroadAction("statement", endpoints.studyAbroadStatementOutline, {
      userId: state.identity.userId,
      request: {
        targetMajor: valueOf("saStatementMajor"),
        professorTopic: valueOf("saStatementTopic"),
        personalStory: valueOf("saPersonalStory"),
        academicExperience: valueOf("saAcademicExperience"),
        careerGoal: valueOf("saCareerGoal"),
        language: "中文"
      }
    }, function (result) { state.studyAbroadStatementResult = result; });
  }

  function submitStudyAbroadVisa(event) {
    event.preventDefault();
    runStudyAbroadAction("visa", endpoints.studyAbroadVisaChecklist, {
      userId: state.identity.userId,
      request: {
        countryOrRegion: valueOf("saVisaCountry"),
        applicationSeason: valueOf("saSeason"),
        admissionStatus: valueOf("saAdmissionStatus"),
        materialStatus: valueOf("saMaterialStatus")
      }
    }, function (result) { state.studyAbroadVisaResult = result; });
  }

  function runStudyAbroadAction(type, endpoint, body, applyResult) {
    state.studyAbroadLoading = type;
    state.studyAbroadMessage = null;
    renderPage(pageByKey["study-abroad"]);
    post(endpoint, body).then(function (result) {
      applyResult(result || {});
      state.studyAbroadMessage = { type: "info", title: "已生成", text: "结果已更新；当前为规则版建议，后续可接入真实 AI 进行更细的多轮指导。" };
    }).catch(function (error) {
      state.studyAbroadMessage = { type: "warning", title: "暂时无法生成", text: error && error.message ? error.message : "请稍后重试。" };
    }).then(function () {
      state.studyAbroadLoading = "";
      renderPage(pageByKey["study-abroad"]);
    });
  }

  function renderStudyAbroadProfile(result) {
    if (!result) return statePanel("等待申请画像", "填写国家地区、语言成绩、预算和经历后，系统会先给出准备度、短板和下一步清单。", "pending");
    return '<div class="postgraduate-result"><h4>准备度：' + escapeHtml(result.readinessScore == null ? "--" : String(result.readinessScore)) + ' 分</h4><p>' + escapeHtml(result.summary || "") + '</p>' +
      simpleListHtml("优势", result.strengths) + simpleListHtml("短板", result.gaps) +
      renderStudyAbroadChecklist(result.nextActions) + simpleListHtml("提醒", result.reminders) + '</div>';
  }

  function renderStudyAbroadLanguage(result) {
    if (!result) return statePanel("等待语言目标", "填写考试类型、当前成绩、目标成绩和考试日期后，系统会生成三轮备考计划。", "pending");
    return '<div class="postgraduate-result"><h4>语言考试计划</h4><p>' + escapeHtml(result.summary || "") + '</p>' +
      renderStudyAbroadChecklist(result.rounds) + simpleListHtml("每周节奏", result.weeklyRoutine) + simpleListHtml("训练提示", result.examinerTips) + '</div>';
  }

  function renderStudyAbroadSchools(result) {
    if (!result) return statePanel("等待选校信息", "填写目标地区、专业、成绩、语言和预算后，系统会给出冲刺、匹配、稳妥三档建议。", "pending");
    return '<div class="postgraduate-result"><h4>选校梯度</h4><p>' + escapeHtml(result.summary || "") + '</p>' +
      '<div class="result-card-grid">' + normalizeArray(result.options).map(function (option) {
        return '<article class="mini-card"><span class="chip">' + escapeHtml(option.tier || "") + '</span><h4>' + escapeHtml(option.schoolName || "目标项目") + '</h4><p>' + escapeHtml(option.program || "") + '</p><p>' + escapeHtml(option.reason || "") + '</p>' + simpleListHtml("准备重点", option.preparation) + '</article>';
      }).join("") + '</div>' + simpleListHtml("注意", result.cautions) + '</div>';
  }

  function renderStudyAbroadStatement(result) {
    if (!result) return statePanel("等待故事素材", "填写个人故事、学术经历和目标项目方向后，系统会生成个人陈述主线和追问清单。", "pending");
    return '<div class="postgraduate-result"><h4>个人陈述主线</h4><p>' + escapeHtml(result.goldenLine || "") + '</p><h4>提纲</h4><pre class="generated-text">' + escapeHtml(result.outline || "") + '</pre>' +
      simpleListHtml("继续追问", result.storyQuestions) + simpleListHtml("建议补充", result.missingInfo) + simpleListHtml("写作提醒", result.writingTips) + '</div>';
  }

  function renderStudyAbroadVisa(result) {
    if (!result) return statePanel("等待申请阶段", "填写目标国家、申请季、录取状态和材料状态后，系统会生成网申与签证清单。", "pending");
    return '<div class="postgraduate-result"><h4>签证与网申清单</h4><p>' + escapeHtml(result.summary || "") + '</p>' +
      renderStudyAbroadChecklist(result.checklist) + simpleListHtml("风险", result.risks) + simpleListHtml("提醒", result.reminders) + '</div>';
  }

  function renderStudyAbroadChecklist(items) {
    var list = normalizeArray(items);
    if (!list.length) return "";
    return '<div class="result-card-grid">' + list.map(function (item) {
      return '<article class="mini-card"><span class="chip">' + escapeHtml(item.stage || "") + '</span><h4>' + escapeHtml(item.title || "") + '</h4><p>' + escapeHtml(item.detail || "") + '</p><p class="panel-note">优先级：' + escapeHtml(item.priority || "") + '</p></article>';
    }).join("") + '</div>';
  }

  function defaultLanguageExamDate() {
    var date = new Date();
    date.setDate(date.getDate() + 90);
    var month = String(date.getMonth() + 1);
    var day = String(date.getDate());
    return date.getFullYear() + "-" + (month.length < 2 ? "0" + month : month) + "-" + (day.length < 2 ? "0" + day : day);
  }

  function studyAbroadMessageHtml() {
    return state.studyAbroadMessage
      ? '<section class="state-card ' + escapeHtml(state.studyAbroadMessage.type || "info") + '"><h3>' + escapeHtml(state.studyAbroadMessage.title || "提示") + '</h3><p>' + escapeHtml(state.studyAbroadMessage.text || "") + '</p></section>'
      : "";
  }

  function studyAbroadBackActions() {
    return '';
  }

  function renderStudyAbroadPage(item) {
    renderFeatureShell(item, item.title, item.summary,
      studyAbroadMessageHtml() +
      '<section class="panel full postgraduate-hero-panel">' +
      '<h3>留学全流程陪伴</h3>' +
      '<p class="panel-note">先确认国家地区和语言考试，再把软实力、选校定位、个人陈述和签证网申拆成可执行清单；当前版本使用规则建议，后续可替换为真实 AI 对话和文书批改。</p>' +
      '<div class="postgraduate-flow"><span>国家地区</span><span>语言考试</span><span>选校定位</span><span>文书主线</span><span>签证网申</span></div>' +
      '</section>' +
      '<section class="panel full"><h3>选择一个功能</h3>' +
      '<div class="postgraduate-action-grid">' +
      '<button type="button" data-link="study-abroad-profile">国家地区</button>' +
      '<button type="button" data-link="study-abroad-language">语言考试</button>' +
      '<button type="button" data-link="study-abroad-school">选校定位</button>' +
      '<button type="button" data-link="study-abroad-statement">文书主线</button>' +
      '<button type="button" data-link="study-abroad-visa">签证网申</button>' +
      '</div></section>');
  }

  function renderStudyAbroadProfilePage(item) {
    var loading = state.studyAbroadLoading;
    renderFeatureShell(item, item.title, item.summary,
      studyAbroadMessageHtml() +
      '<section class="panel full"><h3>国家地区与申请画像</h3>' +
      '<form class="form-grid" id="studyAbroadProfileForm">' +
      field("saCountry", "国家或地区", "text", "") +
      field("saDegree", "目标学位", "select", "硕士", [["本科", "本科"], ["硕士", "硕士"], ["博士", "博士"], ["交换", "交换"]]) +
      field("saTargetMajor", "目标专业", "text", "") +
      field("saSchool", "当前学校", "text", "") +
      field("saMajor", "当前专业", "text", "") +
      field("saGpa", "绩点或平均分", "text", "") +
      field("saLanguageScore", "语言成绩", "text", "") +
      field("saBudget", "预算范围", "text", "") +
      '<label class="full">软实力经历<textarea id="saBackground" placeholder="例如：科研、课程项目、实习、竞赛、交换、志愿经历"></textarea></label>' +
      '<label class="full">申请偏好<textarea id="saPreference" placeholder="例如：偏向一年制、希望毕业后就业、需要奖学金、希望城市安全"></textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "profile") + '>诊断准备度</button></div>' +
      '</form>' + renderStudyAbroadProfile(state.studyAbroadProfileResult) + studyAbroadBackActions() + '</section>');
    bindStudyAbroadForms();
  }

  function renderStudyAbroadLanguagePage(item) {
    var loading = state.studyAbroadLoading;
    renderFeatureShell(item, item.title, item.summary,
      studyAbroadMessageHtml() +
      '<section class="panel full"><h3>语言考试</h3>' +
      '<form class="form-grid" id="studyAbroadLanguageForm">' +
      field("saExamType", "考试类型", "select", "雅思", [["雅思", "雅思"], ["托福", "托福"], ["GRE", "GRE"], ["其他", "其他"]]) +
      field("saCurrentScore", "当前成绩", "text", "") +
      field("saTargetScore", "目标成绩", "text", "") +
      field("saExamDate", "考试日期", "date", defaultLanguageExamDate()) +
      field("saWeeklyHours", "每周可投入时间", "text", "12 小时") +
      '<label class="full">薄弱单项<textarea id="saWeakParts" placeholder="例如：雅思写作小作文、口语追问、托福听力笔记"></textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "language") + '>生成语言计划</button></div>' +
      '</form>' + renderStudyAbroadLanguage(state.studyAbroadLanguageResult) + studyAbroadBackActions() + '</section>');
    bindStudyAbroadForms();
  }

  function renderStudyAbroadSchoolPage(item) {
    var loading = state.studyAbroadLoading;
    renderFeatureShell(item, item.title, item.summary,
      studyAbroadMessageHtml() +
      '<section class="panel full"><h3>选校定位</h3>' +
      '<form class="form-grid" id="studyAbroadSchoolForm">' +
      field("saSchoolCountry", "国家或地区", "text", "") +
      field("saSchoolMajor", "目标专业", "text", "") +
      field("saSchoolGpa", "绩点或平均分", "text", "") +
      field("saSchoolLanguage", "语言成绩", "text", "") +
      field("saSchoolBudget", "预算范围", "text", "") +
      '<label class="full">背景和偏好<textarea id="saSchoolPreference" placeholder="例如：希望冲 QS 前 50、接受跨专业、预算有限、希望带实习项目"></textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "school") + '>生成选校梯度</button></div>' +
      '</form>' + renderStudyAbroadSchools(state.studyAbroadSchoolResult) + studyAbroadBackActions() + '</section>');
    bindStudyAbroadForms();
  }

  function renderStudyAbroadStatementPage(item) {
    var loading = state.studyAbroadLoading;
    renderFeatureShell(item, item.title, item.summary,
      studyAbroadMessageHtml() +
      '<section class="panel full"><h3>文书主线</h3>' +
      '<form class="form-grid" id="studyAbroadStatementForm">' +
      field("saStatementMajor", "目标专业", "text", "") +
      field("saStatementTopic", "教授课题或项目方向", "text", "") +
      '<label class="full">个人故事<textarea id="saPersonalStory" placeholder="写一个真实的转折、问题、观察或经历，不需要华丽"></textarea></label>' +
      '<label class="full">学术或项目经历<textarea id="saAcademicExperience" placeholder="课程项目、科研、论文、实习、竞赛或作品集经历"></textarea></label>' +
      field("saCareerGoal", "未来目标", "text", "") +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "statement") + '>生成文书主线</button></div>' +
      '</form>' + renderStudyAbroadStatement(state.studyAbroadStatementResult) + studyAbroadBackActions() + '</section>');
    bindStudyAbroadForms();
  }

  function renderStudyAbroadVisaPage(item) {
    var loading = state.studyAbroadLoading;
    renderFeatureShell(item, item.title, item.summary,
      studyAbroadMessageHtml() +
      '<section class="panel full"><h3>签证网申</h3>' +
      '<form class="form-grid" id="studyAbroadVisaForm">' +
      field("saVisaCountry", "国家或地区", "text", "") +
      field("saSeason", "申请季", "text", "2026 秋季") +
      field("saAdmissionStatus", "录取状态", "select", "准备申请", [["准备申请", "准备申请"], ["已投递", "已投递"], ["已拿录取", "已拿录取"], ["已确认入读", "已确认入读"]]) +
      '<label class="full">材料状态<textarea id="saMaterialStatus" placeholder="例如：护照已办、成绩单待翻译、推荐信两封确认、资金证明未准备"></textarea></label>' +
      '<div class="full actions-row"><button type="submit"' + disabledAttr(loading === "visa") + '>生成清单</button></div>' +
      '</form>' + renderStudyAbroadVisa(state.studyAbroadVisaResult) + studyAbroadBackActions() + '</section>');
    bindStudyAbroadForms();
  }

  function runStudyAbroadAction(type, endpoint, body, applyResult) {
    state.studyAbroadLoading = type;
    state.studyAbroadMessage = null;
    renderPage(pageByKey[state.route] || pageByKey["study-abroad"]);
    post(endpoint, body).then(function (result) {
      applyResult(result || {});
      state.studyAbroadMessage = { type: "info", title: "已生成", text: "结果已更新；当前为规则版建议，后续可接入真实 AI 进行更细的多轮指导。" };
    }).catch(function (error) {
      state.studyAbroadMessage = { type: "warning", title: "暂时无法生成", text: error && error.message ? error.message : "请稍后重试。" };
    }).then(function () {
      state.studyAbroadLoading = "";
      renderPage(pageByKey[state.route] || pageByKey["study-abroad"]);
    });
  }

  function runFurtherStudyService(endpoint, body) {
    if (window.CYANCRUISE_SERVICES && window.CYANCRUISE_SERVICES.furtherStudy
        && typeof window.CYANCRUISE_SERVICES.furtherStudy.run === "function") {
      return window.CYANCRUISE_SERVICES.furtherStudy.run({
        endpoint: endpoint,
        body: body,
        post: post
      });
    }
    return post(endpoint, body);
  }

  function renderOnboarding(item) {
    var onboarding = getValue(state.snapshot, "onboarding") || {};
    var targetRole = firstText(onboarding.targetRole, textFromSnapshot("preferences.targetRole"));
    renderShell(item,
      '<section class="panel full"><h3>个人情况</h3>' +
      '<form class="form-grid" id="onboardingForm">' +
      field("identityType", "身份类型", "select", firstText(onboarding.identityType, "student"), [["student", "在校学生"], ["graduate", "应届毕业生"], ["career_switcher", "转行求职"]]) +
      field("onboardingTargetRole", "目标岗位", "text", targetRole) +
      field("resumeStatus", "简历状态", "select", firstText(onboarding.resumeStatus, "none"), [["none", "还没有简历"], ["draft", "已有初稿"], ["ready", "已有可投递简历"]]) +
      field("preference", "偏好方向", "text", firstText(onboarding.preference, "")) +
      '<div class="full actions-row"><button type="submit">保存个人情况</button><button type="button" class="secondary" data-link="workbench">返回首页</button></div>' +
      '</form></section>');
    $("onboardingForm").addEventListener("submit", submitOnboarding);
  }

  function renderTodayPage(item) {
    if (hasUserIdentity() && shouldEnsureCurrentPlan(state.plan) && !state.planEnsuring && !isFilePreview()) {
      ensureCurrentRoutePlan();
    }
    var view = buildPlanViewModel();
    if (!view.dailyPlan.items.length) {
      renderShell(item,
        statePanel(state.plan ? "每日计划暂不可用" : "等待路径规划",
          firstText(view.dailyPlan.summary, "今日行动会从路径规划里的当前阶段按顺序生成。请稍后重试。"),
          state.plan ? "warning" : "pending")
      );
      return;
    }
    var body = '<section class="feature-section route-execution-grid daily-first">' +
      renderDailyPlanCard(view.dailyPlan, view.progressState) +
      renderWeeklyPlanCard(view.weeklyPlan, view.weeklyActions, view.weeklyDeliverables, view.targetRole, view.progressState) +
      '</section>' +
      '<section class="feature-section route-control-grid">' +
      metricsPanel("今日来源", [
        [isStudyRoute() ? "升学目标" : "目标岗位", view.targetRole],
        ["当前阶段", view.progressSummary.activePhaseTitle],
        ["规划周期", view.selectedYears + " 年"],
        ["完成进度", view.progressSummary.completedTasks + " / " + view.progressSummary.totalTasks + " 项"]
      ]) +
      '<section class="panel route-control-card"><div class="route-card-head"><div><span class="resource-type">继续规划</span><h3>路线图</h3></div></div>' +
      '<p class="route-goal">今日行动来自路径规划。调整当前阶段或刷新规划后，这里会跟着变化。</p>' +
      renderRouteControlProgress(view.progressSummary) +
      '<div class="actions-row"><button type="button" data-link="career-plan">查看路径规划</button></div></section>' +
      '</section>';
    renderFeatureShell(item, item.title, "根据路径规划拆解今天应该推进的小事项。", body);
  }

  function renderRouteControlProgress(progressSummary) {
    var summary = progressSummary || {};
    var total = Number(summary.totalTasks || 0);
    var completed = Number(summary.completedTasks || 0);
    var percent = total > 0 ? Math.round(completed * 100 / total) : 0;
    return '<div class="route-control-progress">' +
      '<div class="route-control-progress-head"><span>路线图进度</span><strong>' + percent + '%</strong></div>' +
      '<div class="route-progress-track"><span class="route-progress-fill" style="width:' + percent + '%"></span></div>' +
      '<div class="route-control-progress-meta"><span>已完成 ' + completed + ' / ' + total + ' 项</span>' +
      '<span>当前聚焦：' + escapeHtml(firstText(summary.activePhaseTitle, '按路线图继续推进')) + '</span></div>' +
      '</div>';
  }

  function renderResumePage(item) {
    var leftPanels = [
      resumeFormPanel()
    ].join("");
    var rightPanels = resumeListPanel();
    var body = '<div class="resume-layout"><div class="resume-main">' + leftPanels + '</div><aside class="resume-records">' + rightPanels + '</aside></div>';
    if (isDebugMode()) {
      body += metricsPanel("接口契约", [
        ["列表", endpoints.resumes],
        ["创建", endpoints.resumeCreate],
        ["删除", endpoints.resumeDelete],
        ["文件上传", endpoints.fileUpload],
        ["文件下载", endpoints.fileDownload]
      ]);
    }
    renderShell(item, body);
    bindResumeEvents();
  }

  function resumeFormPanel() {
    var draft = state.resumeDraft || {};
    var submitLabel = state.resumeSubmitting ? "创建中..." : "创建简历";
    var submitDisabled = state.resumeSubmitting ? " disabled" : "";
    var fileKey = firstText(draft.fileKey, "");
    var fileText = state.fileMessage
      ? state.fileMessage.text
      : (fileKey ? "PDF 已上传并关联到当前简历。" : "请选择 PDF，系统会读取正文并关联到当前简历。");
    var fileType = state.fileMessage ? state.fileMessage.type : "empty";
    var fileKeyControl = (isDebugMode()
      ? field("resumeFileKey", "文件 key", "text", fileKey)
      : '<input id="resumeFileKey" type="hidden" value="' + escapeAttr(fileKey) + '">') +
      '<div class="resume-upload-field full"><span class="label">上传简历</span>' +
      '<div class="resume-upload-row"><input id="resumeFileInput" type="file" accept=".pdf,application/pdf" aria-label="选择简历 PDF">' +
      '<button type="button" class="secondary" id="uploadResumeFileButton">上传 PDF</button></div>' +
      '<p class="form-note ' + escapeHtml(fileType) + '">' + escapeHtml(fileText) + '</p></div>';
    return '<section class="panel"><h3>创建简历</h3>' +
      '<form class="form-grid" id="resumeForm">' +
      field("resumeTitle", "简历标题", "text", firstText(draft.title, "")) +
      field("resumeTargetJob", "目标岗位", "text", firstText(draft.targetJob, "")) +
      fileKeyControl +
      '<label class="full">解析内容<textarea id="resumeParsedContent" placeholder="可粘贴简历摘要、技能、项目经历或留空。">' + escapeHtml(draft.parsedContent) + '</textarea></label>' +
      '<div class="full actions-row">' +
      '<button type="submit"' + submitDisabled + ">" + submitLabel + "</button>" +
      '</div></form></section>';
  }

  function resumeListPanel() {
    var resumes = normalizeArray(state.resumes);
    if (state.resumeListError) {
      return statePanel("简历记录", state.resumeListError, "warning");
    }
    if (!resumes.length) {
      return '<section class="state-card"><h3>简历记录</h3><p>暂无简历记录。可以先创建元数据，文件 key 和解析内容均可稍后补充。</p></section>';
    }
    return '<section class="panel"><h3>简历记录</h3><div class="item-list">' +
      resumes.map(resumeItem).join("") + "</div></section>";
  }

  function resumeItem(item, index) {
    var id = firstText(item.resumeId, item.id, "记录 " + (index + 1));
    var fileKey = firstText(item.fileKey, item.objectKey, "未关联文件");
    var target = firstText(item.targetJob, item.targetRole, "未设置目标岗位");
    var updated = firstText(item.updatedAt, item.createdAt, "时间待同步");
    var score = firstText(item.diagnosisScore, "未诊断");
    var hasFile = fileKey !== "未关联文件";
    return '<article class="item resume-item">' +
      '<div><strong>' + escapeHtml(firstText(item.title, item.resumeName, "简历 " + id)) + '</strong>' +
      '<p>目标岗位：' + escapeHtml(target) + '</p>' +
      '<p>文件：' + escapeHtml(fileKey === "未关联文件" ? "未关联" : "已关联 PDF") + '</p>' +
      (isDebugMode() ? '<p>文件 key：' + escapeHtml(fileKey) + '</p>' : "") +
      '<p>诊断分：' + escapeHtml(score) + ' ｜ 更新时间：' + escapeHtml(updated) + '</p></div>' +
      '<div class="actions-row compact">' +
      '<button type="button" class="secondary"' +
      (hasFile ? ' data-preview-file="' + escapeAttr(fileKey) + '"' : ' disabled title="请先上传并关联 PDF 文件"') +
      '>预览</button>' +
      '<button type="button" data-diagnose-resume="' + escapeAttr(id) + '">去诊断</button>' +
      '<button type="button" class="secondary danger" data-delete-resume="' + escapeHtml(id) + '">删除</button>' +
      '</div>' +
      "</article>";
  }

  function bindResumeEvents() {
    var form = $("resumeForm");
    if (form) {
      form.addEventListener("submit", submitResume);
    }
    var upload = $("uploadResumeFileButton");
    if (upload) {
      upload.addEventListener("click", uploadResumeFile);
    }
    var previews = els.pageHost.querySelectorAll("[data-preview-file]");
    for (var i = 0; i < previews.length; i += 1) {
      previews[i].addEventListener("click", function (event) {
        previewResumeFile(event.currentTarget.getAttribute("data-preview-file"));
      });
    }
    var deletes = els.pageHost.querySelectorAll("[data-delete-resume]");
    for (var j = 0; j < deletes.length; j += 1) {
      deletes[j].addEventListener("click", function (event) {
        deleteResumeRecord(event.currentTarget.getAttribute("data-delete-resume"));
      });
    }
  }

  function submitResume(event) {
    event.preventDefault();
    if (state.resumeSubmitting) {
      return;
    }
    if (!hasUserIdentity()) {
      state.resumeMessage = { type: "warning", text: "创建简历前需要 Cosmic 身份或显式开发身份。" };
      showMessage("warning", "无法创建简历", state.resumeMessage.text);
      renderPage(pageByKey[state.route]);
      return;
    }
    var request = readResumeDraft();
    state.resumeDraft = request;
    if (!request.title && !request.targetJob && !request.fileKey && !request.parsedContent) {
      state.resumeMessage = { type: "warning", text: "请至少填写标题、目标岗位、文件 key 或解析内容中的一项。" };
      showMessage("warning", "请补充简历信息", state.resumeMessage.text);
      renderPage(pageByKey[state.route]);
      return;
    }
    if (isFilePreview()) {
      state.resumes = [previewResumeRecord(request)];
      state.resumeMessage = { type: "info", text: "file:// 预览模式已生成本地简历记录，不调用后端。" };
      updateOverviewCards();
      renderPage(pageByKey[state.route]);
      return;
    }
    state.resumeSubmitting = true;
    state.resumeMessage = { type: "info", text: "正在创建简历。" };
    renderPage(pageByKey[state.route]);
    createResumeByService(request).then(function () {
      state.resumeMessage = { type: "info", text: "简历记录已创建，列表正在刷新。可继续去简历诊断。" };
      return refreshResumeList(false);
    }).then(function () {
      return refreshSnapshotAfterResume();
    }).then(function () {
      state.resumeSubmitting = false;
      showMessage("info", "简历已创建", "已刷新简历列表和工作台摘要。");
      renderPage(pageByKey[state.route]);
    }).catch(function (error) {
      state.resumeSubmitting = false;
      state.resumeMessage = { type: "warning", text: error.message || "简历创建或刷新失败，请检查 KAPI token 和后端状态。" };
      showMessage("error", "简历操作失败", state.resumeMessage.text);
      renderPage(pageByKey[state.route]);
    });
  }

  function refreshResumeList(showNotice) {
    if (!hasUserIdentity()) {
      state.resumeListError = "缺少 userId，已阻止简历列表调用。";
      return Promise.resolve();
    }
    if (isFilePreview()) {
      state.resumeListError = null;
      state.resumes = normalizeArray(state.resumes);
      return Promise.resolve();
    }
    return listResumes().then(function (resumes) {
      state.resumes = normalizeArray(resumes);
      state.resumeListError = null;
      updateOverviewCards();
      if (showNotice) {
        state.resumeMessage = { type: "info", text: "简历列表已刷新。" };
        renderPage(pageByKey[state.route]);
      }
    }).catch(function (error) {
      state.resumeListError = error.message || "简历列表暂不可用。";
      if (showNotice) {
        state.resumeMessage = { type: "warning", text: state.resumeListError };
        renderPage(pageByKey[state.route]);
      }
    });
  }

  function listResumes() {
    if (window.CYANCRUISE_SERVICES && window.CYANCRUISE_SERVICES.resume
        && typeof window.CYANCRUISE_SERVICES.resume.list === "function") {
      return window.CYANCRUISE_SERVICES.resume.list({
        endpoints: endpoints,
        post: post,
        userId: state.identity.userId
      });
    }
    return post(endpoints.resumes, state.identity.userId);
  }

  function createResumeByService(request) {
    if (window.CYANCRUISE_SERVICES && window.CYANCRUISE_SERVICES.resume
        && typeof window.CYANCRUISE_SERVICES.resume.create === "function") {
      return window.CYANCRUISE_SERVICES.resume.create({
        endpoints: endpoints,
        post: post,
        userId: state.identity.userId,
        request: request
      });
    }
    return post(endpoints.resumeCreate, { userId: state.identity.userId, request: request });
  }

  function refreshSnapshotAfterResume() {
    if (!hasUserIdentity() || isFilePreview()) {
      updateOverviewCards();
      return Promise.resolve();
    }
    return refreshResumeSnapshotByService().then(function (snapshot) {
      state.snapshot = snapshot;
      updateOverviewCards();
    }).catch(function (error) {
      state.resumeMessage = { type: "warning", text: "简历已创建，但画像摘要稍后刷新：" + (error.message || "snapshot unavailable") };
      updateOverviewCards();
    });
  }

  function uploadFileByService(file, base64) {
    if (window.CYANCRUISE_SERVICES && window.CYANCRUISE_SERVICES.file
        && typeof window.CYANCRUISE_SERVICES.file.upload === "function") {
      return window.CYANCRUISE_SERVICES.file.upload({
        endpoints: endpoints,
        post: post,
        folder: "resumes",
        originalFilename: file.name,
        base64: base64
      });
    }
    return post(endpoints.fileUpload, {
      request: {
        folder: "resumes",
        originalFilename: file.name,
        base64: base64
      }
    });
  }

  function extractTextByService(fileKey) {
    if (window.CYANCRUISE_SERVICES && window.CYANCRUISE_SERVICES.file
        && typeof window.CYANCRUISE_SERVICES.file.extractText === "function") {
      return window.CYANCRUISE_SERVICES.file.extractText({
        endpoints: endpoints,
        post: post,
        fileUrlOrKey: fileKey
      });
    }
    return post(endpoints.fileExtractText, { fileUrlOrKey: fileKey });
  }

  function downloadFileByService(fileKey) {
    if (window.CYANCRUISE_SERVICES && window.CYANCRUISE_SERVICES.file
        && typeof window.CYANCRUISE_SERVICES.file.download === "function") {
      return window.CYANCRUISE_SERVICES.file.download({
        endpoints: endpoints,
        post: post,
        fileUrlOrKey: fileKey
      });
    }
    return post(endpoints.fileDownload, { fileUrlOrKey: fileKey });
  }

  function uploadResumeFile() {
    var input = $("resumeFileInput");
    var file = input && input.files && input.files[0];
    if (!file) {
      state.resumeDraft = readResumeDraft();
      state.fileMessage = { type: "warning", text: "请选择一个小文件，或直接手工填写 fileKey。" };
      renderPage(pageByKey[state.route]);
      return;
    }
    if (file.size > 1024 * 1024) {
      state.resumeDraft = readResumeDraft();
      state.fileMessage = { type: "warning", text: "当前 MVP 仅上传 1MB 以内小文件；大文件请先手工填写 fileKey。" };
      renderPage(pageByKey[state.route]);
      return;
    }
    if (isFilePreview()) {
      state.resumeDraft = readResumeDraft();
      state.fileMessage = { type: "info", text: "file:// 预览模式不上传文件，可手工填写 fileKey。" };
      renderPage(pageByKey[state.route]);
      return;
    }
    state.resumeDraft = readResumeDraft();
    readFileAsBase64(file).then(function (base64) {
      state.fileMessage = { type: "info", text: "正在上传文件。" };
      renderPage(pageByKey[state.route]);
      return uploadFileByService(file, base64);
    }).then(function (result) {
      var uploadResult = result || {};
      var fileDto = uploadResult.file || {};
      var objectKey = firstText(fileDto.objectKey, uploadResult.objectKey, fileDto.fileKey, uploadResult.fileKey);
      if (!objectKey || (uploadResult.status && uploadResult.status !== "OK")) {
        state.fileMessage = { type: "warning", text: firstText(uploadResult.message, uploadResult.status, "文件上传不可用，可继续手工填写 fileKey。") };
        renderPage(pageByKey[state.route]);
        return;
      }
      state.resumeDraft = state.resumeDraft || {};
      state.resumeDraft.fileKey = objectKey;
      state.fileMessage = { type: "info", text: "文件已上传，正在检查是否可以读取简历正文。" };
      renderPage(pageByKey[state.route]);
      return extractTextByService(objectKey).then(function (extraction) {
        var extractedText = firstText(extraction && extraction.text, "");
        if (extraction && extraction.status === "OK" && extractedText) {
          state.resumeDraft.parsedContent = extractedText;
          state.fileMessage = { type: "info", text: "文件已上传并提取文本，创建简历后即可直接诊断。" };
        } else if (extraction && extraction.status === "TEXT_EMPTY") {
          state.fileMessage = { type: "warning", text: "PDF 已上传，但其中没有可读取的文字，可能是扫描版或纯图片。请在解析内容中粘贴简历正文后再创建。" };
        } else {
          state.fileMessage = { type: "warning", text: "PDF 已上传，但正文读取失败。请重试，或在解析内容中粘贴简历正文后再创建。" };
        }
        renderPage(pageByKey[state.route]);
      }, function (error) {
        state.fileMessage = { type: "warning", text: "文件已上传，但文本提取失败：" + (error.message || "解析服务不可用") + "。可手工粘贴简历正文。" };
        renderPage(pageByKey[state.route]);
      });
    }).catch(function (error) {
      state.fileMessage = { type: "warning", text: (error.message || "文件上传失败") + "；可继续手工填写 fileKey 或只创建元数据。" };
      renderPage(pageByKey[state.route]);
    });
  }

  function previewResumeFile(fileKey) {
    if (!fileKey) {
      return;
    }
    var previewWindow = null;
    if (isFilePreview()) {
      state.previewUrls[fileKey] = fileKey;
      window.open(fileKey, "_blank");
      renderPage(pageByKey[state.route]);
      return;
    }
    previewWindow = window.open("about:blank", "_blank");
    if (previewWindow) {
      previewWindow.document.title = "PDF 预览加载中";
      previewWindow.document.body.innerHTML = '<p style="font:16px sans-serif;padding:24px;">PDF 预览加载中...</p>';
    }
    state.fileMessage = { type: "info", text: "正在读取文件并生成浏览器预览。" };
    renderPage(pageByKey[state.route]);
    downloadFileByService(fileKey).then(function (result) {
      var bytes = bytesFromDownloadResult(result && result.bytes);
      if (!bytes || (result.status && result.status !== "OK")) {
        state.fileMessage = { type: "warning", text: isDebugMode() ? firstText(result && result.message, result && result.status, "文件下载暂不可用，无法生成预览。") : "文件预览暂不可用，请稍后重试或重新上传 PDF。" };
        closePreviewWindow(previewWindow);
        renderPage(pageByKey[state.route]);
        return;
      }
      var previous = state.previewUrls[fileKey];
      if (previous && previous.indexOf("blob:") === 0 && window.URL && URL.revokeObjectURL) {
        URL.revokeObjectURL(previous);
      }
      var blob = new Blob([bytes], { type: mimeTypeFromKey(fileKey) });
      var url = URL.createObjectURL(blob);
      state.previewUrls[fileKey] = url;
      state.fileMessage = { type: "info", text: "PDF 预览已打开。" };
      openPreviewUrl(previewWindow, url);
      renderPage(pageByKey[state.route]);
    }).catch(function (error) {
      state.fileMessage = { type: "warning", text: isDebugMode() ? (error.message || "文件预览暂不可用。") : "文件预览暂不可用，请稍后重试或重新上传 PDF。" };
      closePreviewWindow(previewWindow);
      renderPage(pageByKey[state.route]);
    });
  }

  function renderResumeDiagnosisPage(item) {
    if (state.resumes === null && !state.resumeListError && !state.diagnosisResumeListLoading && !isFilePreview()) {
      state.diagnosisResumeListLoading = true;
      refreshResumeList(false).then(function () {
        state.diagnosisResumeListLoading = false;
        if (state.route === "resume-diagnosis") {
          if (state.diagnosisMessage && state.diagnosisMessage.text === "正在重新加载简历。") {
            state.diagnosisMessage = state.resumeListError
              ? { type: "warning", text: "简历加载失败：" + state.resumeListError }
              : { type: "info", text: "简历已加载。" };
          }
          renderPage(pageByKey[state.route]);
        }
      });
    }
    var resumes = normalizeArray(state.resumes);
    var selected = selectedDiagnosisResume(resumes);
    var draft = diagnosisDraft(selected);
    var body = "";
    if (state.diagnosisMessage) {
      body += statePanel("简历诊断状态", state.diagnosisMessage.text, state.diagnosisMessage.type);
    }
    body += '<section class="feature-section resume-revision-grid">' +
      diagnosisFormPanel(resumes, selected, draft) +
      diagnosisResultPanel() + diagnosisHistoryPanel(selected) +
      '</section>';
    if (isDebugMode()) {
      body += metricsPanel("接口契约", [
        ["简历列表", endpoints.resumes],
        ["简历诊断", endpoints.resumeDiagnosis],
        ["关键词状态", endpoints.keywordStatus],
        ["画像快照", endpoints.snapshot],
        ["文件预览", endpoints.filePreview]
      ]);
    }
    renderFeatureShell(item, "简历诊断", "基于已有简历、目标岗位和画像上下文生成诊断与优化建议。", body);
    bindResumeDiagnosisEvents();
    ensureDiagnosisHistory(selected);
  }

  function selectedDiagnosisResume(resumes) {
    var pendingId = getUserStorageItem("cyancruise.pendingDiagnosisResumeId");
    if (pendingId) {
      state.selectedDiagnosisResumeId = pendingId;
      removeUserStorageItem("cyancruise.pendingDiagnosisResumeId");
    }
    var selectedId = firstText(state.selectedDiagnosisResumeId, getValue(state.diagnosisDraft, "resumeId"));
    if (!selectedId && resumes.length) {
      selectedId = firstText(resumes[0].resumeId, resumes[0].id);
      state.selectedDiagnosisResumeId = selectedId;
    }
    for (var i = 0; i < resumes.length; i += 1) {
      if (String(firstText(resumes[i].resumeId, resumes[i].id, "")) === String(selectedId)) {
        return resumes[i];
      }
    }
    return resumes.length ? resumes[0] : null;
  }

  function diagnosisDraft(selected) {
    var draft = state.diagnosisDraft || {};
    return {
      resumeId: firstText(draft.resumeId, selected && firstText(selected.resumeId, selected.id), ""),
      targetJob: firstText(selected && selected.targetJob, ""),
      jobDescription: firstText(draft.jobDescription, ""),
      resumeText: firstText(draft.resumeText, "")
    };
  }

  function diagnosisFormPanel(resumes, selected, draft) {
    if (state.resumes === null && !state.resumeListError) {
      return statePanel("正在加载简历", "正在读取你已保存的简历，请稍候。", "pending");
    }
    if (state.resumeListError) {
      return '<section class="state-card warning"><h3>简历加载失败</h3><p>' +
        escapeHtml(state.resumeListError) +
        '</p><div class="actions-row"><button type="button" id="retryDiagnosisResumesButton">重新加载</button></div></section>';
    }
    if (!resumes.length) {
      return '<section class="panel"><h3>选择简历</h3><p>还没有真实简历记录。请先创建或上传简历，再回来做诊断和修改。</p>' +
        '<div class="actions-row"><button type="button" data-link="resume">去创建简历</button></div></section>';
    }
    var resumeOptions = resumes.map(function (resume) {
      var id = firstText(resume.resumeId, resume.id, "");
      var label = firstText(resume.title, resume.resumeName, "简历 " + id);
      return { id: id, label: label, selected: String(id) === String(draft.resumeId) };
    });
    var options = resumeOptions.map(function (option) {
      return '<option value="' + escapeAttr(option.id) + '"' + (option.selected ? " selected" : "") + '>' + escapeHtml(option.label) + '</option>';
    }).join("");
    var selectedResumeLabel = firstText(resumeOptions.filter(function (option) { return option.selected; }).map(function (option) {
      return option.label;
    })[0], resumeOptions[0] && resumeOptions[0].label, "请选择简历");
    var resumePickerOptions = resumeOptions.map(function (option) {
      return '<button type="button" class="diagnosis-select-option' + (option.selected ? ' active' : '') +
        '" data-diagnosis-resume-id="' + escapeAttr(option.id) + '" role="option" aria-selected="' +
        (option.selected ? 'true' : 'false') + '">' + escapeHtml(option.label) + '</button>';
    }).join("");
    var selectedFileKey = firstText(selected && selected.fileKey, selected && selected.objectKey, "");
    var selectedContent = firstText(selected && selected.parsedContent, "");
    var targetMissing = !firstText(draft.targetJob, "");
    var needsPdfExtraction = !!selectedFileKey && !selectedContent && !firstText(draft.resumeText, "");
    var submitText = state.diagnosisLoading
      ? (needsPdfExtraction ? "正在读取 PDF..." : "诊断中...")
      : (needsPdfExtraction ? "读取 PDF 并生成诊断" : "生成诊断建议");
    var contentHint = selectedContent
      ? "已从简历记录读取到可诊断正文。"
      : (selectedFileKey
        ? "当前记录已关联 PDF。点击下方按钮后，系统会先读取 PDF 正文，再生成诊断建议。"
        : "当前记录只有标题和岗位，没有关联文件或简历正文。请先补充简历文本或重新上传 PDF。");
    return '<section class="panel"><h3>诊断输入</h3>' +
      '<form class="form-grid" id="resumeDiagnosisForm">' +
      '<div class="diagnosis-context-grid full">' +
      '<div class="diagnosis-context-field diagnosis-resume-picker"><span>选择简历</span>' +
      '<div class="diagnosis-select" id="diagnosisResumePicker"><select id="diagnosisResumeId" class="diagnosis-native-select" aria-hidden="true" tabindex="-1">' + options + '</select>' +
      '<button type="button" class="diagnosis-select-trigger" id="diagnosisResumeTrigger" aria-haspopup="listbox" aria-expanded="false" aria-controls="diagnosisResumeMenu">' +
      '<span>' + escapeHtml(selectedResumeLabel) + '</span><span class="diagnosis-select-chevron" aria-hidden="true"></span></button>' +
      '<div class="diagnosis-select-menu" id="diagnosisResumeMenu" role="listbox" aria-label="选择简历">' + resumePickerOptions + '</div></div>' +
      '<small>切换后会读取对应的简历正文和目标岗位。</small></div>' +
      '<label class="diagnosis-context-field"><span>目标岗位</span>' +
      '<input id="diagnosisTargetJob" value="' + escapeAttr(draft.targetJob) + '" placeholder="尚未设置" readonly aria-readonly="true">' +
      '<small>' + escapeHtml(targetMissing ? "请先回到简历页补充这份简历的目标岗位。" : "来自所选简历，无需重复填写。") + '</small></label>' +
      '</div>' +
      '<p class="form-note full">' + escapeHtml(contentHint) + '</p>' +
      '<label class="full">目标岗位要求<textarea id="diagnosisJobDescription" placeholder="粘贴目标岗位的招聘要求或岗位说明，未填写时会使用目标岗位和画像作为上下文。">' + escapeHtml(draft.jobDescription) + '</textarea></label>' +
      '<label class="full">简历正文<textarea id="diagnosisResumeText" placeholder="文本型 PDF 会自动读取；扫描版或纯图片 PDF 可在这里粘贴简历文字。">' + escapeHtml(draft.resumeText) + '</textarea></label>' +
      '<div class="full actions-row">' +
      '<button type="submit"' + (state.diagnosisLoading || targetMissing ? ' disabled aria-disabled="true"' : "") + '>' +
      (targetMissing ? "请先补充目标岗位" : submitText) + '</button>' +
      (targetMissing ? '<button type="button" class="secondary" data-link="resume">返回简历页</button>' : "") +
      (selectedFileKey ? '<button type="button" class="secondary" data-preview-file="' + escapeAttr(selectedFileKey) + '">预览文件</button>' : "") +
      '</div>' +
      '</form></section>';
  }

  function diagnosisHistoryPanel(selected) {
    var resumeId = firstText(selected && selected.resumeId, selected && selected.id, "");
    if (!resumeId) return "";
    if (state.diagnosisHistoryLoading && !Object.prototype.hasOwnProperty.call(state.diagnosisHistoryByResume, String(resumeId))) {
      return statePanel("诊断记录", "正在读取这份简历的历史诊断记录。", "pending");
    }
    var records = normalizeArray(state.diagnosisHistoryByResume[String(resumeId)]).filter(function (record) {
      return record && record.fallbackStatus === "AGENT_AI";
    });
    if (!records.length) {
      return '<section class="panel diagnosis-history-panel"><h3>诊断记录</h3><p class="panel-note">暂时没有已保存的诊断记录。生成诊断后，结果会保存在这里。</p></section>';
    }
    return '<section class="panel diagnosis-history-panel"><h3>诊断记录</h3><p class="panel-note">同一份简历的历史结果仅对你可见，可随时查看或删除。</p><div class="item-list">' +
      records.map(function (record) {
        var score = firstText(record.overallScore, "--");
        var time = formatDiagnosisTime(record.diagnosedAt);
        return '<article class="item diagnosis-history-item"><div><strong>' + escapeHtml(time) + '</strong><p>' +
          escapeHtml(firstText(record.targetJob, "未填写目标岗位")) + ' · ' + escapeHtml(String(score)) + ' / 100 分</p></div>' +
          '<div class="actions-row"><button type="button" class="secondary" data-view-diagnosis="' + escapeAttr(record.diagnosisId) + '">查看</button>' +
          '<button type="button" class="danger" data-delete-diagnosis="' + escapeAttr(record.diagnosisId) + '">删除</button></div></article>';
      }).join("") + '</div></section>';
  }

  function ensureDiagnosisHistory(selected) {
    var resumeId = firstText(selected && selected.resumeId, selected && selected.id, "");
    if (!resumeId || isFilePreview() || state.diagnosisHistoryLoading
        || Object.prototype.hasOwnProperty.call(state.diagnosisHistoryByResume, String(resumeId))) {
      return;
    }
    state.diagnosisHistoryLoading = true;
    listDiagnosisHistoryByService(resumeId).then(function (records) {
      state.diagnosisHistoryByResume[String(resumeId)] = normalizeArray(records);
    }).catch(function () {
      state.diagnosisHistoryByResume[String(resumeId)] = [];
    }).then(function () {
      state.diagnosisHistoryLoading = false;
      if (state.route === "resume-diagnosis") renderPage(pageByKey[state.route]);
    });
  }

  function formatDiagnosisTime(value) {
    if (!value) return "历史诊断";
    var text = String(value).replace("T", " ");
    return text.length > 16 ? text.substring(0, 16) : text;
  }

  function diagnosisResultPanel() {
    var result = state.diagnosisResult;
    if (state.diagnosisLoading) {
      return statePanel("正在诊断", "正在结合简历、目标岗位和画像上下文生成诊断建议。", "pending");
    }
    if (!result) {
      return statePanel("等待诊断", "选择已有简历并填写目标岗位或岗位要求后，生成可执行的优化建议。", "empty");
    }
    var plan = result.revisionPlan || {};
    var suggestions = normalizeArray(result.revisionSuggestions);
    var diagnosisNote = "本次由 AI 简历诊断智能体生成，已结合简历、目标岗位、岗位要求和自画像分析。";
    var body = '<section class="panel"><h3>诊断结果</h3>' +
      '<p class="panel-note">' + escapeHtml(diagnosisNote) + '</p>' +
      metricsPanel("诊断概览", [
        ["诊断总分", firstText(result.overallScore, "--") + " / 100"],
        ["建议数", firstText(plan.totalSuggestions, suggestions.length, 0)],
        ["优先处理", firstText(plan.highPrioritySuggestions, 0)],
        ["参考信息", diagnosisContextLabel(result.contextSources)]
      ]) +
      scoreBreakdownPanel(result.scoreBreakdown, result.overallScore) +
      diagnosisTextLists(result) +
      revisionSuggestionList(suggestions) +
      '<div class="actions-row"><button type="button" id="rerunDiagnosisButton">再次诊断</button></div>' +
      '</section>';
    return body;
  }

  function diagnosisTextLists(result) {
    return '<div class="revision-columns">' +
      '<div>' + renderDiagnosisInsightList("做得较好", userFacingList(result.strengths), "positive") + '</div>' +
      '<div>' + renderDiagnosisInsightList("需要改进", userFacingList(result.weaknesses), "warning") + '</div>' +
      '<div>' + renderDiagnosisInsightList("修改方向", userFacingList(result.suggestions), "action") + '</div>' +
      '</div>';
  }

  function renderDiagnosisInsightList(title, items, type) {
    var list = normalizeArray(items).filter(Boolean);
    if (!list.length) {
      return "";
    }
    return '<section class="diagnosis-insight-group ' + escapeAttr(type) + '"><h4>' + escapeHtml(title) +
      '</h4><ul class="diagnosis-insight-list">' + list.map(function (item) {
        return '<li><span class="diagnosis-insight-dot" aria-hidden="true"></span><span>' +
          escapeHtml(item) + '</span></li>';
      }).join("") + '</ul></section>';
  }

  function scoreBreakdownPanel(items, overallScore) {
    var scores = normalizeArray(items);
    if (!scores.length) {
      return '<section class="diagnosis-score-section"><h4>评分标准</h4><p class="panel-note">这条历史诊断只有总分 ' +
        escapeHtml(firstText(overallScore, "--")) + '，暂无分项评分依据。再次诊断后可查看详细得分。</p></section>';
    }
    return '<section class="diagnosis-score-section"><h4>评分标准与本次得分</h4><div class="diagnosis-score-list">' +
      scores.map(function (item) {
        var score = Number(item.score || 0);
        var maxScore = Number(item.maxScore || 0);
        var percent = maxScore > 0 ? Math.max(0, Math.min(100, Math.round(score * 100 / maxScore))) : 0;
        return '<div class="diagnosis-score-item"><div class="diagnosis-score-head"><strong>' +
          escapeHtml(userFacingText(item.name)) + '</strong><span>' + score + ' / ' + maxScore + ' 分</span></div>' +
          '<div class="diagnosis-score-track"><span style="width:' + percent + '%"></span></div><div class="diagnosis-score-reason">' +
          renderDiagnosisNumberedText(item.reason, "暂无评分说明") + '</div></div>';
      }).join("") + '</div></section>';
  }

  function renderDiagnosisNumberedText(value, fallback) {
    var text = userFacingText(value || fallback || "");
    var parts = text.replace(/([。；])(?=\S)/g, "$1\n").split(/\n+/).map(function (item) {
      return String(item || "").trim().replace(/^\d+[.、]\s*/, "");
    }).filter(Boolean);
    if (parts.length <= 1) return escapeHtml(text);
    return '<ol class="diagnosis-numbered-list">' + parts.map(function (item) {
      return '<li>' + escapeHtml(item) + '</li>';
    }).join("") + '</ol>';
  }

  function revisionSuggestionList(suggestions) {
    if (!suggestions.length) {
      return statePanel("诊断建议", "当前诊断没有返回结构化建议，可查看普通建议后再次诊断。", "empty");
    }
    return '<div class="item-list revision-list">' + suggestions.map(function (item) {
      return '<article class="item revision-item">' +
        '<div><strong>' + escapeHtml(priorityLabel(item.priority)) + " · " + escapeHtml(resumeSectionLabel(item.resumeSection)) + '</strong>' +
        '<p class="revision-problem">问题：' + renderDiagnosisText(item.problem || item.action, false, "需要补充简历证据") + '</p>' +
        '<p>怎么改：' + renderDiagnosisText(item.action, false, "围绕目标岗位补充经历证据") + '</p>' +
        '<p>参考写法：' + renderDiagnosisText(item.rewriteExample, false, "用具体行动、方法、结果和个人贡献重写经历。") + '</p>' +
        '<p>建议补充：' + escapeHtml(userFacingList(item.targetKeywords).join(" / ") || "与目标岗位相关的真实证据") + '</p></div></article>';
    }).join("") + '</div>';
  }

  function renderDiagnosisText(value, splitSentences, fallback) {
    var text = userFacingText(value || fallback || "");
    if (splitSentences) {
      text = text.replace(/([。；])(?=\S)/g, "$1\n");
    }
    return escapeHtml(text).replace(/\n/g, "<br>");
  }

  function diagnosisContextLabel(sources) {
    var labels = normalizeArray(sources).map(function (source) {
      var value = String(source || "");
      if (value.indexOf("resume:") === 0) return "当前简历";
      if (value === "resume.targetJob") return "简历目标岗位";
      if (value === "resume.fileText") return "PDF 简历正文";
      if (value === "profile.assessment") return "画像补全";
      if (value === "profile.snapshot") return "自画像";
      if (value === "profile.preferences.targetRole") return "画像目标岗位";
      if (value === "profile.resume.targetJob") return "画像中的简历岗位";
      if (value === "request.jobDescription") return "岗位要求";
      if (value === "targetJob") return "目标岗位";
      return "";
    }).filter(Boolean);
    return uniqueValues(labels).join("、") || "简历正文";
  }

  function priorityLabel(value) {
    var key = String(value || "").toUpperCase();
    return key === "HIGH" ? "优先处理" : key === "LOW" ? "可以完善" : "建议处理";
  }

  function resumeSectionLabel(value) {
    var labels = { projects: "项目经历", experience: "工作或实践经历", skills: "技能说明", summary: "整体内容", education: "教育背景" };
    return labels[String(value || "").toLowerCase()] || "简历内容";
  }

  function userFacingList(values) {
    return normalizeArray(values).map(userFacingText).filter(Boolean);
  }

  function userFacingText(value) {
    return String(value || "")
      // Some model responses double-escape line breaks inside JSON strings.
      .replace(/\\\\r\\\\n|\\\\n|\\\\r/g, "\n")
      .replace(/\r\n?/g, "\n")
      // Do not present placeholder brackets as resume facts. Make the required
      // user-provided evidence explicit instead.
      .replace(/\[([^\]\n]{1,80})\]/g, "（请补充：$1）")
      .replace(/[□]/g, "")
      .replace(/[ \t]+\n/g, "\n")
      .replace(/\n{3,}/g, "\n\n")
      .replace(/\bJD\b/gi, "岗位要求")
      .replace(/jobDescription/g, "岗位要求")
      .replace(/profileContext/g, "自画像")
      .replace(/resumeText/g, "简历正文")
      .replace(/resume\.targetJob/g, "简历目标岗位")
      .replace(/profile\.assessment/g, "画像补全")
      .replace(/targetJob/g, "目标岗位")
      .replace(/\bTODO\b/g, "待处理");
  }

  function uniqueValues(values) {
    var seen = {};
    return values.filter(function (value) {
      if (seen[value]) return false;
      seen[value] = true;
      return true;
    });
  }

  function bindResumeDiagnosisEvents() {
    var form = $("resumeDiagnosisForm");
    if (form) {
      form.addEventListener("submit", submitResumeDiagnosis);
    }
    var select = $("diagnosisResumeId");
    var picker = $("diagnosisResumePicker");
    var trigger = $("diagnosisResumeTrigger");
    function closeResumePicker() {
      if (!picker || !trigger) return;
      picker.classList.remove("open");
      trigger.setAttribute("aria-expanded", "false");
    }
    function chooseDiagnosisResume(resumeId) {
      if (!select || !resumeId || String(select.value) === String(resumeId)) {
        closeResumePicker();
        return;
      }
      select.value = resumeId;
      state.selectedDiagnosisResumeId = select.value;
      state.diagnosisDraft = { resumeId: select.value, targetJob: "", jobDescription: "", resumeText: "" };
      state.diagnosisResult = null;
      state.diagnosisMessage = null;
      renderPage(pageByKey[state.route]);
    }
    if (trigger && picker) {
      trigger.addEventListener("click", function () {
        var isOpen = picker.classList.toggle("open");
        trigger.setAttribute("aria-expanded", isOpen ? "true" : "false");
      });
      trigger.addEventListener("keydown", function (event) {
        if (event.key === "Escape") {
          event.preventDefault();
          closeResumePicker();
        }
        if (event.key === "ArrowDown" && !picker.classList.contains("open")) {
          event.preventDefault();
          picker.classList.add("open");
          trigger.setAttribute("aria-expanded", "true");
        }
      });
    }
    var resumeOptions = els.pageHost.querySelectorAll("[data-diagnosis-resume-id]");
    for (var optionIndex = 0; optionIndex < resumeOptions.length; optionIndex += 1) {
      resumeOptions[optionIndex].addEventListener("click", function (event) {
        chooseDiagnosisResume(event.currentTarget.getAttribute("data-diagnosis-resume-id"));
      });
    }
    var retry = $("retryDiagnosisResumesButton");
    if (retry) {
      retry.addEventListener("click", function () {
        retryDiagnosisResumeList();
      });
    }
    var rerun = $("rerunDiagnosisButton");
    if (rerun) {
      rerun.addEventListener("click", function () {
        submitResumeDiagnosis();
      });
    }
    var historyViews = els.pageHost.querySelectorAll("[data-view-diagnosis]");
    for (var viewIndex = 0; viewIndex < historyViews.length; viewIndex += 1) {
      historyViews[viewIndex].addEventListener("click", function (event) {
        viewDiagnosisHistory(event.currentTarget.getAttribute("data-view-diagnosis"));
      });
    }
    var historyDeletes = els.pageHost.querySelectorAll("[data-delete-diagnosis]");
    for (var deleteIndex = 0; deleteIndex < historyDeletes.length; deleteIndex += 1) {
      historyDeletes[deleteIndex].addEventListener("click", function (event) {
        deleteDiagnosisHistory(event.currentTarget.getAttribute("data-delete-diagnosis"));
      });
    }
    var previews = els.pageHost.querySelectorAll("[data-preview-file]");
    for (var i = 0; i < previews.length; i += 1) {
      previews[i].addEventListener("click", function (event) {
        previewResumeFile(event.currentTarget.getAttribute("data-preview-file"));
      });
    }
  }

  function submitResumeDiagnosis(event) {
    if (event && event.preventDefault) {
      event.preventDefault();
    }
    if (state.diagnosisLoading) {
      return;
    }
    if (!hasUserIdentity()) {
      state.diagnosisMessage = { type: "warning", text: "诊断前需要 Cosmic 身份或显式开发身份。" };
      renderPage(pageByKey[state.route]);
      return;
    }
    var draft = readDiagnosisDraft();
    state.diagnosisDraft = draft;
    state.selectedDiagnosisResumeId = draft.resumeId;
    if (!draft.targetJob) {
      state.diagnosisMessage = { type: "warning", text: "所选简历尚未设置目标岗位，请先回到简历页补充。" };
      renderPage(pageByKey[state.route]);
      return;
    }
    if (!draft.resumeId && !draft.resumeText) {
      state.diagnosisMessage = { type: "warning", text: "请选择已有简历，或粘贴简历文本。" };
      renderPage(pageByKey[state.route]);
      return;
    }
    var selected = selectedDiagnosisResume(normalizeArray(state.resumes));
    if (!firstText(draft.resumeText, selected && selected.parsedContent, "")) {
      var selectedFileKey = firstText(selected && selected.fileKey, selected && selected.objectKey, "");
      if (!selectedFileKey) {
        state.diagnosisMessage = {
          type: "warning",
          text: "所选简历只有标题和岗位，数据库中没有文件或简历正文。请在下方粘贴简历文本，或回到简历页上传 PDF 后重新创建记录。"
        };
        renderPage(pageByKey[state.route]);
        return;
      }
    }
    if (isFilePreview()) {
      state.diagnosisResult = null;
      state.diagnosisMessage = { type: "warning", text: "本地预览模式不支持 AI 简历诊断，请在已部署的应用中重试。" };
      renderPage(pageByKey[state.route]);
      return;
    }
    state.diagnosisResult = null;
    state.diagnosisLoading = true;
    state.diagnosisMessage = {
      type: "info",
      text: firstText(draft.resumeText, selected && selected.parsedContent, "")
        ? "正在生成简历诊断建议。"
        : "正在读取 PDF 正文，读取成功后会继续生成诊断建议。"
    };
    renderPage(pageByKey[state.route]);
    diagnoseResumeByService(diagnosisRequest(draft)).then(function (result) {
      state.diagnosisResult = result || {};
      syncDiagnosisScoreToResumeList(draft.resumeId, state.diagnosisResult.overallScore);
      if (draft.resumeId) {
        delete state.diagnosisHistoryByResume[String(draft.resumeId)];
      }
      state.diagnosisMessage = { type: "info", text: "诊断完成。可按优先级处理建议后再次诊断。" };
      return Promise.all([refreshSnapshotAfterResume(), refreshResumeList(false)]);
    }).then(function () {
      state.diagnosisLoading = false;
      if (state.resumeListError) {
        state.diagnosisMessage = { type: "warning", text: "诊断已完成，简历记录将在列表恢复后自动更新。" };
      }
      renderPage(pageByKey[state.route]);
    }).catch(function (error) {
      state.diagnosisLoading = false;
      state.diagnosisMessage = { type: "warning", text: error.message || "简历诊断暂不可用，请稍后重试。" };
      showMessage("error", "简历诊断失败", state.diagnosisMessage.text);
      renderPage(pageByKey[state.route]);
    });
  }

  function syncDiagnosisScoreToResumeList(resumeId, score) {
    if (!resumeId || score === undefined || score === null) {
      return;
    }
    normalizeArray(state.resumes).forEach(function (resume) {
      if (String(firstText(resume && resume.resumeId, resume && resume.id, "")) === String(resumeId)) {
        resume.diagnosisScore = score;
      }
    });
  }

  function readDiagnosisDraft() {
    return {
      resumeId: valueOf("diagnosisResumeId"),
      targetJob: valueOf("diagnosisTargetJob"),
      jobDescription: valueOf("diagnosisJobDescription"),
      resumeText: valueOf("diagnosisResumeText")
    };
  }

  function diagnosisRequest(draft) {
    return {
      resumeId: draft.resumeId ? Number(draft.resumeId) : null,
      targetJob: draft.targetJob,
      jobDescription: draft.jobDescription,
      resumeText: draft.resumeText,
      profileContext: profileContextText()
    };
  }

  function diagnoseResumeByService(request) {
    if (window.CYANCRUISE_SERVICES && window.CYANCRUISE_SERVICES.resume
        && typeof window.CYANCRUISE_SERVICES.resume.diagnose === "function") {
      return window.CYANCRUISE_SERVICES.resume.diagnose({
        endpoints: endpoints,
        post: post,
        userId: state.identity.userId,
        request: request
      });
    }
    return post(endpoints.resumeDiagnosis, { userId: state.identity.userId, request: request });
  }

  function listDiagnosisHistoryByService(resumeId) {
    var context = { endpoints: endpoints, post: post, userId: state.identity.userId, resumeId: Number(resumeId) };
    if (window.CYANCRUISE_SERVICES && window.CYANCRUISE_SERVICES.resume
        && typeof window.CYANCRUISE_SERVICES.resume.listDiagnosisHistory === "function") {
      return window.CYANCRUISE_SERVICES.resume.listDiagnosisHistory(context);
    }
    return post(endpoints.resumeDiagnosisHistory, { userId: state.identity.userId, resumeId: Number(resumeId) });
  }

  function viewDiagnosisHistory(diagnosisId) {
    var resumeId = firstText(state.selectedDiagnosisResumeId, getValue(state.diagnosisDraft, "resumeId"));
    var records = normalizeArray(state.diagnosisHistoryByResume[String(resumeId)]);
    for (var i = 0; i < records.length; i += 1) {
      if (String(records[i].diagnosisId) === String(diagnosisId)) {
        state.diagnosisResult = records[i];
        state.diagnosisMessage = { type: "info", text: "已查看历史诊断记录。" };
        renderPage(pageByKey[state.route]);
        return;
      }
    }
  }

  function deleteDiagnosisHistory(diagnosisId) {
    var resumeId = firstText(state.selectedDiagnosisResumeId, getValue(state.diagnosisDraft, "resumeId"));
    if (!resumeId || !diagnosisId) return;
    showConfirmDialog("删除诊断记录", "删除后无法恢复，确认删除这条诊断记录吗？", "删除", function () {
      performDeleteDiagnosisHistory(resumeId, diagnosisId);
    });
  }

  function performDeleteDiagnosisHistory(resumeId, diagnosisId) {
    var request = { userId: state.identity.userId, resumeId: Number(resumeId), diagnosisId: Number(diagnosisId) };
    var remove = window.CYANCRUISE_SERVICES && window.CYANCRUISE_SERVICES.resume
      && typeof window.CYANCRUISE_SERVICES.resume.deleteDiagnosisHistory === "function"
      ? window.CYANCRUISE_SERVICES.resume.deleteDiagnosisHistory({ endpoints: endpoints, post: post, userId: request.userId, resumeId: request.resumeId, diagnosisId: request.diagnosisId })
      : post(endpoints.resumeDiagnosisHistoryDelete, request);
    remove.then(function () {
      var records = normalizeArray(state.diagnosisHistoryByResume[String(resumeId)]).filter(function (item) {
        return String(item.diagnosisId) !== String(diagnosisId);
      });
      state.diagnosisHistoryByResume[String(resumeId)] = records;
      if (state.diagnosisResult && String(state.diagnosisResult.diagnosisId) === String(diagnosisId)) {
        state.diagnosisResult = records.length ? records[0] : null;
      }
      state.diagnosisMessage = { type: "info", text: "诊断记录已删除。" };
      renderPage(pageByKey[state.route]);
    }).catch(function (error) {
      state.diagnosisMessage = { type: "warning", text: error.message || "删除诊断记录失败，请稍后重试。" };
      renderPage(pageByKey[state.route]);
    });
  }

  function profileContextText() {
    var parts = [];
    var target = resumeTargetDefault();
    var assessment = firstText(getValue(state.snapshot, "assessment.summary"), getValue(state.snapshot, "assessment.resultSummary"));
    if (target) {
      parts.push("targetRole=" + target);
    }
    if (assessment) {
      parts.push("assessment=" + assessment);
    }
    return parts.join("; ");
  }

  function previewDiagnosisResult(draft) {
    return {
      resumeId: draft.resumeId,
      overallScore: 78,
      strengths: ["已有简历文本和目标岗位输入"],
      weaknesses: ["项目结果和岗位关键词还可以继续量化"],
      suggestions: ["优先补充 1 条与目标岗位要求直接相关的项目经历"],
      contextSources: ["preview", draft.targetJob ? "targetJob" : ""],
      revisionPlan: {
        totalSuggestions: 1,
        highPrioritySuggestions: 1,
        overallPriority: "HIGH",
        contextSummary: "preview / targetJob"
      },
      revisionSuggestions: [{
        suggestionId: "preview-rev-1",
        priority: "HIGH",
        resumeSection: "projects",
        problem: "项目经历缺少可量化结果",
        action: "补充动作、技术、指标和个人贡献",
        rewriteExample: "使用 Java/Spring 完成核心模块，将响应时间或效率提升写成明确指标。",
        targetKeywords: ["Java", "Spring", "项目指标"],
        status: "TODO"
      }]
    };
  }

  function openPreviewUrl(previewWindow, url) {
    if (previewWindow && !previewWindow.closed) {
      previewWindow.opener = null;
      previewWindow.location.href = url;
      return;
    }
    window.open(url, "_blank");
  }

  function closePreviewWindow(previewWindow) {
    if (previewWindow && !previewWindow.closed) {
      previewWindow.close();
    }
  }

  function deleteResumeRecord(resumeId) {
    if (!resumeId) {
      return;
    }
    showConfirmDialog("删除简历记录", "删除后这条简历记录将从列表中移除。", "删除", function () {
      performDeleteResumeRecord(resumeId);
    });
  }

  function renderCareerPlanPage(item) {
    if (hasUserIdentity() && shouldEnsureCurrentPlan(state.plan) && !state.planEnsuring && !isFilePreview()) {
      ensureCurrentRoutePlan();
    }
    var view = buildPlanViewModel();
    var summary = firstText(view.plan.startStateSummary, view.plan.summary,
      isStudyRoute() ? "根据升学方向、目标院校和个人情况生成分阶段升学路线图。" : "根据个人情况、简历记录和目标岗位生成分阶段就业路线图。");
    var phaseHtml = view.visiblePhases.length ? view.visiblePhases.map(function (phase, index) {
      return renderPlanPhaseCard(phase, index, view.progressState, view.activePhaseId, view.currentPhaseId);
    }).join("") :
      statePanel(isStudyRoute() ? "尚未生成真实升学规划" : "路线图待生成",
        isStudyRoute() ? "当前没有通过智能体校验的升学路线，点击“生成智能路线图”后才会展示规划。" : "当前还没有可展示的路线图，点击“生成智能路线图”即可开始规划。", "pending");
    var timelineHtml = view.visiblePhases.length ? renderPlanTimeline(view.visiblePhases, view.activePhaseId, view.progressSummary) : "";
    var flowHtml = view.visiblePhases.length ? renderPlanFlow(view.visiblePhases, view.activePhaseId, view.progressSummary) : "";
    renderFeatureShell(item, item.title, isStudyRoute() ? "根据升学方向、目标院校和个人情况整理未来一年的行动路线。" : "根据你的个人情况、简历和目标岗位整理未来一年的行动路线。",
      renderPlanSummaryCard(summary, view.plan) +
      '<section class="feature-section route-execution-grid daily-first">' +
      renderDailyPlanCard(view.dailyPlan, view.progressState) +
      renderWeeklyPlanCard(view.weeklyPlan, view.weeklyActions, view.weeklyDeliverables, view.targetRole, view.progressState) +
      '</section>' +
      timelineHtml +
      '<section class="feature-section"><div class="section-heading"><div><h3>阶段路线图</h3><p class="section-note">展示未来 1 年的阶段目标，并细化到本周和每天。</p></div>' +
      '<div class="section-actions"><button type="button" data-ensure-plan ' + (state.planEnsuring ? 'disabled aria-disabled="true"' : '') + '>' +
      (state.planEnsuring ? "生成中" : planGenerationButtonLabel(view.plan, "生成智能路线图")) + '</button></div></div>' +
      flowHtml +
      '<div class="route-phase-grid">' + phaseHtml + '</div></section>' +
      '');
  }

  function renderPlanSummaryCard(summary, plan) {
    var items = planSummaryItems(summary, plan);
    if (!items.length) {
      return "";
    }
    return '<section class="panel full plan-summary-card">' +
      '<div class="route-card-head"><div><span class="resource-type">规划依据</span><h3>当前情况概览</h3></div></div>' +
      '<ul class="plan-summary-list">' + items.map(function (item, index) {
        return '<li><span class="plan-summary-label">' + escapeHtml(planSummaryLabel(item, index)) + '</span>' +
          '<p>' + escapeHtml(item) + '</p></li>';
      }).join("") + '</ul></section>';
  }

  function planSummaryItems(summary, plan) {
    var items = splitPlanSummary(summary);
    if (!isStudyRoute()) {
      return items;
    }
    var direction = studyCenterDirectionLabel(firstText(plan && plan.studyDirection,
      getValue(state.studyCenterSelection, "direction"), "POSTGRADUATE"));
    var targetSchool = firstText(plan && plan.targetSchool,
      getValue(state.snapshot, "onboarding.targetSchool"), "");
    var hasStudyTarget = items.some(function (item) {
      return /考研目标|升学目标|目标院校|报考院校|目标专业/.test(item);
    });
    if (!hasStudyTarget && (direction || targetSchool)) {
      items.push("升学目标：" + direction +
        (targetSchool ? "，目标院校为“" + targetSchool + "”" : "，目标院校待确认"));
    }
    return items;
  }

  function splitPlanSummary(summary) {
    var text = trim(summary);
    if (!text) {
      return [];
    }
    var items = text.split(/[；;\n]+/).map(cleanPlanSummaryItem).filter(Boolean);
    if (items.length === 1 && text.length > 90) {
      items = text.split(/[。！？]+/).map(cleanPlanSummaryItem).filter(Boolean);
    }
    return items;
  }

  function cleanPlanSummaryItem(item) {
    return trim(item).replace(/^[，,、\s]+|[。；;，,\s]+$/g, "");
  }

  function planSummaryLabel(item, index) {
    var text = trim(item);
    var study = isStudyRoute();
    if (/当前处于|当前阶段|目前处于|求职阶段|备考阶段|复习阶段/.test(text)) return "当前阶段";
    if (/已具备|已有|优势|基础|经验/.test(text)) return "已有基础";
    if (/缺口|不足|短板|薄弱|需要补|亟需|差距/.test(text)) return study ? "准备差距" : "能力缺口";
    if (/尚未确认|未确认|待确认|信息不足|缺少信息/.test(text)) return "待确认信息";
    if (/时间|窗口|截止|周期|日期|来不及|错过/.test(text)) return "时间提醒";
    if (/资料|招生说明|专业目录|成绩单/.test(text) && study) return "规划资料";
    if (/目标|期望|城市|偏好|岗位|院校|升学|报考/.test(text)) return study ? "考研目标" : "求职目标";
    return "情况 " + (index + 1);
  }

  function buildPlanViewModel() {
    var plan = state.plan && !state.plan.unavailable ? state.plan : {};
    if (isStudyRoute() && !hasVerifiedStudyPlan(plan)) plan = {};
    var targetRole = currentPlanningTarget(plan);
    var phases = normalizeArray(plan.phases);
    var weeklyPlan = plan.weeklyPlan || {};
    if (!phases.length && isFilePreview() && !isStudyRoute()) {
      phases = previewPlanPhases(targetRole);
    }
    var selectedYears = 1;
    var visiblePhases = filterPlanPhasesByYears(phases, selectedYears);
    var progressState = readPlanProgress(plan, targetRole, phases);
    mergeDailyCompletionIntoProgress(state.dailyPlan, progressState, visiblePhases);
    if (!progressState.phaseCursorInitialized || isSelectedPlanPhaseComplete(phases, progressState)) {
      syncPlanPhaseCursor(phases, progressState);
      progressState.phaseCursorInitialized = true;
      persistPlanProgress(plan, targetRole, progressState);
    }
    var currentPhaseId = firstIncompletePlanPhaseId(visiblePhases, progressState);
    var activePhaseId = firstText(progressState.activePhaseId, visiblePhases.length ? phaseKey(visiblePhases[0], 0) : "");
    if (!containsPhaseId(visiblePhases, activePhaseId)) {
      activePhaseId = visiblePhases.length ? phaseKey(visiblePhases[0], 0) : "";
    }
    var dailyPlan = state.dailyPlan && !state.dailyPlan.unavailable
      ? normalizeServerDailyPlan(state.dailyPlan, visiblePhases, activePhaseId)
      : isFilePreview() && !isStudyRoute()
        ? deriveDailyPlan(visiblePhases, weeklyPlan, progressState, activePhaseId, targetRole)
        : { items: [], summary: "每日计划暂时无法加载，请稍后重试；系统不会用无关任务替代。" };
    var progressSummary = summarizePlanProgress(visiblePhases, progressState, currentPhaseId);
    return {
      plan: plan,
      targetRole: targetRole,
      phases: phases,
      visiblePhases: visiblePhases,
      weeklyPlan: weeklyPlan,
      weeklyActions: normalizeArray(weeklyPlan.actions || plan.weeklyFocus),
      weeklyDeliverables: normalizeArray(weeklyPlan.deliverables),
      selectedYears: selectedYears,
      progressState: progressState,
      activePhaseId: activePhaseId,
      currentPhaseId: currentPhaseId,
      dailyPlan: dailyPlan,
      progressSummary: progressSummary
    };
  }

  function renderDailyPlanCard(dailyPlan, progressState) {
    return '<section class="panel route-daily-card"><div class="route-card-head"><div><span class="resource-type">每日计划</span><h3>今天可以做什么</h3></div></div>' +
      '<p class="route-goal">' + escapeHtml(dailyPlan.summary) + '</p>' +
      renderLinkedTaskList("今日小事", dailyPlan.items, progressState) + '</section>';
  }

  function normalizeServerDailyPlan(dailyPlan, phases, activePhaseId) {
    var items = normalizeArray(dailyPlan.items).map(function (item) {
      return {
        taskId: item.taskId,
        sourceTaskId: planProgressTaskId(item.sourceTaskId, phases),
        text: item.text,
        completed: String(item.status || "").toUpperCase() === "COMPLETED",
        carriedOver: !!item.carriedOver,
        persisted: true
      };
    });
    return {
      items: items,
      summary: firstText(dailyPlan.summary, "今天按当前阶段的顺序完成这些事项。"),
      planDate: dailyPlan.planDate,
      phaseId: firstText(dailyPlan.phaseId, activePhaseId)
    };
  }

  function mergeDailyCompletionIntoProgress(dailyPlan, progressState, phases) {
    if (!dailyPlan || dailyPlan.unavailable || !progressState) return;
    normalizeArray(dailyPlan.completedSourceTaskIds).forEach(function (sourceTaskId) {
      var progressTaskId = planProgressTaskId(sourceTaskId, phases);
      if (progressTaskId) progressState.checked[progressTaskId] = true;
    });
    normalizeArray(dailyPlan.items).forEach(function (item) {
      if (item && item.sourceTaskId && String(item.status || "").toUpperCase() === "COMPLETED") {
        progressState.checked[planProgressTaskId(item.sourceTaskId, phases)] = true;
      }
    });
  }

  function planProgressTaskId(sourceTaskId, phases) {
    var source = firstText(sourceTaskId, "");
    if (!source) return "";
    var phaseList = normalizeArray(phases);
    for (var i = 0; i < phaseList.length; i += 1) {
      var phase = phaseList[i];
      var phaseId = phaseKey(phase, i);
      var resolved = planProgressTaskFromGroup(source, phaseId + ".actions", normalizeArray(phase.actions));
      if (resolved) return resolved;
      resolved = planProgressTaskFromGroup(source, phaseId + ".kpis", normalizeArray(phase.kpis));
      if (resolved) return resolved;
      var subStages = normalizeArray(phase.subStages);
      for (var j = 0; j < subStages.length; j += 1) {
        resolved = planProgressTaskFromGroup(source, phaseId + ".substage." + j + ".actions", normalizeArray(subStages[j].actions));
        if (resolved) return resolved;
      }
    }
    return source;
  }

  function planProgressTaskFromGroup(sourceTaskId, scope, items) {
    if (sourceTaskId.indexOf(scope + ".") !== 0) return "";
    var index = Number(sourceTaskId.substring((scope + ".").length));
    if (!Number.isInteger(index) || index < 0 || index >= items.length) return "";
    return scope + "." + index + "." + sanitizeKey(items[index]);
  }

  function renderWeeklyPlanCard(weeklyPlan, weeklyActions, weeklyDeliverables, targetRole, progressState) {
    return '<section class="panel route-weekly-card"><div class="route-card-head"><div><span class="resource-type">本周计划</span><h3>' +
      escapeHtml(firstText(weeklyPlan.weekTitle, "本周推进重点")) + '</h3></div></div><p class="route-goal">' +
      escapeHtml(firstText(weeklyPlan.weekGoal, "围绕当前目标岗位推进简历、项目和面试准备。")) + '</p>' +
      renderTaskList("本周动作", weeklyActions.length ? weeklyActions : defaultRoadmapFocus(targetRole), "weekly-actions", progressState) +
      renderTaskList("本周交付物", weeklyDeliverables.length ? weeklyDeliverables : ["简历优化清单", "岗位关键词清单", "一次模拟面试复盘"], "weekly-deliverables", progressState) +
      '</section>';
  }

  function renderPlanPhaseCard(phase, phaseIndex, progressState, activePhaseId, currentPhaseId) {
    var subStages = normalizeArray(phase && phase.subStages);
    var phaseId = phaseKey(phase, phaseIndex);
    var phaseStatus = phaseProgressStatus(phase, phaseIndex, progressState, currentPhaseId);
    var cls = "panel route-phase-card" + (phaseId === activePhaseId ? " active" : "");
    return '<article class="' + cls + '" data-phase-card="' + escapeAttr(phaseId) + '">' +
      '<div class="route-card-head"><div><span class="resource-type">' + escapeHtml(firstText(phase && phase.horizon, "阶段")) + '</span>' +
      '<h3>' + escapeHtml(firstText(phase && phase.title, "阶段目标")) + '</h3></div>' +
      '<span class="phase-status ' + escapeAttr(phaseStatus.code) + '">' + escapeHtml(phaseStatus.label) + '</span></div>' +
      '<p class="route-goal">' + escapeHtml(firstText(phase && phase.goal, phase && phase.description, "围绕目标岗位推进阶段目标。")) + '</p>' +
      renderTaskList("阶段动作", normalizeArray(phase && phase.actions), phaseId + ".actions", progressState) +
      renderTaskList("阶段达成", normalizeArray(phase && phase.kpis), phaseId + ".kpis", progressState) +
      renderSubStageList(subStages, phaseId, progressState) +
      '</article>';
  }

  function renderSubStageList(subStages, phaseId, progressState) {
    if (!subStages.length) {
      return "";
    }
    return '<div class="route-substage-list">' + subStages.map(function (subStage, index) {
      var subStageId = phaseId + ".substage." + index;
      return '<section class="route-substage">' +
        '<strong>' + escapeHtml(firstText(subStage.period, "短周期")) + " · " + escapeHtml(firstText(subStage.title, "执行阶段")) + '</strong>' +
        '<p>' + escapeHtml(firstText(subStage.goal, "围绕阶段目标推进执行。")) + '</p>' +
        renderTaskList("建议动作", normalizeArray(subStage.actions), subStageId + ".actions", progressState) +
        '</section>';
    }).join("") + '</div>';
  }

  function renderSimpleList(title, items) {
    var list = normalizeArray(items).filter(Boolean);
    if (!list.length) {
      return "";
    }
    return '<div class="route-list-block"><span class="label">' + escapeHtml(title) + '</span><ul class="compact-list">' +
      list.map(function (item) { return '<li>' + escapeHtml(item) + '</li>'; }).join("") + '</ul></div>';
  }

  function renderTaskList(title, items, scopeKey, progressState) {
    var list = normalizeArray(items).filter(Boolean);
    if (!list.length) {
      return "";
    }
    return '<div class="route-list-block"><span class="label">' + escapeHtml(title) + '</span><ul class="route-task-list">' +
      list.map(function (item, index) {
        var taskId = scopeKey + "." + index + "." + sanitizeKey(item);
        var checked = !!(progressState.checked && progressState.checked[taskId]);
        return '<li class="route-task-item' + (checked ? " done" : "") + '">' +
          '<label class="route-task-toggle"><input type="checkbox" data-plan-task="' + escapeAttr(taskId) + '"' + (checked ? " checked" : "") + '>' +
          '<span class="route-checkmark"></span><span class="route-task-text">' + escapeHtml(item) + '</span></label></li>';
      }).join("") + '</ul></div>';
  }

  function renderLinkedTaskList(title, items, progressState) {
    var list = normalizeArray(items).filter(function (item) { return item && item.taskId && item.text; });
    if (!list.length) {
      return "";
    }
    return '<div class="route-list-block"><span class="label">' + escapeHtml(title) + '</span><ul class="route-task-list">' +
      list.map(function (item) {
        var checked = item.persisted ? !!item.completed : !!(progressState.checked && progressState.checked[item.taskId]);
        return '<li class="route-task-item' + (checked ? " done" : "") + '">' +
          '<label class="route-task-toggle"><input type="checkbox" ' + (item.persisted ? 'data-daily-task' : 'data-plan-task') + '="' + escapeAttr(item.taskId) + '"' +
          (item.sourceTaskId ? ' data-source-task="' + escapeAttr(item.sourceTaskId) + '"' : '') + (checked ? " checked" : "") + '>' +
          '<span class="route-checkmark"></span><span class="route-task-text">' + escapeHtml(item.text) +
          (item.carriedOver ? '<small class="route-task-carry">昨日未完成，已优先顺延</small>' : '') + '</span></label></li>';
      }).join("") + '</ul></div>';
  }

  function renderPlanTimeline(phases, activePhaseId, progressSummary) {
    var percent = progressSummary.totalTasks ? Math.round(progressSummary.completedTasks * 100 / progressSummary.totalTasks) : 0;
    return '<section class="panel route-progress-card">' +
      '<div class="route-progress-head"><div><h3>总目标进度</h3><p class="panel-note">只统计阶段路线图里的阶段动作和阶段达成；每日计划和本周计划用于辅助推进。</p></div>' +
      '<strong>' + percent + '%</strong></div>' +
      '<div class="route-progress-track"><span class="route-progress-fill" style="width:' + percent + '%"></span></div>' +
      '<div class="route-progress-meta"><span>已完成 ' + progressSummary.completedTasks + ' / ' + progressSummary.totalTasks + ' 项</span><span>当前聚焦：' + escapeHtml(progressSummary.activePhaseTitle) + '</span></div>' +
      '<div class="route-milestone-row">' + phases.map(function (phase, index) {
        var phaseId = phaseKey(phase, index);
        var left = phases.length === 1 ? 0 : Math.round(index * 100 / (phases.length - 1));
        var active = phaseId === activePhaseId;
        var edgeClass = index === 0 ? " first" : index === phases.length - 1 ? " last" : "";
        return '<button type="button" class="route-milestone' + edgeClass + (active ? " active" : "") + '" style="left:' + left + '%" data-plan-focus="' + escapeAttr(phaseId) + '">' +
          '<span class="route-milestone-dot"></span><span class="route-milestone-label">' + escapeHtml(firstText(phase.horizon, phase.title, "阶段")) + '</span></button>';
      }).join("") + '</div></section>';
  }

  function renderPlanFlow(phases, activePhaseId, progressSummary) {
    return '<div class="route-arrow-flow">' + phases.map(function (phase, index) {
      var phaseId = phaseKey(phase, index);
      var phaseStats = progressSummary.phaseMap[phaseId] || { completed: 0, total: 0 };
      var active = phaseId === activePhaseId;
      var done = phaseStats.total > 0 && phaseStats.completed >= phaseStats.total;
      return '<button type="button" class="route-arrow-step' + (active ? " active" : "") + (done ? " done" : "") + '" data-plan-focus="' + escapeAttr(phaseId) + '">' +
        '<span class="route-arrow-period">' + escapeHtml(firstText(phase.horizon, "阶段")) + '</span>' +
        '<strong>' + escapeHtml(firstText(phase.title, "阶段目标")) + '</strong>' +
        '<small>' + phaseStats.completed + '/' + phaseStats.total + '</small></button>';
    }).join('<span class="route-arrow-connector" aria-hidden="true">→</span>') + '</div>';
  }

  function summarizePlanProgress(phases, progressState, currentPhaseId) {
    var totalTasks = 0;
    var completedTasks = 0;
    var phaseMap = {};
    for (var i = 0; i < phases.length; i += 1) {
      var phase = phases[i];
      var phaseId = phaseKey(phase, i);
      phaseMap[phaseId] = { completed: 0, total: 0, title: firstText(phase.title, phase.horizon, "阶段目标") };
      countTaskGroup(normalizeArray(phase.actions), phaseId + ".actions", progressState, phaseMap[phaseId]);
      countTaskGroup(normalizeArray(phase.kpis), phaseId + ".kpis", progressState, phaseMap[phaseId]);
      var subStages = normalizeArray(phase.subStages);
      for (var j = 0; j < subStages.length; j += 1) {
        countTaskGroup(normalizeArray(subStages[j].actions), phaseId + ".substage." + j + ".actions", progressState, phaseMap[phaseId]);
      }
      totalTasks += phaseMap[phaseId].total;
      completedTasks += phaseMap[phaseId].completed;
    }
    var activePhaseTitle = "";
    if (currentPhaseId && phaseMap[currentPhaseId]) {
      activePhaseTitle = phaseMap[currentPhaseId].title;
    } else if (totalTasks > 0 && completedTasks >= totalTasks) {
      activePhaseTitle = "全部阶段已完成";
    } else if (phases.length) {
      activePhaseTitle = firstText(phases[0].title, phases[0].horizon, "阶段目标");
    }
    return {
      totalTasks: totalTasks,
      completedTasks: completedTasks,
      phaseMap: phaseMap,
      activePhaseTitle: activePhaseTitle
    };
  }

  function countTaskGroup(items, scopeKey, progressState, bucket) {
    var list = normalizeArray(items).filter(Boolean);
    for (var i = 0; i < list.length; i += 1) {
      bucket.total += 1;
      if (isPlanTaskChecked(scopeKey + "." + i + "." + sanitizeKey(list[i]), progressState)) {
        bucket.completed += 1;
      }
    }
  }

  function countCompletedTaskGroup(items, scopeKey, progressState) {
    var list = normalizeArray(items).filter(Boolean);
    var completed = 0;
    for (var i = 0; i < list.length; i += 1) {
      var taskId = typeof list[i] === "object" && list[i].taskId
        ? list[i].taskId
        : scopeKey + "." + i + "." + sanitizeKey(list[i]);
      if (isPlanTaskChecked(taskId, progressState)) {
        completed += 1;
      }
    }
    return completed;
  }

  function isPlanTaskChecked(taskId, progressState) {
    return !!(progressState && progressState.checked && progressState.checked[taskId]);
  }

  function phaseKey(phase, index) {
    return firstText(phase && phase.phaseId, "") || "phase-" + sanitizeKey(firstText(phase && phase.horizon, "")) + "-" + sanitizeKey(firstText(phase && phase.title, "stage"));
  }

  function containsPhaseId(phases, phaseId) {
    for (var i = 0; i < phases.length; i += 1) {
      if (phaseKey(phases[i], i) === phaseId) {
        return true;
      }
    }
    return false;
  }

  function deriveDailyPlan(phases, weeklyPlan, progressState, activePhaseId, targetRole) {
    var linked = [];
    var activePhase = findActivePhase(phases, activePhaseId);
    if (activePhase) {
      linked = linked.concat(collectDailyMicroTasks(activePhase.actions, phaseKey(activePhase.phase, activePhase.index) + ".actions", progressState));
      linked = linked.concat(collectDailyMicroTasks(activePhase.kpis, phaseKey(activePhase.phase, activePhase.index) + ".kpis", progressState));
      var subStages = normalizeArray(activePhase.phase.subStages);
      for (var i = 0; i < subStages.length; i += 1) {
        linked = linked.concat(collectDailyMicroTasks(subStages[i].actions, phaseKey(activePhase.phase, activePhase.index) + ".substage." + i + ".actions", progressState));
      }
    }
    linked = linked.concat(collectDailyMicroTasks(weeklyPlan.actions, "weekly-actions", progressState));
    linked = linked.concat(collectDailyMicroTasks(weeklyPlan.deliverables, "weekly-deliverables", progressState));
    linked = orderDailyTasksByProgress(dedupeLinkedTasks(linked), progressState).slice(0, 5);
    if (!linked.length) {
      linked = orderDailyTasksByProgress(defaultDailySuggestions(targetRole).map(function (text, index) {
        return { taskId: "daily-suggestions." + index + "." + sanitizeKey(text), text: text };
      }), progressState).slice(0, 5);
    }
    var nextItem = firstPendingDailyTask(linked, progressState);
    return {
      items: linked,
      nextItem: nextItem,
      summary: activePhase
        ? "今天优先围绕“" + firstText(activePhase.phase.title, "当前阶段") + "”里还没完成的事项推进。"
        : "今天优先处理当前阶段和本周尚未完成的事项。"
    };
  }

  function collectUnfinishedTasks(items, scopeKey, progressState, prefix) {
    var list = normalizeArray(items).filter(Boolean);
    var out = [];
    for (var i = 0; i < list.length; i += 1) {
      var taskId = scopeKey + "." + i + "." + sanitizeKey(list[i]);
      if (!isPlanTaskChecked(taskId, progressState)) {
        out.push({ taskId: taskId, text: prefix + list[i] });
      }
    }
    return out;
  }

  function orderDailyTasksByProgress(items, progressState) {
    var pending = [];
    var completed = [];
    for (var i = 0; i < items.length; i += 1) {
      if (isPlanTaskChecked(items[i].taskId, progressState)) {
        completed.push(items[i]);
      } else {
        pending.push(items[i]);
      }
    }
    return pending.concat(completed);
  }

  function firstPendingDailyTask(items, progressState) {
    for (var i = 0; i < items.length; i += 1) {
      if (!isPlanTaskChecked(items[i].taskId, progressState)) {
        return items[i];
      }
    }
    return null;
  }

  function collectDailyMicroTasks(items, scopeKey, progressState) {
    var list = normalizeArray(items).filter(Boolean);
    var out = [];
    for (var i = 0; i < list.length; i += 1) {
      var text = microTaskText(list[i], i, scopeKey);
      var taskId = "daily." + scopeKey + "." + i + "." + sanitizeKey(text);
      out.push({ taskId: taskId, text: text });
    }
    return out;
  }

  function microTaskText(taskText, index, scopeKey) {
    var text = trim(taskText);
    if (containsAny(text, ["简历", "bullet", "经历"])) {
      return pickMicroTask([
        "用 30 分钟改 1 条简历经历，补上结果、数字和个人贡献。",
        "挑 1 段项目经历，删掉空泛描述，补 1 个可量化结果。",
        "把 1 条简历 bullet 改成“动作 + 方法 + 结果”的表达。"
      ], index, scopeKey);
    }
    if (containsAny(text, ["岗位", "岗位要求", "招聘", "公司", "关键词"])) {
      return pickMicroTask([
        "看 2 个目标岗位描述，记录 3 个高频能力词。",
        "打开 1 个目标岗位，把任职要求拆成 3 个技能点。",
        "选 1 家目标公司，写下它最看重的 2 个岗位能力。"
      ], index, scopeKey);
    }
    if (containsAny(text, ["项目", "作品", "仓库", "README"])) {
      return pickMicroTask([
        "推进 1 个项目小任务，并在记录里写下今天产出的 1 个证据。",
        "给项目补 1 段 README，说明背景、你的贡献和结果。",
        "整理 1 个项目截图或链接，作为简历里的可展示证据。"
      ], index, scopeKey);
    }
    if (containsAny(text, ["面试", "经历讲述", "复盘", "问答"])) {
      return pickMicroTask([
        "练 1 个面试问题，按背景、任务、行动、结果写下 4 句话版本。",
        "复盘 1 个项目问题，准备 1 个追问和对应回答。",
        "录 3 分钟自述，检查是否讲清楚背景、动作和结果。"
      ], index, scopeKey);
    }
    if (containsAny(text, ["技能", "学习", "练习", "技术"])) {
      return pickMicroTask([
        "做 45 分钟技能练习，并记录 1 个卡点和 1 个解决思路。",
        "选 1 个薄弱技能点，看 1 篇资料并写 3 行笔记。",
        "完成 1 个小练习，把错误原因写成一句复盘。"
      ], index, scopeKey);
    }
    if (containsAny(text, ["投递", "offer", "机会"])) {
      return pickMicroTask([
        "筛选 3 个岗位，给其中 1 个岗位写投递备注。",
        "更新 1 条投递记录，标出下一步跟进时间。",
        "挑 1 个岗位，对照简历找出 1 个需要补强的证据。"
      ], index, scopeKey);
    }
    if (containsAny(text, ["清单", "整理", "梳理"])) {
      return pickMicroTask([
        "整理 1 个小清单，只补 3 条最关键的信息。",
        "把清单里最重要的 1 项拆成今天能完成的第一步。",
        "删掉清单里 1 条不重要的项，保留今天最该推进的内容。"
      ], index, scopeKey);
    }
    return "把“" + shortText(text, 24) + "”拆成 1 个 30 分钟内能完成的小动作。";
  }

  function pickMicroTask(options, index, scopeKey) {
    var seed = Math.abs((index || 0) + sanitizeKey(scopeKey).length);
    return options[seed % options.length];
  }

  function containsAny(text, keywords) {
    for (var i = 0; i < keywords.length; i += 1) {
      if (text.indexOf(keywords[i]) >= 0) {
        return true;
      }
    }
    return false;
  }

  function dedupeLinkedTasks(items) {
    var seen = {};
    var out = [];
    for (var i = 0; i < items.length; i += 1) {
      var key = sanitizeKey(items[i].text);
      if (!seen[key]) {
        seen[key] = true;
        out.push(items[i]);
      }
    }
    return out;
  }

  function findActivePhase(phases, activePhaseId) {
    for (var i = 0; i < phases.length; i += 1) {
      if (phaseKey(phases[i], i) === activePhaseId) {
        return { phase: phases[i], index: i };
      }
    }
    return phases.length ? { phase: phases[0], index: 0 } : null;
  }

  function filterPlanPhasesByYears(phases, selectedYears) {
    var list = normalizeArray(phases);
    if (isStudyRoute()) return list;
    return list.filter(function (phase) {
      return phaseHorizonMaxYears(phase) <= selectedYears;
    });
  }

  function phaseHorizonMaxYears(phase) {
    var horizon = firstText(phase && phase.horizon, "");
    if (horizon.indexOf("年") >= 0) {
      var yearMatch = horizon.match(/(\d+)\s*-\s*(\d+)年|(\d+)年/);
      if (yearMatch) {
        if (yearMatch[2]) {
          return parseInt(yearMatch[2], 10);
        }
        if (yearMatch[3]) {
          return parseInt(yearMatch[3], 10);
        }
      }
    }
    if (horizon.indexOf("个月") >= 0 || horizon.indexOf("月") >= 0) {
      return 1;
    }
    return 3;
  }

  function sanitizeKey(value) {
    return trim(value).toLowerCase().replace(/[^a-z0-9\u4e00-\u9fa5]+/g, "-").replace(/^-+|-+$/g, "") || "item";
  }

  function readPlanProgress(plan, targetRole, phases) {
    var key = planProgressStorageKey(plan, targetRole);
    var stored = parseStorageJson(localStorage, key) || {};
    stored.checked = stored.checked || {};
    if (!firstText(stored.activePhaseId) && phases.length) {
      stored.activePhaseId = phaseKey(phases[0], 0);
    }
    state.planProgress = stored;
    return stored;
  }

  function planProgressStorageKey(plan, targetRole) {
    var userId = hasUserIdentity() ? state.identity.userId : "preview";
    var role = sanitizeKey(firstText(plan && plan.targetRole, targetRole, "general"));
    return "cyancruise.planProgress." + sanitizeKey(userId) + "." + role;
  }

  function persistPlanProgress(plan, targetRole, progressState) {
    var key = planProgressStorageKey(plan, targetRole);
    state.planProgress = progressState;
    localStorage.setItem(key, JSON.stringify(progressState));
  }

  function mergeRefreshedPlan(previousPlan, nextPlan) {
    var incoming = nextPlan || {};
    var existing = previousPlan || {};
    var nextPhases = normalizeArray(incoming.phases);
    if (!nextPhases.length) {
      return incoming;
    }
    var targetRole = employmentTargetRole(existing);
    var progressState = readPlanProgress(existing, targetRole, normalizeArray(existing.phases));
    var existingPhases = normalizeArray(existing.phases);
    var mergedPhases = [];
    var phaseCount = Math.max(existingPhases.length, nextPhases.length);
    for (var phaseIndex = 0; phaseIndex < phaseCount; phaseIndex += 1) {
      var currentPhase = existingPhases[phaseIndex];
      var nextPhase = nextPhases[phaseIndex];
      if (currentPhase && (isStartedPlanStatus(currentPhase.status)
          || isPhaseStartedByProgress(currentPhase, phaseIndex, progressState))) mergedPhases.push(currentPhase);
      else if (nextPhase) mergedPhases.push(nextPhase);
    }
    incoming.phases = mergedPhases;
    if (!firstText(progressState.activePhaseId) && incoming.phases.length) {
      progressState.activePhaseId = phaseKey(incoming.phases[0], 0);
      persistPlanProgress(incoming, employmentTargetRole(incoming), progressState);
    }
    return incoming;
  }

  function sameTargetRole(first, second) {
    return trim(first) === trim(second);
  }

  function phaseProgressStatus(phase, index, progressState, currentPhaseId) {
    var phaseId = phaseKey(phase, index);
    var counters = phaseTaskCounters(phase, index, progressState);
    if (isPlanPhaseComplete(phase, index, progressState)) {
      return { code: "done", label: "已完成" };
    }
    if (phaseId === currentPhaseId || isStartedPlanStatus(phase && phase.status) || counters.completed > 0) {
      return { code: "active", label: "进行中" };
    }
    return { code: "idle", label: "未开始" };
  }

  function isPhaseStartedByProgress(phase, index, progressState) {
    return phaseTaskCounters(phase, index, progressState).completed > 0;
  }

  function phaseTaskCounters(phase, index, progressState) {
    var phaseId = phaseKey(phase, index);
    var counters = { total: 0, completed: 0 };
    countTaskGroup(normalizeArray(phase && phase.actions), phaseId + ".actions", progressState, counters);
    countTaskGroup(normalizeArray(phase && phase.kpis), phaseId + ".kpis", progressState, counters);
    var subStages = normalizeArray(phase && phase.subStages);
    for (var i = 0; i < subStages.length; i += 1) {
      countTaskGroup(normalizeArray(subStages[i].actions), phaseId + ".substage." + i + ".actions", progressState, counters);
    }
    return counters;
  }

  function isPlanPhaseComplete(phase, index, progressState) {
    var counters = phaseTaskCounters(phase, index, progressState);
    return isCompletedPlanStatus(phase && phase.status)
      || (counters.total > 0 && counters.completed >= counters.total);
  }

  function firstIncompletePlanPhaseId(phases, progressState) {
    for (var i = 0; i < phases.length; i += 1) {
      if (!isPlanPhaseComplete(phases[i], i, progressState)) {
        return phaseKey(phases[i], i);
      }
    }
    return "";
  }

  function isSelectedPlanPhaseComplete(phases, progressState) {
    var selectedPhaseId = firstText(progressState && progressState.activePhaseId, "");
    if (!selectedPhaseId) return false;
    for (var i = 0; i < phases.length; i += 1) {
      if (phaseKey(phases[i], i) === selectedPhaseId) {
        return isPlanPhaseComplete(phases[i], i, progressState);
      }
    }
    return false;
  }

  function syncPlanPhaseCursor(phases, progressState) {
    if (!progressState || !phases.length) return "";
    var nextPhaseId = firstIncompletePlanPhaseId(phases, progressState);
    if (!nextPhaseId) {
      nextPhaseId = phaseKey(phases[phases.length - 1], phases.length - 1);
    }
    progressState.activePhaseId = nextPhaseId;
    return nextPhaseId;
  }

  function performDeleteResumeRecord(resumeId) {
    if (isFilePreview()) {
      state.resumes = normalizeArray(state.resumes).filter(function (item) {
        return String(firstText(item.resumeId, item.id, "")) !== String(resumeId);
      });
      state.resumeMessage = { type: "info", text: "本地预览记录已删除。" };
      updateOverviewCards();
      renderPage(pageByKey[state.route]);
      return;
    }
    state.resumeMessage = { type: "info", text: "正在删除简历记录。" };
    renderPage(pageByKey[state.route]);
    deleteResumeByService(resumeId).then(function () {
      state.resumeMessage = { type: "info", text: "简历记录已删除，列表正在刷新。" };
      return refreshResumeList(false);
    }).then(function () {
      return refreshSnapshotAfterResume();
    }).then(function () {
      showMessage("info", "简历已删除", "已刷新简历列表和工作台摘要。");
      renderPage(pageByKey[state.route]);
    }).catch(function (error) {
      state.resumeMessage = { type: "warning", text: error.message || "简历删除失败，请检查 KAPI token 和后端状态。" };
      showMessage("error", "简历删除失败", state.resumeMessage.text);
      renderPage(pageByKey[state.route]);
    });
  }

  function refreshResumeSnapshotByService() {
    if (window.CYANCRUISE_SERVICES && window.CYANCRUISE_SERVICES.resume
        && typeof window.CYANCRUISE_SERVICES.resume.refreshSnapshot === "function") {
      return window.CYANCRUISE_SERVICES.resume.refreshSnapshot({
        endpoints: endpoints,
        post: post,
        userId: state.identity.userId
      });
    }
    return post(endpoints.snapshot, state.identity.userId);
  }

  function deleteResumeByService(resumeId) {
    if (window.CYANCRUISE_SERVICES && window.CYANCRUISE_SERVICES.resume
        && typeof window.CYANCRUISE_SERVICES.resume.deleteRecord === "function") {
      return window.CYANCRUISE_SERVICES.resume.deleteRecord({
        endpoints: endpoints,
        post: post,
        userId: state.identity.userId,
        resumeId: resumeId
      });
    }
    return post(endpoints.resumeDelete, { userId: state.identity.userId, resumeId: resumeId });
  }

  function bytesFromDownloadResult(bytes) {
    if (!bytes) {
      return null;
    }
    if (typeof bytes === "string") {
      return uint8ArrayFromBase64(bytes);
    }
    if (Array.isArray(bytes)) {
      return uint8ArrayFromNumbers(bytes);
    }
    if (bytes.data && Array.isArray(bytes.data)) {
      return uint8ArrayFromNumbers(bytes.data);
    }
    return null;
  }

  function uint8ArrayFromBase64(value) {
    var binary = window.atob(value);
    var out = new Uint8Array(binary.length);
    for (var i = 0; i < binary.length; i += 1) {
      out[i] = binary.charCodeAt(i);
    }
    return out;
  }

  function uint8ArrayFromNumbers(values) {
    var out = new Uint8Array(values.length);
    for (var i = 0; i < values.length; i += 1) {
      var value = Number(values[i]) || 0;
      out[i] = value < 0 ? value + 256 : value;
    }
    return out;
  }

  function mimeTypeFromKey(fileKey) {
    return String(fileKey || "").toLowerCase().indexOf(".pdf") >= 0 ? "application/pdf" : "application/octet-stream";
  }

  function readFileAsBase64(file) {
    return new Promise(function (resolve, reject) {
      var reader = new FileReader();
      reader.onload = function () {
        var result = String(reader.result || "");
        resolve(result.indexOf(",") >= 0 ? result.split(",").pop() : result);
      };
      reader.onerror = function () {
        reject(new Error("读取文件失败"));
      };
      reader.readAsDataURL(file);
    });
  }

  function readResumeDraft() {
    return {
      title: valueOf("resumeTitle"),
      targetJob: valueOf("resumeTargetJob"),
      fileKey: valueOf("resumeFileKey"),
      parsedContent: valueOf("resumeParsedContent")
    };
  }

  function previewResumeRecord(request) {
    return {
      resumeId: "preview",
      title: request.title || "本地预览简历",
      targetJob: request.targetJob,
      fileKey: request.fileKey,
      parsedContent: request.parsedContent,
      createdAt: "file-preview"
    };
  }

  function renderAssessmentPage(item) {
    if (!state.assessmentScales && !state.assessmentLoading) {
      loadAssessment();
    }
    var body = "";

    if (state.assessmentLoading && !state.assessmentScales) {
      body += statePanel("正在加载画像补全", "正在读取青途启航画像补全题组和最近结果。", "pending");
    }
    // A scale-specific failure belongs on its detail page. The assessment home
    // only reports deep-profile availability through deepProfileError.
    if (state.assessmentError && state.assessmentSelectedScaleId) {
      body += statePanel("画像补全暂不可用", state.assessmentError, "warning");
    }

    if (!state.assessmentSelectedScaleId) {
      body += renderAssessmentList();
      renderShell(item, body);
      return;
    }

    var scale = state.assessmentScale || selectedAssessmentScaleSummary() || previewAssessmentScale();
    var detailItem = assessmentDetailPageItem(item, scale);
    var questions = normalizeArray(scale.questions);
    var answered = assessmentAnsweredCount(questions);
    var total = questions.length;
    var result = state.assessmentResult;
    var currentIndex = Math.max(0, Math.min(Number(state.assessmentCurrentIndex || 0), Math.max(total - 1, 0)));
    var currentQuestion = questions[currentIndex];

    if (result && (result.resultSummary || result.summary)) {
      body += '<section class="panel full assessment-result-wrap">' + assessmentResultPanel(result) +
        assessmentAnswerReviewPanel(result) + assessmentAiInterpretationPanel(result) +
        '<div class="actions-row"><button type="button" class="secondary" data-assessment-back>返回题组列表</button>' +
        '<button type="button" data-assessment-retake="' + escapeAttr(result.scaleId || state.assessmentSelectedScaleId) + '">重新测评</button></div></section>';
      renderShell(detailItem, body);
      return;
    }

    body += '<section class="panel full assessment-panel">' +
      '<div class="route-progress-card"><div class="route-progress-head"><strong>' + answered + "/" + total +
      '</strong><span>' + escapeHtml(total ? "第 " + (currentIndex + 1) + " / " + total + " 题" : "暂无题目") + '</span></div>' +
      '<div class="route-progress-track"><span class="route-progress-fill" style="width:' +
      (total ? Math.round(answered * 100 / total) : 0) + '%"></span></div></div>' +
      '</section>';

    body += '<section class="panel full assessment-quiz"><h3>开始答题</h3>' +
      (currentQuestion ? assessmentQuestionCard(currentQuestion, currentIndex) : statePanel("暂无题目", "当前测评题库为空。", "warning")) +
      '<div class="actions-row"><button type="button" class="secondary" data-assessment-prev ' +
      (currentIndex <= 0 || state.assessmentSubmitting ? 'disabled aria-disabled="true"' : '') + '>上一题</button>' +
      '<button type="button" class="secondary" data-assessment-back>返回题组列表</button>' +
      (currentQuestion && currentIndex < total - 1
        ? '<button type="button" data-assessment-next ' +
          (state.assessmentSubmitting || !assessmentQuestionAnswered(currentQuestion) ? 'disabled aria-disabled="true"' : '') + '>下一题</button>'
        : '<button type="button" data-assessment-submit ' +
          (state.assessmentSubmitting || answered < total ? 'disabled aria-disabled="true"' : '') + '>提交补全结果</button>') +
      (state.assessmentSubmitting ? '<button type="button" disabled aria-disabled="true">提交中</button>' : '') + '</div>' +
      '</section>';

    renderShell(detailItem, body);
  }

  function loadAssessment() {
    state.assessmentLoading = true;
    state.assessmentError = null;
    if (isFilePreview()) {
      state.assessmentScales = previewAssessmentScaleSummaries();
      state.assessmentScale = null;
      state.assessmentRecords = [];
      state.deepProfile = null;
      state.assessmentLoading = false;
      if (state.route === "assessment") {
        renderPage(pageByKey.assessment);
      }
      return;
    }
    Promise.all([
      post(endpoints.assessmentScales, {}),
      hasUserIdentity() ? post(endpoints.assessmentRecords, state.identity.userId).catch(function () { return []; }) : Promise.resolve([]),
      hasUserIdentity() ? post(endpoints.deepProfileLatest, state.identity.userId).catch(function () { return null; }) : Promise.resolve(null)
    ]).then(function (results) {
      state.assessmentScales = normalizeArray(results[0]);
      state.assessmentRecords = normalizeArray(results[1]);
      state.deepProfile = results[2] || null;
      if (!state.assessmentScales.length) {
        throw new Error("没有可用的画像补全题组");
      }
      state.assessmentScale = null;
      state.assessmentError = null;
    }).catch(function (error) {
      state.assessmentError = error.message || "画像补全题组暂不可用。";
      state.assessmentScales = previewAssessmentScaleSummaries();
      state.assessmentScale = null;
    }).then(function () {
      state.assessmentLoading = false;
      if (state.route === "assessment") {
        renderPage(pageByKey.assessment);
      }
    });
  }

  function renderAssessmentList() {
    var scales = normalizeArray(state.assessmentScales);
    if (!scales.length) {
      scales = previewAssessmentScaleSummaries();
    }
    var completed = completedAssessmentScaleIds();
    return deepProfileActionPanel() + '<section class="feature-section assessment-list-section full"><div class="section-heading">' +
      '<div><h3>选择画像补全题组</h3><p class="section-note">先补齐人格、性格和偏好信息，结果会写入自画像。</p></div></div>' +
      '<div class="assessment-list">' + scales.map(function (scale, index) {
        var done = completed[String(scale.scaleId)];
        var tone = index === 0 ? "app-icon-tile--cyan" : "app-icon-tile--candy";
        return '<article class="assessment-card app-card-soft">' +
          '<div class="card-left"><div class="app-icon-tile icon-box ' + tone + '"><span class="icon-glyph">' +
          escapeHtml(index === 0 ? "测" : "题") + '</span></div><div class="card-info">' +
          '<strong class="a-title">' + escapeHtml(scale.title) + '</strong>' +
          '<span class="a-desc">' + escapeHtml(scale.description) + '</span>' +
          '<div class="tags"><span class="tag tag-time">约 ' + estimateAssessmentMinutes(scale) + ' 分钟</span>' +
          '<span class="tag tag-blue">' + escapeHtml(firstText(scale.questionCount, normalizeArray(scale.questions).length, 0)) + ' 题</span>' +
          (done ? '<span class="tag tag-done">已完成</span>' : '') + '</div></div></div>' +
          '<div class="card-right"><button type="button" class="btn-start" data-assessment-scale="' +
          escapeAttr(scale.scaleId) + '">' + (done ? "查看上次结果" : "开始测评") + '</button></div></article>';
      }).join("") + '</div></section>';
  }

  function deepProfileActionPanel() {
    var completedCount = Object.keys(completedAssessmentScaleIds()).length;
    var profile = state.deepProfile;
    var action = state.deepProfileGenerating
      ? '<button type="button" class="btn-start" disabled="disabled">正在生成深度画像...</button>'
      : '<button type="button" class="btn-start" data-deep-profile-generate="true">' +
        (profile ? '重新生成深度画像' : '生成深度画像') + '</button>';
    var hint = completedCount
      ? '将根据已完成的测评结果生成深度画像，供后续规划与简历诊断使用。'
      : '完成至少一份测评后，即可生成深度画像。';
    var html = '<section class="feature-section assessment-list-section full"><div class="section-heading">' +
      '<div><h3>深度画像</h3><p class="section-note">' + hint + '</p></div></div>';
    if (state.deepProfileError) {
      html += '<div class="state-panel warning"><strong>深度画像生成失败</strong><p>' +
        escapeHtml(state.deepProfileError) + '</p></div>';
    }
    if (profile) {
      html += renderDeepProfileSummary(profile, action);
    } else {
      html += '<div class="actions-row">' + action + '</div>';
    }
    return html + '</section>';
  }

  function renderDeepProfileSummary(profile, generateAction) {
    var html = '<article class="route-control-card"><div class="route-card-head"><div><h3>最新深度画像</h3>' +
      '<p class="section-note">' + escapeHtml(formatDeepProfileDate(profile.generatedAt)) + '</p></div>' +
      '<span class="tag tag-done">已保存</span></div>';
    return html + '<div class="actions-row">' + generateAction +
      '<button type="button" class="secondary" data-link="deep-profile-detail">查看详情与历史</button></div></article>';
  }

  function renderDeepProfileDetailPage(item) {
    if (state.deepProfileHistory === null && !state.deepProfileHistoryLoading) {
      state.deepProfileHistoryLoading = true;
      window.setTimeout(loadDeepProfileHistory, 0);
    }
    var selected = state.deepProfileSelected || state.deepProfile || normalizeArray(state.deepProfileHistory)[0];
    var body = '<section class="feature-section full"><div class="section-heading"><div><h3>画像详情</h3>' +
      '<p class="section-note">后续规划和诊断始终参考最新一条深度画像，历史记录仅用于回顾。</p></div></div>';
    if (state.deepProfileHistoryLoading && !selected) {
      body += statePanel('正在读取深度画像', '正在加载最新画像和过往记录。', 'pending');
    } else if (selected) {
      body += renderDeepProfile(selected);
    } else {
      body += statePanel('暂无深度画像', '请先完成至少一份测评并生成深度画像。', 'warning');
    }
    body += '</section>' + renderDeepProfileHistory();
    renderShell(item, body);
  }

  function assessmentDetailPageItem(item, scale) {
    return {
      key: item.key,
      title: firstText(scale && scale.title, "测评"),
      summary: firstText(scale && scale.description, "完成答题后将补充到自画像，供后续分析使用。"),
      status: item.status,
      audience: item.audience
    };
  }

  function loadDeepProfileHistory() {
    state.deepProfileHistoryError = null;
    if (isFilePreview()) {
      state.deepProfileHistory = state.deepProfile ? [state.deepProfile] : [];
      state.deepProfileSelected = state.deepProfile || null;
      state.deepProfileHistoryLoading = false;
      if (state.route === 'deep-profile-detail') {
        renderPage(pageByKey['deep-profile-detail']);
      }
      return;
    }
    Promise.all([
      post(endpoints.deepProfileHistory, state.identity.userId),
      state.deepProfile ? Promise.resolve(state.deepProfile) : post(endpoints.deepProfileLatest, state.identity.userId).catch(function () { return null; })
    ]).then(function (results) {
      state.deepProfileHistory = normalizeArray(results[0]);
      state.deepProfile = results[1] || state.deepProfile || state.deepProfileHistory[0] || null;
      state.deepProfileSelected = state.deepProfileSelected || state.deepProfile || state.deepProfileHistory[0] || null;
    }).catch(function (error) {
      state.deepProfileHistory = [];
      state.deepProfileHistoryError = error.message || '深度画像历史暂时无法读取，请稍后重试。';
    }).then(function () {
      state.deepProfileHistoryLoading = false;
      if (state.route === 'deep-profile-detail') {
        renderPage(pageByKey['deep-profile-detail']);
      }
    });
  }

  function renderDeepProfileHistory() {
    var history = normalizeArray(state.deepProfileHistory);
    var html = '<section class="feature-section full"><div class="section-heading"><div><h3>过往记录</h3>' +
      '<p class="section-note">最多保留最近 20 次成功生成的深度画像。</p></div></div>';
    if (state.deepProfileHistoryError) {
      html += statePanel('历史记录暂不可用', state.deepProfileHistoryError, 'warning');
    } else if (!state.deepProfileHistoryLoading && !history.length) {
      html += '<p class="section-note">暂无可查看的历史记录。旧版生成的画像仍可在上方查看，但不会自动补入历史列表。</p>';
    } else {
      html += '<div class="assessment-list">' + history.map(function (profile, index) {
        var selected = state.deepProfileSelected && profile.recordId && String(state.deepProfileSelected.recordId) === String(profile.recordId);
        return '<article class="assessment-card app-card-soft deep-profile-history-card"><div class="card-left"><div class="card-info">' +
          '<strong class="a-title">' + escapeHtml(index === 0 ? '最新深度画像' : '历史深度画像') + '</strong>' +
          '<div class="tags"><span class="tag tag-time">' + escapeHtml(formatDeepProfileDate(profile.generatedAt)) + '</span>' +
          (index === 0 ? '<span class="tag tag-done">当前使用</span>' : '') + '</div></div></div>' +
          '<div class="card-right"><button type="button" class="secondary" data-deep-profile-record="' +
          escapeAttr(profile.recordId || '') + '" ' + (!profile.recordId || selected ? 'disabled aria-disabled="true"' : '') + '>' +
          (selected ? '正在查看' : '查看此版本') + '</button></div></article>';
      }).join('') + '</div>';
    }
    return html + '</section>';
  }

  function selectDeepProfileRecord(recordId) {
    if (!recordId || state.deepProfileHistoryLoading) { return; }
    state.deepProfileHistoryLoading = true;
    state.deepProfileHistoryError = null;
    renderPage(pageByKey['deep-profile-detail']);
    post(endpoints.deepProfileDetail, { userId: state.identity.userId, recordId: recordId }).then(function (profile) {
      if (!profile) {
        throw new Error('未找到这条深度画像记录');
      }
      state.deepProfileSelected = profile;
    }).catch(function (error) {
      state.deepProfileHistoryError = error.message || '这条深度画像暂时无法读取，请稍后重试。';
    }).then(function () {
      state.deepProfileHistoryLoading = false;
      if (state.route === 'deep-profile-detail') {
        renderPage(pageByKey['deep-profile-detail']);
      }
    });
  }

  function formatDeepProfileDate(value) {
    if (!value) { return '生成时间未记录'; }
    var date = new Date(value);
    if (isNaN(date.getTime())) { return String(value); }
    return date.getFullYear() + '-' + padDatePart(date.getMonth() + 1) + '-' + padDatePart(date.getDate()) + ' ' +
      padDatePart(date.getHours()) + ':' + padDatePart(date.getMinutes());
  }

  function padDatePart(value) {
    return Number(value) < 10 ? '0' + Number(value) : String(value);
  }

  function renderDeepProfile(profile) {
    var preferences = profile.workPreferences || {};
    var html = '<article class="route-control-card"><div class="route-card-head"><h3>最新深度画像</h3>' +
      '<span class="tag tag-done">已保存</span></div>';
    html += deepProfileList('画像概述', [profile.profileSummary]);
    html += deepProfileList('画像标签', profile.profileTags);
    html += deepProfileList('优势特点', profile.strengths);
    html += deepProfileList('工作偏好', [preferences.collaborationStyle, preferences.workEnvironment, preferences.decisionStyle].concat(normalizeArray(preferences.motivation)));
    html += deepProfileList('学习偏好', profile.studyPreferences);
    html += deepProfileList('职业倾向', profile.careerInclinations);
    html += deepProfileList('发展建议', profile.developmentSuggestions);
    var evidence = normalizeArray(profile.evidence).map(function (item) {
      if (!item) { return ''; }
      return firstText(item.conclusion, '') + (item.basis ? '：' + item.basis : '');
    });
    html += deepProfileList('分析依据', evidence);
    html += deepProfileList('仍需补充的信息', profile.dataGaps);
    return html + '</article>';
  }

  function deepProfileList(title, values) {
    var items = normalizeArray(values).filter(function (value) { return value; });
    if (!items.length) { return ''; }
    return '<div class="assessment-result-block"><h4>' + escapeHtml(title) + '</h4><ol>' +
      items.map(function (value) { return '<li>' + escapeHtml(value) + '</li>'; }).join('') + '</ol></div>';
  }

  function generateDeepProfile() {
    if (state.deepProfileGenerating) { return; }
    if (!Object.keys(completedAssessmentScaleIds()).length) {
      state.deepProfileError = '请先完成至少一份测评，再生成深度画像。';
      renderPage(pageByKey.assessment);
      return;
    }
    state.deepProfileGenerating = true;
    state.deepProfileError = null;
    renderPage(pageByKey.assessment);
    post(endpoints.deepProfileGenerate, state.identity.userId).then(function (profile) {
      state.deepProfile = profile || null;
      if (!state.deepProfile) {
        throw new Error('未返回有效的深度画像，请稍后重试。');
      }
      state.deepProfileSelected = state.deepProfile;
      state.deepProfileHistory = null;
      state.deepProfileHistoryError = null;
    }).catch(function (error) {
      state.deepProfileError = error.message || '深度画像生成失败，请稍后重试。';
    }).then(function () {
      state.deepProfileGenerating = false;
      if (state.route === 'assessment') {
        renderPage(pageByKey.assessment);
      }
    });
  }

  function startAssessmentScale(scaleId) {
    if (!scaleId) {
      return;
    }
    var previous = latestAssessmentRecord(scaleId) || latestAssessmentFromSnapshot(scaleId);
    state.assessmentSelectedScaleId = String(scaleId);
    state.assessmentScale = null;
    state.assessmentAnswers = {};
    state.assessmentCurrentIndex = 0;
    state.assessmentResult = previous;
    state.assessmentError = null;
    if (previous) {
      renderPage(pageByKey.assessment);
      if (!isFilePreview() && previous.recordId && endpoints.assessmentRecord) {
        state.assessmentLoading = true;
        post(endpoints.assessmentRecord, {
          userId: state.identity.userId,
          recordId: previous.recordId
        }).then(function (result) {
          if (result) {
            state.assessmentResult = result;
          }
        }).catch(function (error) {
          state.assessmentError = error.message || "上次测评详情暂时无法读取。";
        }).then(function () {
          state.assessmentLoading = false;
          if (state.route === "assessment") {
            renderPage(pageByKey.assessment);
          }
        });
      }
      return;
    }
    startNewAssessmentScale(scaleId);
  }

  function startNewAssessmentScale(scaleId) {
    if (!scaleId) {
      return;
    }
    state.assessmentSelectedScaleId = String(scaleId);
    state.assessmentScale = null;
    state.assessmentAnswers = {};
    state.assessmentCurrentIndex = 0;
    state.assessmentResult = null;
    state.assessmentError = null;
    if (isFilePreview()) {
      state.assessmentScale = previewAssessmentScale(scaleId);
      renderPage(pageByKey.assessment);
      return;
    }
    state.assessmentLoading = true;
    renderPage(pageByKey.assessment);
    post(endpoints.assessmentQuestions, { userId: state.identity.userId, scaleId: scaleId }).then(function (scale) {
      if (!scale || !scale.attemptId || !normalizeArray(scale.questions).length) {
        throw new Error("未能创建本次测评，请重试。");
      }
      state.assessmentScale = scale;
    }).catch(function (error) {
      state.assessmentError = error.message || "画像补全题目暂不可用，请稍后重试。";
      state.assessmentScale = null;
    }).then(function () {
      state.assessmentLoading = false;
      if (state.route === "assessment") {
        renderPage(pageByKey.assessment);
      }
    });
  }

  function backToAssessmentList() {
    state.assessmentSelectedScaleId = null;
    state.assessmentScale = null;
    state.assessmentAnswers = {};
    state.assessmentCurrentIndex = 0;
    state.assessmentResult = null;
    renderPage(pageByKey.assessment);
  }

  function assessmentQuestionCard(question, index) {
    var options = normalizeArray(question.options);
    var selected = assessmentSelectedOptionIds(question.questionId);
    var multi = isMultiAssessmentQuestion(question);
    return '<div class="route-control-card assessment-question"><div class="route-card-head"><h3>' +
      (index + 1) + ". " + escapeHtml(question.questionText) + '</h3>' +
      (multi ? '<span class="tag tag-blue">多选</span>' : '<span class="tag tag-time">单选</span>') +
      '</div>' + (multi ? '<p class="section-note">可以选择多个符合你的选项。</p>' : '') +
      '<div class="assessment-options">' +
      options.map(function (option) {
        var active = selected.indexOf(String(option.optionId)) >= 0 ? " active" : "";
        return '<button type="button" class="route-horizon-button' + active + '" data-assessment-question="' +
          escapeAttr(question.questionId) + '" data-assessment-option="' + escapeAttr(option.optionId) + '">' +
          '<strong>' + escapeHtml(option.optionLabel) + '</strong><span>' + escapeHtml(option.optionText) + '</span></button>';
      }).join("") + '</div></div>';
  }

  function chooseAssessmentOption(questionId, optionId) {
    if (!questionId || !optionId || state.assessmentSubmitting) {
      return;
    }
    var questions = normalizeArray(state.assessmentScale && state.assessmentScale.questions);
    var currentIndex = Math.max(0, Math.min(Number(state.assessmentCurrentIndex || 0), Math.max(questions.length - 1, 0)));
    var question = findAssessmentQuestion(questionId);
    if (isMultiAssessmentQuestion(question)) {
      var selected = assessmentSelectedOptionIds(questionId);
      var optionText = String(optionId);
      var index = selected.indexOf(optionText);
      if (index >= 0) {
        selected.splice(index, 1);
      } else {
        selected.push(optionText);
      }
      if (selected.length) {
        state.assessmentAnswers[String(questionId)] = selected;
      } else {
        delete state.assessmentAnswers[String(questionId)];
      }
      renderPage(pageByKey.assessment);
      return;
    }
    state.assessmentAnswers[String(questionId)] = [String(optionId)];
    if (questions.length && currentIndex < questions.length - 1) {
      state.assessmentCurrentIndex = currentIndex + 1;
    } else if (questions.length) {
      submitAssessment();
      return;
    }
    renderPage(pageByKey.assessment);
  }

  function nextAssessmentQuestion() {
    var questions = normalizeArray(state.assessmentScale && state.assessmentScale.questions);
    var currentIndex = Math.max(0, Math.min(Number(state.assessmentCurrentIndex || 0), Math.max(questions.length - 1, 0)));
    if (!questions.length || !assessmentQuestionAnswered(questions[currentIndex])) {
      return;
    }
    state.assessmentCurrentIndex = Math.min(currentIndex + 1, questions.length - 1);
    renderPage(pageByKey.assessment);
  }

  function previousAssessmentQuestion() {
    state.assessmentCurrentIndex = Math.max(0, Number(state.assessmentCurrentIndex || 0) - 1);
    renderPage(pageByKey.assessment);
  }

  function findAssessmentQuestion(questionId) {
    var questions = normalizeArray(state.assessmentScale && state.assessmentScale.questions);
    for (var i = 0; i < questions.length; i += 1) {
      if (String(questions[i].questionId) === String(questionId)) {
        return questions[i];
      }
    }
    return null;
  }

  function isMultiAssessmentQuestion(question) {
    return question && String(question.questionType || "").toUpperCase() === "MULTI";
  }

  function assessmentSelectedOptionIds(questionId) {
    var value = state.assessmentAnswers[String(questionId)];
    if (Array.isArray(value)) {
      return value.map(function (item) { return String(item); });
    }
    return value ? [String(value)] : [];
  }

  function assessmentQuestionAnswered(question) {
    return question && assessmentSelectedOptionIds(question.questionId).length > 0;
  }

  function submitAssessment() {
    var scale = state.assessmentScale;
    var questions = normalizeArray(scale && scale.questions);
    if (!scale || !scale.scaleId || !questions.length) {
      showMessage("warning", "无法提交", "画像补全题组尚未加载完成。");
      return;
    }
    if (!hasUserIdentity() || isFilePreview()) {
      state.assessmentResult = previewAssessmentResult(scale);
      state.snapshot = state.snapshot || {};
      state.snapshot.assessment = {
        scaleId: scale.scaleId,
        scaleTitle: scale.title,
        summary: state.assessmentResult.resultSummary,
        suggestedRoles: state.assessmentResult.suggestedRoles
      };
      updateOverviewCards();
      renderPage(pageByKey.assessment);
      showMessage("info", "已生成预览结果", "当前未调用受保护后端，部署后会写入青途启航职业画像。");
      return;
    }
    state.assessmentSubmitting = true;
    renderPage(pageByKey.assessment);
    post(endpoints.assessmentSubmit, {
      userId: state.identity.userId,
      scaleId: scale.scaleId,
      attemptId: scale.attemptId,
      answers: assessmentLegacyAnswersPayload(),
      answerOptionIds: assessmentAnswersPayload()
    }).then(function (result) {
      state.assessmentResult = result;
      state.assessmentRecords = [result].concat(state.assessmentRecords || []);
      return post(endpoints.snapshot, state.identity.userId).catch(function () { return state.snapshot; });
    }).then(function (snapshot) {
      if (snapshot) {
        state.snapshot = snapshot;
      }
      updateOverviewCards();
      showMessage("info", "测评已完成", "结果已写入青途启航职业画像。");
    }).catch(function (error) {
      showMessage("error", "提交失败", error.message || "画像补全提交接口暂不可用。");
    }).then(function () {
      state.assessmentSubmitting = false;
      if (state.route === "assessment") {
        renderPage(pageByKey.assessment);
      }
    });
  }

  function assessmentAnswersPayload() {
    var out = {};
    Object.keys(state.assessmentAnswers).forEach(function (key) {
      out[key] = assessmentSelectedOptionIds(key);
    });
    return out;
  }

  function assessmentLegacyAnswersPayload() {
    var out = {};
    Object.keys(state.assessmentAnswers).forEach(function (key) {
      var selected = assessmentSelectedOptionIds(key);
      if (selected.length) {
        out[key] = selected[0];
      }
    });
    return out;
  }

  function assessmentAnsweredCount(questions) {
    var count = 0;
    questions.forEach(function (question) {
      if (assessmentQuestionAnswered(question)) {
        count += 1;
      }
    });
    return count;
  }

  function assessmentResultPanel(result) {
    var summary = assessmentPlainLanguageSummary(result);
    var rawSummary = firstText(result.resultSummary, result.summary, "");
    var roles = normalizeArray(result.suggestedRoles);
    var counts = result.dimensionCounts || {};
    var rows = [
      ["你的测评结论", summary],
      ["测评名称", firstText(result.scaleTitle, getValue(state.snapshot, "assessment.scaleTitle"), "画像补全")]
    ];
    return metricsPanel("你的测评结果", rows) +
      '<section class="panel"><h3>可能适合你的方向</h3><ul class="compact-list">' +
      (roles.length ? roles : defaultAssessmentRoles(rawSummary)).map(function (role) {
        return '<li>' + escapeHtml(role) + '</li>';
      }).join("") + '</ul><p class="section-note">这些方向用于帮助你探索，不代表能力高低或唯一选择。</p>' +
      assessmentDimensionDistribution(result) + '</section>';
  }

  function assessmentPlainLanguageSummary(result) {
    var raw = firstText(result && result.resultSummary, result && result.summary, "");
    var title = firstText(result && result.scaleTitle, getValue(state.snapshot, "assessment.scaleTitle"), "");
    var scaleId = String(firstText(result && result.scaleId, state.assessmentSelectedScaleId, ""));
    var counts = result && result.dimensionCounts || {};
    if (scaleId === "1001" || /MBTI|性格倾向/i.test(title)) {
      return mbtiPlainLanguageSummary(raw, counts);
    }
    if (scaleId === "1002" || /RIASEC|职业兴趣/i.test(title)) {
      return dimensionPlainLanguageSummary(raw, counts,
        ["R", "I", "A", "S", "E", "C"],
        { R: "动手实践", I: "分析研究", A: "创意表达", S: "帮助他人", E: "推动事情和影响他人", C: "按规则整理和执行" },
        "你更容易被{items}类的工作吸引。");
    }
    if (scaleId === "1003" || /BIG5|大五人格/i.test(title)) {
      return dimensionPlainLanguageSummary(raw, counts,
        ["O", "C", "E", "A", "N"],
        { O: "喜欢新想法和新体验", C: "做事有计划并重视完成质量", E: "乐于交流和表达", A: "重视合作与他人感受", N: "对压力和负面反馈比较敏感" },
        "你比较突出的特点是{items}。");
    }
    if (scaleId === "1004" || /职业价值观/i.test(title)) {
      return dimensionPlainLanguageSummary(raw, counts,
        ["ACH", "SEC", "AUT", "SOC", "STA", "VAR"],
        { ACH: "获得成果和成就感", SEC: "稳定与安全保障", AUT: "自主安排和决定", SOC: "帮助他人并创造社会价值", STA: "平台认可和个人影响力", VAR: "工作有变化和挑战" },
        "选择工作时，你更看重{items}。");
    }
    if (scaleId === "1005" || /压力应对/i.test(title)) {
      return dimensionPlainLanguageSummary(raw, counts,
        ["PROBLEM", "EMOTION", "PLAN", "SUPPORT", "REFRAME", "AVOID"],
        { PROBLEM: "直接拆解并解决问题", EMOTION: "先照顾和稳定情绪", PLAN: "提前制定计划", SUPPORT: "主动寻求他人支持", REFRAME: "复盘并换个角度看问题", AVOID: "暂时回避、等状态恢复" },
        "面对压力时，你更常通过{items}来应对。");
    }
    return /[\u3400-\u9fff]/.test(raw) ? raw : "你已完成本次测评，可以结合下方建议进一步了解自己。";
  }

  function mbtiPlainLanguageSummary(raw, counts) {
    var code = String(raw || "").toUpperCase();
    var preferences = [
      assessmentPreference(code, 0, counts, "E", "I", "更愿意通过交流获得能量", "更喜欢通过独处恢复精力"),
      assessmentPreference(code, 1, counts, "S", "N", "更关注具体事实和实际经验", "更关注整体方向和未来可能"),
      assessmentPreference(code, 2, counts, "T", "F", "做决定时更看重逻辑和原则", "做决定时更重视感受和关系"),
      assessmentPreference(code, 3, counts, "J", "P", "做事偏好提前计划、明确安排", "做事偏好保持灵活、边走边调整")
    ];
    return "你的性格倾向是：" + preferences.join("；") + "。";
  }

  function assessmentPreference(code, index, counts, left, right, leftText, rightText) {
    var selected = /^[EISNTFJP]{4}$/.test(code) ? code.charAt(index) :
      ((Number(counts[left] || 0) >= Number(counts[right] || 0)) ? left : right);
    return selected === left ? leftText : rightText;
  }

  function dimensionPlainLanguageSummary(raw, counts, codes, labels, template) {
    var topCodes = assessmentTopDimensionCodes(raw, counts, codes);
    var items = topCodes.map(function (code) { return labels[code]; }).filter(function (value) { return value; });
    if (!items.length) return "你已完成本次测评，可以结合下方建议进一步了解自己。";
    return template.replace("{items}", joinChineseItems(items));
  }

  function assessmentTopDimensionCodes(raw, counts, codes) {
    var ranked = codes.filter(function (code) { return Object.prototype.hasOwnProperty.call(counts || {}, code); });
    ranked.sort(function (left, right) {
      var difference = Number(counts[right] || 0) - Number(counts[left] || 0);
      return difference || codes.indexOf(left) - codes.indexOf(right);
    });
    if (ranked.length) return ranked.slice(0, 3);
    var text = String(raw || "").toUpperCase();
    return codes.slice().sort(function (left, right) { return right.length - left.length; }).filter(function (code) {
      if (text.indexOf(code) < 0) return false;
      text = text.replace(code, "");
      return true;
    }).slice(0, 3);
  }

  function joinChineseItems(items) {
    if (items.length < 2) return items[0] || "";
    if (items.length === 2) return items[0] + "和" + items[1];
    return items.slice(0, -1).join("、") + "和" + items[items.length - 1];
  }

  function assessmentDimensionDistribution(result) {
    var counts = result && result.dimensionCounts || {};
    var labels = assessmentDimensionLabels(result);
    var items = Object.keys(counts || {}).map(function (code) {
      return { code: code, count: Number(counts[code] || 0), label: labels[code] || "其他倾向" };
    }).sort(function (left, right) {
      return right.count - left.count || left.label.localeCompare(right.label, "zh-CN");
    });
    if (!items.length) return "";
    return '<div class="assessment-dimension-summary"><strong>本次选择更偏向</strong><div class="assessment-dimension-tags">' +
      items.map(function (item) {
        return '<span>' + escapeHtml(item.label) + ' ' + escapeHtml(String(item.count)) + ' 次</span>';
      }).join("") + '</div></div>';
  }

  function assessmentDimensionLabels(result) {
    var scaleId = String(firstText(result && result.scaleId, state.assessmentSelectedScaleId, ""));
    var title = firstText(result && result.scaleTitle, getValue(state.snapshot, "assessment.scaleTitle"), "");
    if (scaleId === "1001" || /MBTI|性格倾向/i.test(title)) {
      return { E: "乐于交流", I: "偏好独处", S: "关注事实", N: "关注可能",
        T: "重视逻辑", F: "重视感受", J: "偏好计划", P: "偏好灵活" };
    }
    if (scaleId === "1002" || /RIASEC|职业兴趣/i.test(title)) {
      return { R: "动手实践", I: "分析研究", A: "创意表达", S: "帮助他人",
        E: "推动与影响", C: "规则与整理" };
    }
    if (scaleId === "1003" || /BIG5|大五人格/i.test(title)) {
      return { O: "开放探索", C: "计划与责任", E: "交流表达",
        A: "合作与体谅", N: "压力敏感" };
    }
    if (scaleId === "1004" || /职业价值观/i.test(title)) {
      return { ACH: "成就感", SEC: "稳定保障", AUT: "自主空间",
        SOC: "帮助他人", STA: "平台认可", VAR: "变化挑战" };
    }
    return { PROBLEM: "解决问题", EMOTION: "调节情绪", PLAN: "制定计划",
      SUPPORT: "寻求支持", REFRAME: "换角度复盘", AVOID: "暂时回避" };
  }

  function assessmentAnswerReviewPanel(result) {
    var answers = normalizeArray(result && result.answers);
    if (!answers.length) {
      return '<section class="panel"><h3>上次答题记录</h3><p class="section-note">这条较早的测评记录没有保存题目与选项文本，仍可参考上方测评结论。</p></section>';
    }
    var grouped = [];
    var byQuestion = {};
    answers.forEach(function (answer, index) {
      if (!answer) { return; }
      var key = String(firstText(answer.questionId, answer.questionText, index));
      if (!byQuestion[key]) {
        byQuestion[key] = {
          questionText: firstText(answer.questionText, "第 " + (grouped.length + 1) + " 题"),
          optionTexts: []
        };
        grouped.push(byQuestion[key]);
      }
      var optionText = firstText(answer.optionText, answer.optionId ? "选项 " + answer.optionId : "");
      if (optionText && byQuestion[key].optionTexts.indexOf(optionText) < 0) {
        byQuestion[key].optionTexts.push(optionText);
      }
    });
    return '<section class="panel"><h3>上次答题记录</h3><p class="section-note">以下内容来自最近一次已完成测评，仅供回顾。</p>' +
      '<ol class="compact-list assessment-answer-review">' + grouped.map(function (item) {
        return '<li><strong>' + escapeHtml(item.questionText) + '</strong><span>' +
          escapeHtml(item.optionTexts.join('、') || '未记录所选项') + '</span></li>';
      }).join('') + '</ol></section>';
  }

  function latestAssessmentFromSnapshot() {
    var assessment = getValue(state.snapshot, "assessment") || null;
    if (!assessment) {
      return null;
    }
    return {
      recordId: assessment.lastRecordId,
      scaleId: assessment.scaleId,
      scaleTitle: assessment.scaleTitle,
      resultSummary: assessment.summary,
      suggestedRoles: assessment.suggestedRoles || [],
      dimensionCounts: {}
    };
  }

  function assessmentAiInterpretationPanel(result) {
    if (!result || !result.recordId) { return ''; }
    var value = result.aiInterpretation;
    var loading = String(state.assessmentAiInterpretationLoadingId || '') === String(result.recordId);
    var failed = String(state.assessmentAiInterpretationErrorRecordId || '') === String(result.recordId)
      ? state.assessmentAiInterpretationError
      : null;
    if (!value) {
      if (loading) {
        return '<section class="panel"><h3>AI 测评解读</h3><p class="section-note">正在结合本次答题与测评结果生成分析，请稍候。</p>' +
          '<button type="button" class="secondary" disabled aria-disabled="true">正在分析中...</button></section>';
      }
      if (failed) {
        return '<section class="panel"><h3>AI 测评解读</h3>' +
          '<div class="state-panel warning"><strong>AI 测评解读生成失败</strong><p>' + escapeHtml(failed) + '</p></div>' +
          '<button type="button" class="secondary" data-assessment-ai-interpretation="' + escapeAttr(result.recordId) + '">重新生成 AI 解读</button></section>';
      }
      return '<section class="panel"><h3>AI 测评解读</h3><p class="section-note">基于本次答题与测评结果生成解释性分析。</p>' +
        '<button type="button" class="secondary" data-assessment-ai-interpretation="' + escapeAttr(result.recordId) + '">生成 AI 解读</button></section>';
    }
    if (isInvalidAssessmentAiInterpretation(value)) {
      return '<section class="panel"><h3>AI 测评解读</h3>' +
        '<div class="state-panel warning"><strong>这份解读未能正确生成</strong>' +
        '<p>AI 服务返回了无法阅读的中间处理信息，系统已将其隐藏。请重新生成解读。</p></div>' +
        '<button type="button" class="secondary" data-assessment-ai-interpretation="' +
        escapeAttr(result.recordId) + '">重新生成 AI 解读</button></section>';
    }
    return '<section class="panel"><h3>AI 测评解读</h3><p>' + escapeHtml(value.summary || '') + '</p>' +
      renderSimpleNumberedList('本次观察', value.insights) +
      renderSimpleNumberedList('建议行动', value.suggestions) +
      renderSimpleNumberedList('仍需补充的信息', value.dataGaps) + '</section>';
  }

  function isInvalidAssessmentAiInterpretation(value) {
    if (!value) { return false; }
    var summary = String(value.summary || '').trim();
    if (!summary) { return true; }
    return /"(?:Thought|Action|Action_input|thought|action|action_input)"\s*:/.test(summary) ||
      (summary.indexOf('画像补全任务流') >= 0 && summary.indexOf('Action_input') >= 0);
  }

  function renderSimpleNumberedList(title, values) {
    var items = normalizeArray(values).filter(function (value) { return value; });
    if (!items.length) { return ''; }
    return '<div class="assessment-result-block"><h4>' + escapeHtml(title) + '</h4><ol>' +
      items.map(function (value) { return '<li>' + escapeHtml(value) + '</li>'; }).join('') + '</ol></div>';
  }

  function generateAssessmentAiInterpretation(recordId) {
    if (!recordId || !hasUserIdentity() || isFilePreview()) { return; }
    if (state.assessmentAiInterpretationLoadingId) { return; }
    state.assessmentAiInterpretationError = null;
    state.assessmentAiInterpretationErrorRecordId = null;
    state.assessmentAiInterpretationLoadingId = String(recordId);
    renderPage(pageByKey.assessment);
    post(endpoints.assessmentAiInterpretation, { userId: state.identity.userId, recordId: recordId }).then(function (result) {
      state.assessmentAiInterpretationError = null;
      state.assessmentAiInterpretationErrorRecordId = null;
      state.assessmentAiInterpretationLoadingId = null;
      state.assessmentResult = result;
      state.assessmentRecords = normalizeArray(state.assessmentRecords).map(function (record) {
        return String(record.recordId) === String(result.recordId) ? result : record;
      });
      renderPage(pageByKey.assessment);
    }).catch(function (error) {
      state.assessmentAiInterpretationLoadingId = null;
      state.assessmentAiInterpretationErrorRecordId = String(recordId);
      state.assessmentAiInterpretationError = error && error.message ? error.message : 'AI 测评解读生成失败，请稍后重试。';
      renderPage(pageByKey.assessment);
    });
  }

  function previewAssessmentScaleSummary() {
    var scale = previewAssessmentScale();
    return {
      scaleId: scale.scaleId,
      title: scale.title,
      description: scale.description,
      version: scale.version,
      questionCount: scale.questions.length
    };
  }

  function previewAssessmentScale() {
    var questions = [
      ["1", "在社交场合中，你通常会：", [["11", "A", "主动认识新朋友", "E"], ["12", "B", "和少数熟人交流", "I"]]],
      ["2", "学习新内容时，你更喜欢：", [["21", "A", "按步骤掌握细节", "S"], ["22", "B", "先看整体图景", "N"]]],
      ["3", "做决定时，你更看重：", [["31", "A", "逻辑和事实", "T"], ["32", "B", "人的感受", "F"]]],
      ["4", "面对截止日期，你通常：", [["41", "A", "提前完成", "J"], ["42", "B", "临近时效率更高", "P"]]]
    ];
    return {
      scaleId: 1001,
      title: "MBTI 职业性格测评",
      description: "预览题库，部署后使用后端 16 题完整题库。",
      version: "preview",
      questions: questions.map(function (row, index) {
        return {
          questionId: row[0],
          questionText: row[1],
          sortOrder: index + 1,
          options: row[2].map(function (option) {
            return { optionId: option[0], optionLabel: option[1], optionText: option[2], dimensionCode: option[3] };
          })
        };
      })
    };
  }

  function previewAssessmentResult(scale) {
    var counts = {};
    normalizeArray(scale.questions).forEach(function (question) {
      var selected = assessmentSelectedOptionIds(question.questionId);
      normalizeArray(question.options).forEach(function (option) {
        if (selected.indexOf(String(option.optionId)) >= 0 && option.dimensionCode) {
          counts[option.dimensionCode] = (counts[option.dimensionCode] || 0) + 1;
        }
      });
    });
    var summary = (counts.E >= counts.I ? "E" : "I") +
      (counts.S >= counts.N ? "S" : "N") +
      (counts.T >= counts.F ? "T" : "F") +
      (counts.J >= counts.P ? "J" : "P");
    return {
      recordId: "preview",
      scaleId: scale.scaleId,
      scaleTitle: scale.title,
      status: "COMPLETED",
      resultSummary: summary,
      dimensionCounts: counts,
      suggestedRoles: defaultAssessmentRoles(summary)
    };
  }

  function defaultAssessmentRoles(summary) {
    if (summary.indexOf("N") >= 0 && summary.indexOf("T") >= 0) {
      return ["产品经理", "数据分析师", "后端开发工程师"];
    }
    if (summary.indexOf("F") >= 0) {
      return ["用户研究员", "人力资源专员", "客户成功顾问"];
    }
    return ["软件工程师", "业务分析师", "解决方案顾问"];
  }

  function latestAssessmentFromSnapshot(scaleId) {
    var assessment = getValue(state.snapshot, "assessment") || null;
    if (!assessment) {
      return null;
    }
    if (scaleId && assessment.scaleId && String(scaleId) !== String(assessment.scaleId)) {
      return null;
    }
    return {
      recordId: assessment.lastRecordId,
      scaleId: assessment.scaleId,
      scaleTitle: assessment.scaleTitle,
      resultSummary: assessment.summary,
      suggestedRoles: assessment.suggestedRoles || [],
      dimensionCounts: {}
    };
  }

  function selectedAssessmentScaleSummary() {
    var scales = normalizeArray(state.assessmentScales);
    for (var i = 0; i < scales.length; i += 1) {
      if (String(scales[i].scaleId) === String(state.assessmentSelectedScaleId)) {
        return scales[i];
      }
    }
    return null;
  }

  function completedAssessmentScaleIds() {
    var out = {};
    normalizeArray(state.assessmentRecords).forEach(function (record) {
      if (record && record.scaleId) {
        out[String(record.scaleId)] = true;
      }
    });
    var assessment = getValue(state.snapshot, "assessment") || {};
    if (assessment.scaleId) {
      out[String(assessment.scaleId)] = true;
    }
    return out;
  }

  function latestAssessmentRecord(scaleId) {
    var records = normalizeArray(state.assessmentRecords);
    for (var index = 0; index < records.length; index += 1) {
      var record = records[index];
      if (record && record.scaleId && String(record.scaleId) === String(scaleId)) {
        return record;
      }
    }
    return null;
  }

  function estimateAssessmentMinutes(scale) {
    var count = Number(firstText(scale && scale.questionCount, normalizeArray(scale && scale.questions).length, 0));
    return Math.max(1, Math.round(count * 12 / 60));
  }

  function previewAssessmentScaleSummaries() {
    return ["1001", "1002", "1003", "1004", "1005"].map(function (scaleId) {
      var scale = previewAssessmentScale(scaleId);
      return {
        scaleId: scale.scaleId,
        title: scale.title,
        description: scale.description,
        version: scale.version,
        questionCount: scale.questions.length
      };
    });
  }

  function previewAssessmentScale(scaleId) {
    var id = String(scaleId || state.assessmentSelectedScaleId || "1001");
    var meta = previewAssessmentMeta()[id] || previewAssessmentMeta()["1001"];
    return {
      scaleId: Number(id),
      title: meta[0],
      description: meta[1],
      version: "preview",
      questions: meta[2].map(function (row, index) {
        return {
          questionId: id + row[0],
          questionText: row[1],
          sortOrder: index + 1,
          options: row[2].map(function (option) {
            return { optionId: id + option[0], optionLabel: option[1], optionText: option[2], dimensionCode: option[3] };
          })
        };
      })
    };
  }

  function previewAssessmentMeta() {
    return {
      "1001": ["性格倾向测评(MBTI)", "探索你在四个维度上的性格倾向，帮助了解自己的思维与决策风格。", [
        ["1", "在社交场合中，你通常会：", [["11", "A", "主动认识新朋友", "E"], ["12", "B", "和少数熟人交流", "I"]]],
        ["2", "学习新内容时，你更喜欢：", [["21", "A", "按步骤掌握细节", "S"], ["22", "B", "先看整体图景", "N"]]],
        ["3", "做决定时，你更看重：", [["31", "A", "逻辑和事实", "T"], ["32", "B", "人的感受", "F"]]],
        ["4", "面对截止日期，你通常：", [["41", "A", "提前完成", "J"], ["42", "B", "临近时效率更高", "P"]]]
      ]],
      "1002": ["RIASEC职业兴趣", "Holland职业兴趣测评，探索你的实际型、研究型、艺术型、社会型、企业型、常规型倾向。", [
        ["1", "更吸引你的任务是：", [["101", "A", "修理设备或处理工具", "R"], ["102", "B", "分析问题或验证假设", "I"]]],
        ["2", "你更愿意参与：", [["201", "A", "创意表达或设计内容", "A"], ["202", "B", "帮助他人或教学辅导", "S"]]],
        ["3", "团队中你更自然承担：", [["301", "A", "推动决策和组织行动", "E"], ["302", "B", "整理资料和检查细节", "C"]]]
      ]],
      "1003": ["大五人格(BIG5)", "测量开放性、尽责性、外向性、宜人性、神经质五大人格维度。", [
        ["1", "你更像是：", [["101", "A", "喜欢新想法和跨界探索", "O"], ["102", "B", "重视计划和完成质量", "C"]]],
        ["2", "别人更常说你：", [["201", "A", "外向、活跃", "E"], ["202", "B", "温和、配合", "A"]]],
        ["3", "压力来临时你通常：", [["301", "A", "情绪波动明显", "N"], ["302", "B", "先拆任务再推进", "C"]]]
      ]],
      "1004": ["职业价值观", "了解你最看重的职业价值维度：成就感、安全感、自主性、社会服务、地位声望、多样挑战。", [
        ["1", "你更看重工作带来：", [["101", "A", "明确成果和成长成就", "ACH"], ["102", "B", "稳定收入和安全边界", "SEC"]]],
        ["2", "你更希望拥有：", [["201", "A", "自主安排和决策空间", "AUT"], ["202", "B", "帮助他人和社会价值", "SOC"]]],
        ["3", "更吸引你的是：", [["301", "A", "更高平台或行业认可", "STA"], ["302", "B", "不同任务和持续挑战", "VAR"]]]
      ]],
      "1005": ["压力应对测评", "评估你在压力情境中的应对风格和情绪调节模式。", [
        ["1", "压力出现时，你通常先：", [["101", "A", "拆解问题并找行动", "PROBLEM"], ["102", "B", "先处理情绪和感受", "EMOTION"]]],
        ["2", "遇到不确定任务，你更倾向于：", [["201", "A", "列计划控制节奏", "PLAN"], ["202", "B", "找人讨论获得支持", "SUPPORT"]]],
        ["3", "当结果不理想时，你更常：", [["301", "A", "复盘并重新解释问题", "REFRAME"], ["302", "B", "暂时回避等状态恢复", "AVOID"]]]
      ]]
    };
  }

  function previewAssessmentResult(scale) {
    var counts = {};
    normalizeArray(scale.questions).forEach(function (question) {
      var selected = assessmentSelectedOptionIds(question.questionId);
      normalizeArray(question.options).forEach(function (option) {
        if (selected.indexOf(String(option.optionId)) >= 0 && option.dimensionCode) {
          counts[option.dimensionCode] = (counts[option.dimensionCode] || 0) + 1;
        }
      });
    });
    var isMbti = String(scale.title || "").toUpperCase().indexOf("MBTI") >= 0;
    var summary = isMbti
      ? (counts.E >= counts.I ? "E" : "I") +
        (counts.S >= counts.N ? "S" : "N") +
        (counts.T >= counts.F ? "T" : "F") +
        (counts.J >= counts.P ? "J" : "P")
      : topAssessmentDimensions(counts);
    return {
      recordId: "preview",
      scaleId: scale.scaleId,
      scaleTitle: scale.title,
      status: "COMPLETED",
      resultSummary: summary,
      dimensionCounts: counts,
      suggestedRoles: defaultAssessmentRoles(summary)
    };
  }

  function topAssessmentDimensions(counts) {
    var keys = Object.keys(counts || {});
    keys.sort(function (left, right) {
      var diff = (counts[right] || 0) - (counts[left] || 0);
      return diff || left.localeCompare(right);
    });
    return keys.slice(0, 3).join("") || "N/A";
  }

  function defaultAssessmentRoles(summary) {
    if (summary.indexOf("N") >= 0 && summary.indexOf("T") >= 0) {
      return ["产品经理", "数据分析师", "后端开发工程师"];
    }
    if (summary.indexOf("F") >= 0 || summary.indexOf("S") >= 0) {
      return ["用户研究员", "人力资源专员", "客户成功顾问"];
    }
    return ["软件工程师", "业务分析师", "解决方案顾问"];
  }

  function renderContractPage(item) {
    var endpointRows = item.endpoints.map(function (name) {
      return ["WebAPI", endpoints[name] || name];
    });
    var body = "";
    if (isDebugMode()) {
      body = metricsPanel("接口契约", endpointRows.concat([
        ["页面状态", item.status],
        ["身份要求", item.audience === "public" ? "不要求 userId" : item.audience === "admin" ? "ADMIN" : "Cosmic userId"],
        ["降级策略", fallbackText(item)]
      ]));
    }

    if (item.key === "resume") {
      body += listPanel("简历记录", state.resumes, "暂无简历记录，仍可保留创建和文件入口。");
    } else if (item.key === "interview") {
      body += listPanel("面试历史", state.interviews, "暂无面试历史。");
    } else if (item.key === "assessment") {
      body += statePanel("画像补全", "后续通过答题补充人格、性格、偏好和行动风格，并把结果写入完整自画像，用于指导路径规划和今日行动。当前先保留入口，不展开题组。", "pending");
    } else if (item.key === "assistant") {
      body += renderAgentAssistantPanel();
      body += statePanel("助手会话", "仍保留发送消息和会话列表接口契约，便于后续把平台智能体结果同步回青途启航会话记录。", "pending");
    } else if (item.key === "messages") {
      body += statePanel("站内消息", "消息列表、未读数、已读和订阅配额契约已映射；微信真实发送暂不迁移。", "pending");
    } else if (item.key === "file-upload-preview") {
      body += statePanel("文件服务调试", isDebugMode() ? "上传、预览、下载、删除和文本抽取走 Cosmic 文件 adapter；disabled 时显示 unavailable。" : "这是文件服务调试页。普通用户请在简历页上传和预览 PDF；开发排查请使用 ?ccDebug=1。", isDebugMode() ? "pending" : "warning");
    } else if (item.key === "admin-console") {
      body += statePanel("管理员边界", "仅 ADMIN 或平台管理员身份可访问，不使用硬编码 adminId。", "warning");
    } else if (item.status === "entry-only") {
      body += statePanel("入口状态", "当前展示契约入口和安全降级，完整交互由后续页面细化。", "warning");
    } else if (!body) {
      body += statePanel(item.title, item.summary, "empty");
    }
    renderShell(detailItem, body);
  }

  function renderAgentAssistantPanel() {
    if (!agentAssistant.h5Url) {
      return statePanel("金蝶智能体助手", "暂未配置金蝶平台 H5 助手地址。", "warning");
    }
    return '<section class="panel full"><div class="section-heading"><div><h3>金蝶智能体助手</h3>' +
      '<p class="section-note">' + escapeHtml(agentAssistant.statusNote) + '</p></div>' +
      '<a class="secondary" href="' + escapeAttr(agentAssistant.h5Url) + '" target="_blank" rel="noopener">打开助手</a></div>' +
      '<iframe title="金蝶智能体助手" src="' + escapeAttr(agentAssistant.h5Url) + '" ' +
      'style="width:100%;min-height:640px;border:1px solid var(--border);border-radius:8px;background:#fff;"></iframe></section>';
  }

  function renderInterviewPage(item) {
    if (state.interviewViewMode === "transcript") {
      var transcriptBody = state.interviewError ? statePanel("问答记录暂时无法读取", state.interviewError, "warning") : "";
      transcriptBody += renderInterviewTranscript(); renderShell(item, transcriptBody); return;
    }
    var body = "";
    if (state.interviewReport) {
      body += renderInterviewReport(state.interviewReport);
    } else if (state.activeInterview) {
      body += renderInterviewRoom();
    } else {
      body += renderInterviewSetup();
    }
    renderShell(item, body);
  }

  function renderPanoramaInterviewPage(item) {
    if (state.panoramaViewMode === "transcript" && state.panoramaSession) {
      renderShell(item, renderPanoramaTranscript()); return;
    }
    var body = state.panoramaReport ? renderPanoramaReport() : state.panoramaSession ? renderPanoramaRoom() : renderPanoramaPreparation();
    renderShell(item, body);
    attachPanoramaCamera();
    if (state.panoramaSession && state.panoramaQuestion && !state.panoramaReport && !state.panoramaBusy) setTimeout(function () { speakPanoramaQuestion(false); }, 0);
  }

  function renderPanoramaPreparation() {
    var role = firstText(getValue(state.snapshot, "preferences.targetRole"), getValue(state.snapshot, "resume.targetJob"), "");
    var resumes = normalizeArray(state.resumes);
    var cameraReady = !!state.panoramaStream;
    var cameraFallback = state.panoramaCameraState === "fallback";
    return '<section class="panorama-experience panorama-preparation full"><div class="panorama-overlay">' +
      '<div class="panorama-topbar"><div><span class="resource-type">沉浸式练习</span><h3>全景仿真面试</h3>' +
      '<p>面试画面只在当前浏览器中实时预览，不会上传或保存视频。</p></div>' +
      '<button type="button" class="secondary" data-link="interview-panorama-history">查看面试记录</button></div>' +
      '<div class="panorama-setup-grid"><div class="panorama-camera-card">' +
      (cameraReady ? '<video id="panoramaCamera" autoplay muted playsinline aria-label="摄像头实时预览"></video>' :
        '<div class="panorama-camera-placeholder"><span class="camera-glyph">摄</span><strong>' +
        (state.panoramaCameraState === "requesting" ? "正在请求摄像头权限" : cameraFallback ? "无摄像头模式" : "启用摄像头后可预览面试画面") + '</strong></div>') +
      '<span class="panorama-privacy-badge">' + (cameraFallback ? "摄像头未连接" : "仅本地预览") + '</span></div>' +
      '<div class="panorama-settings"><label>目标岗位<input id="panoramaPosition" value="' + escapeAttr(role) + '" placeholder="例如：产品经理"></label>' +
      '<label>使用的简历<select id="panoramaResume"><option value="">暂不使用简历</option>' + resumes.map(function (resume) {
        return '<option value="' + escapeAttr(resume.resumeId) + '">' + escapeHtml(firstText(resume.title, "简历 " + resume.resumeId)) + '</option>';
      }).join("") + '</select></label>' +
      '<label>练习难度<select id="panoramaDifficulty"><option value="Easy">入门</option><option value="Normal" selected>常规</option><option value="Hard">进阶</option></select></label>' +
      (state.panoramaError ? '<p class="panorama-error">' + escapeHtml(state.panoramaError) + '</p>' : '') +
      (state.panoramaNotice ? '<p class="panorama-notice">' + escapeHtml(state.panoramaNotice) + '</p>' : '') +
      renderPanoramaMediaDiagnostics() +
      '<div class="actions-row"><button type="button" data-panorama-action="camera" ' + (state.panoramaCameraState === "requesting" ? "disabled" : "") + '>' +
      (cameraReady ? "重新连接摄像头" : "启用摄像头和麦克风") + '</button>' +
      ((!cameraReady && !cameraFallback && state.panoramaCameraState !== "requesting") ? '<button type="button" class="secondary" data-panorama-action="fallback">无摄像头继续</button>' : '') +
      '<button type="button" data-panorama-action="start" ' + ((!cameraReady && !cameraFallback) || state.panoramaBusy ? "disabled" : "") + '>' +
      (state.panoramaBusy ? "正在准备面试" : "进入面试房间") + '</button></div></div></div></div></section>';
  }

  function renderPanoramaMediaDiagnostics() {
    var items = normalizeArray(state.panoramaMediaDiagnostics);
    if (!items.length) return "";
    return '<div class="panorama-diagnostic"><strong>摄像头和麦克风检查</strong><ul>' +
      items.map(function (item) { return '<li>' + escapeHtml(item) + '</li>'; }).join("") +
      '</ul></div>';
  }

  function renderPanoramaRoom() {
    var transcript = state.panoramaTranscript || "";
    var questionNumber = state.panoramaAnswerCount + 1;
    var speechText = state.panoramaSpeaking ? "AI 面试官正在提问，请听完后回答" :
      (state.panoramaAnswering ? "题目已读完，语音输入已自动开始，请回答" :
      (state.panoramaSpeechSupported === false ? "当前浏览器可能无法朗读题目，请点击播放题目" : "请听 AI 面试官读题，读完后将自动开始语音输入"));
    return '<section class="panorama-experience panorama-live full"><div class="panorama-overlay">' +
      '<div class="panorama-question panorama-question-audio"><span>' + questionNumber + '</span><p id="panoramaSpeechStatus">' + escapeHtml(speechText) + '</p>' +
      '<button type="button" class="secondary" data-panorama-action="speak">播放题目</button></div>' +
      '<div class="panorama-live-stage"><div id="panoramaInterviewer" class="panorama-ai-presence' + (state.panoramaSpeaking ? " speaking" : "") + '"><img src="assets/images/ai-interviewer-human-v1.png" alt="AI 面试官人物形象"><div class="panorama-speaking-rings" aria-hidden="true"></div><div class="panorama-ai-caption"><strong>AI 面试官</strong><small id="panoramaInterviewerStatus">' + (state.panoramaSpeaking ? "正在读出本题" : "正在与你面对面交流") + '</small></div></div>' +
      '<div class="panorama-video-frame">' + (state.panoramaStream ? '<video id="panoramaCamera" autoplay muted playsinline aria-label="摄像头实时预览"></video>' :
      '<div class="panorama-camera-off"><span>摄像头未连接</span><strong>你仍可继续完成 AI 面试</strong></div>') +
      '<span class="panorama-live-badge">' + (state.panoramaStream ? "摄像头已连接 · 仅本地预览" : "无摄像头模式") + '</span></div></div>' +
      '<div class="panorama-answer-panel"><div class="panorama-timer"><small>剩余时间</small><strong id="panoramaTimer">' + formatPanoramaTime(state.panoramaSeconds) + '</strong></div>' +
      '<label>回答内容（可修改）<textarea id="panoramaAnswer" rows="3" placeholder="AI 读完题目后会自动开始语音输入；识别文字可继续修改">' + escapeHtml(transcript) + '</textarea></label>' +
      '<div class="actions-row"><button type="button" data-panorama-action="listen" ' + (state.panoramaBusy || state.panoramaSpeaking || state.panoramaAnswering ? "disabled" : "") + '>' +
      (state.panoramaSpeaking ? "听题中" : (state.panoramaAnswering ? "正在录音" : "重新开启语音输入")) + '</button>' +
      '<button type="button" data-panorama-action="answer" ' + (state.panoramaBusy ? "disabled" : "") + '>回答完毕</button>' +
      '<button type="button" class="secondary" data-panorama-action="finish" ' + (state.panoramaBusy ? "disabled" : "") + '>结束面试</button></div>' +
      (state.panoramaError ? '<p class="panorama-error">' + escapeHtml(state.panoramaError) + '</p>' : '') +
      (state.panoramaNotice ? '<p class="panorama-notice">' + escapeHtml(state.panoramaNotice) + '</p>' : '') + '</div></div></section>';
  }

  function renderPanoramaReport() {
    var report = state.panoramaReport || {};
    return '<section class="panel full interview-report panorama-report"><h3>全景仿真面试复盘</h3>' +
      '<div class="metric-value">' + escapeHtml(firstText(report.overallScore, 0)) + ' 分</div><p>' +
      escapeHtml(firstText(report.textSummary, "本次全景仿真面试已经完成。")) + '</p>' +
      '<div class="actions-row"><button type="button" data-panorama-action="reset">再练一次</button>' +
      '<button type="button" class="secondary" data-link="interview-panorama-history">查看全景仿真面试记录</button></div></section>';
  }

  function renderInterviewSetup() {
    var role = firstText(getValue(state.snapshot, "preferences.targetRole"), getValue(state.snapshot, "resume.targetJob"), "");
    var resumes = normalizeArray(state.resumes);
    var draftRole = firstText(state.interviewSetupPosition, role);
    var draftResumeId = String(firstText(state.interviewSetupResumeId, ""));
    var draftDifficulty = firstText(state.interviewSetupDifficulty, "Normal");
    return '<section class="panel full interview-setup"><h3>开始一次模拟面试</h3>' +
      '<p>系统会结合目标岗位、你选择的简历和个人情况提问。当前提供文字练习。</p>' +
      '<label>目标岗位<input id="interviewPosition" value="' + escapeAttr(draftRole) + '" placeholder="例如：后端开发工程师"></label>' +
      '<label>使用的简历<select id="interviewResume"><option value="">暂不使用简历</option>' + resumes.map(function (resume) {
        var selected = String(resume.resumeId) === draftResumeId ? " selected" : "";
        return '<option value="' + escapeAttr(resume.resumeId) + '"' + selected + '>' + escapeHtml(firstText(resume.title, "简历 " + resume.resumeId)) + '</option>';
      }).join("") + '</select></label>' +
      '<label>练习难度<select id="interviewDifficulty"><option value="Easy"' + (draftDifficulty === "Easy" ? " selected" : "") + '>入门</option><option value="Normal"' + (draftDifficulty === "Normal" ? " selected" : "") + '>常规</option><option value="Hard"' + (draftDifficulty === "Hard" ? " selected" : "") + '>进阶</option></select></label>' +
      '<div class="actions-row interview-entry-actions"><button type="button" data-interview-action="start" ' + (state.interviewBusy ? 'disabled' : '') + '>' +
      (state.interviewBusy ? "正在准备" : "开始练习") + '</button>' +
      '<button type="button" class="secondary" data-link="interview-history">查看面试记录</button></div></section>';
  }

  function renderInterviewRoom() {
    var session = state.activeInterview;
    var questionNumber = Math.min(state.interviewAnswerCount + 1, 7);
    return '<section class="ai-question-workspace full"><div class="ai-question-card">' +
      '<div class="ai-question-head"><span>面试题目</span><strong>' + questionNumber + '/7</strong></div>' +
      '<blockquote>' + escapeHtml(firstText(state.interviewCurrentQuestion, "正在准备问题……")) + '</blockquote>' +
      '<p class="question-role">目标岗位：' + escapeHtml(firstText(session.positionName, "待确认岗位")) + '</p></div>' +
      '<div class="ai-answer-card"><h3>回答当前问题</h3><p>可使用语音转文字，也可以直接输入。提交后进入下一题。</p>' +
      '<button type="button" class="voice-answer-button' + (state.interviewListening ? ' listening' : '') + '" data-interview-action="speech" ' +
      (state.interviewBusy ? 'disabled' : '') + '><span>麦</span>' + (state.interviewListening ? "正在识别，点击停止" : "开始语音回答") + '</button>' +
      '<label>回答内容<textarea id="interviewAnswer" rows="6" placeholder="请回答当前问题；语音识别结果会显示在这里">' + escapeHtml(state.interviewDraft || "") + '</textarea></label>' +
      (state.interviewError ? '<p class="interview-inline-error">' + escapeHtml(state.interviewError) + '</p>' : '') +
      '<div class="actions-row"><button type="button" data-interview-action="answer" ' + (state.interviewBusy ? 'disabled' : '') + '>' +
      (state.interviewAnswerCount >= 6 ? "提交并生成复盘" : "提交回答，进入下一题") + '</button>' +
      '<button type="button" class="secondary" data-interview-action="finish" ' + (state.interviewBusy ? 'disabled' : '') + '>提前结束并评分</button>' +
      '</div></div></section>';
  }

  function renderInterviewReport(report) {
    var radar = report.radarScore || {};
    return '<section class="panel full interview-report ai-score-report"><div class="score-report-head"><div><span class="resource-type">面试结果</span><h3>本次 AI 模拟面试复盘</h3></div>' +
      '<div class="score-ring"><strong>' + escapeHtml(firstText(report.overallScore, 0)) + '</strong><span>分</span></div></div>' +
      '<p class="score-summary">' + escapeHtml(firstText(report.textSummary, "复盘已经生成。")) + '</p>' +
      '<div class="score-dimensions">' + scoreDimension("表达清晰度", radar.expression) + scoreDimension("思路条理", radar.logic) +
      scoreDimension("岗位能力", radar.technical) + scoreDimension("临场应对", radar.pressureResistance) + scoreDimension("沟通效果", radar.communication) + '</div>' +
      '<div class="report-advice-grid">' + renderAdviceList("做得好的地方", report.strengths, "完成更多回答后会出现具体评价。", "strength") +
      renderAdviceList("改进方向", report.improvements, "暂无改进建议。", "improvement") + '</div>' +
      '<div class="actions-row"><button type="button" data-interview-action="reset">再练一次</button>' +
      '<button type="button" class="secondary" data-interview-action="current-transcript">查看本次问答</button>' +
      '<button type="button" class="secondary" data-link="interview-history">查看 AI 模拟面试记录</button></div></section>';
  }

  function scoreDimension(label, value) {
    var score = Math.max(0, Math.min(100, Number(value) || 0));
    return '<div class="score-dimension"><div><span>' + escapeHtml(label) + '</span><strong>' + score + '</strong></div><i><b style="width:' + score + '%"></b></i></div>';
  }

  function renderAdviceList(title, items, emptyText, tone) {
    var list = normalizeArray(items);
    return '<section class="report-advice ' + tone + '"><h3>' + escapeHtml(title) + '</h3>' + (list.length ? list.map(function (item) {
      return '<article><strong>' + escapeHtml(firstText(item.title, title)) + '</strong><p>' + escapeHtml(firstText(item.detail, item.summary, "")) + '</p></article>';
    }).join("") : '<p>' + escapeHtml(emptyText) + '</p>') + '</section>';
  }

  function renderInterviewHistoryPage(item) {
    if (!state.interviewHistoryPage && !state.interviewHistoryLoading && !state.interviewHistoryError) {
      loadInterviewHistoryPage(state.interviewHistoryPageNumber, false);
    }
    var body = "";
    if (state.interviewHistoryLoading) {
      body += statePanel("正在读取面试记录", "请稍候，正在加载当前页。", "pending");
    } else if (state.interviewHistoryError) {
      body += statePanel("面试记录暂时无法读取", state.interviewHistoryError, "warning");
    } else {
      var page = state.interviewHistoryPage || {};
      body += renderInterviewHistoryList("", normalizeArray(page.items),
        "还没有 AI 模拟面试记录。完成一次回答后，这里会保留复盘。", "interview");
      body += renderInterviewHistoryPager(page);
    }
    renderShell(item, body);
  }

  function renderInterviewHistoryPager(page) {
    if (window.CYANCRUISE_COMPONENTS && window.CYANCRUISE_COMPONENTS.pager
        && typeof window.CYANCRUISE_COMPONENTS.pager.render === "function") {
      return window.CYANCRUISE_COMPONENTS.pager.render(page, {
        ariaLabel: "面试记录分页",
        actionAttr: "data-interview-action",
        actionName: "history-page"
      });
    }
    var current = Math.max(1, Number(page.page) || 1);
    var totalPages = Math.max(1, Number(page.totalPages) || 0);
    var total = Math.max(0, Number(page.total) || 0);
    return '<nav class="interview-history-pager full" aria-label="面试记录分页"><span>共 ' + total + ' 条 · 第 ' + current + ' / ' + totalPages + ' 页</span><div class="actions-row compact">' +
      '<button type="button" class="secondary" data-interview-action="history-page" data-page="' + (current - 1) + '" ' + (current <= 1 ? 'disabled' : '') + '>上一页</button>' +
      '<button type="button" class="secondary" data-interview-action="history-page" data-page="' + (current + 1) + '" ' + (current >= totalPages ? 'disabled' : '') + '>下一页</button></div></nav>';
  }

  function loadInterviewHistoryPage(pageNumber, renderLoading) {
    var safePage = Math.max(1, Number(pageNumber) || 1);
    state.interviewHistoryPageNumber = safePage; state.interviewHistoryLoading = true; state.interviewHistoryError = null;
    if (renderLoading && state.route === "interview-history") renderPage(pageByKey["interview-history"]);
    var request;
    if (isFilePreview()) {
      var all = normalizeArray(state.interviews).filter(isAiInterview); var from = (safePage - 1) * 10;
      request = Promise.resolve({ items: all.slice(from, from + 10), page: safePage, size: 10, total: all.length, totalPages: Math.ceil(all.length / 10) });
    } else {
      request = loadInterviewHistoryByService(safePage, "TEXT");
    }
    return request.then(function (result) {
      state.interviewHistoryPage = result || { items: [], page: safePage, size: 10, total: 0, totalPages: 0 };
      state.interviewHistoryPageNumber = Number(state.interviewHistoryPage.page) || safePage;
    }).catch(function (error) {
      state.interviewHistoryError = error.message || "面试记录暂时无法读取，请稍后重试。";
    }).then(function () {
      state.interviewHistoryLoading = false;
      if (state.route === "interview-history") renderPage(pageByKey["interview-history"]);
    });
  }

  function renderPanoramaHistoryPage(item) {
    if (!state.panoramaHistoryPage && !state.panoramaHistoryLoading && !state.panoramaHistoryError) {
      loadPanoramaHistoryPage(state.panoramaHistoryPageNumber, false);
    }
    var body = "";
    if (state.panoramaHistoryLoading) {
      body += statePanel("正在读取全景面试记录", "请稍候，正在加载当前页。", "pending");
    } else if (state.panoramaHistoryError) {
      body += statePanel("全景面试记录暂时无法读取", state.panoramaHistoryError, "warning");
    } else {
      var page = state.panoramaHistoryPage || {};
      body += renderInterviewHistoryList("", normalizeArray(page.items),
        "还没有全景仿真面试记录。完成一次练习后，这里会保留结果。", "interview-panorama");
      body += renderPanoramaHistoryPager(page);
    }
    renderShell(item, body);
  }

  function renderPanoramaHistoryPager(page) {
    if (window.CYANCRUISE_COMPONENTS && window.CYANCRUISE_COMPONENTS.pager
        && typeof window.CYANCRUISE_COMPONENTS.pager.render === "function") {
      return window.CYANCRUISE_COMPONENTS.pager.render(page, {
        ariaLabel: "全景面试记录分页",
        actionAttr: "data-panorama-action",
        actionName: "history-page"
      });
    }
    var current = Math.max(1, Number(page.page) || 1);
    var totalPages = Math.max(1, Number(page.totalPages) || 0);
    var total = Math.max(0, Number(page.total) || 0);
    return '<nav class="interview-history-pager full" aria-label="全景面试记录分页"><span>共 ' + total + ' 条 · 第 ' + current + ' / ' + totalPages + ' 页</span><div class="actions-row compact">' +
      '<button type="button" class="secondary" data-panorama-action="history-page" data-page="' + (current - 1) + '" ' + (current <= 1 ? 'disabled' : '') + '>上一页</button>' +
      '<button type="button" class="secondary" data-panorama-action="history-page" data-page="' + (current + 1) + '" ' + (current >= totalPages ? 'disabled' : '') + '>下一页</button></div></nav>';
  }

  function loadPanoramaHistoryPage(pageNumber, renderLoading) {
    var safePage = Math.max(1, Number(pageNumber) || 1);
    state.panoramaHistoryPageNumber = safePage; state.panoramaHistoryLoading = true; state.panoramaHistoryError = null;
    if (renderLoading && state.route === "interview-panorama-history") renderPage(pageByKey["interview-panorama-history"]);
    var request;
    if (isFilePreview()) {
      var all = normalizeArray(state.interviews).filter(isPanoramaInterview); var from = (safePage - 1) * 10;
      request = Promise.resolve({ items: all.slice(from, from + 10), page: safePage, size: 10, total: all.length, totalPages: Math.ceil(all.length / 10) });
    } else {
      request = loadInterviewHistoryByService(safePage, "VOICE");
    }
    return request.then(function (result) {
      state.panoramaHistoryPage = result || { items: [], page: safePage, size: 10, total: 0, totalPages: 0 };
      state.panoramaHistoryPageNumber = Number(state.panoramaHistoryPage.page) || safePage;
    }).catch(function (error) {
      state.panoramaHistoryError = error.message || "全景面试记录暂时无法读取，请稍后重试。";
    }).then(function () {
      state.panoramaHistoryLoading = false;
      if (state.route === "interview-panorama-history") renderPage(pageByKey["interview-panorama-history"]);
    });
  }

  function loadInterviewHistoryByService(pageNumber, mode) {
    if (window.CYANCRUISE_SERVICES && window.CYANCRUISE_SERVICES.interview
        && typeof window.CYANCRUISE_SERVICES.interview.page === "function") {
      return window.CYANCRUISE_SERVICES.interview.page({
        endpoints: endpoints,
        post: post,
        userId: state.identity.userId,
        page: pageNumber,
        mode: mode
      });
    }
    return post(endpoints.interviewPage, { userId: state.identity.userId, page: pageNumber, mode: mode });
  }

  function renderInterviewHistoryList(title, history, emptyText, route) {
    return '<section class="panel full">' + (title ? '<h3>' + escapeHtml(title) + '</h3>' : '') + (history.length ? history.map(function (entry) {
      var completed = entry.status === "COMPLETED" || entry.finalScore != null || !!entry.report;
      var endedAt = formatInterviewDateTime(entry.endedAt);
      var startedAt = formatInterviewDateTime(entry.startedAt);
      var timeText = completed ? "结束时间：" + (endedAt || "时间待同步")
        : "结束时间：尚未结束" + (startedAt ? " · 开始时间：" + startedAt : "");
      return '<article class="list-row"><div><strong>' + escapeHtml(firstText(entry.positionName, "目标岗位待确认")) + '</strong><p>' +
        escapeHtml(completed ? "已完成" : "进行中") + (entry.finalScore != null ? " · " + entry.finalScore + " 分" : "") +
        '</p><p class="interview-record-time">' + escapeHtml(timeText) + '</p></div>' + renderInterviewHistoryActions(entry, completed, route) + '</article>';
    }).join("") : '<p class="empty-copy">' + escapeHtml(emptyText) + '</p>') + '</section>';
  }

  function renderInterviewHistoryActions(entry, completed, route) {
    var id = escapeAttr(entry.interviewId);
    if (route === "interview") {
      return '<div class="actions-row compact"><button type="button" class="secondary" data-interview-action="open" data-interview-id="' + id + '">' + (completed ? "查看结果" : "继续面试") + '</button>' +
        '<button type="button" class="secondary" data-interview-action="transcript" data-interview-id="' + id + '">查看问答</button>' +
        '<button type="button" class="secondary danger" data-interview-action="delete" data-interview-id="' + id + '">删除记录</button></div>';
    }
    return '<div class="actions-row compact"><button type="button" class="secondary" data-panorama-action="open-record" data-interview-id="' + id + '" data-view="result">' + (completed ? "查看结果" : "继续面试") + '</button>' +
      '<button type="button" class="secondary" data-panorama-action="open-record" data-interview-id="' + id + '" data-view="transcript">查看问答</button>' +
      '<button type="button" class="secondary danger" data-panorama-action="delete-record" data-interview-id="' + id + '">删除记录</button></div>';
  }

  function syncCompletedInterview(session, report) {
    if (!session) return;
    session.status = "COMPLETED"; session.report = report || session.report || null;
    if (!session.endedAt) session.endedAt = new Date();
    if (report && report.overallScore != null) session.finalScore = report.overallScore;
    normalizeArray(state.interviews).forEach(function (entry) {
      if (String(entry.interviewId) !== String(session.interviewId)) return;
      entry.status = "COMPLETED"; entry.report = session.report;
      if (!entry.endedAt) entry.endedAt = session.endedAt;
      if (session.finalScore != null) entry.finalScore = session.finalScore;
    });
    normalizeArray(state.interviewHistoryPage && state.interviewHistoryPage.items).forEach(function (entry) {
      if (String(entry.interviewId) !== String(session.interviewId)) return;
      entry.status = "COMPLETED"; entry.report = session.report;
      if (!entry.endedAt) entry.endedAt = session.endedAt;
      if (session.finalScore != null) entry.finalScore = session.finalScore;
    });
    normalizeArray(state.panoramaHistoryPage && state.panoramaHistoryPage.items).forEach(function (entry) {
      if (String(entry.interviewId) !== String(session.interviewId)) return;
      entry.status = "COMPLETED"; entry.report = session.report;
      if (!entry.endedAt) entry.endedAt = session.endedAt;
      if (session.finalScore != null) entry.finalScore = session.finalScore;
    });
  }

  function formatInterviewDateTime(value) {
    if (!value) return "";
    var parts;
    if (Array.isArray(value) && value.length >= 5) {
      parts = value;
    } else if (typeof value === "object" && value.year != null) {
      parts = [value.year, firstText(value.monthValue, value.month, 1), value.dayOfMonth, value.hour, value.minute];
    } else {
      var matched = String(value).match(/^(\d{4})-(\d{1,2})-(\d{1,2})[T\s](\d{1,2}):(\d{1,2})/);
      if (matched) parts = [matched[1], matched[2], matched[3], matched[4], matched[5]];
    }
    if (!parts) {
      var parsed = new Date(value);
      if (isNaN(parsed.getTime())) return "";
      parts = [parsed.getFullYear(), parsed.getMonth() + 1, parsed.getDate(), parsed.getHours(), parsed.getMinutes()];
    }
    return String(parts[0]) + "-" + padInterviewTime(parts[1]) + "-" + padInterviewTime(parts[2]) +
      " " + padInterviewTime(parts[3]) + ":" + padInterviewTime(parts[4]);
  }

  function padInterviewTime(value) { return String(Math.max(0, Number(value) || 0)).padStart(2, "0"); }

  function renderInterviewTranscript() {
    if (state.interviewBusy && !state.interviewMessages.length) {
      return statePanel("正在读取面试问答", "请稍候，正在加载本次面试的问题和回答。", "pending");
    }
    var session = state.activeInterview || {};
    var completed = session.status === "COMPLETED" || session.finalScore != null || !!session.report;
    var pairs = interviewQuestionAnswerPairs(state.interviewMessages, completed);
    return '<section class="panel full interview-transcript"><div class="interview-transcript-head"><div><span class="resource-type">问答详情</span><h3>' +
      escapeHtml(firstText(session.positionName, "AI 模拟面试")) + '</h3><p>按面试顺序查看本次问题和你的回答。</p></div>' +
      '<div class="actions-row"><button type="button" class="secondary" data-link="interview-history">返回面试记录</button>' +
      (!completed ? '<button type="button" data-interview-action="resume-current">继续本次面试</button>' : '') + '</div></div>' +
      (pairs.length ? '<div class="interview-transcript-list">' + pairs.map(function (pair, index) {
        return '<article class="interview-transcript-pair"><span>第 ' + (index + 1) + ' 题</span><h4>面试官问题</h4><p>' +
          escapeHtml(pair.question) + '</p><h4>我的回答</h4><p class="candidate-answer">' +
          escapeHtml(pair.answer || "尚未回答") + '</p></article>';
      }).join("") + '</div>' : '<p class="empty-copy">本次面试还没有可查看的问答内容。</p>') + '</section>';
  }

  function interviewQuestionAnswerPairs(messages, completed) {
    var pairs = []; var current = null;
    normalizeArray(messages).forEach(function (message) {
      var role = String(message && message.role || "").toUpperCase();
      var content = firstText(message && message.content, "");
      if (!content) return;
      if (role === "USER" || role === "CANDIDATE") {
        if (!current) current = { question: "问题记录暂缺", answer: "" };
        current.answer = content; pairs.push(current); current = null; return;
      }
      if (current && !completed) pairs.push(current);
      current = { question: content, answer: "" };
    });
    if (current && !completed) pairs.push(current);
    return pairs;
  }

  function isAiInterview(entry) {
    var mode = String(entry && entry.mode || "TEXT").toUpperCase();
    return mode !== "VOICE" && mode !== "PANORAMA";
  }

  function isPanoramaInterview(entry) {
    var mode = String(entry && entry.mode || "").toUpperCase();
    return mode === "VOICE" || mode === "PANORAMA";
  }

  function handlePanoramaAction(target) {
    var action = target.getAttribute("data-panorama-action");
    if (action === "history-page") { loadPanoramaHistoryPage(target.getAttribute("data-page"), true); return; }
    if (action === "open-record") { openPanoramaRecord(target.getAttribute("data-interview-id"), target.getAttribute("data-view")); return; }
    if (action === "delete-record") { deleteInterviewRecord(target.getAttribute("data-interview-id")); return; }
    if (action === "resume-record") { state.panoramaViewMode = "practice"; state.panoramaCameraState = "fallback"; renderPage(pageByKey["interview-panorama"]); return; }
    if (action === "camera") { startPanoramaCamera(); return; }
    if (action === "fallback") { usePanoramaWithoutCamera(); return; }
    if (action === "start") { startPanoramaInterview(); return; }
    if (action === "speak") { speakPanoramaQuestion(true); return; }
    if (action === "listen") { startPanoramaAnswer(); return; }
    if (action === "answer") { submitPanoramaAnswer(); return; }
    if (action === "finish") { finishPanoramaInterview(); return; }
    if (action === "reset") { resetPanoramaInterview(); }
  }

  function openPanoramaRecord(interviewId, viewMode) {
    var candidates = normalizeArray(state.panoramaHistoryPage && state.panoramaHistoryPage.items).concat(normalizeArray(state.interviews));
    var session = candidates.filter(function (item) { return String(item.interviewId) === String(interviewId); })[0];
    if (!session) return;
    stopPanoramaMedia();
    state.panoramaSession = session; state.panoramaMessages = []; state.panoramaReport = session.report || null;
    state.panoramaViewMode = viewMode === "transcript" ? "transcript" : "practice";
    state.panoramaDifficulty = firstText(session.difficulty, "Normal");
    state.panoramaCameraState = "fallback"; state.panoramaBusy = true; state.panoramaError = null; state.panoramaLastSpokenQuestion = null;
    if (state.route !== "interview-panorama") replaceRouteInLocation("interview-panorama");
    var completed = session.status === "COMPLETED" || session.finalScore != null || !!session.report;
    var reportRequest = completed && !session.report
      ? post(endpoints.guidedInterviewFinish, { userId: state.identity.userId, interviewId: session.interviewId })
      : Promise.resolve(session.report || null);
    Promise.all([post(endpoints.interviewMessages, { userId: state.identity.userId, interviewId: session.interviewId }), reportRequest]).then(function (results) {
      state.panoramaMessages = normalizeArray(results[0]);
      state.panoramaAnswerCount = state.panoramaMessages.filter(function (message) { return String(message.role).toUpperCase() === "USER"; }).length;
      var questions = state.panoramaMessages.filter(function (message) { return String(message.role).toUpperCase() === "AI"; });
      state.panoramaQuestion = questions.length ? questions[questions.length - 1].content : null;
      if (results[1]) { state.panoramaReport = results[1]; syncCompletedInterview(session, results[1]); }
      state.panoramaSeconds = completed ? 0 : panoramaAnswerLimit(state.panoramaDifficulty);
      state.panoramaDeadlineAt = completed ? null : Date.now() + state.panoramaSeconds * 1000;
    }).catch(function (error) {
      state.panoramaError = error.message || "无法读取全景面试记录。";
    }).then(function () {
      state.panoramaBusy = false; renderPage(pageByKey["interview-panorama"]);
      if (!completed && state.panoramaViewMode === "practice" && state.panoramaQuestion) startPanoramaTimer();
    });
  }

  function renderPanoramaTranscript() {
    var session = state.panoramaSession || {};
    var completed = session.status === "COMPLETED" || session.finalScore != null || !!session.report;
    var pairs = interviewQuestionAnswerPairs(state.panoramaMessages, completed);
    return '<section class="panel full interview-transcript"><div class="interview-transcript-head"><div><span class="resource-type">全景问答详情</span><h3>' +
      escapeHtml(firstText(session.positionName, "全景仿真面试")) + '</h3><p>按面试顺序查看本次问题和你的回答。</p></div>' +
      '<div class="actions-row"><button type="button" class="secondary" data-link="interview-panorama-history">返回全景面试记录</button>' +
      (!completed ? '<button type="button" data-panorama-action="resume-record">继续本次面试</button>' : '') + '</div></div>' +
      (pairs.length ? '<div class="interview-transcript-list">' + pairs.map(function (pair, index) {
        return '<article class="interview-transcript-pair"><span>第 ' + (index + 1) + ' 题</span><h4>面试官问题</h4><p>' +
          escapeHtml(pair.question) + '</p><h4>我的回答</h4><p class="candidate-answer">' +
          escapeHtml(pair.answer || "尚未回答") + '</p></article>';
      }).join("") + '</div>' : '<p class="empty-copy">这条记录还没有可展示的问答内容。</p>') + '</section>';
  }

  function startPanoramaCamera() {
    state.panoramaError = null; state.panoramaNotice = null; state.panoramaMediaDiagnostics = [];
    stopPanoramaMedia();
    state.panoramaCameraState = "requesting";
    renderPage(pageByKey["interview-panorama"]);
    requestPanoramaMedia({ video: { facingMode: "user", width: { ideal: 1280 }, height: { ideal: 720 } }, audio: true })
      .then(function (stream) {
        state.panoramaStream = stream; state.panoramaCameraState = "ready"; state.panoramaError = null; state.panoramaMediaDiagnostics = [];
      }).catch(function (error) {
        state.panoramaCameraState = error && error.name === "NotAllowedError" ? "denied" : "unavailable";
        state.panoramaError = panoramaCameraFailureMessage(error);
        state.panoramaMediaDiagnostics = panoramaMediaDiagnosticTips(error);
      }).then(function () { renderPage(pageByKey["interview-panorama"]); });
  }

  function requestPanoramaMedia(constraints) {
    var candidates = panoramaMediaCandidates();
    var constraintOptions = [constraints, { video: { facingMode: "user" }, audio: true }, { video: true, audio: true }];
    function attemptCandidate(mediaConstraints, index, lastError) {
      if (index >= candidates.length) return Promise.reject(lastError || { name: "UnsupportedError" });
      try {
        return candidates[index](mediaConstraints).catch(function (error) { return attemptCandidate(mediaConstraints, index + 1, error); });
      } catch (error) {
        return attemptCandidate(mediaConstraints, index + 1, error);
      }
    }
    function attemptConstraints(index, lastError) {
      if (index >= constraintOptions.length) return Promise.reject(lastError || { name: "UnsupportedError" });
      return attemptCandidate(constraintOptions[index], 0, lastError).catch(function (error) { return attemptConstraints(index + 1, error); });
    }
    return attemptConstraints(0, null);
  }

  function panoramaMediaCandidates() {
    var candidates = []; var seen = [];
    reachableWindows().forEach(function (sourceWindow) {
      var sourceNavigator;
      try { sourceNavigator = sourceWindow.navigator; } catch (error) { return; }
      if (!sourceNavigator || seen.indexOf(sourceNavigator) >= 0) return;
      seen.push(sourceNavigator);
      if (sourceNavigator.mediaDevices && sourceNavigator.mediaDevices.getUserMedia) {
        candidates.push(function (constraints) { return sourceNavigator.mediaDevices.getUserMedia.call(sourceNavigator.mediaDevices, constraints); });
      }
      var legacy = sourceNavigator.getUserMedia || sourceNavigator.webkitGetUserMedia || sourceNavigator.mozGetUserMedia;
      if (legacy) {
        candidates.push(function (constraints) {
          return new Promise(function (resolve, reject) { legacy.call(sourceNavigator, constraints, resolve, reject); });
        });
      }
    });
    return candidates;
  }

  function panoramaMediaDiagnosticTips(error) {
    var tips = [];
    var protocol = "";
    var host = "";
    try { protocol = window.location.protocol; host = window.location.hostname; } catch (ignore) {}
    var localHost = host === "localhost" || host === "127.0.0.1" || host === "::1";
    if (window.isSecureContext === false && protocol !== "https:" && !localHost) {
      tips.push("当前页面不是 HTTPS 或 localhost，浏览器会禁止摄像头和麦克风。请用 HTTPS 地址打开，或在本机 localhost 调试。");
    }
    var embedded = false;
    try { embedded = window.self !== window.top; } catch (ignoreTop) { embedded = true; }
    if (embedded) {
      tips.push("当前页面是在外层页面中打开的，外层 iframe 需要允许 camera 和 microphone，例如 allow=\"camera; microphone\"。");
      tips.push("如果外层系统设置了 Permissions-Policy，也需要放开摄像头和麦克风权限。");
    }
    var hasStandardMedia = !!(navigator.mediaDevices && navigator.mediaDevices.getUserMedia);
    var hasLegacyMedia = !!(navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia);
    if (!hasStandardMedia && !hasLegacyMedia && (window.isSecureContext !== false || protocol === "https:" || localHost)) {
      tips.push("当前浏览器不支持摄像头和麦克风接口，请换用新版 Chrome 或 Edge。");
    }
    if (error && error.name === "NotAllowedError") {
      tips.push("浏览器或系统拒绝了权限。请点击地址栏左侧的网站设置，把摄像头和麦克风改为允许。");
      tips.push("Windows 还需要在“设置 > 隐私和安全性 > 摄像头/麦克风”中允许桌面应用访问。");
    } else if (error && (error.name === "NotReadableError" || error.name === "TrackStartError")) {
      tips.push("摄像头或麦克风可能正在被会议软件、录屏软件或其他浏览器标签页占用，请关闭占用程序后重试。");
    } else if (error && (error.name === "NotFoundError" || error.name === "DevicesNotFoundError")) {
      tips.push("浏览器没有找到可用的摄像头或麦克风，请确认设备已连接，并在系统中没有被禁用。");
    } else if (error && (error.name === "OverconstrainedError" || error.name === "ConstraintNotSatisfiedError")) {
      tips.push("当前设备不支持页面请求的画面规格，页面会降低要求后再尝试；仍失败时可使用无摄像头模式。");
    } else if (error && error.name === "SecurityError") {
      tips.push("浏览器安全策略阻止了摄像头和麦克风，请检查 HTTPS、iframe allow 和 Permissions-Policy 设置。");
    }
    if (!tips.length) {
      tips.push("请确认浏览器地址栏中已允许摄像头和麦克风，并且没有其他软件占用设备。");
    }
    tips.push("如果暂时无法调整环境，可以先点“无摄像头继续”，提问、回答、计时和复盘仍可使用。");
    return tips;
  }

  function panoramaCameraFailureMessage(error) {
    if (window.isSecureContext === false) {
      return "当前页面不是安全连接，浏览器禁止网页调用摄像头和麦克风。你可以改用 HTTPS，或选择“无摄像头继续”完成面试。";
    }
    if (error && error.name === "NotAllowedError") {
      return "摄像头或麦克风权限被浏览器或苍穹页面阻止。允许权限后可重试，也可以选择“无摄像头继续”。";
    }
    if (error && (error.name === "NotReadableError" || error.name === "TrackStartError")) {
      return "摄像头可能正被其他程序占用。关闭占用程序后重试，或选择“无摄像头继续”。";
    }
    return "当前环境无法连接摄像头。系统已保留无摄像头模式，你仍可以完成 AI 面试。";
  }

  function usePanoramaWithoutCamera() {
    stopPanoramaMedia(); state.panoramaCameraState = "fallback"; state.panoramaError = null; state.panoramaMediaDiagnostics = [];
    state.panoramaNotice = "已切换为无摄像头模式。AI 提问、回答、计时和复盘仍可正常使用。";
    renderPage(pageByKey["interview-panorama"]);
  }

  function startPanoramaInterview() {
    if ((!state.panoramaStream && state.panoramaCameraState !== "fallback") || state.panoramaBusy) return;
    var position = valueOf("panoramaPosition");
    var resumeId = valueOf("panoramaResume");
    var difficulty = valueOf("panoramaDifficulty");
    state.panoramaDifficulty = difficulty || "Normal";
    state.panoramaBusy = true; state.panoramaError = null;
    renderPage(pageByKey["interview-panorama"]);
    post(endpoints.guidedInterviewStart, { userId: state.identity.userId, request: {
      positionName: position, resumeId: resumeId ? Number(resumeId) : null, difficulty: difficulty, mode: "VOICE"
    }}).then(function (result) {
      state.panoramaSession = result.session; state.panoramaQuestion = result.openingMessage.content;
      state.panoramaMessages = [result.openingMessage]; state.panoramaViewMode = "practice";
      state.panoramaAnswerCount = 0; state.panoramaTranscript = ""; state.panoramaLastSpokenQuestion = null;
      state.panoramaSeconds = panoramaAnswerLimit(state.panoramaDifficulty);
      state.panoramaDeadlineAt = null;
      state.interviews = [result.session].concat(normalizeArray(state.interviews));
    }).catch(function (error) {
      state.panoramaError = error.message || "全景面试暂时无法开始，请稍后重试。";
    }).then(function () {
      state.panoramaBusy = false; renderPage(pageByKey["interview-panorama"]);
    });
  }

  function startPanoramaAnswer(automatic) {
    if (state.panoramaAnswering || state.panoramaBusy) return;
    stopPanoramaQuestionSpeech();
    state.panoramaError = null;
    var Recognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!Recognition) {
      state.panoramaAnswering = false;
      state.panoramaError = "当前浏览器不支持语音转文字，请直接在回答框中输入内容。摄像头预览仍会继续。";
      renderPage(pageByKey["interview-panorama"]);
      var fallbackInput = document.getElementById("panoramaAnswer"); if (fallbackInput) fallbackInput.focus();
      return;
    }
    var recognition = new Recognition();
    var recognitionToken = Number(state.panoramaRecognitionToken || 0) + 1;
    state.panoramaRecognitionToken = recognitionToken;
    recognition.lang = "zh-CN"; recognition.continuous = true; recognition.interimResults = true;
    recognition.onstart = function () {
      if (state.panoramaRecognitionToken !== recognitionToken || state.panoramaRecognition !== recognition) return;
      state.panoramaAnswering = true;
      updatePanoramaAnsweringUi(true, automatic ?
        "AI 已读完题目，语音输入已自动开始；系统只会把语音转成文字，不保存音频。" :
        "语音输入已开始；系统只会把语音转成文字，不保存音频。");
    };
    recognition.onresult = function (event) {
      if (state.panoramaRecognitionToken !== recognitionToken || state.panoramaRecognition !== recognition) return;
      var text = "";
      for (var i = 0; i < event.results.length; i += 1) text += event.results[i][0].transcript;
      state.panoramaTranscript = text;
      var input = document.getElementById("panoramaAnswer"); if (input) input.value = text;
    };
    recognition.onerror = function () {
      if (state.panoramaRecognitionToken !== recognitionToken || state.panoramaRecognition !== recognition) return;
      state.panoramaRecognition = null;
      state.panoramaError = automatic ?
        "浏览器没有允许自动开启语音输入，请点击“重新开启语音输入”，或直接输入回答。" :
        "语音识别暂时不可用，你可以重新开启或直接输入回答。";
      state.panoramaAnswering = false;
      renderPage(pageByKey["interview-panorama"]);
    };
    recognition.onend = function () {
      if (state.panoramaRecognitionToken !== recognitionToken || state.panoramaRecognition !== recognition) return;
      state.panoramaRecognition = null;
      state.panoramaAnswering = false;
      updatePanoramaAnsweringUi(false, "语音输入已停止；如需继续，请点击“重新开启语音输入”。");
    };
    state.panoramaRecognition = recognition;
    try {
      recognition.start();
      state.panoramaAnswering = true;
      state.panoramaNotice = automatic ?
        "AI 已读完题目，正在自动开启语音输入；系统只会把语音转成文字，不保存音频。" :
        "正在开启语音输入；系统只会把语音转成文字，不保存音频。";
    } catch (error) {
      state.panoramaRecognition = null;
      state.panoramaAnswering = false;
      state.panoramaError = automatic ?
        "浏览器没有允许自动开启语音输入，请点击“重新开启语音输入”，或直接输入回答。" :
        "语音输入启动失败，请重试或直接输入回答。";
    }
    renderPage(pageByKey["interview-panorama"]);
  }

  function updatePanoramaAnsweringUi(answering, message) {
    state.panoramaAnswering = !!answering;
    state.panoramaNotice = message || null;
    var status = document.getElementById("panoramaSpeechStatus");
    if (status) status.textContent = answering ? "题目已读完，语音输入已自动开始，请回答" : "语音输入已停止，可以重新开启或直接输入";
    var interviewerStatus = document.getElementById("panoramaInterviewerStatus");
    if (interviewerStatus) interviewerStatus.textContent = answering ? "正在听你回答" : "正在与你面对面交流";
    var listenButton = document.querySelector('[data-panorama-action="listen"]');
    if (listenButton) {
      listenButton.textContent = answering ? "正在录音" : "重新开启语音输入";
      listenButton.disabled = !!answering;
    }
    var notice = document.querySelector(".panorama-notice");
    if (notice && message) notice.textContent = message;
  }

  function submitPanoramaAnswer(timedOut) {
    if (!state.panoramaSession || state.panoramaBusy) return;
    stopPanoramaQuestionSpeech();
    var answer = valueOf("panoramaAnswer");
    if (!answer && timedOut) answer = "本题在规定时间内未完成回答。";
    if (!answer) { state.panoramaError = "请先完成本题回答，再进入下一题。"; renderPage(pageByKey["interview-panorama"]); return; }
    stopPanoramaRecognition(); stopPanoramaTimer(); state.panoramaDeadlineAt = null;
    state.panoramaBusy = true; state.panoramaTranscript = answer; state.panoramaError = null;
    state.panoramaNotice = timedOut ? "本题答题时间已结束，正在自动提交并进入下一题。" : null;
    renderPage(pageByKey["interview-panorama"]);
    var shouldFinish = false;
    post(endpoints.guidedInterviewAnswer, { userId: state.identity.userId, interviewId: state.panoramaSession.interviewId, answer: answer })
      .then(function (result) {
        state.panoramaSession = result.session; state.panoramaAnswerCount += 1; state.panoramaTranscript = "";
        state.panoramaMessages.push(result.userMessage, result.interviewerMessage);
        shouldFinish = state.panoramaAnswerCount >= 7 || !result.interviewerMessage;
        state.panoramaQuestion = shouldFinish ? null : result.interviewerMessage.content;
        state.panoramaSeconds = shouldFinish ? 0 : panoramaAnswerLimit(state.panoramaDifficulty);
        state.panoramaDeadlineAt = null;
      }).catch(function (error) { state.panoramaError = error.message || "回答提交失败，请稍后重试。"; })
      .then(function () {
        state.panoramaBusy = false;
        if (shouldFinish) { finishPanoramaInterview(); return; }
        renderPage(pageByKey["interview-panorama"]);
      });
  }

  function finishPanoramaInterview() {
    if (!state.panoramaSession || state.panoramaBusy) return;
    stopPanoramaRecognition(); stopPanoramaTimer(); state.panoramaDeadlineAt = null; state.panoramaBusy = true;
    post(endpoints.guidedInterviewFinish, { userId: state.identity.userId, interviewId: state.panoramaSession.interviewId })
      .then(function (report) {
        state.panoramaReport = report; syncCompletedInterview(state.panoramaSession, report); stopPanoramaMedia();
      }).catch(function (error) { state.panoramaError = error.message || "暂时无法生成复盘，请稍后重试。"; })
      .then(function () { state.panoramaBusy = false; renderPage(pageByKey["interview-panorama"]); });
  }

  function resetPanoramaInterview() {
    stopPanoramaMedia(); state.panoramaSession = null; state.panoramaQuestion = null; state.panoramaTranscript = ""; state.panoramaLastSpokenQuestion = null;
    state.panoramaMessages = []; state.panoramaViewMode = "practice";
    state.panoramaReport = null; state.panoramaError = null; state.panoramaNotice = null; state.panoramaMediaDiagnostics = []; state.panoramaAnswerCount = 0; state.panoramaSeconds = 0; state.panoramaDeadlineAt = null;
    state.panoramaDifficulty = "Normal";
    state.panoramaCameraState = "idle"; renderPage(pageByKey["interview-panorama"]);
  }

  function attachPanoramaCamera() {
    var video = document.getElementById("panoramaCamera");
    if (!video || !state.panoramaStream) return;
    if (video.srcObject !== state.panoramaStream) video.srcObject = state.panoramaStream;
    var playing = video.play(); if (playing && playing.catch) playing.catch(function () {});
  }

  function speakPanoramaQuestion(force) {
    var question = String(state.panoramaQuestion || "").trim();
    var questionKey = String(state.panoramaAnswerCount + 1) + ":" + question;
    if (!question || state.panoramaReport || !state.panoramaSession) return;
    if (!force && state.panoramaLastSpokenQuestion === questionKey) return;
    stopPanoramaRecognition();
    stopPanoramaTimer();
    state.panoramaDeadlineAt = null;
    state.panoramaLastSpokenQuestion = questionKey;
    if (!("speechSynthesis" in window) || !("SpeechSynthesisUtterance" in window)) {
      state.panoramaSpeechSupported = false;
      updatePanoramaSpeechUi(false, "当前浏览器暂时无法朗读题目，请点击查看问答记录或由面试官文字记录辅助练习");
      return;
    }
    state.panoramaSpeechSupported = true;
    try { window.speechSynthesis.cancel(); } catch (error) {}
    var speechToken = Number(state.panoramaSpeechToken || 0) + 1;
    state.panoramaSpeechToken = speechToken;
    var utterance = new SpeechSynthesisUtterance(question);
    utterance.lang = "zh-CN";
    utterance.rate = 0.9;
    utterance.pitch = 0.82;
    utterance.volume = 1;
    var matureMaleVoice = panoramaMatureMaleVoice();
    if (matureMaleVoice) {
      utterance.voice = matureMaleVoice;
      utterance.lang = matureMaleVoice.lang || "zh-CN";
    }
    utterance.onstart = function () {
      if (state.panoramaSpeechToken !== speechToken) return;
      updatePanoramaSpeechUi(true, "AI 面试官正在提问，请听完后回答");
    };
    utterance.onend = function () {
      if (state.panoramaSpeechToken !== speechToken || !state.panoramaSession || !state.panoramaQuestion || state.panoramaReport) return;
      updatePanoramaSpeechUi(false, "题目已读完，正在自动开启语音输入");
      startPanoramaTimer();
      startPanoramaAnswer(true);
    };
    utterance.onerror = function () {
      if (state.panoramaSpeechToken !== speechToken) return;
      state.panoramaSpeechSupported = false;
      updatePanoramaSpeechUi(false, "浏览器没有播放出题目，请点击“播放题目”再试一次");
    };
    updatePanoramaSpeechUi(true, "AI 面试官正在准备提问");
    try { window.speechSynthesis.speak(utterance); } catch (error) { state.panoramaSpeechSupported = false; updatePanoramaSpeechUi(false, "浏览器没有播放出题目，请点击“播放题目”再试一次"); }
  }

  function panoramaMatureMaleVoice() {
    if (!("speechSynthesis" in window) || typeof window.speechSynthesis.getVoices !== "function") return null;
    var voices = [];
    try { voices = window.speechSynthesis.getVoices() || []; } catch (error) { return null; }
    var maleName = /yunxi|yunyang|yunhao|yunjian|yunfeng|yunze|yunfan|yunjhe|wanlung|kangkang|li[-_\s]?mu|male|男声|男音/i;
    var femaleName = /xiaoxiao|xiaoyi|xiaohan|xiaomeng|xiaomo|xiaoqiu|xiaorui|xiaoshuang|xiaoxuan|xiaoyan|xiaoyou|xiaozhen|ting[-_\s]?ting|meijia|female|女声|女音/i;
    var ranked = voices.filter(function (voice) {
      return voice && /^zh(?:-|_)/i.test(String(voice.lang || ""));
    }).map(function (voice) {
      var name = String(voice.name || "") + " " + String(voice.voiceURI || "");
      var score = 0;
      if (/^zh(?:-|_)cn/i.test(String(voice.lang || ""))) score += 40;
      if (maleName.test(name)) score += 100;
      if (femaleName.test(name)) score -= 100;
      if (/natural|neural|online/i.test(name)) score += 15;
      if (voice.localService) score += 5;
      return { voice: voice, score: score };
    }).filter(function (entry) {
      return entry.score >= 100;
    }).sort(function (left, right) {
      return right.score - left.score;
    });
    return ranked.length ? ranked[0].voice : null;
  }

  function warmPanoramaVoices() {
    if (!("speechSynthesis" in window) || typeof window.speechSynthesis.getVoices !== "function") return;
    try { window.speechSynthesis.getVoices(); } catch (error) { return; }
    if (typeof window.speechSynthesis.addEventListener === "function") {
      window.speechSynthesis.addEventListener("voiceschanged", function () {
        try { window.speechSynthesis.getVoices(); } catch (error) {}
      }, { once: true });
    }
  }

  function updatePanoramaSpeechUi(isSpeaking, message) {
    state.panoramaSpeaking = !!isSpeaking;
    var interviewer = document.getElementById("panoramaInterviewer");
    if (interviewer) interviewer.classList.toggle("speaking", !!isSpeaking);
    var status = document.getElementById("panoramaSpeechStatus");
    if (status) status.textContent = message || (isSpeaking ? "AI 面试官正在提问，请听完后回答" : "请听 AI 面试官读题后回答");
    var interviewerStatus = document.getElementById("panoramaInterviewerStatus");
    if (interviewerStatus) interviewerStatus.textContent = isSpeaking ? "正在读出本题" : "正在与你面对面交流";
  }

  function stopPanoramaQuestionSpeech() {
    state.panoramaSpeechToken = Number(state.panoramaSpeechToken || 0) + 1;
    if ("speechSynthesis" in window) {
      try { window.speechSynthesis.cancel(); } catch (error) {}
    }
    updatePanoramaSpeechUi(false, "请听 AI 面试官读题后回答");
  }

  function startPanoramaTimer() {
    stopPanoramaTimer();
    if (!state.panoramaDeadlineAt) {
      if (state.panoramaSeconds <= 0) state.panoramaSeconds = panoramaAnswerLimit(state.panoramaDifficulty);
      state.panoramaDeadlineAt = Date.now() + state.panoramaSeconds * 1000;
    }
    function updatePanoramaTimer() {
      state.panoramaSeconds = Math.max(0, Math.ceil((state.panoramaDeadlineAt - Date.now()) / 1000)); var timer = document.getElementById("panoramaTimer");
      if (timer) timer.textContent = formatPanoramaTime(state.panoramaSeconds);
      if (state.panoramaSeconds <= 0) { stopPanoramaTimer(); state.panoramaDeadlineAt = null; submitPanoramaAnswer(true); }
    }
    updatePanoramaTimer();
    if (state.panoramaSeconds > 0) state.panoramaTimer = window.setInterval(updatePanoramaTimer, 1000);
  }

  function stopPanoramaTimer() { if (state.panoramaTimer) window.clearInterval(state.panoramaTimer); state.panoramaTimer = null; }
  function panoramaAnswerLimit(difficulty) {
    if (difficulty === "Easy") return 3 * 60;
    if (difficulty === "Hard") return 8 * 60;
    return 5 * 60;
  }
  function formatPanoramaTime(seconds) { var safe = Math.max(0, Number(seconds) || 0); return String(Math.floor(safe / 60)).padStart(2, "0") + ":" + String(safe % 60).padStart(2, "0"); }
  function stopPanoramaRecognition() {
    state.panoramaRecognitionToken = Number(state.panoramaRecognitionToken || 0) + 1;
    if (state.panoramaRecognition) { try { state.panoramaRecognition.stop(); } catch (error) {} }
    state.panoramaRecognition = null; state.panoramaAnswering = false;
  }
  function stopPanoramaMedia() {
    stopPanoramaRecognition(); stopPanoramaTimer(); stopPanoramaQuestionSpeech();
    if (state.panoramaStream && state.panoramaStream.getTracks) state.panoramaStream.getTracks().forEach(function (track) { track.stop(); });
    state.panoramaStream = null;
  }

  function handleInterviewAction(target) {
    var action = target.getAttribute("data-interview-action");
    if (state.interviewBusy) return;
    state.interviewError = null;
    if (action === "speech") { toggleAiInterviewSpeech(); return; }
    if (action === "reset" || action === "leave") { resetActiveInterviewView(); return; }
    if (action === "open") { openInterview(target.getAttribute("data-interview-id"), "result"); return; }
    if (action === "transcript") { openInterview(target.getAttribute("data-interview-id"), "transcript"); return; }
    if (action === "current-transcript") { state.interviewViewMode = "transcript"; renderPage(pageByKey.interview); return; }
    if (action === "resume-current") { state.interviewViewMode = "practice"; renderPage(pageByKey.interview); return; }
    if (action === "delete") { deleteInterviewRecord(target.getAttribute("data-interview-id")); return; }
    if (action === "history-page") { loadInterviewHistoryPage(target.getAttribute("data-page"), true); return; }
    var draftPosition = action === "start" ? valueOf("interviewPosition") : "";
    var draftResumeId = action === "start" ? valueOf("interviewResume") : "";
    var draftDifficulty = action === "start" ? valueOf("interviewDifficulty") : "Normal";
    var draftAnswer = action === "answer" ? valueOf("interviewAnswer") : "";
    if (action === "start") {
      syncInterviewSetupDraftFromPage();
      draftPosition = state.interviewSetupPosition;
      draftResumeId = state.interviewSetupResumeId;
      draftDifficulty = state.interviewSetupDifficulty;
    }
    state.interviewBusy = true; renderPage(pageByKey.interview);
    var call;
    if (action === "start") {
      call = post(endpoints.guidedInterviewStart, { userId: state.identity.userId, request: { positionName: draftPosition, resumeId: draftResumeId ? Number(draftResumeId) : null, difficulty: draftDifficulty, mode: "TEXT" } }).then(function (result) {
        state.interviewViewMode = "practice";
        state.activeInterview = result.session; state.interviewMessages = [result.openingMessage]; state.interviewCurrentQuestion = result.openingMessage.content;
      state.interviewAnswerCount = 0; state.interviewDraft = ""; state.interviews = [result.session].concat(normalizeArray(state.interviews));
        state.interviewHistoryPage = null;
      });
    } else if (action === "answer") {
      if (!draftAnswer) { state.interviewBusy = false; state.interviewError = "请先写下你的回答。"; renderPage(pageByKey.interview); return; }
      stopAiInterviewSpeech();
      call = post(endpoints.guidedInterviewAnswer, { userId: state.identity.userId, interviewId: state.activeInterview.interviewId, answer: draftAnswer }).then(function (result) {
        state.interviewMessages.push(result.userMessage, result.interviewerMessage); state.activeInterview = result.session;
        state.interviewAnswerCount += 1; state.interviewDraft = "";
        if (state.interviewAnswerCount >= 7) {
          return post(endpoints.guidedInterviewFinish, { userId: state.identity.userId, interviewId: state.activeInterview.interviewId })
            .then(function (report) { state.interviewReport = report; syncCompletedInterview(state.activeInterview, report); });
        }
        state.interviewCurrentQuestion = result.interviewerMessage.content;
      });
    } else {
      call = post(endpoints.guidedInterviewFinish, { userId: state.identity.userId, interviewId: state.activeInterview.interviewId }).then(function (report) {
        state.interviewReport = report; syncCompletedInterview(state.activeInterview, report);
      });
    }
    call.catch(function (error) { state.interviewError = error.message || "模拟面试暂时不可用，请稍后重试。"; }).then(function () { state.interviewBusy = false; renderPage(pageByKey.interview); });
  }

  function resetActiveInterviewView() {
    stopAiInterviewSpeech();
    state.activeInterview = null;
    state.interviewMessages = [];
    state.interviewReport = null;
    state.interviewCurrentQuestion = null;
    state.interviewAnswerCount = 0;
    state.interviewDraft = "";
    state.interviewViewMode = "practice";
    renderPage(pageByKey.interview);
  }

  function deleteInterviewRecord(interviewId) {
    if (!interviewId) return;
    showConfirmDialog("删除面试记录", "删除后，本次面试的问题、回答和复盘都会移除，且无法恢复。", "删除记录", function () {
      performDeleteInterviewRecord(interviewId);
    });
  }

  function performDeleteInterviewRecord(interviewId) {
    if (isFilePreview()) {
      removeInterviewFromPage(interviewId); renderPage(pageByKey[state.route]); return;
    }
    state.interviewBusy = true; renderPage(pageByKey[state.route]);
    post(endpoints.interviewDelete, { userId: state.identity.userId, interviewId: Number(interviewId) }).then(function () {
      removeInterviewFromPage(interviewId);
      showMessage("info", "面试记录已删除", "本次面试的问题、回答和复盘已从记录中移除。");
      if (state.route === "interview-history") return loadInterviewHistoryPage(state.interviewHistoryPageNumber, false);
      if (state.route === "interview-panorama-history") return loadPanoramaHistoryPage(state.panoramaHistoryPageNumber, false);
    }).catch(function (error) {
      state.interviewError = error.message || "面试记录删除失败，请稍后重试。";
      showMessage("error", "删除失败", state.interviewError);
    }).then(function () {
      state.interviewBusy = false; renderPage(pageByKey[state.route]);
    });
  }

  function removeInterviewFromPage(interviewId) {
    state.interviews = normalizeArray(state.interviews).filter(function (entry) {
      return String(entry.interviewId) !== String(interviewId);
    });
    if (state.interviewHistoryPage) {
      state.interviewHistoryPage.items = normalizeArray(state.interviewHistoryPage.items).filter(function (entry) {
        return String(entry.interviewId) !== String(interviewId);
      });
    }
    if (state.panoramaHistoryPage) {
      state.panoramaHistoryPage.items = normalizeArray(state.panoramaHistoryPage.items).filter(function (entry) {
        return String(entry.interviewId) !== String(interviewId);
      });
    }
    if (state.activeInterview && String(state.activeInterview.interviewId) === String(interviewId)) {
      stopAiInterviewSpeech(); state.activeInterview = null; state.interviewMessages = []; state.interviewReport = null;
      state.interviewCurrentQuestion = null; state.interviewAnswerCount = 0; state.interviewDraft = ""; state.interviewError = null;
      state.interviewViewMode = "practice";
    }
    if (state.panoramaSession && String(state.panoramaSession.interviewId) === String(interviewId)) {
      stopPanoramaMedia(); state.panoramaSession = null; state.panoramaMessages = []; state.panoramaReport = null;
      state.panoramaQuestion = null; state.panoramaAnswerCount = 0; state.panoramaTranscript = ""; state.panoramaError = null; state.panoramaLastSpokenQuestion = null;
      state.panoramaViewMode = "practice";
    }
  }

  function toggleAiInterviewSpeech() {
    if (state.interviewListening) { stopAiInterviewSpeech(); renderPage(pageByKey.interview); return; }
    var Recognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!Recognition) {
      state.interviewError = "当前浏览器不支持语音转文字，请直接输入回答。";
      renderPage(pageByKey.interview); var fallbackInput = document.getElementById("interviewAnswer"); if (fallbackInput) fallbackInput.focus(); return;
    }
    var recognition = new Recognition(); recognition.lang = "zh-CN"; recognition.continuous = true; recognition.interimResults = true;
    recognition.onresult = function (event) {
      var text = ""; for (var i = 0; i < event.results.length; i += 1) text += event.results[i][0].transcript;
      state.interviewDraft = text; var input = document.getElementById("interviewAnswer"); if (input) input.value = text;
    };
    recognition.onerror = function () { state.interviewListening = false; state.interviewError = "语音识别暂时不可用，请直接输入回答。"; renderPage(pageByKey.interview); };
    recognition.onend = function () { state.interviewListening = false; };
    state.interviewRecognition = recognition; state.interviewListening = true;
    try { recognition.start(); } catch (error) { state.interviewListening = false; }
    renderPage(pageByKey.interview);
  }

  function stopAiInterviewSpeech() {
    if (state.interviewRecognition) { try { state.interviewRecognition.stop(); } catch (error) {} }
    state.interviewRecognition = null; state.interviewListening = false;
  }

  function openInterview(interviewId, viewMode) {
    var candidates = normalizeArray(state.interviewHistoryPage && state.interviewHistoryPage.items).concat(normalizeArray(state.interviews));
    var session = candidates.filter(function (item) { return String(item.interviewId) === String(interviewId); })[0];
    if (!session) return;
    state.activeInterview = session; state.interviewReport = session.report || null; state.interviewViewMode = viewMode || "practice";
    state.interviewBusy = true; state.interviewMessages = []; renderPage(pageByKey.interview);
    if (state.route !== "interview") replaceRouteInLocation("interview");
    var completed = session.status === "COMPLETED" || session.finalScore != null || !!session.report;
    var reportRequest = completed && !session.report
      ? post(endpoints.guidedInterviewFinish, { userId: state.identity.userId, interviewId: session.interviewId })
      : Promise.resolve(session.report || null);
    Promise.all([post(endpoints.interviewMessages, { userId: state.identity.userId, interviewId: session.interviewId }), reportRequest]).then(function (results) {
      var messages = results[0]; var report = results[1];
      state.interviewMessages = normalizeArray(messages);
      state.interviewAnswerCount = state.interviewMessages.filter(function (message) { return String(message.role).toUpperCase() === "USER"; }).length;
      var questions = state.interviewMessages.filter(function (message) { return String(message.role).toUpperCase() === "AI"; });
      state.interviewCurrentQuestion = questions.length ? questions[questions.length - 1].content : null;
      if (report) { state.interviewReport = report; syncCompletedInterview(session, report); }
    })
      .catch(function (error) { state.interviewError = error.message || "无法读取练习记录。"; }).then(function () { state.interviewBusy = false; renderPage(pageByKey.interview); });
  }

  function renderFeatureShell(item, title, summary, innerHtml) {
    if (window.CYANCRUISE_COMPONENTS && window.CYANCRUISE_COMPONENTS.pageShell
        && typeof window.CYANCRUISE_COMPONENTS.pageShell.feature === "function") {
      window.CYANCRUISE_COMPONENTS.pageShell.feature(item, title, summary, innerHtml, pageShellContext());
      return;
    }
    els.pageHost.innerHTML =
      '<header class="feature-page-header">' +
      '<div><p class="eyebrow">青途启航</p><h2>' + escapeHtml(title) + '</h2><p class="lead">' + escapeHtml(summary) + '</p></div>' +
      pageHeaderActions(item) +
      '</header>' +
      '<div class="feature-content">' + innerHtml + '</div>';
  }

  function renderShell(item, innerHtml) {
    if (window.CYANCRUISE_COMPONENTS && window.CYANCRUISE_COMPONENTS.pageShell
        && typeof window.CYANCRUISE_COMPONENTS.pageShell.shell === "function") {
      window.CYANCRUISE_COMPONENTS.pageShell.shell(item, innerHtml, pageShellContext());
      return;
    }
    var debugMeta = "";
    if (isDebugMode()) {
      var chips = [
        '<span class="chip">' + escapeHtml(item.status) + "</span>",
        '<span class="chip">' + escapeHtml(item.audience) + "</span>"
      ];
      if (item.status === "entry-only") {
        chips.push('<span class="chip warning">entry-only</span>');
      }
      debugMeta = '<p class="eyebrow">Route: ' + escapeHtml(item.key) + '</p><div class="route-meta">' + chips.join("") + '</div>';
    }
    els.pageHost.innerHTML =
      '<header class="page-header">' +
      '<div>' + debugMeta + '<h2>' + escapeHtml(item.title) + '</h2>' +
      '<p class="lead">' + escapeHtml(item.summary) + '</p></div>' +
      pageHeaderActions(item) +
      '</header><div class="panel-grid">' + innerHtml + '</div>';
  }

  function pageHeaderActions(item) {
    if (window.CYANCRUISE_COMPONENTS && window.CYANCRUISE_COMPONENTS.pageShell
        && typeof window.CYANCRUISE_COMPONENTS.pageShell.actions === "function") {
      return window.CYANCRUISE_COMPONENTS.pageShell.actions(item, pageShellContext());
    }
    if (item.key === "assessment" && state.assessmentSelectedScaleId) {
      return '<div class="page-actions"><button type="button" class="secondary" data-assessment-back>返回</button></div>';
    }
    var parent = backRouteFor(item.key);
    if (!parent || parent === item.key) {
      return "";
    }
    var label = "返回";
    return '<div class="page-actions"><button type="button" class="secondary" data-back-route="' +
      escapeHtml(parent) + '">' + label + '</button></div>';
  }

  function pageShellContext() {
    return {
      backRouteFor: backRouteFor,
      escapeHtml: escapeHtml,
      host: els.pageHost,
      isDebugMode: isDebugMode,
      state: state
    };
  }

  function backRouteFor(key) {
    if (window.CYANCRUISE_NAVIGATION && typeof window.CYANCRUISE_NAVIGATION.backRouteFor === "function") {
      return window.CYANCRUISE_NAVIGATION.backRouteFor(key);
    }
    return parentRouteFor(key);
  }

  function parentRouteFor(key) {
    if (window.CYANCRUISE_NAVIGATION && typeof window.CYANCRUISE_NAVIGATION.parentRouteFor === "function") {
      return window.CYANCRUISE_NAVIGATION.parentRouteFor(key);
    }
    var parents = {
      "employment-home": "workbench",
      "further-study-home": "workbench",
      "resume": "employment-home",
      "resume-diagnosis": "employment-home",
      "interview": "",
      "interview-history": "interview",
      "interview-panorama": "",
      "interview-panorama-history": "interview-panorama",
      "postgraduate": "",
      "postgraduate-school": "",
      "postgraduate-plan": "",
      "postgraduate-mistake": "",
      "postgraduate-reexam": "",
      "postgraduate-recommendation": "",
      "recommendation-ranking": "",
      "recommendation-background": "",
      "recommendation-material": "",
      "recommendation-tutor": "",
      "study-abroad": "",
      "study-abroad-profile": "",
      "study-abroad-language": "",
      "study-abroad-school": "",
      "study-abroad-statement": "",
      "study-abroad-visa": "",
      "today-action": "workbench",
      "assessment": "workbench",
      "deep-profile-detail": "assessment",
      "career-plan": "workbench",
      "assistant": "workbench",
      "messages": "workbench",
      "message-detail": "messages",
      "employment-insight": "employment-home",
      "career-resources": "employment-home",
      "study-resources": "further-study-home",
      "file-upload-preview": "resume",
      "onboarding": "workbench",
      "admin-console": "workbench"
    };
    return parents[key] || "";
  }

  function enhanceAppSelects(root) {
    if (!root || !root.querySelectorAll) { return; }
    var selects = root.querySelectorAll("select:not([multiple])");
    for (var selectIndex = 0; selectIndex < selects.length; selectIndex += 1) {
      var select = selects[selectIndex];
      if (select.classList.contains("diagnosis-native-select") || select.classList.contains("app-native-select") || select.hasAttribute("data-native-select")) {
        continue;
      }
      appSelectSequence += 1;
      if (!select.id) {
        select.id = "appNativeSelect" + appSelectSequence;
      }
      var wrapper = document.createElement("div");
      wrapper.className = "app-select";
      wrapper.setAttribute("data-app-select-for", select.id);
      select.parentNode.insertBefore(wrapper, select);
      wrapper.appendChild(select);
      select.classList.add("app-native-select");
      select.setAttribute("aria-hidden", "true");
      select.setAttribute("tabindex", "-1");

      var trigger = document.createElement("button");
      trigger.type = "button";
      trigger.className = "app-select-trigger";
      trigger.setAttribute("data-app-select-trigger", "true");
      trigger.setAttribute("aria-haspopup", "listbox");
      trigger.setAttribute("aria-expanded", "false");
      trigger.disabled = select.disabled;
      var value = document.createElement("span");
      value.className = "app-select-value";
      var chevron = document.createElement("span");
      chevron.className = "app-select-chevron";
      chevron.setAttribute("aria-hidden", "true");
      trigger.appendChild(value);
      trigger.appendChild(chevron);

      var menu = document.createElement("div");
      menu.className = "app-select-menu";
      menu.id = "appSelectMenu" + appSelectSequence;
      menu.setAttribute("role", "listbox");
      trigger.setAttribute("aria-controls", menu.id);
      for (var optionIndex = 0; optionIndex < select.options.length; optionIndex += 1) {
        var nativeOption = select.options[optionIndex];
        var optionButton = document.createElement("button");
        optionButton.type = "button";
        optionButton.className = "app-select-option";
        optionButton.setAttribute("data-app-select-option", String(optionIndex));
        optionButton.setAttribute("role", "option");
        optionButton.disabled = nativeOption.disabled;
        optionButton.textContent = nativeOption.text;
        menu.appendChild(optionButton);
      }
      wrapper.appendChild(trigger);
      wrapper.appendChild(menu);
      syncAppSelect(wrapper);
    }
  }

  function syncAppSelect(wrapper) {
    if (!wrapper) { return; }
    var select = wrapper.querySelector("select.app-native-select");
    var trigger = wrapper.querySelector("[data-app-select-trigger]");
    var value = wrapper.querySelector(".app-select-value");
    if (!select || !trigger || !value) { return; }
    var selected = select.options[select.selectedIndex];
    value.textContent = selected ? selected.text : "请选择";
    trigger.disabled = select.disabled;
    var options = wrapper.querySelectorAll("[data-app-select-option]");
    for (var index = 0; index < options.length; index += 1) {
      var active = index === select.selectedIndex;
      options[index].classList.toggle("active", active);
      options[index].setAttribute("aria-selected", active ? "true" : "false");
    }
  }

  function closestAppSelect(node) {
    while (node && node !== document) {
      if (node.classList && node.classList.contains("app-select")) { return node; }
      node = node.parentNode;
    }
    return null;
  }

  function closeAppSelects(except) {
    if (!els.pageHost) { return; }
    var openSelects = els.pageHost.querySelectorAll(".app-select.open");
    for (var index = 0; index < openSelects.length; index += 1) {
      if (openSelects[index] === except) { continue; }
      openSelects[index].classList.remove("open");
      var trigger = openSelects[index].querySelector("[data-app-select-trigger]");
      if (trigger) { trigger.setAttribute("aria-expanded", "false"); }
    }
  }

  function toggleAppSelect(wrapper, focusDirection) {
    if (!wrapper) { return; }
    var trigger = wrapper.querySelector("[data-app-select-trigger]");
    if (!trigger || trigger.disabled) { return; }
    var opening = focusDirection ? true : !wrapper.classList.contains("open");
    closeAppSelects(opening ? wrapper : null);
    if (opening) {
      var rect = trigger.getBoundingClientRect();
      var estimatedMenuHeight = Math.min(288, Math.max(58, wrapper.querySelectorAll("[data-app-select-option]").length * 48 + 14));
      var viewportHeight = window.innerHeight || document.documentElement.clientHeight;
      wrapper.classList.toggle("drop-up", viewportHeight - rect.bottom < estimatedMenuHeight && rect.top > viewportHeight - rect.bottom);
    }
    wrapper.classList.toggle("open", opening);
    trigger.setAttribute("aria-expanded", opening ? "true" : "false");
    if (opening && focusDirection) {
      var options = wrapper.querySelectorAll("[data-app-select-option]:not(:disabled)");
      if (!options.length) { return; }
      var selected = wrapper.querySelector(".app-select-option.active:not(:disabled)");
      var target = selected || (focusDirection < 0 ? options[options.length - 1] : options[0]);
      window.setTimeout(function () { target.focus(); }, 0);
    }
  }

  function chooseAppSelectOption(optionButton, restoreKeyboardFocus) {
    var wrapper = closestAppSelect(optionButton);
    var select = wrapper && wrapper.querySelector("select.app-native-select");
    var trigger = wrapper && wrapper.querySelector("[data-app-select-trigger]");
    var optionIndex = Number(optionButton.getAttribute("data-app-select-option"));
    if (!select || !select.options[optionIndex] || select.options[optionIndex].disabled) { return; }
    var changed = select.selectedIndex !== optionIndex;
    select.selectedIndex = optionIndex;
    syncAppSelect(wrapper);
    closeAppSelects();
    if (restoreKeyboardFocus && trigger) {
      trigger.focus();
    } else {
      optionButton.blur();
      if (trigger) { trigger.blur(); }
    }
    if (changed) {
      var changeEvent;
      if (typeof Event === "function") {
        changeEvent = new Event("change", { bubbles: true });
      } else {
        changeEvent = document.createEvent("Event");
        changeEvent.initEvent("change", true, false);
      }
      select.dispatchEvent(changeEvent);
    }
  }

  function handleAppSelectKeydown(event) {
    var trigger = findAttributeTarget(event.target, "data-app-select-trigger");
    if (trigger && event.key === "Escape") {
      event.preventDefault();
      closeAppSelects();
      trigger.focus();
      return;
    }
    if (trigger && (event.key === "Enter" || event.key === " " || event.key === "ArrowDown" || event.key === "ArrowUp")) {
      event.preventDefault();
      var direction = event.key === "ArrowUp" ? -1 : event.key === "ArrowDown" ? 1 : 0;
      toggleAppSelect(closestAppSelect(trigger), direction);
      return;
    }
    var option = findAttributeTarget(event.target, "data-app-select-option");
    if (!option) { return; }
    var wrapper = closestAppSelect(option);
    if (event.key === "Escape") {
      event.preventDefault();
      closeAppSelects();
      var wrapperTrigger = wrapper.querySelector("[data-app-select-trigger]");
      if (wrapperTrigger) { wrapperTrigger.focus(); }
      return;
    }
    if (event.key !== "ArrowDown" && event.key !== "ArrowUp" && event.key !== "Home" && event.key !== "End") { return; }
    event.preventDefault();
    var options = Array.prototype.slice.call(wrapper.querySelectorAll("[data-app-select-option]:not(:disabled)"));
    var current = options.indexOf(option);
    var next = event.key === "Home" ? 0 : event.key === "End" ? options.length - 1 :
      (current + (event.key === "ArrowUp" ? -1 : 1) + options.length) % options.length;
    if (options[next]) { options[next].focus(); }
  }

  function handlePageHostClick(event) {
    var appSelectOption = findAttributeTarget(event.target, "data-app-select-option");
    if (appSelectOption) {
      event.preventDefault();
      event.stopPropagation();
      chooseAppSelectOption(appSelectOption, event.detail === 0);
      return;
    }
    var appSelectTrigger = findAttributeTarget(event.target, "data-app-select-trigger");
    if (appSelectTrigger) {
      event.preventDefault();
      event.stopPropagation();
      toggleAppSelect(closestAppSelect(appSelectTrigger));
      return;
    }
    if (event.target && event.target.classList && event.target.classList.contains("app-native-select")) {
      event.preventDefault();
      return;
    }
    closeAppSelects();
    var panoramaTarget = findAttributeTarget(event.target, "data-panorama-action");
    if (panoramaTarget) { event.preventDefault(); event.stopPropagation(); handlePanoramaAction(panoramaTarget); return; }
    var interviewTarget = findAttributeTarget(event.target, "data-interview-action");
    if (interviewTarget) { event.preventDefault(); event.stopPropagation(); handleInterviewAction(interviewTarget); return; }
    var planFocusTarget = findPlanFocusTarget(event.target);
    if (planFocusTarget) {
      event.preventDefault();
      event.stopPropagation();
      focusPlanPhase(planFocusTarget.getAttribute("data-plan-focus"));
      return;
    }
    var studyPlanTarget = findAttributeTarget(event.target, "data-ensure-study-plan");
    if (studyPlanTarget && !studyPlanTarget.disabled) {
      event.preventDefault();
      event.stopPropagation();
      ensureStudyPlan(true);
      return;
    }
    var ensurePlanTarget = findEnsurePlanTarget(event.target);
    if (ensurePlanTarget) {
      event.preventDefault();
      event.stopPropagation();
      requestAgentCareerPlanGeneration();
      return;
    }
    var studyDirectionTarget = findAttributeTarget(event.target, "data-save-study-direction");
    if (studyDirectionTarget) { event.preventDefault(); event.stopPropagation(); saveStudyCenterDirection(); return; }
    var studyMaterialUploadTarget = findAttributeTarget(event.target, "data-upload-study-material");
    if (studyMaterialUploadTarget && !studyMaterialUploadTarget.disabled) {
      event.preventDefault(); event.stopPropagation(); uploadStudyPlanningMaterial(); return;
    }
    var studyMaterialDeleteTarget = findAttributeTarget(event.target, "data-delete-study-material");
    if (studyMaterialDeleteTarget) {
      event.preventDefault(); event.stopPropagation();
      deleteStudyPlanningMaterial(studyMaterialDeleteTarget.getAttribute("data-delete-study-material"));
      return;
    }
    var resourceTarget = findResourceDetailTarget(event.target);
    if (resourceTarget) {
      event.preventDefault();
      event.stopPropagation();
      showResourceDetailDialog(resourceTarget.getAttribute("data-resource-detail"));
      return;
    }
    var deepProfileGenerateTarget = findAttributeTarget(event.target, "data-deep-profile-generate");
    if (deepProfileGenerateTarget) {
      event.preventDefault();
      event.stopPropagation();
      generateDeepProfile();
      return;
    }
    var deepProfileRecordTarget = findAttributeTarget(event.target, "data-deep-profile-record");
    if (deepProfileRecordTarget) {
      event.preventDefault();
      event.stopPropagation();
      selectDeepProfileRecord(deepProfileRecordTarget.getAttribute("data-deep-profile-record"));
      return;
    }
    var assessmentAiInterpretationTarget = findAttributeTarget(event.target, "data-assessment-ai-interpretation");
    if (assessmentAiInterpretationTarget) {
      event.preventDefault();
      event.stopPropagation();
      generateAssessmentAiInterpretation(assessmentAiInterpretationTarget.getAttribute("data-assessment-ai-interpretation"));
      return;
    }
    var assessmentRetakeTarget = findAttributeTarget(event.target, "data-assessment-retake");
    if (assessmentRetakeTarget) {
      event.preventDefault();
      event.stopPropagation();
      startNewAssessmentScale(assessmentRetakeTarget.getAttribute("data-assessment-retake"));
      return;
    }
    var assessmentOptionTarget = findAssessmentOptionTarget(event.target);
    if (assessmentOptionTarget) {
      event.preventDefault();
      event.stopPropagation();
      chooseAssessmentOption(
        assessmentOptionTarget.getAttribute("data-assessment-question"),
        assessmentOptionTarget.getAttribute("data-assessment-option")
      );
      return;
    }
    var assessmentScaleTarget = findAssessmentScaleTarget(event.target);
    if (assessmentScaleTarget) {
      event.preventDefault();
      event.stopPropagation();
      startAssessmentScale(assessmentScaleTarget.getAttribute("data-assessment-scale"));
      return;
    }
    var assessmentBackTarget = findAssessmentBackTarget(event.target);
    if (assessmentBackTarget) {
      event.preventDefault();
      event.stopPropagation();
      backToAssessmentList();
      return;
    }
    var assessmentPrevTarget = findAssessmentPrevTarget(event.target);
    if (assessmentPrevTarget) {
      event.preventDefault();
      event.stopPropagation();
      previousAssessmentQuestion();
      return;
    }
    var assessmentNextTarget = findAssessmentNextTarget(event.target);
    if (assessmentNextTarget) {
      event.preventDefault();
      event.stopPropagation();
      nextAssessmentQuestion();
      return;
    }
    var assessmentSubmitTarget = findAssessmentSubmitTarget(event.target);
    if (assessmentSubmitTarget) {
      event.preventDefault();
      event.stopPropagation();
      submitAssessment();
      return;
    }
    var diagnosisResumeTarget = findAttributeTarget(event.target, "data-diagnose-resume");
    if (diagnosisResumeTarget) {
      event.preventDefault();
      event.stopPropagation();
      openResumeDiagnosisFor(diagnosisResumeTarget.getAttribute("data-diagnose-resume"));
      return;
    }
    var target = findPageLinkTarget(event.target);
    if (!target) {
      return;
    }
    event.preventDefault();
    event.stopPropagation();
    var backRoute = target.getAttribute("data-back-route");
    if (backRoute) {
      navigateBackToRoute(backRoute);
      return;
    }
    navigateToRoute(target.getAttribute("data-link"));
  }

  function openResumeDiagnosisFor(resumeId) {
    if (!resumeId) {
      navigateToRoute("resume-diagnosis");
      return;
    }
    state.selectedDiagnosisResumeId = String(resumeId);
    state.diagnosisDraft = {
      resumeId: String(resumeId),
      targetJob: "",
      jobDescription: "",
      resumeText: ""
    };
    state.diagnosisResult = null;
    state.diagnosisMessage = null;
    setUserStorageItem("cyancruise.pendingDiagnosisResumeId", String(resumeId));
    navigateToRoute("resume-diagnosis");
  }

  function findAttributeTarget(node, attribute) {
    while (node && node !== els.pageHost) {
      if (node.getAttribute && node.getAttribute(attribute) != null) return node.disabled ? null : node;
      node = node.parentNode;
    }
    return null;
  }

  function retryDiagnosisResumeList() {
    state.diagnosisMessage = { type: "info", text: "正在重新加载简历。" };
    state.resumeListError = null;
    state.resumes = null;
    renderPage(pageByKey[state.route]);
  }

  function handlePageHostChange(event) {
    var target = event.target;
    if (!target || !target.getAttribute) {
      return;
    }
    if (target.classList && target.classList.contains("app-native-select")) {
      syncAppSelect(closestAppSelect(target));
    }
    if (target.id === "interviewPosition" || target.id === "interviewResume" || target.id === "interviewDifficulty") {
      syncInterviewSetupDraftFromPage();
      return;
    }
    if (target.type !== "checkbox") return;
    var taskId = target.getAttribute("data-plan-task");
    if (taskId) {
      setPlanTaskChecked(taskId, target.checked);
      return;
    }
    var dailyTaskId = target.getAttribute("data-daily-task");
    if (dailyTaskId) {
      updateDailyTask(dailyTaskId, target.getAttribute("data-source-task"), target.checked);
    }
  }

  function updateDailyTask(taskId, sourceTaskId, completed) {
    var endpoint = currentDailyTaskUpdateEndpoint();
    if (!hasUserIdentity() || !endpoint) return;
    post(endpoint, {
      userId: state.identity.userId,
      request: { taskId: taskId, completed: !!completed }
    }).then(function (dailyPlan) {
      state.dailyPlan = dailyPlan || {};
      var plan = state.plan && !state.plan.unavailable ? state.plan : {};
      var targetRole = currentPlanningTarget(plan);
      var phases = normalizeArray(plan.phases);
      var progressState = readPlanProgress(plan, targetRole, phases);
      if (sourceTaskId) {
        progressState.checked[sourceTaskId] = !!completed;
        progressState.checked[taskId] = !!completed;
      }
      mergeDailyCompletionIntoProgress(state.dailyPlan, progressState, phases);
      syncPlanPhaseCursor(phases, progressState);
      progressState.phaseCursorInitialized = true;
      persistPlanProgress(plan, targetRole, progressState);
      return refreshCareerPlanAfterDailyUpdate();
    }).then(function () {
      updateOverviewCards();
      renderPage(pageByKey[state.route]);
    }).catch(function (error) {
      renderPage(pageByKey[state.route]);
      showMessage("error", "任务状态未保存", error.message || "请稍后重试。");
    });
  }

  function loadDailyPlan() {
    var endpoint = currentDailyPlanEndpoint();
    if (!hasUserIdentity() || !endpoint || isFilePreview()) return Promise.resolve(null);
    return post(endpoint, state.identity.userId).then(function (dailyPlan) {
      state.dailyPlan = dailyPlan || {};
      if (isStudyRoute()) state.studyDailyPlan = state.dailyPlan;
      else state.employmentDailyPlan = state.dailyPlan;
      return state.dailyPlan;
    });
  }

  function refreshCareerPlanAfterDailyUpdate() {
    var endpoint = currentPlanEndpoint();
    if (!hasUserIdentity() || !endpoint || isFilePreview()) return Promise.resolve(null);
    return post(endpoint, state.identity.userId).then(function (plan) {
      if (plan && !plan.unavailable) {
        state.plan = plan;
        if (isStudyRoute()) state.studyPlan = plan;
        else state.employmentPlan = plan;
        var targetRole = currentPlanningTarget(plan);
        var phases = normalizeArray(plan.phases);
        var progressState = readPlanProgress(plan, targetRole, phases);
        mergeDailyCompletionIntoProgress(state.dailyPlan, progressState, phases);
        syncPlanPhaseCursor(phases, progressState);
        progressState.phaseCursorInitialized = true;
        persistPlanProgress(plan, targetRole, progressState);
      }
      return state.plan;
    }).catch(function () {
      return state.plan;
    });
  }

  function syncInterviewSetupDraftFromPage() {
    state.interviewSetupPosition = valueOf("interviewPosition");
    state.interviewSetupResumeId = valueOf("interviewResume");
    state.interviewSetupDifficulty = normalizeInterviewDifficulty(valueOf("interviewDifficulty"));
  }

  function normalizeInterviewDifficulty(value) {
    var text = trim(value);
    if (text === "Easy" || text === "Hard") return text;
    return "Normal";
  }

  function findPageLinkTarget(node) {
    while (node && node !== els.pageHost) {
      if (node.getAttribute && (node.getAttribute("data-link") || node.getAttribute("data-back-route"))) {
        return node.disabled ? null : node;
      }
      node = node.parentNode;
    }
    return null;
  }

  function findAssessmentOptionTarget(node) {
    while (node && node !== els.pageHost) {
      if (node.getAttribute && node.getAttribute("data-assessment-option")) {
        return node.disabled ? null : node;
      }
      node = node.parentNode;
    }
    return null;
  }

  function findAssessmentScaleTarget(node) {
    while (node && node !== els.pageHost) {
      if (node.getAttribute && node.getAttribute("data-assessment-scale")) {
        return node.disabled ? null : node;
      }
      node = node.parentNode;
    }
    return null;
  }

  function findAssessmentBackTarget(node) {
    while (node && node !== els.pageHost) {
      if (node.getAttribute && node.getAttribute("data-assessment-back") != null) {
        return node.disabled ? null : node;
      }
      node = node.parentNode;
    }
    return null;
  }

  function findAssessmentPrevTarget(node) {
    while (node && node !== els.pageHost) {
      if (node.getAttribute && node.getAttribute("data-assessment-prev") != null) {
        return node.disabled ? null : node;
      }
      node = node.parentNode;
    }
    return null;
  }

  function findAssessmentNextTarget(node) {
    while (node && node !== els.pageHost) {
      if (node.getAttribute && node.getAttribute("data-assessment-next") != null) {
        return node.disabled ? null : node;
      }
      node = node.parentNode;
    }
    return null;
  }

  function findAssessmentSubmitTarget(node) {
    while (node && node !== els.pageHost) {
      if (node.getAttribute && node.getAttribute("data-assessment-submit") != null) {
        return node.disabled ? null : node;
      }
      node = node.parentNode;
    }
    return null;
  }

  function findPlanFocusTarget(node) {
    while (node && node !== els.pageHost) {
      if (node.getAttribute && node.getAttribute("data-plan-focus")) {
        return node.disabled ? null : node;
      }
      node = node.parentNode;
    }
    return null;
  }

  function setPlanTaskChecked(taskId, checked) {
    if (!taskId) {
      return;
    }
    var plan = state.plan && !state.plan.unavailable ? state.plan : {};
    var targetRole = currentPlanningTarget(plan);
    var phases = normalizeArray(plan.phases);
    var progressState = readPlanProgress(plan, targetRole, phases);
    progressState.checked[taskId] = !!checked;
    syncPlanPhaseCursor(phases, progressState);
    progressState.phaseCursorInitialized = true;
    persistPlanProgress(plan, targetRole, progressState);
    updateOverviewCards();
    renderPage(pageByKey[state.route]);
  }

  function focusPlanPhase(phaseId) {
    if (!phaseId || state.route !== "career-plan") {
      return;
    }
    var plan = state.plan && !state.plan.unavailable ? state.plan : {};
    var targetRole = currentPlanningTarget(plan);
    var progressState = readPlanProgress(plan, targetRole, normalizeArray(plan.phases));
    progressState.activePhaseId = phaseId;
    persistPlanProgress(plan, targetRole, progressState);
    renderPage(pageByKey[state.route]);
  }

  function findEnsurePlanTarget(node) {
    while (node && node !== els.pageHost) {
      if (node.getAttribute && node.getAttribute("data-ensure-plan") != null) {
        return node.disabled ? null : node;
      }
      node = node.parentNode;
    }
    return null;
  }

  function findResourceDetailTarget(node) {
    while (node && node !== els.pageHost) {
      if (node.getAttribute && node.getAttribute("data-resource-detail")) {
        return node.disabled ? null : node;
      }
      node = node.parentNode;
    }
    return null;
  }

  function navigateToRoute(route) {
    var key = normalizeRoute(route);
    if (!pageByKey[key]) {
      showMessage("warning", "页面不可用", "未找到页面: " + key);
      return;
    }
    if (state.route && state.route !== key) {
      rememberRouteScroll(state.route);
      state.previousRoute = state.route;
      rememberReturnRoute(key, state.route);
    }
    state.route = key;
    if (window.location.hash !== "#" + key) {
      pushRouteToLocation(key);
    }
    markActiveNav();
    renderPage(pageByKey[key]);
    window.setTimeout(function () {
      window.scrollTo(0, 0);
    }, 0);
  }

  function navigateBackToRoute(route) {
    var key = normalizeRoute(route);
    if (!pageByKey[key]) {
      showMessage("warning", "页面不可用", "未找到页面: " + key);
      return;
    }
    rememberRouteScroll(state.route);
    clearReturnRoute(state.route);
    state.previousRoute = "";
    state.route = key;
    if (window.location.hash !== "#" + key) {
      pushRouteToLocation(key);
    }
    markActiveNav();
    renderPage(pageByKey[key]);
    restoreRouteScroll(key);
  }

  function rememberRouteScroll(route) {
    var key = normalizeRoute(route);
    if (!pageByKey[key]) {
      return;
    }
    state.scrollPositions[key] = currentScrollTop();
  }

  function rememberReturnRoute(route, sourceRoute) {
    var key = normalizeRoute(route);
    var source = normalizeRoute(sourceRoute);
    if (key === "workbench") {
      return;
    }
    if (!pageByKey[key] || !pageByKey[source] || key === source) {
      return;
    }
    state.returnRoutes[key] = source;
  }

  function clearReturnRoute(route) {
    var key = normalizeRoute(route);
    if (state.returnRoutes) {
      delete state.returnRoutes[key];
    }
  }

  function restoreRouteScroll(route) {
    var key = normalizeRoute(route);
    var y = state.scrollPositions[key];
    window.setTimeout(function () {
      window.scrollTo(0, typeof y === "number" ? y : 0);
    }, 0);
  }

  function currentScrollTop() {
    return window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;
  }

  function overviewRows(goal) {
    if (goal === "study") {
      var selection = state.studyCenterSelection || {};
      return [
        ["目标院校", textFromSnapshot("onboarding.targetSchool") || "待确认"],
        ["升学方向", studyCenterDirectionLabel(firstText(selection.direction, getValue(state.studyPlan, "studyDirection")))],
        ["今日行动", todayOverviewStatus()],
        ["路径规划", state.plan && !state.plan.unavailable && state.plan.hasPlan !== false ? "已生成" : "待生成"]
      ];
    }
    return [
      ["目标岗位", textFromSnapshot("preferences.targetRole", "onboarding.targetRole", "resume.targetJob") || "待确认"],
      ["今日行动", todayOverviewStatus()],
      ["简历记录", Array.isArray(state.resumes) ? state.resumes.length + " 份" : "待加载"],
      ["面试练习", Array.isArray(state.interviews) ? state.interviews.length + " 次" : "待加载"]
    ];
  }

  function todayOverviewStatus() {
    if (state.plan && !state.plan.unavailable) {
      var view = buildPlanViewModel();
      if (view.dailyPlan && view.dailyPlan.nextItem) {
        return firstText(view.dailyPlan.nextItem.text, "今日可推进");
      }
      if (view.dailyPlan && view.dailyPlan.items && view.dailyPlan.items.length
          && Number(view.dailyPlan.completedCount || 0) === Number(view.dailyPlan.totalCount || view.dailyPlan.items.length)) {
        return "今日小事已完成";
      }
      if (view.dailyPlan && view.dailyPlan.items && view.dailyPlan.items.length) {
        var firstDailyItem = view.dailyPlan.items[0];
        return firstText(firstDailyItem && firstDailyItem.text, firstDailyItem, "今日可推进");
      }
      return "等待任务拆解";
    }
    if (state.planEnsuring) {
      return "路线生成中";
    }
    return "等待路径规划";
  }

  function overviewStrip(goal) {
    return '<section class="overview-strip">' + overviewRows(goal).map(function (row) {
      return '<article><span class="label">' + escapeHtml(row[0]) + '</span><strong>' + escapeHtml(row[1]) + '</strong></article>';
    }).join("") + '</section>';
  }

  function featureCards(cards) {
    if (window.CYANCRUISE_COMPONENTS && window.CYANCRUISE_COMPONENTS.featureCard
        && typeof window.CYANCRUISE_COMPONENTS.featureCard.renderCards === "function") {
      return window.CYANCRUISE_COMPONENTS.featureCard.renderCards(cards, {
        escapeHtml: escapeHtml,
        pageByKey: pageByKey
      });
    }
    return cards.map(function (card) {
      var linked = !!card.route && pageByKey[card.route] && card.status !== "即将接入";
      var attrs = linked ? ' data-link="' + escapeHtml(card.route) + '"' + (card.platformLink ? ' data-platform-link="true"' : "") : ' disabled aria-disabled="true"';
      var cls = linked ? "feature-card" : "feature-card disabled";
      return '<button type="button" class="' + cls + '"' + attrs + '>' +
        '<span class="feature-icon">' + escapeHtml(card.icon) + '</span>' +
        '<span class="feature-copy"><strong>' + escapeHtml(card.title) + '</strong><small>' + escapeHtml(card.summary) + '</small></span>' +
        '</button>';
    }).join("");
  }

  function renderIdentityRequired(item) {
    renderShell(item, statePanel("需要身份", "生产模式等待 Cosmic 登录上下文；开发验证请使用 ?identityMode=development&userId=xxx。", "warning"));
    showMessage("warning", "已阻止受保护调用", item.title + " 需要 userId，页面没有使用硬编码身份。");
  }

  function renderForbidden(item) {
    hideMessage();
    els.pageHost.innerHTML =
      '<section class="admin-access-denied" aria-labelledby="adminAccessDeniedTitle">' +
      '<div class="admin-access-denied-content">' +
      '<h2 id="adminAccessDeniedTitle">管理后台</h2>' +
      '<p>管理员治理入口，仅对 ADMIN 或平台管理员开放。</p>' +
      '</div></section>';
  }

  function loadOverview() {
    if (!hasUserIdentity()) {
      renderPreview();
      return;
    }
    if (isFilePreview()) {
      renderPreview();
      showMessage("info", "契约预览", "file:// 模式不调用后端；部署到苍穹或 Web 服务后可请求 WebAPI。");
      return;
    }
    loadOverviewData().then(function (overview) {
      state.snapshot = overview.snapshot;
      state.today = overview.today;
      state.resumes = overview.resumes;
      state.plan = overview.plan;
      state.employmentPlan = overview.employmentPlan;
      state.studyPlan = overview.studyPlan;
      state.interviews = overview.interviews;
      state.dailyPlan = overview.dailyPlan;
      state.employmentDailyPlan = overview.employmentDailyPlan;
      state.studyDailyPlan = overview.studyDailyPlan;
      syncCurrentRoutePlanningState();
      updateOverviewCards();
      renderPage(pageByKey[state.route]);
    }).catch(function (error) {
      renderPreview();
      showMessage("error", "加载失败", error.message || "后端暂不可用，已保留可恢复页面状态。");
    });
  }

  function loadOverviewData() {
    if (window.CYANCRUISE_SERVICES && window.CYANCRUISE_SERVICES.profile
        && typeof window.CYANCRUISE_SERVICES.profile.loadOverview === "function") {
      return window.CYANCRUISE_SERVICES.profile.loadOverview({
        endpoints: endpoints,
        post: post,
        state: state,
        asUnavailable: asUnavailable,
        normalizeArray: normalizeArray
      });
    }
    return post(endpoints.snapshot, state.identity.userId).catch(asUnavailable).then(function (snapshot) {
      return Promise.all([
        post(endpoints.today, state.identity.userId).catch(asUnavailable),
        post(endpoints.resumes, state.identity.userId).catch(asUnavailable),
        post(endpoints.plan, state.identity.userId).catch(asUnavailable),
        post(endpoints.interviews, state.identity.userId).catch(asUnavailable),
        post(endpoints.dailyPlan, state.identity.userId).catch(asUnavailable),
        post(endpoints.studyPlan, state.identity.userId).catch(asUnavailable),
        post(endpoints.studyDailyPlan, state.identity.userId).catch(asUnavailable)
      ]).then(function (results) {
        var study = snapshot && snapshot.onboarding && snapshot.onboarding.routeGoal === "study";
        return { snapshot: snapshot, today: results[0], resumes: normalizeArray(results[1]),
          employmentPlan: results[2], interviews: normalizeArray(results[3]), employmentDailyPlan: results[4],
          studyPlan: results[5], studyDailyPlan: results[6],
          plan: study ? results[5] : results[2], dailyPlan: study ? results[6] : results[4] };
      });
    });
  }

  function submitOnboarding(event) {
    event.preventDefault();
    if (!hasUserIdentity()) {
      showMessage("warning", "需要身份", "保存个人情况前需要 Cosmic 身份或显式开发身份。");
      return;
    }
    var request = {
      identityType: valueOf("identityType"),
      targetRole: valueOf("onboardingTargetRole"),
      resumeStatus: valueOf("resumeStatus"),
      preference: valueOf("preference")
    };
    if (isFilePreview()) {
      var previewPreviousTarget = currentProfileTargetRole();
      setUserStorageItem("cyancruise.previewProfile", JSON.stringify(request));
      state.snapshot = { onboarding: request, preferences: { targetRole: request.targetRole } };
      syncCurrentRoutePlanningState();
      updateOverviewCards();
      refreshPlanAfterProfileTargetChange(previewPreviousTarget);
      showMessage("info", "已保存到本地预览", "file:// 模式不调用后端。");
      return;
    }
    var previousTarget = currentProfileTargetRole();
    post(endpoints.onboarding, { userId: state.identity.userId, request: request }).then(function (snapshot) {
      state.snapshot = snapshot;
      syncCurrentRoutePlanningState();
      updateOverviewCards();
      if (!refreshPlanAfterProfileTargetChange(previousTarget)) {
        renderPage(pageByKey[state.route]);
      }
      showMessage("info", "已保存", "个人情况已写入职业画像快照。");
    }).catch(function (error) {
      showMessage("error", "保存失败", error.message || "个人情况 WebAPI 暂不可用。");
    });
  }

  function readHomeIntent() {
    return parseUserStorageJson("cyancruise.homeIntent") || {};
  }

  function submitHomeIntent(event) {
    event.preventDefault();
    var goal = valueOf("homeGoal");
    var targetInput = $("homeTargetRole");
    syncHomeTargetField(goal);
    var targetRole = goal === "study"
      ? firstText(targetInput && targetInput.getAttribute("data-role-value"), "")
      : valueOf("homeTargetRole");
    var targetSchool = goal === "study"
      ? valueOf("homeTargetRole")
      : firstText(targetInput && targetInput.getAttribute("data-school-value"), "");
    var intent = {
      goal: goal,
      targetRole: targetRole,
      targetSchool: targetSchool,
      preference: "",
      identityType: valueOf("profileIdentityType"),
      educationStage: valueOf("profileEducationStage"),
      school: valueOf("profileSchool"),
      major: valueOf("profileMajor"),
      schoolMajor: valueOf("profileMajor"),
      resumeStatus: valueOf("resumeStatus"),
      selectedResumeId: valueOf("profileSelectedResume"),
      experience: valueOf("profileExperience"),
      selfProfileSupplement: valueOf("selfProfileSupplement")
    };
    setUserStorageItem("cyancruise.homeIntent", JSON.stringify(intent));
    var request = {
      identityType: intent.identityType,
      stage: intent.educationStage,
      educationStage: intent.educationStage,
      school: intent.school,
      major: intent.major,
      schoolMajor: intent.major,
      education: {
        school: intent.school,
        major: intent.major
      },
      targetRole: intent.targetRole,
      targetSchool: intent.targetSchool,
      resumeStatus: intent.resumeStatus,
      selectedResumeId: intent.selectedResumeId,
      experience: intent.experience,
      selfProfileSupplement: intent.selfProfileSupplement,
      routeGoal: intent.goal,
      preference: "路线：" + labelForGoal(intent.goal)
    };
    if (!hasUserIdentity() || isFilePreview()) {
      var previewPreviousTarget = currentProfileTargetRole();
      setUserStorageItem("cyancruise.previewProfile", JSON.stringify(request));
      state.snapshot = { onboarding: request, preferences: { targetRole: request.targetRole } };
      syncCurrentRoutePlanningState();
      updateOverviewCards();
      refreshPlanAfterProfileTargetChange(previewPreviousTarget);
      setUserStorageItem("cyancruise.homeIntentEditing", "true");
      renderPage(pageByKey[state.route]);
      showMessage("info", "已保存", "自画像草稿已保存到当前浏览器。");
      return;
    }
    var previousTarget = currentProfileTargetRole();
    var previousResumeId = getValue(state.snapshot, "onboarding.selectedResumeId");
    post(endpoints.onboarding, { userId: state.identity.userId, request: request }).then(function (snapshot) {
      setUserStorageItem("cyancruise.homeIntentSaved", "true");
      removeUserStorageItem("cyancruise.homeIntentEditing");
      removeUserStorageItem("cyancruise.previewProfile");
      state.snapshot = snapshot;
      syncCurrentRoutePlanningState();
      updateOverviewCards();
      if (!refreshPlanAfterProfileTargetChange(previousTarget, previousResumeId)) {
        renderPage(pageByKey[state.route]);
      }
      showMessage("info", "已保存", "自画像草稿已保存。");
    }).catch(function (error) {
      setUserStorageItem("cyancruise.homeIntentEditing", "true");
      renderPage(pageByKey[state.route]);
      showMessage("warning", "已本地保存", "平台暂未写入成功，但自画像草稿已保存在当前浏览器。");
    });
  }

  function currentProfileTargetRole() {
    return textFromSnapshot("preferences.targetRole", "onboarding.targetRole", "resume.targetJob");
  }

  function refreshPlanAfterProfileTargetChange(previousTarget, previousResumeId) {
    var nextTarget = currentProfileTargetRole();
    var nextResumeId = getValue(state.snapshot, "onboarding.selectedResumeId");
    var resumeChanged = previousResumeId !== undefined && String(firstText(previousResumeId, "")) !== String(firstText(nextResumeId, ""));
    if (sameTargetRole(previousTarget, nextTarget) && !resumeChanged) {
      return false;
    }
    state.plan = null;
    state.planProgress = null;
    if (!isPlanVisibleRoute()) {
      return false;
    }
    if (!hasUserIdentity() && !isFilePreview()) {
      return false;
    }
    ensureCurrentRoutePlan();
    return true;
  }

  function isPlanVisibleRoute() {
    return state.route === "employment-home" || state.route === "career-plan" || state.route === "today-action";
  }

  function labelForIdentity(identityType) {
    var map = {
      student: "在校学生",
      graduate: "应届毕业生",
      career_switcher: "转行求职"
    };
    return map[identityType] || identityType || "未填写";
  }

  function labelForEducationStage(stage) {
    var map = {
      undergraduate: "本科",
      postgraduate: "研究生",
      vocational: "高职/专科",
      working: "已工作",
      other: "其他"
    };
    return map[stage] || stage || "未填写";
  }

  function labelForResumeStatus(status) {
    var map = {
      none: "还没有简历",
      draft: "已有初稿",
      ready: "已有可投递简历",
      materials: "已有升学材料"
    };
    return map[status] || status || "未填写";
  }

  function labelForGoal(goal) {
    if (goal === "study") {
      return "深造";
    }
    return "就业";
  }

  function renderPreview() {
    var preview = readPreviewProfile();
    state.snapshot = preview || {};
    state.today = {
      title: "等待路径规划",
      summary: "今日行动会根据路径规划里的当前阶段和本周计划拆解生成。"
    };
    state.resumes = [];
    state.plan = null;
    state.interviews = [];
    updateOverviewCards();
  }

  function updateOverviewCards() {
    var target = isStudyRoute()
      ? textFromSnapshot("onboarding.targetSchool")
      : textFromSnapshot("preferences.targetRole", "onboarding.targetRole", "resume.targetJob");
    var resume = getValue(state.snapshot, "resume") || {};
    var assessment = getValue(state.snapshot, "assessment") || {};
    var score = calculateReadiness(target, resume, assessment);
    els.targetRole.textContent = target || "待确认";
    els.profileStatus.textContent = target ? "方向已建立" : "需要完善个人情况";
    els.readinessScore.textContent = score + "%";
    els.readinessHint.textContent = score >= 80 ? "可以进入专项练习" : "继续补齐测评、简历和面试信号";
    els.resumeState.textContent = Array.isArray(state.resumes) && state.resumes.length ? "已有 " + state.resumes.length + " 份" : resume.lastResumeId ? "已有记录" : "待补入";
    els.resumeHint.textContent = resume.diagnosisScore ? "诊断分 " + resume.diagnosisScore : "建议维护最近简历";
    els.interviewState.textContent = Array.isArray(state.interviews) && state.interviews.length ? "已有 " + state.interviews.length + " 次" : "待练习";
    els.interviewHint.textContent = "从模拟面试页开始练习";
  }

  function updateIdentityState() {
    var identity = state.identity || {};
    if (identity.userId) {
      els.userIdInput.value = identity.userId;
    }
    if (!identity.userId) {
      els.identityTitle.textContent = "等待平台身份";
      els.identityHint.textContent = "生产模式只接受 Cosmic 登录上下文；开发验证请使用 identityMode=development。";
      return;
    }
    els.identityTitle.textContent = identity.mode === "production" ? "Cosmic 平台身份" : "开发验证身份";
    els.identityHint.textContent = identity.userId + " | " + identity.source;
  }

  function saveDevelopmentUser() {
    var userId = trim(els.userIdInput.value);
    if (!userId) {
      localStorage.removeItem("cyancruise.userId");
      state.identity = resolveIdentity();
      updateIdentityState();
      renderPage(pageByKey[state.route]);
      showMessage("warning", "需要 userId", "请输入开发验证 userId 后再加载。");
      return;
    }
    localStorage.setItem("cyancruise.userId", userId);
    state.identity = {
      mode: "development",
      userId: userId,
      adminId: trim(localStorage.getItem("cyancruise.adminId")),
      roles: parseRoles(localStorage.getItem("cyancruise.roles")),
      source: "manual-input"
    };
    updateIdentityState();
    loadOverview();
  }

  function resolveIdentity() {
    var mode = resolveIdentityMode();
    if (mode === "production") {
      return resolveCosmicIdentity();
    }
    return resolveDevelopmentIdentity();
  }

  function resolveIdentityMode() {
    var params = new URLSearchParams(window.location.search);
    var mode = trim(params.get("identityMode")).toLowerCase();
    if (mode === "development" || mode === "dev") {
      return "development";
    }
    return "production";
  }

  function resolveCosmicIdentity() {
    var resolved = resolveCosmicContext();
    var context = resolved.context || {};
    var userId = firstText(
      context.userId,
      context.personId,
      context.operatorId,
      context.uid,
      context.id,
      context.number,
      getValue(context, "user.id"),
      getValue(context, "user.userId"),
      getValue(context, "user.personId"),
      getValue(context, "user.number"),
      getValue(context, "currentUser.id"),
      getValue(context, "currentUser.userId"),
      getValue(context, "currentUser.personId"),
      getValue(context, "currentUser.number"),
      getValue(context, "userInfo.id"),
      getValue(context, "userInfo.userId"),
      getValue(context, "userInfo.personId"),
      getValue(context, "userInfo.number")
    );
    return {
      mode: "production",
      userId: userId,
      adminId: firstText(context.adminId, context.userId, context.operatorId, getValue(context, "user.id"), getValue(context, "currentUser.id")),
      roles: normalizeRoles(firstText(context.roles, context.roleCodes, context.role, context.permissionCodes, getValue(context, "user.roles"), getValue(context, "currentUser.roles"))),
      displayName: firstText(context.displayName, context.userName, context.username, context.name, context.nickName, context.nickname, context.operatorName, context.personName, getValue(context, "user.displayName"), getValue(context, "user.userName"), getValue(context, "user.name"), getValue(context, "currentUser.displayName"), getValue(context, "currentUser.userName"), getValue(context, "currentUser.name"), getValue(context, "userInfo.displayName"), getValue(context, "userInfo.userName"), getValue(context, "userInfo.name")),
      source: userId ? resolved.source : "missing-cosmic-platform-context"
    };
  }

  function resolveCosmicContext() {
    var windows = reachableWindows();
    var names = (window.CYANCRUISE_IDENTITY && window.CYANCRUISE_IDENTITY.contextNames) || ["__CYANCRUISE_COSMIC_CONTEXT__", "__COSMIC_CONTEXT__", "cosmicContext", "kdContext", "KDCONTEXT", "userInfo", "currentUser", "loginUser"];
    for (var i = 0; i < windows.length; i += 1) {
      for (var j = 0; j < names.length; j += 1) {
        var value = safeReadWindow(windows[i], names[j]);
        if (value && typeof value === "object") {
          return { context: value, source: "window." + names[j] };
        }
      }
    }
    return readStoredCosmicContext() || { context: {}, source: "missing-cosmic-platform-context" };
  }

  function reachableWindows() {
    var items = [window];
    try {
      if (window.parent && window.parent !== window) {
        items.push(window.parent);
      }
    } catch (error) {
      // Cross-origin parent windows are ignored.
    }
    try {
      if (window.top && window.top !== window && window.top !== window.parent) {
        items.push(window.top);
      }
    } catch (error) {
      // Cross-origin top windows are ignored.
    }
    return items;
  }

  function safeReadWindow(sourceWindow, name) {
    try {
      return sourceWindow[name];
    } catch (error) {
      return null;
    }
  }

  function readStoredCosmicContext() {
    var keys = (window.CYANCRUISE_IDENTITY && window.CYANCRUISE_IDENTITY.storageKeys) || [
      "cosmicContext",
      "kdContext",
      "userInfo",
      "currentUser",
      "loginUser",
      "operator",
      "sessionUser",
      "bosUser",
      "mcUser"
    ];
    for (var i = 0; i < keys.length; i += 1) {
      var fromSession = parseStorageJson(sessionStorage, keys[i]);
      if (fromSession) {
        return { context: fromSession, source: "sessionStorage:" + keys[i] };
      }
      var fromLocal = parseStorageJson(localStorage, keys[i]);
      if (fromLocal) {
        return { context: fromLocal, source: "localStorage:" + keys[i] };
      }
    }
    var fromCookie = parseCookieContext();
    return fromCookie ? { context: fromCookie, source: "cookie:platform-user" } : null;
  }

  function parseStorageJson(storage, key) {
    try {
      var raw = storage.getItem(key);
      if (!raw || raw === "undefined" || raw === "null") {
        return null;
      }
      if (raw.charAt(0) === "{" || raw.charAt(0) === "[") {
        return JSON.parse(raw);
      }
    } catch (error) {
      return null;
    }
    return null;
  }

  function parseCookieContext() {
    var pairs = document.cookie ? document.cookie.split(";") : [];
    var keys = (window.CYANCRUISE_IDENTITY && window.CYANCRUISE_IDENTITY.cookieKeys) || ["userInfo", "currentUser", "loginUser", "cosmicContext", "kdContext"];
    for (var i = 0; i < pairs.length; i += 1) {
      var pair = pairs[i].split("=");
      var name = trim(pair.shift());
      if (keys.indexOf(name) < 0) {
        continue;
      }
      try {
        var value = decodeURIComponent(pair.join("="));
        if (value && (value.charAt(0) === "{" || value.charAt(0) === "[")) {
          return JSON.parse(value);
        }
      } catch (error) {
        return null;
      }
    }
    return null;
  }

  function resolveDevelopmentIdentity() {
    var params = new URLSearchParams(window.location.search);
    var fromQuery = trim(params.get("userId"));
    if (fromQuery) {
      localStorage.setItem("cyancruise.userId", fromQuery);
      return {
        mode: "development",
        userId: fromQuery,
        adminId: trim(params.get("adminId")),
        roles: parseRoles(params.get("roles")),
        displayName: firstText(params.get("userName"), params.get("displayName"), params.get("name"), fromQuery ? "用户" : ""),
        source: "query:userId"
      };
    }
    migrateLegacyStorageKey("cyancruise.userId", "cyancruise.userId");
    migrateLegacyStorageKey("cyancruise.adminId", "cyancruise.adminId");
    migrateLegacyStorageKey("cyancruise.roles", "cyancruise.roles");
    var stored = trim(localStorage.getItem("cyancruise.userId"));
    return {
      mode: "development",
      userId: stored,
      adminId: trim(localStorage.getItem("cyancruise.adminId")),
      roles: parseRoles(localStorage.getItem("cyancruise.roles")),
      displayName: firstText(localStorage.getItem("cyancruise.userName"), stored ? "用户" : ""),
      source: stored ? "localStorage:cyancruise.userId" : "development-missing-userId"
    };
  }

  function loadPlatformIdentity() {
    if (!state.identity || state.identity.mode !== "production") {
      prepareUserScopedStorage("", state.identity && state.identity.userId);
      return Promise.resolve(false);
    }
    var previousUserId = firstText(state.identity.userId);
    return post(endpoints.identityCurrent, {}).then(function (identity) {
      var userId = firstText(identity.userId, identity.adminId);
      if (!userId || identity.status !== "OK") {
        state.identityDiagnostic = identityDiagnosticText(identity, null);
        state.identity = productionIdentityRequired("cc001-identity-current-invalid");
        updateIdentityState();
        showMessage("warning", "平台身份未就绪", firstText(identity.message, identity.status, "identity response has no userId"));
        return false;
      }
      prepareUserScopedStorage(previousUserId, userId);
      state.identity = {
        mode: "production",
        userId: userId,
        adminId: firstText(identity.adminId, identity.userId),
        roles: normalizeRoles(identity.roles),
        displayName: firstText(identity.displayName, identity.userName, identity.username, identity.name, identity.nickName, identity.nickname, userId ? "用户" : ""),
        source: firstText(identity.source, "cc001-identity-current")
      };
      state.identityDiagnostic = "";
      updateIdentityState();
      return !!previousUserId && previousUserId !== userId;
    }).catch(function (error) {
      state.identityDiagnostic = identityDiagnosticText(null, error);
      state.identity = productionIdentityRequired("cc001-identity-current-failed");
      updateIdentityState();
      showMessage("warning", "平台身份调用失败", error && error.message ? error.message : "identity request failed");
      return false;
    });
  }

  function productionIdentityRequired(source) {
    return {
      mode: "production",
      userId: "",
      adminId: "",
      roles: [],
      displayName: "",
      source: source
    };
  }

  function post(path, body) {
    var request = resolveApiRequest(path, body);
    if (window.CYANCRUISE_API_CLIENT && typeof window.CYANCRUISE_API_CLIENT.postRequest === "function") {
      return window.CYANCRUISE_API_CLIENT.postRequest(request, path, firstText);
    }
    return fetch(request.url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "same-origin",
      body: JSON.stringify(request.body)
    }).then(function (response) {
      if (!response.ok) {
        throw new Error("服务暂不可用，请稍后重试。");
      }
      return response.json();
    }).then(function (payload) {
      if (request.mode === "kapi" || request.mode === "kapi-v2" || request.mode === "server" || request.mode === "server-kapi-v2") {
        if (payload && Object.prototype.hasOwnProperty.call(payload, "success")) {
          if (!payload.success) {
            throw new Error(firstText(payload.message, payload.errorCode, "服务暂不可用，请稍后重试。"));
          }
          return payload.data;
        }
        if (payload && Object.prototype.hasOwnProperty.call(payload, "status")
            && Object.prototype.hasOwnProperty.call(payload, "data")) {
          if (!payload.status) {
            throw new Error(firstText(payload.message, payload.errorCode, "服务暂不可用，请稍后重试。"));
          }
          return payload.data;
        }
      }
      return payload;
    });
  }

  function resolveApiRequest(path, body) {
    var mode = resolveApiMode();
    if (mode === "server" || mode === "server-managed") {
      return {
        mode: "server-kapi-v2",
        url: resolveServerManagedRouteUrl(),
        body: {
          path: path,
          body: body
        }
      };
    }
    if (mode !== "kapi") {
      return { mode: "direct", url: resolveApiBase() + path, body: body };
    }
    if (resolveKapiRouteVersion() === "v2") {
      return resolveKapiV2Request(path, body);
    }
    var appId = firstText(readQueryOrStorage("appId", "cyancruise.kapi.appId"), "cc001");
    var serviceName = firstText(readQueryOrStorage("serviceName", "cyancruise.kapi.serviceName"), "cyancruise");
    var accessToken = readKapiAccessToken();
    var url = resolveApiBase() + "/kapi/app/" + encodeURIComponent(appId) + "/" + encodeURIComponent(serviceName) + "/";
    if (accessToken) {
      url += "?access_token=" + encodeURIComponent(accessToken);
    }
    return {
      mode: "kapi",
      url: url,
      body: {
        path: path,
        body: body,
        platformIdentity: kapiPlatformIdentity(accessToken)
      }
    };
  }

  function resolveServerManagedRouteUrl() {
    var cloudId = firstText(
      readQueryOrStorage("serverCloudId", "cyancruise.server.cloudId"),
      readQueryOrStorage("cloudId", "cyancruise.server.cloudId"),
      "v620"
    );
    var appNumber = firstText(
      readQueryOrStorage("serverAppNumber", "cyancruise.server.appNumber"),
      readQueryOrStorage("appNumber", "cyancruise.server.appNumber"),
      "v620_cc001"
    );
    var apiCode = firstText(
      readQueryOrStorage("serverApiCode", "cyancruise.server.apiCode"),
      readQueryOrStorage("apiCode", "cyancruise.server.apiCode"),
      endpoints.serverManagedApiCode
    );
    return resolveApiBase() + "/kapi/v2/" + encodeURIComponent(cloudId) + "/" + encodeURIComponent(appNumber) + "/" + encodeApiCode(apiCode);
  }

  function resolveKapiV2Request(path, body) {
    var cloudId = firstText(readQueryOrStorage("cloudId", "cyancruise.kapi.cloudId"), "v620");
    var appNumber = firstText(readQueryOrStorage("appNumber", "cyancruise.kapi.appNumber"), "v620_cc001");
    var apiCode = firstText(readQueryOrStorage("apiCode", "cyancruise.kapi.apiCode"), "cc001/cyancruise/route");
    var accessToken = readKapiAccessToken();
    var url = resolveApiBase() + "/kapi/v2/" + encodeURIComponent(cloudId) + "/" + encodeURIComponent(appNumber) + "/" + encodeApiCode(apiCode);
    if (accessToken) {
      url += "?access_token=" + encodeURIComponent(accessToken);
    }
    return {
      mode: "kapi-v2",
      url: url,
      body: {
        path: path,
        body: body,
        platformIdentity: kapiPlatformIdentity(accessToken)
      }
    };
  }

  function kapiPlatformIdentity(accessToken) {
    var token = trim(accessToken);
    var userId = "";
    var separator = token.indexOf("_");
    if (separator > 0) {
      userId = token.substring(0, separator);
    }
    if (!/^[0-9A-Za-z-]{6,64}$/.test(userId)) {
      return {};
    }
    return {
      userId: userId,
      currentUserId: userId,
      operatorId: userId,
      source: "kapi-access-token-prefix"
    };
  }

  function readKapiAccessToken() {
    return firstText(
      readQueryOrStorage("access_token", "cyancruise.kapi.accessToken"),
      readQueryOrStorage("accessToken", "cyancruise.kapi.accessToken"),
      readQueryOrStorage("token", "cyancruise.kapi.accessToken")
    );
  }

  function identityDiagnosticText(identity, error) {
    var token = readKapiAccessToken();
    var tokenUser = kapiPlatformIdentity(token).userId || "";
    var parts = [
      "app=" + APP_VERSION,
      "front=" + firstText(state.identity && state.identity.source, "missing"),
      "apiMode=" + resolveApiMode(),
      "serverRoute=" + (resolveApiMode() === "server" || resolveApiMode() === "server-managed" ? "registered-kapi-v2" : "no"),
      "kapiToken=" + (token ? "yes" : "no"),
      "tokenUser=" + (tokenUser ? "yes" : "no")
    ];
    if (identity) {
      parts.push("backendStatus=" + firstText(identity.status, "empty"));
      parts.push("backendSource=" + firstText(identity.source, "empty"));
      parts.push("backendMessage=" + firstText(identity.message, "empty"));
    }
    if (error) {
      parts.push("error=" + firstText(error.message, String(error)));
    }
    return parts.join("; ");
  }

  function resolveKapiRouteVersion() {
    return firstText(readQueryOrStorage("kapiRouteVersion", "cyancruise.kapi.routeVersion"), "v2").toLowerCase();
  }

  function encodeApiCode(apiCode) {
    return trim(apiCode).split("/").filter(Boolean).map(encodeURIComponent).join("/");
  }

  function resolveApiMode() {
    var params = new URLSearchParams(window.location.search);
    var fromQuery = trim(params.get("apiMode")).toLowerCase();
    if (fromQuery) {
      if (fromQuery === "direct" || fromQuery === "reset") {
        clearKapiState();
        return "direct";
      }
      if (fromQuery === "server" || fromQuery === "server-managed") {
        clearKapiTokenState();
        localStorage.setItem("cyancruise.apiMode", "server");
        return "server";
      }
      localStorage.setItem("cyancruise.apiMode", fromQuery);
      return fromQuery;
    }
    migrateLegacyStorageKey("cyancruise.apiMode", "cyancruise.apiMode");
    var stored = trim(localStorage.getItem("cyancruise.apiMode")).toLowerCase();
    if (stored === "direct") {
      clearKapiState();
      return "direct";
    }
    if (stored === "server" || stored === "server-managed") {
      return "server";
    }
    if (stored === "kapi") {
      if (storedKapiAccessToken()) {
        return "kapi";
      }
      localStorage.removeItem("cyancruise.apiMode");
      return "server";
    }
    return "server";
  }

  function clearKapiState() {
    localStorage.removeItem("cyancruise.apiMode");
    clearKapiTokenState();
  }

  function clearKapiTokenState() {
    localStorage.removeItem("cyancruise.kapi.accessToken");
    localStorage.removeItem("cyancruise.kapi.appId");
    localStorage.removeItem("cyancruise.kapi.serviceName");
    localStorage.removeItem("cyancruise.kapi.cloudId");
    localStorage.removeItem("cyancruise.kapi.appNumber");
    localStorage.removeItem("cyancruise.kapi.apiCode");
    localStorage.removeItem("cyancruise.kapi.routeVersion");
    localStorage.removeItem("cyancruise.server.cloudId");
    localStorage.removeItem("cyancruise.server.appNumber");
    localStorage.removeItem("cyancruise.server.apiCode");
  }

  function storedKapiAccessToken() {
    return firstText(localStorage.getItem("cyancruise.kapi.accessToken"));
  }

  function readQueryOrStorage(queryKey, storageKey) {
    var params = new URLSearchParams(window.location.search);
    var fromQuery = trim(params.get(queryKey));
    if (fromQuery) {
      localStorage.setItem(storageKey, fromQuery);
      return fromQuery;
    }
    migrateLegacyStorageKey(storageKey, legacyStorageKey(storageKey));
    return trim(localStorage.getItem(storageKey));
  }

  function resolveApiBase() {
    var params = new URLSearchParams(window.location.search);
    var fromQuery = trim(params.get("apiBase"));
    if (fromQuery) {
      localStorage.setItem("cyancruise.apiBase", fromQuery);
      return fromQuery.replace(/\/$/, "");
    }
    migrateLegacyStorageKey("cyancruise.apiBase", "cyancruise.apiBase");
    var stored = trim(localStorage.getItem("cyancruise.apiBase"));
    if (stored) {
      return stored.replace(/\/$/, "");
    }
    return defaultApiBase();
  }

  function legacyStorageKey(storageKey) {
    return storageKey.indexOf("cyancruise.") === 0 ? "cyancruise." + storageKey.substring("cyancruise.".length) : "";
  }

  function migrateLegacyStorageKey(storageKey, legacyKey) {
    if (!legacyKey || localStorage.getItem(storageKey)) {
      return;
    }
    var legacyValue = localStorage.getItem(legacyKey);
    if (legacyValue) {
      localStorage.setItem(storageKey, legacyValue);
    }
  }

  function prepareUserScopedStorage(previousUserId, nextUserId) {
    var legacyOwner = firstText(localStorage.getItem(ACTIVE_USER_STORAGE_KEY), previousUserId);
    if (legacyOwner) {
      migrateLegacyUserStorage(legacyOwner);
    } else {
      removeLegacyUserStorage();
    }
    if (nextUserId) {
      localStorage.setItem(ACTIVE_USER_STORAGE_KEY, nextUserId);
    } else {
      localStorage.removeItem(ACTIVE_USER_STORAGE_KEY);
    }
  }

  function migrateLegacyUserStorage(userId) {
    for (var i = 0; i < USER_SCOPED_STORAGE_KEYS.length; i += 1) {
      var legacyKey = USER_SCOPED_STORAGE_KEYS[i];
      var scopedKey = userStorageKey(legacyKey, userId);
      var legacyValue = localStorage.getItem(legacyKey);
      if (legacyValue !== null && localStorage.getItem(scopedKey) === null) {
        localStorage.setItem(scopedKey, legacyValue);
      }
      localStorage.removeItem(legacyKey);
    }
  }

  function removeLegacyUserStorage() {
    for (var i = 0; i < USER_SCOPED_STORAGE_KEYS.length; i += 1) {
      localStorage.removeItem(USER_SCOPED_STORAGE_KEYS[i]);
    }
  }

  function userStorageKey(baseKey, explicitUserId) {
    var userId = firstText(explicitUserId, state.identity && state.identity.userId, "preview");
    return baseKey + ".user." + encodeURIComponent(userId);
  }

  function getUserStorageItem(baseKey) {
    return localStorage.getItem(userStorageKey(baseKey));
  }

  function setUserStorageItem(baseKey, value) {
    localStorage.setItem(userStorageKey(baseKey), value);
  }

  function removeUserStorageItem(baseKey) {
    localStorage.removeItem(userStorageKey(baseKey));
  }

  function parseUserStorageJson(baseKey) {
    return parseStorageJson(localStorage, userStorageKey(baseKey));
  }

  function defaultApiBase() {
    var firstSegment = trim(window.location.pathname).split("/").filter(Boolean)[0];
    return firstSegment === "ierp" ? "/ierp" : "";
  }

  function metricsPanel(title, rows) {
    return '<section class="panel"><h3>' + escapeHtml(title) + '</h3><div class="metric-list">' +
      rows.map(function (row) {
        return '<div class="metric"><span class="label">' + escapeHtml(row[0]) + '</span><strong>' + escapeHtml(row[1]) + '</strong></div>';
      }).join("") + "</div></section>";
  }

  function actionPanel(title, buttons) {
    return '<section class="panel"><h3>' + escapeHtml(title) + '</h3><div class="actions-row">' + buttons + "</div></section>";
  }

  function linkButton(route, title, summary) {
    return '<button type="button" data-link="' + escapeHtml(route) + '" title="' + escapeHtml(summary) + '">' + escapeHtml(title) + "</button>";
  }

  function listPanel(title, list, emptyText) {
    var normalized = normalizeArray(list);
    if (!normalized.length) {
      return statePanel(title, emptyText, "empty");
    }
    return '<section class="panel"><h3>' + escapeHtml(title) + '</h3><div class="item-list">' +
      normalized.slice(0, 5).map(function (item, index) {
        return '<div class="item"><strong>' + escapeHtml(firstText(item.title, item.name, item.resumeName, item.sessionTitle, "记录 " + (index + 1))) + '</strong><p>' + escapeHtml(firstText(item.summary, item.status, item.createdAt, "已接入页面状态")) + "</p></div>";
      }).join("") + "</div></section>";
  }

  function objectPanel(title, value, emptyText) {
    if (!value || value.unavailable) {
      return statePanel(title, emptyText, "empty");
    }
    return metricsPanel(title, [
      ["摘要", firstText(value.summary, value.weekFocus, "已生成")],
      ["健康度", firstText(value.healthStatus, value.status, "待评估")]
    ]);
  }

  function statePanel(title, text, type) {
    if (window.CYANCRUISE_COMPONENTS && window.CYANCRUISE_COMPONENTS.statusPanel
        && typeof window.CYANCRUISE_COMPONENTS.statusPanel.render === "function") {
      return window.CYANCRUISE_COMPONENTS.statusPanel.render(title, text, type, { escapeHtml: escapeHtml });
    }
    var cls = type === "warning" || type === "pending" ? " warning" : "";
    return '<section class="state-card' + cls + '"><h3>' + escapeHtml(title) + '</h3><p>' + escapeHtml(text) + "</p></section>";
  }

  function field(id, label, type, value, options) {
    if (window.CYANCRUISE_COMPONENTS && window.CYANCRUISE_COMPONENTS.form
        && typeof window.CYANCRUISE_COMPONENTS.form.field === "function") {
      return window.CYANCRUISE_COMPONENTS.form.field(id, label, type, value, options, { escapeHtml: escapeHtml });
    }
    if (type === "select") {
      return '<label>' + escapeHtml(label) + '<select id="' + id + '">' +
        options.map(function (option) {
          var selected = option[0] === value ? " selected" : "";
          return '<option value="' + escapeHtml(option[0]) + '"' + selected + ">" + escapeHtml(option[1]) + "</option>";
        }).join("") + "</select></label>";
    }
    return '<label>' + escapeHtml(label) + '<input id="' + id + '" value="' + escapeHtml(value) + '"></label>';
  }

  function fallbackText(item) {
    if (item.audience === "admin") {
      return "缺少管理员身份时显示 forbidden，不调用 admin WebAPI。";
    }
    if (item.status === "entry-only") {
      return "展示契约入口和 pending/empty 状态，不暗示完整生产能力。";
    }
    return "接口不可用时保留导航，局部面板显示 recoverable 状态。";
  }

  function mapTargetToRoute(target) {
    var normalized = trim(target).replace(/^#/, "").replace(/^\//, "");
    var map = {
      "onboarding": "workbench",
      "personal-situation": "workbench",
      "pages/agent/index": "today-action",
      "pages/onboarding/index": "workbench",
      "pages/assistant/index": "assistant",
      "pages/resume/index": "resume",
      "pages/resume-ai/index": "resume-diagnosis",
      "pages/assessment/index": "assessment",
      "pages/postgraduate/index": "postgraduate",
      "pages/interview/index": "interview",
      "pages/interview/start": "interview",
      "pages/interview/chat": "interview",
      "pages/interview/history": "interview-history",
      "pages/interview/panorama-history": "interview-panorama-history",
      "pages/interview/report": "interview"
    };
    return map[normalized] || normalized || "onboarding";
  }

  function readPreviewProfile() {
    try {
      var raw = getUserStorageItem("cyancruise.previewProfile");
      if (!raw) {
        return null;
      }
      var onboarding = JSON.parse(raw);
      return { onboarding: onboarding, preferences: { targetRole: onboarding.targetRole } };
    } catch (error) {
      return null;
    }
  }

  function resumeTargetDefault() {
    return textFromSnapshot("preferences.targetRole", "onboarding.targetRole", "resume.targetJob") || "";
  }

  function textFromSnapshot() {
    for (var i = 0; i < arguments.length; i += 1) {
      var value = firstText(getValue(state.snapshot, arguments[i]));
      if (value) {
        return value;
      }
    }
    return "";
  }

  function getValue(source, path) {
    if (!source || !path) {
      return "";
    }
    var cursor = source;
    var parts = path.split(".");
    for (var i = 0; i < parts.length; i += 1) {
      if (cursor == null) {
        return "";
      }
      cursor = cursor[parts[i]];
    }
    return cursor;
  }

  function valueOf(id) {
    var node = $(id);
    return node ? trim(node.value) : "";
  }

  function pushRouteToLocation(route) {
    setRouteInLocation(route, false);
  }

  function replaceRouteInLocation(route) {
    setRouteInLocation(route, true);
  }

  function setRouteInLocation(route, replace) {
    var key = normalizeRoute(route);
    var url = new URL(window.location.href);
    url.searchParams.set("ccRoute", key);
    url.hash = "";
    var next = url.pathname + url.search + url.hash;
    var current = window.location.pathname + window.location.search + window.location.hash;
    if (next === current) return;
    if (replace) {
      window.history.replaceState({ ccRoute: key }, "", next);
    } else {
      window.history.pushState({ ccRoute: key }, "", next);
    }
  }

  function normalizeRoute(hash) {
    var fromHash = trim(hash || window.location.hash || "").replace(/^#/, "");
    if (fromHash) {
      return normalizeConfiguredRoute(fromHash);
    }
    var params = new URLSearchParams(window.location.search);
    return normalizeConfiguredRoute(trim(params.get("ccRoute") || params.get("route")) || "workbench");
  }

  function needsCanonicalRoute(route) {
    var params = new URLSearchParams(window.location.search);
    var fromQuery = trim(params.get("ccRoute") || params.get("route"));
    return !!window.location.hash || normalizeConfiguredRoute(fromQuery) !== route;
  }

  function normalizeConfiguredRoute(route) {
    if (window.CYANCRUISE_NAVIGATION && typeof window.CYANCRUISE_NAVIGATION.normalizeRoute === "function") {
      return window.CYANCRUISE_NAVIGATION.normalizeRoute(route);
    }
    return route || "workbench";
  }

  function hasUserIdentity() {
    return !!(state.identity && state.identity.userId);
  }

  function hasAdminRole(identity) {
    if (!identity) {
      return false;
    }
    return identity.roles.indexOf("ADMIN") >= 0 || identity.roles.indexOf("COSMIC_ADMIN") >= 0 || identity.roles.indexOf("PLATFORM_ADMIN") >= 0;
  }

  function calculateReadiness(target, resume, assessment) {
    var score = 0;
    if (target) {
      score += 30;
    }
    if (resume && (resume.lastResumeId || resume.targetJob)) {
      score += 30;
    }
    if (assessment && (assessment.latestRecordId || assessment.lastRecordId || assessment.resultCode || assessment.resultSummary || assessment.summary || assessment.mbtiType)) {
      score += 25;
    }
    if (resume && resume.diagnosisScore) {
      score += 15;
    }
    return Math.min(score, 100);
  }

  function normalizeArray(value) {
    if (Array.isArray(value)) {
      return value;
    }
    if (value && Array.isArray(value.records)) {
      return value.records;
    }
    if (value && Array.isArray(value.items)) {
      return value.items;
    }
    if (value && Array.isArray(value.list)) {
      return value.list;
    }
    return [];
  }

  function defaultDailySuggestions(targetRole) {
    var role = targetRole === "目标岗位待确认" ? "目标岗位" : targetRole;
    return [
      "阅读 1 到 2 个" + role + "岗位描述，补充高频关键词。",
      "优化 1 组简历经历，补充结果、数字和个人贡献。",
      "推进 1 个项目任务或技能练习，并保留可展示成果。",
      "练习 1 个面试问题，整理成背景、任务、行动、结果清晰的回答。",
      "记录今天完成项、卡点和明天第一步。"
    ];
  }

  function previewPlanPhases(targetRole) {
    var role = targetRole === "目标岗位待确认" ? "目标岗位" : targetRole;
    return [
      {
        horizon: "0-1个月",
        title: "定位与材料准备",
        goal: "明确" + role + "要求，完成可投递简历和项目证据清单。",
        status: "待推进",
        actions: ["分析 10 个目标岗位要求", "完成 1 版简历", "整理 2 个项目经历讲述"],
        kpis: ["岗位关键词清单", "可投递简历", "项目证据清单"],
        subStages: [{ period: "第 1 周", title: "岗位拆解", goal: "完成目标岗位分析", actions: ["每天分析 2 个岗位要求", "补充项目证据"] }]
      },
      {
        horizon: "1-3个月",
        title: "能力补齐与项目验证",
        goal: "围绕" + role + "补齐核心技能并形成项目成果。",
        status: "待推进",
        actions: ["完成核心技能计划", "交付 1-2 个项目", "形成项目复盘"],
        kpis: ["项目成果", "技能短板清单"],
        subStages: [{ period: "第 2-8 周", title: "技能和项目推进", goal: "形成可展示成果", actions: ["每日技能练习", "每日推进项目任务"] }]
      },
      {
        horizon: "3-12个月",
        title: "投递面试与机会转化",
        goal: "建立稳定投递节奏，持续复盘面试反馈。",
        status: "待推进",
        actions: ["每周投递岗位", "每周模拟面试", "根据反馈更新简历"],
        kpis: ["投递记录", "面试复盘", "offer 或实习机会"],
        subStages: [{ period: "每周循环", title: "投递复盘", goal: "保持节奏并提升转化", actions: ["投递 5-10 个岗位", "复盘 1 次面试"] }]
      }
    ];
  }

  function normalizeRoles(value) {
    if (Array.isArray(value)) {
      return value.map(function (role) {
        if (typeof role === "string") {
          return trim(role);
        }
        return firstText(role.code, role.name, role.roleCode);
      }).filter(Boolean);
    }
    return parseRoles(value);
  }

  function parseRoles(value) {
    var text = trim(value);
    return text ? text.split(/[;,]/).map(function (role) { return trim(role); }).filter(Boolean) : [];
  }

  function firstText() {
    for (var i = 0; i < arguments.length; i += 1) {
      var value = trim(arguments[i]);
      if (value) {
        return value;
      }
    }
    return "";
  }

  function shortText(value, limit) {
    var text = trim(value);
    var max = limit || 160;
    return text.length > max ? text.slice(0, max - 1) + "…" : text;
  }

  function isDisplayChinese(value) {
    var text = trim(value);
    if (!text) {
      return false;
    }
    return /[\u4e00-\u9fff]/.test(text) || !/[A-Za-z]{4,}/.test(text);
  }

  function trim(value) {
    return value == null ? "" : String(value).replace(/^\s+|\s+$/g, "");
  }

  function escapeHtml(value) {
    return trim(value)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }

  function escapeAttr(value) {
    return escapeHtml(value);
  }

  function showMessage(type, title, text) {
    if (window.CYANCRUISE_COMPONENTS && window.CYANCRUISE_COMPONENTS.message
        && typeof window.CYANCRUISE_COMPONENTS.message.show === "function") {
      window.CYANCRUISE_COMPONENTS.message.show(type, title, text, {
        panel: els.messagePanel,
        state: state,
        escapeHtml: escapeHtml,
        hide: hideMessage
      });
      return;
    }
    if (state.messageTimer) {
      window.clearTimeout(state.messageTimer);
      state.messageTimer = null;
    }
    var success = type === "success" || (type === "info" && isSuccessMessageTitle(title));
    var resolvedType = success ? "success" : type;
    els.messagePanel.className = "message-panel " + resolvedType;
    els.messagePanel.innerHTML =
      '<div class="message-copy"><strong>' + escapeHtml(title) + "</strong><span>" + escapeHtml(text) + "</span></div>" +
      '<button type="button" class="message-close" aria-label="关闭提示">×</button>';
    var close = els.messagePanel.querySelector(".message-close");
    if (close) {
      close.addEventListener("click", hideMessage);
    }
    if (success) {
      state.messageTimer = window.setTimeout(hideMessage, 3000);
    } else if (type === "info") {
      state.messageTimer = window.setTimeout(hideMessage, 5000);
    }
  }

  function isSuccessMessageTitle(title) {
    return /已保存|保存成功|已生成|生成成功|已创建|创建成功|已删除|删除成功|已完成|更新成功|已更新|已准备|已取消/.test(String(title || ""));
  }

  function showConfirmDialog(title, text, confirmText, onConfirm, options) {
    options = options || {};
    if (window.CYANCRUISE_COMPONENTS && window.CYANCRUISE_COMPONENTS.dialog
        && typeof window.CYANCRUISE_COMPONENTS.dialog.confirm === "function") {
      window.CYANCRUISE_COMPONENTS.dialog.confirm(title, text, confirmText, onConfirm, {
        escapeHtml: escapeHtml,
        hide: hideConfirmDialog,
        options: options
      });
      return;
    }
    hideConfirmDialog();
    var previousFocus = document.activeElement;
    var overlay = document.createElement("div");
    overlay.className = "confirm-overlay" + (options.danger ? " danger-overlay" : "");
    overlay.innerHTML =
      '<div class="confirm-dialog' + (options.danger ? ' danger-dialog' : '') + '" role="dialog" aria-modal="true" aria-labelledby="confirmDialogTitle" aria-describedby="confirmDialogText">' +
      '<div class="confirm-copy"><strong id="confirmDialogTitle">' + escapeHtml(title) + '</strong>' +
      '<span id="confirmDialogText">' + escapeHtml(text) + '</span></div>' +
      '<div class="confirm-actions">' +
      (options.acknowledgeOnly ? '' : '<button type="button" class="secondary" data-confirm-cancel>取消</button>') +
      '<button type="button" class="danger" data-confirm-ok>' + escapeHtml(confirmText || "确认") + '</button>' +
      '</div></div>';
    document.body.appendChild(overlay);
    var cancel = overlay.querySelector("[data-confirm-cancel]");
    var ok = overlay.querySelector("[data-confirm-ok]");
    function close() {
      hideConfirmDialog();
      document.removeEventListener("keydown", onKeydown);
      if (previousFocus && typeof previousFocus.focus === "function") {
        previousFocus.focus();
      }
    }
    function onKeydown(event) {
      if (event.key === "Escape") {
        close();
      }
    }
    overlay.addEventListener("click", function (event) {
      if (event.target === overlay) {
        close();
      }
    });
    if (cancel) {
      cancel.addEventListener("click", close);
    }
    ok.addEventListener("click", function () {
      close();
      if (typeof onConfirm === "function") {
        onConfirm();
      }
    });
    document.addEventListener("keydown", onKeydown);
    ok.focus();
  }

  function showResourceDetailDialog(key) {
    var detail = resourceDetail(key);
    if (detail && window.CYANCRUISE_COMPONENTS && window.CYANCRUISE_COMPONENTS.dialog
        && typeof window.CYANCRUISE_COMPONENTS.dialog.resourceDetail === "function") {
      window.CYANCRUISE_COMPONENTS.dialog.resourceDetail(detail, {
        escapeHtml: escapeHtml,
        escapeAttr: escapeAttr,
        hide: hideConfirmDialog,
        navigateToRoute: navigateToRoute
      });
      return;
    }
    if (!detail) {
      showMessage("warning", "资源不可用", "未找到对应的资源内容。");
      return;
    }
    hideConfirmDialog();
    var previousFocus = document.activeElement;
    var overlay = document.createElement("div");
    overlay.className = "confirm-overlay resource-overlay";
    overlay.innerHTML =
      '<div class="confirm-dialog resource-dialog" role="dialog" aria-modal="true" aria-labelledby="resourceDialogTitle">' +
      '<div class="confirm-copy"><strong id="resourceDialogTitle">' + escapeHtml(detail.title) + '</strong>' +
      '<span>' + escapeHtml(detail.summary) + '</span></div>' +
      '<div class="resource-detail-body">' + detail.sections.map(function (section) {
        return '<section><h4>' + escapeHtml(section.title) + '</h4><ul>' + section.items.map(function (item) {
          return '<li>' + escapeHtml(item) + '</li>';
        }).join("") + '</ul></section>';
      }).join("") + '</div>' +
      '<div class="confirm-actions">' +
      '<button type="button" class="secondary" data-confirm-cancel>关闭</button>' +
      (detail.route ? '<button type="button" data-resource-route="' + escapeAttr(detail.route) + '">' + escapeHtml(detail.actionText || "进入相关工具") + '</button>' : '') +
      '</div></div>';
    document.body.appendChild(overlay);
    var cancel = overlay.querySelector("[data-confirm-cancel]");
    var route = overlay.querySelector("[data-resource-route]");
    function close() {
      hideConfirmDialog();
      document.removeEventListener("keydown", onKeydown);
      if (previousFocus && typeof previousFocus.focus === "function") {
        previousFocus.focus();
      }
    }
    function onKeydown(event) {
      if (event.key === "Escape") {
        close();
      }
    }
    overlay.addEventListener("click", function (event) {
      if (event.target === overlay) {
        close();
      }
    });
    cancel.addEventListener("click", close);
    if (route) {
      route.addEventListener("click", function () {
        var targetRoute = route.getAttribute("data-resource-route");
        close();
        navigateToRoute(targetRoute);
      });
    }
    document.addEventListener("keydown", onKeydown);
    cancel.focus();
  }

  function resourceDetail(key) {
    if (window.CYANCRUISE_SERVICES && window.CYANCRUISE_SERVICES.resource
        && typeof window.CYANCRUISE_SERVICES.resource.detail === "function") {
      return window.CYANCRUISE_SERVICES.resource.detail(key);
    }
    return null;
  }

  function hideConfirmDialog() {
    if (window.CYANCRUISE_COMPONENTS && window.CYANCRUISE_COMPONENTS.dialog
        && typeof window.CYANCRUISE_COMPONENTS.dialog.hide === "function") {
      window.CYANCRUISE_COMPONENTS.dialog.hide();
      return;
    }
    var existing = document.querySelector(".confirm-overlay");
    if (existing && existing.parentNode) {
      existing.parentNode.removeChild(existing);
    }
  }

  function hideMessage() {
    if (window.CYANCRUISE_COMPONENTS && window.CYANCRUISE_COMPONENTS.message
        && typeof window.CYANCRUISE_COMPONENTS.message.hide === "function") {
      window.CYANCRUISE_COMPONENTS.message.hide({
        panel: els.messagePanel,
        state: state
      });
      return;
    }
    if (state.messageTimer) {
      window.clearTimeout(state.messageTimer);
      state.messageTimer = null;
    }
    els.messagePanel.className = "message-panel hidden";
    els.messagePanel.innerHTML = "";
  }

  function isFilePreview() {
    return window.location.protocol === "file:";
  }

  function asUnavailable(error) {
    return { unavailable: true, error: error };
  }

  function $(id) {
    return document.getElementById(id);
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
}());
