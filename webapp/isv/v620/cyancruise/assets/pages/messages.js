(function (window) {
  "use strict";

  var PAGE_SIZE = 10;
  var pageByUser = {};
  var dataByUser = {};
  var selectedMessageByUser = {};
  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};

  registerPage("messages", ["messages"], "消息中心");
  registerPage("message-detail", ["message-detail"], "消息详情");

  attachRenderer("messages", renderMessagesRoute);
  attachRenderer("message-detail", renderMessagesRoute);

  function renderMessagesRoute(item, context) {
    var cached = currentData(context);
    var detailRoute = item && item.key === "message-detail";
    if (cached) {
      if (detailRoute) {
        renderMessageDetail(item, context, cached);
      } else {
        renderMessages(item, context, cached);
      }
      bindMessages(item, context);
    } else {
      renderLoading(item, context);
    }
    loadMessages(context).then(function (data) {
      storeData(context, data);
      if (detailRoute) {
        renderMessageDetail(item, context, data);
      } else {
        renderMessages(item, context, data);
      }
      bindMessages(item, context);
    }).catch(function (error) {
      if (cached) {
        notice(context, "warning", "刷新失败", messageOf(error));
        return;
      }
      renderUnavailable(item, context, messageOf(error));
    });
  }

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
    context.renderShell(item, '<section class="feature-section full message-center message-workspace-shell">' +
      messageWorkspaceHeading("正在同步你的消息", "正在读取通知、未读数和订阅配额。", "同步中") +
      '<div class="message-loading-layout">' +
      state(context, "正在加载消息中心", "请稍候，系统正在同步与你有关的站内通知。", "pending") +
      '<div class="message-loading-cards" aria-hidden="true"><i></i><i></i><i></i></div></div>' +
      '</section>');
  }

  function renderUnavailable(item, context, textValue) {
    context.renderShell(item, '<section class="feature-section full message-center message-workspace-shell">' +
      messageWorkspaceHeading("消息暂时无法同步", "其他功能仍可正常使用，你可以稍后重新加载。", "需要重试") +
      '<div class="message-unavailable-state"><span class="message-state-icon" aria-hidden="true">!</span><div><h3>消息服务暂时不可用</h3>' +
      '<p>' + esc(context, textValue || "请稍后再试。") + '</p><small>已有消息不会因此被删除或改为已读。</small></div>' +
      '<button type="button" class="secondary" data-message-action="refresh">重新加载</button></div>' +
      '</section>');
    bindMessages(item, context);
  }

  function renderMessages(item, context, data) {
    var notifications = data.notifications || [];
    var unread = data.unread || 0;
    var quotaText = quotaSummary(data.quotas);
    var readCount = Math.max(0, notifications.length - unread);
    var pageInfo = pageSlice(context, notifications);
    var statusTitle = unread > 0 ? "你有 " + unread + " 条消息尚未阅读" : "消息已全部查看";
    var statusNote = unread > 0 ? "优先查看带有未读标记的通知，处理后可以单独或批量标为已读。" :
      (notifications.length ? "当前没有需要处理的未读通知。" : "新的公告、练习结果和每周回顾会出现在这里。");
    var body = '<section class="feature-section full message-center message-workspace-shell">' +
      '<section class="message-overview-hero"><div class="message-overview-copy"><span class="message-eyebrow">消息中心工作台</span>' +
      '<h3>' + esc(context, statusTitle) + '</h3><p>' + esc(context, statusNote) + '</p>' +
      '<div class="message-privacy-note"><span aria-hidden="true">✓</span>这里只展示与你有关的通知和管理员向你发布的公告。</div></div>' +
      '<div class="message-summary-grid">' +
      summaryCard(context, "未读消息", unread, unread > 0 ? "等待查看" : "当前已清空", unread > 0 ? "attention" : "complete", "未") +
      summaryCard(context, "消息总数", notifications.length, readCount + " 条已读", "total", "信") +
      summaryCard(context, "外部提醒额度", quotaText, "剩余订阅授权", "quota", "铃") +
      '</div></section>' +
      '<section class="message-inbox-panel"><div class="message-toolbar"><div><span class="message-eyebrow">通知收件箱</span>' +
      '<h3>' + (notifications.length ? "按时间查看全部通知" : "等待新的通知") + '</h3><p>系统按最新时间排序，完整内容可以进入详情查看。</p></div>' +
      '<div class="actions-row compact message-toolbar-actions">' +
      '<button type="button" class="secondary" data-message-action="refresh">刷新消息</button>' +
      '<button type="button" class="secondary" data-message-action="read-all" ' + (unread <= 0 ? "disabled" : "") + '>全部标为已读</button>' +
      '<button type="button" data-message-action="weekly-report">生成每周回顾</button>' +
      '</div></div>';

    if (!notifications.length) {
      body += renderMessageEmptyState(context, quotaText, unread);
    } else {
      body += renderPager(context, pageInfo, "top");
      body += '<div class="message-list">' + pageInfo.items.map(function (notification) {
        return renderNotification(context, notification);
      }).join("") + '</div>';
      body += renderPager(context, pageInfo, "bottom");
    }
    body += '</section></section>';
    context.renderShell(item, body);
  }

  function messageWorkspaceHeading(title, note, status) {
    return '<div class="message-workspace-heading"><div><span class="message-eyebrow">消息中心工作台</span><h3>' +
      title + '</h3><p>' + note + '</p></div><span class="message-workspace-status">' + status + '</span></div>';
  }

  function renderMessageEmptyState(context, quota, unread) {
    return '<div class="message-empty-state"><div class="message-empty-visual" aria-hidden="true"><span>信</span><i></i><i></i></div>' +
      '<div><span class="message-eyebrow">收件箱已清空</span><h3>暂时没有新的站内消息</h3>' +
      '<p>管理员公告、面试复盘、测评结果、简历诊断和每周回顾会在生成后出现在这里。</p>' +
      '<div class="message-empty-facts"><span><b>' + esc(context, unread) + '</b> 条待处理</span><span><b>' + esc(context, quota) + '</b> 次外部提醒额度</span></div></div></div>';
  }

  function renderNotification(context, notification) {
    var id = text(notification.notificationId);
    var read = notification.readFlag === true;
    var type = text(notification.type).toUpperCase();
    var admin = type === "ADMIN_BROADCAST";
    var typeLabel = admin ? "管理员公告" : first(notification.label, labelForType(type));
    var group = groupLabel(notification.groupKey, type);
    var classes = "message-item" + (read ? " is-read" : " is-unread") + (admin ? " is-admin" : "");
    return '<article class="' + classes + '" data-message-id="' + esc(context, id) + '">' +
      '<div class="message-type-icon ' + esc(context, messageTypeTone(type, admin)) + '" aria-hidden="true">' + esc(context, messageTypeIcon(type, admin)) + '</div>' +
      '<div class="message-main"><div class="message-title-row">' + (!read ? '<i class="message-unread-dot" aria-label="未读"></i>' : '') +
      '<h3>' + esc(context, first(notification.title, admin ? "管理员公告" : "未命名通知")) + '</h3></div>' +
      '<div class="message-meta">' +
      '<span>' + esc(context, group) + '</span>' +
      '<span class="message-tag' + (admin ? " admin" : "") + '">' + esc(context, typeLabel) + '</span>' +
      (read ? '<span class="message-tag muted">已读</span>' : '<span class="message-tag unread">未读</span>') +
      '<span>' + esc(context, formatDate(notification.createdAt)) + '</span></div>' +
      '<p>' + esc(context, first(notification.content, "暂无内容")) + '</p>' +
      '</div>' +
      '<div class="message-actions">' +
      (read ? '<span class="message-read-badge">已读</span>' : '<button type="button" class="secondary" data-message-action="read" data-message-id="' + esc(context, id) + '">标为已读</button>') +
      '<button type="button" class="secondary message-open-action" data-message-action="open" data-message-id="' + esc(context, id) + '">查看详情</button>' +
      '<button type="button" class="secondary danger" data-message-action="delete" data-message-id="' + esc(context, id) + '">移出列表</button>' +
      '</div></article>';
  }

  function messageTypeIcon(type, admin) {
    if (admin) return "告";
    if (type === "WEEKLY_REPORT") return "报";
    if (type === "INTERVIEW_REPORT") return "面";
    if (type === "ASSESSMENT_RESULT") return "测";
    if (type === "RESUME_DIAGNOSIS") return "历";
    if (type === "AI_PROACTIVE") return "AI";
    if (type === "STREAK_WARNING") return "练";
    return "信";
  }

  function messageTypeTone(type, admin) {
    if (admin) return "admin";
    if (type === "WEEKLY_REPORT" || type === "INTERVIEW_REPORT" || type === "ASSESSMENT_RESULT" || type === "RESUME_DIAGNOSIS") return "career";
    if (type === "AI_PROACTIVE") return "ai";
    if (type === "STREAK_WARNING") return "warning";
    return "system";
  }

  function renderMessageDetail(item, context, data) {
    var notification = selectedMessage(context, data && data.notifications);
    if (!notification) {
      context.renderShell(item, '<section class="feature-section full message-center message-workspace-shell">' +
        messageWorkspaceHeading("这条消息已经不在列表中", "消息可能已被归档，返回收件箱可以继续查看其他通知。", "消息不存在") +
        '<div class="message-unavailable-state empty"><span class="message-state-icon" aria-hidden="true">信</span><div><h3>无法打开消息详情</h3>' +
        '<p>这条消息可能已被移出列表，请返回消息中心查看其他消息。</p></div>' +
        '<button type="button" class="secondary" data-message-action="back">返回消息中心</button></div>' +
        '</section>');
      return;
    }
    var type = text(notification.type).toUpperCase();
    var admin = type === "ADMIN_BROADCAST";
    var typeLabel = admin ? "管理员公告" : first(notification.label, labelForType(type));
    var group = groupLabel(notification.groupKey, type);
    var body = '<section class="feature-section full message-center message-detail message-workspace-shell">' +
      '<div class="message-detail-nav"><div><span class="message-eyebrow">消息详情</span><p>完整内容与消息来源</p></div>' +
      '<button type="button" class="secondary" data-message-action="back">返回消息中心</button></div>' +
      '<article class="message-detail-content' + (admin ? ' is-admin' : '') + '">' +
      '<div class="message-detail-icon ' + esc(context, messageTypeTone(type, admin)) + '" aria-hidden="true">' + esc(context, messageTypeIcon(type, admin)) + '</div>' +
      '<div class="message-meta"><span>' + esc(context, group) + '</span>' +
      '<span class="message-tag' + (admin ? ' admin' : '') + '">' + esc(context, typeLabel) + '</span>' +
      '<span class="message-tag muted">已读</span>' +
      '<span>' + esc(context, formatDate(notification.createdAt)) + '</span></div>' +
      '<h2>' + esc(context, first(notification.title, admin ? "管理员公告" : "未命名通知")) + '</h2>' +
      '<div class="message-detail-divider"><span>消息正文</span></div><div class="message-detail-body">' + esc(context, first(notification.content, "暂无内容")) + '</div>' +
      '<div class="message-detail-foot"><span>此消息来自 ' + esc(context, group) + '</span><button type="button" class="secondary" data-message-action="back">查看其他消息</button></div>' +
      '</article></section>';
    context.renderShell(item, body);
  }

  function renderPager(context, pageInfo, position) {
    if (pageInfo.totalPages <= 1) {
      return "";
    }
    return '<nav class="message-pager message-pager-' + esc(context, position) + '" aria-label="消息分页">' +
      '<span>第 ' + pageInfo.page + ' / ' + pageInfo.totalPages + ' 页，每页 ' + PAGE_SIZE + ' 条</span>' +
      '<div class="actions-row compact">' +
      '<button type="button" class="secondary" data-message-action="page" data-page="' + (pageInfo.page - 1) + '" ' + (pageInfo.page <= 1 ? "disabled" : "") + '>上一页</button>' +
      '<button type="button" class="secondary" data-message-action="page" data-page="' + (pageInfo.page + 1) + '" ' + (pageInfo.page >= pageInfo.totalPages ? "disabled" : "") + '>下一页</button>' +
      '</div></nav>';
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
          refreshMessages(item, context, target, "刷新中");
        } else if (action === "read") {
          markRead(item, context, target.getAttribute("data-message-id"));
        } else if (action === "read-all") {
          markAllRead(item, context);
        } else if (action === "delete") {
          archiveMessage(item, context, target.getAttribute("data-message-id"));
        } else if (action === "open") {
          openMessageDetail(context, target.getAttribute("data-message-id"));
        } else if (action === "back") {
          window.location.hash = "messages";
        } else if (action === "weekly-report") {
          runWeeklyReport(item, context);
        } else if (action === "page") {
          setPage(context, target.getAttribute("data-page"));
          renderMessages(item, context, currentData(context) || emptyData());
          bindMessages(item, context);
        }
      });
    }
  }

  function markRead(item, context, notificationId) {
    if (!notificationId) {
      return;
    }
    setButtonBusy(targetButton(context, notificationId, "read"), "处理中");
    context.post(context.endpoints.notificationRead, {
      userId: userIdOf(context),
      notificationId: notificationId
    }).then(function () {
      applyRead(context, notificationId);
      renderMessages(item, context, currentData(context) || emptyData());
      bindMessages(item, context);
      notice(context, "success", "已标为已读", "这条消息已更新。");
      refreshMessages(item, context, null, "");
    }).catch(function (error) {
      clearBusyButtons(context);
      notice(context, "warning", "操作失败", messageOf(error));
    });
  }

  function markAllRead(item, context) {
    var button = actionButton(context, "read-all");
    setButtonBusy(button, "处理中");
    context.post(context.endpoints.notificationReadAll, {
      userId: userIdOf(context)
    }).then(function () {
      applyAllRead(context);
      renderMessages(item, context, currentData(context) || emptyData());
      bindMessages(item, context);
      notice(context, "success", "已全部标为已读", "未读消息数已更新。");
      refreshMessages(item, context, null, "");
    }).catch(function (error) {
      clearBusyButtons(context);
      notice(context, "warning", "操作失败", messageOf(error));
    });
  }

  function archiveMessage(item, context, notificationId) {
    if (!notificationId) {
      return;
    }
    setButtonBusy(targetButton(context, notificationId, "delete"), "移出中");
    context.post(context.endpoints.notificationDelete, {
      userId: userIdOf(context),
      notificationId: notificationId
    }).then(function () {
      applyArchive(context, notificationId);
      renderMessages(item, context, currentData(context) || emptyData());
      bindMessages(item, context);
      notice(context, "success", "已移出列表", "这条消息已归档。");
      refreshMessages(item, context, null, "");
    }).catch(function (error) {
      clearBusyButtons(context);
      notice(context, "warning", "操作失败", messageOf(error));
    });
  }

  function runWeeklyReport(item, context) {
    var button = actionButton(context, "weekly-report");
    setButtonBusy(button, "生成中");
    context.post(context.endpoints.weeklyReport, {
      userId: userIdOf(context),
      highlights: ["查看消息中心", "复盘近期练习"]
    }).then(function (result) {
      var delivered = result && result.delivered === true;
      notice(context, delivered ? "success" : "warning", delivered ? "周报已生成" : "周报暂未生成",
        delivered ? "新的周报通知已加入消息中心。" : first(result && result.reason, "近期活动还不够生成周报。"));
      setPage(context, 1);
      refreshMessages(item, context, null, "");
    }).catch(function (error) {
      clearBusyButtons(context);
      notice(context, "warning", "周报生成失败", messageOf(error));
    });
  }

  function openMessageDetail(context, notificationId) {
    if (!notificationId) return;
    setSelectedMessage(context, notificationId);
    applyRead(context, notificationId);
    context.post(context.endpoints.notificationRead, {
      userId: userIdOf(context),
      notificationId: notificationId
    }).catch(function () {});
    window.location.hash = "message-detail";
  }

  function refreshMessages(item, context, button, busyText) {
    setButtonBusy(button, busyText);
    return loadMessages(context).then(function (data) {
      storeData(context, data);
      renderMessages(item, context, data);
      bindMessages(item, context);
    }).catch(function (error) {
      clearBusyButtons(context);
      notice(context, "warning", "刷新失败", messageOf(error));
    });
  }

  function pageSlice(context, notifications) {
    var totalPages = Math.max(1, Math.ceil(notifications.length / PAGE_SIZE));
    var page = Math.min(Math.max(1, currentPage(context)), totalPages);
    pageByUser[userIdOf(context) || "anonymous"] = page;
    var start = (page - 1) * PAGE_SIZE;
    return {
      page: page,
      totalPages: totalPages,
      items: notifications.slice(start, start + PAGE_SIZE)
    };
  }

  function currentPage(context) {
    return numberOf(pageByUser[userIdOf(context) || "anonymous"]) || 1;
  }

  function setPage(context, value) {
    pageByUser[userIdOf(context) || "anonymous"] = Math.max(1, numberOf(value) || 1);
  }

  function currentData(context) {
    return dataByUser[userIdOf(context) || "anonymous"] || null;
  }

  function storeData(context, data) {
    dataByUser[userIdOf(context) || "anonymous"] = data || emptyData();
  }

  function setSelectedMessage(context, notificationId) {
    var key = userIdOf(context) || "anonymous";
    selectedMessageByUser[key] = text(notificationId);
    try {
      window.sessionStorage.setItem("cyancruise.selectedMessage." + key, text(notificationId));
    } catch (ignored) {}
  }

  function selectedMessage(context, notifications) {
    var key = userIdOf(context) || "anonymous";
    var selectedId = selectedMessageByUser[key];
    if (!selectedId) {
      try {
        selectedId = window.sessionStorage.getItem("cyancruise.selectedMessage." + key) || "";
      } catch (ignored) {}
    }
    for (var i = 0; i < (notifications || []).length; i += 1) {
      if (text(notifications[i].notificationId) === text(selectedId)) return notifications[i];
    }
    return null;
  }

  function emptyData() {
    return {
      notifications: [],
      unread: 0,
      quotas: []
    };
  }

  function applyRead(context, notificationId) {
    var data = currentData(context);
    if (!data) return;
    var notifications = data.notifications || [];
    for (var i = 0; i < notifications.length; i += 1) {
      if (text(notifications[i].notificationId) === text(notificationId)) {
        if (notifications[i].readFlag !== true) {
          data.unread = Math.max(0, numberOf(data.unread) - 1);
        }
        notifications[i].readFlag = true;
        return;
      }
    }
  }

  function applyAllRead(context) {
    var data = currentData(context);
    if (!data) return;
    var notifications = data.notifications || [];
    for (var i = 0; i < notifications.length; i += 1) {
      notifications[i].readFlag = true;
    }
    data.unread = 0;
  }

  function applyArchive(context, notificationId) {
    var data = currentData(context);
    if (!data) return;
    var next = [];
    var removedUnread = 0;
    var notifications = data.notifications || [];
    for (var i = 0; i < notifications.length; i += 1) {
      if (text(notifications[i].notificationId) === text(notificationId)) {
        removedUnread = notifications[i].readFlag === true ? 0 : 1;
      } else {
        next.push(notifications[i]);
      }
    }
    data.notifications = next;
    data.unread = Math.max(0, numberOf(data.unread) - removedUnread);
  }

  function actionButton(context, action) {
    if (!context.pageHost) return null;
    return context.pageHost.querySelector('[data-message-action="' + action + '"]');
  }

  function targetButton(context, notificationId, action) {
    if (!context.pageHost) return null;
    return context.pageHost.querySelector('[data-message-action="' + action + '"][data-message-id="' + esc(context, notificationId) + '"]');
  }

  function setButtonBusy(button, label) {
    if (!button) return;
    if (!button.getAttribute("data-original-label")) {
      button.setAttribute("data-original-label", button.textContent || "");
    }
    button.disabled = true;
    button.classList.add("loading");
    if (label) {
      button.textContent = label;
    }
  }

  function clearBusyButtons(context) {
    if (!context.pageHost) return;
    var buttons = context.pageHost.querySelectorAll("[data-original-label]");
    for (var i = 0; i < buttons.length; i += 1) {
      buttons[i].disabled = false;
      buttons[i].classList.remove("loading");
      buttons[i].textContent = buttons[i].getAttribute("data-original-label");
      buttons[i].removeAttribute("data-original-label");
    }
  }

  function summaryCard(context, title, value, note, tone, icon) {
    return '<article class="message-summary-card ' + esc(context, tone || "total") + '"><div><span>' + esc(context, title) + '</span>' +
      '<strong>' + esc(context, value) + '</strong><p>' + esc(context, note) + '</p></div><i aria-hidden="true">' + esc(context, icon || "信") + '</i></article>';
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
    if (value === "ADMIN_BROADCAST") return "管理员公告";
    if (value === "WEEKLY_REPORT") return "每周回顾";
    if (value === "INTERVIEW_REPORT") return "面试复盘";
    if (value === "ASSESSMENT_RESULT") return "测评结果";
    if (value === "RESUME_DIAGNOSIS") return "简历诊断";
    if (value === "AI_PROACTIVE") return "智能提醒";
    if (value === "STREAK_WARNING") return "练习提醒";
    return "系统消息";
  }

  function groupLabel(groupKey, type) {
    var group = text(groupKey).toUpperCase();
    var value = text(type).toUpperCase();
    if (value === "ADMIN_BROADCAST") return "公告";
    if (group === "CAREER" || value === "INTERVIEW_REPORT" || value === "ASSESSMENT_RESULT" || value === "RESUME_DIAGNOSIS" || value === "WEEKLY_REPORT") return "职业发展";
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
