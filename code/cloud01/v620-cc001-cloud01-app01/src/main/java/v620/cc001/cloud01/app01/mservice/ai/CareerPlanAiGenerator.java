package v620.cc001.cloud01.app01.mservice.ai;

import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;

public interface CareerPlanAiGenerator {

    CareerPlanRecordDto generate(String userId, String targetRole, CareerUserProfileDto profile);
}
