package v620.cc001.cloud01.app01.mservice.furtherstudy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyConstants;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyMaterialDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyMaterialSaveRequest;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordDetailDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordQueryRequest;

import java.time.LocalDate;

final class FurtherStudyRecordSupport {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private FurtherStudyRecordSupport() {
    }

    static FurtherStudyRecordDetailDto record(String track, String recordType, String title,
                                              String targetSchool, String targetMajor, String targetRegion,
                                              String dateText, Object request, Object result) {
        FurtherStudyRecordDetailDto record = new FurtherStudyRecordDetailDto();
        record.setTrack(track);
        record.setRecordType(recordType);
        record.setTitle(defaultText(title, FurtherStudyConstants.trackLabel(track) + " record"));
        record.setStatus(FurtherStudyConstants.STATUS_IN_PROGRESS);
        record.setTargetSchool(trimToNull(targetSchool));
        record.setTargetMajor(trimToNull(targetMajor));
        record.setTargetRegion(trimToNull(targetRegion));
        record.setExamOrDeadlineDate(parseDate(dateText));
        record.setRequestJson(toJson(request));
        record.setResultJson(toJson(result));
        return record;
    }

    static FurtherStudyMaterialDto material(FurtherStudyMaterialSaveRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Further-study material request is required.");
        }
        FurtherStudyMaterialDto material = new FurtherStudyMaterialDto();
        material.setMaterialId(request.getMaterialId());
        material.setTrack(request.getTrack());
        material.setRecordId(request.getRecordId());
        material.setMaterialType(request.getMaterialType());
        material.setTitle(request.getTitle());
        material.setStatus(request.getStatus());
        material.setFileKey(request.getFileKey());
        material.setContentJson(request.getContentJson());
        return material;
    }

    static FurtherStudyRecordQueryRequest trackQuery(String track, FurtherStudyRecordQueryRequest query) {
        FurtherStudyRecordQueryRequest out = query == null ? new FurtherStudyRecordQueryRequest() : query;
        out.setTrack(track);
        return out;
    }

    static String toJson(Object value) {
        if (value == null) {
            return "{}";
        }
        try {
            return MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize further-study record content.", e);
        }
    }

    static String defaultText(String value, String fallback) {
        String safe = trimToNull(value);
        return safe == null ? fallback : safe;
    }

    static String trimToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private static LocalDate parseDate(String value) {
        String safe = trimToNull(value);
        if (safe == null) {
            return null;
        }
        try {
            return LocalDate.parse(safe);
        } catch (Exception ignored) {
            return null;
        }
    }
}
