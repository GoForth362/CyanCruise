package v620.cc001.cloud01.app01.mservice.ai.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.base.common.dto.career.ResumeDiagnosisRequest;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;
import v620.cc001.cloud01.app01.mservice.ai.ResumeDiagnosisAnalyzer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converts the existing resume diagnosis request into the Agent task flow contract.
 */
public class AgentPlatformResumeDiagnosisAnalyzer implements ResumeDiagnosisAnalyzer {

    public static final String SOURCE_AGENT_AI = "AGENT_AI";
    private static final int MAX_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MILLIS = 250L;
    private final AgentPlatformTaskFlowClient client;
    private final AgentPlatformTaskFlowConfig config;
    private final ObjectMapper mapper;
    private final ThreadLocal<String> lastResultSource = new ThreadLocal<String>();

    public AgentPlatformResumeDiagnosisAnalyzer(AgentPlatformTaskFlowClient client,
                                                AgentPlatformTaskFlowConfig config) {
        this(client, config, new ObjectMapper());
    }

    public AgentPlatformResumeDiagnosisAnalyzer(AgentPlatformTaskFlowClient client,
                                                AgentPlatformTaskFlowConfig config,
                                                ResumeDiagnosisAnalyzer fallback) {
        this(client, config, new ObjectMapper());
    }

    public AgentPlatformResumeDiagnosisAnalyzer(AgentPlatformTaskFlowClient client,
                                                AgentPlatformTaskFlowConfig config,
                                                ResumeDiagnosisAnalyzer ignoredFallback,
                                                ObjectMapper mapper) {
        this(client, config, mapper);
    }

    public AgentPlatformResumeDiagnosisAnalyzer(AgentPlatformTaskFlowClient client,
                                                AgentPlatformTaskFlowConfig config,
                                                ObjectMapper mapper) {
        this.client = client;
        this.config = config == null ? new AgentPlatformTaskFlowConfig() : config;
        this.mapper = mapper == null ? new ObjectMapper() : mapper;
    }

    public String analyze(ResumeDiagnosisRequest request, String resumeText) {
        if (client == null) {
            throw unavailable("AI 简历诊断暂未配置，请稍后重试。", null);
        }
        Exception lastError = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                AgentTaskFlowResponseDto response = client.execute(taskFlowRequest(request, resumeText));
                String answer = response == null || !response.isSuccess() ? null : validDiagnosis(response.getAnswer());
                if (answer != null) {
                    lastResultSource.set(SOURCE_AGENT_AI);
                    return answer;
                }
            } catch (Exception e) {
                lastError = e;
            }
            if (attempt < MAX_ATTEMPTS) {
                waitBeforeRetry();
            }
        }
        throw unavailable("AI 简历诊断暂时不可用，请稍后重试。", lastError);
    }

    /**
     * The application service reads this after analyze on the same request thread so the
     * page can mark a successfully parsed Agent result.
     */
    public String getLastResultSource() {
        String source = lastResultSource.get();
        lastResultSource.remove();
        return source == null ? SOURCE_AGENT_AI : source;
    }

    private IllegalStateException unavailable(String message, Exception cause) {
        return cause == null ? new IllegalStateException(message) : new IllegalStateException(message, cause);
    }

    private String validDiagnosis(String answer) {
        String json = jsonObject(answer);
        return isValidDiagnosis(json) ? json : null;
    }

    private String jsonObject(String answer) {
        if (!hasText(answer)) return null;
        int start = answer.indexOf('{');
        int end = answer.lastIndexOf('}');
        return start >= 0 && end > start ? answer.substring(start, end + 1).trim() : answer.trim();
    }

    private void waitBeforeRetry() {
        try {
            Thread.sleep(RETRY_DELAY_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private AgentTaskFlowRequestDto taskFlowRequest(ResumeDiagnosisRequest request, String resumeText) throws Exception {
        ResumeDiagnosisRequest safe = request == null ? new ResumeDiagnosisRequest() : request;
        Map<String, String> context = new LinkedHashMap<String, String>();
        context.put("resumeText", resumeText == null ? "" : resumeText);
        context.put("targetJob", value(safe.getTargetJob()));
        context.put("jobDescription", value(safe.getJobDescription()));
        context.put("profileContext", value(safe.getProfileContext()));
        context.put("diagnosisInstructions", "若 jobDescription 非空，必须逐条阅读岗位要求。"
                + "“目标岗位匹配”这一项的 reason 必须按“1. 已匹配：岗位要求中的具体能力点；2. 未匹配：岗位要求中的具体能力点；3. 证据不足：岗位要求中的具体能力点”的格式输出。"
                + "每条必须引用 jobDescription 中实际出现的能力点；没有对应简历证据时明确写“简历未提供证据”，不得使用泛泛的匹配结论。"
                + "目标岗位、岗位要求与用户画像存在冲突时必须说明冲突。"
                + "不得编造技术、职责、成果、数字或经历；证据不足时写明需要补充的真实证据。"
                + "评分说明、问题、怎么改和参考写法均须拆成不超过三条独立短句，并使用“1. 2. 3.”编号。"
                + "每条只表达一个事实或行动，不得写成长段落，不得输出转义换行符。"
                + "参考写法仅使用用户已提供的事实，缺失位置写成“请补充真实信息”，不要使用方括号占位符或转义字符。");
        AgentTaskFlowRequestDto taskFlowRequest = new AgentTaskFlowRequestDto();
        taskFlowRequest.setTaskFlowCode(invocationCode());
        taskFlowRequest.putInput("question", mapper.writeValueAsString(context));
        return taskFlowRequest;
    }

    private boolean isValidDiagnosis(String answer) {
        try {
            if (!hasText(answer)) return false;
            JsonNode root = mapper.readTree(answer);
            if (!root.isObject() || !root.path("overallScore").canConvertToInt()
                    || !root.path("scoreBreakdown").isArray() || !root.path("revisionSuggestions").isArray()
                    || !root.path("strengths").isArray() || !root.path("weaknesses").isArray()
                    || !root.path("suggestions").isArray()) {
                return false;
            }
            int total = 0;
            boolean completeness = false;
            boolean matching = false;
            boolean evidence = false;
            boolean clarity = false;
            for (JsonNode item : root.path("scoreBreakdown")) {
                if (!item.path("score").canConvertToInt() || !item.path("maxScore").canConvertToInt()) return false;
                int score = item.path("score").asInt();
                int max = item.path("maxScore").asInt();
                if (score < 0 || score > max) return false;
                String name = item.path("name").asText();
                if ("内容完整度".equals(name) && max == 25) completeness = true;
                if ("目标岗位匹配".equals(name) && max == 30) matching = true;
                if ("经历证据".equals(name) && max == 30) evidence = true;
                if ("表达清晰度".equals(name) && max == 15) clarity = true;
                total += score;
            }
            int overall = root.path("overallScore").asInt();
            return overall >= 0 && overall <= 100 && overall == total
                    && completeness && matching && evidence && clarity;
        } catch (Exception ignored) {
            return false;
        }
    }

    private String value(String text) {
        return text == null ? "" : text;
    }

    private String invocationCode() {
        return hasText(config.getAgentNumber()) ? config.getAgentNumber() : config.getTaskFlowCode();
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
