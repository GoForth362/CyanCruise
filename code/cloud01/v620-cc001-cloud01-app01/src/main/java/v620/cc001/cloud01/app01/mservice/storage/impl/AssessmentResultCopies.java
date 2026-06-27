package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.AssessmentAnswerSnapshot;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;

final class AssessmentResultCopies {

    private AssessmentResultCopies() {
    }

    static AssessmentScoreResult copy(AssessmentScoreResult source) {
        if (source == null) {
            return null;
        }
        AssessmentScoreResult copy = new AssessmentScoreResult();
        copy.setRecordId(source.getRecordId());
        copy.setUserId(source.getUserId());
        copy.setScaleId(source.getScaleId());
        copy.setScaleTitle(source.getScaleTitle());
        copy.setStatus(source.getStatus());
        copy.setResultSummary(source.getResultSummary());
        copy.setCreatedAt(source.getCreatedAt());
        copy.setDimensionCounts(source.getDimensionCounts() == null
                ? new LinkedHashMap<String, Integer>()
                : new LinkedHashMap<String, Integer>(source.getDimensionCounts()));
        copy.setAnswers(source.getAnswers() == null
                ? new ArrayList<AssessmentAnswerSnapshot>()
                : new ArrayList<AssessmentAnswerSnapshot>(source.getAnswers()));
        copy.setSuggestedRoles(source.getSuggestedRoles() == null
                ? new ArrayList<String>()
                : new ArrayList<String>(source.getSuggestedRoles()));
        return copy;
    }
}
