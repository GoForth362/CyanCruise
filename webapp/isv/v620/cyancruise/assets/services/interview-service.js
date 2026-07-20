(function (window) {
  "use strict";

  var services = window.CYANCRUISE_SERVICES = window.CYANCRUISE_SERVICES || {};

  services.interview = {
    endpoints: ["interviews", "interviewPage", "guidedInterviewStart", "guidedInterviewAnswer", "guidedInterviewFinish", "interviewEnd", "interviewMessages", "interviewDelete"],
    description: "AI 模拟面试、全景仿真面试和面试记录接口调用服务。",
    page: function (context) {
      return context.post(context.endpoints.interviewPage, {
        userId: context.userId,
        page: context.page,
        mode: context.mode
      });
    }
  };
}(window));
