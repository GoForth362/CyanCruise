package v620.cc001.base.common.dto.career;

import java.util.ArrayList;
import java.util.List;

/** Personal statement outline result. */
public class StudyAbroadStatementOutlineResult {
    private String status;
    private String goldenLine;
    private String outline;
    private final List<String> storyQuestions = new ArrayList<String>();
    private final List<String> missingInfo = new ArrayList<String>();
    private final List<String> writingTips = new ArrayList<String>();

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getGoldenLine() { return goldenLine; }
    public void setGoldenLine(String goldenLine) { this.goldenLine = goldenLine; }
    public String getOutline() { return outline; }
    public void setOutline(String outline) { this.outline = outline; }
    public List<String> getStoryQuestions() { return storyQuestions; }
    public List<String> getMissingInfo() { return missingInfo; }
    public List<String> getWritingTips() { return writingTips; }
}
