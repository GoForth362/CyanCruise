package v620.cc001.cloud01.app01.mservice.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import v620.base.helper.career.FileUploadPreviewService;
import v620.cc001.base.common.dto.career.CareerRouteContext;
import v620.cc001.base.common.dto.career.FileUploadRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialDto;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialUploadRequest;
import v620.cc001.cloud01.app01.mservice.file.FileTextExtractor;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerFileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryStudyCenterStorage;

class StudyPlanningMaterialApplicationServiceTest {

    @Test
    void uploadsExtractsListsAndDeletesOnlyForCurrentUser() {
        InMemoryStudyCenterStorage studyStorage = new InMemoryStudyCenterStorage();
        InMemoryCareerFileStorage fileStorage = new InMemoryCareerFileStorage();
        FileTextExtractor extractor = new FileTextExtractor() {
            public String extract(byte[] bytes, String objectKey) {
                return new String(bytes, StandardCharsets.UTF_8);
            }
            public boolean available() { return true; }
        };
        FileUploadPreviewApplicationService files = new FileUploadPreviewApplicationService(
                fileStorage, extractor, new FileUploadPreviewService());
        StudyPlanningMaterialApplicationService service =
                new StudyPlanningMaterialApplicationService(studyStorage, files);

        StudyPlanningMaterialDto saved = service.upload("user-a", request("admission.txt",
                "电子科技大学计算机专业考试科目说明"));

        assertEquals("OK", saved.getExtractionStatus());
        assertTrue(saved.getExtractedText().contains("考试科目"));
        assertEquals(1, service.list("user-a", CareerRouteContext.POSTGRADUATE).size());
        assertTrue(service.list("user-b", CareerRouteContext.POSTGRADUATE).isEmpty());
        assertThrows(IllegalArgumentException.class,
                () -> service.delete("user-b", saved.getMaterialId()));
        assertTrue(service.delete("user-a", saved.getMaterialId()).getDeleted().booleanValue());
        assertFalse(fileStorage.delete(saved.getObjectKey()));
        assertTrue(service.list("user-a", CareerRouteContext.POSTGRADUATE).isEmpty());
    }

    @Test
    void rejectsOversizedOrNonPostgraduateMaterial() {
        InMemoryStudyCenterStorage studyStorage = new InMemoryStudyCenterStorage();
        FileTextExtractor extractor = new FileTextExtractor() {
            public String extract(byte[] bytes, String objectKey) { return "text"; }
            public boolean available() { return true; }
        };
        StudyPlanningMaterialApplicationService service =
                new StudyPlanningMaterialApplicationService(studyStorage,
                        new FileUploadPreviewApplicationService(new InMemoryCareerFileStorage(),
                                extractor, new FileUploadPreviewService()));
        StudyPlanningMaterialUploadRequest request = request("guide.txt", "content");
        request.setDirection(CareerRouteContext.RECOMMENDATION);

        assertThrows(IllegalArgumentException.class, () -> service.upload("user-a", request));
    }

    private StudyPlanningMaterialUploadRequest request(String filename, String text) {
        FileUploadRequest file = new FileUploadRequest();
        file.setOriginalFilename(filename);
        file.setBytes(text.getBytes(StandardCharsets.UTF_8));
        StudyPlanningMaterialUploadRequest request = new StudyPlanningMaterialUploadRequest();
        request.setDirection(CareerRouteContext.POSTGRADUATE);
        request.setMaterialType("ADMISSION_GUIDE");
        request.setTitle("招生说明");
        request.setMediaType("text/plain");
        request.setFile(file);
        return request;
    }
}
