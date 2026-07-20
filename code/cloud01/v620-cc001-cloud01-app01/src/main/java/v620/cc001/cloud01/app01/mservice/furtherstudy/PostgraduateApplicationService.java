package v620.cc001.cloud01.app01.mservice.furtherstudy;

import v620.base.helper.furtherstudy.PostgraduateCompanionService;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeAnalysisResult;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeAnalyzeRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduatePlanRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduatePlanResult;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateReexamPreparationResult;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateReexamPrepareRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateSchoolRecommendRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateSchoolRecommendationResult;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyAnalysisDraftDto;
import v620.cc001.cloud01.app01.mservice.ai.FurtherStudyCompanionAnalyzer;
import v620.cc001.cloud01.app01.mservice.ai.impl.AgentPlatformFurtherStudyCompanionAnalyzer;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;
import v620.cc001.cloud01.app01.mservice.storage.StudyCenterStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryStudyCenterStorage;

import java.time.LocalDateTime;

/** Application boundary for postgraduate exam companion. */
public class PostgraduateApplicationService {

    private final PostgraduateCompanionService helper;
    private final FurtherStudyCompanionAnalyzer analyzer;
    private final StudyCenterStorage draftStorage;
    private final FurtherStudyCompanionStorage recordStorage;

    public PostgraduateApplicationService() {
        this.helper = new PostgraduateCompanionService();
        this.draftStorage = CyanCruiseStorageFactory.studyCenterStorage();
        this.recordStorage = CyanCruiseStorageFactory.furtherStudyCompanionStorage();
        this.analyzer = AgentPlatformFurtherStudyCompanionAnalyzer.fromSystemProperties(recordStorage);
    }

    public PostgraduateApplicationService(PostgraduateCompanionService helper) {
        this(helper, AgentPlatformFurtherStudyCompanionAnalyzer.fromSystemProperties(),
                new InMemoryStudyCenterStorage(), new v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryFurtherStudyCompanionStorage());
    }

    public PostgraduateApplicationService(FurtherStudyCompanionAnalyzer analyzer) {
        this(new PostgraduateCompanionService(), analyzer, new InMemoryStudyCenterStorage(), new v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryFurtherStudyCompanionStorage());
    }

    public PostgraduateApplicationService(FurtherStudyCompanionAnalyzer analyzer, StudyCenterStorage draftStorage) {
        this(new PostgraduateCompanionService(), analyzer, draftStorage, CyanCruiseStorageFactory.furtherStudyCompanionStorage());
    }

    private PostgraduateApplicationService(PostgraduateCompanionService helper,
                                           FurtherStudyCompanionAnalyzer analyzer,
                                           StudyCenterStorage draftStorage, FurtherStudyCompanionStorage recordStorage) {
        this.helper = helper;
        this.analyzer = analyzer;
        this.draftStorage = draftStorage;
        this.recordStorage = recordStorage;
    }

    public PostgraduateSchoolRecommendationResult recommendSchools(String userId, PostgraduateSchoolRecommendRequest request) {
        return analyze(userId, FurtherStudyCompanionAnalyzer.POSTGRADUATE_SCHOOL_RECOMMEND,
                request, PostgraduateSchoolRecommendationResult.class);
    }

    public PostgraduatePlanResult generatePlan(String userId, PostgraduatePlanRequest request) {
        return analyze(userId, FurtherStudyCompanionAnalyzer.POSTGRADUATE_PLAN_GENERATE,
                request, PostgraduatePlanResult.class);
    }

    public PostgraduateMistakeAnalysisResult analyzeMistake(String userId, PostgraduateMistakeAnalyzeRequest request) {
        return analyze(userId, FurtherStudyCompanionAnalyzer.POSTGRADUATE_MISTAKE_ANALYZE,
                request, PostgraduateMistakeAnalysisResult.class);
    }

    public PostgraduateReexamPreparationResult prepareReexam(String userId, PostgraduateReexamPrepareRequest request) {
        return analyze(userId, FurtherStudyCompanionAnalyzer.POSTGRADUATE_REEXAM_PREPARE,
                request, PostgraduateReexamPreparationResult.class);
    }

    private <T> T analyze(String userId, String taskType, Object request, Class<T> resultType) {
        String safeUserId = requireUserId(userId);
        if (request == null) {
            throw new IllegalArgumentException("请填写本次考研分析所需信息。");
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
        FurtherStudyRecordSupport.saveAnalysis(recordStorage, safeUserId, "POSTGRADUATE", taskType, request, result);
        return result;
    }

    private String requireUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("请先确认当前登录身份，再使用考研陪伴功能。");
        }
        return userId.trim();
    }

    private IllegalStateException unavailable() {
        return new IllegalStateException("考研陪伴真实智能服务尚未接通，系统不会生成规则兜底结果，请稍后重试。");
    }
}
