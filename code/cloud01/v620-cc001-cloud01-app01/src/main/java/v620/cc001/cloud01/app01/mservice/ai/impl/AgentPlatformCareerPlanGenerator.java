package v620.cc001.cloud01.app01.mservice.ai.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;
import v620.cc001.cloud01.app01.mservice.ai.CareerPlanAiGenerator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.time.LocalDate;

/**
 * Generates a structured employment plan through the published Kingdee Agent task flow.
 */
public class AgentPlatformCareerPlanGenerator implements CareerPlanAiGenerator {

    public static final String CONFIG_PREFIX = "cc001.agent.platform.employment";

    private final AgentPlatformTaskFlowClient client;
    private final AgentPlatformTaskFlowConfig config;
    private final ObjectMapper mapper;

    public AgentPlatformCareerPlanGenerator(AgentPlatformTaskFlowClient client,
                                            AgentPlatformTaskFlowConfig config) {
        this(client, config, new ObjectMapper());
    }

    AgentPlatformCareerPlanGenerator(AgentPlatformTaskFlowClient client,
                                     AgentPlatformTaskFlowConfig config,
                                     ObjectMapper mapper) {
        this.client = client;
        this.config = config == null ? new AgentPlatformTaskFlowConfig() : config;
        this.mapper = mapper == null ? new ObjectMapper() : mapper;
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public CareerPlanRecordDto generate(String userId, String targetRole, CareerUserProfileDto profile) {
        if (client == null) {
            throw new IllegalStateException("就业规划智能服务暂不可用，请稍后重试。");
        }
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.setTaskFlowCode(config.getTaskFlowCode());
        request.putInput("question", question(targetRole, profile));
        AgentTaskFlowResponseDto response = client.execute(request);
        if (response == null || !response.isSuccess() || !hasText(response.getAnswer())) {
            throw new IllegalStateException("就业规划智能服务暂不可用，请稍后重试。");
        }
        CareerPlanRecordDto plan = parse(response.getAnswer());
        validate(plan);
        plan.setPlanningMode("AGENT");
        plan.setAgentStatus("AGENT_GENERATED");
        plan.setModelUsed("kingdee-agent-sdk");
        plan.setTokensConsumed(Integer.valueOf(0));
        return plan;
    }

    private String question(String targetRole, CareerUserProfileDto profile) {
        try {
            Map<String, Object> payload = new LinkedHashMap<String, Object>();
            payload.put("mode", "EMPLOYMENT_PLANNING");
            payload.put("currentDate", LocalDate.now().toString());
            payload.put("targetRole", targetRole);
            payload.put("profileSummary", profile);
            payload.put("resumeEvidence", profile == null ? null : profile.getEvidence());
            payload.put("userQuestion", "请根据现有资料生成未来一年的可执行就业路线图");
            return mapper.writeValueAsString(payload);
        } catch (Exception ex) {
            throw new IllegalStateException("就业规划资料暂时无法整理，请稍后重试。");
        }
    }

    private CareerPlanRecordDto parse(String answer) {
        return AgentPlatformPlanJsonParser.parse(mapper, answer,
                "就业规划结果格式不完整，请稍后重试。").getPlan();
    }

    private void validate(CareerPlanRecordDto plan) {
        if (plan == null || !hasText(plan.getTargetRole()) || !hasText(plan.getStartStateSummary())
                || plan.getPhases() == null || plan.getPhases().isEmpty()
                || plan.getWeeklyPlan() == null
                || plan.getDailySuggestions() == null || plan.getDailySuggestions().isEmpty()) {
            throw new IllegalStateException("就业规划结果格式不完整，请稍后重试。");
        }
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
