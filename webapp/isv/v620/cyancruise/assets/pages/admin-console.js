(function (window) {
  "use strict";

  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};

  registerPage("admin-console", ["admin-console"], "管理后台");

  attachRenderer("admin-console", function (item, context) {
    var adminService = window.CYANCRUISE_SERVICES && window.CYANCRUISE_SERVICES.admin;
    var identity = context.identity || {};
    var canAdmin = context.hasAdminRole && context.hasAdminRole(identity);

    if (!identity.userId && !identity.adminId) {
      context.renderShell(item, context.statePanel("需要身份", "请先通过 Cosmic 登录进入应用；开发验证可使用 identityMode=development 并设置 userId。", "warning"));
      return;
    }
    if (!canAdmin) {
      context.renderShell(item, context.statePanel("无管理员权限", "当前账号可以继续使用用户端，但不能进入管理后台。", "warning"));
      return;
    }
    if (!adminService || typeof adminService.load !== "function") {
      context.renderShell(item, context.statePanel("管理服务未加载", "前端管理服务暂不可用，请刷新页面后重试。", "warning"));
      return;
    }

    context.renderShell(item, context.statePanel("正在加载管理后台", "正在读取用户、内容、题库和操作记录。", "info"));
    adminService.load(adminContext(context)).then(function (data) {
      if (!data.authorized) {
        context.renderShell(item, context.statePanel("无管理员权限", data.message || "当前账号没有管理后台权限。", "warning"));
        return;
      }
      context.renderShell(item, renderConsole(data, context));
      bindActions(item, context, data);
    }).catch(function (error) {
      context.renderShell(item, context.statePanel("管理后台暂不可用", messageOf(error), "warning"));
    });
  });

  function adminContext(context) {
    return {
      endpoints: context.endpoints,
      identity: context.identity,
      post: context.post,
      pageSize: 20
    };
  }

  function renderConsole(data, context) {
    return '<section class="admin-console">' +
      '<div class="admin-hero">' +
      '<div><p class="eyebrow">管理后台</p><h2>平台运营管理</h2><p>管理用户状态、内容展示、题库审核、通知公告和操作记录。</p></div>' +
      '<div class="admin-identity"><span>当前管理员</span><strong>' + esc(context, data.adminId || "-") + '</strong><small>' + esc(context, roleText(data.whoami)) + '</small></div>' +
      '</div>' +
      '<div class="admin-tabs" role="tablist">' +
      tab("overview", "总览", true) +
      tab("users", "用户管理", false) +
      tab("content", "内容管理", false) +
      tab("questions", "题库审核", false) +
      tab("broadcast", "通知公告", false) +
      tab("audit", "操作记录", false) +
      '</div>' +
      '<div class="admin-sections">' +
      section("overview", true, renderOverview(data, context)) +
      section("users", false, renderUsers(data, context)) +
      section("content", false, renderContent(data, context)) +
      section("questions", false, renderQuestions(data, context)) +
      section("broadcast", false, renderBroadcast(context)) +
      section("audit", false, renderAudit(data, context)) +
      '</div>' +
      '</section>';
  }

  function renderOverview(data, context) {
    var dashboard = data.dashboard || {};
    var analytics = data.analytics || {};
    return '<div class="admin-metrics">' +
      metric(context, "学生数量", dashboard.studentCount) +
      metric(context, "面试次数", first(analytics.totalInterviews, dashboard.interviewCount)) +
      metric(context, "测评次数", analytics.totalAssessments) +
      metric(context, "用户总数", analytics.totalUsers) +
      '</div>' +
      '<section class="panel full"><h3>数据覆盖</h3><p>' +
      '报告数量：' + esc(context, first(dashboard.reportCount, 0)) +
      '，跳过报告：' + esc(context, first(dashboard.skippedReportCount, 0)) +
      '。近 30 天事件会按后端返回原样展示，未知事件不会导致页面失败。' +
      '</p></section>';
  }

  function renderUsers(data, context) {
    var users = data.users && data.users.items || [];
    if (!users.length) {
      return empty("暂无用户数据", "接入正式用户存储后，这里会显示用户状态和最近面试情况。", context);
    }
    return '<section class="panel full"><h3>用户管理</h3><div class="admin-table-wrap"><table class="admin-table">' +
      '<thead><tr><th>用户</th><th>学校/专业</th><th>状态</th><th>组织</th><th>操作</th></tr></thead><tbody>' +
      users.map(function (user) {
        var status = String(user.status || "ACTIVE");
        var banned = status === "BANNED";
        return '<tr><td><strong>' + esc(context, first(user.nickname, user.userId)) + '</strong><small>' + esc(context, user.userId) + '</small></td>' +
          '<td>' + esc(context, first(user.school, "-")) + '<small>' + esc(context, first(user.major, "")) + '</small></td>' +
          '<td><span class="admin-badge ' + (banned ? "danger" : "ok") + '">' + esc(context, banned ? "已禁用" : "正常") + '</span></td>' +
          '<td>' + esc(context, first(user.orgId, "-")) + '</td>' +
          '<td><button type="button" class="admin-action" data-action="' + (banned ? "unban-user" : "ban-user") + '" data-id="' + escAttr(context, user.userId) + '">' + (banned ? "解禁" : "禁用") + '</button></td></tr>';
      }).join("") +
      '</tbody></table></div></section>';
  }

  function renderContent(data, context) {
    var content = data.content || [];
    if (!content.length) {
      return empty("暂无内容数据", "这里用于管理首页文章、视频和资源内容。", context);
    }
    return '<section class="panel full"><h3>内容管理</h3><div class="admin-table-wrap"><table class="admin-table">' +
      '<thead><tr><th>标题</th><th>类型</th><th>展示状态</th><th>操作</th></tr></thead><tbody>' +
      content.map(function (item) {
        return '<tr><td><strong>' + esc(context, item.title) + '</strong><small>' + esc(context, first(item.summary, item.contentId)) + '</small></td>' +
          '<td>' + esc(context, contentType(item.type)) + '</td>' +
          '<td>' + badge(context, item.pinned ? "已置顶" : "未置顶", item.pinned ? "ok" : "") + " " + badge(context, item.hidden ? "已隐藏" : "展示中", item.hidden ? "danger" : "ok") + '</td>' +
          '<td><button type="button" class="admin-action" data-action="pin-content" data-id="' + escAttr(context, item.contentId) + '">' + (item.pinned ? "取消置顶" : "置顶") + '</button>' +
          '<button type="button" class="admin-action" data-action="hide-content" data-id="' + escAttr(context, item.contentId) + '">' + (item.hidden ? "恢复展示" : "隐藏") + '</button></td></tr>';
      }).join("") +
      '</tbody></table></div></section>';
  }

  function renderQuestions(data, context) {
    var questions = data.questions || [];
    if (!questions.length) {
      return empty("暂无题库数据", "这里用于审核用户贡献题、AI 生成题和待发布题目。", context);
    }
    return '<section class="panel full"><h3>题库审核</h3><div class="admin-table-wrap"><table class="admin-table">' +
      '<thead><tr><th>题目</th><th>来源</th><th>状态</th><th>操作</th></tr></thead><tbody>' +
      questions.map(function (question) {
        return '<tr><td><strong>' + esc(context, first(question.summary, question.content)) + '</strong><small>' + esc(context, first(question.position, "通用岗位")) + '</small></td>' +
          '<td>' + esc(context, sourceText(question.source)) + '</td>' +
          '<td>' + badge(context, reviewText(question.reviewStatus), question.reviewStatus === "REJECTED" ? "danger" : "ok") + '</td>' +
          '<td><button type="button" class="admin-action" data-action="approve-question" data-id="' + escAttr(context, question.questionId) + '">通过</button>' +
          '<button type="button" class="admin-action" data-action="reject-question" data-id="' + escAttr(context, question.questionId) + '">驳回</button></td></tr>';
      }).join("") +
      '</tbody></table></div></section>';
  }

  function renderBroadcast(context) {
    return '<section class="panel full"><h3>通知公告</h3>' +
      '<div class="admin-form">' +
      '<label>接收用户<input id="adminBroadcastUser" placeholder="留空表示发送给所有正常用户"></label>' +
      '<label>标题<input id="adminBroadcastTitle" placeholder="例如：面试练习服务维护通知"></label>' +
      '<label>内容<textarea id="adminBroadcastContent" rows="4" placeholder="请输入公告内容"></textarea></label>' +
      '<label>链接<input id="adminBroadcastLink" placeholder="可选，例如 admin-console"></label>' +
      '<button type="button" class="primary" id="adminBroadcastButton">发送公告</button>' +
      '</div></section>';
  }

  function renderAudit(data, context) {
    var logs = data.auditLogs && data.auditLogs.items || [];
    if (!logs.length) {
      return empty("暂无操作记录", "管理员进行禁用、审核、置顶、公告等操作后，会在这里留下审计记录。", context);
    }
    return '<section class="panel full"><h3>操作记录</h3><div class="admin-table-wrap"><table class="admin-table">' +
      '<thead><tr><th>动作</th><th>对象</th><th>管理员</th><th>时间</th></tr></thead><tbody>' +
      logs.map(function (log) {
        return '<tr><td>' + esc(context, actionText(log.action)) + '</td><td>' + esc(context, first(log.targetType, "-")) + '<small>' + esc(context, first(log.targetId, "")) + '</small></td>' +
          '<td>' + esc(context, log.adminId) + '</td><td>' + esc(context, first(log.createdAt, "-")) + '</td></tr>';
      }).join("") +
      '</tbody></table></div></section>';
  }

  function bindActions(item, context, data) {
    var root = context.pageHost && context.pageHost.querySelector(".admin-console");
    var service = window.CYANCRUISE_SERVICES.admin;
    if (!root || !service) {
      return;
    }
    var tabs = root.querySelectorAll(".admin-tab");
    for (var i = 0; i < tabs.length; i += 1) {
      tabs[i].addEventListener("click", function () {
        activateTab(root, this.getAttribute("data-tab"));
      });
    }
    var actions = root.querySelectorAll(".admin-action");
    for (var j = 0; j < actions.length; j += 1) {
      actions[j].addEventListener("click", function () {
        runAction(item, context, service, this.getAttribute("data-action"), this.getAttribute("data-id"));
      });
    }
    var broadcastButton = root.querySelector("#adminBroadcastButton");
    if (broadcastButton) {
      broadcastButton.addEventListener("click", function () {
        var request = {
          userId: value(root, "adminBroadcastUser"),
          title: value(root, "adminBroadcastTitle"),
          content: value(root, "adminBroadcastContent"),
          link: value(root, "adminBroadcastLink")
        };
        service.broadcast(adminContext(context), request).then(function (result) {
          context.showMessage("info", "公告已发送", "成功 " + first(result.successCount, 0) + " 个，失败 " + first(result.failedCount, 0) + " 个。");
          context.renderPage(item);
        }).catch(function (error) {
          context.showMessage("error", "发送失败", messageOf(error));
        });
      });
    }
  }

  function runAction(item, context, service, action, id) {
    var call;
    if (action === "ban-user") call = service.banUser(adminContext(context), id, "管理员在后台手动禁用");
    if (action === "unban-user") call = service.unbanUser(adminContext(context), id);
    if (action === "approve-question") call = service.approveQuestion(adminContext(context), id);
    if (action === "reject-question") call = service.rejectQuestion(adminContext(context), id);
    if (action === "pin-content") call = service.toggleContentPin(adminContext(context), id);
    if (action === "hide-content") call = service.toggleContentHidden(adminContext(context), id);
    if (!call) {
      return;
    }
    call.then(function () {
      context.showMessage("info", "操作完成", "管理后台数据已更新。");
      context.renderPage(item);
    }).catch(function (error) {
      context.showMessage("error", "操作失败", messageOf(error));
    });
  }

  function activateTab(root, key) {
    var tabs = root.querySelectorAll(".admin-tab");
    var sections = root.querySelectorAll(".admin-section");
    for (var i = 0; i < tabs.length; i += 1) {
      tabs[i].classList.toggle("active", tabs[i].getAttribute("data-tab") === key);
    }
    for (var j = 0; j < sections.length; j += 1) {
      sections[j].classList.toggle("active", sections[j].getAttribute("data-section") === key);
    }
  }

  function tab(key, label, active) {
    return '<button type="button" class="admin-tab' + (active ? " active" : "") + '" data-tab="' + key + '">' + label + '</button>';
  }

  function section(key, active, html) {
    return '<div class="admin-section' + (active ? " active" : "") + '" data-section="' + key + '">' + html + '</div>';
  }

  function metric(context, label, value) {
    return '<article class="admin-metric"><span>' + esc(context, label) + '</span><strong>' + esc(context, first(value, 0)) + '</strong></article>';
  }

  function empty(title, text, context) {
    return '<section class="state-card"><h3>' + esc(context, title) + '</h3><p>' + esc(context, text) + '</p></section>';
  }

  function badge(context, text, type) {
    return '<span class="admin-badge ' + (type || "") + '">' + esc(context, text) + '</span>';
  }

  function roleText(whoami) {
    return whoami && whoami.status === "OK" ? "已通过管理员身份校验" : "等待管理员身份校验";
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
      BAN_USER: "禁用用户",
      UNBAN_USER: "解禁用户",
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
}(window));
