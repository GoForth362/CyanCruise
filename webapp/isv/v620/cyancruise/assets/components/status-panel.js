(function (window) {
  "use strict";

  var components = window.CYANCRUISE_COMPONENTS = window.CYANCRUISE_COMPONENTS || {};

  components.statusPanel = {
    name: "status-panel",
    description: "状态面板组件，承接 statePanel 渲染逻辑。",
    render: function (title, text, type, context) {
      var escapeHtml = context.escapeHtml;
      var cls = type === "warning" || type === "pending" ? " warning" : "";
      return '<section class="state-card' + cls + '"><h3>' + escapeHtml(title) + '</h3><p>' + escapeHtml(text) + "</p></section>";
    }
  };
}(window));
