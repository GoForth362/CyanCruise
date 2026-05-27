package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Ordered message in an assistant chat session.
 */
public class AssistantChatMessageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long msgId;
    private Long sessionId;
    private String role;
    private String content;
    private Integer promptTokens = Integer.valueOf(AssistantChatConstants.DEFAULT_PROMPT_TOKENS);
    private Integer completionTokens = Integer.valueOf(AssistantChatConstants.DEFAULT_COMPLETION_TOKENS);
    private Integer totalTokens = Integer.valueOf(AssistantChatConstants.DEFAULT_TOTAL_TOKENS);
    private Long costMicros = Long.valueOf(AssistantChatConstants.DEFAULT_COST_MICROS);
    private LocalDateTime createdAt;

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
