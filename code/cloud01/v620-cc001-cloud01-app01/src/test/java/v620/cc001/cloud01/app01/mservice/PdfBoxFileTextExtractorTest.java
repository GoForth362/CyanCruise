package v620.cc001.cloud01.app01.mservice;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;
import v620.base.helper.career.FileUploadPreviewService;
import v620.cc001.base.common.dto.career.FileConstants;
import v620.cc001.base.common.dto.career.FileTextExtractionResult;
import v620.cc001.base.common.dto.career.FileUploadRequest;
import v620.cc001.base.common.dto.career.FileUploadResult;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfBoxFileTextExtractorTest {

    @Test
    void extractsSearchablePdfText() throws Exception {
        FileUploadPreviewApplicationService service = service();
        FileUploadResult upload = service.upload(upload("resume.pdf", pdf("Java backend resume")));

        FileTextExtractionResult result = service.extractText(upload.getFile().getObjectKey());

        assertEquals(FileConstants.STATUS_OK, result.getStatus());
        assertTrue(result.getText().contains("Java backend resume"));
    }

    @Test
    void reportsBlankAndDamagedPdfWithoutInventingText() throws Exception {
        FileUploadPreviewApplicationService service = service();
        FileUploadResult blank = service.upload(upload("blank.pdf", blankPdf()));
        FileUploadResult damaged = service.upload(upload("damaged.pdf", "not a pdf".getBytes(StandardCharsets.UTF_8)));

        assertEquals(FileConstants.STATUS_TEXT_EMPTY,
                service.extractText(blank.getFile().getObjectKey()).getStatus());
        assertEquals("PDF_PARSE_FAILED",
                service.extractText(damaged.getFile().getObjectKey()).getStatus());
    }

    @Test
    void truncatesLongPdfTextAndKeepsPlainTextCompatibility() throws Exception {
        FileUploadPreviewApplicationService service = service();
        String longText = repeat("resume ", 3500);
        FileUploadResult pdf = service.upload(upload("long.pdf", pdf(longText)));
        FileUploadResult txt = service.upload(upload("resume.txt", "plain resume".getBytes(StandardCharsets.UTF_8)));

        FileTextExtractionResult pdfResult = service.extractText(pdf.getFile().getObjectKey());
        FileTextExtractionResult txtResult = service.extractText(txt.getFile().getObjectKey());

        assertEquals(FileConstants.STATUS_OK, pdfResult.getStatus());
        assertEquals(Integer.valueOf(FileConstants.MAX_EXTRACTED_TEXT_CHARS), pdfResult.getCharCount());
        assertEquals(Boolean.TRUE, pdfResult.getTruncated());
        assertEquals("plain resume", txtResult.getText());
    }

    private FileUploadPreviewApplicationService service() {
        return new FileUploadPreviewApplicationService(
                new InMemoryCareerFileStorage(),
                new PdfBoxFileTextExtractor(),
                new FileUploadPreviewService());
    }

    private FileUploadRequest upload(String filename, byte[] bytes) {
        FileUploadRequest request = new FileUploadRequest();
        request.setFolder("resumes");
        request.setOriginalFilename(filename);
        request.setBytes(bytes);
        return request;
    }

    private byte[] pdf(String text) throws Exception {
        PDDocument document = new PDDocument();
        try {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream content = new PDPageContentStream(document, page);
            try {
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 10);
                content.newLineAtOffset(40, 700);
                content.showText(text);
                content.endText();
            } finally {
                content.close();
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            document.save(output);
            return output.toByteArray();
        } finally {
            document.close();
        }
    }

    private byte[] blankPdf() throws Exception {
        PDDocument document = new PDDocument();
        try {
            document.addPage(new PDPage());
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            document.save(output);
            return output.toByteArray();
        } finally {
            document.close();
        }
    }

    private String repeat(String value, int count) {
        StringBuilder out = new StringBuilder(value.length() * count);
        for (int i = 0; i < count; i += 1) {
            out.append(value);
        }
        return out.toString();
    }
}
