package v620.cc001.cloud01.app01.mservice;

import v620.cc001.cloud01.app01.mservice.file.impl.CosmicCareerFileServiceProvider;
import v620.cc001.cloud01.app01.mservice.application.FileUploadPreviewApplicationService;
import v620.cc001.cloud01.app01.mservice.file.impl.CosmicCareerFileServiceProvider;
import v620.cc001.cloud01.app01.mservice.file.CosmicFileAdapterConfig;
import v620.cc001.cloud01.app01.mservice.file.CyanCruiseFileServiceAdapterFactory;
import org.junit.jupiter.api.Test;
import v620.base.helper.career.FileUploadPreviewService;
import v620.cc001.base.common.dto.career.FileConstants;
import v620.cc001.base.common.dto.career.FileDeleteResult;
import v620.cc001.base.common.dto.career.FileDownloadResult;
import v620.cc001.base.common.dto.career.FilePreviewUrlResult;
import v620.cc001.base.common.dto.career.FileReferenceDto;
import v620.cc001.base.common.dto.career.FileTextExtractionResult;
import v620.cc001.base.common.dto.career.FileUploadRequest;
import v620.cc001.base.common.dto.career.FileUploadResult;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CosmicFileServiceAdapterTest {

    @Test
    void defaultProductionAdapterIsDisabledSafe() {
        FileUploadPreviewApplicationService service = CyanCruiseFileServiceAdapterFactory.production();

        FileUploadResult upload = service.upload(upload("resumes", "resume.pdf", new byte[]{1, 2}));
        FilePreviewUrlResult preview = service.previewUrl("resumes/a.pdf", 1);
        FileDownloadResult download = service.download("resumes/a.pdf");
        FileDeleteResult deleted = service.delete("resumes/a.pdf");
        FileTextExtractionResult text = service.extractText("resumes/a.pdf");

        assertEquals(FileConstants.STATUS_UNAVAILABLE, upload.getStatus());
        assertEquals(FileConstants.STATUS_UNAVAILABLE, preview.getStatus());
        assertEquals(FileConstants.STATUS_UNAVAILABLE, download.getStatus());
        assertEquals(FileConstants.STATUS_UNAVAILABLE, deleted.getStatus());
        assertEquals(FileConstants.STATUS_UNAVAILABLE, text.getStatus());
    }

    @Test
    void enabledAdapterDelegatesAndPreservesObjectKeyContract() {
        RecordingCosmicFileProvider provider = new RecordingCosmicFileProvider();
        FileUploadPreviewApplicationService service = CyanCruiseFileServiceAdapterFactory.production(
                provider, enabled(), new FileUploadPreviewService());

        FileUploadResult uploaded = service.upload(upload("resumes", "resume.txt", "hello".getBytes()));
        String key = uploaded.getFile().getObjectKey();
        FilePreviewUrlResult preview = service.previewUrl("https://bucket.example/" + key + "?Signature=secret", 1);
        FileDownloadResult download = service.download(key);
        FileTextExtractionResult text = service.extractText(key);
        FileDeleteResult deleted = service.delete(key);
        FileDeleteResult deletedAgain = service.delete(key);

        assertEquals(FileConstants.STATUS_OK, uploaded.getStatus());
        assertTrue(key.startsWith("resumes/"));
        assertEquals(FileConstants.PROVIDER_COSMIC, uploaded.getFile().getProvider());
        assertEquals(FileConstants.STATUS_OK, preview.getStatus());
        assertEquals(Long.valueOf(FileConstants.MIN_PREVIEW_TTL_SECONDS), preview.getTtlSeconds());
        assertFalse(preview.getMessage().contains("Signature"));
        assertEquals(FileConstants.STATUS_OK, download.getStatus());
        assertEquals(FileConstants.STATUS_OK, text.getStatus());
        assertEquals("hello", text.getText());
        assertEquals(FileConstants.PROVIDER_COSMIC, text.getProvider());
        assertEquals(Boolean.TRUE, deleted.getDeleted());
        assertEquals(FileConstants.STATUS_OK, deletedAgain.getStatus());
        assertEquals(Boolean.FALSE, deletedAgain.getDeleted());
    }

    @Test
    void damagedPdfReturnsParseFailureWithoutBreakingDownload() {
        RecordingCosmicFileProvider provider = new RecordingCosmicFileProvider();
        provider.textExtractionAvailable = false;
        FileUploadPreviewApplicationService service = CyanCruiseFileServiceAdapterFactory.production(
                provider, enabled(), new FileUploadPreviewService());
        FileUploadResult uploaded = service.upload(upload("resumes", "resume.pdf", new byte[]{1, 2, 3}));

        assertEquals(FileConstants.STATUS_OK, service.download(uploaded.getFile().getObjectKey()).getStatus());
        assertEquals("PDF_PARSE_FAILED", service.extractText(uploaded.getFile().getObjectKey()).getStatus());
    }

    @Test
    void configReadsSystemPropertiesSafely() {
        System.setProperty(CosmicFileAdapterConfig.ENABLED_PROPERTY, "true");
        System.setProperty(CosmicFileAdapterConfig.PROVIDER_NAME_PROPERTY, "tenant-file");
        System.setProperty(CosmicFileAdapterConfig.MAX_PREVIEW_TTL_PROPERTY, "999999");
        System.setProperty(CosmicFileAdapterConfig.TEXT_EXTRACTION_MODE_PROPERTY, "platform");
        System.setProperty(CosmicFileAdapterConfig.DIAGNOSTICS_ENABLED_PROPERTY, "false");
        try {
            CosmicFileAdapterConfig config = CosmicFileAdapterConfig.fromSystemProperties();
            assertTrue(config.isEnabled());
            assertEquals("tenant-file", config.getProviderName());
            assertEquals(FileConstants.MAX_PREVIEW_TTL_SECONDS, config.getMaxPreviewTtlSeconds());
            assertEquals("platform", config.getTextExtractionMode());
            assertFalse(config.isDiagnosticsEnabled());
        } finally {
            System.clearProperty(CosmicFileAdapterConfig.ENABLED_PROPERTY);
            System.clearProperty(CosmicFileAdapterConfig.PROVIDER_NAME_PROPERTY);
            System.clearProperty(CosmicFileAdapterConfig.MAX_PREVIEW_TTL_PROPERTY);
            System.clearProperty(CosmicFileAdapterConfig.TEXT_EXTRACTION_MODE_PROPERTY);
            System.clearProperty(CosmicFileAdapterConfig.DIAGNOSTICS_ENABLED_PROPERTY);
        }
    }

    private CosmicFileAdapterConfig enabled() {
        CosmicFileAdapterConfig config = new CosmicFileAdapterConfig();
        config.setEnabled(true);
        return config;
    }

    private FileUploadRequest upload(String folder, String filename, byte[] bytes) {
        FileUploadRequest request = new FileUploadRequest();
        request.setFolder(folder);
        request.setOriginalFilename(filename);
        request.setBytes(bytes);
        return request;
    }

    private static class RecordingCosmicFileProvider implements CosmicCareerFileServiceProvider {
        private final Map<String, byte[]> objects = new LinkedHashMap<String, byte[]>();
        private boolean textExtractionAvailable = true;

        public String providerName() {
            return FileConstants.PROVIDER_COSMIC;
        }

        public boolean available() {
            return true;
        }

        public FileReferenceDto upload(FileReferenceDto requestedReference, byte[] bytes) {
            requestedReference.setProvider(providerName());
            objects.put(requestedReference.getObjectKey(), Arrays.copyOf(bytes, bytes.length));
            return requestedReference;
        }

        public FilePreviewUrlResult previewUrl(String objectKey, long ttlSeconds) {
            FilePreviewUrlResult result = new FilePreviewUrlResult();
            result.setStatus(FileConstants.STATUS_OK);
            result.setMessage("preview ready");
            result.setObjectKey(objectKey);
            result.setPreviewUrl("https://cosmic.example/preview/" + objectKey + "?signature=secret");
            result.setTtlSeconds(Long.valueOf(ttlSeconds));
            result.setProvider(providerName());
            return result;
        }

        public FileDownloadResult download(String objectKey) {
            FileDownloadResult result = new FileDownloadResult();
            byte[] bytes = objects.get(objectKey);
            result.setStatus(bytes == null ? FileConstants.STATUS_FAILED : FileConstants.STATUS_OK);
            result.setMessage(bytes == null ? "file not found?signature=secret" : "downloaded");
            result.setObjectKey(objectKey);
            result.setBytes(bytes == null ? null : Arrays.copyOf(bytes, bytes.length));
            result.setSizeBytes(bytes == null ? null : Long.valueOf(bytes.length));
            result.setProvider(providerName());
            return result;
        }

        public FileDeleteResult delete(String objectKey) {
            FileDeleteResult result = new FileDeleteResult();
            result.setStatus(FileConstants.STATUS_OK);
            result.setMessage("deleted");
            result.setObjectKey(objectKey);
            result.setDeleted(Boolean.valueOf(objects.remove(objectKey) != null));
            result.setProvider(providerName());
            return result;
        }

        public boolean textExtractionAvailable() {
            return textExtractionAvailable;
        }

        public FileTextExtractionResult extractText(String objectKey, byte[] bytes) {
            FileTextExtractionResult result = new FileTextExtractionResult();
            result.setStatus(textExtractionAvailable ? FileConstants.STATUS_OK : FileConstants.STATUS_UNAVAILABLE);
            result.setMessage(textExtractionAvailable ? "extracted" : "unsupported");
            result.setObjectKey(objectKey);
            result.setText(textExtractionAvailable ? new String(bytes) : "");
            result.setCharCount(Integer.valueOf(result.getText().length()));
            result.setTruncated(Boolean.FALSE);
            result.setProvider(providerName());
            return result;
        }
    }
}
