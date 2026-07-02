(function (window, document) {
  "use strict";

  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};

  var NAV_ITEMS = [
    ["overview", "数智大屏"],
    ["users", "用户管理"],
    ["content", "内容审核"],
    ["questions", "题库审核"],
    ["broadcast", "通知公告"],
    ["audit", "审计日志"]
  ];

  registerPage("admin-console", ["admin-console"], "管理后台");

  attachRenderer("admin-console", function (item, context) {
    var adminService = window.CYANCRUISE_SERVICES && window.CYANCRUISE_SERVICES.admin;
    var identity = context.identity || {};
    var canAdmin = context.hasAdminRole && context.hasAdminRole(identity);
    setAdminMode(true);

    if (!identity.userId && !identity.adminId) {
      renderStandaloneState(context, "需要身份", "请先通过 Cosmic 登录进入应用。");
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
      pageSize: 20
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
      section("broadcast", false, renderBroadcast(context)) +
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

  function renderOverview(data, context) {
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
          identityCell(context, first(user.nickname, user.userId), user.userId) +
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
    return panel(context, "内容审核", "管理首页文章、视频和资源展示", "" +
      (content.length ? table(["标题", "类型", "展示状态", "操作"], content.map(function (item) {
        return [
          twoLine(context, item.title, first(item.summary, item.contentId)),
          esc(context, contentType(item.type)),
          badge(context, item.pinned ? "置顶" : "未置顶", item.pinned ? "ok" : "") + " " +
            badge(context, item.hidden ? "隐藏" : "展示中", item.hidden ? "warn" : "ok"),
          actionButton(context, "pin-content", item.contentId, item.pinned ? "取消置顶" : "置顶") +
            actionButton(context, "hide-content", item.contentId, item.hidden ? "恢复" : "隐藏")
        ];
      })) : empty("暂无内容数据", "这里用于管理首页文章、视频和资源内容。", context)));
  }

  function renderQuestions(data, context) {
    var questions = data.questions || [];
    return panel(context, "题库审核", "审核用户贡献题和 AI 生成题", "" +
      (questions.length ? table(["题目", "来源", "状态", "操作"], questions.map(function (question) {
        return [
          twoLine(context, first(question.summary, question.content), first(question.position, "通用岗位")),
          esc(context, sourceText(question.source)),
          badge(context, reviewText(question.reviewStatus), question.reviewStatus === "REJECTED" ? "warn" : "ok"),
          actionButton(context, "approve-question", question.questionId, "通过") +
            actionButton(context, "reject-question", question.questionId, "驳回")
        ];
      })) : empty("暂无题库数据", "这里用于审核用户贡献题、AI 生成题和待发布题目。", context)));
  }

  function renderBroadcast(context) {
    return panel(context, "通知公告", "给用户发送站内公告", '<div class="admin-form">' +
      '<label>接收用户<input id="adminBroadcastUser" placeholder="留空表示发送给所有正常用户"></label>' +
      '<label>标题<input id="adminBroadcastTitle" placeholder="例如：面试练习服务维护通知"></label>' +
      '<label>内容<textarea id="adminBroadcastContent" rows="5" placeholder="请输入公告内容"></textarea></label>' +
      '<label>链接<input id="adminBroadcastLink" placeholder="可选，例如 admin-console"></label>' +
      '<button type="button" class="admin-primary" id="adminBroadcastButton">发送公告</button>' +
      '</div>');
  }

  function renderAudit(data, context) {
    var logs = data.auditLogs && data.auditLogs.items || [];
    return panel(context, "审计日志", "查看管理员操作记录", "" +
      (logs.length ? table(["动作", "对象", "管理员", "时间"], logs.map(function (log) {
        return [
          esc(context, actionText(log.action)),
          twoLine(context, first(log.targetType, "-"), first(log.targetId, "")),
          esc(context, log.adminId),
          esc(context, first(log.createdAt, "-"))
        ];
      })) : empty("暂无操作记录", "管理员进行禁用、审核、置顶、公告等操作后，会在这里留下审计记录。", context)));
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
    var broadcastButton = root.querySelector("#adminBroadcastButton");
    if (broadcastButton) {
      broadcastButton.addEventListener("click", function () {
        var activeTab = currentActiveTab(root);
        broadcastButton.disabled = true;
        service.broadcast(adminContext(context), {
          userId: value(root, "adminBroadcastUser"),
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
  }

  function runAction(item, context, service, action, id, button) {
    var root = context.pageHost && context.pageHost.querySelector(".admin-app");
    var activeTab = currentActiveTab(root);
    var call;
    if (action === "ban-user") call = service.banUser(adminContext(context), id, "管理员在后台限制用户端访问");
    if (action === "unban-user") call = service.unbanUser(adminContext(context), id);
    if (action === "approve-question") call = service.approveQuestion(adminContext(context), id);
    if (action === "reject-question") call = service.rejectQuestion(adminContext(context), id);
    if (action === "pin-content") call = service.toggleContentPin(adminContext(context), id);
    if (action === "hide-content") call = service.toggleContentHidden(adminContext(context), id);
    if (!call) return;
    if (button) {
      button.disabled = true;
      button.classList.add("loading");
    }
    call.then(function () {
      return refreshAdminApp(item, context, service, activeTab).then(function () {
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
      content: ["内容审核", "管理首页文章、视频和资源展示"],
      questions: ["题库审核", "审核用户贡献题和 AI 生成题"],
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

  function identityCell(context, title, detail) {
    return '<strong class="admin-cell-title">' + esc(context, title) + '</strong><small>' + esc(context, detail) + '</small>';
  }

  function twoLine(context, title, detail) {
    return '<strong class="admin-cell-title">' + esc(context, first(title, "-")) + '</strong><small>' + esc(context, detail) + '</small>';
  }

  function actionButton(context, action, id, label) {
    return '<button type="button" class="admin-action" data-action="' + escAttr(context, action) +
      '" data-id="' + escAttr(context, id) + '">' + esc(context, label) + '</button>';
  }

  function empty(title, text, context) {
    return '<div class="admin-empty"><h3>' + esc(context, title) + '</h3><p>' + esc(context, text) + '</p></div>';
  }

  function badge(context, text, type) {
    return '<span class="admin-badge ' + (type || "") + '">' + esc(context, text) + '</span>';
  }

  function contentType(type) {
    if (type === "VIDEO") return "视频";
    if (type === "ARTICLE") return "文章";
    return type || "内容";
  }

  function sourceText(source) {
    if (source === "AI") return "AI 生成";
    if (source === "USER") return "用户贡献";
    return source || "系统";
  }

  function reviewText(status) {
    if (status === "PUBLISHED") return "已发布";
    if (status === "REJECTED") return "已驳回";
    if (status === "PENDING_REVIEW") return "待审核";
    return status || "待审核";
  }

  function actionText(action) {
    var map = {
      BAN_USER: "禁用用户端",
      UNBAN_USER: "恢复用户端",
      BROADCAST: "发送公告",
      APPROVE_QUESTION: "通过题目",
      REJECT_QUESTION: "驳回题目",
      UPDATE_QUESTION: "更新题目",
      SAVE_CONTENT: "保存内容",
      TOGGLE_CONTENT: "切换内容状态"
    };
    return map[action] || action || "-";
  }

  function value(root, id) {
    var el = root.querySelector("#" + id);
    return el ? el.value.trim() : "";
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
