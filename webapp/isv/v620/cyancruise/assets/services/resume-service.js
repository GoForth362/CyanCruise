(function (window) {
  "use strict";

  var services = window.CYANCRUISE_SERVICES = window.CYANCRUISE_SERVICES || {};

  services.resume = {
    endpoints: ["resumes", "resumeCreate", "resumeDelete", "resumeDiagnosis", "resumeDiagnosisHistory", "resumeDiagnosisHistoryDelete", "keywordStatus", "filePreview"],
    description: "简历列表、创建、删除和诊断接口调用服务。",
    list: function (context) {
      return context.post(context.endpoints.resumes, context.userId);
    },
    create: function (context) {
      return context.post(context.endpoints.resumeCreate, {
        userId: context.userId,
        request: context.request
      });
    },
    deleteRecord: function (context) {
      return context.post(context.endpoints.resumeDelete, {
        userId: context.userId,
        resumeId: context.resumeId
      });
    },
    refreshSnapshot: function (context) {
      return context.post(context.endpoints.snapshot, context.userId);
    },
    diagnose: function (context) {
      return context.post(context.endpoints.resumeDiagnosis, {
        userId: context.userId,
        request: context.request
      });
    },
    listDiagnosisHistory: function (context) {
      return context.post(context.endpoints.resumeDiagnosisHistory, {
        userId: context.userId,
        resumeId: context.resumeId
      });
    },
    deleteDiagnosisHistory: function (context) {
      return context.post(context.endpoints.resumeDiagnosisHistoryDelete, {
        userId: context.userId,
        resumeId: context.resumeId,
        diagnosisId: context.diagnosisId
      });
    }
  };
}(window));
