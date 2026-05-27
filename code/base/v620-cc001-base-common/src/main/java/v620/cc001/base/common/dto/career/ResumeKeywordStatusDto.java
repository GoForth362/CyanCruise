package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Keyword extraction status for a resume.
 */
public class ResumeKeywordStatusDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long resumeId;
    private String status = ResumeDiagnosisConstants.STATUS_PENDING;
    private String errorMsg;
    private List<ResumeKeywordDto> keywords = new ArrayList<ResumeKeywordDto>();

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<ResumeKeywordDto> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<ResumeKeywordDto> keywords) {
        this.keywords = keywords == null ? new ArrayList<ResumeKeywordDto>() : keywords;
    }
}
