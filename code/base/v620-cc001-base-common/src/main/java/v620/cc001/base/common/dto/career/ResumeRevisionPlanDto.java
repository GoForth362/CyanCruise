package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Summary of the resume revision loop for the latest diagnosis.
 */
public class ResumeRevisionPlanDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer totalSuggestions = Integer.valueOf(0);
    private Integer highPrioritySuggestions = Integer.valueOf(0);
    private String overallPriority;
    private String nextAction;
    private String contextSummary;
    private List<String> contextSources = new ArrayList<String>();

    public Integer getTotalSuggestions() {
        return totalSuggestions;
    }

    public void setTotalSuggestions(Integer totalSuggestions) {
        this.totalSuggestions = totalSuggestions == null ? Integer.valueOf(0) : totalSuggestions;
    }

    public Integer getHighPrioritySuggestions() {
        return highPrioritySuggestions;
    }

    public void setHighPrioritySuggestions(Integer highPrioritySuggestions) {
        this.highPrioritySuggestions = highPrioritySuggestions == null ? Integer.valueOf(0) : highPrioritySuggestions;
    }

    public String getOverallPriority() {
        return overallPriority;
    }

    public void setOverallPriority(String overallPriority) {
        this.overallPriority = overallPriority;
    }

    public String getNextAction() {
        return nextAction;
    }

    public void setNextAction(String nextAction) {
        this.nextAction = nextAction;
    }

    public String getContextSummary() {
        return contextSummary;
    }

    public void setContextSummary(String contextSummary) {
        this.contextSummary = contextSummary;
    }

    public List<String> getContextSources() {
        return contextSources;
    }

    public void setContextSources(List<String> contextSources) {
        this.contextSources = contextSources == null ? new ArrayList<String>() : contextSources;
    }
}
