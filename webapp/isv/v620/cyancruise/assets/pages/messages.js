(function (window) {
  "use strict";

  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};

  registerPage("messages", ["messages"], "消息中心");

  attachRenderer("messages", function (item, context) {
    renderLoading(item, context);
    loadMessages(context).then(function (data) {
      renderMessages(item, context, data);
      bindMessages(item, context);
    }).catch(function (error) {
      renderUnavailable(item, context, messageOf(error));
    });
  });

  function loadMessages(context) {
    var userId = userIdOf(context);
    return Promise.all([
      context.post(context.endpoints.notifications, userId),
      context.post(context.endpoints.notificationUnread, userId),
      context.post(context.endpoints.subscriptionQuota, userId)
    ]).then(function (results) {
      return {
        notifications: arrayOf(results[0]),
        unread: numberOf(results[1] && results[1].count),
        quotas: arrayOf(results[2])
      };
    });
  }

  function renderLoading(item, context) {
    context.renderShell(item, '<section class="feature-section full message-center">' +
      '<div class="section-heading"><div><h3>消息中心</h3><p class="section-note">正在读取站内通知。</p></div></div>' +
      state(context, "正在加载", "正在同步你的消息、未读数和订阅配额。", "pending") +
      '</section>');
  }

  function renderUnavailable(item, context, text) {
    context.renderShell(item, '<section class="feature-section full message-center">' +
      '<div class="section-heading"><div><h3>消息中心</h3><p class="section-note">站内通知暂时无法读取。</p></div>' +
      '<button type="button" class="secondary" data-message-action="refresh">重试</button></div>' +
      state(context, "消息暂时不可用", text || "请稍后再试。", "warning") +
      '</section>');
    bindMessages(item, context);
  }

  function renderMessages(item, context, data) {
    var notifications = data.notifications || [];
    var unread = data.unread || 0;
    var quotaText = quotaSummary(data.quotas);
    var body = '<section class="feature-section full message-center">' +
      '<div class="section-heading"><div><h3>消息中心</h3>' +
      '<p class="section-note">查看系统通知、练习提醒、复盘结果和管理员公告。</p></div>' +
      '<div class="actions-row compact">' +
      '<button type="button" class="secondary" data-message-action="refresh">刷新</button>' +
      '<button type="button" class="secondary" data-message-action="read-all" ' + (unread <= 0 ? "disabled" : "") + '>全部标为已读</button>' +
      '<button type="button" data-message-action="weekly-report">生成周报通知</button>' +
      '</div></div>' +
      '<div class="message-summary-grid">' +
      summaryCard(context, "未读消息", unread, "需要你查看的站内消息") +
      summaryCard(context, "全部消息", notifications.length, "当前保留在消息中心的通知") +
      summaryCard(context, "订阅配额", quotaText, "可用于外部提醒的剩余授权") +
      '</div>';

    if (!notifications.length) {
      body += state(context, "暂无消息", "新的公告、练习结果和周报会出现在这里。", "empty");
    } else {
      body += '<div class="message-list">' + notifications.map(function (notification) {
        return renderNotification(context, notification);
      }).join("") + '</div>';
    }
    body += '</section>';
    context.renderShell(item, body);
  }

  function renderNotification(context, notification) {
    var id = text(notification.notificationId);
    var read = notification.readFlag === true;
    var typeLabel = first(notification.label, labelForType(notification.type));
    var group = groupLabel(notification.groupKey, notification.type);
    var link = text(notification.link);
    return '<article class="message-item' + (read ? " is-read" : " is-unread") + '" data-message-id="' + esc(context, id) + '">' +
      '<div class="message-main">' +
      '<div class="message-meta"><span>' + esc(context, group) + '</span><span>' + esc(context, typeLabel) + '</span><span>' + esc(context, formatDate(notification.createdAt)) + '</span></div>' +
      '<h3>' + esc(context, first(notification.title, "未命名通知")) + '</h3>' +
      '<p>' + esc(context, first(notification.content, "暂无内容")) + '</p>' +
      '</div>' +
      '<div class="message-actions">' +
      (read ? '<span class="message-read-badge">已读</span>' : '<button type="button" class="secondary" data-message-action="read" data-message-id="' + esc(context, id) + '">标为已读</button>') +
      (link ? '<button type="button" class="secondary" data-message-action="open" data-message-link="' + esc(context, link) + '">前往查看</button>' : '') +
      '<button type="button" class="secondary danger" data-message-action="delete" data-message-id="' + esc(context, id) + '">移出列表</button>' +
      '</div></article>';
  }

  function bindMessages(item, context) {
    var host = context.pageHost;
    if (!host) {
      return;
    }
    var buttons = host.querySelectorAll("[data-message-action]");
    for (var i = 0; i < buttons.length; i += 1) {
      buttons[i].addEventListener("click", function (event) {
        var target = event.currentTarget;
        var action = target.getAttribute("data-message-action");
        if (action === "refresh") {
          rerender(item, context);
        } else if (action === "read") {
          markRead(item, context, target.getAttribute("data-message-id"));
        } else if (action === "read-all") {
          markAllRead(item, context);
        } else if (action === "delete") {
          archiveMessage(item, context, target.getAttribute("data-message-id"));
        } else if (action === "open") {
          openLink(target.getAttribute("data-message-link"));
        } else if (action === "weekly-report") {
          runWeeklyReport(item, context);
        }
      });
    }
  }

  function markRead(item, context, notificationId) {
    if (!notificationId) {
      return;
    }
    context.post(context.endpoints.notificationRead, {
      userId: userIdOf(context),
      notificationId: notificationId
    }).then(function () {
      notice(context, "success", "已标为已读", "这条消息已更新。");
      rerender(item, context);
    }).catch(function (error) {
      notice(context, "warning", "操作失败", messageOf(error));
    });
  }

  function markAllRead(item, context) {
    context.post(context.endpoints.notificationReadAll, {
      userId: userIdOf(context)
    }).then(function () {
      notice(context, "success", "已全部标为已读", "未读消息数已更新。");
      rerender(item, context);
    }).catch(function (error) {
      notice(context, "warning", "操作失败", messageOf(error));
    });
  }

  function archiveMessage(item, context, notificationId) {
    if (!notificationId) {
      return;
    }
    context.post(context.endpoints.notificationDelete, {
      userId: userIdOf(context),
      notificationId: notificationId
    }).then(function () {
      notice(context, "success", "已移出列表", "这条消息已归档。");
      rerender(item, context);
    }).catch(function (error) {
      notice(context, "warning", "操作失败", messageOf(error));
    });
  }

  function runWeeklyReport(item, context) {
    context.post(context.endpoints.weeklyReport, {
      userId: userIdOf(context),
      highlights: ["查看消息中心", "复盘近期练习"]
    }).then(function (result) {
      var delivered = result && result.delivered === true;
      notice(context, delivered ? "success" : "warning", delivered ? "周报已生成" : "周报暂未生成",
        delivered ? "新的周报通知已加入消息中心。" : first(result && result.reason, "近期活动还不够生成周报。"));
      rerender(item, context);
    }).catch(function (error) {
      notice(context, "warning", "周报生成失败", messageOf(error));
    });
  }

  function openLink(link) {
    var route = text(link).replace(/^index\.html[#?]*/, "").replace(/^ccRoute=/, "").replace(/^#/, "");
    if (route) {
      window.location.hash = route;
    }
  }

  function rerender(item, context) {
    renderLoading(item, context);
    loadMessages(context).then(function (data) {
      renderMessages(item, context, data);
      bindMessages(item, context);
    }).catch(function (error) {
      renderUnavailable(item, context, messageOf(error));
    });
  }

  function summaryCard(context, title, value, note) {
    return '<article class="message-summary-card"><span>' + esc(context, title) + '</span>' +
      '<strong>' + esc(context, value) + '</strong><p>' + esc(context, note) + '</p></article>';
  }

  function quotaSummary(quotas) {
    var total = 0;
    for (var i = 0; i < (quotas || []).length; i += 1) {
      total += numberOf(quotas[i] && quotas[i].remaining);
    }
    return total;
  }

  function labelForType(type) {
    var value = text(type).toUpperCase();
    if (value === "ADMIN_BROADCAST") return "通知公告";
    if (value === "WEEKLY_REPORT") return "周报通知";
    if (value === "INTERVIEW_REPORT") return "面试复盘";
    if (value === "ASSESSMENT_RESULT") return "测评结果";
    if (value === "RESUME_DIAGNOSIS") return "简历诊断";
    if (value === "AI_PROACTIVE") return "智能提醒";
    if (value === "STREAK_WARNING") return "练习提醒";
    return "系统通知";
  }

  function groupLabel(groupKey, type) {
    var group = text(groupKey).toUpperCase();
    var value = text(type).toUpperCase();
    if (group === "CAREER" || value === "INTERVIEW_REPORT" || value === "ASSESSMENT_RESULT" || value === "RESUME_DIAGNOSIS") return "职业发展";
    if (group === "AI" || value === "AI_PROACTIVE") return "智能助手";
    return "系统消息";
  }

  function formatDate(value) {
    var textValue = text(value);
    if (!textValue) {
      return "时间未知";
    }
    return textValue.replace("T", " ").replace(/\.\d+$/, "").slice(0, 16);
  }

  function state(context, title, textValue, type) {
    if (context.statePanel) {
      return context.statePanel(title, textValue, type);
    }
    return '<div class="state-panel ' + esc(context, type || "empty") + '"><strong>' + esc(context, title) +
      '</strong><p>' + esc(context, textValue) + '</p></div>';
  }

  function userIdOf(context) {
    return context.identity && context.identity.userId ? context.identity.userId : "";
  }

  function notice(context, type, title, textValue) {
    if (context.showMessage) {
      context.showMessage(type, title, textValue);
    }
  }

  function arrayOf(value) {
    return Array.isArray(value) ? value : [];
  }

  function numberOf(value) {
    var number = Number(value);
    return isNaN(number) ? 0 : number;
  }

  function first() {
    for (var i = 0; i < arguments.length; i += 1) {
      var value = text(arguments[i]);
      if (value) {
        return value;
      }
    }
    return "";
  }

  function text(value) {
    return value == null ? "" : String(value).replace(/^\s+|\s+$/g, "");
  }

  function esc(context, value) {
    return context.escapeHtml ? context.escapeHtml(value) : text(value)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;");
  }

  function messageOf(error) {
    return error && error.message ? error.message : "服务暂时不可用，请稍后再试。";
  }
}(window));
