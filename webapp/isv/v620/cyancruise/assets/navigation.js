(function (window) {
  "use strict";

  var parentRoutes = {
    "employment-home": "workbench",
    "further-study-home": "workbench",
    "study-resources": "further-study-home",
    resume: "employment-home",
    "resume-diagnosis": "employment-home",
    interview: "",
    "interview-history": "interview",
    "interview-panorama": "",
    "interview-panorama-history": "interview-panorama",
    postgraduate: "",
    "postgraduate-school": "",
    "postgraduate-plan": "",
    "postgraduate-mistake": "",
    "postgraduate-reexam": "",
    "postgraduate-recommendation": "",
    "recommendation-ranking": "",
    "recommendation-background": "",
    "recommendation-material": "",
    "recommendation-tutor": "",
    "study-abroad": "",
    "study-abroad-profile": "",
    "study-abroad-language": "",
    "study-abroad-school": "",
    "study-abroad-statement": "",
    "study-abroad-visa": "",
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
      return this.parentRouteFor(route);
    }
  };
}(window));
