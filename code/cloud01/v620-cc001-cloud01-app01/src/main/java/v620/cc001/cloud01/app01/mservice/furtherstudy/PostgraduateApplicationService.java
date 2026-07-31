package v620.cc001.cloud01.app01.mservice.furtherstudy;

import v620.base.helper.furtherstudy.PostgraduateCompanionService;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeAnalysisResult;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeAnalyzeRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeBookEntryDto;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeBookPageDto;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeBookQueryRequest;
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
    private final PostgraduateMistakeBookStorage mistakeBookStorage;

    public PostgraduateApplicationService() {
        this.helper = new PostgraduateCompanionService();
        this.draftStorage = CyanCruiseStorageFactory.studyCenterStorage();
        this.recordStorage = CyanCruiseStorageFactory.furtherStudyCompanionStorage();
        this.mistakeBookStorage = CyanCruiseStorageFactory.postgraduateMistakeBookStorage();
        this.analyzer = AgentPlatformFurtherStudyCompanionAnalyzer.fromSystemProperties();
    }

    public PostgraduateApplicationService(PostgraduateCompanionService helper) {
        this(helper, AgentPlatformFurtherStudyCompanionAnalyzer.fromSystemProperties(),
                new InMemoryStudyCenterStorage(), new v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryFurtherStudyCompanionStorage(), new v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryPostgraduateMistakeBookStorage());
    }

    public PostgraduateApplicationService(FurtherStudyCompanionAnalyzer analyzer) {
        this(new PostgraduateCompanionService(), analyzer, new InMemoryStudyCenterStorage(), new v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryFurtherStudyCompanionStorage(), new v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryPostgraduateMistakeBookStorage());
    }

    public PostgraduateApplicationService(FurtherStudyCompanionAnalyzer analyzer, StudyCenterStorage draftStorage) {
        this(new PostgraduateCompanionService(), analyzer, draftStorage, CyanCruiseStorageFactory.furtherStudyCompanionStorage(), CyanCruiseStorageFactory.postgraduateMistakeBookStorage());
    }

    PostgraduateApplicationService(PostgraduateCompanionService helper,
                                   FurtherStudyCompanionAnalyzer analyzer,
                                   StudyCenterStorage draftStorage, FurtherStudyCompanionStorage recordStorage, PostgraduateMistakeBookStorage mistakeBookStorage) {
        this.helper = helper;
        this.analyzer = analyzer;
        this.draftStorage = draftStorage;
        this.recordStorage = recordStorage;
        this.mistakeBookStorage = mistakeBookStorage;
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
        String safeUserId = requireUserId(userId);
        PostgraduateMistakeAnalysisResult result = analyze(safeUserId, FurtherStudyCompanionAnalyzer.POSTGRADUATE_MISTAKE_ANALYZE,
                request, PostgraduateMistakeAnalysisResult.class);
        PostgraduateMistakeBookEntryDto entry = new PostgraduateMistakeBookEntryDto();
        entry.setSubject(request == null ? null : request.getSubject());
        entry.setQuestionText(request == null ? null : request.getQuestionText());
        entry.setWrongAnswer(request == null ? null : request.getWrongAnswer());
        entry.setResultJson(FurtherStudyRecordSupport.toJson(result));
        mistakeBookStorage.save(safeUserId, entry);
        return result;
    }

    public PostgraduateMistakeBookPageDto listMistakeBook(String userId, PostgraduateMistakeBookQueryRequest request) {
        return mistakeBookStorage.list(requireUserId(userId), request);
    }

    public PostgraduateMistakeBookEntryDto loadMistakeBookEntry(String userId, String mistakeId) {
        PostgraduateMistakeBookEntryDto entry = mistakeBookStorage.load(requireUserId(userId), mistakeId);
        if (entry == null) throw new IllegalArgumentException("未找到这道错题，或无权查看该记录。");
        return entry;
    }

    public void deleteMistakeBookEntry(String userId, String mistakeId) {
        if (!mistakeBookStorage.delete(requireUserId(userId), mistakeId)) {
            throw new IllegalArgumentException("未找到这道错题，或无权移除该记录。");
        }
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
        if (!FurtherStudyCompanionAnalyzer.POSTGRADUATE_MISTAKE_ANALYZE.equals(taskType)) {
            FurtherStudyRecordSupport.saveAnalysis(recordStorage, safeUserId, "POSTGRADUATE", taskType, request, result);
        }
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
