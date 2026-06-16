package v620.base.helper.career;

import v620.cc001.base.common.dto.career.CareerResourceCardDto;
import v620.cc001.base.common.dto.career.CareerResourceFeedDto;
import v620.cc001.base.common.dto.career.EmploymentInsightDto;
import v620.cc001.base.common.dto.career.EmploymentInsightProfileContext;
import v620.cc001.base.common.dto.career.EmploymentInsightRecordDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Pure Java rules for source-backed employment insight and resource feed shaping.
 */
public class EmploymentInsightsResourcesService {

    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_MISSING_SCHOOL = "MISSING_SCHOOL";
    public static final String STATUS_UNSUPPORTED_SCHOOL = "UNSUPPORTED_SCHOOL";
    public static final String STATUS_MISSING_TARGET_ROLE = "MISSING_TARGET_ROLE";
    public static final String STATUS_NO_SOURCES = "NO_SOURCES";
    public static final String STATUS_EMPTY = "EMPTY";

    public static final String COVERAGE_VERIFIED_FULL = "VERIFIED_FULL";
    public static final String COVERAGE_PARTIAL = "PARTIAL";
    public static final String COVERAGE_MISSING = "MISSING";
    public static final String COVERAGE_NEEDS_REVIEW = "NEEDS_MANUAL_REVIEW";

    private static final int SOURCE_LIMIT = 8;
    private static final int COVERAGE_YEAR_COUNT = 5;

    private static final List<String> SUPPORTED_SCHOOLS = Collections.unmodifiableList(Arrays.asList(
            "Sichuan University",
            "University of Electronic Science and Technology of China",
            "Southwest Jiaotong University",
            "Southwestern University of Finance and Economics",
            "Southwest Petroleum University",
            "Sichuan Agricultural University",
            "Chengdu University of Technology",
            "Chengdu University of Traditional Chinese Medicine"
    ));

    private static final Map<String, String> SCHOOL_ALIASES = new LinkedHashMap<String, String>();

    static {
        alias("Sichuan University", "scu", "sichuan university");
        alias("University of Electronic Science and Technology of China", "uestc", "electronic science", "dianzi keji");
        alias("Southwest Jiaotong University", "swjtu", "southwest jiaotong", "xinan jiaoda");
        alias("Southwestern University of Finance and Economics", "swufe", "southwestern finance", "xinan caijing");
        alias("Southwest Petroleum University", "swpu", "southwest petroleum", "xinan shiyou");
        alias("Sichuan Agricultural University", "sicau", "sichuan agricultural", "sichuan nongye");
        alias("Chengdu University of Technology", "cdut", "chengdu university of technology", "chengdu ligong");
        alias("Chengdu University of Traditional Chinese Medicine", "cdutcm", "chengdu university of traditional chinese medicine", "chengdu zhongyiyao");
    }

    public EmploymentInsightDto buildInsight(EmploymentInsightProfileContext context,
                                             List<EmploymentInsightRecordDto> records,
                                             LocalDateTime now) {
        EmploymentInsightProfileContext safeContext = context == null ? new EmploymentInsightProfileContext() : context;
        LocalDateTime safeNow = now == null ? LocalDateTime.now() : now;
        String major = firstText(safeContext.getMajor(), "Unknown major");
        String targetRole = firstText(safeContext.getTargetRole(), "Unknown target role");
        String normalizedSchool = normalizeSchool(safeContext.getSchool());

        if (!hasText(safeContext.getSchool())) {
            return unavailable(STATUS_MISSING_SCHOOL, "Unknown school", major, targetRole,
                    "School is required before source-backed employment insight can be generated.", safeNow);
        }
        if (!isSupportedSchool(normalizedSchool)) {
            return unavailable(STATUS_UNSUPPORTED_SCHOOL, safeContext.getSchool(), major, targetRole,
                    "This school is not connected to verified employment insight sources yet.", safeNow);
        }

        List<EmploymentInsightRecordDto> schoolRecords = filterBySchool(records, normalizedSchool);
        if (schoolRecords.isEmpty()) {
            return unavailable(STATUS_NO_SOURCES, normalizedSchool, major, targetRole,
                    "No traceable employment source is available for this school yet.", safeNow);
        }

        List<EmploymentInsightRecordDto> selected = selectRecords(schoolRecords, major, targetRole);
        EmploymentInsightDto result = new EmploymentInsightDto();
        result.setStatus(hasText(safeContext.getTargetRole()) ? STATUS_AVAILABLE : STATUS_MISSING_TARGET_ROLE);
        result.setSchool(normalizedSchool);
        result.setMajor(major);
        result.setTargetRole(targetRole);
        result.setSourceCount(Integer.valueOf(selected.size()));
        result.setUpdatedAt(latestFetchedAt(selected));
        result.setCoverage(buildCoverage(normalizedSchool, schoolRecords, safeNow));
        result.setTrend(buildTrend(schoolRecords));
        result.setSources(toSourceItems(selected));
        result.setLatestYear(latestYear(selected));
        result.setLatestEmploymentRate(latestMetric(selected, true));
        result.setLatestPostgraduateRate(latestMetric(selected, false));
        result.setDestinationHighlights(highlights(selected, safeContext.getTargetRole()));
        result.setMatchLabel(matchLabel(selected, major, targetRole));
        result.setSummary(summary(result));
        return result;
    }

    public CareerResourceFeedDto buildResourceFeed(List<CareerResourceCardDto> cards, String userId, LocalDateTime now) {
        CareerResourceFeedDto feed = new CareerResourceFeedDto();
        feed.setUpdatedAt(now == null ? LocalDateTime.now() : now);
        List<CareerResourceCardDto> ordered = orderedCards(cards, userId);
        for (CareerResourceCardDto card : ordered) {
            if (card == null) {
                continue;
            }
            String type = safeLower(card.getType());
            if ("article".equals(type)) {
                feed.getArticles().add(card);
            } else if ("video".equals(type)) {
                feed.getVideos().add(card);
            } else if ("consultation".equals(type) || "tip".equals(type)) {
                feed.getConsultations().add(card);
            } else if ("career_path".equals(type) || "career-path".equals(type)) {
                feed.getCareerPaths().add(card);
            }
        }
        int total = feed.getArticles().size() + feed.getVideos().size()
                + feed.getConsultations().size() + feed.getCareerPaths().size();
        if (total == 0) {
            feed.setStatus(STATUS_EMPTY);
            feed.setMessage("No configured CyanCruise resources are available.");
        } else {
            feed.setStatus(STATUS_AVAILABLE);
            feed.setMessage("CyanCruise resources are available.");
        }
        return feed;
    }

    public String normalizeSchool(String raw) {
        if (!hasText(raw)) {
            return null;
        }
        String value = raw.trim();
        for (String school : SUPPORTED_SCHOOLS) {
            if (value.equalsIgnoreCase(school) || value.toLowerCase(Locale.ROOT).contains(school.toLowerCase(Locale.ROOT))) {
                return school;
            }
        }
        String lower = value.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, String> entry : SCHOOL_ALIASES.entrySet()) {
            if (lower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return value;
    }

    public boolean isSupportedSchool(String school) {
        return SUPPORTED_SCHOOLS.contains(school);
    }

    public List<String> supportedSchools() {
        return new ArrayList<String>(SUPPORTED_SCHOOLS);
    }

    private static void alias(String school, String... aliases) {
        for (String value : aliases) {
            SCHOOL_ALIASES.put(value, school);
        }
    }

    private EmploymentInsightDto unavailable(String status, String school, String major, String targetRole,
                                             String summary, LocalDateTime now) {
        EmploymentInsightDto result = new EmploymentInsightDto();
        result.setStatus(status);
        result.setSchool(school);
        result.setMajor(major);
        result.setTargetRole(targetRole);
        result.setMatchLabel("Employment insight unavailable");
        result.setSummary(summary);
        result.setSourceCount(Integer.valueOf(0));
        result.setUpdatedAt(now);
        result.getDestinationHighlights().add(summary);
        return result;
    }

    private List<EmploymentInsightRecordDto> filterBySchool(List<EmploymentInsightRecordDto> records, String school) {
        List<EmploymentInsightRecordDto> out = new ArrayList<EmploymentInsightRecordDto>();
        if (records == null) {
            return out;
        }
        for (EmploymentInsightRecordDto record : records) {
            if (record != null && school.equals(normalizeSchool(record.getSchool()))) {
                out.add(record);
            }
        }
        return out;
    }

    private List<EmploymentInsightRecordDto> selectRecords(List<EmploymentInsightRecordDto> records, String major, String targetRole) {
        List<EmploymentInsightRecordDto> out = new ArrayList<EmploymentInsightRecordDto>(records);
        Collections.sort(out, new Comparator<EmploymentInsightRecordDto>() {
            public int compare(EmploymentInsightRecordDto left, EmploymentInsightRecordDto right) {
                int score = score(right, major, targetRole) - score(left, major, targetRole);
                if (score != 0) {
                    return score;
                }
                return safeInt(right.getYear()) - safeInt(left.getYear());
            }
        });
        if (out.size() > SOURCE_LIMIT) {
            return new ArrayList<EmploymentInsightRecordDto>(out.subList(0, SOURCE_LIMIT));
        }
        return out;
    }

    private int score(EmploymentInsightRecordDto record, String major, String targetRole) {
        int score = 0;
        if (containsAny(record.getMajorKeyword(), major)) {
            score += 3;
        }
        if (containsAny(record.getCareerKeyword(), targetRole)) {
            score += 3;
        }
        if (record.getEmploymentRate() != null || record.getPostgraduateRate() != null) {
            score += 2;
        }
        if (hasText(record.getDestinationSummary())) {
            score += 1;
        }
        if (containsIgnoreCase(record.getSourceType(), "official")) {
            score += 1;
        }
        return score;
    }

    private String matchLabel(List<EmploymentInsightRecordDto> records, String major, String targetRole) {
        for (EmploymentInsightRecordDto record : records) {
            if (score(record, major, targetRole) >= 4) {
                return "Matched to profile signals";
            }
        }
        return "Using school-level public sources";
    }

    private String summary(EmploymentInsightDto insight) {
        if (STATUS_MISSING_TARGET_ROLE.equals(insight.getStatus())) {
            return "Complete the target role to get role-specific employment insight. Current response only uses school-level sources.";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Based on ").append(insight.getSchool()).append(" public sources");
        if (hasText(insight.getMajor()) && !"Unknown major".equals(insight.getMajor())) {
            builder.append(" and major ").append(insight.getMajor());
        }
        if (hasText(insight.getTargetRole()) && !"Unknown target role".equals(insight.getTargetRole())) {
            builder.append(", target role ").append(insight.getTargetRole());
        }
        builder.append(".");
        if (insight.getLatestEmploymentRate() != null) {
            builder.append(" Latest employment placement rate: ").append(insight.getLatestEmploymentRate()).append("%.");
        }
        if (insight.getLatestPostgraduateRate() != null) {
            builder.append(" Latest postgraduate rate: ").append(insight.getLatestPostgraduateRate()).append("%.");
        }
        if (insight.getLatestEmploymentRate() == null && insight.getLatestPostgraduateRate() == null) {
            builder.append(" Numeric metrics are not available in traceable sources yet.");
        }
        return builder.toString();
    }

    private List<String> highlights(List<EmploymentInsightRecordDto> records, String targetRole) {
        List<String> out = new ArrayList<String>();
        for (EmploymentInsightRecordDto record : records) {
            if (hasText(record.getDestinationSummary())) {
                out.add(shortText(record.getDestinationSummary(), 160));
            }
            if (out.size() >= 4) {
                break;
            }
        }
        if (out.isEmpty()) {
            if (hasText(targetRole)) {
                out.add("Traceable sources do not contain destination highlights for the target role yet.");
            } else {
                out.add("Complete the target role before role-specific destination highlights are generated.");
            }
        }
        return out;
    }

    private List<EmploymentInsightDto.YearPoint> buildTrend(List<EmploymentInsightRecordDto> records) {
        Map<Integer, EmploymentInsightDto.YearPoint> byYear = new LinkedHashMap<Integer, EmploymentInsightDto.YearPoint>();
        List<EmploymentInsightRecordDto> ordered = new ArrayList<EmploymentInsightRecordDto>(records);
        Collections.sort(ordered, new Comparator<EmploymentInsightRecordDto>() {
            public int compare(EmploymentInsightRecordDto left, EmploymentInsightRecordDto right) {
                return safeInt(left.getYear()) - safeInt(right.getYear());
            }
        });
        for (EmploymentInsightRecordDto record : ordered) {
            if (record.getYear() == null || (record.getEmploymentRate() == null && record.getPostgraduateRate() == null)) {
                continue;
            }
            EmploymentInsightDto.YearPoint point = byYear.get(record.getYear());
            if (point == null) {
                point = new EmploymentInsightDto.YearPoint();
                point.setYear(record.getYear());
                byYear.put(record.getYear(), point);
            }
            if (record.getEmploymentRate() != null) {
                point.setEmploymentRate(record.getEmploymentRate());
            }
            if (record.getPostgraduateRate() != null) {
                point.setPostgraduateRate(record.getPostgraduateRate());
            }
        }
        return new ArrayList<EmploymentInsightDto.YearPoint>(byYear.values());
    }

    private List<EmploymentInsightDto.CoverageItem> buildCoverage(String school, List<EmploymentInsightRecordDto> records, LocalDateTime now) {
        List<EmploymentInsightDto.CoverageItem> out = new ArrayList<EmploymentInsightDto.CoverageItem>();
        int latestGraduateYear = now.getYear() - 1;
        for (int i = COVERAGE_YEAR_COUNT - 1; i >= 0; i--) {
            int year = latestGraduateYear - i;
            EmploymentInsightRecordDto best = bestCoverageRecord(records, year);
            EmploymentInsightDto.CoverageItem item = new EmploymentInsightDto.CoverageItem();
            item.setSchool(school);
            item.setYear(Integer.valueOf(year));
            if (best == null) {
                item.setStatus(COVERAGE_MISSING);
                item.setLabel("Missing");
                item.setReason("No verifiable public source was found for this school-year.");
            } else if (isVerifiedFull(best)) {
                item.setStatus(COVERAGE_VERIFIED_FULL);
                item.setLabel("Verified full");
                item.setReason("Official source includes metrics and destination description.");
                item.setSourceUrl(best.getSourceUrl());
            } else if (containsIgnoreCase(best.getSourceType(), "official")) {
                item.setStatus(COVERAGE_PARTIAL);
                item.setLabel("Partial");
                item.setReason("Official source exists, but core fields are incomplete.");
                item.setSourceUrl(best.getSourceUrl());
            } else {
                item.setStatus(COVERAGE_NEEDS_REVIEW);
                item.setLabel("Needs manual review");
                item.setReason("Only aggregate or incomplete source is available.");
                item.setSourceUrl(best.getSourceUrl());
            }
            out.add(item);
        }
        return out;
    }

    private EmploymentInsightRecordDto bestCoverageRecord(List<EmploymentInsightRecordDto> records, int year) {
        EmploymentInsightRecordDto best = null;
        int bestScore = -1;
        for (EmploymentInsightRecordDto record : records) {
            if (record.getYear() == null || record.getYear().intValue() != year) {
                continue;
            }
            int score = coverageScore(record);
            if (score > bestScore) {
                best = record;
                bestScore = score;
            }
        }
        return best;
    }

    private int coverageScore(EmploymentInsightRecordDto record) {
        int score = 0;
        if (containsIgnoreCase(record.getSourceType(), "official")) {
            score += 4;
        }
        if (record.getEmploymentRate() != null) {
            score += 2;
        }
        if (record.getPostgraduateRate() != null) {
            score += 1;
        }
        if (hasText(record.getDestinationSummary())) {
            score += 2;
        }
        return score;
    }

    private boolean isVerifiedFull(EmploymentInsightRecordDto record) {
        return containsIgnoreCase(record.getSourceType(), "official")
                && (record.getEmploymentRate() != null || record.getPostgraduateRate() != null)
                && hasText(record.getDestinationSummary());
    }

    private List<EmploymentInsightDto.SourceItem> toSourceItems(List<EmploymentInsightRecordDto> records) {
        List<EmploymentInsightDto.SourceItem> out = new ArrayList<EmploymentInsightDto.SourceItem>();
        for (EmploymentInsightRecordDto record : records) {
            EmploymentInsightDto.SourceItem item = new EmploymentInsightDto.SourceItem();
            item.setId(record.getId());
            item.setYear(record.getYear());
            item.setTitle(record.getSourceTitle());
            item.setUrl(record.getSourceUrl());
            item.setSourceType(record.getSourceType());
            item.setMajorKeyword(record.getMajorKeyword());
            item.setCareerKeyword(record.getCareerKeyword());
            item.setEmploymentRate(record.getEmploymentRate());
            item.setPostgraduateRate(record.getPostgraduateRate());
            item.setExcerpt(shortText(firstText(record.getDestinationSummary(), record.getRawExcerpt()), 240));
            item.setFetchedAt(record.getFetchedAt());
            out.add(item);
        }
        return out;
    }

    private LocalDateTime latestFetchedAt(List<EmploymentInsightRecordDto> records) {
        LocalDateTime latest = null;
        for (EmploymentInsightRecordDto record : records) {
            if (record.getFetchedAt() != null && (latest == null || record.getFetchedAt().isAfter(latest))) {
                latest = record.getFetchedAt();
            }
        }
        return latest;
    }

    private Integer latestYear(List<EmploymentInsightRecordDto> records) {
        Integer latest = null;
        for (EmploymentInsightRecordDto record : records) {
            if (record.getYear() != null && (latest == null || record.getYear().intValue() > latest.intValue())) {
                latest = record.getYear();
            }
        }
        return latest;
    }

    private BigDecimal latestMetric(List<EmploymentInsightRecordDto> records, boolean employment) {
        EmploymentInsightRecordDto best = null;
        for (EmploymentInsightRecordDto record : records) {
            BigDecimal value = employment ? record.getEmploymentRate() : record.getPostgraduateRate();
            if (value == null) {
                continue;
            }
            if (best == null || safeInt(record.getYear()) > safeInt(best.getYear())) {
                best = record;
            }
        }
        return best == null ? null : (employment ? best.getEmploymentRate() : best.getPostgraduateRate());
    }

    private List<CareerResourceCardDto> orderedCards(List<CareerResourceCardDto> cards, String userId) {
        List<CareerResourceCardDto> out = cards == null
                ? new ArrayList<CareerResourceCardDto>()
                : new ArrayList<CareerResourceCardDto>(cards);
        final int seed = hasText(userId) ? Math.abs(userId.trim().hashCode()) : 0;
        Collections.sort(out, new Comparator<CareerResourceCardDto>() {
            public int compare(CareerResourceCardDto left, CareerResourceCardDto right) {
                return stableRank(left, seed) - stableRank(right, seed);
            }
        });
        return out;
    }

    private int stableRank(CareerResourceCardDto card, int seed) {
        String id = card == null ? "" : firstText(card.getId(), card.getTitle(), "");
        return Math.abs((id + seed).hashCode());
    }

    private boolean containsAny(String source, String target) {
        if (!hasText(source) || !hasText(target)) {
            return false;
        }
        String lowerSource = source.toLowerCase(Locale.ROOT);
        String lowerTarget = target.toLowerCase(Locale.ROOT);
        return lowerSource.contains(lowerTarget) || lowerTarget.contains(lowerSource);
    }

    private boolean containsIgnoreCase(String value, String pattern) {
        return hasText(value) && hasText(pattern) && value.toLowerCase(Locale.ROOT).contains(pattern.toLowerCase(Locale.ROOT));
    }

    private String safeLower(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value.intValue();
    }

    private String firstText(String first, String second) {
        return hasText(first) ? first.trim() : second;
    }

    private String firstText(String first, String second, String third) {
        return hasText(first) ? first.trim() : firstText(second, third);
    }

    private String shortText(String value, int max) {
        String safe = value == null ? "" : value.trim().replaceAll("\\s+", " ");
        return safe.length() <= max ? safe : safe.substring(0, max - 1) + "...";
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
