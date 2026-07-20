(function (window, document) {
  "use strict";

  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};
  var CONTENT_PAGE_SIZE = 5;
  var QUESTION_PAGE_SIZE = 10;
  var selectedContentType = "RESOURCE";
  var selectedContentScope = "EMPLOYMENT";
  var contentPageByType = { RESOURCE: 1, ARTICLE: 1, VIDEO: 1 };
  var currentContentItems = [];
  var currentStudyContentItems = [];
  var showUnpublishedContentOnly = false;
  var showUnpublishedInterviewQuestionsOnly = false;
  var showUnpublishedAssessmentQuestionsOnly = false;
  var interviewQuestionPage = 1;
  var assessmentQuestionPageByScale = {};
  var currentInterviewQuestions = [];
  var currentAssessmentBanks = [];

  var NAV_ITEMS = [
    ["overview", "管理总览", "01"],
    ["users", "用户管理", "02"],
    ["content", "内容管理", "03"],
    ["questions", "题库管理", "04"],
    ["broadcast", "通知公告", "05"],
    ["audit", "审计日志", "06"]
  ];

  var CONTENT_GROUPS = [
    { type: "RESOURCE", category: "公共服务", label: "公共服务" },
    { type: "ARTICLE", category: "精选文章", label: "精选文章" },
    { type: "VIDEO", category: "相关视频", label: "相关视频" }
  ];

  registerPage("admin-console", ["admin-console"], "管理后台");

  attachRenderer("admin-console", function (item, context) {
    var adminService = window.CYANCRUISE_SERVICES && window.CYANCRUISE_SERVICES.admin;
    var identity = context.identity || {};
    var canAdmin = context.hasAdminRole && context.hasAdminRole(identity);
    setAdminMode(true);

    if (!identity.userId && !identity.adminId) {
      renderStandaloneState(context, "需要身份", "请先通过金蝶登录进入应用。");
      return;
    }
    if (!canAdmin) {
      renderUnauthorized(context);
      return;
    }
    if (!adminService || typeof adminService.load !== "function") {
      renderStandaloneState(context, "管理服务未加载", "前端管理服务暂不可用，请刷新页面后重试。");
      return;
    }

    renderStandaloneState(context, "正在加载管理后台", "正在读取用户、内容、题库和操作记录。");
    adminService.load(adminContext(context)).then(function (data) {
      if (!data.authorized) {
        renderUnauthorized(context);
        return;
      }
      renderAdminApp(item, context, data);
      bindActions(item, context);
    }).catch(function (error) {
      renderStandaloneState(context, "管理数据暂时无法加载", messageOf(error), true);
    });
  });

  function setAdminMode(on) {
    if (document.body) {
      document.body.classList.toggle("admin-mode", !!on);
    }
  }

  function renderUnauthorized(context) {
    if (context.hideMessage) {
      context.hideMessage();
    }
    if (!context.pageHost) {
      return;
    }
    context.pageHost.innerHTML =
      '<section class="admin-access-denied" aria-labelledby="adminAccessDeniedTitle">' +
      '<div class="admin-access-denied-content">' +
      '<h2 id="adminAccessDeniedTitle">管理后台</h2>' +
      '<p>管理员治理入口，仅对 ADMIN 或平台管理员开放。</p>' +
      '</div></section>';
  }

  function renderStandaloneState(context, title, text, retryable) {
    if (context.pageHost) {
      context.pageHost.innerHTML = '<section class="admin-app admin-state-only">' +
        '<main class="admin-main"><section class="admin-panel admin-state-panel">' +
        '<h2>' + esc(context, title) + '</h2><p>' + esc(context, text) + '</p>' +
        (retryable ? '<div class="admin-state-actions"><button type="button" class="admin-retry-load">重新加载</button></div>' : '') +
        '</section></main></section>';
      if (retryable) {
        var retryButton = context.pageHost.querySelector(".admin-retry-load");
        if (retryButton) {
          retryButton.addEventListener("click", function () {
            window.location.reload();
          });
        }
      }
      return;
    }
    context.renderShell({ title: "管理后台", summary: "" }, context.statePanel(title, text, "warning"));
  }

  function adminContext(context) {
    return {
      endpoints: context.endpoints,
      identity: context.identity,
      post: context.post,
      pageSize: 20,
      auditPageSize: 10
    };
  }

  function renderAdminApp(item, context, data) {
    context.pageHost.innerHTML = '<section class="admin-app">' +
      renderSidebar(context) +
      '<main class="admin-main">' +
      renderTopbar(context, data) +
      '<section class="admin-workspace">' +
      section("overview", true, renderOverview(data, context)) +
      section("users", false, renderUsers(data, context)) +
      section("content", false, renderContent(data, context)) +
      section("questions", false, renderQuestions(data, context)) +
      section("broadcast", false, renderBroadcast(data, context)) +
      section("audit", false, renderAudit(data, context)) +
      '</section></main></section>';
  }

  function renderSidebar(context) {
    return '<aside class="admin-sidebar" aria-label="管理后台导航">' +
      '<div class="admin-brand"><strong>青途启航</strong><span>数智管理后台</span></div>' +
      '<nav class="admin-side-nav" role="tablist">' +
      NAV_ITEMS.map(function (item, index) {
        return '<button type="button" class="admin-nav-item' + (index === 0 ? " active" : "") +
          '" data-tab="' + item[0] + '"><span class="admin-nav-mark" aria-hidden="true">' +
          esc(context, item[2]) + '</span><span>' + esc(context, item[1]) + '</span></button>';
      }).join("") +
      '</nav></aside>';
  }

  function renderTopbar(context, data) {
    var name = first(data.whoami && data.whoami.userName, data.whoami && data.whoami.displayName, data.adminId, "admin");
    return '<header class="admin-topbar">' +
      '<div><h2 id="adminSectionTitle">管理总览</h2><p id="adminSectionSub">掌握平台状态并处理需要关注的事项</p></div>' +
      '<div class="admin-account"><span class="admin-account-avatar" aria-hidden="true">管</span>' +
      '<span class="admin-account-copy"><strong>平台管理员</strong><small>' + esc(context, name) + '</small></span></div>' +
      '</header>';
  }

  function renderOverview(data, context) {
    var dashboard = data.dashboard || {};
    var analytics = data.analytics || {};
    var users = data.users && data.users.items || [];
    var content = data.content || [];
    var questions = data.questions || [];
    var banks = data.assessmentQuestionBanks || [];
    var logs = data.auditLogs && data.auditLogs.items || [];
    var bannedUsers = countWhere(users, function (user) { return String(user.status || "") === "BANNED"; });
    var visibleContent = countWhere(content, function (item) { return !item.hidden; });
    var hiddenContent = countWhere(content, function (item) { return !!item.hidden; });
    var pinnedContent = countWhere(content, function (item) { return !!item.pinned; });
    var publishedQuestions = countWhere(questions, function (question) {
      return question.reviewStatus === "PUBLISHED" || question.status === "APPROVED";
    });
    var hiddenQuestions = countWhere(questions, function (question) {
      return question.reviewStatus === "REJECTED" || question.status === "HIDDEN";
    });
    var assessmentQuestionCount = banks.reduce(function (total, bank) {
      return total + Number(first(bank.poolQuestionCount, bank.questions && bank.questions.length, 0));
    }, 0);
    var alerts = overviewAlerts(content, questions, banks);
    var totalUsers = first(analytics.totalUsers, users.length, 0);
    var totalInterviews = first(analytics.totalInterviews, dashboard.interviewCount, 0);
    var summaryTitle = alerts.length ? "有 " + alerts.length + " 项内容需要关注" : "平台治理状态平稳";
    var summaryText = "当前管理 " + totalUsers + " 位用户、" + content.length + " 条内容，累计完成 " +
      totalInterviews + " 次模拟面试。";
    return '<section class="admin-dashboard-hero">' +
      '<div class="admin-dashboard-hero-copy"><span class="admin-dashboard-eyebrow">平台治理概览</span>' +
      '<h3>' + esc(context, summaryTitle) + '</h3><p>' + esc(context, summaryText) + '</p>' +
      '<div class="admin-dashboard-signal ' + (alerts.length ? "warn" : "ok") + '"><i></i><span>' +
      (alerts.length ? "建议优先检查右侧待关注事项" : "当前没有从已加载数据中发现明显异常") +
      '</span></div></div>' +
      '<div class="admin-dashboard-quick"><span>常用管理</span><div>' +
      quickEntry(context, "users", "用户", "账号状态") +
      quickEntry(context, "content", "内容", "展示与置顶") +
      quickEntry(context, "questions", "题库", "发布与维护") +
      quickEntry(context, "broadcast", "公告", "站内通知") +
      '</div></div></section>' +
      '<div class="admin-dashboard-metrics">' +
      dashboardMetric(context, "01", "用户数量", totalUsers, bannedUsers ? bannedUsers + " 个用户端已禁用" : "全部用户端正常", bannedUsers ? "warn" : "ok") +
      dashboardMetric(context, "02", "面试次数", totalInterviews, "模拟面试累计", "") +
      dashboardMetric(context, "03", "测评次数", first(analytics.totalAssessments, 0), "能力测评累计", "") +
      dashboardMetric(context, "04", "内容数量", content.length, visibleContent + " 条展示中，" + hiddenContent + " 条隐藏", hiddenContent ? "warn" : "ok") +
      '</div>' +
      '<div class="admin-dashboard-body">' +
      '<section class="admin-panel admin-dashboard-health-panel"><div class="admin-panel-head"><div><span class="admin-panel-kicker">运行概况</span>' +
      '<h3>平台健康度</h3><p>按用户、内容和题库查看当前覆盖情况</p></div></div>' +
      '<div class="admin-dashboard-health-groups">' +
      healthGroup(context, "用户与报告", "用户端及面试报告采集", [
        healthItem(context, "用户端正常", users.length - bannedUsers, totalUsers, bannedUsers ? "warn" : "ok"),
        healthItem(context, "报告数量", first(dashboard.reportCount, 0), totalInterviews, "ok"),
        healthItem(context, "跳过报告", first(dashboard.skippedReportCount, 0), totalInterviews, Number(first(dashboard.skippedReportCount, 0)) > 0 ? "warn" : "ok")
      ]) +
      healthGroup(context, "内容供给", "用户端资源展示状态", [
        healthItem(context, "展示中", visibleContent, content.length, visibleContent > 0 ? "ok" : "warn"),
        healthItem(context, "置顶内容", pinnedContent, content.length, pinnedContent > 0 ? "ok" : "warn"),
        healthItem(context, "隐藏内容", hiddenContent, content.length, hiddenContent > 0 ? "warn" : "ok")
      ]) +
      healthGroup(context, "题库覆盖", "面试与职业测评题目", [
        healthItem(context, "已发布面试题", publishedQuestions, questions.length, publishedQuestions > 0 ? "ok" : "warn"),
        healthItem(context, "隐藏面试题", hiddenQuestions, questions.length, hiddenQuestions > 0 ? "warn" : "ok"),
        healthItem(context, "测评题目", assessmentQuestionCount, banks.length, assessmentQuestionCount > 0 ? "ok" : "warn")
      ]) +
      '</div></section>' +
      '<section class="admin-panel admin-dashboard-attention"><div class="admin-panel-head"><div><span class="admin-panel-kicker">优先处理</span>' +
      '<h3>待关注事项</h3><p>根据当前已加载数据生成</p></div><span class="admin-attention-count">' + esc(context, alerts.length) + '</span></div>' +
      '<div class="admin-alert-list">' + (alerts.length ? alerts.map(function (alert, index) {
        return '<div class="admin-alert-item ' + escAttr(context, alert.type) + '"><span class="admin-alert-index">' +
          esc(context, index + 1) + '</span><div><strong>' + esc(context, alert.title) +
          '</strong><span>' + esc(context, alert.text) + '</span></div></div>';
      }).join("") : '<div class="admin-alert-item ok"><span class="admin-alert-index">✓</span><div><strong>暂无明显异常</strong>' +
        '<span>当前用户、内容和题库数据可以支撑基础使用。</span></div></div>') +
      '</div></section></div>' +
      '<section class="admin-panel admin-dashboard-recent"><div class="admin-panel-head"><div><span class="admin-panel-kicker">治理留痕</span>' +
      '<h3>最近操作</h3><p>管理员最近的关键动作</p></div><button type="button" class="admin-text-link" data-admin-jump="audit">查看全部记录</button></div>' +
      (logs.length ? table(["操作", "对象", "管理员", "时间"], logs.slice(0, 5).map(function (log) {
        return [
          esc(context, actionText(log.action)),
          twoLine(context, first(log.targetType, "-"), first(log.targetId, "")),
          esc(context, log.adminId),
          esc(context, first(log.createdAt, "-"))
        ];
      })) : empty("暂无操作记录", "管理员进行用户、内容、题库或公告操作后，会在这里形成记录。", context)) +
      '</section>';
  }

  function dashboardMetric(context, index, label, value, hint, status) {
    return '<article class="admin-dashboard-metric ' + escAttr(context, status || "") + '">' +
      '<span class="admin-dashboard-metric-index">' + esc(context, index) + '</span>' +
      '<div><span>' + esc(context, label) + '</span><strong>' + esc(context, first(value, 0)) +
      '</strong><small>' + esc(context, hint) + '</small></div></article>';
  }

  function quickEntry(context, key, title, detail) {
    return '<button type="button" class="admin-dashboard-quick-entry" data-admin-jump="' +
      escAttr(context, key) + '"><strong>' + esc(context, title) + '</strong><span>' +
      esc(context, detail) + '</span><i aria-hidden="true">→</i></button>';
  }

  function healthGroup(context, title, subtitle, items) {
    return '<section class="admin-dashboard-health-group"><div class="admin-dashboard-health-title"><strong>' +
      esc(context, title) + '</strong><span>' + esc(context, subtitle) + '</span></div>' +
      '<div class="admin-health-list">' + items.join("") + '</div></section>';
  }

  function renderUsers(data, context) {
    return panel(context, "用户管理", "管理注册用户和账号状态",
      '<div class="admin-panel-tools"><input id="adminUserSearch" type="search" placeholder="搜索姓名或用户ID..."></div>' +
      '<div class="admin-users-region">' + renderUserTable(data, context) + '</div>');
  }

  function renderUserTable(data, context) {
    var users = data.users && data.users.items || [];
    return users.length ? table([
        "用户", "身份类型", "状态", "操作"
      ], users.map(function (user) {
        var status = String(user.status || "ACTIVE");
        var banned = status === "BANNED";
        var currentAdmin = isCurrentAdminUser(data, user);
        var operation = banned
          ? actionButton(context, "unban-user", user.userId, "恢复用户端")
          : (user.administrator
            ? '<span class="admin-operation-note">' + (currentAdmin ? '不可禁用自己' : '管理员不可禁用') + '</span>'
            : actionButton(context, "ban-user", user.userId, "禁用用户端"));
        return [
          identityCell(context, first(user.nickname, user.displayName, user.userName, user.userId), user.userId) +
            (currentAdmin ? '<small>当前管理员，管理权限请在金蝶安全管理调整</small>' : ""),
          badge(context, user.administrator ? "管理员" : "普通用户", user.administrator ? "ok" : ""),
          badge(context, banned ? "用户端禁用" : "用户端正常", banned ? "warn" : "ok"),
          operation
        ];
      })) : empty("未找到匹配用户", "请尝试其他姓名或用户ID关键字。", context);
  }

  function renderContent(data, context) {
    var content = data.content || [];
    var studyContent = data.studyContent || [];
    currentContentItems = content;
    currentStudyContentItems = studyContent;
    return panel(context, "内容管理", "分别维护就业和升学的文章、视频与服务内容",
      contentForm(context) +
      '<div class="admin-content-group-lists">' + renderContentGroupLists(context) + '</div>');
  }

  function renderContentGroupLists(context) {
    return CONTENT_GROUPS.map(function (group) {
      return '<div class="admin-content-group-region' + (selectedContentType === group.type ? ' active' : '') +
        '" data-content-group="' + escAttr(context, group.type) + '">' +
        renderContentTable(contentItemsForGroup(group.type), group, context) + '</div>';
    }).join("");
  }

  function contentItemsForGroup(type) {
    var source = selectedContentScope === "STUDY" ? currentStudyContentItems : currentContentItems;
    return source.filter(function (item) {
      return normalizeContentType(item && item.type) === type &&
        (!showUnpublishedContentOnly || !!item.hidden);
    });
  }

  function renderContentTable(content, group, context) {
    var totalPages = Math.max(1, Math.ceil(content.length / CONTENT_PAGE_SIZE));
    var page = Math.min(Math.max(1, Number(contentPageByType[group.type]) || 1), totalPages);
    var start = (page - 1) * CONTENT_PAGE_SIZE;
    var pageItems = content.slice(start, start + CONTENT_PAGE_SIZE);
    contentPageByType[group.type] = page;
    var contentHtml = pageItems.length ? table(["标题", "展示分组", "展示状态", "外部链接", "操作"], pageItems.map(function (item) {
        return [
          twoLine(context, item.title, first(item.summary, item.contentId)),
          esc(context, contentGroupLabel(item.type, item.category)),
          badge(context, item.pinned ? "置顶" : "未置顶", item.pinned ? "ok" : "") + " " +
            badge(context, item.hidden ? "未发布" : "展示中", item.hidden ? "warn" : "ok"),
          linkCell(context, item.sourceUrl),
          editContentButton(context, item) +
            actionButton(context, "pin-content", item.contentId, item.pinned ? "取消置顶" : "置顶") +
            actionButton(context, "hide-content", item.contentId, item.hidden ? "恢复展示" : "隐藏") +
            actionButton(context, "delete-content", item.contentId, "删除", true)
        ];
      })) : (showUnpublishedContentOnly
        ? empty("暂无未发布的" + group.label, "当前分组没有暂不展示给用户的内容。", context)
        : empty("暂无" + group.label + "内容", "可以在上方新增" + group.label + "；保存后会同步进入用户端资源页。", context));
    return contentHtml + renderContentPager(context, group.type, page, totalPages, content.length);
  }

  function renderContentPager(context, type, page, totalPages, total) {
    return '<div class="admin-content-pager">' +
      '<span>第 ' + esc(context, page) + ' / ' + esc(context, totalPages) + ' 页，共 ' + esc(context, total) + ' 条，每页 ' + CONTENT_PAGE_SIZE + ' 条</span>' +
      '<div>' +
      '<button type="button" class="admin-content-page" data-content-page-type="' + escAttr(context, type) +
      '" data-content-page="' + escAttr(context, page - 1) + '"' + (page <= 1 ? ' disabled' : '') + '>上一页</button>' +
      '<button type="button" class="admin-content-page" data-content-page-type="' + escAttr(context, type) +
      '" data-content-page="' + escAttr(context, page + 1) + '"' + (page >= totalPages ? ' disabled' : '') + '>下一页</button>' +
      '</div></div>';
  }

  function contentForm(context) {
    return '<div class="admin-content-form">' +
      '<input type="hidden" id="adminContentId">' +
      '<input type="hidden" id="adminContentType" value="' + escAttr(context, selectedContentType) + '">' +
      '<input type="hidden" id="adminContentCategory" value="' + escAttr(context, categoryFromContentType(selectedContentType)) + '">' +
      '<div class="admin-form-title"><strong>编辑内容</strong><span>保存后，已发布内容会展示在对应用户端页面。</span></div>' +
      '<div class="admin-form-field wide"><span>内容归属</span><div class="admin-choice-tabs" role="tablist">' +
      '<button type="button" class="admin-choice-tab' + (selectedContentScope === "EMPLOYMENT" ? " active" : "") + '" data-content-scope="EMPLOYMENT">就业资讯</button>' +
      '<button type="button" class="admin-choice-tab' + (selectedContentScope === "STUDY" ? " active" : "") + '" data-content-scope="STUDY">升学资讯</button></div></div>' +
      '<div class="admin-form-field wide"><span>内容类型</span><div class="admin-choice-tabs" role="tablist">' +
      CONTENT_GROUPS.map(function (group, index) {
        return '<button type="button" class="admin-choice-tab' + (selectedContentType === group.type ? " active" : "") +
          '" data-content-type="' + escAttr(context, group.type) + '" data-content-category="' +
          escAttr(context, group.category) + '">' + esc(context, group.label) + '</button>';
      }).join("") + '</div></div>' +
      '<div class="admin-form-grid">' +
      '<label><span>标题</span><input id="adminContentTitle" placeholder="例如：后端简历优化指南"></label>' +
      '<label><span>外部链接</span><input id="adminContentSourceUrl" placeholder="https://..."></label>' +
      '<label class="wide"><span>摘要</span><textarea id="adminContentSummary" rows="3" placeholder="写给用户看的简介"></textarea></label>' +
      '</div>' +
      '<div class="admin-form-row">' +
      '<label class="admin-check"><input type="checkbox" id="adminContentPinned"><span>置顶展示</span></label>' +
      '<label class="admin-check"><input type="checkbox" id="adminContentHidden"><span>暂不展示给用户</span></label>' +
      '</div>' +
      '<div class="admin-form-actions">' +
      '<button type="button" class="admin-primary" id="adminContentSaveButton">保存内容</button>' +
      '<button type="button" class="admin-secondary' + (showUnpublishedContentOnly ? ' active' : '') +
      '" id="adminContentVisibilityButton" aria-pressed="' + (showUnpublishedContentOnly ? 'true' : 'false') + '">' +
      (showUnpublishedContentOnly ? '查看全部内容' : '查看未发布内容') + '</button>' +
      '</div>' +
      '</div>';
  }

  function renderQuestions(data, context) {
    var questions = data.questions || [];
    var banks = data.assessmentQuestionBanks || [];
    currentInterviewQuestions = questions;
    currentAssessmentBanks = banks;
    return panel(context, "题库管理", "管理面试题库，查看职业测评题库",
      '<div class="admin-bank-tabs" role="tablist">' +
      '<button type="button" class="admin-bank-tab active" data-bank-tab="interview">面试题库</button>' +
      '<button type="button" class="admin-bank-tab" data-bank-tab="assessment">职业测评题库</button>' +
      '</div>' +
      '<div class="admin-bank-section active" data-bank-section="interview">' +
      questionForm(context) +
      '<div class="admin-interview-question-region">' + renderInterviewQuestionList(questions, context) + '</div>' +
      '</div>' +
      '<div class="admin-bank-section" data-bank-section="assessment">' +
      assessmentQuestionForm(data, context) +
      renderAssessmentBanks(banks, context) +
      '</div>');
  }

  function renderInterviewQuestionList(questions, context) {
    var displayedQuestions = showUnpublishedInterviewQuestionsOnly
      ? questions.filter(isUnpublishedInterviewQuestion) : questions;
    var pagination = paginateQuestions(displayedQuestions, interviewQuestionPage);
    interviewQuestionPage = pagination.page;
    var body = pagination.items.length ? table(["题目", "岗位/难度", "来源", "状态", "操作"], pagination.items.map(function (question) {
        var hidden = question.reviewStatus === "REJECTED" || question.status === "HIDDEN";
        return [
          twoLine(context, first(question.summary, question.content), first(question.content, "")),
          twoLine(context, first(question.position, "通用岗位"), difficultyText(question.difficulty)),
          esc(context, sourceText(question.source)),
          badge(context, reviewText(question.reviewStatus), hidden ? "warn" : "ok"),
          editQuestionButton(context, question) +
            actionButton(context, "publish-question", question.questionId, "发布") +
            actionButton(context, "hide-question", question.questionId, "隐藏") +
            actionButton(context, "delete-question", question.questionId, "删除", true)
        ];
      })) : empty(showUnpublishedInterviewQuestionsOnly ? "暂无未发布面试题" : "暂无面试题",
        showUnpublishedInterviewQuestionsOnly ? "当前没有待审核或已隐藏的面试题。" : "可以在上方保存面试题；发布后的题目可用于后续面试练习。", context);
    return body + renderQuestionPager(context, "interview", "", pagination);
  }

  function questionForm(context) {
    return '<div class="admin-question-form">' +
      '<input type="hidden" id="adminQuestionId">' +
      '<div class="admin-form-title"><strong>编辑面试题</strong><span>发布后的题目会进入面试题库；隐藏后不会给用户端使用。</span></div>' +
      '<div class="admin-form-grid">' +
      '<label><span>适用岗位</span><input id="adminQuestionPosition" placeholder="例如：后端开发"></label>' +
      '<label><span>难度</span><select id="adminQuestionDifficulty">' +
      '<option value="EASY">入门</option><option value="NORMAL">常规</option><option value="HARD">进阶</option>' +
      '</select></label>' +
      '<label><span>展示标题</span><input id="adminQuestionSummary" placeholder="例如：Redis 缓存一致性"></label>' +
      '<label><span>发布状态</span><select id="adminQuestionReviewStatus">' +
      '<option value="PENDING_REVIEW">待审核</option><option value="PUBLISHED">发布</option><option value="REJECTED">隐藏</option>' +
      '</select></label>' +
      '<label class="wide"><span>题目正文</span><textarea id="adminQuestionContent" rows="3" placeholder="写清楚候选人需要回答的问题"></textarea></label>' +
      '<label class="wide"><span>参考答案</span><textarea id="adminQuestionAnswer" rows="4" placeholder="可选，写给练习或后续解析使用"></textarea></label>' +
      '</div>' +
      '<div class="admin-form-actions">' +
      '<button type="button" class="admin-primary" id="adminQuestionSaveButton">保存题目</button>' +
      '<button type="button" class="admin-secondary' + (showUnpublishedInterviewQuestionsOnly ? ' active' : '') +
      '" id="adminQuestionVisibilityButton" aria-pressed="' + (showUnpublishedInterviewQuestionsOnly ? 'true' : 'false') + '">' +
      (showUnpublishedInterviewQuestionsOnly ? '查看全部题目' : '查看未发布题目') + '</button>' +
      '</div>' +
      '</div>';
  }

  function assessmentQuestionForm(data, context) {
    var banks = data.assessmentQuestionBanks || data.assessmentScales || [];
    return '<div class="admin-question-form admin-assessment-form">' +
      '<input type="hidden" id="adminAssessmentQuestionId">' +
      '<div class="admin-form-title"><strong>编辑测评题</strong><span>保存后会立即影响应用运行期的测评题库。</span></div>' +
      '<div class="admin-form-grid">' +
      '<label><span>所属量表</span><select id="adminAssessmentScaleId">' +
      banks.map(function (bank) {
        return '<option value="' + escAttr(context, bank.scaleId) + '">' + esc(context, bank.title || bank.scaleId) + '</option>';
      }).join("") + '</select></label>' +
      '<label><span>排序</span><input id="adminAssessmentSortOrder" placeholder="留空自动追加"></label>' +
      '<label><span>维度</span><input id="adminAssessmentDimension" placeholder="例如：EI、R/I、PLAN"><small class="admin-form-hint" id="adminAssessmentDimensionHint">选择量表后显示建议维度。</small></label>' +
      '<label><span>题型</span><select id="adminAssessmentQuestionType"><option value="SINGLE">单选</option><option value="MULTI">多选</option></select><small class="admin-form-hint" id="adminAssessmentTypeHint">单选题填写 A/B 两个选项；多选题填写 A/B/C/D 四个选项。</small></label>' +
      '<label class="wide"><span>题目</span><textarea id="adminAssessmentQuestionText" rows="3" placeholder="请输入测评题目"></textarea></label>' +
      '<label><span>选项 A</span><input id="adminAssessmentOptionAText" placeholder="选项 A 文案"></label>' +
      '<label><span>A 维度</span><input id="adminAssessmentOptionADimension" placeholder="例如：E"></label>' +
      '<label><span>选项 B</span><input id="adminAssessmentOptionBText" placeholder="选项 B 文案"></label>' +
      '<label><span>B 维度</span><input id="adminAssessmentOptionBDimension" placeholder="例如：I"></label>' +
      '<label class="admin-assessment-extra-option"><span>选项 C</span><input id="adminAssessmentOptionCText" placeholder="选项 C 文案"></label>' +
      '<label class="admin-assessment-extra-option"><span>C 维度</span><input id="adminAssessmentOptionCDimension" placeholder="例如：S"></label>' +
      '<label class="admin-assessment-extra-option"><span>选项 D</span><input id="adminAssessmentOptionDText" placeholder="选项 D 文案"></label>' +
      '<label class="admin-assessment-extra-option"><span>D 维度</span><input id="adminAssessmentOptionDDimension" placeholder="例如：N"></label>' +
      '</div>' +
      '<div class="admin-form-row"><label class="admin-check"><input type="checkbox" id="adminAssessmentQuestionPublished"><span>立即发布给用户</span></label></div>' +
      '<div class="admin-form-actions">' +
      '<button type="button" class="admin-primary" id="adminAssessmentQuestionSaveButton">保存测评题</button>' +
      '<button type="button" class="admin-secondary' + (showUnpublishedAssessmentQuestionsOnly ? ' active' : '') +
      '" id="adminAssessmentQuestionVisibilityButton" aria-pressed="' + (showUnpublishedAssessmentQuestionsOnly ? 'true' : 'false') + '">' +
      (showUnpublishedAssessmentQuestionsOnly ? '查看全部题目' : '查看未发布题目') + '</button>' +
      '</div>' +
      '</div>';
  }

  function renderAssessmentBanks(banks, context) {
    if (!banks.length) {
      return empty("暂无职业测评题库", "当前没有读取到职业测评量表。", context);
    }
    return '<div class="admin-assessment-note">题库和每次作答题数会持久保存；用户开始测评时按维度均衡抽题。</div>' +
      '<div class="admin-assessment-type-tabs" role="tablist">' + banks.map(function (bank, index) {
        var questions = bank.questions || [];
        return '<button type="button" class="admin-assessment-tab' + (index === 0 ? " active" : "") +
          '" data-scale-id="' + escAttr(context, bank.scaleId) + '">' +
          '<strong>' + esc(context, bank.title || "职业测评") + '</strong><span>' +
          esc(context, first(bank.poolQuestionCount, questions.length, 0)) + ' 题题库</span></button>';
      }).join("") + '</div>' +
      '<div class="admin-assessment-grid">' + banks.map(function (bank, index) {
        var questions = bank.questions || [];
        return '<div class="admin-assessment-panel' + (index === 0 ? " active" : "") +
          '" data-scale-id="' + escAttr(context, bank.scaleId) + '"><article class="admin-assessment-card">' +
          '<header><div><h4>' + esc(context, bank.title || "职业测评") + '</h4><p>' +
          esc(context, first(bank.description, "用于职业倾向和能力画像分析")) + '</p></div>' +
          '<span>每次 ' + esc(context, first(bank.answerQuestionCount, bank.questionCount, questions.length, 0)) + ' 题</span></header>' +
          '<div class="admin-assessment-meta">版本 ' + esc(context, first(bank.version, "-")) + '</div>' +
          '<div class="admin-assessment-settings">' +
          '<label><span>题库总数</span><strong>' + esc(context, first(bank.poolQuestionCount, questions.length, 0)) + ' 题</strong></label>' +
          '<label><span>每次作答题数</span><input type="number" min="1" max="' +
          escAttr(context, first(bank.poolQuestionCount, questions.length, 1)) + '" value="' +
          escAttr(context, first(bank.answerQuestionCount, bank.questionCount, questions.length, 1)) +
          '" data-assessment-answer-count="' + escAttr(context, bank.scaleId) + '"></label>' +
          '<button type="button" class="admin-secondary" data-save-assessment-scale="' +
          escAttr(context, bank.scaleId) + '">保存作答题数</button></div>' +
          '<div class="admin-assessment-questions-region">' + renderAssessmentQuestionList(bank, context) +
          '</div></article></div>';
      }).join("") + '</div>';
  }

  function renderAssessmentQuestionList(bank, context) {
    var scaleId = String(first(bank && bank.scaleId, ""));
    var questions = bank && bank.questions || [];
    var displayedQuestions = showUnpublishedAssessmentQuestionsOnly
      ? questions.filter(isUnpublishedAssessmentQuestion) : questions;
    var pagination = paginateQuestions(displayedQuestions, assessmentQuestionPageByScale[scaleId]);
    assessmentQuestionPageByScale[scaleId] = pagination.page;
    var body = pagination.items.length ? '<div class="admin-assessment-questions">' + pagination.items.map(function (question) {
      return '<div class="admin-assessment-question"><strong>' + esc(context, first(question.sortOrder, "")) +
        '. ' + esc(context, question.questionText) + '</strong><small>' +
        esc(context, questionTypeText(question.questionType)) + ' · ' + esc(context, first(question.dimensionCode, "未设置维度")) +
        ' · ' + (question && question.published === false ? '未发布' : '已发布') + '</small>' +
        '<p>' + (question.options || []).map(function (option) {
          return esc(context, option.optionLabel) + ". " + esc(context, option.optionText);
        }).join(" / ") + '</p><div class="admin-assessment-actions">' +
        editAssessmentQuestionButton(context, bank, question) +
        actionButton(context, "delete-assessment-question", question.questionId, "删除", true,
          ' data-scale-id="' + escAttr(context, scaleId) + '"') +
        '</div></div>';
    }).join("") + '</div>' : empty(showUnpublishedAssessmentQuestionsOnly ? "暂无未发布测评题" : "暂无测评题",
      showUnpublishedAssessmentQuestionsOnly ? "当前量表没有未发布的测评题。" : "当前量表还没有题目，可以在上方保存。", context);
    return body + renderQuestionPager(context, "assessment", scaleId, pagination);
  }

  function isUnpublishedInterviewQuestion(question) {
    var reviewStatus = String(first(question && question.reviewStatus, "")).toUpperCase();
    var status = String(first(question && question.status, "")).toUpperCase();
    return reviewStatus !== "PUBLISHED" || status === "HIDDEN";
  }

  function isUnpublishedAssessmentQuestion(question) {
    return question && question.published === false;
  }

  function paginateQuestions(questions, requestedPage) {
    questions = questions || [];
    var totalPages = Math.max(1, Math.ceil(questions.length / QUESTION_PAGE_SIZE));
    var page = Math.min(Math.max(1, Number(requestedPage) || 1), totalPages);
    var start = (page - 1) * QUESTION_PAGE_SIZE;
    return {
      items: questions.slice(start, start + QUESTION_PAGE_SIZE),
      page: page,
      totalPages: totalPages,
      total: questions.length
    };
  }

  function renderQuestionPager(context, kind, scaleId, pagination) {
    var attributes = kind === "assessment"
      ? ' data-assessment-question-page="' + escAttr(context, pagination.page) + '" data-scale-id="' + escAttr(context, scaleId) + '"'
      : ' data-interview-question-page="' + escAttr(context, pagination.page) + '"';
    var previousAttribute = kind === "assessment" ? "data-assessment-question-target" : "data-interview-question-target";
    return '<div class="admin-question-pager">' +
      '<span>第 ' + esc(context, pagination.page) + ' / ' + esc(context, pagination.totalPages) + ' 页，共 ' +
      esc(context, pagination.total) + ' 道题，每页 ' + QUESTION_PAGE_SIZE + ' 道</span><div>' +
      '<button type="button" class="admin-question-page" ' + previousAttribute + '="' + escAttr(context, pagination.page - 1) + '"' + attributes +
      (pagination.page <= 1 ? ' disabled' : '') + '>上一页</button>' +
      '<button type="button" class="admin-question-page" ' + previousAttribute + '="' + escAttr(context, pagination.page + 1) + '"' + attributes +
      (pagination.page >= pagination.totalPages ? ' disabled' : '') + '>下一页</button></div></div>';
  }

  function renderBroadcast(data, context) {
    var users = (data.users && data.users.items || []).filter(function (user) {
      return user && !user.deletedAt && String(user.status || "ACTIVE") === "ACTIVE";
    });
    return panel(context, "通知公告", "给用户发送站内公告", '<div class="admin-broadcast-form">' +
      '<div class="admin-form-title"><strong>发送公告</strong><span>可搜索并勾选多个接收用户；未选择时发送给所有用户端正常的用户。</span></div>' +
      '<div class="admin-form-grid">' +
      '<label class="wide"><span>接收用户</span><input id="adminBroadcastUserSearch" type="search" placeholder="搜索用户姓名、昵称或用户ID"></label>' +
      '<label class="wide"><span>标题</span><input id="adminBroadcastTitle" placeholder="例如：面试练习服务维护通知"></label>' +
      '<label class="wide"><span>内容</span><textarea id="adminBroadcastContent" rows="4" placeholder="请输入公告内容"></textarea></label>' +
      '</div>' +
      '<div class="admin-recipient-tools">' +
      '<span id="adminBroadcastRecipientSummary">未选择用户时，将发送给所有用户端正常的用户。</span>' +
      '<div><button type="button" class="admin-secondary" id="adminBroadcastSelectVisible">全选当前结果</button>' +
      '<button type="button" class="admin-secondary" id="adminBroadcastClearUsers">清空选择</button></div>' +
      '</div>' +
      '<div class="admin-recipient-list" id="adminBroadcastRecipientList">' + renderBroadcastRecipientList(context, users) + '</div>' +
      '<div class="admin-form-row"><button type="button" class="admin-primary" id="adminBroadcastButton">发送公告</button></div>' +
      '</div>');
  }

  function renderAudit(data, context) {
    return panel(context, "\u5ba1\u8ba1\u65e5\u5fd7", "\u67e5\u770b\u7ba1\u7406\u5458\u64cd\u4f5c\u8bb0\u5f55",
      '<div class="admin-audit-region">' + renderAuditTable(data.auditLogs || {}, context) + '</div>');
  }

  function renderAuditTable(auditLogs, context) {
    auditLogs = auditLogs || {};
    var logs = auditLogs.items || [];
    return (logs.length ? table(["\u52a8\u4f5c", "\u5bf9\u8c61", "\u7ba1\u7406\u5458", "\u65f6\u95f4"], logs.map(function (log) {
        return [
          esc(context, actionText(log.action)),
          twoLine(context, first(log.targetType, "-"), first(log.targetId, "")),
          esc(context, log.adminId),
          esc(context, first(log.createdAt, "-"))
        ];
      })) : empty("\u6682\u65e0\u64cd\u4f5c\u8bb0\u5f55", "\u7ba1\u7406\u5458\u8fdb\u884c\u7981\u7528\u3001\u5ba1\u6838\u3001\u7f6e\u9876\u3001\u516c\u544a\u7b49\u64cd\u4f5c\u540e\uff0c\u4f1a\u5728\u8fd9\u91cc\u7559\u4e0b\u5ba1\u8ba1\u8bb0\u5f55\u3002", context)) +
      renderAuditPager(context, auditLogs);
  }

  function renderAuditPager(context, auditLogs) {
    auditLogs = auditLogs || {};
    var page = Number(first(auditLogs.page, 0));
    var size = Number(first(auditLogs.size, 10)) || 10;
    var total = Number(first(auditLogs.total, auditLogs.items && auditLogs.items.length, 0));
    var totalPages = Math.max(1, Math.ceil(total / size));
    return '<div class="admin-audit-pager" data-page="' + escAttr(context, page) + '" data-size="' + escAttr(context, size) +
      '" data-total="' + escAttr(context, total) + '">' +
      '<span>\u7b2c ' + esc(context, page + 1) + ' / ' + esc(context, totalPages) + ' \u9875\uff0c\u5171 ' + esc(context, total) + ' \u6761\uff0c\u6bcf\u9875 10 \u6761</span>' +
      '<div>' +
      '<button type="button" class="admin-audit-page" data-audit-page="' + escAttr(context, page - 1) + '"' + (page <= 0 ? " disabled" : "") + '>\u4e0a\u4e00\u9875</button>' +
      '<button type="button" class="admin-audit-page" data-audit-page="' + escAttr(context, page + 1) + '"' + (page >= totalPages - 1 ? " disabled" : "") + '>\u4e0b\u4e00\u9875</button>' +
      '</div></div>';
  }

  function bindActions(item, context) {
    var root = context.pageHost && context.pageHost.querySelector(".admin-app");
    var service = window.CYANCRUISE_SERVICES.admin;
    if (!root || !service) return;
    Array.prototype.forEach.call(root.querySelectorAll(".admin-nav-item"), function (button) {
      button.addEventListener("click", function () {
        activateTab(root, this.getAttribute("data-tab"));
      });
    });
    Array.prototype.forEach.call(root.querySelectorAll("[data-admin-jump]"), function (button) {
      button.addEventListener("click", function () {
        activateTab(root, this.getAttribute("data-admin-jump"));
        var main = root.querySelector(".admin-main");
        if (main && typeof main.scrollIntoView === "function") {
          main.scrollIntoView({ block: "start" });
        }
      });
    });
    bindActionButtons(item, context, service, root);
    bindUserSearch(item, context, service, root);
    bindContentForm(item, context, service, root);
    bindContentPager(item, context, service, root);
    bindQuestionPager(item, context, service, root);
    bindQuestionForm(item, context, service, root);
    bindAssessmentQuestionForm(item, context, service, root);
    bindAssessmentScaleSettings(item, context, service, root);
    bindQuestionBankTabs(root);
    bindAssessmentTabs(root);
    bindBroadcast(item, context, service, root);
    bindAuditPager(context, service, root);
  }

  function bindActionButtons(item, context, service, scope) {
    Array.prototype.forEach.call(scope.querySelectorAll(".admin-action"), function (button) {
      if (button.getAttribute("data-admin-action-bound") === "true") return;
      button.setAttribute("data-admin-action-bound", "true");
      button.addEventListener("click", function () {
        runAction(item, context, service, this.getAttribute("data-action"), this.getAttribute("data-id"), this);
      });
    });
  }

  function bindUserSearch(item, context, service, root) {
    var input = root.querySelector("#adminUserSearch");
    var region = root.querySelector(".admin-users-region");
    if (!input || !region) return;
    var timer = 0;
    var requestSequence = 0;
    input.addEventListener("input", function () {
      var keyword = input.value;
      if (timer) window.clearTimeout(timer);
      timer = window.setTimeout(function () {
        var sequence = ++requestSequence;
        input.setAttribute("aria-busy", "true");
        service.listUsers(adminContext(context), keyword, 20).then(function (users) {
          if (sequence !== requestSequence) return;
          region.innerHTML = renderUserTable({
            users: users,
            adminId: first(context.identity && context.identity.adminId, context.identity && context.identity.userId)
          }, context);
          bindActionButtons(item, context, service, region);
        }).catch(function (error) {
          if (sequence === requestSequence) {
            context.showMessage("error", "搜索失败", messageOf(error));
          }
        }).then(function () {
          if (sequence === requestSequence) input.removeAttribute("aria-busy");
        });
      }, 250);
    });
  }

  function bindAuditPager(context, service, root) {
    Array.prototype.forEach.call(root.querySelectorAll(".admin-audit-page"), function (button) {
      button.addEventListener("click", function () {
        var page = Number(this.getAttribute("data-audit-page"));
        var region = root.querySelector(".admin-audit-region");
        if (!region || !isFinite(page) || page < 0) return;
        this.disabled = true;
        service.auditLogs(adminContext(context), page, 10).then(function (result) {
          region.innerHTML = renderAuditTable(result, context);
          bindAuditPager(context, service, root);
        }).catch(function (error) {
          context.showMessage("error", "\u52a0\u8f7d\u5931\u8d25", messageOf(error));
        });
      });
    });
  }

  function activateBankTab(root, key) {
    if (!root) return;
    Array.prototype.forEach.call(root.querySelectorAll(".admin-bank-tab"), function (tab) {
      tab.classList.toggle("active", tab.getAttribute("data-bank-tab") === key);
    });
    Array.prototype.forEach.call(root.querySelectorAll(".admin-bank-section"), function (section) {
      section.classList.toggle("active", section.getAttribute("data-bank-section") === key);
    });
  }

  function bindQuestionBankTabs(root) {
    Array.prototype.forEach.call(root.querySelectorAll(".admin-bank-tab"), function (button) {
      button.addEventListener("click", function () {
        activateBankTab(root, this.getAttribute("data-bank-tab"));
      });
    });
  }

  function activateAssessmentTab(root, scaleId) {
    if (!root || !scaleId) return;
    Array.prototype.forEach.call(root.querySelectorAll(".admin-assessment-tab"), function (tab) {
      tab.classList.toggle("active", tab.getAttribute("data-scale-id") === String(scaleId));
    });
    Array.prototype.forEach.call(root.querySelectorAll(".admin-assessment-panel"), function (panel) {
      panel.classList.toggle("active", panel.getAttribute("data-scale-id") === String(scaleId));
    });
    setValue(root, "adminAssessmentScaleId", scaleId);
    updateAssessmentQuestionFormHints(root);
  }

  function bindContentPager(item, context, service, root) {
    Array.prototype.forEach.call(root.querySelectorAll(".admin-content-page"), function (button) {
      if (button.getAttribute("data-content-page-bound") === "true") return;
      button.setAttribute("data-content-page-bound", "true");
      button.addEventListener("click", function () {
        var type = normalizeContentType(this.getAttribute("data-content-page-type"));
        var page = Number(this.getAttribute("data-content-page"));
        var group = contentGroupByType(type);
        var region = root.querySelector('[data-content-group="' + type + '"]');
        if (!region || !group || !isFinite(page) || page < 1) return;
        contentPageByType[type] = page;
        region.innerHTML = renderContentTable(contentItemsForGroup(type), group, context);
        bindActionButtons(item, context, service, region);
        bindContentPager(item, context, service, root);
      });
    });
  }

  function bindQuestionPager(item, context, service, root) {
    Array.prototype.forEach.call(root.querySelectorAll(".admin-question-page"), function (button) {
      if (button.getAttribute("data-question-page-bound") === "true") return;
      button.setAttribute("data-question-page-bound", "true");
      button.addEventListener("click", function () {
        var assessmentTarget = this.getAttribute("data-assessment-question-target");
        if (assessmentTarget !== null) {
          var scaleId = this.getAttribute("data-scale-id") || "";
          var assessmentPage = Number(assessmentTarget);
          var bank = assessmentBankByScaleId(scaleId);
          var panel = findAssessmentPanel(root, scaleId);
          var assessmentRegion = panel && panel.querySelector(".admin-assessment-questions-region");
          if (!bank || !assessmentRegion || !isFinite(assessmentPage) || assessmentPage < 1) return;
          assessmentQuestionPageByScale[scaleId] = assessmentPage;
          assessmentRegion.innerHTML = renderAssessmentQuestionList(bank, context);
          bindActionButtons(item, context, service, assessmentRegion);
          bindQuestionPager(item, context, service, root);
          return;
        }
        var interviewPage = Number(this.getAttribute("data-interview-question-target"));
        var interviewRegion = root.querySelector(".admin-interview-question-region");
        if (!interviewRegion || !isFinite(interviewPage) || interviewPage < 1) return;
        interviewQuestionPage = interviewPage;
        interviewRegion.innerHTML = renderInterviewQuestionList(currentInterviewQuestions, context);
        bindActionButtons(item, context, service, interviewRegion);
        bindQuestionPager(item, context, service, root);
      });
    });
  }

  function assessmentBankByScaleId(scaleId) {
    for (var index = 0; index < currentAssessmentBanks.length; index += 1) {
      if (String(currentAssessmentBanks[index] && currentAssessmentBanks[index].scaleId) === String(scaleId)) {
        return currentAssessmentBanks[index];
      }
    }
    return null;
  }

  function findAssessmentPanel(root, scaleId) {
    var panels = root.querySelectorAll(".admin-assessment-panel[data-scale-id]");
    for (var index = 0; index < panels.length; index += 1) {
      if (panels[index].getAttribute("data-scale-id") === String(scaleId)) return panels[index];
    }
    return null;
  }

  function bindAssessmentTabs(root) {
    Array.prototype.forEach.call(root.querySelectorAll(".admin-assessment-tab"), function (button) {
      button.addEventListener("click", function () {
        activateAssessmentTab(root, this.getAttribute("data-scale-id"));
      });
    });
  }

  function bindQuestionForm(item, context, service, root) {
    var saveButton = root.querySelector("#adminQuestionSaveButton");
    var visibilityButton = root.querySelector("#adminQuestionVisibilityButton");
    if (visibilityButton) {
      visibilityButton.addEventListener("click", function () {
        showUnpublishedInterviewQuestionsOnly = !showUnpublishedInterviewQuestionsOnly;
        interviewQuestionPage = 1;
        visibilityButton.classList.toggle("active", showUnpublishedInterviewQuestionsOnly);
        visibilityButton.setAttribute("aria-pressed", showUnpublishedInterviewQuestionsOnly ? "true" : "false");
        visibilityButton.textContent = showUnpublishedInterviewQuestionsOnly ? "查看全部题目" : "查看未发布题目";
        var region = root.querySelector(".admin-interview-question-region");
        if (region) {
          region.innerHTML = renderInterviewQuestionList(currentInterviewQuestions, context);
          bindActionButtons(item, context, service, region);
          bindQuestionPager(item, context, service, root);
        }
      });
    }
    if (saveButton) {
      saveButton.addEventListener("click", function () {
        var payload = readQuestionForm(root);
        if (!payload.content) {
          context.showMessage("error", "保存失败", "请先填写题目正文。");
          return;
        }
        saveButton.disabled = true;
        service.saveQuestion(adminContext(context), payload).then(function () {
          return refreshAdminApp(item, context, service, "questions").then(function () {
            context.showMessage("info", "题目已保存", "面试题库数据已更新。");
          });
        }).catch(function (error) {
          saveButton.disabled = false;
          context.showMessage("error", "保存失败", messageOf(error));
        });
      });
    }
  }

  function bindAssessmentQuestionForm(item, context, service, root) {
    var saveButton = root.querySelector("#adminAssessmentQuestionSaveButton");
    var visibilityButton = root.querySelector("#adminAssessmentQuestionVisibilityButton");
    var scaleSelect = root.querySelector("#adminAssessmentScaleId");
    var typeSelect = root.querySelector("#adminAssessmentQuestionType");
    if (scaleSelect) {
      scaleSelect.addEventListener("change", function () {
        updateAssessmentQuestionFormHints(root);
      });
    }
    if (typeSelect) {
      typeSelect.addEventListener("change", function () {
        updateAssessmentQuestionFormHints(root);
      });
    }
    updateAssessmentQuestionFormHints(root);
    if (visibilityButton) {
      visibilityButton.addEventListener("click", function () {
        showUnpublishedAssessmentQuestionsOnly = !showUnpublishedAssessmentQuestionsOnly;
        Object.keys(assessmentQuestionPageByScale).forEach(function (scaleId) {
          assessmentQuestionPageByScale[scaleId] = 1;
        });
        visibilityButton.classList.toggle("active", showUnpublishedAssessmentQuestionsOnly);
        visibilityButton.setAttribute("aria-pressed", showUnpublishedAssessmentQuestionsOnly ? "true" : "false");
        visibilityButton.textContent = showUnpublishedAssessmentQuestionsOnly ? "查看全部题目" : "查看未发布题目";
        currentAssessmentBanks.forEach(function (bank) {
          var panel = findAssessmentPanel(root, bank && bank.scaleId);
          var region = panel && panel.querySelector(".admin-assessment-questions-region");
          if (!region) return;
          region.innerHTML = renderAssessmentQuestionList(bank, context);
          bindActionButtons(item, context, service, region);
        });
        bindQuestionPager(item, context, service, root);
      });
    }
    if (saveButton) {
      saveButton.addEventListener("click", function () {
        var payload = readAssessmentQuestionForm(root);
        if (!payload.scaleId || !payload.question.questionText) {
          context.showMessage("error", "保存失败", "请先选择量表并填写测评题目。");
          return;
        }
        if (!assessmentOptionsComplete(payload.question)) {
          context.showMessage("error", "保存失败", payload.question.questionType === "MULTI" ? "多选题需要填写 A、B、C、D 四个选项及对应维度。" : "单选题需要填写 A、B 两个选项及对应维度。");
          return;
        }
        saveButton.disabled = true;
        service.saveAssessmentQuestion(adminContext(context), payload.scaleId, payload.question).then(function () {
          return refreshAdminApp(item, context, service, "questions").then(function () {
            var appRoot = context.pageHost && context.pageHost.querySelector(".admin-app");
            activateBankTab(appRoot, "assessment");
            activateAssessmentTab(appRoot, payload.scaleId);
            context.showMessage("info", "测评题已保存", "职业测评题库数据已更新。");
          });
        }).catch(function (error) {
          saveButton.disabled = false;
          context.showMessage("error", "保存失败", messageOf(error));
        });
      });
    }
  }

  function bindAssessmentScaleSettings(item, context, service, root) {
    Array.prototype.forEach.call(root.querySelectorAll("[data-save-assessment-scale]"), function (button) {
      button.addEventListener("click", function () {
        var scaleId = this.getAttribute("data-save-assessment-scale");
        var input = root.querySelector('[data-assessment-answer-count="' + scaleId + '"]');
        var answerQuestionCount = input ? Number(input.value) : 0;
        var max = input ? Number(input.getAttribute("max")) : 0;
        if (!answerQuestionCount || answerQuestionCount < 1 || (max && answerQuestionCount > max)) {
          context.showMessage("error", "保存失败", "每次作答题数必须在 1 到题库总数之间。");
          return;
        }
        button.disabled = true;
        service.saveAssessmentScale(adminContext(context), scaleId, answerQuestionCount).then(function () {
          return refreshAdminApp(item, context, service, "questions").then(function () {
            var appRoot = context.pageHost && context.pageHost.querySelector(".admin-app");
            activateBankTab(appRoot, "assessment");
            activateAssessmentTab(appRoot, scaleId);
            context.showMessage("info", "作答题数已保存", "用户下次开始该测评时会按新题数均衡抽题。");
          });
        }).catch(function (error) {
          button.disabled = false;
          context.showMessage("error", "保存失败", messageOf(error));
        });
      });
    });
  }

  function bindContentForm(item, context, service, root) {
    var saveButton = root.querySelector("#adminContentSaveButton");
    var visibilityButton = root.querySelector("#adminContentVisibilityButton");
    bindContentTypeTabs(root);
    Array.prototype.forEach.call(root.querySelectorAll("[data-content-scope]"), function (button) { button.addEventListener("click", function () { selectedContentScope = this.getAttribute("data-content-scope"); refreshAdminApp(item, context, service, "content"); }); });
    if (visibilityButton) {
      visibilityButton.addEventListener("click", function () {
        showUnpublishedContentOnly = !showUnpublishedContentOnly;
        CONTENT_GROUPS.forEach(function (group) {
          contentPageByType[group.type] = 1;
        });
        visibilityButton.classList.toggle("active", showUnpublishedContentOnly);
        visibilityButton.setAttribute("aria-pressed", showUnpublishedContentOnly ? "true" : "false");
        visibilityButton.textContent = showUnpublishedContentOnly ? "查看全部内容" : "查看未发布内容";
        var lists = root.querySelector(".admin-content-group-lists");
        if (lists) {
          lists.innerHTML = renderContentGroupLists(context);
          bindActionButtons(item, context, service, lists);
          bindContentPager(item, context, service, root);
        }
      });
    }
    if (saveButton) {
      saveButton.addEventListener("click", function () {
        var payload = readContentForm(root);
        if (!payload.title) {
          context.showMessage("error", "保存失败", "请先填写内容标题。");
          return;
        }
        saveButton.disabled = true;
        (selectedContentScope === "STUDY" ? service.saveStudyContent(adminContext(context), payload) : service.saveContent(adminContext(context), payload)).then(function () {
          if (selectedContentScope === "STUDY") invalidateStudyCenterResources(context); else invalidateEmploymentResources(context);
          return refreshAdminApp(item, context, service, "content").then(function () {
          context.showMessage("info", "内容已保存", "已发布内容会展示在对应用户端页面。");
          });
        }).catch(function (error) {
          saveButton.disabled = false;
          context.showMessage("error", "保存失败", messageOf(error));
        });
      });
    }
  }

  function bindBroadcast(item, context, service, root) {
    var broadcastButton = root.querySelector("#adminBroadcastButton");
    if (!broadcastButton) return;
    bindBroadcastRecipients(root, context, service);
    broadcastButton.addEventListener("click", function () {
      var activeTab = currentActiveTab(root);
      var userIds = selectedBroadcastUserIds(root);
      broadcastButton.disabled = true;
      service.broadcast(adminContext(context), {
        userIds: userIds,
        userId: userIds.length === 1 ? userIds[0] : "",
        title: value(root, "adminBroadcastTitle"),
        content: value(root, "adminBroadcastContent")
      }).then(function (result) {
        return refreshAdminApp(item, context, service, activeTab).then(function () {
          context.showMessage("info", "公告已发送", "成功 " + first(result.successCount, 0) + " 个，失败 " + first(result.failedCount, 0) + " 个。");
        });
      }).catch(function (error) {
        broadcastButton.disabled = false;
        context.showMessage("error", "发送失败", messageOf(error));
      });
    });
  }

  function bindBroadcastRecipients(root, context, service) {
    root._cyanBroadcastSelectedIds = root._cyanBroadcastSelectedIds || {};
    var search = root.querySelector("#adminBroadcastUserSearch");
    var selectVisible = root.querySelector("#adminBroadcastSelectVisible");
    var clearUsers = root.querySelector("#adminBroadcastClearUsers");
    var searchTimer = 0;
    if (search) {
      search.addEventListener("input", function () {
        var keyword = search.value;
        if (searchTimer) window.clearTimeout(searchTimer);
        searchTimer = window.setTimeout(function () {
          queryBroadcastRecipients(root, context, service, keyword);
        }, 250);
      });
    }
    bindBroadcastRecipientCheckboxes(root);
    if (selectVisible) {
      selectVisible.addEventListener("click", function () {
        Array.prototype.forEach.call(root.querySelectorAll(".admin-recipient-option"), function (option) {
          if (option.style.display === "none") return;
          var checkbox = option.querySelector(".admin-broadcast-user");
          if (checkbox) {
            checkbox.checked = true;
            root._cyanBroadcastSelectedIds[checkbox.value] = true;
          }
        });
        updateBroadcastRecipientSummary(root);
      });
    }
    if (clearUsers) {
      clearUsers.addEventListener("click", function () {
        root._cyanBroadcastSelectedIds = {};
        Array.prototype.forEach.call(root.querySelectorAll(".admin-broadcast-user"), function (checkbox) {
          checkbox.checked = false;
        });
        updateBroadcastRecipientSummary(root);
      });
    }
    updateBroadcastRecipientSummary(root);
  }

  function bindBroadcastRecipientCheckboxes(root) {
    Array.prototype.forEach.call(root.querySelectorAll(".admin-broadcast-user"), function (checkbox) {
      checkbox.checked = !!(root._cyanBroadcastSelectedIds && root._cyanBroadcastSelectedIds[checkbox.value]);
      checkbox.addEventListener("change", function () {
        root._cyanBroadcastSelectedIds = root._cyanBroadcastSelectedIds || {};
        if (checkbox.checked) root._cyanBroadcastSelectedIds[checkbox.value] = true;
        else delete root._cyanBroadcastSelectedIds[checkbox.value];
        updateBroadcastRecipientSummary(root);
      });
    });
  }

  function selectedBroadcastUserIds(root) {
    var selected = root._cyanBroadcastSelectedIds || {};
    Array.prototype.forEach.call(root.querySelectorAll(".admin-broadcast-user:checked"), function (checkbox) {
      selected[checkbox.value] = true;
    });
    return Object.keys(selected).filter(function (userId) {
      return !!userId;
    });
  }

  function queryBroadcastRecipients(root, context, service, keyword) {
    var list = root.querySelector("#adminBroadcastRecipientList");
    if (!list || !service.listUsers) return;
    list.innerHTML = '<div class="admin-recipient-empty">正在查询用户...</div>';
    service.listUsers(adminContext(context), keyword, 50).then(function (page) {
      var users = (page.items || []).filter(function (user) {
        return user && !user.deletedAt && String(user.status || "ACTIVE") === "ACTIVE";
      });
      list.innerHTML = renderBroadcastRecipientList(context, users);
      bindBroadcastRecipientCheckboxes(root);
      updateBroadcastRecipientSummary(root);
    }).catch(function (error) {
      list.innerHTML = '<div class="admin-recipient-empty">' + esc(context, messageOf(error)) + '</div>';
    });
  }

  function renderBroadcastRecipientList(context, users) {
    return users.length ? users.map(function (user) {
      return broadcastRecipientOption(context, user);
    }).join("") : '<div class="admin-recipient-empty">暂无可接收公告的正常用户。</div>';
  }

  function updateBroadcastRecipientSummary(root) {
    var summary = root.querySelector("#adminBroadcastRecipientSummary");
    if (!summary) return;
    var selected = selectedBroadcastUserIds(root).length;
    summary.textContent = selected > 0 ? "已选择 " + selected + " 个接收用户，将批量发送给这些用户。" :
      "未选择用户时，将发送给所有用户端正常的用户。";
  }

  function runAction(item, context, service, action, id, button, confirmed) {
    var root = context.pageHost && context.pageHost.querySelector(".admin-app");
    var activeTab = currentActiveTab(root);
    var call;
    if (action === "edit-content") {
      fillContentForm(root, button);
      return;
    }
    if (action === "edit-question") {
      fillQuestionForm(root, button);
      return;
    }
    if (action === "edit-assessment-question") {
      fillAssessmentQuestionForm(root, button);
      activateBankTab(root, "assessment");
      activateAssessmentTab(root, button && button.getAttribute("data-scale-id"));
      return;
    }
    var confirmation = destructiveActionConfirmation(action);
    if (confirmation && !confirmed) {
      if (typeof context.showConfirmDialog !== "function") {
        context.showMessage("error", "确认弹窗暂不可用", "请刷新页面后重试，系统未执行删除操作。");
        return;
      }
      context.showConfirmDialog(confirmation.title, confirmation.text, confirmation.confirmText, function () {
        runAction(item, context, service, action, id, button, true);
      }, { danger: true });
      return;
    }
    if (action === "ban-user") call = service.banUser(adminContext(context), id, "管理员在后台限制用户端访问");
    if (action === "unban-user") call = service.unbanUser(adminContext(context), id);
    if (action === "approve-question" || action === "publish-question") call = service.approveQuestion(adminContext(context), id);
    if (action === "reject-question" || action === "hide-question") call = service.rejectQuestion(adminContext(context), id);
    if (action === "delete-question") call = service.deleteQuestion(adminContext(context), id);
    var assessmentScaleId = button && button.getAttribute("data-scale-id");
    if (action === "delete-assessment-question") call = service.deleteAssessmentQuestion(
      adminContext(context), assessmentScaleId, id);
    if (action === "pin-content") call = selectedContentScope === "STUDY" ? service.toggleStudyContentPin(adminContext(context), id) : service.toggleContentPin(adminContext(context), id);
    if (action === "hide-content") call = selectedContentScope === "STUDY" ? service.toggleStudyContentHidden(adminContext(context), id) : service.toggleContentHidden(adminContext(context), id);
    if (action === "delete-content") call = selectedContentScope === "STUDY" ? service.deleteStudyContent(adminContext(context), id) : service.deleteContent(adminContext(context), id);
    if (!call) return;
    if (button) {
      button.disabled = true;
      button.classList.add("loading");
    }
    call.then(function () {
      if (action === "pin-content" || action === "hide-content" || action === "delete-content") {
        if (selectedContentScope === "STUDY") invalidateStudyCenterResources(context); else invalidateEmploymentResources(context);
      }
      return refreshAdminApp(item, context, service, activeTab).then(function () {
        if (action === "delete-assessment-question") {
          var appRoot = context.pageHost && context.pageHost.querySelector(".admin-app");
          activateBankTab(appRoot, "assessment");
          activateAssessmentTab(appRoot, assessmentScaleId);
        }
        context.showMessage("info", "操作完成", "管理后台数据已更新。");
      });
    }).catch(function (error) {
      if (button) {
        button.disabled = false;
        button.classList.remove("loading");
      }
      context.showMessage("error", "操作失败", messageOf(error));
    });
  }

  function destructiveActionConfirmation(action) {
    if (action === "delete-content") {
      return { title: "删除内容", text: "删除后，这条内容将从管理后台和用户端移除，且无法恢复。", confirmText: "确认删除" };
    }
    if (action === "delete-question") {
      return { title: "删除面试题", text: "删除后，这道面试题将从题库中移除，且无法恢复。", confirmText: "确认删除" };
    }
    if (action === "delete-assessment-question") {
      return { title: "删除职业测评题", text: "删除后，当前职业测评题库会立即更新，且无法恢复。", confirmText: "确认删除" };
    }
    return null;
  }

  function invalidateEmploymentResources(context) {
    if (context && typeof context.invalidateEmploymentResources === "function") {
      context.invalidateEmploymentResources();
    }
  }

  function invalidateStudyCenterResources(context) {
    if (context && typeof context.invalidateStudyCenterResources === "function") {
      context.invalidateStudyCenterResources();
    }
  }

  function refreshAdminApp(item, context, service, activeTab) {
    return service.load(adminContext(context)).then(function (data) {
      if (!data.authorized) {
        renderUnauthorized(context);
        return;
      }
      renderAdminApp(item, context, data);
      bindActions(item, context);
      var root = context.pageHost && context.pageHost.querySelector(".admin-app");
      activateTab(root, activeTab || "overview");
    });
  }

  function activateTab(root, key) {
    if (!root) return;
    var title = {
      overview: ["管理总览", "掌握平台状态并处理需要关注的事项"],
      users: ["用户管理", "管理注册用户和账号状态"],
      content: ["内容管理", "管理首页文章、视频和资源展示"],
      questions: ["题库管理", "管理面试题库，查看职业测评题库"],
      broadcast: ["通知公告", "给用户发送站内公告"],
      audit: ["审计日志", "查看管理员操作记录"]
    }[key] || ["管理后台", ""];
    Array.prototype.forEach.call(root.querySelectorAll(".admin-nav-item"), function (button) {
      button.classList.toggle("active", button.getAttribute("data-tab") === key);
    });
    Array.prototype.forEach.call(root.querySelectorAll(".admin-section"), function (section) {
      section.classList.toggle("active", section.getAttribute("data-section") === key);
    });
    root.querySelector("#adminSectionTitle").textContent = title[0];
    root.querySelector("#adminSectionSub").textContent = title[1];
  }

  function currentActiveTab(root) {
    var current = root && root.querySelector(".admin-nav-item.active");
    return current ? current.getAttribute("data-tab") : "overview";
  }

  function readContentForm(root) {
    var type = normalizeContentType(value(root, "adminContentType"));
    return {
      contentId: value(root, "adminContentId") || null,
      type: type,
      title: value(root, "adminContentTitle"),
      summary: value(root, "adminContentSummary"),
      category: categoryFromContentType(type),
      sourceUrl: value(root, "adminContentSourceUrl"),
      pinned: checked(root, "adminContentPinned"),
      hidden: checked(root, "adminContentHidden")
    };
  }

  function fillContentForm(root, button) {
    if (!root || !button) return;
    var type = normalizeContentType(button.getAttribute("data-type") || "");
    setValue(root, "adminContentId", button.getAttribute("data-content-id") || "");
    setContentType(root, type);
    setValue(root, "adminContentTitle", button.getAttribute("data-title") || "");
    setValue(root, "adminContentSummary", button.getAttribute("data-summary") || "");
    setValue(root, "adminContentSourceUrl", button.getAttribute("data-source-url") || "");
    setChecked(root, "adminContentPinned", button.getAttribute("data-pinned") === "true");
    setChecked(root, "adminContentHidden", button.getAttribute("data-hidden") === "true");
    var input = root.querySelector("#adminContentTitle");
    if (input && input.focus) input.focus();
  }

  function bindContentTypeTabs(root) {
    Array.prototype.forEach.call(root.querySelectorAll(".admin-choice-tab[data-content-type]"), function (button) {
      button.addEventListener("click", function () {
        setContentType(root, this.getAttribute("data-content-type"));
      });
    });
  }

  function setContentType(root, type) {
    type = normalizeContentType(type);
    selectedContentType = type;
    setValue(root, "adminContentType", type);
    setValue(root, "adminContentCategory", categoryFromContentType(type));
    Array.prototype.forEach.call(root.querySelectorAll(".admin-choice-tab[data-content-type]"), function (button) {
      button.classList.toggle("active", button.getAttribute("data-content-type") === type);
    });
    Array.prototype.forEach.call(root.querySelectorAll(".admin-content-group-region[data-content-group]"), function (region) {
      region.classList.toggle("active", region.getAttribute("data-content-group") === type);
    });
  }

  function readQuestionForm(root) {
    var reviewStatus = value(root, "adminQuestionReviewStatus") || "PUBLISHED";
    return {
      questionId: value(root, "adminQuestionId") || null,
      position: value(root, "adminQuestionPosition") || "通用岗位",
      difficulty: value(root, "adminQuestionDifficulty") || "NORMAL",
      summary: value(root, "adminQuestionSummary"),
      content: value(root, "adminQuestionContent"),
      answer: value(root, "adminQuestionAnswer"),
      source: "ADMIN",
      reviewStatus: reviewStatus,
      status: reviewStatus === "REJECTED" ? "HIDDEN" : "APPROVED"
    };
  }

  function resetQuestionForm(root) {
    setValue(root, "adminQuestionId", "");
    setValue(root, "adminQuestionPosition", "");
    setValue(root, "adminQuestionDifficulty", "NORMAL");
    setValue(root, "adminQuestionSummary", "");
    setValue(root, "adminQuestionReviewStatus", "PUBLISHED");
    setValue(root, "adminQuestionContent", "");
    setValue(root, "adminQuestionAnswer", "");
  }

  function fillQuestionForm(root, button) {
    if (!root || !button) return;
    setValue(root, "adminQuestionId", button.getAttribute("data-question-id") || "");
    setValue(root, "adminQuestionPosition", button.getAttribute("data-position" ) || "");
    setValue(root, "adminQuestionDifficulty", button.getAttribute("data-difficulty") || "NORMAL");
    setValue(root, "adminQuestionSummary", button.getAttribute("data-summary") || "");
    setValue(root, "adminQuestionReviewStatus", button.getAttribute("data-review-status") || "PUBLISHED");
    setValue(root, "adminQuestionContent", button.getAttribute("data-content") || "");
    setValue(root, "adminQuestionAnswer", button.getAttribute("data-answer") || "");
    var input = root.querySelector("#adminQuestionContent");
    if (input && input.focus) input.focus();
  }

  function readAssessmentQuestionForm(root) {
    var scaleId = value(root, "adminAssessmentScaleId");
    var questionId = value(root, "adminAssessmentQuestionId");
    var sortOrder = value(root, "adminAssessmentSortOrder");
    var dimension = value(root, "adminAssessmentDimension");
    var questionType = value(root, "adminAssessmentQuestionType") || "SINGLE";
    var optionLabels = questionType === "MULTI" ? ["A", "B", "C", "D"] : ["A", "B"];
    return {
      scaleId: scaleId,
      question: {
        questionId: questionId ? Number(questionId) : null,
        scaleId: scaleId ? Number(scaleId) : null,
        questionText: value(root, "adminAssessmentQuestionText"),
        questionType: questionType,
        dimensionCode: dimension,
        sortOrder: sortOrder ? Number(sortOrder) : null,
        published: !!(root.querySelector("#adminAssessmentQuestionPublished") || {}).checked,
        options: optionLabels.map(function (label, index) {
          return {
            optionLabel: label,
            optionText: value(root, "adminAssessmentOption" + label + "Text"),
            dimensionCode: value(root, "adminAssessmentOption" + label + "Dimension") || dimension,
            sortOrder: index
          };
        })
      }
    };
  }

  function assessmentOptionsComplete(question) {
    var options = question && question.options || [];
    var expected = question && question.questionType === "MULTI" ? 4 : 2;
    if (options.length !== expected) return false;
    for (var i = 0; i < options.length; i += 1) {
      if (!first(options[i].optionText, "") || !first(options[i].dimensionCode, "")) return false;
    }
    return true;
  }

  function updateAssessmentQuestionFormHints(root) {
    if (!root) return;
    var type = value(root, "adminAssessmentQuestionType") || "SINGLE";
    var scaleTitle = selectedAssessmentScaleTitle(root);
    var hint = assessmentDimensionHint(scaleTitle);
    var dimension = root.querySelector("#adminAssessmentDimension");
    var hintNode = root.querySelector("#adminAssessmentDimensionHint");
    if (dimension) dimension.setAttribute("placeholder", hint.placeholder);
    if (hintNode) hintNode.textContent = hint.text;
    ["A", "B", "C", "D"].forEach(function (label, index) {
      var optionDimension = root.querySelector("#adminAssessmentOption" + label + "Dimension");
      if (optionDimension) optionDimension.setAttribute("placeholder", hint.optionPlaceholders[index] || hint.optionPlaceholders[0] || "例如：E");
    });
    Array.prototype.forEach.call(root.querySelectorAll(".admin-assessment-extra-option"), function (node) {
      node.hidden = type !== "MULTI";
    });
  }

  function selectedAssessmentScaleTitle(root) {
    var select = root && root.querySelector("#adminAssessmentScaleId");
    if (!select || !select.options || select.selectedIndex < 0) return "";
    return select.options[select.selectedIndex].text || "";
  }

  function assessmentDimensionHint(title) {
    var text = String(title || "").toUpperCase();
    if (text.indexOf("MBTI") >= 0) {
      return {
        placeholder: "例如：EI、SN、TF、JP",
        text: "MBTI 建议填写成对维度：EI、SN、TF、JP；选项维度用 E/I/S/N/T/F/J/P。",
        optionPlaceholders: ["例如：E", "例如：I", "例如：S", "例如：N"]
      };
    }
    if (text.indexOf("RIASEC") >= 0) {
      return {
        placeholder: "例如：R/I、A/S、E/C",
        text: "RIASEC 建议填写兴趣维度组合：R、I、A、S、E、C。",
        optionPlaceholders: ["例如：R", "例如：I", "例如：A", "例如：S"]
      };
    }
    if (text.indexOf("BIG5") >= 0 || text.indexOf("大五") >= 0) {
      return {
        placeholder: "例如：OPEN、CON、EXT、AGR、NEU",
        text: "大五人格可使用 OPEN、CON、EXT、AGR、NEU 等维度。",
        optionPlaceholders: ["例如：OPEN", "例如：CON", "例如：EXT", "例如：AGR"]
      };
    }
    if (text.indexOf("价值") >= 0) {
      return {
        placeholder: "例如：ACH、SEC、AUT、SOC",
        text: "职业价值观可使用 ACH、SEC、AUT、SOC、STA、VAR 等维度。",
        optionPlaceholders: ["例如：ACH", "例如：SEC", "例如：AUT", "例如：SOC"]
      };
    }
    if (text.indexOf("压力") >= 0) {
      return {
        placeholder: "例如：PLAN、SUPPORT、REFRAME",
        text: "压力应对可使用 PROBLEM、EMOTION、PLAN、SUPPORT、REFRAME、AVOID 等维度。",
        optionPlaceholders: ["例如：PROBLEM", "例如：EMOTION", "例如：PLAN", "例如：SUPPORT"]
      };
    }
    return {
      placeholder: "例如：EI、R/I、PLAN",
      text: "请填写该题归属维度；选项维度用于统计画像补全结果。",
      optionPlaceholders: ["例如：E", "例如：I", "例如：PLAN", "例如：SUPPORT"]
    };
  }

  function resetAssessmentQuestionForm(root) {
    setValue(root, "adminAssessmentQuestionId", "");
    setValue(root, "adminAssessmentSortOrder", "");
    setValue(root, "adminAssessmentDimension", "");
    setValue(root, "adminAssessmentQuestionType", "SINGLE");
    setValue(root, "adminAssessmentQuestionText", "");
    setValue(root, "adminAssessmentOptionAText", "");
    setValue(root, "adminAssessmentOptionADimension", "");
    setValue(root, "adminAssessmentOptionBText", "");
    setValue(root, "adminAssessmentOptionBDimension", "");
    setValue(root, "adminAssessmentOptionCText", "");
    setValue(root, "adminAssessmentOptionCDimension", "");
    setValue(root, "adminAssessmentOptionDText", "");
    setValue(root, "adminAssessmentOptionDDimension", "");
    setChecked(root, "adminAssessmentQuestionPublished", true);
  }

  function fillAssessmentQuestionForm(root, button) {
    if (!root || !button) return;
    setValue(root, "adminAssessmentScaleId", button.getAttribute("data-scale-id") || "");
    setValue(root, "adminAssessmentQuestionId", button.getAttribute("data-question-id") || "");
    setValue(root, "adminAssessmentSortOrder", button.getAttribute("data-sort-order") || "");
    setValue(root, "adminAssessmentDimension", button.getAttribute("data-dimension") || "");
    setValue(root, "adminAssessmentQuestionType", button.getAttribute("data-question-type") || "SINGLE");
    setValue(root, "adminAssessmentQuestionText", button.getAttribute("data-question-text") || "");
    setValue(root, "adminAssessmentOptionAText", button.getAttribute("data-option-a-text") || "");
    setValue(root, "adminAssessmentOptionADimension", button.getAttribute("data-option-a-dimension") || "");
    setValue(root, "adminAssessmentOptionBText", button.getAttribute("data-option-b-text") || "");
    setValue(root, "adminAssessmentOptionBDimension", button.getAttribute("data-option-b-dimension") || "");
    setValue(root, "adminAssessmentOptionCText", button.getAttribute("data-option-c-text") || "");
    setValue(root, "adminAssessmentOptionCDimension", button.getAttribute("data-option-c-dimension") || "");
    setValue(root, "adminAssessmentOptionDText", button.getAttribute("data-option-d-text") || "");
    setValue(root, "adminAssessmentOptionDDimension", button.getAttribute("data-option-d-dimension") || "");
    setChecked(root, "adminAssessmentQuestionPublished", button.getAttribute("data-published") !== "false");
    updateAssessmentQuestionFormHints(root);
    var input = root.querySelector("#adminAssessmentQuestionText");
    if (input && input.focus) input.focus();
  }

  function section(key, active, html) {
    return '<div class="admin-section' + (active ? " active" : "") + '" data-section="' + key + '">' + html + '</div>';
  }

  function panel(context, title, subtitle, body) {
    return '<section class="admin-panel"><div class="admin-panel-head"><div><h3>' + esc(context, title) +
      '</h3><p>' + esc(context, subtitle) + '</p></div></div>' + body + '</section>';
  }

  function table(headers, rows) {
    return '<div class="admin-table-wrap"><table class="admin-table"><thead><tr>' +
      headers.map(function (head) { return '<th>' + head + '</th>'; }).join("") +
      '</tr></thead><tbody>' + rows.map(function (row) {
        return '<tr>' + row.map(function (cell) { return '<td>' + cell + '</td>'; }).join("") + '</tr>';
      }).join("") + '</tbody></table></div>';
  }

  function kpi(context, label, value, hint) {
    return '<article class="admin-kpi"><span>' + esc(context, label) + '</span><strong>' + esc(context, first(value, 0)) +
      '</strong><small>' + esc(context, hint) + '</small></article>';
  }

  function countWhere(items, predicate) {
    if (!items || !items.length) return 0;
    return items.reduce(function (total, item) {
      return total + (predicate(item) ? 1 : 0);
    }, 0);
  }

  function overviewAlerts(content, questions, banks) {
    var alerts = [];
    var visibleContent = countWhere(content, function (item) { return !item.hidden; });
    var missingLinks = countWhere(content, function (item) { return !item.hidden && !first(item.sourceUrl, ""); });
    var publishedQuestions = countWhere(questions, function (question) {
      return question.reviewStatus === "PUBLISHED" || question.status === "APPROVED";
    });
    var emptyBanks = countWhere(banks, function (bank) {
      return Number(first(bank.poolQuestionCount, bank.questions && bank.questions.length, 0)) === 0;
    });
    if (!visibleContent) {
      alerts.push({ type: "warn", title: "资源页暂无可见内容", text: "请在内容管理中发布或恢复展示内容。" });
    }
    if (missingLinks) {
      alerts.push({ type: "warn", title: "内容链接不完整", text: missingLinks + " 条展示内容缺少外部链接。" });
    }
    if (!publishedQuestions) {
      alerts.push({ type: "warn", title: "面试题库暂无可用题", text: "请发布至少一批面试题，方便用户练习。" });
    }
    if (emptyBanks) {
      alerts.push({ type: "warn", title: "测评题库不完整", text: emptyBanks + " 个测评类型还没有题目。" });
    }
    return alerts;
  }

  function healthItem(context, label, value, total, status) {
    var safeValue = Number(first(value, 0));
    var safeTotal = Number(first(total, 0));
    var percent = safeTotal > 0 ? Math.min(100, Math.round((safeValue / safeTotal) * 100)) : (safeValue > 0 ? 100 : 0);
    var copy = safeTotal ? safeValue + " / " + safeTotal : String(safeValue);
    return '<div class="admin-health-item ' + escAttr(context, status || "") + '"><div><strong>' +
      esc(context, label) + '</strong><span>' + esc(context, copy) + '</span></div>' +
      '<div class="admin-health-bar"><i style="width:' + percent + '%"></i></div></div>';
  }

  function identityCell(context, title, detail) {
    return '<strong class="admin-cell-title">' + esc(context, title) + '</strong><small>' + esc(context, detail) + '</small>';
  }

  function twoLine(context, title, detail) {
    return '<strong class="admin-cell-title">' + esc(context, first(title, "-")) + '</strong><small>' + esc(context, detail) + '</small>';
  }

  function linkCell(context, url) {
    if (!url) return "-";
    return '<small class="admin-link-cell">' + esc(context, url) + '</small>';
  }

  function actionButton(context, action, id, label, danger, attrs) {
    return '<button type="button" class="admin-action' + (danger ? " danger" : "") + '" data-action="' + escAttr(context, action) +
      '" data-id="' + escAttr(context, id) + '"' + (attrs || "") + '>' + esc(context, label) + '</button>';
  }

  function editContentButton(context, item) {
    var attrs = ' data-content-id="' + escAttr(context, item.contentId) + '"' +
      ' data-type="' + escAttr(context, item.type || "ARTICLE") + '"' +
      ' data-title="' + escAttr(context, item.title || "") + '"' +
      ' data-summary="' + escAttr(context, item.summary || "") + '"' +
      ' data-category="' + escAttr(context, item.category || "") + '"' +
      ' data-source-url="' + escAttr(context, item.sourceUrl || "") + '"' +
      ' data-pinned="' + (item.pinned ? "true" : "false") + '"' +
      ' data-hidden="' + (item.hidden ? "true" : "false") + '"';
    return actionButton(context, "edit-content", item.contentId, "编辑", false, attrs);
  }

  function editQuestionButton(context, item) {
    var attrs = ' data-question-id="' + escAttr(context, item.questionId) + '"' +
      ' data-position="' + escAttr(context, item.position || "") + '"' +
      ' data-difficulty="' + escAttr(context, item.difficulty || "NORMAL") + '"' +
      ' data-summary="' + escAttr(context, item.summary || "") + '"' +
      ' data-review-status="' + escAttr(context, item.reviewStatus || "PUBLISHED") + '"' +
      ' data-content="' + escAttr(context, item.content || "") + '"' +
      ' data-answer="' + escAttr(context, item.answer || "") + '"';
    return actionButton(context, "edit-question", item.questionId, "编辑", false, attrs);
  }

  function editAssessmentQuestionButton(context, bank, question) {
    var options = question.options || [];
    var firstOption = options[0] || {};
    var secondOption = options[1] || {};
    var thirdOption = options[2] || {};
    var fourthOption = options[3] || {};
    var attrs = ' data-scale-id="' + escAttr(context, bank.scaleId) + '"' +
      ' data-question-id="' + escAttr(context, question.questionId) + '"' +
      ' data-sort-order="' + escAttr(context, question.sortOrder || "") + '"' +
      ' data-dimension="' + escAttr(context, question.dimensionCode || "") + '"' +
      ' data-question-type="' + escAttr(context, question.questionType || "SINGLE") + '"' +
      ' data-published="' + (question.published === false ? "false" : "true") + '"' +
      ' data-question-text="' + escAttr(context, question.questionText || "") + '"' +
      ' data-option-a-text="' + escAttr(context, firstOption.optionText || "") + '"' +
      ' data-option-a-dimension="' + escAttr(context, firstOption.dimensionCode || "") + '"' +
      ' data-option-b-text="' + escAttr(context, secondOption.optionText || "") + '"' +
      ' data-option-b-dimension="' + escAttr(context, secondOption.dimensionCode || "") + '"' +
      ' data-option-c-text="' + escAttr(context, thirdOption.optionText || "") + '"' +
      ' data-option-c-dimension="' + escAttr(context, thirdOption.dimensionCode || "") + '"' +
      ' data-option-d-text="' + escAttr(context, fourthOption.optionText || "") + '"' +
      ' data-option-d-dimension="' + escAttr(context, fourthOption.dimensionCode || "") + '"';
    return actionButton(context, "edit-assessment-question", question.questionId, "编辑", false, attrs);
  }

  function questionTypeText(type) {
    return type === "MULTI" ? "多选" : "单选";
  }

  function empty(title, text, context) {
    return '<div class="admin-empty"><h3>' + esc(context, title) + '</h3><p>' + esc(context, text) + '</p></div>';
  }

  function badge(context, text, type) {
    return '<span class="admin-badge ' + (type || "") + '">' + esc(context, text) + '</span>';
  }

  function contentType(type) {
    if (type === "VIDEO") return "相关视频";
    if (type === "ARTICLE") return "精选文章";
    if (type === "RESOURCE") return "公共服务";
    return type || "内容";
  }

  function normalizeContentType(type) {
    type = String(type || "").toUpperCase();
    if (type === "VIDEO") return "VIDEO";
    if (type === "ARTICLE") return "ARTICLE";
    return "RESOURCE";
  }

  function categoryFromContentType(type) {
    type = normalizeContentType(type);
    if (type === "VIDEO") return "相关视频";
    if (type === "ARTICLE") return "精选文章";
    return "公共服务";
  }

  function contentGroupLabel(type, category) {
    var allowed = ["公共服务", "精选文章", "相关视频"];
    if (allowed.indexOf(category) >= 0) return category;
    return categoryFromContentType(type);
  }

  function contentGroupByType(type) {
    type = normalizeContentType(type);
    for (var i = 0; i < CONTENT_GROUPS.length; i += 1) {
      if (CONTENT_GROUPS[i].type === type) return CONTENT_GROUPS[i];
    }
    return null;
  }

  function sourceText(source) {
    if (source === "AI") return "AI 生成";
    if (source === "USER") return "用户贡献";
    return source || "系统";
  }

  function reviewText(status) {
    if (status === "PUBLISHED") return "已发布";
    if (status === "REJECTED") return "已隐藏";
    if (status === "PENDING_REVIEW") return "待审核";
    return status || "待审核";
  }

  function difficultyText(difficulty) {
    if (difficulty === "EASY") return "入门";
    if (difficulty === "HARD") return "进阶";
    if (difficulty === "NORMAL") return "常规";
    return difficulty || "常规";
  }

  function actionText(action) {
    var map = {
      BAN_USER: "禁用用户端",
      UNBAN_USER: "恢复用户端",
      BROADCAST: "发送公告",
      APPROVE_QUESTION: "通过题目",
      REJECT_QUESTION: "驳回题目",
      UPDATE_QUESTION: "更新题目",
      DELETE_QUESTION: "删除题目",
      SAVE_CONTENT: "保存内容",
      DELETE_CONTENT: "删除内容",
      TOGGLE_CONTENT: "切换内容状态"
    };
    return map[action] || action || "-";
  }

  function broadcastRecipientOption(context, user) {
    var name = first(user.nickname, user.displayName, user.userName, user.userId);
    var detail = first(user.userId, "");
    var search = [name, detail].join(" ");
    return '<label class="admin-recipient-option" data-search="' + escAttr(context, search) + '">' +
      '<input type="checkbox" class="admin-broadcast-user" value="' + escAttr(context, user.userId) + '">' +
      '<span><strong>' + esc(context, name) + '</strong><small>' + esc(context, first(detail, "无补充信息")) +
      '</small></span></label>';
  }

  function value(root, id) {
    var el = root.querySelector("#" + id);
    return el ? String(el.value || "").trim() : "";
  }

  function setValue(root, id, text) {
    var el = root && root.querySelector("#" + id);
    if (el) el.value = text == null ? "" : String(text);
  }

  function checked(root, id) {
    var el = root.querySelector("#" + id);
    return !!(el && el.checked);
  }

  function setChecked(root, id, value) {
    var el = root && root.querySelector("#" + id);
    if (el) el.checked = !!value;
  }

  function first() {
    for (var i = 0; i < arguments.length; i += 1) {
      var value = arguments[i];
      if (value !== null && value !== undefined && String(value).length > 0) {
        return value;
      }
    }
    return "";
  }

  function esc(context, value) {
    return context.escapeHtml ? context.escapeHtml(value) : String(value == null ? "" : value);
  }

  function escAttr(context, value) {
    return esc(context, value).replace(/"/g, "&quot;");
  }

  function messageOf(error) {
    return error && error.message ? error.message : "管理后台请求失败，请稍后重试。";
  }

  function isCurrentAdminUser(data, user) {
    if (!user) return false;
    var userId = first(user.userId, "");
    var whoami = data && data.whoami || {};
    return same(userId, data && data.adminId) || same(userId, whoami.userId);
  }

  function same(left, right) {
    return String(left || "").trim() !== "" && String(left || "").trim() === String(right || "").trim();
  }

}(window, document));
