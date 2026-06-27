package v620.cc001.base.common.dto.career;

/** One score dimension for postgraduate recommendation background. */
public class RecommendationScoreItemDto {
    private String name;
    private Integer score;
    private Integer maxScore;
    private String comment;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Integer getMaxScore() { return maxScore; }
    public void setMaxScore(Integer maxScore) { this.maxScore = maxScore; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
