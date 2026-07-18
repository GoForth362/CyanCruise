(function (window) {
  "use strict";

  window.CYANCRUISE_API_CONFIG = {
    endpoints: {
      snapshot: "/cc001/career-profile/snapshot/get",
      draft: "/cc001/career-profile/draft/get",
      draftSave: "/cc001/career-profile/draft/save",
      draftClear: "/cc001/career-profile/draft/clear",
      onboarding: "/cc001/career-profile/onboarding/save",
      today: "/cc001/career-agent/today/get",
      assessmentScales: "/cc001/assessment/scales",
      assessmentQuestions: "/cc001/assessment/questions",
      assessmentSubmit: "/cc001/assessment/submit",
      assessmentRecords: "/cc001/assessment/records",
      assessmentRecord: "/cc001/assessment/record/get",
      deepProfileGenerate: "/cc001/career-profile/deep-profile/generate",
      deepProfileLatest: "/cc001/career-profile/deep-profile/latest",
      deepProfileHistory: "/cc001/career-profile/deep-profile/history",
      deepProfileDetail: "/cc001/career-profile/deep-profile/detail",
      resumes: "/cc001/resume/list",
      resumeCreate: "/cc001/resume/create",
      resumeDelete: "/cc001/resume/delete",
      plan: "/cc001/career-plan/summary",
      ensurePlan: "/cc001/career-plan/ensure",
      generatePlan: "/cc001/career-plan/generate",
      dailyPlan: "/cc001/career-plan/daily/get",
      dailyTaskUpdate: "/cc001/career-plan/daily/task/update",
      studyPlan: "/cc001/study-center/plan/summary",
      studyEnsurePlan: "/cc001/study-center/plan/ensure",
      studyGeneratePlan: "/cc001/study-center/plan/generate",
      studyDailyPlan: "/cc001/study-center/daily/get",
      studyDailyTaskUpdate: "/cc001/study-center/daily/task/update",
      studyMaterialUpload: "/cc001/study-center/materials/upload",
      studyMaterialList: "/cc001/study-center/materials/list",
      studyMaterialDelete: "/cc001/study-center/materials/delete",
      interviews: "/cc001/interview/list",
      interviewPage: "/cc001/interview/page",
      startInterview: "/cc001/interview/start",
      guidedInterviewStart: "/cc001/interview/guided/start",
      guidedInterviewAnswer: "/cc001/interview/guided/answer",
      guidedInterviewFinish: "/cc001/interview/guided/finish",
      interviewMessages: "/cc001/interview/messages",
      interviewDelete: "/cc001/interview/delete",
      assistantSend: "/cc001/assistant-chat/send",
      assistantSessions: "/cc001/assistant-chat/session/list",
      employmentInsight: "/cc001/career-employment/insight/get",
      careerResources: "/cc001/career-employment/resources/list",
      studyCenterSelection: "/cc001/study-center/selection/get",
      studyCenterSelectionSave: "/cc001/study-center/selection/save",
      studyCenterInsight: "/cc001/study-center/insight/get",
      studyCenterResources: "/cc001/study-center/resources/list",
      notifications: "/cc001/notifications/list",
      notificationUnread: "/cc001/notifications/unread-count",
      notificationRead: "/cc001/notifications/read",
      notificationReadAll: "/cc001/notifications/read-all",
      notificationDelete: "/cc001/notifications/delete",
      subscriptionQuota: "/cc001/notifications/subscription/quota",
      weeklyReport: "/cc001/notifications/weekly-report/run",
      adminWhoami: "/cc001/admin/whoami",
      adminOrganizations: "/cc001/admin/organizations/list",
      adminDashboard: "/cc001/admin/organizations/dashboard",
      adminUsers: "/cc001/admin/users/list",
      adminUsersBan: "/cc001/admin/users/ban",
      adminUsersUnban: "/cc001/admin/users/unban",
      adminQuestions: "/cc001/admin/questions/list",
      adminQuestionSave: "/cc001/admin/questions/save",
      adminQuestionUpdate: "/cc001/admin/questions/update",
      adminQuestionApprove: "/cc001/admin/questions/approve",
      adminQuestionReject: "/cc001/admin/questions/reject",
      adminQuestionDelete: "/cc001/admin/questions/delete",
      adminAssessmentQuestionSave: "/cc001/admin/assessment/questions/save",
      adminAssessmentQuestionDelete: "/cc001/admin/assessment/questions/delete",
      adminAssessmentCatalog: "/cc001/admin/assessment/catalog",
      adminAssessmentScaleSave: "/cc001/admin/assessment/scales/save",
      adminContent: "/cc001/admin/content/list",
      adminContentSave: "/cc001/admin/content/save",
      adminContentPin: "/cc001/admin/content/pin",
      adminContentHide: "/cc001/admin/content/hide",
      adminContentDelete: "/cc001/admin/content/delete",
      studyCenterAdminResources: "/cc001/study-center/admin/resources/list",
      studyCenterAdminResourceSave: "/cc001/study-center/admin/resources/save",
      studyCenterAdminResourcePin: "/cc001/study-center/admin/resources/pin",
      studyCenterAdminResourceHide: "/cc001/study-center/admin/resources/hide",
      studyCenterAdminResourceDelete: "/cc001/study-center/admin/resources/delete",
      adminBroadcast: "/cc001/admin/broadcast",
      adminAnalytics: "/cc001/admin/analytics/summary",
      adminAuditLog: "/cc001/admin/audit-log/list",
      identityCurrent: "/cc001/identity/current",
      fileUpload: "/cc001/files/upload",
      filePreview: "/cc001/files/preview-url",
      fileDownload: "/cc001/files/download",
      fileDelete: "/cc001/files/delete",
      fileExtractText: "/cc001/files/extract-text",
      resumeDiagnosis: "/cc001/resume-diagnosis/analyze",
      resumeDiagnosisHistory: "/cc001/resume-diagnosis/history/list",
      resumeDiagnosisHistoryDelete: "/cc001/resume-diagnosis/history/delete",
      keywordStatus: "/cc001/resume-diagnosis/keywords/status",
      postgraduateSchoolRecommend: "/cc001/postgraduate/school-recommend",
      postgraduatePlanGenerate: "/cc001/postgraduate/plan/generate",
      postgraduateMistakeAnalyze: "/cc001/postgraduate/mistake/analyze",
      postgraduateReexamPrepare: "/cc001/postgraduate/reexam/prepare",
      recommendationDiagnose: "/cc001/recommendation/diagnose",
      recommendationPlanGenerate: "/cc001/recommendation/plan/generate",
      recommendationDocumentPolish: "/cc001/recommendation/document/polish",
      recommendationTutorLetterGenerate: "/cc001/recommendation/tutor-letter/generate",
      studyAbroadProfileDiagnose: "/cc001/study-abroad/profile/diagnose",
      studyAbroadLanguagePlan: "/cc001/study-abroad/language/plan",
      studyAbroadSchoolPosition: "/cc001/study-abroad/school/position",
      studyAbroadStatementOutline: "/cc001/study-abroad/statement/outline",
      studyAbroadVisaChecklist: "/cc001/study-abroad/visa/checklist",
      furtherStudyRecordsList: "/cc001/further-study/records/list",
      furtherStudyRecordDetail: "/cc001/further-study/records/detail",
      furtherStudyRecordStatusUpdate: "/cc001/further-study/records/status/update",
      furtherStudyMaterialSave: "/cc001/further-study/materials/save",
      furtherStudyMaterialList: "/cc001/further-study/materials/list",
      furtherStudyRecordEvents: "/cc001/further-study/records/events",
      serverManagedApiCode: "cc001/cyancruise/route"
    }
  };

  window.CYANCRUISE_API_CLIENT = {
    postRequest: function (request, path, firstText) {
      return fetch(request.url, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "same-origin",
        body: JSON.stringify(request.body)
      }).then(function (response) {
        if (!response.ok) {
          throw new Error("服务暂不可用，请稍后重试。");
        }
        return response.json();
      }).then(function (payload) {
        var mode = request.mode;
        if (mode === "kapi" || mode === "kapi-v2" || mode === "server" || mode === "server-kapi-v2") {
          if (payload && Object.prototype.hasOwnProperty.call(payload, "success")) {
            if (!payload.success) {
              throw new Error(firstText(payload.message, payload.errorCode, "服务暂不可用，请稍后重试。"));
            }
            return payload.data;
          }
          if (payload && Object.prototype.hasOwnProperty.call(payload, "status")
              && Object.prototype.hasOwnProperty.call(payload, "data")) {
            if (!payload.status) {
              throw new Error(firstText(payload.message, payload.errorCode, "服务暂不可用，请稍后重试。"));
            }
            return payload.data;
          }
        }
        return payload;
      });
    }
  };
}(window));
