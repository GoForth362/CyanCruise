package v620.cc001.base.common.dto.furtherstudy;

import java.util.ArrayList;
import java.util.List;

/** Structured analysis for one postgraduate mistake. */
public class PostgraduateMistakeAnalysisResult {
    private String status;
    private String subject;
    private String answer;
    private String explanation;
    private List<PostgraduateKnowledgeNodeDto> knowledgeTree = new ArrayList<PostgraduateKnowledgeNodeDto>();
    private List<String> errorReasons = new ArrayList<String>();
    private List<String> correctionSteps = new ArrayList<String>();
    private List<PostgraduateDerivedQuestionDto> derivedQuestions = new ArrayList<PostgraduateDerivedQuestionDto>();

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public List<PostgraduateKnowledgeNodeDto> getKnowledgeTree() { return knowledgeTree; }
    public void setKnowledgeTree(List<PostgraduateKnowledgeNodeDto> knowledgeTree) { this.knowledgeTree = knowledgeTree == null ? new ArrayList<PostgraduateKnowledgeNodeDto>() : knowledgeTree; }
    public List<String> getErrorReasons() { return errorReasons; }
    public void setErrorReasons(List<String> errorReasons) { this.errorReasons = errorReasons == null ? new ArrayList<String>() : errorReasons; }
    public List<String> getCorrectionSteps() { return correctionSteps; }
    public void setCorrectionSteps(List<String> correctionSteps) { this.correctionSteps = correctionSteps == null ? new ArrayList<String>() : correctionSteps; }
    public List<PostgraduateDerivedQuestionDto> getDerivedQuestions() { return derivedQuestions; }
    public void setDerivedQuestions(List<PostgraduateDerivedQuestionDto> derivedQuestions) { this.derivedQuestions = derivedQuestions == null ? new ArrayList<PostgraduateDerivedQuestionDto>() : derivedQuestions; }
}
