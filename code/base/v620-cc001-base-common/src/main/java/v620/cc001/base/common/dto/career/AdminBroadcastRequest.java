package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class AdminBroadcastRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userId;
    private String title;
    private String content;
    private String link;
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}
