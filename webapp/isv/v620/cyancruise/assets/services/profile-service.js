(function (window) {
  "use strict";

  var services = window.CYANCRUISE_SERVICES = window.CYANCRUISE_SERVICES || {};

  services.profile = {
    endpoints: ["snapshot", "draft", "draftSave", "draftClear", "onboarding", "today", "plan", "ensurePlan", "dailyPlan", "studyPlan", "studyDailyPlan"],
    description: "用户画像、首页草稿、今日行动和路径规划接口调用服务。",
    loadOverview: function (context) {
      var endpoints = context.endpoints;
      var post = context.post;
      var state = context.state;
      var asUnavailable = context.asUnavailable;
      var normalizeArray = context.normalizeArray;
      return post(endpoints.snapshot, state.identity.userId).catch(asUnavailable).then(function (snapshot) {
        return Promise.all([
          post(endpoints.today, state.identity.userId).catch(asUnavailable),
          post(endpoints.resumes, state.identity.userId).catch(asUnavailable),
          post(endpoints.plan, state.identity.userId).catch(asUnavailable),
          post(endpoints.interviews, state.identity.userId).catch(asUnavailable),
          post(endpoints.dailyPlan, state.identity.userId).catch(asUnavailable),
          post(endpoints.studyPlan, state.identity.userId).catch(asUnavailable),
          post(endpoints.studyDailyPlan, state.identity.userId).catch(asUnavailable)
        ]).then(function (results) {
          var study = snapshot && snapshot.onboarding && snapshot.onboarding.routeGoal === "study";
        return {
          snapshot: snapshot,
          today: results[0],
          resumes: normalizeArray(results[1]),
          employmentPlan: results[2],
          interviews: normalizeArray(results[3]),
          employmentDailyPlan: results[4],
          studyPlan: results[5],
          studyDailyPlan: results[6],
          plan: study ? results[5] : results[2],
          dailyPlan: study ? results[6] : results[4]
        };
        });
      });
    }
  };
}(window));
