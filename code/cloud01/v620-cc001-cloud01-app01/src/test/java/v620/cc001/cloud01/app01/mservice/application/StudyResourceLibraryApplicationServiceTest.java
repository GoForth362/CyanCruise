package v620.cc001.cloud01.app01.mservice.application;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.career.CareerResourceFeedDto;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryStudyCenterStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudyResourceLibraryApplicationServiceTest {
    @Test
    void emptyStorageRestoresPublishedStudyCatalogForAdminAndUserFeeds() {
        InMemoryStudyCenterStorage storage = new InMemoryStudyCenterStorage();

        CareerResourceFeedDto feed = new StudyCenterApplicationService(storage,
                new CareerProfileApplicationService()).resources();

        assertEquals(12, storage.listResources().size());
        assertEquals(6, feed.getArticles().size());
        assertEquals(3, feed.getVideos().size());
        assertEquals(3, feed.getConsultations().size());
        assertTrue(storage.isPublishedResourceCatalogInitialized());
    }

    @Test
    void visibleStoredResourceIsPublishedEvenWhenItUsesAHistoricalSeedId() {
        InMemoryStudyCenterStorage storage = new InMemoryStudyCenterStorage();
        AdminContentItemDto visible = new AdminContentItemDto();
        visible.setContentId("study-service-chsi");
        visible.setType("CONSULTATION");
        visible.setTitle("中国研究生招生信息网");
        visible.setSourceUrl("https://yz.chsi.com.cn/");
        visible.setHidden(Boolean.FALSE);
        storage.saveResource(visible);

        CareerResourceFeedDto feed = new StudyCenterApplicationService(storage,
                new CareerProfileApplicationService()).resources();

        assertEquals(1, feed.getConsultations().size());
        assertEquals("study-service-chsi", feed.getConsultations().get(0).getId());
    }

    @Test
    void existingAdminContentIsNeverOverwrittenByCatalogRestoration() {
        InMemoryStudyCenterStorage storage = new InMemoryStudyCenterStorage();
        AdminContentItemDto existing = new AdminContentItemDto();
        existing.setContentId("admin-study-resource");
        existing.setType("ARTICLE");
        existing.setTitle("管理员维护的升学资讯");
        existing.setHidden(Boolean.FALSE);
        storage.saveResource(existing);

        CareerResourceFeedDto feed = new StudyCenterApplicationService(storage,
                new CareerProfileApplicationService()).resources();

        assertEquals(1, storage.listResources().size());
        assertEquals(1, feed.getArticles().size());
        assertEquals("admin-study-resource", feed.getArticles().get(0).getId());
        assertTrue(feed.getConsultations().isEmpty());
        assertTrue(storage.isPublishedResourceCatalogInitialized());
    }

    @Test
    void deletedCatalogDoesNotReturnAfterItHasBeenInitialized() {
        InMemoryStudyCenterStorage storage = new InMemoryStudyCenterStorage();
        new StudyCenterApplicationService(storage, new CareerProfileApplicationService());
        for (AdminContentItemDto item : storage.listResources()) {
            storage.deleteResource(item.getContentId());
        }

        CareerResourceFeedDto feed = new StudyCenterApplicationService(storage,
                new CareerProfileApplicationService()).resources();

        assertTrue(storage.listResources().isEmpty());
        assertTrue(feed.getArticles().isEmpty());
        assertTrue(feed.getVideos().isEmpty());
        assertTrue(feed.getConsultations().isEmpty());
    }
}
