package v620.cc001.base.common.dto.furtherstudy;

import java.io.Serializable;

public class PostgraduateMistakeBookQueryRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer limit;
    private Integer offset;
    public Integer getLimit() { return limit; }
    public void setLimit(Integer limit) { this.limit = limit; }
    public Integer getOffset() { return offset; }
    public void setOffset(Integer offset) { this.offset = offset; }
}
