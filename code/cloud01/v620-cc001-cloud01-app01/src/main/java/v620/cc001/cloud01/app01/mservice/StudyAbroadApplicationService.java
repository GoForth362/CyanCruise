package v620.cc001.cloud01.app01.mservice;

import v620.base.helper.career.StudyAbroadCompanionService;
import v620.cc001.base.common.dto.career.StudyAbroadLanguagePlanRequest;
import v620.cc001.base.common.dto.career.StudyAbroadLanguagePlanResult;
import v620.cc001.base.common.dto.career.StudyAbroadProfileDiagnosisResult;
import v620.cc001.base.common.dto.career.StudyAbroadProfileRequest;
import v620.cc001.base.common.dto.career.StudyAbroadSchoolPositionRequest;
import v620.cc001.base.common.dto.career.StudyAbroadSchoolPositionResult;
import v620.cc001.base.common.dto.career.StudyAbroadStatementOutlineResult;
import v620.cc001.base.common.dto.career.StudyAbroadStatementRequest;
import v620.cc001.base.common.dto.career.StudyAbroadVisaChecklistRequest;
import v620.cc001.base.common.dto.career.StudyAbroadVisaChecklistResult;

/** Application boundary for study abroad companion. */
public class StudyAbroadApplicationService {

    private final StudyAbroadCompanionService helper;

    public StudyAbroadApplicationService() {
        this(new StudyAbroadCompanionService());
    }

    public StudyAbroadApplicationService(StudyAbroadCompanionService helper) {
        this.helper = helper;
    }

    public StudyAbroadProfileDiagnosisResult diagnoseProfile(String userId, StudyAbroadProfileRequest request) {
        requireUserId(userId);
        return helper.diagnoseProfile(request);
    }

    public StudyAbroadLanguagePlanResult generateLanguagePlan(String userId, StudyAbroadLanguagePlanRequest request) {
        requireUserId(userId);
        return helper.generateLanguagePlan(request);
    }

    public StudyAbroadSchoolPositionResult positionSchools(String userId, StudyAbroadSchoolPositionRequest request) {
        requireUserId(userId);
        return helper.positionSchools(request);
    }

    public StudyAbroadStatementOutlineResult buildStatementOutline(String userId, StudyAbroadStatementRequest request) {
        requireUserId(userId);
        return helper.buildStatementOutline(request);
    }

    public StudyAbroadVisaChecklistResult buildVisaChecklist(String userId, StudyAbroadVisaChecklistRequest request) {
        requireUserId(userId);
        return helper.buildVisaChecklist(request);
    }

    private void requireUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("请先确认当前登录身份，再使用留学陪伴功能。");
        }
    }
}
