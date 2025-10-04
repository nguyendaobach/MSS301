package mss.mindmap.mindmapservice.mindmap.dto.request;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import mss.mindmap.mindmapservice.mindmap.entity.Mindmap;

public class NodeDto {
    @ManyToOne
    @JoinColumn(name = "mindmapId")
    private Mindmap mindmap;

    private Integer version;

    private String label;
    private Float positionX;
    private Float positionY;
}
