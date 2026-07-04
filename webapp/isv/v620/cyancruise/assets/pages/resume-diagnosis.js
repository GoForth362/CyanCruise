(function (window) {
  "use strict";
  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};
  registerPage("resume-diagnosis", ["resume-diagnosis"], "简历诊断");
  attachRenderer("resume-diagnosis", function (item, context) {
    context.renderers.renderResumeDiagnosisPage(item);
  });
}(window));
