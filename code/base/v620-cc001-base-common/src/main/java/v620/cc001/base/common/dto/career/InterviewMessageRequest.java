package v620.cc001.base.common.dto.career;

/**
 * Request to append a message to a mock interview.
 */
public class InterviewMessageRequest {

    private String role;
    private String content;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
