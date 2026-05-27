package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class AdminStudentRowDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String nickname;
    private String school;
    private String major;
    private Integer interviewCount;
    private Integer lastInterviewScore;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public Integer getInterviewCount() { return interviewCount; }
    public void setInterviewCount(Integer interviewCount) { this.interviewCount = interviewCount; }
    public Integer getLastInterviewScore() { return lastInterviewScore; }
    public void setLastInterviewScore(Integer lastInterviewScore) { this.lastInterviewScore = lastInterviewScore; }
}
