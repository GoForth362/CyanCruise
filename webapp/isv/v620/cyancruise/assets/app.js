(function () {
  "use strict";

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
    assessmentRecords: "/cc001/assessment/records",
    assessmentRecord: "/cc001/assessment/record/get",
    resumes: "/cc001/resume/list",
    resumeCreate: "/cc001/resume/create",
    resumeDelete: "/cc001/resume/delete",
    plan: "/cc001/career-plan/summary",
    ensurePlan: "/cc001/career-plan/ensure",
    interviews: "/cc001/interview/list",
    startInterview: "/cc001/interview/start",
    guidedInterviewStart: "/cc001/interview/guided/start",
    guidedInterviewAnswer: "/cc001/interview/guided/answer",
    guidedInterviewFinish: "/cc001/interview/guided/finish",
    interviewMessages: "/cc001/interview/messages",
    assistantSend: "/cc001/assistant-chat/send",
    assistantSessions: "/cc001/assistant-chat/session/list",
    employmentInsight: "/cc001/career-employment/insight/get",
    careerResources: "/cc001/career-employment/resources/list",
    notifications: "/cc001/notifications/list",
    notificationUnread: "/cc001/notifications/unread-count",
    notificationRead: "/cc001/notifications/read",
    subscriptionQuota: "/cc001/notifications/subscription/quota",
    weeklyReport: "/cc001/notifications/weekly-report/run",
    adminWhoami: "/cc001/admin/whoami",
    adminDashboard: "/cc001/admin/organizations/dashboard",
    adminUsersBan: "/cc001/admin/users/ban",
    adminQuestions: "/cc001/admin/questions/list",
    adminContent: "/cc001/admin/content/list",
    adminBroadcast: "/cc001/admin/broadcast",
    adminAuditLog: "/cc001/admin/audit-log/list",
    identityCurrent: "/cc001/identity/current",
    fileUpload: "/cc001/files/upload",
    filePreview: "/cc001/files/preview-url",
    fileDownload: "/cc001/files/download",
    fileDelete: "/cc001/files/delete",
    fileExtractText: "/cc001/files/extract-text",
    resumeDiagnosis: "/cc001/resume-diagnosis/analyze",
    keywordStatus: "/cc001/resume-diagnosis/keywords/status"
  };

  var pages = [
    page("workbench", "CyanCruise 首页", "available", "user", "填写用户画像草稿，选择就业或深造路线。", ["snapshot", "onboarding"]),
    page("employment-home", "就业", "available", "user", "进入简历和面试核心功能。", ["resumes", "resumeCreate", "resumeDiagnosis", "interviews", "startInterview"]),
    page("further-study-home", "深造", "available", "user", "考研、保研和留学方向规划入口。", ["snapshot", "plan"]),
    page("postgraduate-exam", "考研", "entry-only", "user", "考研规划入口，后续接入规划智能体。", ["plan"]),
    page("postgraduate-recommendation", "保研", "entry-only", "user", "保研规划入口，后续接入规划智能体。", ["plan"]),
    page("study-abroad", "留学", "entry-only", "user", "留学规划入口，后续接入规划智能体。", ["plan"]),
    page("onboarding", "个人情况", "available", "user", "收集身份、目标岗位、简历状态和偏好信号。", ["onboarding"]),
    page("today-action", "今日行动", "entry-only", "user", "根据路径规划拆解每天应该推进的事项。", ["today"]),
    page("assessment", "职业测评", "entry-only", "user", "通过答题分析人格、性格和偏好，进一步明确用户画像。", ["assessmentSubmit"]),
    page("resume-home", "简历", "available", "user", "简历制作和简历诊断入口。", ["resumes", "resumeCreate", "resumeDiagnosis"]),
    page("resume", "简历", "available", "user", "查看简历记录，创建元数据，并关联文件能力。", ["resumes", "resumeCreate", "resumeDelete"]),
    page("file-upload-preview", "文件上传预览", "entry-only", "user", "展示上传、预览、下载、删除和文本抽取契约。", ["fileUpload", "filePreview", "fileDownload", "fileDelete", "fileExtractText"], { defaultNav: false, debugNav: true }),
    page("resume-diagnosis", "简历诊断", "available", "user", "围绕目标岗位分析匹配度、关键词和建议。", ["resumes", "snapshot", "filePreview", "resumeDiagnosis", "keywordStatus"]),
    page("career-plan", "路径规划", "entry-only", "user", "根据用户方向和画像生成实现路径规划，后续接入规划智能体。", ["plan", "ensurePlan"]),
    page("interview-home", "面试中心", "available", "user", "选择 AI 模拟面试或全景仿真面试，并分别查看练习记录。", ["interviews"]),
    page("interview", "AI 模拟面试", "available", "user", "围绕目标岗位完成文字问答练习和复盘。", ["interviews", "guidedInterviewStart", "guidedInterviewAnswer", "guidedInterviewFinish"]),
    page("interview-panorama", "全景仿真面试", "available", "user", "在沉浸式面试环境中使用摄像头与 AI 面试官面对面练习。", ["interviews", "guidedInterviewStart", "guidedInterviewAnswer", "guidedInterviewFinish"]),
    page("assistant", "求职助手", "available", "user", "发送助手问题并查看会话历史入口。", ["assistantSend", "assistantSessions"]),
    page("messages", "消息中心", "available", "user", "查看站内通知、未读数、订阅配额和周报入口。", ["notifications", "notificationUnread", "notificationRead", "subscriptionQuota", "weeklyReport"]),
    page("employment-insight", "就业洞察", "available", "user", "按学校、专业和目标岗位查看就业洞察。", ["employmentInsight"]),
    page("career-resources", "职业资源", "available", "public", "查看文章、视频、咨询和职业路径资源。", ["careerResources"], { defaultNav: false, debugNav: true }),
    page("admin-console", "管理后台", "entry-only", "admin", "管理员治理入口，仅对 ADMIN 或平台管理员开放。", ["adminWhoami", "adminDashboard", "adminUsersBan", "adminQuestions", "adminContent", "adminBroadcast", "adminAuditLog"], { defaultNav: false, debugNav: true })
  ];

  var pageByKey = {};
  var platformMenuLinks = [
    ["CyanCruise 首页", "#workbench", "已接入"],
    ["就业", "#employment-home", "已接入"],
    ["简历 / 简历制作", "#resume-home", "已接入"],
    ["简历 / 简历诊断", "#resume-diagnosis", "已接入"],
    ["面试 / 全景仿真面试", "#interview-panorama", "独立入口"],
    ["面试 / AI 模拟面试", "#interview", "已接入"],
    ["深造", "#further-study-home", "规划中"],
    ["深造 / 考研", "#postgraduate-exam", "规划中"],
    ["深造 / 保研", "#postgraduate-recommendation", "规划中"],
    ["深造 / 留学", "#study-abroad", "规划中"]
  ];
  var featureGroups = {
    "employment-home": [
      feature("简历制作", "简", "上传或创建简历，关联 PDF 并维护简历记录", "resume", "已接入"),
      feature("简历诊断", "诊", "围绕目标岗位诊断简历匹配度和优化建议", "resume-diagnosis", "已接入"),
      feature("全景仿真面试", "仿", "开启摄像头，在沉浸式环境中进行面对面练习", "interview-panorama", "已接入"),
      feature("AI 模拟面试", "面", "通过文字问答练习并获得针对性复盘", "interview", "已接入")
    ],
    "resume-home": [
      feature("简历制作", "简", "沿用 IPD 简历创建流程，上传或创建简历并关联 PDF", "resume", "已接入"),
      feature("简历诊断", "诊", "沿用 IPD 简历诊断逻辑，围绕目标岗位给出优化建议", "resume-diagnosis", "已接入")
    ],
    "interview-home": [
      feature("全景仿真面试", "仿", "开启摄像头进入沉浸式面试房间", "interview-panorama", "已接入"),
      feature("AI 模拟面试", "面", "进入 AI 文字问答练习并查看该类型记录", "interview", "已接入")
    ],
    "further-study-home": [
      feature("考研", "研", "记录目标院校、考试时间线和复习策略，后续接入考研规划智能体", "postgraduate-exam", "规划中"),
      feature("保研", "保", "记录排名、加分项和目标院校，后续接入保研规划智能体", "postgraduate-recommendation", "规划中"),
      feature("留学", "留", "记录语言、GPA 和背景提升计划，后续接入留学规划智能体", "study-abroad", "规划中")
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
    diagnosisDraft: null,
    diagnosisMessage: null,
    selectedDiagnosisResumeId: null,
    fileMessage: null,
    messageTimer: null,
    previewUrls: {},
    plan: null,
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
    panoramaStream: null,
    panoramaSession: null,
    panoramaQuestion: null,
    panoramaTranscript: "",
    panoramaReport: null,
    panoramaCameraState: "idle",
    panoramaBusy: false,
    panoramaError: null,
    panoramaNotice: null,
    panoramaRecognition: null,
    panoramaAnswering: false,
    panoramaDifficulty: "Normal",
    panoramaSeconds: 0,
    panoramaTimer: null,
    panoramaAnswerCount: 0,
    employmentResources: null,
    employmentResourcesLoading: false,
    employmentResourcesError: null,
    employmentInsight: null,
    employmentInsightLoading: false,
    employmentInsightError: null,
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
    assessmentError: null,
    scrollPositions: {},
    returnRoutes: {},
    route: "workbench",
    previousRoute: ""
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

  function init() {
    pages.forEach(function (item) {
      pageByKey[item.key] = item;
    });
    cacheElements();
    renderNav();
    state.identity = resolveIdentity();
    updateIdentityState();
    bindEvents();
    loadPlatformIdentity().then(function () {
      handleRouteChange();
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
    window.addEventListener("beforeunload", stopPanoramaMedia);
    els.pageHost.addEventListener("click", handlePageHostClick);
    els.pageHost.addEventListener("change", handlePageHostChange);
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
        window.location.hash = route;
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
      if (window.location.hash !== "#workbench") {
        window.location.hash = "workbench";
        return;
      }
    } else {
      if (previous && previous !== key) {
        rememberRouteScroll(previous);
        rememberReturnRoute(key, previous);
      }
      state.route = key;
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
    if (legacyValue === "careerloop") {
      return true;
    }
    return value === "1" || value === "true" || value === "yes";
  }

  function renderPage(item) {
    if (!item) {
      return;
    }
    if (item.audience === "admin" && !hasAdminRole(state.identity)) {
      renderForbidden(item);
      return;
    }
    if (item.audience === "user" && !hasUserIdentity()) {
      renderIdentityRequired(item);
      return;
    }
    if (item.key === "workbench") {
      renderWorkbench(item);
    } else if (item.key === "employment-home" || item.key === "resume-home" || item.key === "further-study-home") {
      renderFeatureHome(item);
    } else if (item.key === "interview-home") {
      renderInterviewHub(item);
    } else if (item.key === "postgraduate-exam" || item.key === "postgraduate-recommendation" || item.key === "study-abroad") {
      renderPlannedStudyPage(item);
    } else if (item.key === "onboarding") {
      renderOnboarding(item);
    } else if (item.key === "resume") {
      renderResumePage(item);
    } else if (item.key === "resume-diagnosis") {
      renderResumeDiagnosisPage(item);
    } else if (item.key === "career-plan") {
      renderCareerPlanPage(item);
    } else if (item.key === "today-action") {
      renderTodayPage(item);
    } else if (item.key === "assessment") {
      renderAssessmentPage(item);
    } else if (item.key === "interview") {
      renderInterviewPage(item);
    } else if (item.key === "interview-panorama") {
      renderPanoramaInterviewPage(item);
    } else if (item.key === "career-resources") {
      renderCareerResourcesPage(item);
    } else {
      renderContractPage(item);
    }
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
      experience: firstText(intent.experience, onboarding.experience, onboarding.strengths)
    };
    var targetRole = firstText(intent.targetRole, onboarding.targetRole, textFromSnapshot("preferences.targetRole"));
    var preference = firstText(intent.preference, onboarding.preference);
    var selectedGoal = resolveHomeGoal(intent.goal, targetRole, preference);
    var intentPanel = isHomeIntentCollapsed(intent)
      ? homeIntentSummaryPanel(selectedGoal, profile, targetRole, preference)
      : homeIntentFormPanel(selectedGoal, profile, targetRole, preference);
    renderFeatureShell(item, homeWelcomeTitle(), "这里汇总你的路线、今日行动、简历和面试进展。",
      intentPanel +
      overviewStrip(selectedGoal) +
      '<section class="feature-section"><h3>路线入口</h3><div class="feature-grid">' +
      featureCards(homeRouteFeatures(selectedGoal)) + '</div></section>' +
      '<section class="feature-section"><h3>推荐工具</h3><div class="feature-grid">' +
      featureCards(homeRecommendedFeatures(selectedGoal)) + '</div></section>');
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
    return localStorage.getItem("cyancruise.homeIntentSaved") === "true" && localStorage.getItem("cyancruise.homeIntentEditing") !== "true" && !!intent.goal;
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

  function homeIntentFormPanel(selectedGoal, onboarding, targetRole, preference) {
    return '<section class="panel full home-intent-panel">' +
      '<h3>用户画像</h3>' +
      '<form class="form-grid" id="homeIntentForm">' +
      '<h4 class="form-section-title full">个人情况</h4>' +
      field("profileIdentityType", "身份类型", "select", firstText(onboarding.identityType, "student"), [["student", "在校学生"], ["graduate", "应届毕业生"], ["career_switcher", "转行求职"]]) +
      field("profileEducationStage", "当前阶段", "select", firstText(onboarding.educationStage, onboarding.stage, "undergraduate"), [["undergraduate", "本科"], ["postgraduate", "研究生"], ["vocational", "高职/专科"], ["working", "已工作"], ["other", "其他"]]) +
      field("profileSchool", "学校", "text", firstText(onboarding.school, "")) +
      field("profileMajor", "专业", "text", firstText(onboarding.major, onboarding.schoolMajor, "")) +
      field("resumeStatus", "简历/材料状态", "select", firstText(onboarding.resumeStatus, "none"), [["none", "还没有简历"], ["draft", "已有初稿"], ["ready", "已有可投递简历"], ["materials", "已有升学材料"]]) +
      '<label class="full">经历与优势<textarea id="profileExperience" placeholder="可以写课程、项目、实习、竞赛、语言、技能、研究方向等。">' + escapeHtml(firstText(onboarding.experience, onboarding.strengths, "")) + '</textarea></label>' +
      '<h4 class="form-section-title full">路线选择</h4>' +
      field("homeGoal", "当前路线", "select", selectedGoal, [["employment", "就业"], ["study", "深造"], ["explore", "先了解一下"]]) +
      field("homeTargetRole", "目标岗位或方向", "text", targetRole) +
      field("homePreference", "路线偏好说明", "text", preference) +
      '<div class="full actions-row"><button type="submit">保存用户画像</button><button type="button" class="secondary" id="cancelHomeIntentButton">取消修改</button>' + homeDirectionButtons(selectedGoal) + '</div>' +
      '</form></section>';
  }

  function homeIntentSummaryPanel(selectedGoal, onboarding, targetRole, preference) {
    var expanded = localStorage.getItem("cyancruise.homeProfileExpanded") === "true";
    var summaryRows = [
      ["当前路线", labelForGoal(selectedGoal)],
      ["目标岗位或方向", firstText(targetRole, "待确认")],
      ["身份类型", labelForIdentity(firstText(onboarding.identityType, "student"))],
      ["简历/材料状态", labelForResumeStatus(firstText(onboarding.resumeStatus, "none"))]
    ];
    var detailRows = [
      ["身份类型", labelForIdentity(firstText(onboarding.identityType, "student"))],
      ["当前阶段", labelForEducationStage(firstText(onboarding.educationStage, onboarding.stage, "undergraduate"))],
      ["学校", firstText(onboarding.school, "未填写")],
      ["专业", firstText(onboarding.major, onboarding.schoolMajor, "未填写")],
      ["简历/材料状态", labelForResumeStatus(firstText(onboarding.resumeStatus, "none"))],
      ["经历与优势", firstText(onboarding.experience, "未填写")],
      ["当前路线", labelForGoal(selectedGoal)],
      ["目标岗位或方向", firstText(targetRole, "待确认")],
      ["路线偏好说明", firstText(preference, "未填写")]
    ];
    var rows = expanded ? detailRows : summaryRows;
    return '<section class="panel full home-intent-panel">' +
      '<h3>用户画像已保存</h3>' +
      '<div class="metric-list profile-summary-grid">' + rows.map(function (row) {
        return '<div class="metric"><span class="label">' + escapeHtml(row[0]) + '</span><strong>' + escapeHtml(row[1]) + '</strong></div>';
      }).join("") + '</div>' +
      '<div class="actions-row"><button type="button" class="secondary" id="toggleHomeProfileButton">' + (expanded ? "收起画像" : "展开画像") + '</button><button type="button" class="secondary" id="editHomeIntentButton">修改用户画像</button></div>' +
      '</section>';
  }

  function editHomeIntent() {
    localStorage.setItem("cyancruise.homeIntentEditing", "true");
    renderPage(pageByKey.workbench);
  }

  function cancelHomeIntentEdit() {
    if (localStorage.getItem("cyancruise.homeIntentSaved") === "true") {
      localStorage.removeItem("cyancruise.homeIntentEditing");
      localStorage.removeItem("cyancruise.previewProfile");
    } else {
      localStorage.removeItem("cyancruise.homeIntent");
      localStorage.removeItem("cyancruise.homeIntentEditing");
      localStorage.removeItem("cyancruise.previewProfile");
    }
    renderPage(pageByKey.workbench);
    showMessage("info", "已取消修改", "页面已恢复到修改前的用户画像。");
  }

  function toggleHomeProfile() {
    var expanded = localStorage.getItem("cyancruise.homeProfileExpanded") === "true";
    localStorage.setItem("cyancruise.homeProfileExpanded", expanded ? "false" : "true");
    renderPage(pageByKey.workbench);
  }

  function homeDirectionButtons(goal) {
    var buttons = [];
    if (goal === "employment" || goal === "explore") {
      buttons.push('<button type="button" class="secondary" data-link="employment-home" data-platform-link="true">进入就业</button>');
    }
    if (goal === "study" || goal === "explore") {
      buttons.push('<button type="button" class="secondary" data-link="further-study-home" data-platform-link="true">进入深造</button>');
    }
    return buttons.join("");
  }

  function homeRouteFeatures(goal) {
    var employment = feature("就业", "就", "进入简历制作、简历诊断、全景仿真面试和模拟面试", "employment-home", "已接入");
    var study = feature("深造", "深", "进入考研、保研、留学规划入口，后续接入规划智能体", "further-study-home", "规划中");
    var plan = feature("路径规划", "路", "根据当前方向生成实现路径规划，后续接入规划智能体", "career-plan", "规划中");
    var assessment = feature("职业测评", "测", "通过答题分析人格、性格和偏好，补全用户画像", "assessment", "规划中");
    var today = feature("今日行动", "今", "根据路径规划拆解每天应该推进的事项", "today-action", "已接入");
    employment.platformLink = true;
    study.platformLink = true;
    if (goal === "employment") {
      return [employment, today, plan, assessment];
    }
    if (goal === "study") {
      return [study, today, plan, assessment];
    }
    return [employment, today, plan, assessment, study];
  }

  function homeRecommendedFeatures(goal) {
    if (goal === "study") {
      return featureGroups["further-study-home"];
    }
    return [
      feature("简历制作", "简", "上传或创建简历，关联 PDF 并维护记录", "resume", "已接入"),
      feature("简历诊断", "诊", "根据目标岗位诊断简历匹配度", "resume-diagnosis", "已接入"),
      feature("全景仿真面试", "仿", "开启摄像头，在沉浸式环境中进行面对面练习", "interview-panorama", "已接入"),
      feature("AI 模拟面试", "面", "通过逐题问答练习并获得针对性复盘", "interview", "已接入")
    ];
  }

  function changeHomeGoal() {
    localStorage.setItem("cyancruise.homeIntentEditing", "true");
  }

  function resolveHomeGoal(goal, target, preference) {
    var normalized = trim(goal);
    if (normalized === "employment" || normalized === "study" || normalized === "explore") {
      return normalized;
    }
    var text = firstText(target, preference);
    if (/考研|保研|留学|升学|深造|研究生|院校|GPA|雅思|托福|申请/.test(text)) {
      return "study";
    }
    return "employment";
  }

  function renderFeatureHome(item) {
    if (item.key === "employment-home") {
      renderEmploymentHome(item);
      return;
    }
    var cards = featureGroups[item.key] || [];
    renderFeatureShell(item, item.title, item.summary,
      '<section class="feature-section"><h3>' + escapeHtml(item.title) + '工具</h3><div class="feature-grid">' +
      featureCards(cards) + '</div></section>');
  }

  function renderEmploymentHome(item) {
    ensureEmploymentHomeData();
    renderFeatureShell(item, item.title, "先看路线图，再进入简历、面试、就业洞察和公开就业资源。",
      employmentRoadmapPanel() +
      employmentInsightPanel() +
      employmentResourcePanels() +
      '<section class="feature-section"><h3>就业工具</h3><div class="feature-grid">' +
      featureCards(featureGroups[item.key] || []) + '</div></section>');
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
    var plan = state.plan && !state.plan.unavailable ? state.plan : {};
    var hasPlan = !!(plan && Object.keys(plan).length);
    var targetRole = employmentTargetRole(plan);
    var weeklyFocus = normalizeArray(plan.weeklyFocus || getValue(plan, "weeklyPlan.actions") || plan.weekFocus || plan.actions || plan.nextActions).slice(0, 3);
    if (!weeklyFocus.length) {
      weeklyFocus = defaultRoadmapFocus(targetRole);
    }
    var phases = normalizeArray(plan.phases);
    var summary = firstText(plan.startStateSummary, plan.summary, plan.planSummary, "当前先使用规则版路线图组织就业动作；接入智能体后，这里会升级为个性化路径规划。");
    var planLabel = hasPlan ? planModeLabel(plan) : "规则版";
    return '<section class="feature-section roadmap-section">' +
      '<div class="section-heading"><div><h3>就业路线图</h3><p class="section-note">优先展示下一步怎么走，工具入口放在路线之后。</p></div>' +
      '<div class="section-actions">' +
      '<button type="button" class="secondary" data-link="career-plan">完整规划</button>' +
      '<button type="button" data-ensure-plan ' + (state.planEnsuring ? 'disabled aria-disabled="true"' : '') + '>' +
      (state.planEnsuring ? "生成中" : hasPlan ? "刷新路线图" : "生成路线图") + '</button></div></div>' +
      '<div class="roadmap-panel">' +
      '<div class="roadmap-summary"><span class="resource-type">' + escapeHtml(planLabel) + '</span>' +
      '<strong>' + escapeHtml(targetRole) + '</strong><p>' + escapeHtml(summary) + '</p>' +
      '<ul class="compact-list">' + weeklyFocus.map(function (item) { return '<li>' + escapeHtml(item) + '</li>'; }).join("") + '</ul></div>' +
      '<div class="roadmap-steps">' + (phases.length ? phases.slice(0, 4).map(planPhaseStepCard).join("") : employmentRoadmapSteps(targetRole).map(roadmapStepCard).join("")) + '</div>' +
      '</div></section>';
  }

  function planModeLabel(plan) {
    var mode = firstText(plan && plan.planningMode, "");
    if (mode === "AGENT") {
      return "智能体生成";
    }
    if (mode === "RULE_FALLBACK") {
      return "规则版";
    }
    return "已生成";
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
        route: "resume-home"
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
        route: "interview-home"
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

  function ensureEmploymentPlan() {
    if (state.planEnsuring) {
      return;
    }
    if (isFilePreview()) {
      var previewPlan = {
        startStateSummary: "预览模式下展示规则版路线图；部署后可调用 CyanCruise 路径规划接口生成并保存。",
        planningMode: "RULE_FALLBACK",
        agentStatus: "FALLBACK_READY",
        horizonYears: 3,
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
    renderPage(pageByKey[state.route]);
    post(endpoints.ensurePlan, state.identity.userId).then(function (plan) {
      state.plan = mergeRefreshedPlan(state.plan, plan || {});
      showMessage("info", "路线图已刷新", "未开始的阶段已更新，已完成的阶段保持不变。");
    }).catch(function (error) {
      showMessage("error", "路线图生成失败", error.message || "路径规划接口暂不可用。");
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
    if (insight.latestYear) {
      rows.push(["最近年份", insight.latestYear]);
    }
    var resumeSummary = employmentResumeSummary();
    var summaryText = firstText(resumeSummary, chineseInsightSummary(insight), "暂无简历摘要。完成简历创建和智能分析后，这里会展示简历摘要。");
    return '<section class="feature-section"><div class="section-heading">' +
      '<h3>就业洞察</h3><button type="button" class="secondary" data-link="employment-insight">查看详情</button></div>' +
      '<div class="insight-summary">' +
      metricsPanel("用户画像", rows) +
      '<section class="panel"><h3>简历摘要</h3><p>' + escapeHtml(summaryText) + '</p>' +
      insightHighlights(insight) + '</section>' +
      '</div></section>';
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

  function employmentResumeSummary() {
    var resumeBlock = getValue(state.snapshot, "resume") || {};
    var resumes = normalizeArray(state.resumes);
    var latest = resumes.length ? resumes[0] : {};
    var summary = firstText(
      latest.aiSummary,
      latest.resumeSummary,
      latest.summary,
      latest.parsedContent,
      resumeBlock.summary,
      resumeBlock.aiSummary
    );
    if (isDisplayChinese(summary)) {
      return shortText(summary, 180);
    }
    var score = firstText(latest.diagnosisScore, resumeBlock.diagnosisScore, "");
    if (score) {
      return "当前简历诊断分为 " + score + "，建议结合目标岗位继续补充项目证据、技能关键词和成果量化。";
    }
    var title = firstText(latest.title, latest.resumeName, resumeBlock.title, "");
    var target = firstText(latest.targetJob, latest.targetRole, resumeBlock.targetJob, textFromSnapshot("preferences.targetRole", "onboarding.targetRole"));
    if (title || target) {
      return "当前已维护" + (title ? "《" + title + "》" : "简历记录") + (target ? "，目标岗位为" + target : "") + "。完成简历智能分析后，这里会展示更完整的简历摘要。";
    }
    return "";
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
      return "用户画像中还缺少学校信息，补充学校和专业后可生成更准确的就业洞察。";
    }
    if (status === "UNSUPPORTED_SCHOOL" || status === "NO_SOURCES") {
      return "当前学校的可追溯就业来源尚未接入，页面会先展示用户画像与简历摘要。";
    }
    if (status === "MISSING_TARGET_ROLE") {
      return "用户画像中还缺少目标岗位，补充后可结合岗位方向生成就业洞察。";
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
      "School is required before source-backed employment insight can be generated.": "用户画像中还缺少学校信息，补充学校后可生成就业洞察。",
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
    var articles = normalizeArray(feed.articles);
    var services = normalizeArray(feed.consultations).concat(normalizeArray(feed.careerPaths));
    var videos = normalizeArray(feed.videos);
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
    var articles = normalizeArray(feed.articles);
    var services = normalizeArray(feed.consultations).concat(normalizeArray(feed.careerPaths));
    var videos = normalizeArray(feed.videos);
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
    var items = normalizeArray(cards).slice(0, limit || 4);
    if (!items.length) {
      return '<section class="resource-column"><h4>' + escapeHtml(title) + '</h4><p class="panel-note">暂无内容。</p></section>';
    }
    return '<section class="resource-column"><h4>' + escapeHtml(title) + '</h4><div class="resource-list">' +
      items.map(function (item) { return resourceCard(item, type); }).join("") + '</div></section>';
  }

  function resourceCard(item, type) {
    var rawUrl = firstText(item.sourceUrl, item.url, "");
    var url = externalResourceUrl(item, rawUrl);
    var detailKey = url ? "" : resourceDetailKey(item, rawUrl);
    var meta = [resourceTypeLabel(item.type || type), firstText(item.category, item.keyword, ""), shortDate(item.publishedAt)].filter(Boolean).join(" · ");
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
    if (value.indexOf("/careerloop/resources/resume-evidence") >= 0 || value.indexOf("/cyancruise/resources/resume-evidence") >= 0 || id === "article-resume-001") {
      return "resume-evidence";
    }
    if (value.indexOf("/careerloop/resources/weekly-focus") >= 0 || value.indexOf("/cyancruise/resources/weekly-focus") >= 0 || id === "tip-plan-001") {
      return "weekly-focus";
    }
    if (value.indexOf("BV1careerloop") >= 0 || value.indexOf("BV1cyancruise") >= 0 || id === "video-interview-001") {
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
      '<p class="panel-note">这个方向先作为深造路线入口预留。后续会接入主调度智能体、用户画像智能体和对应规划智能体；当前可以先回到深造页选择方向，或回到首页调整路线信息。</p>' +
      '<div class="actions-row"><button type="button" data-link="further-study-home">返回深造</button><button type="button" class="secondary" data-link="workbench">返回首页</button></div>' +
      '</section>');
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
    if (hasUserIdentity() && !state.plan && !state.planEnsuring && !isFilePreview()) {
      ensureEmploymentPlan();
    }
    var view = buildPlanViewModel();
    if (!view.dailyPlan.items.length) {
      renderShell(item,
        statePanel("等待路径规划", "今日行动会从路径规划里的当前阶段和本周计划拆解出来。请先生成路线图。", "pending")
      );
      return;
    }
    var body = '<section class="feature-section route-execution-grid daily-first">' +
      renderDailyPlanCard(view.dailyPlan, view.progressState) +
      renderWeeklyPlanCard(view.weeklyPlan, view.weeklyActions, view.weeklyDeliverables, view.targetRole, view.progressState) +
      '</section>' +
      '<section class="feature-section route-control-grid">' +
      metricsPanel("今日来源", [
        ["目标岗位", view.targetRole],
        ["当前阶段", view.progressSummary.activePhaseTitle],
        ["规划周期", view.selectedYears + " 年"],
        ["完成进度", view.progressSummary.completedTasks + " / " + view.progressSummary.totalTasks + " 项"]
      ]) +
      '<section class="panel route-control-card"><div class="route-card-head"><div><span class="resource-type">继续规划</span><h3>路线图</h3></div></div>' +
      '<p class="route-goal">今日行动来自路径规划。调整阶段、周期或刷新规划后，这里会跟着变化。</p>' +
      '<div class="actions-row"><button type="button" data-link="career-plan">查看路径规划</button></div></section>' +
      '</section>';
    renderFeatureShell(item, item.title, "根据路径规划拆解今天应该推进的小事项。", body);
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
      '<button type="button" data-link="resume-diagnosis">去诊断</button>' +
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
    post(endpoints.resumeCreate, { userId: state.identity.userId, request: request }).then(function () {
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
    return post(endpoints.resumes, state.identity.userId).then(function (resumes) {
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

  function refreshSnapshotAfterResume() {
    if (!hasUserIdentity() || isFilePreview()) {
      updateOverviewCards();
      return Promise.resolve();
    }
    return post(endpoints.snapshot, state.identity.userId).then(function (snapshot) {
      state.snapshot = snapshot;
      updateOverviewCards();
    }).catch(function (error) {
      state.resumeMessage = { type: "warning", text: "简历已创建，但画像摘要稍后刷新：" + (error.message || "snapshot unavailable") };
      updateOverviewCards();
    });
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
      return post(endpoints.fileUpload, {
        request: {
          folder: "resumes",
          originalFilename: file.name,
          base64: base64
        }
      });
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
      return post(endpoints.fileExtractText, { fileUrlOrKey: objectKey }).then(function (extraction) {
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
    post(endpoints.fileDownload, { fileUrlOrKey: fileKey }).then(function (result) {
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
      diagnosisResultPanel() +
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
  }

  function selectedDiagnosisResume(resumes) {
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
    var options = resumes.map(function (resume) {
      var id = firstText(resume.resumeId, resume.id, "");
      var label = firstText(resume.title, resume.resumeName, "简历 " + id);
      return '<option value="' + escapeAttr(id) + '"' + (String(id) === String(draft.resumeId) ? " selected" : "") + '>' + escapeHtml(label) + '</option>';
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
      '<label>已有简历<select id="diagnosisResumeId">' + options + '</select></label>' +
      '<input id="diagnosisTargetJob" type="hidden" value="' + escapeAttr(draft.targetJob) + '">' +
      '<div class="diagnosis-target-display"><span class="label">目标岗位</span><strong>' +
      escapeHtml(targetMissing ? "尚未设置" : draft.targetJob) + '</strong><small>' +
      escapeHtml(targetMissing ? "请回到简历页补充这份简历的目标岗位" : "来自所选简历，切换简历时会自动更新") + '</small></div>' +
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
    var body = '<section class="panel"><h3>诊断结果</h3>' +
      '<p class="panel-note">当前为基础规则诊断。总分由内容完整度、目标岗位匹配、经历证据和表达清晰度四项组成。</p>' +
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
          '<div class="diagnosis-score-track"><span style="width:' + percent + '%"></span></div><p>' +
          escapeHtml(userFacingText(item.reason) || "暂无评分说明") + '</p></div>';
      }).join("") + '</div></section>';
  }

  function revisionSuggestionList(suggestions) {
    if (!suggestions.length) {
      return statePanel("诊断建议", "当前诊断没有返回结构化建议，可查看普通建议后再次诊断。", "empty");
    }
    return '<div class="item-list revision-list">' + suggestions.map(function (item) {
      return '<article class="item revision-item">' +
        '<div><strong>' + escapeHtml(priorityLabel(item.priority)) + " · " + escapeHtml(resumeSectionLabel(item.resumeSection)) + '</strong>' +
        '<p class="revision-problem">问题：' + escapeHtml(userFacingText(firstText(item.problem, item.action, "需要补充简历证据"))) + '</p>' +
        '<p>怎么改：' + escapeHtml(userFacingText(firstText(item.action, "围绕目标岗位补充经历证据"))) + '</p>' +
        '<p>参考写法：' + escapeHtml(userFacingText(firstText(item.rewriteExample, "用具体行动、方法、结果和个人贡献重写经历。"))) + '</p>' +
        '<p>建议补充：' + escapeHtml(userFacingList(item.targetKeywords).join(" / ") || "与目标岗位相关的真实证据") + '</p></div></article>';
    }).join("") + '</div>';
  }

  function diagnosisContextLabel(sources) {
    var labels = normalizeArray(sources).map(function (source) {
      var value = String(source || "");
      if (value.indexOf("resume:") === 0) return "当前简历";
      if (value === "resume.targetJob") return "简历目标岗位";
      if (value === "resume.fileText") return "PDF 简历正文";
      if (value === "profile.assessment") return "职业测评";
      if (value === "profile.snapshot") return "用户画像";
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
      .replace(/\bJD\b/gi, "岗位要求")
      .replace(/resume\.targetJob/g, "简历目标岗位")
      .replace(/profile\.assessment/g, "职业测评")
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
    if (select) {
      select.addEventListener("change", function () {
        state.selectedDiagnosisResumeId = select.value;
        state.diagnosisDraft = { resumeId: select.value, targetJob: "", jobDescription: "", resumeText: "" };
        state.diagnosisResult = null;
        state.diagnosisMessage = null;
        renderPage(pageByKey[state.route]);
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
      state.diagnosisResult = previewDiagnosisResult(draft);
      state.diagnosisMessage = { type: "info", text: "file:// 预览模式已生成本地诊断示例。" };
      renderPage(pageByKey[state.route]);
      return;
    }
    state.diagnosisLoading = true;
    state.diagnosisMessage = {
      type: "info",
      text: firstText(draft.resumeText, selected && selected.parsedContent, "")
        ? "正在生成简历诊断建议。"
        : "正在读取 PDF 正文，读取成功后会继续生成诊断建议。"
    };
    renderPage(pageByKey[state.route]);
    post(endpoints.resumeDiagnosis, { userId: state.identity.userId, request: diagnosisRequest(draft) }).then(function (result) {
      state.diagnosisResult = result || {};
      syncDiagnosisScoreToResumeList(draft.resumeId, state.diagnosisResult.overallScore);
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
    if (hasUserIdentity() && !state.plan && !state.planEnsuring && !isFilePreview()) {
      ensureEmploymentPlan();
    }
    var view = buildPlanViewModel();
    var summary = firstText(view.plan.startStateSummary, view.plan.summary, "根据用户画像、简历记录和目标岗位生成分阶段路线图，后续可直接接入 agent 自动规划。");
    var metrics = [
      ["规划模式", planModeLabel(view.plan)],
      ["目标岗位", view.targetRole],
      ["规划周期", view.selectedYears + " 年"],
      ["Agent 状态", firstText(view.plan.agentStatus, "待接入")]
    ];
    var phaseHtml = view.visiblePhases.length ? view.visiblePhases.map(function (phase, index) {
      return renderPlanPhaseCard(phase, index, view.progressState, view.activePhaseId);
    }).join("") :
      statePanel("路线图待生成", "当前还没有可展示的路线图，点击“生成路线图”后会先使用规则版规划，后续可平滑切换到 agent。", "pending");
    var timelineHtml = view.visiblePhases.length ? renderPlanTimeline(view.visiblePhases, view.activePhaseId, view.progressSummary) : "";
    var flowHtml = view.visiblePhases.length ? renderPlanFlow(view.visiblePhases, view.activePhaseId, view.progressSummary) : "";
    renderFeatureShell(item, item.title, summary,
      '<section class="feature-section route-execution-grid daily-first">' +
      renderDailyPlanCard(view.dailyPlan, view.progressState) +
      renderWeeklyPlanCard(view.weeklyPlan, view.weeklyActions, view.weeklyDeliverables, view.targetRole, view.progressState) +
      '</section>' +
      '<section class="feature-section route-control-grid">' +
      planningHorizonPanel(view.selectedYears) +
      metricsPanel("路线概览", metrics) +
      '</section>' +
      timelineHtml +
      '<section class="feature-section"><div class="section-heading"><div><h3>阶段路线图</h3><p class="section-note">先给出 1 年和 3 年的大阶段，再细化到周和每天。</p></div>' +
      '<div class="section-actions"><button type="button" data-ensure-plan ' + (state.planEnsuring ? 'disabled aria-disabled="true"' : '') + '>' +
      (state.planEnsuring ? "生成中" : "刷新路线图") + '</button></div></div>' +
      flowHtml +
      '<div class="route-phase-grid">' + phaseHtml + '</div></section>' +
      '');
  }

  function buildPlanViewModel() {
    var plan = state.plan && !state.plan.unavailable ? state.plan : {};
    var targetRole = employmentTargetRole(plan);
    var phases = normalizeArray(plan.phases);
    var weeklyPlan = plan.weeklyPlan || {};
    if (!phases.length && isFilePreview()) {
      phases = previewPlanPhases(targetRole);
    }
    var selectedYears = readSelectedPlanHorizon(plan, targetRole);
    var visiblePhases = filterPlanPhasesByYears(phases, selectedYears);
    if (!visiblePhases.length) {
      visiblePhases = phases;
    }
    var progressState = readPlanProgress(plan, targetRole, phases);
    var activePhaseId = firstText(progressState.activePhaseId, visiblePhases.length ? phaseKey(visiblePhases[0], 0) : "");
    if (!containsPhaseId(visiblePhases, activePhaseId)) {
      activePhaseId = visiblePhases.length ? phaseKey(visiblePhases[0], 0) : "";
    }
    var dailyPlan = deriveDailyPlan(visiblePhases, weeklyPlan, progressState, activePhaseId, targetRole);
    var progressSummary = summarizePlanProgress(visiblePhases, progressState);
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
      dailyPlan: dailyPlan,
      progressSummary: progressSummary
    };
  }

  function renderDailyPlanCard(dailyPlan, progressState) {
    return '<section class="panel route-daily-card"><div class="route-card-head"><div><span class="resource-type">每日计划</span><h3>今天可以做什么</h3></div></div>' +
      '<p class="route-goal">' + escapeHtml(dailyPlan.summary) + '</p>' +
      renderLinkedTaskList("今日小事", dailyPlan.items, progressState) + '</section>';
  }

  function renderWeeklyPlanCard(weeklyPlan, weeklyActions, weeklyDeliverables, targetRole, progressState) {
    return '<section class="panel route-weekly-card"><div class="route-card-head"><div><span class="resource-type">本周计划</span><h3>' +
      escapeHtml(firstText(weeklyPlan.weekTitle, "本周推进重点")) + '</h3></div></div><p class="route-goal">' +
      escapeHtml(firstText(weeklyPlan.weekGoal, "围绕当前目标岗位推进简历、项目和面试准备。")) + '</p>' +
      renderTaskList("本周动作", weeklyActions.length ? weeklyActions : defaultRoadmapFocus(targetRole), "weekly-actions", progressState) +
      renderTaskList("本周交付物", weeklyDeliverables.length ? weeklyDeliverables : ["简历优化清单", "岗位关键词清单", "一次模拟面试复盘"], "weekly-deliverables", progressState) +
      '</section>';
  }

  function renderPlanPhaseCard(phase, phaseIndex, progressState, activePhaseId) {
    var subStages = normalizeArray(phase && phase.subStages);
    var phaseId = phaseKey(phase, phaseIndex);
    var phaseStatus = phaseProgressStatus(phase, phaseIndex, progressState);
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
        var checked = !!(progressState.checked && progressState.checked[item.taskId]);
        return '<li class="route-task-item' + (checked ? " done" : "") + '">' +
          '<label class="route-task-toggle"><input type="checkbox" data-plan-task="' + escapeAttr(item.taskId) + '"' + (checked ? " checked" : "") + '>' +
          '<span class="route-checkmark"></span><span class="route-task-text">' + escapeHtml(item.text) + '</span></label></li>';
      }).join("") + '</ul></div>';
  }

  function planningHorizonPanel(selectedYears) {
    return '<section class="panel route-control-card"><div class="route-card-head"><div><span class="resource-type">规划周期</span><h3>查看周期</h3></div></div>' +
      '<p class="route-goal">可以先聚焦 1 年内目标，也可以切回完整 3 年路线图。</p>' +
      '<div class="route-horizon-toggle">' +
      horizonButton(1, selectedYears) +
      horizonButton(3, selectedYears) +
      '</div></section>';
  }

  function horizonButton(years, selectedYears) {
    return '<button type="button" class="route-horizon-button' + (years === selectedYears ? " active" : "") + '" data-plan-horizon="' + years + '">' + years + '年</button>';
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

  function summarizePlanProgress(phases, progressState) {
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
    if (progressState.activePhaseId && phaseMap[progressState.activePhaseId]) {
      activePhaseTitle = phaseMap[progressState.activePhaseId].title;
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

  function readSelectedPlanHorizon(plan, targetRole) {
    var key = planHorizonStorageKey(plan, targetRole);
    var stored = parseInt(trim(localStorage.getItem(key)), 10);
    if (stored === 1 || stored === 3) {
      return stored;
    }
    var planYears = Number(plan && plan.horizonYears);
    return planYears === 1 ? 1 : 3;
  }

  function persistSelectedPlanHorizon(plan, targetRole, years) {
    localStorage.setItem(planHorizonStorageKey(plan, targetRole), String(years));
  }

  function planHorizonStorageKey(plan, targetRole) {
    var userId = hasUserIdentity() ? state.identity.userId : "preview";
    var role = sanitizeKey(firstText(plan && plan.targetRole, targetRole, "general"));
    return "cyancruise.planHorizon." + sanitizeKey(userId) + "." + role;
  }

  function filterPlanPhasesByYears(phases, selectedYears) {
    return normalizeArray(phases).filter(function (phase) {
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
    if (targetRoleChanged(existing, incoming)) {
      return incoming;
    }
    if (!nextPhases.length) {
      return incoming;
    }
    var targetRole = employmentTargetRole(existing);
    var progressState = readPlanProgress(existing, targetRole, normalizeArray(existing.phases));
    var completedPhaseMap = {};
    var existingPhases = normalizeArray(existing.phases);
    for (var i = 0; i < existingPhases.length; i += 1) {
      var existingPhase = existingPhases[i];
      if (isPhaseCompletedByProgress(existingPhase, i, progressState)) {
        completedPhaseMap[phaseIdentityKey(existingPhase, i)] = existingPhase;
      }
    }
    incoming.phases = nextPhases.map(function (phase, index) {
      var identity = phaseIdentityKey(phase, index);
      return completedPhaseMap[identity] || phase;
    });
    if (!firstText(progressState.activePhaseId) && incoming.phases.length) {
      progressState.activePhaseId = phaseKey(incoming.phases[0], 0);
      persistPlanProgress(incoming, employmentTargetRole(incoming), progressState);
    }
    return incoming;
  }

  function targetRoleChanged(previousPlan, nextPlan) {
    var previousTarget = previousPlan && previousPlan.targetRole;
    var nextTarget = nextPlan && nextPlan.targetRole;
    return firstText(previousTarget) && firstText(nextTarget) && !sameTargetRole(previousTarget, nextTarget);
  }

  function sameTargetRole(first, second) {
    return trim(first) === trim(second);
  }

  function isPhaseCompletedByProgress(phase, index, progressState) {
    var phaseId = phaseKey(phase, index);
    var counters = { total: 0, completed: 0 };
    countTaskGroup(normalizeArray(phase && phase.actions), phaseId + ".actions", progressState, counters);
    countTaskGroup(normalizeArray(phase && phase.kpis), phaseId + ".kpis", progressState, counters);
    var subStages = normalizeArray(phase && phase.subStages);
    for (var i = 0; i < subStages.length; i += 1) {
      countTaskGroup(normalizeArray(subStages[i].actions), phaseId + ".substage." + i + ".actions", progressState, counters);
    }
    return counters.total > 0 && counters.completed >= counters.total;
  }

  function phaseIdentityKey(phase, index) {
    return sanitizeKey(firstText(phase && phase.horizon, "")) + "::" + sanitizeKey(firstText(phase && phase.title, phaseKey(phase, index)));
  }

  function phaseProgressStatus(phase, index, progressState) {
    var phaseId = phaseKey(phase, index);
    var counters = { total: 0, completed: 0 };
    countTaskGroup(normalizeArray(phase && phase.actions), phaseId + ".actions", progressState, counters);
    countTaskGroup(normalizeArray(phase && phase.kpis), phaseId + ".kpis", progressState, counters);
    var subStages = normalizeArray(phase && phase.subStages);
    for (var i = 0; i < subStages.length; i += 1) {
      countTaskGroup(normalizeArray(subStages[i].actions), phaseId + ".substage." + i + ".actions", progressState, counters);
    }
    if (counters.total > 0 && counters.completed >= counters.total) {
      return { code: "done", label: "已完成" };
    }
    if (counters.completed > 0) {
      return { code: "active", label: "进行中" };
    }
    return { code: "idle", label: "未开始" };
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
    post(endpoints.resumeDelete, { userId: state.identity.userId, resumeId: resumeId }).then(function () {
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
      body += statePanel("正在加载职业测评", "正在读取 CyanCruise 职业测评题库和最近结果。", "pending");
    }
    if (state.assessmentError) {
      body += statePanel("测评暂不可用", state.assessmentError, "warning");
    }

    if (!state.assessmentSelectedScaleId) {
      body += renderAssessmentList();
      renderShell(item, body);
      return;
    }

    var scale = state.assessmentScale || selectedAssessmentScaleSummary() || previewAssessmentScale();
    var questions = normalizeArray(scale.questions);
    var answered = assessmentAnsweredCount(questions);
    var total = questions.length;
    var result = state.assessmentResult;
    var currentIndex = Math.max(0, Math.min(Number(state.assessmentCurrentIndex || 0), Math.max(total - 1, 0)));
    var currentQuestion = questions[currentIndex];

    body += '<section class="panel assessment-panel"><h3>' + escapeHtml(scale.title || "职业测评") + '</h3>' +
      '<p>' + escapeHtml(scale.description || "完成答题后，系统会生成职业画像结果，并写入 CyanCruise 职业画像。") + '</p>' +
      '<div class="route-progress-card"><div class="route-progress-head"><strong>' + answered + "/" + total +
      '</strong><span>' + escapeHtml(estimateAssessmentMinutes(scale) + " 分钟") + '</span></div>' +
      '<div class="route-progress-track"><span class="route-progress-fill" style="width:' +
      (total ? Math.round(answered * 100 / total) : 0) + '%"></span></div></div>' +
      '</section>';

    if (result && (result.resultSummary || result.summary)) {
      body += assessmentResultPanel(result);
    }

    body += '<section class="panel assessment-quiz"><h3>开始答题</h3>' +
      questions.map(assessmentQuestionCard).join("") +
      '<div class="actions-row"><button type="button" data-assessment-submit ' +
      (state.assessmentSubmitting || answered < total ? 'disabled aria-disabled="true"' : '') + '>' +
      (state.assessmentSubmitting ? "提交中" : "提交测评") + '</button>' +
      '<button type="button" class="secondary" data-assessment-back>返回测评列表</button></div>' +
      '</section>';

    renderShell(item, body);
  }

  function renderAssessmentPage(item) {
    if (!state.assessmentScales && !state.assessmentLoading) {
      loadAssessment();
    }
    var body = "";

    if (state.assessmentLoading && !state.assessmentScales) {
      body += statePanel("正在加载职业测评", "正在读取 CyanCruise 职业测评题库和最近结果。", "pending");
    }
    if (state.assessmentError) {
      body += statePanel("测评暂不可用", state.assessmentError, "warning");
    }

    if (!state.assessmentSelectedScaleId) {
      body += renderAssessmentList();
      renderShell(item, body);
      return;
    }

    var scale = state.assessmentScale || selectedAssessmentScaleSummary() || previewAssessmentScale();
    var questions = normalizeArray(scale.questions);
    var answered = assessmentAnsweredCount(questions);
    var total = questions.length;
    var result = state.assessmentResult;
    var currentIndex = Math.max(0, Math.min(Number(state.assessmentCurrentIndex || 0), Math.max(total - 1, 0)));
    var currentQuestion = questions[currentIndex];

    body += '<section class="panel full assessment-panel"><h3>' + escapeHtml(scale.title || "职业测评") + '</h3>' +
      '<p>' + escapeHtml(scale.description || "完成答题后，系统会生成职业画像结果，并写入 CyanCruise 职业画像。") + '</p>' +
      '<div class="route-progress-card"><div class="route-progress-head"><strong>' + answered + "/" + total +
      '</strong><span>' + escapeHtml(total ? "第 " + (currentIndex + 1) + " / " + total + " 题" : "暂无题目") + '</span></div>' +
      '<div class="route-progress-track"><span class="route-progress-fill" style="width:' +
      (total ? Math.round(answered * 100 / total) : 0) + '%"></span></div></div>' +
      '</section>';

    if (result && (result.resultSummary || result.summary)) {
      body += '<section class="panel full assessment-result-wrap">' + assessmentResultPanel(result) +
        '<div class="actions-row"><button type="button" data-assessment-back>返回测评列表</button></div></section>';
      renderShell(item, body);
      return;
    }

    body += '<section class="panel full assessment-quiz"><h3>开始答题</h3>' +
      (currentQuestion ? assessmentQuestionCard(currentQuestion, currentIndex) : statePanel("暂无题目", "当前测评题库为空。", "warning")) +
      '<div class="actions-row"><button type="button" class="secondary" data-assessment-prev ' +
      (currentIndex <= 0 || state.assessmentSubmitting ? 'disabled aria-disabled="true"' : '') + '>上一题</button>' +
      '<button type="button" class="secondary" data-assessment-back>返回测评列表</button>' +
      (state.assessmentSubmitting ? '<button type="button" disabled aria-disabled="true">提交中</button>' : '') + '</div>' +
      '</section>';

    renderShell(item, body);
  }

  function loadAssessment() {
    state.assessmentLoading = true;
    state.assessmentError = null;
    if (isFilePreview()) {
      state.assessmentScales = previewAssessmentScaleSummaries();
      state.assessmentScale = null;
      state.assessmentRecords = [];
      state.assessmentLoading = false;
      if (state.route === "assessment") {
        renderPage(pageByKey.assessment);
      }
      return;
    }
    Promise.all([
      post(endpoints.assessmentScales, {}),
      hasUserIdentity() ? post(endpoints.assessmentRecords, state.identity.userId).catch(function () { return []; }) : Promise.resolve([])
    ]).then(function (results) {
      state.assessmentScales = normalizeArray(results[0]);
      state.assessmentRecords = normalizeArray(results[1]);
      if (!state.assessmentScales.length) {
        throw new Error("没有可用的职业测评量表");
      }
      state.assessmentScale = null;
      state.assessmentError = null;
    }).catch(function (error) {
      state.assessmentError = error.message || "职业测评题库暂不可用。";
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
    return '<section class="feature-section assessment-list-section full"><div class="section-heading">' +
      '<div><h3>选择职业测评</h3><p class="section-note">按你当前最想了解的问题选择测评，结果会写入职业画像。</p></div></div>' +
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
          escapeAttr(scale.scaleId) + '">' + (done ? "重新测" : "开始") + '</button></div></article>';
      }).join("") + '</div></section>';
  }

  function startAssessmentScale(scaleId) {
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
    post(endpoints.assessmentQuestions, { scaleId: scaleId }).then(function (scale) {
      state.assessmentScale = scale || previewAssessmentScale(scaleId);
    }).catch(function (error) {
      state.assessmentError = error.message || "职业测评题目暂不可用。";
      state.assessmentScale = previewAssessmentScale(scaleId);
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
    var selected = state.assessmentAnswers[String(question.questionId)];
    return '<div class="route-control-card assessment-question"><div class="route-card-head"><h3>' +
      (index + 1) + ". " + escapeHtml(question.questionText) + '</h3></div><div class="assessment-options">' +
      options.map(function (option) {
        var active = String(option.optionId) === String(selected) ? " active" : "";
        return '<button type="button" class="route-horizon-button' + active + '" data-assessment-question="' +
          escapeAttr(question.questionId) + '" data-assessment-option="' + escapeAttr(option.optionId) + '">' +
          '<strong>' + escapeHtml(option.optionLabel) + '</strong><span>' + escapeHtml(option.optionText) + '</span></button>';
      }).join("") + '</div></div>';
  }

  function chooseAssessmentOption(questionId, optionId) {
    if (!questionId || !optionId || state.assessmentSubmitting) {
      return;
    }
    state.assessmentAnswers[String(questionId)] = String(optionId);
    var questions = normalizeArray(state.assessmentScale && state.assessmentScale.questions);
    var currentIndex = Math.max(0, Math.min(Number(state.assessmentCurrentIndex || 0), Math.max(questions.length - 1, 0)));
    if (questions.length && currentIndex < questions.length - 1) {
      state.assessmentCurrentIndex = currentIndex + 1;
    } else if (questions.length) {
      submitAssessment();
      return;
    }
    renderPage(pageByKey.assessment);
  }

  function previousAssessmentQuestion() {
    state.assessmentCurrentIndex = Math.max(0, Number(state.assessmentCurrentIndex || 0) - 1);
    renderPage(pageByKey.assessment);
  }

  function submitAssessment() {
    var scale = state.assessmentScale;
    var questions = normalizeArray(scale && scale.questions);
    if (!scale || !scale.scaleId || !questions.length) {
      showMessage("warning", "无法提交", "职业测评题库尚未加载完成。");
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
      showMessage("info", "已生成预览结果", "当前未调用受保护后端，部署后会写入 CyanCruise 职业画像。");
      return;
    }
    state.assessmentSubmitting = true;
    renderPage(pageByKey.assessment);
    post(endpoints.assessmentSubmit, {
      userId: state.identity.userId,
      scaleId: scale.scaleId,
      answers: assessmentAnswersPayload()
    }).then(function (result) {
      state.assessmentResult = result;
      state.assessmentRecords = [result].concat(state.assessmentRecords || []);
      return post(endpoints.snapshot, state.identity.userId).catch(function () { return state.snapshot; });
    }).then(function (snapshot) {
      if (snapshot) {
        state.snapshot = snapshot;
      }
      updateOverviewCards();
      showMessage("info", "测评已完成", "结果已写入 CyanCruise 职业画像。");
    }).catch(function (error) {
      showMessage("error", "提交失败", error.message || "职业测评提交接口暂不可用。");
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
      out[key] = state.assessmentAnswers[key];
    });
    return out;
  }

  function assessmentAnsweredCount(questions) {
    var count = 0;
    questions.forEach(function (question) {
      if (state.assessmentAnswers[String(question.questionId)]) {
        count += 1;
      }
    });
    return count;
  }

  function assessmentResultPanel(result) {
    var summary = firstText(result.resultSummary, result.summary, "已完成");
    var roles = normalizeArray(result.suggestedRoles);
    var counts = result.dimensionCounts || {};
    var rows = [
      ["结果", summary],
      ["量表", firstText(result.scaleTitle, getValue(state.snapshot, "assessment.scaleTitle"), "职业测评")],
      ["记录", firstText(result.recordId, getValue(state.snapshot, "assessment.lastRecordId"), "预览")]
    ];
    return metricsPanel("测评结果", rows) +
      '<section class="panel"><h3>推荐方向</h3><ul class="compact-list">' +
      (roles.length ? roles : defaultAssessmentRoles(summary)).map(function (role) {
        return '<li>' + escapeHtml(role) + '</li>';
      }).join("") + '</ul><p class="section-note">维度计数：' +
      escapeHtml(Object.keys(counts).map(function (key) { return key + "=" + counts[key]; }).join(" / ") || "预览结果") +
      '</p></section>';
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
      var optionId = state.assessmentAnswers[String(question.questionId)];
      var option = normalizeArray(question.options).filter(function (item) {
        return String(item.optionId) === String(optionId);
      })[0];
      if (option && option.dimensionCode) {
        counts[option.dimensionCode] = (counts[option.dimensionCode] || 0) + 1;
      }
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
      var optionId = state.assessmentAnswers[String(question.questionId)];
      var option = normalizeArray(question.options).filter(function (item) {
        return String(item.optionId) === String(optionId);
      })[0];
      if (option && option.dimensionCode) {
        counts[option.dimensionCode] = (counts[option.dimensionCode] || 0) + 1;
      }
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
      body += statePanel("职业测评", "后续通过答题分析人格、性格、偏好和行动风格，并把结果补入完整用户画像，用于指导路径规划和今日行动。当前先保留入口，不展开题组。", "pending");
    } else if (item.key === "assistant") {
      body += statePanel("助手会话", "可调用发送消息和会话列表契约；真实智能服务在后续 change 接入。", "pending");
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
    renderShell(item, body);
  }

  function renderInterviewPage(item) {
    var body = "";
    if (state.interviewError) body += statePanel("暂时没有完成操作", state.interviewError, "warning");
    if (state.interviewReport) {
      body += renderInterviewReport(state.interviewReport);
    } else if (state.activeInterview) {
      body += renderInterviewRoom();
    } else {
      body += renderInterviewSetup();
    }
    body += renderInterviewHistory();
    renderShell(item, body);
  }

  function renderInterviewHub(item) {
    var aiHistory = normalizeArray(state.interviews).filter(isAiInterview);
    var panoramaHistory = normalizeArray(state.interviews).filter(isPanoramaInterview);
    var body = '<section class="feature-section full"><div class="section-heading"><div><h3>选择面试方式</h3>' +
      '<p class="section-note">两种练习方式使用不同页面，记录也分别保存和展示。</p></div></div>' +
      '<div class="feature-grid interview-type-grid">' +
      '<article class="feature-card"><div class="app-icon-tile app-icon-tile--cyan"><span class="icon-glyph">智</span></div>' +
      '<h3>AI 模拟面试</h3><p>通过文字问答练习岗位表达，结束后获得结构化复盘。</p>' +
      '<button type="button" data-link="interview">进入 AI 模拟面试</button></article>' +
      '<article class="feature-card"><div class="app-icon-tile app-icon-tile--candy"><span class="icon-glyph">景</span></div>' +
      '<h3>全景仿真面试</h3><p>独立的全景面试方式，用于更贴近真实场景的综合练习。</p>' +
      '<button type="button" data-link="interview-panorama">进入全景仿真面试</button></article></div></section>' +
      renderInterviewHistoryList("AI 模拟面试记录", aiHistory, "还没有 AI 模拟面试记录。", "interview") +
      renderInterviewHistoryList("全景仿真面试记录", panoramaHistory, "还没有全景仿真面试记录。", "interview-panorama");
    renderShell(item, body);
  }

  function renderPanoramaInterviewPage(item) {
    var history = normalizeArray(state.interviews).filter(isPanoramaInterview);
    var body = state.panoramaReport ? renderPanoramaReport() : state.panoramaSession ? renderPanoramaRoom() : renderPanoramaPreparation();
    if (!state.panoramaSession && !state.panoramaReport) {
      body += renderInterviewHistoryList("全景仿真面试记录", history, "还没有全景仿真面试记录。", "interview-panorama");
    }
    renderShell(item, body);
    attachPanoramaCamera();
  }

  function renderPanoramaPreparation() {
    var role = firstText(getValue(state.snapshot, "preferences.targetRole"), getValue(state.snapshot, "resume.targetJob"), "");
    var resumes = normalizeArray(state.resumes);
    var cameraReady = !!state.panoramaStream;
    var cameraFallback = state.panoramaCameraState === "fallback";
    return '<section class="panorama-experience panorama-preparation full"><div class="panorama-overlay">' +
      '<div class="panorama-topbar"><div><span class="resource-type">沉浸式练习</span><h3>全景仿真面试</h3>' +
      '<p>面试画面只在当前浏览器中实时预览，不会上传或保存视频。</p></div>' +
      '<button type="button" class="secondary" data-link="interview-home">返回面试中心</button></div>' +
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
      '<div class="actions-row"><button type="button" data-panorama-action="camera" ' + (state.panoramaCameraState === "requesting" ? "disabled" : "") + '>' +
      (cameraReady ? "重新连接摄像头" : "启用摄像头和麦克风") + '</button>' +
      ((!cameraReady && !cameraFallback && state.panoramaCameraState !== "requesting") ? '<button type="button" class="secondary" data-panorama-action="fallback">无摄像头继续</button>' : '') +
      '<button type="button" data-panorama-action="start" ' + ((!cameraReady && !cameraFallback) || state.panoramaBusy ? "disabled" : "") + '>' +
      (state.panoramaBusy ? "正在准备面试" : "进入面试房间") + '</button></div></div></div></div></section>';
  }

  function renderPanoramaRoom() {
    var transcript = state.panoramaTranscript || "";
    return '<section class="panorama-experience panorama-live full"><div class="panorama-overlay">' +
      '<div class="panorama-question"><span>' + (state.panoramaAnswerCount + 1) + '</span><p>' + escapeHtml(state.panoramaQuestion || "正在准备问题……") + '</p></div>' +
      '<div class="panorama-live-stage"><div class="panorama-ai-presence"><span class="ai-pulse"></span><strong>AI 面试官</strong><small>正在与你面对面交流</small></div>' +
      '<div class="panorama-video-frame">' + (state.panoramaStream ? '<video id="panoramaCamera" autoplay muted playsinline aria-label="摄像头实时预览"></video>' :
      '<div class="panorama-camera-off"><span>摄像头未连接</span><strong>你仍可继续完成 AI 面试</strong></div>') +
      '<span class="panorama-live-badge">' + (state.panoramaStream ? "摄像头已连接 · 仅本地预览" : "无摄像头模式") + '</span></div></div>' +
      '<div class="panorama-answer-panel"><div class="panorama-timer"><small>剩余时间</small><strong id="panoramaTimer">' + formatPanoramaTime(state.panoramaSeconds) + '</strong></div>' +
      '<label>回答内容（可修改）<textarea id="panoramaAnswer" rows="3" placeholder="题目出现后已自动计时；你可以开启语音输入，也可以直接输入">' + escapeHtml(transcript) + '</textarea></label>' +
      '<div class="actions-row"><button type="button" data-panorama-action="listen" ' + (state.panoramaBusy ? "disabled" : "") + '>' +
      (state.panoramaAnswering ? "正在聆听" : "开启语音输入") + '</button>' +
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
      '<button type="button" class="secondary" data-link="interview-home">返回面试中心</button></div></section>';
  }

  function renderInterviewSetup() {
    var role = firstText(getValue(state.snapshot, "preferences.targetRole"), getValue(state.snapshot, "resume.targetJob"), "");
    var resumes = normalizeArray(state.resumes);
    return '<section class="panel full interview-setup"><h3>开始一次模拟面试</h3>' +
      '<p>系统会结合目标岗位、你选择的简历和个人情况提问。当前提供文字练习。</p>' +
      '<label>目标岗位<input id="interviewPosition" value="' + escapeAttr(role) + '" placeholder="例如：后端开发工程师"></label>' +
      '<label>使用的简历<select id="interviewResume"><option value="">暂不使用简历</option>' + resumes.map(function (resume) {
        return '<option value="' + escapeAttr(resume.resumeId) + '">' + escapeHtml(firstText(resume.title, "简历 " + resume.resumeId)) + '</option>';
      }).join("") + '</select></label>' +
      '<label>练习难度<select id="interviewDifficulty"><option value="Easy">入门</option><option value="Normal" selected>常规</option><option value="Hard">进阶</option></select></label>' +
      '<div class="actions-row"><button type="button" data-interview-action="start" ' + (state.interviewBusy ? 'disabled' : '') + '>' +
      (state.interviewBusy ? "正在准备" : "开始练习") + '</button></div></section>';
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
      (state.interviewAnswerCount > 0 ? '<button type="button" class="secondary" data-interview-action="finish" ' + (state.interviewBusy ? 'disabled' : '') + '>提前结束并评分</button>' : '') +
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
      '<div class="actions-row"><button type="button" data-interview-action="reset">再练一次</button></div></section>';
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

  function renderInterviewHistory() {
    return renderInterviewHistoryList("AI 模拟面试记录", normalizeArray(state.interviews).filter(isAiInterview),
      "还没有 AI 模拟面试记录。完成一次回答后，这里会保留复盘。", "interview");
  }

  function renderInterviewHistoryList(title, history, emptyText, route) {
    return '<section class="panel full"><h3>' + escapeHtml(title) + '</h3>' + (history.length ? history.map(function (entry) {
      return '<article class="list-row"><div><strong>' + escapeHtml(firstText(entry.positionName, "目标岗位待确认")) + '</strong><p>' +
        escapeHtml(entry.status === "COMPLETED" ? "已完成" : "进行中") + (entry.finalScore != null ? " · " + entry.finalScore + " 分" : "") +
        '</p></div>' + (route === "interview" ? '<button type="button" class="secondary" data-interview-action="open" data-interview-id="' +
        escapeAttr(entry.interviewId) + '">查看</button>' : '<span class="chip">全景记录</span>') + '</article>';
    }).join("") : '<p class="empty-copy">' + escapeHtml(emptyText) + '</p>') + '</section>';
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
    if (action === "camera") { startPanoramaCamera(); return; }
    if (action === "fallback") { usePanoramaWithoutCamera(); return; }
    if (action === "start") { startPanoramaInterview(); return; }
    if (action === "listen") { startPanoramaAnswer(); return; }
    if (action === "answer") { submitPanoramaAnswer(); return; }
    if (action === "finish") { finishPanoramaInterview(); return; }
    if (action === "reset") { resetPanoramaInterview(); }
  }

  function startPanoramaCamera() {
    state.panoramaError = null; state.panoramaNotice = null;
    stopPanoramaMedia();
    state.panoramaCameraState = "requesting";
    renderPage(pageByKey["interview-panorama"]);
    requestPanoramaMedia({ video: { facingMode: "user", width: { ideal: 1280 }, height: { ideal: 720 } }, audio: true })
      .then(function (stream) {
        state.panoramaStream = stream; state.panoramaCameraState = "ready"; state.panoramaError = null;
      }).catch(function (error) {
        state.panoramaCameraState = error && error.name === "NotAllowedError" ? "denied" : "unavailable";
        state.panoramaError = panoramaCameraFailureMessage(error);
      }).then(function () { renderPage(pageByKey["interview-panorama"]); });
  }

  function requestPanoramaMedia(constraints) {
    var candidates = panoramaMediaCandidates();
    function attempt(index, lastError) {
      if (index >= candidates.length) return Promise.reject(lastError || { name: "UnsupportedError" });
      try {
        return candidates[index](constraints).catch(function (error) { return attempt(index + 1, error); });
      } catch (error) {
        return attempt(index + 1, error);
      }
    }
    return attempt(0, null);
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

  function panoramaCameraFailureMessage(error) {
    if (window.isSecureContext === false) {
      return "当前页面不是安全连接，浏览器禁止网页调用摄像头。你可以改用 HTTPS，或选择“无摄像头继续”完成面试。";
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
    stopPanoramaMedia(); state.panoramaCameraState = "fallback"; state.panoramaError = null;
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
      state.panoramaAnswerCount = 0; state.panoramaTranscript = "";
      state.panoramaSeconds = panoramaAnswerLimit(state.panoramaDifficulty);
      state.interviews = [result.session].concat(normalizeArray(state.interviews));
    }).catch(function (error) {
      state.panoramaError = error.message || "全景面试暂时无法开始，请稍后重试。";
    }).then(function () {
      state.panoramaBusy = false; renderPage(pageByKey["interview-panorama"]);
      if (state.panoramaSession && state.panoramaQuestion) startPanoramaTimer();
    });
  }

  function startPanoramaAnswer() {
    if (state.panoramaAnswering || state.panoramaBusy) return;
    state.panoramaError = null; state.panoramaAnswering = true;
    var Recognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!Recognition) {
      state.panoramaError = "当前浏览器不支持语音转文字，请直接在回答框中输入内容。摄像头预览仍会继续。";
      renderPage(pageByKey["interview-panorama"]);
      var fallbackInput = document.getElementById("panoramaAnswer"); if (fallbackInput) fallbackInput.focus();
      return;
    }
    var recognition = new Recognition();
    recognition.lang = "zh-CN"; recognition.continuous = true; recognition.interimResults = true;
    recognition.onresult = function (event) {
      var text = "";
      for (var i = 0; i < event.results.length; i += 1) text += event.results[i][0].transcript;
      state.panoramaTranscript = text;
      var input = document.getElementById("panoramaAnswer"); if (input) input.value = text;
    };
    recognition.onerror = function () {
      state.panoramaError = "语音识别暂时不可用，你可以继续手工输入回答。";
      state.panoramaAnswering = false; renderPage(pageByKey["interview-panorama"]);
    };
    recognition.onend = function () { state.panoramaAnswering = false; };
    state.panoramaRecognition = recognition;
    try { recognition.start(); } catch (error) { state.panoramaAnswering = false; }
    renderPage(pageByKey["interview-panorama"]);
  }

  function submitPanoramaAnswer(timedOut) {
    if (!state.panoramaSession || state.panoramaBusy) return;
    var answer = valueOf("panoramaAnswer");
    if (!answer && timedOut) answer = "本题在规定时间内未完成回答。";
    if (!answer) { state.panoramaError = "请先完成本题回答，再进入下一题。"; renderPage(pageByKey["interview-panorama"]); return; }
    stopPanoramaRecognition(); stopPanoramaTimer();
    state.panoramaBusy = true; state.panoramaTranscript = answer; state.panoramaError = null;
    state.panoramaNotice = timedOut ? "本题答题时间已结束，正在自动提交并进入下一题。" : null;
    renderPage(pageByKey["interview-panorama"]);
    var shouldFinish = false;
    post(endpoints.guidedInterviewAnswer, { userId: state.identity.userId, interviewId: state.panoramaSession.interviewId, answer: answer })
      .then(function (result) {
        state.panoramaSession = result.session; state.panoramaAnswerCount += 1; state.panoramaTranscript = "";
        shouldFinish = state.panoramaAnswerCount >= 7 || !result.interviewerMessage;
        state.panoramaQuestion = shouldFinish ? null : result.interviewerMessage.content;
        state.panoramaSeconds = shouldFinish ? 0 : panoramaAnswerLimit(state.panoramaDifficulty);
      }).catch(function (error) { state.panoramaError = error.message || "回答提交失败，请稍后重试。"; })
      .then(function () {
        state.panoramaBusy = false;
        if (shouldFinish) { finishPanoramaInterview(); return; }
        renderPage(pageByKey["interview-panorama"]);
        if (!state.panoramaError) startPanoramaTimer();
      });
  }

  function finishPanoramaInterview() {
    if (!state.panoramaSession || state.panoramaBusy) return;
    if (!state.panoramaAnswerCount) { state.panoramaError = "请至少完成一道回答后再结束面试。"; renderPage(pageByKey["interview-panorama"]); return; }
    stopPanoramaRecognition(); stopPanoramaTimer(); state.panoramaBusy = true;
    post(endpoints.guidedInterviewFinish, { userId: state.identity.userId, interviewId: state.panoramaSession.interviewId })
      .then(function (report) {
        state.panoramaReport = report; state.panoramaSession.report = report; state.panoramaSession.status = "COMPLETED";
        state.panoramaSession.finalScore = report.overallScore; stopPanoramaMedia();
      }).catch(function (error) { state.panoramaError = error.message || "暂时无法生成复盘，请稍后重试。"; })
      .then(function () { state.panoramaBusy = false; renderPage(pageByKey["interview-panorama"]); });
  }

  function resetPanoramaInterview() {
    stopPanoramaMedia(); state.panoramaSession = null; state.panoramaQuestion = null; state.panoramaTranscript = "";
    state.panoramaReport = null; state.panoramaError = null; state.panoramaNotice = null; state.panoramaAnswerCount = 0; state.panoramaSeconds = 0;
    state.panoramaDifficulty = "Normal";
    state.panoramaCameraState = "idle"; renderPage(pageByKey["interview-panorama"]);
  }

  function attachPanoramaCamera() {
    var video = document.getElementById("panoramaCamera");
    if (!video || !state.panoramaStream) return;
    if (video.srcObject !== state.panoramaStream) video.srcObject = state.panoramaStream;
    var playing = video.play(); if (playing && playing.catch) playing.catch(function () {});
  }

  function startPanoramaTimer() {
    stopPanoramaTimer();
    if (state.panoramaSeconds <= 0) state.panoramaSeconds = panoramaAnswerLimit(state.panoramaDifficulty);
    state.panoramaTimer = window.setInterval(function () {
      state.panoramaSeconds = Math.max(0, state.panoramaSeconds - 1); var timer = document.getElementById("panoramaTimer");
      if (timer) timer.textContent = formatPanoramaTime(state.panoramaSeconds);
      if (state.panoramaSeconds <= 0) { stopPanoramaTimer(); submitPanoramaAnswer(true); }
    }, 1000);
  }

  function stopPanoramaTimer() { if (state.panoramaTimer) window.clearInterval(state.panoramaTimer); state.panoramaTimer = null; }
  function panoramaAnswerLimit(difficulty) {
    if (difficulty === "Easy") return 3 * 60;
    if (difficulty === "Hard") return 8 * 60;
    return 5 * 60;
  }
  function formatPanoramaTime(seconds) { var safe = Math.max(0, Number(seconds) || 0); return String(Math.floor(safe / 60)).padStart(2, "0") + ":" + String(safe % 60).padStart(2, "0"); }
  function stopPanoramaRecognition() {
    if (state.panoramaRecognition) { try { state.panoramaRecognition.stop(); } catch (error) {} }
    state.panoramaRecognition = null; state.panoramaAnswering = false;
  }
  function stopPanoramaMedia() {
    stopPanoramaRecognition(); stopPanoramaTimer();
    if (state.panoramaStream && state.panoramaStream.getTracks) state.panoramaStream.getTracks().forEach(function (track) { track.stop(); });
    state.panoramaStream = null;
  }

  function handleInterviewAction(target) {
    var action = target.getAttribute("data-interview-action");
    if (state.interviewBusy) return;
    state.interviewError = null;
    if (action === "speech") { toggleAiInterviewSpeech(); return; }
    if (action === "reset") { stopAiInterviewSpeech(); state.activeInterview = null; state.interviewMessages = []; state.interviewReport = null; state.interviewCurrentQuestion = null; state.interviewAnswerCount = 0; state.interviewDraft = ""; renderPage(pageByKey.interview); return; }
    if (action === "open") { openInterview(target.getAttribute("data-interview-id")); return; }
    var draftPosition = action === "start" ? valueOf("interviewPosition") : "";
    var draftResumeId = action === "start" ? valueOf("interviewResume") : "";
    var draftDifficulty = action === "start" ? valueOf("interviewDifficulty") : "Normal";
    var draftAnswer = action === "answer" ? valueOf("interviewAnswer") : "";
    state.interviewBusy = true; renderPage(pageByKey.interview);
    var call;
    if (action === "start") {
      call = post(endpoints.guidedInterviewStart, { userId: state.identity.userId, request: { positionName: draftPosition, resumeId: draftResumeId ? Number(draftResumeId) : null, difficulty: draftDifficulty, mode: "TEXT" } }).then(function (result) {
        state.activeInterview = result.session; state.interviewMessages = [result.openingMessage]; state.interviewCurrentQuestion = result.openingMessage.content;
        state.interviewAnswerCount = 0; state.interviewDraft = ""; state.interviews = [result.session].concat(normalizeArray(state.interviews));
      });
    } else if (action === "answer") {
      if (!draftAnswer) { state.interviewBusy = false; state.interviewError = "请先写下你的回答。"; renderPage(pageByKey.interview); return; }
      stopAiInterviewSpeech();
      call = post(endpoints.guidedInterviewAnswer, { userId: state.identity.userId, interviewId: state.activeInterview.interviewId, answer: draftAnswer }).then(function (result) {
        state.interviewMessages.push(result.userMessage, result.interviewerMessage); state.activeInterview = result.session;
        state.interviewAnswerCount += 1; state.interviewDraft = "";
        if (state.interviewAnswerCount >= 7) {
          return post(endpoints.guidedInterviewFinish, { userId: state.identity.userId, interviewId: state.activeInterview.interviewId })
            .then(function (report) { state.interviewReport = report; state.activeInterview.status = "COMPLETED"; state.activeInterview.finalScore = report.overallScore; });
        }
        state.interviewCurrentQuestion = result.interviewerMessage.content;
      });
    } else {
      call = post(endpoints.guidedInterviewFinish, { userId: state.identity.userId, interviewId: state.activeInterview.interviewId }).then(function (report) { state.interviewReport = report; });
    }
    call.catch(function (error) { state.interviewError = error.message || "模拟面试暂时不可用，请稍后重试。"; }).then(function () { state.interviewBusy = false; renderPage(pageByKey.interview); });
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

  function openInterview(interviewId) {
    var session = normalizeArray(state.interviews).filter(function (item) { return String(item.interviewId) === String(interviewId); })[0];
    if (!session) return;
    state.activeInterview = session; state.interviewReport = session.report || null; state.interviewBusy = true; renderPage(pageByKey.interview);
    if (state.route !== "interview") window.location.hash = "interview";
    post(endpoints.interviewMessages, { userId: state.identity.userId, interviewId: session.interviewId }).then(function (messages) {
      state.interviewMessages = normalizeArray(messages);
      state.interviewAnswerCount = state.interviewMessages.filter(function (message) { return String(message.role).toUpperCase() === "USER"; }).length;
      var questions = state.interviewMessages.filter(function (message) { return String(message.role).toUpperCase() === "AI"; });
      state.interviewCurrentQuestion = questions.length ? questions[questions.length - 1].content : null;
    })
      .catch(function (error) { state.interviewError = error.message || "无法读取练习记录。"; }).then(function () { state.interviewBusy = false; renderPage(pageByKey.interview); });
  }

  function renderFeatureShell(item, title, summary, innerHtml) {
    els.pageHost.innerHTML =
      '<header class="feature-page-header">' +
      '<div><p class="eyebrow">CyanCruise</p><h2>' + escapeHtml(title) + '</h2><p class="lead">' + escapeHtml(summary) + '</p></div>' +
      pageHeaderActions(item) +
      '</header>' +
      '<div class="feature-content">' + innerHtml + '</div>';
  }

  function renderShell(item, innerHtml) {
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
    if (item.key === "workbench" || (item.key === "assessment" && state.assessmentSelectedScaleId)) {
      return "";
    }
    var back = backRouteFor(item.key);
    var actions = [];
    if (back) {
      actions.push('<button type="button" class="secondary" data-back-route="' + escapeHtml(back) + '">返回</button>');
    }
    actions.push('<button type="button" class="secondary" data-link="workbench">首页</button>');
    return actions.length ? '<div class="page-actions">' + actions.join("") + '</div>' : "";
  }

  function backRouteFor(key) {
    var source = state.returnRoutes && state.returnRoutes[key];
    if (source && source !== key && pageByKey[source]) {
      return source;
    }
    return parentRouteFor(key);
  }

  function parentRouteFor(key) {
    var parents = {
      "employment-home": "workbench",
      "further-study-home": "workbench",
      "resume-home": "employment-home",
      "resume": "resume-home",
      "resume-diagnosis": "resume-home",
      "interview-home": "employment-home",
      "interview": "interview-home",
      "interview-panorama": "interview-home",
      "postgraduate-exam": "further-study-home",
      "postgraduate-recommendation": "further-study-home",
      "study-abroad": "further-study-home",
      "today-action": "workbench",
      "assessment": "workbench",
      "career-plan": "workbench",
      "assistant": "workbench",
      "messages": "workbench",
      "employment-insight": "employment-home",
      "career-resources": "employment-home",
      "file-upload-preview": "resume-home",
      "onboarding": "workbench",
      "admin-console": "workbench"
    };
    return parents[key] || "";
  }

  function handlePageHostClick(event) {
    var panoramaTarget = findAttributeTarget(event.target, "data-panorama-action");
    if (panoramaTarget) { event.preventDefault(); event.stopPropagation(); handlePanoramaAction(panoramaTarget); return; }
    var interviewTarget = findAttributeTarget(event.target, "data-interview-action");
    if (interviewTarget) { event.preventDefault(); event.stopPropagation(); handleInterviewAction(interviewTarget); return; }
    var planHorizonTarget = findPlanHorizonTarget(event.target);
    if (planHorizonTarget) {
      event.preventDefault();
      event.stopPropagation();
      updatePlanHorizon(planHorizonTarget.getAttribute("data-plan-horizon"));
      return;
    }
    var planFocusTarget = findPlanFocusTarget(event.target);
    if (planFocusTarget) {
      event.preventDefault();
      event.stopPropagation();
      focusPlanPhase(planFocusTarget.getAttribute("data-plan-focus"));
      return;
    }
    var ensurePlanTarget = findEnsurePlanTarget(event.target);
    if (ensurePlanTarget) {
      event.preventDefault();
      event.stopPropagation();
      ensureEmploymentPlan();
      return;
    }
    var resourceTarget = findResourceDetailTarget(event.target);
    if (resourceTarget) {
      event.preventDefault();
      event.stopPropagation();
      showResourceDetailDialog(resourceTarget.getAttribute("data-resource-detail"));
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
    var assessmentSubmitTarget = findAssessmentSubmitTarget(event.target);
    if (assessmentSubmitTarget) {
      event.preventDefault();
      event.stopPropagation();
      submitAssessment();
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
    if (!target || !target.getAttribute || target.type !== "checkbox") {
      return;
    }
    var taskId = target.getAttribute("data-plan-task");
    if (taskId) {
      setPlanTaskChecked(taskId, target.checked);
    }
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

  function findPlanHorizonTarget(node) {
    while (node && node !== els.pageHost) {
      if (node.getAttribute && node.getAttribute("data-plan-horizon")) {
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
    var targetRole = employmentTargetRole(plan);
    var progressState = readPlanProgress(plan, targetRole, normalizeArray(plan.phases));
    progressState.checked[taskId] = !!checked;
    persistPlanProgress(plan, targetRole, progressState);
    updateOverviewCards();
    renderPage(pageByKey[state.route]);
  }

  function focusPlanPhase(phaseId) {
    if (!phaseId || state.route !== "career-plan") {
      return;
    }
    var plan = state.plan && !state.plan.unavailable ? state.plan : {};
    var targetRole = employmentTargetRole(plan);
    var progressState = readPlanProgress(plan, targetRole, normalizeArray(plan.phases));
    progressState.activePhaseId = phaseId;
    persistPlanProgress(plan, targetRole, progressState);
    renderPage(pageByKey[state.route]);
  }

  function updatePlanHorizon(value) {
    if (state.route !== "career-plan") {
      return;
    }
    var years = parseInt(trim(value), 10);
    if (years !== 1 && years !== 3) {
      return;
    }
    var plan = state.plan && !state.plan.unavailable ? state.plan : {};
    var targetRole = employmentTargetRole(plan);
    persistSelectedPlanHorizon(plan, targetRole, years);
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
      window.location.hash = key;
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
      window.location.hash = key;
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
      return [
        ["目标方向", textFromSnapshot("preferences.targetRole", "onboarding.targetRole") || "待确认"],
        ["规划状态", state.plan && !state.plan.unavailable ? firstText(state.plan.summary, state.plan.weekFocus, "已生成") : "规划中"],
        ["方向入口", "考研 / 保研 / 留学"],
        ["下一步", "选择深造方向"]
      ];
    }
    if (goal === "explore") {
      return [
        ["当前路线", "先了解一下"],
        ["就业入口", "可进入"],
        ["深造入口", "可进入"],
        ["下一步", "选择方向"]
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
      if (view.dailyPlan && view.dailyPlan.items && view.dailyPlan.items.length) {
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
    renderShell(item, statePanel("无管理员权限", "该页面需要 ADMIN 或平台管理员身份，当前不会调用 /cc001/admin/*。", "warning"));
    showMessage("warning", "已阻止管理员调用", "当前身份不满足管理员页面要求。");
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
    Promise.all([
      post(endpoints.snapshot, state.identity.userId).catch(asUnavailable),
      post(endpoints.today, state.identity.userId).catch(asUnavailable),
      post(endpoints.resumes, state.identity.userId).catch(asUnavailable),
      post(endpoints.plan, state.identity.userId).catch(asUnavailable),
      post(endpoints.interviews, state.identity.userId).catch(asUnavailable)
    ]).then(function (results) {
      state.snapshot = results[0];
      state.today = results[1];
      state.resumes = normalizeArray(results[2]);
      state.plan = results[3];
      state.interviews = normalizeArray(results[4]);
      updateOverviewCards();
      renderPage(pageByKey[state.route]);
    }).catch(function (error) {
      renderPreview();
      showMessage("error", "加载失败", error.message || "后端暂不可用，已保留可恢复页面状态。");
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
      localStorage.setItem("cyancruise.previewProfile", JSON.stringify(request));
      state.snapshot = { onboarding: request, preferences: { targetRole: request.targetRole } };
      updateOverviewCards();
      refreshPlanAfterProfileTargetChange(previewPreviousTarget);
      showMessage("info", "已保存到本地预览", "file:// 模式不调用后端。");
      return;
    }
    var previousTarget = currentProfileTargetRole();
    post(endpoints.onboarding, { userId: state.identity.userId, request: request }).then(function (snapshot) {
      state.snapshot = snapshot;
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
    migrateLegacyStorageKey("cyancruise.homeIntent", "careerloop.homeIntent");
    return parseStorageJson(localStorage, "cyancruise.homeIntent") || {};
  }

  function submitHomeIntent(event) {
    event.preventDefault();
    var intent = {
      goal: valueOf("homeGoal"),
      targetRole: valueOf("homeTargetRole"),
      preference: valueOf("homePreference"),
      identityType: valueOf("profileIdentityType"),
      educationStage: valueOf("profileEducationStage"),
      school: valueOf("profileSchool"),
      major: valueOf("profileMajor"),
      schoolMajor: valueOf("profileMajor"),
      resumeStatus: valueOf("resumeStatus"),
      experience: valueOf("profileExperience")
    };
    localStorage.setItem("cyancruise.homeIntent", JSON.stringify(intent));
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
      resumeStatus: intent.resumeStatus,
      experience: intent.experience,
      routeGoal: intent.goal,
      preference: ["路线：" + labelForGoal(intent.goal), intent.preference].filter(Boolean).join("；")
    };
    if (!hasUserIdentity() || isFilePreview()) {
      var previewPreviousTarget = currentProfileTargetRole();
      localStorage.setItem("cyancruise.previewProfile", JSON.stringify(request));
      state.snapshot = { onboarding: request, preferences: { targetRole: request.targetRole } };
      updateOverviewCards();
      refreshPlanAfterProfileTargetChange(previewPreviousTarget);
      localStorage.setItem("cyancruise.homeIntentEditing", "true");
      renderPage(pageByKey[state.route]);
      showMessage("info", "已保存", "用户画像草稿已保存到当前浏览器。");
      return;
    }
    var previousTarget = currentProfileTargetRole();
    post(endpoints.onboarding, { userId: state.identity.userId, request: request }).then(function (snapshot) {
      localStorage.setItem("cyancruise.homeIntentSaved", "true");
      localStorage.removeItem("cyancruise.homeIntentEditing");
      localStorage.removeItem("cyancruise.previewProfile");
      state.snapshot = snapshot;
      updateOverviewCards();
      if (!refreshPlanAfterProfileTargetChange(previousTarget)) {
        renderPage(pageByKey[state.route]);
      }
      showMessage("info", "已保存", "用户画像草稿已写入职业画像。");
    }).catch(function (error) {
      localStorage.setItem("cyancruise.homeIntentEditing", "true");
      renderPage(pageByKey[state.route]);
      showMessage("warning", "已本地保存", "平台暂未写入成功，但用户画像草稿已保存在当前浏览器。");
    });
  }

  function currentProfileTargetRole() {
    return textFromSnapshot("preferences.targetRole", "onboarding.targetRole", "resume.targetJob");
  }

  function refreshPlanAfterProfileTargetChange(previousTarget) {
    var nextTarget = currentProfileTargetRole();
    if (sameTargetRole(previousTarget, nextTarget)) {
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
    ensureEmploymentPlan();
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
    if (goal === "explore") {
      return "先了解一下";
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
    var target = textFromSnapshot("preferences.targetRole", "onboarding.targetRole", "resume.targetJob");
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
    var names = ["__CAREERLOOP_COSMIC_CONTEXT__", "__COSMIC_CONTEXT__", "cosmicContext", "kdContext", "KDCONTEXT", "userInfo", "currentUser", "loginUser"];
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
    var keys = [
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
    var keys = ["userInfo", "currentUser", "loginUser", "cosmicContext", "kdContext"];
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
    migrateLegacyStorageKey("cyancruise.userId", "careerloop.userId");
    migrateLegacyStorageKey("cyancruise.adminId", "careerloop.adminId");
    migrateLegacyStorageKey("cyancruise.roles", "careerloop.roles");
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
    if (!state.identity || state.identity.mode !== "production" || state.identity.userId) {
      return Promise.resolve();
    }
    return post(endpoints.identityCurrent, {}).then(function (identity) {
      var userId = firstText(identity.userId, identity.adminId);
      if (!userId || identity.status !== "OK") {
        showMessage("warning", "平台身份未就绪", firstText(identity.message, identity.status, "identity response has no userId"));
        return;
      }
      state.identity = {
        mode: "production",
        userId: userId,
        adminId: firstText(identity.adminId, identity.userId),
        roles: normalizeRoles(identity.roles),
        displayName: firstText(identity.displayName, identity.userName, identity.username, identity.name, identity.nickName, identity.nickname, userId ? "用户" : ""),
        source: firstText(identity.source, "cc001-identity-current")
      };
      updateIdentityState();
    }).catch(function (error) {
      showMessage("warning", "平台身份调用失败", error && error.message ? error.message : "identity request failed");
    });
  }

  function post(path, body) {
    var request = resolveApiRequest(path, body);
    return fetch(request.url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "same-origin",
      body: JSON.stringify(request.body)
    }).then(function (response) {
      if (!response.ok) {
        throw new Error(path + " 返回 " + response.status);
      }
      return response.json();
    }).then(function (payload) {
      if (request.mode === "kapi" || request.mode === "kapi-v2") {
        if (payload && Object.prototype.hasOwnProperty.call(payload, "success")) {
          if (!payload.success) {
            throw new Error(path + " " + firstText(payload.message, payload.errorCode, "custom WebAPI failed"));
          }
          return payload.data;
        }
        if (payload && Object.prototype.hasOwnProperty.call(payload, "status")
            && Object.prototype.hasOwnProperty.call(payload, "data")) {
          if (!payload.status) {
            throw new Error(path + " " + firstText(payload.message, payload.errorCode, "custom WebAPI failed"));
          }
          return payload.data;
        }
      }
      return payload;
    });
  }

  function resolveApiRequest(path, body) {
    var mode = resolveApiMode();
    if (mode !== "kapi") {
      return { mode: "direct", url: resolveApiBase() + path, body: body };
    }
    if (resolveKapiRouteVersion() === "v2") {
      return resolveKapiV2Request(path, body);
    }
    var appId = firstText(readQueryOrStorage("appId", "cyancruise.kapi.appId"), "cc001");
    var serviceName = firstText(readQueryOrStorage("serviceName", "cyancruise.kapi.serviceName"), "careerloop");
    var accessToken = readQueryOrStorage("access_token", "cyancruise.kapi.accessToken");
    var url = resolveApiBase() + "/kapi/app/" + encodeURIComponent(appId) + "/" + encodeURIComponent(serviceName) + "/";
    if (accessToken) {
      url += "?access_token=" + encodeURIComponent(accessToken);
    }
    return {
      mode: "kapi",
      url: url,
      body: {
        path: path,
        body: body
      }
    };
  }

  function resolveKapiV2Request(path, body) {
    var cloudId = firstText(readQueryOrStorage("cloudId", "cyancruise.kapi.cloudId"), "v620");
    var appNumber = firstText(readQueryOrStorage("appNumber", "cyancruise.kapi.appNumber"), "v620_cc001");
    var apiCode = firstText(readQueryOrStorage("apiCode", "cyancruise.kapi.apiCode"), "cc001/careerloop/route");
    var accessToken = readQueryOrStorage("access_token", "cyancruise.kapi.accessToken");
    var url = resolveApiBase() + "/kapi/v2/" + encodeURIComponent(cloudId) + "/" + encodeURIComponent(appNumber) + "/" + encodeApiCode(apiCode);
    if (accessToken) {
      url += "?access_token=" + encodeURIComponent(accessToken);
    }
    return {
      mode: "kapi-v2",
      url: url,
      body: {
        path: path,
        body: body
      }
    };
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
      if (fromQuery === "direct") {
        localStorage.removeItem("cyancruise.apiMode");
        return "direct";
      }
      localStorage.setItem("cyancruise.apiMode", fromQuery);
      return fromQuery;
    }
    migrateLegacyStorageKey("cyancruise.apiMode", "careerloop.apiMode");
    var stored = trim(localStorage.getItem("cyancruise.apiMode")).toLowerCase();
    if (stored === "direct") {
      localStorage.removeItem("cyancruise.apiMode");
      return "kapi";
    }
    return firstText(stored, "kapi").toLowerCase();
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
    migrateLegacyStorageKey("cyancruise.apiBase", "careerloop.apiBase");
    var stored = trim(localStorage.getItem("cyancruise.apiBase"));
    if (stored) {
      return stored.replace(/\/$/, "");
    }
    return defaultApiBase();
  }

  function legacyStorageKey(storageKey) {
    return storageKey.indexOf("cyancruise.") === 0 ? "careerloop." + storageKey.substring("cyancruise.".length) : "";
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
    var cls = type === "warning" || type === "pending" ? " warning" : "";
    return '<section class="state-card' + cls + '"><h3>' + escapeHtml(title) + '</h3><p>' + escapeHtml(text) + "</p></section>";
  }

  function field(id, label, type, value, options) {
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
      "pages/interview/index": "interview-home",
      "pages/interview/start": "interview",
      "pages/interview/chat": "interview",
      "pages/interview/history": "interview-home",
      "pages/interview/report": "interview"
    };
    return map[normalized] || normalized || "onboarding";
  }

  function readPreviewProfile() {
    try {
      migrateLegacyStorageKey("cyancruise.previewProfile", "careerloop.previewProfile");
      var raw = localStorage.getItem("cyancruise.previewProfile");
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

  function normalizeRoute(hash) {
    var fromHash = trim(hash || window.location.hash || "").replace(/^#/, "");
    if (fromHash) {
      return fromHash;
    }
    var params = new URLSearchParams(window.location.search);
    return trim(params.get("ccRoute") || params.get("route")) || "workbench";
  }

  function hasUserIdentity() {
    return !!(state.identity && state.identity.userId);
  }

  function hasAdminRole(identity) {
    if (!identity) {
      return false;
    }
    if (identity.mode === "production" && identity.adminId) {
      return true;
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
    if (state.messageTimer) {
      window.clearTimeout(state.messageTimer);
      state.messageTimer = null;
    }
    els.messagePanel.className = "message-panel " + type;
    els.messagePanel.innerHTML =
      '<div class="message-copy"><strong>' + escapeHtml(title) + "</strong><span>" + escapeHtml(text) + "</span></div>" +
      '<button type="button" class="message-close" aria-label="关闭提示">×</button>';
    var close = els.messagePanel.querySelector(".message-close");
    if (close) {
      close.addEventListener("click", hideMessage);
    }
    if (type === "info") {
      state.messageTimer = window.setTimeout(hideMessage, 5000);
    }
  }

  function showConfirmDialog(title, text, confirmText, onConfirm) {
    hideConfirmDialog();
    var previousFocus = document.activeElement;
    var overlay = document.createElement("div");
    overlay.className = "confirm-overlay";
    overlay.innerHTML =
      '<div class="confirm-dialog" role="dialog" aria-modal="true" aria-labelledby="confirmDialogTitle" aria-describedby="confirmDialogText">' +
      '<div class="confirm-copy"><strong id="confirmDialogTitle">' + escapeHtml(title) + '</strong>' +
      '<span id="confirmDialogText">' + escapeHtml(text) + '</span></div>' +
      '<div class="confirm-actions">' +
      '<button type="button" class="secondary" data-confirm-cancel>取消</button>' +
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
    cancel.addEventListener("click", close);
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
    var details = {
      "resume-evidence": {
        title: "简历证据清单",
        summary: "把经历先整理成可验证证据，再写成简历要点。",
        route: "resume-diagnosis",
        actionText: "去简历诊断",
        sections: [
          { title: "先收集", items: ["目标岗位要求中反复出现的能力词。", "课程、项目、实习、竞赛、开源或作品中能证明这些能力的材料。", "每段经历对应的任务、动作、结果和可量化指标。"] },
          { title: "再改写", items: ["用动词开头描述你做了什么。", "补上技术栈、业务场景或协作对象。", "尽量写出结果，例如效率、规模、准确率、用户量或交付物。"] },
          { title: "检查", items: ["一条经历只服务一个核心能力。", "删除泛泛的形容词，保留证据和结果。", "和目标岗位无关的内容放到次要位置。"] }
        ]
      },
      "weekly-focus": {
        title: "本周求职行动重点",
        summary: "把求职推进拆成一周内能完成的三个动作。",
        route: "career-plan",
        actionText: "查看路径规划",
        sections: [
          { title: "第 1 步", items: ["选定一个本周主攻岗位，不要同时追太多方向。", "保存 3 条代表性岗位要求，提取共同能力词。"] },
          { title: "第 2 步", items: ["按能力词修改一版简历。", "至少补充一个项目或实习的结果指标。"] },
          { title: "第 3 步", items: ["完成一次模拟面试。", "记录 2 个回答卡住的问题，并写下下一轮改进动作。"] }
        ]
      },
      "interview-practice": {
        title: "面试练习前的回答结构",
        summary: "用固定结构减少临场组织语言的成本。",
        route: "interview",
        actionText: "开始模拟面试",
        sections: [
          { title: "经历讲述框架", items: ["背景：一句话交代当时的场景。", "任务：说明你负责解决什么问题。", "行动：讲清楚你的关键动作和取舍。", "结果：用结果、指标或复盘收尾。"] },
          { title: "项目题", items: ["先讲目标和边界，再讲架构或流程。", "准备一个技术难点、一个协作难点、一个复盘点。"] },
          { title: "行为题", items: ["准备失败经历、冲突经历、主动推进经历。", "回答时避免只讲态度，要给具体场景。"] }
        ]
      },
      "software-engineer-path": {
        title: "软件工程师路径",
        summary: "面向后端、Web、数据和 AI 应用岗位的基础成长路线。",
        route: "career-plan",
        actionText: "查看路径规划",
        sections: [
          { title: "基础能力", items: ["数据结构与算法、计算机网络、数据库、操作系统。", "至少掌握一门主力语言和对应工程框架。"] },
          { title: "项目能力", items: ["完成一个可部署的业务系统。", "补充登录、权限、存储、日志、测试和部署说明。"] },
          { title: "求职准备", items: ["把项目写成问题、方案、结果三段。", "围绕目标岗位准备高频八股和项目追问。"] }
        ]
      }
    };
    return details[key] || null;
  }

  function hideConfirmDialog() {
    var existing = document.querySelector(".confirm-overlay");
    if (existing && existing.parentNode) {
      existing.parentNode.removeChild(existing);
    }
  }

  function hideMessage() {
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

  document.addEventListener("DOMContentLoaded", init);
}());
