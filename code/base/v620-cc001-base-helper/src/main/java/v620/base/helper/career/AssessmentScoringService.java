package v620.base.helper.career;

import v620.cc001.base.common.dto.career.AssessmentAnswerSnapshot;
import v620.cc001.base.common.dto.career.AssessmentOptionDto;
import v620.cc001.base.common.dto.career.AssessmentQuestionDto;
import v620.cc001.base.common.dto.career.AssessmentScaleDto;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;
import v620.cc001.base.common.dto.career.AssessmentSubmitRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pure Java scorer for IPD-style career assessments.
 */
public class AssessmentScoringService {

    public AssessmentScoreResult score(AssessmentScaleDto scale, AssessmentSubmitRequest request) {
        AssessmentScaleDto safeScale = scale == null ? new AssessmentScaleDto() : scale;
        AssessmentSubmitRequest safeRequest = request == null ? new AssessmentSubmitRequest() : request;
        Map<Long, Long> submitted = safeRequest.getAnswers() == null
                ? new LinkedHashMap<Long, Long>()
                : safeRequest.getAnswers();
        Map<Long, AssessmentQuestionDto> questions = questionsById(safeScale);

        Map<String, Integer> counts = new LinkedHashMap<String, Integer>();
        List<AssessmentAnswerSnapshot> answerSnapshots = new ArrayList<AssessmentAnswerSnapshot>();

        for (Map.Entry<Long, Long> entry : submitted.entrySet()) {
            Long questionId = entry.getKey();
            Long optionId = entry.getValue();
            AssessmentOptionDto option = findOption(questions.get(questionId), optionId);

            AssessmentAnswerSnapshot snapshot = new AssessmentAnswerSnapshot();
            snapshot.setQuestionId(questionId);
            snapshot.setOptionId(optionId);

            if (option != null) {
                snapshot.setValidOption(true);
                snapshot.setDimensionCode(option.getDimensionCode());
                snapshot.setScoreSnapshot(option.getScoreValue() == null ? BigDecimal.ZERO : option.getScoreValue());
                if (hasText(option.getDimensionCode())) {
                    increment(counts, option.getDimensionCode().trim());
                }
            } else {
                snapshot.setValidOption(false);
                snapshot.setScoreSnapshot(BigDecimal.ZERO);
            }
            answerSnapshots.add(snapshot);
        }

        AssessmentScoreResult result = new AssessmentScoreResult();
        result.setScaleId(safeScale.getScaleId());
        result.setScaleTitle(safeScale.getTitle());
        result.setStatus("COMPLETED");
        result.setDimensionCounts(counts);
        result.setAnswers(answerSnapshots);
        result.setResultSummary(generatePortrait(safeScale.getTitle(), counts));
        return result;
    }

    private Map<Long, AssessmentQuestionDto> questionsById(AssessmentScaleDto scale) {
        Map<Long, AssessmentQuestionDto> result = new LinkedHashMap<Long, AssessmentQuestionDto>();
        if (scale.getQuestions() == null) {
            return result;
        }
        for (AssessmentQuestionDto question : scale.getQuestions()) {
            if (question != null && question.getQuestionId() != null) {
                result.put(question.getQuestionId(), question);
            }
        }
        return result;
    }

    private AssessmentOptionDto findOption(AssessmentQuestionDto question, Long optionId) {
        if (question == null || optionId == null || question.getOptions() == null) {
            return null;
        }
        for (AssessmentOptionDto option : question.getOptions()) {
            if (option != null && optionId.equals(option.getOptionId())) {
                return option;
            }
        }
        return null;
    }

    private String generatePortrait(String scaleTitle, Map<String, Integer> counts) {
        if (counts == null || counts.isEmpty()) {
            return "N/A";
        }
        String title = scaleTitle == null ? "" : scaleTitle.toUpperCase();
        if (title.contains("MBTI")) {
            StringBuilder builder = new StringBuilder();
            builder.append(get(counts, "E") >= get(counts, "I") ? "E" : "I");
            builder.append(get(counts, "S") >= get(counts, "N") ? "S" : "N");
            builder.append(get(counts, "T") >= get(counts, "F") ? "T" : "F");
            builder.append(get(counts, "J") >= get(counts, "P") ? "J" : "P");
            return builder.toString();
        }
        return topThree(counts);
    }

    private String topThree(Map<String, Integer> counts) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(counts.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> left, Map.Entry<String, Integer> right) {
                int byCount = right.getValue().compareTo(left.getValue());
                if (byCount != 0) {
                    return byCount;
                }
                return left.getKey().compareTo(right.getKey());
            }
        });
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < entries.size() && i < 3; i++) {
            builder.append(entries.get(i).getKey());
        }
        return builder.toString();
    }

    private void increment(Map<String, Integer> counts, String key) {
        Integer current = counts.get(key);
        counts.put(key, Integer.valueOf(current == null ? 1 : current.intValue() + 1));
    }

    private int get(Map<String, Integer> counts, String key) {
        Integer value = counts.get(key);
        return value == null ? 0 : value.intValue();
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
