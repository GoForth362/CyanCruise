package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.ResumeCreateRequest;
import v620.cc001.base.common.dto.career.ResumeRecordDto;
import v620.cc001.base.common.dto.career.ResumeUpdateRequest;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Application boundary for migrated resume record management.
 */
public class ResumeApplicationService {

    private final ResumeStorage resumeStorage;
    private final CareerProfileApplicationService profileApplicationService;

    public ResumeApplicationService() {
        this(new FileResumeStorage(), new CareerProfileApplicationService());
    }

    public ResumeApplicationService(ResumeStorage resumeStorage,
                                    CareerProfileApplicationService profileApplicationService) {
        this.resumeStorage = resumeStorage;
        this.profileApplicationService = profileApplicationService;
    }

    public ResumeRecordDto create(String userId, ResumeCreateRequest request) {
        String safeUserId = requireUserId(userId);
        ResumeCreateRequest safeRequest = request == null ? new ResumeCreateRequest() : request;
        LocalDateTime now = LocalDateTime.now();
        String fileKey = trimToNull(safeRequest.getFileKey());
        ResumeRecordDto existing = findExistingByFileKey(safeUserId, fileKey);
        if (existing != null) {
            existing.setTitle(trimToNull(safeRequest.getTitle()));
            existing.setTargetJob(trimToNull(safeRequest.getTargetJob()));
            existing.setFileKey(fileKey);
            existing.setParsedContent(trimToNull(safeRequest.getParsedContent()));
            existing.setUpdatedAt(now);
            ResumeRecordDto saved = resumeStorage.save(existing);
            syncResumeBlock(safeUserId, saved);
            return saved;
        }
        ResumeRecordDto record = new ResumeRecordDto();
        record.setUserId(safeUserId);
        record.setTitle(trimToNull(safeRequest.getTitle()));
        record.setTargetJob(trimToNull(safeRequest.getTargetJob()));
        record.setFileKey(fileKey);
        record.setParsedContent(trimToNull(safeRequest.getParsedContent()));
        record.setVersion("v1.0");
        record.setStatus("UPLOADED");
        record.setDiagnosisScore(Integer.valueOf(0));
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        ResumeRecordDto saved = resumeStorage.save(record);
        syncResumeBlock(safeUserId, saved);
        return saved;
    }

    public ResumeRecordDto get(String userId, Long resumeId) {
        return requireOwned(requireUserId(userId), resumeId);
    }

    public List<ResumeRecordDto> listByUser(String userId) {
        return resumeStorage.listByUser(requireUserId(userId));
    }

    public ResumeRecordDto update(String userId, Long resumeId, ResumeUpdateRequest request) {
        String safeUserId = requireUserId(userId);
        ResumeRecordDto record = requireOwned(safeUserId, resumeId);
        if (request != null) {
            if (request.getTitle() != null) record.setTitle(trimToNull(request.getTitle()));
            if (request.getTargetJob() != null) record.setTargetJob(trimToNull(request.getTargetJob()));
            if (request.getFileKey() != null) record.setFileKey(trimToNull(request.getFileKey()));
            if (request.getParsedContent() != null) record.setParsedContent(trimToNull(request.getParsedContent()));
            if (request.getVersion() != null) record.setVersion(trimToNull(request.getVersion()));
            if (request.getStatus() != null) record.setStatus(trimToNull(request.getStatus()));
            if (request.getDiagnosisScore() != null) record.setDiagnosisScore(request.getDiagnosisScore());
        }
        record.setUpdatedAt(LocalDateTime.now());
        ResumeRecordDto saved = resumeStorage.save(record);
        syncResumeBlock(safeUserId, saved);
        return saved;
    }

    public void delete(String userId, Long resumeId) {
        String safeUserId = requireUserId(userId);
        ResumeRecordDto owned = requireOwned(safeUserId, resumeId);
        resumeStorage.delete(owned.getResumeId());
        UserProfileSnapshot snapshot = profileApplicationService.getSnapshot(safeUserId);
        UserProfileSnapshot.ResumeBlock resume = snapshot.getResume();
        if (resume != null && owned.getResumeId().equals(resume.getLastResumeId())) {
            List<ResumeRecordDto> remaining = resumeStorage.listByUser(safeUserId);
            if (remaining.isEmpty()) {
                profileApplicationService.clearResume(safeUserId);
            } else {
                syncResumeBlock(safeUserId, remaining.get(0));
            }
        }
    }

    private ResumeRecordDto requireOwned(String userId, Long resumeId) {
        if (resumeId == null) {
            throw new IllegalArgumentException("resumeId is required");
        }
        ResumeRecordDto record = resumeStorage.load(resumeId);
        if (record == null) {
            throw new IllegalArgumentException("resume not found: " + resumeId);
        }
        if (!userId.equals(record.getUserId())) {
            throw new IllegalArgumentException("resume does not belong to user: " + resumeId);
        }
        return record;
    }

    private void syncResumeBlock(String userId, ResumeRecordDto record) {
        if (record == null) {
            profileApplicationService.clearResume(userId);
            return;
        }
        UserProfileSnapshot.ResumeBlock block = new UserProfileSnapshot.ResumeBlock();
        block.setLastResumeId(record.getResumeId());
        block.setLastResumeKey(record.getFileKey());
        block.setTitle(record.getTitle());
        block.setTargetJob(record.getTargetJob());
        block.setDiagnosisScore(record.getDiagnosisScore());
        block.setUpdatedAt(record.getUpdatedAt());
        profileApplicationService.saveResume(userId, block);
    }

    private ResumeRecordDto findExistingByFileKey(String userId, String fileKey) {
        if (fileKey == null) {
            return null;
        }
        List<ResumeRecordDto> records = resumeStorage.listByUser(userId);
        for (ResumeRecordDto record : records) {
            if (record != null && fileKey.equals(trimToNull(record.getFileKey()))) {
                return record;
            }
        }
        return null;
    }

    private String requireUserId(String userId) {
        if (userId == null || userId.trim().length() == 0) {
            throw new IllegalArgumentException("userId is required");
        }
        return userId.trim();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.length() == 0 ? null : trimmed;
    }
}
