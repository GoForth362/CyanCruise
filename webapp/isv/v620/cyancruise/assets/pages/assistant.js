(function (window) {
  "use strict";
  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};
  registerPage("assistant", ["assistant"], "求职助手");
  attachRenderer("assistant", function (item, context) {
    context.renderers.renderAssistantPage(item);
  });
}(window));
