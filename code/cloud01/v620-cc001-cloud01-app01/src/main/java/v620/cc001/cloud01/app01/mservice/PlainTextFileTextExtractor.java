package v620.cc001.cloud01.app01.mservice;

import java.nio.charset.StandardCharsets;

/**
 * Test/default extractor. Production PDF/OCR extraction should replace this adapter.
 */
public class PlainTextFileTextExtractor implements FileTextExtractor {

    public String extract(byte[] bytes, String objectKey) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        String lower = objectKey == null ? "" : objectKey.toLowerCase();
        if (lower.endsWith(".txt") || lower.endsWith(".md")) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return "";
    }

    public boolean available() {
        return true;
    }
}
