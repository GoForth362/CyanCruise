package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;
import v620.cc001.cloud01.app01.mservice.datamodel.CyanCruiseDatamodelObjects;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelRecord;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicRecordFilter;
import v620.cc001.cloud01.app01.mservice.datamodel.DatamodelFieldMapper;
import v620.cc001.cloud01.app01.mservice.datamodel.DatamodelOwnershipGuard;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CosmicAssessmentResultStorage implements AssessmentResultStorage {

    private final CosmicDatamodelGateway gateway;

    public CosmicAssessmentResultStorage(CosmicDatamodelGateway gateway) {
        this.gateway = gateway;
    }

    public Long saveResult(String userId, AssessmentScoreResult result) {
        CosmicDatamodelRecord record = new CosmicDatamodelRecord(CyanCruiseDatamodelObjects.ASSESSMENT_RECORD)
                .set(CyanCruiseDatamodelObjects.USER_ID, userId)
                .set("scale_id", result.getScaleId())
                .set("scale_title", result.getScaleTitle())
                .set("status", result.getStatus())
                .set("result_summary", result.getResultSummary())
                .set("result_json", result.getDimensionCounts())
                .set("answers_json", result.getAnswers())
                .set(CyanCruiseDatamodelObjects.CREATED_AT, LocalDateTime.now());
        return DatamodelFieldMapper.asLong(gateway.save(record).get(CyanCruiseDatamodelObjects.ID));
    }

    public AssessmentScoreResult loadResult(String userId, Long recordId) {
        CosmicDatamodelRecord record = gateway.load(CyanCruiseDatamodelObjects.ASSESSMENT_RECORD, recordId);
        DatamodelOwnershipGuard.requireUser(userId, record, CyanCruiseDatamodelObjects.ASSESSMENT_RECORD);
        return toDto(record);
    }

    public List<AssessmentScoreResult> listResults(final String userId) {
        List<CosmicDatamodelRecord> rows = gateway.list(CyanCruiseDatamodelObjects.ASSESSMENT_RECORD, new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord record) {
                return DatamodelFieldMapper.same(userId, record.get(CyanCruiseDatamodelObjects.USER_ID));
            }
        }, DatamodelFieldMapper.dateTimeDesc(CyanCruiseDatamodelObjects.CREATED_AT));
        List<AssessmentScoreResult> result = new ArrayList<AssessmentScoreResult>();
        for (CosmicDatamodelRecord row : rows) {
            result.add(toDto(row));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private AssessmentScoreResult toDto(CosmicDatamodelRecord record) {
        AssessmentScoreResult result = new AssessmentScoreResult();
        result.setScaleId(DatamodelFieldMapper.asLong(record.get("scale_id")));
        result.setScaleTitle(DatamodelFieldMapper.asString(record.get("scale_title")));
        result.setStatus(DatamodelFieldMapper.asString(record.get("status")));
        result.setResultSummary(DatamodelFieldMapper.asString(record.get("result_summary")));
        result.setDimensionCounts((java.util.Map<String, Integer>) record.get("result_json"));
        result.setAnswers((java.util.List<v620.cc001.base.common.dto.career.AssessmentAnswerSnapshot>) record.get("answers_json"));
        return result;
    }
}
