package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Source-backed employment insight for the CareerLoop workbench.
 */
public class EmploymentInsightDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private String school;
    private String major;
    private String targetRole;
    private String matchLabel;
    private String summary;
    private BigDecimal latestEmploymentRate;
    private BigDecimal latestPostgraduateRate;
    private Integer latestYear;
    private Integer sourceCount;
    private LocalDateTime updatedAt;
    private List<String> destinationHighlights = new ArrayList<String>();
    private List<YearPoint> trend = new ArrayList<YearPoint>();
    private List<CoverageItem> coverage = new ArrayList<CoverageItem>();
    private List<SourceItem> sources = new ArrayList<SourceItem>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getMatchLabel() {
        return matchLabel;
    }

    public void setMatchLabel(String matchLabel) {
        this.matchLabel = matchLabel;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public BigDecimal getLatestEmploymentRate() {
        return latestEmploymentRate;
    }

    public void setLatestEmploymentRate(BigDecimal latestEmploymentRate) {
        this.latestEmploymentRate = latestEmploymentRate;
    }

    public BigDecimal getLatestPostgraduateRate() {
        return latestPostgraduateRate;
    }

    public void setLatestPostgraduateRate(BigDecimal latestPostgraduateRate) {
        this.latestPostgraduateRate = latestPostgraduateRate;
    }

    public Integer getLatestYear() {
        return latestYear;
    }

    public void setLatestYear(Integer latestYear) {
        this.latestYear = latestYear;
    }

    public Integer getSourceCount() {
        return sourceCount;
    }

    public void setSourceCount(Integer sourceCount) {
        this.sourceCount = sourceCount;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getDestinationHighlights() {
        return destinationHighlights;
    }

    public void setDestinationHighlights(List<String> destinationHighlights) {
        this.destinationHighlights = destinationHighlights;
    }

    public List<YearPoint> getTrend() {
        return trend;
    }

    public void setTrend(List<YearPoint> trend) {
        this.trend = trend;
    }

    public List<CoverageItem> getCoverage() {
        return coverage;
    }

    public void setCoverage(List<CoverageItem> coverage) {
        this.coverage = coverage;
    }

    public List<SourceItem> getSources() {
        return sources;
    }

    public void setSources(List<SourceItem> sources) {
        this.sources = sources;
    }

    public static class YearPoint implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer year;
        private BigDecimal employmentRate;
        private BigDecimal postgraduateRate;

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public BigDecimal getEmploymentRate() {
            return employmentRate;
        }

        public void setEmploymentRate(BigDecimal employmentRate) {
            this.employmentRate = employmentRate;
        }

        public BigDecimal getPostgraduateRate() {
            return postgraduateRate;
        }

        public void setPostgraduateRate(BigDecimal postgraduateRate) {
            this.postgraduateRate = postgraduateRate;
        }
    }

    public static class SourceItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private String id;
        private Integer year;
        private String title;
        private String url;
        private String sourceType;
        private String majorKeyword;
        private String careerKeyword;
        private BigDecimal employmentRate;
        private BigDecimal postgraduateRate;
        private String excerpt;
        private LocalDateTime fetchedAt;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public String getMajorKeyword() {
            return majorKeyword;
        }

        public void setMajorKeyword(String majorKeyword) {
            this.majorKeyword = majorKeyword;
        }

        public String getCareerKeyword() {
            return careerKeyword;
        }

        public void setCareerKeyword(String careerKeyword) {
            this.careerKeyword = careerKeyword;
        }

        public BigDecimal getEmploymentRate() {
            return employmentRate;
        }

        public void setEmploymentRate(BigDecimal employmentRate) {
            this.employmentRate = employmentRate;
        }

        public BigDecimal getPostgraduateRate() {
            return postgraduateRate;
        }

        public void setPostgraduateRate(BigDecimal postgraduateRate) {
            this.postgraduateRate = postgraduateRate;
        }

        public String getExcerpt() {
            return excerpt;
        }

        public void setExcerpt(String excerpt) {
            this.excerpt = excerpt;
        }

        public LocalDateTime getFetchedAt() {
            return fetchedAt;
        }

        public void setFetchedAt(LocalDateTime fetchedAt) {
            this.fetchedAt = fetchedAt;
        }
    }

    public static class CoverageItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private String school;
        private Integer year;
        private String status;
        private String label;
        private String reason;
        private String sourceUrl;

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getSourceUrl() {
            return sourceUrl;
        }

        public void setSourceUrl(String sourceUrl) {
            this.sourceUrl = sourceUrl;
        }
    }
}
