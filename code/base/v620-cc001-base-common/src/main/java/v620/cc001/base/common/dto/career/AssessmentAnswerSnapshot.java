package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Snapshot of one submitted answer at scoring time.
 */
public class AssessmentAnswerSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long questionId;
    private Long optionId;
    private String questionText;
    private String optionText;
    private String dimensionCode;
    private BigDecimal scoreSnapshot = BigDecimal.ZERO;
    private boolean validOption;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }

    public String getDimensionCode() {
        return dimensionCode;
    }

    public void setDimensionCode(String dimensionCode) {
        this.dimensionCode = dimensionCode;
    }

    public BigDecimal getScoreSnapshot() {
        return scoreSnapshot;
    }

    public void setScoreSnapshot(BigDecimal scoreSnapshot) {
        this.scoreSnapshot = scoreSnapshot;
    }

    public boolean isValidOption() {
        return validOption;
    }

    public void setValidOption(boolean validOption) {
        this.validOption = validOption;
    }
}
