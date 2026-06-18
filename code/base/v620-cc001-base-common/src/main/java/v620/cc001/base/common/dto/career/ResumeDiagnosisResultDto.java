package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Structured resume diagnosis result.
 */
public class ResumeDiagnosisResultDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long resumeId;
    private Integer overallScore;
    private List<String> strengths = new ArrayList<String>();
    private List<String> weaknesses = new ArrayList<String>();
    private List<String> suggestions = new ArrayList<String>();
    private List<ResumeRevisionSuggestionDto> revisionSuggestions = new ArrayList<ResumeRevisionSuggestionDto>();
    private ResumeRevisionPlanDto revisionPlan;
    private List<String> contextSources = new ArrayList<String>();
    private String fallbackStatus;
    private String rawAnalysis;

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public Integer getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths == null ? new ArrayList<String>() : strengths;
    }

    public List<String> getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(List<String> weaknesses) {
        this.weaknesses = weaknesses == null ? new ArrayList<String>() : weaknesses;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions == null ? new ArrayList<String>() : suggestions;
    }

    public List<ResumeRevisionSuggestionDto> getRevisionSuggestions() {
        return revisionSuggestions;
    }

    public void setRevisionSuggestions(List<ResumeRevisionSuggestionDto> revisionSuggestions) {
        this.revisionSuggestions = revisionSuggestions == null ? new ArrayList<ResumeRevisionSuggestionDto>() : revisionSuggestions;
    }

    public ResumeRevisionPlanDto getRevisionPlan() {
        return revisionPlan;
    }

    public void setRevisionPlan(ResumeRevisionPlanDto revisionPlan) {
        this.revisionPlan = revisionPlan;
    }

    public List<String> getContextSources() {
        return contextSources;
    }

    public void setContextSources(List<String> contextSources) {
        this.contextSources = contextSources == null ? new ArrayList<String>() : contextSources;
    }

    public String getFallbackStatus() {
        return fallbackStatus;
    }

    public void setFallbackStatus(String fallbackStatus) {
        this.fallbackStatus = fallbackStatus;
    }

    public String getRawAnalysis() {
        return rawAnalysis;
    }

    public void setRawAnalysis(String rawAnalysis) {
        this.rawAnalysis = rawAnalysis;
    }
}
