package v620.cc001.cloud01.app01.mservice.ai.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;

/** Shared JSON parser for employment and further-study planning results. */
final class AgentPlatformPlanJsonParser {

    private AgentPlatformPlanJsonParser() {
    }

    static ParsedPlan parse(ObjectMapper mapper, String answer, String errorMessage) {
        try {
            List<String> candidates = jsonObjects(answer);
            for (int index = candidates.size() - 1; index >= 0; index--) {
                JsonNode root = planningNode(mapper, mapper.readTree(candidates.get(index)), 0);
                if (root != null) {
                    return new ParsedPlan(root, mapper.treeToValue(root, CareerPlanRecordDto.class));
                }
            }
            JsonNode root = planningNode(mapper, mapper.readTree(answer), 0);
            if (root != null) {
                return new ParsedPlan(root, mapper.treeToValue(root, CareerPlanRecordDto.class));
            }
            throw new IllegalArgumentException("No structured planning result");
        } catch (Exception ex) {
            throw new IllegalStateException(errorMessage);
        }
    }

    private static JsonNode planningNode(ObjectMapper mapper, JsonNode node, int depth) {
        if (node == null || node.isNull() || depth > 8) return null;
        if (isPlanningObject(node)) return node;
        if (node.isTextual()) {
            String text = node.asText();
            if (!hasText(text)) return null;
            try {
                List<String> objects = jsonObjects(text);
                for (int index = objects.size() - 1; index >= 0; index--) {
                    JsonNode found = planningNode(mapper, mapper.readTree(objects.get(index)), depth + 1);
                    if (found != null) return found;
                }
                return planningNode(mapper, mapper.readTree(text), depth + 1);
            } catch (Exception ignored) {
                return null;
            }
        }
        if (node.isArray()) {
            for (int index = node.size() - 1; index >= 0; index--) {
                JsonNode found = planningNode(mapper, node.get(index), depth + 1);
                if (found != null) return found;
            }
            return null;
        }
        if (!node.isObject()) return null;

        String[] preferred = {"answer", "output", "result", "data", "content", "promptoutput"};
        for (String field : preferred) {
            JsonNode found = planningNode(mapper, fieldIgnoreCase(node, field), depth + 1);
            if (found != null) return found;
        }
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            if (isToolInput(field.getKey())) continue;
            JsonNode found = planningNode(mapper, field.getValue(), depth + 1);
            if (found != null) return found;
        }
        return null;
    }

    private static JsonNode fieldIgnoreCase(JsonNode node, String expected) {
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            if (expected.equals(normalize(field.getKey()))) return field.getValue();
        }
        return null;
    }

    private static boolean isToolInput(String field) {
        String normalized = normalize(field);
        return "actioninput".equals(normalized) || "toolinput".equals(normalized)
                || "arguments".equals(normalized);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase()
                .replace("_", "").replace("-", "");
    }

    private static boolean isPlanningObject(JsonNode root) {
        if (root == null || !root.isObject()) return false;
        boolean target = text(root, "targetRole") || text(root, "targetSummary");
        return target && root.has("phases") && root.get("phases").isArray()
                && root.has("weeklyPlan") && root.get("weeklyPlan").isObject();
    }

    private static boolean text(JsonNode root, String field) {
        JsonNode value = root.get(field);
        return value != null && value.isTextual() && hasText(value.asText());
    }

    private static List<String> jsonObjects(String value) {
        List<String> objects = new ArrayList<String>();
        if (!hasText(value)) return objects;
        int start = -1;
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            if (inString) {
                if (escaped) escaped = false;
                else if (current == '\\') escaped = true;
                else if (current == '"') inString = false;
                continue;
            }
            if (current == '"') inString = true;
            else if (current == '{') {
                if (depth == 0) start = index;
                depth++;
            } else if (current == '}' && depth > 0) {
                depth--;
                if (depth == 0 && start >= 0) {
                    objects.add(value.substring(start, index + 1));
                    start = -1;
                }
            }
        }
        return objects;
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    static final class ParsedPlan {
        private final JsonNode root;
        private final CareerPlanRecordDto plan;

        private ParsedPlan(JsonNode root, CareerPlanRecordDto plan) {
            this.root = root;
            this.plan = plan;
        }

        JsonNode getRoot() {
            return root;
        }

        CareerPlanRecordDto getPlan() {
            return plan;
        }
    }
}
