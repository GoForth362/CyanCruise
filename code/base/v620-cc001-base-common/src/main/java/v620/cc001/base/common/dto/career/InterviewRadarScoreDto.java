package v620.cc001.base.common.dto.career;

import java.io.Serializable;

/**
 * Structured interview report radar scores.
 */
public class InterviewRadarScoreDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer expression;
    private Integer logic;
    private Integer technical;
    private Integer pressureResistance;
    private Integer communication;
    private Integer bodyLanguage;

    public Integer getExpression() {
        return expression;
    }

    public void setExpression(Integer expression) {
        this.expression = expression;
    }

    public Integer getLogic() {
        return logic;
    }

    public void setLogic(Integer logic) {
        this.logic = logic;
    }

    public Integer getTechnical() {
        return technical;
    }

    public void setTechnical(Integer technical) {
        this.technical = technical;
    }

    public Integer getPressureResistance() {
        return pressureResistance;
    }

    public void setPressureResistance(Integer pressureResistance) {
        this.pressureResistance = pressureResistance;
    }

    public Integer getCommunication() {
        return communication;
    }

    public void setCommunication(Integer communication) {
        this.communication = communication;
    }

    public Integer getBodyLanguage() {
        return bodyLanguage;
    }

    public void setBodyLanguage(Integer bodyLanguage) {
        this.bodyLanguage = bodyLanguage;
    }
}
