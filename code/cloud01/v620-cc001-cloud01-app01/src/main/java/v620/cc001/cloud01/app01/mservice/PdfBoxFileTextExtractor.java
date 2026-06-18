package v620.cc001.cloud01.app01.mservice;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Extracts searchable PDF text and keeps the existing plain-text behavior.
 */
public class PdfBoxFileTextExtractor implements FileTextExtractor {

    public String extract(byte[] bytes, String objectKey) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        String lower = objectKey == null ? "" : objectKey.toLowerCase();
        if (lower.endsWith(".txt") || lower.endsWith(".md")) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
        if (!lower.endsWith(".pdf")) {
            return "";
        }
        try (PDDocument document = PDDocument.load(bytes)) {
            return new PDFTextStripper().getText(document);
        } catch (IOException ex) {
            throw new FileTextExtractionException("PDF_PARSE_FAILED", "PDF 正文读取失败");
        }
    }

    public boolean available() {
        return true;
    }
}
