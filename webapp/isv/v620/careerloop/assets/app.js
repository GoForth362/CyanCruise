(function () {
  "use strict";

  var endpoints = {
    snapshot: "/cc001/career-profile/snapshot/get",
    onboarding: "/cc001/career-profile/onboarding/save",
    today: "/cc001/career-agent/today/get",
    assessmentSubmit: "/cc001/assessment/submit",
    resumes: "/cc001/resume/list",
    resumeCreate: "/cc001/resume/create",
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
    page("workbench", "工作台", "available", "user", "从目标岗位、画像和今日行动进入主循环。", ["snapshot", "today", "resumes", "plan", "interviews"]),
    page("onboarding", "新用户引导", "available", "user", "收集身份、目标岗位、简历状态和偏好信号。", ["onboarding"]),
    page("today-action", "今日行动", "available", "user", "展示下一步建议，并跳转到对应页面。", ["today"]),
    page("assessment", "职业测评", "entry-only", "user", "测评提交契约已就绪，完整题组页面后续细化。", ["assessmentSubmit"]),
    page("resume", "简历", "available", "user", "查看简历记录，创建元数据，并关联文件能力。", ["resumes", "resumeCreate"]),
    page("file-upload-preview", "文件上传预览", "entry-only", "user", "展示上传、预览、下载、删除和文本抽取契约。", ["fileUpload", "filePreview", "fileDownload", "fileDelete", "fileExtractText"]),
    page("resume-diagnosis", "简历诊断", "available", "user", "围绕目标岗位分析匹配度、关键词和建议。", ["resumeDiagnosis", "keywordStatus"]),
    page("career-plan", "职业计划", "available", "user", "查看长期计划摘要和本周重点。", ["plan", "ensurePlan"]),
    page("interview", "模拟面试", "available", "user", "查看面试历史，并从岗位目标开始练习。", ["interviews", "startInterview"]),
    page("assistant", "求职助手", "available", "user", "发送助手问题并查看会话历史入口。", ["assistantSend", "assistantSessions"]),
    page("messages", "消息中心", "available", "user", "查看站内通知、未读数、订阅配额和周报入口。", ["notifications", "notificationUnread", "notificationRead", "subscriptionQuota", "weeklyReport"]),
    page("employment-insight", "就业洞察", "available", "user", "按学校、专业和目标岗位查看就业洞察。", ["employmentInsight"]),
    page("career-resources", "职业资源", "available", "public", "查看文章、视频、咨询和职业路径资源。", ["careerResources"]),
    page("admin-console", "管理后台", "entry-only", "admin", "管理员治理入口，仅对 ADMIN 或平台管理员开放。", ["adminWhoami", "adminDashboard", "adminUsersBan", "adminQuestions", "adminContent", "adminBroadcast", "adminAuditLog"])
  ];

  var pageByKey = {};
  var els = {};
  var state = {
    identity: null,
    snapshot: null,
    today: null,
    resumes: null,
    plan: null,
    interviews: null,
    route: "workbench"
  };

  function page(key, title, status, audience, summary, endpointKeys) {
    return {
      key: key,
      title: title,
      status: status,
      audience: audience,
      summary: summary,
      endpoints: endpointKeys || []
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
    loadOverview();
    loadPlatformIdentity();
  }

  function cacheElements() {
    els.routeNav = $("routeNav");
    els.pageHost = $("pageHost");
    els.messagePanel = $("messagePanel");
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
    els.routeNav.innerHTML = pages.map(function (item) {
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
    } else if (item.key === "onboarding") {
      renderOnboarding(item);
    } else if (item.key === "today-action") {
      renderTodayPage(item);
    } else {
      renderContractPage(item);
    }
  }

  function renderWorkbench(item) {
    var panels = [
      metricsPanel("主循环状态", [
        ["目标岗位", textFromSnapshot("preferences.targetRole", "onboarding.targetRole", "resume.targetJob") || "待确认"],
        ["今日行动", firstText(getValue(state.today, "title"), getValue(state.today, "actionTitle"), getValue(state.today, "nextAction"), "等待生成")],
        ["简历记录", Array.isArray(state.resumes) ? state.resumes.length + " 份" : "待加载"],
        ["面试练习", Array.isArray(state.interviews) ? state.interviews.length + " 次" : "待加载"]
      ]),
      actionPanel("下一步入口", pages.filter(function (pageItem) {
        return pageItem.audience !== "admin" && pageItem.key !== "workbench";
      }).map(function (pageItem) {
        return linkButton(pageItem.key, pageItem.title, pageItem.summary);
      }).join(""))
    ];
    renderShell(item, panels.join(""));
  }

  function renderOnboarding(item) {
    var onboarding = getValue(state.snapshot, "onboarding") || {};
    var targetRole = firstText(onboarding.targetRole, textFromSnapshot("preferences.targetRole"));
    renderShell(item,
      '<section class="panel full"><h3>引导信息</h3>' +
      '<form class="form-grid" id="onboardingForm">' +
      field("identityType", "身份类型", "select", firstText(onboarding.identityType, "student"), [["student", "在校学生"], ["graduate", "应届毕业生"], ["career_switcher", "转行求职"]]) +
      field("targetRole", "目标岗位", "text", targetRole) +
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

  function renderContractPage(item) {
    var endpointRows = item.endpoints.map(function (name) {
      return ["WebAPI", endpoints[name] || name];
    });
    var body = metricsPanel("接口契约", endpointRows.concat([
      ["页面状态", item.status],
      ["身份要求", item.audience === "public" ? "不要求 userId" : item.audience === "admin" ? "ADMIN" : "Cosmic userId"],
      ["降级策略", fallbackText(item)]
    ]));

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
      body += statePanel("文件服务", "上传、预览、下载、删除和文本抽取走 Cosmic 文件 adapter；disabled 时显示 unavailable。", "pending");
    } else if (item.key === "admin-console") {
      body += statePanel("管理员边界", "仅 ADMIN 或平台管理员身份可访问，不使用硬编码 adminId。", "warning");
    } else if (item.status === "entry-only") {
      body += statePanel("入口状态", "当前展示契约入口和安全降级，完整交互由后续页面细化。", "warning");
    }
    renderShell(item, body);
  }

  function renderShell(item, innerHtml) {
    var chips = [
      '<span class="chip">' + escapeHtml(item.status) + "</span>",
      '<span class="chip">' + escapeHtml(item.audience) + "</span>"
    ];
    if (item.status === "entry-only") {
      chips.push('<span class="chip warning">entry-only</span>');
    }
    els.pageHost.innerHTML =
      '<header class="page-header">' +
      '<div><p class="eyebrow">Route: ' + escapeHtml(item.key) + '</p><h2>' + escapeHtml(item.title) + '</h2>' +
      '<p class="lead">' + escapeHtml(item.summary) + '</p><div class="route-meta">' + chips.join("") + '</div></div>' +
      '<button type="button" class="secondary" data-link="workbench">工作台</button>' +
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
      targetRole: valueOf("targetRole"),
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
      return;
    }
    post(endpoints.identityCurrent, {}).then(function (identity) {
      var userId = firstText(identity.userId, identity.adminId);
      if (!userId || identity.status !== "OK") {
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
      loadOverview();
    }).catch(function () {
      // Older packages may not expose /cc001/identity/current yet; keep the guarded state.
    });
  }

  function post(path, body) {
    return fetch(resolveApiBase() + path, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body)
    }).then(function (response) {
      if (!response.ok) {
        throw new Error(path + " 返回 " + response.status);
      }
      return response.json();
    });
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

  function showMessage(type, title, text) {
    els.messagePanel.className = "message-panel " + type;
    els.messagePanel.innerHTML = "<strong>" + escapeHtml(title) + "</strong><span>" + escapeHtml(text) + "</span>";
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
