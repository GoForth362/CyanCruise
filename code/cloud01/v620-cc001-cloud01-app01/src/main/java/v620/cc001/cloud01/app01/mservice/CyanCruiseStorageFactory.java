package v620.cc001.cloud01.app01.mservice;

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
}
