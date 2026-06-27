package v620.cc001.cloud01.app01.mservice.storage;

import v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryFurtherStudyCompanionStorage;
import v620.cc001.cloud01.app01.mservice.furtherstudy.impl.PostgresqlFurtherStudyCompanionStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlAssessmentResultStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlAssistantChatStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlInterviewStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlResumeDiagnosisStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlResumeStorage;
import v620.cc001.cloud01.app01.mservice.furtherstudy.FurtherStudyCompanionStorage;
import v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryFurtherStudyCompanionStorage;
import v620.cc001.cloud01.app01.mservice.furtherstudy.impl.PostgresqlFurtherStudyCompanionStorage;

/**
 * Runtime storage factory for CyanCruise business-state PostgreSQL adapters.
 */
public final class CyanCruiseStorageFactory {

    private CyanCruiseStorageFactory() {
    }

    public static PostgresqlStorageConfig config() {
        return PostgresqlStorageConfig.fromSystemProperties();
    }

    public static CareerProfileStorage profileStorage() {
        return CareerProfileStorageFactory.fromConfig(config());
    }

    public static CareerPlanStorage careerPlanStorage() {
        return new PostgresqlCareerPlanStorage(config());
    }

    public static ResumeStorage resumeStorage() {
        return new PostgresqlResumeStorage(config());
    }

    public static ResumeDiagnosisStorage resumeDiagnosisStorage() {
        return new PostgresqlResumeDiagnosisStorage(config());
    }

    public static InterviewStorage interviewStorage() {
        return new PostgresqlInterviewStorage(config());
    }

    public static AssistantChatStorage assistantChatStorage() {
        return new PostgresqlAssistantChatStorage(config());
    }

    public static AssessmentResultStorage assessmentResultStorage() {
        return new PostgresqlAssessmentResultStorage(config());
    }

    public static FurtherStudyCompanionStorage furtherStudyCompanionStorage() {
        PostgresqlStorageConfig storageConfig = config();
        if ("postgresql".equalsIgnoreCase(storageConfig.getBackend())) {
            return new PostgresqlFurtherStudyCompanionStorage(storageConfig);
        }
        return new InMemoryFurtherStudyCompanionStorage();
    }
}
