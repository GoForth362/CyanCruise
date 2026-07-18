package v620.cc001.cloud01.app01.mservice.application;

import v620.cc001.cloud01.app01.mservice.storage.AdminGovernanceStorage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Deletes expired admin audit logs once at startup and then every day at 02:10.
 */
final class AdminAuditLogRetentionScheduler {

    static final int DEFAULT_RETENTION_DAYS = 180;
    private static final String RETENTION_DAYS_PROPERTY = "cc001.admin.audit.retention.days";
    private static final AtomicBoolean STARTED = new AtomicBoolean(false);

    private AdminAuditLogRetentionScheduler() {
    }

    static void ensureStarted(final AdminGovernanceStorage storage) {
        if (storage == null || !STARTED.compareAndSet(false, true)) return;
        runCleanup(storage, retentionDays());
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            public Thread newThread(Runnable task) {
                Thread thread = new Thread(task, "cc001-admin-audit-retention");
                thread.setDaemon(true);
                return thread;
            }
        });
        executor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                runCleanup(storage, retentionDays());
            }
        }, delayUntilNextRunMillis(), TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
    }

    static int runCleanup(AdminGovernanceStorage storage, int retentionDays) {
        if (storage == null) return 0;
        return storage.deleteAuditLogsBefore(LocalDateTime.now().minusDays(Math.max(1, retentionDays)));
    }

    private static int retentionDays() {
        String value = System.getProperty(RETENTION_DAYS_PROPERTY);
        if (value == null) return DEFAULT_RETENTION_DAYS;
        try {
            return Math.max(1, Integer.parseInt(value.trim()));
        } catch (NumberFormatException ignored) {
            return DEFAULT_RETENTION_DAYS;
        }
    }

    private static long delayUntilNextRunMillis() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.with(LocalTime.of(2, 10, 0, 0));
        if (!nextRun.isAfter(now)) nextRun = nextRun.plusDays(1);
        return Math.max(1L, Duration.between(now, nextRun).toMillis());
    }
}
