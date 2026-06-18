package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Actionable resume revision suggestion derived from diagnosis.
 */
public class ResumeRevisionSuggestionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String suggestionId;
    private String issueType;
    private String priority;
    private String resumeSection;
    private String problem;
    private String action;
    private String rewriteExample;
    private String evidence;
    private List<String> targetKeywords = new ArrayList<String>();
    private String status;
    private String contextSource;
    private String updatedAt;

    public String getSuggestionId() {
        return suggestionId;
    }

    public void setSuggestionId(String suggestionId) {
        this.suggestionId = suggestionId;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getResumeSection() {
        return resumeSection;
    }

    public void setResumeSection(String resumeSection) {
        this.resumeSection = resumeSection;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRewriteExample() {
        return rewriteExample;
    }

    public void setRewriteExample(String rewriteExample) {
        this.rewriteExample = rewriteExample;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public List<String> getTargetKeywords() {
        return targetKeywords;
    }

    public void setTargetKeywords(List<String> targetKeywords) {
        this.targetKeywords = targetKeywords == null ? new ArrayList<String>() : targetKeywords;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContextSource() {
        return contextSource;
    }

    public void setContextSource(String contextSource) {
        this.contextSource = contextSource;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
