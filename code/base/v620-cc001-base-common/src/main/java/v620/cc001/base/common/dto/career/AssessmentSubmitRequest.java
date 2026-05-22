package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Assessment submit payload. Answers are questionId -> optionId.
 */
public class AssessmentSubmitRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long scaleId;
    private Map<Long, Long> answers = new LinkedHashMap<Long, Long>();

    public Long getScaleId() {
        return scaleId;
    }

    public void setScaleId(Long scaleId) {
        this.scaleId = scaleId;
    }

    public Map<Long, Long> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, Long> answers) {
        this.answers = answers;
    }
}
