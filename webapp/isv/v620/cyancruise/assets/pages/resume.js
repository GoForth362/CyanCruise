(function (window) {
  "use strict";
  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};
  registerPage("resume", ["resume", "file-upload-preview"], "简历制作");
  attachRenderer("resume", function (item, context) {
    context.renderers.renderResumePage(item);
  });
  attachRenderer("file-upload-preview", function (item, context) {
    context.renderers.renderContractPage(item);
  });
}(window));
