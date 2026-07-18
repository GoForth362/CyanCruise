package v620.cc001.cloud01.app01.mservice.ai.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;

/** Shared JSON parser for employment and further-study planning results. */
final class AgentPlatformPlanJsonParser {

    private AgentPlatformPlanJsonParser() {
    }

    static ParsedPlan parse(ObjectMapper mapper, String answer, String errorMessage) {
        try (JsonParser parser = mapper.getFactory().createParser(answer)) {
            JsonNode root = mapper.readTree(parser);
            if (parser.nextToken() != null) {
                throw new IllegalArgumentException("Multiple JSON roots are not supported");
            }
            if (root != null && root.has("data") && root.get("data").isObject()) {
                root = root.get("data");
            }
            return new ParsedPlan(root, mapper.treeToValue(root, CareerPlanRecordDto.class));
        } catch (Exception ex) {
            throw new IllegalStateException(errorMessage);
        }
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
