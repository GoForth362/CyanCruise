(function () {
  "use strict";

  var endpoints = {
    snapshot: "/cc001/career-profile/snapshot/get",
    onboarding: "/cc001/career-profile/onboarding/save",
    today: "/cc001/career-agent/today/get",
    assessmentSubmit: "/cc001/assessment/submit",
    resumes: "/cc001/resume/list",
    resumeCreate: "/cc001/resume/create",
    resumeDelete: "/cc001/resume/delete",
    plan: "/cc001/career-plan/summary",
    ensurePlan: "/cc001/career-plan/ensure",
    interviews: "/cc001/interview/list",
    startInterview: "/cc001/interview/start",
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
    page("workbench", "CyanCruise 首页", "available", "user", "填写基础信息，选择就业或深造路线。", ["snapshot", "onboarding"]),
    page("employment-home", "就业", "available", "user", "进入简历和面试核心功能。", ["resumes", "resumeCreate", "resumeDiagnosis", "interviews", "startInterview"]),
    page("further-study-home", "深造", "available", "user", "考研、保研和留学方向规划入口。", ["snapshot", "plan"]),
    page("postgraduate-exam", "考研", "entry-only", "user", "考研规划入口，后续接入规划 Agent。", ["plan"]),
    page("postgraduate-recommendation", "保研", "entry-only", "user", "保研规划入口，后续接入规划 Agent。", ["plan"]),
    page("study-abroad", "留学", "entry-only", "user", "留学规划入口，后续接入规划 Agent。", ["plan"]),
    page("onboarding", "新用户引导", "available", "user", "收集身份、目标岗位、简历状态和偏好信号。", ["onboarding"]),
    page("today-action", "今日行动", "available", "user", "展示下一步建议，并跳转到对应页面。", ["today"]),
    page("assessment", "职业测评", "entry-only", "user", "测评提交契约已就绪，完整题组页面后续细化。", ["assessmentSubmit"]),
    page("resume-home", "简历", "available", "user", "AI 简历制作和 AI 简历修改入口。", ["resumes", "resumeCreate", "resumeDiagnosis"]),
    page("resume", "简历", "available", "user", "查看简历记录，创建元数据，并关联文件能力。", ["resumes", "resumeCreate", "resumeDelete"]),
    page("file-upload-preview", "文件上传预览", "entry-only", "user", "展示上传、预览、下载、删除和文本抽取契约。", ["fileUpload", "filePreview", "fileDownload", "fileDelete", "fileExtractText"], { defaultNav: false, debugNav: true }),
    page("resume-diagnosis", "简历诊断", "available", "user", "围绕目标岗位分析匹配度、关键词和建议。", ["resumeDiagnosis", "keywordStatus"]),
    page("career-plan", "职业计划", "available", "user", "查看长期计划摘要和本周重点。", ["plan", "ensurePlan"]),
    page("interview-home", "面试", "available", "user", "全景仿真面试和 AI 模拟面试入口。", ["interviews", "startInterview"]),
    page("interview", "模拟面试", "available", "user", "查看面试历史，并从岗位目标开始练习。", ["interviews", "startInterview"]),
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
    ["简历 / AI简历制作", "#resume-home", "已接入"],
    ["简历 / AI简历修改", "#resume-diagnosis", "已接入"],
    ["面试 / 全景仿真面试", "#interview-home", "已接入"],
    ["面试 / AI模拟面试", "#interview", "已接入"],
    ["深造", "#further-study-home", "规划中"],
    ["深造 / 考研", "#postgraduate-exam", "规划中"],
    ["深造 / 保研", "#postgraduate-recommendation", "规划中"],
    ["深造 / 留学", "#study-abroad", "规划中"]
  ];
  var featureGroups = {
    "employment-home": [
      feature("AI简历制作", "AI", "上传或创建简历，关联 PDF 并维护简历记录", "resume", "已接入"),
      feature("AI简历修改", "改", "围绕目标岗位诊断简历匹配度和优化建议", "resume-diagnosis", "已接入"),
      feature("全景仿真面试", "仿", "按目标岗位进入真实面试流程练习", "interview", "已接入"),
      feature("AI模拟面试", "AI", "查看面试历史并开始 AI 模拟练习", "interview", "已接入")
    ],
    "resume-home": [
      feature("AI简历制作", "AI", "沿用 IPD 简历创建流程，上传或创建简历并关联 PDF", "resume", "已接入"),
      feature("AI简历修改", "改", "沿用 IPD 简历诊断逻辑，围绕目标岗位给出优化建议", "resume-diagnosis", "已接入")
    ],
    "interview-home": [
      feature("全景仿真面试", "仿", "沿用 IPD 模拟面试流程，按目标岗位进入练习", "interview", "已接入"),
      feature("AI模拟面试", "AI", "沿用 IPD AI 追问和复盘逻辑，查看历史并开始练习", "interview", "已接入")
    ],
    "further-study-home": [
      feature("考研", "研", "记录目标院校、考试时间线和复习策略，后续接入考研规划 Agent", "postgraduate-exam", "规划中"),
      feature("保研", "保", "记录排名、加分项和目标院校，后续接入保研规划 Agent", "postgraduate-recommendation", "规划中"),
      feature("留学", "留", "记录语言、GPA 和背景提升计划，后续接入留学规划 Agent", "study-abroad", "规划中")
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
    fileMessage: null,
    messageTimer: null,
    previewUrls: {},
    plan: null,
    interviews: null,
    route: "workbench"
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
    handleRouteChange();
    loadPlatformIdentity().then(function () {
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
    if (!pageByKey[key]) {
      state.route = "workbench";
      showMessage("warning", "未知页面", "未找到 route: " + key + "，已回到工作台。");
      if (window.location.hash !== "#workbench") {
        window.location.hash = "workbench";
        return;
      }
    } else {
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
    } else if (item.key === "employment-home" || item.key === "resume-home" || item.key === "interview-home" || item.key === "further-study-home") {
      renderFeatureHome(item);
    } else if (item.key === "postgraduate-exam" || item.key === "postgraduate-recommendation" || item.key === "study-abroad") {
      renderPlannedStudyPage(item);
    } else if (item.key === "onboarding") {
      renderOnboarding(item);
    } else if (item.key === "resume") {
      renderResumePage(item);
    } else if (item.key === "today-action") {
      renderTodayPage(item);
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
    var targetRole = firstText(onboarding.targetRole, textFromSnapshot("preferences.targetRole"));
    renderFeatureShell(item, "选择你的路线", "先补充一点基础信息，再选择进入就业或深造方向。",
      '<section class="panel full home-intent-panel">' +
      '<h3>基础信息</h3>' +
      '<form class="form-grid" id="homeIntentForm">' +
      field("homeGoal", "当前路线", "select", firstText(readHomeIntent().goal, "employment"), [["employment", "就业"], ["study", "深造"], ["explore", "先了解一下"]]) +
      field("homeTargetRole", "目标岗位或方向", "text", targetRole) +
      field("homePreference", "想用 CyanCruise 做什么", "text", firstText(readHomeIntent().preference, firstText(onboarding.preference, ""))) +
      '<div class="full actions-row"><button type="submit">保存路线信息</button><button type="button" class="secondary" data-link="employment-home">进入就业</button><button type="button" class="secondary" data-link="further-study-home">进入深造</button></div>' +
      '</form></section>' +
      overviewStrip() +
      '<section class="feature-section"><h3>路线入口</h3><div class="feature-grid">' +
      featureCards([
        feature("就业", "就", "进入 AI 简历制作、AI 简历修改、全景仿真面试和 AI 模拟面试", "employment-home", "已接入"),
        feature("深造", "深", "进入考研、保研、留学规划入口，后续接入规划 Agent", "further-study-home", "规划中")
      ]) + '</div></section>' +
      '<section class="feature-section"><h3>推荐入口</h3><div class="feature-grid">' +
      featureCards([
        feature("AI简历制作", "AI", "上传或创建简历，关联 PDF 并维护记录", "resume", "已接入"),
        feature("AI简历修改", "改", "根据目标岗位诊断简历匹配度", "resume-diagnosis", "已接入"),
        feature("今日行动", "今", "查看下一步建议并继续执行", "today-action", "已接入"),
        feature("AI模拟面试", "AI", "从岗位目标开始面试练习", "interview", "已接入")
      ]) + '</div></section>');
    var form = $("homeIntentForm");
    if (form) {
      form.addEventListener("submit", submitHomeIntent);
    }
  }

  function renderFeatureHome(item) {
    var cards = featureGroups[item.key] || [];
    renderFeatureShell(item, item.title, item.summary,
      '<section class="feature-section"><h3>' + escapeHtml(item.title) + '工具</h3><div class="feature-grid">' +
      featureCards(cards) + '</div></section>');
  }

  function renderPlannedStudyPage(item) {
    renderFeatureShell(item, item.title, item.summary,
      '<section class="panel full">' +
      '<h3>' + escapeHtml(item.title) + '规划</h3>' +
      '<p class="panel-note">这个方向先作为深造路线入口预留。后续会接入主调度 Agent、用户画像 Agent 和对应规划 Agent；当前可以先回到深造页选择方向，或回到首页调整路线信息。</p>' +
      '<div class="actions-row"><button type="button" data-link="further-study-home">返回深造</button><button type="button" class="secondary" data-link="workbench">返回首页</button></div>' +
      '</section>');
  }

  function renderOnboarding(item) {
    var onboarding = getValue(state.snapshot, "onboarding") || {};
    var targetRole = firstText(onboarding.targetRole, textFromSnapshot("preferences.targetRole"));
    renderShell(item,
      '<section class="panel full"><h3>引导信息</h3>' +
      '<form class="form-grid" id="onboardingForm">' +
      field("identityType", "身份类型", "select", firstText(onboarding.identityType, "student"), [["student", "在校学生"], ["graduate", "应届毕业生"], ["career_switcher", "转行求职"]]) +
      field("onboardingTargetRole", "目标岗位", "text", targetRole) +
      field("resumeStatus", "简历状态", "select", firstText(onboarding.resumeStatus, "none"), [["none", "还没有简历"], ["draft", "已有初稿"], ["ready", "已有可投递简历"]]) +
      field("preference", "偏好方向", "text", firstText(onboarding.preference, "")) +
      '<div class="full actions-row"><button type="submit">保存引导信息</button><button type="button" class="secondary" data-link="workbench">返回工作台</button></div>' +
      '</form></section>');
    $("onboardingForm").addEventListener("submit", submitOnboarding);
  }

  function renderTodayPage(item) {
    var target = mapTargetToRoute(firstText(getValue(state.today, "target"), getValue(state.today, "route"), getValue(state.today, "targetPath")));
    renderShell(item,
      metricsPanel("推荐动作", [
        ["标题", firstText(getValue(state.today, "title"), getValue(state.today, "actionTitle"), getValue(state.today, "nextAction"), "补齐求职画像")],
        ["原因", firstText(getValue(state.today, "reason"), getValue(state.today, "summary"), "完成画像、简历和面试信号后会给出更精确建议")],
        ["目标页面", target]
      ]) +
      actionPanel("继续执行", linkButton(target, "进入下一步", "按今日行动跳转到对应页面。"))
    );
  }

  function renderResumePage(item) {
    var target = resumeTargetDefault();
    var panels = [
      resumeListPanel(),
      resumeFormPanel(target),
      resumeFilePanel()
    ];
    if (isDebugMode()) {
      panels.push(metricsPanel("接口契约", [
        ["列表", endpoints.resumes],
        ["创建", endpoints.resumeCreate],
        ["删除", endpoints.resumeDelete],
        ["文件上传", endpoints.fileUpload],
        ["文件下载", endpoints.fileDownload]
      ]));
    }
    if (state.resumeMessage) {
      panels.unshift(statePanel("简历状态", state.resumeMessage.text, state.resumeMessage.type));
    }
    renderShell(item, panels.join(""));
    bindResumeEvents();
  }

  function resumeFormPanel(target) {
    var draft = state.resumeDraft || {};
    var submitLabel = state.resumeSubmitting ? "创建中..." : "创建简历";
    var submitDisabled = state.resumeSubmitting ? " disabled" : "";
    var fileKey = firstText(draft.fileKey, "");
    var fileKeyControl = isDebugMode()
      ? field("resumeFileKey", "文件 key", "text", fileKey)
      : '<input id="resumeFileKey" type="hidden" value="' + escapeAttr(fileKey) + '"><p class="form-note full">文件状态：' + escapeHtml(fileKey ? "已关联 PDF" : "可先上传 PDF，也可只创建简历信息") + '</p>';
    return '<section class="panel"><h3>创建简历记录</h3>' +
      '<form class="form-grid" id="resumeForm">' +
      field("resumeTitle", "简历标题", "text", firstText(draft.title, "后端开发简历")) +
      field("resumeTargetJob", "目标岗位", "text", firstText(draft.targetJob, target)) +
      fileKeyControl +
      '<label class="full">解析内容<textarea id="resumeParsedContent" placeholder="可粘贴简历摘要、技能、项目经历或留空。">' + escapeHtml(draft.parsedContent) + '</textarea></label>' +
      '<div class="full actions-row">' +
      '<button type="submit"' + submitDisabled + ">" + submitLabel + "</button>" +
      '<button type="button" class="secondary" data-link="resume-diagnosis">去简历诊断</button>' +
      '<button type="button" class="secondary" id="refreshResumesButton">刷新列表</button>' +
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
    var preview = state.previewUrls[fileKey];
    return '<article class="item resume-item">' +
      '<div><strong>' + escapeHtml(firstText(item.title, item.resumeName, "简历 " + id)) + '</strong>' +
      '<p>目标岗位：' + escapeHtml(target) + '</p>' +
      '<p>文件：' + escapeHtml(fileKey === "未关联文件" ? "未关联" : "已关联 PDF") + '</p>' +
      (isDebugMode() ? '<p>文件 key：' + escapeHtml(fileKey) + '</p>' : "") +
      '<p>诊断分：' + escapeHtml(score) + ' ｜ 更新时间：' + escapeHtml(updated) + '</p></div>' +
      '<div class="actions-row compact">' +
      (fileKey === "未关联文件" ? "" : '<button type="button" class="secondary" data-preview-file="' + escapeHtml(fileKey) + '">预览</button>') +
      '<button type="button" data-link="resume-diagnosis">去诊断</button>' +
      '<button type="button" class="secondary danger" data-delete-resume="' + escapeHtml(id) + '">删除</button>' +
      '</div>' +
      "</article>";
  }

  function resumeFilePanel() {
    var defaultText = isDebugMode()
      ? "文件上传是可选增强；也可以手工填写 fileKey 后直接创建简历元数据。"
      : "可以先选择 PDF 上传，系统会自动关联到本次简历。";
    var text = state.fileMessage ? state.fileMessage.text : defaultText;
    var type = state.fileMessage ? state.fileMessage.type : "empty";
    return '<section class="panel"><h3>可选文件上传</h3>' +
      '<div class="form-grid">' +
      '<label class="full">选择小文件<input id="resumeFileInput" type="file"></label>' +
      '<div class="full actions-row"><button type="button" class="secondary" id="uploadResumeFileButton">上传并填入 fileKey</button></div>' +
      '</div>' +
      '<p class="panel-note ' + escapeHtml(type) + '">' + escapeHtml(text) + '</p>' +
      '</section>';
  }

  function bindResumeEvents() {
    var form = $("resumeForm");
    if (form) {
      form.addEventListener("submit", submitResume);
    }
    var refresh = $("refreshResumesButton");
    if (refresh) {
      refresh.addEventListener("click", function () {
        refreshResumeList(true);
      });
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
      renderPage(pageByKey[state.route]);
      return;
    }
    var request = readResumeDraft();
    state.resumeDraft = request;
    if (!request.title && !request.targetJob && !request.fileKey && !request.parsedContent) {
      state.resumeMessage = { type: "warning", text: "请至少填写标题、目标岗位、文件 key 或解析内容中的一项。" };
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
    state.resumeMessage = { type: "info", text: "正在创建简历记录。" };
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
      state.fileMessage = { type: "info", text: "文件已上传，object key 已填入表单。" };
      state.resumeDraft = state.resumeDraft || {};
      state.resumeDraft.fileKey = objectKey;
      renderPage(pageByKey[state.route]);
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
    if (!window.confirm("确定删除这条简历记录吗？")) {
      return;
    }
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
    } else if (item.key === "career-plan") {
      body += objectPanel("计划摘要", state.plan, "职业计划摘要待生成。");
    } else if (item.key === "assistant") {
      body += statePanel("助手会话", "可调用发送消息和会话列表契约；真实 AI provider 在后续 change 接入。", "pending");
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

  function renderFeatureShell(item, title, summary, innerHtml) {
    els.pageHost.innerHTML =
      '<header class="feature-page-header">' +
      '<div><p class="eyebrow">CyanCruise</p><h2>' + escapeHtml(title) + '</h2><p class="lead">' + escapeHtml(summary) + '</p></div>' +
      '<button type="button" class="secondary" data-link="workbench">首页</button>' +
      '</header>' +
      '<div class="feature-content">' + innerHtml + '</div>';
    bindPageLinks();
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
      '<button type="button" class="secondary" data-link="workbench">首页</button>' +
      '</header><div class="panel-grid">' + innerHtml + '</div>';
    bindPageLinks();
  }

  function bindPageLinks() {
    var links = els.pageHost.querySelectorAll("[data-link]");
    for (var i = 0; i < links.length; i += 1) {
      links[i].addEventListener("click", function (event) {
        window.location.hash = event.currentTarget.getAttribute("data-link");
      });
    }
  }

  function overviewRows() {
    return [
      ["目标岗位", textFromSnapshot("preferences.targetRole", "onboarding.targetRole", "resume.targetJob") || "待确认"],
      ["今日行动", firstText(getValue(state.today, "title"), getValue(state.today, "actionTitle"), getValue(state.today, "nextAction"), "等待生成")],
      ["简历记录", Array.isArray(state.resumes) ? state.resumes.length + " 份" : "待加载"],
      ["面试练习", Array.isArray(state.interviews) ? state.interviews.length + " 次" : "待加载"]
    ];
  }

  function overviewStrip() {
    return '<section class="overview-strip">' + overviewRows().map(function (row) {
      return '<article><span class="label">' + escapeHtml(row[0]) + '</span><strong>' + escapeHtml(row[1]) + '</strong></article>';
    }).join("") + '</section>';
  }

  function featureCards(cards) {
    return cards.map(function (card) {
      var linked = !!card.route && pageByKey[card.route] && card.status !== "即将接入";
      var status = card.status || (linked ? "已接入" : "即将接入");
      var attrs = linked ? ' data-link="' + escapeHtml(card.route) + '"' : ' disabled aria-disabled="true"';
      var cls = linked ? "feature-card" : "feature-card disabled";
      return '<button type="button" class="' + cls + '"' + attrs + '>' +
        '<span class="feature-icon">' + escapeHtml(card.icon) + '</span>' +
        '<span class="feature-copy"><strong>' + escapeHtml(card.title) + '</strong><small>' + escapeHtml(card.summary) + '</small></span>' +
        '<span class="feature-status">' + escapeHtml(status) + '</span>' +
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
    showMessage("info", "正在加载", "读取画像、今日行动、简历、计划和面试摘要。");
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
      showMessage("info", "已加载", "页面按当前身份刷新完成。");
    }).catch(function (error) {
      renderPreview();
      showMessage("error", "加载失败", error.message || "后端暂不可用，已保留可恢复页面状态。");
    });
  }

  function submitOnboarding(event) {
    event.preventDefault();
    if (!hasUserIdentity()) {
      showMessage("warning", "需要身份", "保存引导信息前需要 Cosmic 身份或显式开发身份。");
      return;
    }
    var request = {
      identityType: valueOf("identityType"),
      targetRole: valueOf("onboardingTargetRole"),
      resumeStatus: valueOf("resumeStatus"),
      preference: valueOf("preference")
    };
    if (isFilePreview()) {
      localStorage.setItem("careerloop.previewProfile", JSON.stringify(request));
      state.snapshot = { onboarding: request, preferences: { targetRole: request.targetRole } };
      updateOverviewCards();
      showMessage("info", "已保存到本地预览", "file:// 模式不调用后端。");
      return;
    }
    post(endpoints.onboarding, { userId: state.identity.userId, request: request }).then(function (snapshot) {
      state.snapshot = snapshot;
      updateOverviewCards();
      renderPage(pageByKey[state.route]);
      showMessage("info", "已保存", "引导信息已写入职业画像快照。");
    }).catch(function (error) {
      showMessage("error", "保存失败", error.message || "onboarding WebAPI 暂不可用。");
    });
  }

  function readHomeIntent() {
    return parseStorageJson(localStorage, "careerloop.homeIntent") || {};
  }

  function submitHomeIntent(event) {
    event.preventDefault();
    var intent = {
      goal: valueOf("homeGoal"),
      targetRole: valueOf("homeTargetRole"),
      preference: valueOf("homePreference")
    };
    localStorage.setItem("careerloop.homeIntent", JSON.stringify(intent));
    var request = {
      identityType: "student",
      targetRole: intent.targetRole,
      resumeStatus: Array.isArray(state.resumes) && state.resumes.length > 0 ? "draft" : "none",
      preference: ["路线：" + labelForGoal(intent.goal), intent.preference].filter(Boolean).join("；")
    };
    state.snapshot = state.snapshot || {};
    state.snapshot.onboarding = request;
    state.snapshot.preferences = state.snapshot.preferences || {};
    state.snapshot.preferences.targetRole = intent.targetRole;
    updateOverviewCards();
    if (!hasUserIdentity() || isFilePreview()) {
      localStorage.setItem("careerloop.previewProfile", JSON.stringify(request));
      showMessage("info", "已保存", "路线信息已保存到当前浏览器。");
      return;
    }
    post(endpoints.onboarding, { userId: state.identity.userId, request: request }).then(function (snapshot) {
      state.snapshot = snapshot;
      updateOverviewCards();
      renderPage(pageByKey[state.route]);
      showMessage("info", "已保存", "路线信息已写入职业画像。");
    }).catch(function () {
      showMessage("warning", "已本地保存", "平台暂未写入成功，但路线信息已保存在当前浏览器。");
    });
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
      title: "补齐画像后生成今日行动",
      summary: "部署到苍穹后可调用 " + endpoints.today,
      target: "onboarding"
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
    els.profileStatus.textContent = target ? "方向已建立" : "需要完成 onboarding";
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
      localStorage.removeItem("careerloop.userId");
      state.identity = resolveIdentity();
      updateIdentityState();
      renderPage(pageByKey[state.route]);
      showMessage("warning", "需要 userId", "请输入开发验证 userId 后再加载。");
      return;
    }
    localStorage.setItem("careerloop.userId", userId);
    state.identity = {
      mode: "development",
      userId: userId,
      adminId: trim(localStorage.getItem("careerloop.adminId")),
      roles: parseRoles(localStorage.getItem("careerloop.roles")),
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
      localStorage.setItem("careerloop.userId", fromQuery);
      return {
        mode: "development",
        userId: fromQuery,
        adminId: trim(params.get("adminId")),
        roles: parseRoles(params.get("roles")),
        source: "query:userId"
      };
    }
    var stored = trim(localStorage.getItem("careerloop.userId"));
    return {
      mode: "development",
      userId: stored,
      adminId: trim(localStorage.getItem("careerloop.adminId")),
      roles: parseRoles(localStorage.getItem("careerloop.roles")),
      source: stored ? "localStorage:careerloop.userId" : "development-missing-userId"
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
        source: firstText(identity.source, "cc001-identity-current")
      };
      updateIdentityState();
      renderPage(pageByKey[state.route]);
    }).catch(function (error) {
      showMessage("warning", "平台身份调用失败", error && error.message ? error.message : "identity request failed");
    });
  }

  function post(path, body) {
    var request = resolveApiRequest(path, body);
    return fetch(request.url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
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
    var appId = firstText(readQueryOrStorage("appId", "careerloop.kapi.appId"), "cc001");
    var serviceName = firstText(readQueryOrStorage("serviceName", "careerloop.kapi.serviceName"), "careerloop");
    var accessToken = readQueryOrStorage("access_token", "careerloop.kapi.accessToken");
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
    var cloudId = firstText(readQueryOrStorage("cloudId", "careerloop.kapi.cloudId"), "v620");
    var appNumber = firstText(readQueryOrStorage("appNumber", "careerloop.kapi.appNumber"), "v620_cc001");
    var apiCode = firstText(readQueryOrStorage("apiCode", "careerloop.kapi.apiCode"), "cc001/careerloop/route");
    var accessToken = readQueryOrStorage("access_token", "careerloop.kapi.accessToken");
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
    return firstText(readQueryOrStorage("kapiRouteVersion", "careerloop.kapi.routeVersion"), "v2").toLowerCase();
  }

  function encodeApiCode(apiCode) {
    return trim(apiCode).split("/").filter(Boolean).map(encodeURIComponent).join("/");
  }

  function resolveApiMode() {
    return firstText(readQueryOrStorage("apiMode", "careerloop.apiMode"), "direct").toLowerCase();
  }

  function readQueryOrStorage(queryKey, storageKey) {
    var params = new URLSearchParams(window.location.search);
    var fromQuery = trim(params.get(queryKey));
    if (fromQuery) {
      localStorage.setItem(storageKey, fromQuery);
      return fromQuery;
    }
    return trim(localStorage.getItem(storageKey));
  }

  function resolveApiBase() {
    var params = new URLSearchParams(window.location.search);
    var fromQuery = trim(params.get("apiBase"));
    if (fromQuery) {
      localStorage.setItem("careerloop.apiBase", fromQuery);
      return fromQuery.replace(/\/$/, "");
    }
    var stored = trim(localStorage.getItem("careerloop.apiBase"));
    if (stored) {
      return stored.replace(/\/$/, "");
    }
    return defaultApiBase();
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
      "pages/agent/index": "today-action",
      "pages/assistant/index": "assistant",
      "pages/resume/index": "resume",
      "pages/resume-ai/index": "resume-diagnosis",
      "pages/assessment/index": "assessment",
      "pages/interview/index": "interview"
    };
    return map[normalized] || normalized || "onboarding";
  }

  function readPreviewProfile() {
    try {
      var raw = localStorage.getItem("careerloop.previewProfile");
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
    return trim(hash || window.location.hash || "").replace(/^#/, "") || "workbench";
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
    if (assessment && (assessment.latestRecordId || assessment.resultCode || assessment.mbtiType)) {
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
