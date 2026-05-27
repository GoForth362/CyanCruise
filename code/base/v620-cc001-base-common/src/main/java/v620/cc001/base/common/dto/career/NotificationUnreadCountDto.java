package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class NotificationUnreadCountDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private Integer count;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
