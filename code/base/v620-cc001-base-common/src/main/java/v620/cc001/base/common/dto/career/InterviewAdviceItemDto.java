package v620.cc001.base.common.dto.career;

import java.io.Serializable;

/**
 * One strength or improvement item from an interview report.
 */
public class InterviewAdviceItemDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String detail;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
