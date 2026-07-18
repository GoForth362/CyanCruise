package v620.cc001.cloud01.app01.mservice.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryStudyCenterStorage;

class StudyResourceLibraryApplicationServiceTest {

    @Test
    void defaultStudyResourcesCoverServicesArticlesAndVideos() {
        List<AdminContentItemDto> resources = new InMemoryStudyCenterStorage().listResources();
        Set<String> types = new HashSet<String>();

        for (AdminContentItemDto resource : resources) {
            types.add(resource.getType());
            assertFalse(resource.getContentId().startsWith("career-"));
            assertTrue(resource.getTitle() != null && resource.getTitle().trim().length() > 0);
            assertTrue(resource.getSummary() != null && resource.getSummary().trim().length() > 0);
        }

        assertEquals(12, resources.size());
        assertTrue(types.contains("CONSULTATION"));
        assertTrue(types.contains("ARTICLE"));
        assertTrue(types.contains("VIDEO"));
    }
}
