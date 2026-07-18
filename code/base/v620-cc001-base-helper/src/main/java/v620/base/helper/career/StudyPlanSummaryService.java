package v620.base.helper.career;

import java.time.LocalDateTime;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerPlanSummaryDto;
import v620.cc001.base.common.dto.career.CareerRouteContext;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;

/** Summarizes validated study plans without creating rule-based route content. */
public class StudyPlanSummaryService {
    private final CareerPlanSummaryService delegate = new CareerPlanSummaryService();

    public CareerPlanSummaryDto summarize(CareerPlanRecordDto plan, LocalDateTime now) {
        CareerPlanSummaryDto summary = delegate.summarize(plan, now);
        summary.setRouteType(CareerRouteContext.STUDY);
        if (plan != null) {
            summary.setStudyDirection(plan.getStudyDirection());
            summary.setTargetSchool(plan.getTargetSchool());
        }
        return summary;
    }

    /** Only trusted metadata may be supplied here; route content must come from the Agent. */
    public CareerPlanRecordDto enrich(CareerPlanRecordDto plan, String direction, String targetSchool,
                                      CareerUserProfileDto profile, LocalDateTime now) {
        if (plan == null) return null;
        plan.setRouteType(CareerRouteContext.STUDY);
        plan.setStudyDirection(direction);
        plan.setTargetSchool(targetSchool);
        if (plan.getGeneratedAt() == null) plan.setGeneratedAt(now == null ? LocalDateTime.now() : now);
        if (plan.getLastUpdatedAt() == null) plan.setLastUpdatedAt(now == null ? LocalDateTime.now() : now);
        if (plan.getVersion() == null) plan.setVersion(Integer.valueOf(1));
        return plan;
    }
}
