package v620.cc001.cloud01.app01.mservice.furtherstudy.impl;

import v620.cc001.cloud01.app01.mservice.furtherstudy.*;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyConstants;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyEventDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyMaterialDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordDetailDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordQueryRequest;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordStatusUpdateRequest;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordSummaryDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyTargetDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryFurtherStudyCompanionStorage implements FurtherStudyCompanionStorage {

    private final Map<String, FurtherStudyTargetDto> targets = new ConcurrentHashMap<String, FurtherStudyTargetDto>();
    private final Map<String, FurtherStudyRecordDetailDto> records = new ConcurrentHashMap<String, FurtherStudyRecordDetailDto>();
    private final Map<String, FurtherStudyMaterialDto> materials = new ConcurrentHashMap<String, FurtherStudyMaterialDto>();
    private final Map<String, FurtherStudyEventDto> events = new ConcurrentHashMap<String, FurtherStudyEventDto>();

    public FurtherStudyTargetDto saveTarget(String userId, FurtherStudyTargetDto target) {
        String safeUserId = requireUserId(userId);
        if (target == null) {
            throw new IllegalArgumentException("Further-study target is required.");
        }
        LocalDateTime now = LocalDateTime.now();
        if (!hasText(target.getTargetId())) {
            target.setTargetId(newId("fst"));
            target.setCreatedAt(now);
        } else if (target.getCreatedAt() == null) {
            FurtherStudyTargetDto existing = targets.get(target.getTargetId());
            target.setCreatedAt(existing == null ? now : existing.getCreatedAt());
        }
        target.setUserId(safeUserId);
        target.setStatus(defaultText(target.getStatus(), FurtherStudyConstants.STATUS_IN_PROGRESS));
        target.setUpdatedAt(now);
        targets.put(target.getTargetId(), target);
        return target;
    }

    public FurtherStudyRecordDetailDto saveRecord(String userId, FurtherStudyRecordDetailDto record) {
        String safeUserId = requireUserId(userId);
        if (record == null) {
            throw new IllegalArgumentException("Further-study record is required.");
        }
        LocalDateTime now = LocalDateTime.now();
        boolean created = !hasText(record.getRecordId());
        if (created) {
            record.setRecordId(newId("fsr"));
            record.setCreatedAt(now);
        } else if (record.getCreatedAt() == null) {
            FurtherStudyRecordDetailDto existing = records.get(record.getRecordId());
            record.setCreatedAt(existing == null ? now : existing.getCreatedAt());
        }
        record.setUserId(safeUserId);
        record.setStatus(defaultText(record.getStatus(), FurtherStudyConstants.STATUS_IN_PROGRESS));
        record.setUpdatedAt(now);
        records.put(record.getRecordId(), record);
        appendEvent(safeUserId, event(record.getTrack(), record.getRecordId(),
                created ? FurtherStudyConstants.EVENT_RECORD_CREATED : FurtherStudyConstants.EVENT_STATUS_UPDATED,
                created ? "Saved further-study record: " + defaultText(record.getTitle(), "untitled record") : "Updated further-study record."));
        return record;
    }

    public List<FurtherStudyRecordSummaryDto> listRecords(String userId, FurtherStudyRecordQueryRequest query) {
        String safeUserId = requireUserId(userId);
        List<FurtherStudyRecordSummaryDto> out = new ArrayList<FurtherStudyRecordSummaryDto>();
        for (FurtherStudyRecordDetailDto record : records.values()) {
            if (!safeUserId.equals(record.getUserId()) || !matches(record, query)) {
                continue;
            }
            out.add(copySummary(record));
        }
        Collections.sort(out, new Comparator<FurtherStudyRecordSummaryDto>() {
            public int compare(FurtherStudyRecordSummaryDto left, FurtherStudyRecordSummaryDto right) {
                LocalDateTime a = left.getUpdatedAt();
                LocalDateTime b = right.getUpdatedAt();
                if (a == null && b == null) {
                    return 0;
                }
                if (a == null) {
                    return 1;
                }
                if (b == null) {
                    return -1;
                }
                return b.compareTo(a);
            }
        });
        int offset = query == null || query.getOffset() == null ? 0 : Math.max(0, query.getOffset().intValue());
        int limit = query == null || query.getLimit() == null ? out.size() : Math.max(0, query.getLimit().intValue());
        int to = Math.min(out.size(), offset + limit);
        if (offset >= out.size()) {
            return new ArrayList<FurtherStudyRecordSummaryDto>();
        }
        return new ArrayList<FurtherStudyRecordSummaryDto>(out.subList(offset, to));
    }

    public FurtherStudyRecordDetailDto loadRecord(String userId, String recordId) {
        String safeUserId = requireUserId(userId);
        FurtherStudyRecordDetailDto record = records.get(requireText(recordId, "recordId"));
        return record == null || !safeUserId.equals(record.getUserId()) ? null : record;
    }

    public FurtherStudyRecordDetailDto updateRecordStatus(String userId, FurtherStudyRecordStatusUpdateRequest request) {
        String safeUserId = requireUserId(userId);
        if (request == null) {
            throw new IllegalArgumentException("Status update request is required.");
        }
        FurtherStudyRecordDetailDto record = loadRecord(safeUserId, request.getRecordId());
        if (record == null) {
            throw new IllegalArgumentException("Further-study record was not found.");
        }
        record.setStatus(defaultText(request.getStatus(), FurtherStudyConstants.STATUS_IN_PROGRESS));
        record.setUpdatedAt(LocalDateTime.now());
        records.put(record.getRecordId(), record);
        appendEvent(safeUserId, event(record.getTrack(), record.getRecordId(), FurtherStudyConstants.EVENT_STATUS_UPDATED,
                defaultText(request.getEventSummary(), "Updated status to " + FurtherStudyConstants.statusLabel(record.getStatus()))));
        return record;
    }

    public FurtherStudyMaterialDto saveMaterial(String userId, FurtherStudyMaterialDto material) {
        String safeUserId = requireUserId(userId);
        if (material == null) {
            throw new IllegalArgumentException("Further-study material is required.");
        }
        LocalDateTime now = LocalDateTime.now();
        if (!hasText(material.getMaterialId())) {
            material.setMaterialId(newId("fsm"));
            material.setCreatedAt(now);
        } else if (material.getCreatedAt() == null) {
            FurtherStudyMaterialDto existing = materials.get(material.getMaterialId());
            material.setCreatedAt(existing == null ? now : existing.getCreatedAt());
        }
        material.setUserId(safeUserId);
        material.setStatus(defaultText(material.getStatus(), FurtherStudyConstants.STATUS_IN_PROGRESS));
        material.setUpdatedAt(now);
        materials.put(material.getMaterialId(), material);
        appendEvent(safeUserId, event(material.getTrack(), material.getRecordId(),
                FurtherStudyConstants.EVENT_MATERIAL_SAVED, "Saved material: " + defaultText(material.getTitle(), "untitled material")));
        return material;
    }

    public List<FurtherStudyMaterialDto> listMaterials(String userId, String track, String recordId) {
        String safeUserId = requireUserId(userId);
        List<FurtherStudyMaterialDto> out = new ArrayList<FurtherStudyMaterialDto>();
        for (FurtherStudyMaterialDto material : materials.values()) {
            if (!safeUserId.equals(material.getUserId())) {
                continue;
            }
            if (hasText(track) && !track.trim().equals(material.getTrack())) {
                continue;
            }
            if (hasText(recordId) && !recordId.trim().equals(material.getRecordId())) {
                continue;
            }
            out.add(material);
        }
        return out;
    }

    public FurtherStudyEventDto appendEvent(String userId, FurtherStudyEventDto event) {
        String safeUserId = requireUserId(userId);
        if (event == null) {
            throw new IllegalArgumentException("Further-study event is required.");
        }
        if (!hasText(event.getEventId())) {
            event.setEventId(newId("fse"));
        }
        event.setUserId(safeUserId);
        if (event.getCreatedAt() == null) {
            event.setCreatedAt(LocalDateTime.now());
        }
        events.put(event.getEventId(), event);
        return event;
    }

    public List<FurtherStudyEventDto> listEvents(String userId, String recordId) {
        String safeUserId = requireUserId(userId);
        String safeRecordId = requireText(recordId, "recordId");
        List<FurtherStudyEventDto> out = new ArrayList<FurtherStudyEventDto>();
        for (FurtherStudyEventDto event : events.values()) {
            if (safeUserId.equals(event.getUserId()) && safeRecordId.equals(event.getRecordId())) {
                out.add(event);
            }
        }
        Collections.sort(out, new Comparator<FurtherStudyEventDto>() {
            public int compare(FurtherStudyEventDto left, FurtherStudyEventDto right) {
                return left.getCreatedAt().compareTo(right.getCreatedAt());
            }
        });
        return out;
    }

    private boolean matches(FurtherStudyRecordDetailDto record, FurtherStudyRecordQueryRequest query) {
        if (query == null) {
            return true;
        }
        if (hasText(query.getTrack()) && !query.getTrack().trim().equals(record.getTrack())) {
            return false;
        }
        if (hasText(query.getRecordType()) && !query.getRecordType().trim().equals(record.getRecordType())) {
            return false;
        }
        if (hasText(query.getStatus()) && !query.getStatus().trim().equals(record.getStatus())) {
            return false;
        }
        if (hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            return contains(record.getTitle(), keyword)
                    || contains(record.getTargetSchool(), keyword)
                    || contains(record.getTargetMajor(), keyword)
                    || contains(record.getTargetRegion(), keyword);
        }
        return true;
    }

    private FurtherStudyRecordSummaryDto copySummary(FurtherStudyRecordDetailDto record) {
        FurtherStudyRecordSummaryDto summary = new FurtherStudyRecordSummaryDto();
        summary.setRecordId(record.getRecordId());
        summary.setUserId(record.getUserId());
        summary.setTrack(record.getTrack());
        summary.setRecordType(record.getRecordType());
        summary.setTargetId(record.getTargetId());
        summary.setTitle(record.getTitle());
        summary.setStatus(record.getStatus());
        summary.setTargetSchool(record.getTargetSchool());
        summary.setTargetMajor(record.getTargetMajor());
        summary.setTargetRegion(record.getTargetRegion());
        summary.setExamOrDeadlineDate(record.getExamOrDeadlineDate());
        summary.setCreatedAt(record.getCreatedAt());
        summary.setUpdatedAt(record.getUpdatedAt());
        return summary;
    }

    private FurtherStudyEventDto event(String track, String recordId, String eventType, String summary) {
        FurtherStudyEventDto event = new FurtherStudyEventDto();
        event.setTrack(track);
        event.setRecordId(recordId);
        event.setEventType(eventType);
        event.setSummary(summary);
        return event;
    }

    private String requireUserId(String userId) {
        return requireText(userId, "userId");
    }

    private String requireText(String value, String name) {
        if (!hasText(value)) {
            throw new IllegalArgumentException(name + " is required.");
        }
        return value.trim();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.contains(keyword);
    }

    private String defaultText(String value, String fallback) {
        return hasText(value) ? value.trim() : fallback;
    }

    private String newId(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().replace("-", "");
    }
}
