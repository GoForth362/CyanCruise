package v620.cc001.cloud01.app01.mservice;

import v620.base.helper.career.ResumeDiagnosisService;
import v620.cc001.base.common.dto.career.ResumeDiagnosisConstants;
import v620.cc001.base.common.dto.career.ResumeDiagnosisRequest;
import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeKeywordStatusDto;
import v620.cc001.base.common.dto.career.ResumeRecordDto;
import v620.cc001.base.common.dto.career.ResumeUpdateRequest;
import v620.cc001.cloud01.app01.mservice.ai.AiProviderAdapterFactory;
import v620.cc001.cloud01.app01.mservice.ai.DefaultAiGateway;

/**
 * Application boundary for migrated resume diagnosis.
 */
public class ResumeDiagnosisApplicationService {

    private final ResumeApplicationService resumeApplicationService;
    private final ResumeDiagnosisStorage storage;
    private final ResumeDiagnosisAnalyzer analyzer;
    private final ResumeDiagnosisService helper;

    public ResumeDiagnosisApplicationService() {
        this(new ResumeApplicationService(),
                CyanCruiseStorageFactory.resumeDiagnosisStorage(),
                new AiGatewayResumeDiagnosisAnalyzer(
                        new DefaultAiGateway(AiProviderAdapterFactory.fromSystemProperties()),
                        new DefaultResumeDiagnosisAnalyzer()),
                new ResumeDiagnosisService());
    }

    public ResumeDiagnosisApplicationService(ResumeApplicationService resumeApplicationService,
                                             ResumeDiagnosisStorage storage,
                                             ResumeDiagnosisAnalyzer analyzer,
                                             ResumeDiagnosisService helper) {
        this.resumeApplicationService = resumeApplicationService;
        this.storage = storage;
        this.analyzer = analyzer;
        this.helper = helper;
    }

    public ResumeDiagnosisResultDto diagnose(String userId, ResumeDiagnosisRequest request) {
        String safeUserId = requireUserId(userId);
        ResumeDiagnosisRequest safeRequest = request == null ? new ResumeDiagnosisRequest() : request;
        ResumeRecordDto resume = null;
        if (safeRequest.getResumeId() != null) {
            resume = resumeApplicationService.get(safeUserId, safeRequest.getResumeId());
        }
        String text = trimToNull(safeRequest.getResumeText());
        if (text == null && resume != null) {
            text = trimToNull(resume.getParsedContent());
        }
        if (text == null) {
            throw new IllegalArgumentException("resume content is empty or could not be parsed");
        }
        String rawAnalysis = analyzer.analyze(safeRequest, limitText(text));
        ResumeDiagnosisResultDto result = helper.parseAnalysis(rawAnalysis);
        result.setResumeId(safeRequest.getResumeId());
        result.setRawAnalysis(rawAnalysis);
        if (result.getResumeId() != null) {
            storage.saveDiagnosis(result);
            ResumeUpdateRequest update = new ResumeUpdateRequest();
            update.setDiagnosisScore(result.getOverallScore());
            resumeApplicationService.update(safeUserId, result.getResumeId(), update);
        }
        return result;
    }

    public ResumeKeywordStatusDto getKeywordStatus(String userId, Long resumeId) {
        requireOwned(userId, resumeId);
        ResumeKeywordStatusDto status = storage.loadKeywordStatus(resumeId);
        if (status != null) {
            return status;
        }
        ResumeKeywordStatusDto pending = new ResumeKeywordStatusDto();
        pending.setResumeId(resumeId);
        pending.setStatus(ResumeDiagnosisConstants.STATUS_PENDING);
        return pending;
    }

    public ResumeKeywordStatusDto triggerKeywordExtraction(String userId, Long resumeId, boolean force) {
        ResumeRecordDto resume = requireOwned(userId, resumeId);
        ResumeKeywordStatusDto current = storage.loadKeywordStatus(resumeId);
        if (!force && current != null && hasReusableStatus(current.getStatus())) {
            return current;
        }
        ResumeKeywordStatusDto processing = new ResumeKeywordStatusDto();
        processing.setResumeId(resumeId);
        processing.setStatus(ResumeDiagnosisConstants.STATUS_PROCESSING);
        storage.saveKeywordStatus(processing);
        try {
            ResumeKeywordStatusDto extracted = helper.extractKeywordStatus(resume);
            return storage.saveKeywordStatus(extracted);
        } catch (Exception e) {
            ResumeKeywordStatusDto failed = new ResumeKeywordStatusDto();
            failed.setResumeId(resumeId);
            failed.setStatus(ResumeDiagnosisConstants.STATUS_FAILED);
            failed.setErrorMsg(trim(e.getMessage(), 250));
            return storage.saveKeywordStatus(failed);
        }
    }

    public ResumeDiagnosisResultDto getDiagnosis(String userId, Long resumeId) {
        requireOwned(userId, resumeId);
        return storage.loadDiagnosis(resumeId);
    }

    private ResumeRecordDto requireOwned(String userId, Long resumeId) {
        return resumeApplicationService.get(requireUserId(userId), resumeId);
    }

    private boolean hasReusableStatus(String status) {
        return ResumeDiagnosisConstants.STATUS_READY.equals(status)
                || ResumeDiagnosisConstants.STATUS_EMPTY.equals(status)
                || ResumeDiagnosisConstants.STATUS_FAILED.equals(status)
                || ResumeDiagnosisConstants.STATUS_PENDING.equals(status)
                || ResumeDiagnosisConstants.STATUS_PROCESSING.equals(status);
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

    private String limitText(String value) {
        return value.length() > ResumeDiagnosisConstants.MAX_TEXT_CHARS
                ? value.substring(0, ResumeDiagnosisConstants.MAX_TEXT_CHARS)
                : value;
    }

    private String trim(String value, int max) {
        if (value == null) return null;
        return value.length() <= max ? value : value.substring(0, max);
    }
}
