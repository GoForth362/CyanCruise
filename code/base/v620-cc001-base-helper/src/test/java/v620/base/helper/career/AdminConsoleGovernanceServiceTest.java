package v620.base.helper.career;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.AdminBroadcastRequest;
import v620.cc001.base.common.dto.career.AdminConstants;
import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.career.AdminIdentityDto;
import v620.cc001.base.common.dto.career.AdminInterviewSummaryDto;
import v620.cc001.base.common.dto.career.AdminOrgDashboardDto;
import v620.cc001.base.common.dto.career.AdminQuestionContributionRequest;
import v620.cc001.base.common.dto.career.AdminQuestionDto;
import v620.cc001.base.common.dto.career.AdminUserDto;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminConsoleGovernanceServiceTest {

    private final AdminConsoleGovernanceService service = new AdminConsoleGovernanceService();

    @Test
    void authorizationCoversMissingForbiddenAndAdmin() {
        AdminIdentityDto missing = service.authorize(" ", false);
        AdminIdentityDto forbidden = service.authorize("u1", false);
        AdminIdentityDto admin = service.authorize("u1", true);

        assertEquals(AdminConstants.STATUS_IDENTITY_REQUIRED, missing.getStatus());
        assertEquals(AdminConstants.STATUS_FORBIDDEN, forbidden.getStatus());
        assertEquals(AdminConstants.STATUS_OK, admin.getStatus());
        assertEquals(Boolean.TRUE, admin.getAdmin());
    }

    @Test
    void paginationAndBanRulesAreBounded() {
        AdminUserDto user = new AdminUserDto();

        service.applyBan(user, " ");

        assertEquals(0, service.safePage(-1));
        assertEquals(100, service.safeSize(500));
        assertEquals(AdminConstants.USER_STATUS_BANNED, user.getStatus());
        assertTrue(user.getBannedReason().contains("Violated"));
        service.applyUnban(user);
        assertEquals(AdminConstants.USER_STATUS_ACTIVE, user.getStatus());
    }

    @Test
    void dashboardSkipsMalformedRadarAndSortsWeakDimensions() {
        AdminUserDto student = new AdminUserDto();
        student.setUserId("u1");
        AdminInterviewSummaryDto good = new AdminInterviewSummaryDto();
        good.setFinalScore(Integer.valueOf(82));
        good.setReportJson("{\"radarChart\":{\"expression\":4,\"logic\":2,\"technical\":3,\"pressureResistance\":5,\"communication\":1}}");
        AdminInterviewSummaryDto bad = new AdminInterviewSummaryDto();
        bad.setReportJson("{bad json}");
        Map<String, List<AdminInterviewSummaryDto>> interviews = new LinkedHashMap<String, List<AdminInterviewSummaryDto>>();
        interviews.put("u1", Arrays.asList(good, bad));

        AdminOrgDashboardDto dashboard = service.dashboard("org1", Arrays.asList(student), interviews);

        assertEquals(Integer.valueOf(2), dashboard.getInterviewCount());
        assertEquals(Integer.valueOf(1), dashboard.getReportCount());
        assertEquals(Integer.valueOf(1), dashboard.getSkippedReportCount());
        assertEquals("communication", dashboard.getWeakDimensionsTop3().get(0));
    }

    @Test
    void questionRulesPatchNormalizeAndRejectUnsafeContribution() {
        AdminQuestionDto question = new AdminQuestionDto();
        question.setQuestionId("q1");
        question.setContent("old");
        AdminQuestionDto patch = new AdminQuestionDto();
        patch.setDifficulty("hard");
        patch.setReviewStatus(AdminConstants.QUESTION_REVIEW_PENDING);

        service.applyQuestionPatch(question, patch);

        assertEquals("Hard", question.getDifficulty());
        assertEquals(AdminConstants.QUESTION_REVIEW_PUBLISHED, service.approveReviewStatus(question.getReviewStatus()));

        AdminQuestionContributionRequest unsafe = new AdminQuestionContributionRequest();
        unsafe.setUserId("u1");
        unsafe.setContent("这是一个赌博推广问题内容");
        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() {
                service.buildContribution(unsafe, "pepper");
            }
        });
    }

    @Test
    void contributionInitializesPublicQuestionFields() {
        AdminQuestionContributionRequest request = new AdminQuestionContributionRequest();
        request.setUserId("u1");
        request.setDifficulty("easy");
        request.setContent("Please describe a difficult project you completed.");

        AdminQuestionDto question = service.buildContribution(request, "pepper");

        assertEquals("General", question.getPosition());
        assertEquals("Easy", question.getDifficulty());
        assertEquals(Integer.valueOf(0), question.getLikes());
        assertFalse(question.getContributorHash().contains("u1"));
    }

    @Test
    void broadcastContentAndAuditSnapshotRulesAreSafe() {
        AdminBroadcastRequest invalid = new AdminBroadcastRequest();
        AdminContentItemDto item = new AdminContentItemDto();
        Map<String, String> snapshot = new LinkedHashMap<String, String>();
        snapshot.put("nickname", "Ming");
        snapshot.put("password", "secret");
        snapshot.put("openid", "openid-x");

        assertEquals(AdminConstants.STATUS_FAILED, service.validateBroadcast(invalid, 10).getStatus());
        assertEquals(Boolean.TRUE, service.togglePinned(item).getPinned());
        assertEquals(Boolean.TRUE, service.toggleHidden(item).getHidden());
        String json = service.auditSnapshot(snapshot);
        assertTrue(json.contains("Ming"));
        assertTrue(json.contains("***"));
        assertFalse(json.contains("secret"));
        assertFalse(json.contains("openid-x"));
    }
}
