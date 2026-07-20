package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cached structured interview report summary.
 */
public class InterviewReportDto implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ANALYSIS_SOURCE_AI_AGENT = "AI_AGENT";
    public static final String ANALYSIS_SOURCE_BASIC_RULES = "BASIC_RULES";

    private Long interviewId;
    private String positionName;
    private String difficulty;
    private String mode;
    private Integer durationSeconds;
    private Integer overallScore;
    private Integer totalQuestions;
    private String analysisSource;
    private InterviewRadarScoreDto radarScore;
    private Map<String, String> scoreReasons = new LinkedHashMap<String, String>();
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

    public String getAnalysisSource() {
        return analysisSource;
    }

    public void setAnalysisSource(String analysisSource) {
        this.analysisSource = analysisSource;
    }

    public InterviewRadarScoreDto getRadarScore() {
        return radarScore;
    }

    public void setRadarScore(InterviewRadarScoreDto radarScore) {
        this.radarScore = radarScore;
    }

    public Map<String, String> getScoreReasons() {
        return scoreReasons;
    }

    public void setScoreReasons(Map<String, String> scoreReasons) {
        this.scoreReasons = scoreReasons;
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
