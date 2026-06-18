package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiMessageDto;
import v620.cc001.base.common.dto.career.ResumeDiagnosisRequest;
import v620.cc001.cloud01.app01.mservice.ai.AiGateway;

import java.util.Collections;

public class AiGatewayResumeDiagnosisAnalyzer implements ResumeDiagnosisAnalyzer {

    private final AiGateway gateway;
    private final ResumeDiagnosisAnalyzer fallback;

    public AiGatewayResumeDiagnosisAnalyzer(AiGateway gateway, ResumeDiagnosisAnalyzer fallback) {
        this.gateway = gateway;
        this.fallback = fallback == null ? new DefaultResumeDiagnosisAnalyzer() : fallback;
    }

    public String analyze(ResumeDiagnosisRequest request, String resumeText) {
        AiChatRequestDto aiRequest = new AiChatRequestDto();
        aiRequest.setMessages(Collections.singletonList(new AiMessageDto("user", prompt(request, resumeText))));
        AiChatResponseDto response = gateway.chat(aiRequest);
        if (response == null || response.getErrorCode() != null || response.getContent() == null) {
            return fallback.analyze(request, resumeText);
        }
        return response.getContent();
    }

    private String prompt(ResumeDiagnosisRequest request, String resumeText) {
        String jd = request == null ? "" : request.getJobDescription();
        String targetJob = request == null ? "" : request.getTargetJob();
        String profile = request == null ? "" : request.getProfileContext();
        return "请诊断简历并只返回 JSON。字段必须包含 overallScore,strengths,weaknesses,suggestions,revisionSuggestions。"
                + " revisionSuggestions 是数组，每项包含 suggestionId,issueType,priority,resumeSection,problem,action,rewriteExample,evidence,targetKeywords,status,contextSource。"
                + " 建议必须围绕目标岗位/JD，给出可执行改写方向。\nTARGET_JOB:\n"
                + (targetJob == null ? "" : targetJob)
                + "\nPROFILE_CONTEXT:\n"
                + (profile == null ? "" : profile)
                + "\nJD:\n"
                + (jd == null ? "" : jd)
                + "\nRESUME:\n"
                + (resumeText == null ? "" : resumeText);
    }
}
