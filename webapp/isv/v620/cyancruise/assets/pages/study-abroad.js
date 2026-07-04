(function (window) {
  "use strict";
  var registerPage = window.CYANCRUISE_REGISTER_PAGE_MODULE || function () {};
  var attachRenderer = window.CYANCRUISE_ATTACH_PAGE_RENDERER || function () {};
  registerPage("study-abroad", ["study-abroad", "study-abroad-profile", "study-abroad-language", "study-abroad-school", "study-abroad-statement", "study-abroad-visa"], "留学陪伴");
  attachRenderer("study-abroad", function (item, context) {
    context.renderers.renderStudyAbroadPage(item);
  });
  attachRenderer("study-abroad-profile", function (item, context) {
    context.renderers.renderStudyAbroadProfilePage(item);
  });
  attachRenderer("study-abroad-language", function (item, context) {
    context.renderers.renderStudyAbroadLanguagePage(item);
  });
  attachRenderer("study-abroad-school", function (item, context) {
    context.renderers.renderStudyAbroadSchoolPage(item);
  });
  attachRenderer("study-abroad-statement", function (item, context) {
    context.renderers.renderStudyAbroadStatementPage(item);
  });
  attachRenderer("study-abroad-visa", function (item, context) {
    context.renderers.renderStudyAbroadVisaPage(item);
  });
}(window));
