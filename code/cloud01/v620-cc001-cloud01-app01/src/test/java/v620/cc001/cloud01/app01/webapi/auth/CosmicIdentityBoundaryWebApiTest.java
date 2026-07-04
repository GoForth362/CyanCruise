package v620.cc001.cloud01.app01.webapi.auth;

import v620.cc001.cloud01.app01.webapi.admin.AdminConsoleGovernanceWebApi;
import v620.cc001.cloud01.app01.webapi.resume.ResumeWebApi;
import v620.cc001.cloud01.app01.mservice.auth.impl.ConfigurableCosmicIdentityResolver;
import v620.cc001.cloud01.app01.mservice.auth.impl.DevelopmentCyanCruiseIdentityResolver;
import v620.cc001.cloud01.app01.mservice.auth.impl.IdentityAwareCyanCruiseWebApiBoundary;
import v620.cc001.cloud01.app01.mservice.auth.impl.UnavailableCosmicIdentityResolver;
import v620.cc001.cloud01.app01.mservice.application.AdminConsoleGovernanceApplicationService;
import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.AdminConstants;
import v620.cc001.base.common.dto.career.AdminOperationResult;
import v620.cc001.base.common.dto.career.CosmicIdentityConstants;
import v620.cc001.base.common.dto.career.ResumeCreateRequest;
import v620.cc001.base.common.dto.career.ResumeRecordDto;
import v620.cc001.cloud01.app01.mservice.auth.impl.ConfigurableCosmicIdentityResolver;
import v620.cc001.cloud01.app01.mservice.auth.CosmicIdentityAdapterConfig;
import v620.cc001.cloud01.app01.mservice.auth.CosmicIdentityContextProvider;
import v620.cc001.cloud01.app01.mservice.auth.impl.DevelopmentCyanCruiseIdentityResolver;
import v620.cc001.cloud01.app01.mservice.auth.impl.IdentityAwareCyanCruiseWebApiBoundary;
import v620.cc001.cloud01.app01.mservice.auth.IdentityBoundaryException;
import v620.cc001.cloud01.app01.mservice.application.ResumeApplicationService;
import v620.cc001.cloud01.app01.mservice.auth.impl.UnavailableCosmicIdentityResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CosmicIdentityBoundaryWebApiTest {

    @Test
    void userEndpointRejectsMissingIdentityBeforeServiceCall() {
        CountingResumeApplicationService service = new CountingResumeApplicationService();
        ResumeWebApi webApi = new ResumeWebApi(service,
                new IdentityAwareCyanCruiseWebApiBoundary(new UnavailableCosmicIdentityResolver()));

        IdentityBoundaryException ex = assertThrows(IdentityBoundaryException.class,
                () -> webApi.create("u1", new ResumeCreateRequest()));

        assertEquals(CosmicIdentityConstants.STATUS_IDENTITY_REQUIRED, ex.getStatus());
        assertEquals(0, service.createCalls);
    }

    @Test
    void userEndpointRejectsIdentityMismatchBeforeServiceCall() {
        CountingResumeApplicationService service = new CountingResumeApplicationService();
        ResumeWebApi webApi = new ResumeWebApi(service,
                new IdentityAwareCyanCruiseWebApiBoundary(new DevelopmentCyanCruiseIdentityResolver("u1")));

        IdentityBoundaryException ex = assertThrows(IdentityBoundaryException.class,
                () -> webApi.list("u2"));

        assertEquals(CosmicIdentityConstants.STATUS_IDENTITY_MISMATCH, ex.getStatus());
        assertEquals(0, service.listCalls);
    }

    @Test
    void adminEndpointRejectsForbiddenCallerWithoutServiceCall() {
        CountingAdminWebApi webApi = new CountingAdminWebApi(new IdentityAwareCyanCruiseWebApiBoundary(
                new DevelopmentCyanCruiseIdentityResolver("user", "user")));

        AdminOperationResult result = webApi.ban("user", "target", "risk");

        assertEquals(AdminConstants.STATUS_FORBIDDEN, result.getStatus());
        assertEquals(Integer.valueOf(0), result.getUpdated());
    }

    @Test
    void factoryEnabledAdapterAuthorizesMatchingUserCall() {
        CountingResumeApplicationService service = new CountingResumeApplicationService();
        ResumeWebApi webApi = new ResumeWebApi(service,
                new IdentityAwareCyanCruiseWebApiBoundary(new ConfigurableCosmicIdentityResolver(
                        provider("userId", "u1"), enabled())));

        webApi.create("u1", new ResumeCreateRequest());

        assertEquals(1, service.createCalls);
    }

    @Test
    void factoryEnabledAdapterAuthorizesAdminCall() {
        CountingAdminWebApi webApi = new CountingAdminWebApi(new IdentityAwareCyanCruiseWebApiBoundary(
                new ConfigurableCosmicIdentityResolver(provider("adminId", "admin", "roles", "ADMIN"), enabled())));

        assertEquals(AdminConstants.STATUS_OK, webApi.whoami("admin").getStatus());
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
        CountingAdminWebApi(IdentityAwareCyanCruiseWebApiBoundary boundary) {
            super(new v620.cc001.cloud01.app01.mservice.application.AdminConsoleGovernanceApplicationService(), boundary);
        }
    }

    private CosmicIdentityAdapterConfig enabled() {
        CosmicIdentityAdapterConfig config = new CosmicIdentityAdapterConfig();
        config.setEnabled(true);
        return config;
    }

    private CosmicIdentityContextProvider provider(String key, Object value) {
        return provider(key, value, null, null);
    }

    private CosmicIdentityContextProvider provider(String key, Object value, String secondKey, Object secondValue) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put(key, value);
        if (secondKey != null) {
            map.put(secondKey, secondValue);
        }
        return new CosmicIdentityContextProvider() {
            public Map<String, Object> currentContext() {
                return map;
            }
        };
    }
}
