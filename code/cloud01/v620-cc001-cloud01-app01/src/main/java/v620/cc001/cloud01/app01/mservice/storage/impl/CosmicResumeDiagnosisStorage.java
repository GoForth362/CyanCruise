package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeKeywordStatusDto;
import v620.cc001.cloud01.app01.mservice.datamodel.CyanCruiseDatamodelObjects;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelRecord;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicRecordFilter;
import v620.cc001.cloud01.app01.mservice.datamodel.DatamodelFieldMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CosmicResumeDiagnosisStorage implements ResumeDiagnosisStorage {

    private final CosmicDatamodelGateway gateway;

    public CosmicResumeDiagnosisStorage(CosmicDatamodelGateway gateway) {
        this.gateway = gateway;
    }

    public ResumeDiagnosisResultDto saveDiagnosis(ResumeDiagnosisResultDto result) {
        if (result == null || result.getResumeId() == null || result.getUserId() == null) {
            return result;
        }
        LocalDateTime now = result.getDiagnosedAt() == null ? LocalDateTime.now() : result.getDiagnosedAt();
        CosmicDatamodelRecord record = new CosmicDatamodelRecord(CyanCruiseDatamodelObjects.RESUME_DIAGNOSIS);
        CosmicDatamodelRecord saved = gateway.save(record.set(CyanCruiseDatamodelObjects.USER_ID, result.getUserId())
                .set("resume_id", result.getResumeId())
                .set("score", result.getOverallScore())
                .set("diagnosis_json", result)
                .set(CyanCruiseDatamodelObjects.CREATED_AT, now)
                .set(CyanCruiseDatamodelObjects.UPDATED_AT, now));
        result.setDiagnosisId(DatamodelFieldMapper.asLong(saved.get(CyanCruiseDatamodelObjects.ID)));
        return result;
    }

    public ResumeDiagnosisResultDto loadDiagnosis(Long resumeId) {
        CosmicDatamodelRecord record = latest(resumeId);
        return record == null ? null : toDiagnosis(record);
    }

    public List<ResumeDiagnosisResultDto> listDiagnoses(final String userId, final Long resumeId) {
        List<CosmicDatamodelRecord> rows = gateway.list(CyanCruiseDatamodelObjects.RESUME_DIAGNOSIS,
                new CosmicRecordFilter() {
                    public boolean matches(CosmicDatamodelRecord record) {
                        return DatamodelFieldMapper.same(userId, record.get(CyanCruiseDatamodelObjects.USER_ID))
                                && DatamodelFieldMapper.same(resumeId, record.get("resume_id"));
                    }
                }, DatamodelFieldMapper.dateTimeDesc(CyanCruiseDatamodelObjects.CREATED_AT));
        List<ResumeDiagnosisResultDto> result = new ArrayList<ResumeDiagnosisResultDto>();
        for (CosmicDatamodelRecord row : rows) {
            result.add(toDiagnosis(row));
        }
        return result;
    }

    public boolean deleteDiagnosis(String userId, Long diagnosisId) {
        CosmicDatamodelRecord record = gateway.load(CyanCruiseDatamodelObjects.RESUME_DIAGNOSIS, diagnosisId);
        if (record == null || !DatamodelFieldMapper.same(userId, record.get(CyanCruiseDatamodelObjects.USER_ID))) {
            return false;
        }
        gateway.delete(CyanCruiseDatamodelObjects.RESUME_DIAGNOSIS, diagnosisId);
        return true;
    }

    public ResumeKeywordStatusDto saveKeywordStatus(ResumeKeywordStatusDto status) {
        CosmicDatamodelRecord record = latest(status.getResumeId());
        if (record == null) {
            record = new CosmicDatamodelRecord(CyanCruiseDatamodelObjects.RESUME_DIAGNOSIS)
                    .set("resume_id", status.getResumeId())
                    .set(CyanCruiseDatamodelObjects.CREATED_AT, LocalDateTime.now());
        }
        gateway.save(record.set("resume_id", status.getResumeId())
                .set("keyword_status_json", status)
                .set(CyanCruiseDatamodelObjects.UPDATED_AT, LocalDateTime.now()));
        return status;
    }

    public ResumeKeywordStatusDto loadKeywordStatus(Long resumeId) {
        CosmicDatamodelRecord record = latest(resumeId);
        return record == null ? null : (ResumeKeywordStatusDto) record.get("keyword_status_json");
    }

    private CosmicDatamodelRecord latest(final Long resumeId) {
        List<CosmicDatamodelRecord> rows = gateway.list(CyanCruiseDatamodelObjects.RESUME_DIAGNOSIS, new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord record) {
                return DatamodelFieldMapper.same(resumeId, record.get("resume_id"));
            }
        }, DatamodelFieldMapper.dateTimeDesc(CyanCruiseDatamodelObjects.CREATED_AT));
        return rows.isEmpty() ? null : rows.get(0);
    }

    private ResumeDiagnosisResultDto toDiagnosis(CosmicDatamodelRecord record) {
        ResumeDiagnosisResultDto result = (ResumeDiagnosisResultDto) record.get("diagnosis_json");
        if (result == null) {
            return null;
        }
        result.setDiagnosisId(DatamodelFieldMapper.asLong(record.get(CyanCruiseDatamodelObjects.ID)));
        result.setResumeId(DatamodelFieldMapper.asLong(record.get("resume_id")));
        result.setUserId(DatamodelFieldMapper.asString(record.get(CyanCruiseDatamodelObjects.USER_ID)));
        result.setDiagnosedAt(DatamodelFieldMapper.asDateTime(record.get(CyanCruiseDatamodelObjects.CREATED_AT)));
        return result;
    }
}
