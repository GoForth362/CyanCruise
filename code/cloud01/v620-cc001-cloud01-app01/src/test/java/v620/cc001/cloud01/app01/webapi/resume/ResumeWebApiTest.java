package v620.cc001.cloud01.app01.webapi.resume;

import v620.cc001.cloud01.app01.mservice.auth.impl.DevelopmentCyanCruiseIdentityResolver;
import v620.cc001.cloud01.app01.mservice.auth.impl.IdentityAwareCyanCruiseWebApiBoundary;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryResumeStorage;
import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.ResumeCreateRequest;
import v620.cc001.base.common.dto.career.ResumeRecordDto;
import v620.cc001.base.common.dto.career.ResumeUpdateRequest;
import v620.cc001.cloud01.app01.mservice.application.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.auth.impl.DevelopmentCyanCruiseIdentityResolver;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryResumeStorage;
import v620.cc001.cloud01.app01.mservice.auth.impl.IdentityAwareCyanCruiseWebApiBoundary;
import v620.cc001.cloud01.app01.mservice.application.ResumeApplicationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResumeWebApiTest {

    @Test
    void exposesCreateGetListUpdateAndDelete() {
        ResumeWebApi webApi = new ResumeWebApi(new ResumeApplicationService(
                new InMemoryResumeStorage(),
                new CareerProfileApplicationService(
                        new InMemoryCareerProfileStorage(),
                        new CareerProfileSnapshotMergeService(),
                        new CareerProfileBuildService())),
                new IdentityAwareCyanCruiseWebApiBoundary(new DevelopmentCyanCruiseIdentityResolver("api-user")));

        ResumeRecordDto created = webApi.create("api-user", request("API Resume", "Java Engineer"));
        ResumeRecordDto detail = webApi.get("api-user", created.getResumeId());
        List<ResumeRecordDto> records = webApi.list("api-user");

        ResumeUpdateRequest update = new ResumeUpdateRequest();
        update.setTitle("Updated API Resume");
        ResumeRecordDto updated = webApi.update("api-user", created.getResumeId(), update);
        String deleted = webApi.delete("api-user", created.getResumeId());

        assertEquals("API Resume", detail.getTitle());
        assertEquals(1, records.size());
        assertEquals("Updated API Resume", updated.getTitle());
        assertEquals("OK", deleted);
    }

    private ResumeCreateRequest request(String title, String targetJob) {
        ResumeCreateRequest request = new ResumeCreateRequest();
        request.setTitle(title);
        request.setTargetJob(targetJob);
        request.setFileKey("resumes/api.pdf");
        return request;
    }
}
