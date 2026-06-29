(function (window) {
  "use strict";

  var services = window.CYANCRUISE_SERVICES = window.CYANCRUISE_SERVICES || {};

  services.file = {
    upload: function (context) {
      return context.post(context.endpoints.fileUpload, {
        request: {
          folder: context.folder || "resumes",
          originalFilename: context.originalFilename,
          base64: context.base64
        }
      });
    },
    extractText: function (context) {
      return context.post(context.endpoints.fileExtractText, {
        fileUrlOrKey: context.fileUrlOrKey
      });
    },
    download: function (context) {
      return context.post(context.endpoints.fileDownload, {
        fileUrlOrKey: context.fileUrlOrKey
      });
    },
    previewUrl: function (context) {
      return context.post(context.endpoints.filePreview, {
        fileUrlOrKey: context.fileUrlOrKey
      });
    },
    deleteFile: function (context) {
      return context.post(context.endpoints.fileDelete, {
        fileUrlOrKey: context.fileUrlOrKey
      });
    }
  };
}(window));
