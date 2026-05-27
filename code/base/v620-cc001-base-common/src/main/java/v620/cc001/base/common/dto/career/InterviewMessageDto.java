package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Ordered message in a mock interview conversation.
 */
public class InterviewMessageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long messageId;
    private Long interviewId;
    private String role;
    private String content;
    private LocalDateTime createdAt;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
