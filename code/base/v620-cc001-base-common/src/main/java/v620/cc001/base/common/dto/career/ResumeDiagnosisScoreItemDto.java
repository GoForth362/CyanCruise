package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class ResumeDiagnosisScoreItemDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private Integer score = Integer.valueOf(0);
    private Integer maxScore = Integer.valueOf(0);
    private String reason;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Integer getMaxScore() { return maxScore; }
    public void setMaxScore(Integer maxScore) { this.maxScore = maxScore; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
