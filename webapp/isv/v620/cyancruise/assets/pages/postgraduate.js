(function (window) {
  "use strict";
  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};
  registerPage("postgraduate", ["postgraduate", "postgraduate-school", "postgraduate-plan", "postgraduate-mistake", "postgraduate-reexam"], "考研陪伴");
  attachRenderer("further-study-home", function (item, context) {
    var cards = context.featureGroups[item.key] || [];
    context.renderFeatureShell(item, item.title, item.summary,
      '<section class="feature-section"><h3>' + context.escapeHtml(item.title) + '工具</h3><div class="feature-grid">' +
      context.featureCards(cards) + '</div></section>');
  });
  attachRenderer("postgraduate", function (item, context) {
    context.renderers.renderPostgraduatePage(item);
  });
  attachRenderer("postgraduate-school", function (item, context) {
    context.renderers.renderPostgraduateSchoolPage(item);
  });
  attachRenderer("postgraduate-plan", function (item, context) {
    context.renderers.renderPostgraduatePlanPage(item);
  });
  attachRenderer("postgraduate-mistake", function (item, context) {
    context.renderers.renderPostgraduateMistakePage(item);
  });
  attachRenderer("postgraduate-reexam", function (item, context) {
    context.renderers.renderPostgraduateReexamPage(item);
  });
}(window));
