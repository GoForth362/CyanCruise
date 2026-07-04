(function (window) {
  "use strict";

  var components = window.CYANCRUISE_COMPONENTS = window.CYANCRUISE_COMPONENTS || {};

  components.dialog = {
    name: "dialog",
    description: "弹窗组件，承接确认弹窗和资源详情弹窗。",
    hide: function () {
      var existing = document.querySelector(".confirm-overlay");
      if (existing && existing.parentNode) {
        existing.parentNode.removeChild(existing);
      }
    },
    confirm: function (title, text, confirmText, onConfirm, context) {
      var escapeHtml = context.escapeHtml;
      var hide = context.hide;
      hide();
      var previousFocus = document.activeElement;
      var overlay = document.createElement("div");
      overlay.className = "confirm-overlay";
      overlay.innerHTML =
        '<div class="confirm-dialog" role="dialog" aria-modal="true" aria-labelledby="confirmDialogTitle" aria-describedby="confirmDialogText">' +
        '<div class="confirm-copy"><strong id="confirmDialogTitle">' + escapeHtml(title) + '</strong>' +
        '<span id="confirmDialogText">' + escapeHtml(text) + '</span></div>' +
        '<div class="confirm-actions">' +
        '<button type="button" class="secondary" data-confirm-cancel>取消</button>' +
        '<button type="button" class="danger" data-confirm-ok>' + escapeHtml(confirmText || "确认") + '</button>' +
        '</div></div>';
      document.body.appendChild(overlay);
      var cancel = overlay.querySelector("[data-confirm-cancel]");
      var ok = overlay.querySelector("[data-confirm-ok]");
      function close() {
        hide();
        document.removeEventListener("keydown", onKeydown);
        if (previousFocus && typeof previousFocus.focus === "function") {
          previousFocus.focus();
        }
      }
      function onKeydown(event) {
        if (event.key === "Escape") {
          close();
        }
      }
      overlay.addEventListener("click", function (event) {
        if (event.target === overlay) {
          close();
        }
      });
      cancel.addEventListener("click", close);
      ok.addEventListener("click", function () {
        close();
        if (typeof onConfirm === "function") {
          onConfirm();
        }
      });
      document.addEventListener("keydown", onKeydown);
      ok.focus();
    },
    resourceDetail: function (detail, context) {
      var escapeHtml = context.escapeHtml;
      var escapeAttr = context.escapeAttr;
      var hide = context.hide;
      var navigateToRoute = context.navigateToRoute;
      hide();
      var previousFocus = document.activeElement;
      var overlay = document.createElement("div");
      overlay.className = "confirm-overlay resource-overlay";
      overlay.innerHTML =
        '<div class="confirm-dialog resource-dialog" role="dialog" aria-modal="true" aria-labelledby="resourceDialogTitle">' +
        '<div class="confirm-copy"><strong id="resourceDialogTitle">' + escapeHtml(detail.title) + '</strong>' +
        '<span>' + escapeHtml(detail.summary) + '</span></div>' +
        '<div class="resource-detail-body">' + detail.sections.map(function (section) {
          return '<section><h4>' + escapeHtml(section.title) + '</h4><ul>' + section.items.map(function (item) {
            return '<li>' + escapeHtml(item) + '</li>';
          }).join("") + '</ul></section>';
        }).join("") + '</div>' +
        '<div class="confirm-actions">' +
        '<button type="button" class="secondary" data-confirm-cancel>关闭</button>' +
        (detail.route ? '<button type="button" data-resource-route="' + escapeAttr(detail.route) + '">' + escapeHtml(detail.actionText || "进入相关工具") + '</button>' : '') +
        '</div></div>';
      document.body.appendChild(overlay);
      var cancel = overlay.querySelector("[data-confirm-cancel]");
      var route = overlay.querySelector("[data-resource-route]");
      function close() {
        hide();
        document.removeEventListener("keydown", onKeydown);
        if (previousFocus && typeof previousFocus.focus === "function") {
          previousFocus.focus();
        }
      }
      function onKeydown(event) {
        if (event.key === "Escape") {
          close();
        }
      }
      overlay.addEventListener("click", function (event) {
        if (event.target === overlay) {
          close();
        }
      });
      cancel.addEventListener("click", close);
      if (route) {
        route.addEventListener("click", function () {
          var targetRoute = route.getAttribute("data-resource-route");
          close();
          navigateToRoute(targetRoute);
        });
      }
      document.addEventListener("keydown", onKeydown);
      cancel.focus();
    }
  };
}(window));
