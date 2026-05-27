package v620.cc001.base.common.dto.career;

/**
 * Constants for the migrated assistant chat capability.
 */
public final class AssistantChatConstants {

    public static final String PERSONA_MENTOR = "MENTOR";
    public static final String PERSONA_CHALLENGER = "CHALLENGER";
    public static final String PERSONA_INTERVIEWER = "INTERVIEWER";

    public static final String ROLE_SYSTEM = "system";
    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";

    public static final String DEFAULT_TITLE = "新的求职对话";
    public static final String DEFAULT_MODEL_NAME = "UNCONFIGURED";

    public static final int DEFAULT_PROMPT_TOKENS = 0;
    public static final int DEFAULT_COMPLETION_TOKENS = 0;
    public static final int DEFAULT_TOTAL_TOKENS = 0;
    public static final long DEFAULT_COST_MICROS = 0L;

    public static final String ERROR_CHAT_GENERATOR_UNAVAILABLE = "assistant chat generator is not configured";

    private AssistantChatConstants() {
    }
}
