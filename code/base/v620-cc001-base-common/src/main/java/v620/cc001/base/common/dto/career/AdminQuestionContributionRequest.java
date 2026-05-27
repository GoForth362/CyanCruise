package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class AdminQuestionContributionRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userId;
    private String position;
    private String difficulty;
    private String content;
    private String summary;
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}
