(function (window) {
  "use strict";
  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};
  registerPage("interview-home", ["interview-home"], "面试中心");
  attachRenderer("interview-home", function (item, context) {
    context.renderers.renderInterviewHub(item);
  });
}(window));
