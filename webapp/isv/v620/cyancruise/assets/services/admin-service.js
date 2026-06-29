(function (window) {
  "use strict";

  var services = window.CYANCRUISE_SERVICES = window.CYANCRUISE_SERVICES || {};

  function adminId(identity) {
    if (!identity) {
      return "";
    }
    return trim(identity.adminId) || trim(identity.userId);
  }

  function load(context) {
    var identity = context.identity || {};
    var id = adminId(identity);
    var endpoints = context.endpoints || {};
    var post = context.post;
    var orgId = context.orgId || "";
    var pageSize = context.pageSize || 20;

    return whoami(context).then(function (who) {
      if (!who || who.status !== "OK") {
        return {
          authorized: false,
          whoami: who || {},
          dashboard: {},
          analytics: {},
          users: emptyPage(pageSize),
          questions: [],
          content: [],
          auditLogs: emptyPage(pageSize),
          message: (who && who.message) || "当前账号没有管理后台权限。"
        };
      }
      return Promise.all([
        safe(post, endpoints.adminDashboard, { adminId: id, orgId: orgId }, {}),
        safe(post, endpoints.adminAnalytics, id, {}),
        safe(post, endpoints.adminUsers, { adminId: id, page: 0, size: pageSize, keyword: "" }, emptyPage(pageSize)),
        safe(post, endpoints.adminQuestions, { adminId: id, source: "", reviewStatus: "" }, []),
        safe(post, endpoints.adminContent, { adminId: id, type: "" }, []),
        safe(post, endpoints.adminAuditLog, { adminId: id, page: 0, size: pageSize }, emptyPage(pageSize))
      ]).then(function (results) {
        return {
          authorized: true,
          adminId: id,
          whoami: who,
          dashboard: results[0] || {},
          analytics: results[1] || {},
          users: normalizePage(results[2], pageSize),
          questions: normalizeArray(results[3]),
          content: normalizeArray(results[4]),
          auditLogs: normalizePage(results[5], pageSize),
          message: ""
        };
      });
    });
  }

  function whoami(context) {
    var endpoints = context.endpoints || {};
    return safe(context.post, endpoints.adminWhoami, adminId(context.identity), {
      status: "IDENTITY_REQUIRED",
      admin: false,
      message: "未识别到当前登录身份。"
    });
  }

  function banUser(context, userId, reason) {
    return action(context, context.endpoints.adminUsersBan, {
      adminId: adminId(context.identity),
      userId: userId,
      reason: reason || "管理员手动限制"
    });
  }

  function unbanUser(context, userId) {
    return action(context, context.endpoints.adminUsersUnban, {
      adminId: adminId(context.identity),
      userId: userId
    });
  }

  function approveQuestion(context, questionId) {
    return action(context, context.endpoints.adminQuestionApprove, {
      adminId: adminId(context.identity),
      questionId: questionId
    });
  }

  function rejectQuestion(context, questionId) {
    return action(context, context.endpoints.adminQuestionReject, {
      adminId: adminId(context.identity),
      questionId: questionId
    });
  }

  function toggleContentPin(context, contentId) {
    return action(context, context.endpoints.adminContentPin, {
      adminId: adminId(context.identity),
      contentId: contentId
    });
  }

  function toggleContentHidden(context, contentId) {
    return action(context, context.endpoints.adminContentHide, {
      adminId: adminId(context.identity),
      contentId: contentId
    });
  }

  function broadcast(context, request) {
    return action(context, context.endpoints.adminBroadcast, {
      adminId: adminId(context.identity),
      request: request || {}
    });
  }

  function action(context, endpoint, body) {
    return context.post(endpoint, body).then(function (result) {
      if (result && (result.status === "FORBIDDEN" || result.status === "IDENTITY_REQUIRED" || result.status === "FAILED")) {
        throw new Error(result.message || "管理操作未完成。");
      }
      return result;
    });
  }

  function safe(post, endpoint, body, fallback) {
    if (!endpoint || typeof post !== "function") {
      return Promise.resolve(fallback);
    }
    return post(endpoint, body).catch(function () {
      return fallback;
    });
  }

  function normalizePage(page, size) {
    page = page || {};
    return {
      items: normalizeArray(page.items),
      page: number(page.page, 0),
      size: number(page.size, size || 20),
      total: number(page.total, 0)
    };
  }

  function emptyPage(size) {
    return { items: [], page: 0, size: size || 20, total: 0 };
  }

  function normalizeArray(value) {
    return Array.isArray(value) ? value : [];
  }

  function number(value, fallback) {
    var parsed = Number(value);
    return isFinite(parsed) ? parsed : fallback;
  }

  function trim(value) {
    return value == null ? "" : String(value).trim();
  }

  services.admin = {
    adminId: adminId,
    load: load,
    whoami: whoami,
    banUser: banUser,
    unbanUser: unbanUser,
    approveQuestion: approveQuestion,
    rejectQuestion: rejectQuestion,
    toggleContentPin: toggleContentPin,
    toggleContentHidden: toggleContentHidden,
    broadcast: broadcast
  };
}(window));
