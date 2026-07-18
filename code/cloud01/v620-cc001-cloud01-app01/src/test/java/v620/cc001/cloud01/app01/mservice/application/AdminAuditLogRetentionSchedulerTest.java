package v620.cc001.cloud01.app01.mservice.application;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.AdminAuditLogDto;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAdminGovernanceStorage;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminAuditLogRetentionSchedulerTest {

    @Test
    void cleanupRemovesOnlyAuditLogsOutsideRetentionPeriod() {
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        storage.saveAudit(audit("expired", LocalDateTime.now().minusDays(181)));
        storage.saveAudit(audit("recent", LocalDateTime.now().minusDays(1)));

        assertEquals(1, AdminAuditLogRetentionScheduler.runCleanup(storage, 180));
        assertEquals(1, storage.listAuditLogs().size());
        assertEquals("recent", storage.listAuditLogs().get(0).getAuditId());
    }

    private AdminAuditLogDto audit(String id, LocalDateTime createdAt) {
        AdminAuditLogDto audit = new AdminAuditLogDto();
        audit.setAuditId(id);
        audit.setCreatedAt(createdAt);
        return audit;
    }
}
