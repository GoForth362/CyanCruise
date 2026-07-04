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
    public String getWrongAnswer() { return wrongAnswer; }
    public void setWrongAnswer(String wrongAnswer) { this.wrongAnswer = wrongAnswer; }
    public String getTargetExam() { return targetExam; }
    public void setTargetExam(String targetExam) { this.targetExam = targetExam; }
}
