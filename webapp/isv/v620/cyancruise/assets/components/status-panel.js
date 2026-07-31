(function (window) {
  "use strict";

  var components = window.CYANCRUISE_COMPONENTS = window.CYANCRUISE_COMPONENTS || {};

  components.statusPanel = {
    name: "status-panel",
    description: "状态面板组件，承接 statePanel 渲染逻辑。",
    render: function (title, text, type, context) {
      var escapeHtml = context.escapeHtml;
      var cls = type === "warning" || type === "pending" ? " warning" : (type === "error" ? " error" : " info");
      return '<section class="state-card unified-notice' + cls + '" role="status" data-page-operation-notice data-notice-type="' + (type === "info" ? "info" : type) + '" data-notice-state-key="">' +
        '<div class="notice-copy"><strong>' + escapeHtml(title) + '</strong><span>' + escapeHtml(text) + '</span></div>' +
        '<button type="button" class="notice-close" data-dismiss-inline-notice aria-label="关闭提示">&times;</button></section>';
    }
  };
}(window));
