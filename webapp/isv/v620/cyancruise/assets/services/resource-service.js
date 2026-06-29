(function (window) {
  "use strict";

  var services = window.CYANCRUISE_SERVICES = window.CYANCRUISE_SERVICES || {};

  var details = {
    "resume-evidence": {
      title: "简历证据清单",
      summary: "把经历先整理成可验证证据，再写成简历要点。",
      route: "resume-diagnosis",
      actionText: "去简历诊断",
      sections: [
        { title: "先收集", items: ["目标岗位要求中反复出现的能力词。", "课程、项目、实习、竞赛、开源或作品中能证明这些能力的材料。", "每段经历对应的任务、动作、结果和可量化指标。"] },
        { title: "再改写", items: ["用动词开头描述你做了什么。", "补上技术栈、业务场景或协作对象。", "尽量写出结果，例如效率、规模、准确率、用户量或交付物。"] },
        { title: "检查", items: ["一条经历只服务一个核心能力。", "删除泛泛的形容词，保留证据和结果。", "和目标岗位无关的内容放到次要位置。"] }
      ]
    },
    "weekly-focus": {
      title: "本周求职行动重点",
      summary: "把求职推进拆成一周内能完成的三个动作。",
      route: "career-plan",
      actionText: "查看路径规划",
      sections: [
        { title: "第 1 步", items: ["选定一个本周主攻岗位，不要同时追太多方向。", "保存 3 条代表性岗位要求，提取共同能力词。"] },
        { title: "第 2 步", items: ["按能力词修改一版简历。", "至少补充一个项目或实习的结果指标。"] },
        { title: "第 3 步", items: ["完成一次模拟面试。", "记录 2 个回答卡住的问题，并写下下一轮改进动作。"] }
      ]
    },
    "interview-practice": {
      title: "面试练习前的回答结构",
      summary: "用固定结构减少临场组织语言的成本。",
      route: "interview",
      actionText: "开始模拟面试",
      sections: [
        { title: "经历讲述框架", items: ["背景：一句话交代当时的场景。", "任务：说明你负责解决什么问题。", "行动：讲清楚你的关键动作和取舍。", "结果：用结果、指标或复盘收尾。"] },
        { title: "项目题", items: ["先讲目标和边界，再讲架构或流程。", "准备一个技术难点、一个协作难点、一个复盘点。"] },
        { title: "行为题", items: ["准备失败经历、冲突经历、主动推进经历。", "回答时避免只讲态度，要给具体场景。"] }
      ]
    },
    "software-engineer-path": {
      title: "软件工程师路径",
      summary: "面向后端、Web、数据和 AI 应用岗位的基础成长路线。",
      route: "career-plan",
      actionText: "查看路径规划",
      sections: [
        { title: "基础能力", items: ["数据结构与算法、计算机网络、数据库、操作系统。", "至少掌握一门主力语言和对应工程框架。"] },
        { title: "项目能力", items: ["完成一个可部署的业务系统。", "补充登录、权限、存储、日志、测试和部署说明。"] },
        { title: "求职准备", items: ["把项目写成问题、方案、结果三段。", "围绕目标岗位准备高频八股和项目追问。"] }
      ]
    }
  };

  services.resource = {
    detail: function (key) {
      return details[key] || null;
    }
  };
}(window));
