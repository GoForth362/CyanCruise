package v620.cc001.base.common.dto.furtherstudy;

import java.util.ArrayList;
import java.util.List;

/** Result for recommendation document polishing. */
public class RecommendationDocumentPolishResult {
    private String status;
    private String polishedText;
    private List<String> rewriteReasons = new ArrayList<String>();
    private List<String> retainedHighlights = new ArrayList<String>();
    private List<String> missingInfo = new ArrayList<String>();

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPolishedText() { return polishedText; }
    public void setPolishedText(String polishedText) { this.polishedText = polishedText; }
    public List<String> getRewriteReasons() { return rewriteReasons; }
    public void setRewriteReasons(List<String> rewriteReasons) { this.rewriteReasons = rewriteReasons == null ? new ArrayList<String>() : rewriteReasons; }
    public List<String> getRetainedHighlights() { return retainedHighlights; }
    public void setRetainedHighlights(List<String> retainedHighlights) { this.retainedHighlights = retainedHighlights == null ? new ArrayList<String>() : retainedHighlights; }
    public List<String> getMissingInfo() { return missingInfo; }
    public void setMissingInfo(List<String> missingInfo) { this.missingInfo = missingInfo == null ? new ArrayList<String>() : missingInfo; }
}
