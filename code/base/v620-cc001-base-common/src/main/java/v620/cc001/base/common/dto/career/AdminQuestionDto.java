package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AdminQuestionDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String questionId;
    private String position;
    private String difficulty;
    private String content;
    private String summary;
    private String answer;
    private Integer likes;
    private Integer drawCount;
    private String status;
    private String source;
    private String reviewStatus;
    private String contributorHash;
    private LocalDateTime createdAt;
    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }
    public Integer getDrawCount() { return drawCount; }
    public void setDrawCount(Integer drawCount) { this.drawCount = drawCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }
    public String getContributorHash() { return contributorHash; }
    public void setContributorHash(String contributorHash) { this.contributorHash = contributorHash; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
