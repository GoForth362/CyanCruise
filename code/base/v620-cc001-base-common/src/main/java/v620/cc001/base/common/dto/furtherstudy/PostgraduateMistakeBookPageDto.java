package v620.cc001.base.common.dto.furtherstudy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PostgraduateMistakeBookPageDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<PostgraduateMistakeBookEntryDto> items = new ArrayList<PostgraduateMistakeBookEntryDto>();
    private boolean hasMore;
    private int nextOffset;
    public List<PostgraduateMistakeBookEntryDto> getItems() { return items; }
    public void setItems(List<PostgraduateMistakeBookEntryDto> items) { this.items = items == null ? new ArrayList<PostgraduateMistakeBookEntryDto>() : items; }
    public boolean isHasMore() { return hasMore; }
    public void setHasMore(boolean hasMore) { this.hasMore = hasMore; }
    public int getNextOffset() { return nextOffset; }
    public void setNextOffset(int nextOffset) { this.nextOffset = nextOffset; }
}
