package v620.cc001.cloud01.app01.webapi;

import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.career.FileDeleteResult;
import v620.cc001.base.common.dto.career.FileDownloadResult;
import v620.cc001.base.common.dto.career.FilePreviewUrlResult;
import v620.cc001.base.common.dto.career.FileTextExtractionResult;
import v620.cc001.base.common.dto.career.FileUploadRequest;
import v620.cc001.base.common.dto.career.FileUploadResult;
import v620.cc001.cloud01.app01.mservice.FileUploadPreviewApplicationService;

@ApiController(value = "fileUploadPreviewWebApi", desc = "CareerLoop file upload and preview API")
@ApiMapping("/cc001/files")
public class FileUploadPreviewWebApi {

    private final FileUploadPreviewApplicationService applicationService;

    public FileUploadPreviewWebApi() {
        this(new FileUploadPreviewApplicationService());
    }

    FileUploadPreviewWebApi(FileUploadPreviewApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @ApiPostMapping(value = "/upload", desc = "Upload file bytes", methodParamNames = {"request"})
    public @ApiResponseBody(value = "Upload result") FileUploadResult upload(
            @ApiRequestBody(value = "request", required = true) FileUploadRequest request) {
        return applicationService.upload(request);
    }

    @ApiPostMapping(value = "/preview-url", desc = "Create preview URL", methodParamNames = {"fileUrlOrKey", "ttlSeconds"})
    public @ApiResponseBody(value = "Preview URL") FilePreviewUrlResult previewUrl(
            @ApiRequestBody(value = "fileUrlOrKey", required = true) String fileUrlOrKey,
            @ApiRequestBody(value = "ttlSeconds", required = false) long ttlSeconds) {
        return applicationService.previewUrl(fileUrlOrKey, ttlSeconds);
    }

    @ApiPostMapping(value = "/download", desc = "Download file bytes", methodParamNames = {"fileUrlOrKey"})
    public @ApiResponseBody(value = "Download result") FileDownloadResult download(
            @ApiRequestBody(value = "fileUrlOrKey", required = true) String fileUrlOrKey) {
        return applicationService.download(fileUrlOrKey);
    }

    @ApiPostMapping(value = "/delete", desc = "Delete file", methodParamNames = {"fileUrlOrKey"})
    public @ApiResponseBody(value = "Delete result") FileDeleteResult delete(
            @ApiRequestBody(value = "fileUrlOrKey", required = false) String fileUrlOrKey) {
        return applicationService.delete(fileUrlOrKey);
    }

    @ApiPostMapping(value = "/extract-text", desc = "Extract text", methodParamNames = {"fileUrlOrKey"})
    public @ApiResponseBody(value = "Text extraction result") FileTextExtractionResult extractText(
            @ApiRequestBody(value = "fileUrlOrKey", required = true) String fileUrlOrKey) {
        return applicationService.extractText(fileUrlOrKey);
    }
}
