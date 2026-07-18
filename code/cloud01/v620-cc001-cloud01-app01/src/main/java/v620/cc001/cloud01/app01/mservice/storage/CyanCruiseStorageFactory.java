package v620.cc001.cloud01.app01.mservice.storage;

import v620.cc001.cloud01.app01.mservice.datamodel.CosmicBusinessObjectDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.datamodel.MappedCosmicDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryFurtherStudyCompanionStorage;
import v620.cc001.cloud01.app01.mservice.furtherstudy.impl.PostgresqlFurtherStudyCompanionStorage;
import v620.cc001.cloud01.app01.mservice.notification.NotificationStorage;
import v620.cc001.cloud01.app01.mservice.notification.impl.InMemoryNotificationStorage;
import v620.cc001.cloud01.app01.mservice.notification.impl.PostgresqlNotificationStorage;
import v620.cc001.cloud01.app01.mservice.notification.SubscriptionQuotaStorage;
import v620.cc001.cloud01.app01.mservice.notification.impl.InMemorySubscriptionQuotaStorage;
import v620.cc001.cloud01.app01.mservice.notification.impl.PostgresqlSubscriptionQuotaStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAdminGovernanceStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.CosmicAssessmentResultStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.CosmicAssistantChatStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.CosmicCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.CosmicInterviewStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.CosmicResumeDiagnosisStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.CosmicResumeStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentResultStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentCatalog;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentAttemptStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssistantChatStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerDailyTaskStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryInterviewStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryResumeDiagnosisStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryResumeStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlAdminGovernanceStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlAssessmentResultStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlAssessmentCatalog;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlAssessmentAttemptStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlAssistantChatStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlCareerDailyTaskStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.CosmicCareerDailyTaskStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlInterviewStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlResumeDiagnosisStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlResumeStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryStudyCenterStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlStudyCenterStorage;
import v620.cc001.cloud01.app01.mservice.furtherstudy.FurtherStudyCompanionStorage;

/**
 * Runtime storage factory for CyanCruise business-state PostgreSQL adapters.
 */
public final class CyanCruiseStorageFactory {

    public static final String SHARED_RUNTIME_PROPERTY = "cc001.shared.runtime.enabled";

    private static final InMemoryAdminGovernanceStorage IN_MEMORY_ADMIN_GOVERNANCE_STORAGE =
            new InMemoryAdminGovernanceStorage();
    private static final InMemoryAssessmentCatalog IN_MEMORY_ASSESSMENT_CATALOG =
            new InMemoryAssessmentCatalog();
    private static final InMemoryAssessmentAttemptStorage IN_MEMORY_ASSESSMENT_ATTEMPTS =
            new InMemoryAssessmentAttemptStorage();
    private static final InMemoryCareerDailyTaskStorage IN_MEMORY_CAREER_DAILY_TASKS =
            new InMemoryCareerDailyTaskStorage();
    private static final InMemoryStudyCenterStorage IN_MEMORY_STUDY_CENTER_STORAGE =
            new InMemoryStudyCenterStorage();

    private CyanCruiseStorageFactory() {
    }

    public static PostgresqlStorageConfig config() {
        PostgresqlStorageConfig storageConfig = PostgresqlStorageConfig.fromSystemProperties();
        if (isSharedRuntime()) {
            storageConfig.requireComplete("shared CyanCruise storage");
            if (storageConfig.isCosmicEnabled()) {
                throw new IllegalStateException("Shared runtime requires PostgreSQL storage instead of Cosmic or in-memory storage.");
            }
        }
        return storageConfig;
    }

    public static boolean isSharedRuntime() {
        return Boolean.parseBoolean(System.getProperty(SHARED_RUNTIME_PROPERTY, "false"));
    }

    public static CareerProfileStorage profileStorage() {
        return CareerProfileStorageFactory.fromConfig(config());
    }

    public static CareerPlanStorage careerPlanStorage() {
        PostgresqlStorageConfig storageConfig = config();
        if (storageConfig.isCosmicModuleEnabled("career-plan")) {
            return new CosmicCareerPlanStorage(cosmicGateway(storageConfig));
        }
        if (storageConfig.isCosmicEnabled()) {
            return new InMemoryCareerPlanStorage();
        }
        return new PostgresqlCareerPlanStorage(storageConfig);
    }

    public static ResumeStorage resumeStorage() {
        PostgresqlStorageConfig storageConfig = config();
        if (storageConfig.isCosmicModuleEnabled("resume")) {
            return new CosmicResumeStorage(cosmicGateway(storageConfig));
        }
        if (storageConfig.isCosmicEnabled()) {
            return new InMemoryResumeStorage();
        }
        return new PostgresqlResumeStorage(storageConfig);
    }

    public static ResumeDiagnosisStorage resumeDiagnosisStorage() {
        PostgresqlStorageConfig storageConfig = config();
        if (storageConfig.isCosmicModuleEnabled("resume-diagnosis")) {
            return new CosmicResumeDiagnosisStorage(cosmicGateway(storageConfig));
        }
        if (storageConfig.isCosmicEnabled()) {
            return new InMemoryResumeDiagnosisStorage();
        }
        return new PostgresqlResumeDiagnosisStorage(storageConfig);
    }

    public static InterviewStorage interviewStorage() {
        PostgresqlStorageConfig storageConfig = config();
        if (storageConfig.isCosmicModuleEnabled("interview")) {
            return new CosmicInterviewStorage(cosmicGateway(storageConfig));
        }
        if (storageConfig.isCosmicEnabled()) {
            return new InMemoryInterviewStorage();
        }
        return new PostgresqlInterviewStorage(storageConfig);
    }

    public static AssistantChatStorage assistantChatStorage() {
        PostgresqlStorageConfig storageConfig = config();
        if (storageConfig.isCosmicModuleEnabled("assistant")) {
            return new CosmicAssistantChatStorage(cosmicGateway(storageConfig));
        }
        if (storageConfig.isCosmicEnabled()) {
            return new InMemoryAssistantChatStorage();
        }
        return new PostgresqlAssistantChatStorage(storageConfig);
    }

    public static AssessmentResultStorage assessmentResultStorage() {
        PostgresqlStorageConfig storageConfig = config();
        if (storageConfig.isCosmicModuleEnabled("assessment")) {
            return new CosmicAssessmentResultStorage(cosmicGateway(storageConfig));
        }
        if (storageConfig.isCosmicEnabled()) {
            return new InMemoryAssessmentResultStorage();
        }
        return new PostgresqlAssessmentResultStorage(storageConfig);
    }

    public static CareerDailyTaskStorage careerDailyTaskStorage() {
        PostgresqlStorageConfig storageConfig = config();
        if (storageConfig.isCosmicModuleEnabled("career-task")) {
            return new CosmicCareerDailyTaskStorage(cosmicGateway(storageConfig));
        }
        if (storageConfig.isCosmicEnabled()) {
            return IN_MEMORY_CAREER_DAILY_TASKS;
        }
        return new PostgresqlCareerDailyTaskStorage(storageConfig);
    }

    public static AssessmentCatalog assessmentCatalog() {
        PostgresqlStorageConfig storageConfig = config();
        if (storageConfig.isPostgresqlBackend()) {
            return new PostgresqlAssessmentCatalog(storageConfig);
        }
        return IN_MEMORY_ASSESSMENT_CATALOG;
    }

    public static AssessmentAttemptStorage assessmentAttemptStorage() {
        PostgresqlStorageConfig storageConfig = config();
        if (storageConfig.isPostgresqlBackend()) {
            return new PostgresqlAssessmentAttemptStorage(storageConfig);
        }
        return IN_MEMORY_ASSESSMENT_ATTEMPTS;
    }

    public static NotificationStorage notificationStorage() {
        PostgresqlStorageConfig storageConfig = config();
        if (storageConfig.isPostgresqlBackend()) {
            return new PostgresqlNotificationStorage(storageConfig);
        }
        return new InMemoryNotificationStorage();
    }

    public static SubscriptionQuotaStorage subscriptionQuotaStorage() {
        PostgresqlStorageConfig storageConfig = config();
        if (storageConfig.isPostgresqlBackend()) {
            return new PostgresqlSubscriptionQuotaStorage(storageConfig);
        }
        return new InMemorySubscriptionQuotaStorage();
    }

    public static AdminGovernanceStorage adminGovernanceStorage() {
        PostgresqlStorageConfig storageConfig = config();
        if (storageConfig.isPostgresqlBackend()) {
            return new PostgresqlAdminGovernanceStorage(storageConfig);
        }
        return IN_MEMORY_ADMIN_GOVERNANCE_STORAGE;
    }

    public static FurtherStudyCompanionStorage furtherStudyCompanionStorage() {
        PostgresqlStorageConfig storageConfig = config();
        if ("postgresql".equalsIgnoreCase(storageConfig.getBackend())) {
            return new PostgresqlFurtherStudyCompanionStorage(storageConfig);
        }
        return new InMemoryFurtherStudyCompanionStorage();
    }

    public static StudyCenterStorage studyCenterStorage() {
        PostgresqlStorageConfig storageConfig = config();
        if (storageConfig.isPostgresqlBackend()) {
            return new PostgresqlStudyCenterStorage(storageConfig);
        }
        return IN_MEMORY_STUDY_CENTER_STORAGE;
    }

    private static CosmicDatamodelGateway cosmicGateway(PostgresqlStorageConfig storageConfig) {
        return new MappedCosmicDatamodelGateway(CosmicBusinessObjectDatamodelGateway.fromConfig(storageConfig));
    }
}
