(function (window) {
  "use strict";

  var services = window.CYANCRUISE_SERVICES = window.CYANCRUISE_SERVICES || {};

  services.furtherStudy = {
    endpoints: [
      "postgraduateSchoolRecommend",
      "postgraduatePlanGenerate",
      "postgraduateMistakeAnalyze",
      "postgraduateReexamPrepare",
      "recommendationDiagnose",
      "recommendationPlanGenerate",
      "recommendationDocumentPolish",
      "recommendationTutorLetterGenerate",
      "studyAbroadProfileDiagnose",
      "studyAbroadLanguagePlan",
      "studyAbroadSchoolPosition",
      "studyAbroadStatementOutline",
      "studyAbroadVisaChecklist"
    ],
    description: "考研、保研、留学陪伴接口调用服务。",
    run: function (context) {
      return context.post(context.endpoint, context.body);
    }
  };
}(window));
