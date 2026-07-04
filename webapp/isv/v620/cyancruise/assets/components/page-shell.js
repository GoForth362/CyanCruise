(function (window) {
  "use strict";

  var components = window.CYANCRUISE_COMPONENTS = window.CYANCRUISE_COMPONENTS || {};

  components.pageShell = {
    name: "page-shell",
    description: "页面壳组件，承接普通页面、功能页面和页面顶部动作按钮。",
    feature: function (item, title, summary, innerHtml, context) {
      context.host.innerHTML =
        '<header class="feature-page-header">' +
        '<div><p class="eyebrow">CyanCruise</p><h2>' + context.escapeHtml(title) + '</h2><p class="lead">' + context.escapeHtml(summary) + '</p></div>' +
        this.actions(item, context) +
        '</header>' +
        '<div class="feature-content">' + innerHtml + '</div>';
    },
    shell: function (item, innerHtml, context) {
      var debugMeta = "";
      if (context.isDebugMode()) {
        var chips = [
          '<span class="chip">' + context.escapeHtml(item.status) + "</span>",
          '<span class="chip">' + context.escapeHtml(item.audience) + "</span>"
        ];
        if (item.status === "entry-only") {
          chips.push('<span class="chip warning">entry-only</span>');
        }
        debugMeta = '<p class="eyebrow">Route: ' + context.escapeHtml(item.key) + '</p><div class="route-meta">' + chips.join("") + '</div>';
      }
      context.host.innerHTML =
        '<header class="page-header">' +
        '<div>' + debugMeta + '<h2>' + context.escapeHtml(item.title) + '</h2>' +
        '<p class="lead">' + context.escapeHtml(item.summary) + '</p></div>' +
        this.actions(item, context) +
        '</header><div class="panel-grid">' + innerHtml + '</div>';
    },
    actions: function (item, context) {
      if (item.key === "workbench" || (item.key === "assessment" && context.state.assessmentSelectedScaleId)) {
        return "";
      }
      var back = context.backRouteFor(item.key);
      var actions = [];
      if (item.key === "interview" && (context.state.activeInterview || context.state.interviewReport || context.state.interviewViewMode === "transcript")) {
        actions.push('<button type="button" class="secondary" data-interview-action="leave">返回 AI 面试中心</button>');
      } else if (back) {
        actions.push('<button type="button" class="secondary" data-back-route="' + context.escapeHtml(back) + '">返回</button>');
      }
      actions.push('<button type="button" class="secondary" data-link="workbench">首页</button>');
      return actions.length ? '<div class="page-actions">' + actions.join("") + '</div>' : "";
    }
  };
}(window));
