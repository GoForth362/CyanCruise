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
    var auditPageSize = context.auditPageSize || 10;

    return whoami(context).then(function (who) {
      if (!who || who.status !== "OK") {
        return {
          authorized: false,
          whoami: who || {},
          dashboard: {},
          analytics: {},
          users: emptyPage(pageSize),
          questions: [],
          assessmentScales: [],
          assessmentQuestionBanks: [],
          content: [],
          auditLogs: emptyPage(auditPageSize),
          message: (who && who.message) || "当前账号没有管理后台权限。"
        };
      }
      return Promise.all([
        safe(post, endpoints.adminDashboard, { adminId: id, orgId: orgId }, {}),
        safe(post, endpoints.adminAnalytics, id, {}),
        safe(post, endpoints.adminUsers, { adminId: id, page: 0, size: pageSize, keyword: "" }, emptyPage(pageSize)),
        safe(post, endpoints.adminQuestions, { adminId: id, source: "", reviewStatus: "" }, []),
        loadAssessmentBanks(context),
        safe(post, endpoints.adminContent, { adminId: id, type: "" }, []),
        safe(post, endpoints.adminAuditLog, { adminId: id, page: 0, size: auditPageSize }, emptyPage(auditPageSize))
      ]).then(function (results) {
        return {
          authorized: true,
          adminId: id,
          whoami: who,
          dashboard: results[0] || {},
          analytics: results[1] || {},
          users: normalizePage(results[2], pageSize),
          questions: normalizeArray(results[3]),
          assessmentScales: normalizeArray(results[4] && results[4].scales),
          assessmentQuestionBanks: normalizeArray(results[4] && results[4].banks),
          content: normalizeArray(results[5]),
          auditLogs: normalizePage(results[6], auditPageSize),
          message: ""
        };
      });
    });
  }

  function loadAssessmentBanks(context) {
    var endpoints = context.endpoints || {};
    var post = context.post;
    return safe(post, endpoints.assessmentScales, {}, []).then(function (scales) {
      scales = normalizeArray(scales);
      return Promise.all(scales.map(function (scale) {
        return safe(post, endpoints.assessmentQuestions, { scaleId: scale.scaleId }, scale);
      })).then(function (banks) {
        return {
          scales: scales,
          banks: normalizeArray(banks)
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

  function saveQuestion(context, question) {
    return action(context, context.endpoints.adminQuestionSave, {
      adminId: adminId(context.identity),
      question: question || {}
    });
  }

  function updateQuestion(context, questionId, patch) {
    return action(context, context.endpoints.adminQuestionUpdate, {
      adminId: adminId(context.identity),
      questionId: questionId,
      patch: patch || {}
    });
  }

  function deleteQuestion(context, questionId) {
    return action(context, context.endpoints.adminQuestionDelete, {
      adminId: adminId(context.identity),
      questionId: questionId
    });
  }

  function saveAssessmentQuestion(context, scaleId, question) {
    return action(context, context.endpoints.adminAssessmentQuestionSave, {
      adminId: adminId(context.identity),
      scaleId: scaleId,
      question: question || {}
    });
  }

  function deleteAssessmentQuestion(context, scaleId, questionId) {
    return action(context, context.endpoints.adminAssessmentQuestionDelete, {
      adminId: adminId(context.identity),
      scaleId: scaleId,
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

  function saveContent(context, content) {
    return action(context, context.endpoints.adminContentSave, {
      adminId: adminId(context.identity),
      content: content || {}
    });
  }

  function deleteContent(context, contentId) {
    return action(context, context.endpoints.adminContentDelete, {
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

  function listUsers(context, keyword, size) {
    return context.post(context.endpoints.adminUsers, {
      adminId: adminId(context.identity),
      page: 0,
      size: size || context.pageSize || 20,
      keyword: keyword || ""
    }).then(function (result) {
      return normalizePage(result, size || context.pageSize || 20);
    });
  }

  function auditLogs(context, page, size) {
    return context.post(context.endpoints.adminAuditLog, {
      adminId: adminId(context.identity),
      page: page || 0,
      size: size || 10
    }).then(function (result) {
      return normalizePage(result, size || 10);
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
    saveQuestion: saveQuestion,
    updateQuestion: updateQuestion,
    deleteQuestion: deleteQuestion,
    saveAssessmentQuestion: saveAssessmentQuestion,
    deleteAssessmentQuestion: deleteAssessmentQuestion,
    saveContent: saveContent,
    toggleContentPin: toggleContentPin,
    toggleContentHidden: toggleContentHidden,
    deleteContent: deleteContent,
    broadcast: broadcast,
    listUsers: listUsers,
    auditLogs: auditLogs
  };
}(window));
