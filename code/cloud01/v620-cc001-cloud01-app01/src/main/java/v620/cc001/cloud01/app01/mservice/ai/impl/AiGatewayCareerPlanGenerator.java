package v620.cc001.cloud01.app01.mservice.ai.impl;

import v620.cc001.cloud01.app01.mservice.ai.*;
import v620.base.helper.ai.AiJsonHelper;
import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiMessageDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.cloud01.app01.mservice.ai.AiGateway;

import java.time.LocalDateTime;
import java.util.Collections;

public class AiGatewayCareerPlanGenerator implements CareerPlanAiGenerator {

    private final AiGateway gateway;
    private final AiJsonHelper jsonHelper;

    public AiGatewayCareerPlanGenerator(AiGateway gateway) {
        this(gateway, new AiJsonHelper());
    }

    public AiGatewayCareerPlanGenerator(AiGateway gateway, AiJsonHelper jsonHelper) {
        this.gateway = gateway;
        this.jsonHelper = jsonHelper;
    }

    public CareerPlanRecordDto generate(String userId, String targetRole, CareerUserProfileDto profile) {
        AiChatRequestDto request = new AiChatRequestDto();
        request.setMessages(Collections.singletonList(new AiMessageDto("user", prompt(targetRole, profile))));
        AiChatResponseDto response = gateway.chat(request);
        if (response == null || response.getErrorCode() != null) {
            return null;
        }
        String json = jsonHelper.extractJsonObject(response.getContent());
        if (json == null || !jsonHelper.containsRequiredFields(json,
                java.util.Arrays.asList("target_role", "phases", "weekly_plan", "daily_suggestions"))) {
            return null;
        }
        CareerPlanRecordDto plan = new CareerPlanRecordDto();
        plan.setUserId(userId);
        plan.setTargetRole(targetRole);
        plan.setStartStateSummary(json);
        plan.setPlanningMode("AGENT");
        plan.setAgentStatus("AGENT_GENERATED");
        plan.setHorizonYears(Integer.valueOf(3));
        plan.setModelUsed(response.getModelName());
        plan.setTokensConsumed(response.getUsage() == null ? Integer.valueOf(0) : response.getUsage().getTotalTokens());
        plan.setGeneratedAt(LocalDateTime.now());
        plan.setLastUpdatedAt(LocalDateTime.now());
        plan.setVersion(Integer.valueOf(2));
        return plan;
    }

    private String prompt(String targetRole, CareerUserProfileDto profile) {
        return "请生成职业路线规划 JSON，必须包含 target_role,start_state,phases,weekly_plan,daily_suggestions。"
                + "phases 需要覆盖 1 年和 3 年目标，每个阶段包含 goal,actions,kpis,sub_stages；"
                + "weekly_plan 给出本周目标、行动和交付物；daily_suggestions 给出每天建议。目标岗位："
                + (targetRole == null ? "" : targetRole)
                + "；画像完整度："
                + (profile == null ? "" : profile.getCompletenessScore());
    }
}
