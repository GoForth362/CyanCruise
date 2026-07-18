(function (window) {
  "use strict";

  var components = window.CYANCRUISE_COMPONENTS = window.CYANCRUISE_COMPONENTS || {};

  components.pageShell = {
    name: "page-shell",
    description: "页面壳组件，承接普通页面和功能页面。",
    feature: function (item, title, summary, innerHtml, context) {
      context.host.innerHTML =
        '<header class="feature-page-header">' +
        '<div><p class="eyebrow">青途启航</p><h2>' + context.escapeHtml(title) + '</h2><p class="lead">' + context.escapeHtml(summary) + '</p></div>' +
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
      if (item.key === "assessment" && context.state && context.state.assessmentSelectedScaleId) {
        return '<div class="page-actions"><button type="button" class="secondary" data-assessment-back>返回</button></div>';
      }
      var parent = typeof context.backRouteFor === "function" ? context.backRouteFor(item.key) : "";
      if (!parent || parent === item.key) {
        return "";
      }
      var labels = {
        postgraduate: "返回考研陪伴",
        "postgraduate-recommendation": "返回保研陪伴",
        "study-abroad": "返回留学陪伴"
      };
      var label = labels[parent] || "返回";
      return '<div class="page-actions"><button type="button" class="secondary" data-back-route="' +
        context.escapeHtml(parent) + '">' + label + '</button></div>';
    }
  };
}(window));
