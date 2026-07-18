(function (window) {
  "use strict";
  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};
  registerPage("assessment", ["assessment"], "画像补全");
  registerPage("deep-profile-detail", ["deep-profile-detail"], "深度画像详情");
  attachRenderer("assessment", function (item, context) {
    context.renderers.renderAssessmentPage(item);
  });
  attachRenderer("deep-profile-detail", function (item, context) {
    context.renderers.renderDeepProfileDetailPage(item);
  });
}(window));
