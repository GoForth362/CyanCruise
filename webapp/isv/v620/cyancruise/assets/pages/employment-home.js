(function (window) {
  "use strict";
  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};
  registerPage("employment-home", ["employment-home"], "就业首页");
  attachRenderer("employment-home", function (item, context) {
    context.renderers.renderEmploymentHome(item);
  });
}(window));
