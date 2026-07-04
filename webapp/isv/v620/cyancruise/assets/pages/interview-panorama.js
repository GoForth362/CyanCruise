(function (window) {
  "use strict";
  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};
  registerPage("interview-panorama", ["interview-panorama", "interview-panorama-history"], "全景仿真面试");
  attachRenderer("interview-panorama", function (item, context) {
    context.renderers.renderPanoramaInterviewPage(item);
  });
  attachRenderer("interview-panorama-history", function (item, context) {
    context.renderers.renderPanoramaHistoryPage(item);
  });
}(window));
