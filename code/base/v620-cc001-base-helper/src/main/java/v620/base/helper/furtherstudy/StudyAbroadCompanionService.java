package v620.base.helper.furtherstudy;

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

/** Compatibility boundary retained until the real study-abroad Agent is connected. */
public class StudyAbroadCompanionService {
    public StudyAbroadProfileDiagnosisResult diagnoseProfile(StudyAbroadProfileRequest request) {
        throw unavailable();
    }

    public StudyAbroadLanguagePlanResult generateLanguagePlan(StudyAbroadLanguagePlanRequest request) {
        throw unavailable();
    }

    public StudyAbroadSchoolPositionResult positionSchools(StudyAbroadSchoolPositionRequest request) {
        throw unavailable();
    }

    public StudyAbroadStatementOutlineResult buildStatementOutline(StudyAbroadStatementRequest request) {
        throw unavailable();
    }

    public StudyAbroadVisaChecklistResult buildVisaChecklist(StudyAbroadVisaChecklistRequest request) {
        throw unavailable();
    }

    private IllegalStateException unavailable() {
        return new IllegalStateException("留学陪伴真实智能服务尚未接通，请稍后重试。");
    }
}
