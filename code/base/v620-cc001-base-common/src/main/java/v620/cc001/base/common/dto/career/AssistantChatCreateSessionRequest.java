package v620.cc001.base.common.dto.career;

import java.io.Serializable;

/**
 * Request to create an assistant chat session.
 */
public class AssistantChatCreateSessionRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String persona;
    private String modelName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
