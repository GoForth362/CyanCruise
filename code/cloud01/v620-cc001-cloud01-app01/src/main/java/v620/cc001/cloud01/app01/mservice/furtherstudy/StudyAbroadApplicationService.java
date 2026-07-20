package v620.cc001.cloud01.app01.mservice.furtherstudy;

import v620.base.helper.furtherstudy.StudyAbroadCompanionService;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadLanguagePlanRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadLanguagePlanResult;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadProfileDiagnosisResult;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadProfileRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadSchoolPositionRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadSchoolPositionResult;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadStatementOutlineResult;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadStatementRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadVisaChecklistRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadVisaChecklistResult;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyAnalysisDraftDto;
import v620.cc001.cloud01.app01.mservice.ai.FurtherStudyCompanionAnalyzer;
import v620.cc001.cloud01.app01.mservice.ai.impl.AgentPlatformFurtherStudyCompanionAnalyzer;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;
import v620.cc001.cloud01.app01.mservice.storage.StudyCenterStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryStudyCenterStorage;

import java.time.LocalDateTime;

/** Application boundary for study abroad companion. */
public class StudyAbroadApplicationService {

    private final StudyAbroadCompanionService helper;
    private final FurtherStudyCompanionAnalyzer analyzer;
    private final StudyCenterStorage draftStorage;
    private final FurtherStudyCompanionStorage recordStorage;

    public StudyAbroadApplicationService() {
        this.helper = new StudyAbroadCompanionService();
        this.draftStorage = CyanCruiseStorageFactory.studyCenterStorage();
        this.recordStorage = CyanCruiseStorageFactory.furtherStudyCompanionStorage();
        this.analyzer = AgentPlatformFurtherStudyCompanionAnalyzer.fromSystemProperties(recordStorage);
    }

    public StudyAbroadApplicationService(StudyAbroadCompanionService helper) {
        this(helper, AgentPlatformFurtherStudyCompanionAnalyzer.fromSystemProperties(),
                new InMemoryStudyCenterStorage(), new v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryFurtherStudyCompanionStorage());
    }

    public StudyAbroadApplicationService(FurtherStudyCompanionAnalyzer analyzer) {
        this(new StudyAbroadCompanionService(), analyzer, new InMemoryStudyCenterStorage(), new v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryFurtherStudyCompanionStorage());
    }

    public StudyAbroadApplicationService(FurtherStudyCompanionAnalyzer analyzer,
                                         StudyCenterStorage draftStorage) {
        this(new StudyAbroadCompanionService(), analyzer, draftStorage, CyanCruiseStorageFactory.furtherStudyCompanionStorage());
    }

    private StudyAbroadApplicationService(StudyAbroadCompanionService helper,
                                          FurtherStudyCompanionAnalyzer analyzer,
                                          StudyCenterStorage draftStorage, FurtherStudyCompanionStorage recordStorage) {
        this.helper = helper;
        this.analyzer = analyzer;
        this.draftStorage = draftStorage;
        this.recordStorage = recordStorage;
    }

    public StudyAbroadProfileDiagnosisResult diagnoseProfile(String userId, StudyAbroadProfileRequest request) {
        return analyze(userId, FurtherStudyCompanionAnalyzer.STUDY_ABROAD_PROFILE_DIAGNOSE,
                request, StudyAbroadProfileDiagnosisResult.class);
    }

    public StudyAbroadLanguagePlanResult generateLanguagePlan(String userId, StudyAbroadLanguagePlanRequest request) {
        return analyze(userId, FurtherStudyCompanionAnalyzer.STUDY_ABROAD_LANGUAGE_PLAN,
                request, StudyAbroadLanguagePlanResult.class);
    }

    public StudyAbroadSchoolPositionResult positionSchools(String userId, StudyAbroadSchoolPositionRequest request) {
        return analyze(userId, FurtherStudyCompanionAnalyzer.STUDY_ABROAD_SCHOOL_POSITION,
                request, StudyAbroadSchoolPositionResult.class);
    }

    public StudyAbroadStatementOutlineResult buildStatementOutline(String userId, StudyAbroadStatementRequest request) {
        return analyze(userId, FurtherStudyCompanionAnalyzer.STUDY_ABROAD_STATEMENT_OUTLINE,
                request, StudyAbroadStatementOutlineResult.class);
    }

    public StudyAbroadVisaChecklistResult buildVisaChecklist(String userId, StudyAbroadVisaChecklistRequest request) {
        return analyze(userId, FurtherStudyCompanionAnalyzer.STUDY_ABROAD_VISA_CHECKLIST,
                request, StudyAbroadVisaChecklistResult.class);
    }

    private <T> T analyze(String userId, String taskType, Object request, Class<T> resultType) {
        String safeUserId = requireUserId(userId);
        if (request == null) {
            throw new IllegalArgumentException("请填写本次留学分析所需信息。");
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
        FurtherStudyRecordSupport.saveAnalysis(recordStorage, safeUserId, "STUDY_ABROAD", taskType, request, result);
        return result;
    }

    private String requireUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("请先确认当前登录身份，再使用留学陪伴功能。");
        }
        return userId.trim();
    }

    private IllegalStateException unavailable() {
        return new IllegalStateException("留学陪伴真实智能服务尚未接通，系统不会生成规则兜底结果，请稍后重试。");
    }
}
