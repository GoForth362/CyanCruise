package v620.cc001.cloud01.app01.webapi;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.AdminConstants;
import v620.cc001.base.common.dto.career.AdminOperationResult;
import v620.cc001.base.common.dto.career.CosmicIdentityConstants;
import v620.cc001.base.common.dto.career.ResumeCreateRequest;
import v620.cc001.base.common.dto.career.ResumeRecordDto;
import v620.cc001.cloud01.app01.mservice.DevelopmentCareerLoopIdentityResolver;
import v620.cc001.cloud01.app01.mservice.IdentityAwareCareerLoopWebApiBoundary;
import v620.cc001.cloud01.app01.mservice.IdentityBoundaryException;
import v620.cc001.cloud01.app01.mservice.ResumeApplicationService;
import v620.cc001.cloud01.app01.mservice.UnavailableCosmicIdentityResolver;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CosmicIdentityBoundaryWebApiTest {

    @Test
    void userEndpointRejectsMissingIdentityBeforeServiceCall() {
        CountingResumeApplicationService service = new CountingResumeApplicationService();
        ResumeWebApi webApi = new ResumeWebApi(service,
                new IdentityAwareCareerLoopWebApiBoundary(new UnavailableCosmicIdentityResolver()));

        IdentityBoundaryException ex = assertThrows(IdentityBoundaryException.class,
                () -> webApi.create("u1", new ResumeCreateRequest()));

        assertEquals(CosmicIdentityConstants.STATUS_IDENTITY_REQUIRED, ex.getStatus());
        assertEquals(0, service.createCalls);
    }

    @Test
    void userEndpointRejectsIdentityMismatchBeforeServiceCall() {
        CountingResumeApplicationService service = new CountingResumeApplicationService();
        ResumeWebApi webApi = new ResumeWebApi(service,
                new IdentityAwareCareerLoopWebApiBoundary(new DevelopmentCareerLoopIdentityResolver("u1")));

        IdentityBoundaryException ex = assertThrows(IdentityBoundaryException.class,
                () -> webApi.list("u2"));

        assertEquals(CosmicIdentityConstants.STATUS_IDENTITY_MISMATCH, ex.getStatus());
        assertEquals(0, service.listCalls);
    }

    @Test
    void adminEndpointRejectsForbiddenCallerWithoutServiceCall() {
        CountingAdminWebApi webApi = new CountingAdminWebApi(new IdentityAwareCareerLoopWebApiBoundary(
                new DevelopmentCareerLoopIdentityResolver("user", "user")));

        AdminOperationResult result = webApi.ban("user", "target", "risk");

        assertEquals(AdminConstants.STATUS_FORBIDDEN, result.getStatus());
        assertEquals(Integer.valueOf(0), result.getUpdated());
    }

    private static class CountingResumeApplicationService extends ResumeApplicationService {
        private int createCalls;
        private int listCalls;

        public ResumeRecordDto create(String userId, ResumeCreateRequest request) {
            createCalls += 1;
            return new ResumeRecordDto();
        }

        public List<ResumeRecordDto> listByUser(String userId) {
            listCalls += 1;
            return java.util.Collections.emptyList();
        }
    }

    private static class CountingAdminWebApi extends AdminConsoleGovernanceWebApi {
        CountingAdminWebApi(IdentityAwareCareerLoopWebApiBoundary boundary) {
            super(new v620.cc001.cloud01.app01.mservice.AdminConsoleGovernanceApplicationService(), boundary);
        }
    }
}
