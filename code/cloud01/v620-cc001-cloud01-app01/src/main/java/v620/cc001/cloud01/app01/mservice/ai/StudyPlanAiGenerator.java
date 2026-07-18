package v620.cc001.cloud01.app01.mservice.ai;

import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialDto;
import java.util.List;

/** Generates a study plan for one explicitly selected study direction. */
public interface StudyPlanAiGenerator {
    CareerPlanRecordDto generate(String userId, String direction, String targetSchool,
                                 CareerUserProfileDto profile);

    default CareerPlanRecordDto generate(String userId, String direction, String targetSchool,
                                         CareerUserProfileDto profile,
                                         CareerPlanRecordDto existingPlan,
                                         List<StudyPlanningMaterialDto> materials) {
        return generate(userId, direction, targetSchool, profile);
    }

    default CareerPlanRecordDto generate(String userId, String direction, String targetSchool,
                                         CareerUserProfileDto profile,
                                         UserProfileSnapshot snapshot,
                                         CareerPlanRecordDto existingPlan,
                                         List<StudyPlanningMaterialDto> materials) {
        return generate(userId, direction, targetSchool, profile, existingPlan, materials);
    }
}
