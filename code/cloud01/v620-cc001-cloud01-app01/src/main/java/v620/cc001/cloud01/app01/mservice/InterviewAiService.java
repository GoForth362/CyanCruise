package v620.cc001.cloud01.app01.mservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import v620.base.helper.ai.AiJsonHelper;
import v620.base.helper.career.InterviewAiHelper;
import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiMessageDto;
import v620.cc001.base.common.dto.career.InterviewAdviceItemDto;
import v620.cc001.base.common.dto.career.InterviewRadarScoreDto;
import v620.cc001.base.common.dto.career.InterviewReportDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;
import v620.cc001.cloud01.app01.mservice.ai.AiGateway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InterviewAiService {
    private final AiGateway gateway;
    private final InterviewAiHelper helper;
    private final AiJsonHelper jsonHelper;
    private final ObjectMapper mapper;

    public InterviewAiService(AiGateway gateway) {
        this(gateway, new InterviewAiHelper(), new AiJsonHelper(), new ObjectMapper());
    }

    InterviewAiService(AiGateway gateway, InterviewAiHelper helper, AiJsonHelper jsonHelper, ObjectMapper mapper) {
        this.gateway = gateway; this.helper = helper; this.jsonHelper = jsonHelper; this.mapper = mapper;
    }

    public String question(String position, String difficulty, String resumeText, String profileSummary,
                           String transcript, int answerCount, boolean opening) {
        String content = ask(helper.questionPrompt(position, difficulty, resumeText, profileSummary, transcript, opening));
        if (content == null || content.trim().length() == 0 || content.trim().startsWith("{")) {
            return helper.fallbackQuestion(position, answerCount);
        }
        return content.trim();
    }

    public InterviewReportDto report(InterviewSessionDto session, String transcript, int answerCount) {
        String content = ask(helper.reportPrompt(session, transcript, answerCount));
        InterviewReportDto parsed = parseReport(content, answerCount);
        return parsed == null ? helper.fallbackReport(session, answerCount) : parsed;
    }

    private String ask(String prompt) {
        try {
            AiChatRequestDto request = new AiChatRequestDto();
            request.setMessages(Collections.singletonList(new AiMessageDto("user", prompt)));
            AiChatResponseDto response = gateway == null ? null : gateway.chat(request);
            return response == null || response.getErrorCode() != null ? null : response.getContent();
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private InterviewReportDto parseReport(String raw, int answerCount) {
        try {
            String json = jsonHelper.extractJsonObject(raw);
            if (json == null) return null;
            JsonNode root = mapper.readTree(json);
            JsonNode radarNode = root.path("radarScore");
            if (radarNode.isMissingNode()) radarNode = root.path("radarChart");
            InterviewRadarScoreDto radar = new InterviewRadarScoreDto();
            radar.setExpression(score(radarNode, "expression", 60));
            radar.setLogic(score(radarNode, "logic", 60));
            radar.setTechnical(score(radarNode, "technical", 60));
            radar.setPressureResistance(score(radarNode, "pressureResistance", 60));
            radar.setCommunication(score(radarNode, "communication", 60));
            InterviewReportDto report = new InterviewReportDto();
            report.setOverallScore(Integer.valueOf(helper.clampScore(root.path("overallScore").asInt(60))));
            report.setTotalQuestions(Integer.valueOf(answerCount));
            report.setRadarScore(radar);
            report.setStrengths(advice(root.path("strengths")));
            report.setImprovements(advice(root.path("improvements")));
            report.setTextSummary(root.path("textSummary").asText(root.path("summary").asText("")));
            if (report.getTextSummary().trim().length() == 0) return null;
            return report;
        } catch (Exception ex) {
            return null;
        }
    }

    private Integer score(JsonNode node, String field, int fallback) {
        return Integer.valueOf(helper.clampScore(node.path(field).asInt(fallback)));
    }

    private List<InterviewAdviceItemDto> advice(JsonNode array) {
        List<InterviewAdviceItemDto> result = new ArrayList<InterviewAdviceItemDto>();
        if (!array.isArray()) return result;
        for (JsonNode node : array) {
            String title = node.path("title").asText("").trim();
            String detail = node.path("detail").asText("").trim();
            if (title.length() > 0 || detail.length() > 0) {
                InterviewAdviceItemDto item = new InterviewAdviceItemDto(); item.setTitle(title); item.setDetail(detail); result.add(item);
            }
        }
        return result;
    }
}
