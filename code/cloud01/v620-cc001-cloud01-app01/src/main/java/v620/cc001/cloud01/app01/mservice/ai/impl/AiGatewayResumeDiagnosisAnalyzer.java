package v620.cc001.cloud01.app01.mservice.ai.impl;

import v620.cc001.cloud01.app01.mservice.ai.*;
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
        String jobRequirements = request == null ? "" : request.getJobDescription();
        String targetJob = request == null ? "" : request.getTargetJob();
        String profile = request == null ? "" : request.getProfileContext();
        return "请诊断简历并只返回 JSON。字段必须包含 overallScore,scoreBreakdown,strengths,weaknesses,suggestions,revisionSuggestions。"
                + " scoreBreakdown 必须包含内容完整度25分、目标岗位匹配30分、经历证据30分、表达清晰度15分，每项包含name,score,maxScore,reason，四项得分之和等于overallScore。"
                + " revisionSuggestions 是数组，每项包含 suggestionId,issueType,priority,resumeSection,problem,action,rewriteExample,evidence,targetKeywords,status,contextSource。"
                + " 建议必须结合目标岗位和岗位要求，指出简历中的具体问题、修改动作和参考写法。所有用户可见文字使用普通中文，不使用行业缩写。\n目标岗位：\n"
                + (targetJob == null ? "" : targetJob)
                + "\n用户画像：\n"
                + (profile == null ? "" : profile)
                + "\n岗位要求：\n"
                + (jobRequirements == null ? "" : jobRequirements)
                + "\n简历正文：\n"
                + (resumeText == null ? "" : resumeText);
    }
}
