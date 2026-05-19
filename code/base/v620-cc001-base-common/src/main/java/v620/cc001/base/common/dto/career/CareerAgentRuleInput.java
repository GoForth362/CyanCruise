package v620.cc001.base.common.dto.career;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal dependency-free input for the migrated career agent daily rule.
 */
public class CareerAgentRuleInput {

    private UserProfileSnapshot snapshot;
    private CheckInStatus checkInStatus;
    private List<String> weeklyFocusItems = new ArrayList<String>();
    private String userMajor;
    private String userSchool;

    public UserProfileSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(UserProfileSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public CheckInStatus getCheckInStatus() {
        return checkInStatus;
    }

    public void setCheckInStatus(CheckInStatus checkInStatus) {
        this.checkInStatus = checkInStatus;
    }

    public List<String> getWeeklyFocusItems() {
        return weeklyFocusItems;
    }

    public void setWeeklyFocusItems(List<String> weeklyFocusItems) {
        this.weeklyFocusItems = weeklyFocusItems;
    }

    public String getUserMajor() {
        return userMajor;
    }

    public void setUserMajor(String userMajor) {
        this.userMajor = userMajor;
    }

    public String getUserSchool() {
        return userSchool;
    }

    public void setUserSchool(String userSchool) {
        this.userSchool = userSchool;
    }

    public static class CheckInStatus {
        private int weeklyDays;
        private int todayCompleted;
        private int todayTotal;

        public int getWeeklyDays() {
            return weeklyDays;
        }

        public void setWeeklyDays(int weeklyDays) {
            this.weeklyDays = weeklyDays;
        }

        public int getTodayCompleted() {
            return todayCompleted;
        }

        public void setTodayCompleted(int todayCompleted) {
            this.todayCompleted = todayCompleted;
        }

        public int getTodayTotal() {
            return todayTotal;
        }

        public void setTodayTotal(int todayTotal) {
            this.todayTotal = todayTotal;
        }
    }
}
