(function (window) {
  "use strict";
  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};
  registerPage("interview", ["interview", "interview-history"], "AI 模拟面试");
  attachRenderer("interview", function (item, context) {
    context.renderers.renderInterviewPage(item);
  });
  attachRenderer("interview-history", function (item, context) {
    context.renderers.renderInterviewHistoryPage(item);
  });
}(window));
