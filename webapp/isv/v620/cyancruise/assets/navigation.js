(function (window) {
  "use strict";

  var parentRoutes = {
    "employment-home": "workbench",
    "further-study-home": "workbench",
    "study-resources": "further-study-home",
    resume: "",
    "resume-diagnosis": "",
    interview: "",
    "interview-history": "interview",
    "interview-panorama": "",
    "interview-panorama-history": "interview-panorama",
    postgraduate: "",
    "postgraduate-school": "postgraduate",
    "postgraduate-plan": "postgraduate",
    "postgraduate-mistake": "postgraduate",
    "postgraduate-reexam": "postgraduate",
    "postgraduate-recommendation": "",
    "recommendation-ranking": "postgraduate-recommendation",
    "recommendation-background": "postgraduate-recommendation",
    "recommendation-material": "postgraduate-recommendation",
    "recommendation-tutor": "postgraduate-recommendation",
    "study-abroad": "",
    "study-abroad-profile": "study-abroad",
    "study-abroad-language": "study-abroad",
    "study-abroad-school": "study-abroad",
    "study-abroad-statement": "study-abroad",
    "study-abroad-visa": "study-abroad",
    "today-action": "workbench",
    assessment: "workbench",
    "deep-profile-detail": "assessment",
    "career-plan": "workbench",
    assistant: "workbench",
    messages: "workbench",
    "message-detail": "messages",
    "employment-insight": "employment-home",
    "career-resources": "employment-home",
    "admin-console": "workbench",
    "file-upload-preview": "resume"
  };

  window.CYANCRUISE_NAVIGATION = {
    parentRoutes: parentRoutes,
    normalizeRoute: function (route) {
      if (route === "resume-home") {
        return "resume";
      }
      if (route === "interview-home") {
        return "interview";
      }
      return route || "workbench";
    },
    parentRouteFor: function (route) {
      return Object.prototype.hasOwnProperty.call(parentRoutes, route) ? parentRoutes[route] : "workbench";
    },
    backRouteFor: function (route) {
      if (route === "resume" || route === "resume-diagnosis") {
        return "";
      }
      return this.parentRouteFor(route);
    }
  };
}(window));
