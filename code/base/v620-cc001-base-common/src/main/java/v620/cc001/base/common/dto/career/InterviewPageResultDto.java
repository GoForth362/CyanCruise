package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** One persisted interview history page for the current user. */
public class InterviewPageResultDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<InterviewSessionDto> items = new ArrayList<InterviewSessionDto>();
    private Integer page = Integer.valueOf(1);
    private Integer size = Integer.valueOf(InterviewConstants.INTERVIEW_HISTORY_PAGE_SIZE);
    private Integer total = Integer.valueOf(0);
    private Integer totalPages = Integer.valueOf(0);

    public List<InterviewSessionDto> getItems() { return items; }
    public void setItems(List<InterviewSessionDto> items) {
        this.items = items == null ? new ArrayList<InterviewSessionDto>() : items;
    }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }
    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
}
