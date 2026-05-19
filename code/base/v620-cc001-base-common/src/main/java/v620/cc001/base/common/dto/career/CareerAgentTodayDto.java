package v620.cc001.base.common.dto.career;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of the career agent daily recommendation rule.
 */
public class CareerAgentTodayDto {

    private String stage;
    private String riskLevel;
    private String headline;
    private String headlineKey;
    private String reason;
    private String reasonKey;
    private String todayFocus;
    private String focusKey;
    private Integer progressPercent;
    private List<String> riskReasons = new ArrayList<String>();
    private List<String> riskReasonKeys = new ArrayList<String>();
    private List<Action> actions = new ArrayList<Action>();

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getHeadlineKey() {
        return headlineKey;
    }

    public void setHeadlineKey(String headlineKey) {
        this.headlineKey = headlineKey;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReasonKey() {
        return reasonKey;
    }

    public void setReasonKey(String reasonKey) {
        this.reasonKey = reasonKey;
    }

    public String getTodayFocus() {
        return todayFocus;
    }

    public void setTodayFocus(String todayFocus) {
        this.todayFocus = todayFocus;
    }

    public String getFocusKey() {
        return focusKey;
    }

    public void setFocusKey(String focusKey) {
        this.focusKey = focusKey;
    }

    public Integer getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(Integer progressPercent) {
        this.progressPercent = progressPercent;
    }

    public List<String> getRiskReasons() {
        return riskReasons;
    }

    public void setRiskReasons(List<String> riskReasons) {
        this.riskReasons = riskReasons;
    }

    public List<String> getRiskReasonKeys() {
        return riskReasonKeys;
    }

    public void setRiskReasonKeys(List<String> riskReasonKeys) {
        this.riskReasonKeys = riskReasonKeys;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public static class Action {
        private String label;
        private String labelKey;
        private String target;
        private String type;
        private String priority;
        private String source;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getLabelKey() {
            return labelKey;
        }

        public void setLabelKey(String labelKey) {
            this.labelKey = labelKey;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }
}
