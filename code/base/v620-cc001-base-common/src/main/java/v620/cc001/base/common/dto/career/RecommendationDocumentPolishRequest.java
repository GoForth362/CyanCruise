package v620.cc001.base.common.dto.career;

/** Request for recommendation document polishing. */
public class RecommendationDocumentPolishRequest {
    private String documentType;
    private String targetMajor;
    private String draft;
    private String highlights;

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getTargetMajor() { return targetMajor; }
    public void setTargetMajor(String targetMajor) { this.targetMajor = targetMajor; }
    public String getDraft() { return draft; }
    public void setDraft(String draft) { this.draft = draft; }
    public String getHighlights() { return highlights; }
    public void setHighlights(String highlights) { this.highlights = highlights; }
}
