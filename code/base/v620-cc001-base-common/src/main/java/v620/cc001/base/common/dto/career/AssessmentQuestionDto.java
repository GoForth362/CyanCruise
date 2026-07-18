package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Assessment question with options.
 */
public class AssessmentQuestionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long questionId;
    private Long scaleId;
    private String questionText;
    private String questionType = "SINGLE";
    private String dimensionCode;
    private Integer sortOrder;
    private boolean published;
    private List<AssessmentOptionDto> options = new ArrayList<AssessmentOptionDto>();

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getScaleId() {
        return scaleId;
    }

    public void setScaleId(Long scaleId) {
        this.scaleId = scaleId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getDimensionCode() {
        return dimensionCode;
    }

    public void setDimensionCode(String dimensionCode) {
        this.dimensionCode = dimensionCode;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public List<AssessmentOptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<AssessmentOptionDto> options) {
        this.options = options;
    }
}
