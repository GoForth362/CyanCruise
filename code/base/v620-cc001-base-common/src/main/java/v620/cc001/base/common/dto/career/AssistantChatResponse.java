package v620.cc001.base.common.dto.career;

import java.io.Serializable;

/**
 * Response for a single assistant chat turn.
 */
public class AssistantChatResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String reply;
    private String persona;
    private String modelName;
    private Integer promptTokens = Integer.valueOf(AssistantChatConstants.DEFAULT_PROMPT_TOKENS);
    private Integer completionTokens = Integer.valueOf(AssistantChatConstants.DEFAULT_COMPLETION_TOKENS);
    private Integer totalTokens = Integer.valueOf(AssistantChatConstants.DEFAULT_TOTAL_TOKENS);
    private Long costMicros = Long.valueOf(AssistantChatConstants.DEFAULT_COST_MICROS);

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Integer getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(Integer promptTokens) {
        this.promptTokens = promptTokens;
    }

    public Integer getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(Integer completionTokens) {
        this.completionTokens = completionTokens;
    }

    public Integer getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(Integer totalTokens) {
        this.totalTokens = totalTokens;
    }

    public Long getCostMicros() {
        return costMicros;
    }

    public void setCostMicros(Long costMicros) {
        this.costMicros = costMicros;
    }
}
