package v620.cc001.cloud01.app01.mservice;

import v620.base.helper.career.ResumeDiagnosisService;
import v620.cc001.base.common.dto.career.ResumeDiagnosisConstants;
import v620.cc001.base.common.dto.career.ResumeDiagnosisRequest;
import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeKeywordStatusDto;
import v620.cc001.base.common.dto.career.ResumeRecordDto;
import v620.cc001.base.common.dto.career.ResumeUpdateRequest;
import v620.cc001.base.common.dto.career.FileConstants;
import v620.cc001.base.common.dto.career.FileTextExtractionResult;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.cloud01.app01.mservice.ai.AiProviderAdapterFactory;
import v620.cc001.cloud01.app01.mservice.ai.DefaultAiGateway;

import java.util.ArrayList;
import java.util.List;

/**
 * Application boundary for migrated resume diagnosis.
 */
public class ResumeDiagnosisApplicationService {

    private final ResumeApplicationService resumeApplicationService;
    private final ResumeDiagnosisStorage storage;
    private final ResumeDiagnosisAnalyzer analyzer;
    private final ResumeDiagnosisService helper;
    private final CareerProfileApplicationService profileApplicationService;
    private final FileUploadPreviewApplicationService fileApplicationService;

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
        this(resumeApplicationService, storage, analyzer, helper, new CareerProfileApplicationService());
    }

    public ResumeDiagnosisApplicationService(ResumeApplicationService resumeApplicationService,
                                             ResumeDiagnosisStorage storage,
                                             ResumeDiagnosisAnalyzer analyzer,
                                             ResumeDiagnosisService helper,
                                             CareerProfileApplicationService profileApplicationService) {
        this(resumeApplicationService, storage, analyzer, helper, profileApplicationService,
                new FileUploadPreviewApplicationService());
    }

    public ResumeDiagnosisApplicationService(ResumeApplicationService resumeApplicationService,
                                             ResumeDiagnosisStorage storage,
                                             ResumeDiagnosisAnalyzer analyzer,
                                             ResumeDiagnosisService helper,
                                             CareerProfileApplicationService profileApplicationService,
                                             FileUploadPreviewApplicationService fileApplicationService) {
        this.resumeApplicationService = resumeApplicationService;
        this.storage = storage;
        this.analyzer = analyzer;
        this.helper = helper;
        this.profileApplicationService = profileApplicationService;
        this.fileApplicationService = fileApplicationService;
    }

    public ResumeDiagnosisResultDto diagnose(String userId, ResumeDiagnosisRequest request) {
        String safeUserId = requireUserId(userId);
        ResumeDiagnosisRequest safeRequest = request == null ? new ResumeDiagnosisRequest() : request;
        ResumeRecordDto resume = null;
        if (safeRequest.getResumeId() != null) {
            resume = resumeApplicationService.get(safeUserId, safeRequest.getResumeId());
        }
        List<String> contextSources = applyProfileContext(safeUserId, safeRequest, resume);
        String text = trimToNull(safeRequest.getResumeText());
        if (text == null && resume != null) {
            text = trimToNull(resume.getParsedContent());
        }
        if (text == null && resume != null && hasText(resume.getFileKey())) {
            text = extractAndSaveResumeText(safeUserId, resume, contextSources);
        }
        if (text == null) {
            throw new IllegalArgumentException("resume content is empty or could not be parsed");
        }
        String rawAnalysis = analyzer.analyze(safeRequest, limitText(text));
        ResumeDiagnosisResultDto result = helper.parseAnalysis(rawAnalysis);
        result.setResumeId(safeRequest.getResumeId());
        result.setRawAnalysis(rawAnalysis);
        result.setContextSources(contextSources);
        result.setRevisionPlan(helper.buildRevisionPlan(result.getRevisionSuggestions(), contextSources));
        if (result.getResumeId() != null) {
            storage.saveDiagnosis(result);
            ResumeUpdateRequest update = new ResumeUpdateRequest();
            update.setDiagnosisScore(result.getOverallScore());
            resumeApplicationService.update(safeUserId, result.getResumeId(), update);
        }
        return result;
    }

    private String extractAndSaveResumeText(String userId,
                                            ResumeRecordDto resume,
                                            List<String> contextSources) {
        FileTextExtractionResult extraction = fileApplicationService.extractText(resume.getFileKey());
        String text = extraction == null ? null : trimToNull(extraction.getText());
        if (extraction != null && FileConstants.STATUS_OK.equals(extraction.getStatus()) && text != null) {
            ResumeUpdateRequest update = new ResumeUpdateRequest();
            update.setParsedContent(text);
            resumeApplicationService.update(userId, resume.getResumeId(), update);
            contextSources.add("resume.fileText");
            return text;
        }
        String status = extraction == null ? FileConstants.STATUS_UNAVAILABLE : extraction.getStatus();
        if (FileConstants.STATUS_TEXT_EMPTY.equals(status)) {
            throw new IllegalArgumentException("PDF 中没有可读取的文字，可能是扫描件；请粘贴简历正文后再诊断");
        }
        throw new IllegalArgumentException("PDF 正文读取失败，请稍后重试或粘贴简历正文");
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

    private List<String> applyProfileContext(String userId, ResumeDiagnosisRequest request, ResumeRecordDto resume) {
        List<String> sources = new ArrayList<String>();
        if (resume != null) {
            sources.add("resume:" + resume.getResumeId());
            if (hasText(resume.getTargetJob())) {
                request.setTargetJob(resume.getTargetJob());
                sources.add("resume.targetJob");
            }
        }
        UserProfileSnapshot snapshot = null;
        try {
            snapshot = profileApplicationService.getSnapshot(userId);
        } catch (Exception ignored) {
        }
        if (snapshot != null) {
            UserProfileSnapshot.PreferencesBlock preferences = snapshot.getPreferences();
            UserProfileSnapshot.ResumeBlock resumeBlock = snapshot.getResume();
            UserProfileSnapshot.AssessmentBlock assessment = snapshot.getAssessment();
            if (!hasText(request.getTargetJob()) && preferences != null && hasText(preferences.getTargetRole())) {
                request.setTargetJob(preferences.getTargetRole());
                sources.add("profile.preferences.targetRole");
            }
            if (!hasText(request.getTargetJob()) && resumeBlock != null && hasText(resumeBlock.getTargetJob())) {
                request.setTargetJob(resumeBlock.getTargetJob());
                sources.add("profile.resume.targetJob");
            }
            if (!hasText(request.getProfileContext())) {
                request.setProfileContext(profileContext(snapshot));
                if (hasText(request.getProfileContext())) {
                    sources.add("profile.snapshot");
                }
            }
            if (assessment != null && hasText(assessment.getSummary())) {
                sources.add("profile.assessment");
            }
        }
        if (hasText(request.getJobDescription())) {
            sources.add("request.jobDescription");
        }
        if (hasText(request.getTargetJob())) {
            sources.add("targetJob");
        }
        return sources;
    }

    private String profileContext(UserProfileSnapshot snapshot) {
        StringBuilder builder = new StringBuilder();
        UserProfileSnapshot.PreferencesBlock preferences = snapshot.getPreferences();
        UserProfileSnapshot.AssessmentBlock assessment = snapshot.getAssessment();
        UserProfileSnapshot.OnboardingBlock onboarding = snapshot.getOnboarding();
        if (preferences != null && hasText(preferences.getTargetRole())) {
            append(builder, "targetRole=" + preferences.getTargetRole());
        }
        if (assessment != null && hasText(assessment.getSummary())) {
            append(builder, "assessment=" + assessment.getSummary());
        }
        if (onboarding != null && hasText(onboarding.getStage())) {
            append(builder, "stage=" + onboarding.getStage());
        }
        return builder.length() == 0 ? null : builder.toString();
    }

    private void append(StringBuilder builder, String text) {
        if (builder.length() > 0) {
            builder.append("; ");
        }
        builder.append(text);
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

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
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
