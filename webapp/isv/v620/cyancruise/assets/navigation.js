(function (window) {
  "use strict";

  var parentRoutes = {
    "employment-home": "workbench",
    "further-study-home": "workbench",
    "resume-home": "employment-home",
    resume: "resume-home",
    "resume-diagnosis": "resume-home",
    "interview-home": "employment-home",
    interview: "interview-home",
    "interview-history": "interview",
    "interview-panorama": "interview-home",
    "interview-panorama-history": "interview-home",
    postgraduate: "further-study-home",
    "postgraduate-recommendation": "further-study-home",
    "study-abroad": "further-study-home",
    "today-action": "workbench",
    assessment: "workbench",
    "career-plan": "workbench",
    assistant: "workbench",
    messages: "workbench",
    "employment-insight": "employment-home",
    "career-resources": "employment-home",
    "admin-console": "workbench",
    "file-upload-preview": "resume-home"
  };

  window.CYANCRUISE_NAVIGATION = {
    parentRoutes: parentRoutes,
    normalizeRoute: function (route) {
      return route || "workbench";
    },
    parentRouteFor: function (route) {
      return parentRoutes[route] || "workbench";
    },
    backRouteFor: function (route, context) {
      var source = context.returnRoutes && context.returnRoutes[route];
      if (source && source !== route && context.pageByKey[source]) {
        return source;
      }
      return this.parentRouteFor(route);
    }
  };
}(window));
