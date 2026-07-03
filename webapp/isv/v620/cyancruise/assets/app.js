(function (window, document) {
  "use strict";

  var APP_BOOT_VERSION = "20260704-cyancruise-v161";

  function bootRuntime() {
    var currentScript = document.currentScript;
    var script = document.createElement("script");
    script.src = "assets/app-runtime.js?v=" + APP_BOOT_VERSION;
    script.defer = true;
    if (currentScript && currentScript.parentNode) {
      currentScript.parentNode.insertBefore(script, currentScript.nextSibling);
    } else {
      document.body.appendChild(script);
    }
  }

  window.CYANCRUISE_APP_BOOT_VERSION = APP_BOOT_VERSION;
  bootRuntime();
}(window, document));
