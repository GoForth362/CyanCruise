(function (window) {
  "use strict";

  var apiConfig = window.CYANCRUISE_API_CONFIG || {};
  var routeConfig = window.CYANCRUISE_ROUTE_CONFIG || {};

  window.CYANCRUISE_APP_CONFIG = {
    endpoints: apiConfig.endpoints || {},
    pages: routeConfig.pages || [],
    featureGroups: routeConfig.featureGroups || {}
  };
}(window));
