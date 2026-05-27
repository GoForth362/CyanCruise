package v620.cc001.base.common.dto.ai;

import java.io.Serializable;

public class AiUsageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer promptTokens = Integer.valueOf(AiConstants.DEFAULT_PROMPT_TOKENS);
    private Integer completionTokens = Integer.valueOf(AiConstants.DEFAULT_COMPLETION_TOKENS);
    private Integer totalTokens = Integer.valueOf(AiConstants.DEFAULT_TOTAL_TOKENS);
    private Long costMicros = Long.valueOf(AiConstants.DEFAULT_COST_MICROS);

    public Integer getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(Integer promptTokens) {
        this.promptTokens = promptTokens == null ? Integer.valueOf(0) : promptTokens;
    }

    public Integer getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(Integer completionTokens) {
        this.completionTokens = completionTokens == null ? Integer.valueOf(0) : completionTokens;
    }

    public Integer getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(Integer totalTokens) {
        this.totalTokens = totalTokens == null ? Integer.valueOf(0) : totalTokens;
    }

    public Long getCostMicros() {
        return costMicros;
    }

    public void setCostMicros(Long costMicros) {
        this.costMicros = costMicros == null ? Long.valueOf(0L) : costMicros;
    }
}
