package v620.cc001.cloud01.app01.mservice.storage;

import v620.cc001.cloud01.app01.mservice.datamodel.CosmicBusinessObjectDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.datamodel.MappedCosmicDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryFurtherStudyCompanionStorage;
import v620.cc001.cloud01.app01.mservice.furtherstudy.impl.PostgresqlFurtherStudyCompanionStorage;
import v620.cc001.cloud01.app01.mservice.notification.NotificationStorage;
import v620.cc001.cloud01.app01.mservice.notification.impl.InMemoryNotificationStorage;
import v620.cc001.cloud01.app01.mservice.notification.impl.PostgresqlNotificationStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAdminGovernanceStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.CosmicAssessmentResultStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.CosmicAssistantChatStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.CosmicCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.CosmicInterviewStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.CosmicResumeDiagnosisStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.CosmicResumeStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentResultStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssistantChatStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryInterviewStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryResumeDiagnosisStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryResumeStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlAdminGovernanceStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlAssessmentResultStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlAssistantChatStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlInterviewStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlResumeDiagnosisStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlResumeStorage;
import v620.cc001.cloud01.app01.mservice.furtherstudy.FurtherStudyCompanionStorage;

/**
 * Runtime storage factory for CyanCruise business-state PostgreSQL adapters.
 */
public final class CyanCruiseStorageFactory {

    private static final InMemoryAdminGovernanceStorage IN_MEMORY_ADMIN_GOVERNANCE_STORAGE =
            new InMemoryAdminGovernanceStorage();

    private CyanCruiseStorageFactory() {
    }

    public static PostgresqlStorageConfig config() {
        return PostgresqlStorageConfig.fromSystemProperties();
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

    public static NotificationStorage notificationStorage() {
        PostgresqlStorageConfig storageConfig = config();
        if (storageConfig.isPostgresqlBackend()) {
            return new PostgresqlNotificationStorage(storageConfig);
        }
        return new InMemoryNotificationStorage();
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

    private static CosmicDatamodelGateway cosmicGateway(PostgresqlStorageConfig storageConfig) {
        return new MappedCosmicDatamodelGateway(CosmicBusinessObjectDatamodelGateway.fromConfig(storageConfig));
    }
}
