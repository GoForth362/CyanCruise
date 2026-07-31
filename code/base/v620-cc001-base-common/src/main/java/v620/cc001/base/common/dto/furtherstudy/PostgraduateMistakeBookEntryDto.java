package v620.cc001.base.common.dto.furtherstudy;

import java.io.Serializable;
import java.time.LocalDateTime;

/** A persisted postgraduate wrong-question record owned by one user. */
public class PostgraduateMistakeBookEntryDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String mistakeId;
    private String userId;
    private String subject;
    private String questionText;
    private String wrongAnswer;
    private String resultJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public String getMistakeId() { return mistakeId; }
    public void setMistakeId(String mistakeId) { this.mistakeId = mistakeId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getWrongAnswer() { return wrongAnswer; }
    public void setWrongAnswer(String wrongAnswer) { this.wrongAnswer = wrongAnswer; }
    public String getResultJson() { return resultJson; }
    public void setResultJson(String resultJson) { this.resultJson = resultJson; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
