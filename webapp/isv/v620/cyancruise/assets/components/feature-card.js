(function (window) {
  "use strict";

  var components = window.CYANCRUISE_COMPONENTS = window.CYANCRUISE_COMPONENTS || {};

  components.featureCard = {
    name: "feature-card",
    description: "功能卡片组件，承接 featureCards 渲染逻辑。",
    renderCards: function (cards, context) {
      var escapeHtml = context.escapeHtml;
      var pageByKey = context.pageByKey;
      return cards.map(function (card) {
        var linked = !!card.route && pageByKey[card.route] && card.status !== "即将接入";
        var attrs = linked ? ' data-link="' + escapeHtml(card.route) + '"' + (card.platformLink ? ' data-platform-link="true"' : "") : ' disabled aria-disabled="true"';
        var cls = linked ? "feature-card" : "feature-card disabled";
        return '<button type="button" class="' + cls + '"' + attrs + '>' +
          '<span class="feature-icon">' + escapeHtml(card.icon) + '</span>' +
          '<span class="feature-copy"><strong>' + escapeHtml(card.title) + '</strong><small>' + escapeHtml(card.summary) + '</small></span>' +
          '</button>';
      }).join("");
    }
  };
}(window));
