(function (window) {
  "use strict";
  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};
  registerPage("recommendation", ["postgraduate-recommendation", "recommendation-ranking", "recommendation-background", "recommendation-material", "recommendation-tutor"], "保研陪伴");
  attachRenderer("postgraduate-recommendation", function (item, context) {
    context.renderers.renderRecommendationPage(item);
  });
  attachRenderer("recommendation-ranking", function (item, context) {
    context.renderers.renderRecommendationRankingPage(item);
  });
  attachRenderer("recommendation-background", function (item, context) {
    context.renderers.renderRecommendationBackgroundPage(item);
  });
  attachRenderer("recommendation-material", function (item, context) {
    context.renderers.renderRecommendationMaterialPage(item);
  });
  attachRenderer("recommendation-tutor", function (item, context) {
    context.renderers.renderRecommendationTutorPage(item);
  });
}(window));
