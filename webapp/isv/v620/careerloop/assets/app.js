(function () {
  "use strict";

  var routeMap = [
    {
      key: "assessment",
      title: "职业测评",
      icon: "测",
      summary: "完成方向测评，把兴趣和能力变成画像信号。",
      state: "入口可用",
      route: "#assessment"
    },
    {
      key: "resume",
      title: "简历",
      icon: "历",
      summary: "维护最近简历记录，让今日行动知道你的准备状态。",
      state: "入口可用",
      route: "#resume"
    },
    {
      key: "resume-diagnosis",
      title: "简历诊断",
      icon: "诊",
      summary: "围绕目标岗位检查匹配度、关键词和优化建议。",
      state: "入口可用",
      route: "#resume-diagnosis"
    },
    {
      key: "interview",
      title: "模拟面试",
      icon: "面",
      summary: "开始一场岗位练习，保存会话和报告摘要。",
      state: "入口可用",
      route: "#interview"
    },
    {
      key: "career-plan",
      title: "职业计划",
      icon: "计",
      summary: "查看本周重点和长期计划健康度。",
      state: "入口可用",
      route: "#career-plan"
    },
    {
      key: "assistant",
      title: "求职助手",
      icon: "助",
      summary: "用多角色助手处理简历、面试和计划问题。",
      state: "入口可用",
      route: "#assistant"
    },
    {
      key: "employment-insight",
      title: "就业洞察",
      icon: "业",
      summary: "按学校、专业和目标岗位查看可追溯就业来源、趋势和覆盖状态。",
      state: "入口可用",
      route: "#employment-insight"
    },
    {
      key: "career-resources",
      title: "资源入口",
      icon: "源",
      summary: "查看文章、视频、咨询提示和职业路径资源卡片。",
      state: "入口可用",
      route: "#career-resources"
    },
    {
      key: "messages",
      title: "消息提醒",
      icon: "信",
      summary: "任务通知和订阅消息等待后续迁移。",
      state: "后续迁移",
      route: "#pending",
      pending: true
    },
    {
      key: "voice-interview",
      title: "语音面试",
      icon: "声",
      summary: "ASR/TTS、数字人和身体语言能力后续平台适配。",
      state: "后续迁移",
      route: "#pending",
      pending: true
    }
  ];

  var endpoints = {
    snapshot: "/cc001/career-profile/snapshot/get",
    onboarding: "/cc001/career-profile/onboarding/save",
    today: "/cc001/career-agent/today/get",
    resumes: "/cc001/resume/list",
    plan: "/cc001/career-plan/summary",
    interviews: "/cc001/interview/list",
    sessions: "/cc001/assistant-chat/session/list",
    employmentInsight: "/cc001/career-employment/insight/get",
    careerResources: "/cc001/career-employment/resources/list"
  };

  var els = {};

  function $(id) {
    return document.getElementById(id);
  }

  function init() {
    cacheElements();
    renderActions();
    var userId = resolveUserId();
    if (userId) {
      els.userIdInput.value = userId;
      loadWorkbench(userId);
    } else {
      showMessage("warning", "需要用户 ID", "请输入 userId 后再加载工作台。");
      updateIdentityState(null);
    }
    els.saveUserIdButton.addEventListener("click", onSaveUserId);
    els.userIdInput.addEventListener("keydown", function (event) {
      if (event.key === "Enter") {
        event.preventDefault();
        onSaveUserId();
      }
    });
    els.onboardingForm.addEventListener("submit", onSaveOnboarding);
    els.todayButton.addEventListener("click", function () {
      scrollToRoute(els.todayButton.getAttribute("data-route"));
    });
  }

  function cacheElements() {
    els.userIdInput = $("userIdInput");
    els.saveUserIdButton = $("saveUserIdButton");
    els.identityHint = $("identityHint");
    els.messagePanel = $("messagePanel");
    els.targetRole = $("targetRole");
    els.profileStatus = $("profileStatus");
    els.readinessScore = $("readinessScore");
    els.readinessHint = $("readinessHint");
    els.resumeState = $("resumeState");
    els.resumeHint = $("resumeHint");
    els.interviewState = $("interviewState");
    els.interviewHint = $("interviewHint");
    els.todayTitle = $("todayTitle");
    els.todaySummary = $("todaySummary");
    els.todayButton = $("todayButton");
    els.actionGrid = $("actionGrid");
    els.onboardingForm = $("onboardingForm");
    els.identityType = $("identityType");
    els.targetRoleInput = $("targetRoleInput");
    els.resumeStatusInput = $("resumeStatusInput");
    els.preferenceInput = $("preferenceInput");
  }

  function resolveUserId() {
    var params = new URLSearchParams(window.location.search);
    var fromQuery = trim(params.get("userId"));
    if (fromQuery) {
      localStorage.setItem("careerloop.userId", fromQuery);
      return fromQuery;
    }
    return trim(localStorage.getItem("careerloop.userId"));
  }

  function resolveApiBase() {
    var params = new URLSearchParams(window.location.search);
    var fromQuery = trim(params.get("apiBase"));
    if (fromQuery) {
      localStorage.setItem("careerloop.apiBase", fromQuery);
      return fromQuery.replace(/\/$/, "");
    }
    var stored = trim(localStorage.getItem("careerloop.apiBase"));
    return stored ? stored.replace(/\/$/, "") : "";
  }

  function onSaveUserId() {
    var userId = trim(els.userIdInput.value);
    if (!userId) {
      localStorage.removeItem("careerloop.userId");
      updateIdentityState(null);
      showMessage("warning", "需要用户 ID", "请输入 userId 后再加载工作台。");
      return;
    }
    localStorage.setItem("careerloop.userId", userId);
    loadWorkbench(userId);
  }

  function loadWorkbench(userId) {
    updateIdentityState(userId);
    if (window.location.protocol === "file:") {
      showMessage("info", "契约预览", "本地文件模式不会调用后端；部署到苍穹 webapp 或通过 Web 服务打开后可请求 WebAPI。");
      renderOfflinePreview(userId);
      return;
    }

    showMessage("info", "正在加载", "正在读取画像、今日行动和主循环状态。");
    Promise.all([
      post(endpoints.snapshot, userId),
      post(endpoints.today, userId),
      post(endpoints.resumes, userId).catch(asUnavailable),
      post(endpoints.plan, userId).catch(asUnavailable),
      post(endpoints.interviews, userId).catch(asUnavailable)
    ]).then(function (results) {
      renderProfile(results[0]);
      renderToday(results[1]);
      renderResume(results[2]);
      renderPlan(results[3]);
      renderInterview(results[4]);
      showMessage("info", "已加载", "工作台已按当前用户状态刷新。");
    }).catch(function (error) {
      renderOfflinePreview(userId);
      showMessage("error", "加载失败", error.message || "后端暂不可用，页面已保留可恢复入口。");
    });
  }

  function post(path, body) {
    return fetch(resolveApiBase() + path, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(body)
    }).then(function (response) {
      if (!response.ok) {
        throw new Error(path + " 返回 " + response.status);
      }
      return response.json();
    });
  }

  function onSaveOnboarding(event) {
    event.preventDefault();
    var userId = trim(els.userIdInput.value);
    if (!userId) {
      showMessage("warning", "需要用户 ID", "保存引导信息前请输入 userId。");
      return;
    }
    var request = {
      identityType: els.identityType.value,
      targetRole: trim(els.targetRoleInput.value),
      resumeStatus: els.resumeStatusInput.value,
      preference: trim(els.preferenceInput.value)
    };
    if (window.location.protocol === "file:") {
      localStorage.setItem("careerloop.previewProfile", JSON.stringify(request));
      renderProfile({ onboarding: request, preferences: { targetRole: request.targetRole } });
      showMessage("info", "已保存到本地预览", "本地文件模式不会调用苍穹 WebAPI。");
      return;
    }
    showMessage("info", "正在保存", "正在提交 onboarding 信息。");
    post(endpoints.onboarding, { userId: userId, request: request }).then(function (snapshot) {
      renderProfile(snapshot);
      showMessage("info", "已保存", "引导信息已写入职业画像快照。");
    }).catch(function (error) {
      showMessage("error", "保存失败", error.message || "onboarding WebAPI 暂不可用。");
    });
  }

  function renderActions() {
    els.actionGrid.innerHTML = "";
    routeMap.forEach(function (item) {
      var button = document.createElement("button");
      button.type = "button";
      button.className = "action-card" + (item.pending ? " pending" : "");
      button.setAttribute("data-route", item.route);
      button.innerHTML =
        '<span class="action-icon">' + escapeHtml(item.icon) + "</span>" +
        "<span><h3>" + escapeHtml(item.title) + "</h3>" +
        "<p>" + escapeHtml(item.summary) + "</p>" +
        "<small>" + escapeHtml(item.state) + "</small></span>";
      button.addEventListener("click", function () {
        if (item.pending) {
          showMessage("warning", item.title + "待迁移", item.summary);
          return;
        }
        scrollToRoute(item.key);
      });
      els.actionGrid.appendChild(button);
    });
  }

  function renderOfflinePreview(userId) {
    var preview = readPreviewProfile();
    renderProfile(preview || {});
    renderToday({
      title: "补齐画像后生成今日行动",
      summary: userId ? "当前为静态预览，可部署到苍穹后调用 /cc001/career-agent/today/get。" : "请输入 userId 后进入预览。",
      target: "onboarding"
    });
    renderResume(null);
    renderPlan(null);
    renderInterview(null);
  }

  function renderProfile(snapshot) {
    var onboarding = snapshot && snapshot.onboarding ? snapshot.onboarding : {};
    var preferences = snapshot && snapshot.preferences ? snapshot.preferences : {};
    var resume = snapshot && snapshot.resume ? snapshot.resume : {};
    var assessment = snapshot && snapshot.assessment ? snapshot.assessment : {};
    var target = firstText(preferences.targetRole, onboarding.targetRole, resume.targetJob);
    els.targetRole.textContent = target || "待确认";
    els.profileStatus.textContent = target ? "方向已建立" : "需要完成 onboarding";
    els.targetRoleInput.value = target || els.targetRoleInput.value;
    if (onboarding.identityType) {
      els.identityType.value = onboarding.identityType;
    }
    if (onboarding.resumeStatus) {
      els.resumeStatusInput.value = onboarding.resumeStatus;
    }
    var score = calculateReadiness(target, resume, assessment);
    els.readinessScore.textContent = score + "%";
    els.readinessHint.textContent = score >= 80 ? "可以进入专项练习" : "继续补齐测评、简历和面试信号";
    els.resumeState.textContent = resume.lastResumeId || resume.targetJob ? "已有记录" : "待补充";
    els.resumeHint.textContent = resume.diagnosisScore ? "诊断分 " + resume.diagnosisScore : "建议维护最近简历";
  }

  function renderToday(today) {
    var title = firstText(today && today.title, today && today.actionTitle, today && today.nextAction, "先建立你的求职画像");
    var summary = firstText(today && today.summary, today && today.reason, today && today.description, "补齐身份、目标岗位和简历状态后，系统会给出下一步建议。");
    var route = mapTargetToRoute(firstText(today && today.target, today && today.route, today && today.targetPath));
    els.todayTitle.textContent = title;
    els.todaySummary.textContent = summary;
    els.todayButton.textContent = route === "onboarding" ? "开始补齐" : "继续行动";
    els.todayButton.setAttribute("data-route", route);
  }

  function renderResume(resumes) {
    if (Array.isArray(resumes)) {
      els.resumeState.textContent = resumes.length ? "已有 " + resumes.length + " 份" : "暂无简历";
      els.resumeHint.textContent = resumes.length ? "最近记录已接入" : "从简历入口创建记录";
    }
  }

  function renderPlan(plan) {
    if (plan && !plan.unavailable) {
      els.readinessHint.textContent = firstText(plan.weekFocus, plan.summary, els.readinessHint.textContent);
    }
  }

  function renderInterview(interviews) {
    if (Array.isArray(interviews)) {
      els.interviewState.textContent = interviews.length ? "已有 " + interviews.length + " 次" : "待练习";
      els.interviewHint.textContent = interviews.length ? "查看最近报告摘要" : "从模拟面试开始练习";
    } else {
      els.interviewState.textContent = "待练习";
      els.interviewHint.textContent = "完成简历后进入练习";
    }
  }

  function updateIdentityState(userId) {
    els.identityHint.textContent = userId ? "当前验证用户：" + userId : "生产登录态等待苍穹平台接入；当前使用显式 userId 验证。";
  }

  function showMessage(type, title, text) {
    els.messagePanel.className = "message-panel " + type;
    els.messagePanel.innerHTML = "<strong>" + escapeHtml(title) + "</strong><span>" + escapeHtml(text) + "</span>";
  }

  function scrollToRoute(route) {
    var id = route || "workbench";
    var node = id === "onboarding" ? $("onboarding") : $("today-action");
    if (id === "assessment" || id === "resume" || id === "resume-diagnosis" || id === "interview" || id === "career-plan" || id === "assistant" || id === "employment-insight" || id === "career-resources") {
      showMessage("info", "入口已就绪", "请在苍穹挂载后对接 " + id + " 对应页面状态和 WebAPI。");
    }
    if (node && node.scrollIntoView) {
      node.scrollIntoView({ behavior: "smooth", block: "start" });
    }
  }

  function mapTargetToRoute(target) {
    var safe = trim(target);
    if (!safe) {
      return "onboarding";
    }
    var normalized = safe.replace(/^#/, "").replace(/^\//, "");
    var map = {
      "pages/agent/index": "today-action",
      "pages/assistant/index": "assistant",
      "pages/resume/index": "resume",
      "pages/resume-ai/index": "resume-diagnosis",
      "pages/assessment/index": "assessment",
      "pages/interview/index": "interview",
      "career-plan": "career-plan"
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

  function asUnavailable(error) {
    return { unavailable: true, error: error };
  }

  document.addEventListener("DOMContentLoaded", init);
}());
