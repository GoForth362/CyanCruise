package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Cached structured interview report summary.
 */
public class InterviewReportDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long interviewId;
    private String positionName;
    private String difficulty;
    private String mode;
    private Integer durationSeconds;
    private Integer overallScore;
    private Integer totalQuestions;
    private InterviewRadarScoreDto radarScore;
    private List<InterviewAdviceItemDto> strengths = new ArrayList<InterviewAdviceItemDto>();
    private List<InterviewAdviceItemDto> improvements = new ArrayList<InterviewAdviceItemDto>();
    private String textSummary;

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Integer getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public InterviewRadarScoreDto getRadarScore() {
        return radarScore;
    }

    public void setRadarScore(InterviewRadarScoreDto radarScore) {
        this.radarScore = radarScore;
    }

    public List<InterviewAdviceItemDto> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<InterviewAdviceItemDto> strengths) {
        this.strengths = strengths;
    }

    public List<InterviewAdviceItemDto> getImprovements() {
        return improvements;
    }

    public void setImprovements(List<InterviewAdviceItemDto> improvements) {
        this.improvements = improvements;
    }

    public String getTextSummary() {
        return textSummary;
    }

    public void setTextSummary(String textSummary) {
        this.textSummary = textSummary;
    }
}
