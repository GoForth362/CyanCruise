package v620.cc001.cloud01.app01.webapi.file;

import v620.cc001.cloud01.app01.mservice.file.impl.PlainTextFileTextExtractor;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerFileStorage;
import org.junit.jupiter.api.Test;
import v620.base.helper.career.FileUploadPreviewService;
import v620.cc001.base.common.dto.career.FileConstants;
import v620.cc001.base.common.dto.career.FileUploadRequest;
import v620.cc001.base.common.dto.career.FileUploadResult;
import v620.cc001.cloud01.app01.mservice.application.FileUploadPreviewApplicationService;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerFileStorage;
import v620.cc001.cloud01.app01.mservice.file.impl.PlainTextFileTextExtractor;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileUploadPreviewWebApiTest {

    @Test
    void webApiExposesFileUploadPreviewAndExtractionContracts() {
        FileUploadPreviewWebApi webApi = new FileUploadPreviewWebApi(
                new FileUploadPreviewApplicationService(
                        new InMemoryCareerFileStorage(),
                        new PlainTextFileTextExtractor(),
                        new FileUploadPreviewService()));
        FileUploadRequest request = new FileUploadRequest();
        request.setFolder("resumes");
        request.setOriginalFilename("resume.txt");
        request.setBytes("hello".getBytes());

        FileUploadResult uploaded = webApi.upload(request);
        String key = uploaded.getFile().getObjectKey();

        assertEquals(FileConstants.STATUS_OK, uploaded.getStatus());
        assertEquals(FileConstants.STATUS_OK, webApi.previewUrl(key, 60).getStatus());
        assertEquals(FileConstants.STATUS_OK, webApi.download(key).getStatus());
        assertEquals(FileConstants.STATUS_OK, webApi.extractText(key).getStatus());
        assertEquals(FileConstants.STATUS_OK, webApi.delete(key).getStatus());
    }
}
