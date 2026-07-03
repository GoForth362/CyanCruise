(function (window, document) {
  "use strict";

  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};

  var NAV_ITEMS = [
    ["overview", "数智大屏"],
    ["users", "用户管理"],
    ["content", "内容管理"],
    ["questions", "题库管理"],
    ["broadcast", "通知公告"],
    ["audit", "审计日志"]
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
      renderStandaloneState(context, "无管理员权限", "当前账号可以继续使用用户端，但不能进入管理后台。");
      return;
    }
    if (!adminService || typeof adminService.load !== "function") {
      renderStandaloneState(context, "管理服务未加载", "前端管理服务暂不可用，请刷新页面后重试。");
      return;
    }

    renderStandaloneState(context, "正在加载管理后台", "正在读取用户、内容、题库和操作记录。");
    adminService.load(adminContext(context)).then(function (data) {
      if (!data.authorized) {
        renderStandaloneState(context, "无管理员权限", data.message || "当前账号没有管理后台权限。");
        return;
      }
      renderAdminApp(item, context, data);
      bindActions(item, context);
    }).catch(function (error) {
      renderStandaloneState(context, "管理后台暂不可用", messageOf(error));
    });
  });

  function setAdminMode(on) {
    if (document.body) {
      document.body.classList.toggle("admin-mode", !!on);
    }
  }

  function renderStandaloneState(context, title, text) {
    if (context.pageHost) {
      context.pageHost.innerHTML = '<section class="admin-app admin-state-only">' +
        '<main class="admin-main"><section class="admin-panel admin-state-panel">' +
        '<h2>' + esc(context, title) + '</h2><p>' + esc(context, text) + '</p>' +
        '</section></main></section>';
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
      '<div class="admin-brand"><strong>CyanCruise</strong><span>数智管理后台</span></div>' +
      '<nav class="admin-side-nav" role="tablist">' +
      NAV_ITEMS.map(function (item, index) {
        return '<button type="button" class="admin-nav-item' + (index === 0 ? " active" : "") +
          '" data-tab="' + item[0] + '"><span class="admin-nav-mark"></span>' + esc(context, item[1]) + '</button>';
      }).join("") +
      '</nav></aside>';
  }

  function renderTopbar(context, data) {
    var name = first(data.whoami && data.whoami.userName, data.whoami && data.whoami.displayName, data.adminId, "admin");
    return '<header class="admin-topbar">' +
      '<div><h2 id="adminSectionTitle">数智大屏</h2><p id="adminSectionSub">查看平台关键指标和数据覆盖情况</p></div>' +
      '<div class="admin-account"><span>' + esc(context, name) + '</span></div>' +
      '</header>';
  }

  function renderOverviewLegacy(data, context) {
    var dashboard = data.dashboard || {};
    var analytics = data.analytics || {};
    var events = analytics.eventBreakdown30d || {};
    return '<div class="admin-kpi-grid">' +
      kpi(context, "用户数量", analytics.totalUsers, "CyanCruise 已纳入管理的用户") +
      kpi(context, "面试次数", first(analytics.totalInterviews, dashboard.interviewCount), "模拟面试累计") +
      kpi(context, "测评次数", analytics.totalAssessments, "能力测评累计") +
      kpi(context, "内容数量", events.CONTENT, "后台内容条目") +
      '</div>' +
      '<section class="admin-panel"><div class="admin-panel-head"><div><h3>数据覆盖</h3><p>报告、事件和后台采集状态</p></div></div>' +
      '<div class="admin-summary-row">' +
      '<span>报告数量 <strong>' + esc(context, first(dashboard.reportCount, 0)) + '</strong></span>' +
      '<span>跳过报告 <strong>' + esc(context, first(dashboard.skippedReportCount, 0)) + '</strong></span>' +
      '<span>近 30 天事件会按后端返回原样展示</span>' +
      '</div></section>';
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
      return total + Number(first(bank.questionCount, bank.questions && bank.questions.length, 0));
    }, 0);
    var alerts = overviewAlerts(content, questions, banks);
    return '<div class="admin-kpi-grid">' +
      kpi(context, "用户数量", analytics.totalUsers, bannedUsers ? bannedUsers + " 个用户端已禁用" : "全部用户端正常") +
      kpi(context, "面试次数", first(analytics.totalInterviews, dashboard.interviewCount), "模拟面试累计") +
      kpi(context, "测评次数", analytics.totalAssessments, "能力测评累计") +
      kpi(context, "内容数量", content.length, visibleContent + " 条展示中，" + hiddenContent + " 条隐藏") +
      '</div>' +
      '<div class="admin-overview-grid">' +
      '<section class="admin-panel admin-overview-card"><div class="admin-panel-head"><div><h3>运营状态</h3><p>用户端、报告和后台采集情况</p></div></div>' +
      '<div class="admin-health-list">' +
      healthItem(context, "用户端正常", users.length - bannedUsers, first(analytics.totalUsers, users.length), "ok") +
      healthItem(context, "报告数量", first(dashboard.reportCount, 0), first(analytics.totalInterviews, dashboard.interviewCount, 0), "ok") +
      healthItem(context, "跳过报告", first(dashboard.skippedReportCount, 0), first(analytics.totalInterviews, dashboard.interviewCount, 0), Number(first(dashboard.skippedReportCount, 0)) > 0 ? "warn" : "ok") +
      '</div></section>' +
      '<section class="admin-panel admin-overview-card"><div class="admin-panel-head"><div><h3>内容状态</h3><p>资源页内容是否足够、是否可见</p></div></div>' +
      '<div class="admin-health-list">' +
      healthItem(context, "展示中", visibleContent, content.length, visibleContent > 0 ? "ok" : "warn") +
      healthItem(context, "置顶内容", pinnedContent, content.length, pinnedContent > 0 ? "ok" : "warn") +
      healthItem(context, "隐藏内容", hiddenContent, content.length, hiddenContent > 0 ? "warn" : "ok") +
      '</div></section>' +
      '<section class="admin-panel admin-overview-card"><div class="admin-panel-head"><div><h3>题库覆盖</h3><p>面试题和职业测评题库维护情况</p></div></div>' +
      '<div class="admin-health-list">' +
      healthItem(context, "已发布面试题", publishedQuestions, questions.length, publishedQuestions > 0 ? "ok" : "warn") +
      healthItem(context, "隐藏面试题", hiddenQuestions, questions.length, hiddenQuestions > 0 ? "warn" : "ok") +
      healthItem(context, "测评题目", assessmentQuestionCount, banks.length, assessmentQuestionCount > 0 ? "ok" : "warn") +
      '</div></section>' +
      '<section class="admin-panel admin-overview-card"><div class="admin-panel-head"><div><h3>待关注事项</h3><p>需要管理员尽快检查的地方</p></div></div>' +
      '<div class="admin-alert-list">' + (alerts.length ? alerts.map(function (alert) {
        return '<div class="admin-alert-item ' + escAttr(context, alert.type) + '"><strong>' + esc(context, alert.title) +
          '</strong><span>' + esc(context, alert.text) + '</span></div>';
      }).join("") : '<div class="admin-alert-item ok"><strong>暂无明显异常</strong><span>当前用户、内容和题库数据可以支撑基础使用。</span></div>') +
      '</div></section>' +
      '</div>' +
      '<section class="admin-panel"><div class="admin-panel-head"><div><h3>最近操作</h3><p>管理员最近的关键动作</p></div></div>' +
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

  function renderUsers(data, context) {
    var users = data.users && data.users.items || [];
    return panel(context, "用户管理", "管理注册用户和账号状态", '<div class="admin-panel-tools"><input type="search" placeholder="搜索用户名称..."></div>' +
      (users.length ? table([
        "用户", "学校/专业", "状态", "平台组织ID", "操作"
      ], users.map(function (user) {
        var status = String(user.status || "ACTIVE");
        var banned = status === "BANNED";
        var currentAdmin = isCurrentAdminUser(data, user);
        return [
          identityCell(context, first(user.nickname, user.displayName, user.userName, user.userId), user.userId) +
            (currentAdmin ? '<small>当前管理员，管理权限请在金蝶安全管理调整</small>' : ""),
          twoLine(context, first(user.school, "-"), first(user.major, "")),
          badge(context, banned ? "用户端禁用" : "用户端正常", banned ? "warn" : "ok"),
          esc(context, first(user.orgId, "-")),
          actionButton(context, banned ? "unban-user" : "ban-user", user.userId, banned ? "恢复用户端" : "禁用用户端")
        ];
      })) : empty("暂无用户数据", "接入正式用户存储后，这里会显示用户状态和最近面试情况。", context)));
  }

  function renderContent(data, context) {
    var content = data.content || [];
    return panel(context, "内容管理", "管理首页文章、视频和资源展示",
      contentForm(context) +
      (content.length ? table(["标题", "展示分组", "展示状态", "外部链接", "操作"], content.map(function (item) {
        return [
          twoLine(context, item.title, first(item.summary, item.contentId)),
          esc(context, contentGroupLabel(item.type, item.category)),
          badge(context, item.pinned ? "置顶" : "未置顶", item.pinned ? "ok" : "") + " " +
            badge(context, item.hidden ? "已隐藏" : "展示中", item.hidden ? "warn" : "ok"),
          linkCell(context, item.sourceUrl),
          editContentButton(context, item) +
            actionButton(context, "pin-content", item.contentId, item.pinned ? "取消置顶" : "置顶") +
            actionButton(context, "hide-content", item.contentId, item.hidden ? "恢复展示" : "隐藏") +
            actionButton(context, "delete-content", item.contentId, "删除", true)
        ];
      })) : empty("暂无内容数据", "可以在上方新增文章、视频或资源入口；保存后会同步进入用户端资源页。", context)));
  }

  function contentForm(context) {
    return '<div class="admin-content-form">' +
      '<input type="hidden" id="adminContentId">' +
      '<input type="hidden" id="adminContentType" value="RESOURCE">' +
      '<input type="hidden" id="adminContentCategory" value="公共服务">' +
      '<div class="admin-form-title"><strong>编辑内容</strong><span>保存后，未隐藏的内容会展示在用户端资源页。</span></div>' +
      '<div class="admin-form-field wide"><span>内容类型</span><div class="admin-choice-tabs" role="tablist">' +
      CONTENT_GROUPS.map(function (group, index) {
        return '<button type="button" class="admin-choice-tab' + (index === 0 ? " active" : "") +
          '" data-content-type="' + escAttr(context, group.type) + '" data-content-category="' +
          escAttr(context, group.category) + '">' + esc(context, group.label) + '</button>';
      }).join("") + '</div></div>' +
      '<div class="admin-form-grid">' +
      '<label><span>标题</span><input id="adminContentTitle" placeholder="例如：后端简历优化指南"></label>' +
      '<label><span>外部链接</span><input id="adminContentSourceUrl" placeholder="https://..."></label>' +
      '<label><span>封面链接</span><input id="adminContentImageUrl" placeholder="可选，https://..."></label>' +
      '<label class="wide"><span>摘要</span><textarea id="adminContentSummary" rows="3" placeholder="写给用户看的简介"></textarea></label>' +
      '</div>' +
      '<div class="admin-form-row">' +
      '<label class="admin-check"><input type="checkbox" id="adminContentPinned"><span>置顶展示</span></label>' +
      '<label class="admin-check"><input type="checkbox" id="adminContentHidden"><span>暂不展示给用户</span></label>' +
      '</div>' +
      '<div class="admin-form-actions">' +
      '<button type="button" class="admin-primary" id="adminContentSaveButton">保存内容</button>' +
      '<button type="button" class="admin-secondary" id="adminContentResetButton">新建内容</button>' +
      '</div>' +
      '</div>';
  }

  function renderQuestions(data, context) {
    var questions = data.questions || [];
    var banks = data.assessmentQuestionBanks || [];
    return panel(context, "题库管理", "管理面试题库，查看职业测评题库",
      '<div class="admin-bank-tabs" role="tablist">' +
      '<button type="button" class="admin-bank-tab active" data-bank-tab="interview">面试题库</button>' +
      '<button type="button" class="admin-bank-tab" data-bank-tab="assessment">职业测评题库</button>' +
      '</div>' +
      '<div class="admin-bank-section active" data-bank-section="interview">' +
      questionForm(context) +
      (questions.length ? table(["题目", "岗位/难度", "来源", "状态", "操作"], questions.map(function (question) {
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
      })) : empty("暂无面试题", "可以在上方新增面试题；发布后的题目可用于后续面试练习。", context)) +
      '</div>' +
      '<div class="admin-bank-section" data-bank-section="assessment">' +
      assessmentQuestionForm(data, context) +
      renderAssessmentBanks(banks, context) +
      '</div>');
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
      '<option value="PUBLISHED">发布</option><option value="PENDING_REVIEW">待审核</option><option value="REJECTED">隐藏</option>' +
      '</select></label>' +
      '<label class="wide"><span>题目正文</span><textarea id="adminQuestionContent" rows="3" placeholder="写清楚候选人需要回答的问题"></textarea></label>' +
      '<label class="wide"><span>参考答案</span><textarea id="adminQuestionAnswer" rows="4" placeholder="可选，写给练习或后续解析使用"></textarea></label>' +
      '</div>' +
      '<div class="admin-form-actions">' +
      '<button type="button" class="admin-primary" id="adminQuestionSaveButton">保存题目</button>' +
      '<button type="button" class="admin-secondary" id="adminQuestionResetButton">新建题目</button>' +
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
      '<label><span>维度</span><input id="adminAssessmentDimension" placeholder="例如：EI、R/I、PLAN"></label>' +
      '<label><span>题型</span><select id="adminAssessmentQuestionType"><option value="SINGLE">单选</option></select></label>' +
      '<label class="wide"><span>题目</span><textarea id="adminAssessmentQuestionText" rows="3" placeholder="请输入测评题目"></textarea></label>' +
      '<label><span>选项 A</span><input id="adminAssessmentOptionAText" placeholder="选项 A 文案"></label>' +
      '<label><span>A 维度</span><input id="adminAssessmentOptionADimension" placeholder="例如：E"></label>' +
      '<label><span>选项 B</span><input id="adminAssessmentOptionBText" placeholder="选项 B 文案"></label>' +
      '<label><span>B 维度</span><input id="adminAssessmentOptionBDimension" placeholder="例如：I"></label>' +
      '</div>' +
      '<div class="admin-form-actions">' +
      '<button type="button" class="admin-primary" id="adminAssessmentQuestionSaveButton">保存测评题</button>' +
      '<button type="button" class="admin-secondary" id="adminAssessmentQuestionResetButton">新建测评题</button>' +
      '</div>' +
      '</div>';
  }

  function renderAssessmentBanks(banks, context) {
    if (!banks.length) {
      return empty("暂无职业测评题库", "当前没有读取到职业测评量表。", context);
    }
    return '<div class="admin-assessment-note">职业测评题库当前保存在应用运行期题库中；后续接入业务对象后可跨重启保留。</div>' +
      '<div class="admin-assessment-type-tabs" role="tablist">' + banks.map(function (bank, index) {
        var questions = bank.questions || [];
        return '<button type="button" class="admin-assessment-tab' + (index === 0 ? " active" : "") +
          '" data-scale-id="' + escAttr(context, bank.scaleId) + '">' +
          '<strong>' + esc(context, bank.title || "职业测评") + '</strong><span>' +
          esc(context, first(bank.questionCount, questions.length, 0)) + ' 题</span></button>';
      }).join("") + '</div>' +
      '<div class="admin-assessment-grid">' + banks.map(function (bank, index) {
        var questions = bank.questions || [];
        return '<div class="admin-assessment-panel' + (index === 0 ? " active" : "") +
          '" data-scale-id="' + escAttr(context, bank.scaleId) + '"><article class="admin-assessment-card">' +
          '<header><div><h4>' + esc(context, bank.title || "职业测评") + '</h4><p>' +
          esc(context, first(bank.description, "用于职业倾向和能力画像分析")) + '</p></div>' +
          '<span>' + esc(context, first(bank.questionCount, questions.length, 0)) + ' 题</span></header>' +
          '<div class="admin-assessment-meta">版本 ' + esc(context, first(bank.version, "-")) + '</div>' +
          '<div class="admin-assessment-questions">' + questions.map(function (question) {
            return '<div class="admin-assessment-question"><strong>' + esc(context, first(question.sortOrder, "")) +
              '. ' + esc(context, question.questionText) + '</strong><small>' +
              esc(context, first(question.dimensionCode, "未设置维度")) + '</small>' +
              '<p>' + (question.options || []).map(function (option) {
                return esc(context, option.optionLabel) + ". " + esc(context, option.optionText);
              }).join(" / ") + '</p><div class="admin-assessment-actions">' +
              editAssessmentQuestionButton(context, bank, question) +
              actionButton(context, "delete-assessment-question", question.questionId, "删除", true,
                ' data-scale-id="' + escAttr(context, bank.scaleId) + '"') +
              '</div></div>';
          }).join("") + '</div></article></div>';
      }).join("") + '</div>';
  }

  function renderBroadcast(data, context) {
    var users = (data.users && data.users.items || []).filter(function (user) {
      return user && !user.deletedAt && String(user.status || "ACTIVE") === "ACTIVE";
    });
    return panel(context, "通知公告", "给用户发送站内公告", '<div class="admin-broadcast-form">' +
      '<div class="admin-form-title"><strong>发送公告</strong><span>可搜索并勾选多个接收用户；未选择时发送给所有用户端正常的用户。</span></div>' +
      '<div class="admin-form-grid">' +
      '<label class="wide"><span>接收用户</span><input id="adminBroadcastUserSearch" type="search" placeholder="搜索用户姓名、昵称或用户ID"></label>' +
      '<label><span>标题</span><input id="adminBroadcastTitle" placeholder="例如：面试练习服务维护通知"></label>' +
      '<label><span>链接</span><input id="adminBroadcastLink" placeholder="可选，例如 admin-console"></label>' +
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
    Array.prototype.forEach.call(root.querySelectorAll(".admin-action"), function (button) {
      button.addEventListener("click", function () {
        runAction(item, context, service, this.getAttribute("data-action"), this.getAttribute("data-id"), this);
      });
    });
    bindContentForm(item, context, service, root);
    bindQuestionForm(item, context, service, root);
    bindAssessmentQuestionForm(item, context, service, root);
    bindQuestionBankTabs(root);
    bindAssessmentTabs(root);
    bindBroadcast(item, context, service, root);
    bindAuditPager(context, service, root);
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
    var resetButton = root.querySelector("#adminQuestionResetButton");
    if (resetButton) {
      resetButton.addEventListener("click", function () {
        resetQuestionForm(root);
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
    var resetButton = root.querySelector("#adminAssessmentQuestionResetButton");
    if (resetButton) {
      resetButton.addEventListener("click", function () {
        resetAssessmentQuestionForm(root);
      });
    }
    if (saveButton) {
      saveButton.addEventListener("click", function () {
        var payload = readAssessmentQuestionForm(root);
        if (!payload.scaleId || !payload.question.questionText) {
          context.showMessage("error", "保存失败", "请先选择量表并填写测评题目。");
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

  function bindContentForm(item, context, service, root) {
    var saveButton = root.querySelector("#adminContentSaveButton");
    var resetButton = root.querySelector("#adminContentResetButton");
    bindContentTypeTabs(root);
    if (resetButton) {
      resetButton.addEventListener("click", function () {
        resetContentForm(root);
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
        service.saveContent(adminContext(context), payload).then(function () {
          return refreshAdminApp(item, context, service, "content").then(function () {
            context.showMessage("info", "内容已保存", "用户端资源页会展示未隐藏的内容。");
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
        content: value(root, "adminBroadcastContent"),
        link: value(root, "adminBroadcastLink")
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

  function runAction(item, context, service, action, id, button) {
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
    if (action === "delete-content" && !window.confirm("确定删除这条内容吗？删除后用户端不会再展示。")) {
      return;
    }
    if (action === "delete-question" && !window.confirm("确定删除这道面试题吗？删除后无法在题库中恢复。")) {
      return;
    }
    if (action === "delete-assessment-question" && !window.confirm("确定删除这道职业测评题吗？删除后当前测评题库会立即更新。")) {
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
    if (action === "pin-content") call = service.toggleContentPin(adminContext(context), id);
    if (action === "hide-content") call = service.toggleContentHidden(adminContext(context), id);
    if (action === "delete-content") call = service.deleteContent(adminContext(context), id);
    if (!call) return;
    if (button) {
      button.disabled = true;
      button.classList.add("loading");
    }
    call.then(function () {
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

  function refreshAdminApp(item, context, service, activeTab) {
    return service.load(adminContext(context)).then(function (data) {
      if (!data.authorized) {
        renderStandaloneState(context, "无管理员权限", data.message || "当前账号没有管理后台权限。");
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
      overview: ["数智大屏", "查看平台关键指标和数据覆盖情况"],
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
      imageUrl: value(root, "adminContentImageUrl"),
      pinned: checked(root, "adminContentPinned"),
      hidden: checked(root, "adminContentHidden")
    };
  }

  function resetContentForm(root) {
    setValue(root, "adminContentId", "");
    setContentType(root, "RESOURCE");
    setValue(root, "adminContentTitle", "");
    setValue(root, "adminContentSummary", "");
    setValue(root, "adminContentSourceUrl", "");
    setValue(root, "adminContentImageUrl", "");
    setChecked(root, "adminContentPinned", false);
    setChecked(root, "adminContentHidden", false);
  }

  function fillContentForm(root, button) {
    if (!root || !button) return;
    var type = normalizeContentType(button.getAttribute("data-type") || "");
    setValue(root, "adminContentId", button.getAttribute("data-content-id") || "");
    setContentType(root, type);
    setValue(root, "adminContentTitle", button.getAttribute("data-title") || "");
    setValue(root, "adminContentSummary", button.getAttribute("data-summary") || "");
    setValue(root, "adminContentSourceUrl", button.getAttribute("data-source-url") || "");
    setValue(root, "adminContentImageUrl", button.getAttribute("data-image-url") || "");
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
    setValue(root, "adminContentType", type);
    setValue(root, "adminContentCategory", categoryFromContentType(type));
    Array.prototype.forEach.call(root.querySelectorAll(".admin-choice-tab[data-content-type]"), function (button) {
      button.classList.toggle("active", button.getAttribute("data-content-type") === type);
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
    return {
      scaleId: scaleId,
      question: {
        questionId: questionId ? Number(questionId) : null,
        scaleId: scaleId ? Number(scaleId) : null,
        questionText: value(root, "adminAssessmentQuestionText"),
        questionType: value(root, "adminAssessmentQuestionType") || "SINGLE",
        dimensionCode: dimension,
        sortOrder: sortOrder ? Number(sortOrder) : null,
        options: [
          {
            optionLabel: "A",
            optionText: value(root, "adminAssessmentOptionAText"),
            dimensionCode: value(root, "adminAssessmentOptionADimension") || dimension,
            sortOrder: 0
          },
          {
            optionLabel: "B",
            optionText: value(root, "adminAssessmentOptionBText"),
            dimensionCode: value(root, "adminAssessmentOptionBDimension") || dimension,
            sortOrder: 1
          }
        ]
      }
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
      return Number(first(bank.questionCount, bank.questions && bank.questions.length, 0)) === 0;
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
      ' data-image-url="' + escAttr(context, item.imageUrl || "") + '"' +
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
    var attrs = ' data-scale-id="' + escAttr(context, bank.scaleId) + '"' +
      ' data-question-id="' + escAttr(context, question.questionId) + '"' +
      ' data-sort-order="' + escAttr(context, question.sortOrder || "") + '"' +
      ' data-dimension="' + escAttr(context, question.dimensionCode || "") + '"' +
      ' data-question-type="' + escAttr(context, question.questionType || "SINGLE") + '"' +
      ' data-question-text="' + escAttr(context, question.questionText || "") + '"' +
      ' data-option-a-text="' + escAttr(context, firstOption.optionText || "") + '"' +
      ' data-option-a-dimension="' + escAttr(context, firstOption.dimensionCode || "") + '"' +
      ' data-option-b-text="' + escAttr(context, secondOption.optionText || "") + '"' +
      ' data-option-b-dimension="' + escAttr(context, secondOption.dimensionCode || "") + '"';
    return actionButton(context, "edit-assessment-question", question.questionId, "编辑", false, attrs);
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
    var detail = [first(user.userId, ""), first(user.school, ""), first(user.major, "")].filter(function (item) {
      return !!item;
    }).join(" / ");
    var search = [name, detail, user.orgId].join(" ");
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
