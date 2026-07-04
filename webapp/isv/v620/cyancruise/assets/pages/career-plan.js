(function (window) {
  "use strict";
  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};
  registerPage("career-plan", ["career-plan", "today-action"], "路径规划");
  attachRenderer("career-plan", function (item, context) {
    context.renderers.renderCareerPlanPage(item);
  });
  attachRenderer("today-action", function (item, context) {
    context.renderers.renderTodayPage(item);
  });
}(window));
