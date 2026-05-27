package v620.cc001.base.common.dto.career;

import java.io.Serializable;

/**
 * Resume keyword signal extracted from resume text or metadata.
 */
public class ResumeKeywordDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String category;
    private String label;
    private Integer weight;
    private String evidence;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }
}
