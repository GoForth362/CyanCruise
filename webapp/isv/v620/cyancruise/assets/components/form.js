(function (window) {
  "use strict";

  var components = window.CYANCRUISE_COMPONENTS = window.CYANCRUISE_COMPONENTS || {};

  components.form = {
    name: "form",
    description: "表单控件组件，承接字段、输入、选择器和按钮组合。",
    field: function (id, label, type, value, options, context) {
      var escapeHtml = context.escapeHtml;
      if (type === "select") {
        return '<label>' + escapeHtml(label) + '<select id="' + id + '">' +
          options.map(function (option) {
            var selected = option[0] === value ? " selected" : "";
            return '<option value="' + escapeHtml(option[0]) + '"' + selected + ">" + escapeHtml(option[1]) + "</option>";
          }).join("") + "</select></label>";
      }
      return '<label>' + escapeHtml(label) + '<input id="' + id + '" value="' + escapeHtml(value) + '"></label>';
    }
  };
}(window));
