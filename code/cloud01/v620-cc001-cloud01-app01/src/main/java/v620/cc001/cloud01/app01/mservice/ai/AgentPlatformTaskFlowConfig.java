package v620.cc001.cloud01.app01.mservice.ai;

/**
 * Server-side configuration for the published Agent task flow endpoint.
 */
public class AgentPlatformTaskFlowConfig {

    public static final String PROP_ENABLED = "cc001.agent.platform.resume.enabled";
    public static final String PROP_AGENT_NUMBER = "cc001.agent.platform.resume.agentNumber";
    public static final String PROP_ENDPOINT = "cc001.agent.platform.resume.endpoint";
    public static final String PROP_ACCESS_TOKEN = "cc001.agent.platform.resume.accessToken";
    public static final String PROP_TASK_FLOW_CODE = "cc001.agent.platform.resume.taskFlowCode";
    public static final String PROP_TIMEOUT_SECONDS = "cc001.agent.platform.resume.timeoutSeconds";
    public static final String PROP_AUTHORIZATION_HEADER = "cc001.agent.platform.resume.authorizationHeader";
    public static final String PROP_AUTHORIZATION_PREFIX = "cc001.agent.platform.resume.authorizationPrefix";
    public static final String PROP_FLOW_CODE_FIELD = "cc001.agent.platform.resume.flowCodeField";
    public static final String PROP_INPUT_FIELD = "cc001.agent.platform.resume.inputField";
    public static final String PROP_ANSWER_PATH = "cc001.agent.platform.resume.answerPath";

    private boolean enabled;
    private String agentNumber;
    private String endpoint;
    private String accessToken;
    private String taskFlowCode;
    private int timeoutSeconds = 30;
    private String authorizationHeader = "Authorization";
    private String authorizationPrefix = "Bearer ";
    private String flowCodeField = "taskFlowCode";
    private String inputField = "inputs";
    private String answerPath = "answer";

    public static AgentPlatformTaskFlowConfig fromSystemProperties() {
        return fromSystemProperties("cc001.agent.platform.resume");
    }

    public static AgentPlatformTaskFlowConfig fromSystemProperties(String prefix) {
        String safePrefix = hasText(prefix) ? prefix.trim() : "cc001.agent.platform.resume";
        AgentPlatformTaskFlowConfig config = new AgentPlatformTaskFlowConfig();
        config.setEnabled(Boolean.parseBoolean(value(safePrefix + ".enabled", "false")));
        config.setAgentNumber(value(safePrefix + ".agentNumber", null));
        config.setEndpoint(value(safePrefix + ".endpoint", null));
        config.setAccessToken(value(safePrefix + ".accessToken", null));
        config.setTaskFlowCode(value(safePrefix + ".taskFlowCode", null));
        config.setTimeoutSeconds(parseInt(value(safePrefix + ".timeoutSeconds", null), 30));
        config.setAuthorizationHeader(value(safePrefix + ".authorizationHeader", "Authorization"));
        config.setAuthorizationPrefix(value(safePrefix + ".authorizationPrefix", "Bearer "));
        config.setFlowCodeField(value(safePrefix + ".flowCodeField", "taskFlowCode"));
        config.setInputField(value(safePrefix + ".inputField", "inputs"));
        config.setAnswerPath(value(safePrefix + ".answerPath", "answer"));
        return config;
    }

    public boolean isAvailable() {
        return enabled && hasText(endpoint) && hasText(accessToken) && hasText(taskFlowCode);
    }

    /**
     * The official Cosmic Agent SDK uses an agent number and the current server context,
     * so it does not require a browser-visible endpoint or access token.
     */
    public boolean isAgentSdkAvailable() {
        return enabled && (hasText(taskFlowCode) || hasText(agentNumber));
    }

    public String missingAgentSdkReason() {
        if (!enabled) return "Agent platform resume diagnosis is disabled";
        if (!hasText(taskFlowCode) && !hasText(agentNumber)) {
            return "Agent platform task flow code or agent number is not configured";
        }
        return null;
    }

    public String missingReason() {
        if (!enabled) return "Agent platform task flow is disabled";
        if (!hasText(endpoint)) return "Agent platform endpoint is not configured";
        if (!hasText(accessToken)) return "Agent platform access token is not configured";
        if (!hasText(taskFlowCode)) return "Agent platform task flow code is not configured";
        return null;
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getAgentNumber() { return agentNumber; }
    public void setAgentNumber(String agentNumber) { this.agentNumber = agentNumber; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getTaskFlowCode() { return taskFlowCode; }
    public void setTaskFlowCode(String taskFlowCode) { this.taskFlowCode = taskFlowCode; }
    public int getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds <= 0 ? 30 : timeoutSeconds; }
    public String getAuthorizationHeader() { return authorizationHeader; }
    public void setAuthorizationHeader(String authorizationHeader) { this.authorizationHeader = textOrDefault(authorizationHeader, "Authorization"); }
    public String getAuthorizationPrefix() { return authorizationPrefix; }
    public void setAuthorizationPrefix(String authorizationPrefix) { this.authorizationPrefix = authorizationPrefix == null ? "Bearer " : authorizationPrefix; }
    public String getFlowCodeField() { return flowCodeField; }
    public void setFlowCodeField(String flowCodeField) { this.flowCodeField = textOrDefault(flowCodeField, "taskFlowCode"); }
    public String getInputField() { return inputField; }
    public void setInputField(String inputField) { this.inputField = textOrDefault(inputField, "inputs"); }
    public String getAnswerPath() { return answerPath; }
    public void setAnswerPath(String answerPath) { this.answerPath = textOrDefault(answerPath, "answer"); }

    private static int parseInt(String value, int fallback) {
        try {
            return hasText(value) ? Integer.parseInt(value.trim()) : fallback;
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static String value(String propertyName, String fallback) {
        String configured = System.getProperty(propertyName);
        if (!hasText(configured)) {
            configured = System.getenv(propertyName.toUpperCase().replace('.', '_'));
        }
        return hasText(configured) ? configured : fallback;
    }

    private static String textOrDefault(String value, String fallback) {
        return hasText(value) ? value.trim() : fallback;
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
