package v620.cc001.cloud01.app01.mservice.ai;

import v620.cc001.base.common.dto.career.InterviewReportDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;

/** Server-side boundary for evidence-based interview report generation. */
public interface InterviewReportAnalyzer {
    InterviewReportDto analyze(InterviewSessionDto session, String transcript, int answerCount);
}
