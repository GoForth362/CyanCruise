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
      var success = type === "success" || (type === "info" && isSuccessTitle(title));
      var resolvedType = success ? "success" : type;
      panel.className = "message-panel " + resolvedType;
      panel.innerHTML =
        '<div class="message-copy"><strong>' + escapeHtml(title) + "</strong><span>" + escapeHtml(text) + "</span></div>" +
        '<button type="button" class="message-close" aria-label="关闭提示">&times;</button>';
      var close = panel.querySelector(".message-close");
      if (close) {
        close.addEventListener("click", hide);
      }
      if (success) {
        state.messageTimer = window.setTimeout(hide, 3000);
      } else if (type === "info") {
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

  function isSuccessTitle(title) {
    return /已保存|保存成功|已生成|生成成功|已创建|创建成功|已删除|删除成功|已完成|更新成功|已更新|已准备|已取消/.test(String(title || ""));
  }
}(window));
