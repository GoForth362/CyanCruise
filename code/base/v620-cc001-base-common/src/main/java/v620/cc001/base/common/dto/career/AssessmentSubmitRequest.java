package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Assessment submit payload. Answers are questionId -> optionId for legacy
 * single-choice clients; answerOptionIds supports multi-choice questions.
 */
public class AssessmentSubmitRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long scaleId;
    private String attemptId;
    private Map<Long, Long> answers = new LinkedHashMap<Long, Long>();
    private Map<Long, List<Long>> answerOptionIds = new LinkedHashMap<Long, List<Long>>();

    public Long getScaleId() {
        return scaleId;
    }

    public void setScaleId(Long scaleId) {
        this.scaleId = scaleId;
    }

    public String getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(String attemptId) {
        this.attemptId = attemptId;
    }

    public Map<Long, Long> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, Long> answers) {
        this.answers = answers;
    }

    public Map<Long, List<Long>> getAnswerOptionIds() {
        return answerOptionIds;
    }

    public void setAnswerOptionIds(Map<Long, List<Long>> answerOptionIds) {
        this.answerOptionIds = answerOptionIds;
    }

    public Map<Long, List<Long>> effectiveAnswerOptionIds() {
        Map<Long, List<Long>> result = new LinkedHashMap<Long, List<Long>>();
        if (answerOptionIds != null) {
            for (Map.Entry<Long, List<Long>> entry : answerOptionIds.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    continue;
                }
                List<Long> values = new ArrayList<Long>();
                for (Long optionId : entry.getValue()) {
                    if (optionId != null && !values.contains(optionId)) {
                        values.add(optionId);
                    }
                }
                if (!values.isEmpty()) {
                    result.put(entry.getKey(), values);
                }
            }
        }
        if (result.isEmpty() && answers != null) {
            for (Map.Entry<Long, Long> entry : answers.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    List<Long> values = new ArrayList<Long>();
                    values.add(entry.getValue());
                    result.put(entry.getKey(), values);
                }
            }
        }
        return result;
    }
}
