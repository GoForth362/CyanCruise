package v620.cc001.cloud01.app01.mservice.furtherstudy;

import v620.base.helper.furtherstudy.RecommendationCompanionService;
import v620.cc001.base.common.dto.furtherstudy.RecommendationDiagnosisResult;
import v620.cc001.base.common.dto.furtherstudy.RecommendationDocumentPolishRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationDocumentPolishResult;
import v620.cc001.base.common.dto.furtherstudy.RecommendationPlanResult;
import v620.cc001.base.common.dto.furtherstudy.RecommendationProfileRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationTutorLetterRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationTutorLetterResult;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyAnalysisDraftDto;
import v620.cc001.cloud01.app01.mservice.ai.FurtherStudyCompanionAnalyzer;
import v620.cc001.cloud01.app01.mservice.ai.impl.AgentPlatformFurtherStudyCompanionAnalyzer;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;
import v620.cc001.cloud01.app01.mservice.storage.StudyCenterStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryStudyCenterStorage;

import java.time.LocalDateTime;

/** Application boundary for postgraduate recommendation companion. */
public class RecommendationApplicationService {

    private final RecommendationCompanionService helper;
    private final FurtherStudyCompanionAnalyzer analyzer;
    private final StudyCenterStorage draftStorage;
    private final FurtherStudyCompanionStorage recordStorage;

    public RecommendationApplicationService() {
        this.helper = new RecommendationCompanionService();
        this.draftStorage = CyanCruiseStorageFactory.studyCenterStorage();
        this.recordStorage = CyanCruiseStorageFactory.furtherStudyCompanionStorage();
        this.analyzer = AgentPlatformFurtherStudyCompanionAnalyzer.fromSystemProperties(recordStorage);
    }

    public RecommendationApplicationService(RecommendationCompanionService helper) {
        this(helper, AgentPlatformFurtherStudyCompanionAnalyzer.fromSystemProperties(),
                new InMemoryStudyCenterStorage(), new v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryFurtherStudyCompanionStorage());
    }

    public RecommendationApplicationService(FurtherStudyCompanionAnalyzer analyzer) {
        this(new RecommendationCompanionService(), analyzer, new InMemoryStudyCenterStorage(), new v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryFurtherStudyCompanionStorage());
    }

    public RecommendationApplicationService(FurtherStudyCompanionAnalyzer analyzer,
                                            StudyCenterStorage draftStorage) {
        this(new RecommendationCompanionService(), analyzer, draftStorage, CyanCruiseStorageFactory.furtherStudyCompanionStorage());
    }

    private RecommendationApplicationService(RecommendationCompanionService helper,
                                             FurtherStudyCompanionAnalyzer analyzer,
                                             StudyCenterStorage draftStorage, FurtherStudyCompanionStorage recordStorage) {
        this.helper = helper;
        this.analyzer = analyzer;
        this.draftStorage = draftStorage;
        this.recordStorage = recordStorage;
    }

    public RecommendationDiagnosisResult diagnose(String userId, RecommendationProfileRequest request) {
        return analyze(userId, FurtherStudyCompanionAnalyzer.RECOMMENDATION_DIAGNOSE,
                request, RecommendationDiagnosisResult.class);
    }

    public RecommendationPlanResult generatePlan(String userId, RecommendationProfileRequest request) {
        return analyze(userId, FurtherStudyCompanionAnalyzer.RECOMMENDATION_PLAN_GENERATE,
                request, RecommendationPlanResult.class);
    }

    public RecommendationDocumentPolishResult polishDocument(String userId, RecommendationDocumentPolishRequest request) {
        return analyze(userId, FurtherStudyCompanionAnalyzer.RECOMMENDATION_DOCUMENT_POLISH,
                request, RecommendationDocumentPolishResult.class);
    }

    public RecommendationTutorLetterResult generateTutorLetter(String userId, RecommendationTutorLetterRequest request) {
        return analyze(userId, FurtherStudyCompanionAnalyzer.RECOMMENDATION_TUTOR_LETTER,
                request, RecommendationTutorLetterResult.class);
    }

    private <T> T analyze(String userId, String taskType, Object request, Class<T> resultType) {
        String safeUserId = requireUserId(userId);
        if (request == null) {
            throw new IllegalArgumentException("请填写本次保研分析所需信息。");
        }
        if (analyzer == null) {
            throw unavailable();
        }
        FurtherStudyAnalysisDraftDto draft = new FurtherStudyAnalysisDraftDto();
        draft.setTaskType(taskType);
        draft.setPayloadJson(FurtherStudyRecordSupport.toJson(request));
        draft.setUpdatedAt(LocalDateTime.now());
        draftStorage.saveAnalysisDraft(safeUserId, draft);
        T result = analyzer.analyze(safeUserId, taskType, request, resultType);
        FurtherStudyRecordSupport.saveAnalysis(recordStorage, safeUserId, "RECOMMENDATION", taskType, request, result);
        return result;
    }

    private String requireUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("请先确认当前登录身份，再使用保研陪伴功能。");
        }
        return userId.trim();
    }

    private IllegalStateException unavailable() {
        return new IllegalStateException("保研陪伴真实智能服务尚未接通，系统不会生成规则兜底结果，请稍后重试。");
    }
}
