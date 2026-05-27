package v620.cc001.base.common.dto.career;

import java.io.Serializable;

/**
 * Request for resume diagnosis.
 */
public class ResumeDiagnosisRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long resumeId;
    private String resumeText;
    private String jobDescription;
    private String profileContext;

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getProfileContext() {
        return profileContext;
    }

    public void setProfileContext(String profileContext) {
        this.profileContext = profileContext;
    }
}
