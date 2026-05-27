package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.EmploymentInsightRecordDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeded non-crawler employment records for migration validation.
 */
public class InMemoryEmploymentInsightStorage implements EmploymentInsightStorage {

    private final List<EmploymentInsightRecordDto> records = new ArrayList<EmploymentInsightRecordDto>();

    public InMemoryEmploymentInsightStorage() {
        LocalDateTime fetchedAt = LocalDateTime.now().minusDays(2);
        records.add(record("cdut-2025", "Chengdu University of Technology", 2025,
                "Chengdu University of Technology employment quality source",
                "https://example.test/cdut/employment-2025",
                "CDUT_OFFICIAL_SUMMARY", "Computer Science", "Software Engineer",
                new BigDecimal("91.20"), new BigDecimal("23.40"),
                "Engineering, software, data and product roles appear in public destination summaries.", fetchedAt));
        records.add(record("uestc-2025", "University of Electronic Science and Technology of China", 2025,
                "UESTC employment guidance source",
                "https://example.test/uestc/employment-2025",
                "UESTC_OFFICIAL_PAGE", "Electronic Information", "Algorithm Engineer",
                new BigDecimal("94.10"), new BigDecimal("35.60"),
                "Electronic information, algorithm, chip and software roles are common destination signals.", fetchedAt));
        records.add(record("scu-2025", "Sichuan University", 2025,
                "Sichuan University public employment source",
                "https://example.test/scu/employment-2025",
                "PUBLIC_SUMMARY", "Management", "Product Manager",
                null, null,
                "Public aggregate source is available, but numeric metrics require manual verification.", fetchedAt));
    }

    public InMemoryEmploymentInsightStorage(List<EmploymentInsightRecordDto> records) {
        if (records != null) {
            this.records.addAll(records);
        }
    }

    public List<EmploymentInsightRecordDto> listRecords() {
        return new ArrayList<EmploymentInsightRecordDto>(records);
    }

    private EmploymentInsightRecordDto record(String id, String school, int year, String title, String url,
                                              String type, String majorKeyword, String careerKeyword,
                                              BigDecimal employmentRate, BigDecimal postgraduateRate,
                                              String summary, LocalDateTime fetchedAt) {
        EmploymentInsightRecordDto record = new EmploymentInsightRecordDto();
        record.setId(id);
        record.setSchool(school);
        record.setYear(Integer.valueOf(year));
        record.setSourceTitle(title);
        record.setSourceUrl(url);
        record.setSourceType(type);
        record.setMajorKeyword(majorKeyword);
        record.setCareerKeyword(careerKeyword);
        record.setEmploymentRate(employmentRate);
        record.setPostgraduateRate(postgraduateRate);
        record.setDestinationSummary(summary);
        record.setRawExcerpt(summary);
        record.setFetchedAt(fetchedAt);
        return record;
    }
}
