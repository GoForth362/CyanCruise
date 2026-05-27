package v620.base.helper.ai;

import java.util.Collection;
import java.util.Map;

public class AiJsonHelper {

    public String extractJsonObject(String raw) {
        return extractJson(raw, '{', '}');
    }

    public String extractJsonArray(String raw) {
        return extractJson(raw, '[', ']');
    }

    public boolean containsRequiredFields(String json, Collection<String> requiredFields) {
        if (json == null || requiredFields == null) {
            return false;
        }
        for (String field : requiredFields) {
            if (field == null) {
                continue;
            }
            if (!json.contains("\"" + field + "\"")) {
                return false;
            }
        }
        return true;
    }

    public String buildFlatJson(Map<String, String> values) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        if (values != null) {
            for (Map.Entry<String, String> entry : values.entrySet()) {
                if (!first) {
                    sb.append(",");
                }
                first = false;
                sb.append("\"").append(escape(entry.getKey())).append("\":");
                sb.append("\"").append(escape(entry.getValue())).append("\"");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private String extractJson(String raw, char open, char close) {
        if (raw == null || raw.trim().length() == 0) {
            return null;
        }
        String text = stripFence(raw.trim());
        int start = text.indexOf(open);
        if (start < 0) {
            return null;
        }
        boolean inString = false;
        boolean escaped = false;
        int depth = 0;
        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '"') {
                inString = !inString;
                continue;
            }
            if (inString) {
                continue;
            }
            if (c == open) {
                depth++;
            } else if (c == close) {
                depth--;
                if (depth == 0) {
                    return text.substring(start, i + 1).trim();
                }
            }
        }
        return null;
    }

    private String stripFence(String text) {
        if (!text.startsWith("```")) {
            return text;
        }
        int firstLine = text.indexOf('\n');
        int lastFence = text.lastIndexOf("```");
        if (firstLine >= 0 && lastFence > firstLine) {
            return text.substring(firstLine + 1, lastFence).trim();
        }
        return text;
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
