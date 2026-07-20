package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.ResumeDiagnosisRequest;
import v620.cc001.cloud01.app01.mservice.ai.impl.DefaultResumeDiagnosisAnalyzer;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultResumeDiagnosisAnalyzerTest {

    @Test
    void compatibilityAnalyzerDoesNotInventDiagnosis() {
        assertThrows(IllegalStateException.class,
                () -> new DefaultResumeDiagnosisAnalyzer().analyze(new ResumeDiagnosisRequest(), "resume"));
    }
}
