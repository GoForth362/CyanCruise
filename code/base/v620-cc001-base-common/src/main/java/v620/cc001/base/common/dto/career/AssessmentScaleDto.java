package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * JDK 8 compatible assessment scale contract migrated from IPD.
 */
public class AssessmentScaleDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long scaleId;
    private String title;
    private String description;
    private String version;
    private Integer questionCount;
    private List<AssessmentQuestionDto> questions = new ArrayList<AssessmentQuestionDto>();

    public Long getScaleId() {
        return scaleId;
    }

    public void setScaleId(Long scaleId) {
        this.scaleId = scaleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public List<AssessmentQuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<AssessmentQuestionDto> questions) {
        this.questions = questions;
    }
}
