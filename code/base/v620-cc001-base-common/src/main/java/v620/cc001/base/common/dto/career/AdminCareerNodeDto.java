package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class AdminCareerNodeDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nodeId;
    private String pathId;
    private String name;
    private String description;
    private Integer sortOrder;
    private String parentId;
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public String getPathId() { return pathId; }
    public void setPathId(String pathId) { this.pathId = pathId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
}
