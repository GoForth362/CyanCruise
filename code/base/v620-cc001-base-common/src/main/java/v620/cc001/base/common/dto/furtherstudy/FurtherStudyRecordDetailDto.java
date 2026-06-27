package v620.cc001.base.common.dto.furtherstudy;

public class FurtherStudyRecordDetailDto extends FurtherStudyRecordSummaryDto {

    private static final long serialVersionUID = 1L;

    private String requestJson;
    private String resultJson;

    public String getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }

    public String getResultJson() {
        return resultJson;
    }

    public void setResultJson(String resultJson) {
        this.resultJson = resultJson;
    }
}
