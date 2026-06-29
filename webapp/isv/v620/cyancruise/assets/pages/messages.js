(function (window) {
  "use strict";
  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};
  registerPage("messages", ["messages"], "消息中心");
  attachRenderer("messages", function (item, context) {
    context.renderers.renderMessagesPage(item);
  });
}(window));
