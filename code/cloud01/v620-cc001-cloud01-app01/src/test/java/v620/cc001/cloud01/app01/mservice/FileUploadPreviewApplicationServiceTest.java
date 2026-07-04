package v620.cc001.cloud01.app01.mservice;

import v620.cc001.cloud01.app01.mservice.file.impl.PlainTextFileTextExtractor;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerFileStorage;
import v620.cc001.cloud01.app01.mservice.application.FileUploadPreviewApplicationService;
import v620.cc001.cloud01.app01.mservice.file.FileTextExtractor;
import v620.cc001.cloud01.app01.mservice.file.impl.PlainTextFileTextExtractor;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerFileStorage;
import org.junit.jupiter.api.Test;
import v620.base.helper.career.FileUploadPreviewService;
import v620.cc001.base.common.dto.career.FileConstants;
import v620.cc001.base.common.dto.career.FileDeleteResult;
import v620.cc001.base.common.dto.career.FilePreviewUrlResult;
import v620.cc001.base.common.dto.career.FileTextExtractionResult;
import v620.cc001.base.common.dto.career.FileUploadRequest;
import v620.cc001.base.common.dto.career.FileUploadResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileUploadPreviewApplicationServiceTest {

    @Test
    void uploadPreviewDownloadDeleteAndExtractTextUseObjectKeyContract() {
        InMemoryCareerFileStorage storage = new InMemoryCareerFileStorage();
        FileUploadPreviewApplicationService service = new FileUploadPreviewApplicationService(
                storage, new PlainTextFileTextExtractor(), new FileUploadPreviewService());
        FileUploadResult uploaded = service.upload(upload("resumes", "resume.txt", "hello resume".getBytes()));

        assertEquals(FileConstants.STATUS_OK, uploaded.getStatus());
        assertTrue(uploaded.getFile().getObjectKey().startsWith("resumes/"));

        FilePreviewUrlResult preview = service.previewUrl(uploaded.getFile().getObjectKey(), 1);
        assertEquals(FileConstants.STATUS_OK, preview.getStatus());
        assertEquals(Long.valueOf(FileConstants.MIN_PREVIEW_TTL_SECONDS), preview.getTtlSeconds());
        assertEquals(FileConstants.STATUS_OK, service.download(uploaded.getFile().getObjectKey()).getStatus());

        FileTextExtractionResult text = service.extractText(uploaded.getFile().getObjectKey());
        assertEquals(FileConstants.STATUS_OK, text.getStatus());
        assertEquals("hello resume", text.getText());

        FileDeleteResult deleted = service.delete(uploaded.getFile().getObjectKey());
        FileDeleteResult deletedAgain = service.delete(uploaded.getFile().getObjectKey());
        assertEquals(FileConstants.STATUS_OK, deleted.getStatus());
        assertEquals(Boolean.TRUE, deleted.getDeleted());
        assertEquals(FileConstants.STATUS_OK, deletedAgain.getStatus());
        assertEquals(Boolean.FALSE, deletedAgain.getDeleted());
    }

    @Test
    void emptyUploadBlankPreviewMalformedReferenceAndUnavailablePreviewAreExplicit() {
        InMemoryCareerFileStorage storage = new InMemoryCareerFileStorage();
        storage.setPreviewAvailable(false);
        FileUploadPreviewApplicationService service = new FileUploadPreviewApplicationService(
                storage, new PlainTextFileTextExtractor(), new FileUploadPreviewService());

        assertEquals(FileConstants.STATUS_FILE_EMPTY, service.upload(upload("resumes", "empty.pdf", new byte[0])).getStatus());
        assertEquals(FileConstants.STATUS_SKIPPED, service.previewUrl(" ", 60).getStatus());
        assertEquals(FileConstants.STATUS_MALFORMED_REFERENCE, service.previewUrl("https://bucket.endpoint", 60).getStatus());
        assertEquals(FileConstants.STATUS_UNAVAILABLE, service.previewUrl("resumes/a.pdf", 60).getStatus());
    }

    @Test
    void extractionFallbackDoesNotBreakFileFlow() {
        FileUploadPreviewApplicationService service = new FileUploadPreviewApplicationService(
                new InMemoryCareerFileStorage(), new FileTextExtractor() {
                    public String extract(byte[] bytes, String objectKey) {
                        throw new IllegalStateException("boom");
                    }

                    public boolean available() {
                        return true;
                    }
                }, new FileUploadPreviewService());
        FileUploadResult uploaded = service.upload(upload("resumes", "resume.pdf", new byte[]{1, 2, 3}));

        FileTextExtractionResult result = service.extractText(uploaded.getFile().getObjectKey());

        assertEquals(FileConstants.STATUS_FAILED, result.getStatus());
        assertEquals("", result.getText());
    }

    private FileUploadRequest upload(String folder, String filename, byte[] bytes) {
        FileUploadRequest request = new FileUploadRequest();
        request.setFolder(folder);
        request.setOriginalFilename(filename);
        request.setBytes(bytes);
        return request;
    }
}
