(function (window) {
  "use strict";
  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};
  registerPage("assessment", ["assessment"], "职业测评");
  attachRenderer("assessment", function (item, context) {
    context.renderers.renderAssessmentPage(item);
  });
}(window));
