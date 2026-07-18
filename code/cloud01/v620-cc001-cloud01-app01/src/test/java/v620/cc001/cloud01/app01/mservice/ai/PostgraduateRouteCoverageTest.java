package v620.cc001.cloud01.app01.mservice.ai;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.CareerPlanPhaseDto;

class PostgraduateRouteCoverageTest {

    @Test
    void acceptsContinuousRelativeTwelveMonthRanges() {
        assertTrue(PostgraduateRouteCoverage.coversTwelveContinuousMonths(Arrays.asList(
                phase("第1—2个月（2026年7月—2026年8月）"),
                phase("第3—6个月（2026年9月—2026年12月）"),
                phase("第7—12个月（2027年1月—2027年6月）"))));
    }

    @Test
    void acceptsContinuousActualYearMonthRanges() {
        assertTrue(PostgraduateRouteCoverage.coversTwelveContinuousMonths(Arrays.asList(
                phase("2026年7月—2026年8月"),
                phase("2026年9月—2026年12月"),
                phase("2027年1月—2027年6月"))));
    }

    @Test
    void rejectsDecemberDateWhenRouteDoesNotCoverTwelveMonths() {
        assertFalse(PostgraduateRouteCoverage.coversTwelveContinuousMonths(Arrays.asList(
                phase("现在-1个月"),
                phase("2026-09-01 至 2026-12-31"),
                phase("2027-01-01 至 2027-04-30"))));
    }

    private CareerPlanPhaseDto phase(String horizon) {
        CareerPlanPhaseDto phase = new CareerPlanPhaseDto();
        phase.setHorizon(horizon);
        return phase;
    }
}
