(function (window) {
  "use strict";

  var components = window.CYANCRUISE_COMPONENTS = window.CYANCRUISE_COMPONENTS || {};

  components.message = {
    name: "message",
    description: "顶部提示和错误提示组件，承接 showMessage/hideMessage。",
    show: function (type, title, text, context) {
      var panel = context.panel;
      var state = context.state;
      var escapeHtml = context.escapeHtml;
      var hide = context.hide;
      if (state.messageTimer) {
        window.clearTimeout(state.messageTimer);
        state.messageTimer = null;
      }
      panel.className = "message-panel " + type;
      panel.innerHTML =
        '<div class="message-copy"><strong>' + escapeHtml(title) + "</strong><span>" + escapeHtml(text) + "</span></div>" +
        '<button type="button" class="message-close" aria-label="关闭提示">&times;</button>';
      var close = panel.querySelector(".message-close");
      if (close) {
        close.addEventListener("click", hide);
      }
      if (type === "info") {
        state.messageTimer = window.setTimeout(hide, 5000);
      }
    },
    hide: function (context) {
      var panel = context.panel;
      var state = context.state;
      if (state.messageTimer) {
        window.clearTimeout(state.messageTimer);
        state.messageTimer = null;
      }
      panel.className = "message-panel hidden";
      panel.innerHTML = "";
    }
  };
}(window));
