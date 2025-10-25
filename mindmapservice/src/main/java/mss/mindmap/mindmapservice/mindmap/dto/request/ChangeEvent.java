package mss.mindmap.mindmapservice.mindmap.dto.request;

import lombok.Data;
import mss.mindmap.mindmapservice.mindmap.dto.enums.ChangeType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class ChangeEvent {
    public ChangeType type;
    public String mindMapid;
    public String mindMapName;

    // Node
    public String nodeId;
    public String label;
    public Float x;
    public Float y;

    //Edge
    public String edgeId;
    public String sourceNode;
    public String targetNode;

    public String title;
    public String status;
}


