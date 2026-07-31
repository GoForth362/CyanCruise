package v620.cc001.base.common.dto.furtherstudy;

/** Request for postgraduate mistake analysis. */
public class PostgraduateMistakeAnalyzeRequest {
    private String subject;
    private String questionText;
    private String wrongAnswer;
    private String targetExam;

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    /**
     * Backward-compatible aliases for the published task flow.  Earlier
     * versions called the same field "mistakeText" or "question"; exposing
     * them keeps an already-published flow from treating a populated stem as
     * missing while the WebAPI continues to use questionText.
     */
    public String getMistakeText() { return questionText; }
    public String getQuestion() { return questionText; }
    public String getWrongAnswer() { return wrongAnswer; }
    public void setWrongAnswer(String wrongAnswer) { this.wrongAnswer = wrongAnswer; }
    public String getTargetExam() { return targetExam; }
    public void setTargetExam(String targetExam) { this.targetExam = targetExam; }
}
