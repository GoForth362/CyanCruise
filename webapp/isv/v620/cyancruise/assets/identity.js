(function (window) {
  "use strict";

  window.CYANCRUISE_IDENTITY = {
    contextNames: [
      "__CYANCRUISE_COSMIC_CONTEXT__",
      "__COSMIC_CONTEXT__",
      "cosmicContext",
      "kdContext",
      "KDCONTEXT",
      "userInfo",
      "currentUser",
      "loginUser"
    ],
    storageKeys: [
      "cosmicContext",
      "kdContext",
      "userInfo",
      "currentUser",
      "loginUser",
      "operator",
      "sessionUser",
      "bosUser",
      "mcUser"
    ],
    cookieKeys: [
      "userInfo",
      "currentUser",
      "loginUser",
      "cosmicContext",
      "kdContext"
    ],
    developmentStorage: {
      userId: "cyancruise.userId",
      adminId: "cyancruise.adminId",
      roles: "cyancruise.roles",
      userName: "cyancruise.userName"
    },
    adminRoles: [
      "ADMIN",
      "COSMIC_ADMIN",
      "PLATFORM_ADMIN"
    ]
  };
}(window));
