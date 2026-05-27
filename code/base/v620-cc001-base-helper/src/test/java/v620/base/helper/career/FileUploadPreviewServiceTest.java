package v620.base.helper.career;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.FileConstants;
import v620.cc001.base.common.dto.career.FileReferenceDto;
import v620.cc001.base.common.dto.career.FileTextExtractionResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileUploadPreviewServiceTest {

    private final FileUploadPreviewService service = new FileUploadPreviewService();

    @Test
    void folderAndExtensionRulesMatchIpdContract() {
        FileReferenceDto reference = service.buildReference(" ", "resume.PDF", new byte[]{1, 2});

        assertEquals(FileConstants.DEFAULT_FOLDER, reference.getFolder());
        assertEquals(".pdf", reference.getExtension());
        assertTrue(reference.getObjectKey().startsWith("others/"));
        assertTrue(reference.getObjectKey().endsWith(".pdf"));
    }

    @Test
    void objectKeyNormalizationHandlesKeysAndLegacyUrls() {
        assertEquals("resumes/a.pdf", service.normalizeObjectKey("resumes/a.pdf"));
        assertEquals("resumes/a.pdf", service.normalizeObjectKey("/resumes/a.pdf"));
        assertEquals("resumes/a.pdf", service.normalizeObjectKey("https://bucket.endpoint/resumes/a.pdf?signature=x"));
        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() {
                service.normalizeObjectKey("https://bucket.endpoint");
            }
        });
    }

    @Test
    void ttlAndUploadValidationAreSafe() {
        assertEquals(FileConstants.MIN_PREVIEW_TTL_SECONDS, service.clampTtl(1));
        assertEquals(FileConstants.MAX_PREVIEW_TTL_SECONDS, service.clampTtl(999999));
        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() {
                service.validateUpload(new byte[0]);
            }
        });
    }

    @Test
    void textExtractionResultIsCapped() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < FileConstants.MAX_EXTRACTED_TEXT_CHARS + 10; i++) {
            builder.append('a');
        }

        FileTextExtractionResult result = service.textResult("resumes/a.pdf", builder.toString(), FileConstants.STATUS_OK, "OK");

        assertEquals(Integer.valueOf(FileConstants.MAX_EXTRACTED_TEXT_CHARS), result.getCharCount());
        assertEquals(Boolean.TRUE, result.getTruncated());
    }
}
