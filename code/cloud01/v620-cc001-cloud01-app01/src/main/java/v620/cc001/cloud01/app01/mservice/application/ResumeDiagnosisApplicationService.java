package v620.cc001.cloud01.app01.mservice.application;

import v620.cc001.cloud01.app01.mservice.ai.impl.AgentPlatformResumeDiagnosisAnalyzer;
import v620.cc001.cloud01.app01.mservice.ai.impl.DefaultAgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.impl.KingdeeAgentSdkTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.ResumeDiagnosisAnalyzer;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;
import v620.cc001.cloud01.app01.mservice.storage.ResumeDiagnosisStorage;
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

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/**
 * Application boundary for migrated resume diagnosis.
 */
public class ResumeDiagnosisApplicationService {

    private static final long DIAGNOSIS_CACHE_TTL_MILLIS = 30L * 60L * 1000L;

    private final ResumeApplicationService resumeApplicationService;
    private final ResumeDiagnosisStorage storage;
    private final ResumeDiagnosisAnalyzer analyzer;
    private final ResumeDiagnosisService helper;
    private final CareerProfileApplicationService profileApplicationService;
    private final FileUploadPreviewApplicationService fileApplicationService;
    private final Map<String, CachedDiagnosis> diagnosisCache = new ConcurrentHashMap<String, CachedDiagnosis>();

    public ResumeDiagnosisApplicationService() {
        this(new ResumeApplicationService(),
                CyanCruiseStorageFactory.resumeDiagnosisStorage(),
                defaultAnalyzer(),
                new ResumeDiagnosisService());
    }

    private static ResumeDiagnosisAnalyzer defaultAnalyzer() {
        AgentPlatformTaskFlowConfig platformConfig = AgentPlatformTaskFlowConfig.fromSystemProperties();
        if (platformConfig.isAgentSdkAvailable()) {
            return new AgentPlatformResumeDiagnosisAnalyzer(
                    new KingdeeAgentSdkTaskFlowClient(platformConfig),
                    platformConfig);
        }
        if (platformConfig.isEnabled()) {
            return new AgentPlatformResumeDiagnosisAnalyzer(
                    new DefaultAgentPlatformTaskFlowClient(platformConfig),
                    platformConfig);
        }
        return new AgentPlatformResumeDiagnosisAnalyzer(null, platformConfig);
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
        String cacheKey = diagnosisCacheKey(safeUserId, safeRequest, text);
        CachedDiagnosis cached = diagnosisCache.get(cacheKey);
        boolean reused = cached != null && cached.isFresh();
        String rawAnalysis;
        String source;
        if (reused) {
            rawAnalysis = cached.rawAnalysis;
            source = cached.source;
        } else {
            rawAnalysis = analyzer.analyze(safeRequest, limitText(text));
            source = diagnosisSource();
        }
        ResumeDiagnosisResultDto result = helper.parseAnalysis(rawAnalysis);
        result.setFallbackStatus(source);
        result.setResumeId(safeRequest.getResumeId());
        result.setUserId(safeUserId);
        result.setTargetJob(safeRequest.getTargetJob());
        result.setDiagnosedAt(LocalDateTime.now());
        result.setRawAnalysis(rawAnalysis);
        result.setContextSources(contextSources);
        result.setRevisionPlan(helper.buildRevisionPlan(result.getRevisionSuggestions(), contextSources));
        if (result.getResumeId() != null) {
            if (reused && cached.diagnosisId != null) {
                result.setDiagnosisId(cached.diagnosisId);
                result.setDiagnosedAt(cached.diagnosedAt);
            } else {
                storage.saveDiagnosis(result);
                diagnosisCache.put(cacheKey, new CachedDiagnosis(rawAnalysis, source,
                        result.getDiagnosisId(), result.getDiagnosedAt()));
            }
            ResumeUpdateRequest update = new ResumeUpdateRequest();
            update.setDiagnosisScore(result.getOverallScore());
            resumeApplicationService.update(safeUserId, result.getResumeId(), update);
        } else if (!reused) {
            diagnosisCache.put(cacheKey, new CachedDiagnosis(rawAnalysis, source, null, result.getDiagnosedAt()));
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

    public List<ResumeDiagnosisResultDto> listDiagnosisHistory(String userId, Long resumeId) {
        requireOwned(userId, resumeId);
        return storage.listDiagnoses(requireUserId(userId), resumeId);
    }

    public boolean deleteDiagnosisHistory(String userId, Long resumeId, Long diagnosisId) {
        requireOwned(userId, resumeId);
        if (diagnosisId == null) {
            throw new IllegalArgumentException("diagnosisId is required");
        }
        return storage.deleteDiagnosis(requireUserId(userId), diagnosisId);
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
            UserProfileSnapshot.AiDeepProfileBlock aiDeepProfile = snapshot.getAiDeepProfile();
            if (!hasText(request.getTargetJob()) && preferences != null && hasText(preferences.getTargetRole())) {
                request.setTargetJob(preferences.getTargetRole());
                sources.add("profile.preferences.targetRole");
            }
            if (!hasText(request.getTargetJob()) && resumeBlock != null && hasText(resumeBlock.getTargetJob())) {
                request.setTargetJob(resumeBlock.getTargetJob());
                sources.add("profile.resume.targetJob");
            }
            String snapshotContext = profileContext(snapshot);
            if (hasText(snapshotContext)) {
                request.setProfileContext(mergeProfileContext(request.getProfileContext(), snapshotContext));
                sources.add("profile.snapshot");
            }
            if (assessment != null && hasText(assessment.getSummary())) {
                sources.add("profile.assessment");
            }
            if (aiDeepProfile != null && hasText(aiDeepProfile.getProfileSummary())) {
                sources.add("profile.aiDeepProfile");
            }
            if (snapshot.getOnboarding() != null
                    && hasText(snapshot.getOnboarding().getSelfProfileSupplement())) {
                sources.add("profile.onboarding.selfProfileSupplement");
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
        UserProfileSnapshot.AiDeepProfileBlock aiDeepProfile = snapshot.getAiDeepProfile();
        if (preferences != null && hasText(preferences.getTargetRole())) {
            append(builder, "targetRole=" + preferences.getTargetRole());
        }
        if (assessment != null && hasText(assessment.getSummary())) {
            append(builder, "assessment=" + assessment.getSummary());
        }
        if (aiDeepProfile != null && hasText(aiDeepProfile.getProfileSummary())) {
            append(builder, "aiDeepProfileSummary=" + aiDeepProfile.getProfileSummary());
            append(builder, "aiDeepProfileTags=" + join(aiDeepProfile.getProfileTags()));
            append(builder, "aiDeepProfileSource=assessment-ai-analysis");
        }
        if (onboarding != null && hasText(onboarding.getStage())) {
            append(builder, "stage=" + onboarding.getStage());
        }
        if (onboarding != null && hasText(onboarding.getIdentityType())) {
            append(builder, "identityType=" + onboarding.getIdentityType());
        }
        if (onboarding != null && hasText(onboarding.getExperience())) {
            append(builder, "experience=" + onboarding.getExperience());
        }
        if (onboarding != null && hasText(onboarding.getSelfProfileSupplement())) {
            append(builder, "selfProfileSupplement(user-provided-fact)="
                    + onboarding.getSelfProfileSupplement());
        }
        if (onboarding != null && hasText(onboarding.getWeeklyAvailability())) {
            append(builder, "weeklyAvailability=" + onboarding.getWeeklyAvailability());
        }
        if (onboarding != null && onboarding.getEducation() != null) {
            UserProfileSnapshot.EducationBlock education = onboarding.getEducation();
            if (hasText(education.getSchool())) append(builder, "school=" + education.getSchool());
            if (hasText(education.getMajor())) append(builder, "major=" + education.getMajor());
            if (hasText(education.getDegree())) append(builder, "degree=" + education.getDegree());
            if (hasText(education.getGraduationYear())) append(builder, "graduationYear=" + education.getGraduationYear());
        }
        return builder.length() == 0 ? null : builder.toString();
    }

    private String join(List<String> values) {
        if (values == null || values.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            if (!hasText(value)) continue;
            if (builder.length() > 0) builder.append("、");
            builder.append(value.trim());
        }
        return builder.toString();
    }

    private String mergeProfileContext(String requestContext, String snapshotContext) {
        if (!hasText(requestContext)) return snapshotContext;
        if (!hasText(snapshotContext) || requestContext.contains(snapshotContext)) return requestContext;
        return requestContext + "; " + snapshotContext;
    }

    private String diagnosisCacheKey(String userId, ResumeDiagnosisRequest request, String resumeText) {
        String source = value(userId) + "\n" + value(resumeText) + "\n" + value(request.getTargetJob())
                + "\n" + value(request.getJobDescription()) + "\n" + value(request.getProfileContext());
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(source.getBytes(StandardCharsets.UTF_8));
            StringBuilder key = new StringBuilder();
            for (byte item : digest) {
                key.append(String.format("%02x", item & 0xff));
            }
            return key.toString();
        } catch (Exception ignored) {
            return String.valueOf(source.hashCode());
        }
    }

    private String value(String text) {
        return text == null ? "" : text;
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

    private String diagnosisSource() {
        if (analyzer instanceof AgentPlatformResumeDiagnosisAnalyzer) {
            return ((AgentPlatformResumeDiagnosisAnalyzer) analyzer).getLastResultSource();
        }
        return AgentPlatformResumeDiagnosisAnalyzer.SOURCE_AGENT_AI;
    }

    private static class CachedDiagnosis {
        private final String rawAnalysis;
        private final String source;
        private final Long diagnosisId;
        private final LocalDateTime diagnosedAt;
        private final long createdAt;

        private CachedDiagnosis(String rawAnalysis, String source, Long diagnosisId, LocalDateTime diagnosedAt) {
            this.rawAnalysis = rawAnalysis;
            this.source = source;
            this.diagnosisId = diagnosisId;
            this.diagnosedAt = diagnosedAt;
            this.createdAt = System.currentTimeMillis();
        }

        private boolean isFresh() {
            return System.currentTimeMillis() - createdAt < DIAGNOSIS_CACHE_TTL_MILLIS;
        }
    }
}
