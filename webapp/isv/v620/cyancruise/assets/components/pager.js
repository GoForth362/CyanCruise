(function (window) {
  "use strict";

  var components = window.CYANCRUISE_COMPONENTS = window.CYANCRUISE_COMPONENTS || {};

  components.pager = {
    name: "pager",
    description: "分页组件，承接面试记录等分页渲染。",
    render: function (page, options) {
      var current = Math.max(1, Number(page.page) || 1);
      var totalPages = Math.max(1, Number(page.totalPages) || 0);
      var total = Math.max(0, Number(page.total) || 0);
      var actionName = options.actionName;
      var actionAttr = options.actionAttr;
      var ariaLabel = options.ariaLabel;
      return '<nav class="interview-history-pager full" aria-label="' + ariaLabel + '"><span>共 ' + total + ' 条 · 第 ' + current + ' / ' + totalPages + ' 页</span><div class="actions-row compact">' +
        '<button type="button" class="secondary" ' + actionAttr + '="' + actionName + '" data-page="' + (current - 1) + '" ' + (current <= 1 ? 'disabled' : '') + '>上一页</button>' +
        '<button type="button" class="secondary" ' + actionAttr + '="' + actionName + '" data-page="' + (current + 1) + '" ' + (current >= totalPages ? 'disabled' : '') + '>下一页</button></div></nav>';
    }
  };
}(window));
