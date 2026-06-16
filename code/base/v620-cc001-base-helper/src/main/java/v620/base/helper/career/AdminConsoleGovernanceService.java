package v620.base.helper.career;

import v620.cc001.base.common.dto.career.AdminBroadcastRequest;
import v620.cc001.base.common.dto.career.AdminBroadcastResult;
import v620.cc001.base.common.dto.career.AdminConstants;
import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.career.AdminIdentityDto;
import v620.cc001.base.common.dto.career.AdminInterviewSummaryDto;
import v620.cc001.base.common.dto.career.AdminOrgDashboardDto;
import v620.cc001.base.common.dto.career.AdminQuestionContributionRequest;
import v620.cc001.base.common.dto.career.AdminQuestionDto;
import v620.cc001.base.common.dto.career.AdminStudentRowDto;
import v620.cc001.base.common.dto.career.AdminUserDto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pure Java governance rules extracted from the IPD admin console.
 */
public class AdminConsoleGovernanceService {

    public static final List<String> RADAR_DIMENSIONS = Collections.unmodifiableList(Arrays.asList(
            "expression", "logic", "technical", "pressureResistance", "communication"));

    private static final List<String> BLOCKED_PATTERNS = Collections.unmodifiableList(Arrays.asList(
            "博彩", "赌博", "赌场", "彩票平台", "快速致富", "一夜暴富", "洗钱", "诈骗", "传销",
            "虚假发票", "套现", "色情", "裸聊", "约炮", "援交", "外围", "毒品", "大麻",
            "可卡因", "冰毒", "微信号加我", "私信我", "点击领取", "扫码领红包", "杀人",
            "爆炸物", "制作炸弹", "法轮功", "六四真相"));

    public AdminIdentityDto authorize(String userId, boolean isAdmin) {
        AdminIdentityDto identity = new AdminIdentityDto();
        identity.setUserId(trim(userId));
        identity.setAdmin(Boolean.valueOf(false));
        if (!hasText(userId)) {
            identity.setStatus(AdminConstants.STATUS_IDENTITY_REQUIRED);
            identity.setMessage("admin identity is required");
            return identity;
        }
        if (!isAdmin) {
            identity.setStatus(AdminConstants.STATUS_FORBIDDEN);
            identity.setMessage("admin role is required");
            return identity;
        }
        identity.setAdmin(Boolean.TRUE);
        identity.setStatus(AdminConstants.STATUS_OK);
        identity.setMessage("authorized");
        return identity;
    }

    public int safePage(int page) {
        return Math.max(0, page);
    }

    public int safeSize(int size) {
        return Math.min(100, Math.max(1, size));
    }

    public String banReason(String reason) {
        return hasText(reason) ? reason.trim() : "Violated community guidelines";
    }

    public void applyBan(AdminUserDto user, String reason) {
        if (user == null) {
            throw new IllegalArgumentException("user is required");
        }
        user.setStatus(AdminConstants.USER_STATUS_BANNED);
        user.setBannedReason(banReason(reason));
    }

    public void applyUnban(AdminUserDto user) {
        if (user == null) {
            throw new IllegalArgumentException("user is required");
        }
        user.setStatus(AdminConstants.USER_STATUS_ACTIVE);
        user.setBannedReason(null);
    }

    public String normalizeDifficulty(String raw) {
        if (!hasText(raw)) {
            return "Normal";
        }
        String value = raw.trim();
        if ("easy".equalsIgnoreCase(value)) return "Easy";
        if ("hard".equalsIgnoreCase(value)) return "Hard";
        return "Normal";
    }

    public String approveReviewStatus(String current) {
        return AdminConstants.QUESTION_REVIEW_PUBLISHED;
    }

    public String rejectReviewStatus(String current) {
        return AdminConstants.QUESTION_REVIEW_REJECTED;
    }

    public AdminQuestionDto applyQuestionPatch(AdminQuestionDto existing, AdminQuestionDto patch) {
        if (existing == null) throw new IllegalArgumentException("question is required");
        if (patch == null) return existing;
        if (patch.getContent() != null) existing.setContent(patch.getContent());
        if (patch.getSummary() != null) existing.setSummary(patch.getSummary());
        if (patch.getPosition() != null) existing.setPosition(patch.getPosition());
        if (patch.getDifficulty() != null) existing.setDifficulty(normalizeDifficulty(patch.getDifficulty()));
        if (patch.getStatus() != null) existing.setStatus(patch.getStatus());
        if (patch.getReviewStatus() != null) existing.setReviewStatus(patch.getReviewStatus());
        if (patch.getAnswer() != null) existing.setAnswer(patch.getAnswer());
        return existing;
    }

    public String contentSafetyFailure(String text) {
        if (!hasText(text)) {
            return null;
        }
        String lower = text.toLowerCase(Locale.ROOT);
        for (String pattern : BLOCKED_PATTERNS) {
            if (lower.contains(pattern.toLowerCase(Locale.ROOT))) {
                return "内容包含不当信息，请修改后重新提交";
            }
        }
        return null;
    }

    public AdminQuestionDto buildContribution(AdminQuestionContributionRequest request, String pepper) {
        if (request == null || !hasText(request.getUserId())) {
            throw new IllegalArgumentException("userId is required");
        }
        String content = trim(request.getContent());
        if (content.length() < 8) {
            throw new IllegalArgumentException("Question is too short. Add a few more words so others can answer it.");
        }
        String safetyFailure = contentSafetyFailure(content);
        if (safetyFailure != null) {
            throw new IllegalArgumentException(safetyFailure);
        }
        if (content.length() > 1000) {
            content = content.substring(0, 1000);
        }
        AdminQuestionDto question = new AdminQuestionDto();
        question.setPosition(hasText(request.getPosition()) ? request.getPosition().trim() : "General");
        question.setDifficulty(normalizeDifficulty(request.getDifficulty()));
        question.setContent(content);
        question.setSummary(trimToNull(request.getSummary()));
        question.setContributorHash(hashUserId(request.getUserId(), pepper));
        question.setLikes(Integer.valueOf(0));
        question.setDrawCount(Integer.valueOf(0));
        question.setStatus(AdminConstants.QUESTION_STATUS_APPROVED);
        question.setSource("USER");
        question.setReviewStatus(AdminConstants.QUESTION_REVIEW_PUBLISHED);
        return question;
    }

    public AdminContentItemDto togglePinned(AdminContentItemDto item) {
        if (item == null) throw new IllegalArgumentException("content item is required");
        item.setPinned(Boolean.valueOf(!Boolean.TRUE.equals(item.getPinned())));
        return item;
    }

    public AdminContentItemDto toggleHidden(AdminContentItemDto item) {
        if (item == null) throw new IllegalArgumentException("content item is required");
        item.setHidden(Boolean.valueOf(!Boolean.TRUE.equals(item.getHidden())));
        return item;
    }

    public AdminBroadcastResult validateBroadcast(AdminBroadcastRequest request, int targetCount) {
        AdminBroadcastResult result = new AdminBroadcastResult();
        result.setTargetCount(Integer.valueOf(Math.max(0, targetCount)));
        result.setSuccessCount(Integer.valueOf(0));
        result.setFailedCount(Integer.valueOf(0));
        result.setSkippedCount(Integer.valueOf(0));
        if (request == null || !hasText(request.getTitle()) || !hasText(request.getContent())) {
            result.setStatus(AdminConstants.STATUS_FAILED);
            result.setMessage("title and content are required");
            return result;
        }
        result.setStatus(AdminConstants.STATUS_OK);
        result.setMessage("ready");
        return result;
    }

    public AdminBroadcastResult broadcastResult(int targets, int success, int failed, int skipped) {
        AdminBroadcastResult result = new AdminBroadcastResult();
        result.setTargetCount(Integer.valueOf(targets));
        result.setSuccessCount(Integer.valueOf(success));
        result.setFailedCount(Integer.valueOf(failed));
        result.setSkippedCount(Integer.valueOf(skipped));
        result.setStatus(failed > 0 ? AdminConstants.STATUS_FAILED : AdminConstants.STATUS_OK);
        result.setMessage("targets=" + targets + ", success=" + success + ", failed=" + failed + ", skipped=" + skipped);
        return result;
    }

    public AdminOrgDashboardDto dashboard(String orgId, List<AdminUserDto> students,
                                           Map<String, List<AdminInterviewSummaryDto>> interviewsByUser) {
        AdminOrgDashboardDto dashboard = new AdminOrgDashboardDto();
        dashboard.setOrgId(orgId);
        List<AdminUserDto> safeStudents = students == null ? Collections.<AdminUserDto>emptyList() : students;
        dashboard.setStudentCount(Integer.valueOf(safeStudents.size()));
        Map<String, double[]> sums = new LinkedHashMap<String, double[]>();
        for (String dimension : RADAR_DIMENSIONS) {
            sums.put(dimension, new double[]{0.0, 0.0});
        }
        int interviewCount = 0;
        int reportCount = 0;
        int skipped = 0;
        for (AdminUserDto student : safeStudents) {
            List<AdminInterviewSummaryDto> interviews = interviewsByUser == null ? null : interviewsByUser.get(student.getUserId());
            if (interviews == null) continue;
            interviewCount += interviews.size();
            for (AdminInterviewSummaryDto interview : interviews) {
                Map<String, Double> radar = extractRadar(interview == null ? null : interview.getReportJson());
                if (radar.isEmpty()) {
                    skipped++;
                    continue;
                }
                reportCount++;
                for (String dimension : RADAR_DIMENSIONS) {
                    Double value = radar.get(dimension);
                    if (value != null) {
                        double[] cell = sums.get(dimension);
                        cell[0] += value.doubleValue();
                        cell[1] += 1.0;
                    }
                }
            }
        }
        Map<String, Double> averages = new LinkedHashMap<String, Double>();
        for (String dimension : RADAR_DIMENSIONS) {
            double[] cell = sums.get(dimension);
            averages.put(dimension, Double.valueOf(cell[1] == 0.0 ? 0.0 : Math.round((cell[0] / cell[1]) * 10.0) / 10.0));
        }
        dashboard.setInterviewCount(Integer.valueOf(interviewCount));
        dashboard.setReportCount(Integer.valueOf(reportCount));
        dashboard.setSkippedReportCount(Integer.valueOf(skipped));
        dashboard.setRadarAverages(averages);
        dashboard.setWeakDimensionsTop3(weakTop3(averages));
        return dashboard;
    }

    public List<AdminStudentRowDto> studentRows(List<AdminUserDto> students,
                                                 Map<String, List<AdminInterviewSummaryDto>> interviewsByUser) {
        List<AdminStudentRowDto> rows = new ArrayList<AdminStudentRowDto>();
        if (students == null) return rows;
        for (AdminUserDto user : students) {
            List<AdminInterviewSummaryDto> interviews = interviewsByUser == null ? null : interviewsByUser.get(user.getUserId());
            AdminStudentRowDto row = new AdminStudentRowDto();
            row.setUserId(user.getUserId());
            row.setNickname(user.getNickname());
            row.setSchool(user.getSchool());
            row.setMajor(user.getMajor());
            row.setInterviewCount(Integer.valueOf(interviews == null ? 0 : interviews.size()));
            row.setLastInterviewScore(lastScore(interviews));
            rows.add(row);
        }
        return rows;
    }

    public String auditSnapshot(Map<String, String> fields) {
        if (fields == null || fields.isEmpty()) {
            return "{}";
        }
        StringBuilder builder = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (!first) builder.append(',');
            first = false;
            builder.append('"').append(escape(entry.getKey())).append("\":\"")
                    .append(escape(maskIfSensitive(entry.getKey(), entry.getValue()))).append('"');
        }
        builder.append('}');
        return builder.toString();
    }

    private Integer lastScore(List<AdminInterviewSummaryDto> interviews) {
        if (interviews == null) return null;
        for (AdminInterviewSummaryDto interview : interviews) {
            if (interview != null && interview.getFinalScore() != null) {
                return interview.getFinalScore();
            }
        }
        return null;
    }

    private List<String> weakTop3(final Map<String, Double> averages) {
        List<String> dimensions = new ArrayList<String>(averages.keySet());
        Collections.sort(dimensions, new Comparator<String>() {
            public int compare(String a, String b) {
                return Double.compare(averages.get(a).doubleValue(), averages.get(b).doubleValue());
            }
        });
        return dimensions.size() > 3 ? new ArrayList<String>(dimensions.subList(0, 3)) : dimensions;
    }

    private Map<String, Double> extractRadar(String reportJson) {
        Map<String, Double> radar = new LinkedHashMap<String, Double>();
        if (!hasText(reportJson)) return radar;
        for (String dimension : RADAR_DIMENSIONS) {
            Pattern pattern = Pattern.compile("\"" + Pattern.quote(dimension) + "\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)");
            Matcher matcher = pattern.matcher(reportJson);
            if (matcher.find()) {
                radar.put(dimension, Double.valueOf(Double.parseDouble(matcher.group(1))));
            }
        }
        return radar;
    }

    private String hashUserId(String userId, String pepper) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(((hasText(pepper) ? pepper.trim() : "cyancruise-qbank") + ":" + userId)
                    .getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                builder.append(String.format("%02x", Byte.valueOf(b)));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            return "uid-" + userId;
        }
    }

    private String maskIfSensitive(String key, String value) {
        String lower = key == null ? "" : key.toLowerCase(Locale.ROOT);
        if (lower.contains("password") || lower.contains("token") || lower.contains("secret")
                || lower.contains("openid") || lower.contains("phone") || lower.contains("credential")) {
            return "***";
        }
        return value;
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String trimToNull(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
