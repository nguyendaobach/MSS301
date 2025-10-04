package mss.mindmap.mindmapservice.mindmap.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mindmap_node")
public class Nodes extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "mindmapId")
    private Mindmap mindmap;

    private Integer version;

    private String label;
    private Float positionX;
    private Float positionY;




}
