package v620.cc001.cloud01.app01.mservice.furtherstudy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import v620.base.helper.furtherstudy.PostgraduateCompanionService;
import v620.base.helper.furtherstudy.RecommendationCompanionService;
import v620.base.helper.furtherstudy.StudyAbroadCompanionService;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeAnalyzeRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduatePlanRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateReexamPrepareRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateSchoolRecommendRequest;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyAnalysisDraftDto;
import v620.cc001.base.common.dto.furtherstudy.RecommendationDocumentPolishRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationProfileRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationTutorLetterRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadLanguagePlanRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadProfileRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadSchoolPositionRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadStatementRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadVisaChecklistRequest;
import v620.cc001.cloud01.app01.mservice.ai.FurtherStudyCompanionAnalyzer;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryStudyCenterStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FurtherStudyCompanionApplicationServiceTest {

    @Test
    void postgraduateDoesNotReturnRuleGeneratedResult() {
        PostgraduateApplicationService service =
                new PostgraduateApplicationService(new PostgraduateCompanionService());
        assertThrows(IllegalStateException.class,
                () -> service.generatePlan("u1", new PostgraduatePlanRequest()));
    }

    @Test
    void recommendationDoesNotReturnRuleGeneratedResult() {
        RecommendationApplicationService service =
                new RecommendationApplicationService(new RecommendationCompanionService());
        assertThrows(IllegalStateException.class,
                () -> service.generatePlan("u1", new RecommendationProfileRequest()));
    }

    @Test
    void studyAbroadDoesNotReturnRuleGeneratedResult() {
        StudyAbroadApplicationService service =
                new StudyAbroadApplicationService(new StudyAbroadCompanionService());
        assertThrows(IllegalStateException.class,
                () -> service.diagnoseProfile("u1", new StudyAbroadProfileRequest()));
    }

    @Test
    void mapsAllThirteenOperationsToThePublishedCompanionContract() {
        CapturingAnalyzer analyzer = new CapturingAnalyzer();
        PostgraduateApplicationService postgraduate = new PostgraduateApplicationService(analyzer);
        RecommendationApplicationService recommendation = new RecommendationApplicationService(analyzer);
        StudyAbroadApplicationService abroad = new StudyAbroadApplicationService(analyzer);

        postgraduate.recommendSchools("u1", new PostgraduateSchoolRecommendRequest());
        postgraduate.generatePlan("u1", new PostgraduatePlanRequest());
        postgraduate.analyzeMistake("u1", new PostgraduateMistakeAnalyzeRequest());
        postgraduate.prepareReexam("u1", new PostgraduateReexamPrepareRequest());
        recommendation.diagnose("u1", new RecommendationProfileRequest());
        recommendation.generatePlan("u1", new RecommendationProfileRequest());
        recommendation.polishDocument("u1", new RecommendationDocumentPolishRequest());
        recommendation.generateTutorLetter("u1", new RecommendationTutorLetterRequest());
        abroad.diagnoseProfile("u1", new StudyAbroadProfileRequest());
        abroad.generateLanguagePlan("u1", new StudyAbroadLanguagePlanRequest());
        abroad.positionSchools("u1", new StudyAbroadSchoolPositionRequest());
        abroad.buildStatementOutline("u1", new StudyAbroadStatementRequest());
        abroad.buildVisaChecklist("u1", new StudyAbroadVisaChecklistRequest());

        assertEquals(Arrays.asList(
                FurtherStudyCompanionAnalyzer.POSTGRADUATE_SCHOOL_RECOMMEND,
                FurtherStudyCompanionAnalyzer.POSTGRADUATE_PLAN_GENERATE,
                FurtherStudyCompanionAnalyzer.POSTGRADUATE_MISTAKE_ANALYZE,
                FurtherStudyCompanionAnalyzer.POSTGRADUATE_REEXAM_PREPARE,
                FurtherStudyCompanionAnalyzer.RECOMMENDATION_DIAGNOSE,
                FurtherStudyCompanionAnalyzer.RECOMMENDATION_PLAN_GENERATE,
                FurtherStudyCompanionAnalyzer.RECOMMENDATION_DOCUMENT_POLISH,
                FurtherStudyCompanionAnalyzer.RECOMMENDATION_TUTOR_LETTER,
                FurtherStudyCompanionAnalyzer.STUDY_ABROAD_PROFILE_DIAGNOSE,
                FurtherStudyCompanionAnalyzer.STUDY_ABROAD_LANGUAGE_PLAN,
                FurtherStudyCompanionAnalyzer.STUDY_ABROAD_SCHOOL_POSITION,
                FurtherStudyCompanionAnalyzer.STUDY_ABROAD_STATEMENT_OUTLINE,
                FurtherStudyCompanionAnalyzer.STUDY_ABROAD_VISA_CHECKLIST), analyzer.taskTypes);
    }

    @Test
    void persistsSchoolRecommendationDraftBeforeAgentFailureAndIsolatesUsers() {
        InMemoryStudyCenterStorage storage = new InMemoryStudyCenterStorage();
        FurtherStudyCompanionAnalyzer failingAnalyzer = new FurtherStudyCompanionAnalyzer() {
            @Override
            public <T> T analyze(String userId, String taskType, Object payload, Class<T> resultType) {
                throw new IllegalStateException("agent failed");
            }
        };
        PostgraduateApplicationService service =
                new PostgraduateApplicationService(failingAnalyzer, storage);
        PostgraduateSchoolRecommendRequest request = new PostgraduateSchoolRecommendRequest();
        request.setUndergraduateSchool("成都理工大学");
        request.setUndergraduateLevel("普通本科");
        request.setGpa("3.6");
        request.setEnglishLevel("四级");
        request.setPreferredRegion("成都");
        request.setTargetMajor("计算机");

        assertThrows(IllegalStateException.class,
                () -> service.recommendSchools("user-a", request));

        FurtherStudyAnalysisDraftDto draft = storage.loadAnalysisDraft("user-a",
                FurtherStudyCompanionAnalyzer.POSTGRADUATE_SCHOOL_RECOMMEND);
        assertEquals(FurtherStudyCompanionAnalyzer.POSTGRADUATE_SCHOOL_RECOMMEND,
                draft.getTaskType());
        assertTrue(draft.getPayloadJson().contains("成都理工大学"));
        assertTrue(draft.getPayloadJson().contains("\"gpa\":\"3.6\""));
        assertNull(storage.loadAnalysisDraft("user-b",
                FurtherStudyCompanionAnalyzer.POSTGRADUATE_SCHOOL_RECOMMEND));
        assertNull(storage.loadAnalysisDraft("user-a",
                FurtherStudyCompanionAnalyzer.POSTGRADUATE_PLAN_GENERATE));
    }

    @Test
    void persistsRecommendationAndStudyAbroadDraftsBeforeAgentFailure() {
        InMemoryStudyCenterStorage storage = new InMemoryStudyCenterStorage();
        FurtherStudyCompanionAnalyzer failingAnalyzer = new FurtherStudyCompanionAnalyzer() {
            @Override
            public <T> T analyze(String userId, String taskType, Object payload, Class<T> resultType) {
                throw new IllegalStateException("agent failed");
            }
        };
        RecommendationApplicationService recommendation =
                new RecommendationApplicationService(failingAnalyzer, storage);
        StudyAbroadApplicationService abroad =
                new StudyAbroadApplicationService(failingAnalyzer, storage);
        RecommendationProfileRequest profile = new RecommendationProfileRequest();
        profile.setSchool("成都理工大学");
        profile.setRank("前 8%");
        StudyAbroadLanguagePlanRequest language = new StudyAbroadLanguagePlanRequest();
        language.setExamType("雅思");
        language.setTargetScore("7.0");

        assertThrows(IllegalStateException.class, () -> recommendation.diagnose("user-a", profile));
        assertThrows(IllegalStateException.class, () -> abroad.generateLanguagePlan("user-a", language));

        FurtherStudyAnalysisDraftDto recommendationDraft = storage.loadAnalysisDraft("user-a",
                FurtherStudyCompanionAnalyzer.RECOMMENDATION_DIAGNOSE);
        FurtherStudyAnalysisDraftDto languageDraft = storage.loadAnalysisDraft("user-a",
                FurtherStudyCompanionAnalyzer.STUDY_ABROAD_LANGUAGE_PLAN);
        assertTrue(recommendationDraft.getPayloadJson().contains("成都理工大学"));
        assertTrue(recommendationDraft.getPayloadJson().contains("前 8%"));
        assertTrue(languageDraft.getPayloadJson().contains("雅思"));
        assertTrue(languageDraft.getPayloadJson().contains("7.0"));
        assertNull(storage.loadAnalysisDraft("user-b",
                FurtherStudyCompanionAnalyzer.RECOMMENDATION_DIAGNOSE));
    }

    private static class CapturingAnalyzer implements FurtherStudyCompanionAnalyzer {
        private final List<String> taskTypes = new ArrayList<String>();

        @Override
        public <T> T analyze(String userId, String taskType, Object payload, Class<T> resultType) {
            taskTypes.add(taskType);
            try {
                return resultType.getDeclaredConstructor().newInstance();
            } catch (Exception error) {
                throw new IllegalStateException(error);
            }
        }
    }
}
