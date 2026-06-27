package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.ResumeRecordDto;
import v620.cc001.cloud01.app01.mservice.datamodel.CyanCruiseDatamodelObjects;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelRecord;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicRecordFilter;
import v620.cc001.cloud01.app01.mservice.datamodel.DatamodelFieldMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CosmicResumeStorage implements ResumeStorage {

    private final CosmicDatamodelGateway gateway;

    public CosmicResumeStorage(CosmicDatamodelGateway gateway) {
        this.gateway = gateway;
    }

    public ResumeRecordDto save(ResumeRecordDto record) {
        LocalDateTime now = LocalDateTime.now();
        CosmicDatamodelRecord row = record.getResumeId() == null ? null
                : gateway.load(CyanCruiseDatamodelObjects.RESUME, record.getResumeId());
        if (row == null) {
            row = new CosmicDatamodelRecord(CyanCruiseDatamodelObjects.RESUME)
                    .set(CyanCruiseDatamodelObjects.CREATED_AT, record.getCreatedAt() == null ? now : record.getCreatedAt());
        }
        LocalDateTime updatedAt = record.getUpdatedAt() == null ? now : record.getUpdatedAt();
        row.set(CyanCruiseDatamodelObjects.USER_ID, record.getUserId())
                .set("title", record.getTitle())
                .set("target_job", record.getTargetJob())
                .set("file_key", record.getFileKey())
                .set("version", record.getVersion())
                .set("status", record.getStatus())
                .set("parsed_content", record.getParsedContent())
                .set("diagnosis_score", record.getDiagnosisScore())
                .set(CyanCruiseDatamodelObjects.UPDATED_AT, updatedAt);
        CosmicDatamodelRecord saved = gateway.save(row);
        return toDto(saved);
    }

    public ResumeRecordDto load(Long resumeId) {
        CosmicDatamodelRecord row = gateway.load(CyanCruiseDatamodelObjects.RESUME, resumeId);
        if (row == null) {
            throw new IllegalArgumentException("resume not found: " + resumeId);
        }
        return toDto(row);
    }

    public List<ResumeRecordDto> listByUser(final String userId) {
        List<CosmicDatamodelRecord> rows = gateway.list(CyanCruiseDatamodelObjects.RESUME, new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord record) {
                return DatamodelFieldMapper.same(userId, record.get(CyanCruiseDatamodelObjects.USER_ID));
            }
        }, DatamodelFieldMapper.dateTimeDesc(CyanCruiseDatamodelObjects.UPDATED_AT));
        List<ResumeRecordDto> result = new ArrayList<ResumeRecordDto>();
        for (CosmicDatamodelRecord row : rows) {
            result.add(toDto(row));
        }
        return result;
    }

    public void delete(Long resumeId) {
        gateway.delete(CyanCruiseDatamodelObjects.RESUME, resumeId);
    }

    private ResumeRecordDto toDto(CosmicDatamodelRecord row) {
        ResumeRecordDto dto = new ResumeRecordDto();
        dto.setResumeId(DatamodelFieldMapper.asLong(row.get(CyanCruiseDatamodelObjects.ID)));
        dto.setUserId(DatamodelFieldMapper.asString(row.get(CyanCruiseDatamodelObjects.USER_ID)));
        dto.setTitle(DatamodelFieldMapper.asString(row.get("title")));
        dto.setTargetJob(DatamodelFieldMapper.asString(row.get("target_job")));
        dto.setFileKey(DatamodelFieldMapper.asString(row.get("file_key")));
        dto.setVersion(DatamodelFieldMapper.asString(row.get("version")));
        dto.setStatus(DatamodelFieldMapper.asString(row.get("status")));
        dto.setParsedContent(DatamodelFieldMapper.asString(row.get("parsed_content")));
        dto.setDiagnosisScore(DatamodelFieldMapper.asInteger(row.get("diagnosis_score")));
        dto.setCreatedAt(DatamodelFieldMapper.asDateTime(row.get(CyanCruiseDatamodelObjects.CREATED_AT)));
        dto.setUpdatedAt(DatamodelFieldMapper.asDateTime(row.get(CyanCruiseDatamodelObjects.UPDATED_AT)));
        return dto;
    }
}
