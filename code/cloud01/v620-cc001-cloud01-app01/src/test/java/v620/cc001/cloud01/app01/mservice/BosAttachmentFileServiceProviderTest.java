package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.FileUploadPreviewService;
import v620.cc001.base.common.dto.career.FileConstants;
import v620.cc001.base.common.dto.career.FileDeleteResult;
import v620.cc001.base.common.dto.career.FileDownloadResult;
import v620.cc001.base.common.dto.career.FilePreviewUrlResult;
import v620.cc001.base.common.dto.career.FileReferenceDto;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BosAttachmentFileServiceProviderTest {

    @Test
    void enabledBosProviderDelegatesUploadPreviewDownloadAndDelete() {
        RecordingBosFileServiceClient client = new RecordingBosFileServiceClient();
        BosAttachmentFileServiceProvider provider = new BosAttachmentFileServiceProvider(
                client, FileConstants.PROVIDER_COSMIC);

        FileReferenceDto uploaded = provider.upload(reference("resumes/a.pdf"), new byte[]{1, 2, 3});
        FilePreviewUrlResult preview = provider.previewUrl(uploaded.getObjectKey(), 60);
        FileDownloadResult download = provider.download(uploaded.getObjectKey());
        FileDeleteResult deleted = provider.delete(uploaded.getObjectKey());

        assertEquals("bos/resumes/a.pdf", uploaded.getObjectKey());
        assertEquals(FileConstants.PROVIDER_COSMIC, uploaded.getProvider());
        assertEquals(FileConstants.STATUS_OK, preview.getStatus());
        assertEquals("http://files.example/preview/bos/resumes/a.pdf?ticket=secret", preview.getPreviewUrl());
        assertEquals(FileConstants.STATUS_OK, download.getStatus());
        assertEquals(Long.valueOf(3), download.getSizeBytes());
        assertEquals(FileConstants.STATUS_OK, deleted.getStatus());
        assertEquals(Boolean.TRUE, deleted.getDeleted());
        assertFalse(client.objects.containsKey(uploaded.getObjectKey()));
    }

    @Test
    void previewFallsBackToDownloadUrlWhenPreviewMapHasNoUrl() {
        RecordingBosFileServiceClient client = new RecordingBosFileServiceClient();
        client.previewHasUrl = false;
        BosAttachmentFileServiceProvider provider = new BosAttachmentFileServiceProvider(
                client, FileConstants.PROVIDER_COSMIC);
        FileReferenceDto uploaded = provider.upload(reference("resumes/a.pdf"), new byte[]{1});

        FilePreviewUrlResult preview = provider.previewUrl(uploaded.getObjectKey(), 60);

        assertEquals(FileConstants.STATUS_OK, preview.getStatus());
        assertEquals("http://files.example/file/download.do?path=bos%2Fresumes%2Fa.pdf", preview.getPreviewUrl());
    }

    @Test
    void previewFallsBackToDownloadUrlWhenPreviewThrows() {
        RecordingBosFileServiceClient client = new RecordingBosFileServiceClient();
        client.previewFails = true;
        BosAttachmentFileServiceProvider provider = new BosAttachmentFileServiceProvider(
                client, FileConstants.PROVIDER_COSMIC);

        FilePreviewUrlResult preview = provider.previewUrl("resumes/a.pdf", 60);

        assertEquals(FileConstants.STATUS_OK, preview.getStatus());
        assertEquals("http://files.example/file/download.do?path=resumes%2Fa.pdf", preview.getPreviewUrl());
        assertFalse(preview.getMessage().contains("signature"));
    }

    @Test
    void providerErrorsBecomeRecoverableResultsAndSanitizedMessages() {
        RecordingBosFileServiceClient client = new RecordingBosFileServiceClient();
        client.failOperations = true;
        client.httpPrefix = "";
        BosAttachmentFileServiceProvider provider = new BosAttachmentFileServiceProvider(
                client, FileConstants.PROVIDER_COSMIC);

        FilePreviewUrlResult preview = provider.previewUrl("resumes/a.pdf", 60);
        FileDownloadResult download = provider.download("resumes/a.pdf");
        FileDeleteResult deleted = provider.delete("resumes/a.pdf");

        assertEquals(FileConstants.STATUS_UNAVAILABLE, preview.getStatus());
        assertEquals(FileConstants.STATUS_UNAVAILABLE, download.getStatus());
        assertEquals(FileConstants.STATUS_UNAVAILABLE, deleted.getStatus());
        assertFalse(preview.getMessage().contains("signature"));
        assertEquals(Boolean.FALSE, deleted.getDeleted());
    }

    @Test
    void applicationServiceUsesBosProviderThroughExistingStorageBoundary() {
        RecordingBosFileServiceClient client = new RecordingBosFileServiceClient();
        CosmicFileAdapterConfig config = new CosmicFileAdapterConfig();
        config.setEnabled(true);
        BosAttachmentFileServiceProvider provider = new BosAttachmentFileServiceProvider(
                client, FileConstants.PROVIDER_COSMIC);
        FileUploadPreviewApplicationService service = new FileUploadPreviewApplicationService(
                new CosmicCareerFileStorage(provider, config, new FileUploadPreviewService()),
                new CosmicFileTextExtractor(provider, config),
                new FileUploadPreviewService());

        assertEquals(FileConstants.STATUS_OK,
                service.upload(upload("resumes", "resume.pdf", new byte[]{1, 2})).getStatus());
    }

    private FileReferenceDto reference(String objectKey) {
        FileReferenceDto reference = new FileReferenceDto();
        reference.setObjectKey(objectKey);
        reference.setFolder("resumes");
        reference.setOriginalFilename("resume.pdf");
        reference.setExtension(".pdf");
        reference.setSizeBytes(Long.valueOf(3));
        return reference;
    }

    private v620.cc001.base.common.dto.career.FileUploadRequest upload(String folder, String filename, byte[] bytes) {
        v620.cc001.base.common.dto.career.FileUploadRequest request =
                new v620.cc001.base.common.dto.career.FileUploadRequest();
        request.setFolder(folder);
        request.setOriginalFilename(filename);
        request.setBytes(bytes);
        return request;
    }

    private static class RecordingBosFileServiceClient implements BosFileServiceClient {
        private final Map<String, byte[]> objects = new LinkedHashMap<String, byte[]>();
        private boolean previewHasUrl = true;
        private boolean previewFails = false;
        private boolean failOperations = false;
        private String httpPrefix = "http://files.example/";

        public boolean available() {
            return !failOperations;
        }

        public String upload(String objectKey, String originalFilename, byte[] bytes) {
            if (failOperations) {
                throw new IllegalStateException("upload failed?signature=secret");
            }
            String uploaded = "bos/" + objectKey;
            objects.put(uploaded, Arrays.copyOf(bytes, bytes.length));
            return uploaded;
        }

        public Map<String, Object> preview(String originalFilename, String objectKey) {
            if (previewFails) {
                throw new IllegalStateException("preview failed?signature=secret");
            }
            if (failOperations) {
                throw new IllegalStateException("preview failed?signature=secret");
            }
            Map<String, Object> result = new LinkedHashMap<String, Object>();
            if (previewHasUrl) {
                result.put("url", "http://files.example/preview/" + objectKey + "?ticket=secret");
            }
            return result;
        }

        public byte[] download(String objectKey) {
            if (failOperations) {
                throw new IllegalStateException("download failed?signature=secret");
            }
            byte[] bytes = objects.get(objectKey);
            return bytes == null ? null : Arrays.copyOf(bytes, bytes.length);
        }

        public void delete(String objectKey) {
            if (failOperations) {
                throw new IllegalStateException("delete failed?signature=secret");
            }
            objects.remove(objectKey);
        }

        public String httpUrlPrefix() {
            return httpPrefix;
        }
    }
}
