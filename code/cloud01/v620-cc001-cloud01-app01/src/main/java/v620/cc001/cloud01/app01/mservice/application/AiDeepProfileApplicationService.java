package v620.cc001.cloud01.app01.mservice.application;

import v620.cc001.base.common.dto.career.AssessmentScoreResult;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;
import v620.cc001.cloud01.app01.mservice.ai.impl.AgentPlatformDeepProfileAnalyzer;
import v620.cc001.cloud01.app01.mservice.ai.impl.DefaultAgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.impl.KingdeeAgentSdkTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.storage.AssessmentResultStorage;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Coordinates profile enrichment from persisted assessments without changing user facts. */
public class AiDeepProfileApplicationService {

    private final AssessmentResultStorage assessmentStorage;
    private final CareerProfileApplicationService profileService;
    private final AgentPlatformDeepProfileAnalyzer analyzer;

    public AiDeepProfileApplicationService() {
        this(CyanCruiseStorageFactory.assessmentResultStorage(), new CareerProfileApplicationService(), defaultAnalyzer());
    }

    public AiDeepProfileApplicationService(AssessmentResultStorage assessmentStorage,
                                           CareerProfileApplicationService profileService,
                                           AgentPlatformDeepProfileAnalyzer analyzer) {
        this.assessmentStorage = assessmentStorage;
        this.profileService = profileService;
        this.analyzer = analyzer;
    }

    public UserProfileSnapshot.AiDeepProfileBlock generate(String userId) {
        String safeUserId = requireUserId(userId);
        List<AssessmentScoreResult> completed = completed(assessmentStorage.listResults(safeUserId));
        if (completed.isEmpty()) {
            throw new IllegalArgumentException("请先完成至少一项画像测评，再生成深度画像");
        }
        UserProfileSnapshot snapshot = profileService.getSnapshot(safeUserId);
        UserProfileSnapshot.AiDeepProfileBlock profile = analyzeWithRetry(completed, snapshot);
        profile.setRecordId(UUID.randomUUID().toString());
        profileService.saveAiDeepProfile(safeUserId, profile);
        return profile;
    }

    public UserProfileSnapshot.AiDeepProfileBlock latest(String userId) {
        return profileService.getSnapshot(requireUserId(userId)).getAiDeepProfile();
    }

    public List<UserProfileSnapshot.AiDeepProfileBlock> history(String userId) {
        return profileService.getAiDeepProfileHistory(requireUserId(userId));
    }

    public UserProfileSnapshot.AiDeepProfileBlock detail(String userId, String recordId) {
        String safeRecordId = requireRecordId(recordId);
        List<UserProfileSnapshot.AiDeepProfileBlock> history = history(userId);
        for (UserProfileSnapshot.AiDeepProfileBlock profile : history) {
            if (profile != null && safeRecordId.equals(profile.getRecordId())) {
                return profile;
            }
        }
        throw new IllegalArgumentException("未找到这条深度画像记录");
    }

    private static AgentPlatformDeepProfileAnalyzer defaultAnalyzer() {
        AgentPlatformTaskFlowConfig config = AgentPlatformTaskFlowConfig.fromSystemProperties("cc001.agent.platform.profile");
        if (config.isAgentSdkAvailable()) {
            return new AgentPlatformDeepProfileAnalyzer(new KingdeeAgentSdkTaskFlowClient(config));
        }
        if (config.isEnabled()) {
            return new AgentPlatformDeepProfileAnalyzer(new DefaultAgentPlatformTaskFlowClient(config));
        }
        return new AgentPlatformDeepProfileAnalyzer(null);
    }

    private List<AssessmentScoreResult> completed(List<AssessmentScoreResult> values) {
        Map<String, AssessmentScoreResult> latestByScale =
                new LinkedHashMap<String, AssessmentScoreResult>();
        if (values == null) return new ArrayList<AssessmentScoreResult>();
        for (AssessmentScoreResult value : values) {
            if (value == null || !"COMPLETED".equalsIgnoreCase(value.getStatus())) continue;
            String key = scaleKey(value);
            AssessmentScoreResult current = latestByScale.get(key);
            if (current == null || isNewer(value, current)) {
                latestByScale.put(key, value);
            }
        }
        return new ArrayList<AssessmentScoreResult>(latestByScale.values());
    }

    private UserProfileSnapshot.AiDeepProfileBlock analyzeWithRetry(List<AssessmentScoreResult> completed,
                                                                     UserProfileSnapshot snapshot) {
        return analyzer.analyze(completed, snapshot);
    }

    private String scaleKey(AssessmentScoreResult value) {
        if (value.getScaleId() != null) {
            return "id:" + value.getScaleId();
        }
        String title = value.getScaleTitle();
        return "title:" + (title == null ? "" : title.trim());
    }

    private boolean isNewer(AssessmentScoreResult candidate, AssessmentScoreResult current) {
        if (candidate.getCreatedAt() != null && current.getCreatedAt() != null) {
            int compared = candidate.getCreatedAt().compareTo(current.getCreatedAt());
            if (compared != 0) return compared > 0;
        } else if (candidate.getCreatedAt() != null) {
            return true;
        } else if (current.getCreatedAt() != null) {
            return false;
        }
        long candidateId = candidate.getRecordId() == null ? 0L : candidate.getRecordId().longValue();
        long currentId = current.getRecordId() == null ? 0L : current.getRecordId().longValue();
        return candidateId > currentId;
    }

    private String requireUserId(String userId) {
        if (userId == null || userId.trim().length() == 0) throw new IllegalArgumentException("userId is required");
        return userId.trim();
    }

    private String requireRecordId(String recordId) {
        if (recordId == null || recordId.trim().length() == 0) {
            throw new IllegalArgumentException("recordId is required");
        }
        return recordId.trim();
    }
}
