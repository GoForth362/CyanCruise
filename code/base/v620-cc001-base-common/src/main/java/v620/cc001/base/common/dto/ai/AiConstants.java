package v620.cc001.base.common.dto.ai;

public final class AiConstants {

    public static final String ROLE_SYSTEM = "system";
    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";
    public static final String ROLE_TOOL = "tool";

    public static final String FINISH_STOP = "stop";
    public static final String FINISH_TOOL_CALLS = "tool_calls";
    public static final String FINISH_ERROR = "error";

    public static final String ERROR_UNAVAILABLE = "AI_UNAVAILABLE";
    public static final String ERROR_TIMEOUT = "AI_TIMEOUT";
    public static final String ERROR_PROVIDER = "AI_PROVIDER_ERROR";
    public static final String ERROR_INVALID_RESPONSE = "AI_INVALID_RESPONSE";
    public static final String ERROR_AUTHENTICATION = "AI_AUTHENTICATION_ERROR";
    public static final String ERROR_BAD_REQUEST = "AI_BAD_REQUEST";
    public static final String ERROR_NETWORK = "AI_NETWORK_ERROR";
    public static final String ERROR_INVALID_JSON = "AI_INVALID_JSON";
    public static final String ERROR_TOOL_NOT_FOUND = "AI_TOOL_NOT_FOUND";
    public static final String ERROR_TOOL_FAILED = "AI_TOOL_FAILED";
    public static final String ERROR_TOOL_LIMIT = "AI_TOOL_LIMIT";

    public static final String STREAM_TOKEN = "token";
    public static final String STREAM_DONE = "done";
    public static final String STREAM_ERROR = "error";

    public static final String DEFAULT_MODEL_NAME = "unconfigured";
    public static final String PROVIDER_UNAVAILABLE = "unavailable";
    public static final String PROVIDER_OPENAI_COMPATIBLE = "openai-compatible";
    public static final int DEFAULT_PROMPT_TOKENS = 0;
    public static final int DEFAULT_COMPLETION_TOKENS = 0;
    public static final int DEFAULT_TOTAL_TOKENS = 0;
    public static final long DEFAULT_COST_MICROS = 0L;

    private AiConstants() {
    }
}
