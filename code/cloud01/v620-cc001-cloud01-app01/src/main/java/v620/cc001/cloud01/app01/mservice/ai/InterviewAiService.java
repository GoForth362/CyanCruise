package v620.cc001.cloud01.app01.mservice.ai;

import v620.base.helper.career.InterviewAiHelper;
import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiMessageDto;
import v620.cc001.base.common.dto.career.InterviewReportDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;
import v620.cc001.cloud01.app01.mservice.ai.impl.AgentPlatformInterviewReportAnalyzer;
import v620.cc001.cloud01.app01.mservice.ai.impl.DefaultAgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.impl.KingdeeAgentSdkTaskFlowClient;

import java.util.Collections;

public class InterviewAiService {
    private final AiGateway gateway;
    private final InterviewAiHelper helper;
    private final InterviewReportAnalyzer reportAnalyzer;

    public InterviewAiService(AiGateway gateway) {
        this(gateway, defaultReportAnalyzer(), new InterviewAiHelper());
    }

    public InterviewAiService(AiGateway gateway, InterviewReportAnalyzer reportAnalyzer) {
        this(gateway, reportAnalyzer, new InterviewAiHelper());
    }

    InterviewAiService(AiGateway gateway, InterviewReportAnalyzer reportAnalyzer, InterviewAiHelper helper) {
        this.gateway = gateway;
        this.reportAnalyzer = reportAnalyzer;
        this.helper = helper == null ? new InterviewAiHelper() : helper;
    }

    public String question(String position, String difficulty, String resumeText, String profileSummary,
                           String transcript, int answerCount, boolean opening) {
        String content = ask(helper.questionPrompt(position, difficulty, resumeText, profileSummary, transcript, opening));
        if (content == null || content.trim().length() == 0 || content.trim().startsWith("{")) {
            return helper.temporaryQuestion(position, difficulty, answerCount, opening);
        }
        return content.trim();
    }

    public InterviewReportDto report(InterviewSessionDto session, String transcript, int answerCount) {
        if (reportAnalyzer != null) {
            try {
                InterviewReportDto report = reportAnalyzer.analyze(session, transcript, answerCount);
                if (report != null) {
                    report.setAnalysisSource(InterviewReportDto.ANALYSIS_SOURCE_AI_AGENT);
                    return report;
                }
            } catch (RuntimeException ignored) {
                // The provider failure remains observable in its adapter; the interview flow uses a scoped business fallback.
            }
        }
        return helper.basicRulesReport(session, transcript, answerCount);
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

    private static InterviewReportAnalyzer defaultReportAnalyzer() {
        AgentPlatformTaskFlowConfig config = AgentPlatformTaskFlowConfig.fromSystemProperties(
                AgentPlatformInterviewReportAnalyzer.CONFIG_PREFIX);
        if (config.isEnabled() && hasText(config.getAgentNumber())) {
            return new AgentPlatformInterviewReportAnalyzer(new KingdeeAgentSdkTaskFlowClient(config), config);
        }
        if (config.isAvailable()) {
            return new AgentPlatformInterviewReportAnalyzer(new DefaultAgentPlatformTaskFlowClient(config), config);
        }
        if (config.isAgentSdkAvailable()) {
            return new AgentPlatformInterviewReportAnalyzer(new KingdeeAgentSdkTaskFlowClient(config), config);
        }
        return new AgentPlatformInterviewReportAnalyzer(null, config);
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
