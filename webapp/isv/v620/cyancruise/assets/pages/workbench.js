(function (window) {
  "use strict";
  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};
  registerPage("workbench", ["workbench"], "首页");
  attachRenderer("workbench", function (item, context) {
    context.renderers.renderWorkbench(item);
  });
}(window));
