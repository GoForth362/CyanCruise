(function (window) {
  "use strict";
  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};
  registerPage("resume-home", ["resume-home"], "简历中心");
  attachRenderer("resume-home", function (item, context) {
    var cards = context.featureGroups[item.key] || [];
    context.renderFeatureShell(item, item.title, item.summary,
      '<section class="feature-section"><h3>' + context.escapeHtml(item.title) + '工具</h3><div class="feature-grid">' +
      context.featureCards(cards) + '</div></section>');
  });
}(window));
