package v620.cc001.cloud01.app01.mservice.ai;

import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import v620.cc001.base.common.dto.career.CareerPlanPhaseDto;

/** Validates that postgraduate route horizons describe twelve continuous months. */
public final class PostgraduateRouteCoverage {
    private static final Pattern CHINESE_RELATIVE = Pattern.compile("(?:第)?(\\d{1,2})-(\\d{1,2})个?月");
    private static final Pattern ENGLISH_RELATIVE = Pattern.compile("months?(\\d{1,2})-(\\d{1,2})");
    private static final Pattern YEAR_MONTH = Pattern.compile("(20\\d{2})(?:-|/|年)(1[0-2]|0?[1-9])");

    private PostgraduateRouteCoverage() {
    }

    public static boolean coversTwelveContinuousMonths(List<CareerPlanPhaseDto> phases) {
        if (phases == null || phases.size() < 3) return false;
        List<MonthRange> relative = new ArrayList<MonthRange>();
        List<MonthRange> dated = new ArrayList<MonthRange>();
        for (CareerPlanPhaseDto phase : phases) {
            String horizon = phase == null ? null : phase.getHorizon();
            MonthRange relativeRange = relativeRange(horizon);
            MonthRange datedRange = datedRange(horizon);
            if (relativeRange != null) relative.add(relativeRange);
            if (datedRange != null) dated.add(datedRange);
        }
        if (relative.size() == phases.size() && continuous(relative)
                && relative.get(0).start == 1
                && relative.get(relative.size() - 1).end >= 12) {
            return true;
        }
        if (dated.size() != phases.size() || !continuous(dated)) return false;
        MonthRange first = dated.get(0);
        MonthRange last = dated.get(dated.size() - 1);
        return ChronoUnit.MONTHS.between(first.startMonth, last.endMonth) >= 11;
    }

    private static MonthRange relativeRange(String horizon) {
        if (!hasText(horizon)) return null;
        String normalized = normalize(horizon);
        Matcher matcher = CHINESE_RELATIVE.matcher(normalized);
        if (!matcher.find()) {
            matcher = ENGLISH_RELATIVE.matcher(normalized.toLowerCase());
            if (!matcher.find()) return null;
        }
        int start = integer(matcher.group(1));
        int end = integer(matcher.group(2));
        return start > 0 && end >= start ? MonthRange.relative(start, end) : null;
    }

    private static MonthRange datedRange(String horizon) {
        if (!hasText(horizon)) return null;
        Matcher matcher = YEAR_MONTH.matcher(normalize(horizon));
        List<YearMonth> values = new ArrayList<YearMonth>();
        while (matcher.find()) {
            values.add(YearMonth.of(integer(matcher.group(1)), integer(matcher.group(2))));
        }
        if (values.isEmpty()) return null;
        YearMonth start = values.get(0);
        YearMonth end = values.size() > 1 ? values.get(values.size() - 1) : start;
        return end.isBefore(start) ? null : MonthRange.dated(start, end);
    }

    private static boolean continuous(List<MonthRange> ranges) {
        if (ranges == null || ranges.isEmpty()) return false;
        for (int index = 0; index < ranges.size(); index += 1) {
            MonthRange range = ranges.get(index);
            if (range == null) return false;
            if (index == 0) continue;
            MonthRange previous = ranges.get(index - 1);
            if (range.relative) {
                if (!previous.relative || range.start != previous.end + 1) return false;
            } else if (previous.relative || !range.startMonth.equals(previous.endMonth.plusMonths(1))) {
                return false;
            }
        }
        return true;
    }

    private static String normalize(String value) {
        return value.trim().replace('—', '-').replace('–', '-').replace('－', '-')
                .replace("至", "-").replace("到", "-").replaceAll("\\s+", "");
    }

    private static int integer(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) {
            return -1;
        }
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private static final class MonthRange {
        private final boolean relative;
        private final int start;
        private final int end;
        private final YearMonth startMonth;
        private final YearMonth endMonth;

        private MonthRange(boolean relative, int start, int end, YearMonth startMonth, YearMonth endMonth) {
            this.relative = relative;
            this.start = start;
            this.end = end;
            this.startMonth = startMonth;
            this.endMonth = endMonth;
        }

        private static MonthRange relative(int start, int end) {
            return new MonthRange(true, start, end, null, null);
        }

        private static MonthRange dated(YearMonth start, YearMonth end) {
            return new MonthRange(false, 0, 0, start, end);
        }
    }
}
