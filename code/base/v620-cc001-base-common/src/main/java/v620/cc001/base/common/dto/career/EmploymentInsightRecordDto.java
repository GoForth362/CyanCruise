package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Traceable employment source record, independent of IPD JPA/Flyway models.
 */
public class EmploymentInsightRecordDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String school;
    private Integer year;
    private String sourceTitle;
    private String sourceUrl;
    private String sourceType;
    private String majorKeyword;
    private String careerKeyword;
    private BigDecimal employmentRate;
    private BigDecimal postgraduateRate;
    private String destinationSummary;
    private String rawExcerpt;
    private LocalDateTime fetchedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getSourceTitle() {
        return sourceTitle;
    }

    public void setSourceTitle(String sourceTitle) {
        this.sourceTitle = sourceTitle;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
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

    public String getDestinationSummary() {
        return destinationSummary;
    }

    public void setDestinationSummary(String destinationSummary) {
        this.destinationSummary = destinationSummary;
    }

    public String getRawExcerpt() {
        return rawExcerpt;
    }

    public void setRawExcerpt(String rawExcerpt) {
        this.rawExcerpt = rawExcerpt;
    }

    public LocalDateTime getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(LocalDateTime fetchedAt) {
        this.fetchedAt = fetchedAt;
    }
}
