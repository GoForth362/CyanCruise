package v620.cc001.base.common.dto.career;

import java.util.ArrayList;
import java.util.List;

/** One node in a mistake knowledge tree. */
public class PostgraduateKnowledgeNodeDto {
    private String name;
    private List<String> children = new ArrayList<String>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getChildren() { return children; }
    public void setChildren(List<String> children) { this.children = children == null ? new ArrayList<String>() : children; }
}
