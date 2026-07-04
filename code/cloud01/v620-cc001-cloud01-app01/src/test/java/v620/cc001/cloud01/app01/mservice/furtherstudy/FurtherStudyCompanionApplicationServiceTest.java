package v620.cc001.cloud01.app01.mservice.furtherstudy;

import org.junit.jupiter.api.Test;
import v620.base.helper.furtherstudy.PostgraduateCompanionService;
import v620.base.helper.furtherstudy.RecommendationCompanionService;
import v620.base.helper.furtherstudy.StudyAbroadCompanionService;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeAnalyzeRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduatePlanRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateReexamPrepareRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateSchoolRecommendRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationDocumentPolishRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationProfileRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationTutorLetterRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadLanguagePlanRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadProfileRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadSchoolPositionRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadStatementRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadVisaChecklistRequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FurtherStudyCompanionApplicationServiceTest {

    @Test
    void postgraduateCompanionGeneratesAllCoreResults() {
        PostgraduateApplicationService service =
                new PostgraduateApplicationService(new PostgraduateCompanionService());

        PostgraduateSchoolRecommendRequest school = new PostgraduateSchoolRecommendRequest();
        school.setTargetMajor("Computer Science");
        assertNotNull(service.recommendSchools("u1", school));

        PostgraduatePlanRequest plan = new PostgraduatePlanRequest();
        plan.setTargetSchool("Example University");
        plan.setTargetMajor("Computer Science");
        plan.setExamDate("2026-12-20");
        assertNotNull(service.generatePlan("u1", plan));

        PostgraduateMistakeAnalyzeRequest mistake = new PostgraduateMistakeAnalyzeRequest();
        mistake.setSubject("math");
        mistake.setQuestionText("1+1=?");
        mistake.setWrongAnswer("3");
        assertNotNull(service.analyzeMistake("u1", mistake));

        PostgraduateReexamPrepareRequest reexam = new PostgraduateReexamPrepareRequest();
        reexam.setTargetSchool("Example University");
        assertNotNull(service.prepareReexam("u1", reexam));
    }

    @Test
    void recommendationCompanionGeneratesAllCoreResults() {
        RecommendationApplicationService service =
                new RecommendationApplicationService(new RecommendationCompanionService());
        RecommendationProfileRequest profile = new RecommendationProfileRequest();
        profile.setTargetSchools("Example University");
        profile.setTargetMajor("Computer Science");

        assertNotNull(service.diagnose("u1", profile));
        assertNotNull(service.generatePlan("u1", profile));

        RecommendationDocumentPolishRequest document = new RecommendationDocumentPolishRequest();
        document.setTargetMajor("Computer Science");
        document.setDocumentType("statement");
        document.setDraft("I joined a research project.");
        assertNotNull(service.polishDocument("u1", document));

        RecommendationTutorLetterRequest tutor = new RecommendationTutorLetterRequest();
        tutor.setTargetSchool("Example University");
        tutor.setTargetMajor("Computer Science");
        tutor.setTutorName("Professor A");
        assertNotNull(service.generateTutorLetter("u1", tutor));
    }

    @Test
    void studyAbroadCompanionGeneratesAllCoreResults() {
        StudyAbroadApplicationService service =
                new StudyAbroadApplicationService(new StudyAbroadCompanionService());

        StudyAbroadProfileRequest profile = new StudyAbroadProfileRequest();
        profile.setCountryOrRegion("UK");
        profile.setTargetMajor("Computer Science");
        assertNotNull(service.diagnoseProfile("u1", profile));

        StudyAbroadLanguagePlanRequest language = new StudyAbroadLanguagePlanRequest();
        language.setExamType("IELTS");
        language.setExamDate("2026-09-01");
        assertNotNull(service.generateLanguagePlan("u1", language));

        StudyAbroadSchoolPositionRequest school = new StudyAbroadSchoolPositionRequest();
        school.setCountryOrRegion("UK");
        school.setTargetMajor("Computer Science");
        assertNotNull(service.positionSchools("u1", school));

        StudyAbroadStatementRequest statement = new StudyAbroadStatementRequest();
        statement.setTargetMajor("Computer Science");
        statement.setPersonalStory("I built a course project.");
        assertNotNull(service.buildStatementOutline("u1", statement));

        StudyAbroadVisaChecklistRequest visa = new StudyAbroadVisaChecklistRequest();
        visa.setCountryOrRegion("UK");
        assertNotNull(service.buildVisaChecklist("u1", visa));
    }
}
