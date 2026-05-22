package v620.base.helper.career;

import v620.cc001.base.common.dto.career.CareerAgentRuleInput;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds a unified career profile from snapshot blocks and lightweight facts.
 */
public class CareerProfileBuildService {

    private static final double W_DIRECTION = 0.25D;
    private static final double W_RESUME = 0.25D;
    private static final double W_INTERVIEW = 0.25D;
    private static final double W_ACTION = 0.15D;
    private static final double W_PLAN = 0.10D;

    public CareerUserProfileDto build(UserProfileSnapshot snapshot,
                                      Map<String, String> facts,
                                      CareerAgentRuleInput.CheckInStatus checkIn,
                                      boolean hasPlan) {
        UserProfileSnapshot safeSnapshot = snapshot == null ? new UserProfileSnapshot() : snapshot;
        Map<String, String> safeFacts = facts == null ? new LinkedHashMap<String, String>() : facts;
        CareerAgentRuleInput.CheckInStatus safeCheckIn = checkIn == null
                ? new CareerAgentRuleInput.CheckInStatus()
                : checkIn;

        UserProfileSnapshot.AssessmentBlock assessment = safeSnapshot.getAssessment();
        UserProfileSnapshot.ResumeBlock resume = safeSnapshot.getResume();
        UserProfileSnapshot.InterviewBlock interview = safeSnapshot.getInterview();
        UserProfileSnapshot.OnboardingBlock onboarding = safeSnapshot.getOnboarding();

        CareerUserProfileDto.TargetRole target = resolveTargetRole(safeSnapshot, safeFacts);
        boolean hasTargetRole = hasText(target.getRole());
        boolean hasAssessment = assessment != null;
        boolean hasResume = resume != null;
        boolean hasInterview = interview != null;

        int resumeScore = resume != null && resume.getDiagnosisScore() != null ? resume.getDiagnosisScore().intValue() : 0;
        int interviewScore = interview != null && interview.getLastScore() != null ? interview.getLastScore().intValue() : 0;

        int directionClarity = computeDirectionClarity(target, hasAssessment, hasText(safeFacts.get("target_city")),
                hasText(safeFacts.get("target_industry")));
        int resumeReadiness = computeResumeReadiness(hasResume, resumeScore, onboarding);
        int interviewReadiness = computeInterviewReadiness(hasInterview, interviewScore);
        int actionContinuity = computeActionContinuity(safeCheckIn);
        int planReadiness = hasPlan ? 100 : 0;
        int overall = clampPercent((int) Math.round(directionClarity * W_DIRECTION
                + resumeReadiness * W_RESUME
                + interviewReadiness * W_INTERVIEW
                + actionContinuity * W_ACTION
                + planReadiness * W_PLAN));

        CareerUserProfileDto.Readiness readiness = new CareerUserProfileDto.Readiness();
        readiness.setOverallPercent(Integer.valueOf(overall));
        readiness.setDirectionClarityPercent(Integer.valueOf(directionClarity));
        readiness.setResumeReadinessPercent(Integer.valueOf(resumeReadiness));
        readiness.setInterviewReadinessPercent(Integer.valueOf(interviewReadiness));
        readiness.setActionContinuityPercent(Integer.valueOf(actionContinuity));
        readiness.setResumeScore(Integer.valueOf(resumeScore));
        readiness.setInterviewScore(Integer.valueOf(interviewScore));
        readiness.setHasAssessment(Boolean.valueOf(hasAssessment));
        readiness.setHasResume(Boolean.valueOf(hasResume));
        readiness.setHasInterview(Boolean.valueOf(hasInterview));
        readiness.setHasPlan(Boolean.valueOf(hasPlan));

        CareerUserProfileDto.Behavior behavior = new CareerUserProfileDto.Behavior();
        behavior.setStreakDays(Integer.valueOf(0));
        behavior.setWeeklyDays(Integer.valueOf(safeCheckIn.getWeeklyDays()));
        behavior.setTodayCompleted(Integer.valueOf(safeCheckIn.getTodayCompleted()));
        behavior.setTodayTotal(Integer.valueOf(safeCheckIn.getTodayTotal()));
        behavior.setPreferredDifficulty(firstText(safeFacts.get("preferred_task_difficulty"), "MEDIUM"));

        List<CareerUserProfileDto.MissingSignal> missingSignals = missingSignals(hasTargetRole, hasAssessment,
                hasResume, hasInterview, hasPlan, safeFacts, onboarding);
        int completeness = computeCompleteness(hasTargetRole, hasAssessment, hasResume, resumeScore, hasInterview,
                interviewScore, hasPlan, safeFacts, safeCheckIn);

        CareerUserProfileDto profile = new CareerUserProfileDto();
        profile.setPersonalizationLevel(completeness >= 70 ? "HIGH" : completeness >= 40 ? "MEDIUM" : "LOW");
        profile.setCompletenessScore(Integer.valueOf(completeness));
        profile.setCurrentStage(inferStage(target.getRole(), assessment, resume, interview, safeCheckIn, onboarding));
        profile.setTarget(target);
        profile.setReadiness(readiness);
        profile.setBehavior(behavior);
        profile.setMissingSignals(missingSignals);
        profile.setEvidence(evidence(target, safeFacts));
        return profile;
    }

    public CareerUserProfileDto.TargetRole resolveTargetRole(UserProfileSnapshot snapshot,
                                                             Map<String, String> facts) {
        UserProfileSnapshot safeSnapshot = snapshot == null ? new UserProfileSnapshot() : snapshot;
        Map<String, String> safeFacts = facts == null ? new LinkedHashMap<String, String>() : facts;
        UserProfileSnapshot.PreferencesBlock preferences = safeSnapshot.getPreferences();
        UserProfileSnapshot.ResumeBlock resume = safeSnapshot.getResume();
        UserProfileSnapshot.InterviewBlock interview = safeSnapshot.getInterview();
        UserProfileSnapshot.AssessmentBlock assessment = safeSnapshot.getAssessment();

        if (preferences != null && hasText(preferences.getTargetRole())) {
            return target(preferences.getTargetRole(), "PREFERENCES", "0.90");
        }
        if (resume != null && hasText(resume.getTargetJob())) {
            return target(resume.getTargetJob(), "RESUME", "0.75");
        }
        if (interview != null && hasText(interview.getPositionName())) {
            return target(interview.getPositionName(), "INTERVIEW", "0.60");
        }
        if (hasText(safeFacts.get("target_role"))) {
            return target(safeFacts.get("target_role"), "USER_INPUT", "0.80");
        }
        if (assessment != null && assessment.getSuggestedRoles() != null) {
            for (String role : assessment.getSuggestedRoles()) {
                if (hasText(role)) {
                    return target(role, "INFERRED", "0.45");
                }
            }
        }
        return target(null, null, "0");
    }

    private CareerUserProfileDto.TargetRole target(String role, String source, String confidence) {
        CareerUserProfileDto.TargetRole target = new CareerUserProfileDto.TargetRole();
        target.setRole(hasText(role) ? role.trim() : null);
        target.setSource(source);
        target.setConfidence(new BigDecimal(confidence));
        return target;
    }

    private int computeDirectionClarity(CareerUserProfileDto.TargetRole target,
                                        boolean hasAssessment,
                                        boolean hasTargetCity,
                                        boolean hasTargetIndustry) {
        BigDecimal confidenceValue = target == null ? BigDecimal.ZERO : target.getConfidence();
        double confidence = confidenceValue == null ? 0.0D : confidenceValue.doubleValue();
        confidence = Math.max(0.0D, Math.min(1.0D, confidence));
        int score = (int) Math.round(confidence * 60.0D);
        if (hasAssessment) score += 20;
        if (hasTargetCity) score += 10;
        if (hasTargetIndustry) score += 10;
        return clampPercent(score);
    }

    private int computeResumeReadiness(boolean hasResume,
                                       int resumeScore,
                                       UserProfileSnapshot.OnboardingBlock onboarding) {
        if (hasResume) {
            return resumeScore > 0 ? clampPercent(30 + (int) Math.round(clampPercent(resumeScore) * 0.70D)) : 30;
        }
        String selfReported = onboarding == null ? null : onboarding.getHasResume();
        String resumeStatus = onboarding == null ? null : onboarding.getResumeStatus();
        if ("ready".equalsIgnoreCase(resumeStatus)) return 20;
        if ("draft".equalsIgnoreCase(resumeStatus) || "yes".equalsIgnoreCase(selfReported)) return 10;
        return 0;
    }

    private int computeInterviewReadiness(boolean hasInterview, int interviewScore) {
        if (!hasInterview) return 0;
        return interviewScore > 0 ? clampPercent(20 + (int) Math.round(clampPercent(interviewScore) * 0.80D)) : 20;
    }

    private int computeActionContinuity(CareerAgentRuleInput.CheckInStatus checkIn) {
        int weeklyPts = Math.min(35, Math.max(0, checkIn.getWeeklyDays()) * 5);
        int todayPts = checkIn.getTodayCompleted() > 0 ? Math.min(15, checkIn.getTodayCompleted() * 5) : 0;
        return clampPercent(weeklyPts + todayPts);
    }

    private int computeCompleteness(boolean hasTargetRole,
                                    boolean hasAssessment,
                                    boolean hasResume,
                                    int resumeScore,
                                    boolean hasInterview,
                                    int interviewScore,
                                    boolean hasPlan,
                                    Map<String, String> facts,
                                    CareerAgentRuleInput.CheckInStatus checkIn) {
        int score = 0;
        if (hasTargetRole) score += 20;
        if (hasAssessment) score += 15;
        if (hasResume) score += 15;
        if (resumeScore >= 60) score += 10;
        if (hasInterview) score += 15;
        if (interviewScore >= 60) score += 5;
        if (hasPlan) score += 10;
        if (checkIn.getWeeklyDays() >= 3) score += 5;
        if (hasText(facts.get("target_city"))) score += 3;
        if (hasText(facts.get("weekly_hours"))) score += 2;
        return clampPercent(score);
    }

    private String inferStage(String targetRole,
                              UserProfileSnapshot.AssessmentBlock assessment,
                              UserProfileSnapshot.ResumeBlock resume,
                              UserProfileSnapshot.InterviewBlock interview,
                              CareerAgentRuleInput.CheckInStatus checkIn,
                              UserProfileSnapshot.OnboardingBlock onboarding) {
        if (!hasText(targetRole)) return "TARGET_ROLE_SELECTION";
        String identityType = onboarding == null ? null : firstText(onboarding.getIdentityType(), onboarding.getStage());
        String selfReportedResume = onboarding == null ? null : onboarding.getHasResume();
        if ("career_switcher".equals(identityType)) return "CAREER_SWITCH_POSITIONING";
        if ("internship_seeker".equals(identityType) && !"yes".equalsIgnoreCase(selfReportedResume) && resume == null) {
            return "INTERNSHIP_RESUME_BOOTSTRAP";
        }
        if ("new_graduate".equals(identityType) && "yes".equalsIgnoreCase(selfReportedResume) && resume == null) {
            return "GRADUATE_RESUME_UPLOAD";
        }
        if (assessment == null) return "ASSESSMENT_BASELINE";
        if (resume == null) return "RESUME_BOOTSTRAP";
        if (resume.getDiagnosisScore() != null && resume.getDiagnosisScore().intValue() < 70) return "RESUME_IMPROVEMENT";
        if (interview == null) return "INTERVIEW_BOOTSTRAP";
        if (interview.getLastScore() != null && interview.getLastScore().intValue() < 70) return "INTERVIEW_IMPROVEMENT";
        if (checkIn.getWeeklyDays() < 3) return "EXECUTION_RHYTHM";
        return "CAREER_MOMENTUM";
    }

    private List<CareerUserProfileDto.MissingSignal> missingSignals(boolean hasTargetRole,
                                                                    boolean hasAssessment,
                                                                    boolean hasResume,
                                                                    boolean hasInterview,
                                                                    boolean hasPlan,
                                                                    Map<String, String> facts,
                                                                    UserProfileSnapshot.OnboardingBlock onboarding) {
        List<CareerUserProfileDto.MissingSignal> signals = new ArrayList<CareerUserProfileDto.MissingSignal>();
        if (!hasTargetRole) signals.add(signal("target_role", "目标岗位", "HIGH"));
        if (!hasAssessment) signals.add(signal("assessment", "完成一次职业测评", "HIGH"));
        if (!hasResume) {
            String label = onboarding != null && "yes".equalsIgnoreCase(onboarding.getHasResume())
                    ? "上传已有简历"
                    : "上传或创建简历";
            signals.add(signal("resume", label, "HIGH"));
        }
        if (!hasInterview) signals.add(signal("interview", "完成一次模拟面试", "MEDIUM"));
        if (!hasText(facts.get("target_city"))) signals.add(signal("target_city", "期望工作城市", "MEDIUM"));
        if (!hasText(facts.get("weekly_hours"))) signals.add(signal("weekly_hours", "每周可投入时间", "LOW"));
        if (!hasPlan) signals.add(signal("career_plan", "生成长期职业计划", "MEDIUM"));
        return signals;
    }

    private CareerUserProfileDto.MissingSignal signal(String key, String label, String priority) {
        CareerUserProfileDto.MissingSignal signal = new CareerUserProfileDto.MissingSignal();
        signal.setKey(key);
        signal.setLabel(label);
        signal.setPriority(priority);
        return signal;
    }

    private Map<String, String> evidence(CareerUserProfileDto.TargetRole target, Map<String, String> facts) {
        Map<String, String> evidence = new LinkedHashMap<String, String>();
        if (target != null && hasText(target.getSource())) {
            evidence.put("target_role", "from " + target.getSource());
        }
        if (hasText(facts.get("target_city"))) evidence.put("target_city", "from user input");
        if (hasText(facts.get("target_industry"))) evidence.put("target_industry", "from user input");
        if (hasText(facts.get("weekly_hours"))) evidence.put("weekly_hours", "from user input");
        return evidence;
    }

    private int clampPercent(int value) {
        return Math.max(0, Math.min(100, value));
    }

    private String firstText(String first, String second) {
        return hasText(first) ? first : second;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
