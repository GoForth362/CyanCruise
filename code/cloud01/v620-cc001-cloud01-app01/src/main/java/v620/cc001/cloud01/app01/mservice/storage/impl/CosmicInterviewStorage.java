package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.InterviewMessageDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;
import v620.cc001.cloud01.app01.mservice.datamodel.CyanCruiseDatamodelObjects;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelRecord;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicRecordFilter;
import v620.cc001.cloud01.app01.mservice.datamodel.DatamodelFieldMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CosmicInterviewStorage implements InterviewStorage {

    private final CosmicDatamodelGateway gateway;

    public CosmicInterviewStorage(CosmicDatamodelGateway gateway) {
        this.gateway = gateway;
    }

    public InterviewSessionDto saveInterview(InterviewSessionDto interview) {
        CosmicDatamodelRecord row = interview.getInterviewId() == null ? null
                : gateway.load(CyanCruiseDatamodelObjects.INTERVIEW, interview.getInterviewId());
        if (row == null) {
            row = new CosmicDatamodelRecord(CyanCruiseDatamodelObjects.INTERVIEW)
                    .set(CyanCruiseDatamodelObjects.CREATED_AT, interview.getStartedAt() == null ? LocalDateTime.now() : interview.getStartedAt());
        }
        row.set(CyanCruiseDatamodelObjects.USER_ID, interview.getUserId())
                .set("resume_id", interview.getResumeId())
                .set("position_name", interview.getPositionName())
                .set("difficulty", interview.getDifficulty())
                .set("status", interview.getStatus())
                .set("mode", interview.getMode())
                .set("final_score", interview.getFinalScore())
                .set("report_json", interview.getReport())
                .set("started_at", interview.getStartedAt())
                .set("ended_at", interview.getEndedAt())
                .set("duration_seconds", interview.getDurationSeconds())
                .set(CyanCruiseDatamodelObjects.UPDATED_AT, LocalDateTime.now());
        return toInterview(gateway.save(row));
    }

    public InterviewSessionDto loadInterview(Long interviewId) {
        CosmicDatamodelRecord row = gateway.load(CyanCruiseDatamodelObjects.INTERVIEW, interviewId);
        if (row == null) {
            throw new IllegalArgumentException("interview not found: " + interviewId);
        }
        return toInterview(row);
    }

    public List<InterviewSessionDto> listByUser(final String userId) {
        List<CosmicDatamodelRecord> rows = gateway.list(CyanCruiseDatamodelObjects.INTERVIEW, new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord record) {
                return DatamodelFieldMapper.same(userId, record.get(CyanCruiseDatamodelObjects.USER_ID));
            }
        }, DatamodelFieldMapper.dateTimeDesc("started_at"));
        List<InterviewSessionDto> result = new ArrayList<InterviewSessionDto>();
        for (CosmicDatamodelRecord row : rows) {
            result.add(toInterview(row));
        }
        return result;
    }

    public void deleteInterview(Long interviewId) {
        gateway.delete(CyanCruiseDatamodelObjects.INTERVIEW, interviewId);
        deleteMessages(interviewId);
    }

    public InterviewMessageDto saveMessage(InterviewMessageDto message) {
        CosmicDatamodelRecord row = message.getMessageId() == null ? null
                : gateway.load(CyanCruiseDatamodelObjects.INTERVIEW_MESSAGE, message.getMessageId());
        if (row == null) {
            row = new CosmicDatamodelRecord(CyanCruiseDatamodelObjects.INTERVIEW_MESSAGE);
        }
        row.set("interview_id", message.getInterviewId())
                .set("role", message.getRole())
                .set("content", message.getContent())
                .set(CyanCruiseDatamodelObjects.CREATED_AT, message.getCreatedAt() == null ? LocalDateTime.now() : message.getCreatedAt());
        return toMessage(gateway.save(row));
    }

    public List<InterviewMessageDto> listMessages(final Long interviewId) {
        List<CosmicDatamodelRecord> rows = gateway.list(CyanCruiseDatamodelObjects.INTERVIEW_MESSAGE, new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord record) {
                return DatamodelFieldMapper.same(interviewId, record.get("interview_id"));
            }
        }, createdAtAsc());
        List<InterviewMessageDto> result = new ArrayList<InterviewMessageDto>();
        for (CosmicDatamodelRecord row : rows) {
            result.add(toMessage(row));
        }
        return result;
    }

    public void deleteMessages(final Long interviewId) {
        gateway.deleteWhere(CyanCruiseDatamodelObjects.INTERVIEW_MESSAGE, new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord record) {
                return DatamodelFieldMapper.same(interviewId, record.get("interview_id"));
            }
        });
    }

    private InterviewSessionDto toInterview(CosmicDatamodelRecord row) {
        InterviewSessionDto dto = new InterviewSessionDto();
        dto.setInterviewId(DatamodelFieldMapper.asLong(row.get(CyanCruiseDatamodelObjects.ID)));
        dto.setUserId(DatamodelFieldMapper.asString(row.get(CyanCruiseDatamodelObjects.USER_ID)));
        dto.setResumeId(DatamodelFieldMapper.asLong(row.get("resume_id")));
        dto.setPositionName(DatamodelFieldMapper.asString(row.get("position_name")));
        dto.setDifficulty(DatamodelFieldMapper.asString(row.get("difficulty")));
        dto.setStatus(DatamodelFieldMapper.asString(row.get("status")));
        dto.setMode(DatamodelFieldMapper.asString(row.get("mode")));
        dto.setFinalScore(DatamodelFieldMapper.asInteger(row.get("final_score")));
        dto.setReport((v620.cc001.base.common.dto.career.InterviewReportDto) row.get("report_json"));
        dto.setStartedAt(DatamodelFieldMapper.asDateTime(row.get("started_at")));
        dto.setEndedAt(DatamodelFieldMapper.asDateTime(row.get("ended_at")));
        dto.setDurationSeconds(DatamodelFieldMapper.asInteger(row.get("duration_seconds")));
        return dto;
    }

    private InterviewMessageDto toMessage(CosmicDatamodelRecord row) {
        InterviewMessageDto dto = new InterviewMessageDto();
        dto.setMessageId(DatamodelFieldMapper.asLong(row.get(CyanCruiseDatamodelObjects.ID)));
        dto.setInterviewId(DatamodelFieldMapper.asLong(row.get("interview_id")));
        dto.setRole(DatamodelFieldMapper.asString(row.get("role")));
        dto.setContent(DatamodelFieldMapper.asString(row.get("content")));
        dto.setCreatedAt(DatamodelFieldMapper.asDateTime(row.get(CyanCruiseDatamodelObjects.CREATED_AT)));
        return dto;
    }

    private Comparator<CosmicDatamodelRecord> createdAtAsc() {
        return new Comparator<CosmicDatamodelRecord>() {
            public int compare(CosmicDatamodelRecord left, CosmicDatamodelRecord right) {
                LocalDateTime leftValue = DatamodelFieldMapper.asDateTime(left.get(CyanCruiseDatamodelObjects.CREATED_AT));
                LocalDateTime rightValue = DatamodelFieldMapper.asDateTime(right.get(CyanCruiseDatamodelObjects.CREATED_AT));
                if (leftValue == null && rightValue == null) {
                    return 0;
                }
                if (leftValue == null) {
                    return -1;
                }
                if (rightValue == null) {
                    return 1;
                }
                return leftValue.compareTo(rightValue);
            }
        };
    }
}
