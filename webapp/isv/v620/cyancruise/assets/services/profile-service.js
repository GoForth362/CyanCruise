(function (window) {
  "use strict";

  var services = window.CYANCRUISE_SERVICES = window.CYANCRUISE_SERVICES || {};

  services.profile = {
    endpoints: ["snapshot", "draft", "draftSave", "draftClear", "onboarding", "today", "plan", "ensurePlan"],
    description: "用户画像、首页草稿、今日行动和路径规划接口调用服务。",
    loadOverview: function (context) {
      var endpoints = context.endpoints;
      var post = context.post;
      var state = context.state;
      var asUnavailable = context.asUnavailable;
      var normalizeArray = context.normalizeArray;
      return Promise.all([
        post(endpoints.snapshot, state.identity.userId).catch(asUnavailable),
        post(endpoints.today, state.identity.userId).catch(asUnavailable),
        post(endpoints.resumes, state.identity.userId).catch(asUnavailable),
        post(endpoints.plan, state.identity.userId).catch(asUnavailable),
        post(endpoints.interviews, state.identity.userId).catch(asUnavailable)
      ]).then(function (results) {
        return {
          snapshot: results[0],
          today: results[1],
          resumes: normalizeArray(results[2]),
          plan: results[3],
          interviews: normalizeArray(results[4])
        };
      });
    }
  };
}(window));
