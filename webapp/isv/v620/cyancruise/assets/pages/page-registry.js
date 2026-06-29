(function (window) {
  "use strict";

  var registry = window.CYANCRUISE_PAGE_MODULES = window.CYANCRUISE_PAGE_MODULES || {};

  window.CYANCRUISE_REGISTER_PAGE_MODULE = function (key, routes, title) {
    registry[key] = {
      key: key,
      routes: routes || [key],
      title: title || key
    };
  };

  window.CYANCRUISE_ATTACH_PAGE_RENDERER = function (key, renderer) {
    registry[key] = registry[key] || { key: key, routes: [key], title: key };
    registry[key].render = renderer;
  };
}(window));
